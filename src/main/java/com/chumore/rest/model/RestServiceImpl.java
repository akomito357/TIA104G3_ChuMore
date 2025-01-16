package com.chumore.rest.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
	SessionFactory sessionFactory;

	@Override
	public void addRest(RestVO rest) {
		repository.save(rest);
	}

	 @Override
	    @Transactional
	    public void updateRest(RestVO rest) {
	        try {
	            // 獲取原有資料
	            RestVO existingRest = repository.findById(rest.getRestId())
	                .orElseThrow(() -> new RuntimeException("餐廳不存在"));

	            // 設置不可修改的欄位
	            rest.setMerchantPassword(existingRest.getMerchantPassword());
	            rest.setMerchantIdNumber(existingRest.getMerchantIdNumber());
	            rest.setRegisterDatetime(existingRest.getRegisterDatetime());
	            rest.setCreatedDatetime(existingRest.getCreatedDatetime());
	            rest.setWeeklyBizDays(existingRest.getWeeklyBizDays());
	            rest.setBusinessHours(existingRest.getBusinessHours());
	            rest.setOrderTableCount(existingRest.getOrderTableCount());
	            rest.setRestStars(existingRest.getRestStars());
	            rest.setRestReviewers(existingRest.getRestReviewers());
	            rest.setApprovalStatus(existingRest.getApprovalStatus());

	            // 設置更新時間
	            rest.setUpdatedDatetime(LocalDateTime.now());

	            // 保存更新
	            repository.saveAndFlush(rest);

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

	@Override
	public List<RestVO> getAllCompos(Map<String, String[]> map) {
		List<RestVO> rests = RestCompositeQuery.getAllC(map, sessionFactory.openSession());
		if (rests.isEmpty()) {
			throw new ResourceNotFoundException("No match rests found.");
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
	
	public Set<OrderTableVO> getOrderTablesByRestId(Integer restId){
		return getOneById(restId).getOrderTables();
	}
	
	@Override
	public RestVO getOneByEmail(String email) {
	    return repository.findByMerchantEmail(email).orElse(null);
	}

}
