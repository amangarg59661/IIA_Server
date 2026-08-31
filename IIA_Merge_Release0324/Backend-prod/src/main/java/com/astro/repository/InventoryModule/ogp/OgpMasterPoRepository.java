package com.astro.repository.InventoryModule.ogp;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;

import com.astro.entity.InventoryModule.OgpMasterPoEntity;

@Repository
public interface OgpMasterPoRepository extends JpaRepository<OgpMasterPoEntity, Integer> {
    boolean existsByPoId(String poId);

    List<OgpMasterPoEntity> findByPoId(String poId);

    List<OgpMasterPoEntity> findByStatusOrderByCreateDateAsc(String status);
List<OgpMasterPoEntity> findByUpdatedByAndUpdateDateBetween(String updatedBy, LocalDateTime start, LocalDateTime end);

    
}
