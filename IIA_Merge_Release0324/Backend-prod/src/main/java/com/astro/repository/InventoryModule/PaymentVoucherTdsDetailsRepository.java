package com.astro.repository.InventoryModule;

import com.astro.entity.PaymentVoucherTdsDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentVoucherTdsDetailsRepository extends JpaRepository<PaymentVoucherTdsDetails, Long> {
}