
package com.astro.dto.workflow;

import lombok.Data;
import java.util.List;

/**
 * DTO used by the Director to form an ad-hoc committee for tenders above ₹1 Crore (Cases 9 & 10).
 */
@Data
public class DirectorFormCommitteeDto {

    private String tenderId;
    private Integer directorUserId;

    /** UserId of the Chairman for this ad-hoc committee */
    private Integer chairmanUserId;
    private String chairmanName;

    /** UserId of the Co-Chairman (optional) */
    private Integer coChairmanUserId;
    private String coChairmanName;

    /** List of regular committee members */
    private List<AdHocMemberDto> members;

    @Data
    public static class AdHocMemberDto {
        private Integer userId;
        private String memberName;
        private String designation;
    }
}