package com.chumore.search.controller;

import com.chumore.search.model.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping
    @ResponseBody
    public ResponseEntity<?> searchRests(
            @RequestParam(name="keyword",defaultValue="") String keyword,
            @RequestParam(name="reservedDate")LocalDate reservedDate,
            @RequestParam(name="reservedTime") LocalTime reservedTime,
            @RequestParam(name="guestCount") int guestCount
    ) throws Exception {
        System.out.println("keyword:"+keyword);
        System.out.println("reservedDate:"+reservedDate);
        System.out.println("reservedTime:"+reservedTime);
        System.out.println("guestCount:"+guestCount);
        List<Integer> restIds = searchService.searchRests(keyword,reservedDate,reservedTime.getHour(),guestCount);
        return ResponseEntity.ok(restIds);
    }
}
