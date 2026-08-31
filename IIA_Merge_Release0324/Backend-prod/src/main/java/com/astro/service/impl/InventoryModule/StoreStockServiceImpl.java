package com.astro.service.impl.InventoryModule;

import java.math.BigDecimal;
import java.util.Optional;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.astro.constant.AppConstant;
import com.astro.entity.InventoryModule.OhqConsumableStoreStockEntity;
import com.astro.exception.BusinessException;
import com.astro.exception.ErrorDetails;
import com.astro.exception.InvalidInputException;
import com.astro.repository.InventoryModule.OhqConsumableStoreStockRepository;
import com.astro.service.InventoryModule.StoreStockService;

@Service
public class StoreStockServiceImpl implements StoreStockService {

    @Autowired
    private OhqConsumableStoreStockRepository ohqStoreStockRepo;

    @Override
    @Transactional
    public OhqConsumableStoreStockEntity adjustQuantity(String materialCode, Integer locatorId, String custodianId, BigDecimal delta) {

        OhqConsumableStoreStockEntity stock = ohqStoreStockRepo
                .findForUpdateByMaterialCodeAndLocatorIdAndCustodianId(materialCode, locatorId, custodianId)
                .orElseThrow(() -> new InvalidInputException(new ErrorDetails(
                        AppConstant.ERROR_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_RESOURCE,
                        "Store stock not found for material: " + materialCode
                                + ", locator: " + locatorId + ", custodian: " + custodianId)));

        return applyDelta(stock, delta, materialCode);
    }

    @Override
    @Transactional
    public OhqConsumableStoreStockEntity adjustOrCreateQuantity(String materialCode, Integer locatorId, String custodianId,
                                                                  BigDecimal delta, BigDecimal unitPriceIfCreating) {

        Optional<OhqConsumableStoreStockEntity> existing = ohqStoreStockRepo
                .findForUpdateByMaterialCodeAndLocatorIdAndCustodianId(materialCode, locatorId, custodianId);

        if (existing.isPresent()) {
            return applyDelta(existing.get(), delta, materialCode);
        }

        // No existing row. A negative delta here means "count says less than a
        // system value of zero" -- not a valid adjustment, so reject rather than
        // silently drop it or create a negative-quantity row.
        if (delta.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(new ErrorDetails(
                    AppConstant.USER_INVALID_INPUT,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "Cannot apply a negative adjustment to material: " + materialCode
                            + " at locator: " + locatorId + ", custodian: " + custodianId
                            + " -- no existing stock record."));
        }

        OhqConsumableStoreStockEntity stock = new OhqConsumableStoreStockEntity();
        stock.setMaterialCode(materialCode);
        stock.setLocatorId(locatorId);
        stock.setCustodianId(custodianId);
        stock.setQuantity(delta);
        stock.setUnitPrice(unitPriceIfCreating != null ? unitPriceIfCreating : BigDecimal.ZERO);
        stock.setBookValue(BigDecimal.ZERO);
        stock.setDepriciationRate(BigDecimal.ZERO);
        return ohqStoreStockRepo.save(stock);
    }

    private OhqConsumableStoreStockEntity applyDelta(OhqConsumableStoreStockEntity stock, BigDecimal delta, String materialCode) {
        BigDecimal currentQty = stock.getQuantity() != null ? stock.getQuantity() : BigDecimal.ZERO;
        BigDecimal newQty = currentQty.add(delta);

        if (newQty.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(new ErrorDetails(
                    AppConstant.USER_INVALID_INPUT,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "Insufficient stock for material: " + materialCode
                            + ". Available: " + currentQty + ", requested change: " + delta));
        }

        stock.setQuantity(newQty);
        return ohqStoreStockRepo.save(stock);
    }
}
