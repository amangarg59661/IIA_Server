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
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `designator_master`
--

LOCK TABLES `designator_master` WRITE;
/*!40000 ALTER TABLE `designator_master` DISABLE KEYS */;
INSERT INTO `designator_master` VALUES (1,1,'consigneeLocation','Consignee Location',NULL,'STRING',1,1,'SYSTEM','2025-12-26 17:03:40'),(2,2,'deliveryPeriod','Delivery Period',NULL,'STRING',1,1,'SYSTEM','2025-12-26 17:03:40'),(3,2,'warranty','Warranty',NULL,'STRING',1,2,'SYSTEM','2025-12-26 17:03:40'),(4,2,'applicablePbgToBeSubmitted','Applicable PBG to be Submitted',NULL,'STRING',1,3,'SYSTEM','2025-12-26 17:03:40'),(5,3,'incoTerms','INCO Terms',NULL,'STRING',1,1,'SYSTEM','2025-12-26 17:03:40'),(6,3,'paymentTerms','Payment Terms',NULL,'STRING',1,2,'SYSTEM','2025-12-26 17:03:40'),(7,5,'status','Status',NULL,'STRING',1,1,'SYSTEM','2025-12-26 17:03:40'),(8,6,'status','Status',NULL,'STRING',1,1,'SYSTEM','2025-12-26 17:03:40'),(9,6,'budgetType','Budget Type',NULL,'STRING',1,2,'SYSTEM','2025-12-26 17:03:40'),(10,8,'locator','Locator',NULL,'STRING',1,1,'SYSTEM','2025-12-26 17:03:40'),(11,4,'gstPercentage','GST (%)',NULL,'STRING',1,1,'SYSTEM','2025-12-26 17:03:40'),(12,4,'paymentTo','Payment To',NULL,'STRING',1,2,'SYSTEM','2025-12-26 17:03:40'),(13,7,'department','Department',NULL,'STRING',1,1,'SYSTEM','2025-12-26 17:03:40'),(14,7,'designation','Designation',NULL,'STRING',1,2,'SYSTEM','2025-12-26 17:03:40'),(15,7,'location','Location',NULL,'STRING',1,3,'SYSTEM','2025-12-26 17:03:40'),(16,9,'jobCategory','Job Category',NULL,'STRING',1,1,'SYSTEM','2025-12-26 17:03:40'),(17,9,'jobSubCategory','Job SubCategory',NULL,'STRING',1,2,'SYSTEM','2025-12-26 17:03:40'),(18,9,'uom','UOM',NULL,'STRING',1,3,'SYSTEM','2025-12-26 17:03:40'),(19,9,'currency','Currency',NULL,'STRING',1,4,'SYSTEM','2025-12-26 17:03:40'),(20,10,'category','Category',NULL,'STRING',1,1,'SYSTEM','2025-12-26 17:03:40'),(21,10,'subCategory','SubCategory',NULL,'STRING',1,2,'SYSTEM','2025-12-26 17:03:40'),(22,10,'uom','UOM',NULL,'STRING',1,3,'SYSTEM','2025-12-26 17:03:40'),(23,10,'currency','Currency',NULL,'STRING',1,4,'SYSTEM','2025-12-26 17:03:40'),(24,11,'primaryBusiness','Primary Business',NULL,'STRING',1,1,'SYSTEM','2025-12-26 17:03:40'),(25,6,'department','Department','Department/Division for the project','STRING',1,3,'SYSTEM','2026-02-13 10:12:43'),(26,6,'category','Category','Project category classification','STRING',1,4,'SYSTEM','2026-02-13 10:12:43');
/*!40000 ALTER TABLE `designator_master` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-29 21:41:28
