package com.astro.repository.InventoryModule;

import com.astro.entity.InventoryModule.CycleCountDtlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CycleCountDtlRepository extends JpaRepository<CycleCountDtlEntity, Long> {
    List<CycleCountDtlEntity> findByCycleCountId(Long cycleCountId);
}
