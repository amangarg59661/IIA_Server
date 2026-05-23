package com.astro.config;

import com.astro.entity.VendorLoginDetails;
import com.astro.repository.VendorLoginDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Order(100)
public class VendorPasswordMigration implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(VendorPasswordMigration.class);

    @Autowired
    private VendorLoginDetailsRepository vendorLoginDetailsRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        List<VendorLoginDetails> allVendors = vendorLoginDetailsRepository.findAll();
        int migrated = 0;

        for (VendorLoginDetails vendor : allVendors) {
            String pwd = vendor.getPassword();
            if (pwd != null && !pwd.startsWith("$2a$") && !pwd.startsWith("$2b$")) {
                vendor.setPassword(passwordEncoder.encode(pwd));
                vendorLoginDetailsRepository.save(vendor);
                migrated++;
            }
        }

        if (migrated > 0) {
            log.info("Vendor password migration: hashed {} plain-text passwords", migrated);
        } else {
            log.info("Vendor password migration: no plain-text passwords found");
        }
    }
}
