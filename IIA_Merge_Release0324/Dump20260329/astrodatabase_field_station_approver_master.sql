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
-- Table structure for table `field_station_approver_master`
--

DROP TABLE IF EXISTS `field_station_approver_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `field_station_approver_master` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `field_station_name` varchar(100) NOT NULL,
  `incharge_employee_id` varchar(50) DEFAULT NULL,
  `incharge_role_id` int DEFAULT NULL,
  `approval_limit` decimal(15,2) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_by` varchar(100) DEFAULT NULL,
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_by` varchar(100) DEFAULT NULL,
  `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `incharge_type` varchar(50) DEFAULT 'ENGINEER_INCHARGE',
  `incharge_employee_name` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_station_incharge_type` (`field_station_name`,`incharge_type`),
  KEY `idx_field_station_active` (`is_active`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `field_station_approver_master`
--

LOCK TABLES `field_station_approver_master` WRITE;
/*!40000 ALTER TABLE `field_station_approver_master` DISABLE KEYS */;
INSERT INTO `field_station_approver_master` VALUES (9,'Hosakote','TEST-A-15',NULL,NULL,1,'SYSTEM','2026-03-04 09:11:42',NULL,'2026-03-04 09:11:42','PROFESSOR_INCHARGE','Test ProfIC HSK'),(10,'Gauribidanur','TEST-A-16',NULL,NULL,1,'SYSTEM','2026-03-04 09:11:42',NULL,'2026-03-04 09:11:42','PROFESSOR_INCHARGE','Test ProfIC GBN'),(11,'Kavalur','TEST-A-12',NULL,NULL,1,'SYSTEM','2026-03-04 09:11:42',NULL,'2026-03-04 09:11:42','ENGINEER_INCHARGE','Test EngIC KVL'),(12,'Kavalur','TEST-A-17',NULL,NULL,1,'SYSTEM','2026-03-04 09:11:42',NULL,'2026-03-04 09:11:42','PROFESSOR_INCHARGE','Test ProfIC KVL'),(13,'Kodaikanal','TEST-A-13',NULL,NULL,1,'SYSTEM','2026-03-04 09:11:42',NULL,'2026-03-04 09:11:42','ENGINEER_INCHARGE','Test EngIC KDK'),(14,'Kodaikanal','TEST-A-18',NULL,NULL,1,'SYSTEM','2026-03-04 09:11:42',NULL,'2026-03-04 09:11:42','PROFESSOR_INCHARGE','Test ProfIC KDK'),(15,'Leh','TEST-A-14',NULL,NULL,1,'SYSTEM','2026-03-04 09:11:42',NULL,'2026-03-04 09:11:42','ENGINEER_INCHARGE','Test EngIC LEH'),(16,'Leh','TEST-A-19',NULL,NULL,1,'SYSTEM','2026-03-04 09:11:42',NULL,'2026-03-04 09:11:42','PROFESSOR_INCHARGE','Test ProfIC LEH');
/*!40000 ALTER TABLE `field_station_approver_master` ENABLE KEYS */;
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
