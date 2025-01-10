package com.chumore.favrest.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.chumore.member.model.MemberVO;
import com.chumore.rest.model.RestVO;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "fav_rest")
public class FavRestVO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "fav_rest_id")
	private Integer favRestId;
	
	@ManyToOne
	@JoinColumn(name = "member_id")
	private MemberVO member;
	
	@ManyToOne
	@JoinColumn(name = "rest_id")
	@JsonBackReference("favRest-rest")
	private RestVO rest;
	
	@Column(name = "saved_datetime", columnDefinition = "DATETIME CURRENT TIMESTAMP")
	private LocalDateTime savedDatetime;
	
	public FavRestVO() {
	
	}

	public Integer getFavRestId() {
		return favRestId;
	}

	public void setFavRestId(Integer favRestId) {
		this.favRestId = favRestId;
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

	public LocalDateTime getSavedDatetime() {
		return savedDatetime;
	}

	public void setSavedDatetime(LocalDateTime savedDatetime) {
		this.savedDatetime = savedDatetime;
	}

	@Override
	public String toString() {
		return "FavRestVO [favRestId=" + favRestId + ", member=" + member + ", rest=" + rest + ", savedDatetime="
				+ savedDatetime + "]";
	}
	
	

}
