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
-- Table structure for table `vendor_master_util`
--

DROP TABLE IF EXISTS `vendor_master_util`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vendor_master_util` (
  `vendor_id` varchar(50) NOT NULL,
  `vendor_name` varchar(255) DEFAULT NULL,
  `vendor_type` varchar(100) DEFAULT NULL,
  `contact_number` varchar(20) DEFAULT NULL,
  `email_address` varchar(255) DEFAULT NULL,
  `registered_platform` tinyint(1) DEFAULT NULL,
  `pfms_vendor_code` varchar(100) DEFAULT NULL,
  `primary_business` varchar(255) DEFAULT NULL,
  `address` text,
  `alternate_email_or_phone_number` varchar(255) DEFAULT NULL,
  `fax_number` varchar(50) DEFAULT NULL,
  `pan_number` varchar(50) DEFAULT NULL,
  `gst_number` varchar(50) DEFAULT NULL,
  `bank_name` varchar(255) DEFAULT NULL,
  `account_number` varchar(50) DEFAULT NULL,
  `ifsc_code` varchar(50) DEFAULT NULL,
  `approval_status` enum('APPROVED','REJECTED','AWAITING_APPROVAL','CHANGE_REQUEST') DEFAULT NULL,
  `comments` text,
  `created_by` int DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `created_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `vendor_number` int DEFAULT NULL,
  `swift_code` varchar(100) DEFAULT NULL,
  `bic_code` varchar(100) DEFAULT NULL,
  `iban_aba_number` varchar(100) DEFAULT NULL,
  `sort_code` varchar(100) DEFAULT NULL,
  `bank_routing_number` varchar(100) DEFAULT NULL,
  `bank_address` varchar(500) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `place` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`vendor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vendor_master_util`
--

LOCK TABLES `vendor_master_util` WRITE;
/*!40000 ALTER TABLE `vendor_master_util` DISABLE KEYS */;
INSERT INTO `vendor_master_util` VALUES ('CHEM002','Moni Enterprises','Domestic','8878401401','abhinay@gazelle.in',0,'1234555','Chemicals','Big Bajar khhbfddbgv','abhinay.ksj@gmail.com',NULL,'ASDFG1234B','11AAAAA0000A1Z5','UCO Bank','345677654567','ASDF0999999','REJECTED','sorry no requirement',102,'102','2026-03-12 05:12:57','2026-03-12 05:12:57',2,NULL,NULL,NULL,NULL,NULL,NULL,'IN','AN','Nicobar'),('ELEC001','Test Vendor UI','Domestic','9801722265','ritwiksinghkkc@gmail.com',0,NULL,'Electricals','542-d, Regent Shipra Suncity','8986235162',NULL,'ABCDE1234S','29ABCDE1234F1Z5','HDFC Bank','12345678901','HDFC0001234','CHANGE_REQUEST','ok',NULL,NULL,'2025-11-25 09:38:46','2025-11-25 09:38:46',1,NULL,NULL,NULL,NULL,NULL,NULL,'IN','AR','Changlang'),('FURN001','Aman','Domestic','9801722265','ritwiksinghkkc@gmail.com',0,'12345678','Furniture','Jakkanpur d.v.c gate p.o g.p.o Right side gali house no. 2 Bandana Niwas','ritwiksinghkkc@gmail.com',NULL,'DWPPR8019S','29ABCDE1234F1Z5','HDFC Bank','12345678901','HDFC0001234','AWAITING_APPROVAL',NULL,NULL,NULL,'2026-03-03 05:39:58','2026-03-03 05:39:58',1,NULL,NULL,NULL,NULL,NULL,NULL,'IN','BR','Patna'),('OPTI001','RitwikSingh','Domestic','9801722265','ritwiksinghkkc@gmail.com',0,'12345678','Optics','542-d, Regent Shipra Suncity','8986235162',NULL,'DWPPR8019J','29ABCDE1234F1Z5','HDFC Bank','12345678901','HDFC0001234','REJECTED','ok',61,'61','2025-11-23 09:42:53','2025-11-23 09:42:53',1,NULL,NULL,NULL,NULL,NULL,NULL,'AL','BR','Bashkia Berat'),('OPTI003','Test Vendor UI','Domestic','9801722265','ritwiksinghkkc@gmail.com',0,'12345678','Optics','542-d, Regent Shipra Suncity','8986235162',NULL,'DWPPQ8019K','29ABCDE1234F1Z5','HDFC Bank','12345678901','SBIN0040014','AWAITING_APPROVAL',NULL,NULL,NULL,'2025-11-25 09:42:23','2025-11-25 09:42:23',3,NULL,NULL,NULL,NULL,NULL,NULL,'IN','UP','Afzalgarh'),('V1033','kiran','Domestic','12345678','kudaykiran.9949@gmail.com',0,NULL,'Computers & Peripherals','HYd','456789',NULL,'3456789078','gH23','ICICI','1234','234567','REJECTED','dummy reject',NULL,NULL,'2025-11-07 15:04:59','2025-11-23 16:09:23',1033,NULL,NULL,NULL,NULL,NULL,NULL,'TD','EO','Fada'),('V1035','kiran','Domestic','123456','udaychowdhary743@gmail.com',0,NULL,'Chemicals','HYD','1234567',NULL,'234567890i','234','ICICI','SBI123','HJGS','REJECTED','ok',NULL,NULL,'2025-11-07 15:16:07','2025-11-23 16:09:23',1035,NULL,NULL,NULL,NULL,NULL,NULL,'DZ','46','El Malah'),('V1037','murali grp','Domestic','1234567','udaychowdhary743@gmail.com',0,NULL,'Computers & Peripherals','Hyderabad','456789',NULL,'GHT12345GH','YGUH12345','ICICI','ICICI1234','ICIC1234','REJECTED','ok',NULL,NULL,'2025-11-07 15:23:37','2025-11-23 16:09:23',1037,NULL,NULL,NULL,NULL,NULL,NULL,'AD','05','Ordino'),('V1040','Test','Domestic','9801722265','ritwiksinghkkc@gmail.com',0,'1242424','Computers & Peripherals','542-d, Regent Shipra Suncity','8986235162',NULL,'DWPPR8019J','29ABCDE1234F1Z5','HDFC Bank','12345678901','HDFC0001234','CHANGE_REQUEST','oik',61,'61','2025-11-23 06:48:30','2025-11-23 16:09:23',1040,NULL,NULL,NULL,NULL,NULL,NULL,'AO','CUS','Quibala'),('V1041','Test','Domestic','9801722265','ritwiksinghkkc@gmail.com',0,'12345678','Computers & Peripherals','Jakkanpur d.v.c gate p.o g.p.o Right side gali house no. 2 Bandana Niwas','8986235162',NULL,'DWPPR8019J','29ABCDE1234F1Z5','HDFC Bank','12345678901','HDFC0001234','CHANGE_REQUEST','OK',61,'61','2025-11-23 07:00:01','2025-11-23 16:09:23',1041,NULL,NULL,NULL,NULL,NULL,NULL,'AD','07','Andorra la Vella'),('V1042','Ritwik','Domestic','9801722265','ritwiksinghkkc@gmail.com',0,'12345678','Computers & Peripherals','Jakkanpur d.v.c gate p.o g.p.o Right side gali house no. 2 Bandana Niwas','8986235162',NULL,'DWPPR8019J','29ABCDE1234F1Z5','HDFC Bank','12345678901','HDFC0001234','CHANGE_REQUEST','OK',61,'61','2025-11-23 07:03:05','2025-11-23 16:09:23',1042,NULL,NULL,NULL,NULL,NULL,NULL,'IN','BR','Patna');
/*!40000 ALTER TABLE `vendor_master_util` ENABLE KEYS */;
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
