-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: astrodatabase
-- ------------------------------------------------------
-- Server version	8.0.44

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
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `budget_master`
--

LOCK TABLES `budget_master` WRITE;
/*!40000 ALTER TABLE `budget_master` DISABLE KEYS */;
INSERT INTO `budget_master` (`budget_id`, `budget_code`, `budget_name`, `category`, `allocated_amount`, `on_hold_amount`, `spent_amount`, `fiscal_year`, `start_date`, `end_date`, `status`, `project_code`, `department_name`, `created_by`, `updated_by`, `created_date`, `updated_date`) VALUES (11,'Ritwik81','Ritwik Budget','Consumable',45634345.00,0.00,0.00,'2026','2026-02-24','2027-02-27','Active','Ritwik81','Engineering','admin',NULL,'2026-02-24 03:19:24','2026-02-24 03:19:42'),(12,'Aman81','Aman Budget','Consumable',432432432.00,0.00,32423.00,'2026','2026-02-24','2027-02-27','Active','Aman81','Engineering','admin',NULL,'2026-02-24 03:21:11','2026-02-24 03:21:44'),(13,'Abhi01','Abhi01','Capital',20000000.00,0.00,0.00,'2026','2026-03-01','2026-03-31','Active','Abhi01','IT','admin',NULL,'2026-03-09 05:17:34','2026-03-09 05:18:48'),(14,'Abhi02','Abhi02','Capital',100000.00,0.00,0.00,'2026','2026-03-10','2026-03-31','Active','Abhi02','IT','admin',NULL,'2026-03-09 23:40:55','2026-03-09 23:42:19');
/*!40000 ALTER TABLE `budget_master` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-29 21:41:32
