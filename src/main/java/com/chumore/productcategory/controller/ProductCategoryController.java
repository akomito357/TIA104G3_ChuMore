package com.chumore.productcategory.controller;

import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.chumore.product.model.ProductVO;
import com.chumore.product.model.Product_Service;
import com.chumore.productcategory.dto.ProductCategoryDto;
import com.chumore.productcategory.dto.ProductCategoryDto.ProductDTO;
import com.chumore.productcategory.model.MenuUploadReq;
import com.chumore.productcategory.model.ProductCategoryVO;
import com.chumore.productcategory.model.ProductCategory_Service;
import com.chumore.productcategory.res.ProductCategoryResponse;
import com.chumore.rest.model.RestVO;
import com.opencsv.CSVReader;

@Controller
@RequestMapping("/rests")
@CrossOrigin
public class ProductCategoryController {

	@Autowired
	ProductCategory_Service productCategorySvc;
	
	@Autowired
	HttpSession session;
	
	@Autowired
	Product_Service productSvc;

//	@PostMapping("addProductCategory")
//	public ResponseEntity<ProductCategoryResponse> insert(@RequestBody ProductCategoryVO productCategory) {
//		ProductCategoryVO vo = productCategorySvc.addProductCategory(productCategory);
//		ProductCategoryResponse<ProductCategoryVO> response = new ProductCategoryResponse<ProductCategoryVO>("Success",200,vo);
//		return ResponseEntity.ok(response);
//	
//	}

//	@PostMapping("updateProductCategory")
//	public ResponseEntity<ProductCategoryResponse> update(@RequestBody ProductCategoryVO productCategory) {
//		ProductCategoryVO vo = productCategorySvc.updateProductCategory(productCategory);
//		ProductCategoryResponse<ProductCategoryVO> response = new ProductCategoryResponse<ProductCategoryVO>("Success",
//				200, vo);
//		return ResponseEntity.ok(response);
//	}

	@PostMapping("deleteProductCategory")
	public ResponseEntity<ProductCategoryResponse> delete(@RequestBody Map<String, Integer> request) {
		Integer productCategoryId = request.get("productCategoryId");
		Integer vo = productCategorySvc.deleteProductCategory(productCategoryId);
		ProductCategoryResponse<Integer> response = new ProductCategoryResponse<Integer>("Success", 200, vo);
		return ResponseEntity.ok(response);
	}

	@GetMapping("getListByRestId")
	@ResponseBody
	public ResponseEntity<ProductCategoryResponse> getListByRestId(@RequestParam Integer restId) {
//		Integer restId = request.get("restId");
		
		List<ProductCategoryVO> vo = productCategorySvc.getAllCategoryByRest(restId);
		ProductCategoryResponse<List<ProductCategoryVO>> response = new ProductCategoryResponse<List<ProductCategoryVO>>(
				"Success", 200, vo);

		return ResponseEntity.ok(response);
	}
	
	@GetMapping("getActiveListByRestId")
	@ResponseBody
	public ResponseEntity<ProductCategoryResponse> getActiveListByRestId() {
		Object restNum = session.getAttribute("restId");
		Integer restId = null;
		if (restNum == null) {
//			restId = 2001;
		} else {
			Integer rest = (Integer) restNum;
//			restId = rest.getRestId();
			restId = rest;
			
		}
		List<ProductCategoryVO> list = productCategorySvc.getActiveListByRestId(restId);
		ProductCategoryResponse<List<ProductCategoryVO>> response = new ProductCategoryResponse<List<ProductCategoryVO>>(
				"Success", 200, list);

		return ResponseEntity.ok(response);
	}

	@PostMapping("csvFile")
	@ResponseBody
	public ResponseEntity<ProductCategoryResponse> uploadfile(@RequestParam("file") MultipartFile mutipartFile) {
		Object restNum = session.getAttribute("restId");
		Integer restId = null;
		if (restNum == null) {
//			restId = 2001;
		} else {
			restId = (Integer) restNum;
//			restId = rest.getRestId();
		}

		ProductCategoryResponse response = new ProductCategoryResponse();
		List<Map<String, String>> fileData = new ArrayList<>();
		try {
			File file = new File(System.getProperty("java.io.tmpdir") + "/" + mutipartFile.getOriginalFilename());
			mutipartFile.transferTo(file);

			FileReader fileReader = new FileReader(file);
			CSVReader csvReader = new CSVReader(fileReader);

			String[] header = csvReader.readNext();
			if (header == null || header.length != 5) {
				throw new IllegalArgumentException("CSV 文件格式錯誤，應包含 5 個欄位：類別,名稱,價格,描述,狀態");
			}

			Map<String, ProductCategoryVO> categoryMap = new HashMap<>();
			String[] line;
			while ((line = csvReader.readNext()) != null) {
				String categoryName = line[0];
				String product = line[1];
				BigDecimal price = new BigDecimal(line[2]);
				String description = line[3];
				int status = Integer.parseInt(line[4]);

				List<ProductVO> productList = null;
				ProductCategoryVO category = categoryMap.get(categoryName);
				if (category == null) {
					ProductCategoryVO prodouctCategory = new ProductCategoryVO();
					prodouctCategory.setCategoryName(categoryName);
					prodouctCategory.setRestId(restId);
					prodouctCategory.setEnableStatus(1);
					productList = new ArrayList();
					prodouctCategory.setProductList(productList);
					categoryMap.put(categoryName, prodouctCategory);
				} else {
					productList = category.getProductList();
				}

				ProductVO productvo = new ProductVO();
				productvo.setRestId(restId);
				productvo.setProductName(product);
				productvo.setProductPrice(price);
				productvo.setProductDescription(description);
				productvo.setSupplyStatus(status);
				
				
				productList.add(productvo);

			}
			csvReader.close();
			fileReader.close();
			
			Set <String> catNameSet = categoryMap.keySet();
			for(String catName : catNameSet) {
				ProductCategoryVO category = categoryMap.get(catName);
				List <ProductVO> productList = category.getProductList();
				category.setProductList(null);
				category = productCategorySvc.addProductCategory(category);
				for(ProductVO product : productList) {
					product.setProductCategoryId(category.getProductCategoryId());
					productSvc.addProduct(product);
				}
			}
			
//			Gson gson = new GsonBuilder().setPrettyPrinting().create();
//	        		return gson.toJson(categories.values());
//			String json = gson.toJson(categoryMap.values());
//			System.out.println(json);
//			return json;

			response.setCode(200);
			response.setMsg("success");
			return ResponseEntity.ok(response);

		} catch (Exception e) {
			e.printStackTrace();
			response.setCode(500);
			response.setMsg("檔案處理失敗: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
	
	@PostMapping("modifyProduct")
	public ResponseEntity<ProductCategoryResponse> modifyProduct(@RequestBody MenuUploadReq req){
		Object restNum = session.getAttribute("restId");
		Integer restId = null;
		if (restNum == null) {
			restId = 2001;
		} else {
			restId = (Integer) restNum;
//			restId = rest;
		}
		productCategorySvc.batchDelete(req);
		for(ProductCategoryDto category : req.getProductCatList()) {

			for(ProductDTO product : category.getProductList()) {
				if((product.getProductImage() == null || product.getProductImage().length == 0) && (product.getProductId() != null)) {
					byte[]exsitingImage = productCategorySvc.getImageById(product.getProductId());
					product.setProductImage(exsitingImage);
				}
			}
			
			if(category.getProductCategoryId()==null) {
				System.out.println("Adding new category: " + category.getCategoryName());
				productCategorySvc.addProductCat(category);
			}else {
				System.out.println("Updating category with ID: " + category.getProductCategoryId());
				productCategorySvc.updateProductCategory(category);
			}
		}
		ProductCategoryResponse response = new ProductCategoryResponse();
		response.setCode(200);
		response.setMsg("success");
		return ResponseEntity.ok(response);
		
		
	}
	
	@GetMapping("menu/setting")
	public String restMenuSetting() {
		return "secure/rest/menu/menu_setting";
	}
	

}