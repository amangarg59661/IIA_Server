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
-- Table structure for table `department_approver_mapping`
--

DROP TABLE IF EXISTS `department_approver_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `department_approver_mapping` (
  `mapping_id` bigint NOT NULL AUTO_INCREMENT,
  `department_name` varchar(100) NOT NULL,
  `approver_type` varchar(50) NOT NULL,
  `approver_employee_id` varchar(50) DEFAULT NULL,
  `approver_role_id` int DEFAULT NULL,
  `approval_limit` decimal(15,2) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_by` varchar(100) DEFAULT NULL,
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_by` varchar(100) DEFAULT NULL,
  `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`mapping_id`),
  UNIQUE KEY `uk_dept_approver_type` (`department_name`,`approver_type`),
  UNIQUE KEY `UK4c0em5n37pvd3bqyejdeo1qt5` (`department_name`,`approver_type`),
  KEY `idx_dept_mapping_dept` (`department_name`),
  KEY `idx_dept_mapping_type` (`approver_type`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `department_approver_mapping`
--

LOCK TABLES `department_approver_mapping` WRITE;
/*!40000 ALTER TABLE `department_approver_mapping` DISABLE KEYS */;
INSERT INTO `department_approver_mapping` VALUES (1,'Admin','DEAN','TEST-A-10',NULL,150000.00,1,'admin','2026-02-25 08:05:29','admin','2026-03-04 03:47:52'),(2,'Academic','DEAN','TEST-A-10',NULL,150000.00,1,'admin','2026-02-25 08:05:54','SYSTEM','2026-03-04 09:12:16'),(3,'Non-Technical','DEAN','TEST-A-10',NULL,150000.00,1,'admin','2026-02-25 08:06:12','SYSTEM','2026-03-04 09:12:16'),(4,'BGS','DEAN','TEST-A-10',NULL,150000.00,1,'admin','2026-02-25 08:06:22','SYSTEM','2026-03-04 09:12:16'),(5,'Technical and Engineering','HEAD_SEG','TEST-A-09',NULL,100000.00,1,'admin','2026-02-25 08:06:37','SYSTEM','2026-03-04 09:12:16');
/*!40000 ALTER TABLE `department_approver_mapping` ENABLE KEYS */;
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
