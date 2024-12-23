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

@Entity
@Table(name = "cuisine_type")
public class CuisineTypeVO implements Serializable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cuisine_type_id", insertable = false)
	private Integer cuisineTypeId;
	
	@OneToMany(mappedBy = "cuisineType", cascade = CascadeType.ALL)
	private Set<RestVO> rest;

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
		return rest;
	}

	public void setRests(Set<RestVO> rest) {
		this.rest = rest;
	}

	@Override
	public String toString() {
		return "CuisineTypeVO [cuisineTypeId=" + cuisineTypeId + ", cuisineDescr=" + cuisineDescr + "]";
	}
	
	
	
}
