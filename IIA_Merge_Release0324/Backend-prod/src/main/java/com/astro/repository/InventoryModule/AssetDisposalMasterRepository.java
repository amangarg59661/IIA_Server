package com.astro.repository.InventoryModule;

import org.springframework.data.jpa.repository.JpaRepository;
import com.astro.entity.InventoryModule.AssetDisposalMasterEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AssetDisposalMasterRepository extends JpaRepository<AssetDisposalMasterEntity, Integer> {
    @Query(value = "SELECT disposal_id, disposal_date, custodian_id, created_by, create_date,updated_by, location_id, status, action, auction_id, auction_date,reserve_price,auction_price, updated_date,vendor_name  FROM asset_disposal WHERE status = 'For Disposal' AND action = 'Awaiting For Approval'",
            nativeQuery = true)
    List<AssetDisposalMasterEntity> findAllAwaitingForApproval();
    @Query(value = "SELECT disposal_id, disposal_date, custodian_id, created_by, create_date, updated_date, updated_by,location_id, status, action, auction_id, auction_date,reserve_price,auction_price, vendor_name  FROM asset_disposal WHERE status = 'For Disposal' AND action = 'Approved'",
            nativeQuery = true)
    List<AssetDisposalMasterEntity> findAllApprovedAssetDisposals();


    @Query(value = """
    SELECT 
        m.disposal_id AS disposalId,
        m.location_id AS locationId,
        m.status AS status,
        m.custodian_id AS custodianId,
        m.create_date AS createDate,
        m.disposal_date AS disposalDate,
        m.created_by AS createdBy,
        m.action AS action,
        m.auction_id AS auctionId,
        m.auction_date AS auctionDate,
        m.reserve_price AS reservePrice,
        m.auction_price AS auctionPrice,
        m.vendor_name AS vendorName,
        JSON_ARRAYAGG(
            JSON_OBJECT(
                'disposalDetailId', d.disposal_detail_id,
                'assetId', d.asset_id,
                'assetDesc', d.asset_desc,
                'disposalQuantity', d.disposal_quantity,
                'disposalCategory', d.disposal_category,
                'disposalMode', d.disposal_mode,
                'salesNoteFilename', d.sales_note_filename,
                'locatorId', d.locator_id,
                'ohqId', d.ohq_id,
                'bookValue', d.book_value,
                'depriciationRate', d.depriciation_rate,
                'unitPrice', d.unit_price,
                'custodianId', d.custodian_id,
                'poValue', d.po_value,
                'reasonForDisposal', d.reason_for_disposal
            )
        ) AS materialsJson
    FROM asset_disposal m
    JOIN asset_disposal_detail d ON m.disposal_id = d.disposal_id
    WHERE m.status = 'Disposed'
      AND m.create_date BETWEEN :from AND :to
    GROUP BY 
        m.disposal_id, m.location_id, m.status, 
        m.custodian_id, m.create_date, m.disposal_date, 
        m.created_by, m.action, m.auction_id, m.auction_date,
        m.reserve_price, m.auction_price, m.vendor_name
    ORDER BY m.create_date DESC
    """, nativeQuery = true)
    List<Object[]> getDisposedAssetDisposalReport(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );


    @Query(value = """
 SELECT
                 a.auction_id AS auctionId,
                 a.auction_code AS auctionCode,
                 a.auction_date AS auctionDate,
                 a.reserve_price AS reservePrice,
                 a.auction_price AS auctionPrice,
                 a.vendor_name AS vendorName,
                 IFNULL(JSON_ARRAYAGG(
                     JSON_OBJECT(
                         'disposalId', d.disposal_id,
                         'disposalDate', d.disposal_date,
                         'locationId', d.location_id,
                         'status', d.status,
                         'custodianId', d.custodian_id,
                         'createdBy', d.created_by,
                         'createDate', d.create_date,
                         'action', d.action,
                         'assets', IFNULL(
                             (SELECT JSON_ARRAYAGG(
                                 JSON_OBJECT(
                                     'disposalDetailId', dd.disposal_detail_id,
                                     'assetId', dd.asset_id,
                                     'assetDesc', dd.asset_desc,
                                     'disposalQuantity', dd.disposal_quantity,
                                     'disposalCategory', dd.disposal_category,
                                     'disposalMode', dd.disposal_mode,
                                     'salesNoteFilename', dd.sales_note_filename,
                                     'locatorId', dd.locator_id,
                                     'ohqId', dd.ohq_id,
                                     'bookValue', dd.book_value,
                                     'depriciationRate', dd.depriciation_rate,
                                     'unitPrice', dd.unit_price,
                                     'custodianId', dd.custodian_id,
                                     'poValue', dd.po_value,
                                     'reasonForDisposal', dd.reason_for_disposal
                                 )
                             ) FROM asset_disposal_detail dd WHERE dd.disposal_id = d.disposal_id), JSON_ARRAY())
                     )
                 ), JSON_ARRAY()) AS disposalsJson
             FROM asset_disposal_auction a
             LEFT JOIN asset_disposal_auction_detail ad ON a.auction_id = ad.auction_id
             LEFT JOIN asset_disposal d ON ad.disposal_id = d.disposal_id
             WHERE a.auction_date BETWEEN :from AND :to
             GROUP BY
                 a.auction_id, a.auction_code, a.auction_date,
                 a.reserve_price, a.auction_price, a.vendor_name
             ORDER BY a.auction_date DESC;
                                       
""", nativeQuery = true)
    List<Object[]> getAuctionReport(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );


    List<AssetDisposalMasterEntity> findByDisposalIdIn(List<Integer> disposalIds);


    @Query(value = "SELECT a.auction_id FROM asset_disposal_auction a " +
            "WHERE a.auction_id NOT IN (SELECT o.auction_id FROM ogp_asset_disposal o)",
            nativeQuery = true)
    List<Integer> findPendingAuctionIdsWithoutOgp();

    List<AssetDisposalMasterEntity> findByStatusOrderByCreateDateAsc(String status);
List<AssetDisposalMasterEntity> findByUpdatedByAndUpdateDateBetween(String updatedBy, LocalDateTime start, LocalDateTime end);

}