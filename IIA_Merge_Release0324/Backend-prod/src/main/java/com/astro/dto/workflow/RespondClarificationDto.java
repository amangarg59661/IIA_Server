package com.astro.dto.workflow;

import lombok.Data;

/**
 * DTO used when a vendor, indentor, purchase personnel, or committee member
 * responds to a clarification request.
 */
@Data
public class RespondClarificationDto {

    private String tenderId;

    /**
     * Role of the person responding.
     * Allowed: VENDOR, INDENTOR, PURCHASE_PERSONNEL, COMMITTEE_MEMBER
     */
    private String respondedByRole;

    /** UserId (or vendorId for vendors) of the responder */
    private String respondedById;

    /** The clarification response text */
    private String responseText;

    /** Optional supporting file name (uploaded separately) */
    private String responseFileName;

    /** When PP responds on behalf of a vendor (GEM/OPEN/GLOBAL mode) */
    private String vendorId;

    /** PK of TenderClarificationHistory row — targets exact clarification question.
        Null = fallback to filtered lookup (backward compat). */
    private Long clarificationHistoryId;
}