package com.astro.repository.ProcurementModule;

import com.astro.dto.workflow.ApprovedIndentsDto;
import com.astro.dto.workflow.ProcurementDtos.IndentDto.SearchIndentIdDto;
import com.astro.dto.workflow.ProcurementDtos.SearchTenderIdDto;
import com.astro.entity.ProcurementModule.IndentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IndentIdRepository extends JpaRepository<IndentId,Long> {
    @Query("SELECT i.indentId FROM IndentId i WHERE i.tenderRequest.tenderId = :tenderId")
 List<String> findTenderWithIndent(@Param("tenderId") String tenderId);


    @Query("SELECT DISTINCT new com.astro.dto.workflow.ProcurementDtos.SearchTenderIdDto(i.tenderRequest.tenderId) " +
            "FROM IndentId i WHERE i.indentId IN :indentIds AND i.tenderRequest IS NOT NULL")
    List<SearchTenderIdDto> findTenderIdsByIndentIds(@Param("indentIds") List<String> indentIds);

    List<IndentId> findByIndentIdIn(List<String> indentIds);

    Optional<IndentId> findByIndentId(String requestId);

    boolean existsByIndentId(String indentId);

    //  List<String> findIndentIdsByTenderId(String tenderId);
}
