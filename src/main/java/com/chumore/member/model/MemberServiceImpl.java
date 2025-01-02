package com.chumore.member.model;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MemberServiceImpl implements MemberService {
    
    @Autowired
    private MemberRepository memberRepository;
    
    /**
     * 驗證會員資料
     */
    private void validateMemberData(String memberName, String memberEmail, 
                                  String memberPassword, String memberPhoneNumber) {
        if (memberName == null || memberName.trim().isEmpty()) {
            throw new IllegalArgumentException("會員名稱不能為空");
        }
        
        if (memberEmail == null || !memberEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("電子郵件格式不正確");
        }
        
        if (memberPassword == null || memberPassword.length() < 6) {
            throw new IllegalArgumentException("密碼長度不能少於6個字符");
        }
        
        if (memberPhoneNumber != null && !memberPhoneNumber.matches("^09\\d{8}$")) {
            throw new IllegalArgumentException("手機號碼格式不正確");
        }
    }
    
    @Override
    public MemberVO addMember(String memberName, String memberEmail, String memberPassword,
                             String memberPhoneNumber, Integer memberGender, Date memberBirthdate,
                             String memberAddress) {
        
        validateMemberData(memberName, memberEmail, memberPassword, memberPhoneNumber);
        
        if (memberRepository.findByMemberEmail(memberEmail).isPresent()) {
            throw new IllegalArgumentException("此電子郵件已被註冊");
        }

        if (memberRepository.findByMemberPhoneNumber(memberPhoneNumber).isPresent()) {
            throw new IllegalArgumentException("此手機號碼已被註冊");
        }

        MemberVO memberVO = new MemberVO();
        memberVO.setMemberName(memberName);
        memberVO.setMemberEmail(memberEmail);
        memberVO.setMemberPassword(memberPassword);
        memberVO.setMemberPhoneNumber(memberPhoneNumber);
        memberVO.setMemberGender(memberGender);
        memberVO.setMemberBirthdate(memberBirthdate);
        memberVO.setMemberAddress(memberAddress);

        return memberRepository.save(memberVO);
    }

    @Override
    public MemberVO updateMember(Integer memberId, String memberName, String memberEmail,
                                String memberPassword, String memberPhoneNumber, Integer memberGender,
                                Date memberBirthdate, String memberAddress) {
        
        validateMemberData(memberName, memberEmail, memberPassword, memberPhoneNumber);
        
        MemberVO existingMember = memberRepository.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("找不到此會員"));

        memberRepository.findByMemberEmail(memberEmail)
            .ifPresent(member -> {
                if (!member.getMemberId().equals(memberId)) {
                    throw new IllegalArgumentException("此電子郵件已被其他會員使用");
                }
            });

        memberRepository.findByMemberPhoneNumber(memberPhoneNumber)
            .ifPresent(member -> {
                if (!member.getMemberId().equals(memberId)) {
                    throw new IllegalArgumentException("此手機號碼已被其他會員使用");
                }
            });

        existingMember.setMemberName(memberName);
        existingMember.setMemberEmail(memberEmail);
        existingMember.setMemberPassword(memberPassword);
        existingMember.setMemberPhoneNumber(memberPhoneNumber);
        existingMember.setMemberGender(memberGender);
        existingMember.setMemberBirthdate(memberBirthdate);
        existingMember.setMemberAddress(memberAddress);

        return memberRepository.save(existingMember);
    }

    @Override
    public void deleteMember(Integer memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new IllegalArgumentException("找不到此會員");
        }
        memberRepository.deleteById(memberId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MemberVO> getOneMember(Integer memberId) {
        return memberRepository.findById(memberId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberVO> getAll() {
        return memberRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MemberVO> findMemberByEmail(String memberEmail) {
        return memberRepository.findByMemberEmail(memberEmail);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MemberVO> findMemberByPhoneNumber(String memberPhoneNumber) {
        return memberRepository.findByMemberPhoneNumber(memberPhoneNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verifyMemberEmail(String memberEmail) {
        return !memberRepository.existsByMemberEmail(memberEmail);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verifyMemberPhoneNumber(String memberPhoneNumber) {
        return !memberRepository.existsByMemberPhoneNumber(memberPhoneNumber);
    }
}