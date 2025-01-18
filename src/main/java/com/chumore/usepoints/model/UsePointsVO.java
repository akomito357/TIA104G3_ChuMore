package com.chumore.usepoints.model;

import javax.persistence.*;

import com.chumore.discpts.model.DiscPtsVO;
import com.chumore.ordermaster.model.OrderMasterVO;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.io.Serializable;

@Entity
@Table(name = "use_points")
public class UsePointsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "use_points_id", nullable = false)
    private Integer usePointsId; // 流水號PK

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "disc_pts_id", referencedColumnName = "disc_pts_id", nullable = false)
    @JsonBackReference("discPts-usePoints")
    private DiscPtsVO discPts; // 折扣點數ID (外鍵)

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", referencedColumnName = "order_id", nullable = false)
    @JsonBackReference("orderMaster-usePoints")
    private OrderMasterVO orderMaster; // 訂單ID (外鍵)

    @Column(name = "used_points_quantity", nullable = false)
    private Integer usedPointsQuantity; // 使用點數數量

    // 無參數建構子
    public UsePointsVO() {}

    // 全參數建構子
    public UsePointsVO(Integer usePointsId, DiscPtsVO discPts, OrderMasterVO orderMaster, Integer usedPointsQuantity) {
        this.usePointsId = usePointsId;
        this.discPts = discPts;
        this.orderMaster = orderMaster;
        this.usedPointsQuantity = usedPointsQuantity;
    }

    // Getter 和 Setter
    public Integer getUsePointsId() {
        return usePointsId;
    }

    public void setUsePointsId(Integer usePointsId) {
        this.usePointsId = usePointsId;
    }

    public DiscPtsVO getDiscPts() {
        return discPts;
    }

    public void setDiscPts(DiscPtsVO discPts) {
        this.discPts = discPts;
    }

    public OrderMasterVO getOrderMaster() {
        return orderMaster;
    }

    public void setOrderMaster(OrderMasterVO orderMaster) {
        this.orderMaster = orderMaster;
    }

    public Integer getUsedPointsQuantity() {
        return usedPointsQuantity;
    }

    public void setUsedPointsQuantity(Integer usedPointsQuantity) {
        this.usedPointsQuantity = usedPointsQuantity;
    }

    @Override
    public String toString() {
        return "UsePointsVO{" +
                "usePointsId=" + usePointsId +
                ", discPts=" + (discPts != null ? discPts.getDiscPtsId() : null) +
                ", orderMaster=" + (orderMaster != null ? orderMaster.getOrderId() : null) +
                ", usedPointsQuantity=" + usedPointsQuantity +
                '}';
    }
}