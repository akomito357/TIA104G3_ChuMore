package com.chumore.rest.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chumore.cuisinetype.model.CuisineTypeService;
import com.chumore.cuisinetype.model.CuisineTypeVO;
import com.chumore.dailyreservation.model.DailyReservationService;
import com.chumore.dailyreservation.model.DailyReservationVO;
import com.chumore.exception.ResourceNotFoundException;
import com.chumore.rest.model.RestService;
import com.chumore.rest.model.RestVO;
import com.chumore.rest.model.SpecificHolidayService;
import com.chumore.rest.model.SpecificHolidayVO;
import com.chumore.tabletype.model.TableTypeService;
import com.chumore.tabletype.model.TableTypeVO;
import com.chumore.util.ResponseUtil;

// @SessionAttributes(names={"rest","member"})

@CrossOrigin
@Controller
@RequestMapping("/rests")
//@SessionAttribute

public class RestController {

	@Autowired
	HttpSession session;

	@Autowired
	RestService restSvc;

	@Autowired
	CuisineTypeService cuisineTypeSvc;

	@Autowired
	TableTypeService tableTypeSvc;

	@Autowired
	DailyReservationService dailyReservationSvc;

	@Autowired
	SpecificHolidayService specificHolidaySvc;

	// add
	@GetMapping("addRest")
	public String addRest(ModelMap model) {
		RestVO rest = new RestVO();
		model.addAttribute("rest", rest);
		return "";
	}

	// insert
	@PostMapping("insert")
	public String insert(@Valid RestVO rest, BindingResult result, ModelMap model) {
		if (result.hasErrors()) {
			return ""; // 返回原頁面顯示錯誤訊息
		}
		restSvc.addRest(rest);
		rest = restSvc.getOneById(Integer.valueOf(rest.getRestId()));
		model.addAttribute("rest", rest);
		return "";
	}


	// update
	@PostMapping("update")
	public String update(@Valid RestVO rest, BindingResult result, ModelMap model, HttpSession session) {
		if (result.hasErrors()) {
			return "";
		}
		restSvc.updateRest(rest);
		rest = restSvc.getOneById(Integer.valueOf(rest.getRestId()));
		model.addAttribute("rest", session.getAttribute("rest"));
		return "n";
	}


	@GetMapping("/getOneRest")  // 添加這行
	@ResponseBody              // 添加這行
	public RestVO getOneRest(HttpSession session) {
	    try {
	        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	        String merchantEmail = authentication.getName();
	        
	        RestVO rest = restSvc.getOneByEmail(merchantEmail);
	        
	        if (rest != null) {
	            // 設置會話屬性
	            session.setAttribute("restId", rest.getRestId());
	            session.setAttribute("userType", "ROLE_RESTAURANT");
	        }
	        
	        return rest;

	    } catch (Exception e) {
	        return null;
	    }
	}

	@GetMapping("getRestById")
	@ResponseBody
	public ResponseEntity<RestVO> getRestById(@RequestParam Integer restId) {
		try {
			RestVO rest = restSvc.getOneById(restId);
			if (rest == null) {
				return ResponseEntity.notFound().build();
			}
			return ResponseEntity.ok(rest);
		} catch (Exception e) {
			
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@Valid
	@PutMapping("updateRest")
	public ResponseEntity<Map<String, Object>> updateRest(@RequestBody Map<String, Object> requestData) {
	    Map<String, Object> response = new HashMap<>();

	    try {
	        Integer restId = Integer.parseInt(requestData.get("restId").toString());
	        Integer cuisineTypeId = Integer.parseInt(requestData.get("cuisineTypeId").toString());

	        // 獲取原有的餐廳資料
	        RestVO existingRest = restSvc.getOneById(restId);
	        if (existingRest == null) {
	            response.put("success", false);
	            response.put("message", "找不到餐廳資料");
	            return ResponseEntity.ok(response);
	        }

	        // 密碼驗證通過或沒有要更新密碼，繼續更新其他資料
	        CuisineTypeVO cuisineType = cuisineTypeSvc.getOneCuisineType(cuisineTypeId);
	        if (cuisineType == null) {
	            response.put("success", false);
	            response.put("message", "找不到料理類型");
	            return ResponseEntity.ok(response);
	        }

	        // ... 更新其他資料 ...
	        existingRest.setMerchantName((String) requestData.get("merchantName"));
	        existingRest.setPhoneNumber((String) requestData.get("phoneNumber"));
	        existingRest.setRestName((String) requestData.get("restName"));
	        existingRest.setRestRegist((String) requestData.get("restRegist"));
	        existingRest.setRestPhone((String) requestData.get("restPhone"));
	        existingRest.setRestCity((String) requestData.get("restCity"));
	        existingRest.setRestDist((String) requestData.get("restDist"));
	        existingRest.setRestAddress((String) requestData.get("restAddress"));
	        existingRest.setRestIntro((String) requestData.get("restIntro"));
	        existingRest.setCuisineType(cuisineType);
	        existingRest.setBusinessStatus(Integer.parseInt(requestData.get("businessStatus").toString()));

	        restSvc.updateRest(existingRest);

	        response.put("success", true);
	        response.put("message", "更新成功");
	        return ResponseEntity.ok(response);
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        response.put("success", false);
	        response.put("message", "更新失敗：" + e.getMessage());
	        return ResponseEntity.ok(response);
	    }
	}

	@GetMapping("getBusinessHours")
	public ResponseEntity<?> getBusinessHoursTo(@RequestParam Integer restId) {
		List<Integer[]> businessHours = restSvc.getBusinessHoursFor(restId);
		return ResponseEntity.ok(businessHours);
	}

	@PostMapping("updateBusinessHours")
	public ResponseEntity<Map<String, Object>> updateBusinessHours(@RequestBody Map<String, Object> requestData) {
		Map<String, Object> response = new HashMap<>();

		try {
			// 從請求中獲取必要資料
			Integer restId = Integer.parseInt(requestData.get("restId").toString());
			List<List<Integer>> businessHours = (List<List<Integer>>) requestData.get("businessHours");

			// 驗證資料
			if (restId == null || businessHours == null) {
				response.put("success", false);
				response.put("message", "缺少必要資料");
				return ResponseEntity.ok(response);
			}

			// 獲取餐廳資訊
			RestVO existingRest = restSvc.getOneById(restId);
			if (existingRest == null) {
				response.put("success", false);
				response.put("message", "找不到餐廳資料");
				return ResponseEntity.ok(response);
			}

			// 將多個營業時段轉換為單一24小時營業狀態字串
			StringBuilder businessHoursStr = new StringBuilder("0".repeat(24));
			for (List<Integer> hourArray : businessHours) {
				if (hourArray.size() != 24) {
					response.put("success", false);
					response.put("message", "營業時間格式錯誤");
					return ResponseEntity.ok(response);
				}

				// 更新營業狀態字串
				for (int i = 0; i < 24; i++) {
					if (hourArray.get(i) == 1 && businessHoursStr.charAt(i) == '0') {
						businessHoursStr.setCharAt(i, '1');
					}
				}
			}

			// 更新餐廳營業時間
			existingRest.setBusinessHours(businessHoursStr.toString());
			restSvc.updateRest(existingRest);

			// 更新所有桌型的預約限制
			List<TableTypeVO> tableTypes = tableTypeSvc.getAllTableTypeById(restId);
			for (TableTypeVO tableType : tableTypes) {
				StringBuilder reservedLimit = new StringBuilder();
				String tableCount = String.format("%02d", tableType.getTableCount());

				// 根據營業時間設定每個時段的可訂位數
				for (int i = 0; i < 24; i++) {
					if (businessHoursStr.charAt(i) == '1') {
						reservedLimit.append(tableCount);
					} else {
						reservedLimit.append("00");
					}
				}

				tableType.setReservedLimit(reservedLimit.toString());
				tableTypeSvc.updateTableType(tableType);

				// 更新該桌型所有日期的 DailyReservation
				try {
					for (LocalDate date = LocalDate.now(); date.isBefore(LocalDate.now().plusDays(60)); // 更新未來 60 天的資料
							date = date.plusDays(1)) {
						try {
							DailyReservationVO dailyReservation = dailyReservationSvc.findDailyReservationByDate(restId,
									date, tableType.getTableType());

							// 更新 reservedLimit
							dailyReservation.setReservedLimit(reservedLimit.toString());

							// 確保已預約的桌數不超過新的限制
							String reservedTables = dailyReservation.getReservedTables();
							if (reservedTables != null) {
								StringBuilder newReservedTables = new StringBuilder();
								for (int i = 0; i < 24; i++) {
									String currentReserved = reservedTables.substring(i * 2, (i + 1) * 2);
									int reservedCount = Integer.parseInt(currentReserved);
									int limitCount = businessHoursStr.charAt(i) == '1' ? tableType.getTableCount() : 0;

									newReservedTables
											.append(String.format("%02d", Math.min(reservedCount, limitCount)));
								}
								dailyReservation.setReservedTables(newReservedTables.toString());
							}
							dailyReservationSvc.updateDailyReservation(dailyReservation);
						} catch (ResourceNotFoundException e) {
							// 如果找不到特定日期的記錄，建立新的
							DailyReservationVO newDailyReservation = new DailyReservationVO();
							newDailyReservation.setRest(existingRest);
							newDailyReservation.setTableType(tableType);
							newDailyReservation.setReservedDate(date);
							newDailyReservation.setReservedLimit(reservedLimit.toString());

							dailyReservationSvc.addDailyReservation(newDailyReservation);
						}
					}
				} catch (Exception e) {
					// 記錄錯誤但繼續處理其他桌型
					e.printStackTrace();
				}
			}

			response.put("success", true);
			response.put("message", "營業時間、預約限制和每日預約資料更新成功");
			return ResponseEntity.ok(response);

		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("message", "更新失敗：" + e.getMessage());
			return ResponseEntity.ok(response);
		}
	}

	@GetMapping("getWeeklyBusinessDays")
	public ResponseEntity<List<Boolean>> getWeeklyBusinessDays(@RequestParam Integer restId) {

		String weeklyDays = restSvc.getBusinessDays(restId);
		List<Boolean> businessDays = weeklyDays.chars().mapToObj(ch -> ch == '1').collect(Collectors.toList());
		return ResponseEntity.ok(businessDays);
	}

	// RestController.java 的 updateWeeklyBusinessDays 方法
	@PostMapping("updateWeeklyBusinessDays")
	public ResponseEntity<Map<String, Object>> updateWeeklyBusinessDays(@RequestBody Map<String, Object> requestData) {
		Map<String, Object> response = new HashMap<>();
		try {
			Integer restId = Integer.parseInt(requestData.get("restId").toString());
			String weeklyStatus = (String) requestData.get("weeklyStatus");

			// 驗證格式是否正確
			if (weeklyStatus == null || weeklyStatus.length() != 7 || !weeklyStatus.matches("[01]+")) {
				response.put("success", false);
				response.put("message", "營業日格式不正確，請確認是否為7位的0/1字串");
				return ResponseEntity.ok(response);
			}

			// 獲取餐廳資訊
			RestVO existingRest = restSvc.getOneById(restId);
			if (existingRest == null) {
				response.put("success", false);
				response.put("message", "找不到餐廳資料");
				return ResponseEntity.ok(response);
			}

			// 取得原本的營業日設定
			String originalWeeklyStatus = existingRest.getWeeklyBizDays();

			// 更新每週營業日
			existingRest.setWeeklyBizDays(weeklyStatus);
			restSvc.updateRest(existingRest);

			// 獲取特定公休日清單
			List<SpecificHolidayVO> specificHolidays = specificHolidaySvc.getHolidaysByRestId(restId);
			List<LocalDate> holidayDates = specificHolidays.stream().map(SpecificHolidayVO::getDay)
					.collect(Collectors.toList());

			// 計算未來 60 天中需要更新的日期
			LocalDate startDate = LocalDate.now();
			LocalDate endDate = startDate.plusDays(60);
			List<LocalDate> nonBusinessDates = new ArrayList<>(); // 新的休息日
			List<LocalDate> newBusinessDates = new ArrayList<>(); // 新的營業日

			for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
				// 如果是特定公休日，跳過處理
				if (holidayDates.contains(date)) {
					continue;
				}

				int dayOfWeek = date.getDayOfWeek().getValue() - 1; // 0 = Monday, 6 = Sunday

				// 比較新舊設定，確定狀態是否改變
				char oldStatus = originalWeeklyStatus != null ? originalWeeklyStatus.charAt(dayOfWeek) : '1';
				char newStatus = weeklyStatus.charAt(dayOfWeek);

				if (oldStatus == '1' && newStatus == '0') {
					// 由營業改為不營業
					nonBusinessDates.add(date);
				} else if (oldStatus == '0' && newStatus == '1') {
					// 由不營業改為營業
					newBusinessDates.add(date);
				}
			}

			// 處理新的休息日
			for (LocalDate date : nonBusinessDates) {
				try {
					List<DailyReservationVO> dailyReservations = dailyReservationSvc.findDailyReservationsByDate(restId,
							date);

					for (DailyReservationVO dailyReservation : dailyReservations) {
						dailyReservation.setReservedLimit("0".repeat(48));
						dailyReservation.setReservedTables("0".repeat(48));
						dailyReservationSvc.updateDailyReservation(dailyReservation);
					}
				} catch (ResourceNotFoundException e) {
					// 如果找不到記錄，建立新的休息日記錄
					List<TableTypeVO> tableTypes = tableTypeSvc.getAllTableTypeById(restId);
					for (TableTypeVO tableType : tableTypes) {
						DailyReservationVO newDailyReservation = new DailyReservationVO();
						newDailyReservation.setRest(existingRest);
						newDailyReservation.setTableType(tableType);
						newDailyReservation.setReservedDate(date);
						newDailyReservation.setReservedLimit("0".repeat(48));
						newDailyReservation.setReservedTables("0".repeat(48));
						dailyReservationSvc.addDailyReservation(newDailyReservation);
					}
				}
			}

			// 處理新的營業日
			for (LocalDate date : newBusinessDates) {
				// 如果是特定公休日，跳過處理
				if (holidayDates.contains(date)) {
					continue;
				}

				try {
					// 獲取所有桌型
					List<TableTypeVO> tableTypes = tableTypeSvc.getAllTableTypeById(restId);

					// 對每種桌型處理
					for (TableTypeVO tableType : tableTypes) {
						try {
							// 嘗試更新現有記錄
							DailyReservationVO dailyReservation = dailyReservationSvc.findDailyReservationByDate(restId,
									date, tableType.getTableType());

							// 直接使用對應 TableType 的 reservedLimit
							dailyReservation.setReservedLimit(tableType.getReservedLimit());
							dailyReservationSvc.updateDailyReservation(dailyReservation);
						} catch (ResourceNotFoundException e) {
							// 建立新記錄
							DailyReservationVO newDailyReservation = new DailyReservationVO();
							newDailyReservation.setRest(existingRest);
							newDailyReservation.setTableType(tableType);
							newDailyReservation.setReservedDate(date);
							newDailyReservation.setReservedLimit(tableType.getReservedLimit());

							dailyReservationSvc.addDailyReservation(newDailyReservation);
						}
					}
				} catch (Exception e) {
					// 記錄錯誤但繼續處理其他日期
					e.printStackTrace();
				}
			}

			response.put("success", true);
			response.put("message", "每週營業日和預約限制更新成功");
			return ResponseEntity.ok(response);

		} catch (NumberFormatException e) {
			response.put("success", false);
			response.put("message", "餐廳ID格式錯誤");
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("message", "更新失敗：" + e.getMessage());
			return ResponseEntity.ok(response);
		}
	}
	
	@PostMapping("/updatePassword")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updatePassword(@RequestBody Map<String, Object> requestData) {
	    Map<String, Object> response = new HashMap<>();
	    
	    try {
	        // 從 SecurityContext 獲取當前登入的商家信箱
	        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	        String merchantEmail = authentication.getName();
	        
	        // 獲取商家資訊
	        RestVO rest = restSvc.getOneByEmail(merchantEmail);
	        if (rest == null) {
	            response.put("success", false);
	            response.put("message", "找不到商家資料");
	            return ResponseEntity.ok(response);
	        }
	        
	        String oldPassword = (String) requestData.get("oldPassword");
	        String newPassword = (String) requestData.get("newPassword");
	        
	        // 進行密碼更新
	        boolean updateSuccess = restSvc.updatePassword(rest.getRestId(), oldPassword, newPassword);
	        
	        if (updateSuccess) {
	            response.put("success", true);
	            response.put("message", "密碼更新成功");
	        } else {
	            response.put("success", false);
	            response.put("message", "舊密碼錯誤");
	        }
	        
	        return ResponseEntity.ok(response);
	        
	    } catch (Exception e) {
	        response.put("success", false);
	        response.put("message", "更新失敗：" + e.getMessage());
	        return ResponseEntity.ok(response);
	    }
	}

	// 獲取根本原因的輔助方法
	private Throwable getRootCause(Throwable e) {
		Throwable cause = e;
		while (cause.getCause() != null) {
			cause = cause.getCause();
		}
		return cause;
	}

}
