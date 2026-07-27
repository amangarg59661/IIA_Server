package com.astro.service.impl;

import com.astro.entity.InventoryModule.AssetMasterEntity;
import com.astro.entity.InventoryModule.OhqMasterEntity;
import com.astro.entity.InventoryModule.OhqMasterConsumableEntity;
import com.astro.entity.MaterialMaster;
import com.astro.entity.ProcurementModule.ContigencyPurchase;
import com.astro.entity.ProcurementModule.CpMaterials;
import com.astro.exception.BusinessException;
import com.astro.exception.ErrorDetails;
import com.astro.repository.InventoryModule.AssetMasterRepository;
import com.astro.repository.InventoryModule.OhqMasterConsumableRepository;
import com.astro.repository.ohq.OhqMasterRepository;
import com.astro.repository.MaterialMasterRepository; // ASSUMPTION: confirm actual package, fix import if wrong
import com.astro.repository.LocatorMasterRepository;   // ASSUMPTION: file not seen, confirm package/method name
import com.astro.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private AssetMasterRepository assetMasterRepository;

    @Autowired
    private OhqMasterRepository ohqMasterRepository;

    @Autowired
    private OhqMasterConsumableRepository ohqMasterConsumableRepository;

    @Autowired
    private MaterialMasterRepository materialMasterRepository;

    @Autowired
    private LocatorMasterRepository locatorMasterRepository;

    private static final String PERSONAL_CUSTODY_PREFIX = "PC";

    @Override
    @Transactional
    public void updateInventoryForCp(ContigencyPurchase cp) {

        String custodianId = cp.getCreatedBy();
        Integer locatorId = locatorMasterRepository.findByLocatorDesc("Personal Custody")
                .map(lm -> lm.getLocatorId())
                .orElseThrow(() -> new BusinessException(new ErrorDetails(
                        400, 4, "Locator Not Found",
                        "Personal Custody locator not found in locator_master. Seed it before CP approvals can update inventory.")));

        if (cp.getCpMaterials() == null) return;

        for (CpMaterials material : cp.getCpMaterials()) {
            if (material.getMaterialCode() == null) continue;

            MaterialMaster materialMaster = materialMasterRepository.findById(material.getMaterialCode())
                    .orElseThrow(() -> new BusinessException(new ErrorDetails(
                            400, 4, "Material Not Found",
                            "Material not found for code: " + material.getMaterialCode())));

            if (Boolean.TRUE.equals(materialMaster.getAssetFlag())) {
                int qty = material.getQuantity() != null ? material.getQuantity().intValue() : 0;
                for (int i = 0; i < qty; i++) {
                    createAssetForCp(cp, material, materialMaster, custodianId, locatorId);
                }
            } else {
                upsertConsumableForCp(cp, material, custodianId, locatorId);
            }
        }
    }

    private void createAssetForCp(ContigencyPurchase cp, CpMaterials material, MaterialMaster materialMaster,
                                   String custodianId, Integer locatorId) {

        AssetMasterEntity asset = new AssetMasterEntity();
        asset.setMaterialCode(material.getMaterialCode());
        asset.setMaterialDesc(material.getMaterialDescription());
        asset.setAssetDesc(material.getMaterialDescription());
        asset.setUomId(material.getUom());
        asset.setUnitPrice(material.getUnitPrice());
        asset.setInitQuantity(BigDecimal.ONE);
        asset.setLocatorId(locatorId);
        asset.setCpId(cp.getContigencyId());
        asset.setCreatedBy(custodianId);

        String subCat = materialMaster.getSubCategory() != null && materialMaster.getSubCategory().length() >= 3
                ? materialMaster.getSubCategory().substring(0, 3)
                : "GEN";
        asset.setAssetCode(generateAssetCode(PERSONAL_CUSTODY_PREFIX, subCat));

        assetMasterRepository.save(asset);

        OhqMasterEntity ohq = new OhqMasterEntity();
        ohq.setAssetId(asset.getAssetId());
        ohq.setAssetCode(asset.getAssetCode());
        ohq.setLocatorId(locatorId);
        ohq.setCustodianId(custodianId);
        ohq.setQuantity(BigDecimal.ONE);
        ohq.setBookValue(material.getUnitPrice() != null ? material.getUnitPrice() : BigDecimal.ZERO);
        ohq.setUnitPrice(material.getUnitPrice() != null ? material.getUnitPrice() : BigDecimal.ZERO);
        ohq.setDepriciationRate(BigDecimal.ZERO);
        ohq.setCpId(cp.getContigencyId());

        ohqMasterRepository.save(ohq);
    }

    private void upsertConsumableForCp(ContigencyPurchase cp, CpMaterials material,
                                        String custodianId, Integer locatorId) {

        BigDecimal addQty = material.getQuantity() != null ? material.getQuantity() : BigDecimal.ZERO;

        Optional<OhqMasterConsumableEntity> existing = ohqMasterConsumableRepository
                .findByMaterialCodeAndLocatorIdAndCustodianId(material.getMaterialCode(), locatorId, custodianId);

        OhqMasterConsumableEntity ohq;
        if (existing.isPresent()) {
            ohq = existing.get();
            BigDecimal currentQty = ohq.getQuantity() != null ? ohq.getQuantity() : BigDecimal.ZERO;
            ohq.setQuantity(currentQty.add(addQty));
        } else {
            ohq = new OhqMasterConsumableEntity();
            ohq.setMaterialCode(material.getMaterialCode());
            ohq.setLocatorId(locatorId);
            ohq.setCustodianId(custodianId);
            ohq.setQuantity(addQty);
            ohq.setBookValue(material.getUnitPrice() != null ? material.getUnitPrice() : BigDecimal.ZERO);
            ohq.setUnitPrice(material.getUnitPrice() != null ? material.getUnitPrice() : BigDecimal.ZERO);
            ohq.setDepriciationRate(BigDecimal.ZERO);
        }
        ohq.setCpId(cp.getContigencyId());

        ohqMasterConsumableRepository.save(ohq);
    }

    private String generateAssetCode(String fieldStation, String subCategory) {
        String financialYear = getFinancialYear();
        String prefix = (fieldStation + subCategory + financialYear + "-").toUpperCase();

        Integer maxAssetId = assetMasterRepository.findMaxAssetId();
        int nextSeq = (maxAssetId != null ? maxAssetId + 1 : 1);

        return prefix + String.format("%03d", nextSeq);
    }

    private String getFinancialYear() {
        LocalDate today = LocalDate.now();
        int startYear = today.getMonthValue() >= 4 ? today.getYear() % 100 : (today.getYear() - 1) % 100;
        int endYear = (startYear + 1) % 100;
        return String.format("%02d%02d", startYear, endYear);
    }
}