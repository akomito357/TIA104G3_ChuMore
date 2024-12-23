package com.chumore.reservation.model;


import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

@Entity
@Table(name="reservation")
public class ReservationVO implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="reservation_id",updatable=false,nullable=false)
    private Integer reservationId;

    @Column(name="guest_count",nullable=false)
    private Integer guestCount;

    @Column(name="reservation_status",columnDefinition = "TINYINT",nullable=false)
    private Integer reservationStatus;

    @Column(name="phone_number",nullable=false)
    private String phoneNumber;

    @Column(name="reservation_date",nullable=false)
    private Date reservationDate;

    @Column(name="reservation_time",nullable=false)
    private Time reservationTime;

    @Column(name="created_datetime",insertable=false,updatable=false,nullable=false,columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdDatetime;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "rest_id",referencedColumnName = "rest_id")
    private RestVO restVO;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "member_id",referencedColumnName = "member_id")
    private MemberVO memberVO;


    public Reservation() {

    }

    public RestVO getRestVO() {
        return restVO;
    }

    public void setRestVO(RestVO restVO) {
        this.restVO = restVO;
    }

    public MemberVO getMemberVO() {
        return memberVO;
    }

    public void setMemberVO(MemberVO memberVO) {
        this.memberVO = memberVO;
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

    public Date getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(Date reservationDate) {
        this.reservationDate = reservationDate;
    }

    public Time getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(Time reservationTime) {
        this.reservationTime = reservationTime;
    }

    public Timestamp getCreatedDatetime() {
        return createdDatetime;
    }

    public void setCreatedDatetime(Timestamp createdDatetime) {
        this.createdDatetime = createdDatetime;
    }

    @Override
    public String toString() {
        return "Reservation[" +"reservation_id="+ reservationId +", guest_count="+ guestCount +", reservation_status="+ reservationStatus +", phone_number="+ phoneNumber +", reservation_date="+ reservationDate +", reservation_time="+ reservationTime +", created_datetime="+ createdDatetime +"]";
    }
}