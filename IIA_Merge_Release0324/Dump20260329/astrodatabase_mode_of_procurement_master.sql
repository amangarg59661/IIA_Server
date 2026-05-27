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
-- Dumping data for table `mode_of_procurement_master`
--

LOCK TABLES `mode_of_procurement_master` WRITE;
/*!40000 ALTER TABLE `mode_of_procurement_master` DISABLE KEYS */;
INSERT INTO `mode_of_procurement_master` VALUES (1,'GEM','GeM (Government e-Marketplace)','Procurement through Government e-Marketplace portal',0,1,1,'2026-01-22 14:23:43','2026-01-22 14:23:43'),(2,'OPEN_TENDER','Open Tender','Open competitive bidding',1,1,2,'2026-01-22 14:23:43','2026-01-22 14:23:43'),(3,'LIMITED_TENDER','Limited Tender','Limited competitive bidding with selected vendors',1,1,3,'2026-01-22 14:23:43','2026-01-22 14:23:43'),(4,'SINGLE_TENDER','Single Tender','Single source procurement',1,1,4,'2026-01-22 14:23:43','2026-01-22 14:23:43'),(5,'PROPRIETARY','Proprietary Purchase','Procurement from sole/proprietary source',0,1,5,'2026-01-22 14:23:43','2026-01-22 14:23:43'),(6,'RATE_CONTRACT','Rate Contract','Procurement under existing rate contract',0,1,6,'2026-01-22 14:23:43','2026-01-22 14:23:43'),(7,'DIRECT_PURCHASE','Direct Purchase','Direct purchase without tender (for small value items)',0,1,7,'2026-01-22 14:23:43','2026-01-22 14:23:43'),(8,'QUOTATION','Quotation Based','Procurement based on quotations',0,1,8,'2026-01-22 14:23:43','2026-01-22 14:23:43');
/*!40000 ALTER TABLE `mode_of_procurement_master` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-29 21:41:34
