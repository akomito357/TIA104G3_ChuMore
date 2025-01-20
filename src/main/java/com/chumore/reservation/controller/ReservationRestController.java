package com.chumore.reservation.controller;

import com.chumore.reservation.model.ReservationService;
import com.chumore.reservation.model.ReservationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@SessionAttributes(names="{restId}")
@RequestMapping("/rests/reservations")
public class ReservationRestController {

    @Autowired
    private ReservationService reservationService;


    // 查詢餐廳所有的訂位
    @GetMapping
    @ResponseBody
    public ResponseEntity<?> findAllRestReservations(@SessionAttribute("restId") Integer restId){
        List<ReservationVO> reservations = reservationService.findAllRestReservations(restId);
        return ResponseEntity.ok(reservations);
    }


    // 變更訂位狀態
    @PostMapping("/reservation/{reservationId}/reservationStatus")
    @ResponseBody
    public ResponseEntity<?> updateReservationStatus(@RequestBody Map<String,String> conditions, @PathVariable int reservationId){
        ReservationVO reservation = new ReservationVO();
        if("checkIn".equals(conditions.get("operation"))){
            reservation = reservationService.processCheckIn(reservationId);
        }else if("cancel".equals(conditions.get("operation"))){
            reservation = reservationService.cancelReservation(reservationId);
        }else if("restore".equals(conditions.get("operation"))){
            reservation = reservationService.restoreReservation(reservationId);
        }

        return ResponseEntity.ok(reservation);
    }
}
