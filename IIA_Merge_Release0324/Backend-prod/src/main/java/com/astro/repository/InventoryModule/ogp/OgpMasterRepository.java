package com.astro.repository.InventoryModule.ogp;

import com.astro.entity.InventoryModule.OgpMasterEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OgpMasterRepository extends JpaRepository<OgpMasterEntity, Integer> {
    boolean existsByIssueNoteId(Integer issueNoteId);

    
    
    Optional<OgpMasterEntity> findById(Integer ogpSubProcessId);
    
    @Query(value = """
            SELECT 
                om.ogp_process_id,
                om.ogp_sub_process_id,
                om.issue_note_id,
                om.ogp_date,
                om.location_id,
                om.created_by,
                om.create_date,
                JSON_ARRAYAGG(
                    JSON_OBJECT(
                        'detailId', od.detail_id,
                        'assetId', od.asset_id,
                        'assetDesc', am.asset_desc,
                        'materialDesc', am.material_desc,
                        'locatorId', od.locator_id,
                        'locatorDesc', lm.locator_desc,
                        'quantity', od.quantity,
                        'uomId', am.uom_id
                    )
                ) as ogp_details
            FROM ogp_master om
            JOIN ogp_detail od ON om.ogp_sub_process_id = od.ogp_sub_process_id
            JOIN asset_master am ON od.asset_id = am.asset_id
            JOIN locator_master lm ON od.locator_id = lm.locator_id
            WHERE om.ogp_date BETWEEN :startDate AND :endDate
            GROUP BY om.ogp_sub_process_id
            ORDER BY om.ogp_date DESC
        """, nativeQuery = true)
        List<Object[]> getOgpReport(LocalDateTime startDate, LocalDateTime endDate);

    @Query(value = """
    SELECT 
        om.ogp_sub_process_id,
        om.ogp_type,
        om.status,
        om.gi_id,
        om.location_id,
        om.created_by,
        om.sender_name,
        om.receiver_name,
        om.receiver_location,
        om.ogp_date,
        om.return_date,
        JSON_ARRAYAGG(
            JSON_OBJECT(
                'detailId', od.detail_id,
                'materialCode', od.material_code,
                'materialDesc', od.material_desc,
                'assetId', od.asset_id,
                'assetDesc', od.asset_desc,
                'rejectionType', od.rejection_type,
                'rejectedQuantity', od.rejected_quantity
            )
        ) as rejected_details
    FROM ogp_master_rejected_gi om
    JOIN ogp_detail_rejected_gi od ON om.ogp_sub_process_id = od.ogp_sub_process_id
    WHERE om.ogp_date BETWEEN :startDate AND :endDate
    GROUP BY om.ogp_sub_process_id
    ORDER BY om.ogp_date DESC
""", nativeQuery = true)
    List<Object[]> getOgpRejectedGiReport(LocalDateTime startDate, LocalDateTime endDate);

        List<OgpMasterEntity> findByStatusOrderByCreateDateAsc(String status);
List<OgpMasterEntity> findByUpdatedByAndUpdateDateBetween(String updatedBy, LocalDateTime start, LocalDateTime end);


// ---- new for the Store Person dashboard's "Recent Gate Passes" ----
// One row per gate-pass line item (not one row per pass) — see GatePassDto's
// note on why. issued-to uses raw location_id since no receiver-name column
// exists on ogp_master in the queries I've seen (only the separate
// ogp_master_rejected_gi table has one).
@Query(value = """
    SELECT om.ogp_sub_process_id, om.create_date, od.quantity, om.location_id
    FROM ogp_master om
    JOIN ogp_detail od ON om.ogp_sub_process_id = od.ogp_sub_process_id
    WHERE om.create_date BETWEEN :start AND :end
    ORDER BY om.create_date DESC
    """, nativeQuery = true)
List<Object[]> findRecentGatePassLines(LocalDateTime start, LocalDateTime end);



}
