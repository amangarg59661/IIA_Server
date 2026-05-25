package com.astro.entity.InventoryModule;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
public class GoodsTransfer {

    @Id
    @Column(name = "goods_transfer_id")
    private String goodsTransferID;
    private String consignorDetails;
    private String consigneeDetails;
    private String fieldStationName;
    private String materialCode;
    private String uom;
    private Integer quantity;
    private String locator;
    private String note;
    @Column(name = "created_by")
    @CreatedBy
    private String createdBy;
    @Column(name = "updated_by")
    @LastModifiedBy
    private String updatedBy;
    @Column(name = "created_date", nullable = false)
    @CreatedDate
    private LocalDateTime createdDate;
    @Column(name = "updated_date", nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedDate;
}
