package com.chumore.product.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<ProductVO, Integer>{

	@Query("FROM ProductVO p WHERE p.restId = :restId ORDER BY p.productCategoryId")
	List<ProductVO> getMenuByRestId(Integer restId);

	
	@Query("FROM ProductVO p WHERE p.restId = :restId AND p.supplyStatus = 0 ORDER BY p.productCategoryId")
	List<ProductVO> getMenuBySupplyStatus(Integer restId);
}
