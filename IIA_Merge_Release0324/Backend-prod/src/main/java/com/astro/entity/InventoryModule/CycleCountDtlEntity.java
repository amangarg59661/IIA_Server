package com.astro.entity.InventoryModule;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "cycle_count_dtl")
@Data
@EntityListeners(AuditingEntityListener.class)
public class CycleCountDtlEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "cycle_count_id")
    private Long cycleCountId; // FK -> CycleCountMasterEntity.id (plain column, matching DemandAndIssueDtlEntity.diId style, no @ManyToOne)

    @Column(name = "material_code")
    private String materialCode;

    @Column(name = "material_desc")
    private String materialDesc;

    @Column(name = "uom")
    private String uom;

    @Column(name = "locator_id")
    private Integer locatorId;

    @Column(name = "custodian_id")
    private String custodianId; // resolved per row; null until approval for a net-new sweep row with no prior stock

    @Column(name = "system_qty_snapshot")
    private BigDecimal systemQtySnapshot;

    @Column(name = "unit_price_snapshot")
    private BigDecimal unitPriceSnapshot; // 0 for net-new rows -- pricing source still open per earlier discussion

    @Column(name = "counted_qty")
    private BigDecimal countedQty;

    @Column(name = "variance_qty")
    private BigDecimal varianceQty; // signed: countedQty - systemQtySnapshot

    @Column(name = "variance_value")
    private BigDecimal varianceValue; // signed: varianceQty * unitPriceSnapshot

    @Column(name = "multiple_custodian_rows")
    private Boolean multipleCustodianRows; // true when systemQtySnapshot was summed across >1 existing custodian row

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "create_date", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createDate;

    @CreatedBy
    @Column(name = "created_by", length = 50)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @LastModifiedDate
    @Column(name = "update_date")
    private LocalDateTime updateDate;
}
