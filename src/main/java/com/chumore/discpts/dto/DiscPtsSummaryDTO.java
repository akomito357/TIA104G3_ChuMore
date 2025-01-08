package com.chumore.discpts.dto;

import java.time.LocalDate;

public class DiscPtsSummaryDTO {
    private String restName;     // 餐廳名稱
    private Long totalPoints;    // 總點數
    private Long expiringPoints; // 即將到期點數
    private LocalDate nearestExpDate; // 最近到期日

    public DiscPtsSummaryDTO() {
    }

    public DiscPtsSummaryDTO(String restName, Long totalPoints, Long expiringPoints, LocalDate nearestExpDate) {
        this.restName = restName;
        this.totalPoints = totalPoints;
        this.expiringPoints = expiringPoints;
        this.nearestExpDate = nearestExpDate;
    }

    // getters and setters
    public String getRestName() {
        return restName;
    }

    public void setRestName(String restName) {
        this.restName = restName;
    }

    public Long getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(Long totalPoints) {
        this.totalPoints = totalPoints;
    }

    public Long getExpiringPoints() {
        return expiringPoints;
    }

    public void setExpiringPoints(Long expiringPoints) {
        this.expiringPoints = expiringPoints;
    }

    public LocalDate getNearestExpDate() {
        return nearestExpDate;
    }

    public void setNearestExpDate(LocalDate nearestExpDate) {
        this.nearestExpDate = nearestExpDate;
    }
}