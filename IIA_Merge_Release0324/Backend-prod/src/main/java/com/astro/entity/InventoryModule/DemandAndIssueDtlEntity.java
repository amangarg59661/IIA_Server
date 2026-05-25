package com.astro.entity.InventoryModule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "demand_and_issue_dtl")
@EntityListeners(AuditingEntityListener.class)
public class DemandAndIssueDtlEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="di_id")
    private Long diId;

    @Column(name = "asset_id")
    private Integer assetId;

    @Column(name = "asset_desc", length = 500)
    private String assetDesc;

    @Column(name = "material_code", length = 100)
    private String materialCode;

    @Column(name = "material_desc", length = 500)
    private String materialDesc;

    @Column(name = "quantity", nullable = false)
    private BigDecimal quantity;

    @Column(name = "receiver_locator_id")
    private Integer receiverLocatorId;

    @Column(name = "sender_locator_id")
    private Integer senderLocatorId;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    @Column(name = "depriciation_rate")
    private BigDecimal depriciationRate;

    @Column(name = "book_value")
    private BigDecimal bookValue;
    private String uom;

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
