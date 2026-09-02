package com.astro.service.InventoryModule;

import java.math.BigDecimal;
import com.astro.entity.InventoryModule.OhqConsumableStoreStockEntity;

public interface StoreStockService {

    /**
     * Applies a signed delta to an EXISTING store-stock row under a pessimistic
     * write lock, so concurrent callers touching the same
     * (materialCode, locatorId, custodianId) row serialize instead of racing.
     * Positive delta adds stock, negative deducts. Throws if the row doesn't
     * exist, or if the resulting quantity would go below zero.
     *
     * Used by DiServiceImpl at issue time.
     */
    // OhqConsumableStoreStockEntity adjustQuantity(String materialCode, Integer locatorId, String custodianId, BigDecimal delta);
    OhqConsumableStoreStockEntity adjustQuantity(String materialCode, Integer locatorId, BigDecimal delta);


    /**
     * Same locking/validation as adjustQuantity, but creates a new store-stock
     * row (priced at unitPriceIfCreating) if one doesn't already exist for this
     * (materialCode, locatorId, custodianId) -- used when a Cycle Count sweep
     * finds physical stock for a material that never had a system record.
     * A negative delta against a non-existent row is rejected rather than
     * silently ignored or turned into a negative-quantity row.
     *
     * Used by CycleCountServiceImpl on approval.
     */
    // OhqConsumableStoreStockEntity adjustOrCreateQuantity(String materialCode, Integer locatorId, String custodianId, BigDecimal delta, BigDecimal unitPriceIfCreating);
    OhqConsumableStoreStockEntity adjustOrCreateQuantity(String materialCode, Integer locatorId,  BigDecimal delta, BigDecimal unitPriceIfCreating);
}
