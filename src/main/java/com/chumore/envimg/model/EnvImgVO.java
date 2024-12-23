package com.chumore.envimg.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "env_img")
public class EnvImgVO implements java.io.Serializable {
	private Integer envImgId;
	private Integer restId;
	private byte[] image;
	private Timestamp uploadDatetime;
	
	
//  @ManyToOne(fetch=FetchType.EAGER)
//  @JoinColumn(name="rest_Id",referencedColumnName="rest_id")
//  private Rest rest;
	public EnvImgVO() {
	};
	
		
	@Id
	@Column(name = "env_img_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	public Integer getEnvImgId() {
		return envImgId;
	}

	public void setEnvImgId(Integer envImgId) {
		this.envImgId = envImgId;
	}

	@Column(name = "rest_id")
	public Integer getRestId() {
		return restId;
	}

	public void setRestId(Integer restId) {
		this.restId = restId;
	}

	@Column(name = "image")
	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	@Column(name = "upload_datetime")
	public Timestamp getUploadDatetime() {
		return uploadDatetime;
	}

	public void setUploadDatetime(Timestamp uploadDatetime) {
		this.uploadDatetime = uploadDatetime;
	}

}
