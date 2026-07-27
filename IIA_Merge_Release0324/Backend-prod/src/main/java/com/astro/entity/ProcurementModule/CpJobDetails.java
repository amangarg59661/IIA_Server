package com.astro.entity.ProcurementModule;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "cp_job_details")
@EntityListeners(AuditingEntityListener.class)
public class CpJobDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_code", nullable = false)
    private String jobCode;

    @Column(name = "job_description")
    private String jobDescription;

    @Column(name = "quantity")
    private BigDecimal quantity;

    @Column(name = "estimated_price")
    private BigDecimal estimatedPrice;

    @Column(name = "uom")
    private String uom;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column(name = "budget_code")
    private String budgetCode;

    @Column(name = "job_category")
    private String jobCategory;

    @Column(name = "job_sub_category")
    private String jobSubCategory;

    @Column(name = "currency")
    private String currency;
    private BigDecimal gst;
    private String countryOfOrigin;

    @ManyToOne
    @JoinColumn(name = "contigency_id")
    @ToString.Exclude
    private ContigencyPurchase contigencyPurchase;

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