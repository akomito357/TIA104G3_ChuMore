package com.chumore.reservation.controller;

import com.chumore.member.model.MemberRepository;
import com.chumore.member.model.MemberVO;
import com.chumore.reservation.model.ReservationService;
import com.chumore.reservation.model.ReservationVO;
import com.chumore.rest.model.RestVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@Controller
@SessionAttributes(names = {"member"})
@RequestMapping("/test/reservations")
public class ReservationTestController {

    private ReservationService reservationService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    public ReservationTestController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    // 會員查詢
    @GetMapping("/member/{memberId}")
    @ResponseBody
    public ResponseEntity<?> finalAllMemberReservations(@PathVariable int memberId){
        List<ReservationVO> reservations = reservationService.findAllMemberReservations(memberId);
        return ResponseEntity.ok(reservations);
    }



    // 餐廳查詢

    @GetMapping
    @ResponseBody
    public ResponseEntity<?> findAllRestReservations(@RequestParam int restId){
        List<ReservationVO> reservations = reservationService.findAllRestReservations(restId);
        return ResponseEntity.ok(reservations);
    }


    @GetMapping("/{reservationDate}")
    @ResponseBody
    public ResponseEntity<?> findReservationsByRestIdAndDate(@RequestParam int restId, @PathVariable LocalDate reservationDate){
        List<ReservationVO> reservations = reservationService.findReservationsByRestIdAndDate(restId, reservationDate);
        return ResponseEntity.ok(reservations);
    }



//    @PostMapping("/reservation")
//    @ResponseBody
//    public ResponseEntity<?> addReservation(@RequestBody ReservationVO reservation, @SessionAttribute("member") MemberVO member){
//        reservation.setMember(member);
//        System.out.println(member.getMemberPhoneNumber());
//        reservation.setPhoneNumber(member.getMemberPhoneNumber());
//        if (reservation.getReservationStatus() == null) {
//            reservation.setReservationStatus(1);
//        }
//        ReservationVO newReservation = reservationService.addReservation(reservation);
//        return ResponseEntity.ok(newReservation);
//    }

    // 新增

    @PostMapping("/reservation")
    @ResponseBody
    public ResponseEntity<?> addReservation(@RequestBody ReservationVO reservation,  @RequestParam int memberId, @RequestParam int restId){
        RestVO rest = new RestVO();
        rest.setRestId(restId);
        reservation.setRest(rest);

        MemberVO member = memberRepository.findById(memberId).get();
        reservation.setMember(member);
        System.out.println(member.getMemberPhoneNumber());
        reservation.setPhoneNumber(member.getMemberPhoneNumber());
        if (reservation.getReservationStatus() == null) {
            reservation.setReservationStatus(1);
        }
        ReservationVO newReservation = reservationService.addReservation(reservation);
        return ResponseEntity.ok(newReservation);
    }

    // 修改訂位狀態
    /*
    e.g.
    POST http://localhost:8080/test/reservations/reservation/1/reservationStatus
    {
        "reservationStatus": 2
    }
     */


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


//    @PostMapping("/reservation")
//    @ResponseBody
//    public ResponseEntity<?> addReservation(@RequestBody ReservationVO reservation, @SessionAttribute("member") MemberVO member, @RequestParam int restId){
//        RestVO rest = new RestVO();
//        rest.setRestId(restId);
//        reservation.setRest(rest);
//
//        reservation.setMember(member);
//        System.out.println(member.getMemberPhoneNumber());
//        reservation.setPhoneNumber(member.getMemberPhoneNumber());
//        if (reservation.getReservationStatus() == null) {
//            reservation.setReservationStatus(1);
//        }
//        ReservationVO newReservation = reservationService.addReservation(reservation);
//        return ResponseEntity.ok(newReservation);
//    }

    // 訂位確認頁面

    @PostMapping("/reservation/confirm")
    public String confirmReservation(@RequestBody ReservationVO reservation, @RequestParam int memberId, @RequestParam int restId, ModelMap model){
        RestVO rest = new RestVO();
        rest.setRestId(restId);
        reservation.setRest(rest);

        MemberVO member = memberRepository.findById(memberId).get();
        reservation.setMember(member);
        reservation.setPhoneNumber(member.getMemberPhoneNumber());
        if (reservation.getReservationStatus() == null) {
            reservation.setReservationStatus(1);
        }

        model.addAttribute("reservation", reservation);
        return "secure/reservation/reservation_confirm" ;
    }


//    //session 版本
//    @PostMapping("/reservation/confirm")
//    public String confirmReservation(@RequestBody ReservationVO reservation, @SessionAttribute("member") MemberVO member, @SessionAttribute("rest") RestVO rest, ModelMap model){
//
//    }



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
