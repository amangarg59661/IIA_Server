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
-- Table structure for table `ogp_master`
--

DROP TABLE IF EXISTS `ogp_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ogp_master` (
  `ogp_process_id` varchar(50) NOT NULL,
  `ogp_sub_process_id` int NOT NULL AUTO_INCREMENT,
  `issue_note_id` int NOT NULL,
  `ogp_date` date NOT NULL,
  `location_id` varchar(10) NOT NULL,
  `created_by` int NOT NULL,
  `create_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ogp_type` varchar(20) NOT NULL,
  `receiver_name` varchar(50) DEFAULT NULL,
  `receiver_location` varchar(100) DEFAULT NULL,
  `date_of_return` date DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `sender_name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`ogp_sub_process_id`),
  KEY `location_id` (`location_id`),
  KEY `issue_note_id` (`issue_note_id`),
  CONSTRAINT `ogp_master_ibfk_1` FOREIGN KEY (`location_id`) REFERENCES `location_master` (`location_code`) ON UPDATE CASCADE,
  CONSTRAINT `ogp_master_ibfk_2` FOREIGN KEY (`issue_note_id`) REFERENCES `issue_note_master` (`issue_note_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ogp_master`
--

LOCK TABLES `ogp_master` WRITE;
/*!40000 ALTER TABLE `ogp_master` DISABLE KEYS */;
INSERT INTO `ogp_master` VALUES ('INV1',1,1,'2025-06-26','BNG',18,'2025-06-26 15:11:24','Returnable','sunit1','BNG','2025-06-30','APPROVED',NULL),('INV2',2,2,'2025-06-27','BNG',29,'2025-06-26 15:52:09','Returnable','sunit1','BNG','2025-06-27','APPROVED',NULL),('INV3',3,3,'2025-07-09','BNG',18,'2025-07-09 14:01:08','Returnable','sunit1','BNG','2025-07-09','APPROVED',NULL),('INV4',4,4,'2025-07-10','BNG',18,'2025-07-10 12:13:30','Returnable','ANC','Bangalore','2025-07-17','APPROVED',NULL),('INV5',5,5,'2025-07-18','BNG',18,'2025-07-18 11:36:39','Returnable','sunit1','BNG','2025-07-23','APPROVED','sunit');
/*!40000 ALTER TABLE `ogp_master` ENABLE KEYS */;
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
