package com.chumore.rest.compositequery;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

import com.chumore.rest.model.RestVO;

public class CompositeQueryRest {
	
	public static Predicate getaPrecidateForRest(CriteriaBuilder builder, Root<RestVO> root, String columnName, String value) {
		Predicate predicate = null;
		
		if ("rest_id".equals(columnName) || "business_status".equals(columnName)) {
			// Integer
		}
		
		
		return null;
	}

}
