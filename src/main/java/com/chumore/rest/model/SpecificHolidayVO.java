package com.chumore.rest.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "specific_holiday")
public class SpecificHolidayVO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "specific_holiday_id")
	private Integer SpecificHolidayId;
	
	@Column(name = "rest_id")
	private	Integer restId;
	
	@Column(name = "day")
	private	 LocalDate day;
	
	
	public Integer getSpecificHolidayId() {
		return SpecificHolidayId;
	}

	public void setSpecificHolidayId(Integer specificHoliday) {
		SpecificHolidayId = specificHoliday;
	}

	public Integer getRestId() {
		return restId;
	}

	public void setRestId(Integer restId) {
		this.restId = restId;
	}

	public LocalDate getDay() {
		return day;
	}

	public void setDay(LocalDate day) {
		this.day = day;
	}


}
