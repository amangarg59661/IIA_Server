


CREATE TABLE `action_master` (
   `actionId` int NOT NULL AUTO_INCREMENT,
   `actionName` varchar(255) NOT NULL,
   `createdDate` datetime DEFAULT NULL,
   `createdBy` varchar(45) DEFAULT NULL,
   PRIMARY KEY (`actionId`)
 );

 CREATE TABLE `location_master` (
   `location_code` varchar(10) NOT NULL,
   `location_name` varchar(255) DEFAULT NULL,
   `address` text,
   `created_by` varchar(255) DEFAULT NULL,
   `updated_by` varchar(255) DEFAULT NULL,
   `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
   `updated_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   PRIMARY KEY (`location_code`)
 );
 CREATE TABLE `vendor_master` (
    `vendor_id` varchar(255) NOT NULL,
    `vendor_type` varchar(50) DEFAULT NULL,
    `vendor_name` varchar(100) DEFAULT NULL,
    `contact_no` varchar(20) DEFAULT NULL,
    `email_address` varchar(100) DEFAULT NULL,
    `registered_platform` varchar(10) DEFAULT NULL,
    `pfms_vendor_code` varchar(20) DEFAULT NULL,
    `primary_business` varchar(50) DEFAULT NULL,
    `address` text,
    `landline` varchar(20) DEFAULT NULL,
    `mobile_no` varchar(20) DEFAULT NULL,
    `fax` varchar(20) DEFAULT NULL,
    `pan_no` varchar(20) DEFAULT NULL,
    `gst_no` varchar(20) DEFAULT NULL,
    `bank_name` varchar(50) DEFAULT NULL,
    `account_no` varchar(20) DEFAULT NULL,
    `ifsc_code` varchar(15) DEFAULT NULL,
    `purchase_history` text,
    `status` varchar(10) DEFAULT NULL,
    `updated_by` varchar(200) DEFAULT NULL,
    `created_by` mediumtext,
    `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
    `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `remarks` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`vendor_id`)
  );

 CREATE TABLE `material_master` (
   `material_code` varchar(50) NOT NULL,
   `category` varchar(100) DEFAULT NULL,
   `sub_category` varchar(100) DEFAULT NULL,
   `description` text,
   `uom` varchar(50) DEFAULT NULL,
   `upload_image` longblob,
   `indigenous_or_imported` boolean DEFAULT NULL,
   `updated_by` varchar(200) DEFAULT NULL,
   `created_by` varchar(200) DEFAULT NULL,
   `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
   `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   `upload_image_name` varchar(255) DEFAULT NULL,
   `estimated_price_with_ccy` decimal(10,2) DEFAULT NULL,
   `unit_price` decimal(10,2) DEFAULT NULL,
   `currency` varchar(255) DEFAULT NULL,
   `status` varchar(255) DEFAULT NULL,
   `remarks` text,
   `brief_description` text,
   PRIMARY KEY (`material_code`)
 );

 CREATE TABLE `uom_master` (
   `uom_code` varchar(50) NOT NULL,
   `uom_name` varchar(255) DEFAULT NULL,
   `created_by` varchar(255) DEFAULT NULL,
   `updated_by` varchar(255) DEFAULT NULL,
   `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
   `updated_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   PRIMARY KEY (`uom_code`)
 );

 CREATE TABLE `locator_master` (
   `location_id` varchar(10) NOT NULL,
   `locator_id` int NOT NULL AUTO_INCREMENT,
   `locator_desc` varchar(40) NOT NULL,
   `created_by` varchar(20) DEFAULT NULL,
   `create_date` datetime DEFAULT CURRENT_TIMESTAMP,
   `updated_by` varchar(20) DEFAULT NULL,
   `update_date` datetime DEFAULT NULL,
   PRIMARY KEY (`locator_id`),
   KEY `fk_location_id` (`location_id`),
   CONSTRAINT `fk_location_id` FOREIGN KEY (`location_id`) REFERENCES `location_master` (`location_code`) ON UPDATE CASCADE
 );


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
   PRIMARY KEY (`contigency_id`)
 );


 CREATE TABLE `cp_materials` (
   `id` bigint NOT NULL AUTO_INCREMENT,
   `material_code` varchar(100) NOT NULL,
   `material_description` varchar(1000) DEFAULT NULL,
   `quantity` decimal(15,2) DEFAULT NULL,
   `unit_price` decimal(15,2) DEFAULT NULL,
   `uom` varchar(50) DEFAULT NULL,
   `total_price` decimal(15,2) DEFAULT NULL,
   `budget_code` varchar(100) DEFAULT NULL,
   `material_category` varchar(255) DEFAULT NULL,
   `material_sub_category` varchar(255) DEFAULT NULL,
   `currency` varchar(50) DEFAULT NULL,
   `contigency_id` varchar(255) DEFAULT NULL,
   `gst` decimal(10,2) DEFAULT NULL,
   PRIMARY KEY (`id`),
   KEY `contigency_id` (`contigency_id`),
   CONSTRAINT `cp_materials_ibfk_1` FOREIGN KEY (`contigency_id`) REFERENCES `contigency_purchase` (`contigency_id`) ON DELETE CASCADE
 );

CREATE TABLE `indent_creation` (
   `indent_id` varchar(255) NOT NULL,
   `indentor_name` varchar(255) NOT NULL,
   `indentor_mobile_no` varchar(20) DEFAULT NULL,
   `indentor_email_address` varchar(255) DEFAULT NULL,
   `consignes_location` varchar(255) DEFAULT NULL,
   `uploading_prior_approvals` blob,
   `project_name` varchar(255) DEFAULT NULL,
   `upload_tender_documents` blob,
   `is_pre_bit_meeting_required` boolean DEFAULT NULL,
   `pre_bid_meeting_date` date DEFAULT NULL,
   `pre_bid_meeting_venue` varchar(255) DEFAULT NULL,
   `is_it_a_rate_contract_indent` boolean DEFAULT NULL,
   `estimated_rate` decimal(10,2) DEFAULT NULL,
   `period_of_contract` decimal(10,2) DEFAULT NULL,
   `single_and_multiple_job` varchar(50) DEFAULT NULL,
   `upload_goi_or_rfp` blob,
   `upload_pac_or_brand_pac` blob,
   `updated_by` varchar(255) DEFAULT NULL,
   `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
   `updated_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   `uploading_prior_approvals_file_name` varchar(255) DEFAULT NULL,
   `technical_specifications_file_name` varchar(255) DEFAULT NULL,
   `draft_Eoi_Or_Rfp_file_name` varchar(255) DEFAULT NULL,
   `upload_pac_or_brand_pac_file_name` varchar(255) DEFAULT NULL,
   `created_by` int DEFAULT NULL,
   `file_type` varchar(255) DEFAULT NULL,
   `total_indent_value` decimal(15,2) DEFAULT NULL,
   `brand_and_model` varchar(255) DEFAULT NULL,
   `justification` varchar(255) DEFAULT NULL,
   `brand_pac` boolean DEFAULT NULL,
   `indent_number` int DEFAULT NULL,
   `quarter` varchar(200) DEFAULT NULL,
   `purpose` varchar(200) DEFAULT NULL,
   `reason` text,
   `proprietary_justification` varchar(250) DEFAULT NULL,
   `upload_buy_back_file_names` varchar(500) DEFAULT NULL,
   `buy_back` boolean DEFAULT NULL,
   `model_number` varchar(255) DEFAULT NULL,
   `serial_number` varchar(255) DEFAULT NULL,
   `date_of_purchase` date DEFAULT NULL,
   `proprietary_and_limited_declaration` text,
   `employee_department` varchar(50) DEFAULT NULL,
   PRIMARY KEY (`indent_id`)
 );


 CREATE TABLE `material_details` (
   `id` bigint NOT NULL AUTO_INCREMENT,
   `material_code` varchar(255) DEFAULT NULL,
   `indent_id` varchar(255) DEFAULT NULL,
   `material_description` varchar(255) DEFAULT NULL,
   `quantity` decimal(15,2) DEFAULT NULL,
   `unit_price` decimal(15,2) DEFAULT NULL,
   `uom` varchar(50) DEFAULT NULL,
   `total_price` decimal(15,2) DEFAULT NULL,
   `budget_code` varchar(255) DEFAULT NULL,
   `material_category` varchar(255) DEFAULT NULL,
   `material_sub_category` varchar(255) DEFAULT NULL,
   `material_and_job` varchar(255) DEFAULT NULL,
   `mode_of_procurement` varchar(255) DEFAULT NULL,
   `currency` varchar(100) DEFAULT NULL,
   PRIMARY KEY (`id`),
   KEY `indent_id` (`indent_id`),
   CONSTRAINT `material_details_ibfk_1` FOREIGN KEY (`indent_id`) REFERENCES `indent_creation` (`indent_id`) ON DELETE CASCADE
 );


 CREATE TABLE `tender_request` (
   `tender_id` varchar(255) NOT NULL,
   `title_of_tender` varchar(255) NOT NULL,
   `opening_date` date DEFAULT NULL,
   `closing_date` date DEFAULT NULL,
   `indent_materials` varchar(200) DEFAULT NULL,
   `mode_of_procurement` varchar(255) DEFAULT NULL,
   `bid_type` varchar(255) DEFAULT NULL,
   `last_date_of_submission` date DEFAULT NULL,
   `applicable_taxes` text,
   `billinng_address` varchar(255) DEFAULT NULL,
   `inco_terms` varchar(255) DEFAULT NULL,
   `payment_terms` varchar(255) DEFAULT NULL,
   `ld_clause` varchar(255) DEFAULT NULL,
   `performance_and_warranty_security` varchar(255) DEFAULT NULL,
   `bid_security_declaration` boolean DEFAULT NULL,
   `mll_status_declaration` boolean DEFAULT NULL,
   `upload_tender_documents` blob,
   `single_and_multiple_vendors` varchar(255) DEFAULT NULL,
   `upload_general_terms_and_conditions` blob,
   `upload_specific_terms_and_conditions` blob,
   `pre_bid_disscussions` text,
   `updated_by` varchar(255) DEFAULT NULL,
   `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
   `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   `upload_tender_documents_file_name` varchar(255) DEFAULT NULL,
   `upload_general_terms_and_conditions_file_name` varchar(255) DEFAULT NULL,
   `upload_specific_terms_and_conditions_file_name` varchar(255) DEFAULT NULL,
   `total_tender_value` decimal(10,2) DEFAULT NULL,
   `created_by` int DEFAULT NULL,
   `project_name` varchar(255) DEFAULT NULL,
   `file_type` varchar(255) DEFAULT NULL,
   `consignes` varchar(255) DEFAULT NULL,
   `tender_number` int DEFAULT NULL,
   `bid_security_declaration_file_name` varchar(255) DEFAULT NULL,
   `mll_status_declaration_file_name` varchar(255) DEFAULT NULL,
   `quotation_file_name` varchar(255) DEFAULT NULL,
   `vendor_id` varchar(255) DEFAULT NULL,
   PRIMARY KEY (`tender_id`)
 );

 CREATE TABLE `indent_id` (
   `id` bigint NOT NULL AUTO_INCREMENT,
   `indent_id` varchar(255) DEFAULT NULL,
   `tender_id` varchar(255) DEFAULT NULL,
   PRIMARY KEY (`id`),
   KEY `tender_id` (`tender_id`),
   CONSTRAINT `indent_id_ibfk_1` FOREIGN KEY (`tender_id`) REFERENCES `tender_request` (`tender_id`)
 );



 CREATE TABLE `purchase_order` (
   `po_id` varchar(255) NOT NULL,
   `tender_id` varchar(255) DEFAULT NULL,
   `indent_id` varchar(255) DEFAULT NULL,
   `warranty` decimal(10,2) DEFAULT NULL,
   `consignes_address` varchar(255) DEFAULT NULL,
   `billing_address` varchar(255) DEFAULT NULL,
   `delivery_period` decimal(10,2) DEFAULT NULL,
   `if_ld_clause_applicable` boolean DEFAULT NULL,
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
   PRIMARY KEY (`po_id`)
 );

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
   PRIMARY KEY (`id`),
   KEY `po_id` (`po_id`),
   CONSTRAINT `purchase_order_attributes_ibfk_1` FOREIGN KEY (`po_id`) REFERENCES `purchase_order` (`po_id`) ON DELETE CASCADE
 );

 CREATE TABLE `service_order` (
   `so_id` varchar(255) NOT NULL,
   `tender_id` varchar(255) DEFAULT NULL,
   `consignes_address` varchar(255) DEFAULT NULL,
   `billing_address` varchar(255) DEFAULT NULL,
   `job_completion_period` decimal(10,2) DEFAULT NULL,
   `if_ld_clause_applicable` boolean DEFAULT NULL,
   `inco_terms` varchar(255) DEFAULT NULL,
   `payment_terms` varchar(255) DEFAULT NULL,
   `vendor_name` varchar(255) DEFAULT NULL,
   `vendor_address` varchar(255) DEFAULT NULL,
   `applicable_pbg_to_be_submitted` varchar(255) DEFAULT NULL,
   `vendors_account_no` varchar(255) DEFAULT NULL,
   `vendors_zrsc_code` varchar(255) DEFAULT NULL,
   `vendors_account_name` varchar(255) DEFAULT NULL,
   `created_by` int DEFAULT NULL,
   `updated_by` varchar(200) DEFAULT NULL,
   `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
   `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   `total_value_of_so` decimal(10,0) DEFAULT NULL,
   `project_name` varchar(200) DEFAULT NULL,
   `vendor_id` varchar(255) DEFAULT NULL,
   PRIMARY KEY (`so_id`)
 );


 CREATE TABLE `service_order_material` (
   `id` bigint NOT NULL AUTO_INCREMENT,
   `material_code` varchar(255) DEFAULT NULL,
   `so_id` varchar(255) DEFAULT NULL,
   `material_description` varchar(255) DEFAULT NULL,
   `quantity` decimal(19,2) DEFAULT NULL,
   `rate` decimal(19,2) DEFAULT NULL,
   `exchange_rate` decimal(19,2) DEFAULT NULL,
   `currency` varchar(255) DEFAULT NULL,
   `gst` decimal(19,2) DEFAULT NULL,
   `duties` decimal(19,2) DEFAULT NULL,
   `budget_code` varchar(255) DEFAULT NULL,
   PRIMARY KEY (`id`),
   KEY `so_id` (`so_id`),
   CONSTRAINT `service_order_material_ibfk_1` FOREIGN KEY (`so_id`) REFERENCES `service_order` (`so_id`) ON DELETE CASCADE
 );

 CREATE TABLE `tender_evaluation` (
   `tender_id` varchar(255) NOT NULL,
   `upload_qualified_vendors_file_name` varchar(255) DEFAULT NULL,
   `upload_technically_qualified_vendors_file_name` varchar(255) DEFAULT NULL,
   `upload_commerially_qualified_vendors_file_name` varchar(255) DEFAULT NULL,
   `formation_of_techno_commerial_comitee` varchar(255) DEFAULT NULL,
   `response_file_name` varchar(255) DEFAULT NULL,
   `response_for_technically_qualified_vendors_file_name` varchar(255) DEFAULT NULL,
   `response_for_commerially_qualified_vendors_file_name` varchar(255) DEFAULT NULL,
   `file_type` varchar(255) DEFAULT NULL,
   `updated_by` varchar(255) DEFAULT NULL,
   `created_by` int DEFAULT NULL,
   `created_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
   `updated_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   `upload_qualified_vendors_file_name_created_by` int DEFAULT NULL,
   `upload_technically_qualified_vendors_file_name_created_by` int DEFAULT NULL,
   `upload_commerially_qualified_vendors_file_name_created_by` int DEFAULT NULL,
   `formation_of_techno_commerial_comitee_created_by` int DEFAULT NULL,
   `response_file_name_created_by` int DEFAULT NULL,
   `response_for_technically_qualified_vendors_file_name_created_by` int DEFAULT NULL,
   `response_for_commerially_qualified_vendors_file_name_created_by` int DEFAULT NULL,
   PRIMARY KEY (`tender_id`)
 );


 CREATE TABLE `work_order` (
   `wo_id` varchar(255) NOT NULL,
   `tender_id` varchar(255) DEFAULT NULL,
   `consignes_address` varchar(255) DEFAULT NULL,
   `billing_address` varchar(255) DEFAULT NULL,
   `job_completion_period` decimal(10,2) DEFAULT NULL,
   `if_ld_clause_applicable` boolean DEFAULT NULL,
   `inco_terms` varchar(255) DEFAULT NULL,
   `payment_terms` varchar(255) DEFAULT NULL,
   `vendor_name` varchar(255) DEFAULT NULL,
   `vendor_address` varchar(255) DEFAULT NULL,
   `applicable_pbg_to_be_submitted` varchar(255) DEFAULT NULL,
   `vendors_account_no` varchar(255) DEFAULT NULL,
   `vendors_zrsc_code` varchar(255) DEFAULT NULL,
   `vendors_account_name` varchar(255) DEFAULT NULL,
   `created_by` int DEFAULT NULL,
   `updated_by` varchar(200) DEFAULT NULL,
   `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
   `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   PRIMARY KEY (`wo_id`)
 );
 CREATE TABLE `work_order_material` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `work_code` varchar(255) DEFAULT NULL,
    `work_description` varchar(255) DEFAULT NULL,
    `quantity` decimal(19,2) DEFAULT NULL,
    `rate` decimal(19,2) DEFAULT NULL,
    `exchange_rate` decimal(19,2) DEFAULT NULL,
    `currency` varchar(255) DEFAULT NULL,
    `gst` decimal(19,2) DEFAULT NULL,
    `duties` decimal(19,2) DEFAULT NULL,
    `budget_code` varchar(255) DEFAULT NULL,
    `wo_id` varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `wo_id` (`wo_id`),
    CONSTRAINT `work_order_material_ibfk_1` FOREIGN KEY (`wo_id`) REFERENCES `work_order` (`wo_id`) ON DELETE CASCADE
  );





 CREATE TABLE `employee_department_master` (
   `employee_id` varchar(100) NOT NULL,
   `employee_name` varchar(100) DEFAULT NULL,
   `department_name` varchar(100) DEFAULT NULL,
   `designation` varchar(50) DEFAULT NULL,
   `contact_details` varchar(100) DEFAULT NULL,
   `updated_by` varchar(200) DEFAULT NULL,
   `created_by` varchar(200) DEFAULT NULL,
   `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
   `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   `location` varchar(255) DEFAULT NULL,
   PRIMARY KEY (`employee_id`)
 );
CREATE TABLE `job_master` (
   `job_code` varchar(255) NOT NULL,
   `category` varchar(255) DEFAULT NULL,
   `job_description` text,
   `asset_id` varchar(255) DEFAULT NULL,
   `uom` varchar(50) DEFAULT NULL,
   `value` decimal(15,2) DEFAULT NULL,
   `created_by` int DEFAULT NULL,
   `updated_by` varchar(255) DEFAULT NULL,
   `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
   `updated_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   `mode_of_procurement` varchar(255) DEFAULT NULL,
   `sub_category` varchar(255) DEFAULT NULL,
   `currency` varchar(255) DEFAULT NULL,
   `estimated_price_with_ccy` decimal(15,2) DEFAULT NULL,
   `brief_description` text,
   PRIMARY KEY (`job_code`)
 );





 CREATE TABLE `material_id_sequence` (
   `id` bigint NOT NULL AUTO_INCREMENT,
   `material_id` int DEFAULT NULL,
   PRIMARY KEY (`id`)
 );



 CREATE TABLE `material_master_util` (
   `material_code` varchar(255) NOT NULL,
   `category` varchar(255) DEFAULT NULL,
   `sub_category` varchar(255) DEFAULT NULL,
   `description` text,
   `uom` varchar(50) DEFAULT NULL,
   `unit_price` decimal(15,2) DEFAULT NULL,
   `currency` varchar(10) DEFAULT NULL,
   `estimated_price_with_ccy` decimal(15,2) DEFAULT NULL,
   `upload_image_name` varchar(255) DEFAULT NULL,
   `indigenous_or_imported` boolean DEFAULT NULL,
   `approval_status` enum('APPROVED','REJECTED','AWAITING_APPROVAL','CHANGE_REQUEST') DEFAULT NULL,
   `comments` text,
   `created_by` int DEFAULT NULL,
   `updated_by` varchar(255) DEFAULT NULL,
   `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
   `updated_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   `brief_description` text,
   `material_number` int DEFAULT NULL,
   PRIMARY KEY (`material_code`)
 );
 CREATE TABLE `material_status` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `material_code` varchar(255) NOT NULL,
    `status` varchar(50) DEFAULT NULL,
    `comments` text,
    `created_by` int DEFAULT NULL,
    `updated_by` int DEFAULT NULL,
    `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `action` varchar(200) DEFAULT NULL,
    `role_name` varchar(250) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `material_code` (`material_code`)
  );


 CREATE TABLE `project_master` (
   `project_code` varchar(255) NOT NULL,
   `project_name_description` varchar(255) DEFAULT NULL,
   `financial_year` varchar(20) DEFAULT NULL,
   `allocated_amount` decimal(15,2) DEFAULT NULL,
   `department_division` varchar(255) DEFAULT NULL,
   `budget_type` varchar(255) DEFAULT NULL,
   `start_date` date DEFAULT NULL,
   `end_date` date DEFAULT NULL,
   `remarks_notes` text,
   `project_head` varchar(255) DEFAULT NULL,
   `created_by` varchar(255) DEFAULT NULL,
   `updated_by` varchar(255) DEFAULT NULL,
   `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
   `updated_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   PRIMARY KEY (`project_code`)
 );


 CREATE TABLE `asset_master` (
     `asset_id` int NOT NULL AUTO_INCREMENT,
     `material_code` varchar(50) NOT NULL,
     `material_desc` varchar(50) NOT NULL,
     `asset_desc` varchar(50) NOT NULL,
     `make_no` varchar(50) DEFAULT NULL,
     `serial_no` varchar(50) DEFAULT NULL,
     `model_no` varchar(50) DEFAULT NULL,
     `init_quantity` decimal(10,2) DEFAULT NULL,
     `uom_id` varchar(10) NOT NULL,
     `component_name` varchar(50) DEFAULT NULL,
     `component_id` int DEFAULT NULL,
     `create_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
     `created_by` int NOT NULL,
     `updated_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
     `updated_by` int DEFAULT NULL,
     `unit_price` decimal(10,2) DEFAULT NULL,
     `depriciation_rate` decimal(10,2) DEFAULT NULL,
     `end_of_life` date DEFAULT NULL,
     `stock_levels` decimal(10,2) DEFAULT NULL,
     `condition_of_goods` varchar(100) DEFAULT NULL,
     `shelf_life` varchar(50) DEFAULT NULL,
     `po_id` varchar(50) DEFAULT NULL,
     `locator` decimal(10,2) DEFAULT NULL,
     `locator_id` varchar(20) DEFAULT NULL,
     PRIMARY KEY (`asset_id`),
     KEY `idx_material_code` (`material_code`),
     KEY `idx_uom` (`uom_id`),
     KEY `idx_material_desc` (`material_desc`),
     CONSTRAINT `asset_master_ibfk_1` FOREIGN KEY (`material_code`) REFERENCES `material_master` (`material_code`) ON UPDATE CASCADE
   );



CREATE TABLE `asset` (
   `asset_code` varchar(255) NOT NULL,
   `material_code` varchar(255) DEFAULT NULL,
   `description` text,
   `uom` varchar(50) DEFAULT NULL,
   `make_no` varchar(100) DEFAULT NULL,
   `model_no` varchar(100) DEFAULT NULL,
   `serial_no` varchar(100) DEFAULT NULL,
   `component_name` varchar(255) DEFAULT NULL,
   `component_code` varchar(255) DEFAULT NULL,
   `quantity` int DEFAULT NULL,
   `locator` varchar(255) DEFAULT NULL,
   `transaction_history` text,
   `current_condition` varchar(50) DEFAULT NULL,
   `updated_by` varchar(200) DEFAULT NULL,
   `created_by` varchar(200) DEFAULT NULL,
   `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
   `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   PRIMARY KEY (`asset_code`)
 );

CREATE TABLE `material_disposal` (
   `material_disposal_code` varchar(255) NOT NULL,
   `disposal_category` varchar(255) DEFAULT NULL,
   `disposal_mode` varchar(255) DEFAULT NULL,
   `vendor_details` varchar(255) DEFAULT NULL,
   `disposal_date` date DEFAULT NULL,
   `current_book_value` decimal(19,2) DEFAULT NULL,
   `edit_reserve_value` decimal(19,2) DEFAULT NULL,
   `final_bid_value` decimal(19,2) DEFAULT NULL,
   `sale_note` longblob,
   `sale_note_file_name` varchar(255) DEFAULT NULL,
   `edit_quantity` decimal(19,2) DEFAULT NULL,
   `edit_value_materials` decimal(19,2) DEFAULT NULL,
   `created_by` varchar(255) DEFAULT NULL,
   `updated_by` varchar(255) DEFAULT NULL,
   `created_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
   `updated_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   PRIMARY KEY (`material_disposal_code`)
 );
 CREATE TABLE `asset_disposal` (
    `disposal_id` int NOT NULL AUTO_INCREMENT,
    `disposal_date` date NOT NULL,
    `created_by` int NOT NULL,
    `create_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `location_id` varchar(10) NOT NULL,
    `vendor_id` varchar(50) DEFAULT NULL,
    PRIMARY KEY (`disposal_id`),
    KEY `location_id` (`location_id`),
    CONSTRAINT `asset_disposal_ibfk_1` FOREIGN KEY (`location_id`) REFERENCES `location_master` (`location_code`) ON UPDATE CASCADE
  );

 CREATE TABLE `asset_disposal_detail` (
    `disposal_detail_id` int NOT NULL AUTO_INCREMENT,
    `disposal_id` int NOT NULL,
    `asset_id` int NOT NULL,
    `asset_desc` varchar(50) NOT NULL,
    `disposal_quantity` decimal(10,2) NOT NULL,
    `disposal_category` varchar(50) NOT NULL,
    `disposal_mode` varchar(50) NOT NULL,
    `sales_note_filename` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`disposal_detail_id`),
    KEY `disposal_id` (`disposal_id`),
    CONSTRAINT `asset_disposal_detail_ibfk_1` FOREIGN KEY (`disposal_id`) REFERENCES `asset_disposal` (`disposal_id`) ON DELETE CASCADE ON UPDATE CASCADE
  );

CREATE TABLE `gatepass_out_in` (
   `gate_pass_id` varchar(255) NOT NULL,
   `gate_pass_type` varchar(255) DEFAULT NULL,
   `material_details` text,
   `expected_date_of_return` date DEFAULT NULL,
   `extendEDR` decimal(10,2) DEFAULT NULL,
   `created_by` varchar(255) DEFAULT NULL,
   `updated_by` varchar(255) DEFAULT NULL,
   `created_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
   `updated_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   PRIMARY KEY (`gate_pass_id`)
 );

CREATE TABLE `goods_inspection` (
   `id` bigint NOT NULL AUTO_INCREMENT,
   `goods_inspection_no` varchar(50) NOT NULL,
   `installation_date` varchar(20) DEFAULT NULL,
   `commissioning_date` varchar(20) DEFAULT NULL,
   `upload_installation_report` blob,
   `accepted_quantity` int NOT NULL,
   `rejected_quantity` int NOT NULL,
   `created_by` varchar(255) DEFAULT NULL,
   `updated_by` varchar(200) DEFAULT NULL,
   `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
   `updated_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   `upload_installation_report_file_name` varchar(100) DEFAULT NULL,
   `receipt_inspection_no` varchar(200) DEFAULT NULL,
   PRIMARY KEY (`id`)
 );

 CREATE TABLE `goods_receipt_inspection` (
   `receipt_inspection_no` varchar(255) NOT NULL,
   `installation_date` date DEFAULT NULL,
   `commissioning_date` date DEFAULT NULL,
   `asset_code` varchar(255) DEFAULT NULL,
   `additional_material_description` text,
   `locator` varchar(255) DEFAULT NULL,
   `print_label_option` boolean DEFAULT '0',
   `depreciation_rate` double DEFAULT NULL,
   `book_value` double DEFAULT NULL,
   `attach_component_popup` varchar(255) DEFAULT NULL,
   `updated_by` varchar(255) DEFAULT NULL,
   `created_by` varchar(255) DEFAULT NULL,
   `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
   `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   PRIMARY KEY (`receipt_inspection_no`)
 );

 CREATE TABLE `goods_return` (
   `goods_return_id` varchar(255) NOT NULL,
   `goods_return_note_no` varchar(255) DEFAULT NULL,
   `rejected_quantity` int DEFAULT NULL,
   `return_quantity` int DEFAULT NULL,
   `type_of_return` varchar(100) DEFAULT NULL,
   `reason_of_return` text,
   `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
   `created_by` varchar(200) DEFAULT NULL,
   `updated_by` varchar(200) DEFAULT NULL,
   `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   PRIMARY KEY (`goods_return_id`)
 );


 CREATE TABLE `gprn` (
   `gprn_no` varchar(255) NOT NULL,
   `po_id` varchar(255) DEFAULT NULL,
   `date` date NOT NULL,
   `delivery_challan_no` varchar(255) DEFAULT NULL,
   `delivery_challan_date` date DEFAULT NULL,
   `vendor_id` varchar(255) DEFAULT NULL,
   `vendor_name` varchar(255) DEFAULT NULL,
   `vendor_email` varchar(255) DEFAULT NULL,
   `vendor_contact_no` bigint DEFAULT NULL,
   `field_station` varchar(255) DEFAULT NULL,
   `indentor_name` varchar(255) DEFAULT NULL,
   `expected_supply_date` date DEFAULT NULL,
   `consignee_detail` varchar(255) DEFAULT NULL,
   `warranty_years` int DEFAULT NULL,
   `project` varchar(255) DEFAULT NULL,
   `received_qty` varchar(255) DEFAULT NULL,
   `pending_qty` varchar(255) DEFAULT NULL,
   `accepted_qty` varchar(255) DEFAULT NULL,
   `provisional_receipt_certificate` blob,
   `received_by` varchar(255) DEFAULT NULL,
   `created_by` varchar(255) DEFAULT NULL,
   `updated_by` varchar(255) DEFAULT NULL,
   `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
   `updated_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   PRIMARY KEY (`gprn_no`)
 );

 CREATE TABLE `gprn_master` (
   `process_id` varchar(50) NOT NULL,
   `sub_process_id` int NOT NULL AUTO_INCREMENT,
   `po_id` varchar(50) NOT NULL,
   `location_id` varchar(10) NOT NULL,
   `date` date DEFAULT NULL,
   `challan_no` varchar(50) NOT NULL,
   `delivery_date` date NOT NULL,
   `vendor_id` varchar(255) DEFAULT NULL,
   `field_station` varchar(50) NOT NULL,
   `indentor_name` varchar(50) NOT NULL,
   `supply_expected_date` date NOT NULL,
   `consignee_detail` varchar(100) NOT NULL,
   `warranty_years` decimal(10,1) DEFAULT NULL,
   `project` varchar(50) DEFAULT NULL,
   `received_by` varchar(50) NOT NULL,
   `created_by` varchar(50) NOT NULL,
   `updated_by` varchar(50) DEFAULT NULL,
   `create_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
   `updated_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   `warranty` varchar(100) DEFAULT NULL,
   `status` varchar(20) DEFAULT NULL,
   PRIMARY KEY (`sub_process_id`),
   KEY `location_id` (`location_id`),
   KEY `gprn_master_ibfk_1` (`vendor_id`),
   CONSTRAINT `gprn_master_ibfk_1` FOREIGN KEY (`vendor_id`) REFERENCES `vendor_master` (`vendor_id`),
   CONSTRAINT `gprn_master_ibfk_2` FOREIGN KEY (`location_id`) REFERENCES `location_master` (`location_code`) ON UPDATE CASCADE
 );





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
 );
 CREATE TABLE `gprn_materials` (
   `material_code` varchar(255) NOT NULL,
   `description` varchar(255) DEFAULT NULL,
   `uom` varchar(50) DEFAULT NULL,
   `ordered_quantity` int DEFAULT NULL,
   `quantity_delivered` int DEFAULT NULL,
   `received_quantity` int DEFAULT NULL,
   `unit_price` double DEFAULT NULL,
   `net_price` decimal(18,2) DEFAULT NULL,
   `make_no` varchar(255) DEFAULT NULL,
   `model_no` varchar(255) DEFAULT NULL,
   `serial_no` varchar(255) DEFAULT NULL,
   `warranty` varchar(255) DEFAULT NULL,
   `note` varchar(255) DEFAULT NULL,
   `photograph_path` blob,
   `gprn_id` varchar(255) DEFAULT NULL,
   `photo_file_name` varchar(100) DEFAULT NULL,
   PRIMARY KEY (`material_code`),
   KEY `gprn_id` (`gprn_id`),
   CONSTRAINT `gprn_materials_ibfk_1` FOREIGN KEY (`gprn_id`) REFERENCES `gprn` (`gprn_no`)
);

 CREATE TABLE `issue_note_master` (
   `issue_note_id` int NOT NULL AUTO_INCREMENT,
   `issue_note_type` enum('Returnable','Non Returnable') DEFAULT NULL,
   `issue_date` date NOT NULL,
   `consignee_detail` varchar(50) DEFAULT NULL,
   `indentor_name` varchar(50) DEFAULT NULL,
   `field_station` varchar(50) DEFAULT NULL,
   `created_by` int NOT NULL,
   `create_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
   `location_id` varchar(10) NOT NULL,
   PRIMARY KEY (`issue_note_id`),
   KEY `location_id` (`location_id`),
   CONSTRAINT `issue_note_master_ibfk_1` FOREIGN KEY (`location_id`) REFERENCES `location_master` (`location_code`) ON UPDATE CASCADE
 );
 CREATE TABLE `issue_note_detail` (
   `detail_id` int NOT NULL AUTO_INCREMENT,
   `issue_note_id` int NOT NULL,
   `asset_id` int NOT NULL,
   `locator_id` int NOT NULL,
   `quantity` decimal(10,2) NOT NULL,
   PRIMARY KEY (`detail_id`),
   KEY `issue_note_id` (`issue_note_id`),
   KEY `asset_id` (`asset_id`),
   KEY `locator_id` (`locator_id`),
   CONSTRAINT `issue_note_detail_ibfk_1` FOREIGN KEY (`issue_note_id`) REFERENCES `issue_note_master` (`issue_note_id`) ON DELETE CASCADE ON UPDATE CASCADE,
   CONSTRAINT `issue_note_detail_ibfk_2` FOREIGN KEY (`asset_id`) REFERENCES `asset_master` (`asset_id`) ON UPDATE CASCADE,
   CONSTRAINT `issue_note_detail_ibfk_3` FOREIGN KEY (`locator_id`) REFERENCES `locator_master` (`locator_id`) ON UPDATE CASCADE
 );


 CREATE TABLE `ogp_master` (
   `ogp_process_id` varchar(50) NOT NULL,
   `ogp_sub_process_id` int NOT NULL AUTO_INCREMENT,
   `issue_note_id` int NOT NULL,
   `ogp_date` date NOT NULL,
   `location_id` varchar(10) NOT NULL,
   `created_by` int NOT NULL,
   `create_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
   `ogp_type` varchar(20) NOT NULL,
   `receiver_name` varchar(50) DEFAULT NULL,
   `receiver_location` varchar(100) DEFAULT NULL,
   `date_of_return` date DEFAULT NULL,
   `status` varchar(20) DEFAULT NULL,
   PRIMARY KEY (`ogp_sub_process_id`),
   KEY `location_id` (`location_id`),
   KEY `issue_note_id` (`issue_note_id`),
   CONSTRAINT `ogp_master_ibfk_1` FOREIGN KEY (`location_id`) REFERENCES `location_master` (`location_code`) ON UPDATE CASCADE,
   CONSTRAINT `ogp_master_ibfk_2` FOREIGN KEY (`issue_note_id`) REFERENCES `issue_note_master` (`issue_note_id`) ON DELETE CASCADE ON UPDATE CASCADE
 );

 CREATE TABLE `ogp_detail` (
    `detail_id` int NOT NULL AUTO_INCREMENT,
    `ogp_process_id` varchar(50) NOT NULL,
    `issue_note_id` int DEFAULT NULL,
    `ogp_sub_process_id` int NOT NULL,
    `asset_id` int NOT NULL,
    `locator_id` int NOT NULL,
    `quantity` decimal(10,2) NOT NULL,
    PRIMARY KEY (`detail_id`),
    KEY `ogp_sub_process_id` (`ogp_sub_process_id`),
    KEY `issue_note_id` (`issue_note_id`),
    KEY `asset_id` (`asset_id`),
    KEY `locator_id` (`locator_id`),
    CONSTRAINT `ogp_detail_ibfk_1` FOREIGN KEY (`ogp_sub_process_id`) REFERENCES `ogp_master` (`ogp_sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `ogp_detail_ibfk_2` FOREIGN KEY (`issue_note_id`) REFERENCES `issue_note_master` (`issue_note_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `ogp_detail_ibfk_3` FOREIGN KEY (`asset_id`) REFERENCES `asset_master` (`asset_id`) ON UPDATE CASCADE,
    CONSTRAINT `ogp_detail_ibfk_4` FOREIGN KEY (`locator_id`) REFERENCES `locator_master` (`locator_id`) ON UPDATE CASCADE
  );

CREATE TABLE `ogp_master_po` (
   `ogp_sub_process_id` int NOT NULL AUTO_INCREMENT,
   `po_id` varchar(50) NOT NULL,
   `ogp_date` date NOT NULL,
   `location_id` varchar(10) NOT NULL,
   `created_by` int NOT NULL,
   `create_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
   `ogp_type` varchar(20) NOT NULL,
   `receiver_name` varchar(50) DEFAULT NULL,
   `receiver_location` varchar(100) DEFAULT NULL,
   `date_of_return` date DEFAULT NULL,
   `status` varchar(20) DEFAULT NULL,
   PRIMARY KEY (`ogp_sub_process_id`),
   KEY `location_id` (`location_id`),
   KEY `po_id` (`po_id`),
   CONSTRAINT `ogp_master_po_ibfk_1` FOREIGN KEY (`location_id`) REFERENCES `location_master` (`location_code`) ON UPDATE CASCADE,
   CONSTRAINT `ogp_master_po_ibfk_2` FOREIGN KEY (`po_id`) REFERENCES `purchase_order` (`po_id`) ON DELETE CASCADE ON UPDATE CASCADE
 );

CREATE TABLE `ogp_po_detail` (
   `detail_id` int NOT NULL AUTO_INCREMENT,
   `ogp_sub_process_id` int NOT NULL,
   `material_code` varchar(50) NOT NULL,
   `material_desc` varchar(50) NOT NULL,
   `uom_id` varchar(10) NOT NULL,
   `quantity` decimal(10,2) NOT NULL,
   PRIMARY KEY (`detail_id`),
   KEY `ogp_sub_process_id` (`ogp_sub_process_id`),
   KEY `material_code` (`material_code`),
   CONSTRAINT `ogp_po_detail_ibfk_1` FOREIGN KEY (`ogp_sub_process_id`) REFERENCES `ogp_master_po` (`ogp_sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE,
   CONSTRAINT `ogp_po_detail_ibfk_2` FOREIGN KEY (`material_code`) REFERENCES `material_master` (`material_code`) ON UPDATE CASCADE
 );

CREATE TABLE `ohq_master` (
   `ohq_id` int NOT NULL AUTO_INCREMENT,
   `asset_id` int NOT NULL,
   `locator_id` int NOT NULL,
   `book_value` decimal(10,2) NOT NULL,
   `depriciation_rate` decimal(10,2) NOT NULL,
   `unit_price` decimal(10,2) NOT NULL,
   `quantity` decimal(10,2) NOT NULL,
   PRIMARY KEY (`ohq_id`),
   KEY `asset_id` (`asset_id`),
   KEY `locator_id` (`locator_id`),
   CONSTRAINT `ohq_master_ibfk_1` FOREIGN KEY (`asset_id`) REFERENCES `asset_master` (`asset_id`) ON UPDATE CASCADE,
   CONSTRAINT `ohq_master_ibfk_2` FOREIGN KEY (`locator_id`) REFERENCES `locator_master` (`locator_id`) ON UPDATE CASCADE
 );
 CREATE TABLE `ohq_master_consumable` (
    `ohq_id` int NOT NULL AUTO_INCREMENT,
    `material_code` varchar(50) NOT NULL,
    `locator_id` int NOT NULL,
    `book_value` decimal(10,2) NOT NULL,
    `depriciation_rate` decimal(10,2) NOT NULL,
    `unit_price` decimal(10,2) NOT NULL,
    `quantity` decimal(10,2) NOT NULL,
    PRIMARY KEY (`ohq_id`),
    KEY `material_code` (`material_code`),
    KEY `locator_id` (`locator_id`),
    CONSTRAINT `ohq_master_consumable_ibfk_1` FOREIGN KEY (`material_code`) REFERENCES `material_master` (`material_code`) ON UPDATE CASCADE,
    CONSTRAINT `ohq_master_consumable_ibfk_2` FOREIGN KEY (`locator_id`) REFERENCES `locator_master` (`locator_id`) ON UPDATE CASCADE
  );

 CREATE TABLE `goods_inspection_master` (
   `inspection_sub_process_id` int NOT NULL AUTO_INCREMENT,
   `gprn_process_id` varchar(50) NOT NULL,
   `gprn_sub_process_id` int NOT NULL,
   `installation_date` date DEFAULT NULL,
   `commissioning_date` date DEFAULT NULL,
   `location_id` varchar(10) DEFAULT NULL,
   `create_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
   `created_by` int NOT NULL,
   `status` varchar(20) DEFAULT NULL,
   PRIMARY KEY (`inspection_sub_process_id`),
   KEY `idx_gprn_process` (`gprn_process_id`),
   KEY `idx_gprn_subprocess` (`gprn_sub_process_id`),
   KEY `location_id` (`location_id`),
   CONSTRAINT `goods_inspection_master_ibfk_1` FOREIGN KEY (`location_id`) REFERENCES `location_master` (`location_code`) ON UPDATE CASCADE,
   CONSTRAINT `goods_inspection_master_ibfk_2` FOREIGN KEY (`gprn_sub_process_id`) REFERENCES `gprn_master` (`sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE
 );

CREATE TABLE `goods_inspection_consumable_detail` (
   `inspection_detail_id` int NOT NULL AUTO_INCREMENT,
   `inspection_sub_process_id` int NOT NULL,
   `gprn_sub_process_id` int NOT NULL,
   `gprn_process_id` int NOT NULL,
   `material_code` varchar(50) NOT NULL,
   `material_desc` varchar(50) NOT NULL,
   `uom_id` varchar(10) NOT NULL,
   `installation_report_filename` varchar(255) DEFAULT NULL,
   `received_quantity` decimal(10,2) NOT NULL,
   `accepted_quantity` decimal(10,2) NOT NULL,
   `rejected_quantity` decimal(10,2) NOT NULL,
   PRIMARY KEY (`inspection_detail_id`),
   KEY `idx_inspection_subprocess` (`inspection_sub_process_id`),
   KEY `idx_gprn_subprocess` (`gprn_sub_process_id`),
   KEY `idx_material` (`material_code`),
   CONSTRAINT `goods_inspection_consumable_detail_ibfk_1` FOREIGN KEY (`inspection_sub_process_id`) REFERENCES `goods_inspection_master` (`inspection_sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE,
   CONSTRAINT `goods_inspection_consumable_detail_ibfk_2` FOREIGN KEY (`gprn_sub_process_id`) REFERENCES `gprn_master` (`sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE
 );

 CREATE TABLE `goods_inspection_detail` (
   `inspection_detail_id` int NOT NULL AUTO_INCREMENT,
   `inspection_sub_process_id` int NOT NULL,
   `gprn_sub_process_id` int NOT NULL,
   `gprn_process_id` int NOT NULL,
   `material_code` varchar(50) NOT NULL,
   `material_desc` varchar(50) NOT NULL,
   `asset_id` int DEFAULT NULL,
   `installation_report_filename` varchar(255) DEFAULT NULL,
   `received_quantity` decimal(10,2) NOT NULL,
   `accepted_quantity` decimal(10,2) NOT NULL,
   `rejected_quantity` decimal(10,2) NOT NULL,
   `reject_reason` varchar(100) DEFAULT NULL,
   PRIMARY KEY (`inspection_detail_id`),
   KEY `idx_inspection_subprocess` (`inspection_sub_process_id`),
   KEY `idx_gprn_subprocess` (`gprn_sub_process_id`),
   KEY `idx_material` (`material_code`),
   KEY `asset_id` (`asset_id`),
   CONSTRAINT `goods_inspection_detail_ibfk_1` FOREIGN KEY (`inspection_sub_process_id`) REFERENCES `goods_inspection_master` (`inspection_sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE,
   CONSTRAINT `goods_inspection_detail_ibfk_2` FOREIGN KEY (`gprn_sub_process_id`) REFERENCES `gprn_master` (`sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE,
   CONSTRAINT `goods_inspection_detail_ibfk_3` FOREIGN KEY (`asset_id`) REFERENCES `asset_master` (`asset_id`) ON UPDATE CASCADE
 );

 CREATE TABLE `igp_master` (
   `igp_process_id` varchar(50) NOT NULL,
   `igp_sub_process_id` int NOT NULL AUTO_INCREMENT,
   `ogp_sub_process_id` int NOT NULL,
   `igp_date` date NOT NULL,
   `location_id` varchar(10) NOT NULL,
   `created_by` int NOT NULL,
   `create_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
   PRIMARY KEY (`igp_sub_process_id`),
   KEY `location_id` (`location_id`),
   KEY `ogp_sub_process_id` (`ogp_sub_process_id`),
   CONSTRAINT `igp_master_ibfk_1` FOREIGN KEY (`location_id`) REFERENCES `location_master` (`location_code`) ON UPDATE CASCADE,
   CONSTRAINT `igp_master_ibfk_2` FOREIGN KEY (`ogp_sub_process_id`) REFERENCES `ogp_master` (`ogp_sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE
 );

 CREATE TABLE `igp_po_detail` (
   `detail_id` int NOT NULL AUTO_INCREMENT,
   `igp_sub_process_id` int NOT NULL,
   `material_code` varchar(255) NOT NULL,
   `material_desc` varchar(255) NOT NULL,
   `uom_id` varchar(255) NOT NULL,
   `quantity` decimal(10,2) NOT NULL,
   PRIMARY KEY (`detail_id`),
   KEY `igp_sub_process_id` (`igp_sub_process_id`),
   CONSTRAINT `igp_po_detail_ibfk_1` FOREIGN KEY (`igp_sub_process_id`) REFERENCES `igp_master` (`igp_sub_process_id`)
 );

CREATE TABLE `igp_detail` (
   `detail_id` int NOT NULL AUTO_INCREMENT,
   `igp_process_id` varchar(50) NOT NULL,
   `igp_sub_process_id` int NOT NULL,
   `ogp_sub_process_id` int DEFAULT NULL,
   `asset_id` int NOT NULL,
   `locator_id` int NOT NULL,
   `quantity` decimal(10,2) NOT NULL,
   PRIMARY KEY (`detail_id`),
   KEY `igp_sub_process_id` (`igp_sub_process_id`),
   KEY `asset_id` (`asset_id`),
   KEY `locator_id` (`locator_id`),
   KEY `ogp_sub_process_id` (`ogp_sub_process_id`),
   CONSTRAINT `igp_detail_ibfk_1` FOREIGN KEY (`igp_sub_process_id`) REFERENCES `igp_master` (`igp_sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE,
   CONSTRAINT `igp_detail_ibfk_2` FOREIGN KEY (`asset_id`) REFERENCES `asset_master` (`asset_id`) ON UPDATE CASCADE,
   CONSTRAINT `igp_detail_ibfk_3` FOREIGN KEY (`locator_id`) REFERENCES `locator_master` (`locator_id`) ON UPDATE CASCADE,
   CONSTRAINT `igp_detail_ibfk_4` FOREIGN KEY (`ogp_sub_process_id`) REFERENCES `ogp_master` (`ogp_sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE
 );



 CREATE TABLE `grn_master` (
   `grn_process_id` varchar(50) NOT NULL,
   `grn_sub_process_id` int NOT NULL AUTO_INCREMENT,
   `gi_process_id` varchar(50) DEFAULT NULL,
   `gi_sub_process_id` int DEFAULT NULL,
   `grn_type` varchar(10) DEFAULT NULL,
   `igp_process_id` varchar(50) DEFAULT NULL,
   `igp_sub_process_id` int DEFAULT NULL,
   `grn_date` date DEFAULT NULL,
   `installation_date` date DEFAULT NULL,
   `commissioning_date` date DEFAULT NULL,
   `created_by` varchar(50) NOT NULL,
   `system_created_by` int NOT NULL,
   `create_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
   `location_id` varchar(10) NOT NULL,
   PRIMARY KEY (`grn_sub_process_id`),
   KEY `location_id` (`location_id`),
   KEY `gi_sub_process_id` (`gi_sub_process_id`),
   KEY `igp_sub_process_id` (`igp_sub_process_id`),
   CONSTRAINT `grn_master_ibfk_1` FOREIGN KEY (`location_id`) REFERENCES `location_master` (`location_code`) ON UPDATE CASCADE,
   CONSTRAINT `grn_master_ibfk_2` FOREIGN KEY (`gi_sub_process_id`) REFERENCES `goods_inspection_master` (`inspection_sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE,
   CONSTRAINT `grn_master_ibfk_3` FOREIGN KEY (`igp_sub_process_id`) REFERENCES `igp_master` (`igp_sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE
 );



 CREATE TABLE `grn_consumable_detail` (
   `detail_id` int NOT NULL AUTO_INCREMENT,
   `grn_process_id` varchar(50) NOT NULL,
   `grn_sub_process_id` int NOT NULL,
   `gi_sub_process_id` int DEFAULT NULL,
   `igp_sub_process_id` int DEFAULT NULL,
   `quantity` decimal(10,2) NOT NULL,
   `material_code` varchar(50) NOT NULL,
   `locator_id` int NOT NULL,
   `book_value` decimal(10,2) NOT NULL,
   `depriciation_rate` decimal(10,2) NOT NULL,
   PRIMARY KEY (`detail_id`),
   KEY `grn_sub_process_id` (`grn_sub_process_id`),
   KEY `gi_sub_process_id` (`gi_sub_process_id`),
   KEY `locator_id` (`locator_id`),
   KEY `igp_sub_process_id` (`igp_sub_process_id`),
   CONSTRAINT `grn_consumable_detail_ibfk_1` FOREIGN KEY (`grn_sub_process_id`) REFERENCES `grn_master` (`grn_sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE,
   CONSTRAINT `grn_consumable_detail_ibfk_2` FOREIGN KEY (`gi_sub_process_id`) REFERENCES `goods_inspection_master` (`inspection_sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE,
   CONSTRAINT `grn_consumable_detail_ibfk_3` FOREIGN KEY (`locator_id`) REFERENCES `locator_master` (`locator_id`) ON UPDATE CASCADE,
   CONSTRAINT `grn_consumable_detail_ibfk_4` FOREIGN KEY (`igp_sub_process_id`) REFERENCES `igp_master` (`igp_sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE
 );


 CREATE TABLE `grn_material_detail` (
   `detail_id` int NOT NULL AUTO_INCREMENT,
   `grn_process_id` varchar(50) NOT NULL,
   `grn_sub_process_id` int NOT NULL,
   `gi_sub_process_id` int DEFAULT NULL,
   `igp_sub_process_id` int DEFAULT NULL,
   `quantity` decimal(10,2) NOT NULL,
   `asset_id` int NOT NULL,
   `locator_id` int NOT NULL,
   `book_value` decimal(10,2) NOT NULL,
   `depriciation_rate` decimal(10,2) NOT NULL,
   PRIMARY KEY (`detail_id`),
   KEY `asset_id` (`asset_id`),
   KEY `grn_sub_process_id` (`grn_sub_process_id`),
   KEY `gi_sub_process_id` (`gi_sub_process_id`),
   KEY `locator_id` (`locator_id`),
   KEY `igp_sub_process_id` (`igp_sub_process_id`),
   CONSTRAINT `grn_material_detail_ibfk_1` FOREIGN KEY (`asset_id`) REFERENCES `asset_master` (`asset_id`) ON UPDATE CASCADE,
   CONSTRAINT `grn_material_detail_ibfk_2` FOREIGN KEY (`grn_sub_process_id`) REFERENCES `grn_master` (`grn_sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE,
   CONSTRAINT `grn_material_detail_ibfk_3` FOREIGN KEY (`gi_sub_process_id`) REFERENCES `goods_inspection_master` (`inspection_sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE,
   CONSTRAINT `grn_material_detail_ibfk_4` FOREIGN KEY (`locator_id`) REFERENCES `locator_master` (`locator_id`) ON UPDATE CASCADE,
   CONSTRAINT `grn_material_detail_ibfk_5` FOREIGN KEY (`igp_sub_process_id`) REFERENCES `igp_master` (`igp_sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE
 );
 CREATE TABLE `grv_master` (
   `gi_sub_process_id` int NOT NULL,
   `gi_process_id` varchar(50) NOT NULL,
   `grv_process_id` varchar(50) NOT NULL,
   `grv_sub_process_id` int NOT NULL AUTO_INCREMENT,
   `date` date DEFAULT NULL,
   `created_by` varchar(50) NOT NULL,
   `create_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
   `location_id` varchar(10) NOT NULL,
   PRIMARY KEY (`grv_sub_process_id`),
   KEY `idx_grv_process_id` (`grv_process_id`),
   KEY `idx_gi_sub_process` (`gi_sub_process_id`),
   KEY `idx_date` (`date`),
   KEY `location_id` (`location_id`),
   CONSTRAINT `grv_master_ibfk_1` FOREIGN KEY (`gi_sub_process_id`) REFERENCES `goods_inspection_master` (`inspection_sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE,
   CONSTRAINT `grv_master_ibfk_2` FOREIGN KEY (`location_id`) REFERENCES `location_master` (`location_code`) ON UPDATE CASCADE
 );



 CREATE TABLE `grv_material_detail` (
   `detail_id` int NOT NULL AUTO_INCREMENT,
   `grv_process_id` varchar(50) NOT NULL,
   `grv_sub_process_id` int NOT NULL,
   `gi_sub_process_id` int NOT NULL,
   `material_code` varchar(50) NOT NULL,
   `material_desc` varchar(50) NOT NULL,
   `uom_id` varchar(10) DEFAULT NULL,
   `rejected_quantity` decimal(10,2) NOT NULL,
   `return_quantity` decimal(10,2) NOT NULL,
   `return_type` varchar(50) NOT NULL,
   `reject_reason` varchar(50) NOT NULL,
   PRIMARY KEY (`detail_id`),
   KEY `idx_grv_sub_process` (`grv_sub_process_id`),
   KEY `idx_grv_process_id` (`grv_process_id`),
   KEY `idx_material` (`material_code`),
   KEY `idx_return_type` (`return_type`),
   KEY `uom_id` (`uom_id`),
   CONSTRAINT `grv_material_detail_ibfk_1` FOREIGN KEY (`grv_sub_process_id`) REFERENCES `grv_master` (`grv_sub_process_id`) ON DELETE CASCADE ON UPDATE CASCADE,
   CONSTRAINT `grv_material_detail_ibfk_2` FOREIGN KEY (`uom_id`) REFERENCES `uom_master` (`uom_code`) ON UPDATE CASCADE,
   CONSTRAINT `grv_material_detail_ibfk_3` FOREIGN KEY (`material_code`) REFERENCES `material_master` (`material_code`) ON UPDATE CASCADE
 );






 CREATE TABLE `role_master` (
   `roleId` int NOT NULL AUTO_INCREMENT,
   `roleName` varchar(100) DEFAULT NULL,
   `createdDate` datetime DEFAULT NULL,
   `createdBy` varchar(45) DEFAULT NULL,
   PRIMARY KEY (`roleId`)
 );


 CREATE TABLE `state_master` (
   `stateId` int NOT NULL AUTO_INCREMENT,
   `stateName` varchar(255) NOT NULL,
   `createdDate` datetime DEFAULT NULL,
   `createdBy` varchar(45) DEFAULT NULL,
   PRIMARY KEY (`stateId`)
 );


 CREATE TABLE `user_master` (
   `userId` int NOT NULL AUTO_INCREMENT,
   `password` varchar(100) NOT NULL,
   `userName` varchar(100) DEFAULT NULL,
   `email` varchar(255) DEFAULT NULL,
   `mobileNumber` varchar(10) DEFAULT NULL,
   `createdDate` datetime DEFAULT NULL,
   `createdBy` varchar(45) DEFAULT NULL,
   `role_name` varchar(255) DEFAULT NULL,
   `employee_id` varchar(50) DEFAULT NULL,
   PRIMARY KEY (`userId`)
 );

 CREATE TABLE `user_role_master` (
   `userRoleId` int NOT NULL AUTO_INCREMENT,
   `userId` int NOT NULL,
   `roleId` int NOT NULL,
   `readPermission` boolean NOT NULL,
   `writePermission` boolean NOT NULL,
   `createdDate` datetime DEFAULT NULL,
   `createdBy` varchar(45) DEFAULT NULL,
   PRIMARY KEY (`userRoleId`)
 );


 CREATE TABLE `vendor_id_sequence` (
   `id` bigint NOT NULL AUTO_INCREMENT,
   `vendor_id` int DEFAULT NULL,
   PRIMARY KEY (`id`)
 );

 CREATE TABLE `vendor_login_details` (
   `id` bigint NOT NULL AUTO_INCREMENT,
   `vendor_id` varchar(255) DEFAULT NULL,
   `email_address` varchar(255) DEFAULT NULL,
   `password` varchar(255) DEFAULT NULL,
   `email_sent` boolean DEFAULT NULL,
   `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
   PRIMARY KEY (`id`)
 );


 CREATE TABLE `vendor_master_util` (
   `vendor_id` varchar(50) NOT NULL,
   `vendor_name` varchar(255) DEFAULT NULL,
   `vendor_type` varchar(100) DEFAULT NULL,
   `contact_number` varchar(20) DEFAULT NULL,
   `email_address` varchar(255) DEFAULT NULL,
   `registered_platform` boolean DEFAULT NULL,
   `pfms_vendor_code` varchar(100) DEFAULT NULL,
   `primary_business` varchar(255) DEFAULT NULL,
   `address` text,
   `landline_number` varchar(20) DEFAULT NULL,
   `mobile_number` varchar(20) DEFAULT NULL,
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
   PRIMARY KEY (`vendor_id`)
 );

CREATE TABLE `vendor_names_for_job_work_material` (
   `id` bigint NOT NULL AUTO_INCREMENT,
   `material_id` bigint NOT NULL,
   `vendor_name` varchar(255) NOT NULL,
   `job_code` varchar(255) DEFAULT NULL,
   `material_code` varchar(255) DEFAULT NULL,
   `work_code` varchar(255) DEFAULT NULL,
   `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
   `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   `indent_id` varchar(255) DEFAULT NULL,
   PRIMARY KEY (`id`),
   KEY `material_id` (`material_id`),
   CONSTRAINT `vendor_names_for_job_work_material_ibfk_1` FOREIGN KEY (`material_id`) REFERENCES `material_details` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
 );
 CREATE TABLE `vendor_quotation_against_tender` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `tender_id` varchar(255) DEFAULT NULL,
    `vendor_id` varchar(255) DEFAULT NULL,
    `quotation_file_name` varchar(500) DEFAULT NULL,
    `file_type` varchar(50) DEFAULT NULL,
    `created_by` int DEFAULT NULL,
    `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
  );


 CREATE TABLE `work_master` (
   `work_code` varchar(255) NOT NULL,
   `work_sub_category` varchar(255) DEFAULT NULL,
   `mode_of_procurement` varchar(255) DEFAULT NULL,
   `work_description` text,
   `created_by` int DEFAULT NULL,
   `updated_by` varchar(255) DEFAULT NULL,
   `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
   `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   PRIMARY KEY (`work_code`)
 );






 CREATE TABLE `sub_workflow_transition` (
    `subWorkflowTransitionId` int NOT NULL AUTO_INCREMENT,
    `workflowId` int NOT NULL,
    `workflowName` varchar(255) NOT NULL,
    `workflowTransitionId` int NOT NULL,
    `requestId` varchar(255) NOT NULL,
    `createdBy` int NOT NULL,
    `modifiedBy` int DEFAULT NULL,
    `status` varchar(255) NOT NULL,
    `action` varchar(45) DEFAULT NULL,
    `remarks` varchar(255) DEFAULT NULL,
    `actionOn` int DEFAULT NULL,
    `createdDate` datetime DEFAULT NULL,
    `modificationDate` datetime DEFAULT NULL,
    `workflowSequence` int NOT NULL,
    `transitionName` varchar(100) DEFAULT NULL,
    `transitionType` varchar(100) DEFAULT NULL,
    PRIMARY KEY (`subWorkflowTransitionId`)
  );


 CREATE TABLE `transition_condition_master` (
   `conditionId` int NOT NULL AUTO_INCREMENT,
   `workflowId` int NOT NULL,
   `conditionKey` varchar(255) NOT NULL,
   `conditionValue` varchar(255) NOT NULL,
   `createdDate` datetime DEFAULT NULL,
   `createdBy` varchar(45) DEFAULT NULL,
   PRIMARY KEY (`conditionId`)
 );


 CREATE TABLE `transition_master` (
   `transitionId` int NOT NULL AUTO_INCREMENT,
   `transitionName` varchar(255) NOT NULL,
   `workflowId` int NOT NULL,
   `currentRoleId` int NOT NULL,
   `nextRoleId` int DEFAULT NULL,
   `previousRoleId` int DEFAULT NULL,
   `conditionId` int DEFAULT NULL,
   `transitionOrder` int NOT NULL,
   `transitionSubOrder` int NOT NULL,
   `createdDate` datetime DEFAULT NULL,
   `createdBy` varchar(45) DEFAULT NULL,
   PRIMARY KEY (`transitionId`)
 );

 CREATE TABLE `workflow_master` (
    `workflowId` int NOT NULL AUTO_INCREMENT,
    `workflowName` varchar(255) NOT NULL,
    `createdDate` datetime DEFAULT NULL,
    `createdBy` varchar(45) DEFAULT NULL,
    PRIMARY KEY (`workflowId`)
  );


 CREATE TABLE `workflow_transition` (
   `workflowTransitionId` int NOT NULL AUTO_INCREMENT,
   `workflowId` int NOT NULL,
   `workflowName` varchar(255) NOT NULL,
   `transitionId` int NOT NULL,
   `requestId` varchar(255) NOT NULL,
   `createdBy` int NOT NULL,
   `modifiedBy` int DEFAULT NULL,
   `status` varchar(255) NOT NULL,
   `nextAction` varchar(100) DEFAULT NULL,
   `transitionOrder` int NOT NULL,
   `transitionSubOrder` int NOT NULL,
   `action` varchar(45) DEFAULT NULL,
   `remarks` varchar(255) DEFAULT NULL,
   `currentRole` varchar(255) DEFAULT NULL,
   `nextRole` varchar(255) DEFAULT NULL,
   `createdDate` datetime DEFAULT NULL,
   `modificationDate` datetime DEFAULT NULL,
   `workflowSequence` int NOT NULL,
   PRIMARY KEY (`workflowTransitionId`)
 );
CREATE TABLE gi_workflow_status (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    process_id VARCHAR(50),
    sub_process_id INT,
    action VARCHAR(50),
    remarks VARCHAR(500),
    created_by INT,
    create_date DATETIME DEFAULT CURRENT_TIMESTAMP
);



##################
CREATE TABLE `gem_vendor_id_tracker` ( `id` bigint NOT NULL AUTO_INCREMENT, `vendor_id` bigint NOT NULL, `created_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP, `gem_vendor_id` varchar(100) DEFAULT NULL, `vendor_name` varchar(255) DEFAULT NULL,  PRIMARY KEY (`id`), UNIQUE KEY `vendor_id` (`vendor_id`) );
ALTER TABLE indent_creation ADD COLUMN employee_id VARCHAR(50), ADD COLUMN employee_name VARCHAR(100);

ALTER TABLE purchase_order ADD COLUMN gem_contract_file_name VARCHAR(500);


ALTER TABLE purchase_order ADD COLUMN type_of_security VARCHAR(255), ADD COLUMN security_number VARCHAR(255), ADD COLUMN security_date DATE, ADD COLUMN expiry_date DATE;



ALTER TABLE indent_creation ADD COLUMN cancel_status BOOLEAN DEFAULT FALSE, ADD COLUMN cancel_remarks VARCHAR(1000);

ALTER TABLE tender_request ADD COLUMN cancel_status BOOLEAN DEFAULT FALSE, ADD COLUMN cancel_remarks VARCHAR(1000);

ALTER TABLE indent_creation ADD COLUMN buy_back_amount VARCHAR(50);

ALTER TABLE purchase_order ADD COLUMN quotation_number VARCHAR(255), ADD COLUMN quotation_date DATE;

CREATE TABLE iia_freight_forwarder_details ( id BIGINT PRIMARY KEY AUTO_INCREMENT, country_name VARCHAR(255) NOT NULL, freight_forwarder_details TEXT);

CREATE TABLE iia_address_for_consignee_location ( id BIGINT AUTO_INCREMENT PRIMARY KEY, consignee VARCHAR(255),  iia_address TEXT);


ALTER TABLE purchase_order ADD COLUMN additional_terms_and_conditions VARCHAR(500);


CREATE TABLE officer_signature ( id BIGINT AUTO_INCREMENT PRIMARY KEY, officer_name VARCHAR(100) NOT NULL, designation VARCHAR(100), signature_path VARCHAR(255), created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP );

ALTER TABLE service_order ADD COLUMN start_date_amc DATE, ADD COLUMN end_date_amc DATE;

ALTER TABLE purchase_order MODIFY COLUMN warranty VARCHAR(50);

ALTER TABLE purchase_order ADD COLUMN buy_back_amount DECIMAL(18,2);





######not create bellow tables#############

CREATE TABLE demand_and_issue_master ( id BIGINT AUTO_INCREMENT PRIMARY KEY, sender_location_id VARCHAR(255) NOT NULL, status VARCHAR(255), sender_custodian_id INT NOT NULL, receiver_location_id VARCHAR(255) NOT NULL, receiver_custodian_id INT NOT NULL, create_date DATETIME NOT NULL, di_date DATE NOT NULL, created_by INT NOT NULL );

CREATE TABLE demand_and_issue_dtl ( id BIGINT AUTO_INCREMENT PRIMARY KEY, di_id BIGINT,  asset_id INT,  asset_desc VARCHAR(500),   material_code VARCHAR(100),  material_desc VARCHAR(500), quantity DECIMAL(18,2) NOT NULL, receiver_locator_id INT, sender_locator_id INT, unit_price DECIMAL(18,2), depriciation_rate DECIMAL(18,2), book_value DECIMAL(18,2),  CONSTRAINT fk_di_id FOREIGN KEY (di_id) REFERENCES demand_and_issue_master(id) );

CREATE TABLE ohq_consumable_store_stock_entity ( ohq_id BIGINT AUTO_INCREMENT PRIMARY KEY, material_code VARCHAR(255),  locator_id INT,  book_value DECIMAL(19,2), depriciation_rate DECIMAL(19,2),  unit_price DECIMAL(19,2),  custodian_id VARCHAR(255),  quantity DECIMAL(19,2), uom VARCHAR(100), create_date TIMESTAMP NOT NULL );

ALTER TABLE demand_and_issue_dtl ADD COLUMN uom VARCHAR(50);


ALTER TABLE demand_and_issue_master ADD COLUMN issue_date DATE, ADD COLUMN issued_by INT;

ALTER TABLE demand_and_issue_master DROP COLUMN receiver_location_id, DROP COLUMN receiver_custodian_id;

##########


#########16/09/2025#####
ALTER TABLE asset_disposal ADD COLUMN custodian_id VARCHAR(50);

ALTER TABLE asset_disposal ADD COLUMN status VARCHAR(50);

ALTER TABLE asset_disposal ADD COLUMN action VARCHAR(50);

ALTER TABLE asset_disposal_detail ADD COLUMN ohq_id INT, ADD COLUMN locator_id INT, ADD COLUMN book_value DECIMAL(18,2), ADD COLUMN depriciation_rate DECIMAL(18,2), ADD COLUMN unit_price DECIMAL(18,2), ADD COLUMN custodian_id VARCHAR(50), ADD COLUMN po_value DECIMAL(18,2);

ALTER TABLE asset_disposal ADD COLUMN auction_id VARCHAR(50), ADD COLUMN auction_date DATE, ADD COLUMN reserve_price DECIMAL(18,2), ADD COLUMN auction_price DECIMAL(18,2), ADD COLUMN vendor_name VARCHAR(100);

ALTER TABLE asset_disposal_detail ADD COLUMN reason_for_disposal VARCHAR(150);

CREATE TABLE `asset_disposal_auction` ( `auction_id` INT NOT NULL AUTO_INCREMENT, `auction_code` VARCHAR(50) NOT NULL UNIQUE, `auction_date` DATE NOT NULL, `reserve_price` DECIMAL(18,2) DEFAULT NULL, `auction_price` DECIMAL(18,2) DEFAULT NULL, `vendor_name` VARCHAR(100) DEFAULT NULL, `created_by` INT NOT NULL, `created_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (`auction_id`) );

CREATE TABLE `asset_disposal_auction_detail` (
`auction_detail_id` INT NOT NULL AUTO_INCREMENT,
`auction_id` INT NOT NULL,
`disposal_id` INT NOT NULL,
PRIMARY KEY (`auction_detail_id`),
KEY `auction_id_idx` (`auction_id`),
KEY `disposal_id_idx` (`disposal_id`),
CONSTRAINT `fk_auction_detail_auction` FOREIGN KEY (`auction_id`) REFERENCES `asset_disposal_auction`(`auction_id`) ON DELETE CASCADE ON UPDATE CASCADE,  CONSTRAINT `fk_auction_detail_disposal` FOREIGN KEY (`disposal_id`) REFERENCES `asset_disposal`(`disposal_id`) ON DELETE CASCADE ON UPDATE CASCADE );


CREATE TABLE ogp_asset_disposal (
disposal_ogp_id INT PRIMARY KEY AUTO_INCREMENT,
auction_id INT NOT NULL,
auction_code VARCHAR(50) NOT NULL,
auction_date DATE, reserve_price DECIMAL(15,2),
auction_price DECIMAL(15,2), vendor_name VARCHAR(100),
status VARCHAR(50),
created_by INT,
create_date DATETIME DEFAULT CURRENT_TIMESTAMP );

CREATE TABLE ogp_asset_disposal_detail (
ogp_disposal_detail_id INT PRIMARY KEY AUTO_INCREMENT,
disposal_ogp_id INT NOT NULL,
 disposal_id INT NOT NULL,
 asset_id INT NOT NULL,
 asset_desc VARCHAR(255),
 disposal_quantity DECIMAL(10,2),
 locator_id INT,
 book_value DECIMAL(15,2),
 depriciation_rate DECIMAL(5,2),
 unit_price DECIMAL(15,2),
 custodian_id VARCHAR(50),
 po_value DECIMAL(15,2),
 reason_for_disposal VARCHAR(255),
 disposal_date DATE, location_id VARCHAR(50),
 status VARCHAR(50),
  FOREIGN KEY (disposal_ogp_id) REFERENCES ogp_asset_disposal(disposal_ogp_id) ON DELETE CASCADE);




###payment voucher

INSERT INTO workflow_master (workflowName)
VALUES ('Payment Voucher Workflow');

INSERT INTO transition_master (transitionName, workflowId, currentRoleId, nextRoleId, transitionOrder, transitionSubOrder)
VALUES
('Submitted to Store Purchase Officer', 10, 23, 11, 1, 1),
('Submitted to Billing Section Personnel', 10, 11, 12, 2, 1),
('Submitted to Account Officer', 10, 12, 13, 3, 1),
('Submitted to Administrative Officer', 10, 13, 3, 4, 1),
('Final Submission', 10, 3, NULL, 5, 1);

CREATE TABLE `payment_voucher` ( `id` bigint NOT NULL AUTO_INCREMENT, `payment_voucher_number` varchar(255) DEFAULT NULL, `payment_voucher_date` varchar(50) DEFAULT NULL, `payment_voucher_is_for` varchar(100) DEFAULT NULL, `purchase_order_id` varchar(100) DEFAULT NULL, `grn_number` varchar(100) DEFAULT NULL, `service_order_details` varchar(500) DEFAULT NULL, `payment_voucher_type` varchar(100) NOT NULL, `vendor_name` varchar(255) DEFAULT NULL, `vendor_invoice_number` varchar(255) NOT NULL, `vendor_invoice_date` varchar(50) DEFAULT NULL, `currency` varchar(50) NOT NULL, `exchange_rate` varchar(50) DEFAULT NULL, `status` varchar(100) DEFAULT NULL, `remarks` varchar(500) DEFAULT NULL, `total_amount` decimal(15,2) DEFAULT NULL, `partial_amount` decimal(15,2) DEFAULT NULL, `advance_amount` decimal(15,2) DEFAULT NULL, `paid_amount` decimal(15,2) DEFAULT NULL, `so_id` varchar(50) DEFAULT NULL, `created_by` int DEFAULT NULL, `created_date` datetime DEFAULT NULL, PRIMARY KEY (`id`))
CREATE TABLE `payment_voucher_materials` ( `id` bigint NOT NULL AUTO_INCREMENT, `material_code` varchar(100) DEFAULT NULL, `material_description` varchar(500) DEFAULT NULL, `quantity` decimal(18,2) DEFAULT NULL, `unit_price` decimal(18,2) DEFAULT NULL, `currency` varchar(50) DEFAULT NULL, `exchange_rate` decimal(18,2) DEFAULT NULL, `gst` decimal(5,2) DEFAULT NULL, `payment_voucher_id` bigint DEFAULT NULL,  PRIMARY KEY (`id`),  KEY `fk_payment_voucher` (`payment_voucher_id`), CONSTRAINT `fk_payment_voucher` FOREIGN KEY (`payment_voucher_id`) REFERENCES `payment_voucher` (`id`) ON DELETE CASCADE );


ALTER TABLE indent_creation DROP COLUMN proprietary_and_limited_declaration;

ALTER TABLE indent_creation Add COLUMN proprietary_and_limited_declaration BOOLEAN;ALTER TABLE indent_creation Add COLUMN proprietary_and_limited_declaration BOOLEAN;



ALTER TABLE astrodatabase.grn_master ADD COLUMN custodian_id INT;

ALTER TABLE asset_disposal_detail ADD COLUMN po_id VARCHAR(50), ADD COLUMN po_date DATE, ADD COLUMN serial_no VARCHAR(50), ADD COLUMN model_no VARCHAR(50);


ALTER TABLE gt_dtl ADD COLUMN po_id VARCHAR(50), ADD COLUMN model_no VARCHAR(100), ADD COLUMN serial_no VARCHAR(100), ADD COLUMN reason_for_transfer VARCHAR(255);
ALTER TABLE gt_dtl ADD COLUMN po_id VARCHAR(50), ADD COLUMN model_no VARCHAR(100), ADD COLUMN serial_no VARCHAR(100), ADD COLUMN reason_for_transfer VARCHAR(255);

ALTER TABLE goods_inspection_master ADD COLUMN po_amount DECIMAL(15,2), ADD COLUMN gprn_amount DECIMAL(15,2);

alter table goods_inspection_consumable_detail Add column reject_reason varchar(100);alter table goods_inspection_consumable_detail Add column reject_reason varchar(100);


ALTER TABLE tender_request DROP COLUMN ld_clause,ADD COLUMN ld_clause BOOLEAN DEFAULT FALSE;


ALTER TABLE tender_request ADD COLUMN buy_back BOOLEAN, ADD COLUMN buy_back_amount VARCHAR(255), ADD COLUMN model_number VARCHAR(255), ADD COLUMN serial_number VARCHAR(255), ADD COLUMN date_of_purchase DATE, ADD COLUMN upload_buy_back_file_names VARCHAR(500);


CREATE TABLE employee_id_sequence (id BIGINT AUTO_INCREMENT PRIMARY KEY,employee_id INT);
/////////
ALTER TABLE asset_master ADD COLUMN asset_code VARCHAR(200);

ALTER TABLE goods_inspection_detail ADD COLUMN asset_code VARCHAR(200);

ALTER TABLE grn_material_detail ADD COLUMN asset_code VARCHAR(200);INV1157/158
/////ALTER TABLE grn_material_detail ADD COLUMN ohq_master VARCHAR(200);

ALTER TABLE ohq_master ADD COLUMN asset_code VARCHAR(200);


CREATE TABLE asset_serial (id BIGINT AUTO_INCREMENT PRIMARY KEY, asset_id INT NOT NULL, asset_code VARCHAR(100), serial_no VARCHAR(100) NOT NULL, custodian_id VARCHAR(100), locator_id INT, po_id VARCHAR(100),  created_date DATETIME DEFAULT CURRENT_TIMESTAMP, CONSTRAINT fk_asset_serial_asset  FOREIGN KEY (asset_id) REFERENCES asset_master(asset_id) ON DELETE CASCADE  ON UPDATE CASCADE);

ALTER TABLE gt_dtl ADD COLUMN asset_code VARCHAR(200);

ALTER TABLE ogp_gt_dtl ADD COLUMN asset_code VARCHAR(200);
ALTER TABLE ogp_gt_dtl ADD COLUMN serial_no VARCHAR(200);

ALTER TABLE asset_disposal_detail ADD COLUMN asset_code VARCHAR(200);

ALTER TABLE asset_serial ADD COLUMN status VARCHAR(200);

ALTER TABLE ogp_asset_disposal_detail ADD COLUMN asset_code VARCHAR(200);
ALTER TABLE ogp_asset_disposal_detail ADD COLUMN serial_no VARCHAR(200);

/////////////06/11/25//////////////

ALTER TABLE vendor_master ADD COLUMN status_of_vendor_active_or_debar VARCHAR(200);

ALTER TABLE vendor_master ADD COLUMN reason_for_debar VARCHAR(200);

ALTER TABLE material_master ADD COLUMN status_of_material_active_or_deactive VARCHAR(200);
ALTER TABLE material_master ADD COLUMN reason_for_deactive VARCHAR(200);
ALTER TABLE material_master ADD COLUMN reason_for_deactive VARCHAR(200);



/////////////


ALTER TABLE ogp_detail_rejected_gi ADD COLUMN asset_code VARCHAR(200);
ALTER TABLE payment_voucher
ADD COLUMN tds_amount DECIMAL(18,2),
ADD COLUMN payment_voucher_net_amount DECIMAL(18,2);

