package com.astro.repository.InventoryModule.GoodsTransfer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.astro.entity.InventoryModule.GtMasterEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GtMasterRepository extends JpaRepository<GtMasterEntity,Long> {
    List<GtMasterEntity> findByStatus(String status);

    @Query(value = "SELECT gm.id, gm.sender_location_id, gm.receiver_location_id, " +
            "gm.sender_custodian_id, gm.receiver_custodian_id, gm.status, gm.gt_date, gm.create_date, gm.created_by, " +
            "JSON_ARRAYAGG(JSON_OBJECT(" +
            "  'assetId', gd.asset_id, 'assetDesc', gd.asset_desc, " +
            "  'materialCode', gd.material_code, 'materialDesc', gd.material_desc, " +
            "  'quantity', gd.quantity, 'unitPrice', gd.unit_price, " +
            "  'depriciationRate', gd.depriciation_rate, 'bookValue', gd.book_value, " +
            "  'receiverLocatorId', gd.receiver_locator_id, 'senderLocatorId', gd.sender_locator_id)) " +
            "FROM gt_master gm " +
            "JOIN gt_dtl gd ON gm.id = gd.gt_id " +
            "WHERE gm.create_date BETWEEN :startDate AND :endDate " +
            "GROUP BY gm.id", nativeQuery = true)
    List<Object[]> getGtReport(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query(value = "SELECT g.id FROM gt_master g " +
            "WHERE TRIM(UPPER(g.status)) = 'APPROVED' " +
            "AND g.sender_location_id <> g.receiver_location_id " +
            "AND NOT EXISTS (SELECT 1 FROM ogp_gt_master o WHERE o.gt_id = g.id)",
            nativeQuery = true)
    List<Long> findApprovedIdsWithoutOgp();

List<GtMasterEntity> findByStatusInOrderByCreateDateAsc(List<String> statuses);
List<GtMasterEntity> findByUpdatedByAndUpdateDateBetween(String updatedBy, LocalDateTime start, LocalDateTime end);


}

