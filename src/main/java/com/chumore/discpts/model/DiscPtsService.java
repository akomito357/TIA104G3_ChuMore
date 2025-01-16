package com.chumore.discpts.model;


import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chumore.discpts.dto.DiscPtsAvailableDTO;
import com.chumore.discpts.dto.DiscPtsSummaryDTO;
import com.chumore.exception.DiscPtsAbsentException;
import com.chumore.exception.PointsInsufficientException;
import com.chumore.member.model.MemberService;
import com.chumore.ordermaster.model.OrderMasterVO;
import com.chumore.usepoints.model.UsePointsService;
import com.chumore.usepoints.model.UsePointsVO;

@Transactional
@Service
public class DiscPtsService {
    
    @Autowired
    private DiscPtsRepository discPtsRepository;
    
    @Autowired
	UsePointsService usePtSvc;
    
    @Autowired
    MemberService memberSvc;
    
    @Transactional(readOnly = true)
    public List<DiscPtsSummaryDTO> getPointsSummary(Integer memberId) {
        return discPtsRepository.findPointsSummaryByMember(memberId);
    }
    
    @Transactional(readOnly = true)
    public DiscPtsAvailableDTO getAvailablePointsByMemberAndRest(Integer memberId, Integer restId) {
//    	DiscPtsAvailableDTO discPtsAvail = discPtsRepository.findPointsByMemberAndRest(memberId, restId);
    	DiscPtsAvailableDTO discPtsAvail = discPtsRepository.findAvailablePointsByMemberAndRest(memberId, restId);
//    	DiscPtsVO discPts = new DiscPtsVO();
    	
    	if (discPtsAvail == null) {
    		throw new DiscPtsAbsentException(
    				"Can not found discPts where memberId = " + memberId + " and restId = " + restId);
    	}
    	
    	return discPtsAvail;
    }
    
    @Transactional(readOnly = true)
    public boolean checkPointsSufficiency(Integer memberId, Integer usePoints, HttpSession session) {
    	Integer restId = (Integer)session.getAttribute("restId");
		if (session.getAttribute("restId") == null) {
			restId = 2001;
		}
		
		DiscPtsAvailableDTO discPtsAvail = getAvailablePointsByMemberAndRest(memberId, restId);
		if (usePoints < 0) {
			throw new DiscPtsAbsentException("using points can not < 0!");
		} else if (usePoints > discPtsAvail.getAvailablePoints()) {
			throw new PointsInsufficientException("points insufficient! AvailablePoints is " + discPtsAvail.getAvailablePoints());
		}
		
		return true;
    }
    
    @Transactional(readOnly = true)
    public List<DiscPtsVO> findAvailablePointsListByMemberAndRest(Integer memberId, Integer restId){
    	List<DiscPtsVO> list = discPtsRepository.findAvailablePointsListByMemberAndRest(memberId, restId);
    	return list;
    }
    
    @Transactional(readOnly = true)
    public List<DiscPtsVO> findRecentPointsByMemberAndRest(Integer memberId, 
    		Integer restId, LocalDate expDate){
    	List<DiscPtsVO> list = discPtsRepository.findRecentPointsByMemberAndRest(memberId, restId, expDate);
    	return list;
    }
    
    public DiscPtsVO update(DiscPtsVO discPts) {
    	return discPtsRepository.save(discPts);
    }
    
    public DiscPtsVO add(DiscPtsVO discPts) {
    	return discPtsRepository.save(discPts);
    }
    
    public void deductPoints(Integer memberId, OrderMasterVO orderMaster, Integer pointUsed) {
    	List<DiscPtsVO> availPointList= findAvailablePointsListByMemberAndRest(
				memberId, orderMaster.getRestId());
		Integer remainPtsToCalc = pointUsed;
		
		for (DiscPtsVO discPts : availPointList) {
//			System.out.println("pts: " + discPts);
			Integer thisPtsQty = discPts.getDiscPtsQty();
			
			UsePointsVO usePoints = new UsePointsVO();
			usePoints.setDiscPts(discPts);
			usePoints.setOrderMaster(orderMaster);
			
			if (remainPtsToCalc - thisPtsQty >= 0) {
				discPts.setDiscPtsQty(0); // 這筆被扣完了
				usePoints.setUsedPointsQuantity(thisPtsQty);
				remainPtsToCalc = remainPtsToCalc - thisPtsQty;
				
			} else if (remainPtsToCalc - thisPtsQty < 0) {
				discPts.setDiscPtsQty(thisPtsQty - remainPtsToCalc);
				usePoints.setUsedPointsQuantity(remainPtsToCalc);
				remainPtsToCalc = 0;
			}
			
			update(discPts);
			if (usePoints.getUsedPointsQuantity() > 0) {				
				usePtSvc.addUsePoints(usePoints);
			}
//			System.out.println(discPts);
//			System.out.println(usePoints);
		}
    }
    
    public void gainPoints(Integer memberId, OrderMasterVO orderMaster, Integer pointUsed, Integer earnedPoints) {
//		點數依取得時間決定到期日：
//		上半年度(1/1-6/30)取得之點數，到期日為當年12/31
//		下半年度(7/1-12/31)取得之點數，到期日為次年6/30
    	
    	LocalDate today = LocalDate.now();
		int currentMonth = today.getMonth().getValue();
		int currentYear = today.getYear();
		LocalDate expectExpirDate = null;
		
		if (currentMonth <= 6) {
			expectExpirDate = LocalDate.of(currentYear, 12, 31);
		} else if (currentMonth > 6) {
			expectExpirDate = LocalDate.of(currentYear + 1, 6, 30);
		}
		
		List<DiscPtsVO> recentPoints = findRecentPointsByMemberAndRest(memberId, orderMaster.getRestId(), expectExpirDate);
		if (recentPoints.size() != 0) {
			DiscPtsVO firstRecentPoints = recentPoints.get(0);
			Integer firstRecentPointsQty = firstRecentPoints.getDiscPtsQty();
			firstRecentPoints.setDiscPtsQty(firstRecentPointsQty + earnedPoints);
			update(firstRecentPoints);
		} else {
			DiscPtsVO discPts = new DiscPtsVO();
			discPts.setMember(memberSvc.getOneMember(memberId).orElse(null));
			discPts.setRest(orderMaster.getRest());
			discPts.setDiscPtsQty(earnedPoints);
			discPts.setExpDate(expectExpirDate);
			add(discPts);
		}
    }
    
}