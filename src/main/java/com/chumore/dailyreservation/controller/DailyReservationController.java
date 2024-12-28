package com.chumore.dailyreservation.controller;

import com.chumore.dailyreservation.model.DailyReservationService;

public class DailyReservationController {

    private final DailyReservationService dailyReservationService;

    // 使用建構子注入 service

    public DailyReservationController(DailyReservationService dailyReservationService) {
        this.dailyReservationService = dailyReservationService;
    }

    /*
    TODO
      查詢
      1.日期查詢：回傳查詢日期的三個桌種的營業時間列表 (以方法回傳包含三個桌種的營業時間列表的 map)
      2.更新完成：回傳更新日期的三個桌種的營業時間列表 (以方法回傳包含三個桌種的營業時間列表的 map)
    */

    /*
    TODO
      更新、修改
      1.檢查輸入的參數必須有日期、桌種、操作、數量 => 回傳訊息：請輸入日期、桌種、操作、數量
      2.時間
        * 沒有輸入時間的話就對所有營業時間進行操作 => 找出營業時間中所有為1的欄位，對映到可訂位上限都增加或減少該數量
        * 只輸入開始時間： => 找出營業時間中開始時間之後為1的欄位，對映到可訂位上限都增加或減少該數量
        * 只輸入結束時間： => 找出營業時間中結束時間之前為1的欄位，對映到可訂位上限都增加或減少該數量
        * 輸入的時間範圍不為營業時間：比對輸入的時間範圍對映的營業時間欄位都為1 =>回傳訊息：輸入的時間範圍包含非營業時間，請重新輸入
        * 回傳訊息：___時段 已增加/減少n個訂位上限
        * 前端：增加新增規則的說明區塊 (減少可訂位上限，1.最多只會減少到當前該時段的已訂位桌數的數量，輸入的減少數量超過時，仍會進行修改，但僅會減少到當前該時段的已訂位桌數的數量(包括0) )
     */



}
