package com.chumore.ordertable.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.chumore.ordertable.model.OrderTableService;
import com.chumore.ordertable.model.OrderTableVO;
import com.chumore.rest.model.RestService;
import com.chumore.rest.model.RestVO;


@CrossOrigin(origins = {"http://127.0.0.1:5501", "http://localhost:5501"})
@Controller
@RequestMapping("/rests/ordertables")
public class OrderTableController {
	@Autowired
	OrderTableService orderTableSvc;
	
	@Autowired
	HttpSession session;
	
	@Autowired
	private RestService restSvc;
	
    
    //測試用
    @GetMapping("testOrderTable")
    public String testOrderTable(ModelMap model) {
    	return "secure/rest/order/order_table";
    }

    
    @PostMapping(value = "/addTable")
    public ResponseEntity<Map<String, Object>> addTable(@RequestBody Map<String, Object> tableData) {
        Map<String, Object> response = new HashMap<>();
        try {

            Integer restId = Integer.parseInt(tableData.get("restID").toString());
            String tableNumber = tableData.get("tableNumber").toString();
                        
           List<OrderTableVO> existingTables = orderTableSvc.getAllByRestId(restId);
            
           boolean isDuplicate = existingTables.stream()
                   .anyMatch(table -> table.getTableNumber().equals(tableNumber));
           if (isDuplicate) {
               response.put("message", "桌號已存在");
               return ResponseEntity.badRequest().body(response);
           }
            // 建立 RestVO
            RestVO rest = new RestVO();
            rest.setRestId(restId);
            
            // 建立並設定 OrderTableVO
            OrderTableVO orderTable = new OrderTableVO();
            orderTable.setRest(rest);
            orderTable.setTableNumber(tableNumber);
            
            //生成 URL
            String orderUrl = String.format("http://chumore.ddns.net/orders/addOrder/%d/%s", restId, tableNumber);
            orderTable.setOrderTableUrl(orderUrl);            
            
            OrderTableVO newTable = orderTableSvc.addOrderTable(orderTable);
            
            response.put("success", true);
            response.put("data", newTable);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    
    @GetMapping("getAllTables")
    public ResponseEntity<?>getRestTables(@RequestParam Integer restId){
    	List<OrderTableVO> list = orderTableSvc.getAllByRestId(restId);
    	return ResponseEntity.ok(list);
    }
    
    @PutMapping("/updateTable/{orderTableId}")
    public ResponseEntity<Map<String, Object>> updateTable(
            @PathVariable Integer orderTableId,
            @RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
        	
        	
            String tableNumber = request.get("tableNumber");
            
            // 先取得原始的 OrderTableVO
            OrderTableVO orderTable = orderTableSvc.getOrderTableById(orderTableId);
            if (orderTable == null) {
                throw new RuntimeException("找不到此桌位");
            }
            Integer restId = orderTable.getRest().getRestId();

            // 檢查是否有其他桌位使用相同的桌號
            List<OrderTableVO> existingTables = orderTableSvc.getAllByRestId(restId);
            boolean isDuplicate = existingTables.stream()
                    .anyMatch(table -> !table.getOrderTableId().equals(orderTableId) 
                                       && table.getTableNumber().equals(tableNumber));
            if (isDuplicate) {
                response.put("success", false);
                response.put("message", "桌號已存在，無法更新。");
                return ResponseEntity.badRequest().body(response);
            }
            // 更新桌號
            orderTable.setTableNumber(tableNumber);
            
            // 更新 URL
            String newUrl = String.format("https://orders/addOrder/%d/%s", 
                    orderTable.getRest().getRestId(), tableNumber);
            orderTable.setOrderTableUrl(newUrl);
            
            // 儲存更新
            OrderTableVO updatedTable = orderTableSvc.updateOrderTable(orderTable);
            
            response.put("success", true);
            response.put("data", updatedTable);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/deleteTable/{orderTableId}")
    public ResponseEntity<Map<String, Object>> deleteTable(@PathVariable Integer orderTableId) {
        Map<String, Object> response = new HashMap<>();
        try {
            orderTableSvc.deleteOrderTable(orderTableId);
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "此桌尚有訂單");
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("getTableNumberById")
    public ResponseEntity<List<Map<String, Object>>> getTableNumberById(){
    	Object restNum = session.getAttribute("restId");
		Integer restId = null;
		if (restNum == null) {
			restId = 2001;
		} else {
			Integer rest = (Integer) restNum;
//			restId = rest.getRestId();
			restId = rest;
			
		}
		

    	List<Map<String, Object>> tableName = orderTableSvc.getTableNumberById(restId);
    	return ResponseEntity.ok(tableName);
    }
    
}
