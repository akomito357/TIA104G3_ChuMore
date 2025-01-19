package com.chumore.productcategory.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chumore.product.model.ProductRepository;
import com.chumore.product.model.ProductVO;
import com.chumore.product.model.Product_Service;
import com.chumore.productcategory.dto.ProductCategoryDto;
import com.chumore.productcategory.dto.ProductCategoryDto.ProductDTO;
import com.chumore.rest.model.RestVO;

@Service("productCategorySvc")
public class ProductCategory_Service {

	@Autowired
	ProductCategoryRepository repository;

	@Autowired
	Product_Service productSvc;

	@Autowired
	ProductRepository productRepository;
	
	@Autowired
	HttpSession session;

	public ProductCategoryVO addProductCategory(ProductCategoryVO productCategory) {
		return repository.save(productCategory);
	}

	
	public ProductCategoryVO addProductCat(ProductCategoryDto productCategoryDto) {
		Object restNum = session.getAttribute("restId");
		Integer restId = null;
		if (restNum == null) {
			restId = 2001;
		} else {
			RestVO rest = (RestVO) restNum;
			restId = rest.getRestId();
		}
	    ProductCategoryVO productCategoryVO = new ProductCategoryVO();
	    productCategoryVO.setCategoryName(productCategoryDto.getCategoryName());
	    productCategoryVO.setRestId(restId);
	    productCategoryVO.setEnableStatus(1);
	    
	    productCategoryVO = repository.save(productCategoryVO);
	    Integer productCategoryId = productCategoryVO.getProductCategoryId();
	    
	    List<ProductVO> newProducts = new ArrayList<>();
	    for (ProductDTO productDTO : productCategoryDto.getProductList()) {
	        ProductVO product = new ProductVO();
	        product.setRestId(restId);
	        product.setProductCategoryId(productCategoryId); //新分類的productCategoryId

	        product.setProductName(productDTO.getProductName());
	        product.setProductPrice(productDTO.getProductPrice());
	        product.setProductDescription(productDTO.getProductDescription());
	        product.setSupplyStatus(productDTO.getSupplyStatus());
	        
	        if(productDTO.getProductImage() == null || productDTO.getProductImage().length==0) {
	        	product.setProductImage(null);
	        }else {
	        	product.setProductImage(productDTO.getProductImage());
	        }
	        
	        newProducts.add(product);
	    }

	    productRepository.saveAll(newProducts);

	    return productCategoryVO;
	}
	public ProductCategoryVO updateProductCategory(ProductCategoryDto productCategoryDto) {
		if (productCategoryDto.getProductCategoryId() == null) {
			throw new IllegalArgumentException("getProductCategoryId CANNOT BE NULL");
		}

		ProductCategoryVO exstingProductCategory = repository.findById(productCategoryDto.getProductCategoryId())
				.orElse(null);
		
		UpdateUtil.updateIfChanged(
		        exstingProductCategory.getEnableStatus(),
		        productCategoryDto.getEnableStatus(),
		        exstingProductCategory::setEnableStatus
		    );
		
		UpdateUtil.updateIfChanged(exstingProductCategory.getCategoryName(), productCategoryDto.getCategoryName(),
				exstingProductCategory::setCategoryName);

		List<ProductVO> updatedProducts = new ArrayList();

		for (ProductDTO productDTO : productCategoryDto.getProductList()) {
			ProductVO product;
			if (productDTO.getProductId() == null) {
				product = new ProductVO();
				product.setRestId(exstingProductCategory.getRestId());
				product.setProductCategoryId(exstingProductCategory.getProductCategoryId());
				
			} else {
				product = productRepository.findById(productDTO.getProductId())
						.orElseThrow(() -> new IllegalArgumentException("Product not found for update"));
			}

			UpdateUtil.updateIfChanged(product.getProductName(), productDTO.getProductName(), product::setProductName);
			UpdateUtil.updateIfChanged(product.getProductPrice(), productDTO.getProductPrice(),
					product::setProductPrice);
			UpdateUtil.updateIfChanged(product.getProductDescription(), productDTO.getProductDescription(),
					product::setProductDescription);
			UpdateUtil.updateIfChanged(product.getSupplyStatus(), productDTO.getSupplyStatus(),
					product::setSupplyStatus);
			UpdateUtil.updateIfChanged(product.getProductImage(), productDTO.getProductImage(),
					product::setProductImage);
	        UpdateUtil.updateIfChanged(product.getProductImage(), productDTO.getProductImage(), product::setProductImage);
			
			updatedProducts.add(product);

		}
		productRepository.saveAll(updatedProducts);
		exstingProductCategory = repository.save(exstingProductCategory);
		return exstingProductCategory;
	}


	public byte[] getImageById(Integer productId) {
		Optional<ProductVO> productOptional = productRepository.findById(productId);
		if(productOptional.isPresent()) {
			return productOptional.get().getProductImage();
		}
		return null;
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

	public void deleteCategoriesById(List<Integer> productCategoryIds) {
		repository.deleteAllById(productCategoryIds);
	}
	
	public void deleteCategory(Integer productCategoryId) {
		repository.deleteProductCategory(productCategoryId);
	}
	
	public void batchDelete(MenuUploadReq request) {
		
		List<Integer> productIdToDelete = request.getDeleteProductList();
		
		List<Integer> categoryIdToDelete = request.getDeleteCatList();
		
		if(!productIdToDelete.isEmpty()) {
			for(Integer productId : productIdToDelete) {
				productSvc.deleteProduct(productId);
			}
		}
		
		if(!categoryIdToDelete.isEmpty()) {
			for(Integer categoryId : categoryIdToDelete) {
				List<ProductVO> products = productSvc.getProductByProductCatId(categoryId);
				for(ProductVO product: products) {
					productSvc.deleteProduct(product.getProductId());
				}
				deleteCategory(categoryId);
			}
			
		}
	}
	public List<ProductCategoryVO> getActiveListByRestId(Integer restId) {
		 List<ProductCategoryVO> productCategoryList = repository.getCategoryListByRest(restId);
				 
		 return productCategoryList.stream()
				 .filter(pc -> pc.getEnableStatus() == 1)
	                .peek(pc -> pc.setProductList(pc.getProductList().stream()
	                        .filter(p -> p.getSupplyStatus() == 0 || p.getSupplyStatus() == 1 || p.getSupplyStatus() == 2)
	                        .collect(Collectors.toList())))
	                .collect(Collectors.toList());
	}
	
	 public Map<Integer, List<ProductCategoryVO>> groupByCategoryId(List<ProductCategoryVO> productCategoryList) {
	        return productCategoryList.stream()
	                .collect(Collectors.groupingBy(ProductCategoryVO::getProductCategoryId));
	    }
}