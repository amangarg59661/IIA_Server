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
-- Dumping data for table `form_master`
--

LOCK TABLES `form_master` WRITE;
/*!40000 ALTER TABLE `form_master` DISABLE KEYS */;
INSERT INTO `form_master` VALUES (1,'IndentCreation','Indent Creation','Indent/Requisition creation and management','Procurement',1,1,'SYSTEM','2025-12-26 17:03:40'),(2,'PurchaseOrder','Purchase Order','Purchase order management','Procurement',1,2,'SYSTEM','2025-12-26 17:03:40'),(3,'TenderRequest','Tender Request','Tender request management','Procurement',1,3,'SYSTEM','2025-12-26 17:03:40'),(4,'ContingencyPurchase','Contingency Purchase','Contingency purchase management','Procurement',1,4,'SYSTEM','2025-12-26 17:03:40'),(5,'BudgetMaster','Budget Master','Budget master data management','Admin',1,5,'SYSTEM','2025-12-26 17:03:40'),(6,'ProjectMaster','Project Master','Project master data management','Admin',1,6,'SYSTEM','2025-12-26 17:03:40'),(7,'EmployeeRegistration','Employee Registration','Employee registration and management','Admin',1,7,'SYSTEM','2025-12-26 17:03:40'),(8,'AssetMaster','Asset Master','Asset inventory management','Inventory',1,8,'SYSTEM','2025-12-26 17:03:40'),(9,'JobMaster','Job Master','Job/Service master data','MasterData',1,9,'SYSTEM','2025-12-26 17:03:40'),(10,'MaterialMaster','Material Master','Material master data','MasterData',1,10,'SYSTEM','2025-12-26 17:03:40'),(11,'VendorMaster','Vendor Master','Vendor master data','MasterData',1,11,'SYSTEM','2025-12-26 17:03:40');
/*!40000 ALTER TABLE `form_master` ENABLE KEYS */;
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
