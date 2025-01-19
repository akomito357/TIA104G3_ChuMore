package com.chumore.dailyreservation.model;


import java.time.LocalDate;
import java.util.List;
import java.util.Map;


public interface DailyReservationDAO {

    DailyReservationVO save(DailyReservationVO dailyReservation);
    DailyReservationVO update(DailyReservationVO dailyReservation);
    DailyReservationVO findById(Integer dailyReservationId);

    List<DailyReservationVO> findDailyReservationsByDate(Integer restId,LocalDate date);
    DailyReservationVO findByDate(Integer restId, LocalDate date, Integer tableType);


    List<DailyReservationVO> findDailyReservationsByCompositeQuery(Map<String,String> params);
    DailyReservationVO findByCompositeQuery(Map<String,String> params);


    }