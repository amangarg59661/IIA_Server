package com.astro.dto.workflow;

import lombok.Data;

/**
 * DTO used by any approver (Indentor, Purchase Personnel, SPO, Chairman, Director) to seek
 * clarification from vendor, indentor, purchase personnel, or committee members.
 */
@Data
public class SeekClarificationDto {

    private String tenderId;

    /**
     * Role of the person seeking clarification.
     * Allowed: INDENTOR, PURCHASE_PERSONNEL, SPO, CHAIRMAN, DIRECTOR
     */
    private String requestedByRole;

    /** UserId of the person seeking clarification */
    private Integer requestedByUserId;

    /**
     * Who the clarification is sent to.
     * Allowed:
     *   VENDOR              - Goes to a specific vendor portal (targetVendorId required)
     *   ALL_VENDORS         - Goes to ALL vendors on this tender (for mass clarification)
     *   INDENTOR            - Goes back to Indentor (SPO or Chairman sending back)
     *   PURCHASE_PERSONNEL  - Goes back to Purchase Personnel (for GEM/non-portal tenders)
     *   CHAIRMAN            - Director sending clarification to Chairman
     *   SPECIFIC_MEMBER     - Chairman/Director sending to one specific committee member
     *   ALL_MEMBERS         - Chairman/Director sending to all committee members (for re-vote)
     */
    private String clarificationTarget;

    /**
     * When clarificationTarget = VENDOR: the specific vendor's ID.
     * If null and target = VENDOR, falls back to eval.approvedVendorId.
     * Not used for ALL_VENDORS (all latest quotations are marked).
     */
    private String targetVendorId;

    /**
     * UserId of the specific person when clarificationTarget = SPECIFIC_MEMBER
     * or INDENTOR/PURCHASE_PERSONNEL
     */
    private Integer targetUserId;

    /** Name of target person (display purposes) */
    private String targetUserName;

    /** The clarification question or remarks */
    private String remarks;
}