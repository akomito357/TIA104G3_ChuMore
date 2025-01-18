package com.chumore.reservation.model;


import com.chumore.member.model.MemberVO;
import com.chumore.rest.model.RestVO;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name="reservation")
public class ReservationVO implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="reservation_id",updatable=false)
    private Integer reservationId;

    @Column(name="guest_count")
    private Integer guestCount;

    @Column(name="reservation_status",columnDefinition = "TINYINT")
    private Integer reservationStatus;

    @Column(name="phone_number")
    private String phoneNumber;

    @JsonFormat(pattern="yyyy-MM-dd")
    @Column(name="reservation_date")
    private LocalDate reservationDate;

    @JsonFormat(pattern="HH:mm[:ss]")
    @Column(name="reservation_time")
    private LocalTime reservationTime;

    @Column(name="created_datetime",insertable=false,updatable=false,columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdDatetime;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "rest_id", referencedColumnName = "rest_id")
    @JsonBackReference("reservation-rest")
    private RestVO rest;


    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "member_id", referencedColumnName = "member_id")
    @JsonBackReference("reservation-member")
    private MemberVO member;


    public ReservationVO() {

    }

    // Getters and Setters

    public String getMemberName(){
        return member.getMemberName();
    }

    public Integer getMemberGender(){
        return member.getMemberGender();
    }

    public String getRestName(){
        return rest.getRestName();
    }


    public RestVO getRest() {
        return rest;
    }

    public void setRest(RestVO rest) {
        this.rest = rest;
    }

    public MemberVO getMember() {
        return member;
    }

    public void setMember(MemberVO member) {
        this.member = member;
    }


    public Integer getReservationId() {
        return reservationId;
    }

    public void setReservationId(Integer reservationId) {
        this.reservationId = reservationId;
    }

    public Integer getGuestCount() {
        return guestCount;
    }

    public void setGuestCount(Integer guestCount) {
        this.guestCount = guestCount;
    }

    public Integer getReservationStatus() {
        return reservationStatus;
    }

    public void setReservationStatus(Integer reservationStatus) {
        this.reservationStatus = reservationStatus;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
    }

    public LocalTime getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(LocalTime reservationTime) {
        this.reservationTime = reservationTime;
    }

    public LocalDateTime getCreatedDatetime() {
        return createdDatetime;
    }

    public void setCreatedDatetime(LocalDateTime createdDatetime) {
        this.createdDatetime = createdDatetime;
    }

    @Override
    public String toString() {
       return "Reservation[" +"reservation_id="+ reservationId +", rest_name="+rest.getRestName()+", member_name="+member.getMemberName()+
               ", guest_count="+ guestCount +", reservation_status="+ reservationStatus +", phone_number="+ phoneNumber +
               ", reservation_date="+ reservationDate +", reservation_time="+ reservationTime +", created_datetime="+ createdDatetime +"]";
    }
}