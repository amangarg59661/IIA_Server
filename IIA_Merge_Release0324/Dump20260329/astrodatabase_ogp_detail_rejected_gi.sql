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
-- Table structure for table `ogp_detail_rejected_gi`
--

DROP TABLE IF EXISTS `ogp_detail_rejected_gi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ogp_detail_rejected_gi` (
  `detail_id` int NOT NULL AUTO_INCREMENT,
  `ogp_sub_process_id` int NOT NULL,
  `material_code` varchar(255) DEFAULT NULL,
  `material_desc` varchar(255) DEFAULT NULL,
  `asset_id` int DEFAULT NULL,
  `asset_desc` varchar(255) DEFAULT NULL,
  `rejection_type` varchar(50) DEFAULT NULL,
  `rejected_quantity` decimal(10,2) NOT NULL,
  `asset_code` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`detail_id`),
  KEY `ogp_sub_process_id` (`ogp_sub_process_id`),
  CONSTRAINT `ogp_detail_rejected_gi_ibfk_1` FOREIGN KEY (`ogp_sub_process_id`) REFERENCES `ogp_master_rejected_gi` (`ogp_sub_process_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ogp_detail_rejected_gi`
--

LOCK TABLES `ogp_detail_rejected_gi` WRITE;
/*!40000 ALTER TABLE `ogp_detail_rejected_gi` DISABLE KEYS */;
INSERT INTO `ogp_detail_rejected_gi` VALUES (1,1,'M1104','Desktop',8,'Desktop','replacement',5.00,NULL),(2,2,'M1112','External 1TB Hard Disk',NULL,NULL,'permanent',1.00,NULL),(3,3,'M1111','Pen',NULL,NULL,'replacement',5.00,NULL),(4,4,'M1111','Pen',NULL,NULL,'permanent',2.00,NULL),(5,5,'M1111','Pen',NULL,NULL,'replacement',2.00,NULL),(6,6,'M1111','Pen',NULL,NULL,'replacement',1.00,NULL);
/*!40000 ALTER TABLE `ogp_detail_rejected_gi` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-29 21:41:31
