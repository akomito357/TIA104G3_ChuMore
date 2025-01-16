package com.chumore.discpts.dto;

public class DiscPtsAvailableDTO {
	
	private Integer memberId;
	private Integer restId;
	private Long availablePoints;
	
	
	public DiscPtsAvailableDTO() {

	}
	
	public DiscPtsAvailableDTO(Integer memberId, Integer restId, Long availablePoints) {
		super();
		this.memberId = memberId;
		this.restId = restId;
		this.availablePoints = availablePoints;
	}
	
	public Integer getMemberId() {
		return memberId;
	}
	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}
	public Integer getRestId() {
		return restId;
	}
	public void setRestId(Integer restId) {
		this.restId = restId;
	}
	public Long getAvailablePoints() {
		return availablePoints;
	}
	public void setAvailablePoints(Long availablePoints) {
		this.availablePoints = availablePoints;
	}
	
	

}
