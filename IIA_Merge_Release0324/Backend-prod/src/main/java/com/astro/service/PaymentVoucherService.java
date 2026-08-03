package com.astro.service;

import com.astro.dto.workflow.PaymentVoucherReportDto;
import com.astro.dto.workflow.paymentVoucherRequestDto;
import com.astro.entity.PaymentVoucher;
import org.springframework.stereotype.Service;
import java.util.Map;
// import java.util.Objects;
import java.math.BigDecimal;

import java.util.List;

@Service
public interface PaymentVoucherService {

    public String createPaymentVoucher(paymentVoucherRequestDto dto);

    public paymentVoucherRequestDto getVoucherByProcessNo(String processNo);
    public List<PaymentVoucherReportDto> getPaymentVoucherReport(String startDate, String endDate);
    Map<String, BigDecimal> getAdvancePaidStatusByPoId(String poId);
Map<String, BigDecimal> getAdvancePaidStatusBySoId(String soId);
}
