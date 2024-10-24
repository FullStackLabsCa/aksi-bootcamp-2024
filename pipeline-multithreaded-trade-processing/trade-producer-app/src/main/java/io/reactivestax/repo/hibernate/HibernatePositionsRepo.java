package io.reactivestax.repo.hibernate;

import io.reactivestax.model.Trade;
import io.reactivestax.repo.PositionsRepo;
import io.reactivestax.repo.jdbc.JDBCSecuritiesReferenceRepo;
import io.reactivestax.utility.database.HibernateUtils;
import io.reactivestax.utility.exceptions.OptimisticLockingExceptionThrowable;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;

import java.util.List;

public class HibernatePositionsRepo implements PositionsRepo {
    private static HibernatePositionsRepo instance;

    private HibernatePositionsRepo(){
        // Private Constructor to avoid anyone creating instance of this class
    }

    public static synchronized HibernatePositionsRepo getInstance(){
        if(instance == null) instance = new HibernatePositionsRepo();
        return instance;
    }

    @Override
    public void updatePositionsTable(Trade trade) throws OptimisticLockingExceptionThrowable {
        Session session = HibernateUtils.getInstance().getConnection();
        JDBCSecuritiesReferenceRepo securitiesReference = JDBCSecuritiesReferenceRepo.getInstance();
        HibernatePositionsRepo positionsReference = HibernatePositionsRepo.getInstance();

        try {
            int securityID = securitiesReference.getSecurityIdForCusip(trade.getCusip());
            int version = positionsReference.getVersionIdForPosition(trade, securityID);

            if (version == -1) {
                //Perform Insertion Logic
                Position position = new Position();

                position.setPositionID(new PositionCompositeKey(trade.getAccountNumber(), securityID));
                position.setVersion(0);
                if (trade.getActivity().equals("BUY")) {
                    position.setPositionAmount(trade.getQuantity());
                } else if (trade.getActivity().equals("SELL")) {
                    position.setPositionAmount(trade.getQuantity());
                } else System.out.println("UnrecognisedActivityOperationException");

                session.persist(position);

            } else {
                //Perform Update Logic
                String updateHQL = "update Position p set p.positionAmount = (p.positionAmount + :positionIncrement), p.version = (p.version +1) where p.version = :currentVersion";
                Query updatePositionQuery = session.createQuery(updateHQL);

                if (trade.getActivity().equals("BUY")) {
                    updatePositionQuery.setParameter("positionIncrement", trade.getQuantity());
                } else if (trade.getActivity().equals("SELL")) {
                    updatePositionQuery.setParameter("positionIncrement", -trade.getQuantity());
                } else System.out.println("UnrecognisedActivityOperationException...");

                updatePositionQuery.setParameter("currentVersion", version);

                if (updatePositionQuery.executeUpdate() == 0)
                    throw new OptimisticLockingExceptionThrowable("Optimistic Locking Occurring!!!!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getVersionIdForPosition(Trade trade, int securityId) {
        Session session = HibernateUtils.getInstance().getConnection();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Position> getPositionVersionQuery = builder.createQuery(Position.class);
        Root<Position> root = getPositionVersionQuery.from(Position.class);
        getPositionVersionQuery.select(root).where(
                builder.and(
                        builder.equal(root.get("positionID"), new PositionCompositeKey(trade.getAccountNumber(), securityId))
                ));
        List<Position> result = session.createQuery(getPositionVersionQuery).getResultList();

        if (result.isEmpty()) {
            return -1;
        } else {
            return result.get(0).getVersion();
        }
    }
}
