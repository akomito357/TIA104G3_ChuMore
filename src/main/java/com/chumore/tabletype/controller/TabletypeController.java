package com.chumore.tabletype.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.chumore.dailyreservation.model.DailyReservationService;
import com.chumore.dailyreservation.model.DailyReservationVO;
import com.chumore.exception.ResourceNotFoundException;
import com.chumore.rest.model.RestVO;
import com.chumore.rest.model.SpecificHolidayService;
import com.chumore.rest.model.SpecificHolidayVO;
import com.chumore.tabletype.model.TableTypeService;
import com.chumore.tabletype.model.TableTypeVO;

@CrossOrigin
@Controller
@RequestMapping("/rests/tableType")
public class TabletypeController {

	@Autowired
	TableTypeService tableTypeSvc;
	
	@Autowired
	DailyReservationService dailyReservationSvc;

	@Autowired
	SpecificHolidayService specificHolidaySvc;
	
	@Autowired
	HttpSession httpsession;
	
	@GetMapping("getTableType/{restId}")
	public ResponseEntity<?> getTableTypeByRestId(@PathVariable Integer restId) {
		List<TableTypeVO> list = tableTypeSvc.getAllTableTypeById(restId);
		return ResponseEntity.ok(list);
	}
	
    @PostMapping("updateTableCount")
    public ResponseEntity<Map<String, Object>> updateTableType(@RequestBody TableTypeVO tableType) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 驗證輸入數據
            if (tableType.getTableTypeId() == null || tableType.getTableCount() == null) {
                response.put("success", false);
                response.put("message", "缺少必要資料");
                return ResponseEntity.ok(response);
            }

            // 獲取原有的桌型資料
            Optional<TableTypeVO> existingTableType = tableTypeSvc.getTableTypeById(tableType.getTableTypeId());
            if (!existingTableType.isPresent()) {
                response.put("success", false);
                response.put("message", "找不到指定的桌型資料");
                return ResponseEntity.ok(response);
            }

            // 獲取餐廳的營業時間
            TableTypeVO currentTableType = existingTableType.get();
            RestVO rest = currentTableType.getRest();
            String businessHours = rest.getBusinessHours();

            // 建立新的 reservedLimit
            StringBuilder reservedLimit = new StringBuilder();
            String newTableCount = String.format("%02d", tableType.getTableCount());

            // 根據營業時間設定每個時段的訂位限制
            for (int i = 0; i < 24; i++) {
                if (businessHours.charAt(i) == '1') {
                    reservedLimit.append(newTableCount);
                } else {
                    reservedLimit.append("00");
                }
            }

            // 更新桌型資料
            currentTableType.setTableCount(tableType.getTableCount());
            currentTableType.setReservedLimit(reservedLimit.toString());
            tableTypeSvc.updateTableType(currentTableType);

            // 獲取特定公休日列表
            List<SpecificHolidayVO> specificHolidays = 
                specificHolidaySvc.getHolidaysByRestId(rest.getRestId());
            List<LocalDate> holidayDates = specificHolidays.stream()
                .map(SpecificHolidayVO::getDay)
                .collect(Collectors.toList());

            // 更新未來 60 天的 DailyReservation
            LocalDate startDate = LocalDate.now();
            LocalDate endDate = startDate.plusDays(60);

            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                // 檢查是否為特定公休日
                if (holidayDates.contains(date)) {
                    continue; // 如果是特定公休日，跳過更新
                }

                // 檢查是否為週營業日
                int dayOfWeek = date.getDayOfWeek().getValue() - 1; // 0 = Monday, 6 = Sunday
                if (rest.getWeeklyBizDays().charAt(dayOfWeek) == '0') {
                    continue; // 如果不是營業日，跳過更新
                }

                try {
                    // 更新 DailyReservation
                    DailyReservationVO dailyReservation = 
                        dailyReservationSvc.findDailyReservationByDate(
                            rest.getRestId(), date, currentTableType.getTableType());
                    dailyReservation.setReservedLimit(reservedLimit.toString());
                    dailyReservationSvc.updateDailyReservation(dailyReservation);
                } catch (ResourceNotFoundException e) {
                    // 如果找不到記錄，建立新的
                    DailyReservationVO newDailyReservation = new DailyReservationVO();
                    newDailyReservation.setRest(rest);
                    newDailyReservation.setTableType(currentTableType);
                    newDailyReservation.setReservedDate(date);
                    newDailyReservation.setReservedLimit(reservedLimit.toString());
                    newDailyReservation.setReservedTables("0".repeat(48));
                    dailyReservationSvc.addDailyReservation(newDailyReservation);
                }
            }

            response.put("success", true);
            response.put("message", "桌數和預約限制更新成功");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "更新失敗：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
	}
