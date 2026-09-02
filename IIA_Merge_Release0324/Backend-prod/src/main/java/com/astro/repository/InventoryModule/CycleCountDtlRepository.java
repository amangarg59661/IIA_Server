package com.astro.repository.InventoryModule;

import com.astro.entity.InventoryModule.CycleCountDtlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Repository
public interface CycleCountDtlRepository extends JpaRepository<CycleCountDtlEntity, Long> {
    List<CycleCountDtlEntity> findByCycleCountId(Long cycleCountId);

    @Query(value = """
        SELECT
            m.count_date,
            d.material_desc,
            mm.category,
            mm.sub_category,
            lm.locator_desc,
            d.system_qty_snapshot,
            d.counted_qty,
            d.variance_qty,
            d.variance_value,
            m.counted_by,
            m.status,
            d.remarks
        FROM cycle_count_dtl d
        JOIN cycle_count_master m ON m.id = d.cycle_count_id
        JOIN material_master mm ON mm.material_code = d.material_code
        LEFT JOIN locator_master lm ON lm.locator_id = d.locator_id
        WHERE m.status <> 'DRAFT'
        ORDER BY m.count_date DESC
        """, nativeQuery = true)
    List<Object[]> getCycleCountReport();
}
