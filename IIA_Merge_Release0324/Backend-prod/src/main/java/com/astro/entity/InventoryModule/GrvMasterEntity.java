package com.astro.entity.InventoryModule;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.EntityListeners;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "grv_master")
@Data
@EntityListeners(AuditingEntityListener.class)
public class GrvMasterEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer grvSubProcessId;
    
    private Integer giSubProcessId;
    private String giProcessId;
    private String grvProcessId;
    private LocalDate date;
    @CreatedBy
    private String createdBy;
    private String locationId;
    
    @Column(name = "create_date", updatable = false)
    @CreatedDate
    private LocalDateTime createDate;
}