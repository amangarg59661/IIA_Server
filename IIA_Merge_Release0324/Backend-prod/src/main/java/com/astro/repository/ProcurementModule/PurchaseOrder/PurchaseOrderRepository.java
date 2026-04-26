package com.astro.repository.ProcurementModule.PurchaseOrder;

import com.astro.dto.workflow.PaymentVoucherPoSearchDto;
import com.astro.dto.workflow.ProcurementDtos.IndentDto.materialHistoryDto;
import com.astro.dto.workflow.ProcurementDtos.ProcurementActivityReportResponse;
import com.astro.dto.workflow.ProcurementDtos.performanceWarrsntySecurityReportDto;
import com.astro.dto.workflow.ProcurementDtos.purchaseOrder.SearchPOIdDto;
import com.astro.entity.ProcurementModule.PurchaseOrder;
import com.azure.core.http.rest.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, String> {

    @Query("SELECT COALESCE(SUM(po.totalValueOfPo), 0) FROM PurchaseOrder po WHERE po.projectName = :projectName")
    java.math.BigDecimal getTotalPoValueByProjectName(@Param("projectName") String projectName);

    @Query(value = """
                  select
                  po.po_id AS orderId,
                            tr.mode_of_procurement AS modeOfProcurement,
                            '' AS underAMC,
                            '' AS amcFor,
                            '' AS endUser,
                             CAST(NULL AS UNSIGNED) AS noOfParticipants,
                            po.total_value_of_po AS value,
                            po.consignes_address AS location,
                            po.vendor_name AS vendorName,
                            '' AS previouslyRenewedAMCs,
                            '' AS categoryOfSecurity,
                            '' AS validityOfSecurity
                        FROM purchase_order AS po
                        JOIN tender_request AS tr ON po.tender_id = tr.tender_id
                WHERE po.created_date BETWEEN :startDate AND :endDate
                ORDER BY po.created_date
            """, nativeQuery = true)
    List<Object[]> getVendorContractDetails(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query(value = """
                SELECT
                po.po_id AS 'Order Id',
                CASE
                WHEN vm.registered_platform = 'Yes' THEN 'GeM'
                WHEN vm.registered_platform = 'No' THEN 'Non-GeM'
                ELSE 'Unknown'
                END AS 'GeM / Non-GeM',
                '' As 'Indentor',
                po.total_value_of_po AS 'Value',
                '' AS 'Description of goods/services',
                vm.vendor_name AS 'Vendor Name'
                FROM purchase_order AS po
                LEFT JOIN vendor_master AS vm ON po.vendor_name = vm.vendor_name
                WHERE po.created_date BETWEEN :startDate AND :endDate
            ORDER BY po.created_date
                 """, nativeQuery = true)
    List<Object[]> getProcurementActivityReport(@Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate);

    PurchaseOrder findByPoId(String poId);

    List<PurchaseOrder> findByVendorId(String vendorId);

    PurchaseOrder findByTenderId(String tenderId);

    @Query(value = """
            SELECT
              wt.createdDate                               AS approvedDate,
              po.po_id                                     AS poId,
              po.vendor_name                               AS vendorName,
              po.total_value_of_po                         AS value,
              po.tender_id                                 AS tenderId,
              po.project_name                              AS project,
              po.vendor_id                                 AS vendorId,
              (SELECT GROUP_CONCAT(i.indent_id SEPARATOR ', ')
                 FROM indent_id i WHERE i.tender_id = po.tender_id
              )                                            AS indentIds,
               (SELECT md.mode_of_procurement
                            FROM material_details md
                            WHERE md.indent_id IN (
                            SELECT i.indent_id FROM indent_id i WHERE i.tender_id = po.tender_id
                         ) LIMIT 1) AS modeOfProcurement,
              JSON_ARRAYAGG(
                JSON_OBJECT(
                  'materialCode',       attr.material_code,
                  'materialDescription',attr.material_description,
                  'quantity',           attr.quantity,
                  'rate',               attr.rate,
                  'currency',           attr.currency,
                  'exchangeRate',       attr.exchange_rate,
                  'gst',                attr.gst,
                  'duties',             attr.duties,
                  'freightCharge',      attr.freight_charge,
                  'budgetCode',         attr.budget_code,
                  'receivedQuantity',   attr.received_quantity
                )
              )                                            AS attributesJson
            FROM workflow_transition wt
            JOIN purchase_order po  ON wt.requestId = po.po_id
            JOIN purchase_order_attributes attr 
              ON po.po_id = attr.po_id
            WHERE wt.workflowName = 'PO Workflow'
              AND wt.status        = 'Completed'
              AND wt.nextAction   IS NULL
              AND wt.createdDate BETWEEN :from AND :to
            GROUP BY
              wt.createdDate, po.po_id, po.vendor_name,
              po.total_value_of_po, po.tender_id,
              po.project_name, po.vendor_id
            ORDER BY wt.createdDate, po.po_id
            """, nativeQuery = true)
    List<Object[]> getApprovedPoReport(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    @Query(value = """
            SELECT
              po.po_id                                     AS poId,
              po.tender_id                                 AS tenderId,
              (SELECT GROUP_CONCAT(i.indent_id SEPARATOR ', ')
                 FROM indent_id i WHERE i.tender_id = po.tender_id
              )                                            AS indentIds,
              po.total_value_of_po                         AS value,
              po.vendor_name                               AS vendorName,
              wt.createdDate                               AS submittedDate,
              wt.nextRole                                  AS pendingWith,
              wt.modificationDate                          AS pendingFrom,
              wt.status                                    AS status,
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
                  'freightCharge',       attr.freight_charge,
                  'budgetCode',          attr.budget_code,
                  'receivedQuantity',    attr.received_quantity
                )
              )                                            AS attributesJson
            FROM workflow_transition wt
            JOIN purchase_order po
              ON wt.requestId = po.po_id
            JOIN purchase_order_attributes attr
              ON po.po_id = attr.po_id
            WHERE wt.workflowName = 'PO Workflow'
              AND wt.nextAction = 'Pending'
              AND wt.nextRole IS NOT NULL
              AND wt.createdDate BETWEEN :fromDate AND :toDate
            GROUP BY
              po.po_id, po.tender_id, po.total_value_of_po,
              po.vendor_name, wt.createdDate, wt.nextRole,
              wt.modificationDate, wt.status
            ORDER BY wt.createdDate, po.po_id
            """,
            nativeQuery = true)
    List<Object[]> getPendingPoReport(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    @Query(value = """
            SELECT
              po.po_id AS orderNo,
              DATE(po.created_date) AS orderDate,
              po.total_value_of_po AS value,
              JSON_ARRAYAGG(
                JSON_OBJECT(
                  'materialCode', poa.material_code,
                  'materialDescription', poa.material_description
                )
              ) AS descriptions,
              po.vendor_name AS vendorName,
              po.consignes_address AS location,
              po.delivery_date AS deliveryDate
            FROM purchase_order po
            LEFT JOIN purchase_order_attributes poa ON po.po_id = poa.po_id
            WHERE po.created_date BETWEEN :startDate AND :endDate
            GROUP BY po.po_id, po.created_date, po.total_value_of_po, po.vendor_name, po.consignes_address, po.delivery_date
            """, nativeQuery = true)
    List<Object[]> findQuarterlyVigilanceReportDto(@Param("startDate") LocalDateTime startDate,
                                                   @Param("endDate") LocalDateTime endDate);

    @Query(value = """
              SELECT
              po.po_id AS poId,
              po.tender_id AS tenderId,
                      (
              SELECT GROUP_CONCAT(i.indent_id SEPARATOR ', ')
              FROM indent_id i
              WHERE i.tender_id = po.tender_id
            ) AS indentIds,
              po.total_value_of_po AS value,
              po.vendor_name AS vendorName,
              wt.createdDate AS submittedDate,
                      (
              SELECT JSON_ARRAYAGG(
                      JSON_OBJECT(
                      'materialCode', attr.material_code,
                         'materialDescription', attr.material_description
                      )
                     )
              FROM purchase_order_attributes attr
              WHERE attr.po_id = po.po_id
            ) AS materials,
              wt.remarks AS reason
              FROM workflow_transition wt
              JOIN purchase_order po
              ON wt.requestId = po.po_id
              WHERE wt.workflowName = 'PO Workflow'
              AND wt.action = 'Rejected'
              AND wt.createdDate BETWEEN :startDate AND :endDate
              ORDER BY wt.createdDate, po.po_id
              """, nativeQuery = true)
    List<Object[]> findShortClosedCancelledOrder(@Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);

    @Query(value = """
    SELECT 
        DATE_FORMAT(po.created_date, '%Y-%m') AS month,
        po.po_id AS poNumber,
        po.created_date AS date,
        (
            SELECT GROUP_CONCAT(i.indent_id SEPARATOR ', ')
            FROM indent_id i
            WHERE i.tender_id = po.tender_id
        ) AS indentIds,
        po.total_value_of_po AS value,
        po.vendor_name AS vendorName,
        (
            SELECT md.mode_of_procurement
            FROM material_details md
            WHERE md.indent_id = (
                SELECT i.indent_id
                FROM indent_id i
                WHERE i.tender_id = po.tender_id
                LIMIT 1
            )
            LIMIT 1
        ) AS modeOfProcurement
    FROM purchase_order po
    WHERE po.created_date BETWEEN :startDate AND :endDate
    ORDER BY po.created_date, po.po_id
    """, nativeQuery = true)
    List<Object[]> getMonthlyProcurementReport(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    List<SearchPOIdDto> findByPoIdContainingIgnoreCase(String poId);
    List<SearchPOIdDto> findByCreatedDateBetween(LocalDateTime start, LocalDateTime end);
    @Query("SELECT new com.astro.dto.workflow.ProcurementDtos.purchaseOrder.SearchPOIdDto(a.purchaseOrder.poId) " +
            "FROM PurchaseOrderAttributes a " +
            "WHERE a.materialDescription = :materialDescription")
    List<SearchPOIdDto> findPoIdByMaterialDescription(@Param("materialDescription") String materialDescription);
    
    @Query("SELECT new com.astro.dto.workflow.ProcurementDtos.purchaseOrder.SearchPOIdDto(p.poId) " +
            "FROM PurchaseOrder p WHERE p.vendorId = :vendorName")
    List<SearchPOIdDto> findPoIdsByVendorName(@Param("vendorName") String vendorName);

    @Query("SELECT po FROM PurchaseOrder po " +
            "WHERE po.deliveryDate <= :endDate " +
            "AND NOT EXISTS (SELECT g FROM GrnMasterEntity g WHERE g.grnProcessId = SUBSTRING(po.poId, 3))")
    List<PurchaseOrder> findPOsExpiringWithoutGRN(@Param("endDate") LocalDate endDate);

    @Query("""
    SELECT new com.astro.dto.workflow.ProcurementDtos.performanceWarrsntySecurityReportDto(
        po.poId,
        po.createdDate,
        tr.modeOfProcurement,
        po.vendorName,
        tr.titleOfTender,
        po.totalValueOfPo,
        po.typeOfSecurity,
        po.securityNumber,
        po.securityDate,
        po.expiryDate,
        po.applicablePbgToBeSubmitted
    )
    FROM PurchaseOrder po
    JOIN TenderRequest tr ON tr.tenderId = po.tenderId
    WHERE po.createdDate BETWEEN :startDate AND :endDate
      AND po.typeOfSecurity IS NOT NULL
      AND po.typeOfSecurity <> ''
""")
    List<performanceWarrsntySecurityReportDto> getPerformanceSecurityAndWarrantyReport(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query(value = """
    SELECT
      wt.createdDate AS approvedDate,
      po.po_id AS poId,
      po.vendor_name AS vendorName,
      po.total_value_of_po AS value,
      po.tender_id AS tenderId,
      po.project_name AS project,
      po.vendor_id AS vendorId,
      GROUP_CONCAT(DISTINCT i.indent_id SEPARATOR ', ') AS indentIds,
      (SELECT md.mode_of_procurement
         FROM material_details md
         WHERE md.indent_id IN (
             SELECT i.indent_id
             FROM indent_id i
             JOIN indent_creation ic ON i.indent_id = ic.indent_id
             WHERE i.tender_id = po.tender_id
               AND ic.created_by = :userId
         ) LIMIT 1) AS modeOfProcurement,
      JSON_ARRAYAGG(
        JSON_OBJECT(
          'materialCode', attr.material_code,
          'materialDescription', attr.material_description,
          'quantity', attr.quantity,
          'rate', attr.rate,
          'currency', attr.currency,
          'exchangeRate', attr.exchange_rate,
          'gst', attr.gst,
          'duties', attr.duties,
          'freightCharge', attr.freight_charge,
          'budgetCode', attr.budget_code,
          'receivedQuantity', attr.received_quantity
        )
      ) AS attributesJson
    FROM workflow_transition wt
    JOIN purchase_order po ON wt.requestId = po.po_id
    JOIN purchase_order_attributes attr ON po.po_id = attr.po_id
    JOIN indent_id i ON i.tender_id = po.tender_id
    JOIN indent_creation ic ON i.indent_id = ic.indent_id AND ic.created_by = :userId
    WHERE wt.workflowName = 'PO Workflow'
      AND wt.status = 'Completed'
      AND wt.nextAction IS NULL
      AND wt.createdDate BETWEEN :from AND :to
    GROUP BY
      wt.createdDate, po.po_id, po.vendor_name,
      po.total_value_of_po, po.tender_id,
      po.project_name, po.vendor_id
    ORDER BY wt.createdDate, po.po_id
    """, nativeQuery = true)
    List<Object[]> getApprovedPoReportByIndentCreator(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("userId") Integer userId
    );


    @Query(value = """
    SELECT
      po.po_id AS poId,
      po.tender_id AS tenderId,
      GROUP_CONCAT(DISTINCT i.indent_id SEPARATOR ', ') AS indentIds,
      po.total_value_of_po AS value,
      po.vendor_name AS vendorName,
      wt.createdDate AS submittedDate,
      wt.nextRole AS pendingWith,
      wt.modificationDate AS pendingFrom,
      wt.status AS status,
      JSON_ARRAYAGG(
        JSON_OBJECT(
          'materialCode', attr.material_code,
          'materialDescription', attr.material_description,
          'quantity', attr.quantity,
          'rate', attr.rate,
          'currency', attr.currency,
          'exchangeRate', attr.exchange_rate,
          'gst', attr.gst,
          'duties', attr.duties,
          'freightCharge', attr.freight_charge,
          'budgetCode', attr.budget_code,
          'receivedQuantity', attr.received_quantity
        )
      ) AS attributesJson
    FROM workflow_transition wt
    JOIN purchase_order po ON wt.requestId = po.po_id
    JOIN purchase_order_attributes attr ON po.po_id = attr.po_id
    JOIN indent_id i ON i.tender_id = po.tender_id
    JOIN indent_creation ic ON ic.indent_id = i.indent_id AND ic.created_by = :userId
    WHERE wt.workflowName = 'PO Workflow'
      AND wt.nextAction = 'Pending'
      AND wt.nextRole IS NOT NULL
      AND wt.createdDate BETWEEN :fromDate AND :toDate
    GROUP BY
      po.po_id, po.tender_id, po.total_value_of_po,
      po.vendor_name, wt.createdDate, wt.nextRole,
      wt.modificationDate, wt.status
    ORDER BY wt.createdDate, po.po_id
    """, nativeQuery = true)
    List<Object[]> getPendingPoReportForIndentCreator(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("userId") Integer userId
    );

    @Query("""
SELECT new com.astro.dto.workflow.PaymentVoucherPoSearchDto(
    p.poId, p.vendorId, p.vendorName, p.createdDate,
    a.materialDescription
)
FROM PurchaseOrder p
LEFT JOIN p.purchaseOrderAttributes a
WHERE p.poId IN :poIds
""")
    List<PaymentVoucherPoSearchDto> findPoDetailsDtoByPoIds(@Param("poIds") List<String> poIds);
        

        // All versions of a PO family
@Query("SELECT p FROM PurchaseOrder p WHERE (p.poId = :baseId OR p.poId LIKE CONCAT(:baseId, '/%')) ORDER BY p.poVersion DESC")
List<PurchaseOrder> findAllVersionsByBaseId(@Param("baseId") String baseId);

// Active version
@Query("SELECT p FROM PurchaseOrder p WHERE (p.poId = :baseId OR p.poId LIKE CONCAT(:baseId, '/%')) AND p.isActive = true")
Optional<PurchaseOrder> findActiveVersionByBaseId(@Param("baseId") String baseId);

// Update existing search queries to filter isActive = true:
List<SearchPOIdDto> findByPoIdContainingIgnoreCaseAndIsActiveTrue(String poId);

@Query("SELECT new com.astro.dto.workflow.ProcurementDtos.purchaseOrder.SearchPOIdDto(p.poId) " +
        "FROM PurchaseOrder p WHERE p.createdDate BETWEEN :start AND :end AND p.isActive = true")
List<SearchPOIdDto> findByCreatedDateBetweenAndIsActive(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);



  /*  @Query("SELECT new com.astro.dto.materialHistoryDto(p.poId, CAST(p.createdDate AS string), p.vendorName) " +
            "FROM PurchaseOrder p " +
            "JOIN p.purchaseOrderAttributes a " +
            "WHERE a.materialCode = :materialCode " +
            "ORDER BY p.createdDate DESC, p.poId DESC")
    Page<materialHistoryDto> findMaterialHistory(@Param("materialCode") String materialCode, Pageable pageable);*/


    // PurchaseOrder getByPoId(String poId);
}
