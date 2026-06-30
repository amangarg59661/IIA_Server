package com.astro.repository;

import com.astro.entity.VendorLoginDetails;
import com.astro.entity.VendorMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VendorLoginDetailsRepository extends JpaRepository<VendorLoginDetails,Long> {


    Optional<VendorLoginDetails> findFirstByVendorId(String vendorId);
}
