package com.chumore.productcategory.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chumore.product.model.Product_Service;
import com.chumore.productcategory.model.ProductCategoryVO;
import com.chumore.productcategory.model.ProductCategory_Service;
import com.chumore.productcategory.res.ProductCategoryResponse;

@Controller
@RequestMapping("/orders/productcategory")
public class OrderProductController {

	@Autowired
	ProductCategory_Service productCategorySvc;
	
	@Autowired
	Product_Service productSvc;
	
	@GetMapping("getListByRestId")
	@ResponseBody
	public ResponseEntity<ProductCategoryResponse> getListByRestId(@RequestParam Integer restId) {		
		List<ProductCategoryVO> vo = productCategorySvc.getAllCategoryByRest(restId);
		ProductCategoryResponse<List<ProductCategoryVO>> response = new ProductCategoryResponse<List<ProductCategoryVO>>(
				"Success", 200, vo);

		return ResponseEntity.ok(response);
	}
	
}
