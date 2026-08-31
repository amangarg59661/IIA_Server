package com.astro.repository.InventoryModule;

import com.astro.entity.InventoryModule.DemandAndIssueMasterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DemandAndIssueMasterEntityRepository extends JpaRepository<DemandAndIssueMasterEntity, Long> {
    List<DemandAndIssueMasterEntity> findByStatus(String status);

    @Query(value = """
    SELECT 
        m.id AS diId,
        m.sender_location_id AS senderLocationId,
        m.status AS status,
        m.sender_custodian_id AS senderCustodianId,
        m.create_date AS createDate,
        m.di_date AS demandIssueDate,
        m.created_by AS createdBy,
        m.issue_date AS issueDate,
        m.issued_by AS issuedBy,
        JSON_ARRAYAGG(
            JSON_OBJECT(
                'id', d.id,
                'assetId', d.asset_id,
                'assetDesc', d.asset_desc,
                'materialCode', d.material_code,
                'materialDesc', d.material_desc,
                'quantity', d.quantity,
                'receiverLocatorId', d.receiver_locator_id,
                'senderLocatorId', d.sender_locator_id,
                'unitPrice', d.unit_price,
                'depriciationRate', d.depriciation_rate,
                'bookValue', d.book_value,
                'uom', d.uom
            )
        ) AS materialsJson
    FROM demand_and_issue_master m
    JOIN demand_and_issue_dtl d ON m.id = d.di_id
    WHERE m.status = 'Approved'
      AND m.create_date BETWEEN :from AND :to
    GROUP BY 
        m.id, m.sender_location_id, m.status, 
        m.sender_custodian_id, m.create_date, 
        m.di_date, m.created_by, m.issue_date, m.issued_by
    ORDER BY m.create_date DESC
    """, nativeQuery = true)
    List<Object[]> getApprovedDemandAndIssueReport(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    List<DemandAndIssueMasterEntity> findByStatusOrderByCreateDateAsc(String status);
List<DemandAndIssueMasterEntity> findByUpdatedByAndUpdateDateBetween(String updatedBy, LocalDateTime start, LocalDateTime end);


}
