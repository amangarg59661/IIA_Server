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
-- Table structure for table `grv_material_detail`
--

DROP TABLE IF EXISTS `grv_material_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `grv_material_detail` (
  `detail_id` int NOT NULL AUTO_INCREMENT,
  `grv_process_id` varchar(50) NOT NULL,
  `grv_sub_process_id` int NOT NULL,
  `gi_sub_process_id` int NOT NULL,
  `material_code` varchar(50) NOT NULL,
  `material_desc` varchar(50) NOT NULL,
  `uom_id` varchar(10) DEFAULT NULL,
  `rejected_quantity` decimal(10,2) NOT NULL,
  `return_quantity` decimal(10,2) NOT NULL,
  `return_type` varchar(50) NOT NULL,
  `reject_reason` varchar(50) NOT NULL,
  PRIMARY KEY (`detail_id`),
  KEY `idx_grv_sub_process` (`grv_sub_process_id`),
  KEY `idx_grv_process_id` (`grv_process_id`),
  KEY `idx_material` (`material_code`),
  KEY `idx_return_type` (`return_type`),
  KEY `uom_id` (`uom_id`),
  CONSTRAINT `grv_material_detail_ibfk_1` FOREIGN KEY (`grv_sub_process_id`) REFERENCES `grv_master` (`grv_sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `grv_material_detail_ibfk_2` FOREIGN KEY (`uom_id`) REFERENCES `uom_master` (`uom_code`) ON UPDATE CASCADE,
  CONSTRAINT `grv_material_detail_ibfk_3` FOREIGN KEY (`material_code`) REFERENCES `material_master` (`material_code`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `grv_material_detail`
--

LOCK TABLES `grv_material_detail` WRITE;
/*!40000 ALTER TABLE `grv_material_detail` DISABLE KEYS */;
INSERT INTO `grv_material_detail` VALUES (1,'1002',1,1,'M1100','laptop','Numbers',2.00,1.00,'Damaged','not good'),(2,'1004',2,4,'M1102','Stationary','Numbers',1.00,1.00,'Excess','not good'),(3,'1005',3,9,'M1102','Stationary','Numbers',2.00,1.00,'Damaged','Damaged');
/*!40000 ALTER TABLE `grv_material_detail` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-29 21:41:33
