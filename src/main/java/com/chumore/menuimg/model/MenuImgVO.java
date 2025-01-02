package com.chumore.menuimg.model;

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
@Table(name = "menu_img")
public class MenuImgVO implements java.io.Serializable {
	@Id
	@Column(name = "menu_img_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer menuImgId;
	
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

	public MenuImgVO() {
	};

	public Integer getMenuImgId() {
		return menuImgId;
	}

	public void setMenuImgId(Integer menuImgId) {
		this.menuImgId = menuImgId;
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
