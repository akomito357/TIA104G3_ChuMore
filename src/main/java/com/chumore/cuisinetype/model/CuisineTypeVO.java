package com.chumore.cuisinetype.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.chumore.rest.model.RestVO;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "cuisine_type")
public class CuisineTypeVO implements Serializable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cuisine_type_id", insertable = false)
	private Integer cuisineTypeId;
	
	@OneToMany(mappedBy = "cuisineType", cascade = CascadeType.ALL)
	@JsonManagedReference("cuisineType-rest")
	private Set<RestVO> rests;

	@Column(name = "cuisine_descr")
	private String cuisineDescr;
	
	
	public CuisineTypeVO() {	
	}

	public Integer getCuisineTypeId() {
		return cuisineTypeId;
	}

	public void setCuisineTypeId(Integer cuisineTypeId) {
		this.cuisineTypeId = cuisineTypeId;
	}

	public String getCuisineDescr() {
		return cuisineDescr;
	}

	public void setCuisineDescr(String cuisineDescr) {
		this.cuisineDescr = cuisineDescr;
	}

	public Set<RestVO> getRests() {
		return rests;
	}

	public void setRests(Set<RestVO> rests) {
		this.rests = rests;
	}

	@Override
	public String toString() {
		return "CuisineTypeVO [cuisineTypeId=" + cuisineTypeId + ", cuisineDescr=" + cuisineDescr + "]";
	}
	
	
	
}
