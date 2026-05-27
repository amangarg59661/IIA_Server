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
-- Table structure for table `asset_master`
--

DROP TABLE IF EXISTS `asset_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `asset_master` (
  `asset_id` int NOT NULL AUTO_INCREMENT,
  `material_code` varchar(50) NOT NULL,
  `material_desc` varchar(50) NOT NULL,
  `asset_desc` varchar(50) NOT NULL,
  `make_no` varchar(50) DEFAULT NULL,
  `serial_no` varchar(50) DEFAULT NULL,
  `model_no` varchar(50) DEFAULT NULL,
  `init_quantity` decimal(10,2) DEFAULT NULL,
  `uom_id` varchar(10) NOT NULL,
  `component_name` varchar(50) DEFAULT NULL,
  `component_id` int DEFAULT NULL,
  `create_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` int NOT NULL,
  `updated_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `updated_by` int DEFAULT NULL,
  `unit_price` decimal(10,2) DEFAULT NULL,
  `depriciation_rate` decimal(10,2) DEFAULT NULL,
  `end_of_life` date DEFAULT NULL,
  `stock_levels` decimal(10,2) DEFAULT NULL,
  `condition_of_goods` varchar(100) DEFAULT NULL,
  `shelf_life` varchar(50) DEFAULT NULL,
  `po_id` varchar(50) DEFAULT NULL,
  `locator` decimal(10,2) DEFAULT NULL,
  `locator_id` varchar(20) DEFAULT NULL,
  `igp_id` bigint DEFAULT NULL,
  `asset_code` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`asset_id`),
  KEY `idx_material_code` (`material_code`),
  KEY `idx_uom` (`uom_id`),
  KEY `idx_material_desc` (`material_desc`),
  CONSTRAINT `asset_master_ibfk_1` FOREIGN KEY (`material_code`) REFERENCES `material_master` (`material_code`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `asset_master`
--

LOCK TABLES `asset_master` WRITE;
/*!40000 ALTER TABLE `asset_master` DISABLE KEYS */;
INSERT INTO `asset_master` VALUES (1,'M1100','laptop','laptop','M1','S1','M1',3.00,'Numbers',NULL,NULL,'2025-06-25 17:03:02',18,'2025-06-25 17:03:02',NULL,1200.00,NULL,NULL,NULL,NULL,NULL,'PO1002',NULL,NULL,NULL,NULL),(2,'M1103','Hp laptop','Hp laptop','HP','QWEASD12Y','12345XY',NULL,'Numbers',NULL,NULL,'2025-07-03 16:35:13',29,'2025-07-03 16:35:13',NULL,50000.00,NULL,NULL,NULL,NULL,NULL,'PO1011',NULL,NULL,NULL,NULL),(3,'M1102','Stationary','Stationary','M1','S1','M1',2.00,'Numbers',NULL,NULL,'2025-07-09 13:57:55',18,'2025-07-09 13:57:55',NULL,200.00,NULL,NULL,NULL,NULL,NULL,'PO1004',NULL,NULL,NULL,NULL),(4,'M1102','Stationary','Stationary','M1','S1','M1',1.00,'Numbers',NULL,NULL,'2025-07-10 10:45:40',18,'2025-07-10 10:45:40',NULL,200.00,NULL,NULL,NULL,NULL,NULL,'PO1005',NULL,NULL,NULL,NULL),(5,'M1103','Hp laptop','Hp laptop','HP','123WQ1','HP G9',NULL,'Numbers',NULL,NULL,'2025-07-10 11:17:41',18,'2025-07-10 11:17:41',NULL,50000.00,NULL,NULL,NULL,NULL,NULL,'PO1013',NULL,NULL,NULL,NULL),(6,'M1102','Stationary','Stationary','ABC','123ASD','ABC109',5.00,'Numbers',NULL,NULL,'2025-07-10 11:31:24',18,'2025-07-10 11:31:24',NULL,200.00,NULL,NULL,NULL,NULL,NULL,'PO1005',NULL,NULL,NULL,NULL),(7,'M1104','Desktop','Desktop','M1','S1','M1',1.00,'Numbers',NULL,NULL,'2025-09-13 10:36:33',18,'2025-09-13 10:36:33',NULL,50000.00,NULL,NULL,NULL,NULL,NULL,'PO1033',NULL,NULL,NULL,NULL),(8,'M1104','Desktop','Desktop','M1','S1','M1',5.00,'Numbers',NULL,NULL,'2025-09-13 10:40:45',18,'2025-09-13 10:40:45',NULL,50000.00,NULL,NULL,NULL,NULL,NULL,'PO1029',NULL,NULL,NULL,NULL),(9,'M1104','Desktop','Desktop','qweasd','123qweasd','123456qwe',NULL,'Numbers',NULL,NULL,'2025-09-22 11:31:09',18,'2025-09-22 11:31:09',NULL,50000.00,NULL,NULL,NULL,NULL,NULL,'PO1033',NULL,NULL,NULL,NULL),(12,'M1104','Desktop','Desktop','Dell','ZYX890','Dell 123',2.00,'Numbers',NULL,NULL,'2025-10-06 11:57:01',18,'2025-10-06 11:57:01',NULL,50000.00,NULL,NULL,NULL,NULL,NULL,'PO1037',NULL,NULL,NULL,NULL),(13,'M1104','Desktop','Desktop','Dell','ZYX890','ABC1',1.00,'Numbers',NULL,NULL,'2025-10-23 16:33:52',18,'2025-10-23 16:33:52',NULL,50000.00,NULL,NULL,NULL,NULL,NULL,'PO1043',NULL,NULL,NULL,NULL),(14,'M1127','mouse hp','mouse hp','M1','S1','M1',5.00,'Numbers',NULL,NULL,'2025-11-03 11:38:52',43,'2025-11-03 11:52:08',NULL,10000.00,NULL,NULL,5.00,NULL,NULL,'PO1051',NULL,NULL,NULL,'BNGCOM2526-014'),(15,'M1127','mouse hp','mouse hp','M1','S1','M1',5.00,'Numbers',NULL,NULL,'2025-11-03 12:35:17',43,'2025-11-07 15:46:26',NULL,10000.00,NULL,NULL,5.00,NULL,NULL,'PO1053',NULL,NULL,NULL,'BNGCOM2526-015'),(16,'M1128','hp cpu','hp cpu','m21','A1','M22',5.00,'Numbers',NULL,NULL,'2025-11-03 12:35:17',43,'2025-11-07 15:19:18',NULL,1000.00,NULL,NULL,5.00,NULL,NULL,'PO1053',NULL,NULL,NULL,'BNGCOM2526-016');
/*!40000 ALTER TABLE `asset_master` ENABLE KEYS */;
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
