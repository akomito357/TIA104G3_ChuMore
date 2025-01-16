package com.chumore.product.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ProductRepository extends JpaRepository<ProductVO, Integer>{

	@Query("FROM ProductVO p WHERE p.restId = :restId ORDER BY p.productCategoryId")
	List<ProductVO> getMenuByRestId(Integer restId);

	
	@Query("FROM ProductVO p WHERE p.restId = :restId AND p.supplyStatus = 0 ORDER BY p.productCategoryId")
	List<ProductVO> getMenuBySupplyStatus(Integer restId);
	

	@Query("FROM ProductVO p WHERE p.productCategoryId = :productCategoryId")
	List<ProductVO> getProductListByProductCatId(Integer productCategoryId);
	
	@Query("FROM ProductVO p WHERE p.productCategoryId = :productCategoryId")
	List<ProductVO> getProductByProductCatId(Integer productCategoryId);
	
	@Transactional
	@Modifying
	@Query(value="UPDATE product p set supply_status = 3 where product_id = :productId", nativeQuery = true)
	int deleteProduct(Integer productId);
}
