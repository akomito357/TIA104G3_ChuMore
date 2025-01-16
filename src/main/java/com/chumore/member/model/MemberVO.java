package com.chumore.member.model;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.*;

import com.chumore.ordermaster.model.OrderMasterVO;
import com.chumore.reservation.model.ReservationVO;
import com.chumore.review.model.ReviewVO;
import com.fasterxml.jackson.annotation.JsonManagedReference;

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
    @Column(name = "member_password", nullable = false, length = 60)
    private String memberPassword;

    @Pattern(regexp = "^09\\d{8}$", message = "請輸入有效的手機號碼")
    @Column(name = "member_phone_number")
    private String memberPhoneNumber;

    @NotNull(message = "性別不能為空")
    @Column(name = "member_gender", columnDefinition = "TINYINT")
    private Integer memberGender;

    @Past(message = "生日必須是過去的日期")
    @Column(name = "member_birthdate")
    private Date memberBirthdate;

    @Size(max = 255, message = "地址長度不能超過255個字符")
    @Column(name = "member_address")
    private String memberAddress;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    @JsonManagedReference("reservation-member")
    private Set<ReservationVO> reservations;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    @JsonManagedReference("orderMaster-member")
    private Set<OrderMasterVO> orderMasters;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    @JsonManagedReference("member-review")
    private Set<ReviewVO> reviews;

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

    public Integer getMemberGender() {
        return memberGender;
    }

    public void setMemberGender(Integer memberGender) {
        this.memberGender = memberGender;
    }

    public Date getMemberBirthdate() {
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

    public Set<ReservationVO> getReservations() {
        return reservations;
    }

    public void setReservations(Set<ReservationVO> reservations) {
        this.reservations = reservations;
    }

    public Set<OrderMasterVO> getOrderMasters() {
        return orderMasters;
    }

    public void setOrderMasters(Set<OrderMasterVO> orderMasters) {
        this.orderMasters = orderMasters;
    }

    public Set<ReviewVO> getReviews() {
        return reviews;
    }

    public void setReviews(Set<ReviewVO> reviews) {
        this.reviews = reviews;
    }
}
