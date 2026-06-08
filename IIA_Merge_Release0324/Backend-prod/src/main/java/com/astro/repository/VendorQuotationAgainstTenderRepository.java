package com.astro.repository;

import com.astro.dto.workflow.CompletedVendorsDto;
import com.astro.entity.VendorQuotationAgainstTender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendorQuotationAgainstTenderRepository extends JpaRepository<VendorQuotationAgainstTender,Long> {

    List<VendorQuotationAgainstTender> findByTenderId(String tenderId);
  @Query("SELECT v.vendorId FROM VendorQuotationAgainstTender v WHERE v.tenderId = :tenderId")
  List<String> findVendorIdsByTenderId(@Param("tenderId") String tenderId);

   // Optional<VendorQuotationAgainstTender> findByTenderIdAndVendorId(String tenderId, String vendorId);
    List<VendorQuotationAgainstTender> findAllByTenderIdAndVendorId(String tenderId, String vendorId);
    @Query("""
  SELECT v FROM VendorQuotationAgainstTender v
  WHERE v.tenderId = :tenderId
    AND v.vendorId = :vendorId
    AND v.version = (
        SELECT MAX(v2.version)
        FROM VendorQuotationAgainstTender v2
        WHERE v2.tenderId = :tenderId
          AND v2.vendorId = :vendorId
    )
""")
    Optional<VendorQuotationAgainstTender> findLatestByTenderIdAndVendorId(
            @Param("tenderId") String tenderId,
            @Param("vendorId") String vendorId
    );


    // List<VendorQuotationAgainstTender> findLatestNonRejectedQuotations(String tenderId);
   @Query("SELECT v FROM VendorQuotationAgainstTender v WHERE v.tenderId = :tenderId AND v.isLatest = true AND v.status <> 'Rejected'")
   List<VendorQuotationAgainstTender> findLatestNonRejectedQuotations(@Param("tenderId") String tenderId);

   // @Query("SELECT v FROM VendorQuotationAgainstTender v WHERE v.tenderId = :tenderId AND (v.status = 'Rejected' OR v.acceptanceStatus = 'Accepted')")
   @Query("SELECT v FROM VendorQuotationAgainstTender v " +
           "WHERE v.tenderId = :tenderId " +
           "AND v.isLatest = true " +
           "AND (v.status = 'ACCEPTED' OR v.status = 'REJECTED')")
    List<VendorQuotationAgainstTender> findRejectedOrAcceptedQuotations(@Param("tenderId") String tenderId);

    List<VendorQuotationAgainstTender> findByTenderIdAndVendorId(String tenderId, String vendorId);


  Optional<VendorQuotationAgainstTender> findTopByTenderIdAndVendorIdAndIsLatestTrueOrderByVersionDesc(String tenderId, String vendorId);

    List<VendorQuotationAgainstTender> findAllByTenderIdAndVendorIdOrderByCreatedDateDesc(String tenderId, String vendorId);

    @Query("SELECT v FROM VendorQuotationAgainstTender v " +
            "WHERE v.tenderId = :tenderId " +
            "AND v.isLatest = 1 " +
            "AND v.spoStatus = 'CHANGE_REQUESTED_TO_INTENTOR'")
    List<VendorQuotationAgainstTender> findSpoChangeRequestedQuotations(@Param("tenderId") String tenderId);

    @Query("""
  SELECT v FROM VendorQuotationAgainstTender v
  WHERE v.tenderId = :tenderId
    AND v.version = (
        SELECT MAX(v2.version)
        FROM VendorQuotationAgainstTender v2
        WHERE v2.tenderId = v.tenderId
          AND v2.vendorId = v.vendorId
    )
""")
    List<VendorQuotationAgainstTender> findLatestVersionsForTender(@Param("tenderId") String tenderId);

    @Query("""
        SELECT DISTINCT v.vendorId 
        FROM VendorQuotationAgainstTender v 
        WHERE v.tenderId = :tenderId 
          AND v.isLatest = true 
          AND UPPER(v.status) = 'COMPLETED'
    """)
    List<String> findVendorIdsWithCompletedStatus(@Param("tenderId") String tenderId);
 /* @Query("""
    SELECT DISTINCT new com.astro.dto.workflow.CompletedVendorsDto(vq.vendorId, vm.vendorName)
    FROM VendorQuotationAgainstTender vq
    JOIN VendorMaster vm ON vq.vendorId = vm.vendorId
    WHERE vq.tenderId = :tenderId
      AND vq.isLatest = true
      AND UPPER(vq.status) = 'COMPLETED'
""")
  List<CompletedVendorsDto> findVendorsNameWithCompletedStatus(@Param("tenderId") String tenderId);*/
 // For VendorMaster (vendorId starts with "V")
 @Query("""
    SELECT new com.astro.dto.workflow.CompletedVendorsDto(vq.vendorId, vm.vendorName)
    FROM VendorQuotationAgainstTender vq
    JOIN VendorMaster vm ON vq.vendorId = vm.vendorId
    WHERE vq.tenderId = :tenderId
      AND vq.isLatest = true
      AND UPPER(vq.status) = 'COMPLETED'
      AND vq.vendorId LIKE 'V%'
""")
 List<CompletedVendorsDto> findVendorMasterCompleted(@Param("tenderId") String tenderId);

    // For GemVendorIdTracker (vendorId starts with "Gem")
    @Query("""
    SELECT new com.astro.dto.workflow.CompletedVendorsDto(
        gem.gemVendorId, gem.vendorName)
    FROM GemVendorIdTracker gem
    JOIN VendorQuotationAgainstTender vq ON vq.vendorId = gem.gemVendorId
    WHERE vq.tenderId = :tenderId
      AND vq.isLatest = true
      AND UPPER(vq.status) = 'COMPLETED'
      AND vq.vendorId LIKE 'Gem%'
""")
    List<CompletedVendorsDto> findGemVendorCompleted(@Param("tenderId") String tenderId);

    @Query("SELECT v.vendorId " +
            "FROM VendorQuotationAgainstTender v " +
            "WHERE v.tenderId = :tenderId " +
            "AND v.isLatest = true")
    List<String> findLatestVendorIdsByTenderId(@Param("tenderId") String tenderId);


    List<VendorQuotationAgainstTender> findAllLatestByTenderId(String tenderId);

    
    // ── Added for TenderEvaluationApprovalService ──
    List<VendorQuotationAgainstTender> findByTenderIdAndIsLatestTrue(String tenderId);

    Optional<VendorQuotationAgainstTender> findByTenderIdAndVendorIdAndIsLatestTrue(String tenderId, String vendorId);

    // After the existing findTopByTenderIdAndVendorIdAndIsLatestTrueOrderByVersionDesc method

/**
 * Returns true if any OTHER vendor for this tender still has
 * status = 'CHANGE_REQUESTED' on their latest active quotation.
 */
@Query("SELECT COUNT(v) > 0 FROM VendorQuotationAgainstTender v " +
       "WHERE v.tenderId = :tenderId " +
       "AND v.vendorId <> :excludeVendorId " +
       "AND v.isLatest = true " +
       "AND v.status = 'CHANGE_REQUESTED'")
boolean existsOtherPendingClarificationVendors(
    @Param("tenderId") String tenderId,
    @Param("excludeVendorId") String excludeVendorId
);
}
