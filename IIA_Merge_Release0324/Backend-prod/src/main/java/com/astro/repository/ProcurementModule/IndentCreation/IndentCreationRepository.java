package com.astro.repository.ProcurementModule.IndentCreation;

import com.astro.dto.workflow.ApprovedIndentsDto;
import com.astro.dto.workflow.ProcurementDtos.IndentDto.IndentReportDetailsDTO;
import com.astro.dto.workflow.ProcurementDtos.IndentDto.SearchIndentIdDto;
import com.astro.dto.workflow.ProcurementDtos.TechnoMomReportDTO;
import com.astro.entity.ProcurementModule.IndentCreation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IndentCreationRepository extends JpaRepository<IndentCreation, String> {

    @Query("SELECT DISTINCT i.projectName FROM IndentCreation i WHERE i.indentId IN :indentIds")
    List<String> findDistinctProjectNames(@Param("indentIds") List<String> indentIds);

    @Query(value = """
             SELECT
                 ic.indent_id AS `Indent Id`,
               
                 -- Approved Date (only if the current role is Reporting Officer)
                 (SELECT wt.createdDate
                  FROM workflow_transition wt
                  WHERE wt.requestId = ic.indent_id AND wt.CURRENTROLE = 'Reporting Officer'
                  ORDER BY wt.createdDate DESC LIMIT 1) AS `Approved Date`,
              
                 -- Assigned To (latest next role from workflow transition table)
                
               ic.employee_name AS `Assigned To`,
                 tr.tender_id AS `Tender Request`,
                 tr.mode_of_procurement AS `Mode of Tendering`,
              
                 -- Corresponding PO/SO (taken from PO/SO table via Tender)
                 (SELECT COALESCE(po.po_id, so.so_id)
                  FROM tender_request tr2
                  LEFT JOIN purchase_order po ON tr2.tender_id = po.tender_id
                  LEFT JOIN service_order so ON tr2.tender_id = so.tender_id
                  WHERE tr2.tender_id = tr.tender_id
                  ORDER BY po.created_date DESC, so.created_date DESC LIMIT 1) AS `Corresponding PO/ SO`,
               
                 -- Status of PO/SO (latest status from workflow transition)
                 (SELECT wt.status
                  FROM workflow_transition wt
                  WHERE wt.requestId= (SELECT COALESCE(po.po_id, so.so_id)
                                         FROM purchase_order po
                                         LEFT JOIN service_order so ON po.tender_id = so.tender_id
                                         WHERE po.tender_id = tr.tender_id OR so.tender_id = tr.tender_id
                                         ORDER BY po.created_date DESC, so.created_date DESC LIMIT 1)
                  ORDER BY wt.createdDate DESC LIMIT 1) AS `Status of PO/ SO`,
             
                 ic.created_date AS `Submitted Date`,
               
                 -- Pending Approval With & Pending From (latest next role for indent)
                 (SELECT wt.nextRole
                  FROM workflow_transition wt
                  WHERE wt.requestId = ic.indent_id
                  ORDER BY wt.createdDate DESC LIMIT 1) AS `Pending Approval With & Pending From`,
                
                 -- PO/SO Approved Date (2nd transaction create date for that request id)
                 (SELECT wt.createdDate
                  FROM workflow_transition wt
                  WHERE wt.requestId = (SELECT COALESCE(po.po_id, so.so_id)
                                         FROM purchase_order po
                                         LEFT JOIN service_order so ON po.tender_id = so.tender_id
                                         WHERE po.tender_id = tr.tender_id OR so.tender_id = tr.tender_id
                                         ORDER BY po.created_date DESC, so.created_date DESC LIMIT 1)
                  ORDER BY wt.createdDate ASC LIMIT 1 OFFSET 1) AS `PO/ SO Approved Date`,
               
               md.material_description AS `Material`,
               md.material_category AS `Material Category`,
               md.material_sub_category AS `Material Sub-Category`,
              
                 -- Vendor Name (latest from PO/SO)
                 (SELECT COALESCE(po.vendor_name, so.vendor_name)
                  FROM purchase_order po
                  LEFT JOIN service_order so ON po.tender_id = so.tender_id
                  WHERE po.tender_id = tr.tender_id OR so.tender_id = tr.tender_id
                  ORDER BY po.created_date DESC, so.created_date DESC LIMIT 1) AS `Vendor Name`,
             
                 ic.indentor_name AS `Indentor Name`,
                       
                 -- Sum of value of all materials in indent
             --  (SELECT SUM(ic2.value) FROM indent_creation ic2 WHERE ic2.indent_id = ic.indent_id) AS `Value of Indent`,
               (SELECT SUM(md.total_price) FROM material_details md WHERE md.indent_id = ic.indent_id) AS `Value of Indent`,
               -- Value of PO (linked via Tender)
               (SELECT po.total_value_of_po FROM purchase_order po WHERE po.tender_id = tr.tender_id ORDER BY po.created_date DESC LIMIT 1) AS `Value of PO`,
               (SELECT po.gem_contract_file_name
                FROM purchase_order po
                WHERE po.tender_id = tr.tender_id
                ORDER BY po.created_date DESC
                LIMIT 1) AS `PO GEM Contract File`,
               -- GRIN No (latest GRIN entry)
               -- (SELECT gr.grin_no FROM goods_receipt_inspection gr WHERE gr.indent_id = ic.indent_id ORDER BY gr.create_date DESC LIMIT 1) AS `GRIN No`,
               ic.project_name AS `Project`,
                       
               CAST(NULL AS CHAR) AS 'invoiceNo',
               CAST(NULL AS CHAR) AS 'gissNo',
               CAST(NULL AS DECIMAL(19, 2)) AS 'valuePendingToBePaid',
                
                
             
                 -- Current Stage of Indent (latest next role)
                 (SELECT wt.nextRole
                  FROM workflow_transition wt
                  WHERE wt.requestId = ic.indent_id
                  ORDER BY wt.createdDate DESC LIMIT 1) AS `Current Stage of the Indent`,
                
                 -- Short-closed and cancelled through amendment
                 (SELECT CASE WHEN wt.action = 'Rejected' THEN 'Short-closed and cancelled through amendment' ELSE NULL END
                  FROM workflow_transition wt
                  WHERE wt.requestId = ic.indent_id
                  ORDER BY wt.createdDate DESC LIMIT 1) AS `Short-Closed and Cancelled Through Amendment`,
               
                 -- Reason for short closure & cancellation
                 (SELECT wt.remarks
                  FROM workflow_transition wt
                  WHERE wt.requestId = ic.indent_id AND wt.action = 'Rejected'
                  ORDER BY wt.createdDate DESC LIMIT 1) AS `Reason for Short-Closure & Cancellation`
                         
            FROM indent_creation ic
               LEFT JOIN indent_id iid ON ic.indent_id = iid.indent_id
               LEFT JOIN tender_request tr ON iid.tender_id = tr.tender_id
               LEFT JOIN  material_details md ON ic.indent_id = md.indent_id
               WHERE ic.created_date BETWEEN :startDate AND :endDate
                  
                  """, nativeQuery = true)
    List<Object[]> fetchIndentReportDetails(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query(value = """
                   SELECT
                       indent.created_date AS 'Date',
                       CAST(NULL AS CHAR) AS 'Uploaded Techno Commercial MoM Reports',
                       po.po_id AS 'PO/ WO No',
                       po.total_value_of_po AS 'Value',
                       indent.indent_id AS 'Corresponding Indent Number'
                   FROM indent_creation AS indent
                   LEFT JOIN purchase_order AS po ON indent.indent_id = po.indent_id
                   WHERE indent.created_date BETWEEN :startDate AND :endDate
                   ORDER BY
                       indent.created_date;
            """, nativeQuery = true)
    List<Object[]> getTechnoMomReport(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    IndentCreation getByIndentId(String indentId);

    List<IndentCreation> findByIndentIdIn(List<String> indentIds);

    IndentCreation findByIndentId(String indentId);


    @Query("SELECT MAX(i.indentNumber) FROM IndentCreation i")
    Integer findMaxIndentNumber();

    @Query(value = """
            SELECT
              ind.indent_id                          AS indentId,
              ind.indentor_name                      AS indentorName,
              ind.indentor_mobile_no                 AS indentorMobileNo,
              ind.indentor_email_address             AS indentorEmail,
              ind.consignes_location                 AS consignesLocation,
              ind.project_name                       AS projectName,
              wt.createdDate                         AS submittedDate,
              wt.nextRole                            AS pendingWith,
              wt.modificationDate                    AS pendingFrom,
              wt.status                              AS status,
              ind.created_by                          As createdBy,
              ind.total_indent_value                 As indentValue,
              JSON_ARRAYAGG(
                JSON_OBJECT(
                  'materialCode',        m.material_code,
                  'materialDescription', m.material_description,
                  'quantity',            m.quantity,
                  'unitPrice',           m.unit_price,
                  'uom',                 m.uom,
                  'totalPrice',          m.total_price,
                  'materialCategory',    m.material_category,
                  'materialSubCategory', m.material_sub_category,
                  'modeOfProcurement',   m.mode_of_procurement,
                  'currency',            m.currency,
                  'vendorNames',         (SELECT GROUP_CONCAT(v.vendor_name SEPARATOR ', ')
                                           FROM vendor_names_for_job_work_material v
                                           WHERE v.indent_id = ind.indent_id And v.material_code = m.material_code)
                )
              ) AS materialDetails
            FROM workflow_transition wt
            JOIN indent_creation ind
              ON wt.requestId = ind.indent_id
            JOIN material_details m
              ON ind.indent_id = m.indent_id
            WHERE wt.workflowName = 'Indent Workflow'
              AND wt.createdDate BETWEEN :fromDate AND :toDate
            GROUP BY ind.indent_id,
                     ind.indentor_name,
                     ind.indentor_mobile_no,
                     ind.indentor_email_address,
                     ind.consignes_location,
                     ind.project_name,
                     wt.createdDate,
                     wt.nextRole,
                     wt.modificationDate,
                     wt.status
            ORDER BY wt.createdDate, ind.indent_id
            """,
            nativeQuery = true)
    List<Object[]> getAllIndentListReport(LocalDateTime fromDate, LocalDateTime toDate);

    @Query(value = """
            SELECT
              ind.indent_id                          AS indentId,
              ind.indentor_name                      AS indentorName,
              ind.indentor_mobile_no                 AS indentorMobileNo,
              ind.indentor_email_address             AS indentorEmail,
              ind.consignes_location                 AS consignesLocation,
              ind.project_name                       AS projectName,
              wt.createdDate                         AS submittedDate,
              wt.nextRole                            AS pendingWith,
              wt.modificationDate                    AS pendingFrom,
              wt.status                              AS status,
              ind.created_by                         AS createdBy,
              ind.total_indent_value                 As indentValue,
              JSON_ARRAYAGG(
                JSON_OBJECT(
                  'materialCode',        m.material_code,
                  'materialDescription', m.material_description,
                  'quantity',            m.quantity,
                  'unitPrice',           m.unit_price,
                  'uom',                 m.uom,
                  'totalPrice',          m.total_price,
                  'materialCategory',    m.material_category,
                  'materialSubCategory', m.material_sub_category,
                  'modeOfProcurement',   m.mode_of_procurement,
                  'currency',            m.currency,
                  'vendorNames',         (SELECT GROUP_CONCAT(v.vendor_name SEPARATOR ', ')
                                           FROM vendor_names_for_job_work_material v
                                           WHERE v.indent_id = ind.indent_id AND v.material_code = m.material_code)
                )
              ) AS materialDetails
            FROM workflow_transition wt
            JOIN indent_creation ind ON wt.requestId = ind.indent_id
            JOIN material_details m  ON ind.indent_id = m.indent_id
            WHERE wt.workflowName = 'Indent Workflow'
              AND wt.createdDate >= :fromDate AND wt.createdDate < :toDate
              AND ind.created_by = :userId
            GROUP BY ind.indent_id,
                     ind.indentor_name,
                     ind.indentor_mobile_no,
                     ind.indentor_email_address,
                     ind.consignes_location,
                     ind.project_name,
                     wt.createdDate,
                     wt.nextRole,
                     wt.modificationDate,
                     wt.status
            ORDER BY wt.createdDate, ind.indent_id
            """,
            nativeQuery = true)
    List<Object[]> getAllIndentListUserIdsReport(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("userId") Integer userId
    );


    // Search by indentId (Process ID)
    List<SearchIndentIdDto> findByIndentIdContainingIgnoreCase(String indentId);

    // Search by indentId filtered by indentType
    List<SearchIndentIdDto> findByIndentIdContainingIgnoreCaseAndIndentType(String indentId, String indentType);

    // Search by indentId filtered by indentType AND materialCategoryType
    List<SearchIndentIdDto> findByIndentIdContainingIgnoreCaseAndIndentTypeAndMaterialCategoryType(String indentId, String indentType, String materialCategoryType);

    // Search by Submitted Date (createdDate)
    @Query("SELECT new com.astro.dto.workflow.ProcurementDtos.IndentDto.SearchIndentIdDto(i.indentId) FROM IndentCreation i WHERE i.createdDate >= :start AND i.createdDate < :end")
    List<SearchIndentIdDto> findByCreatedDateBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Search by Submitted Date filtered by indentType
    @Query("SELECT new com.astro.dto.workflow.ProcurementDtos.IndentDto.SearchIndentIdDto(i.indentId) FROM IndentCreation i WHERE i.createdDate >= :start AND i.createdDate < :end AND i.indentType = :indentType")
    List<SearchIndentIdDto> findByCreatedDateBetweenAndIndentType(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("indentType") String indentType);

    // Search by Submitted Date filtered by indentType AND materialCategoryType
    @Query("SELECT new com.astro.dto.workflow.ProcurementDtos.IndentDto.SearchIndentIdDto(i.indentId) FROM IndentCreation i WHERE i.createdDate >= :start AND i.createdDate < :end AND i.indentType = :indentType AND i.materialCategoryType = :materialCategoryType")
    List<SearchIndentIdDto> findByCreatedDateBetweenAndIndentTypeAndMaterialCategoryType(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("indentType") String indentType, @Param("materialCategoryType") String materialCategoryType);

    // Search by Indentor Name
    @Query("SELECT new com.astro.dto.workflow.ProcurementDtos.IndentDto.SearchIndentIdDto(i.indentId) FROM IndentCreation i WHERE LOWER(i.indentorName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<SearchIndentIdDto> findByIndentorName(@Param("name") String name);

    // Search by Indentor Name filtered by indentType
    @Query("SELECT new com.astro.dto.workflow.ProcurementDtos.IndentDto.SearchIndentIdDto(i.indentId) FROM IndentCreation i WHERE LOWER(i.indentorName) LIKE LOWER(CONCAT('%', :name, '%')) AND i.indentType = :indentType")
    List<SearchIndentIdDto> findByIndentorNameAndIndentType(@Param("name") String name, @Param("indentType") String indentType);

    // Search by Indentor Name filtered by indentType AND materialCategoryType
    @Query("SELECT new com.astro.dto.workflow.ProcurementDtos.IndentDto.SearchIndentIdDto(i.indentId) FROM IndentCreation i WHERE LOWER(i.indentorName) LIKE LOWER(CONCAT('%', :name, '%')) AND i.indentType = :indentType AND i.materialCategoryType = :materialCategoryType")
    List<SearchIndentIdDto> findByIndentorNameAndIndentTypeAndMaterialCategoryType(@Param("name") String name, @Param("indentType") String indentType, @Param("materialCategoryType") String materialCategoryType);

    // Search by Material Description (join with MaterialDetails)
    @Query("SELECT new com.astro.dto.workflow.ProcurementDtos.IndentDto.SearchIndentIdDto(i.indentId) FROM IndentCreation i JOIN i.materialDetails m WHERE LOWER(m.materialDescription) LIKE LOWER(CONCAT('%', :desc, '%'))")
    List<SearchIndentIdDto> findByMaterialDescription(@Param("desc") String desc);


    @Query("SELECT CASE WHEN COUNT(ic) > 0 THEN true ELSE false END FROM IndentCreation ic WHERE ic.indentId = :requestId AND ic.employeeId IS NOT NULL")
    boolean isAssigned(@Param("requestId") String requestId);

    List<IndentCreation> findAllByCancelStatusTrue();

    @Query("SELECT i.createdDate FROM IndentCreation i WHERE i.indentId IN :indentIds")
    List<LocalDateTime> findCreatedDatesByIndentIds(@Param("indentIds") List<String> indentIds);

    @Query("SELECT i.buyBackAmount FROM IndentCreation i " +
            "WHERE i.indentId IN :indentIds AND i.buyBack = true")
    List<String> findBuyBackAmountsByIndentIds(@Param("indentIds") List<String> indentIds);



   /* @Query("""
    SELECT new com.astro.dto.workflow.ApprovedIndentsDto(
        i.indentId,
        pm.projectNameDescription,
        i.indentorName,
        i.createdDate,
        md.materialDescription
    )
    FROM IndentCreation i
    JOIN ProjectMaster pm ON pm.projectCode = i.projectName
    LEFT JOIN MaterialDetails md ON md.indentCreation.indentId = i.indentId
    WHERE i.indentId IN :approvedIndentIds
""")
    List<ApprovedIndentsDto> findApprovedIndents(@Param("approvedIndentIds") List<String> approvedIndentIds);*/
   @Query("""
    SELECT new com.astro.dto.workflow.ApprovedIndentsDto(
        i.indentId,
        pm.projectNameDescription,
        i.indentorName,
        i.createdDate,
        md.materialDescription
    )
    FROM IndentCreation i
    LEFT JOIN ProjectMaster pm ON pm.projectCode = i.projectName
    LEFT JOIN MaterialDetails md ON md.indentCreation.indentId = i.indentId
    WHERE i.indentId IN :approvedIndentIds
""")
   List<ApprovedIndentsDto> findApprovedIndents(@Param("approvedIndentIds") List<String> approvedIndentIds);


}
