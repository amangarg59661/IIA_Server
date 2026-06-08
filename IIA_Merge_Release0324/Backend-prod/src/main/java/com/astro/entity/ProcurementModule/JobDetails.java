package com.astro.entity.ProcurementModule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@Table(name = "job_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class JobDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_code")
    private String jobCode;

    @Column(name = "job_description", length = 500)
    private String jobDescription;

    @Column(name = "category")
    private String category;

    @Column(name = "sub_category")
    private String subCategory;

    @Column(name = "uom")
    private String uom;

    @Column(name = "brief_description", length = 1000)
    private String briefDescription;

    @Column(name = "estimated_price", precision = 19, scale = 2)
    private BigDecimal estimatedPrice;

    @Column(name = "total_price", precision = 19, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "currency")
    private String currency;

    @Column(name = "origin")
    private String origin;  // Indigenous or Imported

    @Column(name = "quantity", precision = 19, scale = 2)
    private BigDecimal quantity;

    @Column(name = "conversion_rate")
    private BigDecimal conversionRate;

    @Column(name = "mode_of_procurement", length = 100)
    private String modeOfProcurement;

    @Column(name = "budget_code", length = 100)
    private String budgetCode;

    @Column(name = "vendor_names", length = 2000)
    private String vendorNames;  // comma-separated vendor names

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "indent_id", referencedColumnName = "indent_id")
    @ToString.Exclude
    private IndentCreation indentCreation;

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