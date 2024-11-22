package io.reactivestax.repo.hibernate;

import io.reactivestax.entity.Position;
import io.reactivestax.entity.PositionCompositeKey;
import io.reactivestax.model.Trade;
import io.reactivestax.repo.PositionsRepo;
import io.reactivestax.repo.jdbc.JDBCSecuritiesReferenceRepo;
import io.reactivestax.utility.database.HibernateUtils;
import io.reactivestax.utility.exceptions.OptimisticLockingException;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;

import java.util.List;

public class HibernatePositionsRepo implements PositionsRepo {
    private static HibernatePositionsRepo instance;

    private HibernatePositionsRepo() {
        // Private Constructor to avoid anyone creating instance of this class
    }

    public static synchronized HibernatePositionsRepo getInstance() {
        if (instance == null) instance = new HibernatePositionsRepo();
        return instance;
    }

    @Override
    public void updatePositionsTable(Trade trade) throws OptimisticLockingException {
        Session session = HibernateUtils.getInstance().getConnection();
        JDBCSecuritiesReferenceRepo securitiesReference = JDBCSecuritiesReferenceRepo.getInstance();
        HibernatePositionsRepo positionsReference = HibernatePositionsRepo.getInstance();

        try {
            int securityID = securitiesReference.getSecurityIdForCusip(trade.getCusip());
            int version = positionsReference.getVersionIdForPosition(trade, securityID);

            if (version == -1) {
                //Perform Insertion Logic
                PositionCompositeKey positionCompositeKey = PositionCompositeKey.builder()
                        .accountNumber(trade.getAccountNumber())
                        .securityID(securityID)
                        .build();

                int positionAmount;

                if ("BUY".equals(trade.getActivity())) {
                    positionAmount = trade.getQuantity();
                } else if ("SELL".equals(trade.getActivity())) {
                    positionAmount = -trade.getQuantity();
                } else {
                    System.out.println("UnrecognisedActivityOperationException");
                    return;
                }

                Position position = Position.builder()
                        .positionID(positionCompositeKey)
                        .version(0)
                        .positionAmount(positionAmount)
                        .build();

                session.persist(position);

            } else {
                //Perform Update Logic
                String updateHQL = "update Position p set p.positionAmount = (p.positionAmount + :positionIncrement), p.version = (p.version +1) where p.version = :currentVersion";
                Query updatePositionQuery = session.createQuery(updateHQL);

                if ("BUY".equals(trade.getActivity())) {
                    updatePositionQuery.setParameter("positionIncrement", trade.getQuantity());
                } else if ("SELL".equals(trade.getActivity())) {
                    updatePositionQuery.setParameter("positionIncrement", -trade.getQuantity());
                } else {
                    System.out.println("UnrecognisedActivityOperationException...");
                    return;
                }

                updatePositionQuery.setParameter("currentVersion", version);

                if (updatePositionQuery.executeUpdate() == 0)
                    throw new OptimisticLockingException();
            }
        } catch (Exception e) {
            System.out.println("Failed to Update Position");
        }
    }

    public int getVersionIdForPosition(Trade trade, int securityId) {
        Session session = HibernateUtils.getInstance().getConnection();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Position> getPositionVersionQuery = builder.createQuery(Position.class);
        Root<Position> root = getPositionVersionQuery.from(Position.class);
        getPositionVersionQuery.select(root).where(
                builder.and(
                        builder.equal(root.get("positionID"), PositionCompositeKey.builder().accountNumber(trade.getAccountNumber()).securityID(securityId).build())
                ));
        List<Position> result = session.createQuery(getPositionVersionQuery).getResultList();

        if (result.isEmpty()) {
            return -1;
        } else {
            return result.get(0).getVersion();
        }
    }
}
