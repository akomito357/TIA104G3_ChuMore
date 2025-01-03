package com.chumore.orderlineitem.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import com.chumore.orderitem.model.OrderItemVO;
import com.chumore.orderlineitem.model.OrderLineItemVO;
import com.chumore.ordermaster.model.OrderMasterVO;
import com.chumore.product.model.ProductVO;

public class OrderLineItemDto {

	private BigDecimal subtotalPrice; // orderMaster
	private Integer pointUsed; // orderMaster
	private BigDecimal totalPrice; // orderMaster
	private List<OrderItemListDto> orderItemListDto;

	public OrderLineItemDto() {
	}

	public OrderLineItemDto(OrderMasterVO data) {
		setSubtotalPrice(data.getSubtotalPrice());
		setPointUsed(data.getPointUsed());
		setTotalPrice(data.getTotalPrice());
	}

	public BigDecimal getSubtotalPrice() {
		return subtotalPrice;
	}

	public void setSubtotalPrice(BigDecimal subtotalPrice) {
		this.subtotalPrice = subtotalPrice;
	}

	public Integer getPointUsed() {
		return pointUsed;
	}

	public void setPointUsed(Integer pointUsed) {
		this.pointUsed = pointUsed;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}
	
	public List<OrderItemListDto> getOrderItemListDto() {
		return orderItemListDto;
	}

	public void setOrderItemListDto(List<OrderItemListDto> orderItemListDto) {
		this.orderItemListDto = orderItemListDto;
	}



	public static class OrderItemListDto {
		private Integer orderItemId; // order_line_item
		private String memo; // order item
		private Timestamp updatedDatetime; // order_item
		private List<LineItemDto> lineItemList;

		public Integer getOrderItemId() {
			return orderItemId;
		}

		public void setOrderItemId(Integer orderItemId) {
			this.orderItemId = orderItemId;
		}

		public String getMemo() {
			return memo;
		}

		public void setMemo(String memo) {
			this.memo = memo;
		}

		public Timestamp getUpdatedDatetime() {
			return updatedDatetime;
		}

		public void setUpdatedDatetime(Timestamp updatedDatetime) {
			this.updatedDatetime = updatedDatetime;
		}

		public List<LineItemDto> getLineItemList() {
			return lineItemList;
		}

		public void setLineItemList(List<LineItemDto> lineItemList) {
			this.lineItemList = lineItemList;
		}

		public OrderItemListDto() {

		}

		public OrderItemListDto(OrderItemVO data) {
			setOrderItemId(data.getOrderItemId());
			setMemo(data.getMemo());
		}

	}

	public static class LineItemDto {
		private String productName; // product
		private Integer quantity; // order_line_item
		private BigDecimal price; // order_line_item

		public String getProductName() {
			return productName;
		}

		public void setProductName(String productName) {
			this.productName = productName;
		}

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

		public LineItemDto() {

		}

		public LineItemDto(OrderLineItemVO data, ProductVO product) {
			setProductName(product.getProductName());
			setQuantity(data.getQuantity());
			setPrice(data.getPrice());
		}

	}

}

class OrderItemDtoForOrder{
	private Integer orderItemId;
	private String memo;
	private Timestamp updateDatetime;
	
	public Integer getOrderItemId() {
		return orderItemId;
	}
	public void setOrderItemId(Integer orderItemId) {
		this.orderItemId = orderItemId;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public Timestamp getUpdateDatetime() {
		return updateDatetime;
	}
	public void setUpdateDatetime(Timestamp updateDatetime) {
		this.updateDatetime = updateDatetime;
	}
	
}

