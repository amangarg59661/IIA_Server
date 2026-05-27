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
-- Table structure for table `contigency_purchase`
--

DROP TABLE IF EXISTS `contigency_purchase`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contigency_purchase` (
  `contigency_id` varchar(255) NOT NULL,
  `vendors_name` varchar(255) DEFAULT NULL,
  `vendors_invoice_no` varchar(255) DEFAULT NULL,
  `Date` date DEFAULT NULL,
  `remarks_for_purchase` varchar(255) DEFAULT NULL,
  `upload_copy_of_invoice` blob,
  `predifined_purchase_statement` varchar(255) DEFAULT NULL,
  `project_detail` varchar(255) DEFAULT NULL,
  `created_by` int DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `upload_copy_of_invoice_file_name` varchar(255) DEFAULT NULL,
  `project_name` varchar(200) DEFAULT NULL,
  `file_type` varchar(255) DEFAULT NULL,
  `cp_number` int DEFAULT NULL,
  `payment_to` varchar(200) DEFAULT NULL,
  `payment_to_vendor` varchar(200) DEFAULT NULL,
  `payment_to_employee` varchar(200) DEFAULT NULL,
  `purpose` varchar(255) DEFAULT NULL,
  `declaration_one` tinyint(1) DEFAULT NULL,
  `declaration_two` tinyint(1) DEFAULT NULL,
  `total_cp_value` decimal(15,2) DEFAULT NULL,
  PRIMARY KEY (`contigency_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contigency_purchase`
--

LOCK TABLES `contigency_purchase` WRITE;
/*!40000 ALTER TABLE `contigency_purchase` DISABLE KEYS */;
INSERT INTO `contigency_purchase` VALUES ('CP1001','MALEX OFFICE SOLUTIONS',NULL,'2025-06-30',NULL,NULL,NULL,NULL,31,NULL,'2025-06-30 13:10:20','2025-06-30 13:10:20',NULL,'ADA OPT PROJ','CP',1001,'vendor','Turmeric Cloud Technologies Pvt Ltd',NULL,'marketing',1,1,NULL),('CP1002','Turmeric Cloud Technologies Pvt Ltd','12345','2025-07-04',NULL,NULL,NULL,NULL,18,NULL,'2025-07-04 15:28:47','2025-07-04 15:28:47',NULL,NULL,'CP',1002,'vendor','Turmeric Cloud Technologies Pvt Ltd',NULL,'Office use',1,1,NULL),('CP1003','MALEX OFFICE SOLUTIONS','12345','2025-07-04',NULL,NULL,NULL,NULL,18,NULL,'2025-07-04 15:54:28','2025-07-04 15:54:28',NULL,NULL,'CP',1003,'employee',NULL,'1501','Office use',1,1,NULL),('CP1004','kapil enterprise',NULL,'2025-07-08',NULL,NULL,NULL,NULL,31,NULL,'2025-07-07 12:18:42','2025-07-07 12:18:42',NULL,'ADITYA.ISRO','CP',1004,'vendor','kapil enterprise',NULL,'nkl',1,1,2240.00),('CP1005','Test Vendor 1','12345',NULL,NULL,NULL,NULL,NULL,27,NULL,'2025-07-08 16:24:06','2025-07-08 16:24:06',NULL,NULL,'CP',1005,'vendor','Test Vendor 1',NULL,'Office use',1,1,7536.00);
/*!40000 ALTER TABLE `contigency_purchase` ENABLE KEYS */;
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
