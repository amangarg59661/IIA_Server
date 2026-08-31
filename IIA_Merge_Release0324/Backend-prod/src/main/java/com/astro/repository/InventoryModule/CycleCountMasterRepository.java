package com.astro.repository.InventoryModule;

import com.astro.entity.InventoryModule.CycleCountMasterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CycleCountMasterRepository extends JpaRepository<CycleCountMasterEntity, Long> {
    List<CycleCountMasterEntity> findByStatus(String status);
}
