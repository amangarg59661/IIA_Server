package com.astro.entity.InventoryModule;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "goods_inspection_consumable_detail")
@Data
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
}