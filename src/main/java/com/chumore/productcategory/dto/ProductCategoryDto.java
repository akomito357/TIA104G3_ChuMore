package com.chumore.productcategory.dto;

import java.math.BigDecimal;
import java.util.List;

public class ProductCategoryDto {
	 private Integer productCategoryId;
	    private String categoryName;
	    private Integer restId;
	    private Integer enableStatus;
	    private List<ProductDTO> productList;
	    
	    
		public ProductCategoryDto() {
			super();
			// TODO Auto-generated constructor stub
		}
		
		public ProductCategoryDto(Integer productCategoryId, String categoryName, Integer restId, Integer enableStatus,
				List<ProductDTO> productList) {
			super();
			this.productCategoryId = productCategoryId;
			this.categoryName = categoryName;
			this.restId = restId;
			this.enableStatus = enableStatus;
			this.productList = productList;
		}

		public Integer getProductCategoryId() {
			return productCategoryId;
		}
		public void setProductCategoryId(Integer productCategoryId) {
			this.productCategoryId = productCategoryId;
		}
		public String getCategoryName() {
			return categoryName;
		}
		public void setCategoryName(String categoryName) {
			this.categoryName = categoryName;
		}
		public List<ProductDTO> getProductList() {
			return productList;
		}
		public void setProductList(List<ProductDTO> productList) {
			this.productList = productList;
		}
	    
		public Integer getRestId() {
			return restId;
		}
		public void setRestId(Integer restId) {
			this.restId = restId;
		}

		public Integer getEnableStatus() {
			return enableStatus;
		}
		public void setEnableStatus(Integer enableStatus) {
			this.enableStatus = enableStatus;
		}


		public static class ProductDTO {
			private Integer restId;
			private Integer productCategoryId;
			private Integer productId;
			private String productName;
			private BigDecimal productPrice;
			private String productDescription;
			private Integer supplyStatus;
			private byte[] productImage;
			
			
			public ProductDTO() {
				super();
				// TODO Auto-generated constructor stub
			}
			public ProductDTO(Integer restId, Integer productCategoryId, Integer productId, String productName,
					BigDecimal productPrice, String productDescription, Integer supplyStatus, byte[] productImage) {
				super();
				this.restId = restId;
				this.productCategoryId = productCategoryId;
				this.productId = productId;
				this.productName = productName;
				this.productPrice = productPrice;
				this.productDescription = productDescription;
				this.supplyStatus = supplyStatus;
				this.productImage = productImage;
			}
			public Integer getProductId() {
				return productId;
			}
			public void setProductId(Integer productId) {
				this.productId = productId;
			}
			public String getProductName() {
				return productName;
			}
			public void setProductName(String productName) {
				this.productName = productName;
			}
			public BigDecimal getProductPrice() {
				return productPrice;
			}
			public void setProductPrice(BigDecimal productPrice) {
				this.productPrice = productPrice;
			}
			public String getProductDescription() {
				return productDescription;
			}
			public void setProductDescription(String productDescription) {
				this.productDescription = productDescription;
			}
			public Integer getSupplyStatus() {
				return supplyStatus;
			}
			public void setSupplyStatus(Integer supplyStatus) {
				this.supplyStatus = supplyStatus;
			}
			public byte[] getProductImage() {
				return productImage;
			}
			public void setProductImage(byte[] productImage) {
				this.productImage = productImage;
			}
			public Integer getRestId() {
				return restId;
			}
			public void setRestId(Integer restId) {
				this.restId = restId;
			}
			public Integer getProductCategoryId() {
				return productCategoryId;
			}
			public void setProductCategoryId(Integer productCategoryId) {
				this.productCategoryId = productCategoryId;
			}
			
}


}