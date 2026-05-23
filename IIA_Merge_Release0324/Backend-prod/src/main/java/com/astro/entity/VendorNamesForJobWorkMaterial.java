package com.astro.entity;

import com.astro.entity.ProcurementModule.MaterialDetails;
import lombok.Data;

import javax.persistence.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "vendor_names_for_job_work_material")
@EntityListeners(AuditingEntityListener.class)
public class VendorNamesForJobWorkMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long materialId;
    private String indentId;

    @Column(name = "vendor_name", nullable = false)
    private String vendorName;

    @Column(name = "job_code")
    private String jobCode;

    @Column(name = "material_code")
    private String materialCode;

    @Column(name = "work_code")
    private String workCode;

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
