package com.astro.entity.ProcurementModule;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Tracks vendors manually added by Purchase Personnel during GeM/Open/Global
 * Tender Evaluation. Once "Send Quotation for Evaluation" is clicked, the entry
 * is promoted into VendorQuotationAgainstTender and appears in Tender Evaluation.
 */
@Data
@Entity
@Table(name = "gem_tender_evaluation")
public class GemTenderEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tender_id", nullable = false, length = 255)
    private String tenderId;

    @Column(name = "vendor_name", nullable = false, length = 300)
    private String vendorName;

    @Column(name = "vendor_id", length = 100)
    private String vendorId;

    @Column(name = "technical_doc_file_name", length = 500)
    private String technicalDocFileName;

    @Column(name = "financial_doc_file_name", length = 500)
    private String financialDocFileName;

    @Column(name = "added_by_user_id")
    private Integer addedByUserId;

    /** PENDING, SENT_FOR_EVALUATION */
    @Column(name = "status", length = 30)
    private String status = "PENDING";

    @Column(name = "remarks", length = 1000)
    private String remarks;

    @Column(name = "sent_for_evaluation")
    private Boolean sentForEvaluation = false;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "created_date")
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "updated_date")
    private LocalDateTime updatedDate = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        updatedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }
}
