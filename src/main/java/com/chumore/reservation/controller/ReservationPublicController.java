package com.chumore.reservation.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chumore.mail.MailService;
import com.chumore.member.model.MemberService;
import com.chumore.member.model.MemberVO;
import com.chumore.notification.model.NotificationService;
import com.chumore.reservation.model.ReservationService;
import com.chumore.reservation.model.ReservationVO;
import com.chumore.rest.model.RestService;
import com.chumore.rest.model.RestVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Controller
@RequestMapping("/reservations")
public class ReservationPublicController {

    @Value("${app.confirmation.redis.prefix}")
    private String redisPrefix;

    @Value("${app.confirmation.token.expire}")
    private Long tokenExpire;

    @Autowired
    private MemberService memberService;

    @Autowired
    private RestService restService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private MailService mailService;

    private final JedisPool jedisPool;
    
    @Autowired
    private NotificationService notificationSvc;

    public ReservationPublicController(JedisPool jedisPool)  {
        this.jedisPool = jedisPool;
    }


    // 新增訂位
    @PostMapping("/reservation")
    @ResponseBody
    public ResponseEntity<?> addReservation(@RequestBody ReservationVO reservation, @RequestParam int memberId, @RequestParam int restId, HttpSession session){
        RestVO rest = restService.getOneById(restId);
        reservation.setRest(rest);

        MemberVO member = memberService.getOneMember(memberId).get();
        reservation.setMember(member);
        reservation.setPhoneNumber(member.getMemberPhoneNumber());
        if (reservation.getReservationStatus() == null) {
            reservation.setReservationStatus(1);
        }

        // 生成唯一的token
        String token = UUID.randomUUID().toString().replace("-","");

        // 將訂位資料轉換成 JSON
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

        // 移除不需要的欄位
        ObjectNode reservationJsonNode = objectMapper.valueToTree(reservation);

        // 手動增加 restId
        reservationJsonNode.put("restId", restId);

        // 手動增加 memberId
        reservationJsonNode.put("memberId", memberId);
        String reservationJson = "";

        try{
            reservationJson = objectMapper.writeValueAsString(reservationJsonNode);
        }catch (JsonProcessingException e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("訂位資料JSON轉換失敗");
        }

        try{
            Jedis jedis = jedisPool.getResource();
            String redisKey = redisPrefix + token;
            jedis.setex(redisKey,tokenExpire,reservationJson);
            jedis.close();
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Redis連線失敗");
        }

        try{
            mailService.sendConfirmationMail(reservation,token);
        }catch(MessagingException e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("寄送確認信失敗");
        }

        // 將 token 存在 session 中，方便後續重發確認信
        session.setAttribute("confirmationToken",token);

        return ResponseEntity.ok(Map.of(
                "message", "已寄出確認信，請至信箱點擊連結完成訂位。",
                "token", token
        ));
    }

    // 訂位頁面
    @GetMapping("/reservation")
    public String reservationPage(HttpSession session, HttpServletRequest req){
    	session.setAttribute("returnUrl", req.getRequestURI() + "?" + req.getQueryString());
    	System.out.println(req.getRequestURI() + "?" + req.getQueryString());
        return "public/reservation/reservation";
    }


    //確認訂位
    @GetMapping("/reservation/confirmation-link")
    public String confrimReservation(@RequestParam("token") String token,ModelMap model){
        String redisKey = redisPrefix + token;
        String reservationJson = "";

        try{
            Jedis jedis = jedisPool.getResource();
            reservationJson = jedis.get(redisKey);
            System.out.println("Reservation JSON: " + reservationJson);
            jedis.close();
        }catch(Exception e){
            e.printStackTrace();
            return "public/reservation/reservation_confirmation_error";
        }

        if(reservationJson == null){
            System.out.println("Token not found in Redis");
            return "public/reservation/reservation_confirmation_error";
        }

        // 訂位資料反序列化
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        ReservationVO reservation = null;
        int restId = 0; //
        String reservationDate = "";
        String reservationTime = "";

        try {
            // 過濾 JSON 中的無效欄位
            ObjectNode jsonNode = (ObjectNode) objectMapper.readTree(reservationJson);
            jsonNode.remove(List.of("memberName", "restName", "memberGender", "rest"));
            restId = jsonNode.get("restId").asInt(); // 獲取 restId
            int memberId = jsonNode.get("memberId").asInt(); // 獲取 memberId
            reservationDate = jsonNode.get("reservationDate").asText(); // 獲取 reservationDate
            reservationTime = jsonNode.get("reservationTime").asText(); // 獲取 reservationTime
            jsonNode.remove("restId");
            jsonNode.remove("memberId");
            // 將過濾後的 JSON 轉換為 ReservationVO
            reservation = objectMapper.treeToValue(jsonNode, ReservationVO.class);

            // 設置 rest 物件
            RestVO rest = new RestVO();
            rest.setRestId(restId);
            reservation.setRest(rest);

            // 設置 member 物件
            MemberVO member = new MemberVO();
            member.setMemberId(memberId);
            reservation.setMember(member);

        } catch (Exception e) {
            e.printStackTrace();
            return "public/reservation/reservation_confirmation_error";
        }

        if (reservation.getGuestCount() == null || reservation.getGuestCount() <= 0) {
            throw new IllegalArgumentException("Guest count must be a positive integer.");
        }

        System.out.println(reservation);
        // 新增訂位
        ReservationVO newReservation = reservationService.addReservation(reservation);
        
        // 通知
        String reservationDateTime = reservationDate + " " + reservationTime;
        String message = notificationSvc.confirmReservation(Integer.valueOf(restId).toString(), reservationDateTime);
        notificationSvc.notifyToRest(Integer.valueOf(restId).toString(), message);

        // 刪除 redis 中的 token
        try{
            Jedis jedis = jedisPool.getResource();
            jedis.del(redisKey);
        }catch(Exception e){
            e.printStackTrace();
        }

        model.addAttribute("reservation",newReservation);
        return "public/reservation/reservation_confirmation_success";

    }

    //重發確認信
    @PostMapping("/reservation/resend-confirmation-mail")
    @ResponseBody
    public ResponseEntity<?> resendConfirmationEmail(HttpSession session){

        String token = (String) session.getAttribute("confirmationToken");
        if(token==null){
            return ResponseEntity.badRequest().body("No confirmation token found");
        }

        String redisKey = redisPrefix + token;
        String reservationJson = "";
        try {
            Jedis jedis = jedisPool.getResource();
            reservationJson = jedis.get(redisKey);
            jedis.close();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Redis 連接失敗");
        }

        // 找不到對應的訂位確認資料
        if(reservationJson == null){
            return ResponseEntity.badRequest().body("確認連結已過期或無效，無法重寄確認信。");
        }

        // 訂位資料反序列化
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        ReservationVO reservation = null;
        try{
            reservation = objectMapper.readValue(reservationJson,ReservationVO.class);
        }catch(JsonProcessingException e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("訂位資料處理失敗");
        }

        try{
            mailService.sendConfirmationMail(reservation,token);
        }catch(MessagingException e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("寄送確認信失敗");
        }

        return ResponseEntity.ok("確認信已重新發送至您的電子信箱。");

    }


}
