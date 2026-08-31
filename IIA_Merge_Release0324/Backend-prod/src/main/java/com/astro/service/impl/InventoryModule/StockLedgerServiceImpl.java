package com.astro.service.impl.InventoryModule;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.astro.dto.workflow.InventoryModule.StockLedgerDto;
import com.astro.repository.InventoryModule.OhqMasterConsumableRepository;
import com.astro.service.InventoryModule.StockLedgerService;

@Service
public class StockLedgerServiceImpl implements StockLedgerService {

    @Autowired
    private OhqMasterConsumableRepository omcr;

    @Override
    public List<StockLedgerDto> getStockLedger() {
        LocalDate today = LocalDate.now();
        LocalDate fyStart = today.getMonthValue() >= 4
                ? LocalDate.of(today.getYear(), 4, 1)
                : LocalDate.of(today.getYear() - 1, 4, 1);

        List<Object[]> movementRows = omcr.getStockLedgerMovements(fyStart);
        List<Object[]> currentRows = omcr.getCurrentConsumableStoreStock();

        Map<String, BigDecimal> currentByKey = new HashMap<>();
        for (Object[] row : currentRows) {
            currentByKey.put(row[0] + "|" + row[1], (BigDecimal) row[2]);
        }

        List<StockLedgerDto> result = new ArrayList<>();
        for (Object[] row : movementRows) {
            String materialCode = (String) row[0];
            Integer locatorId = (Integer) row[6];
            BigDecimal unitPrice = row[5] != null ? (BigDecimal) row[5] : BigDecimal.ZERO;
            BigDecimal opening = (BigDecimal) row[8];
            BigDecimal received = (BigDecimal) row[9];
            BigDecimal issued = (BigDecimal) row[10];
            BigDecimal closing = opening.add(received).subtract(issued);
            BigDecimal current = currentByKey.getOrDefault(materialCode + "|" + locatorId, BigDecimal.ZERO);

            StockLedgerDto dto = new StockLedgerDto();
            dto.setStockId(materialCode);
            dto.setItemDescription((String) row[1]);
            dto.setCategory((String) row[2]);
            dto.setSubCategory((String) row[3]);
            dto.setUom((String) row[4]);
            dto.setLocation((String) row[7]);
            dto.setOpeningStock(opening);
            dto.setOpeningValue(opening.multiply(unitPrice));
            dto.setQuantityReceived(received);
            dto.setQuantityIssued(issued);
            dto.setCurrentStock(current);
            dto.setCurrentValue(current.multiply(unitPrice));
            dto.setClosingStock(closing);
            dto.setClosingValue(closing.multiply(unitPrice));
            dto.setLastUpdatedDate(row[11] != null ? ((Timestamp) row[11]).toLocalDateTime() : null);

            result.add(dto);
        }
        return result;
    }
}