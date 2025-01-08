package com.chumore.ordertable.model;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("OrderTableService")
public class OrderTableService {
	@Autowired
	OrderTableRepository repository;
	
	@Autowired
	HttpSession session;

	 private Integer getRestIdFromSession() {
	        Integer restId = (Integer) session.getAttribute("restId");
	        if (restId == null) {
	            throw new RuntimeException("未找到餐廳資訊，請重新登入");
	        }
	        return restId;
	    }
	    
	    
	    public OrderTableVO addOrderTable(OrderTableVO ordertable) {
	            return repository.save(ordertable);
	    }

	public List<OrderTableVO> getAllByRestId(Integer restId) {
		return repository.findByRestId(restId);
	}
	
	public String getTableNumberById(Integer orderTableId) {
		return repository.getTableNumberById(orderTableId);
	}
	

    public OrderTableVO getOrderTableById(Integer orderTableId) {
        return repository.findById(orderTableId)
            .orElseThrow(() -> new RuntimeException("找不到此桌位"));
    }
    
    
    public OrderTableVO updateOrderTable(OrderTableVO orderTable) {
        if (orderTable.getOrderTableId() == null) {
            throw new RuntimeException("桌位ID不能為空");
        }
        return repository.save(orderTable);
    }

	public void deleteOrderTable(Integer orderTableId) {
		repository.deleteById(orderTableId);
	}
	

}
