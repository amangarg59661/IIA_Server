package com.astro.repository.InventoryModule;

import com.astro.entity.InventoryModule.GoodsInspectionConsumableDetailEntity;
import com.azure.core.http.HttpHeaders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GoodsInspectionConsumableDetailRepository extends JpaRepository<GoodsInspectionConsumableDetailEntity, Integer> {
    
    List<GoodsInspectionConsumableDetailEntity> findByInspectionSubProcessId(Integer inspectionSubProcessId);
    
    List<GoodsInspectionConsumableDetailEntity> findByGprnSubProcessId(Integer gprnSubProcessId);
    
    List<GoodsInspectionConsumableDetailEntity> findByMaterialCode(String materialCode);

    Optional<GoodsInspectionConsumableDetailEntity> findByGprnSubProcessIdAndMaterialCode(Integer subProcessId, String materialCode);

    List<GoodsInspectionConsumableDetailEntity> findByInspectionSubProcessIdAndMaterialCode(Integer inspectionSubProcessId, String materialCode);

    List<GoodsInspectionConsumableDetailEntity> findByRejectionType(String rejectionType);

    @Query("SELECT g.materialDesc FROM GoodsInspectionConsumableDetailEntity g WHERE g.inspectionSubProcessId = :subProcessId")
    List<String> findMaterialDescriptionsByInspectionSubProcessId(@Param("subProcessId") Integer subProcessId);

    void deleteByInspectionSubProcessId(Integer inspectionSubProcessId);
}