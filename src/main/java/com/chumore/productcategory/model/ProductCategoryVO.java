package com.chumore.productcategory.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.chumore.product.model.ProductVO;

@Entity
@Table(name = "product_category")
public class ProductCategoryVO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_category_id", updatable = false)
	private Integer productCategoryId;

//	@ManyToOne
//	@JoinColumn(name = "rest_id")
//	private RestVO rest;	

	@Column(name = "rest_id")
	private Integer restId;

	@Column(name = "category_name")
	private String categoryName;

	@OneToMany(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "product_category_id", referencedColumnName = "product_category_id")
	private List<ProductVO> productList;

	public Integer getProductCategoryId() {
		return productCategoryId;
	}

	public void setProductCategoryId(Integer productCategoryId) {
		this.productCategoryId = productCategoryId;
	}

//	public RestVO getRest() {
//		return rest;
//	}
//
//	public void setRest(RestVO rest) {
//		this.rest = rest;
//	}

	public Integer getRestId() {
		return restId;
	}

	public void setRestId(Integer restId) {
		this.restId = restId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public List<ProductVO> getProductList() {
		return productList;
	}

	public void setProductList(List<ProductVO> productList) {
		this.productList = productList;
	}
	
}
