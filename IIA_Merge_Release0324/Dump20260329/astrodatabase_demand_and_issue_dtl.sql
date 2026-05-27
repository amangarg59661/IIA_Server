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
-- Table structure for table `demand_and_issue_dtl`
--

DROP TABLE IF EXISTS `demand_and_issue_dtl`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `demand_and_issue_dtl` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `di_id` bigint DEFAULT NULL,
  `asset_id` int DEFAULT NULL,
  `asset_desc` varchar(500) DEFAULT NULL,
  `material_code` varchar(100) DEFAULT NULL,
  `material_desc` varchar(500) DEFAULT NULL,
  `quantity` decimal(18,2) NOT NULL,
  `receiver_locator_id` int DEFAULT NULL,
  `sender_locator_id` int DEFAULT NULL,
  `unit_price` decimal(18,2) DEFAULT NULL,
  `depriciation_rate` decimal(18,2) DEFAULT NULL,
  `book_value` decimal(18,2) DEFAULT NULL,
  `uom` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_di_id` (`di_id`),
  CONSTRAINT `fk_di_id` FOREIGN KEY (`di_id`) REFERENCES `demand_and_issue_master` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `demand_and_issue_dtl`
--

LOCK TABLES `demand_and_issue_dtl` WRITE;
/*!40000 ALTER TABLE `demand_and_issue_dtl` DISABLE KEYS */;
INSERT INTO `demand_and_issue_dtl` VALUES (1,1,NULL,NULL,'M1110','laptop',1.00,NULL,2,0.00,0.00,NULL,'Numbers'),(2,1,NULL,NULL,'M1111','Pen',2.00,NULL,2,0.00,0.00,NULL,'Numbers'),(3,2,NULL,NULL,'M1111','Pen',3.00,NULL,2,0.00,0.00,NULL,'Numbers'),(4,2,NULL,NULL,'M1110','laptop',1.00,NULL,2,0.00,0.00,NULL,'Numbers'),(5,3,NULL,NULL,'M1111','Pen',4.00,NULL,2,0.00,0.00,NULL,'Numbers');
/*!40000 ALTER TABLE `demand_and_issue_dtl` ENABLE KEYS */;
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
