package com.astro.entity.InventoryModule;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.EntityListeners;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@Entity
@Table(name = "grn_material_master")
@EntityListeners(AuditingEntityListener.class)
public class GrnMaterialMasterEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long grnId;

    private Long igpId;

    private LocalDate igpDate;

    @CreatedBy
    private String createdBy;

    @CreatedDate
    private LocalDateTime createDate;

    private Integer indentorId;

}
