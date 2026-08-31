    package com.astro.repository.InventoryModule;

import com.astro.dto.workflow.AssetDataForGtDto;
import com.astro.dto.workflow.InventoryModule.AssetFullResponseDto;
import com.astro.entity.InventoryModule.AssetMasterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetMasterRepository extends JpaRepository<AssetMasterEntity, Integer> {
    
    List<AssetMasterEntity> findByMaterialCode(String materialCode);

    Optional<AssetMasterEntity> findById(Integer id);
    
    Optional<AssetMasterEntity> findBySerialNo(String serialNo);
    
    List<AssetMasterEntity> findByComponentId(Integer componentId);
    
    List<AssetMasterEntity> findByMaterialCodeAndUomId(String materialCode, String uomId);
    
    boolean existsBySerialNo(String serialNo);

    Optional<AssetMasterEntity> findByMaterialCodeAndMaterialDescAndMakeNoAndModelNoAndSerialNoAndUomIdAndPoId(String materialCode, String materialDesc, String makeNo, String modelNo, String serialNo, String uomId, String poId);
    boolean existsByMaterialCodeAndMaterialDescAndMakeNoAndModelNoAndSerialNoAndUomId(
            @Param("materialCode") String materialCode,
            @Param("materialDesc") String materialDesc,
            @Param("makeNo") String makeNo,
            @Param("modelNo") String modelNo,
            @Param("serialNo") String serialNo,
            @Param("uomId") String uomId
    );
   /*
    @Query(value = """
        SELECT 
            asset_id, material_code, material_desc, asset_desc, make_no, 
            serial_no, model_no, init_quantity, unit_price, uom_id,
            depriciation_rate, end_of_life, stock_levels, condition_of_goods,
            shelf_life, component_name, component_id, create_date, created_by,
            updated_date, updated_by
        FROM asset_master
        """, nativeQuery = true)
    List<Object[]> getAssetReport();*/
      @Query(value = """
        SELECT 
            am.asset_id, am.asset_desc, am.make_no, 
            am.serial_no, am.model_no, am.init_quantity, am.unit_price, am.uom_id,
            am.depriciation_rate, am.end_of_life, am.stock_levels, am.condition_of_goods,
            am.shelf_life, am.component_name, am.component_id, am.create_date, am.created_by,
            am.updated_date, am.updated_by, am.po_id,
            po.total_value_of_po,
            po.vendor_id, ohq.custodian_id,
            am.asset_code, mm.category, mm.sub_category, ass.locator_id, ass.status, am.grn_no
        FROM asset_master am
        LEFT JOIN purchase_order po ON am.po_id = po.po_id
        INNER JOIN ohq_master ohq ON am.asset_id = ohq.asset_id
        INNER JOIN material_master mm ON mm.material_code = am.material_code 
        LEFT JOIN asset_serial ass ON ass.asset_id = am.asset_id 
WHERE ohq.custodian_id = :userId
        """, nativeQuery = true)
   List<Object[]> getAssetReportRole(String userId);

      @Query(value = """
        SELECT 
            am.asset_id, am.asset_desc, am.make_no, 
            am.serial_no, am.model_no, am.init_quantity, am.unit_price, am.uom_id,
            am.depriciation_rate, am.end_of_life, am.stock_levels, am.condition_of_goods,
            am.shelf_life, am.component_name, am.component_id, am.create_date, am.created_by,
            am.updated_date, am.updated_by, am.po_id,
            po.total_value_of_po,
            po.vendor_id, ohq.custodian_id,
            am.asset_code, mm.category, mm.sub_category, ass.locator_id, ass.status, am.grn_no
        FROM asset_master am
        LEFT JOIN purchase_order po ON am.po_id = po.po_id
        INNER JOIN ohq_master ohq ON am.asset_id = ohq.asset_id
        INNER JOIN material_master mm ON mm.material_code = am.material_code
        LEFT JOIN asset_serial ass ON ass.asset_id = am.asset_id
        """, nativeQuery = true)
   List<Object[]> getAssetReport();


    @Query("SELECT a.assetId FROM AssetMasterEntity a")
    List<Integer> findAllAssetIds();

    Optional<AssetMasterEntity> findByMaterialCodeAndMaterialDescAndUomIdAndIgpId(String materialCode, String materialDesc, String uomId, Long igpId);

    @Query(value = "SELECT asset_code FROM asset_master " +
            "WHERE asset_code LIKE CONCAT(:prefix, '%') " +
            "ORDER BY asset_code DESC LIMIT 1", nativeQuery = true)
    String findMaxAssetCodeByPrefix(@Param("prefix") String prefix);

//     @Query("""
//     SELECT new com.astro.dto.workflow.InventoryModule.AssetFullResponseDto(
//         a.assetId, a.assetCode, a.materialCode, a.materialDesc, a.assetDesc, a.makeNo,
//         a.serialNo, a.modelNo, a.uomId, a.componentName, a.componentId, a.initQuantity,
//         a.poId, a.unitPrice, a.depriciationRate, a.endOfLife, a.stockLevels,
//         a.conditionOfGoods, a.shelfLife, a.createDate, a.createdBy,
//         a.updatedDate, a.igpId, a.updatedBy,
//         o.custodianId, o.locatorId, o.quantity
//     )
//     FROM AssetMasterEntity a
//     JOIN OhqMasterEntity o ON a.assetId = o.assetId
//     WHERE 
//         (:assetId IS NULL OR a.assetId = :assetId)
//         AND (:assetCode IS NULL OR a.assetCode = :assetCode)
//         AND (:custodianId IS NULL OR o.custodianId = :custodianId)
//         AND (:locatorId IS NULL OR o.locatorId = :locatorId)
// """)
//     List<AssetFullResponseDto> findAssetFullDetails(
//             Integer assetId,
//             String assetCode,
//             String custodianId,
//             Integer locatorId);

@Query("""
    SELECT new com.astro.dto.workflow.InventoryModule.AssetFullResponseDto(
        a.assetId, a.assetCode, a.materialCode, a.materialDesc, a.assetDesc, a.makeNo,
        a.serialNo, a.modelNo, a.uomId, a.componentName, a.componentId, a.initQuantity,
        a.poId, a.unitPrice, a.depriciationRate, a.endOfLife, a.stockLevels,
        a.conditionOfGoods, a.shelfLife, a.createDate, a.createdBy,
        a.updatedDate, a.igpId, a.updatedBy,
        o.custodianId, o.locatorId, o.quantity, u.userName
    )
    FROM AssetMasterEntity a
    JOIN OhqMasterEntity o ON a.assetId = o.assetId
    LEFT JOIN UserMaster u ON CAST(u.userId AS string) = o.custodianId
    WHERE 
        (:assetId IS NULL OR a.assetId = :assetId)
        AND (:assetCode IS NULL OR a.assetCode = :assetCode)
        AND (:custodianId IS NULL OR o.custodianId = :custodianId)
        AND (:locatorId IS NULL OR o.locatorId = :locatorId)
""")
    List<AssetFullResponseDto> findAssetFullDetails(
            Integer assetId,
            String assetCode,
            String custodianId,
            Integer locatorId);


    @Query(value = "SELECT MAX(asset_id) FROM asset_master", nativeQuery = true)
    Integer findMaxAssetId();

    @Query("""
    SELECT new com.astro.dto.workflow.AssetDataForGtDto(
        a.assetId,
        a.assetCode,
        a.materialCode,
        a.materialDesc,
        a.assetDesc,
        a.makeNo,
        a.serialNo,
        a.modelNo,
        a.uomId,
        a.componentName,
        a.componentId,
        a.initQuantity,
        a.poId,
        a.unitPrice,
        o.depriciationRate,
        a.endOfLife,
        a.stockLevels,
        a.conditionOfGoods,
        a.shelfLife,
        a.createDate,
        a.createdBy,
        a.updatedDate,
        a.igpId,
        a.updatedBy,
        o.custodianId,
        o.locatorId,
        o.quantity,
        o.bookValue
    )
    FROM AssetMasterEntity a
    JOIN OhqMasterEntity o ON a.assetId = o.assetId
""")
    List<AssetDataForGtDto> findAllAssetFullDetails();

}