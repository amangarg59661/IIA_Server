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
-- Table structure for table `transition_condition_master`
--

DROP TABLE IF EXISTS `transition_condition_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transition_condition_master` (
  `conditionId` int NOT NULL AUTO_INCREMENT,
  `workflowId` int NOT NULL,
  `conditionKey` varchar(255) NOT NULL,
  `conditionValue` varchar(255) NOT NULL,
  `createdDate` datetime DEFAULT NULL,
  `createdBy` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`conditionId`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transition_condition_master`
--

LOCK TABLES `transition_condition_master` WRITE;
/*!40000 ALTER TABLE `transition_condition_master` DISABLE KEYS */;
INSERT INTO `transition_condition_master` VALUES (1,1,'ProjectName','Not Empty',NULL,NULL),(2,1,'MaterialCategory','Normal',NULL,NULL),(3,1,'MaterialCategory','Computer',NULL,NULL),(4,1,'ConsignesLocation','Normal',NULL,NULL),(5,1,'ConsignesLocation','Computer',NULL,NULL),(6,1,'TotalPriceOfAllMaterials','50000',NULL,NULL),(7,1,'TotalPriceOfAllMaterialsAndDept','100000(Engineering)',NULL,NULL),(8,1,'TotalPriceOfAllMaterialsAndDept','150000(OtherDept)',NULL,NULL),(9,2,'ProjectName','Not Empty',NULL,NULL),(10,3,'TotalPriceOfAllMaterials','50000',NULL,NULL),(11,3,'TotalPriceOfAllMaterials','100000',NULL,NULL),(12,3,'TotalPriceOfAllMaterials','150000',NULL,NULL),(13,3,'ProjectName','Not Empty',NULL,NULL),(14,4,'totalTenderValue','1000000',NULL,NULL),(15,4,'totalTenderValue','10000000000',NULL,NULL),(16,4,'bidType','Double',NULL,NULL),(17,4,'bidType','Single',NULL,NULL),(18,2,'ProjectName','Empty',NULL,NULL),(19,1,'projectLimit','Under',NULL,NULL),(20,3,'TotalPriceOfAllMaterials','10000000000',NULL,NULL),(21,1,'TotalPriceOfAllMaterials','10000000000',NULL,NULL),(22,1,'TotalPriceOfAllMaterialsAndDept','10000000000',NULL,NULL),(23,1,'TotalPriceOfAllMaterialsAnd','150000',NULL,NULL),(24,1,'TotalPriceOfAllMaterialsAndDept','150001(OtherDept)',NULL,NULL),(25,1,'TotalPriceOfAllMaterials','150000',NULL,NULL),(26,1,'TotalPriceOfAllMaterialsAnd','100000',NULL,NULL),(27,1,'TotalPriceOfAllMaterials','100000',NULL,NULL),(28,1,'materialCategoryAndconsignesLocation','Normal+Normal',NULL,NULL),(29,1,'materialCategoryAndconsignesLocation','Computer+Normal',NULL,NULL),(30,1,'materialCategoryAndconsignesLocation','Computer+Computer',NULL,NULL),(31,1,'materialCategoryAndconsignesLocation','Normal+Computer',NULL,NULL);
/*!40000 ALTER TABLE `transition_condition_master` ENABLE KEYS */;
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
