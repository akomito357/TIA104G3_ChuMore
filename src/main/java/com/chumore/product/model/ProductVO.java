package com.chumore.product.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "product")
public class ProductVO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_id", updatable = false)
	private Integer productId;
	
	@Column(name = "rest_id")
	private Integer restId;
	
	@Column(name = "product_category_id")
	private Integer productCategoryId;

//	@ManyToOne
//	@JoinColumn(name = "product_category_id")
//	private ProductCategory productCategory;
	
	@Column(name = "product_name")
	private String productName;
	
	@Column(name = "product_description")
	private String productDescription;
	
	@Column(name = "product_price")
	private BigDecimal ProductPrice;
	
	@Column(name = "product_image", columnDefinition = "mediumblob")
	private byte[] productImage;
	
	@Column(name ="supply_status")
	private Integer supplyStatus;
	
	public Integer getProductId() {
		return productId;
	}
	public void setProductId(Integer productId) {
		this.productId = productId;
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
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getProductDescription() {
		return productDescription;
	}
	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}
	public BigDecimal getProductPrice() {
		return ProductPrice;
	}
	public void setProductPrice(BigDecimal productPrice) {
		ProductPrice = productPrice;
	}
	public byte[] getProductImage() {
		return productImage;
	}
	public void setProductImage(byte[] productImage) {
		this.productImage = productImage;
	}
	public Integer getSupplyStatus() {
		return supplyStatus;
	}
	public void setSupplyStatus(Integer supplyStatus) {
		this.supplyStatus = supplyStatus;
	}
}
