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
-- Dumping data for table `ogp_asset_disposal`
--

LOCK TABLES `ogp_asset_disposal` WRITE;
/*!40000 ALTER TABLE `ogp_asset_disposal` DISABLE KEYS */;
INSERT INTO `ogp_asset_disposal` VALUES (1,1,'ASSET1','2025-09-18',1200.00,13000.00,'uday','APPROVED',1,'2025-09-17 15:55:34'),(2,4,'ASSET4','2025-10-22',1200.00,13000.00,'kapil dev2 enterprise','APPROVED',1,'2025-10-06 12:22:36'),(3,5,'ASSET89','2025-11-27',1000.00,2000.00,'kapil enterprise','APPROVED',1,'2025-11-03 12:29:35');
/*!40000 ALTER TABLE `ogp_asset_disposal` ENABLE KEYS */;
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
