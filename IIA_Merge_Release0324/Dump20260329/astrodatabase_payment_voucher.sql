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
-- Table structure for table `payment_voucher`
--

DROP TABLE IF EXISTS `payment_voucher`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment_voucher` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `payment_voucher_number` varchar(255) DEFAULT NULL,
  `payment_voucher_date` varchar(50) DEFAULT NULL,
  `payment_voucher_is_for` varchar(100) DEFAULT NULL,
  `purchase_order_id` varchar(100) DEFAULT NULL,
  `grn_number` varchar(100) DEFAULT NULL,
  `service_order_details` varchar(500) DEFAULT NULL,
  `payment_voucher_type` varchar(100) NOT NULL,
  `vendor_name` varchar(255) DEFAULT NULL,
  `vendor_invoice_number` varchar(255) NOT NULL,
  `vendor_invoice_date` varchar(50) DEFAULT NULL,
  `currency` varchar(50) NOT NULL,
  `exchange_rate` varchar(50) DEFAULT NULL,
  `status` varchar(100) DEFAULT NULL,
  `remarks` varchar(500) DEFAULT NULL,
  `total_amount` decimal(15,2) DEFAULT NULL,
  `partial_amount` decimal(15,2) DEFAULT NULL,
  `advance_amount` decimal(15,2) DEFAULT NULL,
  `paid_amount` decimal(15,2) DEFAULT NULL,
  `so_id` varchar(50) DEFAULT NULL,
  `created_by` int DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `payment_voucher_net_amount` decimal(19,2) DEFAULT NULL,
  `tds_amount` decimal(19,2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment_voucher`
--

LOCK TABLES `payment_voucher` WRITE;
/*!40000 ALTER TABLE `payment_voucher` DISABLE KEYS */;
INSERT INTO `payment_voucher` VALUES (1,'INV/1','09/10/2025','Purchase Order','1041','INV1041/22',NULL,'Partial','V1011','0123','25/09/2025','INR','0',NULL,NULL,16800.00,1000.00,NULL,1000.00,NULL,48,'2025-10-03 11:55:54',NULL,NULL),(2,'INV/1','15/10/2025','Purchase Order','1041','INV1041/22',NULL,'Partial','V1011','0123','25/09/2025','INR','0',NULL,NULL,16800.00,3000.00,NULL,4000.00,NULL,48,'2025-10-03 11:56:45',NULL,NULL),(3,'INV/1','22/10/2025','Purchase Order','1041','INV1041/22',NULL,'Partial','V1011','0123','25/09/2025','INR','0',NULL,NULL,16800.00,2000.00,NULL,6000.00,NULL,18,'2025-10-03 11:58:56',NULL,NULL);
/*!40000 ALTER TABLE `payment_voucher` ENABLE KEYS */;
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
