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
-- Table structure for table `igp_detail`
--

DROP TABLE IF EXISTS `igp_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `igp_detail` (
  `detail_id` int NOT NULL AUTO_INCREMENT,
  `igp_process_id` varchar(50) NOT NULL,
  `igp_sub_process_id` int NOT NULL,
  `ogp_sub_process_id` int DEFAULT NULL,
  `asset_id` int NOT NULL,
  `locator_id` int NOT NULL,
  `quantity` decimal(10,2) NOT NULL,
  PRIMARY KEY (`detail_id`),
  KEY `igp_sub_process_id` (`igp_sub_process_id`),
  KEY `asset_id` (`asset_id`),
  KEY `locator_id` (`locator_id`),
  KEY `ogp_sub_process_id` (`ogp_sub_process_id`),
  CONSTRAINT `igp_detail_ibfk_1` FOREIGN KEY (`igp_sub_process_id`) REFERENCES `igp_master` (`igp_sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `igp_detail_ibfk_2` FOREIGN KEY (`asset_id`) REFERENCES `asset_master` (`asset_id`) ON UPDATE CASCADE,
  CONSTRAINT `igp_detail_ibfk_3` FOREIGN KEY (`locator_id`) REFERENCES `locator_master` (`locator_id`) ON UPDATE CASCADE,
  CONSTRAINT `igp_detail_ibfk_4` FOREIGN KEY (`ogp_sub_process_id`) REFERENCES `ogp_master` (`ogp_sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `igp_detail`
--

LOCK TABLES `igp_detail` WRITE;
/*!40000 ALTER TABLE `igp_detail` DISABLE KEYS */;
INSERT INTO `igp_detail` VALUES (1,'INV1',1,1,1,1,1.00),(2,'INV2',2,2,1,1,1.00),(3,'INV3',3,3,1,1,1.00),(4,'INV5',5,5,1,1,1.00);
/*!40000 ALTER TABLE `igp_detail` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-29 21:41:28
