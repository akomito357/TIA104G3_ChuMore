package com.chumore.productcategory.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("productCategorySvc")
public class ProductCategory_Service {
	
	@Autowired
	ProductCategoryRepository repository;
	
	public ProductCategoryVO addProductCategory(ProductCategoryVO productCategory) {
		return repository.save(productCategory);
	}
	
	public ProductCategoryVO updateProductCategory(ProductCategoryVO productCategory) {
		if(productCategory.getProductCategoryId() == null) {
			throw new IllegalArgumentException("getProductCategoryId CANNOT BE NULL");
		}else {
			return repository.save(productCategory);
		}
	}
	
	public Integer deleteProductCategory(Integer productCategoryId) {
		if(repository.existsById(productCategoryId)) {
			repository.deleteById(productCategoryId);
			return 1;
		}
		return -1;
	}
	
	
	public List<ProductCategoryVO> getAllCategoryByRest(Integer restId) {
		return repository.getCategoryListByRest(restId);
	}
	
	
}
