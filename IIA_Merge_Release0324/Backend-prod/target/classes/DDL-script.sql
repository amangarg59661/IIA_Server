CREATE TABLE `astrodatabase`.`user_master` (
  `userId` INT NOT NULL AUTO_INCREMENT,
  `password` VARCHAR(100) NOT NULL,
  `userName` VARCHAR(100) NULL,
  `email` VARCHAR(255) NULL,
  `mobileNumber` VARCHAR(10) NULL,
  `createdDate` DATETIME NULL,
  `createdBy` VARCHAR(45) NULL,
  PRIMARY KEY (`userId`));

CREATE TABLE `astrodatabase`.`role_master` (
  `roleId` INT NOT NULL AUTO_INCREMENT,
  `roleName` VARCHAR(100) NULL,
  `createdDate` DATETIME NULL,
  `createdBy` VARCHAR(45) NULL,
  PRIMARY KEY (`roleId`));

CREATE TABLE `astrodatabase`.`user_role_master` (
  `userRoleId` INT NOT NULL AUTO_INCREMENT,
  `userId` INT NOT NULL,
  `roleId` INT NOT NULL,
  `readPermission` boolean NOT NULL,
  `writePermission` boolean NOT NULL,
  `createdDate` DATETIME NULL,
  `createdBy` VARCHAR(45) NULL,
  PRIMARY KEY (`userRoleId`));

CREATE TABLE `astrodatabase`.`workflow_master` (
  `workflowId` INT NOT NULL AUTO_INCREMENT,
  `workflowName` VARCHAR(255) NOT NULL,
  `createdDate` DATETIME NULL,
  `createdBy` VARCHAR(45) NULL,
  PRIMARY KEY (`workflowId`));

CREATE TABLE `astrodatabase`.`state_master` (
  `stateId` INT NOT NULL AUTO_INCREMENT,
  `stateName` VARCHAR(255) NOT NULL,
  `createdDate` DATETIME NULL,
  `createdBy` VARCHAR(45) NULL,
  PRIMARY KEY (`stateId`));

 CREATE TABLE `astrodatabase`.`transition_master` (
  `transitionId` INT NOT NULL AUTO_INCREMENT,
  `transitionName` VARCHAR(255) NOT NULL,
  `workflowId` INT NOT NULL,
  `currentRoleId` INT NOT NULL,
  `nextRoleId` INT NULL,
  `previousRoleId` INT NULL,
  `conditionId` INT NULL,
  `transitionOrder` INT NOT NULL,
  `transitionSubOrder` INT NOT NULL,
  `createdDate` DATETIME NULL,
  `createdBy` VARCHAR(45) NULL,
  PRIMARY KEY (`transitionId`));

  CREATE TABLE `astrodatabase`.`action_master` (
  `actionId` INT NOT NULL AUTO_INCREMENT,
  `actionName` VARCHAR(255) NOT NULL,
  `createdDate` DATETIME NULL,
  `createdBy` VARCHAR(45) NULL,
  PRIMARY KEY (`actionId`));

   CREATE TABLE `astrodatabase`.`workflow_transition` (
  `workflowTransitionId` INT NOT NULL AUTO_INCREMENT,
  `workflowId` INT NOT NULL,
  `workflowName` VARCHAR(255) NOT NULL,
  `transitionId` INT NOT NULL,
  `requestId` VARCHAR(255) NOT NULL,
  `createdBy` INT NOT NULL,
  `modifiedBy` INT NULL,
  `status` VARCHAR(255) NOT NULL,
  `nextAction` VARCHAR(100) NULL,
  `transitionOrder` INT NOT NULL,
  `transitionSubOrder` INT NOT NULL,
  `workflowSequence` INT NOT NULL,
  `createdDate` DATETIME NULL,
  `modificationDate` DATETIME NULL,
  PRIMARY KEY (`workflowTransitionId`));

   CREATE TABLE `astrodatabase`.`transition_condition_master` (
    `conditionId` INT NOT NULL AUTO_INCREMENT,
    `workflowId` INT NOT NULL,
    `conditionKey` VARCHAR(255) NOT NULL,
    `conditionValue` VARCHAR(255) NOT NULL,
    `createdDate` DATETIME NULL,
    `createdBy` VARCHAR(45) NULL,
    PRIMARY KEY (`conditionId`));



--Inventory Modules
CREATE TABLE gprn_master (
    process_id VARCHAR(50) NOT NULL,
    sub_process_id INT AUTO_INCREMENT PRIMARY KEY,
    po_id VARCHAR(50) NOT NULL,
    date DATE,
    challan_no VARCHAR(50) NOT NULL,
    delivery_date DATE NOT NULL,
    vendor_id VARCHAR(50) NOT NULL,
    field_station VARCHAR(50) NOT NULL,
    indentor_name VARCHAR(50) NOT NULL,
    supply_expected_date DATE NOT NULL,
    consignee_detail VARCHAR(100) NOT NULL,
    warranty_years DECIMAL(10,1),
    project VARCHAR(50),
    received_by VARCHAR(50) NOT NULL,
    created_by INT,
    updated_by VARCHAR(50),
    created_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE gprn_material_detail (
    detail_id INT AUTO_INCREMENT PRIMARY KEY,
    process_id VARCHAR(50) NOT NULL,
    sub_process_id INT NOT NULL,
    po_id VARCHAR(50) NOT NULL,
    material_code VARCHAR(50) NOT NULL,
    material_desc VARCHAR(50) NOT NULL,
    uom_id VARCHAR(10) NOT NULL,
    received_quantity DECIMAL(10,2) NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    make_no VARCHAR(50),
    serial_no VARCHAR(50),
    model_no VARCHAR(50),
    warrantyw_terms VARCHAR(100),
    note VARCHAR(100),
    photo_path VARCHAR(100),
    FOREIGN KEY (process_id) REFERENCES gprn_master(process_id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (sub_process_id) REFERENCES gprn_master(sub_process_id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (material_code) REFERENCES material_master(material_code) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (uom_id) REFERENCES uom_master(uom_code) ON UPDATE CASCADE
);
CREATE TABLE material_disposal (
    material_disposal_code VARCHAR(255) primary key,
    disposal_category VARCHAR(255),
    disposal_mode VARCHAR(255),
    vendor_details VARCHAR(255),
    disposal_date DATE,
    current_book_value DECIMAL(19,2),
    edit_reserve_value DECIMAL(19,2),
    final_bid_value DECIMAL(19,2),
    sale_note LONGBLOB,
    sale_note_file_name VARCHAR(255),
    edit_quantity DECIMAL(19,2),
    edit_value_materials DECIMAL(19,2),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    created_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);



CREATE TABLE goods_inspection (
    goods_inspection_no BigInt AUTO_INCREMENT PRIMARY KEY,
	receipt_inspection_no VARCHAR(255), -- Foreign key to Good Provisional Receipt entity
    installation_date varchar(20),
    commissioning_date varchar(20),
    upload_installation_report Blob,
    accepted_quantity INT NOT NULL,
    rejected_quantity INT NOT NULL,
	goods_return_permament_or_replacement VARCHAR(255),
    goods_return_full_or_partial VARCHAR(255),
    goods_return_reason VARCHAR(255),
    material_rejection_advice_sent boolean,
    po_amendment_notified boolean,
    created_by VARCHAR(255),
    updated_by varchar(200),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);



CREATE TABLE goods_return (
    goods_return_id VARCHAR(255) PRIMARY KEY,
    goods_return_note_no VARCHAR(255) ,
    rejected_quantity INT ,
    return_quantity INT,
    type_of_return VARCHAR(100) ,
    reason_of_return TEXT ,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_by varchar(200),
    updated_by varchar(200),
    updated_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE goods_receipt_inspection (
    receipt_inspection_no VARCHAR(255)  PRIMARY KEY,
    installation_date DATE,
    commissioning_date DATE,
    asset_code VARCHAR(255),
    additional_material_description TEXT,
    locator VARCHAR(255),
    print_label_option BOOLEAN DEFAULT FALSE,
    depreciation_rate DOUBLE,
    book_value DOUBLE,
    attach_component_popup VARCHAR(255),
    updated_by VARCHAR(255),
    created_by VARCHAR(255),
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
CREATE TABLE asset (
    asset_code VARCHAR(255) PRIMARY KEY,
    material_code VARCHAR(255),
    description TEXT ,
    uom VARCHAR(50) ,
    make_no VARCHAR(100),
    model_no VARCHAR(100),
    serial_no VARCHAR(100),
    component_name VARCHAR(255),
    component_code VARCHAR(255),
    quantity INT,
    locator VARCHAR(255) ,
    transaction_history TEXT,
    current_condition VARCHAR(50) ,
    updated_by varchar(200),
    created_by varchar(200),
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE tender_request (
    tender_id VARCHAR(255) PRIMARY KEY,
    title_of_tender VARCHAR(255) NOT NULL,
    opening_date DATE,
    closing_date DATE,
    --indent_id VARCHAR(255),
    indent_materials VARCHAR(200),
    mode_of_procurement VARCHAR(255),
    bid_type VARCHAR(255),
    last_date_of_submission DATE,
    applicable_taxes TEXT,
    consignes_and_billinng_address TEXT,
    inco_terms VARCHAR(255),
    payment_terms VARCHAR(255),
    ld_clause VARCHAR(255),
    applicable_performance VARCHAR(255),
    bid_security_declaration BOOLEAN,
    mll_status_declaration BOOLEAN,
    upload_tender_documents BLOB,
    single_and_multiple_vendors VARCHAR(255),
    upload_general_terms_and_conditions BLOB,
    upload_specific_terms_and_conditions BLOB,
    pre_bid_disscussions TEXT,
    total_tender_value DECIMAL(10,2),
    created_by VARCHAR(200),
    updated_by VARCHAR(255),
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE indent_id (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    indent_id VARCHAR(255),
    tender_id Varchar(255),
	FOREIGN KEY (tender_id) REFERENCES tender_request(tender_id)
);

CREATE TABLE contigency_purchase (
    contigency_id VARCHAR(255) PRIMARY KEY,
    vendors_name VARCHAR(255),
    vendors_invoice_no VARCHAR(255),
    Date DATE,
    material_code VARCHAR(255),
    material_description VARCHAR(255),
    quantity DECIMAL(15, 2),
    unit_price DECIMAL(15, 2),
    remarks_for_purchase VARCHAR(255),
    amount_to_be_paid DECIMAL(15, 2),
    upload_copy_of_invoice BLOB,
    predifined_purchase_statement VARCHAR(255),
    project_detail VARCHAR(255),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE indent_creation (
    indent_id VARCHAR(255) PRIMARY KEY,
    indentor_name VARCHAR(255) NOT NULL,
    indentor_mobile_no VARCHAR(20),
    indentor_email_address VARCHAR(255),
    consignes_location VARCHAR(255),
    uploading_prior_approvals BLOB,
    project_name VARCHAR(255),
    upload_tender_documents BLOB,
    is_pre_bit_meeting_required BOOLEAN,
    pre_bid_meeting_date DATE,
    pre_bid_meeting_venue VARCHAR(255),
    is_it_a_rate_contract_indent BOOLEAN,
    estimated_rate DECIMAL(10, 2),
    period_of_contract DECIMAL(10, 2),
    single_and_multiple_job VARCHAR(50),
    upload_goi_or_rfp BLOB,
    upload_pac_or_brand_pac BLOB,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE material_details (
    material_code VARCHAR(255) PRIMARY KEY,
    material_description TEXT,
    quantity DECIMAL(10, 2),
    unit_price DECIMAL(10, 2),
    uom VARCHAR(50),
    total_price DECIMAL(10, 2),
    budget_code VARCHAR(255),
    material_category VARCHAR(255),
    material_sub_category VARCHAR(255),
    material_and_job VARCHAR(255),
    indent_creation_id VARCHAR(255),
    FOREIGN KEY (indent_creation_id) REFERENCES indent_creation(indentor_id) ON DELETE CASCADE
);

CREATE TABLE purchase_order (
    po_id VARCHAR(255) PRIMARY KEY,
    tender_id VARCHAR(255),
    indent_id VARCHAR(255),
    warranty DECIMAL(10, 2),
    consignes_address VARCHAR(255),
    billing_address VARCHAR(255),
    delivery_period DECIMAL(10, 2),
    if_ld_clause_applicable BOOLEAN,
    inco_terms VARCHAR(255),
    payment_terms VARCHAR(255),
    vendor_name VARCHAR(255),
    vendor_address VARCHAR(255),
    applicable_pbg_to_be_submitted VARCHAR(255),
    transporter_and_freight_for_warder_details VARCHAR(255),
    vendor_account_number VARCHAR(255),
    vendors_zfsc_code VARCHAR(255),
    vendor_account_name VARCHAR(255),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
drop table purchase_order_attributes
CREATE TABLE purchase_order_attributes (
    material_code VARCHAR(255) PRIMARY KEY,
    material_description VARCHAR(255),
    quantity DECIMAL(10, 2),
    rate DECIMAL(10, 2),
    currency VARCHAR(255),
    exchange_rate DECIMAL(10, 2),
    gst DECIMAL(10, 2),
    duties DECIMAL(10, 2),
    freight_charge DECIMAL(10, 2),
    budget_code VARCHAR(255),
    purchase_order_id VARCHAR(255),
    FOREIGN KEY (purchase_order_id) REFERENCES purchase_order(po_id)
);


CREATE TABLE service_order (
    so_id VARCHAR(255) PRIMARY KEY,
    tender_id VARCHAR(255),
    consignes_address VARCHAR(255),
    billing_address VARCHAR(255),
    job_completion_period DECIMAL(10, 2),
    if_ld_clause_applicable BOOLEAN,
    inco_terms VARCHAR(255),
    payment_terms VARCHAR(255),
    vendor_name VARCHAR(255),
    vendor_address VARCHAR(255),
    applicable_pbg_to_be_submitted VARCHAR(255),
    vendors_account_no VARCHAR(255),
    vendors_zrsc_code VARCHAR(255),
    vendors_account_name VARCHAR(255),
    created_by VARCHAR(200),
    updated_by VARCHAR(200),
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE service_order_material (
    material_code VARCHAR(255) PRIMARY KEY,
    material_description VARCHAR(255),
    quantity DECIMAL(10, 2),
    rate DECIMAL(10, 2),
    exchange_rate DECIMAL(10, 2),
    currency VARCHAR(50),
    gst DECIMAL(10, 2),
    duties DECIMAL(10, 2),
    budget_code VARCHAR(255),
    service_order_id VARCHAR(255),
    FOREIGN KEY (service_order_id) REFERENCES service_order(so_id) ON DELETE CASCADE
);

CREATE TABLE work_order (
    wo_id VARCHAR(255) PRIMARY KEY,
    tender_id VARCHAR(255),
    consignes_address VARCHAR(255),
    billing_address VARCHAR(255),
    job_completion_period DECIMAL(10, 2),
    if_ld_clause_applicable BOOLEAN,
    inco_terms VARCHAR(255),
    payment_terms VARCHAR(255),
    vendor_name VARCHAR(255),
    vendor_address VARCHAR(255),
    applicable_pbg_to_be_submitted VARCHAR(255),
    vendors_account_no VARCHAR(255),
    vendors_zrsc_code VARCHAR(255),
    vendors_account_name VARCHAR(255),
    created_by VARCHAR(200),
    updated_by VARCHAR(200),
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE work_order_material (
    work_code VARCHAR(255) PRIMARY KEY,
    work_description VARCHAR(255),
    quantity DECIMAL(10, 2),
    rate DECIMAL(10, 2),
    exchange_rate DECIMAL(10, 2),
    currency VARCHAR(50),
    gst DECIMAL(10, 2),
    duties DECIMAL(10, 2),
    budget_code VARCHAR(255),
    work_order_id VARCHAR(255),
    FOREIGN KEY (work_order_id) REFERENCES work_order(wo_id) ON DELETE CASCADE
);
--Masters Tables
CREATE TABLE material_master (
    material_code VARCHAR(50) PRIMARY KEY,
    category VARCHAR(100),
    sub_category VARCHAR(100),
    description TEXT,
    uom VARCHAR(50),
    mode_of_procurement VARCHAR(100),
    end_of_life VARCHAR(50),
    depreciation_rate DECIMAL(10, 2),
    stock_levels_min DECIMAL(10, 2),
    stock_levels_max DECIMAL(10, 2),
    re_order_level DECIMAL(10, 2),
    condition_of_goods VARCHAR(100),
    shelf_life VARCHAR(50),
    upload_image LONGBLOB,
    indigenous_or_imported BOOLEAN,
    updated_by varchar(200),
    created_by varchar(200),
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE project_master (
    project_code VARCHAR(255) PRIMARY KEY,
    project_name_description VARCHAR(255),
    financial_year VARCHAR(20),
    allocated_amount DECIMAL(15, 2),
    department_division VARCHAR(255),
    budget_type VARCHAR(255),
    start_date DATE,
    end_date DATE,
    remarks_notes TEXT,
    project_head VARCHAR(255),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

drop table job_master
CREATE TABLE job_master (
    job_code VARCHAR(255) PRIMARY KEY,
    category VARCHAR(255),
    job_description TEXT,
    asset_id VARCHAR(255),
    uom VARCHAR(50),
    value DECIMAL(15, 2),
    mode_of_procurement VARCHAR(255),
	created_by INT,
    updated_by VARCHAR(255),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
select *from uom_master
CREATE TABLE uom_master (
    uom_code VARCHAR(50) PRIMARY KEY,
    uom_name VARCHAR(255),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE location_master (
    location_code VARCHAR(10) PRIMARY KEY,
    location_name VARCHAR(255),
    address TEXT,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
CREATE TABLE vendor_master (
    vendor_id VARCHAR(250) PRIMARY KEY,
    vendor_type VARCHAR(50),
    vendor_name VARCHAR(100),
    contact_no VARCHAR(20),
    email_address VARCHAR(100),
    registered_platform BOOLEAN,
    pfms_vendor_code VARCHAR(20),
    primary_business VARCHAR(50),
    address TEXT,
    landline VARCHAR(20),
    mobile_no VARCHAR(20),
    fax VARCHAR(20),
    pan_no VARCHAR(20),
    gst_no VARCHAR(20),
    bank_name VARCHAR(50),
    account_no VARCHAR(20),
    ifsc_code VARCHAR(15),
    purchase_history TEXT,
    status VARCHAR(10),
    remarks Varchar(255),
    updated_by varchar(200),
    created_by LONG,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE employee_department_master (
    employee_id varchar(100) PRIMARY KEY,
    employee_name VARCHAR(100),
    department_name VARCHAR(100),
    designation VARCHAR(50),
    contact_details VARCHAR(100),
    updated_by varchar(200),
    created_by varchar(200),
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE vendor_names_for_job_work_material (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    vendor_name VARCHAR(255) NOT NULL,
    job_code VARCHAR(50),
    material_code VARCHAR(50),
    work_code VARCHAR(50),
    FOREIGN KEY (job_code) REFERENCES job_master(job_code) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (material_code) REFERENCES material_master(material_code) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (work_code) REFERENCES work_master(work_code) ON UPDATE CASCADE ON DELETE CASCADE
);
  CREATE TABLE work_master (
    work_code VARCHAR(255) PRIMARY KEY,
    work_sub_category VARCHAR(255),
    mode_of_procurement VARCHAR(255),
    work_description TEXT,
    created_by INT,
    updated_by VARCHAR(255),
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE vendor_master_util (
    vendor_id VARCHAR(50) PRIMARY KEY,
    vendor_name VARCHAR(255),
    vendor_type VARCHAR(100),
    contact_number VARCHAR(20),
    email_address VARCHAR(255),
    registered_platform BOOLEAN,
    pfms_vendor_code VARCHAR(100),
    primary_business VARCHAR(255),
    address TEXT,
    landline_number VARCHAR(20),
    mobile_number VARCHAR(20),
    fax_number VARCHAR(50),
    pan_number VARCHAR(50),
    gst_number VARCHAR(50),
    bank_name VARCHAR(255),
    account_number VARCHAR(50),
    ifsc_code VARCHAR(50),
    approval_status ENUM('APPROVED', 'REJECTED', 'AWAITING_APPROVAL', 'CHANGE_REQUEST'),
    comments TEXT
);

CREATE TABLE material_master_util (
    material_code VARCHAR(255) PRIMARY KEY,
    category VARCHAR(255),
    sub_category VARCHAR(255),
    description TEXT,
    uom VARCHAR(50),
    unit_price DECIMAL(15,2),
    currency VARCHAR(10),
    estimated_price_with_ccy DECIMAL(15,2),
    upload_image_name VARCHAR(255),
    indigenous_or_imported BOOLEAN,
    approval_status ENUM('APPROVED', 'REJECTED', 'AWAITING_APPROVAL', 'CHANGE_REQUEST'),
    comments TEXT,
    created_by INT,
    updated_by VARCHAR(255),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE material_status(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    material_code VARCHAR(255),
    status VARCHAR(50) ,
    action varchar(200),
    comments TEXT,
    created_by INT ,
    updated_by INT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (material_code) REFERENCES material_master_util(material_code)
);






