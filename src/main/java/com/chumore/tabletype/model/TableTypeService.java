package com.chumore.tabletype.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service("tableTypeService")
public class TableTypeService {
	@Autowired
	TableTypeRepository repository;
	
	public void addTableType(TableTypeVO tableType) {
		repository.save(tableType);	
	}
	public void updateTableType(TableTypeVO tableType) {
		repository.save(tableType);	
	}
	public void deleteTableType(Integer tableTypeId) {
		if (repository.existsById(tableTypeId))
			repository.deleteByTableTypeId(tableTypeId);
	}
	
	public List<TableTypeVO> getAll(){
		return repository.findAll();
	}
	
	public List<TableTypeVO>getAllTableTypeById(Integer restId){
		return repository.findByRestId(restId);
	}

}
