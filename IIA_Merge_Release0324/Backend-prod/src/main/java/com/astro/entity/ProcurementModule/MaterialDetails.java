package com.astro.entity.ProcurementModule;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
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
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class MaterialDetails {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "material_code", nullable = false)
    private String materialCode;
    //@Column(name = "indent_id")
  //  @Column(name = "indent_id")
  //  private String indentId;

    @Column(name = "material_description")
    private String materialDescription;

    @Column(name = "quantity")
    private BigDecimal quantity;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    @Column(name = "uom")
    private String uom;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column(name = "budget_code")
    private String budgetCode;

    @Column(name = "material_category")
    private String materialCategory;

    @Column(name = "material_sub_category")
    private String materialSubCategory;

    @Column(name = "mode_of_procurement")
    private String modeOfProcurement;
    @Column(name="currency")
    private String currency;

    @Column(name = "conversion_rate")
    private BigDecimal conversionRate;

    @ManyToOne
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
