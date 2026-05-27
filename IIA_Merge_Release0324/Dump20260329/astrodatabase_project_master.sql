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
-- Dumping data for table `project_master`
--

LOCK TABLES `project_master` WRITE;
/*!40000 ALTER TABLE `project_master` DISABLE KEYS */;
INSERT INTO `project_master` VALUES ('Abhi01','Abhinay Infra','2026',100000.00,'Admin','OPEX','Consumables','2026-03-01','2026-03-31','IN_PROGRESS',NULL,'E1159','admin',NULL,'2026-03-09 05:18:48','2026-03-09 05:18:48',100000.00,'Krishna Kumar','Abhi01'),('Abhi02','Abhinay Infra 2','2026',10000.00,'Admin','CAPEX','Consumables','2026-03-10','2026-03-31','IN_PROGRESS',NULL,'E1152','admin',NULL,'2026-03-09 23:42:19','2026-03-09 23:42:19',10000.00,'Yan Kumar','Abhi02'),('Aman81','Aman Project','2026',454354354543.00,'Finance','Consumable','Infrastructure Upgrade','2026-02-24','2027-02-27','Active',NULL,'E1122','admin',NULL,'2026-02-24 03:21:44','2026-02-24 03:21:44',5435435.00,'Skrossi SK','Aman81'),('Ritwik81','Ritwik Project','2026',3423432432.00,'Information Technology (IT)','Operational','Software Development','2026-02-24','2027-02-27','Active',NULL,'E1122','admin',NULL,'2026-02-24 03:19:42','2026-02-24 03:19:42',32432.00,'Skrossi SK','Ritwik81');
/*!40000 ALTER TABLE `project_master` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-29 21:41:36
