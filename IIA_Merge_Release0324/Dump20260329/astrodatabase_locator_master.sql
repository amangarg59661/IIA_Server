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
-- Table structure for table `locator_master`
--

DROP TABLE IF EXISTS `locator_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `locator_master` (
  `location_id` varchar(10) NOT NULL,
  `locator_id` int NOT NULL AUTO_INCREMENT,
  `locator_desc` varchar(40) NOT NULL,
  `created_by` varchar(20) DEFAULT NULL,
  `create_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_by` varchar(20) DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`locator_id`),
  KEY `fk_location_id` (`location_id`),
  CONSTRAINT `fk_location_id` FOREIGN KEY (`location_id`) REFERENCES `location_master` (`location_code`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `locator_master`
--

LOCK TABLES `locator_master` WRITE;
/*!40000 ALTER TABLE `locator_master` DISABLE KEYS */;
INSERT INTO `locator_master` VALUES ('BNG',1,'Locator 1','Admin','2025-03-18 14:23:12','Admin',NULL),('BNG',2,'Locator 2','Admin','2025-03-18 14:23:12','Admin',NULL),('BNG',3,'Locator 3','Admin','2025-03-18 14:23:12','Admin',NULL),('BNG',4,'Locator 4','Admin','2025-03-18 14:23:12','Admin',NULL),('GBR',5,'Locator 5','Admin','2025-08-04 09:05:45','Admin',NULL),('GBR',6,'Locator 6','Admin','2025-08-04 09:05:56','Admin',NULL),('GBR',7,'Locator 7','Admin','2025-08-04 09:06:10','Admin',NULL),('GBR',8,'Locator 8','Admin','2025-08-04 09:06:15','Admin',NULL),('IAO',9,'Locator 9','Admin','2025-08-04 09:06:54','Admin',NULL),('IAO',10,'Locator 10','Admin','2025-08-04 09:06:58','Admin',NULL),('IAO',11,'Locator 11','Admin','2025-08-04 09:07:01','Admin',NULL),('IAO',12,'Locator 12','Admin','2025-08-04 09:07:05','Admin',NULL),('KSO',13,'Locator 13','Admin','2025-08-04 09:08:04','Admin',NULL),('KSO',14,'Locator 14','Admin','2025-08-04 09:08:09','Admin',NULL),('KSO',15,'Locator 15','Admin','2025-08-04 09:08:12','Admin',NULL),('KSO',16,'Locator 16','Admin','2025-08-04 09:08:16','Admin',NULL),('VBO',17,'Locator 17','Admin','2025-08-04 09:08:53','Admin',NULL),('VBO',18,'Locator 18','Admin','2025-08-04 09:08:56','Admin',NULL),('VBO',19,'Locator 19','Admin','2025-08-04 09:08:58','Admin',NULL),('VBO',20,'Locator 20','Admin','2025-08-04 09:09:01','Admin',NULL);
/*!40000 ALTER TABLE `locator_master` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-29 21:41:35
