package com.astro.repository;

import com.astro.entity.AuditTrail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditTrailRepository extends JpaRepository<AuditTrail, Long> {

    List<AuditTrail> findByEntityNameAndEntityIdOrderByChangedAtDesc(
            String entityName, String entityId);
}
