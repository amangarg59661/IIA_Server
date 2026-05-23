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

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name="ogp_master_po")
@Data
@EntityListeners(AuditingEntityListener.class)
public class OgpMasterPoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ogp_sub_process_id")
    private Integer ogpSubProcessId;

    @Column(name = "po_id", nullable = false)
    private String poId;

    @Column(name = "ogp_date", nullable = false)
    private LocalDate ogpDate;

    @Column(name = "location_id", nullable = false)
    private String locationId;

    @Column(name = "created_by", nullable = false)
    @CreatedBy
    private String createdBy;

    @Column(name = "create_date", nullable = false)
    @CreatedDate
    private LocalDateTime createDate;

    @Column(name = "ogp_type")
    private String ogpType;
    
    @Column(name = "status")
    private String status;
    @Column(name="sender_name")
    private String senderName;

    @Column(name="receiver_name")
    private String receiverName;

    @Column(name="receiver_location")
    private String receiverLocation;

    @Column(name="date_of_return")
    private LocalDate dateOfReturn;

}
