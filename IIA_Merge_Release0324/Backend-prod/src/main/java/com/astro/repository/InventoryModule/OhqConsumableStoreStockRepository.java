package com.astro.repository.InventoryModule;

import com.astro.entity.InventoryModule.OhqConsumableStoreStockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface OhqConsumableStoreStockRepository extends JpaRepository<OhqConsumableStoreStockEntity,Long> {
    Optional<OhqConsumableStoreStockEntity> findByMaterialCodeAndLocatorIdAndCustodianId(String materialCode, Integer locationId, String custodianId);

    Optional<OhqConsumableStoreStockEntity> findByMaterialCode(String materialCode);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<OhqConsumableStoreStockEntity> findForUpdateByMaterialCodeAndLocatorIdAndCustodianId(String materialCode, Integer locatorId, String custodianId);

    @Query(value = """
    SELECT
        mm.material_code,
        mm.description,
        mm.uom,
        oss.locator_id,
        oss.custodian_id,
        oss.quantity,
        mm.unit_price
    FROM material_master mm
    LEFT JOIN ohq_consumable_store_stock_entity oss
        ON mm.material_code = oss.material_code
        AND oss.locator_id = :locatorId
    """, nativeQuery = true)
List<Object[]> getSweepMaterialsForLocator(@Param("locatorId") Integer locatorId);


        Optional<OhqConsumableStoreStockEntity> findByMaterialCodeAndLocatorId(String materialCode, Integer locatorId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<OhqConsumableStoreStockEntity> findForUpdateByMaterialCodeAndLocatorId(String materialCode, Integer locatorId);

}
