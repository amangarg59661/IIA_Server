package com.astro.repository.InventoryModule;

import com.astro.entity.InventoryModule.ServiceInspectionMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceInspectionRepository extends JpaRepository<ServiceInspectionMaster, String> {

    List<ServiceInspectionMaster> findBySoId(String soId);

    // Used to work out the next subProcessId — highest existing cycle for this SO first
    List<ServiceInspectionMaster> findBySoIdOrderBySubProcessIdDesc(String soId);

    List<ServiceInspectionMaster> findByCurrentStatus(String currentStatus);
}
