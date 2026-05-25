package com.astro.entity.InventoryModule;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.EntityListeners;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Data
@Table(name = "gatepass_out_in")
@EntityListeners(AuditingEntityListener.class)
public class GatepassOutAndIn {
    @Id
    private String gatePassId;
    private String gatePassType;
    private String materialDetails;
    private LocalDate expectedDateOfReturn;
   // private BigDecimal editQuantity;
    @Column(name = "extendEDR")
    private BigDecimal extendEDR;
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
