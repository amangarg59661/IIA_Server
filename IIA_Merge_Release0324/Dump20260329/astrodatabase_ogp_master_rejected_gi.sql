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
-- Table structure for table `ogp_master_rejected_gi`
--

DROP TABLE IF EXISTS `ogp_master_rejected_gi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ogp_master_rejected_gi` (
  `ogp_sub_process_id` int NOT NULL AUTO_INCREMENT,
  `ogp_type` varchar(20) DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `gi_id` varchar(255) DEFAULT NULL,
  `ogp_date` date DEFAULT NULL,
  `return_date` date DEFAULT NULL,
  `location_id` varchar(50) DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `sender_name` varchar(50) DEFAULT NULL,
  `receiver_name` varchar(50) DEFAULT NULL,
  `receiver_location` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`ogp_sub_process_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ogp_master_rejected_gi`
--

LOCK TABLES `ogp_master_rejected_gi` WRITE;
/*!40000 ALTER TABLE `ogp_master_rejected_gi` DISABLE KEYS */;
INSERT INTO `ogp_master_rejected_gi` VALUES (1,'Returnable','APPROVED','INV1029/12',NULL,'2025-09-18','BNG','18','sunit','sunit1','BNG'),(2,'Non Returnable','APPROVED','INV1041/22',NULL,NULL,'BNG','29','sunit','This should be vendor name','auto fetched from vendor address'),(3,'Non Returnable','APPROVED','INV1042/23','2025-10-06',NULL,'BNG','43','user','Test Vendor','Bangalore'),(4,'Non Returnable','APPROVED','INV1042/25','2025-10-07','2025-10-07','BNG','43','user','Vendor 1','Bangalore'),(5,'Returnable','APPROVED','INV1040/27','2025-10-22','2025-10-22','BNG','43','user','kapil dev enterprise','Bangalore'),(6,'Returnable','APPROVED','INV1040/29','2025-10-13','2025-10-13','BNG','43','user','kapil dev enterprise','Bangalore');
/*!40000 ALTER TABLE `ogp_master_rejected_gi` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-29 21:41:32
