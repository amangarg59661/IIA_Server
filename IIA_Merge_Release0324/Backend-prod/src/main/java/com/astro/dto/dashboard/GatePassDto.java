package com.astro.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * One row of GET /api/dashboard/recentGatePasses.
 *
 * itemCategory and issuedTo are best-effort, not confirmed:
 * - OgpMasterRepository's existing getOgpReport query never selects a
 *   receiver name for the main ogp_master table — only the separate
 *   ogp_master_rejected_gi query has receiver_name/receiver_location, and
 *   that's a different table entirely. issuedTo falls back to location_id
 *   (a raw ID, not a name) until a Location/Department master is available.
 * - There's no explicit "category" column on ogp_detail in the queries I've
 *   seen — itemCategory uses material_desc as a stand-in.
 * One row is emitted per gate-pass line item, so a multi-item gate pass
 * produces multiple rows (the reference screenshot's one-row-per-pass layout
 * assumes single-item passes).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GatePassDto {
    private String gatePassNo;
    private LocalDateTime dateTime;
    // private String itemCategory;
    private BigDecimal quantity;
    private String issuedTo;
}
