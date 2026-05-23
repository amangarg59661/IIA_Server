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

@Data
@Entity
@Table(name = "gprn_material_detail")
@EntityListeners(AuditingEntityListener.class)
public class GprnMaterialDtlEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detail_id")
    private Integer detailId;

    // @ManyToOne
    // @JoinColumn(name = "process_id", insertable = false, updatable = false)
    // private GprnMasterEntity gprnMaster;

    @Column(name="process_id")
    private String processId;

    @Column(name="category")
    private String category;

    @Column(name = "sub_process_id")
    private Integer subProcessId;

    @Column(name = "po_id")
    private String poId;

    @Column(name = "material_code")
    private String materialCode;

    @Column(name = "material_desc")
    private String materialDesc;

    @Column(name = "uom_id")
    private String uomId;

    @Column(name = "received_quantity")
    private BigDecimal receivedQuantity;
    @Column(name = "quantity_delivered")
    private BigDecimal quantityDelivered;
    
    @Column(name = "ordered_quantity")
    private BigDecimal orderedQuantity;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    @Column(name = "make_no")
    private String makeNo;

    @Column(name = "serial_no")
    private String serialNo;

    @Column(name = "model_no")
    private String modelNo;

    @Column(name = "warranty_terms")
    private String warrantyTerms;

    @Column(name = "note")
    private String note;

    @Column(name = "photo_path")
    private String fileName;

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
