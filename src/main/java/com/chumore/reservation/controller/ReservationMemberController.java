package com.chumore.reservation.controller;


import com.chumore.member.model.MemberService;
import com.chumore.member.model.MemberVO;
import com.chumore.reservation.model.ReservationService;
import com.chumore.reservation.model.ReservationVO;
import com.chumore.rest.model.RestService;
import com.chumore.rest.model.RestVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
@SessionAttributes(names={"memberId"})
@RequestMapping("/member/reservations")
public class ReservationMemberController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private RestService restService;

    @Autowired
    private MemberService memberService;

    // 會員訂位查詢
    @GetMapping
    @ResponseBody
    public ResponseEntity<?> finalAllMemberReservations(@SessionAttribute("memberId") Integer memberId){
        List<ReservationVO> reservations = reservationService.findAllMemberReservations(memberId);
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

    // 跳轉訂位確認頁面
    @PostMapping("/reservation/confirm")
    public String confirmReservationPage(@RequestBody ReservationVO reservation, @SessionAttribute("memberId") int memberId, @RequestParam int restId, ModelMap model){
        RestVO rest = restService.getOneById(restId);
        reservation.setRest(rest);
        MemberVO member = memberService.getOneMember(memberId).get();
        reservation.setMember(member);

        reservation.setPhoneNumber(member.getMemberPhoneNumber());
        if (reservation.getReservationStatus() == null) {
            reservation.setReservationStatus(1);
        }

        System.out.println(reservation.getRest().getRestName());
        model.addAttribute("reservation", reservation);

        return "secure/reservation/reservation_confirm";
    }

    // 訂位成功頁面
    @GetMapping("/reservation/success")
    public String transferToReservationSuccess(){
        return "secure/reservation/reservation_success";
    }

    // 訂位失敗頁面
    @GetMapping("/reservation/error")
    public String transferToReservationFail(){
        return "secure/reservation/reservation_error";
    }

}
