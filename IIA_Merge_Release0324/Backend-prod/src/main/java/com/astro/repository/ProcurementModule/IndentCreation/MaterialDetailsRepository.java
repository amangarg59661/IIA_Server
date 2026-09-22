package com.astro.repository.ProcurementModule.IndentCreation;
import com.astro.dto.dashboard.TopRequestedItemDto;
import com.astro.entity.ProcurementModule.MaterialDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaterialDetailsRepository extends JpaRepository<MaterialDetails,Long> {

  //  List<MaterialDetails> findByIndentId(String indentId);
    List<MaterialDetails> findByIndentCreation_IndentId(String indentId);



    //  @Query("SELECT DISTINCT m.indentId FROM MaterialDetails m WHERE m.materialCode = :materialCode")
 // List<String> findIndentIdsByMaterialCode(@Param("materialCode") String materialCode);

    @Query("SELECT DISTINCT m.indentCreation.indentId FROM MaterialDetails m WHERE m.materialCode = :materialCode")
    List<String> findIndentIdsByMaterialCode(@Param("materialCode") String materialCode);

    @Query("SELECT m.materialSubCategory FROM MaterialDetails m WHERE m.materialCode = :materialCode")
    String findSubCategoryByMaterialCode(String materialCode);

    // new — for the Indentor dashboard's "Top Requested Items" chart
    @Query("SELECT new com.astro.dto.dashboard.TopRequestedItemDto(m.materialDescription, COUNT(m)) " +
           "FROM MaterialDetails m WHERE m.indentCreation.createdBy = :createdBy " +
           "GROUP BY m.materialDescription ORDER BY COUNT(m) DESC")
    List<TopRequestedItemDto> countGroupByMaterialForCreator(@Param("createdBy") String createdBy);


}
