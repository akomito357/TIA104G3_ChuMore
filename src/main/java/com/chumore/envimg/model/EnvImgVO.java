package com.chumore.envimg.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.chumore.rest.model.RestVO;

@Entity
@Table(name = "env_img")
public class EnvImgVO implements java.io.Serializable {
	@Id
	@Column(name = "env_img_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer envImgId;
	
	@Column(name = "image", columnDefinition = "MEDIUMBLOB")
	private byte[] image;
	
	@Column(name = "upload_datetime", columnDefinition = "DATETIME CURRENT TIMESTAMP", insertable = false)
	private Timestamp uploadDatetime;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "rest_Id", referencedColumnName = "rest_id")
	private RestVO rest;

	public RestVO getRest() {
		return rest;
	}

	public void setRest(RestVO rest) {
		this.rest = rest;
	}

	public EnvImgVO() {
	};

	public Integer getEnvImgId() {
		return envImgId;
	}

	public void setEnvImgId(Integer envImgId) {
		this.envImgId = envImgId;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	
	public Timestamp getUploadDatetime() {
		return uploadDatetime;
	}

	public void setUploadDatetime(Timestamp uploadDatetime) {
		this.uploadDatetime = uploadDatetime;
	}

}
