package com.astro.repository.ProcurementModule;

import com.astro.dto.workflow.ProcurementDtos.ApprovedTenderIdDtos;
import com.astro.dto.workflow.ProcurementDtos.SearchTenderIdDto;
import com.astro.entity.ProcurementModule.TenderRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TenderRequestRepository extends JpaRepository<TenderRequest, String> {
    Optional<TenderRequest> findByTenderId(String tenderId);

    @Query("SELECT MAX(t.tenderNumber) FROM TenderRequest t")
    Integer findMaxTenderNumber();

//     @Query("SELECT new com.astro.dto.workflow.ProcurementDtos.SearchTenderIdDto(t.tenderId) " +
//             "FROM TenderRequest t WHERE LOWER(t.tenderId) LIKE LOWER(CONCAT('%', :tenderId, '%'))")
//     List<SearchTenderIdDto> findTenderIdLike(@Param("tenderId") String tenderId);


//     @Query("SELECT new com.astro.dto.workflow.ProcurementDtos.SearchTenderIdDto(t.tenderId) " +
//             "FROM TenderRequest t WHERE t.createdDate BETWEEN :startDate AND :endDate")
//     List<SearchTenderIdDto> findTenderIdsBySubmittedDate(@Param("startDate") LocalDateTime startDate,
//                                                          @Param("endDate") LocalDateTime endDate);

    @Query("SELECT new com.astro.dto.workflow.ProcurementDtos.ApprovedTenderIdDtos(wt.requestId, tr.titleOfTender) " +
            "FROM WorkflowTransition wt " +
            "JOIN TenderRequest tr ON tr.tenderId = wt.requestId " +
            "WHERE wt.workflowName = 'Tender Approver Workflow' " +
            "AND wt.status = 'Completed' " +
            "AND wt.nextAction IS NULL " +
            "AND wt.requestId NOT IN (SELECT po.tenderId FROM PurchaseOrder po) " +
            "AND wt.requestId NOT IN (SELECT so.tenderId FROM ServiceOrder so)")
    List<ApprovedTenderIdDtos> findApprovedTenderIdsAndTitlesForPOANDSO();

    @Query("SELECT new com.astro.dto.workflow.ProcurementDtos.ApprovedTenderIdDtos(wt.requestId, tr.titleOfTender) " +
            "FROM WorkflowTransition wt " +
            "JOIN TenderRequest tr ON tr.tenderId = wt.requestId " +
            "WHERE wt.workflowName = 'Tender Approver Workflow' " +
            "AND wt.status = 'Completed' " +
            "AND wt.nextAction IS NULL " +
           // "AND wt.requestId NOT IN (SELECT po.tenderId FROM PurchaseOrder po) " +
           // "AND wt.requestId NOT IN (SELECT so.tenderId FROM ServiceOrder so) " +
            "AND tr.modeOfProcurement IN ('Gem','CPPP')")   // filter applied
    List<ApprovedTenderIdDtos> findApprovedTenderIdsForGemAndTitlesForPOANDSO();


    List<TenderRequest> findByTenderIdIn(List<String> tenderIds);

    @Query("SELECT tr.modeOfProcurement FROM TenderRequest tr WHERE tr.tenderId = :tenderId")
    String findModeOfProcurementByTenderId(@Param("tenderId") String tenderId);

    // All versions of a tender family
@Query("SELECT t FROM TenderRequest t WHERE (t.tenderId = :baseId OR t.tenderId LIKE CONCAT(:baseId, '/%')) ORDER BY t.tenderVersion DESC")
List<TenderRequest> findAllVersionsByBaseId(@Param("baseId") String baseId);

// Current active version
@Query("SELECT t FROM TenderRequest t WHERE (t.tenderId = :baseId OR t.tenderId LIKE CONCAT(:baseId, '/%')) AND t.isActive = true")
Optional<TenderRequest> findActiveVersionByBaseId(@Param("baseId") String baseId);

// Update existing search queries to filter isActive = true:
@Query("SELECT new com.astro.dto.workflow.ProcurementDtos.SearchTenderIdDto(t.tenderId) " +
        "FROM TenderRequest t WHERE LOWER(t.tenderId) LIKE LOWER(CONCAT('%', :tenderId, '%')) AND t.isActive = true")
List<SearchTenderIdDto> findTenderIdLike(@Param("tenderId") String tenderId);

@Query("SELECT new com.astro.dto.workflow.ProcurementDtos.SearchTenderIdDto(t.tenderId) " +
        "FROM TenderRequest t WHERE t.createdDate BETWEEN :startDate AND :endDate AND t.isActive = true")
List<SearchTenderIdDto> findTenderIdsBySubmittedDate(@Param("startDate") LocalDateTime startDate,
                                                      @Param("endDate") LocalDateTime endDate);

    //  TenderRequest getByTenderId(String tenderId);
}
