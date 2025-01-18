package com.chumore.dailyreservation.model;

import com.chumore.rest.model.RestVO;
import com.chumore.tabletype.model.TableTypeVO;
import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name="daily_reservation")
public class DailyReservationVO implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="daily_reservation_id",updatable= false)
    private Integer dailyReservationId;


    @Column(name="reserved_date")
    private LocalDate reservedDate;

    @Column(name="reserved_tables")
    private String reservedTables;

    @Column(name="reserved_limit")
    private String reservedLimit;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="table_type_id",referencedColumnName="table_type_id")
    @JsonBackReference("dailyReservation-tableType")
    private TableTypeVO tableType;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="rest_id",referencedColumnName="rest_id")
    @JsonBackReference("dailyReservation-rest")
    private RestVO rest;

    public DailyReservationVO(){


    }


    // Getters and Setters
    public Integer getTableTypeName(){
        return tableType.getTableType();
    }

    public String getBusinessHours(){
        return rest.getBusinessHours();
    }



    public RestVO getRest() {
        return rest;
    }

    public void setRest(RestVO rest) {
        this.rest = rest;
    }


    public TableTypeVO getTableType() {
        return tableType;
    }

    public void setTableType(TableTypeVO tableType) {
        this.tableType = tableType;
    }


    public Integer getDailyReservationId() {
        return dailyReservationId;
    }

    public void setDailyReservationId(Integer dailyReservationId) {
        this.dailyReservationId = dailyReservationId;
    }

    public LocalDate getReservedDate() {
        return reservedDate;
    }

    public void setReservedDate(LocalDate reservedDate) {
        this.reservedDate = reservedDate;
    }

    public String getReservedTables() {
        return reservedTables;
    }

    public void setReservedTables(String reservedTables) {
        this.reservedTables = reservedTables;
    }

    public String getReservedLimit() {
        return reservedLimit;
    }

    public void setReservedLimit(String reservedLimit) {
        this.reservedLimit = reservedLimit;
    }

    @Override
    public String toString(){
        return "DailyReservationVO [dailyReservationId=" + dailyReservationId + ", reservedDate=" + reservedDate + ", reservedTables=" + reservedTables + ", reservedLimit=" + reservedLimit + "]";
    }
}