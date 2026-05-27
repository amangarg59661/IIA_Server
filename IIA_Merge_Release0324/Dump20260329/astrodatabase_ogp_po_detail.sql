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
-- Table structure for table `ogp_po_detail`
--

DROP TABLE IF EXISTS `ogp_po_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ogp_po_detail` (
  `detail_id` int NOT NULL AUTO_INCREMENT,
  `ogp_sub_process_id` int NOT NULL,
  `material_code` varchar(50) NOT NULL,
  `material_desc` varchar(50) NOT NULL,
  `uom_id` varchar(10) NOT NULL,
  `quantity` decimal(10,2) NOT NULL,
  PRIMARY KEY (`detail_id`),
  KEY `ogp_sub_process_id` (`ogp_sub_process_id`),
  KEY `material_code` (`material_code`),
  CONSTRAINT `ogp_po_detail_ibfk_1` FOREIGN KEY (`ogp_sub_process_id`) REFERENCES `ogp_master_po` (`ogp_sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `ogp_po_detail_ibfk_2` FOREIGN KEY (`material_code`) REFERENCES `material_master` (`material_code`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ogp_po_detail`
--

LOCK TABLES `ogp_po_detail` WRITE;
/*!40000 ALTER TABLE `ogp_po_detail` DISABLE KEYS */;
INSERT INTO `ogp_po_detail` VALUES (1,1,'M1102','Stationary','Numbers',1.00),(2,2,'M1102','Stationary','Numbers',2.00);
/*!40000 ALTER TABLE `ogp_po_detail` ENABLE KEYS */;
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
