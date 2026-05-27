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
-- Table structure for table `gprn_material_detail`
--

DROP TABLE IF EXISTS `gprn_material_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gprn_material_detail` (
  `detail_id` int NOT NULL AUTO_INCREMENT,
  `process_id` varchar(50) NOT NULL,
  `sub_process_id` int NOT NULL,
  `po_id` varchar(50) NOT NULL,
  `material_code` varchar(50) NOT NULL,
  `material_desc` varchar(50) NOT NULL,
  `uom_id` varchar(10) NOT NULL,
  `received_quantity` decimal(10,2) NOT NULL,
  `unit_price` decimal(10,2) NOT NULL,
  `make_no` varchar(50) DEFAULT NULL,
  `serial_no` varchar(50) DEFAULT NULL,
  `model_no` varchar(50) DEFAULT NULL,
  `warranty_terms` varchar(100) DEFAULT NULL,
  `note` varchar(100) DEFAULT NULL,
  `photo_path` varchar(100) DEFAULT NULL,
  `category` varchar(50) DEFAULT NULL,
  `ordered_quantity` decimal(10,2) DEFAULT NULL,
  `quantity_delivered` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`detail_id`),
  KEY `sub_process_id` (`sub_process_id`),
  KEY `material_code` (`material_code`),
  KEY `uom_id` (`uom_id`),
  CONSTRAINT `gprn_material_detail_ibfk_1` FOREIGN KEY (`sub_process_id`) REFERENCES `gprn_master` (`sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `gprn_material_detail_ibfk_2` FOREIGN KEY (`material_code`) REFERENCES `material_master` (`material_code`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `gprn_material_detail_ibfk_3` FOREIGN KEY (`uom_id`) REFERENCES `uom_master` (`uom_code`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gprn_material_detail`
--

LOCK TABLES `gprn_material_detail` WRITE;
/*!40000 ALTER TABLE `gprn_material_detail` DISABLE KEYS */;
INSERT INTO `gprn_material_detail` VALUES (9,'1002',9,'PO1002','M1100','laptop','Numbers',5.00,1200.00,'M1','S1','M1','1',NULL,'1750850944184_05e2b318088743ec9ead1e9235201e4a.jpg','Capital',10.00,0.00),(10,'1002',10,'PO1002','M1100','laptop','Numbers',2.00,1200.00,'M1','S1','M1','2',NULL,'1750851128776_f12ca8d4fa0f4e42afe9cc02d1fda9cb.png','Capital',5.00,5.00),(14,'1002',14,'PO1002','M1100','laptop','Numbers',3.00,1200.00,'HP','XYZ123','123ABC','5',NULL,'1751280307263_6510cd8eb9f24e9fa177b0133d5eeaff.jpg','Capital',3.00,7.00),(15,'1011',15,'PO1011','M1103','Hp laptop','Numbers',2.00,50000.00,'HP','QWEASD12Y','12345XY','2 Years',NULL,'1751540142871_cecaf9a19cd4421eb016309c2377430b.jpg','Capital',2.00,0.00),(16,'1014',16,'PO1014','M1103','Hp laptop','Numbers',1.00,50000.00,'M1','S1','M1','4 Years',NULL,'1751623208277_40b61387171b42a4ac107bfddc1994d1.png','Capital',1.00,0.00),(17,'1003',17,'PO1003','M1102','Stationary','Numbers',10.00,200.00,'Camlin','Test2','Camlin1','1 Year',NULL,'1751970272544_c11a2547f3194028824c861071eaacd7.jpg','Capital',10.00,0.00),(18,'1004',18,'PO1004','M1102','Stationary','Numbers',3.00,200.00,'M1','S1','M1','2 Years','handle With Care','1752049579954_3cf605f7fe524580a0b1c1ace251c0f9.png','Capital',10.00,0.00),(19,'1005',19,'PO1005','M1102','Stationary','Numbers',1.00,200.00,'M1','S1','M1','1 Year',NULL,'1752053279612_9e9e0379351c40d49b7df196455c7011.png','Capital',10.00,0.00),(20,'1005',20,'PO1005','M1102','Stationary','Numbers',1.00,200.00,'M1','S1','M1','1 Year',NULL,'1752124431594_b484d75db73142ab90983e4225e46e9c.png','Capital',9.00,1.00),(21,'1013',21,'PO1013','M1103','Hp laptop','Numbers',2.00,50000.00,'HP','123WQ1','HP G9','1 Year',NULL,'1752125631595_f93939b4818b498c83fe881773fba6c8.jpg','Capital',2.00,0.00),(22,'1005',22,'PO1005','M1102','Stationary','Numbers',7.00,200.00,'ABC','123ASD','ABC109','NA',NULL,'1752127152916_7e6e86426807425682ae8566db7329a3.jpg','Capital',8.00,2.00),(23,'1005',23,'PO1005','M1102','Stationary','Numbers',1.00,200.00,'M1','S1','M1','1 Year','handle With Care','1752818012261_e1d4ce94376d48fcb5f299c5d4590ff5.png','Capital',1.00,9.00),(24,'1033',24,'PO1033','M1104','Desktop','Numbers',1.00,50000.00,'M1','S1','M1','2 Years',NULL,'1757739906507_4ca009b1b6ce466b9fb7dfdcf440207a.pdf','Capital',2.00,0.00),(25,'1029',25,'PO1029','M1104','Desktop','Numbers',10.00,50000.00,'M1','S1','M1','2 Years',NULL,'1757740207587_8f5921b5d7444f0b81fdbd41a65ecaab.pdf','Capital',22.00,0.00),(26,'1039',26,'PO1039','M1110','laptop','Numbers',50.00,1000.00,'M1','S1','M1','1 Year',NULL,'1758103459840_62904c2c13e84fe9b286d3ced71410b7.pdf','Consumable',50.00,0.00),(27,'1040',27,'PO1040','M1111','Pen','Numbers',50.00,100.00,'M1','S1','M1','3 Years',NULL,'1758103633297_f7d650bf86664dd397cd927bbe660aa4.pdf','Consumable',100.00,0.00),(28,'1040',28,'PO1040','M1111','Pen','Numbers',30.00,100.00,'ABC','09876pl','asd123','NA',NULL,'1758520688731_e1fc37ad88394b05a380b5a88a84d7b6.png','Consumable',50.00,50.00),(29,'1033',30,'PO1033','M1104','Desktop','Numbers',1.00,50000.00,'qweasd','123qweasd','123456qwe','3 Years',NULL,'1758520786614_845f4c3d4e564747948bf1ff69a151aa.png','Capital',1.00,1.00),(30,'1041',31,'PO1041','M1112','External 1TB Hard Disk','Numbers',1.00,15000.00,'Sandisk','987654321','S123','1 Year',NULL,'1758775061779_9dc965e2fa94422785959b7fe239a702.jpg','Consumable',4.00,0.00),(31,'1041',32,'PO1041','M1112','External 1TB Hard Disk','Numbers',1.00,15000.00,'M1','S1','M1','1 Year',NULL,'1758776563199_60bc28d5062a47e7ab7898fb6d78cd60.pdf','Consumable',3.00,1.00),(32,'1041',33,'PO1041','M1112','External 1TB Hard Disk','Numbers',2.00,15000.00,'abc','wer234','qaz123','NA',NULL,'1758776898528_4f5c9efa645046d88985a35583573ef6.jpg','Consumable',2.00,2.00),(33,'1042',34,'PO1042','M1111','Pen','Numbers',25.00,100.00,'cello','758694','456','3 Years',NULL,'1759136924020_9dbcd6eae657407cb506373a661bd395.pdf','Consumable',100.00,0.00),(34,'1042',42,'PO1042','M1111','Pen','Numbers',25.00,100.00,'cello','758694','456','1 Year',NULL,'1759209458834_8670609d83a14b13a0a7309a2c01de98.pdf','Consumable',75.00,25.00),(35,'1042',43,'PO1042','M1111','Pen','Numbers',10.00,100.00,'ABC12','567890','XYZ@#','NA',NULL,'1759211414157_06d74f65884d4eb0903ad6015aeb09ec.pdf','Consumable',50.00,50.00),(36,'1042',44,'PO1042','M1111','Pen','Numbers',5.00,100.00,'cello','758694','456','1 Year',NULL,'1759211552079_f4f8586e585f4f0e9ba999359adb044e.pdf','Consumable',40.00,60.00),(37,'1042',45,'PO1042','M1111','Pen','Numbers',10.00,100.00,'abc','12345XYZ','ABC1','1 Year',NULL,'1759730147286_b8dce3161cd548798ec7bd57cecf5bf1.pdf','Consumable',35.00,65.00),(38,'1037',46,'PO1037','M1104','Desktop','Numbers',2.00,50000.00,'Dell','ZYX890','Dell 123','2 Years',NULL,'1759731942621_85476bd772a04add8fe0dfaabeaf0164.pdf','Capital',5.00,0.00),(39,'1042',47,'PO1042','M1111','Pen','Numbers',5.00,100.00,'cello','758694','S123','1 Year',NULL,'1759734617351_4dfd1a0ee5ca4b3fb91d2d65648f1db9.jpg','Consumable',30.00,70.00),(40,'1042',48,'PO1042','M1111','Pen','Numbers',5.00,100.00,'M1','S1','M1','1 Year',NULL,'1759830114280_912e7e3b93e5406fba6dbabb6d978b36.pdf','Consumable',25.00,75.00),(41,'1040',49,'PO1040','M1111','Pen','Numbers',5.00,100.00,'M1','S1','M1','1 Year',NULL,'1760336244204_74f65f325e4b427f9a7cbe41699eea49.pdf','Consumable',20.00,80.00),(42,'1040',50,'PO1040','M1111','Pen','Numbers',2.00,100.00,'M1','S1','M1','1 Year','','1760357467034_1f1b241976864e84b824e343a39fc86b.pdf','Consumable',17.00,83.00),(43,'1043',51,'PO1043','M1104','Desktop','Numbers',1.00,50000.00,'Dell','ZYX890','ABC1','3 Years',NULL,'1761217186668_ce792e708330485392fb485044f6a1b1.pdf','Capital',1.00,0.00),(44,'1051',52,'PO1051','M1127','mouse hp','Numbers',5.00,10000.00,'M1','S1','M1','2 Years',NULL,'1762149858770_3137d2e1e21142b6a99d0c7c3bc370b0.pdf','Capital',10.00,0.00),(45,'1053',53,'PO1053','M1127','mouse hp','Numbers',5.00,10000.00,'M1','S1','M1','2 Years',NULL,'1762153472336_d96a69a3f26945eab3e93d05aa8a1671.pdf','Capital',10.00,0.00),(46,'1053',53,'PO1053','M1128','hp cpu','Numbers',5.00,1000.00,'m21','A1','M22','3 Years',NULL,'1762153472348_76260f28520b4dd38318c94fa1596941.pdf','Capital',10.00,0.00),(47,'1051',54,'PO1051','M1127','mouse hp','Numbers',2.00,10000.00,'M1','S1','M1','3 Years',NULL,'1762153672796_53f6bf6bd0f344d6b9347166e5412e5d.pdf','Capital',5.00,5.00);
/*!40000 ALTER TABLE `gprn_material_detail` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-29 21:41:26
