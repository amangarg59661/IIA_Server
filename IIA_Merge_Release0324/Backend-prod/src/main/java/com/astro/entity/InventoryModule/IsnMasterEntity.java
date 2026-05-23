package com.astro.entity.InventoryModule;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

@Data
@Entity
@Table(name = "issue_note_master")
@EntityListeners(AuditingEntityListener.class)
public class IsnMasterEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "issue_note_id")
    private Integer issueNoteId;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "consignee_detail")
    private String consigneeDetail;

    @Column(name = "indentor_name")
    private String indentorName;

    @Enumerated(EnumType.STRING)
    @Column(name = "issue_note_type")
    private IssueNoteType issueNoteType;

    @Column(name = "field_station")
    private String fieldStation;

    @Column(name = "created_by")
    @CreatedBy
    private String createdBy;

    @Column(name = "create_date")
    @CreatedDate
    private LocalDateTime createDate;

    @Column(name="location_id")
    private String locationId;
    
    public enum IssueNoteType {
        Returnable,
        NonReturnable
    }
}
