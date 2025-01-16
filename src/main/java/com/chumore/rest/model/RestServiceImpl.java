package com.chumore.rest.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import com.chumore.event.RestChangedEvent;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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

@Service("restService")
public class RestServiceImpl implements RestService{
	
	@Autowired
	RestRepository repository;


	@Autowired
	private ApplicationEventPublisher publisher;

	@Override
	public void addRest(RestVO rest) {
		repository.save(rest);
		publisher.publishEvent(new RestChangedEvent(this,rest, "ADD"));
	}

	@Override
	@Transactional
	public void updateRest(RestVO rest) {
	        try {
	            // 獲取原有資料
	            RestVO existingRest = repository.findById(rest.getRestId())
	                .orElseThrow(() -> new RuntimeException("餐廳不存在"));

	            // 保存更新
	            repository.saveAndFlush(rest);

				// 發布更新索引事件
				publisher.publishEvent(new RestChangedEvent(this,rest, "UPDATE"));

	        } catch (Exception e) {
	            throw new RuntimeException("更新餐廳資料失敗: " + e.getMessage());
	        }
	    }

	@Override
	public RestVO getOneById(Integer restId) {
		Optional<RestVO> optional = repository.findById(restId);
		RestVO rest = optional.orElse(null);
		if (rest == null) {
			throw new ResourceNotFoundException("Rest with id = " + restId + "is not found.");
		}
		return rest;
	}

	@Override
	public List<RestVO> getAll() {
		List<RestVO> rests = repository.findAll();
		if (rests.isEmpty()) {
			throw new ResourceNotFoundException("No rests found.");
		}
		return rests;
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
	public List<String> getFormattedBusinessHours(Integer restId){
		Optional<RestVO> optional = repository.findById(restId);
		List<String> formattedBusinessHours = ConverterUtil.convertStrToHours(optional.get().getBusinessHours());
		return formattedBusinessHours;
	}

	@Override
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
	public List<Integer> getRestIdsByOptionalFields(String city, String district, Integer cuisineTypeId) {
		return repository.findRestIdsByOptionalFields(city, district, cuisineTypeId);
	}
	
	public Set<OrderTableVO> getOrderTablesByRestId(Integer restId){
		return getOneById(restId).getOrderTables();
	}

	
}