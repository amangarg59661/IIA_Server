package com.astro.service;

import com.astro.entity.ProcurementModule.ContigencyPurchase;

public interface InventoryService {

    /**
     * Called only for cpType = MATERIAL, on CP final approval.
     * Asset-flagged materials → one AssetMasterEntity + OhqMasterEntity row per unit.
     * Non-asset materials → accumulated OhqMasterConsumableEntity row.
     * All attributed to custodianId = CP creator, locatorId = fixed "Personal Custody" locator.
     */
    void updateInventoryForCp(ContigencyPurchase cp);
}