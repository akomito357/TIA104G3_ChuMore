package com.chumore.tabletype.model;
import javax.persistence.*;

import com.chumore.rest.model.RestVO;

import java.io.Serializable;

@Entity
@Table(name = "table_type")
public class TableTypeVO implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "table_type_id",updatable = false)
    private Integer tableTypeId;


    @Column(name = "table_count")
    private Integer tableCount;

    @Column(name = "reserved_limit")
    private String reservedLimit;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="rest_Id",referencedColumnName="rest_id")
    private RestVO rest;

    public TableTypeVO(){

    }


    public RestVO getRest() {
        return rest;
    }

    public void setRest(RestVO rest) {
        this.rest = rest;
    }

    public Integer getTableTypeId() {
        return tableTypeId;
    }

    public void setTableTypeId(Integer tableTypeId) {
        this.tableTypeId = tableTypeId;
    }

    public Integer getTableCount() {
        return tableCount;
    }

    public void setTableCount(Integer tableCount) {
        this.tableCount = tableCount;
    }

    public String getReservedLimit() {
        return reservedLimit;
    }

    public void setReservedLimit(String reservedLimit) {
        this.reservedLimit = reservedLimit;
    }

    @Override
    public String toString() {
        return "TableType["+"tableTypeId=" + tableTypeId + ", tableCount=" + tableCount + ", reservedLimit=" + reservedLimit + "]";
    }
}
