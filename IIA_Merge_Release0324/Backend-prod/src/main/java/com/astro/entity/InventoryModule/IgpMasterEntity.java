package com.astro.entity.InventoryModule;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "igp_master")
@Data
@EntityListeners(AuditingEntityListener.class)
public class IgpMasterEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "igp_sub_process_id")
    private Integer igpSubProcessId;

    @Column(name = "igp_process_id")
    private String igpProcessId;

    @Column(name="ogp_sub_process_id")
    private Integer ogpSubProcessId;

    @Column(name = "igp_date")
    private LocalDate igpDate;

    @Column(name = "location_id")
    private String locationId;

    @Column(name = "created_by")
    @CreatedBy
    private String createdBy;

    @Column(name = "create_date")
    @CreatedDate
    private LocalDateTime createDate;
}