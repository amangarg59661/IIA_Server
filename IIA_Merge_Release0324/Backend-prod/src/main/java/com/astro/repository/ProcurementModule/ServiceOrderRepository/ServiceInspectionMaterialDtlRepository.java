package com.astro.repository.InventoryModule;

import com.astro.entity.InventoryModule.ServiceInspectionMaterialDtl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceInspectionMaterialDtlRepository extends JpaRepository<ServiceInspectionMaterialDtl, Long> {

    List<ServiceInspectionMaterialDtl> findByInspectionProcessId(String inspectionProcessId);
}
