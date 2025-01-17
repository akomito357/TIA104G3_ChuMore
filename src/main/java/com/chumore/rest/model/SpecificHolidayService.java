package com.chumore.rest.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("SpecificHoliday")
public class SpecificHolidayService {
	
	@Autowired
	SpecificHolidayRepository repository;
	
	public void addHoliday(SpecificHolidayVO specificHoliday) {
		repository.save(specificHoliday);
	}
	
	public void deleteHoliday(Integer specificHolidayId) {
		if(repository.existsById(specificHolidayId))
		repository.deleteBySpecificHolidayId(specificHolidayId);
	}
	public List<SpecificHolidayVO> getHolidaysByRestId(Integer restId) {
        return repository.findByRestId(restId);
    }
	
    public SpecificHolidayVO getHolidayById(Integer specificHolidayId) {
        return repository.findById(specificHolidayId)
            .orElse(null);
    }
}
