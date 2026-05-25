package com.astro.service.impl;

import com.astro.dto.workflow.PaymentVoucherMaterialDto;
import com.astro.dto.workflow.PaymentVoucherReportDto;
import com.astro.dto.workflow.paymentVoucherMaterialRequestDto;
import com.astro.dto.workflow.paymentVoucherRequestDto;
import com.astro.entity.PaymentVoucher;
import com.astro.entity.PaymentVoucherMaterials;
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
import java.util.List;
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
        voucher.setTdsAmount(dto.getTdsAmount());
        voucher.setPaymentVoucherNetAmount(dto.getPaymentVoucherNetAmount());

        Optional<PaymentVoucher> existingVoucherOpt = paymentVoucherReposiotry.findTopByGrnNumberOrderByIdDesc(dto.getGrnNumber());

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

     PaymentVoucher pv=   paymentVoucherReposiotry.save(voucher);

        return dto.getGrnNumber() + "/" + pv.getId();
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
        dto.setTdsAmount(entity.getTdsAmount());

        // Map materials
        if (entity.getMaterialsList() != null) {
            dto.setMaterials(entity.getMaterialsList().stream().map(this::mapMaterial).collect(Collectors.toList()));
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


            String id = voucher.getGrnNumber()+"/"+ voucher.getId();
            dto.setPaymentVoucherNumber(id);
            dto.setPaymentVoucherDate(voucher.getPaymentVoucherDate());
            dto.setPaymentVoucherIsFor(voucher.getPaymentVoucherIsFor());
            dto.setGrnNumber(voucher.getGrnNumber());
            if(voucher.getPaymentVoucherIsFor().equalsIgnoreCase("Purchase Order")){
                String poId = "PO"+voucher.getPurchaseOrderId();
                dto.setPurchaseOrderId(poId);
            }else{
                String soId = "SO"+voucher.getSoId();
                dto.setSoId(voucher.getSoId());
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
            reportList.add(dto);
        }

        return reportList;
    }






}
