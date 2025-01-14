package com.chumore.productcategory.model;

import java.util.List;

public class MenuUploadReq {
	List<ProductCategoryVO> productCatList;

	public List<ProductCategoryVO> getProductCatList() {
		return productCatList;
	}

	public void setProductCatList(List<ProductCategoryVO> productCatList) {
		this.productCatList = productCatList;
	} 
	
	
}
