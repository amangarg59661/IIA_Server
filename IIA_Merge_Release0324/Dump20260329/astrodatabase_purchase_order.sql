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
-- Table structure for table `purchase_order`
--

DROP TABLE IF EXISTS `purchase_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `purchase_order` (
  `po_id` varchar(255) NOT NULL,
  `tender_id` varchar(255) DEFAULT NULL,
  `indent_id` varchar(255) DEFAULT NULL,
  `warranty` varchar(50) DEFAULT NULL,
  `consignes_address` varchar(255) DEFAULT NULL,
  `billing_address` varchar(255) DEFAULT NULL,
  `delivery_period` decimal(10,2) DEFAULT NULL,
  `if_ld_clause_applicable` tinyint(1) DEFAULT NULL,
  `inco_terms` varchar(255) DEFAULT NULL,
  `payment_terms` varchar(255) DEFAULT NULL,
  `vendor_name` varchar(255) DEFAULT NULL,
  `vendor_address` varchar(255) DEFAULT NULL,
  `applicable_pbg_to_be_submitted` varchar(255) DEFAULT NULL,
  `transporter_and_freight_for_warder_details` varchar(255) DEFAULT NULL,
  `vendor_account_number` varchar(255) DEFAULT NULL,
  `vendors_zfsc_code` varchar(255) DEFAULT NULL,
  `vendor_account_name` varchar(255) DEFAULT NULL,
  `created_by` int DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `total_value_of_po` decimal(10,0) DEFAULT NULL,
  `project_name` varchar(200) DEFAULT NULL,
  `vendor_id` varchar(255) DEFAULT NULL,
  `delivery_date` date DEFAULT NULL,
  `comparative_statement_file_name` varchar(300) DEFAULT NULL,
  `gem_contract_file_name` varchar(500) DEFAULT NULL,
  `type_of_security` varchar(255) DEFAULT NULL,
  `security_number` varchar(255) DEFAULT NULL,
  `security_date` date DEFAULT NULL,
  `expiry_date` date DEFAULT NULL,
  `quotation_number` varchar(255) DEFAULT NULL,
  `quotation_date` date DEFAULT NULL,
  `additional_terms_and_conditions` varchar(500) DEFAULT NULL,
  `buy_back_amount` decimal(18,2) DEFAULT NULL,
  `current_status` varchar(255) DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `is_cancelled` bit(1) DEFAULT NULL,
  `is_locked` bit(1) DEFAULT NULL,
  `locked_by` int DEFAULT NULL,
  `locked_date` datetime DEFAULT NULL,
  `po_version` int DEFAULT NULL,
  PRIMARY KEY (`po_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `purchase_order`
--

LOCK TABLES `purchase_order` WRITE;
/*!40000 ALTER TABLE `purchase_order` DISABLE KEYS */;
INSERT INTO `purchase_order` VALUES ('PO1001','T1001',NULL,NULL,'Bangalore','Koramangala, Bangalore - 560034',NULL,NULL,'dkjd','djk','Turmeric Cloud Technologies Pvt Ltd','Kathriguppe, BSK III Stage, Bangalore',NULL,NULL,'64008876799','SBIN0040014','Turmeric Cloud Technologies Pvt Ltd',36,NULL,'2025-06-24 10:42:16','2025-06-24 10:42:16',4560,NULL,'9020006','2025-06-27',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),('PO1002','T1002',NULL,NULL,'Bangalore','Koramangala, Bangalore - 560034',NULL,NULL,'dkjd','djk','Turmeric Cloud Technologies Pvt Ltd','Kathriguppe, BSK III Stage, Bangalore',NULL,NULL,'64008876799','SBIN0040014','Turmeric Cloud Technologies Pvt Ltd',36,NULL,'2025-06-25 11:27:01','2025-06-25 11:27:01',12000,NULL,'9020006','2025-06-25',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),('PO1003','T1003',NULL,NULL,'Bangalore','Koramangala, Bangalore - 560034',NULL,NULL,'dkjd','djk','kapil enterprise','HYD',NULL,NULL,'1234','2345','kapil enterprise',36,NULL,'2025-06-27 07:22:01','2025-06-27 07:22:01',2000,NULL,'V1001','2025-06-27',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),('PO1004','T1004',NULL,NULL,'Bangalore','Koramangala, Bangalore - 560034',NULL,NULL,'dkjd','djk','ABCD','HYD',NULL,NULL,'1234','2345','ABCD',36,NULL,'2025-06-27 10:08:29','2025-06-27 10:08:29',2000,NULL,'V1004','2025-06-28',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),('PO1005','T1005',NULL,NULL,'Bangalore','Koramangala, Bangalore - 560034',NULL,NULL,'dkjd','djk','kapil dev2 enterprise','HYD',NULL,NULL,'1234','2345','kapil dev2 enterprise',36,NULL,'2025-06-27 10:42:11','2025-06-27 10:42:11',2000,NULL,'V1006','2025-06-27',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),('PO1011','T1011',NULL,'1.00','Bangalore','Koramangala, Bangalore - 560034',NULL,1,'dkjd','100% payment within 30 days from the date of acceptance.','CITO INFOTECH PVT LTD','11/1, 3rd Cross, Nandidurga Road, Bangalore','3%','NA','343805000667','ICICI0003438','CITO INFOTECH PVT LTD',36,NULL,'2025-07-03 09:53:53','2025-07-03 09:53:53',100000,'ADA OPT PROJ','9060087','2025-07-31',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),('PO1013','T1013',NULL,NULL,'Bangalore','Koramangala, Bangalore - 560034',NULL,NULL,'dkjd','100% payment within 30 days from the date of acceptance.','kapil enterprise','HYD','NA',NULL,'1234','2345','kapil enterprise',36,NULL,'2025-07-07 06:44:43','2025-07-07 06:44:43',12285000,NULL,'V1001','2025-07-07','1751870682820_32fb683444f343ad93b34dc28c6eaf53.png',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),('PO1014','T1014',NULL,NULL,'Bangalore','Koramangala, Bangalore - 560034',NULL,NULL,'m','100% payment within 30 days from the date of acceptance.','kapil dev enterprise','hsj',NULL,NULL,'1234','1234','kapil dev enterprise',36,NULL,'2025-07-04 09:55:02','2025-07-04 09:55:02',6318000,NULL,'V1012','2025-07-17',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),('PO1027','T1027',NULL,NULL,'Bangalore','Koramangala, Bangalore - 560034',NULL,NULL,'j','100% payment within 30 days from the date of acceptance.','kapil enterprise','HYD',NULL,NULL,'1234','2345','kapil enterprise',36,NULL,'2025-08-04 05:59:46','2025-08-04 05:59:46',1060000,NULL,'V1001','2025-08-04',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),('PO1029','T1029',NULL,NULL,'Bangalore','Koramangala, Bangalore - 560034',NULL,NULL,'DAP','100% payment within 30 days from the date of acceptance.','kapil dev2 enterprise','HYD',NULL,NULL,'1234','2345','kapil dev2 enterprise',36,NULL,'2025-08-05 06:10:33','2025-08-05 06:10:33',111003750,NULL,'V1006','2025-08-05',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),('PO1033','T1033',NULL,NULL,'Bangalore','Koramangala, Bangalore - 560034',NULL,NULL,'d','100% payment within 30 days from the date of acceptance.','kapil dev2 enterprise','HYD',NULL,NULL,'1234','2345','kapil dev2 enterprise',36,NULL,'2025-08-08 05:44:25','2025-08-08 05:44:25',1212000,NULL,'V1006','2025-08-08',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),('PO1036','T1036',NULL,'5 Years','Bangalore','Koramangala, Bangalore - 560034',4.00,NULL,'m','100% payment within 30 days from the date of acceptance.','kapil enterprise','HYD','4',NULL,'1234','2345','kapil enterprise',18,NULL,'2025-10-21 01:20:25','2025-10-21 01:20:25',100912500,NULL,'V1001','2025-10-23',NULL,NULL,NULL,NULL,NULL,NULL,'ASJK','2025-10-20',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),('PO1037','T1037',NULL,'2 Years','Bangalore','Koramangala, Bangalore - 560034',1.00,NULL,'DAP','100% payment within 30 days from the date of acceptance.','Test Vendor 1','Koramangala','5',NULL,'1234567890','POIUYTR1234C','Test Vendor 1',36,NULL,'2025-09-29 10:19:10','2025-09-29 10:19:10',23034375,NULL,'V1011','2025-09-30',NULL,NULL,NULL,NULL,NULL,NULL,'Aqw13','2025-09-30',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),('PO1038','T1038',NULL,'3 Years','Bangalore','Koramangala, Bangalore - 560034',4.00,NULL,'DAP','100% payment within 30 days from the date of acceptance.','','nka','3',NULL,'nkla','nakn','aklj',36,NULL,'2025-09-13 04:46:18','2025-09-13 04:46:18',18427500,NULL,'GEM1002','2025-09-19',NULL,NULL,NULL,NULL,NULL,NULL,'Aqw13','2025-09-19',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),('PO1039','T1039',NULL,NULL,'Bangalore','Koramangala, Bangalore - 560034',1.00,NULL,'DAP','100% payment within 30 days from the date of acceptance.','Abc','hyd',NULL,NULL,'1234','2345','Abc',36,NULL,'2025-09-17 10:00:24','2025-09-17 10:00:24',50000,NULL,'V1003','2025-09-18',NULL,NULL,NULL,NULL,NULL,NULL,'Aqw13','2025-09-19',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),('PO1040','T1040',NULL,NULL,'Bangalore','Koramangala, Bangalore - 560034',2.00,NULL,'DAP','100% payment within 30 days from the date of acceptance.','kapil dev enterprise','HYD',NULL,NULL,'1234','2345','kapil dev enterprise',36,NULL,'2025-09-17 10:01:10','2025-09-17 10:01:10',10000,NULL,'V1005','2025-09-25',NULL,NULL,NULL,NULL,NULL,NULL,'Aqw13','2025-09-24',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),('PO1041','T1041',NULL,'1 Year','Bangalore','Koramangala, Bangalore - 560034',2.00,1,'DAP','100% payment within 30 days from the date of acceptance.','Test Vendor 1','Koramangala','NA',NULL,'1234567890','POIUYTR1234C','Test Vendor 1',29,NULL,'2025-09-24 10:12:44','2025-09-24 10:12:44',53760,NULL,'V1011','2025-10-09',NULL,NULL,NULL,NULL,NULL,NULL,'123Z25-26','2025-09-24',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),('PO1042','T1042',NULL,'1 Year','Bangalore','Koramangala, Bangalore - 560034',NULL,1,'DAP','100% payment within 30 days from the date of acceptance.','Test 3','NY City','3',NULL,'2345678901','1234','Test 3',36,NULL,'2025-09-29 06:56:00','2025-09-29 06:56:00',11200,NULL,'V1017',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'1321',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),('PO1043','T1043',NULL,'3 Years','Bangalore','Koramangala, Bangalore - 560034',2.00,NULL,'EXWORKS','Quarterly in advance on submission of invoice (in case of AMCs)','kapil dev enterprise','hsj','2',NULL,'1234','1234','kapil dev enterprise',18,NULL,'2025-10-21 01:27:16','2025-10-21 01:27:16',4387500,NULL,'V1012','2025-10-24',NULL,NULL,NULL,NULL,NULL,NULL,'HJ125','2025-10-23',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),('PO1044','T1044',NULL,NULL,'Bangalore','Koramangala, Bangalore - 560034',2.00,NULL,'DAP','100% payment within 30 days from the date of acceptance.','kapil dev enterprise','hsj','1',NULL,'1234','1234','kapil dev enterprise',18,NULL,'2025-10-21 02:21:07','2025-10-21 02:21:07',4387500,NULL,'V1012','2025-10-23','1761013267192_8f38febe8cea4394ad3d5279cf9e1db0.pdf',NULL,NULL,NULL,NULL,NULL,'AQW1','2025-10-23',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),('PO1050','T1050',NULL,NULL,'Bangalore','Koramangala, Bangalore - 560034',1.00,NULL,'DAP','100% payment within 30 days from the date of acceptance.','Turmeric Cloud Technologies Pvt Ltd','HYD',NULL,NULL,'1234','2345','Turmeric Cloud Technologies Pvt Ltd',36,NULL,'2025-11-03 05:59:04','2025-11-03 05:59:04',10000,NULL,'V1002','2025-11-12',NULL,NULL,NULL,NULL,NULL,NULL,'Aqw13','2025-11-12',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),('PO1051','T1051',NULL,NULL,'Bangalore','Koramangala, Bangalore - 560034',5.00,NULL,'DAP','100% payment within 30 days from the date of acceptance.','Turmeric Cloud Technologies Pvt Ltd','HYD',NULL,NULL,'1234','2345','Turmeric Cloud Technologies Pvt Ltd',36,NULL,'2025-11-03 05:59:38','2025-11-03 05:59:38',100000,NULL,'V1002','2025-11-20',NULL,NULL,NULL,NULL,NULL,NULL,'ASJK','2025-11-12',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),('PO1052','T1052',NULL,NULL,'Bangalore','Koramangala, Bangalore - 560034',5.00,NULL,'DAP','100% payment within 30 days from the date of acceptance.','Turmeric Cloud Technologies Pvt Ltd','HYD',NULL,NULL,'1234','2345','Turmeric Cloud Technologies Pvt Ltd',36,NULL,'2025-11-03 06:00:11','2025-11-03 06:00:11',10000,NULL,'V1002','2025-11-27',NULL,NULL,NULL,NULL,NULL,NULL,'Aqw13','2025-11-13',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),('PO1053','T1053',NULL,NULL,'Bangalore','Koramangala, Bangalore - 560034',1.00,NULL,'DAP','100% payment within 30 days from the date of acceptance.','Turmeric Cloud Technologies Pvt Ltd','HYD',NULL,NULL,'1234','2345','Turmeric Cloud Technologies Pvt Ltd',36,NULL,'2025-11-03 06:00:51','2025-11-03 06:00:51',110000,NULL,'V1002','2025-11-13',NULL,NULL,NULL,NULL,NULL,NULL,'Aqw13','2025-11-11',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),('PO1056','T1056',NULL,'3 Years','Bangalore','Koramangala, Bangalore - 560034',1.00,NULL,'DAP','100% payment within 30 days from the date of acceptance.','Test 2','Koramangala 2nd Block','2',NULL,'123567087901','SBIN0123','Test 2',36,NULL,'2025-11-06 09:37:00','2025-11-06 09:37:00',4567500,NULL,'V1016','2025-11-13',NULL,NULL,NULL,NULL,NULL,NULL,'SD123','2025-11-12',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `purchase_order` ENABLE KEYS */;
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
