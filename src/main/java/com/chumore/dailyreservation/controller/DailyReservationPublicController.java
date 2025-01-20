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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/dailyReservations")
public class DailyReservationPublicController {
    @Autowired
    private DailyReservationService drService;

    @Autowired
    private RestService restService;


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


    // 找出在指定人數、日期下可訂位的餐廳的每個時段的剩餘空位
    @PostMapping("/availableTablesList")
    public ResponseEntity<?> getAvailableTablesListByRests(@RequestBody List<Integer> restIds, @RequestParam("reservedDate") LocalDate reservedDate , @RequestParam("guestCount") Integer guestCount) {
        Map<Integer,List<Integer>> availableTablesMap = new HashMap<>();
        for(Integer restId : restIds){
            List<Integer> availableTables = drService.findAvailableTables(restId,reservedDate,guestCount);
            availableTablesMap.put(restId, availableTables);
        }
        return ResponseEntity.ok(availableTablesMap);
    }

    // 找出在指定人數、日期、時間下可訂位的餐廳ID
    @PostMapping("/filteredRestIds")
    public ResponseEntity<?> getFilteredRestIdsByConditions(@RequestParam(name="reservedDate") LocalDate reservedDate, @RequestParam(name="reservedTime") LocalTime reservedTime , @RequestParam(name="guestCount") Integer guestCount, @RequestBody List<Integer> restIds) {
        Integer hour = reservedTime.getHour();
        List<Integer> filteredRestIds = drService.filterRestIdsByConditions(restIds,reservedDate,hour,guestCount);
        return ResponseEntity.ok(filteredRestIds);
    }




}
