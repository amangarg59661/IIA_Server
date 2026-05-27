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
-- Table structure for table `issue_note_master`
--

DROP TABLE IF EXISTS `issue_note_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `issue_note_master` (
  `issue_note_id` int NOT NULL AUTO_INCREMENT,
  `issue_note_type` enum('Returnable','Non Returnable') DEFAULT NULL,
  `issue_date` date NOT NULL,
  `consignee_detail` varchar(50) DEFAULT NULL,
  `indentor_name` varchar(50) DEFAULT NULL,
  `field_station` varchar(50) DEFAULT NULL,
  `created_by` int NOT NULL,
  `create_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `location_id` varchar(10) NOT NULL,
  PRIMARY KEY (`issue_note_id`),
  KEY `location_id` (`location_id`),
  CONSTRAINT `issue_note_master_ibfk_1` FOREIGN KEY (`location_id`) REFERENCES `location_master` (`location_code`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `issue_note_master`
--

LOCK TABLES `issue_note_master` WRITE;
/*!40000 ALTER TABLE `issue_note_master` DISABLE KEYS */;
INSERT INTO `issue_note_master` VALUES (1,NULL,'2025-06-26','Bangalore','sunit','BNG',18,'2025-06-26 15:10:26','BNG'),(2,NULL,'2025-06-26','Bangalore','sunit','BNG',29,'2025-06-26 15:51:21','BNG'),(3,NULL,'2025-07-09','Bangalore','sunit','BNG',18,'2025-07-09 14:00:24','BNG'),(4,NULL,'2025-07-10','Bangalore','ABC','Bangalore',18,'2025-07-10 11:59:27','BNG'),(5,NULL,'2025-07-18','Bangalore','sunit','BNG',18,'2025-07-18 11:35:33','BNG');
/*!40000 ALTER TABLE `issue_note_master` ENABLE KEYS */;
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
