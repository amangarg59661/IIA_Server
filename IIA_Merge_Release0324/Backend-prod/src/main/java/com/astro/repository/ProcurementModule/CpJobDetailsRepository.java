package com.astro.repository.ProcurementModule;

import com.astro.entity.ProcurementModule.CpJobDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CpJobDetailsRepository extends JpaRepository<CpJobDetails, Long> {
}