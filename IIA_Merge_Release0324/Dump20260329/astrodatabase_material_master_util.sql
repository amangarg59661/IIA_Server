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
-- Table structure for table `material_master_util`
--

DROP TABLE IF EXISTS `material_master_util`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `material_master_util` (
  `material_code` varchar(255) NOT NULL,
  `category` varchar(255) DEFAULT NULL,
  `sub_category` varchar(255) DEFAULT NULL,
  `description` text,
  `uom` varchar(50) DEFAULT NULL,
  `unit_price` decimal(19,2) DEFAULT NULL,
  `currency` varchar(10) DEFAULT NULL,
  `estimated_price_with_ccy` decimal(19,2) DEFAULT NULL,
  `upload_image_name` varchar(255) DEFAULT NULL,
  `indigenous_or_imported` tinyint(1) DEFAULT NULL,
  `approval_status` enum('APPROVED','REJECTED','AWAITING_APPROVAL','CHANGE_REQUEST') DEFAULT NULL,
  `comments` text,
  `created_by` int DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `brief_description` text,
  `material_number` int DEFAULT NULL,
  PRIMARY KEY (`material_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `material_master_util`
--

LOCK TABLES `material_master_util` WRITE;
/*!40000 ALTER TABLE `material_master_util` DISABLE KEYS */;
INSERT INTO `material_master_util` VALUES ('M1114','Consumable','Software','msoffice','Numbers',310000.00,'INR',NULL,'',1,'CHANGE_REQUEST','why is this a material and not  ajob ?',18,'18','2025-09-27 14:27:15','2025-09-27 14:27:15','Microsoft office for staff',0),('M1120','Consumable','Computer & Peripherals','5 Ton Air Conditioner','Boxes',100.00,'INR',NULL,NULL,1,'AWAITING_APPROVAL',NULL,18,'18','2025-10-24 07:47:00','2025-10-24 07:47:00','aaa',0),('M1121','Capital','Computer & Peripherals','laptop','Numbers',50000.00,'INR',NULL,NULL,1,'REJECTED','Not requried',18,'18','2025-10-27 08:41:17','2025-10-27 08:41:17','HP lap',0),('M1131','RAW_MATERIAL','CEMENT','Hi Description','LITRE',3243.00,'INR',NULL,NULL,1,'AWAITING_APPROVAL',NULL,96,'96','2026-01-27 01:06:40','2026-01-27 01:06:40','retre',0),('M1133','CONSUMABLES','COMPUTER','Hi Description','KG',34234.00,'INR',NULL,NULL,1,'AWAITING_APPROVAL',NULL,96,'96','2026-01-28 02:49:37','2026-01-28 02:49:37','WEQWEWQEW',0),('M1138','CONSUMABLES','STEEL','Hi Description','KG',9888.00,'INR',NULL,NULL,1,'AWAITING_APPROVAL',NULL,NULL,'null','2026-01-28 14:59:58','2026-01-28 14:59:58','Description of steel',0),('M1139','CONSUMABLES','STEEL','Hi Description','KG',988845345.00,'INR',NULL,NULL,1,'AWAITING_APPROVAL',NULL,NULL,'null','2026-01-28 15:00:19','2026-01-28 15:00:19','Description of steel',0),('M1140','CONSUMABLES','CEMENT','Hi Description','KG',324324.00,'INR',NULL,NULL,1,'AWAITING_APPROVAL',NULL,NULL,'null','2026-01-28 15:01:39','2026-01-28 15:01:39','Description',0),('M1141','CONSUMABLES','CEMENT','Hi Description','KG',324324.00,'INR',NULL,NULL,1,'AWAITING_APPROVAL',NULL,1,'null','2026-01-28 15:04:28','2026-01-28 15:04:28','Description',0),('M1143','CONSUMABLES','COMPUTER','Hi Description','PIECE',343245325.00,'USD',NULL,NULL,1,'AWAITING_APPROVAL',NULL,96,'96','2026-01-28 15:09:16','2026-01-28 15:09:16','wqewqewq',0),('M1148','FINISHED_GOODS','COMPUTER','Printer Fill','PIECE',1200.00,'INR',NULL,NULL,1,'AWAITING_APPROVAL',NULL,116,'96','2026-03-29 09:48:34','2026-03-29 09:48:34','Printer Filll',0);
/*!40000 ALTER TABLE `material_master_util` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-29 21:41:35
