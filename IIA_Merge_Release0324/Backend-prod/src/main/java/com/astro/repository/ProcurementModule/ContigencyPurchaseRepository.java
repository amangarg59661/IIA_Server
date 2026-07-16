package com.astro.repository.ProcurementModule;

import com.astro.entity.ProcurementModule.ContigencyPurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hibernate.hql.internal.antlr.HqlTokenTypes.AS;
import static org.hibernate.hql.internal.antlr.HqlTokenTypes.WHERE;
import static org.hibernate.loader.Loader.SELECT;
import static org.springframework.data.repository.query.parser.Part.Type.BETWEEN;

@Repository
public interface ContigencyPurchaseRepository extends JpaRepository<ContigencyPurchase,String> {

   /* @Query(value = """
        SELECT
            cp.contigency_id AS Id,
            cp.material_description AS Material,
            md.category AS Material_category,
            md.sub_category AS Material_sub_category,
            cp.remarks_for_purchase AS End_user,
            cp.amount_to_be_paid AS Value,
            cp.vendors_name AS Paid_to,
            cp.vendors_name AS Vendor_name,
            cp.project_name AS Project
        FROM
            contigency_purchase cp
        JOIN
            material_master md
        ON
            cp.material_code = md.material_code
        WHERE
            cp.Date BETWEEN :startDate AND :endDate
        ORDER BY
            cp.Date
    """, nativeQuery = true)*/
   /*@Query(value = """
    SELECT
      cp.contigency_id          AS Id,
      cm.material_description    AS Material,
      cm.material_category       AS Material_category,
      cm.material_sub_category   AS Material_sub_category,
      cp.remarks_for_purchase    AS End_user,
      cm.total_price             AS Value,
      cp.vendors_name            AS Paid_to,
      cp.vendors_name            AS Vendor_name,
      cp.project_name            AS Project
    FROM
      contigency_purchase cp
      JOIN cp_materials cm
        ON cp.contigency_id = cm.contigency_id
    WHERE
      cp.Date BETWEEN :startDate AND :endDate
    ORDER BY
      cp.Date
  """,
           nativeQuery = true)
   List<Object[]> findContigencyPurchaseReport(
           @Param("startDate") LocalDate startDate,
           @Param("endDate")   LocalDate endDate
   );*/
   @Query(value = """
    SELECT 
      cp.contigency_id               AS contigencyId,
      cp.vendors_name                AS vendorName,
      cp.project_name                AS projectName,
      cp.payment_to_vendor          AS paymentToVendor,
      cp.payment_to_employee        AS paymentToEmployee,
      cp.remarks_for_purchase       AS purpose,
      cp.created_by                 AS createdBy,
      wt.nextRole                   AS pendingWith,
      wt.modificationDate           AS pendingFrom,
      wt.status                     AS status,
      wt.nextAction                 AS action,
      JSON_ARRAYAGG(
        JSON_OBJECT(
          'materialCode',         m.material_code,
          'materialDescription',  m.material_description,
          'quantity',             m.quantity,
          'unitPrice',            m.unit_price,
          'uom',                  m.uom,
          'budgetCode',           m.budget_code,
          'gst',                  m.gst,
          'materialCategory',     m.material_category,
          'materialSubCategory',  m.material_sub_category,
          'currency',             m.currency,
          'countryOfOrigin',      m.country_of_origin,
          'totalPrice',           m.total_price
        )
      ) AS cpMaterials
    FROM contigency_purchase cp
    JOIN cp_materials m 
      ON cp.contigency_id = m.contigency_id

           LEFT JOIN workflow_transition wt ON wt.workflowTransitionId = (
                SELECT wt2.workflowTransitionId
                FROM workflow_transition wt2
                WHERE wt2.requestId = cp.contigency_id
                  AND wt2.workflowName = 'Contingency Purchase Workflow'
                ORDER BY wt2.workflowTransitionId DESC
                LIMIT 1
            )
            

    WHERE cp.date BETWEEN :fromDate AND :toDate

    GROUP BY 
      cp.contigency_id, cp.vendors_name, cp.project_name,
      cp.payment_to_vendor, cp.payment_to_employee,
      cp.remarks_for_purchase, cp.created_by,
      wt.nextRole, wt.modificationDate, wt.status, wt.nextAction

    ORDER BY cp.date
    """, nativeQuery = true)
   List<Object[]> getContigencyPurchaseReport(
           @Param("fromDate") LocalDate fromDate,
           @Param("toDate") LocalDate toDate
   );


    //   List<Object[]> findContigencyPurchaseReport(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT MAX(i.cpNumber) FROM ContigencyPurchase i")
    Integer findMaxCpNumber();

    // ContigencyPurchase findByContigencyId(String contigencyId);

   // ContigencyPurchase getByCpId(String contigencyId);

    @Query("SELECT c.contigencyId FROM ContigencyPurchase c WHERE LOWER(c.contigencyId) LIKE LOWER(CONCAT('%', :cpId, '%'))")
    List<String> findCpIdByContigencyIdContainingIgnoreCase(@Param("cpId") String cpId);

    @Query("SELECT DISTINCT c.contigencyId FROM ContigencyPurchase c JOIN c.cpMaterials m WHERE LOWER(m.materialDescription) LIKE LOWER(CONCAT('%', :desc, '%'))")
    List<String> findCpIdByMaterialDescriptionContainingIgnoreCase(@Param("desc") String materialDescription);

    @Query("SELECT c.contigencyId FROM ContigencyPurchase c WHERE c.createdDate BETWEEN :start AND :end")
    List<String> findCpIdByCreatedDateBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT c.contigencyId FROM ContigencyPurchase c WHERE LOWER(c.paymentToVendor) LIKE LOWER(CONCAT('%', :vendorName, '%'))")
    List<String> findCpIdByPaymentToVendorContainingIgnoreCase(@Param("vendorName") String vendorName);

    List<ContigencyPurchase> findByCreatedByAndCurrentStatus(String createdBy, String currentStatus);

}
