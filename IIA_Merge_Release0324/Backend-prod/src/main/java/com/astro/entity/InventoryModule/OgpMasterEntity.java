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
@Table(name = "ogp_master")
@Data
@EntityListeners(AuditingEntityListener.class)
public class OgpMasterEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ogp_sub_process_id")
    private Integer ogpSubProcessId;

    @Column(name = "ogp_process_id")
    private String ogpProcessId;

    @Column(name = "issue_note_id")
    private Integer issueNoteId;

    @Column(name = "ogp_date")
    private LocalDate ogpDate;

    @Column(name = "status")
    private String status;

    @Column(name = "location_id")
    private String locationId;

    @Column(name = "created_by")
    @CreatedBy
    private String createdBy;

    @Column(name = "create_date")
    @CreatedDate
    private LocalDateTime createDate;

    @Column(name = "ogp_type")
    private String ogpType;
    @Column(name="sender_name")
    private String senderName;

    @Column(name="receiver_name")
    private String receiverName;

    @Column(name="receiver_location")
    private String receiverLocation;

    @Column(name="date_of_return")
    private LocalDate dateOfReturn;

}
