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
-- Table structure for table `goods_inspection_consumable_detail`
--

DROP TABLE IF EXISTS `goods_inspection_consumable_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `goods_inspection_consumable_detail` (
  `inspection_detail_id` int NOT NULL AUTO_INCREMENT,
  `inspection_sub_process_id` int NOT NULL,
  `gprn_sub_process_id` int NOT NULL,
  `gprn_process_id` int NOT NULL,
  `material_code` varchar(50) NOT NULL,
  `material_desc` varchar(50) NOT NULL,
  `uom_id` varchar(10) NOT NULL,
  `installation_report_filename` varchar(255) DEFAULT NULL,
  `received_quantity` decimal(10,2) NOT NULL,
  `accepted_quantity` decimal(10,2) NOT NULL,
  `rejected_quantity` decimal(10,2) NOT NULL,
  `rejection_type` varchar(50) DEFAULT NULL,
  `reject_reason` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`inspection_detail_id`),
  KEY `idx_inspection_subprocess` (`inspection_sub_process_id`),
  KEY `idx_gprn_subprocess` (`gprn_sub_process_id`),
  KEY `idx_material` (`material_code`),
  CONSTRAINT `goods_inspection_consumable_detail_ibfk_1` FOREIGN KEY (`inspection_sub_process_id`) REFERENCES `goods_inspection_master` (`inspection_sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `goods_inspection_consumable_detail_ibfk_2` FOREIGN KEY (`gprn_sub_process_id`) REFERENCES `gprn_master` (`sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `goods_inspection_consumable_detail`
--

LOCK TABLES `goods_inspection_consumable_detail` WRITE;
/*!40000 ALTER TABLE `goods_inspection_consumable_detail` DISABLE KEYS */;
INSERT INTO `goods_inspection_consumable_detail` VALUES (1,13,26,1039,'M1110','laptop','Numbers',NULL,50.00,50.00,0.00,'permanent',NULL),(2,14,27,1040,'M1111','Pen','Numbers',NULL,50.00,50.00,0.00,'permanent',NULL),(3,16,31,1041,'M1112','External 1TB Hard Disk','Numbers',NULL,1.00,1.00,0.00,NULL,NULL),(4,19,32,1041,'M1112','External 1TB Hard Disk','Numbers',NULL,1.00,1.00,0.00,NULL,NULL),(5,22,33,1041,'M1112','External 1TB Hard Disk','Numbers',NULL,2.00,1.00,1.00,'permanent',NULL),(6,23,45,1042,'M1111','Pen','Numbers',NULL,10.00,5.00,5.00,'replacement',NULL),(7,25,47,1042,'M1111','Pen','Numbers',NULL,5.00,3.00,2.00,'permanent',NULL),(8,26,48,1042,'M1111','Pen','Numbers',NULL,5.00,3.00,2.00,'permanent',NULL),(9,27,49,1040,'M1111','Pen','Numbers',NULL,5.00,3.00,2.00,'replacement','naklm'),(10,28,44,1042,'M1111','Pen','Numbers','1760339996254_0f0d744e83794ece8219ec6ec013fff3.pdf',5.00,2.00,3.00,'replacement','naslk'),(11,29,50,1040,'M1111','Pen','Numbers','1760357910126_9b496823ed874a43ab1f434d0ed6c1cf.pdf',2.00,1.00,1.00,'replacement','as'),(12,30,28,1040,'M1111','Pen','Numbers',NULL,30.00,15.00,15.00,'replacement','not working'),(13,31,34,1042,'M1111','Pen','Numbers',NULL,25.00,10.00,15.00,'replacement','Damaged');
/*!40000 ALTER TABLE `goods_inspection_consumable_detail` ENABLE KEYS */;
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
