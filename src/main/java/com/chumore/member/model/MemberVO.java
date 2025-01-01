package com.chumore.member.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;

@Entity
@Table(name = "member")
public class MemberVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Integer memberId;
    
    @NotBlank(message = "姓名不能為空")
    @Column(name = "member_name", nullable = false)
    private String memberName;
    
    @NotBlank(message = "電子郵件不能為空")
    @Email(message = "請輸入有效的電子郵件地址")
    @Column(name = "member_email", unique = true, nullable = false)
    private String memberEmail;
    
    @NotBlank(message = "密碼不能為空")
    @Size(min = 6, message = "密碼長度至少為6個字符")
    @Column(name = "member_password", nullable = false)
    private String memberPassword;
    
    @Pattern(regexp = "^09\\d{8}$", message = "請輸入有效的手機號碼")
    @Column(name = "member_phone_number")
    private String memberPhoneNumber;
    
    @NotNull(message = "性別不能為空")
    @Column(name = "member_gender",columnDefinition="TINYINT")
    private Integer memberGender;
    
    @Past(message = "生日必須是過去的日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "member_birthdate")
    private Date memberBirthdate;
    
    @Column(name = "member_address")
    private String memberAddress;

    // Getters and Setters
    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

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

    public @NotNull(message = "性別不能為空") Integer getMemberGender() {
        return memberGender;
    }

    public void setMemberGender(Integer memberGender2) {
        this.memberGender = memberGender2;
    }

    public @Past(message = "生日必須是過去的日期") Date getMemberBirthdate() {
        return memberBirthdate;
    }

    public void setMemberBirthdate(Date memberBirthdate2) {
        this.memberBirthdate = memberBirthdate2;
    }

    public String getMemberAddress() {
        return memberAddress;
    }

    public void setMemberAddress(String memberAddress) {
        this.memberAddress = memberAddress;
    }
}