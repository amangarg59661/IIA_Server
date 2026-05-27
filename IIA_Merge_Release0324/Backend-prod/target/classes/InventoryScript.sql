--Inventory Modules
-- // foreign key constraints fro aster table not added yet 
CREATE TABLE gprn_master (
    process_id VARCHAR(50) NOT NULL,
    sub_process_id INT AUTO_INCREMENT PRIMARY KEY,
    po_id VARCHAR(50) NOT NULL,
    location_id VARCHAR(10) NOT NULL,
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
    created_by Varchar(50) NOT NULL,
    updated_by VARCHAR(50),
    create_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (vendor_id) REFERENCES vendor_master(vendor_id) ON UPDATE CASCADE,
    FOREIGN KEY (location_id) REFERENCES location_master(location_code) ON UPDATE CASCADE
    -- FOREIGN KEY (po_id) REFERENCES purchase_order(po_id) ON UPDATE CASCADE ON DELETE CASCADE,
);

CREATE TABLE asset_master(
    asset_id INT AUTO_INCREMENT PRIMARY KEY,
    material_code VARCHAR(50) NOT NULL,
    material_desc VARCHAR(50) NOT NULL,
    asset_desc VARCHAR(50) NOT NULL,
    make_no VARCHAR(50),
    serial_no VARCHAR(50),
    model_no VARCHAR(50),
    init_quantity DECIMAL(10,2),
    unit_price DECIMAL(10,2),
    uom_id VARCHAR(10) NOT NULL,
    depriciation_rate DECIMAL(10,2),
    end_of_life DATE,
    stock_levels DECIMAL(10,2),
    condition_of_goods VARCHAR(100),
    shelf_life VARCHAR(50),
    component_name VARCHAR(50),
    component_id INT,
    create_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by INT NOT NULL,
    updated_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by INT,
    INDEX idx_material_code (material_code),
    INDEX idx_uom (uom_id),
    INDEX idx_material_desc (material_desc),
    FOREIGN KEY (material_code) REFERENCES material_master(material_code) ON UPDATE CASCADE
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
    warranty_terms VARCHAR(100),
    note VARCHAR(100),
    photo_path VARCHAR(100),
    -- FOREIGN KEY (process_id) REFERENCES gprn_master(process_id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (sub_process_id) REFERENCES gprn_master(sub_process_id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (material_code) REFERENCES material_master(material_code) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (uom_id) REFERENCES uom_master(uom_code) ON UPDATE CASCADE
);

CREATE TABLE goods_inspection_master (
    inspection_sub_process_id INT AUTO_INCREMENT PRIMARY KEY,
    gprn_process_id VARCHAR(50) NOT NULL,
    gprn_sub_process_id INT NOT NULL,
    installation_date DATE,
    commissioning_date DATE,
    location_id VARCHAR(10),
    INDEX idx_gprn_process (gprn_process_id),
    INDEX idx_gprn_subprocess (gprn_sub_process_id),
    create_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by INT NOT NULL,
    FOREIGN KEY (location_id) REFERENCES location_master(location_code) ON UPDATE CASCADE,
    FOREIGN KEY (gprn_sub_process_id) REFERENCES gprn_master(sub_process_id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE goods_inspection_detail (
    inspection_detail_id INT AUTO_INCREMENT PRIMARY KEY,
    inspection_sub_process_id INT NOT NULL,
    gprn_sub_process_id INT NOT NULL,
    gprn_process_id INT NOT NULL,
    material_code VARCHAR(50) NOT NULL,
    material_desc VARCHAR(50) NOT NULL,
    asset_id INT,
    installation_report_filename VARCHAR(255),
    received_quantity DECIMAL(10,2) NOT NULL,
    accepted_quantity DECIMAL(10,2) NOT NULL,
    rejected_quantity DECIMAL(10,2) NOT NULL,
    INDEX idx_inspection_subprocess (inspection_sub_process_id),
    INDEX idx_gprn_subprocess (gprn_sub_process_id),
    INDEX idx_material (material_code),
    FOREIGN KEY (inspection_sub_process_id) REFERENCES goods_inspection_master(inspection_sub_process_id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (gprn_sub_process_id) REFERENCES gprn_master(sub_process_id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (asset_id) REFERENCES asset_master(asset_id) ON UPDATE CASCADE
);

CREATE TABLE grv_master (
    gi_sub_process_id INT NOT NULL,
    gi_process_id VARCHAR(50) NOT NULL,
    grv_process_id VARCHAR(50) NOT NULL,
    grv_sub_process_id INT AUTO_INCREMENT PRIMARY KEY,
    date DATE,
    created_by Varchar(50) NOT NULL,
    create_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    location_id VARCHAR(10) NOT NULL,
    INDEX idx_grv_process_id (grv_process_id),
    INDEX idx_gi_sub_process (gi_sub_process_id),
    INDEX idx_date (date),
    FOREIGN KEY (gi_sub_process_id) REFERENCES goods_inspection_master(inspection_sub_process_id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (location_id) REFERENCES location_master(location_code) on update cascade
);

CREATE TABLE grv_material_detail (
    detail_id INT AUTO_INCREMENT PRIMARY KEY,
    grv_process_id VARCHAR(50) NOT NULL,
    grv_sub_process_id INT NOT NULL,
    gi_sub_process_id INT NOT NULL,
    material_code VARCHAR(50) NOT NULL,
    material_desc VARCHAR(50) NOT NULL,
    uom_id VARCHAR(10) NOT NULL,
    rejected_quantity DECIMAL(10,2) NOT NULL,
    return_quantity DECIMAL(10,2) NOT NULL,
    return_type VARCHAR(50) NOT NULL,
    reject_reason VARCHAR(50) NOT NULL,
    INDEX idx_grv_sub_process (grv_sub_process_id),
    INDEX idx_grv_process_id (grv_process_id),
    INDEX idx_material (material_code),
    INDEX idx_return_type (return_type),
    foreign key (grv_sub_process_id) references grv_master(grv_sub_process_id) ON UPDATE CASCADE ON DELETE CASCADE,
    foreign key (uom_id) references uom_master(uom_code) on update cascade,
    foreign key (material_code) references material_master(material_code) on update cascade
);

CREATE TABLE asset_master(
    asset_id INT AUTO_INCREMENT PRIMARY KEY,
    material_code VARCHAR(50) NOT NULL,
    material_desc VARCHAR(50) NOT NULL,
    asset_desc VARCHAR(50) NOT NULL,
    make_no VARCHAR(50),
    serial_no VARCHAR(50),
    model_no VARCHAR(50),
    init_quantity BIGDECIMAL(10,2),
    uom_id VARCHAR(10) NOT NULL,
    component_name VARCHAR(50),
    component_id INT,
    create_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by INT NOT NULL,
    updated_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by INT,
    INDEX idx_material_code (material_code),
    INDEX idx_uom (uom_id),
    INDEX idx_material_desc (material_desc),
    FOREIGN KEY (material_code) REFERENCES material_master(material_code) ON UPDATE CASCADE
);

CREATE TABLE goods_inspection_master (
    inspection_sub_process_id INT AUTO_INCREMENT PRIMARY KEY,
    gprn_process_id VARCHAR(50) NOT NULL,
    gprn_sub_process_id INT NOT NULL,
    installation_date DATE,
    commissioning_date DATE,
    INDEX idx_gprn_process (gprn_process_id),
    INDEX idx_gprn_subprocess (gprn_sub_process_id),
    create_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by INT NOT NULL,
    FOREIGN KEY (location_id) REFERENCES location_master(location_code) ON UPDATE CASCADE,
    FOREIGN KEY (gprn_sub_process_id) REFERENCES gprn_master(sub_process_id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE goods_inspection_detail (
    inspection_detail_id INT AUTO_INCREMENT PRIMARY KEY,
    inspection_sub_process_id INT NOT NULL,
    gprn_sub_process_id INT NOT NULL,
    gprn_process_id INT NOT NULL,
    material_code VARCHAR(50) NOT NULL,
    material_desc VARCHAR(50) NOT NULL,
    asset_id INT,
    installation_report_filename VARCHAR(255),
    received_quantity DECIMAL(10,2) NOT NULL,
    accepted_quantity DECIMAL(10,2) NOT NULL,
    rejected_quantity DECIMAL(10,2) NOT NULL,
    INDEX idx_inspection_subprocess (inspection_sub_process_id),
    INDEX idx_gprn_subprocess (gprn_sub_process_id),
    INDEX idx_material (material_code),
    FOREIGN KEY (inspection_sub_process_id) REFERENCES goods_inspection_master(inspection_sub_process_id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (gprn_sub_process_id) REFERENCES gprn_master(sub_process_id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (asset_id) REFERENCES asset_master(asset_id) ON UPDATE CASCADE,
    FOREIGN KEY (material_code) REFERENCES material_master(material_code) ON UPDATE CASCADE
);

CREATE TABLE grn_master(
    grn_process_id VARCHAR(50) NOT NULL,
    grn_sub_process_id INT AUTO_INCREMENT PRIMARY KEY,
    gi_process_id VARCHAR(50),
    gi_sub_process_id INT,
    grn_type VARCHAR(10),
    igp_process_id VARCHAR(50),
    igp_sub_process_id INT,
    grn_date DATE,
    installation_date DATE,
    commissioning_date DATE,
    created_by VARCHAR(50) NOT NULL,
    system_created_by INT NOT NULL,
    create_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    location_id VARCHAR(10) NOT NULL,

    FOREIGN KEY (location_id) REFERENCES location_master(location_code) ON UPDATE CASCADE,
    FOREIGN KEY (gi_sub_process_id) REFERENCES goods_inspection_master(inspection_sub_process_id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (igp_sub_process_id) REFERENCES igp_master(igp_sub_process_id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE grn_material_detail(
    detail_id INT AUTO_INCREMENT PRIMARY KEY,
    grn_process_id VARCHAR(50) NOT NULL,
    grn_sub_process_id INT NOT NULL,
    gi_sub_process_id INT NOT NULL,
    igp_sub_process_id INT NOT NULL,
    quantity DECIMAL(10, 2) NOT NULL,
    asset_id INT NOT NULL,
    locator_id INT NOT NULL,
    book_value DECIMAL(10,2) NOT NULL,
    depriciation_rate DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (asset_id) REFERENCES asset_master(asset_id) ON UPDATE CASCADE,
    FOREIGN KEY (grn_sub_process_id) REFERENCES grn_master(grn_sub_process_id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (gi_sub_process_id) REFERENCES goods_inspection_master(inspection_sub_process_id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (locator_id) references locator_master(locator_id) on update cascade,
    FOREIGN KEY (igp_sub_process_id) REFERENCES igp_master(igp_sub_process_id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE ohq_master (
    ohq_id INT AUTO_INCREMENT PRIMARY KEY,
    asset_id INT NOT NULL,
    locator_id INT NOT NULL,
    book_value DECIMAL(10,2) NOT NULL,
    depriciation_rate DECIMAL(10,2) NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    quantity DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (asset_id) REFERENCES asset_master(asset_id) ON UPDATE CASCADE,
    -- FOREIGN KEY (locator_id) REFERENCES locator_master(locator_id) ON UPDATE CASCADE
);

CREATE TABLE issue_note_master (
    issue_note_id INT AUTO_INCREMENT PRIMARY KEY,
    issue_note_type ENUM('Returnable', 'Non Returnable'),
    issue_date DATE NOT NULL,
    consignee_detail VARCHAR(50),
    indentor_name VARCHAR(50),
    field_station VARCHAR(50),
    created_by INT NOT NULL,
    create_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    location_id VARCHAR(10) NOT NULL,
    FOREIGN KEY (location_id) REFERENCES location_master(location_code) ON UPDATE CASCADE
);

CREATE TABLE issue_note_detail (
    detail_id INT AUTO_INCREMENT PRIMARY KEY,
    issue_note_id INT NOT NULL,
    asset_id INT NOT NULL,
    locator_id INT NOT NULL,
    quantity DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (issue_note_id) REFERENCES issue_note_master(issue_note_id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (asset_id) REFERENCES asset_master(asset_id) ON UPDATE CASCADE,
    FOREIGN KEY (locator_id) REFERENCES locator_master(locator_id) ON UPDATE CASCADE
);


CREATE TABLE igp_master(
    igp process_id VARCHAR(50) NOT NULL,
    igp_sub_process_id INT AUTO_INCREMENT PRIMARY KEY,
    ogp_sub_process_id INT NOT NULL,
    igp_date DATE NOT NULL,
    location_id VARCHAR(10) NOT NULL,
    created_by INT NOT NULL,
    create_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (location_id) REFERENCES location_master(location_code) ON UPDATE CASCADE,
    FOREIGN KEY (ogp_sub_process_id) REFERENCES ogp_master(ogp_sub_process_id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE igp_detail(
    detail_id INT AUTO_INCREMENT PRIMARY KEY,
    igp_process_id VARCHAR(50) NOT NULL,
    igp_sub_process_id INT NOT NULL,
    ogp_sub_process_id  INT,
    asset_id INT NOT NULL,
    locator_id INT NOT NULL,
    quantity DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (igp_sub_process_id) REFERENCES igp_master(igp_sub_process_id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (asset_id) REFERENCES asset_master(asset_id) ON UPDATE CASCADE,
    FOREIGN KEY (locator_id) REFERENCES locator_master(locator_id) ON UPDATE CASCADE
    FOREIGN KEY (ogp_sub_process_id) REFERENCES ogp_master(ogp_sub_process_id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE ogp_master(
    ogp_process_id VARCHAR(50) NOT NULL,
    ogp_type VARCHAR(20),
    ogp_sub_process_id INT AUTO_INCREMENT PRIMARY KEY,
    issue_note_id INT NOT NULL,
    ogp_date DATE NOT NULL,
    location_id VARCHAR(10) NOT NULL,
    created_by INT NOT NULL,
    create_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (location_id) REFERENCES location_master(location_code) ON UPDATE CASCADE,
    FOREIGN KEY (issue_note_id) REFERENCES issue_note_master(issue_note_id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE ogp_detail(
    detail_id INT AUTO_INCREMENT PRIMARY KEY,
    ogp_process_id VARCHAR(50) NOT NULL,
    ogp_sub_process_id INT NOT NULL,
    asset_id INT NOT NULL,
    locator_id INT NOT NULL,
    quantity DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (ogp_sub_process_id) REFERENCES ogp_master(ogp_sub_process_id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (issue_note_id) REFERENCES issue_note_master(issue_note_id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (asset_id) REFERENCES asset_master(asset_id) ON UPDATE CASCADE,
    FOREIGN KEY (locator_id) REFERENCES locator_master(locator_id) ON UPDATE CASCADE
);


SELECT
    inm.issue_note_id AS IssueID,
    inm.issue_date AS DateOfIssue,
    am.material_desc AS ItemDescription,
    mm.category AS Category,
    mm.sub_category AS SubCategory,
    ind.quantity AS QuantityIssued,
    am.uom_id AS UnitOfMeasure,
    inm.location_id As Location,
    NULL AS IssuedTo,
    NULL AS IssuedBy,
    NULL AS Purpose
FROM issue_note_master inm
JOIN issue_note_detail ind ON inm.issue_note_id = ind.issue_note_id
JOIN asset_master am ON ind.asset_id = am.asset_id
JOIN material_master mm ON am.material_code = mm.material_code;


CREATE TABLE asset_disposal(
    disposal_id INT AUTO_INCREMENT PRIMARY KEY,
    disposal_date DATE NOT NULL,
    created_by INT NOT NULL,
    create_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    location_id VARCHAR(10) NOT NULL,
    vendor_id VARCHAR(10),
    FOREIGN KEY (location_id) REFERENCES location_master(location_code) ON UPDATE CASCADE
    -- FOREIGN KEY (vendor_id) REFERENCES vendor_master(vendor_id) ON UPDATE CASCADE
);

CREATE TABLE asset_disposal_detail(
    disposal_detail_id INT AUTO_INCREMENT PRIMARY KEY,
    disposal_id INT NOT NULL,
    asset_id INT NOT NULL,
    asset_desc VARCHAR(50) NOT NULL,
    disposal_quantity DECIMAL(10,2) NOT NULL,
    disposal_category VARCHAR(50) NOT NULL,
    disposal_mode VARCHAR(50) NOT NULL,
    sales_note_filename VARCHAR(255),
    FOREIGN KEY (disposal_id) REFERENCES asset_disposal(disposal_id) ON UPDATE CASCADE ON DELETE CASCADE
);


CREATE TABLE ogp_master_po (
    ogp_sub_process_id INT AUTO_INCREMENT PRIMARY KEY,
    ogp_type VARCHAR(20),
    po_id VARCHAR(255) NOT NULL,
    ogp_date DATE NOT NULL,
    location_id VARCHAR(255) NOT NULL,
    created_by INT NOT NULL,
    create_date DATETIME NOT NULL
);

CREATE TABLE ogp_po_detail (
    detail_id INT AUTO_INCREMENT PRIMARY KEY,
    ogp_sub_process_id INT NOT NULL,
    material_code VARCHAR(255) NOT NULL,
    material_desc VARCHAR(255) NOT NULL,
    uom_id VARCHAR(255) NOT NULL,
    quantity DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (ogp_sub_process_id) REFERENCES ogp_master_po(ogp_sub_process_id)
);

CREATE TABLE igp_po_detail (
    detail_id INT AUTO_INCREMENT PRIMARY KEY,
    igp_sub_process_id INT NOT NULL,
    material_code VARCHAR(255) NOT NULL,
    material_desc VARCHAR(255) NOT NULL,
    uom_id VARCHAR(255) NOT NULL,
    quantity DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (igp_sub_process_id) REFERENCES igp_master(igp_sub_process_id)
);

----------------------------------------------

ALTER TABLE goods_inspection_detail ADD COLUMN rejection_type VARCHAR(50);

ALTER TABLE goods_inspection_consumable_detail ADD COLUMN rejection_type VARCHAR(50);

ALTER TABLE gprn_master ADD COLUMN indent_id VARCHAR(100);

ALTER TABLE ohq_master ADD COLUMN custodian_id VARCHAR(100);

ALTER TABLE ohq_master_consumable ADD COLUMN custodian_id VARCHAR(100);

CREATE TABLE field_station_master( id INT AUTO_INCREMENT PRIMARY KEY, field_station_name VARCHAR(255) NOT NULL);

CREATE TABLE ogp_master_rejected_gi( ogp_sub_process_id INT AUTO_INCREMENT PRIMARY KEY, ogp_type VARCHAR(20), status VARCHAR(20), gi_id VARCHAR(255), ogp_date DATE, return_date DATE);

ALTER TABLE ogp_master_rejected_gi ADD COLUMN location_id VARCHAR(50);

ALTER TABLE ogp_master_rejected_gi ADD COLUMN created_by VARCHAR(50);

ALTER TABLE ogp_master_rejected_gi ADD COLUMN sender_name VARCHAR(50);

ALTER TABLE ogp_master_rejected_gi ADD COLUMN receiver_name VARCHAR(50);

ALTER TABLE ogp_master_rejected_gi ADD COLUMN receiver_location VARCHAR(50);

CREATE TABLE ogp_detail_rejected_gi( detail_id INT AUTO_INCREMENT PRIMARY KEY, ogp_sub_process_id INT NOT NULL, material_code VARCHAR(255),material_desc VARCHAR(255),asset_id INT,asset_desc VARCHAR(255), rejection_type VARCHAR(50), rejected_quantity DECIMAL(10,2) NOT NULL,FOREIGN KEY (ogp_sub_process_id) REFERENCES ogp_master_rejected_gi(ogp_sub_process_id));

----------------------newest------------

CREATE TABLE igp_material_master ( id BIGINT AUTO_INCREMENT PRIMARY KEY, ogp_id VARCHAR(255), igp_date VARCHAR(20), status VARCHAR(50), igp_type VARCHAR(50), indent_id INT);

ALTER TABLE igp_material_master ADD COLUMN created_by INT;

ALTER TABLE igp_material_master ADD COLUMN create_date DATETIME;

CREATE TABLE igp_material_detail ( id BIGINT AUTO_INCREMENT PRIMARY KEY, material_code VARCHAR(100), category VARCHAR(100), sub_category VARCHAR(100),  material_description VARCHAR(255), uom VARCHAR(20),  estimated_price_with_ccy DECIMAL(10,2), indigenous_or_imported BOOLEAN, quantity DECIMAL(10,2), igp_id BIGINT,  FOREIGN KEY (igp_id)  REFERENCES igp_material_master(id) ON DELETE CASCADE );

alter table asset_master add column igp_id bigint;

alter table igp_material_detail add column asset_id INT;

alter table igp_material_master add column location_id VARCHAR(50);


-----------------goods transfer newest-----------------
CREATE TABLE gt_master (id BIGINT AUTO_INCREMENT PRIMARY KEY,sender_location_id VARCHAR(255) NOT NULL, status VARCHAR(255),sender_custodian_id INT NOT NULL,receiver_location_id VARCHAR(255) NOT NULL,receiver_custodian_id INT NOT NULL,create_date DATETIME NOT NULL,gt_date DATE NOT NULL, created_by INT NOT NULL);

CREATE TABLE gt_dtl ( id BIGINT AUTO_INCREMENT PRIMARY KEY, gt_id BIGINT, asset_id INT, asset_desc VARCHAR(500), material_code VARCHAR(100), material_desc VARCHAR(500), quantity DECIMAL(18,2) NOT NULL, receiver_locator_id INT, sender_locator_id INT, unit_price DECIMAL(18,2), depriciation_rate DECIMAL(5,2), book_value DECIMAL(18,2),FOREIGN KEY (gt_id) REFERENCES gt_master(id) );


--------------- 16 / 08/ 2025 -------
CREATE TABLE ogp_gt_master ( id BIGINT AUTO_INCREMENT PRIMARY KEY, sender_location_id VARCHAR(255) NOT NULL, status VARCHAR(255), sender_custodian_id INT NOT NULL,  receiver_location_id VARCHAR(255) NOT NULL, receiver_custodian_id INT NOT NULL,  create_date DATETIME NOT NULL, gt_date DATE NOT NULL, created_by INT NOT NULL);

CREATE TABLE ogp_gt_dtl ( id BIGINT AUTO_INCREMENT PRIMARY KEY, gt_id BIGINT,  asset_id INT,  asset_desc VARCHAR(500), material_code VARCHAR(100), material_desc VARCHAR(500), quantity DECIMAL(18,6) NOT NULL, receiver_locator_id INT, sender_locator_id INT, unit_price DECIMAL(18,6),  depriciation_rate DECIMAL(18,6),  book_value DECIMAL(18,6));

