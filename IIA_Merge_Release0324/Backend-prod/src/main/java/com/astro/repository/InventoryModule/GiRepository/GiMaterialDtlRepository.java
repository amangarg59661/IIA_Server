package com.astro.repository.InventoryModule.GiRepository;

import com.astro.entity.InventoryModule.GiMaterialDtlEntity;
import com.azure.core.http.HttpHeaders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GiMaterialDtlRepository extends JpaRepository<GiMaterialDtlEntity, Integer> {
    
    List<GiMaterialDtlEntity> findByInspectionSubProcessId(Integer inspectionSubProcessId);
    
    List<GiMaterialDtlEntity> findByMaterialCode(String materialCode);

    Optional<GiMaterialDtlEntity> findByGprnSubProcessIdAndMaterialCode(Integer subProcessId, String materialCode);



    @Query("SELECT g.materialDesc FROM GiMaterialDtlEntity g WHERE g.inspectionSubProcessId = :inspectionSubProcessId")
    List<String> findMaterialDescriptionsByInspectionSubProcessId(@Param("inspectionSubProcessId") Integer inspectionSubProcessId);

    List<GiMaterialDtlEntity> findByRejectionType(String rejectionType);

    void deleteByInspectionSubProcessId(Integer inspectionSubProcessId);
}