package com.chumore.auth.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * 整合 Spring Security 的認證用戶資訊類別
 * 實作 UserDetails 介面以支援 Spring Security 的認證機制
 */
public class AuthenticatedUser implements UserDetails {
    
    private static final long serialVersionUID = 1L;
    
    public static final String TYPE_MEMBER = "MEMBER";
    public static final String TYPE_RESTAURANT = "RESTAURANT";
    
    private Integer userId;
    private Integer memberId;
    private Integer restId;
    private String email;
    private String password;
    private String userType;
    private String name;
    private Integer approvalStatus;
    private Integer businessStatus;
    private Collection<? extends GrantedAuthority> authorities;

    public AuthenticatedUser(Integer userId, Integer memberId, Integer restId, 
            String email, String password, String userType, String name,
            Integer approvalStatus, Integer businessStatus) {
        this.userId = userId;
        this.memberId = memberId;
        this.restId = restId;
        this.email = email;
        this.password = password;
        this.userType = userType;
        this.name = name;
        this.approvalStatus = approvalStatus;
        this.businessStatus = businessStatus;
        
        this.authorities = Collections.singleton(new SimpleGrantedAuthority(
            TYPE_MEMBER.equals(userType) ? "ROLE_MEMBER" : "ROLE_RESTAURANT"
        ));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        if (TYPE_RESTAURANT.equals(userType)) {
            // 餐廳會員允許任何 approvalStatus 狀態登入
            return true;
        }
        // 一般會員維持原有邏輯
        return approvalStatus != null && approvalStatus == 1;
    }
    
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public Integer getRestId() {
        return restId;
    }

    public void setRestId(Integer restId) {
        this.restId = restId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(Integer approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public Integer getBusinessStatus() {
        return businessStatus;
    }

    public void setBusinessStatus(Integer businessStatus) {
        this.businessStatus = businessStatus;
    }

    public static class Builder {
        private Integer userId;
        private Integer memberId;
        private Integer restId;
        private String email;
        private String password;
        private String userType;
        private String name;
        private Integer approvalStatus;
        private Integer businessStatus;

        public Builder userId(Integer userId) {
            this.userId = userId;
            return this;
        }
        
        public Builder memberId(Integer memberId) {
            this.memberId = memberId;
            return this;
        }

        public Builder restId(Integer restId) {
            this.restId = restId;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder userType(String userType) {
            this.userType = userType;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder approvalStatus(Integer approvalStatus) {
            this.approvalStatus = approvalStatus;
            return this;
        }

        public Builder businessStatus(Integer businessStatus) {
            this.businessStatus = businessStatus;
            return this;
        }

        public AuthenticatedUser build() {
            return new AuthenticatedUser(userId, memberId, restId, 
                    email, password, userType, name,
                    approvalStatus, businessStatus);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "AuthenticatedUser{" +
                "userId=" + userId +
                ", memberId=" + memberId +
                ", restId=" + restId +
                ", email='" + email + '\'' +
                ", userType='" + userType + '\'' +
                ", name='" + name + '\'' +
                ", approvalStatus=" + approvalStatus +
                ", businessStatus=" + businessStatus +
                '}';
    }
}