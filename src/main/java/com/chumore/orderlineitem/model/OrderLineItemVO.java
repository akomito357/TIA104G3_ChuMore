package com.chumore.orderlineitem.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.chumore.orderitem.model.OrderItemVO;
import com.chumore.product.model.ProductVO;

@Entity
@Table(name = "order_line_item")
public class OrderLineItemVO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_line_item_id")
	private Integer orderLineItemId;
	
	@Column(name = "order_item_id")
	private Integer orderItemId;
	
//	@ManyToOne
//	@JoinColumn(name = "order_item_id")
//	private OrderItemVO orderItem;
	
	@Column(name = "product_id")
	private Integer productId;
	
//	@ManyToOne
//	@JoinColumn(name = "product_id")
//	private ProductVO product;

	@Column(name = "quantity")	
	private Integer quantity;
	
	@Column(name = "price")
	private BigDecimal price;
	
	@Column(name = "created_datetime")
	private Timestamp createdDatetime;
	
	@Column(name = "updated_datetime")
	private Timestamp updatedDatetime;
	
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			
	public Integer getOrderLineItemId() {
		return orderLineItemId;
	}
	public void setOrderLineItemId(Integer orderLineItemId) {
		this.orderLineItemId = orderLineItemId;
	}
	
	public Integer getOrderItemId() {
		return orderItemId;
	}
	public void setOrderItemId(Integer orderItemId) {
		this.orderItemId = orderItemId;
	}
//	public OrderItemVO getOrderItem() {
//		return orderItem;
//	}
//	public void setOrderItem(OrderItemVO orderItem) {
//		this.orderItem = orderItem;
//	}
	public Integer getProductId() {
		return productId;
	}
	public void setProductId(Integer productId) {
		this.productId = productId;
	}
	
//	public ProductVO getProduct() {
//		return product;
//	}
//	public void setProduct(ProductVO product) {
//		this.product = product;
//	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public Timestamp getCreatedDatetime() {
		return createdDatetime;
	}
	public void setCreatedDatetime(Timestamp createdDatetime) {
		this.createdDatetime = createdDatetime;
	}
	public Timestamp getUpdatedDatetime() {
		return updatedDatetime;
	}
	public void setUpdatedDatetime(Timestamp updatedDatetime) {
		this.updatedDatetime = updatedDatetime;
	}
	public String getFormatCreatedDatetime() {
		return createdDatetime.toLocalDateTime().format(FORMATTER);
	}	
	public String getFormatUpdatedDatetime() {
		return updatedDatetime.toLocalDateTime().format(FORMATTER);
	}
	
}
