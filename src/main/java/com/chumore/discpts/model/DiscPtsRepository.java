package com.chumore.discpts.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DiscPtsRepository extends JpaRepository<DiscPtsVO, Integer> {

    // 查詢特定會員的所有折扣點數
    List<DiscPtsVO> findByMember_MemberId(Integer memberId);

    // 查詢某會員的即將到期的折扣點數
    List<DiscPtsVO> findByMember_MemberIdAndExpDateBefore(Integer memberId, LocalDate expDate);
}
