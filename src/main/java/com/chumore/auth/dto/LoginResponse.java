package com.chumore.auth.dto;

import com.chumore.member.model.MemberVO;
import com.chumore.rest.model.RestVO;

public class LoginResponse {
    private String userType;
    private MemberVO memberVO;
    private RestVO restVO;

    public LoginResponse() {
    }

    public LoginResponse(String userType, MemberVO memberVO, RestVO restVO) {
        this.userType = userType;
        this.memberVO = memberVO;
        this.restVO = restVO;
    }

    // Getter 和 Setter 方法
    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public MemberVO getMemberVO() {
        return memberVO;
    }

    public void setMemberVO(MemberVO memberVO) {
        this.memberVO = memberVO;
    }

    public RestVO getRestVO() {
        return restVO;
    }

    public void setRestVO(RestVO restVO) {
        this.restVO = restVO;
    }
}