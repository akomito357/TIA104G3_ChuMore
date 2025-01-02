package com.chumore.rest.compositequery;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.chumore.cuisinetype.model.CuisineTypeVO;
import com.chumore.rest.model.RestVO;

public class RestCompositeQuery {
	
	public static Predicate getaPrecidateForRest(CriteriaBuilder builder, Root<RestVO> root, String columnName, String value) {
		Predicate predicate = null;
		
		if ("restId".equals(columnName) || "businessStatus".equals(columnName) || "orderTableCount".equals(columnName)) {
			// Integer
			predicate = builder.equal(root.get(columnName), Integer.valueOf(value));
		} else if ("restStars".equals(columnName)){
			// Double
			predicate = builder.equal(root.get(columnName), Double.valueOf(value));
		} else if ("restName".equals(columnName) 
				|| "restCity".equals(columnName) 
				|| "restDist".equals(columnName) 
				|| "restAddress".equals(columnName) 
				|| "restRegist".equals(columnName) 
				|| "restPhone".equals(columnName) 
				|| "restIntro".equals(columnName) 
				|| "merchantName".equals(columnName)
				|| "merchantIdNumber".equals(columnName)
				|| "merchantEmail".equals(columnName)
				|| "merchantPassword".equals(columnName)
				|| "phoneNumber".equals(columnName)
				|| "weeklyBizDays".equals(columnName)
				|| "businessHours".equals(columnName)) {
			// varchar
			predicate = builder.like(root.get(columnName), "%" + value + "%");
		} else if ("registerDatetime".equals(columnName) || "createdDatetime".equals(columnName) || "updatedDatetime".equals(columnName)) {
			// datetime
			predicate = builder.equal(root.get(columnName), LocalDateTime.parse(value));
		} else if ("cuisineTypeId".equals(columnName)) {
			CuisineTypeVO cuisineType = new CuisineTypeVO();
			cuisineType.setCuisineTypeId(Integer.valueOf(value));
			predicate = builder.equal(root.get("cuisineType"), cuisineType);
		}
		
		return predicate;
	}

	@SuppressWarnings("unchecked")
	public static List<RestVO> getAllC(Map<String, String[]> map, Session session){
		Transaction tx = session.beginTransaction();
		List<RestVO> resultList = null;
		try {
			
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<RestVO> criteriaQuery = builder.createQuery(RestVO.class);
			Root<RestVO> root = criteriaQuery.from(RestVO.class);
			
			List<Predicate> predicateList = new ArrayList<>();
			
			Set<String> keys = map.keySet();
			int count = 0;
			for (String key : keys) {
				String value = map.get(key)[0];
				if (value != null && value.trim().length() != 0 && !"action".equals(key)) {
					count ++;
					predicateList.add(getaPrecidateForRest(builder, root, key, value.trim()));
				}
			}
			criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));
			criteriaQuery.orderBy(builder.asc(root.get("restId")));
			
			TypedQuery query = session.createQuery(criteriaQuery);
			resultList = query.getResultList();
			
			tx.commit();
		} catch(RuntimeException re) {
			if (tx != null) {				
				tx.rollback();
			}
			System.out.println(re.getMessage());
		} finally {
			session.close();
		}
		return resultList;
	}
	
	
}
