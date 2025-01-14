package com.chumore.productcategory.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chumore.product.model.ProductVO;
import com.chumore.product.model.Product_Service;

@Service("productCategorySvc")
public class ProductCategory_Service {

	@Autowired
	ProductCategoryRepository repository;

	@Autowired
	Product_Service productSvc;

	public ProductCategoryVO addProductCategory(ProductCategoryVO productCategory) {
		return repository.save(productCategory);
	}

	public ProductCategoryVO updateProductCategory(ProductCategoryVO productCategory) {
		if (productCategory.getProductCategoryId() == null) {
			throw new IllegalArgumentException("getProductCategoryId CANNOT BE NULL");
		}

		ProductCategoryVO exstingProductCategory = repository.findById(productCategory.getProductCategoryId())
				.orElse(null);

		exstingProductCategory.setCategoryName(productCategory.getCategoryName());

		exstingProductCategory = repository.save(exstingProductCategory);
		
		List<ProductVO> updateProducts = productCategory.getProductList();
		List<ProductVO> exstingProducts = exstingProductCategory.getProductList();

//		if (exstingProductCategory != null) {
//			productCategory.setRestId(exstingProductCategory.getRestId());
//		}
		List<ProductVO> products = new ArrayList();
		
		for (ProductVO product : updateProducts) {
			if (product.getProductId() == null) {
				product.setRestId(exstingProductCategory.getRestId());
				product.setProductCategoryId(exstingProductCategory.getProductCategoryId());
			} else {
				ProductVO exstingProduct = null;
				for (ProductVO p : exstingProducts) {
					if (p.getProductId().equals(product.getProductId())) {
						exstingProduct = p;
						break;
					}
				}

				if (exstingProduct != null) {
					product.setRestId(exstingProduct.getRestId());
					product.setProductCategoryId(exstingProductCategory.getProductCategoryId());
//					exstingProduct.setProductName(product.getProductName());
//					exstingProduct.setProductPrice(product.getProductPrice());
//					exstingProduct.setProductDescription(product.getProductDescription());
//					exstingProduct.setSupplyStatus(product.getSupplyStatus());
//					exstingProduct.setProductImage(product.getProductImage());
				} else {
					throw new IllegalArgumentException("Product not found for update");
				}
			}
			ProductVO productItemUpdate = productSvc.addProduct(product);
			products.add(productItemUpdate);
		}
		
		exstingProductCategory.setProductList(products);
		
		return exstingProductCategory;

	}

	public Integer deleteProductCategory(Integer productCategoryId) {
		if (repository.existsById(productCategoryId)) {
			repository.deleteById(productCategoryId);
			return 1;
		}
		return -1;
	}

	public List<ProductCategoryVO> getAllCategoryByRest(Integer restId) {
		return repository.getCategoryListByRest(restId);
	}

}
