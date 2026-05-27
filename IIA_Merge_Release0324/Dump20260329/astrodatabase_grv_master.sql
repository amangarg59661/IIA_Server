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
-- Table structure for table `grv_master`
--

DROP TABLE IF EXISTS `grv_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `grv_master` (
  `gi_sub_process_id` int NOT NULL,
  `gi_process_id` varchar(50) NOT NULL,
  `grv_process_id` varchar(50) NOT NULL,
  `grv_sub_process_id` int NOT NULL AUTO_INCREMENT,
  `date` date DEFAULT NULL,
  `created_by` varchar(50) NOT NULL,
  `create_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `location_id` varchar(10) NOT NULL,
  PRIMARY KEY (`grv_sub_process_id`),
  KEY `idx_grv_process_id` (`grv_process_id`),
  KEY `idx_gi_sub_process` (`gi_sub_process_id`),
  KEY `idx_date` (`date`),
  KEY `location_id` (`location_id`),
  CONSTRAINT `grv_master_ibfk_1` FOREIGN KEY (`gi_sub_process_id`) REFERENCES `goods_inspection_master` (`inspection_sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `grv_master_ibfk_2` FOREIGN KEY (`location_id`) REFERENCES `location_master` (`location_code`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `grv_master`
--

LOCK TABLES `grv_master` WRITE;
/*!40000 ALTER TABLE `grv_master` DISABLE KEYS */;
INSERT INTO `grv_master` VALUES (1,'1002','1002',1,'2025-06-26','18','2025-06-26 16:56:47','BNG'),(4,'1004','1004',2,'2025-07-10','18','2025-07-09 13:59:17','BNG'),(9,'1005','1005',3,NULL,'18','2025-07-10 11:33:36','BNG');
/*!40000 ALTER TABLE `grv_master` ENABLE KEYS */;
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
