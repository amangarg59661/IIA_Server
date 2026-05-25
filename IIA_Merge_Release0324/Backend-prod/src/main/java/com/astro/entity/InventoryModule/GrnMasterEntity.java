package com.astro.entity.InventoryModule;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "grn_master")
@Data
@EntityListeners(AuditingEntityListener.class)
public class GrnMasterEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grn_sub_process_id")
    private Integer grnSubProcessId;
    
    @Column(name = "grn_process_id", nullable = false)
    private String grnProcessId;
    
    @Column(name = "gi_process_id")
    private String giProcessId;
    
    @Column(name = "gi_sub_process_id")
    private Integer giSubProcessId;

    @Column(name = "igp_process_id")
    private String igpProcessId;
    
    @Column(name = "igp_sub_process_id")
    private Integer igpSubProcessId;

    @Column(name = "grn_type", nullable = false)
    private String grnType;

    @Column(name = "grn_date")
    private LocalDate grnDate;
    
    @Column(name = "consignee_name")
    private String consigneeName;

    @Column(name = "installation_date")
    private LocalDate installationDate;
    
    @Column(name = "commissioning_date")
    private LocalDate commissioningDate;
    
    @Column(name = "created_by", nullable = false)
    @CreatedBy
    private String createdBy;
    
    @Column(name = "system_created_by", nullable = false)
    private String systemCreatedBy;
    
    @Column(name = "create_date", nullable = false)
    @CreatedDate
    private LocalDateTime createDate;
    
    @Column(name = "location_id", nullable = false)
    private String locationId;
    @Column(name = "status")
    private String status;

    private Integer custodianId;


}