package com.chumore.favrest.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.chumore.favrest.model.FavRestService;
import com.chumore.favrest.model.FavRestVO;
import com.chumore.favrest.res.FavRestResponse;
import com.chumore.member.model.MemberService;

@Controller
@RequestMapping("/favrests")
public class FavRestController {

	@Autowired
	FavRestService favRestSvc;
	
	@Autowired
	MemberService memberSvc;
	
	// add
	@PostMapping("add")
	public ResponseEntity<FavRestResponse> addFavRest(@RequestBody FavRestVO favRest) {
		FavRestVO vo = null;
		FavRestResponse<FavRestVO> res= null;
		try {
			vo = favRestSvc.addFavRest(favRest);
			res = new FavRestResponse<>(200, "success", vo);
			return ResponseEntity.ok(res);
		} catch(Exception e) {
			res = new FavRestResponse<>(400, "error", vo);
			return ResponseEntity.badRequest().body(res);
		}
	}
	
	// delete - with fetch
	@PostMapping("delete")
	public ResponseEntity<FavRestResponse> deleteFavRest(@RequestBody Map<String, Integer> req){
		FavRestResponse<Integer> res= null;
		Integer id = null;
		try {
			Integer favRestId = req.get("fevRestId");
			id = favRestSvc.deleteFavRest(favRestId);
			res = new FavRestResponse<>(200, "success", id);
			return ResponseEntity.ok(res);
		} catch(Exception e) {
			res = new FavRestResponse<>(400, "error", id);
			return ResponseEntity.badRequest().body(res);
		}
		
	}
	
	// findByMember - with thymeleaf
	public String findByMember(@RequestParam("memberId") String memberId, ModelMap model) {
		List<FavRestVO> favRestList = favRestSvc.getFavRestByMember(memberSvc.getOneMember(Integer.valueOf(memberId)).orElse(null));
		model.addAttribute("favRestList", favRestList);
		return "";
	}
	
}
