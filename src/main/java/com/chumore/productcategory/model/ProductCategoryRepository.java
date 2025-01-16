package com.chumore.productcategory.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ProductCategoryRepository extends JpaRepository<ProductCategoryVO, Integer> {
	@Query("FROM ProductCategoryVO pc WHERE pc.restId = :restId")
	List<ProductCategoryVO> getCategoryListByRest(Integer restId);

	@Transactional
	@Modifying
	@Query(value = "UPDATE product_category SET category_name = :categoryName WHERE product_category_id = :productCategoryId", nativeQuery = true)
	int updateProductCategoryName(String categoryName, Integer productCategoryId);

	@Transactional
	@Modifying
	@Query(value = "UPDATE product_category p set enable_status = 0 where product_category_id = :productCategoryId", nativeQuery = true)
	int deleteProductCategory(Integer productCategoryId);
}