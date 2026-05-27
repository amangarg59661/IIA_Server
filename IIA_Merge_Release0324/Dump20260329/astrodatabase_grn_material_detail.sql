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
-- Table structure for table `grn_material_detail`
--

DROP TABLE IF EXISTS `grn_material_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `grn_material_detail` (
  `detail_id` int NOT NULL AUTO_INCREMENT,
  `grn_process_id` varchar(50) NOT NULL,
  `grn_sub_process_id` int NOT NULL,
  `gi_sub_process_id` int DEFAULT NULL,
  `igp_sub_process_id` int DEFAULT NULL,
  `quantity` decimal(10,2) NOT NULL,
  `asset_id` int NOT NULL,
  `locator_id` int NOT NULL,
  `book_value` decimal(10,2) NOT NULL,
  `depriciation_rate` decimal(10,2) NOT NULL,
  `asset_code` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`detail_id`),
  KEY `asset_id` (`asset_id`),
  KEY `grn_sub_process_id` (`grn_sub_process_id`),
  KEY `gi_sub_process_id` (`gi_sub_process_id`),
  KEY `locator_id` (`locator_id`),
  KEY `igp_sub_process_id` (`igp_sub_process_id`),
  CONSTRAINT `grn_material_detail_ibfk_1` FOREIGN KEY (`asset_id`) REFERENCES `asset_master` (`asset_id`) ON UPDATE CASCADE,
  CONSTRAINT `grn_material_detail_ibfk_2` FOREIGN KEY (`grn_sub_process_id`) REFERENCES `grn_master` (`grn_sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `grn_material_detail_ibfk_3` FOREIGN KEY (`gi_sub_process_id`) REFERENCES `goods_inspection_master` (`inspection_sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `grn_material_detail_ibfk_4` FOREIGN KEY (`locator_id`) REFERENCES `locator_master` (`locator_id`) ON UPDATE CASCADE,
  CONSTRAINT `grn_material_detail_ibfk_5` FOREIGN KEY (`igp_sub_process_id`) REFERENCES `igp_master` (`igp_sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `grn_material_detail`
--

LOCK TABLES `grn_material_detail` WRITE;
/*!40000 ALTER TABLE `grn_material_detail` DISABLE KEYS */;
INSERT INTO `grn_material_detail` VALUES (1,'1002',11,1,NULL,3.00,1,1,3600.00,0.00,NULL),(2,'2',12,NULL,2,1.00,1,1,3600.00,0.00,NULL),(3,'1004',13,4,NULL,2.00,3,2,400.00,0.00,NULL),(4,'3',14,NULL,3,1.00,1,1,3600.00,0.00,NULL),(5,'1005',15,5,NULL,1.00,4,1,200.00,0.00,NULL),(6,'1005',16,9,NULL,5.00,6,1,1000.00,0.00,NULL),(7,'1005',17,10,NULL,1.00,4,1,200.00,0.00,NULL),(8,'1033',18,11,NULL,1.00,7,2,50000.00,0.00,NULL),(9,'1029',19,12,NULL,5.00,8,3,250000.00,0.00,NULL),(10,'1037',24,24,NULL,2.00,12,2,100000.00,0.00,NULL),(11,'1043',29,32,NULL,1.00,13,1,50000.00,0.00,NULL),(12,'1051',30,36,NULL,5.00,14,2,50000.00,0.00,'BNGCOM2526-014'),(13,'1053',31,37,NULL,5.00,15,3,50000.00,0.00,'BNGCOM2526-015'),(14,'1053',31,37,NULL,5.00,16,3,5000.00,0.00,'BNGCOM2526-016'),(15,'1051',32,38,NULL,2.00,14,2,20000.00,0.00,'BNGCOM2526-014');
/*!40000 ALTER TABLE `grn_material_detail` ENABLE KEYS */;
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
