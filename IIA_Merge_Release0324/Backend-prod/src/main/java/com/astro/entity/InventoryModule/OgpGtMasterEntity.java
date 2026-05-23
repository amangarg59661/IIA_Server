package com.astro.entity.InventoryModule;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.EntityListeners;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "ogp_gt_master")
@EntityListeners(AuditingEntityListener.class)
public class OgpGtMasterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="gt_id")
    private Long gtId;

    @Column(name = "sender_location_id", nullable = false)
    private String senderLocationId;

    @Column(name="status")
    private String status;

    // Kept user's spelling "Cusodian" in column name; feel free to rename in DB if desired
    @Column(name = "sender_custodian_id", nullable = false, length = 64)
    private Integer senderCustodianId;

    @Column(name = "receiver_location_id", nullable = false)
    private String receiverLocationId;

    @Column(name = "receiver_custodian_id", nullable = false, length = 64)
    private Integer receiverCustodianId;

    // User field was "createdate": map to that column while keeping Java-friendly name
    @Column(name = "create_date", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createDate;

    @Column(name="gt_date", nullable = false)
    private LocalDate gtDate;

    @Column(name = "created_by", nullable = false, length = 100)
    @CreatedBy
    private String createdBy;
}
