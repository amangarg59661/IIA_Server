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
-- Table structure for table `gi_workflow_status`
--

DROP TABLE IF EXISTS `gi_workflow_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gi_workflow_status` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `process_id` varchar(50) DEFAULT NULL,
  `sub_process_id` int DEFAULT NULL,
  `action` varchar(50) DEFAULT NULL,
  `remarks` varchar(500) DEFAULT NULL,
  `created_by` int DEFAULT NULL,
  `create_date` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=56 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gi_workflow_status`
--

LOCK TABLES `gi_workflow_status` WRITE;
/*!40000 ALTER TABLE `gi_workflow_status` DISABLE KEYS */;
INSERT INTO `gi_workflow_status` VALUES (1,'INV1005',10,'Created','GI Created',18,'2025-07-18 11:25:34'),(2,'INV1005',10,'CHANGE REQUEST','not approved',29,'2025-07-18 11:28:11'),(3,'INV1005',10,'UPDATED','GI updated',18,'2025-07-18 11:28:58'),(4,'INV1005',10,'APPROVED','Approved',29,'2025-07-18 11:29:40'),(5,'INV1033',11,'Created','GI Created',18,'2025-09-13 10:36:33'),(6,'INV1033',11,'APPROVED','Approved',29,'2025-09-13 10:37:52'),(7,'INV1029',12,'Created','GI Created',18,'2025-09-13 10:40:45'),(8,'INV1029',12,'APPROVED','Approved',29,'2025-09-13 10:42:04'),(9,'INV1039',13,'Created','GI Created',18,'2025-09-17 15:34:47'),(10,'INV1039',13,'APPROVED','Approved',29,'2025-09-17 15:35:24'),(11,'INV1040',14,'Created','GI Created',18,'2025-09-17 15:37:45'),(12,'INV1040',14,'APPROVED','Approved',29,'2025-09-17 15:38:24'),(13,'INV1033',15,'Created','GI Created',18,'2025-09-22 11:31:09'),(20,'INV1041',16,'Created','GI Created',18,'2025-09-25 10:11:39'),(24,'INV1041',16,'REJECTED','2 Qty received not 1',29,'2025-09-25 10:13:40'),(25,'INV1041',19,'Created','GI Created',18,'2025-09-25 10:33:20'),(26,'INV1041',19,'APPROVED','Approved',29,'2025-09-25 10:34:23'),(27,'INV1041',22,'Created','GI Created',18,'2025-09-25 10:40:40'),(28,'INV1041',22,'APPROVED','Approved',29,'2025-09-25 10:45:14'),(29,'INV1042',23,'Created','GI Created',18,'2025-10-06 11:31:33'),(30,'INV1042',23,'CHANGE REQUEST','recheck and submit',29,'2025-10-06 11:37:10'),(31,'INV1042',23,'UPDATED','GI updated',18,'2025-10-06 11:38:03'),(32,'INV1042',23,'APPROVED','Approved',29,'2025-10-06 11:38:53'),(33,'INV1037',24,'Created','GI Created',18,'2025-10-06 11:57:01'),(34,'INV1037',24,'APPROVED','Approved',29,'2025-10-06 11:57:43'),(35,'INV1042',25,'Created','GI Created',18,'2025-10-06 12:41:33'),(36,'INV1042',25,'APPROVED','Approved',29,'2025-10-06 12:42:13'),(37,'INV1042',26,'Created','GI Created',43,'2025-10-07 15:12:21'),(38,'INV1040',27,'Created','GI Created',18,'2025-10-13 11:50:51'),(39,'INV1040',27,'APPROVED','Approved',29,'2025-10-13 11:54:52'),(40,'INV1042',28,'Created','GI Created',43,'2025-10-13 12:49:56'),(41,'INV1040',29,'Created','GI Created',18,'2025-10-13 17:48:30'),(42,'INV1040',29,'APPROVED','Approved',29,'2025-10-13 17:52:22'),(43,'INV1040',30,'Created','GI Created',43,'2025-10-15 12:06:26'),(44,'INV1040',30,'APPROVED','Approved',29,'2025-10-15 12:07:28'),(45,'INV1042',31,'Created','GI Created',43,'2025-10-17 16:29:31'),(46,'INV1042',31,'CHANGE REQUEST','resubmit with installation report',29,'2025-10-17 16:38:17'),(47,'INV1002',1,'UPDATED','GI updated',18,'2025-10-23 14:53:48'),(48,'INV1043',32,'Created','GI Created',18,'2025-10-23 16:33:52'),(49,'INV1043',32,'APPROVED','Approved',29,'2025-10-23 16:36:25'),(50,'INV1051',36,'Created','GI Created',43,'2025-11-03 11:38:52'),(51,'INV1051',36,'APPROVED','Approved',29,'2025-11-03 11:39:41'),(52,'INV1053',37,'Created','GI Created',43,'2025-11-03 12:35:17'),(53,'INV1053',37,'APPROVED','Approved',29,'2025-11-03 12:35:51'),(54,'INV1051',38,'Created','GI Created',43,'2025-11-03 12:38:15'),(55,'INV1051',38,'APPROVED','Approved',29,'2025-11-03 12:38:39');
/*!40000 ALTER TABLE `gi_workflow_status` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-29 21:41:33
