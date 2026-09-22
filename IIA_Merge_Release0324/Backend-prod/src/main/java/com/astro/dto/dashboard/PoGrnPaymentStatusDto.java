package com.astro.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response for GET /api/dashboard/poGrnPaymentStatus (Store Person dashboard).
 *
 * paymentVoucherPendingPos is left at 0 pending the Payment Voucher entity —
 * see the note in DashboardSummaryServiceImpl's additions for what's needed
 * to complete this field (memory of this project notes a
 * getPaymentVoucherReport method already exists somewhere to build from).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PoGrnPaymentStatusDto {
    private Long posApproved;
    private Long grnCompletedPos;
    private Long paymentVoucherPendingPos;
}
