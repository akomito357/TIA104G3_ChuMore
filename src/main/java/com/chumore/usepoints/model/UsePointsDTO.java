package com.chumore.usepoints.model;

import java.time.LocalDateTime;

public class UsePointsDTO {
    private String restName;
    private Integer pointsUsed;
    private LocalDateTime usageDate;
    
    // Constructor
    public UsePointsDTO(String restName, Integer pointsUsed, LocalDateTime usageDate) {
        this.restName = restName;
        this.pointsUsed = pointsUsed;
        this.usageDate = usageDate;
    }
    
    // Getters and Setters
    public String getRestName() {
        return restName;
    }
    
    public void setRestName(String restName) {
        this.restName = restName;
    }
    
    public Integer getPointsUsed() {
        return pointsUsed;
    }
    
    public void setPointsUsed(Integer pointsUsed) {
        this.pointsUsed = pointsUsed;
    }
    
    public LocalDateTime getUsageDate() {
        return usageDate;
    }
    
    public void setUsageDate(LocalDateTime usageDate) {
        this.usageDate = usageDate;
    }
}