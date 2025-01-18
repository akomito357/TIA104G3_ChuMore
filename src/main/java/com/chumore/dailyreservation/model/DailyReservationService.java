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
    DailyReservationVO addDailyReservation(DailyReservationVO dailyReservationVO);

    DailyReservationVO updateDailyReservation(DailyReservationVO dailyReservation);

    // 修改可訂位上限相關方法
    DailyReservationVO addReservedLimit(Integer dailyReservationId, String time, Integer amount);

    DailyReservationVO addReservedLimitByInterval(DailyReservationVO dailyReservation, String startTime, String endTime, Integer amount);

    DailyReservationVO addReservedLimitByCondition(DailyReservationVO dailyReservation, Map<String, String> conditions);

    // 設定公休日
    List<DailyReservationVO> setClosedDay(Integer restId, LocalDate date);
}
