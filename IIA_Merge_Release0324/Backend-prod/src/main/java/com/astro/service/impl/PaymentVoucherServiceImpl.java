package com.astro.service.impl;

import com.astro.dto.workflow.PaymentVoucherMaterialDto;
import com.astro.dto.workflow.PaymentVoucherReportDto;
import com.astro.dto.workflow.PaymentVoucherTdsDto;
import com.astro.dto.workflow.PaymentVoucherDeductionDto;
import com.astro.dto.workflow.paymentVoucherMaterialRequestDto;
import com.astro.dto.workflow.paymentVoucherRequestDto;
import com.astro.dto.workflow.paymentVoucherTdsRequestDto;
import com.astro.dto.workflow.paymentVoucherDeductionRequestDto;
import com.astro.entity.PaymentVoucher;
import com.astro.entity.PaymentVoucherMaterials;
import com.astro.entity.PaymentVoucherTdsDetails;
import com.astro.entity.PaymentVoucherDeductions;
import com.astro.repository.InventoryModule.PaymentVoucherMaterialsRepository;
import com.astro.repository.InventoryModule.PaymentVoucherReposiotry;
import com.astro.service.PaymentVoucherService;
import com.astro.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaymentVoucherServiceImpl implements PaymentVoucherService {
    @Autowired
    private PaymentVoucherReposiotry paymentVoucherReposiotry;
    @Autowired
    private PaymentVoucherMaterialsRepository paymentVoucherMaterialsRepository;

    @Override
    @Transactional
    public String createPaymentVoucher(paymentVoucherRequestDto dto) {

        PaymentVoucher voucher = new PaymentVoucher();
        voucher.setPaymentVoucherNumber("INV/1");
        voucher.setPaymentVoucherDate(dto.getPaymentVoucherDate());
        voucher.setPaymentVoucherIsFor(dto.getPaymentVoucherIsFor());
        voucher.setPurchaseOrderId(dto.getPurchaseOrderId());
        voucher.setGrnNumber(dto.getGrnNumber());
        voucher.setServiceOrderDetails(dto.getServiceOrderDetails());
        voucher.setInspectionProcessId(dto.getInspectionProcessId()); 
        voucher.setPaymentVoucherType(dto.getPaymentVoucherType());
        voucher.setVendorName(dto.getVendorName());
        voucher.setVendorInvoiceNumber(dto.getVendorInvoiceNumber());
        voucher.setVendorInvoiceDate(dto.getVendorInvoiceDate());
        voucher.setCurrency(dto.getCurrency());
        voucher.setExchangeRate(dto.getExchangeRate());
        voucher.setStatus(dto.getStatus());
        voucher.setRemarks(dto.getRemarks());
        voucher.setTotalAmount(dto.getTotalAmount());
        voucher.setPartialAmount(dto.getPartialAmount());
        voucher.setAdvanceAmount(dto.getAdvanceAmount());
        voucher.setSoId(dto.getServiceOrderDetails());
        voucher.setCpDetails(dto.getCpDetails());
        // voucher.setTdsAmount(dto.getTdsAmount());
        voucher.setPaymentVoucherNetAmount(dto.getPaymentVoucherNetAmount());

        Optional<PaymentVoucher> existingVoucherOpt;
        String paymentFor = dto.getPaymentVoucherIsFor();

        if ("CP".equalsIgnoreCase(paymentFor) && "Advance".equalsIgnoreCase(dto.getPaymentVoucherType())) {
            throw new IllegalArgumentException("Advance payment is not allowed for Contingency Purchase.");
        }
if ("Service Order".equalsIgnoreCase(paymentFor) && !"Advance".equalsIgnoreCase(dto.getPaymentVoucherType())) {
            existingVoucherOpt = paymentVoucherReposiotry.findTopByInspectionProcessIdOrderByIdDesc(dto.getInspectionProcessId());
        } else if ("Service Order".equalsIgnoreCase(paymentFor)) {
            existingVoucherOpt = paymentVoucherReposiotry.findTopByServiceOrderDetailsOrderByIdDesc(dto.getServiceOrderDetails());
        
        // if ("Service Order".equalsIgnoreCase(paymentFor)) {
        //     existingVoucherOpt = paymentVoucherReposiotry.findTopByServiceOrderDetailsOrderByIdDesc(dto.getServiceOrderDetails());
        } else if ("CP".equalsIgnoreCase(paymentFor)) {
            existingVoucherOpt = paymentVoucherReposiotry.findTopByCpDetailsOrderByIdDesc(dto.getCpDetails());
        } else if ("Purchase Order".equalsIgnoreCase(paymentFor) && "Advance".equalsIgnoreCase(dto.getPaymentVoucherType())) {
            existingVoucherOpt = paymentVoucherReposiotry.findTopByPurchaseOrderIdOrderByIdDesc(dto.getPurchaseOrderId());
        } else {
            existingVoucherOpt = paymentVoucherReposiotry.findTopByGrnNumberOrderByIdDesc(dto.getGrnNumber());
        }

        // Optional<PaymentVoucher> existingVoucherOpt;
        // String paymentFor = dto.getPaymentVoucherIsFor();

        // if ("Service Order".equalsIgnoreCase(paymentFor)) {
        //     existingVoucherOpt = paymentVoucherReposiotry.findTopByServiceOrderDetailsOrderByIdDesc(dto.getServiceOrderDetails());
        // } else if ("CP".equalsIgnoreCase(paymentFor)) {
        //     existingVoucherOpt = paymentVoucherReposiotry.findTopByCpDetailsOrderByIdDesc(dto.getCpDetails());
        // } else {
        //     existingVoucherOpt = paymentVoucherReposiotry.findTopByGrnNumberOrderByIdDesc(dto.getGrnNumber());
        // }

        if (existingVoucherOpt.isPresent()) {
            PaymentVoucher existingVoucher = existingVoucherOpt.get();
            String type = existingVoucher.getPaymentVoucherType();

            if ("Partial".equalsIgnoreCase(type)) {
                BigDecimal paid = existingVoucher.getPaidAmount() != null
                        ? existingVoucher.getPaidAmount()
                        : BigDecimal.ZERO;
                BigDecimal partial = dto.getPartialAmount() != null
                        ? dto.getPartialAmount()
                        : BigDecimal.ZERO;
                voucher.setPaidAmount(paid.add(partial));
            } else if ("Advance".equalsIgnoreCase(type)) {
                BigDecimal paid = existingVoucher.getPaidAmount() != null
                        ? existingVoucher.getPaidAmount()
                        : BigDecimal.ZERO;
                BigDecimal partial = dto.getAdvanceAmount() != null
                        ? dto.getAdvanceAmount()
                        : BigDecimal.ZERO;
                voucher.setPaidAmount(paid.add(partial));
            }
        }else{
          if(dto.getPartialAmount()!=null){
              voucher.setPaidAmount(dto.getPartialAmount());
          }else{
              voucher.setPaidAmount(dto.getAdvanceAmount());
          }
        }
        List<PaymentVoucherMaterials> materialsList = dto.getMaterials().stream().map(m -> {
            PaymentVoucherMaterials material = new PaymentVoucherMaterials();
            material.setMaterialCode(m.getMaterialCode());
            material.setMaterialDescription(m.getMaterialDescription());
            material.setQuantity(m.getQuantity());
            material.setUnitPrice(m.getUnitPrice());
            material.setCurrency(m.getCurrency());
            material.setExchangeRate(m.getExchangeRate());
            material.setGst(m.getGst());
            material.setPaymentVoucher(voucher);
            return material;
        }).collect(Collectors.toList());

        voucher.setMaterialsList(materialsList);

        List<PaymentVoucherTdsDetails> tdsList = dto.getTdsList() == null ? new ArrayList<>() :
                dto.getTdsList().stream().map(t -> {
                    PaymentVoucherTdsDetails tds = new PaymentVoucherTdsDetails();
                    tds.setTdsSection(t.getTdsSection());
                    tds.setTdsAmount(t.getTdsAmount());
                    tds.setRemarks(t.getRemarks());
                    tds.setPaymentVoucher(voucher);
                    return tds;
                }).collect(Collectors.toList());
        voucher.setTdsList(tdsList);
        voucher.setTdsAmount(tdsList.stream()
                .map(PaymentVoucherTdsDetails::getTdsAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        List<PaymentVoucherDeductions> deductionsList = dto.getDeductions() == null ? new ArrayList<>() :
                dto.getDeductions().stream().map(d -> {
                    PaymentVoucherDeductions deduction = new PaymentVoucherDeductions();
                    deduction.setDeductionName(d.getDeductionName());
                    deduction.setDeductionAmount(d.getDeductionAmount());
                    deduction.setRemarks(d.getRemarks());
                    deduction.setPaymentVoucher(voucher);
                    return deduction;
                }).collect(Collectors.toList());
        voucher.setDeductionsList(deductionsList);
        voucher.setDeductionAmount(deductionsList.stream()
                .map(PaymentVoucherDeductions::getDeductionAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        PaymentVoucher pv = paymentVoucherReposiotry.save(voucher);

        if ("Service Order".equalsIgnoreCase(paymentFor)) {
            return dto.getServiceOrderDetails() + "/" + pv.getId();
        } else if ("CP".equalsIgnoreCase(paymentFor)) {
            return dto.getCpDetails() + "/" + pv.getId();
        } else {
            return dto.getGrnNumber() + "/" + pv.getId();
        }
    }


    public paymentVoucherRequestDto getVoucherByProcessNo(String processNo) {

        String[] parts = processNo.split("/");
        Long id = Long.parseLong(parts[parts.length - 1]);
        Optional<PaymentVoucher> entitys = paymentVoucherReposiotry.findById(id);


        PaymentVoucher entity=null;
        if(entitys.isPresent()){
            entity = entitys.get();
        }
        paymentVoucherRequestDto dto = new paymentVoucherRequestDto();
        dto.setPaymentVoucherDate(entity.getPaymentVoucherDate());
        dto.setPaymentVoucherIsFor(entity.getPaymentVoucherIsFor());
        dto.setPurchaseOrderId(entity.getPurchaseOrderId());
        dto.setGrnNumber(entity.getGrnNumber());
        dto.setServiceOrderDetails(entity.getServiceOrderDetails());
        dto.setInspectionProcessId(entity.getInspectionProcessId());
        dto.setPaymentVoucherType(entity.getPaymentVoucherType());
        dto.setVendorName(entity.getVendorName());
        dto.setVendorInvoiceNumber(entity.getVendorInvoiceNumber());
        dto.setVendorInvoiceDate(entity.getVendorInvoiceDate());
        dto.setCurrency(entity.getCurrency());
        dto.setExchangeRate(entity.getExchangeRate());
        dto.setStatus(entity.getStatus());
        dto.setRemarks(entity.getRemarks());
        dto.setTotalAmount(entity.getTotalAmount());
        dto.setPartialAmount(entity.getPartialAmount());
        dto.setAdvanceAmount(entity.getAdvanceAmount());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setPaymentVoucherNetAmount(entity.getPaymentVoucherNetAmount());
        // dto.setTdsAmount(entity.getTdsAmount());
        dto.setCpDetails(entity.getCpDetails());

        // Map materials
        if (entity.getMaterialsList() != null) {
            dto.setMaterials(entity.getMaterialsList().stream().map(this::mapMaterial).collect(Collectors.toList()));
        }
        // Map TDS lines
        if (entity.getTdsList() != null) {
            dto.setTdsList(entity.getTdsList().stream().map(this::mapTds).collect(Collectors.toList()));
        }

        // Map deduction lines
        if (entity.getDeductionsList() != null) {
            dto.setDeductions(entity.getDeductionsList().stream().map(this::mapDeduction).collect(Collectors.toList()));
        }

        return dto;
    }

    private paymentVoucherMaterialRequestDto mapMaterial(PaymentVoucherMaterials m) {
        paymentVoucherMaterialRequestDto dto = new paymentVoucherMaterialRequestDto();
        dto.setMaterialCode(m.getMaterialCode());
        dto.setMaterialDescription(m.getMaterialDescription());
        dto.setQuantity(m.getQuantity());
        dto.setUnitPrice(m.getUnitPrice());
        dto.setCurrency(m.getCurrency());
        dto.setExchangeRate(m.getExchangeRate());
        dto.setGst(m.getGst());
        return dto;
    }
     private paymentVoucherTdsRequestDto mapTds(PaymentVoucherTdsDetails t) {
        paymentVoucherTdsRequestDto dto = new paymentVoucherTdsRequestDto();
        dto.setTdsSection(t.getTdsSection());
        dto.setTdsAmount(t.getTdsAmount());
        dto.setRemarks(t.getRemarks());
        return dto;
    }

    private paymentVoucherDeductionRequestDto mapDeduction(PaymentVoucherDeductions d) {
        paymentVoucherDeductionRequestDto dto = new paymentVoucherDeductionRequestDto();
        dto.setDeductionName(d.getDeductionName());
        dto.setDeductionAmount(d.getDeductionAmount());
        dto.setRemarks(d.getRemarks());
        return dto;
    }
public Map<String, BigDecimal> getAdvancePaidStatusByPoId(String poId) {
        Optional<PaymentVoucher> existing = paymentVoucherReposiotry.findTopByPurchaseOrderIdOrderByIdDesc(poId);
        BigDecimal paid = existing.map(PaymentVoucher::getPaidAmount).orElse(BigDecimal.ZERO);
        Map<String, BigDecimal> res = new HashMap<>();
        res.put("advanceAmountAlreadyPaid", paid);
        return res;
    }

    public Map<String, BigDecimal> getAdvancePaidStatusBySoId(String soId) {
        Optional<PaymentVoucher> existing = paymentVoucherReposiotry.findTopByServiceOrderDetailsOrderByIdDesc(soId);
        BigDecimal paid = existing.map(PaymentVoucher::getPaidAmount).orElse(BigDecimal.ZERO);
        Map<String, BigDecimal> res = new HashMap<>();
        res.put("advanceAmountAlreadyPaid", paid);
        return res;
    }
    @Override
    public List<PaymentVoucherReportDto> getPaymentVoucherReport(String startDate, String endDate) {


        List<LocalDateTime> range = CommonUtils.getDateRenge(startDate, endDate);
        LocalDateTime start = range.get(0);
        LocalDateTime end = range.get(1);


        List<PaymentVoucher> vouchers = paymentVoucherReposiotry
                .findByCreatedDateBetween(start, end);

        List<PaymentVoucherReportDto> reportList = new ArrayList<>();

        for (PaymentVoucher voucher : vouchers) {
            PaymentVoucherReportDto dto = new PaymentVoucherReportDto();


            // String pvIsFor = voucher.getPaymentVoucherIsFor();
            // String id;
            // if ("Service Order".equalsIgnoreCase(pvIsFor)) {
            //     id = voucher.getServiceOrderDetails() + "/" + voucher.getId();
            // } else if ("CP".equalsIgnoreCase(pvIsFor)) {
            //     id = voucher.getCpDetails() + "/" + voucher.getId();
            // } else {
            //     id = voucher.getGrnNumber() + "/" + voucher.getId();
            // }
            String pvIsFor = voucher.getPaymentVoucherIsFor();
            String id;
            if ("Service Order".equalsIgnoreCase(pvIsFor)) {
                id = voucher.getServiceOrderDetails() + "/" + voucher.getId();
            } else if ("CP".equalsIgnoreCase(pvIsFor)) {
                id = voucher.getCpDetails() + "/" + voucher.getId();
            } else if ("Purchase Order".equalsIgnoreCase(pvIsFor) && "Advance".equalsIgnoreCase(voucher.getPaymentVoucherType())) {
                id = voucher.getPurchaseOrderId() + "/" + voucher.getId();
            } else {
                id = voucher.getGrnNumber() + "/" + voucher.getId();
            }
            dto.setPaymentVoucherNumber(id);
            dto.setPaymentVoucherDate(voucher.getPaymentVoucherDate());
            dto.setPaymentVoucherIsFor(pvIsFor);
            dto.setGrnNumber(voucher.getGrnNumber());
            if ("Purchase Order".equalsIgnoreCase(pvIsFor)) {
                String poId = "PO" + voucher.getPurchaseOrderId();
                dto.setPurchaseOrderId(poId);
            } else if ("Service Order".equalsIgnoreCase(pvIsFor)) {
                dto.setSoId(voucher.getSoId());
            } else if ("CP".equalsIgnoreCase(pvIsFor)) {
                dto.setCpDetails(voucher.getCpDetails());
            }

           // dto.setServiceOrderDetails(voucher.getServiceOrderDetails());
            dto.setPaymentVoucherType(voucher.getPaymentVoucherType());
            dto.setVendorName(voucher.getVendorName());
            dto.setVendorInvoiceNumber(voucher.getVendorInvoiceNumber());
            dto.setVendorInvoiceDate(voucher.getVendorInvoiceDate());
            dto.setCurrency(voucher.getCurrency());
            dto.setExchangeRate(voucher.getExchangeRate());
            dto.setRemarks(voucher.getRemarks());
            dto.setTotalAmount(voucher.getTotalAmount());
            dto.setPartialAmount(voucher.getPartialAmount());
            dto.setAdvanceAmount(voucher.getAdvanceAmount());
            dto.setPaidAmount(voucher.getPaidAmount());
            dto.setTdsAmount(voucher.getTdsAmount());
            dto.setDeductionAmount(voucher.getDeductionAmount());
            dto.setPaymentVoucherNetAmount(voucher.getPaymentVoucherNetAmount());


            dto.setCreatedBy(voucher.getCreatedBy());
            dto.setCreatedDate(voucher.getCreatedDate());


            List<PaymentVoucherMaterialDto> materialDtos = voucher.getMaterialsList().stream()
                    .map(m -> {
                        PaymentVoucherMaterialDto mdto = new PaymentVoucherMaterialDto();
                        mdto.setMaterialCode(m.getMaterialCode());
                        mdto.setMaterialDescription(m.getMaterialDescription());
                        mdto.setQuantity(m.getQuantity());
                        mdto.setUnitPrice(m.getUnitPrice());
                        mdto.setCurrency(m.getCurrency());
                        mdto.setExchangeRate(m.getExchangeRate());
                        mdto.setGst(m.getGst());
                        return mdto;
                    }).toList();

            dto.setMaterials(materialDtos);

            List<PaymentVoucherTdsDto> tdsDtos = voucher.getTdsList() == null ? new ArrayList<>() :
                    voucher.getTdsList().stream().map(t -> {
                        PaymentVoucherTdsDto tdto = new PaymentVoucherTdsDto();
                        tdto.setTdsSection(t.getTdsSection());
                        tdto.setTdsAmount(t.getTdsAmount());
                        tdto.setRemarks(t.getRemarks());
                        return tdto;
                    }).collect(Collectors.toList());
            dto.setTdsList(tdsDtos);

            List<PaymentVoucherDeductionDto> deductionDtos = voucher.getDeductionsList() == null ? new ArrayList<>() :
                    voucher.getDeductionsList().stream().map(d -> {
                        PaymentVoucherDeductionDto ddto = new PaymentVoucherDeductionDto();
                        ddto.setDeductionName(d.getDeductionName());
                        ddto.setDeductionAmount(d.getDeductionAmount());
                        ddto.setRemarks(d.getRemarks());
                        return ddto;
                    }).collect(Collectors.toList());
            dto.setDeductions(deductionDtos);
            reportList.add(dto);
        }

        return reportList;
    }






}
