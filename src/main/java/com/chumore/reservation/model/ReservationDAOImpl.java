package com.chumore.reservation.model;

import com.chumore.dailyreservation.model.DailyReservationVO;
import com.chumore.exception.BookingConflictException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class ReservationDAOImpl implements ReservationDAO{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public ReservationVO findById(int reservationId) {
        return entityManager.find(ReservationVO.class, reservationId);
    }

    @Override
    public List<ReservationVO> findAll() {
        return entityManager.createQuery("SELECT r FROM ReservationVO r", ReservationVO.class).getResultList();
    }

    @Override
    public List<ReservationVO> findAllByMemberId(int memberId) {
        return entityManager.createQuery("FROM ReservationVO r WHERE r.member.memberId = :memberId", ReservationVO.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    @Override
    public List<ReservationVO> findAllByRestId(int restId) {
        return entityManager.createQuery("FROM ReservationVO r WHERE r.rest.restId = :restId", ReservationVO.class)
                .setParameter("restId", restId)
                .getResultList();
    }

    @Override
    public List<ReservationVO> findByMemberIdAndDate(int memberId, LocalDate date) {
        return entityManager.createQuery("FROM ReservationVO r where r.memberId = :memberId AND r.reservationDate = :reservationDate", ReservationVO.class)
                .setParameter("memberId",memberId)
                .setParameter("reservationDate",date)
                .getResultList();
    }

    @Override
    public List<ReservationVO> findByRestIdAndDate(int restId, LocalDate date) {
        return entityManager.createQuery("FROM ReservationVO r where r.rest.restId = :restId AND r.reservationDate = :reservationDate", ReservationVO.class)
                .setParameter("restId",restId)
                .setParameter("reservationDate",date)
                .getResultList();
    }

    @Override
    public List<ReservationVO> findByRestIdAndReservationStatus(int restId, String reservationStatus) {
        return entityManager.createQuery("FROM ReservationVO r where r.rest.restId = :restId AND r.reservationStatus = :reservationStatus", ReservationVO.class)
                .setParameter("restId",restId)
                .setParameter("reservationStatus",reservationStatus)
                .getResultList();
    }

    @Override
    public List<ReservationVO> findByMemberIdAndReservationStatus(int memberId, String reservationStatus) {
        return entityManager.createQuery("FROM ReservationVO r where r.member.memberId = :memberId AND r.reservationStatus = :reservationStatus", ReservationVO.class)
                .setParameter("memberId",memberId)
                .setParameter("reservationStatus",reservationStatus)
                .getResultList();
    }

    @Override
    public ReservationVO addReservation(ReservationVO reservationVO) {
        try{
            entityManager.persist(reservationVO);
            entityManager.flush();
        }catch(PersistenceException e){
            Throwable cause = e.getCause();
            if(cause !=null && cause.getCause()!=null){
                String message = cause.getCause().getMessage();
                if(message.contains("Reservation limit reached for the selected time slot.")){
                    throw new BookingConflictException("選取的時段已無可預約的座位，請重新選擇時段");
                }
            }
            throw e;
        }
        return reservationVO;
    }

    @Override
    public ReservationVO updateReservation(ReservationVO reservationVO) {
        entityManager.merge(reservationVO);
        return reservationVO;
    }


    @Override
    public List<ReservationVO> findByCompositeQuery(Map<String, String> params) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ReservationVO> criteria = builder.createQuery(ReservationVO.class);
        Root<ReservationVO> reservationTable = criteria.from(ReservationVO.class);
        List<Predicate> predicates = buildPredicates(builder,reservationTable,params);
        criteria.where(predicates.toArray(new Predicate[predicates.size()]));
        TypedQuery<ReservationVO> query = entityManager.createQuery(criteria);
        return query.getResultList();
    }


    private List<Predicate> buildPredicates(CriteriaBuilder builder,Root<ReservationVO> reservationTable,Map<String,String> params) {
        List<Predicate> predicates = new ArrayList<>();
        // 日期區間條件
        if (params.containsKey("startDate") && params.containsKey("endDate")) {
            predicates.add(builder.between(
                    reservationTable.get("reservedDate"),
                    params.get("startDate"),
                    params.get("endDate")
            ));
        } else if (params.containsKey("startDate")) {
            predicates.add(builder.greaterThanOrEqualTo(
                    reservationTable.get("reservedDate"),
                    params.get("startDate")
            ));
        } else if (params.containsKey("endDate")) {
            predicates.add(builder.lessThanOrEqualTo(
                    reservationTable.get("reservedDate"),
                    params.get("endDate")
            ));
        }

        if (params.containsKey("startTime") && params.containsKey("endTime")) {
            predicates.add(builder.between(
                    reservationTable.get("reservedTime"),
                    params.get("startTime"),
                    params.get("endTime")
            ));
        }else if(params.containsKey("startTime")){
            predicates.add(builder.greaterThanOrEqualTo(
                    reservationTable.get("reservedTime"),
                    params.get("startTime")
            ));
        }else if(params.containsKey("endTime")){
            predicates.add(builder.lessThanOrEqualTo(
                    reservationTable.get("reservedTime"),
                    params.get("endTime")
            ));
        }

        for (Map.Entry<String, String> entry : params.entrySet()) {
            switch (entry.getKey()) {
                case "restId":
                    predicates.add(builder.equal(
                            reservationTable.get("rest").get("restId"),
                            Integer.parseInt(entry.getValue())
                    ));
                    break;
                case "reservationStatus":
                    predicates.add(builder.equal(
                            reservationTable.get("reservationStatus"),
                            entry.getValue()
                    ));
                    break;
                case "memberName":
                    predicates.add(builder.like(
                            reservationTable.get("member").get("memberName"),
                            "%" + entry.getValue() + "%"
                    ));
                    break;
                case "phoneNumber":
                    predicates.add(builder.equal(
                            reservationTable.get("phoneNumber"),
                            entry.getValue()
                    ));
                    break;
                default:
                    break;
            }
        }
        return predicates;
    }
}



