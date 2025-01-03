package com.chumore.rest.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chumore.approval.model.ApprovalVO;
import com.chumore.dailyreservation.model.DailyReservationVO;
import com.chumore.discpts.model.DiscPtsVO;
import com.chumore.envimg.model.EnvImgVO;
import com.chumore.favrest.model.FavRestVO;
import com.chumore.ordermaster.model.OrderMasterVO;
import com.chumore.reservation.model.ReservationVO;
import com.chumore.rest.compositequery.RestCompositeQuery;
import com.chumore.tabletype.model.TableTypeVO;

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
	public void updateRest(RestVO rest) {
		repository.save(rest);
	}

	@Override
	public RestVO getOneById(Integer restId) {
		Optional<RestVO> optional = repository.findById(restId);
		return optional.orElse(null);
	}

	@Override
	public List<RestVO> getAll() {
		return repository.findAll();
	}

	@Override
	public List<RestVO> getAllCompos(Map<String, String[]> map) {
		return RestCompositeQuery.getAllC(map, sessionFactory.openSession());
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

}
