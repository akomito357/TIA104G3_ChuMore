package com.chumore.discpts.model;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.chumore.member.model.MemberVO;
import com.chumore.rest.model.RestVO;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "disc_pts")
public class DiscPtsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "disc_pts_id", nullable = false)
    private Integer discPtsId; // 流水號PK

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id", referencedColumnName = "member_id", nullable = false)
    @JsonBackReference("member-discPts")
    private MemberVO member; // 對應消費者ID (FK)

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rest_id", referencedColumnName = "rest_id", nullable = false)
    @JsonBackReference("discPts-rest")
    private RestVO rest; // 對應餐廳ID (FK)

    @Column(name = "disc_pts_qty", nullable = false)
    private Integer discPtsQty; // 折扣點數數量

    @Column(name = "exp_date", nullable = false)
    private LocalDate expDate; // 到期日期

    // 無參數建構子
    public DiscPtsVO() {}

    // 全參數建構子
    public DiscPtsVO(Integer discPtsId, MemberVO member, RestVO rest, Integer discPtsQty, LocalDate expDate) {
        this.discPtsId = discPtsId;
        this.member = member;
        this.rest = rest;
        this.discPtsQty = discPtsQty;
        this.expDate = expDate;
    }

    // Getter 和 Setter
    public Integer getDiscPtsId() {
        return discPtsId;
    }

    public void setDiscPtsId(Integer discPtsId) {
        this.discPtsId = discPtsId;
    }

    public MemberVO getMember() {
        return member;
    }

    public void setMember(MemberVO member) {
        this.member = member;
    }

    public RestVO getRest() {
        return rest;
    }

    public void setRest(RestVO rest) {
        this.rest = rest;
    }

    public Integer getDiscPtsQty() {
        return discPtsQty;
    }

    public void setDiscPtsQty(Integer discPtsQty) {
        this.discPtsQty = discPtsQty;
    }

    public LocalDate getExpDate() {
        return expDate;
    }

    public void setExpDate(LocalDate expDate) {
        this.expDate = expDate;
    }

    @Override
    public String toString() {
        return "DiscPtsVO{" +
                "discPtsId=" + discPtsId +
                ", member=" + (member != null ? member.getMemberId() : null) +
                ", rest=" + (rest != null ? rest.getRestId() : null) +
                ", discPtsQty=" + discPtsQty +
                ", expDate=" + expDate +
                '}';
    }
}