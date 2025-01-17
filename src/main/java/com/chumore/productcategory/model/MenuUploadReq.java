package com.chumore.productcategory.model;

import java.util.List;

import com.chumore.productcategory.dto.ProductCategoryDto;

import lombok.Data;

@Data
public class MenuUploadReq {
	List<ProductCategoryDto> productCatList;
	List<Integer> deleteCatList;
	List<Integer> deleteProductList;

	
}
