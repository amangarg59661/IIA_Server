package com.astro.entity.InventoryModule;

import javax.persistence.*;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "goods_inspection_detail")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GiMaterialDtlEntity {
    
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
        
    @Column(name = "material_code", nullable = false)
    private String materialCode;
    
    @Column(name = "material_desc", nullable = false)
    private String materialDesc;

    @Column(name= "asset_id")
    private Integer assetId;
    
    @Column(name = "installation_report_filename")
    private String installationReportFileName;
    
    @Column(name = "received_quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal receivedQuantity;
    
    @Column(name = "accepted_quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal acceptedQuantity;
    
    @Column(name = "rejected_quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal rejectedQuantity;

    @Column(name = "reject_reason")
    private String rejectReason;
    
    @Column(name = "rejection_type")
    private String rejectionType;

    private String assetCode;

}