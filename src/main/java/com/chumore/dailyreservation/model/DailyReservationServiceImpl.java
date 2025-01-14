package com.chumore.dailyreservation.model;

import com.chumore.rest.model.RestService;
import com.chumore.util.ConverterUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class DailyReservationServiceImpl implements DailyReservationService {

    @Autowired
    private DailyReservationDAO dailyReservationDAO;

    @Autowired
    private RestService restService;

    // 找出餐廳所有的每日訂位紀錄表 (不分日、不分桌)
    @Override
    @Transactional(readOnly = true)
    public DailyReservationVO findDailyReservationById(Integer dailyReservationId) {
        return dailyReservationDAO.findById(dailyReservationId);
    }

    // 找出餐廳某日某桌種的每日訂位紀錄表
    @Override
    @Transactional(readOnly = true)
    public DailyReservationVO findDailyReservationByDate(Integer restId, LocalDate date, Integer tableType) {
        return dailyReservationDAO.findByDate(restId, date, tableType);
    }

    // 透過ID列表找出特定時間鄰近時段有位的所有餐廳ID

    @Override
    public List<Integer> filterRestIdsByConditions(List<Integer> restIds, LocalDate date, Integer reservedTime, Integer guestCount){
        List<Integer> resultList = new ArrayList<>();
        for(Integer restId : restIds){
            List<Integer> availableTables = findAvailableTables(restId, date, guestCount);
            boolean hasAvailable = false;
            for(int i=Math.max(reservedTime-2,0);i<=Math.min(reservedTime+2,24);i++){
                if(availableTables.get(i)> 0){
                    hasAvailable = true;
                    break;
                }
            }
            if(hasAvailable){
                resultList.add(restId);
            }
        }
        return resultList;
    }






    // 找出餐廳某日的每日訂位紀錄表 (不分桌)
    @Override
    @Transactional(readOnly = true)
    public List<DailyReservationVO> findDailyReservationsByDate(Integer restId, LocalDate date) {
        List<DailyReservationVO> dailyReservations = dailyReservationDAO.findDailyReservationsByDate(restId, date);
        return dailyReservations;
    }

    // 透過自訂條件找出每日訂位紀錄表
    @Override
    @Transactional(readOnly = true)
    public DailyReservationVO findDailyReservationByCompositeQuery(Map<String, String> conditions) {
        return dailyReservationDAO.findByCompositeQuery(conditions);
    }

    // 找出某桌種的剩餘空位數
    @Override
    public List<Integer> findAvailableTables(Integer restId,LocalDate date,Integer guestCount){
        List<Integer> result = new ArrayList<>();
        List<DailyReservationVO> dailyReservations = dailyReservationDAO.findDailyReservationsByDate(restId, date);
        for(DailyReservationVO dailyReservation : dailyReservations){
            if(guestCount>=dailyReservation.getTableTypeName()){
                List<Integer> reservedLimit = ConverterUtil.convertStrToTimeList(dailyReservation.getReservedLimit(), 2);
                List<Integer> reservedTables = ConverterUtil.convertStrToTimeList(dailyReservation.getReservedTables(), 2);
                for(int i=0;i<24;i++){
                    result.add(reservedLimit.get(i)-reservedTables.get(i));
                }
                break;
            }
        }
        return result;
    }


    // 新增每日訂位紀錄表
    @Override
    public DailyReservationVO addDailyReservation(DailyReservationVO dailyReservation) {
        dailyReservationDAO.save(dailyReservation);
        return dailyReservation;
    }


    // 修改每日訂位紀錄表
    @Override
    public DailyReservationVO updateDailyReservation(DailyReservationVO dailyReservation) {
        DailyReservationVO existingReservation = dailyReservationDAO.findById(dailyReservation.getDailyReservationId());
        existingReservation.setReservedTables(dailyReservation.getReservedTables());
        existingReservation.setReservedLimit(dailyReservation.getReservedLimit());

        return dailyReservationDAO.update(existingReservation);
    }

    // 新增可訂位上限
    @Override
    public DailyReservationVO addReservedLimit(Integer dailyReservationId, String time, Integer amount) {
        DailyReservationVO dailyReservation = dailyReservationDAO.findById(dailyReservationId);

        List<Integer> reservedLimitList = ConverterUtil.convertStrToTimeList(dailyReservation.getReservedLimit(), 2);
        int index = Integer.parseInt(time.substring(0, 2)) - 1;
        reservedLimitList.set(index, reservedLimitList.get(index) + amount);

        String reservedLimit = ConverterUtil.convertTimeListToStr(reservedLimitList, 2);
        dailyReservation.setReservedLimit(reservedLimit);
        return dailyReservationDAO.update(dailyReservation);
    }

    // 新增可訂位上限 (區間)
    @Override
    public DailyReservationVO addReservedLimitByInterval(DailyReservationVO dailyReservation, String startTime, String endTime, Integer amount) {
        List<Integer> reservedLimitList = ConverterUtil.convertStrToTimeList(dailyReservation.getReservedLimit(), 2);
        int startIndex = Integer.parseInt(startTime.substring(0, 2)) - 1;
        int endIndex = Integer.parseInt(endTime.substring(0, 2));

        for (int i = startIndex; i < endIndex; i++) {
            reservedLimitList.set(i, reservedLimitList.get(i) + amount);
        }

        String reservedLimit = ConverterUtil.convertTimeListToStr(reservedLimitList, 2);
        dailyReservation.setReservedLimit(reservedLimit);
        return dailyReservationDAO.update(dailyReservation);
    }

//    @Override
//    public DailyReservationVO addReservedLimitByCondition(DailyReservationVO dailyReservation, Map<String, String> conditions) {
//        List<Integer> reservedLimitList = ConverterUtil.convertStrToTimeList(dailyReservation.getReservedLimit(), 2);
//        List<Integer> reservedTablesList = ConverterUtil.convertStrToTimeList(dailyReservation.getReservedTables(), 2);
//
//        if (conditions.containsKey("startTime") && conditions.containsKey("endTime")) {
//            Integer startTime = Integer.parseInt(conditions.get("startTime").substring(0, 2));
//            Integer endTime = Integer.parseInt(conditions.get("endTime").substring(0, 2));
//
//            for (int i = startTime - 1; i < endTime; i++) {
//                if ("increase".equals(conditions.get("operation"))) {
//                    reservedLimitList.set(i, reservedLimitList.get(i) + Integer.parseInt(conditions.get("amount")));
//                } else if ("decrease".equals(conditions.get("operation"))) {
//                    int subTotal = reservedLimitList.get(i) - Integer.parseInt(conditions.get("amount"));
//                    reservedLimitList.set(i, Math.max(subTotal, Math.max(0, reservedTablesList.get(i))));
//                }
//            }
//        }
//
//        String reservedLimit = ConverterUtil.convertTimeListToStr(reservedLimitList, 2);
//        dailyReservation.setReservedLimit(reservedLimit);
//        dailyReservationDAO.update(dailyReservation);
//        return dailyReservation;
//    }

    // 變更餐廳特定日期、特定時間區間的可訂位上限
    @Override
    public DailyReservationVO addReservedLimitByCondition(DailyReservationVO dailyReservation, Map<String, String> conditions) {
        // 取得當前餐廳的營業時間
        Integer restId = dailyReservation.getRest().getRestId(); // 假設 DailyReservationVO 有 restId 欄位
        List<Integer> businessHours = restService.getBusinessHours(restId);

        // 轉換 reservedLimit 與 reservedTables 為時間區間列表
        List<Integer> reservedLimitList = ConverterUtil.convertStrToTimeList(dailyReservation.getReservedLimit(), 2);
        List<Integer> reservedTablesList = ConverterUtil.convertStrToTimeList(dailyReservation.getReservedTables(), 2);

        // 驗證 conditions 是否包含起始與結束時間
        if (conditions.containsKey("startTime") && conditions.containsKey("endTime")) {
            Integer startTime = Integer.parseInt(conditions.get("startTime").substring(0, 2)); // 取小時
            Integer endTime = Integer.parseInt(conditions.get("endTime").substring(0, 2));     // 取小時

            // 根據條件調整預約限制
            for (int i = startTime - 1; i < endTime; i++) {
                // 只有營業時間 (businessHours[i] == 1) 才處理
                if (businessHours.get(i) == 1) {
                    if ("increase".equals(conditions.get("operation"))) {
                        reservedLimitList.set(i, reservedLimitList.get(i) + Integer.parseInt(conditions.get("adjustmentQuantities")));
                    } else if ("decrease".equals(conditions.get("operation"))) {
                        int subTotal = reservedLimitList.get(i) - Integer.parseInt(conditions.get("adjustmentQuantities"));
                        reservedLimitList.set(i, Math.max(subTotal, Math.max(0, reservedTablesList.get(i))));
                    }
                }
            }
        }

        // 更新 reservedLimit 並存入資料庫
        String reservedLimit = ConverterUtil.convertTimeListToStr(reservedLimitList, 2);
        dailyReservation.setReservedLimit(reservedLimit);
        dailyReservationDAO.update(dailyReservation);
        return dailyReservation;
    }


    // 設定公休日
    @Override
    public List<DailyReservationVO> setClosedDay(Integer restId, LocalDate date) {
        List<DailyReservationVO> dailyReservations = dailyReservationDAO.findDailyReservationsByDate(restId, date);
        for (DailyReservationVO dailyReservation : dailyReservations) {
            dailyReservation.setReservedLimit("0".repeat(48));
            dailyReservationDAO.update(dailyReservation);
        }
        return dailyReservations;
    }



}
