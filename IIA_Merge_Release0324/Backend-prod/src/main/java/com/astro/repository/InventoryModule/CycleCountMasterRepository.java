package com.astro.repository.InventoryModule;
import com.astro.entity.InventoryModule.CycleCountMasterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CycleCountMasterRepository extends JpaRepository<CycleCountMasterEntity, Long> {
    List<CycleCountMasterEntity> findByStatus(String status);

    @Query("SELECT c FROM CycleCountMasterEntity c WHERE " +
           "UPPER(CONCAT('CC/', CAST(c.id AS string))) LIKE UPPER(CONCAT('%', :value, '%')) OR " +
           "CAST(c.locatorId AS string) LIKE CONCAT('%', :value, '%') OR " +
           "UPPER(c.status) LIKE UPPER(CONCAT('%', :value, '%'))")
    List<CycleCountMasterEntity> searchByIdLocatorOrStatus(@Param("value") String value);
}