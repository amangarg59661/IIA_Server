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
-- Table structure for table `approval_limit_master`
--

DROP TABLE IF EXISTS `approval_limit_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `approval_limit_master` (
  `limit_id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` int DEFAULT NULL,
  `role_name` varchar(100) NOT NULL,
  `category` varchar(50) DEFAULT NULL,
  `department_name` varchar(100) DEFAULT NULL,
  `location` varchar(100) DEFAULT NULL,
  `min_amount` decimal(15,2) DEFAULT '0.00',
  `max_amount` decimal(15,2) DEFAULT NULL,
  `escalation_role_id` int DEFAULT NULL,
  `escalation_role_name` varchar(100) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `priority` int DEFAULT '0',
  `created_by` varchar(100) DEFAULT NULL,
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_by` varchar(100) DEFAULT NULL,
  `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`limit_id`),
  KEY `idx_approval_limit_role_name` (`role_name`),
  KEY `idx_approval_limit_category` (`category`),
  KEY `idx_approval_limit_active` (`is_active`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `approval_limit_master`
--

LOCK TABLES `approval_limit_master` WRITE;
/*!40000 ALTER TABLE `approval_limit_master` DISABLE KEYS */;
INSERT INTO `approval_limit_master` VALUES (1,NULL,'Purchase Head','NON_COMPUTER',NULL,NULL,0.00,50000.00,NULL,NULL,1,1,NULL,'2026-01-21 03:15:11',NULL,'2026-01-21 03:15:11'),(2,NULL,'Head SEG','NON_COMPUTER',NULL,NULL,50001.00,100000.00,NULL,'Director',1,1,NULL,'2026-01-21 03:15:11',NULL,'2026-01-21 03:15:11'),(3,NULL,'Dean','NON_COMPUTER',NULL,NULL,50001.00,150000.00,NULL,'Director',1,1,NULL,'2026-01-21 03:15:11',NULL,'2026-01-21 03:15:11'),(4,NULL,'Project Head','PROJECT',NULL,NULL,0.00,NULL,NULL,'Director',1,1,NULL,'2026-01-21 03:15:11',NULL,'2026-01-21 03:15:11'),(5,NULL,'Computer Committee Chairman','COMPUTER',NULL,NULL,0.00,NULL,NULL,'Director',1,1,NULL,'2026-01-21 03:15:11',NULL,'2026-01-21 03:15:11'),(6,NULL,'Administrative Officer','ALL',NULL,NULL,0.00,NULL,NULL,NULL,1,1,NULL,'2026-01-21 03:15:11',NULL,'2026-01-21 03:15:11'),(7,NULL,'Engineer In-Charge','ALL',NULL,NULL,0.00,NULL,NULL,NULL,1,1,NULL,'2026-01-21 03:15:11',NULL,'2026-01-21 03:15:11'),(8,NULL,'Professor In-Charge','ALL',NULL,NULL,0.00,NULL,NULL,NULL,1,1,NULL,'2026-01-21 03:15:11',NULL,'2026-01-21 03:15:11');
/*!40000 ALTER TABLE `approval_limit_master` ENABLE KEYS */;
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
