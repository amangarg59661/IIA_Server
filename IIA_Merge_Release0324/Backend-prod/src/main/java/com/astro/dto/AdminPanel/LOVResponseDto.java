package com.astro.dto.AdminPanel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for LOV (List of Values) response
 * Used to send dropdown values to frontend
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LOVResponseDto {

    private Long lovId;
    private String value;           // lovValue - actual value to be stored
    private String displayValue;    // lovDisplayValue - label to be shown in UI
    private String description;     // lovDescription - additional info/tooltip
    private Boolean isActive;
    // private String locationCode;  // added for Locator Added bu Aman 
    private Boolean isDefault;
    private Integer displayOrder;
    private String colorCode;       // For UI styling (e.g., status colors)
    private String iconName;        // For UI icons
    private Long parentLovId;       // For dependent/cascading dropdowns
}
