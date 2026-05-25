package com.astro.entity.InventoryModule;

import lombok.Data;

import javax.persistence.*;
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
@Table(name = "material_disposal")
@EntityListeners(AuditingEntityListener.class)
public class MaterialDisposal {

    @Id
    private String materialDisposalCode;
    private String disposalCategory;
    private String disposalMode;
    private String vendorDetails;
    private LocalDate disposalDate;
    private BigDecimal currentBookValue;
    private BigDecimal editReserveValue;
    private BigDecimal finalBidValue;
    @Lob
    private byte[] saleNote;
    private String saleNoteFileName;
    private BigDecimal editQuantity;
    private BigDecimal editValueMaterials;

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
