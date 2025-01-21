package com.chumore.rest.model;

import java.time.LocalDateTime;
import java.util.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.chumore.event.RestChangedEvent;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.chumore.approval.model.ApprovalVO;
import com.chumore.dailyreservation.model.DailyReservationVO;
import com.chumore.discpts.model.DiscPtsVO;
import com.chumore.envimg.model.EnvImgVO;
import com.chumore.exception.ResourceNotFoundException;
import com.chumore.favrest.model.FavRestVO;
import com.chumore.ordermaster.model.OrderMasterVO;
import com.chumore.ordertable.model.OrderTableVO;
import com.chumore.reservation.model.ReservationVO;
import com.chumore.rest.compositequery.RestCompositeQuery;
import com.chumore.util.ConverterUtil;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service("restService")
public class RestServiceImpl implements RestService{
	
	@Autowired
	RestRepository repository;

	@Autowired
	private ApplicationEventPublisher publisher;

	@PersistenceContext
	private EntityManager entityManager;

	private static final Logger logger = LoggerFactory.getLogger(RestServiceImpl.class);

	@Override
	public void addRest(RestVO rest) {
		repository.save(rest);
		publisher.publishEvent(new RestChangedEvent(this,rest, "ADD"));
	}
	

	@Override
	public void updateRest(RestVO rest) {
		logger.info("開始執行 updateRest 方法，restId: {}", rest.getRestId());
		try {
			repository.saveAndFlush(rest);
			logger.info("Rest entity saved and flushed successfully for restId: {}", rest.getRestId());

			publisher.publishEvent(new RestChangedEvent(this, rest, "UPDATE"));
			logger.info("RestChangedEvent published successfully for restId: {}", rest.getRestId());

			logger.info("更新成功 for restId: {}", rest.getRestId());
		} catch (Exception e) {
			logger.error("Error occurred while updating Rest entity for restId: {}", rest.getRestId(), e);
			throw e; // 根據需要重新拋出例外或處理
		}
	}
	
	
//	    @Autowired
//	    private PasswordEncoder passwordEncoder;
//	    
//	    @Override
//	    public boolean updatePassword(Integer restId, String oldPassword, String newPassword) {
//	        try {
//	            RestVO rest = getOneById(restId);
//	            if (rest == null) {
//	                return false;
//	            }
//	            if (!passwordEncoder.matches(oldPassword, rest.getMerchantPassword())) {
//	                return false;
//	            }
//
//	            rest.setMerchantPassword(passwordEncoder.encode(newPassword));
//	            updateRest(rest);
//	            
//	            return true;
//	        } catch (Exception e) {
//	            e.printStackTrace();
//            return false;
//        }
//	    }
	
	
	@Override
	@Transactional(readOnly = true)
	public RestVO getOneById(Integer restId) {
		Optional<RestVO> optional = repository.findById(restId);
		RestVO rest = optional.orElse(null);
		if (rest == null) {
			throw new ResourceNotFoundException("Rest with id = " + restId + "is not found.");
		}
		return rest;
	}

	@Override
	@Transactional(readOnly = true)
	public List<RestVO> getAll() {
		List<RestVO> rests = repository.findAll();
		if (rests.isEmpty()) {
			throw new ResourceNotFoundException("No rests found.");
		}
		return rests;
	}

	@Override
	@Transactional(readOnly = true)
	public List<RestVO> getRestsByRestIds(List<Integer> restIds) {
		return repository.findAllById(restIds);
	}


	public Set<DiscPtsVO> getDiscPtsByRestId(Integer restId){
		return getOneById(restId).getDiscPts();
	}
	
	public Set<OrderMasterVO> getOrderMastersByRestId(Integer restId){
		return getOneById(restId).getOrderMasters();
	}
	
	public Set<ReservationVO> getReservationsByRestId(Integer restId){
		return getOneById(restId).getReservations();
	}
	
	public Set<ApprovalVO> getApprovalsByRestId(Integer restId){
		return getOneById(restId).getApprovals();
	}
	
	public Set<FavRestVO> getFavRestsByRestId(Integer restId){
		return getOneById(restId).getFavRests();
	}
	
	public Set<DailyReservationVO> getDailyReservationsByRestId(Integer restId){
		return getOneById(restId).getDailyReservations();
	}
	
	public Set<EnvImgVO> getEnvImgsByRestId(Integer restId){
		return getOneById(restId).getEnvImgs();
	}
	
//	public Set<TableTypeVO> getTableTypesByRestId(Integer restId){
//		return getOneById(restId).getTableTypes();
//	}

	@Override
	@Transactional(readOnly = true)
	public List<String> getFormattedBusinessHours(Integer restId){
		Optional<RestVO> optional = repository.findById(restId);
		List<String> formattedBusinessHours = ConverterUtil.convertStrToHours(optional.get().getBusinessHours());
		return formattedBusinessHours;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Integer> getBusinessHours(Integer restId) {
		Optional<RestVO> optional = repository.findById(restId);
		return ConverterUtil.convertStrToTimeList(optional.get().getBusinessHours(),1);
	}
	
	public List<Integer[]> getBusinessHoursFor(Integer restId){
		
		Optional<RestVO> optional = repository.findById(restId);
		String businessHours = optional.get().getBusinessHours();
		
		char[] hours = businessHours.toCharArray();
		List<Integer[]> result = new ArrayList<>();
		
		Integer[] currentSegment = new Integer[24];
        Arrays.fill(currentSegment, 0);
        
        boolean inSegment = false;//如果不是1，則不營業
        
        for (int i = 0; i < hours.length; i++) {
            if (hours[i] == '1') {
                // 如果是營業時間，標記為 1
                currentSegment[i] = 1;
                inSegment = true;
            } else if (hours[i] == '0' && inSegment) {
                // 遇到非營業時間且在營業段中，保存當前段並重置
                result.add(currentSegment);
                currentSegment = new Integer[24];
                Arrays.fill(currentSegment, 0);
                inSegment = false;
            }
        }
        // 如果最後一段是營業時間段，將其加入結果
        if (inSegment) {
            result.add(currentSegment);
        }
		return result;
	}
	
	
	@Override
	public String getBusinessDays(Integer restId) {
		Optional<RestVO> optional = repository.findById(restId);
		String weeklyBusinessDays = optional.get().getWeeklyBizDays();
		return weeklyBusinessDays;
	}


	@Override
	public List<Integer> getRestIdsByOptionalFields(List<String> cities,
													List<String> districts,
													List<Integer> cuisineTypeIds) {
		// 2. 若傳進來的集合是空或 null，就設為 null 以代表不加入條件
		if (cities == null || cities.isEmpty()) {
			cities = null;
		}
		if (districts == null || districts.isEmpty()) {
			districts = null;
		}
		if (cuisineTypeIds == null || cuisineTypeIds.isEmpty()) {
			cuisineTypeIds = null;
		}

		// 3. 動態組裝 JPQL
		//    先寫個基本條件 "WHERE 1=1" 只是讓後續好用 AND 連接
		StringBuilder jpql = new StringBuilder("SELECT r.restId FROM RestVO r WHERE 1=1");

		if (cities != null) {
			jpql.append(" AND r.restCity IN :cities");
		}
		if (districts != null) {
			jpql.append(" AND r.restDist IN :districts");
		}
		if (cuisineTypeIds != null) {
			jpql.append(" AND r.cuisineType.cuisineTypeId IN :cuisineTypeIds");
		}

		// 4. 建立 TypedQuery
		TypedQuery<Integer> query = entityManager.createQuery(jpql.toString(), Integer.class);

		// 5. 設定參數
		if (cities != null) {
			query.setParameter("cities", cities);
		}
		if (districts != null) {
			query.setParameter("districts", districts);
		}
		if (cuisineTypeIds != null) {
			query.setParameter("cuisineTypeIds", cuisineTypeIds);
		}

		// 6. 執行查詢取得結果
		List<Integer> result = query.getResultList();
		return (result != null) ? result : Collections.emptyList();
	}
	
	public Set<OrderTableVO> getOrderTablesByRestId(Integer restId){
		return getOneById(restId).getOrderTables();
	}
	
	public RestVO getOneByEmail(String merchantEmail) {
        return repository.findByMerchantEmail(merchantEmail)
            .orElse(null);
    }
	
	public List<RestVO> getRandomRests(Integer count){
		return repository.findRandomRest(count);
	}

	
}