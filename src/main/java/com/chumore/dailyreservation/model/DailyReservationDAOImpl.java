package com.chumore.dailyreservation.model;


import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
public class DailyReservationDAOImpl implements DailyReservationDAO{


    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public DailyReservationVO save(DailyReservationVO dailyReservation) {
        entityManager.persist(dailyReservation);
        return dailyReservation;
    }

    @Override
    public DailyReservationVO update(DailyReservationVO dailyReservation) {
        entityManager.merge(dailyReservation);
        return dailyReservation;
    }


    @Override
    public DailyReservationVO findById(Integer dailyReservationId) {
        return entityManager.find(DailyReservationVO.class, dailyReservationId);
    }


    // 查詢該餐廳某日特定桌種的 daily reservation
    @Override
    public DailyReservationVO findByDate(Integer restId, LocalDate date, Integer tableType) {

        String jpql = "SELECT dr " +
                "FROM DailyReservationVO dr " +
                "JOIN dr.tableType tt " +
                "WHERE dr.rest.restId = :restId " +
                "AND dr.reservedDate = :reservedDate " +
                "AND tt.tableType = :tableType";


        TypedQuery<DailyReservationVO> query = entityManager.createQuery(jpql, DailyReservationVO.class);
        query.setParameter("restId", restId);
        query.setParameter("reservedDate", date);
        query.setParameter("tableType", tableType);
        return query.getSingleResult();

    }

    // 查詢該餐廳某日所有的 daily reservation
    @Override
    public List<DailyReservationVO> findDailyReservationsByDate(Integer restId,LocalDate date){

        List<DailyReservationVO> dailyReservations = entityManager.createQuery("FROM DailyReservationVO dr WHERE dr.rest.restId = :restId AND dr.reservedDate = :date", DailyReservationVO.class)
                .setParameter("restId", restId)
                .setParameter("date", date)
                .getResultList();
        return dailyReservations;
    }

    //透過request parameters查詢該餐廳當日所有的 daily reservation
    @Override
    public List<DailyReservationVO> findDailyReservationsByCompositeQuery(Map<String,String> params){

        if(params.size() == 3 && params.containsKey("restId") && params.containsKey("operation") && params.containsKey("amount")){
            return findDailyReservationsByDate(Integer.valueOf(params.get("restId")), LocalDate.now());
        }

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<DailyReservationVO> criteria = builder.createQuery(DailyReservationVO.class);
        Root<DailyReservationVO> dailyReservationTable = criteria.from(DailyReservationVO.class);
        // table type

        List<Predicate> predicates = new ArrayList<>();

        for(Map.Entry<String,String> entry : params.entrySet()){
            if(entry.getKey().equals("restId")){
                predicates.add(builder.equal(dailyReservationTable.get("rest").get("restId"), Integer.valueOf(entry.getValue())));
            }else if(entry.getKey().equals("reservedDate")){
                predicates.add(builder.equal(dailyReservationTable.get("reservedDate"), java.sql.Date.valueOf(LocalDate.parse(entry.getValue()))));
            }
        }
        criteria.where(predicates.toArray(new Predicate[predicates.size()]));
        TypedQuery<DailyReservationVO> query = entityManager.createQuery(criteria);
        return query.getResultList();

    }

    //查詢該餐廳當日某個 table type 的 daily reservation
    @Override
    public DailyReservationVO findByCompositeQuery(Map<String,String> params){

        Integer restId;
        Integer tableType;
        LocalDate date;
        String jpql = "SELECT dr " +
                "FROM DailyReservationVO dr " +
                "JOIN dr.tableType tt " +
                "WHERE dr.rest.restId = :restId " +
                "AND dr.reservedDate = :reservedDate " +
                "AND tt.tableType = :tableType";


        DailyReservationVO dailyReservation = new DailyReservationVO();

        if(params.containsKey("restId") && params.containsKey("tableType") && params.containsKey("reservedDate")){
            restId =Integer.parseInt(params.get("restId"));
            tableType = Integer.parseInt(params.get("tableType"));
            date = LocalDate.parse(params.get("reservedDate"));
            dailyReservation = entityManager.createQuery(jpql,DailyReservationVO.class)
                    .setParameter("restId",restId)
                    .setParameter("reservedDate",date)
                    .setParameter("tableType",tableType)
                    .getSingleResult();

            return dailyReservation;
        }

        return dailyReservation;
    }




}