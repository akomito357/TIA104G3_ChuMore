package com.chumore.auth.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import java.sql.Date;
import java.time.LocalDate;

public class RegisterRequest {
    private String memberName;
    private String memberEmail;
    private String memberPassword;
    private String memberPhoneNumber;
    private Integer memberGender;
    private Date memberBirthdate;
    private String memberAddress;

    // 建構子
    public RegisterRequest() {
    }

    public RegisterRequest(String memberName, String memberEmail, String memberPassword, 
                         String memberPhoneNumber, Integer memberGender, 
                         Date memberBirthdate, String memberAddress) {
        this.memberName = memberName;
        this.memberEmail = memberEmail;
        this.memberPassword = memberPassword;
        this.memberPhoneNumber = memberPhoneNumber;
        this.memberGender = memberGender;
        this.memberBirthdate = memberBirthdate;
        this.memberAddress = memberAddress;
    }

    // Getter 和 Setter
    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMemberEmail() {
        return memberEmail;
    }

    public void setMemberEmail(String memberEmail) {
        this.memberEmail = memberEmail;
    }

    public String getMemberPassword() {
        return memberPassword;
    }

    public void setMemberPassword(String memberPassword) {
        this.memberPassword = memberPassword;
    }

    public String getMemberPhoneNumber() {
        return memberPhoneNumber;
    }

    public void setMemberPhoneNumber(String memberPhoneNumber) {
        this.memberPhoneNumber = memberPhoneNumber;
    }

    public Integer getMemberGender() {
        return memberGender;
    }

    public void setMemberGender(Integer memberGender) {
        this.memberGender = memberGender;
    }

    public Date getMemberBirthdate() {
        return memberBirthdate;
    }

    public void setMemberBirthdate(Date memberBirthdate) {
        this.memberBirthdate = memberBirthdate;
    }

    public String getMemberAddress() {
        return memberAddress;
    }

    public void setMemberAddress(String memberAddress) {
        this.memberAddress = memberAddress;
    }

    // 覆寫 toString 方法
    @Override
    public String toString() {
        return "RegisterRequest{" +
                "memberName='" + memberName + '\'' +
                ", memberEmail='" + memberEmail + '\'' +
                ", memberPassword='[PROTECTED]'" +
                ", memberPhoneNumber='" + memberPhoneNumber + '\'' +
                ", memberGender=" + memberGender +
                ", memberBirthdate=" + memberBirthdate +
                ", memberAddress='" + memberAddress + '\'' +
                '}';
    }

    // 覆寫 equals 方法
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegisterRequest that = (RegisterRequest) o;
        return memberName.equals(that.memberName) &&
                memberEmail.equals(that.memberEmail) &&
                memberPassword.equals(that.memberPassword) &&
                memberPhoneNumber.equals(that.memberPhoneNumber) &&
                memberGender.equals(that.memberGender) &&
                memberBirthdate.equals(that.memberBirthdate) &&
                memberAddress.equals(that.memberAddress);
    }

    // 覆寫 hashCode 方法
    @Override
    public int hashCode() {
        int result = memberName != null ? memberName.hashCode() : 0;
        result = 31 * result + (memberEmail != null ? memberEmail.hashCode() : 0);
        result = 31 * result + (memberPassword != null ? memberPassword.hashCode() : 0);
        result = 31 * result + (memberPhoneNumber != null ? memberPhoneNumber.hashCode() : 0);
        result = 31 * result + (memberGender != null ? memberGender.hashCode() : 0);
        result = 31 * result + (memberBirthdate != null ? memberBirthdate.hashCode() : 0);
        result = 31 * result + (memberAddress != null ? memberAddress.hashCode() : 0);
        return result;
    }
}