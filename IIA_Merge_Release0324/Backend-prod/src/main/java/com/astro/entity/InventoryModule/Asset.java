package com.astro.entity.InventoryModule;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Data

@EntityListeners(AuditingEntityListener.class)
public class Asset {
    //@Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
   // private Long id;
    @Id
    private String assetCode;


    private String materialCode;


    private String description;


    private String uom;

    private String makeNo;
    private String modelNo;
    private String serialNo;
    private String componentName;
    private String componentCode;
    private int quantity;


    private String locator;

    private String transactionHistory;


    private String currentCondition;

    @LastModifiedBy
    private String updatedBy;
    @CreatedBy
    private String createdBy;


    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime updatedDate;


}
