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
-- Table structure for table `purchase_order_attributes`
--

DROP TABLE IF EXISTS `purchase_order_attributes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `purchase_order_attributes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `material_code` varchar(255) DEFAULT NULL,
  `po_id` varchar(255) NOT NULL,
  `material_description` text,
  `quantity` decimal(10,2) DEFAULT NULL,
  `rate` decimal(10,2) DEFAULT NULL,
  `currency` varchar(50) DEFAULT NULL,
  `exchange_rate` decimal(10,2) DEFAULT NULL,
  `gst` decimal(10,2) DEFAULT NULL,
  `duties` decimal(10,2) DEFAULT NULL,
  `freight_charge` decimal(10,2) DEFAULT NULL,
  `budget_code` varchar(255) DEFAULT NULL,
  `received_quantity` decimal(19,2) DEFAULT NULL,
  `total_po_material_price_in_inr` decimal(15,2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `po_id` (`po_id`),
  CONSTRAINT `purchase_order_attributes_ibfk_1` FOREIGN KEY (`po_id`) REFERENCES `purchase_order` (`po_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `purchase_order_attributes`
--

LOCK TABLES `purchase_order_attributes` WRITE;
/*!40000 ALTER TABLE `purchase_order_attributes` DISABLE KEYS */;
INSERT INTO `purchase_order_attributes` VALUES (1,'M1001','PO1001','3D Printer',10.00,456.00,'INR',0.00,10.00,10.00,0.00,'',NULL,NULL),(2,'M1100','PO1002','laptop',10.00,1200.00,'USD',0.00,10.00,10.00,0.00,'',10.00,NULL),(3,'M1102','PO1003','Stationary',10.00,200.00,'USD',0.00,10.00,10.00,0.00,'',10.00,NULL),(4,'M1102','PO1004','Stationary',10.00,200.00,'USD',0.00,10.00,10.00,0.00,'',3.00,NULL),(5,'M1102','PO1005','Stationary',10.00,200.00,'USD',0.00,10.00,10.00,0.00,'',10.00,NULL),(8,'M1103','PO1011','Hp laptop',2.00,45000.00,'USD',85.76,18.00,28.00,0.00,'',2.00,NULL),(9,'M1103','PO1014','Hp laptop',1.00,50000.00,'USD',87.75,18.00,26.00,0.00,'',1.00,6318000.00),(10,'M1103','PO1013','Hp laptop',2.00,50000.00,'USD',87.75,12.00,28.00,0.00,'',2.00,12285000.00),(11,'M1104','PO1027','Desktop',2.00,50000.00,'USD',10.00,5.00,1.00,0.00,'',NULL,1060000.00),(12,'M1104','PO1029','Desktop',22.00,50000.00,'USD',87.75,5.00,10.00,0.00,'',5.00,111003750.00),(13,'M1104','PO1033','Desktop',2.00,50000.00,'USD',12.00,0.00,1.00,0.00,'',2.00,1212000.00),(14,'M1104','PO1038','Desktop',4.00,50000.00,'USD',87.75,5.00,0.00,0.00,'',NULL,18427500.00),(15,'M1110','PO1039','laptop',50.00,1000.00,'INR',0.00,0.00,0.00,0.00,'',50.00,50000.00),(16,'M1111','PO1040','Pen',100.00,100.00,'INR',0.00,0.00,0.00,0.00,'',69.00,10000.00),(18,'M1112','PO1041','External 1TB Hard Disk',4.00,12000.00,'INR',0.00,12.00,0.00,0.00,'',4.00,53760.00),(19,'M1111','PO1042','Pen',100.00,100.00,'INR',0.00,12.00,0.00,0.00,'',80.00,11200.00),(20,'M1104','PO1037','Desktop',5.00,50000.00,'USD',87.75,5.00,0.00,0.00,'',2.00,23034375.00),(21,'M1104','PO1036','Desktop',23.00,50000.00,'USD',87.75,0.00,0.00,0.00,'',NULL,100912500.00),(22,'M1104','PO1043','Desktop',1.00,50000.00,'USD',87.75,0.00,0.00,0.00,'',1.00,4387500.00),(23,'M1104','PO1044','Desktop',1.00,50000.00,'USD',87.75,0.00,0.00,0.00,'',NULL,4387500.00),(24,'M1124','PO1050','Bag',10.00,1000.00,'INR',0.00,0.00,0.00,0.00,'',NULL,10000.00),(25,'M1127','PO1051','mouse hp',10.00,10000.00,'INR',0.00,0.00,0.00,0.00,'',7.00,100000.00),(26,'M1128','PO1052','hp cpu',10.00,1000.00,'INR',0.00,0.00,0.00,0.00,'',NULL,10000.00),(27,'M1127','PO1053','mouse hp',10.00,10000.00,'INR',0.00,0.00,0.00,0.00,'',5.00,100000.00),(28,'M1128','PO1053','hp cpu',10.00,1000.00,'INR',0.00,0.00,0.00,0.00,'',5.00,10000.00),(29,'M1104','PO1056','Desktop',1.00,50000.00,'USD',87.00,5.00,0.00,0.00,'',NULL,4567500.00);
/*!40000 ALTER TABLE `purchase_order_attributes` ENABLE KEYS */;
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
