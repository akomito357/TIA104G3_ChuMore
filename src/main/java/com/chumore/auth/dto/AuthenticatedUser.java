package com.chumore.auth.dto;

/**
 * 認證用戶資訊的資料傳輸物件
 */
public class AuthenticatedUser {
    private Integer userId;
    private String email;
    private String userType;    // "MEMBER" 或 "RESTAURANT"
    private String name;
    private Integer approvalStatus;
    private Integer businessStatus;

    // 建構函數
    public AuthenticatedUser() {
    }

    public AuthenticatedUser(Integer userId, String email, String userType, String name, 
            Integer approvalStatus, Integer businessStatus) {
        this.userId = userId;
        this.email = email;
        this.userType = userType;
        this.name = name;
        this.approvalStatus = approvalStatus;
        this.businessStatus = businessStatus;
    }

    // Getters
    public Integer getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getUserType() {
        return userType;
    }

    public String getName() {
        return name;
    }

    public Integer getApprovalStatus() {
        return approvalStatus;
    }

    public Integer getBusinessStatus() {
        return businessStatus;
    }

    // Setters
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setApprovalStatus(Integer approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public void setBusinessStatus(Integer businessStatus) {
        this.businessStatus = businessStatus;
    }

    // Builder 模式實作
    public static class Builder {
        private Integer userId;
        private String email;
        private String userType;
        private String name;
        private Integer approvalStatus;
        private Integer businessStatus;

        public Builder userId(Integer userId) {
            this.userId = userId;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
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
            return new AuthenticatedUser(userId, email, userType, name, 
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
                ", email='" + email + '\'' +
                ", userType='" + userType + '\'' +
                ", name='" + name + '\'' +
                ", approvalStatus=" + approvalStatus +
                ", businessStatus=" + businessStatus +
                '}';
    }
}