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
-- Table structure for table `ohq_consumable_store_stock_entity`
--

DROP TABLE IF EXISTS `ohq_consumable_store_stock_entity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ohq_consumable_store_stock_entity` (
  `ohq_id` bigint NOT NULL AUTO_INCREMENT,
  `material_code` varchar(255) DEFAULT NULL,
  `locator_id` int DEFAULT NULL,
  `book_value` decimal(19,2) DEFAULT NULL,
  `depriciation_rate` decimal(19,2) DEFAULT NULL,
  `unit_price` decimal(19,2) DEFAULT NULL,
  `custodian_id` varchar(255) DEFAULT NULL,
  `quantity` decimal(19,2) DEFAULT NULL,
  `uom` varchar(100) DEFAULT NULL,
  `create_date` timestamp NOT NULL,
  PRIMARY KEY (`ohq_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ohq_consumable_store_stock_entity`
--

LOCK TABLES `ohq_consumable_store_stock_entity` WRITE;
/*!40000 ALTER TABLE `ohq_consumable_store_stock_entity` DISABLE KEYS */;
INSERT INTO `ohq_consumable_store_stock_entity` VALUES (1,'M1110',2,50000.00,0.00,NULL,'18',48.00,'Numbers','2025-09-17 10:06:06'),(2,'M1111',2,5000.00,0.00,NULL,'18',41.00,'Numbers','2025-09-17 10:09:58'),(3,'M1112',1,15000.00,0.00,NULL,'18',1.00,'Numbers','2025-09-25 05:16:44'),(4,'M1111',2,300.00,0.00,NULL,'15',3.00,'Numbers','2025-10-13 06:26:12');
/*!40000 ALTER TABLE `ohq_consumable_store_stock_entity` ENABLE KEYS */;
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
