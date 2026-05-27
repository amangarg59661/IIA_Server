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
-- Table structure for table `transition_master`
--

DROP TABLE IF EXISTS `transition_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transition_master` (
  `transitionId` int NOT NULL AUTO_INCREMENT,
  `transitionName` varchar(255) NOT NULL,
  `workflowId` int NOT NULL,
  `currentRoleId` int NOT NULL,
  `nextRoleId` int DEFAULT NULL,
  `previousRoleId` int DEFAULT NULL,
  `conditionId` int DEFAULT NULL,
  `transitionOrder` int NOT NULL,
  `transitionSubOrder` int NOT NULL,
  `createdDate` datetime DEFAULT NULL,
  `createdBy` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`transitionId`)
) ENGINE=InnoDB AUTO_INCREMENT=77 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transition_master`
--

LOCK TABLES `transition_master` WRITE;
/*!40000 ALTER TABLE `transition_master` DISABLE KEYS */;
INSERT INTO `transition_master` VALUES (1,'Submitted to Reporting officer',1,1,2,NULL,NULL,1,1,NULL,NULL),(2,'Submitted to Project head',1,2,6,1,1,2,1,NULL,NULL),(3,'Submitted to Administrativ officer',1,2,3,1,28,2,2,NULL,NULL),(4,'Submitted to Computer Committee',1,2,4,1,29,2,3,NULL,NULL),(5,'Submitted to Field Station Incharge',1,2,5,1,31,2,4,NULL,NULL),(6,'Submitted to Computer Committee',1,2,4,1,30,2,5,NULL,NULL),(7,'Submitted to Admin officer',1,4,3,2,29,3,2,NULL,NULL),(8,'Submitted to Field Station Incharge',1,4,5,4,30,3,3,NULL,NULL),(9,'Submitted to Administrativ officer',1,5,3,NULL,NULL,4,1,NULL,NULL),(10,'Final Submission',1,3,NULL,NULL,6,5,1,NULL,NULL),(11,'Submitted to Heas SAG',1,3,8,NULL,7,5,2,NULL,NULL),(12,'Submitted to Dean',1,3,7,NULL,8,5,3,NULL,NULL),(13,'Final Submission',1,7,NULL,NULL,25,6,1,NULL,NULL),(14,'Submitted to Director',1,8,9,NULL,26,6,2,NULL,NULL),(15,'Final Submission',1,9,NULL,NULL,NULL,7,1,NULL,NULL),(16,'Submitted to Director',1,6,9,NULL,1,3,2,NULL,NULL),(17,'Final Submission',1,10,NULL,NULL,NULL,8,1,NULL,NULL),(18,'Submitted to Reporting officer',2,14,2,NULL,NULL,1,1,NULL,NULL),(19,'Submitted to Project head',2,2,6,NULL,9,2,1,NULL,NULL),(20,'Submitted to Store Purchase Officer',2,2,11,NULL,18,2,2,NULL,NULL),(21,'Submitted to Store Purchase Officer',2,6,11,NULL,NULL,3,1,NULL,NULL),(22,'Submitted to Billing Section Personnel',2,11,12,NULL,NULL,4,1,NULL,NULL),(23,'Submitted to Account Officer',2,12,13,NULL,NULL,5,1,NULL,NULL),(24,'Submitted to Administrativ officer',2,13,3,NULL,NULL,6,1,NULL,NULL),(25,'Final Submission',2,3,NULL,NULL,NULL,7,1,NULL,NULL),(26,'Submitted to Billing Section Personnel',3,15,11,NULL,NULL,1,1,NULL,NULL),(27,'Submitted to Account Officer',3,11,13,NULL,NULL,2,1,NULL,NULL),(28,'Submitted to Administrativ officer',3,13,3,NULL,NULL,3,1,NULL,NULL),(29,'Final Submission',3,3,NULL,NULL,10,4,1,NULL,NULL),(30,'Submitted to Project head',3,3,6,NULL,13,4,2,NULL,NULL),(31,'Submitted to Heas SAG',3,3,8,NULL,11,4,3,NULL,NULL),(32,'Submitted to Dean',3,3,7,NULL,12,4,4,NULL,NULL),(33,'Submitted to Director',3,6,9,NULL,NULL,5,1,NULL,NULL),(34,'Final Submission',3,8,NULL,NULL,NULL,6,1,NULL,NULL),(35,'Final Submission',3,7,NULL,NULL,NULL,7,1,NULL,NULL),(36,'Final Submission',3,9,NULL,NULL,NULL,8,1,NULL,NULL),(37,'Submitted to Evaluator',4,16,22,NULL,NULL,1,1,NULL,NULL),(38,'Submitted to Project Dept',7,17,18,NULL,14,1,1,NULL,NULL),(39,'Submitted to Director',7,17,9,NULL,15,1,2,NULL,NULL),(40,'Submitted to Project Dept',7,9,18,NULL,NULL,2,1,NULL,NULL),(41,'Final Submission',7,18,NULL,NULL,16,3,1,NULL,NULL),(42,'Final Submission',7,18,NULL,NULL,17,4,1,NULL,NULL),(43,'Submitted to Billing Section Personnel',5,20,11,NULL,NULL,1,1,NULL,NULL),(44,'Submitted to Account Officer',5,11,13,NULL,NULL,2,1,NULL,NULL),(45,'Submitted to Administrativ officer',5,13,3,NULL,NULL,3,1,NULL,NULL),(46,'Final Submission',5,3,NULL,NULL,10,4,1,NULL,NULL),(47,'Submitted to Project head',5,3,6,NULL,13,4,2,NULL,NULL),(48,'Submitted to Heas SAG',5,3,8,NULL,11,4,3,NULL,NULL),(49,'Submitted to Dean',5,3,7,NULL,12,4,4,NULL,NULL),(50,'Submitted to Director',5,6,9,NULL,NULL,5,1,NULL,NULL),(51,'Final Submission',5,8,NULL,NULL,NULL,6,1,NULL,NULL),(52,'Final Submission',5,7,NULL,NULL,NULL,7,1,NULL,NULL),(53,'Final Submission',5,9,NULL,NULL,NULL,8,1,NULL,NULL),(54,'Submitted to Billing Section Personnel',6,19,11,NULL,NULL,1,1,NULL,NULL),(55,'Submitted to Account Officer',6,11,13,NULL,NULL,2,1,NULL,NULL),(56,'Submitted to Administrativ officer',6,13,3,NULL,NULL,3,1,NULL,NULL),(57,'Final Submission',6,3,NULL,NULL,10,4,1,NULL,NULL),(58,'Submitted to Project head',6,3,6,NULL,13,4,2,NULL,NULL),(59,'Submitted to Heas SAG',6,3,8,NULL,11,4,3,NULL,NULL),(60,'Submitted to Dean',6,3,7,NULL,12,4,4,NULL,NULL),(61,'Submitted to Director',6,6,9,NULL,NULL,5,1,NULL,NULL),(62,'Final Submission',6,8,NULL,NULL,NULL,6,1,NULL,NULL),(63,'Final Submission',6,7,NULL,NULL,NULL,7,1,NULL,NULL),(64,'Final Submission',6,9,NULL,NULL,NULL,8,1,NULL,NULL),(65,'Final Submission',1,6,NULL,NULL,19,3,1,NULL,NULL),(66,'Final Submission',4,22,NULL,NULL,NULL,2,1,NULL,NULL),(67,'Submitted to Director',3,3,9,NULL,20,4,5,NULL,NULL),(68,'Submitted to Director',5,3,9,NULL,20,4,5,NULL,NULL),(69,'Submitted to Director',6,3,9,NULL,20,4,5,NULL,NULL),(70,'Submitted to Director',1,7,9,NULL,23,5,4,NULL,NULL),(71,'Final Submission',1,8,NULL,NULL,27,6,3,NULL,NULL),(72,'Submitted to Store Purchase Officer',10,23,11,NULL,NULL,1,1,NULL,NULL),(73,'Submitted to Billing Section Personnel',10,11,12,NULL,NULL,2,1,NULL,NULL),(74,'Submitted to Account Officer',10,12,13,NULL,NULL,3,1,NULL,NULL),(75,'Submitted to Administrative Officer',10,13,3,NULL,NULL,4,1,NULL,NULL),(76,'Final Submission',10,3,NULL,NULL,NULL,5,1,NULL,NULL);
/*!40000 ALTER TABLE `transition_master` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-29 21:41:29
