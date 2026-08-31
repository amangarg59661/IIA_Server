package com.astro.repository.InventoryModule.grn;

import com.astro.entity.InventoryModule.GiWorkflowStatus;
import com.astro.entity.InventoryModule.GrnMasterEntity;
import com.astro.entity.InventoryModule.GrnWorkflowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;

import java.util.List;

@Repository
public interface GrnWorkflowStatusRepository extends JpaRepository<GrnWorkflowStatus, Long> {


    List<GrnWorkflowStatus> findByProcessIdAndSubProcessIdOrderByIdAsc(String processId, Integer subProcessId);
    // List<GiWorkflowStatus> findByCreatedByAndCreateDateBetween(String createdBy, LocalDateTime start, LocalDateTime end);
List<GrnWorkflowStatus> findByCreatedByAndCreateDateBetween(String createdBy, LocalDateTime start, LocalDateTime end);
}
