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
-- Table structure for table `uom_master`
--

DROP TABLE IF EXISTS `uom_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `uom_master` (
  `uom_code` varchar(50) NOT NULL,
  `uom_name` varchar(255) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`uom_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `uom_master`
--

LOCK TABLES `uom_master` WRITE;
/*!40000 ALTER TABLE `uom_master` DISABLE KEYS */;
INSERT INTO `uom_master` VALUES ('BATCH','BATCH','','','2025-06-23 05:03:36','2025-06-23 05:03:36'),('Bottle','BOTTLE','','','2025-06-23 05:03:36','2025-06-23 05:03:36'),('Boxes','BOXES','','','2025-06-23 05:03:36','2025-06-23 05:03:36'),('cans','CANS','','','2025-06-23 05:03:36','2025-06-23 05:03:36'),('cart load','CART LOAD','','','2025-06-23 05:03:36','2025-06-23 05:03:36'),('CARTON','CARTON','','','2025-06-23 05:03:36','2025-06-23 05:03:36'),('cement bag','CEMENT BAG','','','2025-06-23 05:03:36','2025-06-23 05:03:36'),('Coils','COIL','','','2025-06-23 05:03:36','2025-06-23 05:03:36'),('Cubic Meter','CUBIC METER','','','2025-06-23 05:03:36','2025-06-23 05:03:36'),('Cylinder','CYLD','','','2025-06-23 05:03:36','2025-06-23 05:03:36'),('Dozen','DOZEN','','','2025-06-23 05:03:36','2025-06-23 05:03:36'),('Each','EACH','','','2025-06-23 05:03:36','2025-06-23 05:03:36'),('Feet','FEET','','','2025-06-23 05:03:36','2025-06-23 05:03:36'),('GRAMS','GRAMS','','','2025-06-23 05:03:36','2025-06-23 05:03:36'),('Kilograms','KG','','','2025-06-23 05:03:36','2025-06-23 05:03:36'),('Length','LENGTH','','','2025-06-23 05:03:36','2025-06-23 05:03:36'),('Litres','LTS','','','2025-06-23 05:03:36','2025-06-23 05:03:36'),('lot','LOT','','','2025-06-23 05:03:36','2025-06-23 05:03:36'),('Lumpsum','LUMPSUM','','','2025-06-23 05:03:36','2025-06-23 05:03:36'),('Man Days','MANDAYS','','','2025-06-23 05:03:36','2025-06-23 05:03:36'),('MAN HOUR RATE','MAN HOUR RATE','','','2025-06-23 05:03:36','2025-06-23 05:03:36'),('metres','MTRS','','','2025-06-23 05:03:36','2025-06-23 05:03:36'),('Metric Ton','TONNE','','','2025-06-23 05:03:36','2025-06-23 05:03:36'),('Numbers','NUMBERS','','','2025-06-23 05:03:36','2025-06-23 05:03:36'),('Ohm','OHM','','','2025-06-23 05:03:36','2025-06-23 05:03:36'),('packets','PACKETS','','','2025-06-23 05:03:36','2025-06-23 05:03:36'),('Pads','PADS','','','2025-06-23 05:03:36','2025-06-23 05:03:36'),('Pairs','PAIRS','','','2025-06-23 05:03:36','2025-06-23 05:03:36'),('PCS','PCS','','','2025-06-23 05:03:36','2025-06-23 05:03:36'),('Pipes','PIPES','','','2025-06-23 05:03:36','2025-06-23 05:03:36'),('Quarts','QUARTS','','','2025-06-23 05:03:36','2025-06-23 05:03:36'),('Rate per hour','RATE PER HOUR','','','2025-06-23 05:03:36','2025-06-23 05:03:36'),('Rate per month','RATE PER MONTH','','','2025-06-23 05:03:36','2025-06-23 05:03:36'),('Rate per Year','RATE PER YEAR','','','2025-06-23 05:03:36','2025-06-23 05:03:36'),('Reams','REAMS','','','2025-06-23 05:03:36','2025-06-23 05:03:36'),('Rolls','ROLLS','','','2025-06-23 05:03:36','2025-06-23 05:03:36'),('Sets','SETS','','','2025-06-23 05:03:36','2025-06-23 05:03:36'),('Sheets','SHEETS','','','2025-06-23 05:03:36','2025-06-23 05:03:36'),('Sq FT','SFT','','','2025-06-23 05:03:36','2025-06-23 05:03:36'),('Sq Mtrs','SQ METER','','','2025-06-23 05:03:36','2025-06-23 05:03:36'),('SYSTEM','SYSTEM','','','2025-06-23 05:03:36','2025-06-23 05:03:36'),('Thickness measuring sensors','SENSOR','','','2025-06-23 05:03:36','2025-06-23 05:03:36'),('UNIT','UNIT','','','2025-06-23 05:03:36','2025-06-23 05:03:36'),('watts','WTS','','','2025-06-23 05:03:36','2025-06-23 05:03:36');
/*!40000 ALTER TABLE `uom_master` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-29 21:41:31
