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
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `asset_disposal`
--

LOCK TABLES `asset_disposal` WRITE;
/*!40000 ALTER TABLE `asset_disposal` DISABLE KEYS */;
INSERT INTO `asset_disposal` VALUES (1,'2025-07-09',18,'2025-07-09 16:22:20','BNG','9020006',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(3,'2025-07-10',18,'2025-07-09 16:33:08','BNG','2022',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(6,'2025-09-18',18,'2025-09-17 15:52:28','BNG',NULL,'18','Disposed','Approved','ASSET1',NULL,NULL,NULL,NULL),(7,'2025-10-15',18,'2025-10-06 11:42:12','BNG',NULL,'18','Disposed','Approved','ASSET4',NULL,NULL,NULL,NULL),(8,'2025-10-07',43,'2025-10-07 12:15:48','BNG',NULL,'45','Removed','Approved',NULL,NULL,NULL,NULL,NULL),(9,'2025-10-23',18,'2025-10-13 12:32:49','BNG',NULL,'18','For Disposal','Approved',NULL,NULL,NULL,NULL,NULL),(10,'2025-10-20',18,'2025-10-13 18:14:31','BNG',NULL,'18','For Disposal','Approved',NULL,NULL,NULL,NULL,NULL),(11,'2025-10-24',45,'2025-10-24 12:39:44','BNG',NULL,'45','For Disposal','Awaiting For Approval',NULL,NULL,NULL,NULL,NULL),(12,'2025-11-11',46,'2025-11-03 11:57:14','BNG',NULL,'46','Removed','Approved',NULL,NULL,NULL,NULL,NULL),(13,'2025-11-19',46,'2025-11-03 12:27:55','BNG',NULL,'46','Disposed','Approved','ASSET89',NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `asset_disposal` ENABLE KEYS */;
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
