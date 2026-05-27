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
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `asset_disposal_detail`
--

LOCK TABLES `asset_disposal_detail` WRITE;
/*!40000 ALTER TABLE `asset_disposal_detail` DISABLE KEYS */;
INSERT INTO `asset_disposal_detail` VALUES (1,1,1,'laptop',1.00,'SCRAP','AUCTION',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(2,3,3,'Stationary',2.00,'SCRAP','AUCTION',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(3,6,8,'Desktop',1.00,'SCRAP','AUCTION',NULL,17,3,250000.00,0.00,250000.00,'18',111003750.00,'not good',NULL,NULL,NULL,NULL,NULL),(4,7,8,'Desktop',1.00,'SCRAP','AUCTION',NULL,17,3,250000.00,0.00,250000.00,'18',111003750.00,'not good',NULL,NULL,NULL,NULL,NULL),(5,8,7,'Desktop',1.00,'SCRAP','AUCTION',NULL,19,2,50000.00,0.00,50000.00,'45',1212000.00,'Condemned',NULL,NULL,NULL,NULL,NULL),(6,9,8,'Desktop',1.00,'SALE','AUCTION',NULL,17,3,250000.00,0.00,250000.00,'18',111003750.00,'not working','PO1029',NULL,'S1','M1',NULL),(7,10,8,'Desktop',1.00,'SCRAP','TENDER',NULL,20,4,250000.00,0.00,250000.00,'18',111003750.00,'not good','PO1029',NULL,'S1','M1',NULL),(8,11,7,'Desktop',1.00,'SALE','AUCTION',NULL,19,2,50000.00,0.00,50000.00,'45',1212000.00,'Condemned','PO1033',NULL,'S1','M1',NULL),(9,12,14,'mouse hp',1.00,'SCRAP','AUCTION',NULL,24,2,50000.00,0.00,50000.00,'46',100000.00,'not working','PO1051','2025-11-19','S1','M1','BNGCOM2526-014'),(10,13,14,'mouse hp',1.00,'SALE','AUCTION',NULL,24,2,50000.00,0.00,50000.00,'46',100000.00,'not working','PO1051','2025-11-19','S3','M1','BNGCOM2526-014');
/*!40000 ALTER TABLE `asset_disposal_detail` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-29 21:41:27
