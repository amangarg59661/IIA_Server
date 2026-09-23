package com.astro.repository.InventoryModule.grn;

import com.astro.entity.InventoryModule.GrnMaterialDtlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDate;

import java.util.List;

@Repository
public interface GrnMaterialDtlRepository extends JpaRepository<GrnMaterialDtlEntity, Integer> {
    List<GrnMaterialDtlEntity> findByGrnSubProcessId(Integer grnSubProcessId);
    List<GrnMaterialDtlEntity> findByAssetId(Integer assetId);

    List<GrnMaterialDtlEntity> findByGiSubProcessIdAndAssetId(Integer giSubProcessId, Integer assetId);

    List<GrnMaterialDtlEntity> findByGrnProcessId(String grnProcessId);

    @Query("SELECT COALESCE(SUM(gmd.quantity), 0) " +
       "FROM GprnMasterEntity gp, GiMasterEntity gm, GrnMasterEntity gr, GrnMaterialDtlEntity gmd " +
       "WHERE gm.gprnSubProcessId = gp.subProcessId " +
       "AND gr.giSubProcessId = gm.inspectionSubProcessId " +
       "AND gmd.grnSubProcessId = gr.grnSubProcessId " +
       "AND gp.indentId IN :indentIds " +
       "AND gr.grnDate BETWEEN :start AND :end")
BigDecimal sumReceivedQuantityForIndents(@Param("indentIds") List<String> indentIds,
                                          @Param("start") LocalDate start,
                                          @Param("end") LocalDate end);
}