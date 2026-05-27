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
-- Dumping data for table `ogp_asset_disposal_detail`
--

LOCK TABLES `ogp_asset_disposal_detail` WRITE;
/*!40000 ALTER TABLE `ogp_asset_disposal_detail` DISABLE KEYS */;
INSERT INTO `ogp_asset_disposal_detail` VALUES (1,1,6,8,'Desktop',1.00,3,250000.00,0.00,250000.00,'18',111003750.00,'not good','2025-09-18','BNG','Disposed',NULL,NULL),(2,2,7,8,'Desktop',1.00,3,250000.00,0.00,250000.00,'18',111003750.00,'not good','2025-10-15','BNG','Disposed',NULL,NULL),(3,3,13,14,'mouse hp',1.00,2,50000.00,0.00,50000.00,'46',100000.00,'not working','2025-11-19','BNG','Disposed','BNGCOM2526-014','S3');
/*!40000 ALTER TABLE `ogp_asset_disposal_detail` ENABLE KEYS */;
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
