package com.astro.repository.InventoryModule;

import com.astro.entity.InventoryModule.OhqMasterConsumableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import com.astro.dto.dashboard.StockSummaryDto;
import java.math.BigDecimal;

import java.util.List;
import java.util.Optional;

@Repository
public interface OhqMasterConsumableRepository extends JpaRepository<OhqMasterConsumableEntity, Integer> {
    
    List<OhqMasterConsumableEntity> findByMaterialCode(String materialCode);
    
    List<OhqMasterConsumableEntity> findByLocatorId(Integer locatorId);
    
    Optional<OhqMasterConsumableEntity> findByMaterialCodeAndLocatorId(String materialCode, Integer locatorId);
    Optional<OhqMasterConsumableEntity> findByMaterialCodeAndLocatorIdAndCustodianId(String materialCode, Integer locatorId, String custodianId);


@Query("SELECT SUM(o.quantity * o.unitPrice) FROM OhqMasterConsumableEntity o")
BigDecimal sumInventoryValue();

@Query("SELECT new com.astro.dto.dashboard.StockSummaryDto$CategoryQuantity(o.materialCode, SUM(o.quantity)) " +
       "FROM OhqMasterConsumableEntity o GROUP BY o.materialCode")
List<StockSummaryDto.CategoryQuantity> sumQuantityGroupByMaterialCode();


    @Query(value = """
        SELECT 
            ohq.material_code,
            am.description,
            am.uom,
            SUM(ohq.quantity) AS total_quantity,
            ohq.book_value,
            ohq.depriciation_rate,
            ohq.unit_price,
            COALESCE(JSON_ARRAYAGG(
                JSON_OBJECT(
                    'locatorId', ohq.locator_id,
                    'locatorDesc', lm.locator_desc,
                    'quantity', ohq.quantity
                )
            ), '[]') AS locator_details,
            ohq.custodian_id
        FROM ohq_master_consumable ohq
        JOIN material_master am ON ohq.material_code = am.material_code
        JOIN locator_master lm ON ohq.locator_id = lm.locator_id
        WHERE ohq.quantity > 0
        GROUP BY ohq.material_code, am.description, am.uom, 
                 ohq.book_value, ohq.depriciation_rate, ohq.unit_price, ohq.custodian_id
    """, nativeQuery = true)
List<Object[]> getOhqConsumableReport();

    Optional<OhqMasterConsumableEntity> findByMaterialCodeAndCustodianId(String materialCode, String custodianId);

    // @Query(value = """
    //     WITH movements AS (
    //         SELECT material_code, locator_id, quantity AS qty, create_date AS txn_date, 'IN' AS direction
    //         FROM ohq_consumable_store_stock

    //         UNION ALL

    //         SELECT d.material_code, d.sender_locator_id AS locator_id, d.quantity AS qty,
    //                m.issue_date AS txn_date, 'OUT' AS direction
    //         FROM demand_and_issue_detail d
    //         JOIN demand_and_issue_master m ON d.di_id = m.id
    //         WHERE m.status = 'Approved'
    //     )
     @Query(value = """
        WITH movements AS (
            SELECT material_code, locator_id, quantity AS qty, create_date AS txn_date, 'IN' AS direction
            FROM ohq_consumable_store_stock_entity

            UNION ALL

            SELECT d.material_code, d.sender_locator_id AS locator_id, d.quantity AS qty,
                   m.issue_date AS txn_date, 'OUT' AS direction
            FROM demand_and_issue_dtl d
            JOIN demand_and_issue_master m ON d.di_id = m.id
            WHERE m.status = 'Approved'
        )
        SELECT
            mv.material_code, mm.description, mm.category, mm.sub_category, mm.uom, mm.unit_price,
            mv.locator_id, lm.locator_desc,
            SUM(CASE WHEN mv.txn_date < :fyStart AND mv.direction = 'IN'  THEN mv.qty
                     WHEN mv.txn_date < :fyStart AND mv.direction = 'OUT' THEN -mv.qty ELSE 0 END) AS opening_stock,
            SUM(CASE WHEN mv.txn_date >= :fyStart AND mv.direction = 'IN'  THEN mv.qty ELSE 0 END) AS quantity_received,
            SUM(CASE WHEN mv.txn_date >= :fyStart AND mv.direction = 'OUT' THEN mv.qty ELSE 0 END) AS quantity_issued,
            MAX(mv.txn_date) AS last_updated_date
        FROM movements mv
        JOIN material_master mm ON mv.material_code = mm.material_code
        LEFT JOIN locator_master lm ON mv.locator_id = lm.locator_id
        GROUP BY mv.material_code, mm.description, mm.category, mm.sub_category, mm.uom, mm.unit_price,
                 mv.locator_id, lm.locator_desc
        """, nativeQuery = true)
    List<Object[]> getStockLedgerMovements(@Param("fyStart") LocalDate fyStart);

    // @Query(value = """
    //     SELECT material_code, locator_id, SUM(quantity) AS current_stock
    //     FROM ohq_consumable_store_stock
    //     GROUP BY material_code, locator_id
    //     """, nativeQuery = true)
    // List<Object[]> getCurrentConsumableStoreStock();
     @Query(value = """
        SELECT material_code, locator_id, SUM(quantity) AS current_stock
        FROM ohq_consumable_store_stock_entity
        GROUP BY material_code, locator_id
        """, nativeQuery = true)
    List<Object[]> getCurrentConsumableStoreStock();

}