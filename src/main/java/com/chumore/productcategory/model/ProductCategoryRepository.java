package com.chumore.productcategory.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductCategoryRepository extends JpaRepository<ProductCategoryVO, Integer>{
	@Query("FROM ProductCategoryVO pc WHERE pc.restId = :restId")
	List<ProductCategoryVO> getCategoryListByRest(Integer restId);
	
	
	
}