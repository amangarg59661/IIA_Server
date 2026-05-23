package com.astro.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Table(name = "work_master")
@Data
@EntityListeners(AuditingEntityListener.class)
public class WorkMaster {

    @Id
    @Column(name = "work_code")
    private String workCode;
    @Column(name="work_sub_category")
    private String workSubCategory;
  //  @Column(name = "mode_of_procurement")
  //  private String modeOfProcurement;

   // @OneToMany(mappedBy = "jobCode", cascade = CascadeType.ALL, orphanRemoval = true)
  //  private List<VendorNamesForJobWorkMaterial> vendorNames;
    @Column(name = "work_description")
    private String workDescription;
    @Column(name = "created_by")
    @CreatedBy
    private String createdBy;
    @Column(name = "updated_by")
    @LastModifiedBy
    private String updatedBy;

    @CreatedDate
    private LocalDateTime createdDate;
    @LastModifiedDate
    private LocalDateTime updatedDate;
}
