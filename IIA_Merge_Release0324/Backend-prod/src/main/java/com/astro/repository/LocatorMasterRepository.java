package com.astro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
// import com.astro.entity.LocatorMaster;
import com.astro.entity.LocatorMasterEntity;

@Repository
public interface LocatorMasterRepository extends JpaRepository<LocatorMasterEntity, Integer> {
    List<LocatorMasterEntity> findByLocationId(String locationId);
    boolean existsByLocatorId(Integer locatorId);
    Optional<LocatorMasterEntity> findByLocatorDesc(String locatorDesc);
}