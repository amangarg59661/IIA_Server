-- ============================================================
-- Tender Evaluation Migration: add workflow fields
-- Run ONCE against an existing DB that has the base schema.
-- Safe to re-run (uses IF NOT EXISTS / column existence checks).
-- ============================================================

-- ── 1. tender_evaluation: add all new workflow fields ──────────────────────

ALTER TABLE `tender_evaluation`
  ADD COLUMN IF NOT EXISTS `evaluation_status`                 VARCHAR(50)           DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `bid_type`                          VARCHAR(20)           DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `amount_category`                   VARCHAR(30)           DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `indent_category`                   VARCHAR(20)           DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `approved_vendor_id`                VARCHAR(100)          DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `approved_vendor_name`              VARCHAR(300)          DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `approval_remarks`                  VARCHAR(1000)         DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `vendor_portal_registered`          TINYINT(1)            DEFAULT 0,
  ADD COLUMN IF NOT EXISTS `total_tender_value`                DECIMAL(18, 2)        DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `clarification_pending_from`        VARCHAR(50)           DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `clarification_pending_from_id`     INT                   DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `clarification_pending_from_name`   VARCHAR(200)          DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `clarification_requested_by_role`   VARCHAR(50)           DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `clarification_remarks`             TEXT                  DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `previous_evaluation_status`        VARCHAR(50)           DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `financial_bid_phase`               TINYINT(1)            DEFAULT 0,
  ADD COLUMN IF NOT EXISTS `ad_hoc_chairman_user_id`           INT                   DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `ad_hoc_chairman_name`              VARCHAR(200)          DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `ad_hoc_co_chairman_user_id`        INT                   DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `ad_hoc_co_chairman_name`           VARCHAR(200)          DEFAULT NULL;

-- ── 2. Vendor_quotation_against_tender: add workflow fields ────────────────

ALTER TABLE `Vendor_quotation_against_tender`
  ADD COLUMN IF NOT EXISTS `indentor_status`            VARCHAR(30)    DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `indentor_remarks`           VARCHAR(1000)  DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `spo_status`                 VARCHAR(30)    DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `spo_remarks`                VARCHAR(1000)  DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `change_request_to_indentor` TINYINT(1)     DEFAULT 0,
  ADD COLUMN IF NOT EXISTS `modified_by`                INT            DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `current_role`               VARCHAR(30)    DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `next_role`                  VARCHAR(30)    DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `clarification_file_name`    VARCHAR(500)   DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `vendor_response`            TEXT           DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `technical_status`           VARCHAR(20)    DEFAULT 'PENDING',
  ADD COLUMN IF NOT EXISTS `technical_remarks`          VARCHAR(1000)  DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `technical_evaluated_by`     INT            DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `technical_evaluated_date`   DATETIME       DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `financial_bid_visible`      TINYINT(1)     DEFAULT 0,
  ADD COLUMN IF NOT EXISTS `rank`                       INT            DEFAULT NULL;

-- ── 3. tender_clarification_history: create if not exists ─────────────────

CREATE TABLE IF NOT EXISTS `tender_clarification_history` (
  `id`                   BIGINT        NOT NULL AUTO_INCREMENT,
  `tender_id`            VARCHAR(255)  NOT NULL,
  `round_number`         INT           NOT NULL DEFAULT 1,
  `requested_by_role`    VARCHAR(50)   DEFAULT NULL,
  `requested_by_user_id` INT           DEFAULT NULL,
  `clarification_target` VARCHAR(50)   DEFAULT NULL,
  `target_vendor_id`     VARCHAR(100)  DEFAULT NULL,
  `target_user_id`       INT           DEFAULT NULL,
  `target_user_name`     VARCHAR(200)  DEFAULT NULL,
  `question_remarks`     TEXT          DEFAULT NULL,
  `response_text`        TEXT          DEFAULT NULL,
  `response_file_name`   VARCHAR(500)  DEFAULT NULL,
  `responded_by_role`    VARCHAR(50)   DEFAULT NULL,
  `responded_by_id`      VARCHAR(100)  DEFAULT NULL,
  `requested_at`         DATETIME      DEFAULT CURRENT_TIMESTAMP,
  `responded_at`         DATETIME      DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_tch_tender_id` (`tender_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ── 4. techno_financial_committee: create if not exists ───────────────────

CREATE TABLE IF NOT EXISTS `techno_financial_committee` (
  `id`             BIGINT        NOT NULL AUTO_INCREMENT,
  `committee_type` VARCHAR(20)   NOT NULL COMMENT 'STEC_I or STEC_II',
  `user_id`        INT           NOT NULL,
  `member_name`    VARCHAR(200)  DEFAULT NULL,
  `role`           VARCHAR(50)   DEFAULT NULL COMMENT 'CHAIRMAN, MEMBER',
  `is_active`      TINYINT(1)    DEFAULT 1,
  `created_date`   DATETIME      DEFAULT CURRENT_TIMESTAMP,
  `updated_date`   DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_tfc_type_active` (`committee_type`, `is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ── 5. tender_committee_decision: create if not exists ────────────────────

CREATE TABLE IF NOT EXISTS `tender_committee_decision` (
  `id`                      BIGINT        NOT NULL AUTO_INCREMENT,
  `tender_id`               VARCHAR(255)  NOT NULL,
  `committee_user_id`       INT           DEFAULT NULL,
  `committee_member_name`   VARCHAR(200)  DEFAULT NULL,
  `vote`                    VARCHAR(20)   DEFAULT NULL,
  `vote_remarks`            VARCHAR(1000) DEFAULT NULL,
  `voted_date`              DATETIME      DEFAULT NULL,
  `expert_user_id`          INT           DEFAULT NULL,
  `expert_name`             VARCHAR(200)  DEFAULT NULL,
  `expert_assigned_date`    DATETIME      DEFAULT NULL,
  `chairman_decision`       VARCHAR(20)   DEFAULT NULL,
  `chairman_remarks`        VARCHAR(1000) DEFAULT NULL,
  `chairman_override_used`  TINYINT(1)    DEFAULT 0,
  `chairman_decision_date`  DATETIME      DEFAULT NULL,
  `director_decision`       VARCHAR(20)   DEFAULT NULL,
  `director_remarks`        VARCHAR(1000) DEFAULT NULL,
  `director_decision_date`  DATETIME      DEFAULT NULL,
  `director_user_id`        INT           DEFAULT NULL,
  `created_date`            DATETIME      DEFAULT CURRENT_TIMESTAMP,
  `updated_date`            DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_tcd_tender_user` (`tender_id`, `committee_user_id`),
  KEY `idx_tcd_tender_id` (`tender_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ── 6. gem_tender_evaluation: GeM/Open/Global manual vendor upload ─────────

CREATE TABLE IF NOT EXISTS `gem_tender_evaluation` (
  `id`                      BIGINT        NOT NULL AUTO_INCREMENT,
  `tender_id`               VARCHAR(255)  NOT NULL,
  `vendor_name`             VARCHAR(300)  NOT NULL,
  `vendor_id`               VARCHAR(100)  DEFAULT NULL,
  `technical_doc_file_name` VARCHAR(500)  DEFAULT NULL,
  `financial_doc_file_name` VARCHAR(500)  DEFAULT NULL,
  `added_by_user_id`        INT           DEFAULT NULL,
  `status`                  VARCHAR(30)   DEFAULT 'PENDING',
  `remarks`                 VARCHAR(1000) DEFAULT NULL,
  `sent_for_evaluation`     TINYINT(1)    DEFAULT 0,
  `sent_at`                 DATETIME      DEFAULT NULL,
  `created_date`            DATETIME      DEFAULT CURRENT_TIMESTAMP,
  `updated_date`            DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_gte_tender_id` (`tender_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
