package com.astro.entity.InventoryModule;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity
@Table(name = "goods_inspection_consumable_detail")
@Data
@EntityListeners(AuditingEntityListener.class)
public class GoodsInspectionConsumableDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inspection_detail_id")
    private Integer inspectionDetailId;

    @Column(name = "inspection_sub_process_id")
    private Integer inspectionSubProcessId;

    @Column(name = "gprn_sub_process_id")
    private Integer gprnSubProcessId;

    @Column(name = "gprn_process_id")
    // private Integer gprnProcessId;
    private String gprnProcessId;

    @Column(name = "material_code")
    private String materialCode;

    @Column(name="uom_id")
    private String uomId;

    @Column(name = "material_desc")
    private String materialDesc;

    @Column(name = "installation_report_filename")
    private String installationReportFilename;

    @Column(name = "received_quantity")
    private BigDecimal receivedQuantity;

    @Column(name = "accepted_quantity")
    private BigDecimal acceptedQuantity;

    @Column(name = "rejected_quantity")
    private BigDecimal rejectedQuantity;

    @Column(name = "rejection_type")
    private String rejectionType;
    @Column(name = "reject_reason")
    private String rejectReason;

    @CreatedBy
    @Column(name = "created_by", length = 50)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @CreatedDate
    @Column(name = "create_date")
    private LocalDateTime createDate;

    @LastModifiedDate
    @Column(name = "update_date")
    private LocalDateTime updateDate;
}