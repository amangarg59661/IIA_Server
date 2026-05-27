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
-- Table structure for table `grn_consumable_detail`
--

DROP TABLE IF EXISTS `grn_consumable_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `grn_consumable_detail` (
  `detail_id` int NOT NULL AUTO_INCREMENT,
  `grn_process_id` varchar(50) NOT NULL,
  `grn_sub_process_id` int NOT NULL,
  `gi_sub_process_id` int DEFAULT NULL,
  `igp_sub_process_id` int DEFAULT NULL,
  `quantity` decimal(10,2) NOT NULL,
  `material_code` varchar(50) NOT NULL,
  `locator_id` int NOT NULL,
  `book_value` decimal(10,2) NOT NULL,
  `depriciation_rate` decimal(10,2) NOT NULL,
  PRIMARY KEY (`detail_id`),
  KEY `grn_sub_process_id` (`grn_sub_process_id`),
  KEY `gi_sub_process_id` (`gi_sub_process_id`),
  KEY `locator_id` (`locator_id`),
  KEY `igp_sub_process_id` (`igp_sub_process_id`),
  CONSTRAINT `grn_consumable_detail_ibfk_1` FOREIGN KEY (`grn_sub_process_id`) REFERENCES `grn_master` (`grn_sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `grn_consumable_detail_ibfk_2` FOREIGN KEY (`gi_sub_process_id`) REFERENCES `goods_inspection_master` (`inspection_sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `grn_consumable_detail_ibfk_3` FOREIGN KEY (`locator_id`) REFERENCES `locator_master` (`locator_id`) ON UPDATE CASCADE,
  CONSTRAINT `grn_consumable_detail_ibfk_4` FOREIGN KEY (`igp_sub_process_id`) REFERENCES `igp_master` (`igp_sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `grn_consumable_detail`
--

LOCK TABLES `grn_consumable_detail` WRITE;
/*!40000 ALTER TABLE `grn_consumable_detail` DISABLE KEYS */;
INSERT INTO `grn_consumable_detail` VALUES (1,'1039',20,13,NULL,50.00,'M1110',2,50000.00,0.00),(2,'1040',21,14,NULL,50.00,'M1111',2,5000.00,0.00),(3,'1041',22,22,NULL,1.00,'M1112',1,15000.00,0.00),(4,'1042',23,23,NULL,5.00,'M1111',1,500.00,0.00),(5,'1042',25,25,NULL,3.00,'M1111',2,300.00,0.00),(6,'1040',26,27,NULL,3.00,'M1111',2,300.00,0.00),(7,'1041',27,19,NULL,1.00,'M1112',1,15000.00,0.00),(8,'1040',28,29,NULL,1.00,'M1111',1,100.00,0.00);
/*!40000 ALTER TABLE `grn_consumable_detail` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-29 21:41:29
