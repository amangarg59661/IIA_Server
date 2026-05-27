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
-- Table structure for table `material_master`
--

DROP TABLE IF EXISTS `material_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `material_master` (
  `material_code` varchar(50) NOT NULL,
  `category` varchar(100) DEFAULT NULL,
  `sub_category` varchar(100) DEFAULT NULL,
  `description` text,
  `uom` varchar(50) DEFAULT NULL,
  `upload_image` longblob,
  `indigenous_or_imported` tinyint(1) DEFAULT NULL,
  `updated_by` varchar(200) DEFAULT NULL,
  `created_by` varchar(200) DEFAULT NULL,
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `upload_image_name` varchar(255) DEFAULT NULL,
  `estimated_price_with_ccy` decimal(19,2) DEFAULT NULL,
  `unit_price` decimal(19,2) DEFAULT NULL,
  `currency` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `remarks` text,
  `brief_description` text,
  `reason_for_deactive` varchar(255) DEFAULT NULL,
  `status_of_material_active_or_deactive` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`material_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `material_master`
--

LOCK TABLES `material_master` WRITE;
/*!40000 ALTER TABLE `material_master` DISABLE KEYS */;
INSERT INTO `material_master` VALUES ('M1001','Electronics','Electrical','3D Printer','ft',NULL,1,'18','29','2025-04-11 16:28:39','2025-04-11 16:28:39',NULL,NULL,456.00,'INR','APPROVED',NULL,NULL,NULL,NULL),('M1100','Capital','Electrical','laptop','Numbers',NULL,1,'18','29','2025-06-25 16:51:10','2025-06-25 16:51:10','',NULL,1200.00,'USD','APPROVED',NULL,'jkljsa',NULL,NULL),('M1101','Capital','Chemicals','Desktop','Numbers',NULL,1,'18','29','2025-06-27 06:34:06','2025-06-27 06:34:06','',NULL,20000.00,'INR','APPROVED',NULL,'Hp Desktop',NULL,NULL),('M1102','Capital','Chemicals','Stationary','Numbers',NULL,1,'18','29','2025-06-27 06:34:08','2025-06-27 06:34:08','',NULL,200.00,'USD','APPROVED',NULL,'A4 papers',NULL,NULL),('M1103','Capital','Computer & Peripherals','Hp laptop','Numbers',NULL,1,'18','29','2025-06-30 12:26:00','2025-06-30 12:26:00','',NULL,50000.00,'USD','APPROVED',NULL,'i5 Intel',NULL,NULL),('M1104','Capital','Chemicals','Desktop','Numbers',NULL,1,'18','29','2025-06-30 12:26:01','2025-06-30 12:26:01','',NULL,50000.00,'USD','APPROVED',NULL,'I5 Intel',NULL,NULL),('M1105','Capital','Computer & Peripherals','Desktop','Numbers',NULL,1,'18','29','2025-06-30 14:53:16','2025-06-30 14:53:16','1751275254518_Custom Bid Undertaking.pdf',NULL,130000.00,'INR','APPROVED',NULL,'Desktop with i7 Processor',NULL,NULL),('M1106','Capital','Equipment','Telescope','Numbers',NULL,1,'18','29','2025-07-04 15:42:16','2025-07-04 15:42:16','',NULL,100000.00,'INR','APPROVED',NULL,'Telescope',NULL,NULL),('M1107','Capital','Furniture','Conference Table','Numbers',NULL,1,'29','29','2025-07-29 16:12:41','2025-07-29 16:12:41','',NULL,250000.00,'INR','APPROVED',NULL,'Wooden Conference Table 8 seater with chairs',NULL,NULL),('M1108','Capital','Equipment','Equipment','Numbers',NULL,1,'18','29','2025-08-11 10:43:14','2025-08-11 10:43:14','',NULL,1200000.00,'INR','APPROVED',NULL,'Brief Equipment',NULL,NULL),('M1109','Capital','Equipment','Equipment Test 1','UNIT',NULL,1,'18','29','2025-09-22 11:11:33','2025-09-22 11:11:33','',NULL,1100000.00,'INR','APPROVED',NULL,'Brief Equipment Test 1',NULL,NULL),('M1110','Consumable','Computer & Peripherals','laptop','Numbers',NULL,1,'18','29','2025-09-17 15:24:32','2025-09-17 15:24:32','',NULL,1000.00,'INR','APPROVED',NULL,'Hp Laptop',NULL,NULL),('M1111','Consumable','Computer & Peripherals','Pen','Numbers',NULL,1,'18','29','2025-09-17 15:24:35','2025-09-17 15:24:35','',NULL,100.00,'INR','APPROVED',NULL,'Pens',NULL,NULL),('M1112','Consumable','Computer & Peripherals','External 1TB Hard Disk','Numbers',NULL,1,'18','29','2025-09-22 11:11:33','2025-09-22 11:11:33','',NULL,15000.00,'INR','APPROVED',NULL,'External 1TB Hard Disk',NULL,NULL),('M1113','Consumable','Software','msoffice','Numbers',NULL,1,'18','29','2025-09-27 20:18:39','2025-09-27 20:18:39','',NULL,310000.00,'INR','APPROVED',NULL,'Microsoft office for staff',NULL,NULL),('M1115','Capital','Electronic Items','2 Ton Air Conditioner','UNIT',NULL,1,'18','29','2025-10-16 16:22:37','2025-10-16 16:22:37','1760611902418_023ddaffbdbc44249b63ed565b6c02ea.pdf',NULL,100000.00,'INR','APPROVED',NULL,'2 Ton Air Conditioner',NULL,NULL),('M1116','Capital','Electronic Items','2 Ton Air Conditioner','UNIT',NULL,1,'18','29','2025-10-16 16:39:26','2025-10-16 16:39:26','1760612937394_8848b15da6784be2939e7bc7b3553fa4.pdf',NULL,100000.00,'INR','APPROVED',NULL,'2 Ton Air Conditioner',NULL,NULL),('M1117','Consumable','Stationary','Register','Each',NULL,1,'18','29','2025-10-24 13:56:38','2025-10-24 13:56:38',NULL,NULL,50.00,'INR','APPROVED',NULL,'Bill Register',NULL,NULL),('M1118','Consumable','Chemicals','moniterr','Numbers',NULL,1,'18','29','2025-10-24 13:54:38','2025-10-24 13:54:38',NULL,NULL,10000.00,'INR','APPROVED',NULL,'test_moniter',NULL,NULL),('M1119','Consumable','Electrical','laptop','Numbers',NULL,1,'18','29','2025-10-24 13:54:38','2025-10-24 13:54:38',NULL,NULL,10000.00,'INR','APPROVED',NULL,'test_moniteerr',NULL,NULL),('M1122','Capital','Computer & Peripherals','Hp Desktop','Numbers',NULL,1,'18','29','2025-10-28 13:10:08','2025-10-28 13:10:08','1761637107554_7068a117f5824fd0bf3faa7df3cd67a8.pdf',NULL,120000.00,'INR','APPROVED',NULL,'Intel 512 ssd',NULL,NULL),('M1123','Consumable','Stationary','Bag','Numbers',NULL,1,'18','29','2025-10-28 15:35:59','2025-10-28 15:35:59',NULL,NULL,1000.00,'INR','APPROVED',NULL,'Duffle bag',NULL,NULL),('M1124','Consumable','Stationary','Bag','Numbers',NULL,1,'18','29','2025-11-03 11:17:48','2025-11-03 11:17:48',NULL,NULL,1000.00,'INR','APPROVED',NULL,'Duffle bag',NULL,NULL),('M1125','Consumable','Stationary','pencil','Numbers',NULL,1,'18','29','2025-10-28 16:10:34','2025-10-28 16:10:34','1761647989512_8bd0f8a4793145ad91dd3f5e19061e0d.pdf',NULL,10.00,'INR','APPROVED',NULL,'pencils',NULL,NULL),('M1126','Capital','Computer & Peripherals','Mouse dell','Each',NULL,1,'18','29','2025-11-03 11:11:41','2025-11-03 11:11:41',NULL,NULL,500.00,'INR','APPROVED',NULL,'Mouse dell 2025 ',NULL,NULL),('M1127','Capital','Computer & Peripherals','mouse hp','Numbers',NULL,1,'18','29','2025-11-03 11:17:32','2025-11-03 11:17:32',NULL,NULL,10000.00,'INR','APPROVED',NULL,'hp',NULL,NULL),('M1128','Capital','Computer & Peripherals','hp cpu','Numbers',NULL,1,'18','29','2025-11-03 11:17:35','2025-11-03 11:17:35',NULL,NULL,1000.00,'INR','APPROVED',NULL,'ndj',NULL,NULL),('M1129','Capital','Computer & Peripherals','Asus Laptop','Numbers',NULL,1,'18','29','2025-11-03 19:39:52','2025-11-03 19:39:52',NULL,NULL,10000.00,'INR','APPROVED',NULL,'Gaming laptops',NULL,NULL),('M1130','Capital','Computer & Peripherals','Hi Description','Bottle',NULL,1,'51','61','2025-11-26 08:55:51','2025-11-26 08:55:51',NULL,NULL,456.00,'INR','APPROVED',NULL,'Hi Material',NULL,'Active'),('M1132','RAW_MATERIAL','CEMENT','Hi Description','LITRE',NULL,1,'96','96','2026-01-27 06:59:08','2026-01-27 06:59:08',NULL,NULL,343.00,'INR','APPROVED',NULL,'dsfdsf',NULL,'Active'),('M1134','CONSUMABLES','COMPUTER','Ritwik Material','KG',NULL,1,'96','96','2026-01-28 08:48:16','2026-01-28 08:48:16',NULL,NULL,9801.00,'INR','APPROVED',NULL,'Ritwik',NULL,'Active'),('M1135','CONSUMABLES','COMPUTER','DS','KG',NULL,0,'96','96','2026-01-28 08:50:19','2026-01-28 08:50:19',NULL,NULL,53454.00,'USD','APPROVED',NULL,'SDFDSFDS',NULL,'Active'),('M1136','RAW_MATERIAL','STEEL','gfhg','KG',NULL,1,'96','96','2026-01-28 08:52:13','2026-01-28 08:52:13',NULL,NULL,5656.00,'EUR','APPROVED',NULL,'rtytrytrytr',NULL,'Active'),('M1137','CONSUMABLES','ELECTRICAL','EDSFDSFDS','PIECE',NULL,1,'96','96','2026-01-28 09:05:16','2026-01-28 09:05:16',NULL,NULL,45435454.00,'INR','APPROVED',NULL,'FSDF',NULL,'Active'),('M1142','CONSUMABLES','CEMENT','Hi Description','KG',NULL,1,'96','96','2026-01-28 20:37:19','2026-01-28 20:37:19',NULL,NULL,3423432.00,'INR','APPROVED',NULL,'asdasdsa',NULL,'Active'),('M1144','RAW_MATERIAL','ELECTRICAL','Hi Description','PIECE',NULL,1,'96','96','2026-01-28 20:48:45','2026-01-28 20:48:45',NULL,NULL,65.00,'USD','APPROVED',NULL,'rteretretre',NULL,'Active'),('M1145','CONSUMABLES','COMPUTER','Hi Description','PIECE',NULL,1,'96','96','2026-01-28 20:50:49','2026-01-28 20:50:49',NULL,NULL,4545.00,'INR','APPROVED',NULL,'fdsfdsf',NULL,'Active'),('M1146','CONSUMABLES','COMPUTER','Hi Description','PIECE',NULL,1,'96','96','2026-01-28 20:57:13','2026-01-28 20:57:13',NULL,NULL,3443.00,'INR','APPROVED',NULL,'wewqewqe',NULL,'Active'),('M1147','RAW_MATERIAL','COMPUTER','Gaming Mouse','PIECE',NULL,1,'96','96','2026-02-20 11:13:34','2026-02-20 11:13:34',NULL,NULL,43334.00,'INR','APPROVED',NULL,'rerewrewrew',NULL,'Active'),('M1148','FINISHED_GOODS','COMPUTER','Printer Fill','PIECE',NULL,1,'112','116','2026-02-26 07:26:36','2026-02-26 07:26:36',NULL,NULL,1200.00,'INR','APPROVED',NULL,'Printer Fill',NULL,'Active'),('M1149','FINISHED_GOODS','ELECTRICAL','Lamp','PIECE',NULL,1,'112','116','2026-02-26 08:15:45','2026-02-26 08:15:45',NULL,NULL,2000.00,'INR','APPROVED',NULL,'Lamp',NULL,'Active');
/*!40000 ALTER TABLE `material_master` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-29 21:41:28
