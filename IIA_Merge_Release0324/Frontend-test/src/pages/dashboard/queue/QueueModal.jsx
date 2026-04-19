import React from "react";
import {
  Modal,
  Typography,
  Row,
  Col,
  Tag,
  Spin,
  Collapse,
  Divider,
  Empty,
  Button,
  Table,
} from "antd";
import {
  AuditOutlined,
  BarsOutlined,
  CalendarOutlined,
  FilePdfOutlined,
  FileTextOutlined,
  HistoryOutlined,
  InfoCircleOutlined,
  ProfileOutlined,
  ProjectOutlined,
  ShopOutlined,
  ShoppingOutlined,
  SolutionOutlined,
  ToolOutlined,DollarOutlined
} from "@ant-design/icons";
import QueueHistory from "./QueueHistory";
import MaterialHistory from "./MaterialIndentHistory";
import { baseURL } from '../../../App';
import ProjectBudgetDisplay from '../../../components/ProjectBudgetDisplay';



const QueueModal = ({
  modalVisible,
  setModalVisible,
  selectedRecord,
  detailsData,
  historyVisible,
  setHistoryVisible,
  materialHistoryVisible,
  setMaterialHistoryVisible,
  selectedMaterialCode,
  setSelectedMaterialCode,
}) => {

  return (
    <Modal
      title={
        <div className="flex items-center justify-between">
          <span>
            {`${selectedRecord?.workflowName || "Details"} - ${
              selectedRecord?.requestId || "N/A"
            }`}
          </span>
          <Button
            type="link"
            icon={<HistoryOutlined />}
            onClick={() => setHistoryVisible(true)}
          >
            View History
          </Button>
        </div>
      }
      open={modalVisible}
      onCancel={() => setModalVisible(false)}
      footer={null}
      width={1000}
      className="custom-modal"
      styles={{ body: { padding: "24px 24px 8px" } }}
    >
      {detailsData ? (
        <>
          <style>{`
        .custom-modal .ant-modal-title { font-size: 18px; font-weight: 600; }
        .detail-section { margin-bottom: 24px; padding: 16px; border: 1px solid #f0f0f0; border-radius: 8px; }
        .detail-item { margin-bottom: 12px; font-size: 14px; }
        .detail-item strong { display: inline-block; width: 220px; color: rgba(0, 0, 0, 0.85); }
        .section-title { margin: 16px 0; font-size: 16px; font-weight: 500; }
        .ant-table-thead > tr > th { background-color: #fafafa; font-weight: 600; }
        .amount { font-weight: 500; color: #1890ff; }
      `}</style>

          {parseInt(selectedRecord?.workflowId, 10) === 1 && (
            <div>
              <div className="detail-section">
                <Typography.Title level={5} className="section-title">
                  <InfoCircleOutlined /> Indent Details
                </Typography.Title>
                <Row gutter={24}>
                  <Col span={12}>
                    <div className="detail-item">
                      <strong>Indentor Name:</strong> {detailsData.indentorName}
                    </div>
                    <div className="detail-item">
                      <strong>Email:</strong> {detailsData.indentorEmailAddress}
                    </div>
                    <div className="detail-item">
                      <strong>Mobile No:</strong> {detailsData.indentorMobileNo}
                    </div>
                  </Col>
                  <Col span={12}>
                    <div className="detail-item">
                      <strong>Project Name:</strong> {detailsData.projectName}
                    </div>
                    {detailsData.projectCode && (
                      <div className="detail-item">
                        <strong>Project Code:</strong> {detailsData.projectCode}
                      </div>
                    )}
                    <div className="detail-item">
                      <strong>Location:</strong> {detailsData.consignesLocation}
                    </div>
                    <div className="detail-item">
                      <strong>Technical Specs:</strong>
                      {detailsData.technicalSpecificationsFileName
                        ? detailsData.technicalSpecificationsFileName
                            .split(",")
                            .map((fileName, index) => (
                              <div key={index}>
                                <a
                                  href={`${baseURL}/file/view/Indent/${fileName.trim()}`}
                                  target="_blank"
                                  rel="noopener noreferrer"
                                >
                                  {fileName.trim()} (View)
                                </a>
                                {index <
                                  detailsData.technicalSpecificationsFileName.split(
                                    ", "
                                  ).length -
                                    1 && ", "}
                              </div>
                            ))
                        : "N/A"}
                    </div>
                    <div className="detail-item">
                      <strong>Prior Approvals:</strong>
                      {detailsData.uploadingPriorApprovalsFileName
                        ? detailsData.uploadingPriorApprovalsFileName
                            .split(",")
                            .map((fileName, index) => (
                              <div key={index}>
                                <a
                                  href={`${baseURL}/file/view/Indent/${fileName.trim()}`}
                                  target="_blank"
                                  rel="noopener noreferrer"
                                >
                                  {fileName.trim()} (View)
                                </a>
                                {index <
                                  detailsData.uploadingPriorApprovalsFileName.split(
                                    ", "
                                  ).length -
                                    1 && ", "}
                              </div>
                            ))
                        : "N/A"}
                    </div>
                    <div className="detail-item">
                      <strong>Draft EOI/RFP:</strong>
                      {detailsData.draftEOIOrRFPFileName
                        ? detailsData.draftEOIOrRFPFileName
                            .split(",")
                            .map((fileName, index) => (
                              <div key={index}>
                                <a
                                  href={`${baseURL}/file/view/Indent/${fileName.trim()}`}
                                  target="_blank"
                                  rel="noopener noreferrer"
                                >
                                  {fileName.trim()} (View)
                                </a>
                                {index <
                                  detailsData.draftEOIOrRFPFileName.split(", ")
                                    .length -
                                    1 && ", "}
                              </div>
                            ))
                        : "N/A"}
                    </div>
                    <div className="detail-item">
                      <strong>Total Price:</strong> ₹
                      {detailsData.totalPriceOfAllMaterials?.toFixed(2)}
                    </div>
                  </Col>
                </Row>
              </div>

              {/* Project Budget Display */}
              {detailsData.projectCode && (
                <div style={{ marginBottom: '16px' }}>
                  <ProjectBudgetDisplay
                    projectCode={detailsData.projectCode}
                    indentAmount={detailsData.totalPriceOfAllMaterials}
                  />
                </div>
              )}

              {detailsData.isPreBidMeetingRequired && (
                <div className="detail-section">
                  <Typography.Title level={5} className="section-title">
                    <CalendarOutlined /> Pre-Bid Meeting
                  </Typography.Title>
                  <Row gutter={24}>
                    <Col span={12}>
                      <div className="detail-item">
                        <strong>Tentative Meeting Date:</strong> {detailsData.preBidMeetingDate}
                      </div>
                    </Col>
                    <Col span={12}>
                      <div className="detail-item">
                        <strong>Tentative Meeting Location:</strong> {detailsData.preBidMeetingVenue}
                      </div>
                    </Col>
                  </Row>
                </div>
              )} {detailsData.cancelStatus && (
                <div className="detail-section">
                  <Typography.Title level={5} className="section-title">
                    <CalendarOutlined /> Cancel Indent
                  </Typography.Title>
                  <Row gutter={24}>
                   
                    <Col span={12}>
                      <div className="detail-item">
                        <strong>Cancel Remarks:</strong> {detailsData.cancelRemarks}
                      </div>
                    </Col>
                  </Row>
                </div>
              )}

              {detailsData.indentType === 'job' ? (
              <div className="detail-section">
                <Typography.Title level={5} className="section-title">
                  <BarsOutlined /> Job / Service Details
                </Typography.Title>
                <Table
                  dataSource={detailsData.jobDetails}
                  pagination={false}
                  bordered
                  scroll={{ x: true }}
                  rowKey="jobCode"
                  columns={[
                    { title: "Job Code", dataIndex: "jobCode", width: 120 },
                    { title: "Job Description", dataIndex: "jobDescription", ellipsis: true },
                    { title: "Category", dataIndex: "category", ellipsis: true },
                    { title: "Sub Category", dataIndex: "subCategory", ellipsis: true },
                    { title: "UOM", dataIndex: "uom", width: 100 },
                    { title: "Quantity", dataIndex: "quantity", align: "right" },
                    {
                      title: "Estimated Price",
                      dataIndex: "estimatedPrice",
                      align: "right",
                      render: (text) => text != null ? `₹${Number(text).toFixed(2)}` : "N/A",
                    },
                    {
                      title: "Total Price",
                      dataIndex: "totalPrice",
                      align: "right",
                      render: (text) => (
                        <span style={{ fontWeight: 500 }}>
                          {text != null ? `₹${Number(text).toFixed(2)}` : "N/A"}
                        </span>
                      ),
                    },
                    { title: "Currency", dataIndex: "currency", align: "right" },
                    { title: "Origin", dataIndex: "origin", width: 120 },
                    { title: "Budget Code", dataIndex: "budgetCode", width: 120 },
                    { title: "Mode Of Procurement", dataIndex: "modeOfProcurement", ellipsis: true },
                    {
                      title: "Vendor Names",
                      dataIndex: "vendorNames",
                      ellipsis: true,
                      render: (text) => text || "N/A",
                    },
                    {
                      title: "Action",
                      dataIndex: "jobCode",
                      width: 130,
                      render: (code) => (
                        <Button
                          type="link"
                          icon={<HistoryOutlined />}
                          onClick={() => {
                            setSelectedMaterialCode(code);
                            setMaterialHistoryVisible(true);
                          }}
                        >
                          View Job History
                        </Button>
                      ),
                    },
                  ]}
                />
              </div>
              ) : (
              <div className="detail-section">
                <Typography.Title level={5} className="section-title">
                  <BarsOutlined /> Material Details
                </Typography.Title>
                <Table
                  dataSource={detailsData.materialDetails}
                  pagination={false}
                  bordered
                  scroll={{ x: true }}
                  rowKey="materialCode"
                  columns={[
                    {
                      title: "Material Code",
                      dataIndex: "materialCode",
                      width: 120,
                    },
                    {
                      title: "Description",
                      dataIndex: "materialDescription",
                      ellipsis: true,
                    }, {
                      title: "Material Category",
                      dataIndex: "materialCategory",
                      ellipsis: true,
                    }, {
                      title: "Material SubCategory",
                      dataIndex: "materialSubCategory",
                      ellipsis: true,
                    },
                    {
                      title: "Quantity",
                      dataIndex: "quantity",
                      align: "right",
                    },
                    {
                      title: "Unit Price",
                      dataIndex: "unitPrice",
                      align: "right",
                      render: (text) => `${text?.toFixed(2)}`,
                    },
                    {
                      title: "Currency",
                      dataIndex: "currency",
                      align: "right",
                    },
                    {
                      title: "Total Price",
                      dataIndex: "totalPrice",
                      align: "right",
                      render: (text) => (
                        <span style={{ fontWeight: 500 }}>
                          ₹{text?.toFixed(2)}
                        </span>
                      ),
                    },
                    { title: "UOM", dataIndex: "uom", width: 100 },
                    {
                      title: "Budget Code",
                      dataIndex: "budgetCode",
                      width: 120,
                    },
                    {
                      title: "Mode Of Procurement",
                      dataIndex: "modeOfProcurement",
                      ellipsis: true,
                    },
                    {
                      title: "Vendor Names",
                      dataIndex: "vendorNames",
                      width: 120,
                      render: (text) => (text ? text.join(", ") : "N/A"),
                    }, {
                      title: "Action",
                      dataIndex: "materialCode",
                      width: 130,
                      render: (code) => (
                      <Button
                        type="link"
                        icon={<HistoryOutlined />}
                        onClick={() => {
                        setSelectedMaterialCode(code);
                        setMaterialHistoryVisible(true);
                        }}
                        >
                      View Material History
                    </Button>
                  ),
                },

                  ]}
                />
              </div>
              )}
              <div className="detail-section">
              <div className="detail-item">
              <Typography.Title level={5} className="section-title">
                    <ProjectOutlined /> Additional Details
                  </Typography.Title>
                  <Row gutter={24}>
                    <Col span={12}>
                      <strong>Purpose:</strong> {detailsData.purpose || "N/A"}
                    <div className="detail-item">
                      <strong>Quarter:</strong> {detailsData.quarter || "N/A"}
                    </div>
                    <div className="detail-item">
                      <strong>Reason:</strong> {detailsData.reason || "N/A"}
                    </div>
                   <div>
                    <strong>Proprietary And Limited Declaration:</strong>{" "}
                      {detailsData.proprietaryAndLimitedDeclaration === true
                        ? "Yes"
                        : detailsData.proprietaryAndLimitedDeclaration === false
                        ? "No"
                        : "N/A"}
                    </div>
                    <div className="detail-item">
                      <strong>Justification:</strong> {detailsData.proprietaryJustification || "N/A"}
                    </div>
                    </Col>
                    </Row>
                </div>
                </div>
              {detailsData.brandPac && (
                <div className="detail-section">
                  <Typography.Title level={5} className="section-title">
                    <ProjectOutlined /> Brand PAC
                  </Typography.Title>
                  <Row gutter={24}>
                    <Col span={12}>
                      <div className="detail-item">
                        <strong>Brand PAC:</strong>{" "}
                        {String(detailsData.brandPac) || "N/A"}
                      </div>
                    </Col>
                    <Col span={12}>
                      <div className="detail-item">
                        <strong>PAC/Brand PAC:</strong>
                        {detailsData.uploadPACOrBrandPACFileName
                          ? detailsData.uploadPACOrBrandPACFileName
                              .split(",")
                              .map((fileName, index) => (
                                <div key={index}>
                                  <a
                                    href={`${baseURL}/file/view/Indent/${fileName.trim()}`}
                                    target="_blank"
                                    rel="noopener noreferrer"
                                  >
                                    {fileName.trim()} (View)
                                  </a>
                                  {index <
                                    detailsData.uploadPACOrBrandPACFileName.split(
                                      ", "
                                    ).length -
                                      1 && ", "}
                                </div>
                              ))
                          : "N/A"}
                      </div>
                      <div className="detail-item">
                        <strong>Brand and Model:</strong>{" "}
                        {detailsData.brandAndModel || "N/A"}
                      </div>
                      <div className="detail-item">
                        <strong>Justification:</strong>{" "}
                        {detailsData.justification || "N/A"}
                      </div>
                    </Col>
                  </Row>
                </div>
              )}
              {detailsData.buyBack && (
                <div className="detail-section">
                  <Typography.Title level={5} className="section-title">
                    <ProjectOutlined /> Buy Back
                  </Typography.Title>
                  <Row gutter={24}>
                    <Col span={12}>
                      <div className="detail-item">
                        <strong>Buy Back:</strong>{" "}
                        {String(detailsData.buyBack) || "N/A"}
                      </div>
                    </Col>
                    <Col span={12}>
                      <div className="detail-item">
                        <strong>Buy Back File:</strong>
    {detailsData.uploadBuyBackFileNames
      ? detailsData.uploadBuyBackFileNames
          .split(",")
          .map((fileName, index, arr) => (
            <div key={index}>
              <a
                href={`${baseURL}/file/view/Indent/${fileName.trim()}`}
                target="_blank"
                rel="noopener noreferrer"
              >
                {fileName.trim()} (View)
              </a>
              {index < arr.length - 1 && ", "}
            </div>
          ))
      : "N/A"}
  </div>

                      <div className="detail-item">
                        <strong>Model Number:</strong>{" "}
                        {detailsData.modelNumber || "N/A"}
                      </div>
                      <div className="detail-item">
                        <strong>Serial Number:</strong>{" "}
                        {detailsData.serialNumber || "N/A"}
                      </div>
                      <div className="detail-item">
                        <strong>Date Of Purchase:</strong> 
                        {detailsData.dateOfPurchase}
                      </div>
                    </Col>
                  </Row>
                </div>
              )}
              {detailsData.isItARateContractIndent && (
                <div className="detail-section">
                  <Typography.Title level={5} className="section-title">
                    <ProjectOutlined /> Rate Contract Indent
                  </Typography.Title>
                  <Row gutter={24}>
                    <Col span={12}>
                      <div className="detail-item">
                        <strong>isItARateContractIndent:</strong>{" "}
                        {String(detailsData.isItARateContractIndent) || "N/A"}
                      </div>
                    </Col>
                    <Col span={12}>
                      <div className="detail-item">
                        <strong>Estimated Rate:</strong>{" "}
                        {detailsData.estimatedRate || "N/A"}
                      </div>
                      <div className="detail-item">
                        <strong>Contract Period(Months):</strong>{" "}
                        {detailsData.periodOfContract || "N/A"}
                      </div>
                      <div className="detail-item">
                        <strong>Job Type:</strong>
                        {detailsData.singleAndMultipleJob || "N/A"}
                      </div>
                    </Col>
                  </Row>
                </div>
              )}
            </div>
          )}

          {parseInt(selectedRecord?.workflowId, 10) === 2 && (
            <div>
              <div className="detail-section">
                <Typography.Title level={5} className="section-title">
                  <ShoppingOutlined /> Contingency Purchase Details
                </Typography.Title>
                <Row gutter={24}>
                  <Col span={12}>
                    <div className="detail-item">
                      <strong>Contingency ID:</strong>{" "}
                      {detailsData.contigencyId || "N/A"}
                    </div>
                    <div className="detail-item">
                      <strong>Vendor Name:</strong>{" "}
                      {detailsData.vendorName || "N/A"}
                    </div>
                    <div className="detail-item">
                      <strong>Invoice No:</strong>{" "}
                      {detailsData.vendorInvoiceNo || "N/A"}
                    </div>
                  </Col>
                  <Col span={12}>
                    <div className="detail-item">
                      <strong>Amount To Be Paid:</strong>
                      <span className="amount">
                        {/*detailsData.amountToBePaid
                          ? `₹${detailsData.amountToBePaid.toFixed(2)}`
                          : "N/A"*/}
                          {detailsData.totalCpValue?.toFixed(2)}
                      </span>
                    </div>
                    <div className="detail-item">
                      <strong>Purchase Statement:</strong>{" "}
                      {detailsData.predifinedPurchaseStatement || "N/A"}
                    </div>
                    <div className="detail-item">
                      <strong>Invoice Copy:</strong>{" "}
                      {detailsData.uploadCopyOfInvoice || "N/A"}
                    </div>
                  </Col>
                </Row>
              </div>
            {/*
              <div className="detail-section">
                <Typography.Title level={5} className="section-title">
                  <ProfileOutlined /> Material Details
                </Typography.Title>
                <Row gutter={24}>
                  <Col span={12}>
                    <div className="detail-item">
                      <strong>Material Code:</strong>{" "}
                      {detailsData.materialCode || "N/A"}
                    </div>
                    <div className="detail-item">
                      <strong>Description:</strong>{" "}
                      {detailsData.materialDescription || "N/A"}
                    </div>
                  </Col>
                  <Col span={12}>
                    <div className="detail-item">
                      <strong>Quantity:</strong> {detailsData.quantity || "N/A"}
                    </div>
                    <div className="detail-item">
                      <strong>Unit Price:</strong>{" "}
                      {detailsData.unitPrice
                        ? `₹${detailsData.unitPrice.toFixed(2)}`
                        : "N/A"}
                    </div>
                  </Col>
                </Row>
              </div>

              */}
               <div className="detail-section">
                <Typography.Title level={5} className="section-title">
                  <BarsOutlined /> Material Details
                </Typography.Title>
                <Table
                  dataSource={detailsData.cpMaterials}
                  pagination={false}
                  bordered
                  scroll={{ x: true }}
                  rowKey="materialCode"
                  columns={[
                    {
                      title: "Material Code",
                      dataIndex: "materialCode",
                      width: 120,
                    },
                    {
                      title: "Description",
                      dataIndex: "materialDescription",
                      ellipsis: true,
                    },
                     {
                      title: "Material Category",
                      dataIndex: "materialCategory",
                      ellipsis: true,
                    },
                     {
                      title: "Material SubCategory",
                      dataIndex: "materialSubCategory",
                      ellipsis: true,
                    },
                    {
                      title: "Quantity",
                      dataIndex: "quantity",
                      align: "right",
                    },
                    {
                      title: "Currency",
                      dataIndex: "currency",
                      align: "right",
                    },
                    {
                      title: "GSt",
                      dataIndex: "gst",
                      align: "right",
                    },
                    {
                      title: "Unit Price",
                      dataIndex: "unitPrice",
                      align: "right",
                      render: (text) => `${text?.toFixed(2)}`,
                    },
                    { title: "UOM", dataIndex: "uom", width: 100 },
                    {
                      title: "Budget Code",
                      dataIndex: "budgetCode",
                      width: 120,
                    }, {
                      title: "Total Price",
                      dataIndex: "totalPrice",
                      align: "right",
                      render: (text) => (
                        <span style={{ fontWeight: 500 }}>
                          ₹{text?.toFixed(2)}
                        </span>
                      ),
                    },
                  ]}
                />
              </div>
              <div className="detail-section">
                <Typography.Title level={5} className="section-title">
                  <ProjectOutlined /> Project Details
                </Typography.Title>
                <Row gutter={24}>
                  <Col span={12}>
                    <div className="detail-item">
                      <strong>Project Name:</strong>{" "}
                      {detailsData.projectName || "N/A"}
                    </div>
                    <div className="detail-item">
                      <strong>Created By:</strong>{" "}
                      {detailsData.createdBy || "N/A"}
                    </div>
                  </Col>
                  <Col span={12}>
                    <div className="detail-item">
                      <strong>Last Updated:</strong>{" "}
                      {detailsData.updatedDate || "N/A"}
                    </div>
                    <div className="detail-item">
                      <strong>Updated By:</strong>{" "}
                      {detailsData.updatedBy || "N/A"}
                    </div>
                  </Col>
                </Row>
              </div>
            </div>
          )}

          {parseInt(selectedRecord?.workflowId, 10) === 3 && (
            <div>
              <div className="detail-section">
                <Typography.Title level={5} className="section-title">
                  <FileTextOutlined /> PO Basic Details
                </Typography.Title>
                <Row gutter={24}>
                  <Col span={12}>
                    <div className="detail-item">
                      <strong>PO ID:</strong> {detailsData.poId || "N/A"}
                    </div>
                    <div className="detail-item">
                      <strong>Tender ID:</strong>{" "}
                      {detailsData.tenderId || "N/A"}
                    </div>
                    <div className="detail-item">
                      <strong>Delivery Period:</strong>{" "}
                      {detailsData.deliveryPeriod
                        ? `${detailsData.deliveryPeriod} days`
                        : "N/A"}
                    </div>
                  </Col>
                  <Col span={12}>
                    <div className="detail-item">
                      <strong>Total PO Value:</strong>
                      <span className="amount">
                        {detailsData.totalValueOfPo !== undefined
                          ? `₹${detailsData.totalValueOfPo.toFixed(2)}`
                          : "N/A"}
                      </span>
                    </div>
                    <div className="detail-item">
                      <strong>Payment Terms:</strong>{" "}
                      {detailsData.paymentTerms || "N/A"}
                    </div>
                    <div className="detail-item">
                      <strong>LD Clause:</strong>{" "}
                      {detailsData.ifLdClauseApplicable ? "Yes" : "No"}
                    </div>
                  </Col>
                </Row>
              </div>
               <div className="detail-section">
                <Typography.Title level={5} className="section-title">
                  <FilePdfOutlined /> Document Details
                </Typography.Title>
                <Row gutter={24}>
                  <Col span={8}>
                    <div className="detail-item">
                      <strong>PO Document:</strong>
                      {detailsData.comparativeStatementFileName
                        ? detailsData.comparativeStatementFileName
                            .split(",")
                            .map((fileName, index) => (
                              <div key={index}>
                                <a
                                  href={`${baseURL}/file/view/Tender/${fileName.trim()}`}
                                  target="_blank"
                                  rel="noopener noreferrer"
                                >
                                  {fileName.trim()} (View)
                                </a>
                                {index <
                                  detailsData.comparativeStatementFileName.split(", ")
                                    .length -
                                    1 && ", "}
                              </div>
                            ))
                        : "N/A"}
                    </div>
                  </Col>
                </Row>
              </div>

              <div className="detail-section">
                <Typography.Title level={5} className="section-title">
                  <ShopOutlined /> Vendor Details
                </Typography.Title>
                <Row gutter={24}>
                  <Col span={12}>
                    <div className="detail-item">
                      <strong>Vendor Name:</strong>{" "}
                      {detailsData.vendorName || "N/A"}
                    </div>
                    <div className="detail-item">
                      <strong>Account Number:</strong>{" "}
                      {detailsData.vendorAccountNumber || "N/A"}
                    </div>
                  </Col>
                  <Col span={12}>
                    <div className="detail-item">
                      <strong>IFSC Code:</strong>{" "}
                      {detailsData.vendorsIfscCode || "N/A"}
                    </div>
                    <div className="detail-item">
                      <strong>Account Name:</strong>{" "}
                      {detailsData.vendorAccountName || "N/A"}
                    </div>
                  </Col>
                </Row>
              </div>

              <div className="detail-section">
                <Typography.Title level={5} className="section-title">
                  <BarsOutlined /> Purchase Order Items
                </Typography.Title>
                <Table
                  dataSource={detailsData.purchaseOrderAttributes}
                  pagination={false}
                  bordered
                  scroll={{ x: true }}
                  rowKey="materialCode"
                  columns={[
                    {
                      title: "Material Code",
                      dataIndex: "materialCode",
                      width: 120,
                    },
                    {
                      title: "Description",
                      dataIndex: "materialDescription",
                      ellipsis: true,
                    },
                    {
                      title: "Quantity",
                      dataIndex: "quantity",
                      align: "right",
                    },
                    {
                      title: "Rate",
                      dataIndex: "rate",
                      align: "right",
                      render: (text) => `₹${text?.toFixed(2)}`,
                    },
                    { title: "Currency", dataIndex: "currency", width: 100 },
                    {
                      title: "GST",
                      dataIndex: "gst",
                      render: (text) => `${text}%`,
                      align: "right",
                    },
                    {
                      title: "Freight",
                      dataIndex: "freightCharge",
                      align: "right",
                      render: (text) => (text ? `₹${text}` : "N/A"),
                    },
                    {
                      title: "Budget Code",
                      dataIndex: "budgetCode",
                      width: 120,
                    },
                  ]}
                />
              </div>
            </div>
          )}

        { /* {parseInt(selectedRecord?.workflowId, 10) === 4 && (*/}
         {[4, 7].includes(parseInt(selectedRecord?.workflowId, 10)) && (
            <div>
              <div className="detail-section">
                <Typography.Title level={5} className="section-title">
                  <AuditOutlined /> Tender Overview
                </Typography.Title>
                <Row gutter={24}>
                  <Col span={12}>
                    <div className="detail-item">
                      <strong>Tender ID:</strong>{" "}
                      {detailsData.tenderId || "N/A"}
                      {detailsData.tenderVersion && (
                        <Tag color="blue" style={{marginLeft: 8}}>v{detailsData.tenderVersion}</Tag>
                      )}
                      {detailsData.isLocked && (
                        <Tag color="red" style={{marginLeft: 8}}>🔒 Locked</Tag>
                      )}
                    </div>
                    <div className="detail-item">
                      <strong>Title:</strong>{" "}
                      {detailsData.titleOfTender || "N/A"}
                    </div>
                    <div className="detail-item">
                      <strong>Bid Type:</strong> {detailsData.bidType || "N/A"}
                    </div>
                  </Col>
                  <Col span={12}>
                    <div className="detail-item">
                      <strong>Opening Date:</strong>{" "}
                      {detailsData.openingDate || "N/A"}
                    </div>
                    <div className="detail-item">
                      <strong>Closing Date:</strong>{" "}
                      {detailsData.closingDate || "N/A"}
                    </div>
                    <div className="detail-item">
                      <strong>Total Value:</strong>
                      <span className="amount">
                        {detailsData.totalTenderValue
                          ? `₹${detailsData.totalTenderValue.toFixed(2)}`
                          : "N/A"}
                      </span>
                      {detailsData.totalTenderValue > 1000000 && (
                        <Tag color="purple" style={{marginLeft: 8}}>Enhanced Workflow</Tag>
                      )}
                    </div>
                  </Col>
                </Row>

                {/* TC_48: Lock Status Display */}
                {detailsData.isLocked && (
                  <div style={{marginTop: 16, padding: 12, backgroundColor: '#fff1f0', border: '1px solid #ffa39e', borderRadius: 4}}>
                    <strong style={{color: '#cf1322'}}>🔒 Tender Locked</strong>
                    <p style={{margin: '8px 0 0 0', color: '#595959'}}>{detailsData.lockedReason || 'This tender is locked for editing.'}</p>
                    {detailsData.lockedForPO && (
                      <p style={{margin: '4px 0 0 0', fontSize: 12, color: '#8c8c8c'}}>
                        Locked for: {detailsData.lockedForPO} | Date: {new Date(detailsData.lockedDate).toLocaleString()}
                      </p>
                    )}
                  </div>
                )}

                {/* TC_46: Update Reason Display */}
                {detailsData.updateReason && (
                  <div style={{marginTop: 16, padding: 12, backgroundColor: '#e6f7ff', border: '1px solid #91d5ff', borderRadius: 4}}>
                    <strong style={{color: '#096dd9'}}>Last Update Reason:</strong>
                    <p style={{margin: '8px 0 0 0', color: '#595959'}}>{detailsData.updateReason}</p>
                  </div>
                )}
              </div>

              {/* TC_43: Project Budget Display for Tenders */}
              {detailsData.projectCode && (
                <div style={{ marginBottom: '16px' }}>
                  <ProjectBudgetDisplay
                    projectCode={detailsData.projectCode}
                    indentAmount={detailsData.totalTenderValue}
                  />
                </div>
              )}

              <div className="detail-section">
                <Typography.Title level={5} className="section-title">
                  <FilePdfOutlined /> Document Details
                </Typography.Title>
                <Row gutter={24}>
                  <Col span={8}>
                    <div className="detail-item">
                      <strong>Tender Documents:</strong>
                      {detailsData.uploadTenderDocuments
                        ? detailsData.uploadTenderDocuments
                            .split(",")
                            .map((fileName, index) => (
                              <div key={index}>
                                <a
                                  href={`${baseURL}/file/view/Tender/${fileName.trim()}`}
                                  target="_blank"
                                  rel="noopener noreferrer"
                                >
                                  {fileName.trim()} (View)
                                </a>
                                {index <
                                  detailsData.uploadTenderDocuments.split(", ")
                                    .length -
                                    1 && ", "}
                              </div>
                            ))
                        : "N/A"}
                    </div>
                  </Col>
                  <Col span={8}>
                    <div className="detail-item">
                      <strong>Specific Terms:</strong>
                      {detailsData.uploadSpecificTermsAndConditions
                        ? detailsData.uploadSpecificTermsAndConditions
                            .split(",")
                            .map((fileName, index) => (
                              <div key={index}>
                                <a
                                  href={`${baseURL}/file/view/Tender/${fileName.trim()}`}
                                  target="_blank"
                                  rel="noopener noreferrer"
                                >
                                  {fileName.trim()} (View)
                                </a>
                                {index <
                                  detailsData.uploadSpecificTermsAndConditions.split(
                                    ", "
                                  ).length -
                                    1 && ", "}
                              </div>
                            ))
                        : "N/A"}
                    </div>
                  </Col>
                  <Col span={8}>
                    <div className="detail-item">
                      <strong>General Terms:</strong>
                      {detailsData.uploadGeneralTermsAndConditions
                        ? detailsData.uploadGeneralTermsAndConditions
                            .split(",")
                            .map((fileName, index) => (
                              <div key={index}>
                                <a
                                  href={`${baseURL}/file/view/Tender/${fileName.trim()}`}
                                  target="_blank"
                                  rel="noopener noreferrer"
                                >
                                  {fileName.trim()} (View)
                                </a>
                                {index <
                                  detailsData.uploadGeneralTermsAndConditions.split(
                                    ", "
                                  ).length -
                                    1 && ", "}
                              </div>
                            ))
                        : "N/A"}
                    </div>
                  </Col>
                </Row>
              </div>
               {/* TC_47: Pre-bid Meeting Display */}
              {detailsData.preBidMeetingStatus && detailsData.preBidMeetingStatus !== 'NOT_CONDUCTED' && (
                <div className="detail-section">
                  <Typography.Title level={5} className="section-title">
                    <CalendarOutlined /> Pre-bid Meeting
                  </Typography.Title>
                  <Row gutter={24}>
                    <Col span={12}>
                      <div className="detail-item">
                        <strong>Status:</strong>{" "}
                        <Tag color={
                          detailsData.preBidMeetingStatus === 'CONDUCTED' ? 'green' :
                          detailsData.preBidMeetingStatus === 'SCHEDULED' ? 'blue' : 'default'
                        }>
                          {detailsData.preBidMeetingStatus}
                        </Tag>
                      </div>
                      <div className="detail-item">
                        <strong>Meeting Date:</strong> {detailsData.preBidMeetingDate || "N/A"}
                      </div>
                    </Col>
                    <Col span={24} style={{marginTop: 12}}>
                      <div className="detail-item">
                        <strong>Discussion Points:</strong>
                        <div style={{marginTop: 8, padding: 12, backgroundColor: '#fafafa', borderRadius: 4, whiteSpace: 'pre-wrap'}}>
                          {detailsData.preBidMeetingDiscussion || "No discussion points recorded."}
                        </div>
                      </div>
                    </Col>
                  </Row>
                </div>
              )}

              <div className="detail-section">
                <Row gutter={24}>
                  <Col span={12}>
                    <Typography.Title level={5} className="section-title">
                      <AuditOutlined /> Commercial Terms:
                    </Typography.Title>
                    <div className="detail-item">
                      <strong>Inco Terms</strong>{" "}
                      {detailsData.incoTerms || "N/A"}
                    </div>
                    <div className="detail-item">
                      <strong>Consignee Address</strong>{" "}
                      {detailsData.consignes || "N/A"}
                    </div>
                    <div className="detail-item">
                      <strong>Billing Address</strong> {detailsData.billinngAddress || "N/A"}
                    </div>
                  </Col>
                  <Col span={12}>
                    <Typography.Title level={5} className="section-title">
                      <AuditOutlined /> Payment & Performance:
                    </Typography.Title>
                    <div className="detail-item">
                      <strong>Payment Terms</strong>{" "}
                      {detailsData.paymentTerms || "N/A"}
                    </div>
                    <div className="detail-item">
                      <strong>LD Clause</strong>{" "}
                      {detailsData.ldClause || "N/A"}
                    </div>
                  </Col>
                </Row>
              </div>
               {detailsData.bidSecurityDeclaration && (
                <div className="detail-section">
                  <Typography.Title level={5} className="section-title">
                    <ProjectOutlined /> Bid Security
                  </Typography.Title>
                  <Row gutter={24}>
                    <Col span={12}>
                      <div className="detail-item">
                        <strong>Bid Security Declaration:</strong>{" "}
                        {String(detailsData.bidSecurityDeclaration) || "N/A"}
                      </div>
                    </Col>
                    <Col span={12}>
                      <div className="detail-item">
                        <strong>Bid Security Declaration File:</strong>
                        {detailsData.bidSecurityDeclarationFileName
                          ? detailsData.bidSecurityDeclarationFileName
                              .split(",")
                              .map((fileName, index) => (
                                <div key={index}>
                                  <a
                                    href={`${baseURL}/file/view/Tender/${fileName.trim()}`}
                                    target="_blank"
                                    rel="noopener noreferrer"
                                  >
                                    {fileName.trim()} (View)
                                  </a>
                                  {index <
                                    detailsData.bidSecurityDeclarationFileName.split(
                                      ", "
                                    ).length -
                                      1 && ", "}
                                </div>
                              ))
                          : "N/A"}
                      </div>
                    </Col>
                  </Row>
                </div>
              )}
               {detailsData.buyBack && (
                <div className="detail-section">
                  <Typography.Title level={5} className="section-title">
                    <ProjectOutlined /> Buy Back
                  </Typography.Title>
                  <Row gutter={24}>
                    <Col span={12}>
                      <div className="detail-item">
                        <strong>Buy Back:</strong>{" "}
                        {String(detailsData.buyBack) || "N/A"}
                      </div>
                    </Col>
                    <Col span={12}>
                      <div className="detail-item">
                        <strong>Buy Back File:</strong>
                       {detailsData.uploadBuyBackFileNames
      ? detailsData.uploadBuyBackFileNames
          .split(",")
          .map((fileName, index, arr) => (
            <div key={index}>
              <a
                href={`${baseURL}/file/view/Tender/${fileName.trim()}`}
                target="_blank"
                rel="noopener noreferrer"
              >
                {fileName.trim()} (View)
              </a>
              {index < arr.length - 1 && ", "}
            </div>
          ))
      : "N/A"}
  </div>

                      <div className="detail-item">
                        <strong>Model Number:</strong>{" "}
                        {detailsData.modelNumber || "N/A"}
                      </div>
                      <div className="detail-item">
                        <strong>Serial Number:</strong>{" "}
                        {detailsData.serialNumber || "N/A"}
                      </div>
                      <div className="detail-item">
                        <strong>Date Of Purchase:</strong> 
                        {detailsData.dateOfPurchase}
                      </div>
                    </Col>
                  </Row>
                </div>
              )}
              {detailsData.mllStatusDeclaration && (
                <div className="detail-section">
                  <Typography.Title level={5} className="section-title">
                    <ProjectOutlined /> Mll Status
                  </Typography.Title>
                  <Row gutter={24}>
                    <Col span={12}>
                      <div className="detail-item">
                        <strong>Mll Status Declaration:</strong>{" "}
                        {String(detailsData.mllStatusDeclaration) || "N/A"}
                      </div>
                    </Col>
                    <Col span={12}>
                      <div className="detail-item">
                        <strong>Mll Status Declaration File:</strong>
                        {detailsData.mllStatusDeclarationFileName
                          ? detailsData.mllStatusDeclarationFileName
                              .split(",")
                              .map((fileName, index) => (
                                <div key={index}>
                                  <a
                                    href={`${baseURL}/file/view/Tender/${fileName.trim()}`}
                                    target="_blank"
                                    rel="noopener noreferrer"
                                  >
                                    {fileName.trim()} (View)
                                  </a>
                                  {index <
                                    detailsData.mllStatusDeclarationFileName.split(
                                      ", "
                                    ).length -
                                      1 && ", "}
                                </div>
                              ))
                          : "N/A"}
                      </div>
                    </Col>
                  </Row>
                </div>
              )}

              {detailsData.indentResponseDTO &&
                detailsData.indentResponseDTO.length > 0 && (
                  <div className="detail-section">
                    <Typography.Title level={5} className="section-title">
                      <SolutionOutlined /> Associated Indents (
                      {detailsData.indentResponseDTO.length})
                    </Typography.Title>

                    <div style={{ marginBottom: 16 }}>
                      <strong>Indent IDs: </strong>
                      {detailsData.indentResponseDTO.map((indent, index) => (
                        <Tag
                          color="blue"
                          key={indent.indentId}
                          style={{ margin: "4px 4px" }}
                        >
                          {indent.indentId || `Indent ${index + 1}`}
                        </Tag>
                      ))}
                    </div>

                    <Collapse accordion defaultActiveKey={["0"]}style={{ overflow: 'hidden' }}>
                      {detailsData.indentResponseDTO.map((indent, index) => (
                        <Collapse.Panel
                          key={index}
                          header={`Indent ${index + 1} - ${
                            indent.indentId || "N/A"
                          }`}
                          extra={
                            <Tag color={indent.statusColor || "processing"}>
                              {indent.status || "Pending"}
                            </Tag>
                          }
                        >
                          <div style={{ padding: "16px 0",  overflowX: 'auto' }}>
                            <Row gutter={24}>
                              <Col span={12}>
                                <div className="detail-item">
                                  <strong>Project Name:</strong>{" "}
                                  {indent.projectName || "N/A"}
                                </div>
                                <div className="detail-item">
                                  <strong>Indentor:</strong>{" "}
                                  {indent.indentorName || "N/A"}
                                </div>
                                <div className="detail-item">
                                  <strong>Indentor Department:</strong> 
                                  {indent.employeeDept || "N/A"}
                                </div>
                                <div className="detail-item">
                                  <strong>Contact:</strong>{" "}
                                  {indent.indentorMobileNo || "N/A"}
                                </div>
                              </Col>
                              <Col span={12}>
                                <div className="detail-item">
                                  <strong>Email:</strong>{" "}
                                  {indent.indentorEmailAddress || "N/A"}
                                </div>
                                <div className="detail-item">
                                  <strong>Location:</strong>{" "}
                                  {indent.consignesLocation || "N/A"}
                                </div>
                                <div className="detail-item">
                                  <strong>Total Value:</strong> ₹
                                  {indent.totalPriceOfAllMaterials?.toFixed(
                                    2
                                  ) || "N/A"}
                                </div>
                              </Col>
                              <Col span={12}>
                                <div className="detail-item">
                                  <strong>Employee Id:</strong>{" "}
                                  {indent.employeeId || "N/A"}
                                </div>
                                <div className="detail-item">
                                  <strong>Created By:</strong>{" "}
                                  {indent.employeeName || "N/A"}
                                </div>
                                
                              </Col>
                            </Row>

                            {indent.isPreBidMeetingRequired && (
                              <div style={{ marginTop: 16 }}>
                                <Divider orientation="left" plain>
                                  Pre-Bid Meeting Details
                                </Divider>
                                <Row gutter={24}>
                                  <Col span={12}>
                                    <div className="detail-item">
                                      <strong>Date:</strong>{" "}
                                      {indent.preBidMeetingDate || "N/A"}
                                    </div>
                                  </Col>
                                  <Col span={12}>
                                    <div className="detail-item">
                                      <strong>Venue:</strong>{" "}
                                      {indent.preBidMeetingVenue || "N/A"}
                                    </div>
                                  </Col>
                                </Row>
                              </div>
                            )}

                            <Divider orientation="left" plain>
                              Material Requirements
                            </Divider>

                            {indent.materialDetails?.length > 0 ? (
                              <Table
                                dataSource={indent.materialDetails}
                                pagination={false}
                                bordered
                                size="small"
                                rowKey="materialCode"
                                columns={[
                                  {
                                    title: "Material Code",
                                    dataIndex: "materialCode",
                                    width: 120,
                                  },
                                  {
                                    title: "Description",
                                    dataIndex: "materialDescription",
                                   // ellipsis: true,
                                    width: 120,
                                  },
                                  {
                                    title: "Quantity",
                                    dataIndex: "quantity",
                                    align: "right",
                                  },
                                  {
                                    title: "Unit Price",
                                    dataIndex: "unitPrice",
                                    align: "right",
                                    render: (text) => `${text?.toFixed(2)}`,
                                  },
                                  {
                                    title: "Currency",
                                    dataIndex: "currency",
                                    align: "right",
                                    // render: (text) => `₹${text?.toFixed(2)}`,
                                  },
                                  {
                                    title: "Conversion Rate",
                                    dataIndex: "conversionRate",
                                    align: "right",
                                    // render: (text) => `₹${text?.toFixed(2)}`,
                                  },
                                  {
                                    title: "Total Price",
                                    dataIndex: "totalPrice",
                                    align: "right",
                                    render: (text) => (
                                      <span style={{ fontWeight: 500 }}>
                                        ₹{text?.toFixed(2)}
                                      </span>
                                    ),
                                  },
                                  {
                                    title: "UOM",
                                    dataIndex: "uom",
                                    width: 100,
                                  },
                                  {
                                    title: "Budget Code",
                                    dataIndex: "budgetCode",
                                    width: 120,
                                  },
                                  {
                      title: "Mode Of Procurement",
                      dataIndex: "modeOfProcurement",
                      width: 120,
                    },
                    {
                      title: "Vendor Names",
                      dataIndex: "vendorNames",
                      width: 120,
                      render: (text) => (text ? text.join(", ") : "N/A"),
                    }
                                ]}
                              />
                            ) : (
                              <div style={{ textAlign: "center", padding: 16 }}>
                                <Empty
                                  image={Empty.PRESENTED_IMAGE_SIMPLE}
                                  description="No material details found"
                                />
                              </div>
                            )}
                          </div>
                        </Collapse.Panel>
                      ))}
                    </Collapse>
                  </div>
                )}
            </div>
          )}
          {parseInt(selectedRecord?.workflowId, 10) === 10 && ( 
  <div>
    {/* Basic Payment Voucher Details */}
    <div className="detail-section">
      <Typography.Title level={5} className="section-title">
        <FileTextOutlined /> Payment Voucher Basic Details
      </Typography.Title>
      <Row gutter={24}>
        <Col span={12}>
          <div className="detail-item">
            <strong>Payment Voucher Date:</strong> {detailsData.paymentVoucherDate || "N/A"}
          </div>
          <div className="detail-item">
            <strong>Voucher Type:</strong> {detailsData.paymentVoucherType || "N/A"}
          </div>
          <div className="detail-item">
            <strong>Purchase Order ID:</strong> {detailsData.purchaseOrderId || "N/A"}
          </div>
        </Col>
        <Col span={12}>
          <div className="detail-item">
            <strong>GRN Number:</strong> {detailsData.grnNumber || "N/A"}
          </div>
          <div className="detail-item">
            <strong>Status:</strong> {detailsData.status || "N/A"}
          </div>
          <div className="detail-item">
            <strong>Remarks:</strong> {detailsData.remarks || "N/A"}
          </div>
        </Col>
      </Row>
    </div>

    {/* Vendor Details */}
    <div className="detail-section">
      <Typography.Title level={5} className="section-title">
        <ShopOutlined /> Vendor Details
      </Typography.Title>
      <Row gutter={24}>
        <Col span={12}>
          <div className="detail-item">
            <strong>Vendor Name:</strong> {detailsData.vendorName || "N/A"}
          </div>
          <div className="detail-item">
            <strong>Vendor Invoice No:</strong> {detailsData.vendorInvoiceNumber || "N/A"}
          </div>
        </Col>
        <Col span={12}>
          <div className="detail-item">
            <strong>Vendor Invoice Date:</strong> {detailsData.vendorInvoiceDate || "N/A"}
          </div>
          <div className="detail-item">
            <strong>Currency:</strong> {detailsData.currency || "N/A"}
          </div>
        </Col>
      </Row>
    </div>

    {/* Amount Details */}
    <div className="detail-section">
      <Typography.Title level={5} className="section-title">
        <DollarOutlined /> Amount Details
      </Typography.Title>
      <Row gutter={24}>
        <Col span={8}>
          <div className="detail-item">
            <strong>Total Po Amount:</strong> ₹{detailsData.totalAmount?.toFixed(2) || "0.00"}
          </div>
        </Col>
        <Col span={8}>
          <div className="detail-item">
            <strong>Partial Amount:</strong> ₹{detailsData.partialAmount?.toFixed(2) || "0.00"}
          </div>
        </Col>
        <Col span={8}>
          <div className="detail-item">
            <strong>Advance Amount:</strong> ₹{detailsData.advanceAmount?.toFixed(2) || "0.00"}
          </div>
        </Col>
         <Col span={8}>
          <div className="detail-item">
            <strong>TDS Amount:</strong> ₹{detailsData.tdsAmount?.toFixed(2) || "0.00"}
          </div>
        </Col>
         <Col span={8}>
          <div className="detail-item">
            <strong>Payement Voucher Amount:</strong> ₹{detailsData.paymentVoucherNetAmount?.toFixed(2) || "0.00"}
          </div>
        </Col>
      </Row>
    </div>

    {/* Material Details Table */}
    <div className="detail-section">
      <Typography.Title level={5} className="section-title">
        <BarsOutlined /> Materials Details
      </Typography.Title>
      <Table
        dataSource={detailsData.materials}
        pagination={false}
        bordered
        scroll={{ x: true }}
        rowKey="materialCode"
        columns={[
          { title: "Material Code", dataIndex: "materialCode", width: 120 },
          { title: "Description", dataIndex: "materialDescription", ellipsis: true },
          { title: "Quantity", dataIndex: "quantity", align: "right" },
          {
            title: "Unit Price",
            dataIndex: "unitPrice",
            align: "right",
            render: (text) => `₹${text?.toFixed(2)}`
          },
          { title: "Currency", dataIndex: "currency", width: 100 },
          {
            title: "Exchange Rate",
            dataIndex: "exchangeRate",
            align: "right",
            render: (text) => text || "0"
          },
          {
            title: "GST",
            dataIndex: "gst",
            align: "right",
            render: (text) => `${text}%`
          }
        ]}
      />
    </div>
  </div>
)}


          {parseInt(selectedRecord?.workflowId, 10) === 5 && (
            <div>
              <div className="detail-section">
                <Typography.Title level={5} className="section-title">
                  <ToolOutlined /> Service Order Details
                </Typography.Title>
                <Row gutter={24}>
                  <Col span={12}>
                    <div className="detail-item">
                      <strong>SO ID:</strong> {detailsData.soId || "N/A"}
                    </div>
                    <div className="detail-item">
                      <strong>Tender ID:</strong>{" "}
                      {detailsData.tenderId || "N/A"}
                    </div>
                    <div className="detail-item">
                      <strong>Job Completion Period:</strong>{" "}
                      {detailsData.jobCompletionPeriod
                        ? `${detailsData.jobCompletionPeriod} days`
                        : "N/A"}
                    </div>
                  </Col>
                  <Col span={12}>
                    <div className="detail-item">
                      <strong>Total SO Value:</strong>
                      <span className="amount">
                        {detailsData.totalValueOfSo !== undefined
                          ? `₹${detailsData.totalValueOfSo.toFixed(2)}`
                          : "N/A"}
                      </span>
                    </div>
                    <div className="detail-item">
                      <strong>Payment Terms:</strong>{" "}
                      {detailsData.paymentTerms || "N/A"}
                    </div>
                    <div className="detail-item">
                      <strong>LD Clause:</strong>{" "}
                      {detailsData.ifLdClauseApplicable ? "Yes" : "No"}
                    </div>
                  </Col>
                </Row>
              </div>
              <div className="detail-section">
                <Typography.Title level={5} className="section-title">
                  <ShopOutlined /> Vendor Details
                </Typography.Title>
                <Row gutter={24}>
                  <Col span={12}>
                    <div className="detail-item">
                      <strong>Vendor Name:</strong>{" "}
                      {detailsData.vendorName || "N/A"}
                    </div>
                    <div className="detail-item">
                      <strong>Account Number:</strong>{" "}
                      {detailsData.vendorsAccountNo || "N/A"}
                    </div>
                  </Col>
                  <Col span={12}>
                    <div className="detail-item">
                      <strong>IFSC Code:</strong>{" "}
                      {detailsData.vendorsZRSCCode || "N/A"}
                    </div>
                    <div className="detail-item">
                      <strong>Account Name:</strong>{" "}
                      {detailsData.vendorsAccountName || "N/A"}
                    </div>
                  </Col>
                </Row>
              </div>
              <div className="detail-section">
                <Typography.Title level={5} className="section-title">
                  <BarsOutlined /> Purchase Order Items
                </Typography.Title>
                <Table
                  dataSource={detailsData.materials}
                  pagination={false}
                  bordered
                  scroll={{ x: true }}
                  rowKey="materialCode"
                  columns={[
                    {
                      title: "Material Code",
                      dataIndex: "materialCode",
                      width: 120,
                    },
                    {
                      title: "Description",
                      dataIndex: "materialDescription",
                      ellipsis: true,
                    },
                    {
                      title: "Quantity",
                      dataIndex: "quantity",
                      align: "right",
                    },
                    {
                      title: "Rate",
                      dataIndex: "rate",
                      align: "right",
                      render: (text) => `₹${text?.toFixed(2)}`,
                    },
                    { title: "Currency", dataIndex: "currency", width: 100 },
                    {
                      title: "GST",
                      dataIndex: "gst",
                      render: (text) => `${text}%`,
                      align: "right",
                    },
                    {
                      title: "Freight",
                      dataIndex: "freightCharge",
                      align: "right",
                      render: (text) => (text ? `₹${text}` : "N/A"),
                    },
                    {
                      title: "Budget Code",
                      dataIndex: "budgetCode",
                      width: 120,
                    },
                  ]}
                />
              </div>
            </div>
          )}
        </>
      ) : (
        <div style={{ textAlign: "center", padding: "40px 0" }}>
          <Spin tip="Loading details..." size="large" />
        </div>
      )}
      <QueueHistory
        requestId={selectedRecord?.requestId}
        open={historyVisible}
        onCancel={() => setHistoryVisible(false)}
      />
      <MaterialHistory
        open={materialHistoryVisible}
        materialCode={selectedMaterialCode}
        onCancel={() => setMaterialHistoryVisible(false)}
        historyType={detailsData?.indentType === 'job' ? 'job' : 'material'}
      />

    </Modal>
  );
};

export default QueueModal;
