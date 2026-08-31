package com.astro.repository.InventoryModule.igp;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.astro.entity.InventoryModule.IgpMaterialMasterEntity;
import java.time.LocalDateTime;

public interface IgpMaterialMasterRepository extends JpaRepository<IgpMaterialMasterEntity, Long> {
    List<IgpMaterialMasterEntity> findByStatus(String status);
    List<IgpMaterialMasterEntity> findByStatusOrderByCreateDateAsc(String status);
List<IgpMaterialMasterEntity> findByUpdatedByAndUpdateDateBetween(String updatedBy, LocalDateTime start, LocalDateTime end);

}
