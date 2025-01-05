package com.chumore.ordertable.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.chumore.ordermaster.model.OrderMasterVO;
import com.chumore.rest.model.RestVO;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "order_table")
public class OrderTableVO implements java.io.Serializable{
	
	@Id
	@Column(name = "order_table_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer orderTableId;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "rest_id", referencedColumnName = "rest_id")
	@JsonBackReference("orderTable-rest")
	private RestVO rest;
	
	@Column(name = "table_number")
	private String tableNumber;
	
	@Column(name = "order_table_url")
	private String orderTableUrl;
	
	@OneToMany(mappedBy = "orderTable", cascade = CascadeType.ALL)
	private Set<OrderMasterVO> orderMasters;

	public Integer getOrderTableId() {
		return orderTableId;
	}

	public void setOrderTableId(Integer orderTableId) {
		this.orderTableId = orderTableId;
	}

	public RestVO getRest() {
		return rest;
	}

	public void setRest(RestVO rest) {
		this.rest = rest;
	}

	public String getTableNumber() {
		return tableNumber;
	}

	public void setTableNumber(String tableNumber) {
		this.tableNumber = tableNumber;
	}

	public String getOrderTableUrl() {
		return orderTableUrl;
	}

	public void setOrderTableUrl(String orderTableUrl) {
		this.orderTableUrl = orderTableUrl;
	}
}
