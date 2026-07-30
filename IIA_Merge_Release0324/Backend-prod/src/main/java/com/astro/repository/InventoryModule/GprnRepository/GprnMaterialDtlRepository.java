package com.astro.repository.InventoryModule.GprnRepository;

import com.astro.entity.InventoryModule.GprnMaterialDtlEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GprnMaterialDtlRepository extends JpaRepository<GprnMaterialDtlEntity, Integer> {
    List<GprnMaterialDtlEntity> findByPoIdAndMaterialCode(String poId, String materialCode);

    List<GprnMaterialDtlEntity> findBySubProcessId(Integer subProcessId);

    List<GprnMaterialDtlEntity> findBySubProcessIdAndMaterialCode(Integer subProcessId, String materialCode);

    List<GprnMaterialDtlEntity> findByProcessId(String gprnProcessId);

    @Query("SELECT m.materialDesc FROM GprnMaterialDtlEntity m WHERE m.subProcessId = :subProcessId")
    List<String> findMaterialDescriptionsBySubProcessId(@Param("subProcessId") Integer subProcessId);

    boolean existsByPoId(String poId);
}
