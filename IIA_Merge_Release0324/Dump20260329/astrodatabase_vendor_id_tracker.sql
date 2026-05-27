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
-- Table structure for table `vendor_id_tracker`
--

DROP TABLE IF EXISTS `vendor_id_tracker`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vendor_id_tracker` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `primary_business` varchar(100) NOT NULL,
  `prefix` varchar(10) NOT NULL,
  `last_sequence` int DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `primary_business` (`primary_business`),
  UNIQUE KEY `prefix` (`prefix`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vendor_id_tracker`
--

LOCK TABLES `vendor_id_tracker` WRITE;
/*!40000 ALTER TABLE `vendor_id_tracker` DISABLE KEYS */;
INSERT INTO `vendor_id_tracker` VALUES (1,'Chemicals','CHEM',2),(2,'Computers & Peripherals','COMP',7),(3,'Electricals','ELEC',1),(4,'Electronics','ELTC',3),(5,'Optics','OPTI',3),(6,'Fabrication','FABR',1),(7,'Furniture','FURN',1),(8,'Hardware','HARD',0),(9,'Instrument/ Equipment & Machinery','INST',0),(10,'Software','SOFT',0),(11,'Vehicles','VEHI',0),(12,'Stationary','STAT',0),(13,'Miscellaneous','MISC',0),(14,'Services','SERV',0);
/*!40000 ALTER TABLE `vendor_id_tracker` ENABLE KEYS */;
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
