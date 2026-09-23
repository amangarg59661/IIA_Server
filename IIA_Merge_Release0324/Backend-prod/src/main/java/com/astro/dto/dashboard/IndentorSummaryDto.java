package com.astro.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * Response for GET /api/dashboard/indentorSummary.
 *
 * myReceivedItemsThisMonth and inventoryStatusPercent are always null today:
 * - "received items" needs the GRN entity's link back to
 *   PurchaseOrder.indentId (IndentCreationRepository confirms
 *   purchase_order.indent_id exists, but I don't have the GRN entity to
 *   trace PO -> GRN -> "received").
 * - department-scoped inventory status needs a Location/Department master
 *   joining OhqMasterConsumableEntity.locatorId to a department name, which
 *   I don't have either.
 * Send those and I'll complete these two fields.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndentorSummaryDto {
    private Long myPurchaseRequests;
    private Long myApprovedRequestsThisMonth;
    private BigDecimal myReceivedItemsThisMonth;
    private Double inventoryStatusPercent;
}
