package com;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/rests")
@Controller
public class RestHeaderController {

	// 餐廳資料修改
	@GetMapping("rest_infomation_setting")
	public String toRestInfo() {
		return "secure/rest/rest_information";
	}
	
	// 桌號設定
	@GetMapping("order_table_setting")
	public String toOrderTableSetting() {
		return "secure/rest/order/order_table";
	}
	
	// 菜單設定
	@GetMapping("menu_setting")
	public String toMenuSetting() {
		return "secure/rest/menu/menu_setting";
	}
	
	// 營業/用餐時間設定
	@GetMapping("reserved_settings")
	public String toReservationSetting() {
		return "secure/rest/reservation_setting";
	}
	
	// 訂位紀錄
	@GetMapping("rest_reservations")
	public String toRestReservations() {
		return "secure/reservation/restaurant_reservations";
	}
	
	// 訂位設定
	@GetMapping("reserved_limit_setting")
	public String toReservedLimitSetting() {
		return "secure/reservation/reserved_limit_setting";
	}
	
	
	// 餐廳用餐紀錄
	@GetMapping("rest_dining_history")
	public String toRestDiningHistory() {
		return "secure/rest/dining/restaurant_dining_history";
	}
	
	// 訂單管理
	@GetMapping("order_manage")
	public String toOrderManage() {
		return "secure/rest/order/order_manage";
	}
	
	
}
