package com.astro.dto.workflow;

import com.astro.entity.VendorNamesForJobWorkMaterial;
import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.OneToMany;
import java.util.List;

@Data
public class WorkMasterRequestDto {

    private String workSubCategory;
  //  private String modeOfProcurement;
    private String workDescription;
   // private List<String> vendorNames;
    private String updatedBy;
    private String createdBy;







}
