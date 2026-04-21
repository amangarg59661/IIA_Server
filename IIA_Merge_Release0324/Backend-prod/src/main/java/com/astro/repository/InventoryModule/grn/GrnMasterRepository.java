package com.astro.repository.InventoryModule.grn;

import com.astro.dto.workflow.InventoryModule.paymentVoucherDto;
import com.astro.dto.workflow.InventoryModule.paymentVoucherMaterials;
import com.astro.entity.InventoryModule.GiWorkflowStatus;
import com.astro.entity.InventoryModule.GrnMasterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GrnMasterRepository extends JpaRepository<GrnMasterEntity, Integer> {
    Optional<GrnMasterEntity> findByGrnProcessId(String grnProcessId);
    List<GrnMasterEntity> findByStatusIn(List<String> statuses);

    Optional<GrnMasterEntity> findByGrnSubProcessId(Integer subProcessId);


    @Query("SELECT DISTINCT g.grnProcessId FROM GrnMasterEntity g " +
            "WHERE g.grnType = 'GI' AND g.status = 'Approved'")
    List<String> findDistinctGrnProcessIdsForGIAndApproved();

    @Query("SELECT g FROM GrnMasterEntity g WHERE g.grnProcessId = :grnProcessId")
    List<GrnMasterEntity> findByGrn(String grnProcessId);


    boolean existsByGiSubProcessId(Integer giSubProcessId);

    @Query("""
SELECT p.poId, p.vendorName, p.projectName, p.createdDate, a.materialDescription
FROM GrnMasterEntity g
JOIN PurchaseOrder p ON p.poId = CONCAT('PO', g.grnProcessId)
LEFT JOIN p.purchaseOrderAttributes a
WHERE g.grnType = 'GI' AND g.status = 'Approved'
""")
    List<Object[]> findPoDetailsForGIApproved();



    @Query("SELECT ic.indentorName FROM IndentCreation ic " +
       "WHERE ic.indentId = (SELECT i.indentId FROM IndentId i WHERE i.tenderRequest.tenderId = :tenderId)")
String findSingleIndentorNameForTender(@Param("tenderId") String tenderId);

}
