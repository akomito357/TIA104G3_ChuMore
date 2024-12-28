package com.chumore.dailyreservation.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface DailyReservationService {

    // 查詢相關方法
    DailyReservationVO findDailyReservationById(Integer dailyReservationId);

    DailyReservationVO findDailyReservationByDate(Integer restId, LocalDate date, Integer tableType);

    List<DailyReservationVO> findDailyReservationsByDate(Integer restId, LocalDate date);

    DailyReservationVO findDailyReservationByCompositeQuery(Map<String, String> conditions);

    // 新增與更新相關方法
    DailyReservationVO addDailyReservation(Integer restId, Integer tableTypeId, LocalDate reservedDate, String reservedTables, String reservedLimit);

    void updateDailyReservation(Integer dailyReservationId, String reservedTables, String reservedLimit);

    // 修改可訂位上限相關方法
    DailyReservationVO addReservedLimit(Integer dailyReservationId, String time, Integer amount);

    void addReservedLimitByInterval(Integer dailyReservationId, String startTime, String endTime, Integer amount);

    DailyReservationVO addReservedLimitByCondition(Map<String, String> conditions);


    // 設定公休日
}
