package com.chumore.rest.controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.chumore.dailyreservation.model.DailyReservationService;
import com.chumore.dailyreservation.model.DailyReservationVO;
import com.chumore.exception.ResourceNotFoundException;
import com.chumore.rest.model.RestService;
import com.chumore.rest.model.RestVO;
import com.chumore.rest.model.SpecificHolidayService;
import com.chumore.rest.model.SpecificHolidayVO;
import com.chumore.tabletype.model.TableTypeService;
import com.chumore.tabletype.model.TableTypeVO;

@Controller
@RequestMapping("/specificHoliday")
public class SpecificHolidayController {
	@Autowired
	HttpSession session;
	@Autowired
	SpecificHolidayService specificHolidaySvc;
	@Autowired
	private RestService restSvc;
	@Autowired
	DailyReservationService dailyReservationSvc;
	@Autowired
	TableTypeService tableTypeSvc;
	
	@PostMapping("addSpecificHoliday")
	public ResponseEntity<Map<String, Object>> addHoliday(@RequestBody Map<String, Object> requestData) {
	    Map<String, Object> response = new HashMap<>();
	    try {
	        Integer restId = Integer.parseInt(requestData.get("restId").toString());
	        String dayStr = requestData.get("day").toString();
	        
	        // 將字串轉換為 LocalDate 物件
	        LocalDate day = LocalDate.parse(dayStr);
	        
	        // 新增特定公休日
	        SpecificHolidayVO holiday = new SpecificHolidayVO();
	        holiday.setRestId(restId);
	        holiday.setDay(day);
	        specificHolidaySvc.addHoliday(holiday);
	        
	        // 更新該日的 DailyReservation
	        try {
	            List<DailyReservationVO> dailyReservations = 
	                dailyReservationSvc.findDailyReservationsByDate(restId, day);
	            
	            // 將所有時段的預約限制設為 0
	            for (DailyReservationVO dailyReservation : dailyReservations) {
	                dailyReservation.setReservedLimit("0".repeat(48));
	                dailyReservation.setReservedTables("0".repeat(48));
	                dailyReservationSvc.updateDailyReservation(dailyReservation);
	            }
	        } catch (ResourceNotFoundException e) {
	            // 如果找不到該日的記錄，則為該餐廳的每種桌型建立新的記錄
	            List<TableTypeVO> tableTypes = tableTypeSvc.getAllTableTypeById(restId);
	            for (TableTypeVO tableType : tableTypes) {
	                DailyReservationVO newDailyReservation = new DailyReservationVO();
	                newDailyReservation.setRest(restSvc.getOneById(restId));
	                newDailyReservation.setTableType(tableType);
	                newDailyReservation.setReservedDate(day);
	                newDailyReservation.setReservedLimit("0".repeat(48));
	                
	                dailyReservationSvc.addDailyReservation(newDailyReservation);
	            }
	        }
	        
	        response.put("success", true);
	        response.put("message", "新增成功");
	        return ResponseEntity.ok(response);
	        
	    } catch (DateTimeParseException e) {
	        response.put("success", false);
	        response.put("message", "日期格式錯誤");
	        return ResponseEntity.ok(response);
	    } catch (Exception e) {
	        response.put("success", false);
	        response.put("message", "新增失敗：" + e.getMessage());
	        return ResponseEntity.ok(response);
	    }
	}
	
	@GetMapping("/getSpecialClosedDays")
	public ResponseEntity<List<Map<String, Object>>> getSpecialClosedDays(@RequestParam Integer restId) {
	    try {
	        List<SpecificHolidayVO> holidays = specificHolidaySvc.getHolidaysByRestId(restId);
	        
	        List<Map<String, Object>> result = holidays.stream()
	            .map(holiday -> {
	                Map<String, Object> map = new HashMap<>();
	                map.put("day", holiday.getDay().toString());
	                map.put("specificHolidayId", holiday.getSpecificHolidayId());
	                return map;
	            })
	            .collect(Collectors.toList());
	        
	        return ResponseEntity.ok(result);
	        
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}
	    
	@DeleteMapping("/deleteSpecialClosedDay")
	public ResponseEntity<Map<String, Object>> deleteSpecialClosedDay(
	        @RequestParam Integer specificHolidayId) {
	    Map<String, Object> response = new HashMap<>();
	    
	    try {
	        // 先獲取特定公休日的資訊
	    	SpecificHolidayVO holiday = specificHolidaySvc.getHolidayById(specificHolidayId);
	        if (holiday == null) {
	            response.put("success", false);
	            response.put("message", "找不到指定的公休日資料");
	            return ResponseEntity.ok(response);
	        }

	        Integer restId = holiday.getRestId();
	        LocalDate holidayDate = holiday.getDay();

	        // 獲取餐廳資訊
	        RestVO rest = restSvc.getOneById(restId);
	        if (rest == null) {
	            response.put("success", false);
	            response.put("message", "找不到餐廳資料");
	            return ResponseEntity.ok(response);
	        }

	        // 檢查該日是否為週營業日
	        String weeklyBizDays = rest.getWeeklyBizDays();
	        int dayOfWeek = holidayDate.getDayOfWeek().getValue() - 1; // 0 = Monday, 6 = Sunday
	        boolean isBusinessDay = weeklyBizDays.charAt(dayOfWeek) == '1';

	        // 刪除特定公休日
	        specificHolidaySvc.deleteHoliday(specificHolidayId);

	        // 如果是營業日，則需要恢復 DailyReservation 的預約限制
	        if (isBusinessDay) {
	            try {
	                // 獲取所有桌型
	                List<TableTypeVO> tableTypes = tableTypeSvc.getAllTableTypeById(restId);
	                
	                // 對每種桌型處理
	                for (TableTypeVO tableType : tableTypes) {
	                    try {
	                        // 嘗試更新現有記錄
	                        DailyReservationVO dailyReservation = 
	                            dailyReservationSvc.findDailyReservationByDate(restId, holidayDate, tableType.getTableType());
	                        
	                        // 直接使用對應 TableType 的 reservedLimit
	                        dailyReservation.setReservedLimit(tableType.getReservedLimit());
	                        dailyReservationSvc.updateDailyReservation(dailyReservation);
	                    } catch (ResourceNotFoundException e) {
	                        // 如果找不到記錄，建立新的
	                        DailyReservationVO newDailyReservation = new DailyReservationVO();
	                        newDailyReservation.setRest(rest);
	                        newDailyReservation.setTableType(tableType);
	                        newDailyReservation.setReservedDate(holidayDate);
	                        newDailyReservation.setReservedLimit(tableType.getReservedLimit());
	                        newDailyReservation.setReservedTables("0".repeat(48));
	                        dailyReservationSvc.addDailyReservation(newDailyReservation);
	                    }
	                }
	            } catch (Exception e) {
	                // 記錄錯誤但不影響刪除操作的結果
	                e.printStackTrace();
	            }
	        }
	        // 如果不是營業日，則不需要更動 DailyReservation 的設定
	        
	        response.put("success", true);
	        response.put("message", "刪除成功");
	        return ResponseEntity.ok(response);
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        response.put("success", false);
	        response.put("message", "刪除失敗：" + e.getMessage());
	        return ResponseEntity.ok(response);
	    }
	}
}
	
	

