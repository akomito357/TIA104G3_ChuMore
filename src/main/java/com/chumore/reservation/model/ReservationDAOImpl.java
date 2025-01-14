package com.chumore.reservation.model;

import com.chumore.exception.BookingConflictException;
import com.chumore.exception.ResourceNotFoundException;
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
        ReservationVO reservation = entityManager.find(ReservationVO.class, reservationId);
        if (reservation == null) {
            throw new ResourceNotFoundException("Reservation not found for ID: " + reservationId);
        }
        return reservation;
    }

    @Override
    public List<ReservationVO> findAll() {
        List<ReservationVO> reservations =
                entityManager.createQuery("SELECT r FROM ReservationVO r", ReservationVO.class)
                .getResultList();
        if( reservations.isEmpty()){
            throw new ResourceNotFoundException("No reservations found");
        }
        return reservations;
    }

    @Override
    public List<ReservationVO> findAllByMemberId(int memberId) {
        List<ReservationVO> reservations = entityManager.createQuery(
                        "FROM ReservationVO r WHERE r.member.memberId = :memberId", ReservationVO.class)
                .setParameter("memberId", memberId)
                .getResultList();

        if (reservations.isEmpty()) {
            throw new ResourceNotFoundException("No reservations found for Member ID: " + memberId);
        }
        return reservations;
    }

    @Override
    public List<ReservationVO> findAllByRestId(int restId) {
        List<ReservationVO> reservations = entityManager.createQuery(
                        "FROM ReservationVO r WHERE r.rest.restId = :restId", ReservationVO.class)
                .setParameter("restId", restId)
                .getResultList();

        if (reservations.isEmpty()) {
            throw new ResourceNotFoundException("No reservations found for Restaurant ID: " + restId);
        }
        return reservations;
    }

    @Override
    public List<ReservationVO> findByMemberIdAndDate(int memberId, LocalDate date) {
        List<ReservationVO> reservations = entityManager.createQuery(
                        "FROM ReservationVO r WHERE r.member.memberId = :memberId AND r.reservationDate = :reservationDate", ReservationVO.class)
                .setParameter("memberId", memberId)
                .setParameter("reservationDate", date)
                .getResultList();

        if (reservations.isEmpty()) {
            throw new ResourceNotFoundException("No reservations found for Member ID: " + memberId + " on date: " + date);
        }
        return reservations;
    }

    @Override
    public List<ReservationVO> findByRestIdAndDate(int restId, LocalDate date) {
        List<ReservationVO> reservations = entityManager.createQuery(
                        "FROM ReservationVO r WHERE r.rest.restId = :restId AND r.reservationDate = :reservationDate", ReservationVO.class)
                .setParameter("restId", restId)
                .setParameter("reservationDate", date)
                .getResultList();

        if (reservations.isEmpty()) {
            throw new ResourceNotFoundException("No reservations found for Restaurant ID: " + restId + " on date: " + date);
        }
        return reservations;
    }

    @Override
    public List<ReservationVO> findByRestIdAndReservationStatus(int restId, String reservationStatus) {
        List<ReservationVO> reservations = entityManager.createQuery(
                        "FROM ReservationVO r WHERE r.rest.restId = :restId AND r.reservationStatus = :reservationStatus", ReservationVO.class)
                .setParameter("restId", restId)
                .setParameter("reservationStatus", reservationStatus)
                .getResultList();

        if (reservations.isEmpty()) {
            throw new ResourceNotFoundException("No reservations found for Restaurant ID: " + restId + " with status: " + reservationStatus);
        }
        return reservations;
    }

    @Override
    public List<ReservationVO> findByMemberIdAndReservationStatus(int memberId, String reservationStatus) {
        List<ReservationVO> reservations = entityManager.createQuery(
                        "FROM ReservationVO r WHERE r.member.memberId = :memberId AND r.reservationStatus = :reservationStatus", ReservationVO.class)
                .setParameter("memberId", memberId)
                .setParameter("reservationStatus", reservationStatus)
                .getResultList();

        if (reservations.isEmpty()) {
            throw new ResourceNotFoundException("No reservations found for Member ID: " + memberId + " with status: " + reservationStatus);
        }
        return reservations;
    }

    @Override
    public ReservationVO addReservation(ReservationVO reservationVO) {
        try {
            entityManager.persist(reservationVO);
            entityManager.flush();
        } catch (PersistenceException e) {
            Throwable cause = e.getCause();
            // 捕捉來自 SQL 的錯誤
            if (cause != null && cause.getCause() != null) {
                String message = cause.getCause().getMessage();
                if (message.contains("Reservation limit reached for the selected time slot.")) {
                    throw new BookingConflictException("選取的時段已無可預約的座位，請重新選擇時段");
                }
            }
            throw e;
        }
        return reservationVO;
    }

    @Override
    public ReservationVO updateReservation(ReservationVO reservationVO) {
        ReservationVO existingReservation = entityManager.find(ReservationVO.class, reservationVO.getReservationId());

        if (existingReservation == null) {
            throw new ResourceNotFoundException("Reservation not found for ID: " + reservationVO.getReservationId());
        }

        ReservationVO updatedReservation = existingReservation;

        try{
            updatedReservation = entityManager.merge(reservationVO);
        }catch(PersistenceException e){
            Throwable cause = e.getCause();
            // 捕捉來自 SQL 的錯誤
            if (cause != null && cause.getCause() != null) {
                String message = cause.getCause().getMessage();
                if(message.contains("預訂數量超過可用桌數。")){
                    throw new BookingConflictException("訂位數量超過可用桌數。");
                }else if(message.contains("已預訂桌數為負值，資料異常")){
                    throw new BookingConflictException("已訂位桌數為負值，請重新確認訂位資訊");
                }
            }
        }
        return updatedReservation;
    }

    @Override
    public List<ReservationVO> findByCompositeQuery(Map<String, String> params) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ReservationVO> criteria = builder.createQuery(ReservationVO.class);
        Root<ReservationVO> reservationTable = criteria.from(ReservationVO.class);

        List<Predicate> predicates = buildPredicates(builder, reservationTable, params);
        criteria.where(predicates.toArray(new Predicate[0]));

        TypedQuery<ReservationVO> query = entityManager.createQuery(criteria);
        List<ReservationVO> results = query.getResultList();

        if (results.isEmpty()) {
            throw new ResourceNotFoundException("No reservations match the specified criteria.");
        }
        return results;
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



