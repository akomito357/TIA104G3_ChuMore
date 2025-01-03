package com.chumore.ordertable.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.chumore.ordertable.model.OrderTableService;
import com.chumore.ordertable.model.OrderTableVO;


@CrossOrigin
@Controller
@RequestMapping("/ordertables")
public class OrderTableController {
	@Autowired
	OrderTableService orderTableSvc;
	
	@Autowired
	HttpSession session;
	
    @GetMapping("/tables/add")
    public String showAddOrderTableForm(Model model) {
        model.addAttribute("orderTableVO", new OrderTableVO()); // 空的表單對象
        return "/secure/rest/order/order_table"; // Thymeleaf 視圖
    }
    // 處理新增桌位的表單提交
//    @PostMapping("/tables/add")
//    public String addOrderTable(OrderTableVO orderTableVO, Model model) {
//        orderTableSvc.addOrderTable(orderTableVO); // 保存桌位數據
//
//        // 獲取最新的桌位列表
//        List<OrderTableVO> orderTableList = orderTableSvc.getAll();
//        model.addAttribute("orderTable", orderTableList);
//
//        return "/secure/rest/order/order_table"; // 返回桌位管理頁面
//    }

	
//    // 刪除桌位
//    @PostMapping("/tables/delete")
//    public String deleteOrderTable(@RequestParam("order_table_id") String orderTableId, Model model) {
//        // 刪除桌位
//        orderTableSvc.deleteOrderTable(Integer.valueOf(orderTableId));
//
//        // 獲取最新的桌位列表
//        List<OrderTableVO> orderTableList = orderTableSvc.getAllByRestId(restId);
//        model.addAttribute("orderTable", orderTableList);
//
//        return "/secure/rest/order/order_table"; // 返回桌位管理頁面
//    }
    
 // 顯示修改桌位的頁面
    @GetMapping("/tables/edit")
    public String showEditOrderTableForm(@RequestParam("order_table_id") Integer orderTableId, Model model) {
        OrderTableVO orderTable = orderTableSvc.getOneOrderTable(orderTableId);
        model.addAttribute("orderTableVO", orderTable);
        return "/secure/rest/order/order_table_edit";
    }

    // 處理修改桌位的表單提交
    @PostMapping("/tables/edit")
    public String editOrderTable(OrderTableVO orderTableVO, Model model) {
        orderTableSvc.updateOrderTable(orderTableVO); // 更新桌位數據

        // 重導回桌位管理頁面
        return "redirect:/tables";
    }
    
    @GetMapping("getAllTables")
    public ResponseEntity<?>getRestTables(@RequestParam Integer restId){
    	List<OrderTableVO> list = orderTableSvc.getAllByRestId(restId);
    	return ResponseEntity.ok(list);
    	
    }
}
