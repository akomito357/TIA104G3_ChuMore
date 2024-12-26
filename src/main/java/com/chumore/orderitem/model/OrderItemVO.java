package com.chumore.orderitem.model;

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


@Entity
@Table(name = "order_item")
public class OrderItemVO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_item_id")
	private Integer orderItemId;
	
//	@ManyToOne
//	@JoinColumn(name = "order_id")
//	private OrderVO order;
	
	@Column(name = "order_id")
	private Integer orderId;
	
	@Column(name = "memo")
	private String memo;
	
	@Column(name = "created_datetime")
	private Timestamp createdDatetime;
	
	@Column(name = "updated_datetime")
	private Timestamp updatedDatetime;
	
	
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	public Integer getOrderItemId() {
		return orderItemId;
	}
	public void setOrderItemId(Integer orderItemId) {
		this.orderItemId = orderItemId;
	}
	
//	public OrderVO getOrder() {
//		return order;
//	}
//	public void setOrder(OrderVO order) {
//		this.order = order;
//	}
	public Integer getOrderId() {
		return orderId;
	}
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
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
		if(createdDatetime != null) {
			return createdDatetime.toLocalDateTime().format(FORMATTER);
		}else {
			return null;
		}
	}
	
	public String getFormatUpdatedDatetime() {
		if(updatedDatetime != null) {
			return updatedDatetime.toLocalDateTime().format(FORMATTER);
		}else {
			return null;
		}
		
	}
	
}
