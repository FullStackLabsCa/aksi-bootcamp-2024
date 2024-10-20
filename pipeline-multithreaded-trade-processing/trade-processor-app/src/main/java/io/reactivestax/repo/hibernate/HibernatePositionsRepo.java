package io.reactivestax.repo.hibernate;

import io.reactivestax.entity.Position;
import io.reactivestax.model.Trade;
import io.reactivestax.repo.interfaces.PositionsRepo;
import io.reactivestax.utility.OptimisticLockingException;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

public class HibernatePositionsRepo implements PositionsRepo {
    private HibernatePositionsRepo instance;

    private HibernatePositionsRepo(){
        // Private Constructor to avoid anyone creating instance of this class
    }

    public HibernatePositionsRepo getInstance(){
        if(instance == null) instance = new HibernatePositionsRepo();
        return instance;
    }

    @Override
    public void updatePositionsTable(Trade trade) {
        try {
            int securityID = getSecurityIdForCusip(sqlConnection, trade.getCusip());
            int version = getAccountVersionUsingHibernate(session, trade, securityID);

            if (version == -1) {
                //Perform Insertion Logic
                Position position = new Position();

                position.setAccountNumber(trade.getAccountNumber());
                position.setSecurityID(securityID);
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
                    throw new OptimisticLockingException("Optimistic Locking Occurring!!!!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getVersionIdForPosition(Trade trade, int securityId) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Position> getPositionVersionQuery = builder.createQuery(Position.class);
        Root<Position> root = getPositionVersionQuery.from(Position.class);
        getPositionVersionQuery.select(root).where(
                builder.and(
                        builder.equal(root.get("accountNumber"), trade.getAccountNumber()),
                        builder.equal(root.get("securityID"), securityId)
                ));
        List<Position> result = session.createQuery(getPositionVersionQuery).getResultList();

        if (result.isEmpty()) {
            return -1;
        } else {
            return result.get(0).getVersion();
        }
    }
}
