package com.astro.service.InventoryModule;

import java.util.List;
import com.astro.dto.workflow.InventoryModule.StockLedgerDto;

public interface StockLedgerService {
    List<StockLedgerDto> getStockLedger();
}