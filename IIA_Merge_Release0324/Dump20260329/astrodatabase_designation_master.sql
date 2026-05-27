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
-- Table structure for table `designation_master`
--

DROP TABLE IF EXISTS `designation_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `designation_master` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `designation_name` varchar(100) NOT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `designation_name` (`designation_name`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `designation_master`
--

LOCK TABLES `designation_master` WRITE;
/*!40000 ALTER TABLE `designation_master` DISABLE KEYS */;
INSERT INTO `designation_master` VALUES (1,'Indent Creator',1,'2025-11-25 13:20:13'),(2,'Director',1,'2025-11-25 13:20:13'),(3,'Reporting Officer',1,'2025-11-25 13:20:13'),(4,'Administrative Officer',1,'2025-11-25 13:20:13'),(5,'Computer Committee Chairman',1,'2025-11-25 13:20:13'),(6,'Field Station Incharge',1,'2025-11-25 13:20:13'),(7,'Project Head',1,'2025-11-25 13:20:13'),(8,'Dean',1,'2025-11-25 13:20:13'),(9,'Head SAG',1,'2025-11-25 13:20:13'),(10,'Purchase Head',1,'2025-11-25 13:20:13'),(11,'Store Purchase Officer',1,'2025-11-25 13:20:13'),(12,'Billing Section Personnel',1,'2025-11-25 13:20:13'),(13,'Account Officer',1,'2025-11-25 13:20:13'),(14,'CP Creator',1,'2025-11-25 13:20:13'),(15,'PO Creator',1,'2025-11-25 13:20:13'),(16,'Tender Creator',1,'2025-11-25 13:20:13'),(17,'Tender Evaluator',1,'2025-11-25 13:20:13'),(18,'Purchase Dept',1,'2025-11-25 13:20:13'),(19,'WO Creator',1,'2025-11-25 13:20:13'),(20,'SO Creator',1,'2025-11-25 13:20:13'),(21,'Respective Project Head',1,'2025-11-25 13:20:13'),(22,'Tender Approver',1,'2025-11-25 13:20:13'),(23,'Store Person',1,'2025-11-25 13:20:13'),(24,'Purchase Personnel',1,'2025-11-25 13:20:13'),(25,'Software Engineer',1,'2025-11-25 13:20:13'),(26,'UDC',1,'2025-11-25 13:20:13');
/*!40000 ALTER TABLE `designation_master` ENABLE KEYS */;
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
