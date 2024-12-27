package com.chumore.productcategory.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chumore.productcategory.model.ProductCategoryVO;
import com.chumore.productcategory.model.ProductCategory_Service;
import com.chumore.productcategory.res.ProductCategoryResponse;

@RestController
@RequestMapping("/rest/productcategory")
public class ProductCategoryController {

	@Autowired
	ProductCategory_Service productCategorySvc;
	
	
//	@PostMapping("addProductCategory")
//	public ResponseEntity<ProductCategoryResponse> insert(@RequestBody ProductCategoryVO productCategory) {
//		ProductCategoryVO vo = productCategorySvc.addProductCategory(productCategory);
//		ProductCategoryResponse<ProductCategoryVO> response = new ProductCategoryResponse<ProductCategoryVO>("Success",200,vo);
//		return ResponseEntity.ok(response);
//	
//	}
	
	@PostMapping("updateProductCategory")
	public ResponseEntity<ProductCategoryResponse> update(@RequestBody ProductCategoryVO productCategory){
		ProductCategoryVO vo = productCategorySvc.updateProductCategory(productCategory);
		ProductCategoryResponse<ProductCategoryVO> response = new ProductCategoryResponse<ProductCategoryVO>("Success",200,vo);
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("deleteProductCategory")
	public ResponseEntity<ProductCategoryResponse> delete(@RequestBody Map<String, Integer> request){
		Integer productCategoryId = request.get("productCategoryId");
		Integer vo = productCategorySvc.deleteProductCategory(productCategoryId);
		ProductCategoryResponse<Integer> response = new ProductCategoryResponse<Integer>("Success",200,vo);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("getListByRestId")
	public ResponseEntity<ProductCategoryResponse>getListByRestId(@RequestBody Map<String, Integer>request){
		Integer restId = request.get("restId");
		List<ProductCategoryVO> vo = productCategorySvc.getAllCategoryByRest(restId);
		ProductCategoryResponse<List<ProductCategoryVO>> response = new ProductCategoryResponse<List<ProductCategoryVO>>("Success",200,vo);
		
		return ResponseEntity.ok(response);
	}
	
	
}