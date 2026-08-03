package com.astro.repository.InventoryModule;

import com.astro.entity.PaymentVoucherDeductions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentVoucherDeductionsRepository extends JpaRepository<PaymentVoucherDeductions, Long> {
}