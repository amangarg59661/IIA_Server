package com.astro.entity.InventoryModule;

import java.time.LocalDate;

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
@Data
@Table(name = "ogp_master_rejected_gi")
@EntityListeners(AuditingEntityListener.class)
public class OgpMasterRejectedGiEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ogp_sub_process_id")
    private Integer ogpSubProcessId;

    @Column(name = "ogp_type", length = 20)
    private String ogpType;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "location_id")
    private String locationId;

    @Column(name = "gi_id", length = 255)
    private String giId;

    @Column(name = "created_by")
    @CreatedBy
    private String createdBy;

    @Column(name = "sender_name")
    private String senderName;

    @Column(name = "receiver_name")
    private String receiverName;

    @Column(name = "receiver_location")
    private String receiverLocation;

    @Column(name = "ogp_date")
    private LocalDate ogpDate;

    @Column(name = "return_date")
    private LocalDate returnDate;


}

