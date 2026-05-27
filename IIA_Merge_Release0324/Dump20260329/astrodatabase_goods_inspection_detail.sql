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
-- Table structure for table `goods_inspection_detail`
--

DROP TABLE IF EXISTS `goods_inspection_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `goods_inspection_detail` (
  `inspection_detail_id` int NOT NULL AUTO_INCREMENT,
  `inspection_sub_process_id` int NOT NULL,
  `gprn_sub_process_id` int NOT NULL,
  `gprn_process_id` int NOT NULL,
  `material_code` varchar(50) NOT NULL,
  `material_desc` varchar(50) NOT NULL,
  `asset_id` int DEFAULT NULL,
  `installation_report_filename` varchar(255) DEFAULT NULL,
  `received_quantity` decimal(10,2) NOT NULL,
  `accepted_quantity` decimal(10,2) NOT NULL,
  `rejected_quantity` decimal(10,2) NOT NULL,
  `reject_reason` varchar(100) DEFAULT NULL,
  `rejection_type` varchar(50) DEFAULT NULL,
  `asset_code` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`inspection_detail_id`),
  KEY `idx_inspection_subprocess` (`inspection_sub_process_id`),
  KEY `idx_gprn_subprocess` (`gprn_sub_process_id`),
  KEY `idx_material` (`material_code`),
  KEY `asset_id` (`asset_id`),
  CONSTRAINT `goods_inspection_detail_ibfk_1` FOREIGN KEY (`inspection_sub_process_id`) REFERENCES `goods_inspection_master` (`inspection_sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `goods_inspection_detail_ibfk_2` FOREIGN KEY (`gprn_sub_process_id`) REFERENCES `gprn_master` (`sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `goods_inspection_detail_ibfk_3` FOREIGN KEY (`asset_id`) REFERENCES `asset_master` (`asset_id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `goods_inspection_detail`
--

LOCK TABLES `goods_inspection_detail` WRITE;
/*!40000 ALTER TABLE `goods_inspection_detail` DISABLE KEYS */;
INSERT INTO `goods_inspection_detail` VALUES (1,1,9,1002,'M1100','laptop',1,NULL,5.00,4.00,1.00,'not working','permanent',NULL),(2,2,15,1011,'M1103','Hp laptop',2,NULL,2.00,1.00,1.00,'not good',NULL,NULL),(3,4,18,1004,'M1102','Stationary',3,NULL,3.00,2.00,1.00,'not good',NULL,NULL),(4,5,20,1005,'M1102','Stationary',4,NULL,1.00,1.00,0.00,'',NULL,NULL),(5,6,21,1013,'M1103','Hp laptop',5,NULL,2.00,1.00,1.00,'not good',NULL,NULL),(6,9,22,1005,'M1102','Stationary',6,NULL,7.00,5.00,2.00,NULL,NULL,NULL),(7,10,23,1005,'M1102','Stationary',4,NULL,1.00,1.00,0.00,'nk',NULL,NULL),(8,11,24,1033,'M1104','Desktop',7,NULL,1.00,1.00,0.00,'not good','replacement',NULL),(9,12,25,1029,'M1104','Desktop',8,NULL,10.00,5.00,5.00,'not good','replacement',NULL),(10,15,30,1033,'M1104','Desktop',9,NULL,1.00,1.00,0.00,'',NULL,NULL),(11,24,46,1037,'M1104','Desktop',12,NULL,2.00,2.00,0.00,'',NULL,NULL),(12,32,51,1043,'M1104','Desktop',13,NULL,1.00,1.00,0.00,'',NULL,NULL),(13,36,52,1051,'M1127','mouse hp',14,NULL,5.00,5.00,0.00,'',NULL,'BNGCOM2526-014'),(14,37,53,1053,'M1127','mouse hp',15,NULL,5.00,5.00,0.00,'',NULL,'BNGCOM2526-015'),(15,37,53,1053,'M1128','hp cpu',16,NULL,5.00,5.00,0.00,'',NULL,'BNGCOM2526-016'),(16,38,54,1051,'M1127','mouse hp',14,NULL,2.00,2.00,0.00,'',NULL,'BNGCOM2526-014');
/*!40000 ALTER TABLE `goods_inspection_detail` ENABLE KEYS */;
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
