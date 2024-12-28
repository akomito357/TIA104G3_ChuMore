package com.chumore.member.model;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface MemberService {
    MemberVO addMember(String memberName, String memberEmail, String memberPassword,
                      String memberPhoneNumber, Integer memberGender, Date memberBirthdate,
                      String memberAddress);
    
    MemberVO updateMember(Integer memberId, String memberName, String memberEmail,
                         String memberPassword, String memberPhoneNumber, Integer memberGender,
                         Date memberBirthdate, String memberAddress);
    
    void deleteMember(Integer memberId);
    
    Optional<MemberVO> getOneMember(Integer memberId);
    
    List<MemberVO> getAll();
    
    Optional<MemberVO> findMemberByEmail(String memberEmail);
    
    Optional<MemberVO> findMemberByPhoneNumber(String memberPhoneNumber);
    
    boolean verifyMemberEmail(String memberEmail);
    
    boolean verifyMemberPhoneNumber(String memberPhoneNumber);
}