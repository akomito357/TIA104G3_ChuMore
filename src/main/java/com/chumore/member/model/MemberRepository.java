package com.chumore.member.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<MemberVO, Integer> {
    
    /**
     * 根據電子郵件查詢會員
     */
    Optional<MemberVO> findByMemberEmail(String memberEmail);
    
    /**
     * 根據手機號碼查詢會員
     */
    Optional<MemberVO> findByMemberPhoneNumber(String memberPhoneNumber);
    
    /**
     * 檢查電子郵件是否存在
     */
    boolean existsByMemberEmail(String memberEmail);
    
    /**
     * 檢查手機號碼是否存在
     */
    boolean existsByMemberPhoneNumber(String memberPhoneNumber);
}