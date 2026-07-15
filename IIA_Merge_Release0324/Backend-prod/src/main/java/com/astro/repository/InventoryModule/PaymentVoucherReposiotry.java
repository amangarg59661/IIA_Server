package com.astro.repository.InventoryModule;

import com.astro.entity.PaymentVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentVoucherReposiotry extends JpaRepository<PaymentVoucher, Long> {

    PaymentVoucher findByGrnNumber(String grnNumber);

    //Optional<PaymentVoucher> findByGrnNumber(String grnNumber);
    Optional<PaymentVoucher> findTopByGrnNumberOrderByIdDesc(String grnNumber);

    boolean existsByGrnNumberAndPaymentVoucherType(String grnNumber, String paymentVoucherType);

    Optional<PaymentVoucher> findTopByServiceOrderDetailsOrderByIdDesc(String soId);

    Optional<PaymentVoucher> findTopByCpDetailsOrderByIdDesc(String cpDetails);

    List<PaymentVoucher> findByCreatedDateBetween(LocalDateTime start, LocalDateTime end);

}
