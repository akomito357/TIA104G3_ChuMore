package com.chumore.ordermaster.compositequery;

import java.sql.Timestamp;
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

import com.chumore.member.model.MemberVO;
import com.chumore.ordermaster.model.OrderMasterVO;
import com.chumore.rest.model.RestVO;

public class OrderMasterCompositeQuery {
	
	public static Predicate getaPredicateForOrderMaster(CriteriaBuilder builder, Root<OrderMasterVO> root, String columnName, String value){
		
		Predicate predicate = null;
		if("orderId".equals(columnName) || "orderStatus".equals(columnName) || "pointEarned".equals(columnName) || "pointUsed".equals(columnName)) {
			// Integer
			predicate = builder.equal(root.get(columnName), Integer.valueOf(value));
		} else if ("subtotalPrice".equals(columnName) || "totalPrice".equals(columnName)){
			// Double
			predicate = builder.equal(root.get(columnName), Double.valueOf(value));
		} else if ("servedDatetime".equals(columnName) || "checkoutDatetime".equals(columnName)) {
			// Datetime
			predicate = builder.equal(root.get(columnName), Timestamp.valueOf(value));
		} else if ("orderTableId".equals(columnName)) {
			
		} else if ("restId".equals(columnName)) {
			RestVO rest = new RestVO();
			rest.setRestId(Integer.valueOf(value));
			predicate = builder.equal(root.get("rest"), rest);
		} else if ("memberId".equals(columnName)) {
			MemberVO member = new MemberVO();
			member.setMemberId(Integer.valueOf(value));
			predicate = builder.equal(root.get("member"), member);
		}
		
		return predicate;
	}
	
	public static List<OrderMasterVO> getAllC(Map<String, String[]> map, Session session){
		session.beginTransaction();
		List<OrderMasterVO> resultList = null;
		
		try {
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery criteriaQuery = criteriaBuilder.createQuery(OrderMasterVO.class);
			Root<OrderMasterVO> root = criteriaQuery.from(OrderMasterVO.class);
			
			List<Predicate> predicateList = new ArrayList<>();
			
			Set<String> keys = map.keySet();
			int count = 0;
			for (String key : keys) {
				String value = map.get(key)[0];
				if(value != null && value.trim().length() != 0 && !"action".equals(key)) {
					count ++;					
					predicateList.add(getaPredicateForOrderMaster(criteriaBuilder, root, key, value.trim()));
				}
			}
			criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));
			criteriaQuery.orderBy(criteriaBuilder.asc(root.get("orderMasterId")));
			
			TypedQuery query = session.createQuery(criteriaQuery);
			resultList = query.getResultList();
			
			session.getTransaction().commit();
		} catch(RuntimeException re) {
			re.printStackTrace();
			session.getTransaction().rollback();
		} finally {
			session.close();
		}
		
		return resultList;
	}

}
