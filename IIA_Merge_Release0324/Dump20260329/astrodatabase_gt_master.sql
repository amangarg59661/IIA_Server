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
-- Table structure for table `gt_master`
--

DROP TABLE IF EXISTS `gt_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gt_master` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `sender_location_id` varchar(255) NOT NULL,
  `status` varchar(255) DEFAULT NULL,
  `sender_custodian_id` int NOT NULL,
  `receiver_location_id` varchar(255) NOT NULL,
  `receiver_custodian_id` int NOT NULL,
  `create_date` datetime NOT NULL,
  `gt_date` date NOT NULL,
  `created_by` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gt_master`
--

LOCK TABLES `gt_master` WRITE;
/*!40000 ALTER TABLE `gt_master` DISABLE KEYS */;
INSERT INTO `gt_master` VALUES (1,'BNG','APPROVED',18,'GBR',16,'2025-10-06 11:55:07','2025-10-23',43),(2,'BNG','APPROVED',18,'BNG',45,'2025-10-07 11:30:59','2025-10-07',43),(3,'BNG','APPROVED',18,'BNG',18,'2025-10-13 12:21:43','2025-10-31',18),(4,'BNG','APPROVED',18,'BNG',46,'2025-10-13 18:06:39','2025-10-13',18),(5,'BNG','APPROVED',45,'KSO',16,'2025-10-24 11:56:08','2025-10-24',45),(6,'BNG','APPROVED',46,'BNG',18,'2025-11-03 11:54:45','2025-11-27',46);
/*!40000 ALTER TABLE `gt_master` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-29 21:41:30
