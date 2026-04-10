package com.astro.dto.AdminPanel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
// Modified by Aman
import java.math.BigDecimal;
// End
/**
 * DTO for LOV creation/update requests from admin panel
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LOVRequestDto {

    private Long designatorId;
    private String lovValue;
    private String lovDisplayValue;
    private String lovDescription;
    private Boolean isActive = true;
    private Boolean isDefault = false;
    private Integer displayOrder;
    private String colorCode;
    private String iconName;
    private Long parentLovId;
       // Added by Aman
    private BigDecimal departmentLimit;
    // End  
}
