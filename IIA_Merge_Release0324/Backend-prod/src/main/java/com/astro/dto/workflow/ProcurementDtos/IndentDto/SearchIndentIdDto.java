// package com.astro.dto.workflow.ProcurementDtos.IndentDto;

// import lombok.Data;

// @Data
// public class SearchIndentIdDto {

//     private String indentId;
//     private String currentStatus;
//     public SearchIndentIdDto(String indentId) {
//         this.indentId = indentId;
//     }
// }
package com.astro.dto.workflow.ProcurementDtos.IndentDto;

import lombok.Data;

@Data
public class SearchIndentIdDto {

    private String indentId;
    private String currentStatus;

    // Used by JPA for indentId-only projections (legacy)
    public SearchIndentIdDto(String indentId) {
        this.indentId = indentId;
    }

    // ✅ FIX: JPA uses this to populate both fields
    public SearchIndentIdDto(String indentId, String currentStatus) {
        this.indentId = indentId;
        this.currentStatus = currentStatus;
    }
}