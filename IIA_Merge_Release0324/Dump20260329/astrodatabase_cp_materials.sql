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
-- Table structure for table `cp_materials`
--

DROP TABLE IF EXISTS `cp_materials`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cp_materials` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `material_code` varchar(100) NOT NULL,
  `material_description` varchar(1000) DEFAULT NULL,
  `quantity` decimal(15,2) DEFAULT NULL,
  `unit_price` decimal(15,2) DEFAULT NULL,
  `uom` varchar(50) DEFAULT NULL,
  `total_price` decimal(15,2) DEFAULT NULL,
  `budget_code` varchar(100) DEFAULT NULL,
  `material_category` varchar(255) DEFAULT NULL,
  `material_sub_category` varchar(255) DEFAULT NULL,
  `currency` varchar(50) DEFAULT NULL,
  `contigency_id` varchar(255) DEFAULT NULL,
  `gst` decimal(10,2) DEFAULT NULL,
  `country_of_origin` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `contigency_id` (`contigency_id`),
  CONSTRAINT `cp_materials_ibfk_1` FOREIGN KEY (`contigency_id`) REFERENCES `contigency_purchase` (`contigency_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cp_materials`
--

LOCK TABLES `cp_materials` WRITE;
/*!40000 ALTER TABLE `cp_materials` DISABLE KEYS */;
INSERT INTO `cp_materials` VALUES (1,'M1103','Hp laptop',1.00,50000.00,'Numbers',50000.00,'Capital','Capital','Computer & Peripherals','USD','CP1001',10.00,NULL),(2,'M1102','Stationary',5.00,200.00,'Numbers',1000.00,'Consumable','Capital','Chemicals','USD','CP1002',200.00,NULL),(3,'M1100','laptop',2.00,1200.00,'Numbers',2400.00,'Consumable','Capital','Electrical','USD','CP1002',300.00,NULL),(4,'M1106','Telescope',NULL,100000.00,'Numbers',0.00,'Capital','Capital','Equipment','INR','CP1003',1000.00,NULL),(5,'M1102','Stationary',10.00,200.00,'Numbers',2240.00,'Capital','Capital','Chemicals','USD','CP1004',12.00,'British Indian Ocean Territory'),(6,'M1001','3D Printer',10.00,456.00,'ft',5016.00,'Consumable','Electronics','Electrical','INR','CP1005',10.00,'India'),(7,'M1100','laptop',2.00,1200.00,'Numbers',2520.00,'Consumable','Capital','Electrical','USD','CP1005',5.00,'India');
/*!40000 ALTER TABLE `cp_materials` ENABLE KEYS */;
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
