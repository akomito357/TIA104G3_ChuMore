package com.chumore.member.model;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@Transactional
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // 密碼加密器

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

    /**
     * 檢查會員資料是否重複
     */
    private void checkDuplicate(String memberEmail, String memberPhoneNumber, Integer memberId) {
        memberRepository.findByMemberEmail(memberEmail)
            .ifPresent(member -> {
                if (memberId == null || !member.getMemberId().equals(memberId)) {
                    throw new IllegalArgumentException("此電子郵件已被使用");
                }
            });

        memberRepository.findByMemberPhoneNumber(memberPhoneNumber)
            .ifPresent(member -> {
                if (memberId == null || !member.getMemberId().equals(memberId)) {
                    throw new IllegalArgumentException("此手機號碼已被使用");
                }
            });
    }

    @Override
    public MemberVO addMember(String memberName, String memberEmail, String memberPassword,
                              String memberPhoneNumber, Integer memberGender, Date memberBirthdate,
                              String memberAddress) {
        validateMemberData(memberName, memberEmail, memberPassword, memberPhoneNumber);
        checkDuplicate(memberEmail, memberPhoneNumber, null);

        MemberVO memberVO = new MemberVO();
        memberVO.setMemberName(memberName);
        memberVO.setMemberEmail(memberEmail);
        memberVO.setMemberPassword(passwordEncoder.encode(memberPassword)); // 加密密碼
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

        checkDuplicate(memberEmail, memberPhoneNumber, memberId);

        existingMember.setMemberName(memberName);
        existingMember.setMemberEmail(memberEmail);
        existingMember.setMemberPassword(passwordEncoder.encode(memberPassword));
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

    @Override
    @Transactional(readOnly = true)
    public MemberVO getMemberByEmail(String email) {
        return memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("找不到此會員：" + email));
    }
}
