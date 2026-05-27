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
-- Table structure for table `sub_workflow_transition`
--

DROP TABLE IF EXISTS `sub_workflow_transition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sub_workflow_transition` (
  `subWorkflowTransitionId` int NOT NULL AUTO_INCREMENT,
  `workflowId` int NOT NULL,
  `workflowName` varchar(255) NOT NULL,
  `workflowTransitionId` int NOT NULL,
  `requestId` varchar(255) NOT NULL,
  `createdBy` int NOT NULL,
  `modifiedBy` int DEFAULT NULL,
  `status` varchar(255) NOT NULL,
  `action` varchar(45) DEFAULT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  `actionOn` int DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `modificationDate` datetime DEFAULT NULL,
  `workflowSequence` int NOT NULL,
  `transitionName` varchar(100) DEFAULT NULL,
  `transitionType` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`subWorkflowTransitionId`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sub_workflow_transition`
--

LOCK TABLES `sub_workflow_transition` WRITE;
/*!40000 ALTER TABLE `sub_workflow_transition` DISABLE KEYS */;
INSERT INTO `sub_workflow_transition` VALUES (1,7,'Tender Evaluator Workflow',8,'T1001',26,NULL,'Approved','Completed',NULL,18,'2025-06-24 16:02:37','2025-06-24 16:03:58',1,NULL,'Single'),(2,7,'Tender Evaluator Workflow',32,'T1002',26,NULL,'Approved','Completed',NULL,18,'2025-06-25 16:54:39','2025-06-25 16:55:14',1,NULL,'Single'),(3,7,'Tender Evaluator Workflow',54,'T1003',26,NULL,'Approved','Completed',NULL,18,'2025-06-27 12:48:45','2025-06-27 12:49:23',1,NULL,'Single'),(4,7,'Tender Evaluator Workflow',65,'T1004',26,NULL,'Approved','Completed',NULL,18,'2025-06-27 15:35:18','2025-06-27 15:35:56',1,NULL,'Single'),(5,7,'Tender Evaluator Workflow',76,'T1005',26,NULL,'Approved','Completed',NULL,18,'2025-06-27 16:09:27','2025-07-08 12:23:39',1,NULL,'Single'),(6,7,'Tender Evaluator Workflow',224,'T1012',26,NULL,'Approved','Completed',NULL,18,'2025-07-03 12:24:03','2025-07-03 15:13:30',1,'Phase_1','Double'),(7,7,'Tender Evaluator Workflow',226,'T1011',26,NULL,'Approved','Completed',NULL,18,'2025-07-03 15:08:37','2025-07-08 15:47:01',1,NULL,'Single'),(8,7,'Tender Evaluator Workflow',234,'T1013',26,NULL,'Approved','Completed',NULL,18,'2025-07-03 15:19:57','2025-07-08 15:47:27',1,NULL,'Single'),(9,7,'Tender Evaluator Workflow',255,'T1014',26,NULL,'Approved','Completed',NULL,18,'2025-07-04 15:21:25','2025-07-04 15:23:04',1,NULL,'Single'),(10,7,'Tender Evaluator Workflow',292,'T1017',26,NULL,'Approved','Completed',NULL,18,'2025-07-08 12:06:53','2025-07-08 15:46:41',1,'Phase_1','Double'),(11,7,'Tender Evaluator Workflow',296,'T1017',26,NULL,'Pending','Pending',NULL,18,'2025-07-08 16:02:22',NULL,1,'Phase_2','Double'),(12,7,'Tender Evaluator Workflow',299,'T1018',26,NULL,'Approved','Completed',NULL,18,'2025-07-08 16:05:37','2025-07-08 16:06:08',1,NULL,'Single'),(13,7,'Tender Evaluator Workflow',320,'T1020',26,NULL,'Approved','Completed',NULL,18,'2025-07-24 11:28:46','2025-07-24 11:30:33',1,'Phase_1','Double'),(14,7,'Tender Evaluator Workflow',322,'T1020',26,NULL,'Pending','Pending',NULL,18,'2025-07-24 11:36:59',NULL,1,'Phase_2','Double'),(15,7,'Tender Evaluator Workflow',339,'T1022',26,NULL,'Approved','Completed',NULL,18,'2025-07-26 07:44:26','2025-07-26 07:46:49',1,NULL,'Single'),(16,7,'Tender Evaluator Workflow',349,'T1023',18,NULL,'Approved','Completed',NULL,18,'2025-07-29 17:04:53','2025-07-30 12:03:49',1,NULL,'Single'),(17,7,'Tender Evaluator Workflow',356,'T1025',18,NULL,'Approved','Completed',NULL,18,'2025-07-30 10:57:25','2025-07-30 10:58:03',1,NULL,'Single'),(18,7,'Tender Evaluator Workflow',364,'T1026',18,NULL,'Approved','Completed',NULL,18,'2025-07-30 11:33:41','2025-07-30 12:03:45',1,'Phase_1','Double'),(19,7,'Tender Evaluator Workflow',367,'T1026',26,NULL,'Approved','Completed',NULL,18,'2025-07-30 12:13:07','2025-07-30 12:13:43',1,'Phase_2','Double');
/*!40000 ALTER TABLE `sub_workflow_transition` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-29 21:41:36
