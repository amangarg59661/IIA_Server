package com.astro.service.impl.InventoryModule;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.astro.constant.AppConstant;
import com.astro.dto.workflow.InventoryModule.cyclecount.CountedLineDto;
import com.astro.dto.workflow.InventoryModule.cyclecount.CycleCountDto;
import com.astro.dto.workflow.InventoryModule.cyclecount.CycleCountLineDto;
import com.astro.dto.workflow.InventoryModule.cyclecount.InitiateCycleCountDto;
import com.astro.dto.workflow.InventoryModule.cyclecount.ManualCycleCountItemDto;
import com.astro.dto.workflow.InventoryModule.cyclecount.PendingCycleCountDto;
import com.astro.dto.workflow.InventoryModule.cyclecount.SubmitCycleCountDto;
import com.astro.dto.workflow.InventoryModule.cyclecount.CycleCountReportDto;
import com.astro.entity.InventoryModule.CycleCountDtlEntity;
import com.astro.entity.InventoryModule.CycleCountMasterEntity;
import com.astro.entity.InventoryModule.OhqConsumableStoreStockEntity;
import com.astro.exception.BusinessException;
import com.astro.exception.ErrorDetails;
import com.astro.exception.InvalidInputException;
import com.astro.repository.InventoryModule.CycleCountDtlRepository;
import com.astro.repository.InventoryModule.CycleCountMasterRepository;
import com.astro.repository.InventoryModule.OhqConsumableStoreStockRepository;
import com.astro.service.InventoryModule.CycleCountService;
import com.astro.service.InventoryModule.StoreStockService;
import com.astro.repository.MaterialMasterRepository;
import com.astro.entity.MaterialMaster;

@Service
public class CycleCountServiceImpl implements CycleCountService {

    @Autowired
    private CycleCountMasterRepository cycleCountMasterRepository;

    @Autowired
    private CycleCountDtlRepository cycleCountDtlRepository;

    @Autowired
    private OhqConsumableStoreStockRepository ohqStoreStockRepo;

    @Autowired
private MaterialMasterRepository mmr;

    @Autowired
    private StoreStockService storeStockService;

    @Override
    @Transactional
    public String initiateCycleCount(InitiateCycleCountDto req) {

        CycleCountMasterEntity master = new CycleCountMasterEntity();
        master.setCountType(req.getCountType());
        master.setLocatorId(req.getLocatorId());
        master.setStatus("DRAFT");

        List<CycleCountDtlEntity> lines = new ArrayList<>();

                if ("SWEEP".equals(req.getCountType())) {
            List<Object[]> rows = ohqStoreStockRepo.getSweepMaterialsForLocator(req.getLocatorId());

            // A material can appear more than once only if duplicate/stray rows
            // exist for the same material+locator (store stock has no custodian
            // to split across anymore). Flagged below so the approver knows
            // ahead of time this line will fail at approval -- adjustOrCreateQuantity
            // throws on a genuine multi-row match -- unless reconciled first.
            Map<String, List<Object[]>> byMaterial = rows.stream()
                    .collect(Collectors.groupingBy(r -> (String) r[0]));

            for (Map.Entry<String, List<Object[]>> entry : byMaterial.entrySet()) {
                List<Object[]> group = entry.getValue();
                Object[] first = group.get(0);

                 BigDecimal systemQty = BigDecimal.ZERO;
                // Unit price now comes from Material Master (column index 6, see
                // query above), not the store-stock row -- identical for every row
                // in this group since material_code is Material Master's own
                // primary key. systemQty still sums across duplicate rows as before.
                BigDecimal unitPrice = first[6] != null ? (BigDecimal) first[6] : BigDecimal.ZERO;
                long matchingRowCount = 0;

                for (Object[] row : group) {
                    BigDecimal qty = row[5] != null ? (BigDecimal) row[5] : null;
                    if (qty != null) {
                        systemQty = systemQty.add(qty);
                        matchingRowCount++;
                    }
                }
                // BigDecimal systemQty = BigDecimal.ZERO;
                // BigDecimal unitPrice = BigDecimal.ZERO;
                // long matchingRowCount = 0;

                // for (Object[] row : group) {
                //     BigDecimal qty = row[5] != null ? (BigDecimal) row[5] : null;
                //     if (qty != null) {
                //         systemQty = systemQty.add(qty);
                //         matchingRowCount++;
                //         if (row[6] != null) {
                //             unitPrice = (BigDecimal) row[6]; // last non-null wins across duplicate rows
                //         }
                //     }
                // }

                CycleCountDtlEntity dtl = new CycleCountDtlEntity();
                dtl.setMaterialCode(entry.getKey());
                dtl.setMaterialDesc((String) first[1]);
                dtl.setUom((String) first[2]);
                dtl.setLocatorId(req.getLocatorId());
                dtl.setSystemQtySnapshot(systemQty);
                dtl.setUnitPriceSnapshot(unitPrice);
                dtl.setMultipleCustodianRows(matchingRowCount > 1);
                lines.add(dtl);
            }

        } else if ("MANUAL".equals(req.getCountType())) {

        // if ("SWEEP".equals(req.getCountType())) {
        //     if (req.getSweepCustodianId() == null) {
        //         throw new InvalidInputException(new ErrorDetails(
        //                 AppConstant.USER_INVALID_INPUT, AppConstant.ERROR_TYPE_CODE_VALIDATION,
        //                 AppConstant.ERROR_TYPE_VALIDATION,
        //                 "sweepCustodianId is required for a SWEEP cycle count."));
        //     }
        //     master.setSweepCustodianId(req.getSweepCustodianId());

        //     List<Object[]> rows = ohqStoreStockRepo.getSweepMaterialsForLocator(req.getLocatorId());

        //     // A material can appear more than once if stock is currently split
        //     // across multiple custodians at this locator.
        //     Map<String, List<Object[]>> byMaterial = rows.stream()
        //             .collect(Collectors.groupingBy(r -> (String) r[0]));

        //     for (Map.Entry<String, List<Object[]>> entry : byMaterial.entrySet()) {
        //         List<Object[]> group = entry.getValue();
        //         Object[] first = group.get(0);

        //         BigDecimal systemQty = BigDecimal.ZERO;
        //         BigDecimal unitPrice = BigDecimal.ZERO;
        //         long custodianRowCount = 0;

        //         for (Object[] row : group) {
        //             BigDecimal qty = row[5] != null ? (BigDecimal) row[5] : null;
        //             if (qty != null) {
        //                 systemQty = systemQty.add(qty);
        //                 custodianRowCount++;
        //                 if (row[6] != null) {
        //                     unitPrice = (BigDecimal) row[6]; // last non-null wins across multiple custodian rows
        //                 }
        //             }
        //         }

        //         CycleCountDtlEntity dtl = new CycleCountDtlEntity();
        //         dtl.setMaterialCode(entry.getKey());
        //         dtl.setMaterialDesc((String) first[1]);
        //         dtl.setUom((String) first[2]);
        //         dtl.setLocatorId(req.getLocatorId());
        //         dtl.setCustodianId(req.getSweepCustodianId()); // write-back always targets the declared storekeeper
        //         dtl.setSystemQtySnapshot(systemQty);
        //         dtl.setUnitPriceSnapshot(unitPrice);
        //         dtl.setMultipleCustodianRows(custodianRowCount > 1);
        //         lines.add(dtl);
        //     }

        // } else if ("MANUAL".equals(req.getCountType())) {
            if (req.getManualItems() == null || req.getManualItems().isEmpty()) {
                throw new InvalidInputException(new ErrorDetails(
                        AppConstant.USER_INVALID_INPUT, AppConstant.ERROR_TYPE_CODE_VALIDATION,
                        AppConstant.ERROR_TYPE_VALIDATION,
                        "At least one item is required for a MANUAL cycle count."));
            }

                                    for (ManualCycleCountItemDto item : req.getManualItems()) {
                MaterialMaster mm = mmr.findById(item.getMaterialCode()).orElse(null);
                if (mm == null) {
                    throw new InvalidInputException(new ErrorDetails(
                            AppConstant.ERROR_CODE_RESOURCE, AppConstant.ERROR_TYPE_CODE_RESOURCE,
                            AppConstant.ERROR_TYPE_VALIDATION,
                            "Material code not found in Material Master: " + item.getMaterialCode()));
                }

                 if (Boolean.TRUE.equals(mm.getAssetFlag())) {
                    throw new InvalidInputException(new ErrorDetails(
                            AppConstant.USER_INVALID_INPUT, AppConstant.ERROR_TYPE_CODE_VALIDATION,
                            AppConstant.ERROR_TYPE_VALIDATION,
                            "Material " + item.getMaterialCode() + " is flagged as an asset and cannot be included in a cycle count."));
                }

                // No .orElseThrow() here on purpose -- a material can be physically
                // present at a location without ever having a system stock record
                // (that's exactly the discrepancy a cycle count exists to catch).
                // Missing row = system says zero; approveCycleCount's
                // adjustOrCreateQuantity already handles creating the row once
                // the count is approved.
                OhqConsumableStoreStockEntity stock = ohqStoreStockRepo
                        .findByMaterialCodeAndLocatorId(item.getMaterialCode(), item.getLocatorId())
                        .orElse(null);

                CycleCountDtlEntity dtl = new CycleCountDtlEntity();
                dtl.setMaterialCode(item.getMaterialCode());
                dtl.setMaterialDesc(item.getMaterialDesc());
                dtl.setUom(mm.getUom());
                dtl.setLocatorId(item.getLocatorId());
                // dtl.setSystemQtySnapshot(stock != null && stock.getQuantity() != null ? stock.getQuantity() : BigDecimal.ZERO);
                // dtl.setUnitPriceSnapshot(stock != null && stock.getUnitPrice() != null ? stock.getUnitPrice() : BigDecimal.ZERO);
                   dtl.setSystemQtySnapshot(stock != null && stock.getQuantity() != null ? stock.getQuantity() : BigDecimal.ZERO);
                // Always Material Master's price now, not the store-stock row's --
                // consistent with SWEEP, and doesn't depend on whether a stock row
                // happens to exist yet.
                dtl.setUnitPriceSnapshot(mm.getUnitPrice() != null ? mm.getUnitPrice() : BigDecimal.ZERO);
                dtl.setMultipleCustodianRows(false);
                lines.add(dtl);
            }
            // for (ManualCycleCountItemDto item : req.getManualItems()) {
            //     OhqConsumableStoreStockEntity stock = ohqStoreStockRepo
            //             .findByMaterialCodeAndLocatorIdAndCustodianId(
            //                     item.getMaterialCode(), item.getLocatorId(), item.getCustodianId())
            //             .orElseThrow(() -> new InvalidInputException(new ErrorDetails(
            //                     AppConstant.ERROR_CODE_RESOURCE, AppConstant.ERROR_TYPE_CODE_RESOURCE,
            //                     AppConstant.ERROR_TYPE_VALIDATION,
            //                     "No stock record found for material: " + item.getMaterialCode()
            //                             + ", locator: " + item.getLocatorId()
            //                             + ", custodian: " + item.getCustodianId())));

            //     CycleCountDtlEntity dtl = new CycleCountDtlEntity();
            //     dtl.setMaterialCode(stock.getMaterialCode());
            //     dtl.setMaterialDesc(item.getMaterialDesc());
            //     dtl.setUom(stock.getUom());
            //     dtl.setLocatorId(stock.getLocatorId());
            //     dtl.setCustodianId(stock.getCustodianId());
            //     dtl.setSystemQtySnapshot(stock.getQuantity() != null ? stock.getQuantity() : BigDecimal.ZERO);
            //     dtl.setUnitPriceSnapshot(stock.getUnitPrice() != null ? stock.getUnitPrice() : BigDecimal.ZERO);
            //     dtl.setMultipleCustodianRows(false);
            //     lines.add(dtl);
            // }
        } else {
            throw new InvalidInputException(new ErrorDetails(
                    AppConstant.USER_INVALID_INPUT, AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION, "countType must be MANUAL or SWEEP."));
        }

        master = cycleCountMasterRepository.save(master);
        for (CycleCountDtlEntity dtl : lines) {
            dtl.setCycleCountId(master.getId());
            cycleCountDtlRepository.save(dtl);
        }

        return "CC/" + master.getId();
    }


    @Override
public List<CycleCountReportDto> getCycleCountReport() {
    return cycleCountDtlRepository.getCycleCountReport().stream().map(row -> {
        CycleCountReportDto dto = new CycleCountReportDto();
        dto.setVerificationDate(row[0] != null ? ((java.sql.Date) row[0]).toLocalDate() : null);
        dto.setItemDescription((String) row[1]);
        dto.setCategory((String) row[2]);
        dto.setSubCategory((String) row[3]);
        dto.setLocation((String) row[4]);
        dto.setQuantityAsPerRecords((BigDecimal) row[5]);
        dto.setQuantityFound((BigDecimal) row[6]);
        dto.setDiscrepancyQty((BigDecimal) row[7]);
        dto.setDiscrepancyValue((BigDecimal) row[8]);
        dto.setVerifiedBy(row[9] != null ? (Integer) row[9] : null);
        String status = (String) row[10];
        dto.setVerified("APPROVED".equals(status));
        dto.setRemarks((String) row[11]);
        return dto;
    }).collect(Collectors.toList());
}

    @Override
    @Transactional
    public void submitCycleCount(SubmitCycleCountDto req) {
        Long id = extractId(req.getCycleCountId());
        CycleCountMasterEntity master = cycleCountMasterRepository.findById(id)
                .orElseThrow(() -> notFound(req.getCycleCountId()));

        if (!"DRAFT".equals(master.getStatus())) {
            throw new BusinessException(new ErrorDetails(
                    AppConstant.USER_INVALID_INPUT, AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "Cycle count " + req.getCycleCountId() + " is not in DRAFT status."));
        }

        Map<Long, CountedLineDto> countedByDtlId = req.getLines().stream()
                .collect(Collectors.toMap(CountedLineDto::getDtlId, l -> l));

        BigDecimal totalVarianceValue = BigDecimal.ZERO;

        for (CycleCountDtlEntity dtl : cycleCountDtlRepository.findByCycleCountId(id)) {
            CountedLineDto counted = countedByDtlId.get(dtl.getId());
            if (counted == null) {
                throw new InvalidInputException(new ErrorDetails(
                        AppConstant.USER_INVALID_INPUT, AppConstant.ERROR_TYPE_CODE_VALIDATION,
                        AppConstant.ERROR_TYPE_VALIDATION,
                        "Missing counted quantity for line: " + dtl.getId() + " (" + dtl.getMaterialCode() + ")"));
            }

            BigDecimal systemQty = dtl.getSystemQtySnapshot() != null ? dtl.getSystemQtySnapshot() : BigDecimal.ZERO;
            BigDecimal unitPrice = dtl.getUnitPriceSnapshot() != null ? dtl.getUnitPriceSnapshot() : BigDecimal.ZERO;
            BigDecimal varianceQty = counted.getCountedQty().subtract(systemQty);
            BigDecimal varianceValue = varianceQty.multiply(unitPrice);

            dtl.setCountedQty(counted.getCountedQty());
            dtl.setVarianceQty(varianceQty);
            dtl.setVarianceValue(varianceValue);
            dtl.setRemarks(counted.getRemarks());
            cycleCountDtlRepository.save(dtl);

            totalVarianceValue = totalVarianceValue.add(varianceValue);
        }

        master.setCountedBy(req.getCountedBy() != null ? Integer.valueOf(req.getCountedBy()) : null);
        master.setCountDate(LocalDate.now());
        master.setTotalVarianceValue(totalVarianceValue);
        master.setStatus("AWAITING APPROVAL");
        cycleCountMasterRepository.save(master);

        // Workflow initiation happens in ProcessController, right after this call --
        // matches how Payment Voucher's controller calls workflowService.initiateWorkflow
        // immediately after paymentVoucherService.createPaymentVoucher(dto).
    }

    @Override
    @Transactional
    public void approveCycleCount(String cycleCountId) {
        Long id = extractId(cycleCountId);
        CycleCountMasterEntity master = cycleCountMasterRepository.findById(id)
                .orElseThrow(() -> notFound(cycleCountId));

        if (!"AWAITING APPROVAL".equals(master.getStatus())) {
            throw new BusinessException(new ErrorDetails(
                    AppConstant.USER_INVALID_INPUT, AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION, "Cycle count " + cycleCountId + " is not awaiting approval."));
        }

        for (CycleCountDtlEntity dtl : cycleCountDtlRepository.findByCycleCountId(id)) {
            if (dtl.getVarianceQty() == null || dtl.getVarianceQty().compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }

            storeStockService.adjustOrCreateQuantity(
                    dtl.getMaterialCode(), dtl.getLocatorId(),
                    // dtl.getCustodianId(),
                    dtl.getVarianceQty(), dtl.getUnitPriceSnapshot());
        }

        master.setStatus("APPROVED");
        cycleCountMasterRepository.save(master);
    }

    @Override
    @Transactional
    public void rejectCycleCount(String cycleCountId) {
        Long id = extractId(cycleCountId);
        CycleCountMasterEntity master = cycleCountMasterRepository.findById(id)
                .orElseThrow(() -> notFound(cycleCountId));
        master.setStatus("REJECTED");
        cycleCountMasterRepository.save(master);
    }

    @Override
    public CycleCountDto getCycleCountDtls(String cycleCountId) {
        Long id = extractId(cycleCountId);
        CycleCountMasterEntity master = cycleCountMasterRepository.findById(id)
                .orElseThrow(() -> notFound(cycleCountId));

        CycleCountDto dto = new CycleCountDto();
        dto.setCycleCountId(cycleCountId);
        dto.setCountType(master.getCountType());
        dto.setLocatorId(master.getLocatorId());
        // dto.setSweepCustodianId(master.getSweepCustodianId());
        dto.setStatus(master.getStatus());
        dto.setCountedBy(master.getCountedBy());
        dto.setCountDate(master.getCountDate());
        dto.setTotalVarianceValue(master.getTotalVarianceValue());
        dto.setLines(cycleCountDtlRepository.findByCycleCountId(id).stream()
                .map(this::toLineDto).collect(Collectors.toList()));
        return dto;
    }

    @Override
    public List<PendingCycleCountDto> getPendingCycleCounts() {
        return cycleCountMasterRepository.findByStatus("AWAITING APPROVAL").stream()
                .map(this::toPendingDto).collect(Collectors.toList());
    }

    @Override
    public List<PendingCycleCountDto> searchCycleCounts(String value) {
        return cycleCountMasterRepository.searchByIdLocatorOrStatus(value).stream()
                .map(this::toPendingDto).collect(Collectors.toList());
    }

    private PendingCycleCountDto toPendingDto(CycleCountMasterEntity m) {
        PendingCycleCountDto dto = new PendingCycleCountDto();
        dto.setCycleCountId("CC/" + m.getId());
        dto.setCountType(m.getCountType());
        dto.setLocatorId(m.getLocatorId());
        dto.setStatus(m.getStatus());
        dto.setCountDate(m.getCountDate());
        dto.setTotalVarianceValue(m.getTotalVarianceValue());
        return dto;
    }

    // @Override
    // public List<PendingCycleCountDto> getPendingCycleCounts() {
    //     return cycleCountMasterRepository.findByStatus("AWAITING APPROVAL").stream().map(m -> {
    //         PendingCycleCountDto dto = new PendingCycleCountDto();
    //         dto.setCycleCountId("CC/" + m.getId());
    //         dto.setCountType(m.getCountType());
    //         dto.setLocatorId(m.getLocatorId());
    //         dto.setStatus(m.getStatus());
    //         dto.setCountDate(m.getCountDate());
    //         dto.setTotalVarianceValue(m.getTotalVarianceValue());
    //         return dto;
    //     }).collect(Collectors.toList());
    // }

    private CycleCountLineDto toLineDto(CycleCountDtlEntity dtl) {
        CycleCountLineDto line = new CycleCountLineDto();
        line.setDtlId(dtl.getId());
        line.setMaterialCode(dtl.getMaterialCode());
        line.setMaterialDesc(dtl.getMaterialDesc());
        line.setUom(dtl.getUom());
        line.setLocatorId(dtl.getLocatorId());
        // line.setCustodianId(dtl.getCustodianId());
        line.setSystemQtySnapshot(dtl.getSystemQtySnapshot());
        line.setUnitPriceSnapshot(dtl.getUnitPriceSnapshot());
        line.setCountedQty(dtl.getCountedQty());
        line.setVarianceQty(dtl.getVarianceQty());
        line.setVarianceValue(dtl.getVarianceValue());
        line.setMultipleCustodianRows(dtl.getMultipleCustodianRows());
        line.setRemarks(dtl.getRemarks());
        return line;
    }

    private BusinessException notFound(String cycleCountId) {
        return new BusinessException(new ErrorDetails(
                AppConstant.ERROR_CODE_RESOURCE, AppConstant.ERROR_TYPE_CODE_RESOURCE,
                AppConstant.ERROR_TYPE_VALIDATION, "Cycle count not found: " + cycleCountId));
    }

    private Long extractId(String cycleCountId) {
        return Long.valueOf(cycleCountId.split("/")[1]);
    }
}
