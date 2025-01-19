package com.chumore.rest.controller;

import com.chumore.cuisinetype.model.CuisineTypeService;
import com.chumore.cuisinetype.model.CuisineTypeVO;
import com.chumore.rest.model.RestService;
import com.chumore.rest.model.RestVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("test/rests")
public class RestTestController {

    @Autowired
    private RestService restService;


    @Autowired
    private CuisineTypeService cuisineTypeService;


    @PostMapping("/rest/add")
    public ResponseEntity<?> addRest(@RequestBody Map<String, Object> requestData) {
        try {
            RestVO restVO = new RestVO();

            // 設定餐廳的基本資訊
            restVO.setRestName((String) requestData.get("restName"));
            restVO.setRestCity((String) requestData.get("restCity"));
            restVO.setRestDist((String) requestData.get("restDist"));
            restVO.setRestAddress((String) requestData.get("restAddress"));
            restVO.setRestRegist((String) requestData.get("restRegist"));
            restVO.setRestPhone((String) requestData.get("restPhone"));
            restVO.setRestIntro((String) requestData.get("restIntro"));

            // 設定商家的資訊
            restVO.setMerchantName((String) requestData.get("merchantName"));
            restVO.setMerchantIdNumber((String) requestData.get("merchantIdNumber"));
            restVO.setMerchantEmail((String) requestData.get("merchantEmail"));
            restVO.setMerchantPassword((String) requestData.get("merchantPassword"));
            restVO.setPhoneNumber((String) requestData.get("phoneNumber"));

            // 設定餐廳的營業狀態
            restVO.setBusinessStatus(Integer.parseInt(requestData.get("businessStatus").toString()));
            restVO.setWeeklyBizDays((String) requestData.get("weeklyBizDays"));
            restVO.setBusinessHours((String) requestData.get("businessHours"));

            // 設定餐廳的額外屬性
            restVO.setOrderTableCount(Integer.parseInt(requestData.get("orderTableCount").toString()));
            restVO.setRestStars(Double.parseDouble(requestData.get("restStars").toString()));
            restVO.setRestReviewers(Integer.parseInt(requestData.get("restReviewers").toString()));
            restVO.setApprovalStatus(Integer.parseInt(requestData.get("approvalStatus").toString()));

            // 設定料理類型
            Integer cuisineTypeId = Integer.parseInt(requestData.get("cuisineTypeId").toString());
            CuisineTypeVO cuisineType = cuisineTypeService.getOneCuisineType(cuisineTypeId);
            if (cuisineType == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid cuisine type ID");
            }
            restVO.setCuisineType(cuisineType);

            // 保存餐廳
            restService.addRest(restVO);

            return ResponseEntity.ok("Rest added");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding rest: " + e.getMessage());
        }
    }


    @PatchMapping("updatePartialRest")
    public ResponseEntity<Map<String, Object>> updatePartialRest(@RequestBody Map<String, Object> requestData) {
        Map<String, Object> response = new HashMap<>();

        try {
            Integer restId = Integer.parseInt(requestData.get("restId").toString());

            // 獲取原有的餐廳資料
            RestVO existingRest = restService.getOneById(restId);
            if (existingRest == null) {
                response.put("success", false);
                response.put("message", "找不到餐廳資料");
                return ResponseEntity.ok(response);
            }

            // 動態更新欄位
            if (requestData.containsKey("merchantName")) {
                existingRest.setMerchantName((String) requestData.get("merchantName"));
            }
            if (requestData.containsKey("phoneNumber")) {
                existingRest.setPhoneNumber((String) requestData.get("phoneNumber"));
            }
            if (requestData.containsKey("restName")) {
                existingRest.setRestName((String) requestData.get("restName"));
            }
            if (requestData.containsKey("restRegist")) {
                existingRest.setRestRegist((String) requestData.get("restRegist"));
            }
            if (requestData.containsKey("restPhone")) {
                existingRest.setRestPhone((String) requestData.get("restPhone"));
            }
            if (requestData.containsKey("restCity")) {
                existingRest.setRestCity((String) requestData.get("restCity"));
            }
            if (requestData.containsKey("restDist")) {
                existingRest.setRestDist((String) requestData.get("restDist"));
            }
            if (requestData.containsKey("restAddress")) {
                existingRest.setRestAddress((String) requestData.get("restAddress"));
            }
            if (requestData.containsKey("restIntro")) {
                existingRest.setRestIntro((String) requestData.get("restIntro"));
            }
            if (requestData.containsKey("cuisineTypeId")) {
                Integer cuisineTypeId = Integer.parseInt(requestData.get("cuisineTypeId").toString());
                CuisineTypeVO cuisineType = cuisineTypeService.getOneCuisineType(cuisineTypeId);
                if (cuisineType == null) {
                    response.put("success", false);
                    response.put("message", "找不到料理類型");
                    return ResponseEntity.ok(response);
                }
                existingRest.setCuisineType(cuisineType);
            }
            if (requestData.containsKey("businessStatus")) {
                existingRest.setBusinessStatus(Integer.parseInt(requestData.get("businessStatus").toString()));
            }

            // 保存更新
            restService.updateRest(existingRest);

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


    @GetMapping("/rest/{restId}/formattedBusinessHours")
    public ResponseEntity<?> getFormattedBusinessHours(@PathVariable Integer restId){
        return ResponseEntity.ok(restService.getFormattedBusinessHours(restId));
    }


    @GetMapping("/rest/{restId}/businessHours")
    public ResponseEntity<?> getBusinessHours(@PathVariable Integer restId){
        List<Integer> businessHours  = restService.getBusinessHours(restId);
        return ResponseEntity.ok(businessHours);
    }

    @GetMapping("/rest/{restId}")
    public ResponseEntity<?> getRest(@PathVariable Integer restId){
        return ResponseEntity.ok(restService.getOneById(restId));
    }


    @GetMapping("/search")
    public ResponseEntity<?> getRestsByOptionalFields(@RequestParam(name = "city", required = false) List<String> cities,
                                                      @RequestParam(name = "district", required = false) List<String> districts,
                                                      @RequestParam(name = "cuisineTypeId", required = false) List<Integer> cuisineTypeIds){
        return ResponseEntity.ok(restService.getRestIdsByOptionalFields(cities, districts, cuisineTypeIds));
    }


    @PostMapping
    public ResponseEntity<?> getRestsByRestIds(@RequestBody List<Integer> restIds){
        return ResponseEntity.ok(restService.getRestsByRestIds(restIds));
    }


}
