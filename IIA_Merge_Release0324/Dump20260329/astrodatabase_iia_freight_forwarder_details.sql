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
-- Table structure for table `iia_freight_forwarder_details`
--

DROP TABLE IF EXISTS `iia_freight_forwarder_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `iia_freight_forwarder_details` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `country_name` varchar(255) NOT NULL,
  `freight_forwarder_details` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `iia_freight_forwarder_details`
--

LOCK TABLES `iia_freight_forwarder_details` WRITE;
/*!40000 ALTER TABLE `iia_freight_forwarder_details` DISABLE KEYS */;
INSERT INTO `iia_freight_forwarder_details` VALUES (1,'Austria','M/s Balmer Lawrie & Co., Ltd., Bengaluru, Contact: Mr. Arpan Choudhury, e-mail: choudhury.arpan@balmerlawrie.com through their Austria Contact:\nCargo-partner GmbH, \nDivision ABC Air-Sea Cargo, Am Gewerbepark 8,\nA-8402 Werndorf, Austria\nMarkus Hofer, Airfreight Dept.\nEmail: Markus.Hofer@abc-airsea.com\nTEL: +43 3135 57899-15260 \nM: +43 664 9675757\n'),(2,'Australia','M/s Balmer Lawrie & Co., Ltd., Bengaluru, Contact: Mr. Arpan Choudhury, e-mail: choudhury.arpan@balmerlawrie.com through their Australia Contact:\nFreightnet International (Vic) Pty Ltd\n9c International Square, Tullamarine, \nVictoria, Post: P.O Box 537, VIC. 3043, Australia\nContact Person: Nick Kassis, \nEmail: nick@freightnetvic.com.au\nTel: 613 9335 4511\n'),(3,'ARGENTINA','M/s Balmer Lawrie & Co., Ltd., Bengaluru, Contact: Mr. Arpan Choudhury, e-mail: choudhury.arpan@balmerlawrie.com through their Argentina Contact:\nDelfino Global\nSan Martín 439 - 2do Piso - Buenos Aires - Argentina\nEsteban Nashiro\nenashiro@delfinoglobal.com\nTel: (+54 11) 6320 - 1061\nMob: (+54 9 11) 3581-6105\n'),(4,'BELGIUM','M/s Balmer Lawrie & Co., Ltd., Bengaluru, Contact: Mr. Arpan Choudhury, e-mail: choudhury.arpan@balmerlawrie.com through their Belgium Contact:\nCargo-partner B.V.\nDivision ABC Air-Sea Cargo\nLIEGE AIRPORT BUSINESS PARK SA\nAéroport de Liège numéro L378\nB-4460 Grâce-Hollogne , Belgium\nJessica Maita, Customer Service, Jessica.Maita@abc-airsea.com\nHe Susan, Inside Sales, Susan.He@abc-airsea.com\nTEL:  +32 4 235 8969 \nM: +32 4 77 77 18 68\n');
/*!40000 ALTER TABLE `iia_freight_forwarder_details` ENABLE KEYS */;
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
