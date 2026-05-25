-- ============================================================
-- 1. AUDIT TRAIL TABLE (MySQL / InnoDB)
-- ============================================================

CREATE TABLE IF NOT EXISTS `audit_trail` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT,
    `entity_name`  VARCHAR(100) NOT NULL,
    `entity_id`    VARCHAR(100) NOT NULL,
    `action`       VARCHAR(10)  NOT NULL,
    `changed_by`   VARCHAR(50)  DEFAULT NULL,
    `changed_at`   DATETIME     NOT NULL,
    `changes_json` TEXT         DEFAULT NULL,
    PRIMARY KEY (`id`),
    INDEX `idx_audit_entity`     (`entity_name`, `entity_id`),
    INDEX `idx_audit_changed_by` (`changed_by`),
    INDEX `idx_audit_changed_at` (`changed_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- 2. FIX: created_by / modified_by columns (INT → VARCHAR)
--    These columns store vendor IDs like 'COMP008', 'FURN006'
--    which cannot fit in INT columns.
-- ============================================================

-- Vendor_quotation_against_tender (confirmed failing with COMP003)
ALTER TABLE `Vendor_quotation_against_tender`
    MODIFY COLUMN `created_by`  VARCHAR(50) DEFAULT NULL,
    MODIFY COLUMN `modified_by` VARCHAR(50) DEFAULT NULL;

-- vendor_master
ALTER TABLE `vendor_master`
    MODIFY COLUMN `created_by`  VARCHAR(50) DEFAULT NULL,
    MODIFY COLUMN `updated_by`  VARCHAR(50) DEFAULT NULL;

-- vendor_master_util
ALTER TABLE `vendor_master_util`
    MODIFY COLUMN `created_by`  VARCHAR(50) DEFAULT NULL,
    MODIFY COLUMN `updated_by`  VARCHAR(50) DEFAULT NULL;

-- vendor_login_details
ALTER TABLE `vendor_login_details`
    MODIFY COLUMN `created_by`  VARCHAR(50) DEFAULT NULL,
    MODIFY COLUMN `updated_by`  VARCHAR(50) DEFAULT NULL;

-- vendor_names_for_job_work_material
ALTER TABLE `vendor_names_for_job_work_material`
    MODIFY COLUMN `created_by`  VARCHAR(50) DEFAULT NULL,
    MODIFY COLUMN `updated_by`  VARCHAR(50) DEFAULT NULL;
