package com.astro.repository.InventoryModule.GiRepository;

import com.astro.entity.InventoryModule.GiMaterialDtlEntity;
import com.astro.entity.InventoryModule.GiWorkflowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiWorkflowStatusRepository extends JpaRepository<GiWorkflowStatus, Long> {


    List<GiWorkflowStatus> findByProcessIdAndSubProcessIdOrderByIdAsc(String processId, Integer subProcessId);

    List<GiWorkflowStatus> findBySubProcessIdAndActionOrderByIdDesc(Integer subProcessId, String action);

}