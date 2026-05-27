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
-- Table structure for table `iia_address_for_consignee_location`
--

DROP TABLE IF EXISTS `iia_address_for_consignee_location`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `iia_address_for_consignee_location` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `consignee` varchar(255) DEFAULT NULL,
  `iia_address` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `iia_address_for_consignee_location`
--

LOCK TABLES `iia_address_for_consignee_location` WRITE;
/*!40000 ALTER TABLE `iia_address_for_consignee_location` DISABLE KEYS */;
INSERT INTO `iia_address_for_consignee_location` VALUES (1,'Bangalore','Indian Institute of Astrophysics, 2nd Block, 100 Feet Road, Koramangala, Bangalore – 560 034, Karnataka State, Ph: 080 – 2254 1340 / 1234, Email: purchase@iiap.res.in'),(2,'Kavalur','Vainu Bappu Observatory (VBO), Indian Institute of Astrophysics, Kavalur (Village & Post), Alangayam – Jamunamarathur Road, Vaniyambadi Taluk – 635701,Tirupattur District, Tamil Nadu State, Mob: 90357 30557, Tel : 04174-203 119/118/117.'),(3,'Hosakote','Center for Research and Education in Science and Technology (CREST), Indian Institute of Astrophysics, Opp. to KMF Milk Dairy, Shidlaghatta Road, Jodi Dasarahalli Post, Hosakote Taluk - 562 114, Bangalore Rural District, Karnataka State, Mob: 89714 34634, Tel : 079-319 72/52.'),(4,'Gauribidanur','Radio Astronomy Field Station,  Gauribidanur Observatory, Indian Institute of Astrophysics, Kotaladinne, Hossur Post, Tumkur-Madhugiri Road,  Gauribidanur Taluk, Chikkaballapur Dist. – 561 210, Karnataka State, Mob: 88846 77377, Tel : 08155-291655.'),(5,'Kodaikanal','Kodaikanal Solar Observatory (KSO), Indian Institute of Astrophysics, Observatory (PO), Near Govt. Rose Garden, Kodaikanal – 624103, Dindigul Dist., Tamil Nadu, Mob: 90520 87403, Tel: 04542-240 245/588/242.'),(6,'Hanle','Indian Astronomical Observatory (IAO), Indian Institute of Astrophysics, Hanle Village, Leh-194 101, Ladakh UT, Mob: 9419231904.'),(7,'Leh','Indian Astronomical Observatory (IAO), Indian Institute of Astrophysics,  Skara Yokma, Leh–194 101, Ladakh UT, Tel: 01982-252195, 253104, Mob: 9419231904.');
/*!40000 ALTER TABLE `iia_address_for_consignee_location` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-29 21:41:30
