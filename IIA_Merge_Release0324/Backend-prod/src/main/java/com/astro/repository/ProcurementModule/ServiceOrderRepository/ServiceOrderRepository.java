package com.astro.repository.ProcurementModule.ServiceOrderRepository;

import com.astro.entity.ProcurementModule.ServiceOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, String> {


    @Query(value = """
            SELECT
              wt.createdDate                              AS approvedDate,
              so.so_id                                    AS soId,
              so.vendor_name                              AS vendorName,
              so.total_value_of_so                        AS value,
              so.tender_id                                AS tenderId,
              so.project_name                             AS project,
              so.vendor_id                                AS vendorId,
              (SELECT GROUP_CONCAT(i.indent_id SEPARATOR ', ')
                 FROM indent_id i WHERE i.tender_id = so.tender_id
              )                                           AS indentIds,
              (SELECT md.mode_of_procurement
                 FROM material_details md
                 WHERE md.indent_id IN (
                   SELECT i2.indent_id FROM indent_id i2 WHERE i2.tender_id = so.tender_id
                 )
                 LIMIT 1
              )                                           AS modeOfProcurement,
              JSON_ARRAYAGG(
                JSON_OBJECT(
                  'materialCode',        attr.material_code,
                  'materialDescription', attr.material_description,
                  'quantity',            attr.quantity,
                  'rate',                attr.rate,
                  'currency',            attr.currency,
                  'exchangeRate',        attr.exchange_rate,
                  'gst',                 attr.gst,
                  'duties',              attr.duties,
                  'budgetCode',          attr.budget_code
                )
              )                                           AS materialsJson
            FROM workflow_transition wt
            JOIN service_order so     ON wt.requestId = so.so_id
            JOIN service_order_material attr
              ON so.so_id = attr.so_id
            WHERE wt.workflowName = 'SO Workflow'
              AND wt.status       = 'Completed'
              AND wt.nextAction   IS NULL
              AND wt.createdDate BETWEEN :from AND :to
            GROUP BY
              wt.createdDate, so.so_id, so.vendor_name,
              so.total_value_of_so, so.tender_id,
              so.project_name, so.vendor_id,
              indentIds, modeOfProcurement
            ORDER BY wt.createdDate, so.so_id
            """,
            nativeQuery = true)
    List<Object[]> getApprovedSoReport(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );
    @Query(value = """
    SELECT
      wt.createdDate                              AS approvedDate,
      so.so_id                                    AS soId,
      so.vendor_name                              AS vendorName,
      so.total_value_of_so                        AS value,
      so.tender_id                                AS tenderId,
      so.project_name                             AS project,
      so.vendor_id                                AS vendorId,
      GROUP_CONCAT(DISTINCT i.indent_id SEPARATOR ', ') AS indentIds,
      (SELECT md.mode_of_procurement
         FROM material_details md
         WHERE md.indent_id IN (
           SELECT i2.indent_id
           FROM indent_id i2
           JOIN indent_creation ic2 ON i2.indent_id = ic2.indent_id
           WHERE i2.tender_id = so.tender_id
             AND ic2.created_by = :userId
         )
         LIMIT 1
      ) AS modeOfProcurement,
      JSON_ARRAYAGG(
        JSON_OBJECT(
          'materialCode',        attr.material_code,
          'materialDescription', attr.material_description,
          'quantity',            attr.quantity,
          'rate',                attr.rate,
          'currency',            attr.currency,
          'exchangeRate',        attr.exchange_rate,
          'gst',                 attr.gst,
          'duties',              attr.duties,
          'budgetCode',          attr.budget_code
        )
      ) AS materialsJson
    FROM workflow_transition wt
    JOIN service_order so ON wt.requestId = so.so_id
    JOIN service_order_material attr ON so.so_id = attr.so_id
    JOIN indent_id i ON i.tender_id = so.tender_id
    JOIN indent_creation ic ON i.indent_id = ic.indent_id AND ic.created_by = :userId
    WHERE wt.workflowName = 'SO Workflow'
      AND wt.status = 'Completed'
      AND wt.nextAction IS NULL
      AND wt.createdDate BETWEEN :from AND :to
    GROUP BY
      wt.createdDate, so.so_id, so.vendor_name,
      so.total_value_of_so, so.tender_id,
      so.project_name, so.vendor_id
    ORDER BY wt.createdDate, so.so_id
    """, nativeQuery = true)
    List<Object[]> getApprovedUserIdsSoReport(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("userId") Integer userId
    );


    @Query(value = """
            SELECT
              so.so_id,
              so.tender_id,
              (SELECT GROUP_CONCAT(i.indent_id SEPARATOR ', ')
                 FROM indent_id i WHERE i.tender_id = so.tender_id) AS indent_ids,
              so.total_value_of_so As value,
              so.vendor_name,
              wt.createdDate AS submitted_date,
              wt.nextRole AS pending_with,
              wt.modificationDate AS pending_from,
              wt.status AS status,
              JSON_ARRAYAGG(
                JSON_OBJECT(
                  'materialCode',        attr.material_code,
                  'materialDescription', attr.material_description,
                  'quantity',            attr.quantity,
                  'rate',                attr.rate,
                  'currency',            attr.currency,
                  'exchangeRate',        attr.exchange_rate,
                  'gst',                 attr.gst,
                  'duties',              attr.duties,
                  'budgetCode',          attr.budget_code
                )
              ) AS materials_json
            FROM workflow_transition wt
            JOIN service_order so ON wt.requestId = so.so_id
            JOIN service_order_material attr ON so.so_id = attr.so_id
            WHERE wt.workflowName = 'SO Workflow'
              AND wt.nextAction = 'Pending'
              AND wt.nextRole IS NOT NULL
              AND wt.createdDate BETWEEN :fromDate AND :toDate
            GROUP BY
              so.so_id, so.tender_id, indent_ids, so.total_value_of_so,
              so.vendor_name, wt.createdDate, wt.nextRole, wt.modificationDate, wt.status
            ORDER BY wt.createdDate, so.so_id
            """, nativeQuery = true)
    List<Object[]> getPendingSoReport(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );
    @Query(value = """
    SELECT
      so.so_id,
      so.tender_id,
      GROUP_CONCAT(DISTINCT i.indent_id SEPARATOR ', ') AS indent_ids,
      so.total_value_of_so As value,
      so.vendor_name,
      wt.createdDate AS submitted_date,
      wt.nextRole AS pending_with,
      wt.modificationDate AS pending_from,
      wt.status AS status,
      JSON_ARRAYAGG(
        JSON_OBJECT(
          'materialCode',        attr.material_code,
          'materialDescription', attr.material_description,
          'quantity',            attr.quantity,
          'rate',                attr.rate,
          'currency',            attr.currency,
          'exchangeRate',        attr.exchange_rate,
          'gst',                 attr.gst,
          'duties',              attr.duties,
          'budgetCode',          attr.budget_code
        )
      ) AS materials_json
    FROM workflow_transition wt
    JOIN service_order so ON wt.requestId = so.so_id
    JOIN service_order_material attr ON so.so_id = attr.so_id
    JOIN indent_id i ON i.tender_id = so.tender_id
    JOIN indent_creation ic ON i.indent_id = ic.indent_id AND ic.created_by = :userId
    WHERE wt.workflowName = 'SO Workflow'
      AND wt.nextAction = 'Pending'
      AND wt.nextRole IS NOT NULL
      AND wt.createdDate BETWEEN :fromDate AND :toDate
    GROUP BY
      so.so_id, so.tender_id, so.total_value_of_so,
      so.vendor_name, wt.createdDate, wt.nextRole, wt.modificationDate, wt.status
    ORDER BY wt.createdDate, so.so_id
    """, nativeQuery = true)
    List<Object[]> getPendingSoUserIdReport(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("userId") Integer userId
    );

    @Query(value = """
    SELECT
      so.so_id AS orderNo,
      DATE(so.created_date) AS orderDate,
      so.total_value_of_so AS value,
      JSON_ARRAYAGG(
        JSON_OBJECT(
          'materialCode', soa.material_code,
          'materialDescription', soa.material_description
        )
      ) AS descriptions,
      so.vendor_name AS vendorName,
      so.consignes_address AS location
    FROM service_order so
    LEFT JOIN service_order_material soa ON so.so_id = soa.so_id
    WHERE so.created_date BETWEEN :startDate AND :endDate
    GROUP BY so.so_id, so.created_date, so.total_value_of_so, so.vendor_name, so.vendor_address
    """, nativeQuery = true)
    List<Object[]> findQuarterlyVigilanceSoReportDto (@Param("startDate") LocalDateTime startDate,
                                                    @Param("endDate") LocalDateTime endDate);

    @Query(value = """
              SELECT
              so.so_id AS soId,
              so.tender_id AS tenderId,
                      (
              SELECT GROUP_CONCAT(i.indent_id SEPARATOR ', ')
              FROM indent_id i
              WHERE i.tender_id = so.tender_id
            ) AS indentIds,
              so.total_value_of_so AS value,
              so.vendor_name AS vendorName,
              wt.createdDate AS submittedDate,
                      (
              SELECT JSON_ARRAYAGG(
                      JSON_OBJECT(
                      'materialCode', attr.material_code,
                         'materialDescription', attr.material_description
                      )
                     )
              FROM service_order_material attr
              WHERE attr.so_id = so.so_id
            ) AS materials,
              wt.remarks AS reason
              FROM workflow_transition wt
              JOIN service_order so
              ON wt.requestId = so.so_id
              WHERE wt.workflowName = 'SO Workflow'
              AND wt.action = 'Rejected'
              AND wt.createdDate BETWEEN :startDate AND :endDate
              ORDER BY wt.createdDate, so.so_id
              """, nativeQuery = true)
    List<Object[]> findShortClosedCancelledSoOrders(@Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);

    @Query("SELECT so FROM ServiceOrder so WHERE so.endDateAmc <= :alertDate")
    List<ServiceOrder> findExpiringServiceOrders(@Param("alertDate") LocalDate alertDate);
// All versions of an SO family
@Query("SELECT s FROM ServiceOrder s WHERE (s.soId = :baseId OR s.soId LIKE CONCAT(:baseId, '/%')) ORDER BY s.soVersion DESC")
List<ServiceOrder> findAllVersionsByBaseId(@Param("baseId") String baseId);

// Active version
@Query("SELECT s FROM ServiceOrder s WHERE (s.soId = :baseId OR s.soId LIKE CONCAT(:baseId, '/%')) AND s.isActive = true")
Optional<ServiceOrder> findActiveVersionByBaseId(@Param("baseId") String baseId);

    //ServiceOrder getSoId(String soId);
}
