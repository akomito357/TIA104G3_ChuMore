package com.chumore.discpts.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DiscPtsService {

    @Autowired
    private DiscPtsRepository discPtsRepository;

    /**
     * 獲取會員的所有有效折扣點數
     *
     * @param memberId 會員ID
     * @return 有效折扣點數列表
     */
    public List<DiscPtsVO> getAllPointsByMember(Integer memberId) {
        return discPtsRepository.findByMember_MemberId(memberId);
    }

    /**
     * 獲取會員即將到期的折扣點數
     *
     * @param memberId 會員ID
     * @param expDate  到期日期
     * @return 即將到期的折扣點數列表
     */
    public List<DiscPtsVO> getExpiringPointsByMember(Integer memberId, LocalDate expDate) {
        return discPtsRepository.findByMember_MemberIdAndExpDateBefore(memberId, expDate);
    }

    /**
     * 計算會員的總點數
     *
     * @param memberId 會員ID
     * @return 總點數數量
     */
    public Integer calculateTotalPoints(Integer memberId) {
        List<DiscPtsVO> points = discPtsRepository.findByMember_MemberId(memberId);
        return points.stream()
                     .mapToInt(DiscPtsVO::getDiscPtsQty)
                     .sum();
    }

    /**
     * 計算會員即將到期的總點數
     *
     * @param memberId 會員ID
     * @param expDate  到期日期
     * @return 即將到期的總點數數量
     */
    public Integer calculateExpiringPoints(Integer memberId, LocalDate expDate) {
        List<DiscPtsVO> expiringPoints = discPtsRepository.findByMember_MemberIdAndExpDateBefore(memberId, expDate);
        return expiringPoints.stream()
                             .mapToInt(DiscPtsVO::getDiscPtsQty)
                             .sum();
    }
}
