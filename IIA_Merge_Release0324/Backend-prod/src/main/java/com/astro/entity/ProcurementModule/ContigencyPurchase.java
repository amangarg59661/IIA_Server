package com.astro.entity.ProcurementModule;


import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "contigency_purchase")
public class ContigencyPurchase {

    @Id
    @Column(name = "contigency_id")
    private String contigencyId;
    @Column(name = "cp_number", unique = true)
    private Integer cpNumber;
    @Column(name = "vendors_name")
    private String vendorsName;
    @Column(name = "vendors_invoice_no")
    private String vendorsInvoiceNo;
    @Column(name = "Date")
    private LocalDate Date;
    // @Column(name = "material_code")
    //private String materialCode;
    // @Column(name = "material_description")
    // private String materialDescription;
    // @Column(name = "quantity")
    // private BigDecimal quantity;
    //  @Column(name = "unit_price")
    // private BigDecimal unitPrice;
    @Column(name = "remarks_for_purchase")
    private String remarksForPurchase;
    //  @Column(name = "amount_to_be_paid")
    //  private BigDecimal amountToBePaid;
    @Lob
    @Column(name = "upload_copy_of_invoice")
    private byte[] uploadCopyOfInvoice;
    @Column(name = "upload_copy_of_invoice_file_name")
    private String uploadCopyOfInvoiceFileName;
    @Column(name = "predifined_purchase_statement")
    private String predifinedPurchaseStatement;
    @Column(name = "project_detail")
    private String projectDetail;
    @Column(name = "project_name")
    private String projectName;
    @Column(name="file_type")
    private String fileType;
    @Column(name="current_status")
    private String currentStatus;

    private String paymentTo;
    private String paymentToVendor;
    private String paymentToEmployee;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "created_by")
    private Integer createdBy;
    private String purpose;
    private Boolean declarationOne;
    private Boolean declarationTwo;
    private BigDecimal totalCpValue;
    @OneToMany(mappedBy = "contigencyPurchase", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CpMaterials> cpMaterials;

    private LocalDateTime createdDate = LocalDateTime.now();

    private LocalDateTime updatedDate = LocalDateTime.now();






}
