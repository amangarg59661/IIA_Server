package com.astro.repository.ProcurementModule;

import com.astro.entity.ProcurementModule.CpMaterials;
import org.springframework.data.jpa.repository.JpaRepository;
import com.astro.dto.dashboard.CategorySpendDto;

// import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
// import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface CpMaterialRepository extends JpaRepository<CpMaterials, Long> {


    @Query("SELECT new com.astro.dto.dashboard.CategorySpendDto(m.materialCategory, SUM(m.totalPrice)) " +
       "FROM CpMaterials m GROUP BY m.materialCategory")
List<CategorySpendDto> sumAmountGroupByCategory();
    
}
