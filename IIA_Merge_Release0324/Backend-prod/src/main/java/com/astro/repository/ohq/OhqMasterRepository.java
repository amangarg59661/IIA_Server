package com.astro.repository.ohq;

import com.astro.dto.workflow.AssetSearchResponseDto;
import com.astro.dto.workflow.InventoryModule.AssetOhqDetailsDto;
import com.astro.dto.workflow.InventoryModule.asset.AssetOhqDisposalDto;
import com.astro.entity.InventoryModule.OhqMasterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OhqMasterRepository extends JpaRepository<OhqMasterEntity, Integer> {
    List<OhqMasterEntity> findByAssetId(Integer assetId);
    List<OhqMasterEntity> findByLocatorId(Integer locatorId);
    Optional<OhqMasterEntity> findByAssetIdAndLocatorId(Integer assetId, Integer locatorId);
    Optional<OhqMasterEntity> findByAssetIdAndLocatorIdAndCustodianId(Integer assetId, Integer locatorId, String custodianId);

    @Query(value = """
        SELECT 
            ohq.asset_id,
            am.asset_desc,
            am.material_desc,
            am.uom_id,
            SUM(ohq.quantity) as total_quantity,
            ohq.book_value,
            ohq.depriciation_rate,
            ohq.unit_price,
            COALESCE(JSON_ARRAYAGG(
                JSON_OBJECT(
                    'locatorId', ohq.locator_id,
                    'locatorDesc', lm.locator_desc,
                    'quantity', ohq.quantity,
                    'serialNos', (
                                                SELECT JSON_ARRAYAGG(asl.serial_no)
                                                FROM asset_serial asl
                                                WHERE asl.asset_id = ohq.asset_id
                                                  AND asl.locator_id = ohq.locator_id
                                                  AND asl.custodian_id = ohq.custodian_id
                                                  AND (asl.status IS NULL OR asl.status <> 'Disposed')
                                            )
                )
            ), '[]') as locator_details,
            ohq.custodian_id,
            ohq.asset_code
        FROM ohq_master ohq
        JOIN asset_master am ON ohq.asset_id = am.asset_id
        JOIN locator_master lm ON ohq.locator_id = lm.locator_id
        WHERE ohq.quantity > 0
        GROUP BY ohq.asset_id, am.asset_desc, am.material_desc, am.uom_id, 
                ohq.book_value, ohq.depriciation_rate, ohq.unit_price, ohq.custodian_id,ohq.asset_code
    """, nativeQuery = true)
    List<Object[]> getOhqReport();

        @Query(value = """
        SELECT 
            o.ohq_id AS ohqId,
            o.asset_id AS assetId,
            a.asset_desc AS aseetDescription,
            o.locator_id AS locatorId,
            o.book_value AS bookValue,
            o.depriciation_rate AS depriciationRate,
            o.unit_price AS unitPrice,
            o.quantity AS quantity,
            o.custodian_id AS custodianId,
            po.total_value_of_po AS poValue,
            a.po_id As poId,
            po.delivery_date As gprnDate,
            a.serial_no As serialNo,
            a.model_no As modelNo,
            a.asset_code As assetCode
        FROM ohq_master o
        JOIN asset_master a ON o.asset_id = a.asset_id
        LEFT JOIN purchase_order po ON po.po_id = a.po_id
        WHERE o.quantity > 0
    """, nativeQuery = true)
        List<Object[]> getAllAssetOhqDisposalsNative();

    @Query("""
    SELECT new com.astro.dto.workflow.InventoryModule.AssetOhqDetailsDto(
        o.ohqId, 
        o.assetId, 
        a.assetDesc, 
        a.modelNo, 
        a.serialNo, 
        a.poId, 
        o.unitPrice, 
        o.quantity, 
        o.bookValue, 
        o.depriciationRate,
        o.custodianId,
        o.locatorId,
        (
                                SELECT MAX(g.createDate)
                                FROM GprnMasterEntity g
                                WHERE g.poId = a.poId
                            )
    )
    FROM OhqMasterEntity o
    JOIN AssetMasterEntity a ON o.assetId = a.assetId
""")
    List<AssetOhqDetailsDto> fetchAssetOhqDetails();

    @Query("""
       SELECT new com.astro.dto.workflow.AssetSearchResponseDto(
           o.assetCode,
           o.assetId,
           a.poId,
           o.custodianId,
           o.locatorId,
           o.quantity
       )
       FROM OhqMasterEntity o
       JOIN AssetMasterEntity a ON o.assetId = a.assetId
       WHERE 
           (:keyword IS NULL OR 
           LOWER(o.custodianId) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
           LOWER(a.poId) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
           LOWER(o.assetCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
           CAST(o.assetId AS string) LIKE CONCAT('%', :keyword, '%')
           OR LOWER(a.grnNumber)    LIKE LOWER(CONCAT('%', :keyword, '%')))
       """)
    List<AssetSearchResponseDto> searchAssetsByKeyword(String keyword);

}