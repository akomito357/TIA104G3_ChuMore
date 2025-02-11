package com.chumore.product.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("productSvc")
public class Product_Service {
	
	@Autowired
	ProductRepository repository;
	
	
	public ProductVO addProduct(ProductVO product) {
		return repository.save(product);
	}
	
	public ProductVO updateProduct(ProductVO product) {
		return repository.save(product);
	}
	
	public void deleteProductsByIds(List<Integer> productIds) {
	    repository.deleteAllById(productIds);
	}
	
	public List<ProductVO> getAllProductByRestId(Integer restId) {
		return repository.getMenuByRestId(restId);
	}

	public List<ProductVO> getAllProductByActiveStatus(Integer restId) {
		return repository.getMenuBySupplyStatus(restId);
	}
	
	public ProductVO getProductById(Integer productId) {
		return repository.findById(productId).orElse(null);
	}
	
	public List<ProductVO> getAllProductByActiveStatusAndSelected(Integer restId, 
			ProductVO selectedProduct){
		// for review showing
		
		List<ProductVO> availableProduct = getAllProductByActiveStatus(restId);
		Boolean isAvailable = false;
		
		if (availableProduct != null) {
			for (ProductVO product: availableProduct) {
				if (product.getProductId().equals(selectedProduct.getProductId())) {
					isAvailable = true;
					break;
				}
			}
		}
		if (!isAvailable || availableProduct.isEmpty()) {
			availableProduct.add(selectedProduct);
		}
		
		return availableProduct;
	}
		
	public List<ProductVO> getProductByProductCatId(Integer productCategoryId) {
		return repository.getProductByProductCatId(productCategoryId);
	}
	
	public void deleteProduct(Integer productId) {
	    repository.deleteProduct(productId);
	}
}