CREATE TABLE IF NOT EXISTS tender_committee_vendor_decision (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    tender_id         VARCHAR(50)  NOT NULL,
    vendor_id         VARCHAR(50)  NOT NULL,
    committee_user_id INT          NOT NULL,
    member_name       VARCHAR(255),
    decision          VARCHAR(20),
    remarks           VARCHAR(1000),
    phase             VARCHAR(20)  NOT NULL,
    decision_date     DATETIME,
    created_date      DATETIME     NOT NULL,
    updated_date      DATETIME,
    created_by        VARCHAR(50),
    updated_by        VARCHAR(50),
    UNIQUE KEY uk_tender_vendor_member_phase (tender_id, vendor_id, committee_user_id, phase)
);
