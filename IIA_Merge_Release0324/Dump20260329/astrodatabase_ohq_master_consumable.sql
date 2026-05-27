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
-- Table structure for table `ohq_master_consumable`
--

DROP TABLE IF EXISTS `ohq_master_consumable`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ohq_master_consumable` (
  `ohq_id` int NOT NULL AUTO_INCREMENT,
  `material_code` varchar(50) NOT NULL,
  `locator_id` int NOT NULL,
  `book_value` decimal(10,2) NOT NULL,
  `depriciation_rate` decimal(10,2) NOT NULL,
  `unit_price` decimal(10,2) NOT NULL,
  `quantity` decimal(10,2) NOT NULL,
  `custodian_id` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`ohq_id`),
  KEY `material_code` (`material_code`),
  KEY `locator_id` (`locator_id`),
  CONSTRAINT `ohq_master_consumable_ibfk_1` FOREIGN KEY (`material_code`) REFERENCES `material_master` (`material_code`) ON UPDATE CASCADE,
  CONSTRAINT `ohq_master_consumable_ibfk_2` FOREIGN KEY (`locator_id`) REFERENCES `locator_master` (`locator_id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ohq_master_consumable`
--

LOCK TABLES `ohq_master_consumable` WRITE;
/*!40000 ALTER TABLE `ohq_master_consumable` DISABLE KEYS */;
INSERT INTO `ohq_master_consumable` VALUES (1,'M1110',2,0.00,0.00,0.00,2.00,'18'),(2,'M1111',2,0.00,0.00,0.00,9.00,'18'),(3,'M1111',1,500.00,0.00,500.00,5.00,'21'),(4,'M1111',2,300.00,0.00,300.00,3.00,'48'),(5,'M1112',1,15000.00,0.00,15000.00,1.00,'5'),(6,'M1111',1,100.00,0.00,100.00,1.00,'15');
/*!40000 ALTER TABLE `ohq_master_consumable` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-29 21:41:34
