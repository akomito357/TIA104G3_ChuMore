package com.chumore.dailyreservation.controller;

import com.chumore.dailyreservation.model.DailyReservationService;
import com.chumore.dailyreservation.model.DailyReservationVO;
import com.chumore.rest.model.RestService;
import com.chumore.util.ConverterUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@SessionAttributes(names={"restId"})
@RequestMapping("/rests/dailyReservations")
public class DailyReservationRestController {
    @Autowired
    private DailyReservationService drService;

    @Autowired
    private RestService restService;


    // 查詢指定日期、特定餐廳的每日訂位表資料
    @GetMapping
    public ResponseEntity<?> getDailyReservationsByDate(@SessionAttribute("restId") Integer restId, @RequestParam(name = "reservedDate") LocalDate reservedDate) {
        List<DailyReservationVO> dailyReservations = drService.findDailyReservationsByDate(restId, reservedDate);
        Map<Integer, List<Integer>> operationsTimes = formatOperationTimes(dailyReservations);
        return ResponseEntity.ok(operationsTimes);
    }

    // 變更特定時間、特定桌種每日訂位上限
    @PostMapping("/reservedLimits")
    public ResponseEntity<?> addReservedLimitsByCondition(@RequestBody Map<String, String> conditions, @SessionAttribute("restId") Integer restId) {
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

    // 格式化時間的輔助方法
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

    // 變更可訂位上限前的資料驗證方法
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
