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
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gt_dtl`
--

LOCK TABLES `gt_dtl` WRITE;
/*!40000 ALTER TABLE `gt_dtl` DISABLE KEYS */;
INSERT INTO `gt_dtl` VALUES (1,1,7,'Desktop',NULL,NULL,1.00,6,2,50000.00,0.00,50000.00,NULL,NULL,NULL,NULL,NULL),(2,2,7,'Desktop',NULL,NULL,1.00,2,2,50000.00,0.00,50000.00,NULL,NULL,NULL,NULL,NULL),(3,3,8,'Desktop',NULL,NULL,1.00,4,3,250000.00,0.00,250000.00,'PO1029','M1','S1','good product',NULL),(4,4,8,'Desktop',NULL,NULL,1.00,2,3,250000.00,0.00,250000.00,'PO1029','M1','S1','not working ',NULL),(5,5,7,'Desktop',NULL,NULL,1.00,14,2,50000.00,25.00,50000.00,'PO1033','M1','12345XYZ','Will be used at Kodaikanal',NULL),(6,6,14,'mouse hp','M1127','mouse hp',1.00,4,2,10000.00,0.00,50000.00,'PO1051','M1','S2','good product','BNGCOM2526-014'),(7,6,14,'mouse hp','M1127','mouse hp',1.00,3,2,10000.00,0.00,50000.00,'PO1051','M1','S4','Good Product','BNGCOM2526-014');
/*!40000 ALTER TABLE `gt_dtl` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-29 21:41:37
