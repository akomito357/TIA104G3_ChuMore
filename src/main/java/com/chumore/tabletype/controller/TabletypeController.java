package com.chumore.tabletype.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.chumore.rest.model.RestVO;
import com.chumore.tabletype.model.TableTypeService;
import com.chumore.tabletype.model.TableTypeVO;

@CrossOrigin
@Controller
@RequestMapping("tableType")
public class TabletypeController {

	@Autowired
	TableTypeService tableTypeSvc;

	@GetMapping("getTableType")
	public ResponseEntity<?> getTableTypeByRestId(@RequestParam Integer restId) {
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
	                // 營業時間：設定為新的桌數
	                reservedLimit.append(newTableCount);
	            } else {
	                // 非營業時間：設定為 "00"
	                reservedLimit.append("00");
	            }
	        }

	        // 更新桌型資料
	        currentTableType.setTableCount(tableType.getTableCount());
	        currentTableType.setReservedLimit(reservedLimit.toString());
	        tableTypeSvc.updateTableType(currentTableType);

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
