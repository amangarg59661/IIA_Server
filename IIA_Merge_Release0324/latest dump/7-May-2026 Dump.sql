CREATE DATABASE  IF NOT EXISTS `astrodatabase` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `astrodatabase`;
-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: astrodatabase
-- ------------------------------------------------------
-- Server version	8.4.8

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `action_master`
--

DROP TABLE IF EXISTS `action_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `action_master` (
  `actionId` int NOT NULL AUTO_INCREMENT,
  `actionName` varchar(255) NOT NULL,
  `createdDate` datetime DEFAULT NULL,
  `createdBy` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`actionId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `admin_audit_log`
--

DROP TABLE IF EXISTS `admin_audit_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin_audit_log` (
  `audit_id` bigint NOT NULL AUTO_INCREMENT,
  `entity_type` varchar(100) NOT NULL COMMENT 'Employee, User, Project, Budget, etc.',
  `entity_id` varchar(100) NOT NULL,
  `action` varchar(50) NOT NULL COMMENT 'CREATE, UPDATE, DELETE, ACTIVATE, DEACTIVATE',
  `old_value` json DEFAULT NULL,
  `new_value` json DEFAULT NULL,
  `changed_by` varchar(100) NOT NULL,
  `changed_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `ip_address` varchar(50) DEFAULT NULL,
  `user_agent` text,
  PRIMARY KEY (`audit_id`),
  KEY `idx_audit_entity` (`entity_type`,`entity_id`),
  KEY `idx_audit_date` (`changed_date`),
  KEY `idx_audit_user` (`changed_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `approval_limit_master`
--

DROP TABLE IF EXISTS `approval_limit_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `approval_limit_master` (
  `limit_id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` int DEFAULT NULL,
  `role_name` varchar(100) NOT NULL,
  `category` varchar(50) DEFAULT NULL,
  `department_name` varchar(100) DEFAULT NULL,
  `location` varchar(100) DEFAULT NULL,
  `min_amount` decimal(15,2) DEFAULT '0.00',
  `max_amount` decimal(15,2) DEFAULT NULL,
  `escalation_role_id` int DEFAULT NULL,
  `escalation_role_name` varchar(100) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `priority` int DEFAULT '0',
  `created_by` varchar(100) DEFAULT NULL,
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_by` varchar(100) DEFAULT NULL,
  `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`limit_id`),
  KEY `idx_approval_limit_role_name` (`role_name`),
  KEY `idx_approval_limit_category` (`category`),
  KEY `idx_approval_limit_active` (`is_active`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `approver_master`
--

DROP TABLE IF EXISTS `approver_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `approver_master` (
  `approver_id` bigint NOT NULL AUTO_INCREMENT,
  `approver_code` varchar(50) NOT NULL,
  `workflow_id` int NOT NULL,
  `branch_id` bigint NOT NULL,
  `role_id` int NOT NULL,
  `role_name` varchar(100) NOT NULL,
  `approval_level` int NOT NULL DEFAULT '1' COMMENT 'Level 1, Level 2, Level 3, etc.',
  `approval_sequence` int NOT NULL DEFAULT '1' COMMENT 'Order within the same level',
  `is_parallel_approval` tinyint(1) DEFAULT '0' COMMENT 'If true, any one approver at this level can approve (OR logic)',
  `is_mandatory` tinyint(1) DEFAULT '1' COMMENT 'If false, this approval step can be skipped',
  `status` varchar(50) DEFAULT 'Active' COMMENT 'Active, Inactive',
  `created_by` varchar(100) DEFAULT NULL,
  `updated_by` varchar(100) DEFAULT NULL,
  `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `condition_check_type` varchar(50) DEFAULT NULL,
  `limit_check_config` json DEFAULT NULL,
  `skip_if_condition` json DEFAULT NULL,
  `escalate_if_condition` json DEFAULT NULL,
  `escalation_approver_id` bigint DEFAULT NULL,
  `auto_approve_hours` int DEFAULT NULL,
  PRIMARY KEY (`approver_id`),
  UNIQUE KEY `approver_code` (`approver_code`),
  KEY `idx_approver_workflow` (`workflow_id`),
  KEY `idx_approver_branch` (`branch_id`),
  KEY `idx_approver_role` (`role_id`),
  KEY `idx_approver_status` (`status`),
  KEY `idx_approver_level_seq` (`approval_level`,`approval_sequence`),
  CONSTRAINT `fk_approver_branch` FOREIGN KEY (`branch_id`) REFERENCES `workflow_branch_master` (`branch_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_approver_role` FOREIGN KEY (`role_id`) REFERENCES `role_master` (`roleId`) ON DELETE RESTRICT,
  CONSTRAINT `fk_approver_workflow` FOREIGN KEY (`workflow_id`) REFERENCES `workflow_master` (`workflowId`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=707 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `asset`
--

DROP TABLE IF EXISTS `asset`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `asset` (
  `asset_code` varchar(255) NOT NULL,
  `material_code` varchar(255) DEFAULT NULL,
  `description` text,
  `uom` varchar(50) DEFAULT NULL,
  `make_no` varchar(100) DEFAULT NULL,
  `model_no` varchar(100) DEFAULT NULL,
  `serial_no` varchar(100) DEFAULT NULL,
  `component_name` varchar(255) DEFAULT NULL,
  `component_code` varchar(255) DEFAULT NULL,
  `quantity` int DEFAULT NULL,
  `locator` varchar(255) DEFAULT NULL,
  `transaction_history` text,
  `current_condition` varchar(50) DEFAULT NULL,
  `updated_by` varchar(200) DEFAULT NULL,
  `created_by` varchar(200) DEFAULT NULL,
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`asset_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `asset_disposal`
--

DROP TABLE IF EXISTS `asset_disposal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `asset_disposal` (
  `disposal_id` int NOT NULL AUTO_INCREMENT,
  `disposal_date` date NOT NULL,
  `created_by` int NOT NULL,
  `create_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `location_id` varchar(10) NOT NULL,
  `vendor_id` varchar(50) DEFAULT NULL,
  `custodian_id` varchar(50) DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  `action` varchar(50) DEFAULT NULL,
  `auction_id` varchar(50) DEFAULT NULL,
  `auction_date` date DEFAULT NULL,
  `reserve_price` decimal(18,2) DEFAULT NULL,
  `auction_price` decimal(18,2) DEFAULT NULL,
  `vendor_name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`disposal_id`),
  KEY `location_id` (`location_id`),
  CONSTRAINT `asset_disposal_ibfk_1` FOREIGN KEY (`location_id`) REFERENCES `location_master` (`location_code`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `asset_disposal_auction`
--

DROP TABLE IF EXISTS `asset_disposal_auction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `asset_disposal_auction` (
  `auction_id` int NOT NULL AUTO_INCREMENT,
  `auction_code` varchar(50) NOT NULL,
  `auction_date` date NOT NULL,
  `reserve_price` decimal(18,2) DEFAULT NULL,
  `auction_price` decimal(18,2) DEFAULT NULL,
  `vendor_name` varchar(100) DEFAULT NULL,
  `created_by` int NOT NULL,
  `created_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`auction_id`),
  UNIQUE KEY `auction_code` (`auction_code`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `asset_disposal_auction_detail`
--

DROP TABLE IF EXISTS `asset_disposal_auction_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `asset_disposal_auction_detail` (
  `auction_detail_id` int NOT NULL AUTO_INCREMENT,
  `auction_id` int NOT NULL,
  `disposal_id` int NOT NULL,
  PRIMARY KEY (`auction_detail_id`),
  KEY `auction_id_idx` (`auction_id`),
  KEY `disposal_id_idx` (`disposal_id`),
  CONSTRAINT `fk_auction_detail_auction` FOREIGN KEY (`auction_id`) REFERENCES `asset_disposal_auction` (`auction_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_auction_detail_disposal` FOREIGN KEY (`disposal_id`) REFERENCES `asset_disposal` (`disposal_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `asset_disposal_detail`
--

DROP TABLE IF EXISTS `asset_disposal_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `asset_disposal_detail` (
  `disposal_detail_id` int NOT NULL AUTO_INCREMENT,
  `disposal_id` int NOT NULL,
  `asset_id` int NOT NULL,
  `asset_desc` varchar(50) NOT NULL,
  `disposal_quantity` decimal(10,2) NOT NULL,
  `disposal_category` varchar(50) NOT NULL,
  `disposal_mode` varchar(50) NOT NULL,
  `sales_note_filename` varchar(255) DEFAULT NULL,
  `ohq_id` int DEFAULT NULL,
  `locator_id` int DEFAULT NULL,
  `book_value` decimal(18,2) DEFAULT NULL,
  `depriciation_rate` decimal(18,2) DEFAULT NULL,
  `unit_price` decimal(18,2) DEFAULT NULL,
  `custodian_id` varchar(50) DEFAULT NULL,
  `po_value` decimal(18,2) DEFAULT NULL,
  `reason_for_disposal` varchar(150) DEFAULT NULL,
  `po_id` varchar(50) DEFAULT NULL,
  `po_date` date DEFAULT NULL,
  `serial_no` varchar(50) DEFAULT NULL,
  `model_no` varchar(50) DEFAULT NULL,
  `asset_code` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`disposal_detail_id`),
  KEY `disposal_id` (`disposal_id`),
  CONSTRAINT `asset_disposal_detail_ibfk_1` FOREIGN KEY (`disposal_id`) REFERENCES `asset_disposal` (`disposal_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `asset_master`
--

DROP TABLE IF EXISTS `asset_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `asset_master` (
  `asset_id` int NOT NULL AUTO_INCREMENT,
  `material_code` varchar(50) NOT NULL,
  `material_desc` varchar(50) NOT NULL,
  `asset_desc` varchar(50) NOT NULL,
  `make_no` varchar(50) DEFAULT NULL,
  `serial_no` varchar(50) DEFAULT NULL,
  `model_no` varchar(50) DEFAULT NULL,
  `init_quantity` decimal(10,2) DEFAULT NULL,
  `uom_id` varchar(10) NOT NULL,
  `component_name` varchar(50) DEFAULT NULL,
  `component_id` int DEFAULT NULL,
  `create_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` int NOT NULL,
  `updated_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `updated_by` int DEFAULT NULL,
  `unit_price` decimal(10,2) DEFAULT NULL,
  `depriciation_rate` decimal(10,2) DEFAULT NULL,
  `end_of_life` date DEFAULT NULL,
  `stock_levels` decimal(10,2) DEFAULT NULL,
  `condition_of_goods` varchar(100) DEFAULT NULL,
  `shelf_life` varchar(50) DEFAULT NULL,
  `po_id` varchar(50) DEFAULT NULL,
  `locator` decimal(10,2) DEFAULT NULL,
  `locator_id` varchar(20) DEFAULT NULL,
  `igp_id` bigint DEFAULT NULL,
  `asset_code` varchar(200) DEFAULT NULL,
  `grn_no` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`asset_id`),
  KEY `idx_material_code` (`material_code`),
  KEY `idx_uom` (`uom_id`),
  KEY `idx_material_desc` (`material_desc`),
  CONSTRAINT `asset_master_ibfk_1` FOREIGN KEY (`material_code`) REFERENCES `material_master` (`material_code`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=86 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `asset_serial`
--

DROP TABLE IF EXISTS `asset_serial`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `asset_serial` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `asset_id` int NOT NULL,
  `asset_code` varchar(100) DEFAULT NULL,
  `serial_no` varchar(100) NOT NULL,
  `custodian_id` varchar(100) DEFAULT NULL,
  `locator_id` int DEFAULT NULL,
  `po_id` varchar(100) DEFAULT NULL,
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `status` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_asset_serial_asset` (`asset_id`),
  CONSTRAINT `fk_asset_serial_asset` FOREIGN KEY (`asset_id`) REFERENCES `asset_master` (`asset_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=67 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `budget_category_master`
--

DROP TABLE IF EXISTS `budget_category_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `budget_category_master` (
  `category_id` bigint NOT NULL AUTO_INCREMENT,
  `category_name` varchar(100) NOT NULL,
  `description` text,
  `is_active` tinyint(1) DEFAULT '1',
  `display_order` int DEFAULT '0',
  `created_by` varchar(100) DEFAULT NULL,
  `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`category_id`),
  UNIQUE KEY `category_name` (`category_name`),
  KEY `idx_category_active` (`is_active`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `budget_ledger`
--

DROP TABLE IF EXISTS `budget_ledger`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `budget_ledger` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `budget_code` varchar(255) NOT NULL,
  `reference_id` varchar(255) NOT NULL,
  `reference_type` varchar(100) NOT NULL,
  `hold_amount` decimal(15,2) NOT NULL DEFAULT '0.00',
  `spent_amount` decimal(15,2) NOT NULL DEFAULT '0.00',
  `status` varchar(50) NOT NULL DEFAULT 'ACTIVE_HOLD',
  `created_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `budget_master`
--

DROP TABLE IF EXISTS `budget_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `budget_master` (
  `budget_id` bigint NOT NULL AUTO_INCREMENT,
  `budget_code` varchar(50) NOT NULL,
  `budget_name` varchar(200) NOT NULL,
  `category` varchar(100) DEFAULT NULL,
  `allocated_amount` decimal(15,2) NOT NULL DEFAULT '0.00',
  `on_hold_amount` decimal(15,2) DEFAULT '0.00' COMMENT 'Amount reserved by POs',
  `spent_amount` decimal(15,2) DEFAULT '0.00' COMMENT 'Amount deducted by GRNs',
  `remaining_amount` decimal(15,2) GENERATED ALWAYS AS (((`allocated_amount` - `on_hold_amount`) - `spent_amount`)) STORED,
  `fiscal_year` varchar(10) NOT NULL,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `status` varchar(50) DEFAULT 'Active' COMMENT 'Active, Closed, Exhausted',
  `project_code` varchar(50) DEFAULT NULL,
  `department_name` varchar(100) DEFAULT NULL,
  `created_by` varchar(100) DEFAULT NULL,
  `updated_by` varchar(100) DEFAULT NULL,
  `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`budget_id`),
  UNIQUE KEY `budget_code` (`budget_code`),
  KEY `fk_budget_project` (`project_code`),
  KEY `idx_budget_code` (`budget_code`),
  KEY `idx_budget_status` (`status`),
  KEY `idx_budget_fiscal_year` (`fiscal_year`),
  KEY `idx_budget_category` (`category`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contigency_purchase`
--

DROP TABLE IF EXISTS `contigency_purchase`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contigency_purchase` (
  `contigency_id` varchar(255) NOT NULL,
  `vendors_name` varchar(255) DEFAULT NULL,
  `vendors_invoice_no` varchar(255) DEFAULT NULL,
  `Date` date DEFAULT NULL,
  `remarks_for_purchase` varchar(255) DEFAULT NULL,
  `upload_copy_of_invoice` blob,
  `predifined_purchase_statement` varchar(255) DEFAULT NULL,
  `project_detail` varchar(255) DEFAULT NULL,
  `created_by` int DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `upload_copy_of_invoice_file_name` varchar(255) DEFAULT NULL,
  `project_name` varchar(200) DEFAULT NULL,
  `file_type` varchar(255) DEFAULT NULL,
  `cp_number` int DEFAULT NULL,
  `payment_to` varchar(200) DEFAULT NULL,
  `payment_to_vendor` varchar(200) DEFAULT NULL,
  `payment_to_employee` varchar(200) DEFAULT NULL,
  `purpose` varchar(255) DEFAULT NULL,
  `declaration_one` tinyint(1) DEFAULT NULL,
  `declaration_two` tinyint(1) DEFAULT NULL,
  `total_cp_value` decimal(15,2) DEFAULT NULL,
  `current_status` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`contigency_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cp_materials`
--

DROP TABLE IF EXISTS `cp_materials`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cp_materials` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `material_code` varchar(100) NOT NULL,
  `material_description` varchar(1000) DEFAULT NULL,
  `quantity` decimal(15,2) DEFAULT NULL,
  `unit_price` decimal(15,2) DEFAULT NULL,
  `uom` varchar(50) DEFAULT NULL,
  `total_price` decimal(15,2) DEFAULT NULL,
  `budget_code` varchar(100) DEFAULT NULL,
  `material_category` varchar(255) DEFAULT NULL,
  `material_sub_category` varchar(255) DEFAULT NULL,
  `currency` varchar(50) DEFAULT NULL,
  `contigency_id` varchar(255) DEFAULT NULL,
  `gst` decimal(10,2) DEFAULT NULL,
  `country_of_origin` varchar(100) DEFAULT NULL,
  `current_status` varchar(45) DEFAULT 'DRAFT',
  PRIMARY KEY (`id`),
  KEY `contigency_id` (`contigency_id`),
  CONSTRAINT `cp_materials_ibfk_1` FOREIGN KEY (`contigency_id`) REFERENCES `contigency_purchase` (`contigency_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `demand_and_issue_dtl`
--

DROP TABLE IF EXISTS `demand_and_issue_dtl`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `demand_and_issue_dtl` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `di_id` bigint DEFAULT NULL,
  `asset_id` int DEFAULT NULL,
  `asset_desc` varchar(500) DEFAULT NULL,
  `material_code` varchar(100) DEFAULT NULL,
  `material_desc` varchar(500) DEFAULT NULL,
  `quantity` decimal(18,2) NOT NULL,
  `receiver_locator_id` int DEFAULT NULL,
  `sender_locator_id` int DEFAULT NULL,
  `unit_price` decimal(18,2) DEFAULT NULL,
  `depriciation_rate` decimal(18,2) DEFAULT NULL,
  `book_value` decimal(18,2) DEFAULT NULL,
  `uom` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_di_id` (`di_id`),
  CONSTRAINT `fk_di_id` FOREIGN KEY (`di_id`) REFERENCES `demand_and_issue_master` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `demand_and_issue_master`
--

DROP TABLE IF EXISTS `demand_and_issue_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `demand_and_issue_master` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `sender_location_id` varchar(255) NOT NULL,
  `status` varchar(255) DEFAULT NULL,
  `sender_custodian_id` int NOT NULL,
  `create_date` datetime NOT NULL,
  `di_date` date NOT NULL,
  `created_by` int NOT NULL,
  `issue_date` date DEFAULT NULL,
  `issued_by` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `department_approver_mapping`
--

DROP TABLE IF EXISTS `department_approver_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `department_approver_mapping` (
  `mapping_id` bigint NOT NULL AUTO_INCREMENT,
  `department_name` varchar(100) NOT NULL,
  `approver_type` varchar(50) NOT NULL,
  `approver_employee_id` varchar(50) DEFAULT NULL,
  `approver_role_id` int DEFAULT NULL,
  `approval_limit` decimal(15,2) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_by` varchar(100) DEFAULT NULL,
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_by` varchar(100) DEFAULT NULL,
  `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`mapping_id`),
  UNIQUE KEY `uk_dept_approver_type` (`department_name`,`approver_type`),
  UNIQUE KEY `UK4c0em5n37pvd3bqyejdeo1qt5` (`department_name`,`approver_type`),
  KEY `idx_dept_mapping_dept` (`department_name`),
  KEY `idx_dept_mapping_type` (`approver_type`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `department_computer_price_limit`
--

DROP TABLE IF EXISTS `department_computer_price_limit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `department_computer_price_limit` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `department_name` varchar(255) NOT NULL,
  `price_limit` decimal(19,2) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `created_by` int DEFAULT NULL,
  `updated_by` int DEFAULT NULL,
  `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `remarks` varchar(500) DEFAULT NULL,
  `department_code` varchar(200) NOT NULL,
  PRIMARY KEY (`id`,`department_code`),
  UNIQUE KEY `department_name` (`department_name`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `department_master`
--

DROP TABLE IF EXISTS `department_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `department_master` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `department_name` varchar(100) NOT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `department_name` (`department_name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `designation_master`
--

DROP TABLE IF EXISTS `designation_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `designation_master` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `designation_name` varchar(100) NOT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `designation_name` (`designation_name`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `designator_master`
--

DROP TABLE IF EXISTS `designator_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `designator_master` (
  `designator_id` bigint NOT NULL AUTO_INCREMENT,
  `form_id` bigint NOT NULL,
  `designator_name` varchar(100) NOT NULL COMMENT 'e.g., status, category, priority',
  `designator_display_name` varchar(200) NOT NULL,
  `designator_description` text,
  `data_type` varchar(50) DEFAULT 'STRING' COMMENT 'STRING, NUMBER, DATE, BOOLEAN',
  `is_active` tinyint(1) DEFAULT '1',
  `display_order` int DEFAULT '0',
  `created_by` varchar(100) DEFAULT NULL,
  `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`designator_id`),
  UNIQUE KEY `uk_form_designator` (`form_id`,`designator_name`),
  UNIQUE KEY `UKe6x4csjeihq8gfv5outpa14kw` (`form_id`,`designator_name`),
  KEY `idx_designator_active` (`is_active`),
  CONSTRAINT `fk_designator_form` FOREIGN KEY (`form_id`) REFERENCES `form_master` (`form_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `employee_department_master`
--

DROP TABLE IF EXISTS `employee_department_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employee_department_master` (
  `employee_id` varchar(100) NOT NULL,
  `first_name` varchar(100) DEFAULT NULL,
  `last_name` varchar(100) DEFAULT NULL,
  `employee_name` varchar(100) DEFAULT NULL,
  `department_name` varchar(100) DEFAULT NULL,
  `designation` varchar(50) DEFAULT NULL,
  `reporting_officer_id` varchar(50) DEFAULT NULL,
  `reporting_officer_name` varchar(150) DEFAULT NULL,
  `employment_type` varchar(50) DEFAULT NULL,
  `hire_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL COMMENT 'Resignation/Termination Date',
  `contact_details` varchar(100) DEFAULT NULL,
  `updated_by` varchar(200) DEFAULT NULL,
  `created_by` varchar(200) DEFAULT NULL,
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `location` varchar(255) DEFAULT NULL,
  `phone_number` varchar(10) DEFAULT NULL,
  `email_address` varchar(255) DEFAULT NULL,
  `date_of_birth` date DEFAULT NULL,
  `address` text,
  `street_address` varchar(255) DEFAULT NULL,
  `city` varchar(100) DEFAULT NULL,
  `state` varchar(100) DEFAULT NULL,
  `pin_code` varchar(20) DEFAULT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'Active',
  `is_draft` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `employee_id_sequence`
--

DROP TABLE IF EXISTS `employee_id_sequence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employee_id_sequence` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `employee_id` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=77 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `field_station_approver_master`
--

DROP TABLE IF EXISTS `field_station_approver_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `field_station_approver_master` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `field_station_name` varchar(100) NOT NULL,
  `incharge_employee_id` varchar(50) DEFAULT NULL,
  `incharge_role_id` int DEFAULT NULL,
  `approval_limit` decimal(15,2) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_by` varchar(100) DEFAULT NULL,
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_by` varchar(100) DEFAULT NULL,
  `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `incharge_type` varchar(50) DEFAULT 'ENGINEER_INCHARGE',
  `incharge_employee_name` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_station_incharge_type` (`field_station_name`,`incharge_type`),
  KEY `idx_field_station_active` (`is_active`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `field_station_master`
--

DROP TABLE IF EXISTS `field_station_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `field_station_master` (
  `id` int NOT NULL AUTO_INCREMENT,
  `field_station_name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `form_master`
--

DROP TABLE IF EXISTS `form_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `form_master` (
  `form_id` bigint NOT NULL AUTO_INCREMENT,
  `form_name` varchar(100) NOT NULL COMMENT 'e.g., Indent, PurchaseOrder, Employee, Project, Budget',
  `form_display_name` varchar(200) NOT NULL,
  `form_description` text,
  `module_name` varchar(100) DEFAULT NULL COMMENT 'Procurement, Inventory, Admin, etc.',
  `is_active` tinyint(1) DEFAULT '1',
  `display_order` int DEFAULT '0',
  `created_by` varchar(100) DEFAULT NULL,
  `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`form_id`),
  UNIQUE KEY `form_name` (`form_name`),
  KEY `idx_form_active` (`is_active`),
  KEY `idx_form_module` (`module_name`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gatepass_out_in`
--

DROP TABLE IF EXISTS `gatepass_out_in`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gatepass_out_in` (
  `gate_pass_id` varchar(255) NOT NULL,
  `gate_pass_type` varchar(255) DEFAULT NULL,
  `material_details` text,
  `expected_date_of_return` date DEFAULT NULL,
  `extendEDR` decimal(10,2) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `created_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`gate_pass_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gem_vendor_id_tracker`
--

DROP TABLE IF EXISTS `gem_vendor_id_tracker`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gem_vendor_id_tracker` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `vendor_id` bigint NOT NULL,
  `created_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gem_vendor_id` varchar(100) DEFAULT NULL,
  `vendor_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `vendor_id` (`vendor_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gi_workflow_status`
--

DROP TABLE IF EXISTS `gi_workflow_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gi_workflow_status` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `process_id` varchar(50) DEFAULT NULL,
  `sub_process_id` int DEFAULT NULL,
  `action` varchar(50) DEFAULT NULL,
  `remarks` varchar(500) DEFAULT NULL,
  `created_by` int DEFAULT NULL,
  `create_date` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=110 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `goods_inspection`
--

DROP TABLE IF EXISTS `goods_inspection`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `goods_inspection` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `goods_inspection_no` varchar(50) NOT NULL,
  `installation_date` varchar(20) DEFAULT NULL,
  `commissioning_date` varchar(20) DEFAULT NULL,
  `upload_installation_report` blob,
  `accepted_quantity` int NOT NULL,
  `rejected_quantity` int NOT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_by` varchar(200) DEFAULT NULL,
  `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `upload_installation_report_file_name` varchar(100) DEFAULT NULL,
  `receipt_inspection_no` varchar(200) DEFAULT NULL,
  `goods_return_full_or_partial` varchar(255) DEFAULT NULL,
  `goods_return_permament_or_replacement` varchar(255) DEFAULT NULL,
  `goods_return_reason` varchar(255) DEFAULT NULL,
  `gri_id` varchar(255) DEFAULT NULL,
  `material_rejection_advice_sent` bit(1) DEFAULT NULL,
  `po_amendment_notified` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `goods_inspection_consumable_detail`
--

DROP TABLE IF EXISTS `goods_inspection_consumable_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `goods_inspection_consumable_detail` (
  `inspection_detail_id` int NOT NULL AUTO_INCREMENT,
  `inspection_sub_process_id` int NOT NULL,
  `gprn_sub_process_id` int NOT NULL,
  `gprn_process_id` varchar(20) DEFAULT NULL,
  `material_code` varchar(50) NOT NULL,
  `material_desc` varchar(50) NOT NULL,
  `uom_id` varchar(10) NOT NULL,
  `installation_report_filename` varchar(255) DEFAULT NULL,
  `received_quantity` decimal(10,2) NOT NULL,
  `accepted_quantity` decimal(10,2) NOT NULL,
  `rejected_quantity` decimal(10,2) NOT NULL,
  `rejection_type` varchar(50) DEFAULT NULL,
  `reject_reason` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`inspection_detail_id`),
  KEY `idx_inspection_subprocess` (`inspection_sub_process_id`),
  KEY `idx_gprn_subprocess` (`gprn_sub_process_id`),
  KEY `idx_material` (`material_code`),
  CONSTRAINT `goods_inspection_consumable_detail_ibfk_1` FOREIGN KEY (`inspection_sub_process_id`) REFERENCES `goods_inspection_master` (`inspection_sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `goods_inspection_consumable_detail_ibfk_2` FOREIGN KEY (`gprn_sub_process_id`) REFERENCES `gprn_master` (`sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `goods_inspection_detail`
--

DROP TABLE IF EXISTS `goods_inspection_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `goods_inspection_detail` (
  `inspection_detail_id` int NOT NULL AUTO_INCREMENT,
  `inspection_sub_process_id` int NOT NULL,
  `gprn_sub_process_id` int NOT NULL,
  `gprn_process_id` varchar(20) DEFAULT NULL,
  `material_code` varchar(50) NOT NULL,
  `material_desc` varchar(50) NOT NULL,
  `asset_id` int DEFAULT NULL,
  `installation_report_filename` varchar(255) DEFAULT NULL,
  `received_quantity` decimal(10,2) NOT NULL,
  `accepted_quantity` decimal(10,2) NOT NULL,
  `rejected_quantity` decimal(10,2) NOT NULL,
  `reject_reason` varchar(100) DEFAULT NULL,
  `rejection_type` varchar(50) DEFAULT NULL,
  `asset_code` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`inspection_detail_id`),
  KEY `idx_inspection_subprocess` (`inspection_sub_process_id`),
  KEY `idx_gprn_subprocess` (`gprn_sub_process_id`),
  KEY `idx_material` (`material_code`),
  KEY `asset_id` (`asset_id`),
  CONSTRAINT `goods_inspection_detail_ibfk_1` FOREIGN KEY (`inspection_sub_process_id`) REFERENCES `goods_inspection_master` (`inspection_sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `goods_inspection_detail_ibfk_2` FOREIGN KEY (`gprn_sub_process_id`) REFERENCES `gprn_master` (`sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `goods_inspection_detail_ibfk_3` FOREIGN KEY (`asset_id`) REFERENCES `asset_master` (`asset_id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=99 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `goods_inspection_master`
--

DROP TABLE IF EXISTS `goods_inspection_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `goods_inspection_master` (
  `inspection_sub_process_id` int NOT NULL AUTO_INCREMENT,
  `gprn_process_id` varchar(50) NOT NULL,
  `gprn_sub_process_id` int NOT NULL,
  `installation_date` date DEFAULT NULL,
  `commissioning_date` date DEFAULT NULL,
  `location_id` varchar(10) DEFAULT NULL,
  `create_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` int NOT NULL,
  `status` varchar(20) DEFAULT NULL,
  `po_amount` decimal(15,2) DEFAULT NULL,
  `gprn_amount` decimal(15,2) DEFAULT NULL,
  `spo_rejection_count` int NOT NULL,
  `spo_rejection_reason` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`inspection_sub_process_id`),
  KEY `idx_gprn_process` (`gprn_process_id`),
  KEY `idx_gprn_subprocess` (`gprn_sub_process_id`),
  KEY `location_id` (`location_id`),
  CONSTRAINT `goods_inspection_master_ibfk_1` FOREIGN KEY (`location_id`) REFERENCES `location_master` (`location_code`) ON UPDATE CASCADE,
  CONSTRAINT `goods_inspection_master_ibfk_2` FOREIGN KEY (`gprn_sub_process_id`) REFERENCES `gprn_master` (`sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=72 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `goods_receipt_inspection`
--

DROP TABLE IF EXISTS `goods_receipt_inspection`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `goods_receipt_inspection` (
  `receipt_inspection_no` varchar(255) NOT NULL,
  `installation_date` date DEFAULT NULL,
  `commissioning_date` date DEFAULT NULL,
  `asset_code` varchar(255) DEFAULT NULL,
  `additional_material_description` text,
  `locator` varchar(255) DEFAULT NULL,
  `print_label_option` tinyint(1) DEFAULT '0',
  `depreciation_rate` double DEFAULT NULL,
  `book_value` double DEFAULT NULL,
  `attach_component_popup` varchar(255) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `gri_id` varchar(255) NOT NULL,
  PRIMARY KEY (`receipt_inspection_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `goods_return`
--

DROP TABLE IF EXISTS `goods_return`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `goods_return` (
  `goods_return_id` varchar(255) NOT NULL,
  `goods_return_note_no` varchar(255) DEFAULT NULL,
  `rejected_quantity` int DEFAULT NULL,
  `return_quantity` int DEFAULT NULL,
  `type_of_return` varchar(100) DEFAULT NULL,
  `reason_of_return` text,
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `created_by` varchar(200) DEFAULT NULL,
  `updated_by` varchar(200) DEFAULT NULL,
  `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`goods_return_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gprn`
--

DROP TABLE IF EXISTS `gprn`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gprn` (
  `gprn_no` varchar(255) NOT NULL,
  `po_id` varchar(255) DEFAULT NULL,
  `date` date NOT NULL,
  `delivery_challan_no` varchar(255) DEFAULT NULL,
  `delivery_challan_date` date DEFAULT NULL,
  `vendor_id` varchar(255) DEFAULT NULL,
  `vendor_name` varchar(255) DEFAULT NULL,
  `vendor_email` varchar(255) DEFAULT NULL,
  `vendor_contact_no` bigint DEFAULT NULL,
  `field_station` varchar(255) DEFAULT NULL,
  `indentor_name` varchar(255) DEFAULT NULL,
  `expected_supply_date` date DEFAULT NULL,
  `consignee_detail` varchar(255) DEFAULT NULL,
  `warranty_years` int DEFAULT NULL,
  `project` varchar(255) DEFAULT NULL,
  `received_qty` varchar(255) DEFAULT NULL,
  `pending_qty` varchar(255) DEFAULT NULL,
  `accepted_qty` varchar(255) DEFAULT NULL,
  `provisional_receipt_certificate` blob,
  `received_by` varchar(255) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`gprn_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gprn_master`
--

DROP TABLE IF EXISTS `gprn_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gprn_master` (
  `process_id` varchar(50) NOT NULL,
  `sub_process_id` int NOT NULL AUTO_INCREMENT,
  `po_id` varchar(50) NOT NULL,
  `location_id` varchar(10) NOT NULL,
  `date` date DEFAULT NULL,
  `challan_no` varchar(50) NOT NULL,
  `delivery_date` date NOT NULL,
  `vendor_id` varchar(255) DEFAULT NULL,
  `field_station` varchar(50) NOT NULL,
  `indentor_name` varchar(50) NOT NULL,
  `supply_expected_date` date NOT NULL,
  `consignee_detail` varchar(100) NOT NULL,
  `warranty_years` decimal(10,1) DEFAULT NULL,
  `project` varchar(50) DEFAULT NULL,
  `received_by` varchar(50) NOT NULL,
  `created_by` varchar(50) NOT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `create_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `warranty` varchar(100) DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `indent_id` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`sub_process_id`),
  KEY `location_id` (`location_id`),
  KEY `gprn_master_ibfk_1` (`vendor_id`),
  CONSTRAINT `gprn_master_ibfk_1` FOREIGN KEY (`vendor_id`) REFERENCES `vendor_master` (`vendor_id`),
  CONSTRAINT `gprn_master_ibfk_2` FOREIGN KEY (`location_id`) REFERENCES `location_master` (`location_code`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=89 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gprn_material_detail`
--

DROP TABLE IF EXISTS `gprn_material_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gprn_material_detail` (
  `detail_id` int NOT NULL AUTO_INCREMENT,
  `process_id` varchar(50) NOT NULL,
  `sub_process_id` int NOT NULL,
  `po_id` varchar(50) NOT NULL,
  `material_code` varchar(50) NOT NULL,
  `material_desc` varchar(50) NOT NULL,
  `uom_id` varchar(10) NOT NULL,
  `received_quantity` decimal(10,2) NOT NULL,
  `unit_price` decimal(10,2) NOT NULL,
  `make_no` varchar(50) DEFAULT NULL,
  `serial_no` varchar(50) DEFAULT NULL,
  `model_no` varchar(50) DEFAULT NULL,
  `warranty_terms` varchar(100) DEFAULT NULL,
  `note` varchar(100) DEFAULT NULL,
  `photo_path` varchar(100) DEFAULT NULL,
  `category` varchar(50) DEFAULT NULL,
  `ordered_quantity` decimal(10,2) DEFAULT NULL,
  `quantity_delivered` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`detail_id`),
  KEY `sub_process_id` (`sub_process_id`),
  KEY `material_code` (`material_code`),
  KEY `uom_id` (`uom_id`),
  CONSTRAINT `gprn_material_detail_ibfk_1` FOREIGN KEY (`sub_process_id`) REFERENCES `gprn_master` (`sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `gprn_material_detail_ibfk_2` FOREIGN KEY (`material_code`) REFERENCES `material_master` (`material_code`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `gprn_material_detail_ibfk_3` FOREIGN KEY (`uom_id`) REFERENCES `uom_master` (`uom_code`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=82 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gprn_materials`
--

DROP TABLE IF EXISTS `gprn_materials`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gprn_materials` (
  `material_code` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `uom` varchar(50) DEFAULT NULL,
  `ordered_quantity` int DEFAULT NULL,
  `quantity_delivered` int DEFAULT NULL,
  `received_quantity` int DEFAULT NULL,
  `unit_price` double DEFAULT NULL,
  `net_price` decimal(18,2) DEFAULT NULL,
  `make_no` varchar(255) DEFAULT NULL,
  `model_no` varchar(255) DEFAULT NULL,
  `serial_no` varchar(255) DEFAULT NULL,
  `warranty` varchar(255) DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  `photograph_path` blob,
  `gprn_id` varchar(255) DEFAULT NULL,
  `photo_file_name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`material_code`),
  KEY `gprn_id` (`gprn_id`),
  CONSTRAINT `gprn_materials_ibfk_1` FOREIGN KEY (`gprn_id`) REFERENCES `gprn` (`gprn_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `grn_consumable_detail`
--

DROP TABLE IF EXISTS `grn_consumable_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `grn_consumable_detail` (
  `detail_id` int NOT NULL AUTO_INCREMENT,
  `grn_process_id` varchar(50) NOT NULL,
  `grn_sub_process_id` int NOT NULL,
  `gi_sub_process_id` int DEFAULT NULL,
  `igp_sub_process_id` int DEFAULT NULL,
  `quantity` decimal(10,2) NOT NULL,
  `material_code` varchar(50) NOT NULL,
  `locator_id` int NOT NULL,
  `book_value` decimal(10,2) NOT NULL,
  `depriciation_rate` decimal(10,2) NOT NULL,
  PRIMARY KEY (`detail_id`),
  KEY `grn_sub_process_id` (`grn_sub_process_id`),
  KEY `gi_sub_process_id` (`gi_sub_process_id`),
  KEY `locator_id` (`locator_id`),
  KEY `igp_sub_process_id` (`igp_sub_process_id`),
  CONSTRAINT `grn_consumable_detail_ibfk_1` FOREIGN KEY (`grn_sub_process_id`) REFERENCES `grn_master` (`grn_sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `grn_consumable_detail_ibfk_2` FOREIGN KEY (`gi_sub_process_id`) REFERENCES `goods_inspection_master` (`inspection_sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `grn_consumable_detail_ibfk_3` FOREIGN KEY (`locator_id`) REFERENCES `locator_master` (`locator_id`) ON UPDATE CASCADE,
  CONSTRAINT `grn_consumable_detail_ibfk_4` FOREIGN KEY (`igp_sub_process_id`) REFERENCES `igp_master` (`igp_sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `grn_master`
--

DROP TABLE IF EXISTS `grn_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `grn_master` (
  `grn_process_id` varchar(50) NOT NULL,
  `grn_sub_process_id` int NOT NULL AUTO_INCREMENT,
  `gi_process_id` varchar(50) DEFAULT NULL,
  `gi_sub_process_id` int DEFAULT NULL,
  `grn_type` varchar(10) DEFAULT NULL,
  `igp_process_id` varchar(50) DEFAULT NULL,
  `igp_sub_process_id` int DEFAULT NULL,
  `grn_date` date DEFAULT NULL,
  `installation_date` date DEFAULT NULL,
  `commissioning_date` date DEFAULT NULL,
  `created_by` varchar(50) NOT NULL,
  `system_created_by` int NOT NULL,
  `create_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `location_id` varchar(10) NOT NULL,
  `status` varchar(100) DEFAULT NULL,
  `custodian_id` int DEFAULT NULL,
  `consignee_name` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`grn_sub_process_id`),
  KEY `location_id` (`location_id`),
  KEY `gi_sub_process_id` (`gi_sub_process_id`),
  KEY `igp_sub_process_id` (`igp_sub_process_id`),
  CONSTRAINT `grn_master_ibfk_1` FOREIGN KEY (`location_id`) REFERENCES `location_master` (`location_code`) ON UPDATE CASCADE,
  CONSTRAINT `grn_master_ibfk_2` FOREIGN KEY (`gi_sub_process_id`) REFERENCES `goods_inspection_master` (`inspection_sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `grn_master_ibfk_3` FOREIGN KEY (`igp_sub_process_id`) REFERENCES `igp_master` (`igp_sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `grn_material_detail`
--

DROP TABLE IF EXISTS `grn_material_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `grn_material_detail` (
  `detail_id` int NOT NULL AUTO_INCREMENT,
  `grn_process_id` varchar(50) NOT NULL,
  `grn_sub_process_id` int NOT NULL,
  `gi_sub_process_id` int DEFAULT NULL,
  `igp_sub_process_id` int DEFAULT NULL,
  `quantity` decimal(10,2) NOT NULL,
  `asset_id` int NOT NULL,
  `locator_id` int NOT NULL,
  `book_value` decimal(10,2) NOT NULL,
  `depriciation_rate` decimal(10,2) NOT NULL,
  `asset_code` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`detail_id`),
  KEY `asset_id` (`asset_id`),
  KEY `grn_sub_process_id` (`grn_sub_process_id`),
  KEY `gi_sub_process_id` (`gi_sub_process_id`),
  KEY `locator_id` (`locator_id`),
  KEY `igp_sub_process_id` (`igp_sub_process_id`),
  CONSTRAINT `grn_material_detail_ibfk_1` FOREIGN KEY (`asset_id`) REFERENCES `asset_master` (`asset_id`) ON UPDATE CASCADE,
  CONSTRAINT `grn_material_detail_ibfk_2` FOREIGN KEY (`grn_sub_process_id`) REFERENCES `grn_master` (`grn_sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `grn_material_detail_ibfk_3` FOREIGN KEY (`gi_sub_process_id`) REFERENCES `goods_inspection_master` (`inspection_sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `grn_material_detail_ibfk_4` FOREIGN KEY (`locator_id`) REFERENCES `locator_master` (`locator_id`) ON UPDATE CASCADE,
  CONSTRAINT `grn_material_detail_ibfk_5` FOREIGN KEY (`igp_sub_process_id`) REFERENCES `igp_master` (`igp_sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=80 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `grn_workflow_status`
--

DROP TABLE IF EXISTS `grn_workflow_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `grn_workflow_status` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `process_id` varchar(255) DEFAULT NULL,
  `sub_process_id` int DEFAULT NULL,
  `action` varchar(50) DEFAULT NULL,
  `remarks` varchar(1000) DEFAULT NULL,
  `created_by` int DEFAULT NULL,
  `create_date` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `grv_master`
--

DROP TABLE IF EXISTS `grv_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `grv_master` (
  `gi_sub_process_id` int NOT NULL,
  `gi_process_id` varchar(50) NOT NULL,
  `grv_process_id` varchar(50) NOT NULL,
  `grv_sub_process_id` int NOT NULL AUTO_INCREMENT,
  `date` date DEFAULT NULL,
  `created_by` varchar(50) NOT NULL,
  `create_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `location_id` varchar(10) NOT NULL,
  PRIMARY KEY (`grv_sub_process_id`),
  KEY `idx_grv_process_id` (`grv_process_id`),
  KEY `idx_gi_sub_process` (`gi_sub_process_id`),
  KEY `idx_date` (`date`),
  KEY `location_id` (`location_id`),
  CONSTRAINT `grv_master_ibfk_1` FOREIGN KEY (`gi_sub_process_id`) REFERENCES `goods_inspection_master` (`inspection_sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `grv_master_ibfk_2` FOREIGN KEY (`location_id`) REFERENCES `location_master` (`location_code`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `grv_material_detail`
--

DROP TABLE IF EXISTS `grv_material_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `grv_material_detail` (
  `detail_id` int NOT NULL AUTO_INCREMENT,
  `grv_process_id` varchar(50) NOT NULL,
  `grv_sub_process_id` int NOT NULL,
  `gi_sub_process_id` int NOT NULL,
  `material_code` varchar(50) NOT NULL,
  `material_desc` varchar(50) NOT NULL,
  `uom_id` varchar(10) DEFAULT NULL,
  `rejected_quantity` decimal(10,2) NOT NULL,
  `return_quantity` decimal(10,2) NOT NULL,
  `return_type` varchar(50) NOT NULL,
  `reject_reason` varchar(50) NOT NULL,
  PRIMARY KEY (`detail_id`),
  KEY `idx_grv_sub_process` (`grv_sub_process_id`),
  KEY `idx_grv_process_id` (`grv_process_id`),
  KEY `idx_material` (`material_code`),
  KEY `idx_return_type` (`return_type`),
  KEY `uom_id` (`uom_id`),
  CONSTRAINT `grv_material_detail_ibfk_1` FOREIGN KEY (`grv_sub_process_id`) REFERENCES `grv_master` (`grv_sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `grv_material_detail_ibfk_2` FOREIGN KEY (`uom_id`) REFERENCES `uom_master` (`uom_code`) ON UPDATE CASCADE,
  CONSTRAINT `grv_material_detail_ibfk_3` FOREIGN KEY (`material_code`) REFERENCES `material_master` (`material_code`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gt_dtl`
--

DROP TABLE IF EXISTS `gt_dtl`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gt_dtl` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `gt_id` bigint DEFAULT NULL,
  `asset_id` int DEFAULT NULL,
  `asset_desc` varchar(500) DEFAULT NULL,
  `material_code` varchar(100) DEFAULT NULL,
  `material_desc` varchar(500) DEFAULT NULL,
  `quantity` decimal(18,2) NOT NULL,
  `receiver_locator_id` int DEFAULT NULL,
  `sender_locator_id` int DEFAULT NULL,
  `unit_price` decimal(18,2) DEFAULT NULL,
  `depriciation_rate` decimal(5,2) DEFAULT NULL,
  `book_value` decimal(18,2) DEFAULT NULL,
  `po_id` varchar(50) DEFAULT NULL,
  `model_no` varchar(100) DEFAULT NULL,
  `serial_no` varchar(100) DEFAULT NULL,
  `reason_for_transfer` varchar(255) DEFAULT NULL,
  `asset_code` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `gt_id` (`gt_id`),
  CONSTRAINT `gt_dtl_ibfk_1` FOREIGN KEY (`gt_id`) REFERENCES `gt_master` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gt_master`
--

DROP TABLE IF EXISTS `gt_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gt_master` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `sender_location_id` varchar(255) NOT NULL,
  `status` varchar(255) DEFAULT NULL,
  `sender_custodian_id` int NOT NULL,
  `receiver_location_id` varchar(255) NOT NULL,
  `receiver_custodian_id` int NOT NULL,
  `create_date` datetime NOT NULL,
  `gt_date` date NOT NULL,
  `created_by` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `igp_detail`
--

DROP TABLE IF EXISTS `igp_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `igp_detail` (
  `detail_id` int NOT NULL AUTO_INCREMENT,
  `igp_process_id` varchar(50) NOT NULL,
  `igp_sub_process_id` int NOT NULL,
  `ogp_sub_process_id` int DEFAULT NULL,
  `asset_id` int NOT NULL,
  `locator_id` int NOT NULL,
  `quantity` decimal(10,2) NOT NULL,
  PRIMARY KEY (`detail_id`),
  KEY `igp_sub_process_id` (`igp_sub_process_id`),
  KEY `asset_id` (`asset_id`),
  KEY `locator_id` (`locator_id`),
  KEY `ogp_sub_process_id` (`ogp_sub_process_id`),
  CONSTRAINT `igp_detail_ibfk_1` FOREIGN KEY (`igp_sub_process_id`) REFERENCES `igp_master` (`igp_sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `igp_detail_ibfk_2` FOREIGN KEY (`asset_id`) REFERENCES `asset_master` (`asset_id`) ON UPDATE CASCADE,
  CONSTRAINT `igp_detail_ibfk_3` FOREIGN KEY (`locator_id`) REFERENCES `locator_master` (`locator_id`) ON UPDATE CASCADE,
  CONSTRAINT `igp_detail_ibfk_4` FOREIGN KEY (`ogp_sub_process_id`) REFERENCES `ogp_master` (`ogp_sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `igp_master`
--

DROP TABLE IF EXISTS `igp_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `igp_master` (
  `igp_process_id` varchar(50) NOT NULL,
  `igp_sub_process_id` int NOT NULL AUTO_INCREMENT,
  `ogp_sub_process_id` int NOT NULL,
  `igp_date` date NOT NULL,
  `location_id` varchar(10) NOT NULL,
  `created_by` int NOT NULL,
  `create_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`igp_sub_process_id`),
  KEY `location_id` (`location_id`),
  KEY `ogp_sub_process_id` (`ogp_sub_process_id`),
  CONSTRAINT `igp_master_ibfk_1` FOREIGN KEY (`location_id`) REFERENCES `location_master` (`location_code`) ON UPDATE CASCADE,
  CONSTRAINT `igp_master_ibfk_2` FOREIGN KEY (`ogp_sub_process_id`) REFERENCES `ogp_master` (`ogp_sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `igp_material_detail`
--

DROP TABLE IF EXISTS `igp_material_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `igp_material_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `material_code` varchar(100) DEFAULT NULL,
  `category` varchar(100) DEFAULT NULL,
  `sub_category` varchar(100) DEFAULT NULL,
  `material_description` varchar(255) DEFAULT NULL,
  `uom` varchar(20) DEFAULT NULL,
  `estimated_price_with_ccy` decimal(10,2) DEFAULT NULL,
  `indigenous_or_imported` tinyint(1) DEFAULT NULL,
  `quantity` decimal(10,2) DEFAULT NULL,
  `igp_id` bigint DEFAULT NULL,
  `asset_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `igp_id` (`igp_id`),
  CONSTRAINT `igp_material_detail_ibfk_1` FOREIGN KEY (`igp_id`) REFERENCES `igp_material_master` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `igp_material_master`
--

DROP TABLE IF EXISTS `igp_material_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `igp_material_master` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `ogp_id` varchar(255) DEFAULT NULL,
  `igp_date` varchar(20) DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  `igp_type` varchar(50) DEFAULT NULL,
  `indent_id` int DEFAULT NULL,
  `created_by` int DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `location_id` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `igp_po_detail`
--

DROP TABLE IF EXISTS `igp_po_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `igp_po_detail` (
  `detail_id` int NOT NULL AUTO_INCREMENT,
  `igp_sub_process_id` int NOT NULL,
  `material_code` varchar(255) NOT NULL,
  `material_desc` varchar(255) NOT NULL,
  `uom_id` varchar(255) NOT NULL,
  `quantity` decimal(10,2) NOT NULL,
  PRIMARY KEY (`detail_id`),
  KEY `igp_sub_process_id` (`igp_sub_process_id`),
  CONSTRAINT `igp_po_detail_ibfk_1` FOREIGN KEY (`igp_sub_process_id`) REFERENCES `igp_master` (`igp_sub_process_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `iia_address_for_consignee_location`
--

DROP TABLE IF EXISTS `iia_address_for_consignee_location`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `iia_address_for_consignee_location` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `consignee` varchar(255) DEFAULT NULL,
  `iia_address` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `iia_freight_forwarder_details`
--

DROP TABLE IF EXISTS `iia_freight_forwarder_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `iia_freight_forwarder_details` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `country_name` varchar(255) NOT NULL,
  `freight_forwarder_details` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `indent_assignment`
--

DROP TABLE IF EXISTS `indent_assignment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `indent_assignment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `indent_id` varchar(255) NOT NULL,
  `assigned_to_employee_id` varchar(50) NOT NULL,
  `assigned_by_employee_id` varchar(50) NOT NULL,
  `assigned_date` datetime NOT NULL,
  `status` varchar(30) DEFAULT 'ACTIVE',
  PRIMARY KEY (`id`),
  KEY `indent_id` (`indent_id`),
  CONSTRAINT `indent_assignment_ibfk_1` FOREIGN KEY (`indent_id`) REFERENCES `indent_creation` (`indent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `indent_cancellation_request`
--

DROP TABLE IF EXISTS `indent_cancellation_request`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `indent_cancellation_request` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `indent_id` varchar(50) NOT NULL,
  `requested_by` int NOT NULL,
  `requested_by_name` varchar(255) DEFAULT NULL,
  `cancellation_reason` text,
  `request_status` varchar(20) DEFAULT NULL,
  `approved_by` int DEFAULT NULL,
  `approved_by_name` varchar(255) DEFAULT NULL,
  `approval_remarks` text,
  `approval_date` datetime DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `indent_creation`
--

DROP TABLE IF EXISTS `indent_creation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `indent_creation` (
  `indent_id` varchar(255) NOT NULL,
  `indentor_name` varchar(255) NOT NULL,
  `indentor_mobile_no` varchar(20) DEFAULT NULL,
  `indentor_email_address` varchar(255) DEFAULT NULL,
  `indentor_department` varchar(100) DEFAULT NULL,
  `consignes_location` varchar(255) DEFAULT NULL,
  `uploading_prior_approvals` blob,
  `project_name` varchar(255) DEFAULT NULL,
  `upload_tender_documents` blob,
  `is_pre_bit_meeting_required` tinyint(1) DEFAULT NULL,
  `pre_bid_meeting_date` date DEFAULT NULL,
  `pre_bid_meeting_venue` varchar(255) DEFAULT NULL,
  `is_it_a_rate_contract_indent` tinyint(1) DEFAULT NULL,
  `estimated_rate` decimal(10,2) DEFAULT NULL,
  `period_of_contract` decimal(10,2) DEFAULT NULL,
  `single_and_multiple_job` varchar(50) DEFAULT NULL,
  `upload_goi_or_rfp` blob,
  `upload_pac_or_brand_pac` blob,
  `updated_by` varchar(255) DEFAULT NULL,
  `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `uploading_prior_approvals_file_name` text,
  `technical_specifications_file_name` text,
  `draft_Eoi_Or_Rfp_file_name` text,
  `upload_pac_or_brand_pac_file_name` text,
  `created_by` int DEFAULT NULL,
  `file_type` varchar(255) DEFAULT NULL,
  `total_indent_value` decimal(15,2) DEFAULT NULL,
  `brand_and_model` varchar(255) DEFAULT NULL,
  `justification` varchar(255) DEFAULT NULL,
  `brand_pac` tinyint(1) DEFAULT NULL,
  `indent_number` int DEFAULT NULL,
  `quarter` varchar(200) DEFAULT NULL,
  `purpose` varchar(200) DEFAULT NULL,
  `reason` text,
  `proprietary_justification` varchar(250) DEFAULT NULL,
  `upload_buy_back_file_names` text,
  `buy_back` tinyint(1) DEFAULT NULL,
  `model_number` varchar(255) DEFAULT NULL,
  `serial_number` varchar(255) DEFAULT NULL,
  `date_of_purchase` date DEFAULT NULL,
  `employee_department` varchar(50) DEFAULT NULL,
  `employee_id` varchar(50) DEFAULT NULL,
  `employee_name` varchar(100) DEFAULT NULL,
  `cancel_status` tinyint(1) DEFAULT '0',
  `cancel_remarks` varchar(1000) DEFAULT NULL,
  `buy_back_amount` varchar(50) DEFAULT NULL,
  `proprietary_and_limited_declaration` tinyint(1) DEFAULT NULL,
  `indent_type` varchar(50) DEFAULT 'material' COMMENT 'Type of indent: material or job',
  `material_category_type` varchar(50) DEFAULT 'all' COMMENT 'Material category type: all, computer, or non-computer',
  `rate_contract_job_codes` varchar(2000) DEFAULT NULL,
  `is_editable` tinyint(1) DEFAULT '1' COMMENT 'Controls whether indent can be edited. False after submission, true when sent back for revision',
  `is_locked_for_tender` tinyint(1) DEFAULT '0' COMMENT 'Locks indent for editing when tender is created',
  `locked_reason` varchar(500) DEFAULT NULL COMMENT 'Reason why indent is locked',
  `version` int DEFAULT '1' COMMENT 'Version number of indent, incremented on each update',
  `parent_indent_id` varchar(50) DEFAULT NULL COMMENT 'Reference to parent indent if this is a revised version',
  `current_status` varchar(50) DEFAULT 'DRAFT' COMMENT 'DRAFT, IN_APPROVAL, APPROVED, CHANGE_REQUESTED, TENDER_CREATED, CANCELLED',
  `current_stage` varchar(100) DEFAULT 'INDENT_CREATION' COMMENT 'Workflow stage: INDENT_CREATION, INDENT_REVISION, INDENT_APPROVAL, TENDER_GENERATION',
  `approval_level` int DEFAULT '0' COMMENT 'Current approval level (0 = not submitted)',
  `is_under_project` tinyint(1) DEFAULT '0',
  `project_code` varchar(50) DEFAULT NULL,
  `workflow_branch_id` bigint DEFAULT NULL,
  `escalated_to_director` tinyint(1) DEFAULT '0',
  `escalation_reason` varchar(500) DEFAULT NULL,
  `mode_of_procurement` varchar(100) DEFAULT NULL,
  `ro_project_determination` tinyint(1) DEFAULT NULL,
  `ro_project_determination_remarks` varchar(500) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`indent_id`),
  KEY `idx_indent_current_status` (`current_status`),
  KEY `idx_indent_current_stage` (`current_stage`),
  KEY `idx_indent_is_editable` (`is_editable`),
  KEY `idx_indent_is_locked_for_tender` (`is_locked_for_tender`),
  KEY `idx_indent_version` (`version`),
  KEY `idx_indent_approval_level` (`approval_level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `indent_id`
--

DROP TABLE IF EXISTS `indent_id`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `indent_id` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `indent_id` varchar(255) DEFAULT NULL,
  `tender_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `tender_id` (`tender_id`),
  CONSTRAINT `indent_id_ibfk_1` FOREIGN KEY (`tender_id`) REFERENCES `tender_request` (`tender_id`)
) ENGINE=InnoDB AUTO_INCREMENT=87 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `issue_note_detail`
--

DROP TABLE IF EXISTS `issue_note_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `issue_note_detail` (
  `detail_id` int NOT NULL AUTO_INCREMENT,
  `issue_note_id` int NOT NULL,
  `asset_id` int NOT NULL,
  `locator_id` int NOT NULL,
  `quantity` decimal(10,2) NOT NULL,
  PRIMARY KEY (`detail_id`),
  KEY `issue_note_id` (`issue_note_id`),
  KEY `asset_id` (`asset_id`),
  KEY `locator_id` (`locator_id`),
  CONSTRAINT `issue_note_detail_ibfk_1` FOREIGN KEY (`issue_note_id`) REFERENCES `issue_note_master` (`issue_note_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `issue_note_detail_ibfk_2` FOREIGN KEY (`asset_id`) REFERENCES `asset_master` (`asset_id`) ON UPDATE CASCADE,
  CONSTRAINT `issue_note_detail_ibfk_3` FOREIGN KEY (`locator_id`) REFERENCES `locator_master` (`locator_id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `issue_note_master`
--

DROP TABLE IF EXISTS `issue_note_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `issue_note_master` (
  `issue_note_id` int NOT NULL AUTO_INCREMENT,
  `issue_note_type` enum('Returnable','Non Returnable') DEFAULT NULL,
  `issue_date` date NOT NULL,
  `consignee_detail` varchar(50) DEFAULT NULL,
  `indentor_name` varchar(50) DEFAULT NULL,
  `field_station` varchar(50) DEFAULT NULL,
  `created_by` int NOT NULL,
  `create_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `location_id` varchar(10) NOT NULL,
  PRIMARY KEY (`issue_note_id`),
  KEY `location_id` (`location_id`),
  CONSTRAINT `issue_note_master_ibfk_1` FOREIGN KEY (`location_id`) REFERENCES `location_master` (`location_code`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `job_details`
--

DROP TABLE IF EXISTS `job_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `job_details` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `job_code` varchar(50) DEFAULT NULL,
  `job_description` varchar(500) DEFAULT NULL,
  `category` varchar(100) DEFAULT NULL,
  `sub_category` varchar(100) DEFAULT NULL,
  `uom` varchar(50) DEFAULT NULL,
  `quantity` decimal(19,2) DEFAULT NULL,
  `estimated_price` decimal(19,2) DEFAULT NULL,
  `total_price` decimal(19,2) DEFAULT NULL,
  `currency` varchar(10) DEFAULT NULL,
  `brief_description` varchar(1000) DEFAULT NULL,
  `indent_id` varchar(50) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `origin` varchar(50) DEFAULT NULL COMMENT 'Origin: Indigenous or Imported',
  `budget_code` varchar(255) DEFAULT NULL,
  `conversion_rate` decimal(19,6) DEFAULT NULL,
  `mode_of_procurement` varchar(255) DEFAULT NULL,
  `vendor_names` longtext,
  PRIMARY KEY (`id`),
  KEY `idx_job_details_indent_id` (`indent_id`),
  KEY `idx_job_details_job_code` (`job_code`),
  CONSTRAINT `fk_job_details_indent` FOREIGN KEY (`indent_id`) REFERENCES `indent_creation` (`indent_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `job_master`
--

DROP TABLE IF EXISTS `job_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `job_master` (
  `job_code` varchar(255) NOT NULL,
  `category` varchar(255) DEFAULT NULL,
  `job_description` text,
  `asset_id` varchar(255) DEFAULT NULL,
  `uom` varchar(50) DEFAULT NULL,
  `value` decimal(15,2) DEFAULT NULL,
  `created_by` int DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `mode_of_procurement` varchar(255) DEFAULT NULL,
  `sub_category` varchar(255) DEFAULT NULL,
  `currency` varchar(255) DEFAULT NULL,
  `estimated_price_with_ccy` decimal(15,2) DEFAULT NULL,
  `brief_description` text,
  `approval_status` varchar(255) DEFAULT NULL,
  `comments` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`job_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `location_master`
--

DROP TABLE IF EXISTS `location_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `location_master` (
  `location_code` varchar(10) NOT NULL,
  `location_name` varchar(255) DEFAULT NULL,
  `address` text,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`location_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `locator_master`
--

DROP TABLE IF EXISTS `locator_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `locator_master` (
  `location_id` varchar(10) NOT NULL,
  `locator_id` int NOT NULL AUTO_INCREMENT,
  `locator_desc` varchar(40) NOT NULL,
  `created_by` varchar(20) DEFAULT NULL,
  `create_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_by` varchar(20) DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`locator_id`),
  KEY `fk_location_id` (`location_id`),
  CONSTRAINT `fk_location_id` FOREIGN KEY (`location_id`) REFERENCES `location_master` (`location_code`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `lov_master`
--

DROP TABLE IF EXISTS `lov_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lov_master` (
  `lov_id` bigint NOT NULL AUTO_INCREMENT,
  `designator_id` bigint NOT NULL,
  `lov_value` varchar(200) NOT NULL,
  `lov_display_value` varchar(200) NOT NULL,
  `lov_description` text,
  `is_active` tinyint(1) DEFAULT '1',
  `is_default` tinyint(1) DEFAULT '0',
  `display_order` int DEFAULT '0',
  `color_code` varchar(20) DEFAULT NULL COMMENT 'For UI display (e.g., #28a745 for Active)',
  `icon_name` varchar(50) DEFAULT NULL COMMENT 'Icon identifier',
  `parent_lov_id` bigint DEFAULT NULL COMMENT 'For hierarchical LOVs',
  `created_by` varchar(100) DEFAULT NULL,
  `updated_by` varchar(100) DEFAULT NULL,
  `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `locator_location` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`lov_id`),
  UNIQUE KEY `uk_designator_value` (`designator_id`,`lov_value`),
  UNIQUE KEY `UK2wpukje6wb63wgp6qx1kb9qbi` (`designator_id`,`lov_value`),
  KEY `idx_lov_active` (`is_active`),
  KEY `idx_lov_parent` (`parent_lov_id`),
  CONSTRAINT `fk_lov_designator` FOREIGN KEY (`designator_id`) REFERENCES `designator_master` (`designator_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_lov_parent` FOREIGN KEY (`parent_lov_id`) REFERENCES `lov_master` (`lov_id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=251 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `material_details`
--

DROP TABLE IF EXISTS `material_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `material_details` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `material_code` varchar(255) DEFAULT NULL,
  `indent_id` varchar(255) DEFAULT NULL,
  `material_description` varchar(255) DEFAULT NULL,
  `quantity` decimal(15,2) DEFAULT NULL,
  `unit_price` decimal(15,2) DEFAULT NULL,
  `uom` varchar(50) DEFAULT NULL,
  `total_price` decimal(15,2) DEFAULT NULL,
  `budget_code` varchar(255) DEFAULT NULL,
  `material_category` varchar(255) DEFAULT NULL,
  `material_sub_category` varchar(255) DEFAULT NULL,
  `material_and_job` varchar(255) DEFAULT NULL,
  `mode_of_procurement` varchar(255) DEFAULT NULL,
  `currency` varchar(100) DEFAULT NULL,
  `conversion_rate` decimal(19,6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `indent_id` (`indent_id`),
  CONSTRAINT `material_details_ibfk_1` FOREIGN KEY (`indent_id`) REFERENCES `indent_creation` (`indent_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=369 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `material_disposal`
--

DROP TABLE IF EXISTS `material_disposal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `material_disposal` (
  `material_disposal_code` varchar(255) NOT NULL,
  `disposal_category` varchar(255) DEFAULT NULL,
  `disposal_mode` varchar(255) DEFAULT NULL,
  `vendor_details` varchar(255) DEFAULT NULL,
  `disposal_date` date DEFAULT NULL,
  `current_book_value` decimal(19,2) DEFAULT NULL,
  `edit_reserve_value` decimal(19,2) DEFAULT NULL,
  `final_bid_value` decimal(19,2) DEFAULT NULL,
  `sale_note` longblob,
  `sale_note_file_name` varchar(255) DEFAULT NULL,
  `edit_quantity` decimal(19,2) DEFAULT NULL,
  `edit_value_materials` decimal(19,2) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `created_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`material_disposal_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `material_id_sequence`
--

DROP TABLE IF EXISTS `material_id_sequence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `material_id_sequence` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `material_id` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=63 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `material_master`
--

DROP TABLE IF EXISTS `material_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `material_master` (
  `material_code` varchar(50) NOT NULL,
  `category` varchar(100) DEFAULT NULL,
  `sub_category` varchar(100) DEFAULT NULL,
  `description` text,
  `uom` varchar(50) DEFAULT NULL,
  `upload_image` longblob,
  `indigenous_or_imported` tinyint(1) DEFAULT NULL,
  `updated_by` varchar(200) DEFAULT NULL,
  `created_by` varchar(200) DEFAULT NULL,
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `upload_image_name` varchar(255) DEFAULT NULL,
  `estimated_price_with_ccy` decimal(19,2) DEFAULT NULL,
  `unit_price` decimal(19,2) DEFAULT NULL,
  `currency` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `remarks` text,
  `brief_description` text,
  `reason_for_deactive` varchar(255) DEFAULT NULL,
  `status_of_material_active_or_deactive` varchar(20) DEFAULT NULL,
  `Asset_Flag` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`material_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `material_master_util`
--

DROP TABLE IF EXISTS `material_master_util`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `material_master_util` (
  `material_code` varchar(255) NOT NULL,
  `category` varchar(255) DEFAULT NULL,
  `sub_category` varchar(255) DEFAULT NULL,
  `description` text,
  `uom` varchar(50) DEFAULT NULL,
  `unit_price` decimal(19,2) DEFAULT NULL,
  `currency` varchar(10) DEFAULT NULL,
  `estimated_price_with_ccy` decimal(19,2) DEFAULT NULL,
  `upload_image_name` varchar(255) DEFAULT NULL,
  `indigenous_or_imported` tinyint(1) DEFAULT NULL,
  `approval_status` enum('APPROVED','REJECTED','AWAITING_APPROVAL','CHANGE_REQUEST') DEFAULT NULL,
  `comments` text,
  `created_by` int DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `brief_description` text,
  `material_number` int DEFAULT NULL,
  `Asset_Flag` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`material_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `material_status`
--

DROP TABLE IF EXISTS `material_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `material_status` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `material_code` varchar(255) NOT NULL,
  `status` varchar(50) DEFAULT NULL,
  `comments` text,
  `created_by` int DEFAULT NULL,
  `updated_by` int DEFAULT NULL,
  `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `action` varchar(200) DEFAULT NULL,
  `role_name` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `material_code` (`material_code`)
) ENGINE=InnoDB AUTO_INCREMENT=132 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mode_of_procurement_master`
--

DROP TABLE IF EXISTS `mode_of_procurement_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mode_of_procurement_master` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `procurement_code` varchar(50) NOT NULL,
  `procurement_name` varchar(100) NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  `requires_tender` tinyint(1) DEFAULT '0',
  `is_active` tinyint(1) DEFAULT '1',
  `display_order` int DEFAULT '0',
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `procurement_code` (`procurement_code`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `officer_signature`
--

DROP TABLE IF EXISTS `officer_signature`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `officer_signature` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `officer_name` varchar(100) NOT NULL,
  `designation` varchar(100) DEFAULT NULL,
  `signature_path` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ogp_asset_disposal`
--

DROP TABLE IF EXISTS `ogp_asset_disposal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ogp_asset_disposal` (
  `disposal_ogp_id` int NOT NULL AUTO_INCREMENT,
  `auction_id` int NOT NULL,
  `auction_code` varchar(50) NOT NULL,
  `auction_date` date DEFAULT NULL,
  `reserve_price` decimal(15,2) DEFAULT NULL,
  `auction_price` decimal(15,2) DEFAULT NULL,
  `vendor_name` varchar(100) DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  `created_by` int DEFAULT NULL,
  `create_date` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`disposal_ogp_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ogp_asset_disposal_detail`
--

DROP TABLE IF EXISTS `ogp_asset_disposal_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ogp_asset_disposal_detail` (
  `ogp_disposal_detail_id` int NOT NULL AUTO_INCREMENT,
  `disposal_ogp_id` int NOT NULL,
  `disposal_id` int NOT NULL,
  `asset_id` int NOT NULL,
  `asset_desc` varchar(255) DEFAULT NULL,
  `disposal_quantity` decimal(10,2) DEFAULT NULL,
  `locator_id` int DEFAULT NULL,
  `book_value` decimal(15,2) DEFAULT NULL,
  `depriciation_rate` decimal(5,2) DEFAULT NULL,
  `unit_price` decimal(15,2) DEFAULT NULL,
  `custodian_id` varchar(50) DEFAULT NULL,
  `po_value` decimal(15,2) DEFAULT NULL,
  `reason_for_disposal` varchar(255) DEFAULT NULL,
  `disposal_date` date DEFAULT NULL,
  `location_id` varchar(50) DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  `asset_code` varchar(200) DEFAULT NULL,
  `serial_no` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`ogp_disposal_detail_id`),
  KEY `disposal_ogp_id` (`disposal_ogp_id`),
  CONSTRAINT `ogp_asset_disposal_detail_ibfk_1` FOREIGN KEY (`disposal_ogp_id`) REFERENCES `ogp_asset_disposal` (`disposal_ogp_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ogp_detail`
--

DROP TABLE IF EXISTS `ogp_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ogp_detail` (
  `detail_id` int NOT NULL AUTO_INCREMENT,
  `ogp_process_id` varchar(50) NOT NULL,
  `issue_note_id` int DEFAULT NULL,
  `ogp_sub_process_id` int NOT NULL,
  `asset_id` int NOT NULL,
  `locator_id` int NOT NULL,
  `quantity` decimal(10,2) NOT NULL,
  PRIMARY KEY (`detail_id`),
  KEY `ogp_sub_process_id` (`ogp_sub_process_id`),
  KEY `issue_note_id` (`issue_note_id`),
  KEY `asset_id` (`asset_id`),
  KEY `locator_id` (`locator_id`),
  CONSTRAINT `ogp_detail_ibfk_1` FOREIGN KEY (`ogp_sub_process_id`) REFERENCES `ogp_master` (`ogp_sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `ogp_detail_ibfk_2` FOREIGN KEY (`issue_note_id`) REFERENCES `issue_note_master` (`issue_note_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `ogp_detail_ibfk_3` FOREIGN KEY (`asset_id`) REFERENCES `asset_master` (`asset_id`) ON UPDATE CASCADE,
  CONSTRAINT `ogp_detail_ibfk_4` FOREIGN KEY (`locator_id`) REFERENCES `locator_master` (`locator_id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ogp_detail_rejected_gi`
--

DROP TABLE IF EXISTS `ogp_detail_rejected_gi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ogp_detail_rejected_gi` (
  `detail_id` int NOT NULL AUTO_INCREMENT,
  `ogp_sub_process_id` int NOT NULL,
  `material_code` varchar(255) DEFAULT NULL,
  `material_desc` varchar(255) DEFAULT NULL,
  `asset_id` int DEFAULT NULL,
  `asset_desc` varchar(255) DEFAULT NULL,
  `rejection_type` varchar(50) DEFAULT NULL,
  `rejected_quantity` decimal(10,2) NOT NULL,
  `asset_code` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`detail_id`),
  KEY `ogp_sub_process_id` (`ogp_sub_process_id`),
  CONSTRAINT `ogp_detail_rejected_gi_ibfk_1` FOREIGN KEY (`ogp_sub_process_id`) REFERENCES `ogp_master_rejected_gi` (`ogp_sub_process_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ogp_gt_dtl`
--

DROP TABLE IF EXISTS `ogp_gt_dtl`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ogp_gt_dtl` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `gt_id` bigint DEFAULT NULL,
  `asset_id` int DEFAULT NULL,
  `asset_desc` varchar(500) DEFAULT NULL,
  `material_code` varchar(100) DEFAULT NULL,
  `material_desc` varchar(500) DEFAULT NULL,
  `quantity` decimal(18,6) NOT NULL,
  `receiver_locator_id` int DEFAULT NULL,
  `sender_locator_id` int DEFAULT NULL,
  `unit_price` decimal(18,6) DEFAULT NULL,
  `depriciation_rate` decimal(18,6) DEFAULT NULL,
  `book_value` decimal(18,6) DEFAULT NULL,
  `asset_code` varchar(200) DEFAULT NULL,
  `serial_no` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ogp_gt_master`
--

DROP TABLE IF EXISTS `ogp_gt_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ogp_gt_master` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `sender_location_id` varchar(255) NOT NULL,
  `status` varchar(255) DEFAULT NULL,
  `sender_custodian_id` int NOT NULL,
  `receiver_location_id` varchar(255) NOT NULL,
  `receiver_custodian_id` int NOT NULL,
  `create_date` datetime NOT NULL,
  `gt_date` date NOT NULL,
  `created_by` int NOT NULL,
  `gt_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ogp_master`
--

DROP TABLE IF EXISTS `ogp_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ogp_master` (
  `ogp_process_id` varchar(50) NOT NULL,
  `ogp_sub_process_id` int NOT NULL AUTO_INCREMENT,
  `issue_note_id` int NOT NULL,
  `ogp_date` date NOT NULL,
  `location_id` varchar(10) NOT NULL,
  `created_by` int NOT NULL,
  `create_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ogp_type` varchar(20) NOT NULL,
  `receiver_name` varchar(50) DEFAULT NULL,
  `receiver_location` varchar(100) DEFAULT NULL,
  `date_of_return` date DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `sender_name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`ogp_sub_process_id`),
  KEY `location_id` (`location_id`),
  KEY `issue_note_id` (`issue_note_id`),
  CONSTRAINT `ogp_master_ibfk_1` FOREIGN KEY (`location_id`) REFERENCES `location_master` (`location_code`) ON UPDATE CASCADE,
  CONSTRAINT `ogp_master_ibfk_2` FOREIGN KEY (`issue_note_id`) REFERENCES `issue_note_master` (`issue_note_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ogp_master_po`
--

DROP TABLE IF EXISTS `ogp_master_po`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ogp_master_po` (
  `ogp_sub_process_id` int NOT NULL AUTO_INCREMENT,
  `po_id` varchar(50) NOT NULL,
  `ogp_date` date NOT NULL,
  `location_id` varchar(10) NOT NULL,
  `created_by` int NOT NULL,
  `create_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ogp_type` varchar(20) NOT NULL,
  `receiver_name` varchar(50) DEFAULT NULL,
  `receiver_location` varchar(100) DEFAULT NULL,
  `date_of_return` date DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `sender_name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`ogp_sub_process_id`),
  KEY `location_id` (`location_id`),
  KEY `po_id` (`po_id`),
  CONSTRAINT `ogp_master_po_ibfk_1` FOREIGN KEY (`location_id`) REFERENCES `location_master` (`location_code`) ON UPDATE CASCADE,
  CONSTRAINT `ogp_master_po_ibfk_2` FOREIGN KEY (`po_id`) REFERENCES `purchase_order` (`po_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ogp_master_rejected_gi`
--

DROP TABLE IF EXISTS `ogp_master_rejected_gi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ogp_master_rejected_gi` (
  `ogp_sub_process_id` int NOT NULL AUTO_INCREMENT,
  `ogp_type` varchar(20) DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `gi_id` varchar(255) DEFAULT NULL,
  `ogp_date` date DEFAULT NULL,
  `return_date` date DEFAULT NULL,
  `location_id` varchar(50) DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `sender_name` varchar(50) DEFAULT NULL,
  `receiver_name` varchar(50) DEFAULT NULL,
  `receiver_location` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`ogp_sub_process_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ogp_po_detail`
--

DROP TABLE IF EXISTS `ogp_po_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ogp_po_detail` (
  `detail_id` int NOT NULL AUTO_INCREMENT,
  `ogp_sub_process_id` int NOT NULL,
  `material_code` varchar(50) NOT NULL,
  `material_desc` varchar(50) NOT NULL,
  `uom_id` varchar(10) NOT NULL,
  `quantity` decimal(10,2) NOT NULL,
  PRIMARY KEY (`detail_id`),
  KEY `ogp_sub_process_id` (`ogp_sub_process_id`),
  KEY `material_code` (`material_code`),
  CONSTRAINT `ogp_po_detail_ibfk_1` FOREIGN KEY (`ogp_sub_process_id`) REFERENCES `ogp_master_po` (`ogp_sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `ogp_po_detail_ibfk_2` FOREIGN KEY (`material_code`) REFERENCES `material_master` (`material_code`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ohq_consumable_store_stock_entity`
--

DROP TABLE IF EXISTS `ohq_consumable_store_stock_entity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ohq_consumable_store_stock_entity` (
  `ohq_id` bigint NOT NULL AUTO_INCREMENT,
  `material_code` varchar(255) DEFAULT NULL,
  `locator_id` int DEFAULT NULL,
  `book_value` decimal(19,2) DEFAULT NULL,
  `depriciation_rate` decimal(19,2) DEFAULT NULL,
  `unit_price` decimal(19,2) DEFAULT NULL,
  `custodian_id` varchar(255) DEFAULT NULL,
  `quantity` decimal(19,2) DEFAULT NULL,
  `uom` varchar(100) DEFAULT NULL,
  `create_date` timestamp NOT NULL,
  PRIMARY KEY (`ohq_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ohq_master`
--

DROP TABLE IF EXISTS `ohq_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ohq_master` (
  `ohq_id` int NOT NULL AUTO_INCREMENT,
  `asset_id` int NOT NULL,
  `locator_id` int NOT NULL,
  `book_value` decimal(10,2) NOT NULL,
  `depriciation_rate` decimal(10,2) NOT NULL,
  `unit_price` decimal(10,2) NOT NULL,
  `quantity` decimal(10,2) NOT NULL,
  `custodian_id` varchar(100) DEFAULT NULL,
  `asset_code` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`ohq_id`),
  KEY `asset_id` (`asset_id`),
  KEY `locator_id` (`locator_id`),
  CONSTRAINT `ohq_master_ibfk_1` FOREIGN KEY (`asset_id`) REFERENCES `asset_master` (`asset_id`) ON UPDATE CASCADE,
  CONSTRAINT `ohq_master_ibfk_2` FOREIGN KEY (`locator_id`) REFERENCES `locator_master` (`locator_id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=92 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ohq_master_consumable`
--

DROP TABLE IF EXISTS `ohq_master_consumable`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ohq_master_consumable` (
  `ohq_id` int NOT NULL AUTO_INCREMENT,
  `material_code` varchar(50) NOT NULL,
  `locator_id` int NOT NULL,
  `book_value` decimal(10,2) NOT NULL,
  `depriciation_rate` decimal(10,2) NOT NULL,
  `unit_price` decimal(10,2) NOT NULL,
  `quantity` decimal(10,2) NOT NULL,
  `custodian_id` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`ohq_id`),
  KEY `material_code` (`material_code`),
  KEY `locator_id` (`locator_id`),
  CONSTRAINT `ohq_master_consumable_ibfk_1` FOREIGN KEY (`material_code`) REFERENCES `material_master` (`material_code`) ON UPDATE CASCADE,
  CONSTRAINT `ohq_master_consumable_ibfk_2` FOREIGN KEY (`locator_id`) REFERENCES `locator_master` (`locator_id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `payment_voucher`
--

DROP TABLE IF EXISTS `payment_voucher`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment_voucher` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `payment_voucher_number` varchar(255) DEFAULT NULL,
  `payment_voucher_date` varchar(50) DEFAULT NULL,
  `payment_voucher_is_for` varchar(100) DEFAULT NULL,
  `purchase_order_id` varchar(100) DEFAULT NULL,
  `grn_number` varchar(100) DEFAULT NULL,
  `service_order_details` varchar(500) DEFAULT NULL,
  `payment_voucher_type` varchar(100) NOT NULL,
  `vendor_name` varchar(255) DEFAULT NULL,
  `vendor_invoice_number` varchar(255) NOT NULL,
  `vendor_invoice_date` varchar(50) DEFAULT NULL,
  `currency` varchar(50) NOT NULL,
  `exchange_rate` varchar(50) DEFAULT NULL,
  `status` varchar(100) DEFAULT NULL,
  `remarks` varchar(500) DEFAULT NULL,
  `total_amount` decimal(15,2) DEFAULT NULL,
  `partial_amount` decimal(15,2) DEFAULT NULL,
  `advance_amount` decimal(15,2) DEFAULT NULL,
  `paid_amount` decimal(15,2) DEFAULT NULL,
  `so_id` varchar(50) DEFAULT NULL,
  `created_by` int DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `payment_voucher_net_amount` decimal(19,2) DEFAULT NULL,
  `tds_amount` decimal(19,2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `payment_voucher_materials`
--

DROP TABLE IF EXISTS `payment_voucher_materials`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment_voucher_materials` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `material_code` varchar(100) DEFAULT NULL,
  `material_description` varchar(500) DEFAULT NULL,
  `quantity` decimal(18,2) DEFAULT NULL,
  `unit_price` decimal(18,2) DEFAULT NULL,
  `currency` varchar(50) DEFAULT NULL,
  `exchange_rate` decimal(18,2) DEFAULT NULL,
  `gst` decimal(5,2) DEFAULT NULL,
  `payment_voucher_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_payment_voucher` (`payment_voucher_id`),
  CONSTRAINT `fk_payment_voucher` FOREIGN KEY (`payment_voucher_id`) REFERENCES `payment_voucher` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `project_master`
--

DROP TABLE IF EXISTS `project_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `project_master` (
  `project_code` varchar(255) NOT NULL,
  `project_name_description` varchar(255) DEFAULT NULL,
  `financial_year` varchar(20) DEFAULT NULL,
  `allocated_amount` decimal(15,2) DEFAULT NULL,
  `department_division` varchar(255) DEFAULT NULL,
  `budget_type` varchar(255) DEFAULT NULL,
  `category` varchar(100) DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `status` varchar(50) DEFAULT 'Active' COMMENT 'Active, Completed, Closed',
  `remarks_notes` text,
  `project_head` varchar(255) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `available_project_limit` decimal(15,2) DEFAULT NULL,
  `project_head_name` varchar(150) DEFAULT NULL,
  `budget_code` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`project_code`),
  KEY `idx_project_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `purchase_order`
--

DROP TABLE IF EXISTS `purchase_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `purchase_order` (
  `po_id` varchar(255) NOT NULL,
  `tender_id` varchar(255) DEFAULT NULL,
  `indent_id` varchar(255) DEFAULT NULL,
  `warranty` varchar(50) DEFAULT NULL,
  `consignes_address` varchar(255) DEFAULT NULL,
  `billing_address` varchar(255) DEFAULT NULL,
  `delivery_period` decimal(10,2) DEFAULT NULL,
  `if_ld_clause_applicable` tinyint(1) DEFAULT NULL,
  `inco_terms` varchar(255) DEFAULT NULL,
  `payment_terms` varchar(255) DEFAULT NULL,
  `vendor_name` varchar(255) DEFAULT NULL,
  `vendor_address` varchar(255) DEFAULT NULL,
  `applicable_pbg_to_be_submitted` varchar(255) DEFAULT NULL,
  `transporter_and_freight_for_warder_details` varchar(255) DEFAULT NULL,
  `vendor_account_number` varchar(255) DEFAULT NULL,
  `vendors_zfsc_code` varchar(255) DEFAULT NULL,
  `vendor_account_name` varchar(255) DEFAULT NULL,
  `created_by` int DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `total_value_of_po` decimal(10,0) DEFAULT NULL,
  `project_name` varchar(200) DEFAULT NULL,
  `vendor_id` varchar(255) DEFAULT NULL,
  `delivery_date` date DEFAULT NULL,
  `comparative_statement_file_name` varchar(300) DEFAULT NULL,
  `gem_contract_file_name` varchar(500) DEFAULT NULL,
  `type_of_security` varchar(255) DEFAULT NULL,
  `security_number` varchar(255) DEFAULT NULL,
  `security_date` date DEFAULT NULL,
  `expiry_date` date DEFAULT NULL,
  `quotation_number` varchar(255) DEFAULT NULL,
  `quotation_date` date DEFAULT NULL,
  `additional_terms_and_conditions` varchar(500) DEFAULT NULL,
  `buy_back_amount` decimal(18,2) DEFAULT NULL,
  `current_status` varchar(255) DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `is_cancelled` bit(1) DEFAULT NULL,
  `is_locked` bit(1) DEFAULT NULL,
  `locked_by` int DEFAULT NULL,
  `locked_date` datetime DEFAULT NULL,
  `po_version` int DEFAULT NULL,
  `parent_po_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`po_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `purchase_order_attributes`
--

DROP TABLE IF EXISTS `purchase_order_attributes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `purchase_order_attributes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `material_code` varchar(255) DEFAULT NULL,
  `po_id` varchar(255) NOT NULL,
  `material_description` text,
  `quantity` decimal(10,2) DEFAULT NULL,
  `rate` decimal(10,2) DEFAULT NULL,
  `currency` varchar(50) DEFAULT NULL,
  `exchange_rate` decimal(10,2) DEFAULT NULL,
  `gst` decimal(10,2) DEFAULT NULL,
  `duties` decimal(10,2) DEFAULT NULL,
  `freight_charge` decimal(10,2) DEFAULT NULL,
  `budget_code` varchar(255) DEFAULT NULL,
  `received_quantity` decimal(19,2) DEFAULT NULL,
  `total_po_material_price_in_inr` decimal(15,2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `po_id` (`po_id`),
  CONSTRAINT `purchase_order_attributes_ibfk_1` FOREIGN KEY (`po_id`) REFERENCES `purchase_order` (`po_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `purchase_order_history`
--

DROP TABLE IF EXISTS `purchase_order_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `purchase_order_history` (
  `history_id` int NOT NULL AUTO_INCREMENT,
  `po_id` varchar(255) DEFAULT NULL,
  `version` int DEFAULT NULL,
  `snapshot_json` longtext,
  `modified_by` int DEFAULT NULL,
  `modified_date` datetime DEFAULT NULL,
  PRIMARY KEY (`history_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role_master`
--

DROP TABLE IF EXISTS `role_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role_master` (
  `roleId` int NOT NULL AUTO_INCREMENT,
  `roleName` varchar(100) DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `createdBy` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`roleId`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `service_order`
--

DROP TABLE IF EXISTS `service_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `service_order` (
  `so_id` varchar(255) NOT NULL,
  `tender_id` varchar(255) DEFAULT NULL,
  `consignes_address` varchar(255) DEFAULT NULL,
  `billing_address` varchar(255) DEFAULT NULL,
  `job_completion_period` decimal(10,2) DEFAULT NULL,
  `if_ld_clause_applicable` tinyint(1) DEFAULT NULL,
  `inco_terms` varchar(255) DEFAULT NULL,
  `payment_terms` varchar(255) DEFAULT NULL,
  `vendor_name` varchar(255) DEFAULT NULL,
  `vendor_address` varchar(255) DEFAULT NULL,
  `applicable_pbg_to_be_submitted` varchar(255) DEFAULT NULL,
  `vendors_account_no` varchar(255) DEFAULT NULL,
  `vendors_zrsc_code` varchar(255) DEFAULT NULL,
  `vendors_account_name` varchar(255) DEFAULT NULL,
  `created_by` int DEFAULT NULL,
  `updated_by` varchar(200) DEFAULT NULL,
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `total_value_of_so` decimal(10,0) DEFAULT NULL,
  `project_name` varchar(200) DEFAULT NULL,
  `vendor_id` varchar(255) DEFAULT NULL,
  `start_date_amc` date DEFAULT NULL,
  `end_date_amc` date DEFAULT NULL,
  `current_status` varchar(45) DEFAULT 'DRAFT',
  `is_active` bit(1) DEFAULT NULL,
  `parent_so_id` varchar(255) DEFAULT NULL,
  `so_version` int DEFAULT NULL,
  PRIMARY KEY (`so_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `service_order_material`
--

DROP TABLE IF EXISTS `service_order_material`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `service_order_material` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `material_code` varchar(255) DEFAULT NULL,
  `so_id` varchar(255) DEFAULT NULL,
  `material_description` varchar(255) DEFAULT NULL,
  `quantity` decimal(19,2) DEFAULT NULL,
  `rate` decimal(19,2) DEFAULT NULL,
  `exchange_rate` decimal(19,2) DEFAULT NULL,
  `currency` varchar(255) DEFAULT NULL,
  `gst` decimal(19,2) DEFAULT NULL,
  `duties` decimal(19,2) DEFAULT NULL,
  `budget_code` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `so_id` (`so_id`),
  CONSTRAINT `service_order_material_ibfk_1` FOREIGN KEY (`so_id`) REFERENCES `service_order` (`so_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `state_master`
--

DROP TABLE IF EXISTS `state_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `state_master` (
  `stateId` int NOT NULL AUTO_INCREMENT,
  `stateName` varchar(255) NOT NULL,
  `createdDate` datetime DEFAULT NULL,
  `createdBy` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`stateId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sub_workflow_transition`
--

DROP TABLE IF EXISTS `sub_workflow_transition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sub_workflow_transition` (
  `subWorkflowTransitionId` int NOT NULL AUTO_INCREMENT,
  `workflowId` int NOT NULL,
  `workflowName` varchar(255) NOT NULL,
  `workflowTransitionId` int NOT NULL,
  `requestId` varchar(255) NOT NULL,
  `createdBy` int NOT NULL,
  `modifiedBy` int DEFAULT NULL,
  `status` varchar(255) NOT NULL,
  `action` varchar(45) DEFAULT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  `actionOn` int DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `modificationDate` datetime DEFAULT NULL,
  `workflowSequence` int NOT NULL,
  `transitionName` varchar(100) DEFAULT NULL,
  `transitionType` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`subWorkflowTransitionId`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `subcategory_master`
--

DROP TABLE IF EXISTS `subcategory_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `subcategory_master` (
  `Id` int NOT NULL AUTO_INCREMENT,
  `sub_category_id` varchar(45) NOT NULL,
  `sub_category_value` varchar(45) NOT NULL,
  `sequence` varchar(45) DEFAULT NULL,
  `is_active` varchar(1) DEFAULT NULL,
  `attribute1` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `techno_financial_committee`
--

DROP TABLE IF EXISTS `techno_financial_committee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `techno_financial_committee` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `employee_id` varchar(50) DEFAULT NULL,
  `member_name` varchar(200) NOT NULL,
  `designation` varchar(200) DEFAULT NULL,
  `email_address` varchar(200) DEFAULT NULL,
  `role` varchar(20) NOT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_by` varchar(100) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `committee_type` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tender_committee_decision`
--

DROP TABLE IF EXISTS `tender_committee_decision`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tender_committee_decision` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `tender_id` varchar(100) NOT NULL,
  `committee_user_id` int DEFAULT NULL,
  `committee_member_name` varchar(200) DEFAULT NULL,
  `vote` varchar(20) DEFAULT NULL,
  `vote_remarks` varchar(1000) DEFAULT NULL,
  `voted_date` datetime DEFAULT NULL,
  `expert_user_id` int DEFAULT NULL,
  `expert_name` varchar(200) DEFAULT NULL,
  `expert_assigned_date` datetime DEFAULT NULL,
  `chairman_decision` varchar(20) DEFAULT NULL,
  `chairman_remarks` varchar(1000) DEFAULT NULL,
  `chairman_override_used` tinyint(1) DEFAULT '0',
  `chairman_decision_date` datetime DEFAULT NULL,
  `director_decision` varchar(20) DEFAULT NULL,
  `director_remarks` varchar(1000) DEFAULT NULL,
  `director_decision_date` datetime DEFAULT NULL,
  `director_user_id` int DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tender_evaluation`
--

DROP TABLE IF EXISTS `tender_evaluation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tender_evaluation` (
  `tender_id` varchar(255) NOT NULL,
  `upload_qualified_vendors_file_name` varchar(255) DEFAULT NULL,
  `upload_technically_qualified_vendors_file_name` varchar(255) DEFAULT NULL,
  `upload_commerially_qualified_vendors_file_name` varchar(255) DEFAULT NULL,
  `formation_of_techno_commerial_comitee` varchar(255) DEFAULT NULL,
  `response_file_name` varchar(255) DEFAULT NULL,
  `response_for_technically_qualified_vendors_file_name` varchar(255) DEFAULT NULL,
  `response_for_commerially_qualified_vendors_file_name` varchar(255) DEFAULT NULL,
  `file_type` varchar(255) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `created_by` int DEFAULT NULL,
  `created_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `upload_qualified_vendors_file_name_created_by` int DEFAULT NULL,
  `upload_technically_qualified_vendors_file_name_created_by` int DEFAULT NULL,
  `upload_commerially_qualified_vendors_file_name_created_by` int DEFAULT NULL,
  `formation_of_techno_commerial_comitee_created_by` int DEFAULT NULL,
  `response_file_name_created_by` int DEFAULT NULL,
  `response_for_technically_qualified_vendors_file_name_created_by` int DEFAULT NULL,
  `response_for_commerially_qualified_vendors_file_name_created_by` int DEFAULT NULL,
  PRIMARY KEY (`tender_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tender_request`
--

DROP TABLE IF EXISTS `tender_request`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tender_request` (
  `tender_id` varchar(255) NOT NULL,
  `title_of_tender` varchar(255) NOT NULL,
  `opening_date` date DEFAULT NULL,
  `closing_date` date DEFAULT NULL,
  `indent_materials` varchar(200) DEFAULT NULL,
  `mode_of_procurement` varchar(255) DEFAULT NULL,
  `bid_type` varchar(255) DEFAULT NULL,
  `last_date_of_submission` date DEFAULT NULL,
  `applicable_taxes` text,
  `billinng_address` varchar(255) DEFAULT NULL,
  `inco_terms` varchar(255) DEFAULT NULL,
  `payment_terms` varchar(255) DEFAULT NULL,
  `performance_and_warranty_security` varchar(255) DEFAULT NULL,
  `bid_security_declaration` tinyint(1) DEFAULT NULL,
  `mll_status_declaration` tinyint(1) DEFAULT NULL,
  `upload_tender_documents` blob,
  `single_and_multiple_vendors` varchar(255) DEFAULT NULL,
  `upload_general_terms_and_conditions` blob,
  `upload_specific_terms_and_conditions` blob,
  `pre_bid_disscussions` text,
  `updated_by` varchar(255) DEFAULT NULL,
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `upload_tender_documents_file_name` varchar(255) DEFAULT NULL,
  `upload_general_terms_and_conditions_file_name` varchar(255) DEFAULT NULL,
  `upload_specific_terms_and_conditions_file_name` varchar(255) DEFAULT NULL,
  `total_tender_value` decimal(20,2) DEFAULT NULL,
  `created_by` int DEFAULT NULL,
  `project_name` varchar(255) DEFAULT NULL,
  `file_type` varchar(255) DEFAULT NULL,
  `consignes` varchar(255) DEFAULT NULL,
  `tender_number` int DEFAULT NULL,
  `bid_security_declaration_file_name` varchar(255) DEFAULT NULL,
  `mll_status_declaration_file_name` varchar(255) DEFAULT NULL,
  `quotation_file_name` varchar(255) DEFAULT NULL,
  `vendor_id` varchar(255) DEFAULT NULL,
  `cancel_status` tinyint(1) DEFAULT '0',
  `cancel_remarks` varchar(1000) DEFAULT NULL,
  `ld_clause` tinyint(1) DEFAULT '0',
  `buy_back` tinyint(1) DEFAULT NULL,
  `buy_back_amount` varchar(255) DEFAULT NULL,
  `model_number` varchar(255) DEFAULT NULL,
  `serial_number` varchar(255) DEFAULT NULL,
  `date_of_purchase` date DEFAULT NULL,
  `upload_buy_back_file_names` varchar(500) DEFAULT NULL,
  `tender_version` int DEFAULT '1' COMMENT 'TC_44: Auto-incremented version number, starts at 1',
  `update_reason` varchar(1000) DEFAULT NULL COMMENT 'TC_46: Reason provided by user when updating tender',
  `pre_bid_meeting_status` varchar(50) DEFAULT 'NOT_CONDUCTED' COMMENT 'TC_47: Status of pre-bid meeting - NOT_CONDUCTED, SCHEDULED, or CONDUCTED',
  `pre_bid_meeting_discussion` text COMMENT 'TC_47: Discussion points from pre-bid meeting',
  `pre_bid_meeting_date` date DEFAULT NULL COMMENT 'TC_47: Date when pre-bid meeting was/will be held',
  `is_locked` tinyint(1) DEFAULT '0' COMMENT 'TC_48: TRUE when PO is created, prevents further tender updates',
  `locked_reason` varchar(500) DEFAULT NULL COMMENT 'TC_48: Reason why tender is locked (e.g., PO created)',
  `locked_for_po` varchar(50) DEFAULT NULL COMMENT 'TC_48: PO ID that caused the tender to be locked',
  `locked_date` datetime DEFAULT NULL COMMENT 'TC_48: Timestamp when tender was locked',
  `current_status` varchar(45) DEFAULT 'DRAFT',
  `is_active` tinyint(1) DEFAULT '1',
  `parent_tender_id` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`tender_id`),
  KEY `idx_tender_is_locked` (`is_locked`),
  KEY `idx_tender_prebid_status` (`pre_bid_meeting_status`),
  KEY `idx_tender_version` (`tender_version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `transition_condition_master`
--

DROP TABLE IF EXISTS `transition_condition_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transition_condition_master` (
  `conditionId` int NOT NULL AUTO_INCREMENT,
  `workflowId` int NOT NULL,
  `conditionKey` varchar(255) NOT NULL,
  `conditionValue` varchar(255) NOT NULL,
  `createdDate` datetime DEFAULT NULL,
  `createdBy` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`conditionId`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `transition_master`
--

DROP TABLE IF EXISTS `transition_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transition_master` (
  `transitionId` int NOT NULL AUTO_INCREMENT,
  `transitionName` varchar(255) NOT NULL,
  `workflowId` int NOT NULL,
  `currentRoleId` int NOT NULL,
  `nextRoleId` int DEFAULT NULL,
  `previousRoleId` int DEFAULT NULL,
  `conditionId` int DEFAULT NULL,
  `transitionOrder` int NOT NULL,
  `transitionSubOrder` int NOT NULL,
  `createdDate` datetime DEFAULT NULL,
  `createdBy` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`transitionId`)
) ENGINE=InnoDB AUTO_INCREMENT=77 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `uom_master`
--

DROP TABLE IF EXISTS `uom_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `uom_master` (
  `uom_code` varchar(50) NOT NULL,
  `uom_name` varchar(255) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`uom_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_master`
--

DROP TABLE IF EXISTS `user_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_master` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `password` varchar(255) DEFAULT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `mobile_number` varchar(255) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `role_name` varchar(255) DEFAULT NULL,
  `employee_id` varchar(50) DEFAULT NULL,
  `is_first_login` tinyint(1) DEFAULT '1',
  `last_password_change_date` datetime DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=184 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_role_master`
--

DROP TABLE IF EXISTS `user_role_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_role_master` (
  `userRoleId` int NOT NULL AUTO_INCREMENT,
  `userId` int NOT NULL,
  `roleId` int NOT NULL,
  `readPermission` tinyint(1) NOT NULL,
  `writePermission` tinyint(1) NOT NULL,
  `createdDate` datetime DEFAULT NULL,
  `createdBy` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`userRoleId`)
) ENGINE=InnoDB AUTO_INCREMENT=572 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vendor_id_sequence`
--

DROP TABLE IF EXISTS `vendor_id_sequence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vendor_id_sequence` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `vendor_id` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=84 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vendor_id_tracker`
--

DROP TABLE IF EXISTS `vendor_id_tracker`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vendor_id_tracker` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `primary_business` varchar(100) NOT NULL,
  `prefix` varchar(10) NOT NULL,
  `last_sequence` int DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `primary_business` (`primary_business`),
  UNIQUE KEY `prefix` (`prefix`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vendor_login_details`
--

DROP TABLE IF EXISTS `vendor_login_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vendor_login_details` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `vendor_id` varchar(255) DEFAULT NULL,
  `email_address` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `email_sent` tinyint(1) DEFAULT NULL,
  `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `is_first_login` tinyint(1) DEFAULT '1',
  `is_temp_password` tinyint(1) DEFAULT '1',
  `password_changed_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vendor_master`
--

DROP TABLE IF EXISTS `vendor_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vendor_master` (
  `vendor_id` varchar(255) NOT NULL,
  `vendor_type` varchar(50) DEFAULT NULL,
  `vendor_name` varchar(100) DEFAULT NULL,
  `contact_no` varchar(300) DEFAULT NULL,
  `email_address` varchar(100) DEFAULT NULL,
  `registered_platform` varchar(10) DEFAULT NULL,
  `pfms_vendor_code` varchar(20) DEFAULT NULL,
  `primary_business` varchar(50) DEFAULT NULL,
  `address` text,
  `alternate_email_or_phone_number` varchar(300) DEFAULT NULL,
  `fax` varchar(20) DEFAULT NULL,
  `pan_no` varchar(20) DEFAULT NULL,
  `gst_no` varchar(20) DEFAULT NULL,
  `bank_name` varchar(50) DEFAULT NULL,
  `account_no` varchar(20) DEFAULT NULL,
  `ifsc_code` varchar(15) DEFAULT NULL,
  `purchase_history` text,
  `status` varchar(10) DEFAULT NULL,
  `updated_by` varchar(200) DEFAULT NULL,
  `created_by` mediumtext,
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `remarks` varchar(255) DEFAULT NULL,
  `swift_code` varchar(100) DEFAULT NULL,
  `bic_code` varchar(100) DEFAULT NULL,
  `iban_aba_number` varchar(100) DEFAULT NULL,
  `sort_code` varchar(100) DEFAULT NULL,
  `bank_routing_number` varchar(100) DEFAULT NULL,
  `bank_address` varchar(500) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `place` varchar(255) DEFAULT NULL,
  `reason_for_debar` varchar(255) DEFAULT NULL,
  `status_of_vendor_active_or_debar` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`vendor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vendor_master_util`
--

DROP TABLE IF EXISTS `vendor_master_util`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vendor_master_util` (
  `vendor_id` varchar(50) NOT NULL,
  `vendor_name` varchar(255) DEFAULT NULL,
  `vendor_type` varchar(100) DEFAULT NULL,
  `contact_number` varchar(20) DEFAULT NULL,
  `email_address` varchar(255) DEFAULT NULL,
  `registered_platform` tinyint(1) DEFAULT NULL,
  `pfms_vendor_code` varchar(100) DEFAULT NULL,
  `primary_business` varchar(255) DEFAULT NULL,
  `address` text,
  `alternate_email_or_phone_number` varchar(255) DEFAULT NULL,
  `fax_number` varchar(50) DEFAULT NULL,
  `pan_number` varchar(50) DEFAULT NULL,
  `gst_number` varchar(50) DEFAULT NULL,
  `bank_name` varchar(255) DEFAULT NULL,
  `account_number` varchar(50) DEFAULT NULL,
  `ifsc_code` varchar(50) DEFAULT NULL,
  `approval_status` enum('APPROVED','REJECTED','AWAITING_APPROVAL','CHANGE_REQUEST') DEFAULT NULL,
  `comments` text,
  `created_by` int DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `created_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `vendor_number` int DEFAULT NULL,
  `swift_code` varchar(100) DEFAULT NULL,
  `bic_code` varchar(100) DEFAULT NULL,
  `iban_aba_number` varchar(100) DEFAULT NULL,
  `sort_code` varchar(100) DEFAULT NULL,
  `bank_routing_number` varchar(100) DEFAULT NULL,
  `bank_address` varchar(500) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `place` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`vendor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vendor_names_for_job_work_material`
--

DROP TABLE IF EXISTS `vendor_names_for_job_work_material`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vendor_names_for_job_work_material` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `material_id` bigint NOT NULL,
  `vendor_name` varchar(255) NOT NULL,
  `job_code` varchar(255) DEFAULT NULL,
  `material_code` varchar(255) DEFAULT NULL,
  `work_code` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `indent_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `material_id` (`material_id`),
  CONSTRAINT `vendor_names_for_job_work_material_ibfk_1` FOREIGN KEY (`material_id`) REFERENCES `material_details` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=248 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vendor_quotation_against_tender`
--

DROP TABLE IF EXISTS `vendor_quotation_against_tender`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vendor_quotation_against_tender` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `tender_id` varchar(255) DEFAULT NULL,
  `vendor_id` varchar(255) DEFAULT NULL,
  `quotation_file_name` varchar(500) DEFAULT NULL,
  `file_type` varchar(50) DEFAULT NULL,
  `created_by` int DEFAULT NULL,
  `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `remarks` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `version` int DEFAULT NULL,
  `is_latest` tinyint(1) DEFAULT NULL,
  `acceptance_status` varchar(50) DEFAULT NULL,
  `price_bid_file_name` varchar(255) DEFAULT NULL,
  `indentor_status` varchar(50) DEFAULT NULL,
  `indentor_remarks` text,
  `spo_status` varchar(50) DEFAULT NULL,
  `spo_remarks` text,
  `change_request_to_indentor` tinyint(1) DEFAULT '0',
  `modified_by` int DEFAULT NULL,
  `current_role` varchar(50) DEFAULT NULL,
  `next_role` varchar(50) DEFAULT NULL,
  `clarification_file_name` varchar(255) DEFAULT NULL,
  `vendor_response` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=164 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `work_master`
--

DROP TABLE IF EXISTS `work_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `work_master` (
  `work_code` varchar(255) NOT NULL,
  `work_sub_category` varchar(255) DEFAULT NULL,
  `mode_of_procurement` varchar(255) DEFAULT NULL,
  `work_description` text,
  `created_by` int DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`work_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `work_order`
--

DROP TABLE IF EXISTS `work_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `work_order` (
  `wo_id` varchar(255) NOT NULL,
  `tender_id` varchar(255) DEFAULT NULL,
  `consignes_address` varchar(255) DEFAULT NULL,
  `billing_address` varchar(255) DEFAULT NULL,
  `job_completion_period` decimal(10,2) DEFAULT NULL,
  `if_ld_clause_applicable` tinyint(1) DEFAULT NULL,
  `inco_terms` varchar(255) DEFAULT NULL,
  `payment_terms` varchar(255) DEFAULT NULL,
  `vendor_name` varchar(255) DEFAULT NULL,
  `vendor_address` varchar(255) DEFAULT NULL,
  `applicable_pbg_to_be_submitted` varchar(255) DEFAULT NULL,
  `vendors_account_no` varchar(255) DEFAULT NULL,
  `vendors_zrsc_code` varchar(255) DEFAULT NULL,
  `vendors_account_name` varchar(255) DEFAULT NULL,
  `created_by` int DEFAULT NULL,
  `updated_by` varchar(200) DEFAULT NULL,
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`wo_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `work_order_material`
--

DROP TABLE IF EXISTS `work_order_material`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `work_order_material` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `work_code` varchar(255) DEFAULT NULL,
  `work_description` varchar(255) DEFAULT NULL,
  `quantity` decimal(19,2) DEFAULT NULL,
  `rate` decimal(19,2) DEFAULT NULL,
  `exchange_rate` decimal(19,2) DEFAULT NULL,
  `currency` varchar(255) DEFAULT NULL,
  `gst` decimal(19,2) DEFAULT NULL,
  `duties` decimal(19,2) DEFAULT NULL,
  `budget_code` varchar(255) DEFAULT NULL,
  `wo_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `wo_id` (`wo_id`),
  CONSTRAINT `work_order_material_ibfk_1` FOREIGN KEY (`wo_id`) REFERENCES `work_order` (`wo_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `workflow_branch_master`
--

DROP TABLE IF EXISTS `workflow_branch_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `workflow_branch_master` (
  `branch_id` bigint NOT NULL AUTO_INCREMENT,
  `workflow_id` int NOT NULL,
  `branch_code` varchar(50) NOT NULL,
  `branch_name` varchar(200) NOT NULL,
  `branch_description` text,
  `condition_type` varchar(50) DEFAULT NULL COMMENT 'CATEGORY, LOCATION, AMOUNT, CUSTOM',
  `condition_config` json DEFAULT NULL COMMENT 'Stores condition rules as JSON',
  `is_active` tinyint(1) DEFAULT '1',
  `display_order` int DEFAULT '0',
  `created_by` varchar(100) DEFAULT NULL,
  `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `condition_logic` varchar(10) DEFAULT 'AND',
  `requires_budget_check` tinyint(1) DEFAULT '0',
  `budget_check_config` json DEFAULT NULL,
  PRIMARY KEY (`branch_id`),
  UNIQUE KEY `uk_workflow_branch_code` (`workflow_id`,`branch_code`),
  UNIQUE KEY `UK2tsniev0swwqe7uc8eaxy3pen` (`workflow_id`,`branch_code`),
  KEY `idx_branch_active` (`is_active`),
  CONSTRAINT `fk_branch_workflow` FOREIGN KEY (`workflow_id`) REFERENCES `workflow_master` (`workflowId`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=369 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `workflow_master`
--

DROP TABLE IF EXISTS `workflow_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `workflow_master` (
  `workflowId` int NOT NULL AUTO_INCREMENT,
  `workflowName` varchar(255) NOT NULL,
  `createdDate` datetime DEFAULT NULL,
  `createdBy` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`workflowId`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `workflow_transition`
--

DROP TABLE IF EXISTS `workflow_transition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `workflow_transition` (
  `workflowTransitionId` int NOT NULL AUTO_INCREMENT,
  `workflowId` int NOT NULL,
  `workflowName` varchar(255) NOT NULL,
  `transitionId` int DEFAULT NULL,
  `requestId` varchar(255) NOT NULL,
  `createdBy` int NOT NULL,
  `modifiedBy` int DEFAULT NULL,
  `status` varchar(255) NOT NULL,
  `nextAction` varchar(100) DEFAULT NULL,
  `transitionOrder` int NOT NULL,
  `transitionSubOrder` int NOT NULL,
  `action` varchar(45) DEFAULT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  `currentRole` varchar(255) DEFAULT NULL,
  `nextRole` varchar(255) DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `modificationDate` datetime DEFAULT NULL,
  `workflowSequence` int NOT NULL,
  `BRANCH_ID` bigint DEFAULT NULL COMMENT 'Links to workflow_branch_master for branch-based workflows',
  `APPROVER_ID` bigint DEFAULT NULL COMMENT 'Links to approver_master for current approver',
  `APPROVAL_LEVEL` int DEFAULT NULL COMMENT 'Current approval level in the branch approval chain',
  `APPROVAL_SEQUENCE` int DEFAULT NULL COMMENT 'Approval sequence within the same level',
  `assigned_to_employee_id` varchar(50) DEFAULT NULL,
  `assigned_to_user_id` int DEFAULT NULL,
  PRIMARY KEY (`workflowTransitionId`),
  KEY `idx_workflow_transition_branch_id` (`BRANCH_ID`),
  KEY `idx_workflow_transition_approver_id` (`APPROVER_ID`),
  KEY `idx_workflow_transition_approval_level` (`APPROVAL_LEVEL`,`APPROVAL_SEQUENCE`)
) ENGINE=InnoDB AUTO_INCREMENT=1769 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping events for database 'astrodatabase'
--

--
-- Dumping routines for database 'astrodatabase'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-07 12:45:56
