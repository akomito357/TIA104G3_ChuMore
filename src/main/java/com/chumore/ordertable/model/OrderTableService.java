package com.chumore.ordertable.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service("OrderTableService")
public class OrderTableService {
	@Autowired
	OrderTableRepository repository;
	
	public void addOrderTable(OrderTableVO orderTable) {
		repository.save(orderTable);	
	}
	public void updateOrderTable(OrderTableVO orderTable) {
		repository.save(orderTable);	
	}
	public void deleteOrderTable(Integer orderTableId) {
		if (repository.existsById(orderTableId))
			repository.deleteByOrderTableId(orderTableId);
	}
	
	public OrderTableVO getOneOrderTable(Integer orderTableId) {
		Optional<OrderTableVO> optional = repository.findById(orderTableId);
		return optional.orElse(null);
	}
	
	public List<OrderTableVO> getAllByRestId(Integer restId){
		return repository.findByRestId(restId);
	}

}
