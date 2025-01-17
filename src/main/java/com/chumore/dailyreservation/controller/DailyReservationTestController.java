package com.chumore.dailyreservation.controller;

import com.chumore.dailyreservation.model.DailyReservationService;
import com.chumore.dailyreservation.model.DailyReservationVO;
import com.chumore.rest.model.RestService;
import com.chumore.util.ConverterUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test/dailyReservations")
public class DailyReservationTestController {

    @Autowired
    private DailyReservationService drService;

    @Autowired
    private RestService restService;

    /*
    TODO
      查詢
      1.日期查詢：回傳查詢日期的三個桌種的營業時間列表 (以方法回傳包含三個桌種的營業時間列表的 map)
      2.更新完成：回傳更新日期的三個桌種的營業時間列表 (以方法回傳包含三個桌種的營業時間列表的 map)
    */

    @GetMapping
    public ResponseEntity<?> getDailyReservationsByDate(@RequestParam(name = "restId") Integer restId, @RequestParam(name = "reservedDate") LocalDate reservedDate) {
        List<DailyReservationVO> dailyReservations = drService.findDailyReservationsByDate(restId, reservedDate);
        Map<Integer, List<Integer>> operationsTimes = formatOperationTimes(dailyReservations);
        return ResponseEntity.ok(operationsTimes);
    }

    // 查出在指定人數、日期下每個時段的剩餘空位
    @GetMapping("/availableTables")
    public ResponseEntity<?> getAvailableTables(@RequestParam(name = "restId") Integer restId, @RequestParam(name = "reservedDate") LocalDate reservedDate,@RequestParam("guestCount") Integer guestCount) {
        List<DailyReservationVO> dailyReservations = drService.findDailyReservationsByDate(restId,reservedDate);
        DailyReservationVO dailyReservation = new DailyReservationVO();

        // 取出符合人數的桌型紀錄
        for(DailyReservationVO dr : dailyReservations){
            if(dr.getTableTypeName()>= guestCount){
                dailyReservation = dr;
                break;
            }
        }

        // 算出指定人數的桌型的剩餘空位
        List<Integer> reservedLimit = ConverterUtil.convertStrToTimeList(dailyReservation.getReservedLimit(),2);
        List<Integer> reservedTables = ConverterUtil.convertStrToTimeList(dailyReservation.getReservedTables(),2);
        List<Integer> availableTables = new ArrayList<>();
        for(int i = 0; i < reservedLimit.size(); i++){
            availableTables.add(reservedLimit.get(i) - reservedTables.get(i));
        }

        return ResponseEntity.ok(availableTables);
    }

    @PostMapping("/availableTablesList")
    public ResponseEntity<?> getAvailableTablesListByRests(@RequestBody List<Integer> restIds,@RequestParam("reservedDate") LocalDate reservedDate ,@RequestParam("guestCount") Integer guestCount) {
        Map<Integer,List<Integer>> availableTablesMap = new HashMap<>();
        for(Integer restId : restIds){
            List<Integer> availableTables = drService.findAvailableTables(restId,reservedDate,guestCount);
            availableTablesMap.put(restId, availableTables);
        }
        return ResponseEntity.ok(availableTablesMap);
    }


    @PostMapping("/reservedLimits")
    public ResponseEntity<?> addReservedLimitsByCondition(@RequestBody Map<String, String> conditions, @RequestParam Integer restId) {
        validateRequestForAddReservedLimits(conditions);
        String startTime = conditions.get("startTime");
        String endTime = conditions.get("endTime");
        LocalDate reservedDate = LocalDate.parse(conditions.get("reservedDate"));

        List<DailyReservationVO> dailyReservations = drService.findDailyReservationsByDate(restId, reservedDate);


        if (startTime != null && endTime == null) {
            // 預設為營業結束時間
            List<String> businessHours = restService.getFormattedBusinessHours(restId);
            String closedTime = businessHours.get(businessHours.size() - 1).substring(6, 11);
            conditions.put("endTime", closedTime);
        } else if (endTime != null && startTime == null) {
            // 預設為開始營業時間
            List<String> businessHours = restService.getFormattedBusinessHours(restId);
            String openTime = businessHours.get(0).substring(0, 5);
            conditions.put("startTime", openTime);
        } else if (startTime == null && endTime == null) {
            // 當天營業時間都修改
            List<String> businessHours = restService.getFormattedBusinessHours(restId);
            startTime = businessHours.get(0).substring(0, 5);
            endTime = businessHours.get(businessHours.size() - 1).substring(6, 11);
            conditions.put("startTime", startTime);
            conditions.put("endTime", endTime);
        }

        DailyReservationVO target = new DailyReservationVO();
        for (DailyReservationVO dailyReservation : dailyReservations) {
            if (dailyReservation.getTableTypeName().toString().equals(conditions.get("tableType"))) {
                target = dailyReservation;
                drService.addReservedLimitByCondition(target, conditions);

            }
        }

        List<DailyReservationVO> results = drService.findDailyReservationsByDate(restId, reservedDate);
        Map<Integer, List<Integer>> operationsTimes = formatOperationTimes(results);
        return ResponseEntity.ok(operationsTimes);
    }


    @PostMapping("/filteredRestIds")
    public ResponseEntity<?> getFilteredRestIdsByConditions(@RequestParam(name="reservedDate") LocalDate reservedDate,@RequestParam(name="reservedTime") LocalTime reservedTime ,@RequestParam(name="guestCount") Integer guestCount,@RequestBody List<Integer> restIds) {
        Integer hour = reservedTime.getHour();
        List<Integer> filteredRestIds = drService.filterRestIdsByConditions(restIds,reservedDate,hour,guestCount);
        return ResponseEntity.ok(filteredRestIds);
    }




    private Map<Integer, List<Integer>> formatOperationTimes(List<DailyReservationVO> dailyReservations) {
        Map<Integer, List<Integer>> operationTimes = new HashMap<>();

        for (DailyReservationVO dailyReservation : dailyReservations) {
            Integer tableType = dailyReservation.getTableTypeName();
            String reservedLimit = dailyReservation.getReservedLimit();
            List<Integer> reservedLimitList = ConverterUtil.convertStrToTimeList(reservedLimit, 2);
            operationTimes.put(tableType, reservedLimitList);
        }

        return operationTimes;
    }


    private void validateRequestForAddReservedLimits(Map<String, String> conditions) {
        String reservedDate = conditions.get("reservedDate");
        String tableType = conditions.get("tableType");
        String operation = conditions.get("operation");
        String startTime = conditions.get("startTime");
        String endTime = conditions.get("endTime");
        String adjustmentQuantity = conditions.get("adjustmentQuantities");


        StringBuilder errorMsg = new StringBuilder();
        if (reservedDate == null || reservedDate.trim().isEmpty()) {
            errorMsg.append("請選擇日期");
        }

        if (tableType == null || tableType.trim().isEmpty()) {
            errorMsg.append("請選擇桌種");
        }

        if (operation == null || operation.trim().isEmpty()) {
            errorMsg.append("請選擇操作");
        }

        if (adjustmentQuantity == null || adjustmentQuantity.trim().isEmpty()) {
            errorMsg.append("請輸入調整數量");
        }

        if (!(startTime == null || startTime.trim().isEmpty()) && !(endTime == null || endTime.trim().isEmpty())) {
            if (LocalTime.parse(startTime).isAfter(LocalTime.parse(endTime))) {
                errorMsg.append("開始時間不能晚於結束時間");
            }
        }


        if (errorMsg.length() > 0) {
            throw new IllegalArgumentException(errorMsg.toString());
        }

    }
}














