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
-- Table structure for table `vendor_login_details`
--

DROP TABLE IF EXISTS `vendor_login_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vendor_login_details` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `vendor_id` varchar(255) DEFAULT NULL,
  `email_address` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `email_sent` tinyint(1) DEFAULT NULL,
  `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `is_first_login` tinyint(1) DEFAULT '1',
  `is_temp_password` tinyint(1) DEFAULT '1',
  `password_changed_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vendor_login_details`
--

LOCK TABLES `vendor_login_details` WRITE;
/*!40000 ALTER TABLE `vendor_login_details` DISABLE KEYS */;
INSERT INTO `vendor_login_details` VALUES (1,'V1001','kudaykiran.9949@gmail.com','H86TU4uf@e',1,'2025-06-27 07:09:49',0,0,NULL),(2,'V1002','kudaykiran.9949@gmail.com','mKdGbwQ#6w',1,'2025-06-27 07:11:04',0,0,NULL),(3,'V1003','kudaykiran.9949@gmail.com','VF3NAtALV8',1,'2025-06-27 09:54:27',0,0,NULL),(4,'V1004','kudaykiran.9949@gmail.com','@y9#qvo5AL',1,'2025-06-27 09:58:06',0,0,NULL),(5,'V1005','kudaykiran.9949@gmail.com','Rmg5Jw3W0Q',1,'2025-06-27 10:31:20',0,0,NULL),(6,'V1006','kudaykiran.9949@gmail.com','82eQOkBztJ',1,'2025-06-27 10:33:40',0,0,NULL),(7,'V1007','kudaykiran.9949@gmail.com','nFwrrOSLA5',1,'2025-07-02 06:07:14',0,0,NULL),(8,'V1011','shruthi.mathew@iiap.res.in','QaYBICytEW',1,'2025-07-02 10:05:40',0,0,NULL),(9,'V1012','kudaykiran.9949@gmail.com','Z58UJA8Uoc',1,'2025-07-04 09:34:08',0,0,NULL),(10,'V1013','kudaykiran.9949@gmail.com','5A#5yL44#7',1,'2025-07-04 09:37:33',0,0,NULL),(11,'V1014','kudaykiran.9949@gmail.com','Otdqa3fity',1,'2025-07-04 10:02:47',0,0,NULL),(12,'V1015','udaychowdhary743@gmail.com','3hWpi@pri$',1,'2025-07-04 11:58:03',0,0,NULL),(13,'V1016','shruthi.mathew@iiap.res.in','Y99CamdHFU',1,'2025-07-08 05:49:45',0,0,NULL),(14,'V1017','shruthi.mathew@iiap.res.in','M6XH52m6Md',1,'2025-07-08 05:56:33',0,0,NULL),(15,'V1018','shruthi.mathew@iiap.res.in','Ivzi0qWsUv',1,'2025-07-08 06:01:08',0,0,NULL),(16,'V1019','udaychowdhary743@gmail.com','ajYRgpg6Ui',1,'2025-07-23 09:48:23',0,0,NULL),(17,'V1020','indrajit@iiap.res.in','oqQrt8WgeJ',1,'2025-10-15 04:02:36',0,0,NULL),(18,'V1021','vendor@gmail.com','5qEDf7XCpj',1,'2025-10-15 17:59:38',0,0,NULL),(19,'V1022','aman@gazelle.on','JqRvbPvV6x',1,'2025-10-24 08:04:45',0,0,NULL),(20,'V1023','aman@gazelle.on','UTh&zoIlEV',1,'2025-10-27 05:53:52',0,0,NULL),(21,'V1024','aman@gazelle.in','0QUY9baYN@',1,'2025-10-27 05:57:32',0,0,NULL),(22,'V1025','kudaykiran.9949@gmail.com','bY8GbaClb6',1,'2025-10-27 07:56:26',0,0,NULL),(23,'V1026','Kudaykiran.9949@gmail.com','VcNAzJA01s',1,'2025-11-06 10:47:15',0,0,NULL),(24,'V1027','aman@gazelle.in','r6JWoMx8Ai',1,'2025-11-07 06:35:15',0,0,NULL),(25,'V1033','kudaykiran.9949@gmail.com','1b6dI6K$jd',1,'2025-11-07 09:35:04',0,0,NULL),(26,'V1034','kudaykiran.9949@gamil.com','9iyijQHvDY',1,'2025-11-07 09:37:27',0,0,NULL),(27,'V1035','udaychowdhary743@gmail.com','N4vj6QOaYr',1,'2025-11-07 09:46:11',0,0,NULL),(28,'V1036','udaychowdhary743@gmail.com','hXWiPvGl$R',1,'2025-11-07 09:51:50',0,0,NULL),(29,'V1037','udaychowdhary743@gmail.com','9vO3ksvOBF',1,'2025-11-07 09:53:37',0,0,NULL),(30,'V1038','aman@gazelle.in','password',1,'2025-11-07 10:21:50',0,0,NULL),(31,'V1039','ritwiksinghkkc@gmail.com','okiMJYFR7h',1,'2025-11-23 01:10:46',0,0,NULL),(32,'V1040','ritwiksinghkkc@gmail.com','S6inkZ6tWP',1,'2025-11-23 01:18:30',0,0,NULL),(33,'V1041','ritwiksinghkkc@gmail.com','06xCMq9#O8',1,'2025-11-23 01:30:01',0,0,NULL),(34,'V1042','ritwiksinghkkc@gmail.com','lA3l855YiU',1,'2025-11-23 01:33:05',0,0,NULL),(35,'COMP001','ritwiksinghkkc@gmail.com','UG08fjKb4m',1,'2025-11-23 02:04:39',0,0,NULL),(36,'OPTI001','ritwiksinghkkc@gmail.com','dQbQi52fCu',1,'2025-11-23 04:12:53',0,0,NULL),(37,'ELTC001','ritwiksinghkkc@gmail.com','HOXqG2l1sH',1,'2025-11-23 04:28:31',0,0,NULL),(38,'OPTI002','ritwiksinghkkc@gmail.com','qQVK2H8MFx',1,'2025-11-23 07:33:36',0,0,NULL),(39,'COMP002','ritwiksinghkkc@gmail.com','5Pqd$Oc&@Q',1,'2025-11-24 02:59:45',0,0,NULL),(40,'FABR001','ritwiksinghkkc@gmail.com','pVfylT#qds',1,'2025-11-24 06:04:27',0,0,NULL),(41,'COMP003','ritwiksinghkkc@gmail.com','sLznxmFwUD',1,'2025-11-25 03:05:56',0,0,NULL),(42,'ELTC002','ritwiksinghkkc@gmail.com','Ritwik@81',1,'2025-11-25 03:57:26',0,0,'2025-11-25 09:28:49'),(43,'ELEC001','ritwiksinghkkc@gmail.com','elV7keCKgS',1,'2025-11-25 04:08:46',1,1,NULL),(44,'OPTI003','ritwiksinghkkc@gmail.com','Ritwik@11',1,'2025-11-25 04:12:23',0,0,'2025-11-25 09:46:29'),(45,'COMP004','ritwiksinghkkc@gmail.com','Skrossi@81',0,'2026-02-24 00:50:39',0,0,'2026-02-24 06:21:29'),(46,'ELTC003','skrossi81@gmail.com','Skrossi@81',1,'2026-02-24 00:54:21',0,0,'2026-02-24 06:25:03'),(47,'FURN001','ritwiksinghkkc@gmail.com','2P7A&Z4ds7',1,'2026-03-03 00:09:58',1,1,NULL),(48,'COMP005','abhinay@gazelle.in','vc&&FdAGMZ',1,'2026-03-09 06:15:57',1,1,NULL),(49,'COMP006','abhinay@gazelle.in','Asdf@123',1,'2026-03-11 23:35:54',0,0,'2026-03-12 05:17:25'),(50,'COMP007','abhinay@gazelle.in','Asdf@123',1,'2026-03-11 23:38:50',0,0,'2026-03-12 05:35:49'),(51,'CHEM001','abhinay@gazelle.in','Asdf@123',1,'2026-03-11 23:41:01',0,0,'2026-03-12 05:28:10'),(52,'CHEM002','abhinay@gazelle.in','cHSH95zUWL',1,'2026-03-11 23:42:58',1,1,NULL);
/*!40000 ALTER TABLE `vendor_login_details` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-29 21:41:32
