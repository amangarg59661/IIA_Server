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
-- Table structure for table `role_master`
--

DROP TABLE IF EXISTS `role_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role_master` (
  `roleId` int NOT NULL AUTO_INCREMENT,
  `roleName` varchar(100) DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `createdBy` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`roleId`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_master`
--

LOCK TABLES `role_master` WRITE;
/*!40000 ALTER TABLE `role_master` DISABLE KEYS */;
INSERT INTO `role_master` VALUES (1,'Indent Creator',NULL,NULL),(2,'Reporting Officer',NULL,NULL),(3,'Administrative Officer',NULL,NULL),(4,'Computer Committee Chairman',NULL,NULL),(6,'Project Head',NULL,NULL),(7,'Dean',NULL,NULL),(9,'Director',NULL,NULL),(10,'Purchase Head',NULL,NULL),(11,'Store Purchase Officer',NULL,NULL),(12,'Billing Section Personnel',NULL,NULL),(13,'Account Officer',NULL,NULL),(14,'CP Creator',NULL,NULL),(15,'PO Creator',NULL,NULL),(16,'Tender Creator',NULL,NULL),(17,'Tender Evaluator',NULL,NULL),(18,'Purchase Dept',NULL,NULL),(19,'WO Creator',NULL,NULL),(20,'SO Creator',NULL,NULL),(21,'Respective Project Head','2025-02-24 16:53:30',NULL),(22,'Tender Approver',NULL,NULL),(23,'Store Person','2025-03-20 15:24:30',NULL),(24,'Purchase personnel','2025-08-04 10:33:09',NULL),(25,'Admin','2025-12-03 11:41:03','SYSTEM'),(26,'Engineer In-Charge','2026-01-23 01:36:54','System'),(27,'Professor In-Charge','2026-01-23 01:36:54','System'),(28,'Head SEG','2026-01-23 01:36:54','System'),(30,'Indent Creator','2026-03-03 18:16:20','SYSTEM'),(31,'Reporting Officer','2026-03-03 18:16:20','SYSTEM'),(32,'Computer Committee Chairman','2026-03-03 18:16:20','SYSTEM'),(33,'Administrative Officer','2026-03-03 18:16:20','SYSTEM'),(34,'Director','2026-03-03 18:16:20','SYSTEM'),(35,'Head SEG','2026-03-03 18:16:20','SYSTEM'),(36,'Dean','2026-03-03 18:16:20','SYSTEM'),(37,'Engineer In-Charge','2026-03-03 18:16:20','SYSTEM'),(38,'Professor In-Charge','2026-03-03 18:16:20','SYSTEM');
/*!40000 ALTER TABLE `role_master` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-29 21:41:27
