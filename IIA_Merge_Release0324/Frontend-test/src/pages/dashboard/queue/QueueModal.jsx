import React, {useRef} from "react";
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
// Add after: import ProjectBudgetDisplay from '../../../components/ProjectBudgetDisplay';
import PrintFormate from '../../../utils/PrintFormate';
import { useReactToPrint } from 'react-to-print';


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
  fetchVersionHistory,
  versionHistoryLoading,
  // ── version history modal (owned here now) ──
  versionHistoryOpen,
  setVersionHistoryOpen,
  versionHistoryList,
  selectedVersionIdx,
  setSelectedVersionIdx,
}) => {
 const printComponentRef = useRef();
  const handlePrint = useReactToPrint({
    content: () => printComponentRef.current,
    documentTitle: `Indent - ${detailsData?.indentId || selectedRecord?.requestId || "Draft"}`,
  });
  return (
    <>
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
  onClick={() => fetchVersionHistory(selectedRecord)}
  loading={versionHistoryLoading}
>
  View Version History
</Button>
<Button
            type="link"
            icon={<FilePdfOutlined />}
            onClick={handlePrint}
            disabled={!detailsData}
          >
            Print
          </Button>
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
                    },{ title: "UOM", dataIndex: "uom", width: 100 },
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
                      title: "Unit Price",
                      dataIndex: "unitPrice",
                      align: "right",
                      render: (text) => `${text?.toFixed(2)}`,
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
                    <div className="detail-item">
                    <strong>PBG Applicable:</strong>{" "}
                    {detailsData.applicablePbgToBeSubmitted? "Yes" : "No"}
                    </div>
                    <div className="detail-item">
                    <strong>INCO Terms:</strong>{" "}
                    {detailsData.incoTerms}
                    </div>
                    <div className="detail-item">
                    <strong>Consignee Address:</strong>{" "}
                    {detailsData.consignesAddress}
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
                    <strong>Delivery Date:</strong>{" "}
                    {detailsData.deliveryDate || "NA"}
                    </div>
                    <div className="detail-item">
                      <strong>LD Clause:</strong>{" "}
                      {detailsData.ifLdClauseApplicable ? "Yes" : "No"}
                    </div>
                    <div className="detail-item">
                    <strong>Freight Forwarder:</strong>{" "}
                    {detailsData.transporterAndFreightForWarderDetails || "NA"}
                    </div>
                     <div className="detail-item">
                    <strong>Waranty:</strong>{" "}
                    {detailsData.warranty || "NA"}
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
                    <div className="detail-item">
                      <strong>Address:</strong>{" "}
                      {detailsData.vendorAddress || "N/A"}
                    </div>
                  </Col>
                  <Col span={12}>
                    <div className="detail-item">
                      <strong>IFSC Code:</strong>{" "}
                      {detailsData.vendorsIfscCode || "N/A"}
                    </div>
                    <div className="detail-item">
                      <strong>Bank Name:</strong>{" "}
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
                      title: "UOM",
                      dataIndex: "uom",
                      align: "right",
                    },
                    {
                      title: "Quantity",
                      dataIndex: "quantity",
                      align: "right",
                    },
                    {
                      title: "Unit Rate",
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
                      title: "Duties",
                      dataIndex: "duties",
                      align: "right",
                      render: (text) => `${text}%`,
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
 <div style={{ display: "none" }}>
        <PrintFormate ref={printComponentRef} data={detailsData} />
      </div>
    {/* </Modal> */}

    
  {/* ); */}
{/* }; */}
 </Modal>

    
    <Modal
      open={versionHistoryOpen}
      onCancel={() => { setVersionHistoryOpen(false); setSelectedVersionIdx?.(0); }}
      title="Version History"
      footer={null}
      width={960}
      destroyOnClose
    >
      {(() => {
        if (!versionHistoryList || versionHistoryList.length === 0) {
          return <div style={{ padding: '24px', textAlign: 'center', color: '#999' }}>No version history found.</div>;
        }

        const sorted = [...versionHistoryList].sort((a, b) => (a.version || 0) - (b.version || 0));
        const selIdx = Math.max(0, Math.min(selectedVersionIdx, sorted.length - 1));
        const curr = sorted[selIdx];
        const prev = selIdx > 0 ? sorted[selIdx - 1] : null;
        if (!curr) return null;

        const wId = parseInt(curr.workflowId || versionHistoryList[0]?.workflowId, 10);

        // ── Per-workflow header fields ──
        const INDENT_HEADER_FIELDS = [
          { key: 'indentorName',                     label: 'Indentor' },
          { key: 'indentorMobileNo',                 label: 'Mobile No.' },
          { key: 'indentorEmailAddress',             label: 'Email' },
          { key: 'createdBy',                        label: 'Created By' },
          { key: 'modeOfProcurement',                label: 'Mode of Procurement' },
          { key: 'procurementType',                  label: 'Procurement Type' },
          { key: 'projectName',                      label: 'Project' },
          { key: 'projectCode',                      label: 'Project Code' },
          { key: 'isUnderProject',                   label: 'Under Project' },
          { key: 'consignesLocation',                label: 'Consignee Location' },
          { key: 'consignee',                        label: 'Consignee' },
          { key: 'indentType',                       label: 'Indent Type' },
          { key: 'materialCategoryType',             label: 'Material Category' },
          { key: 'purpose',                          label: 'Purpose' },
          { key: 'justification',                    label: 'Justification' },
          { key: 'quarter',                          label: 'Quarter' },
          { key: 'budgetCode',                       label: 'Budget Code' },
          { key: 'isPreBidMeetingRequired',          label: 'Pre-Bid Meeting Required' },
          { key: 'preBidMeetingDate',                label: 'Pre-Bid Meeting Date' },
          { key: 'preBidMeetingVenue',               label: 'Pre-Bid Meeting Venue' },
          { key: 'isItARateContractIndent',          label: 'Rate Contract Indent' },
          { key: 'estimatedRate',                    label: 'Estimated Rate' },
          { key: 'periodOfContract',                 label: 'Period of Contract' },
          { key: 'rateContractJobCodes',             label: 'Rate Contract Job Codes' },
          { key: 'brandPac',                         label: 'Brand PAC' },
          { key: 'brandAndModel',                    label: 'Brand & Model' },
          { key: 'proprietaryJustification',         label: 'Proprietary Justification' },
          { key: 'proprietaryAndLimitedDeclaration', label: 'Proprietary Declaration' },
          { key: 'reason',                           label: 'Reason' },
          { key: 'buyBack',                          label: 'Buy Back' },
          { key: 'buyBackAmount',                    label: 'Buy Back Amount' },
          { key: 'serialNumber',                     label: 'Serial Number' },
          { key: 'modelNumber',                      label: 'Model Number' },
          { key: 'technicalSpecificationsFileName',  label: 'Technical Specs File' },
          { key: 'uploadingPriorApprovalsFileName',  label: 'Prior Approvals File' },
          { key: 'draftEOIOrRFPFileName',            label: 'Draft EOI/RFP File' },
          { key: 'uploadPACOrBrandPACFileName',      label: 'PAC/Brand PAC File' },
          { key: 'uploadBuyBackFileNames',           label: 'Buy Back File' },
        ];

        const PO_HEADER_FIELDS = [
          { key: 'vendorName',            label: 'Vendor Name' },
          { key: 'vendorId',              label: 'Vendor ID' },
          { key: 'vendorAddress',         label: 'Vendor Address' },
          { key: 'vendorAccountNumber',   label: 'Vendor Account No.' },
          { key: 'vendorsIfscCode',       label: 'Vendor IFSC Code' },
          { key: 'vendorAccountName',     label: 'Vendor Account Name' },
          { key: 'deliveryPeriod',        label: 'Delivery Period' },
          { key: 'deliveryDate',          label: 'Delivery Date' },
          { key: 'incoTerms',             label: 'Inco Terms' },
          { key: 'paymentTerms',          label: 'Payment Terms' },
          { key: 'warranty',              label: 'Warranty' },
          { key: 'ifLdClauseApplicable',  label: 'LD Clause' },
          { key: 'consignesAddress',      label: 'Consignee Address' },
          { key: 'billingAddress',        label: 'Billing Address' },
          { key: 'projectName',           label: 'Project Name' },
          { key: 'quotationNumber',       label: 'Quotation Number' },
          { key: 'quotationDate',         label: 'Quotation Date' },
          { key: 'buyBackAmount',         label: 'Buy Back Amount' },
          { key: 'additionalTermsAndConditions', label: 'Additional T&C' },
          { key: 'applicablePbgToBeSubmitted',   label: 'Applicable PBG' },
          { key: 'transporterAndFreightForWarderDetails', label: 'Transporter/Freight Details' },
          { key: 'comparativeStatementFileName', label: 'Comparative Statement File' },
          { key: 'gemContractFileName',          label: 'GeM Contract File' },
          { key: 'typeOfSecurity',        label: 'Type of Security' },
          { key: 'securityNumber',        label: 'Security Number' },
          { key: 'securityDate',          label: 'Security Date' },
          { key: 'expiryDate',            label: 'Expiry Date' },
        ];

        const TENDER_HEADER_FIELDS = [
          { key: 'titleOfTender',         label: 'Title' },
          { key: 'modeOfProcurement',     label: 'Mode of Procurement' },
          { key: 'bidType',               label: 'Bid Type' },
          { key: 'openingDate',           label: 'Opening Date' },
          { key: 'closingDate',           label: 'Closing Date' },
          { key: 'lastDateOfSubmission',  label: 'Last Date of Submission' },
          { key: 'applicableTaxes',       label: 'Applicable Taxes' },
          { key: 'incoTerms',             label: 'Inco Terms' },
          { key: 'paymentTerms',          label: 'Payment Terms' },
          { key: 'ldClause',              label: 'LD Clause' },
          { key: 'projectName',           label: 'Project Name' },
          { key: 'singleAndMultipleVendors', label: 'Vendor Type' },
          { key: 'consignes',             label: 'Consignee Address' },
          { key: 'billinngAddress',       label: 'Billing Address' },
          { key: 'performanceAndWarrantySecurity', label: 'Performance & Warranty Security' },
          { key: 'bidSecurityDeclaration',         label: 'Bid Security Declaration' },
          { key: 'mllStatusDeclaration',           label: 'MLL Status Declaration' },
          { key: 'buyBack',               label: 'Buy Back' },
          { key: 'buyBackAmount',         label: 'Buy Back Amount' },
          { key: 'modelNumber',           label: 'Model Number' },
          { key: 'serialNumber',          label: 'Serial Number' },
          { key: 'dateOfPurchase',        label: 'Date of Purchase' },
          { key: 'uploadTenderDocuments',              label: 'Tender Documents File' },
          { key: 'uploadGeneralTermsAndConditions',    label: 'General T&C File' },
          { key: 'uploadSpecificTermsAndConditions',   label: 'Specific T&C File' },
          { key: 'uploadBuyBackFileNames',             label: 'Buy Back File' },
        ];

        const SO_HEADER_FIELDS = [
          { key: 'vendorName',             label: 'Vendor Name' },
          { key: 'vendorId',               label: 'Vendor ID' },
          { key: 'vendorAddress',          label: 'Vendor Address' },
          { key: 'vendorsAccountNo',       label: 'Vendor Account No.' },
          { key: 'vendorsZRSCCode',        label: 'Vendor IFSC Code' },
          { key: 'vendorsAccountName',     label: 'Vendor Account Name' },
          { key: 'tenderId',               label: 'Tender ID' },
          { key: 'jobCompletionPeriod',    label: 'Job Completion Period' },
          { key: 'startDateAmc',           label: 'AMC Start Date' },
          { key: 'endDateAmc',             label: 'AMC End Date' },
          { key: 'incoTerms',              label: 'Inco Terms' },
          { key: 'paymentTerms',           label: 'Payment Terms' },
          { key: 'ifLdClauseApplicable',   label: 'LD Clause' },
          { key: 'consignesAddress',       label: 'Consignee Address' },
          { key: 'billingAddress',         label: 'Billing Address' },
          { key: 'applicablePBGToBeSubmitted', label: 'Applicable PBG' },
          { key: 'projectName',            label: 'Project Name' },
        ];

        const CP_HEADER_FIELDS = INDENT_HEADER_FIELDS;

        const HEADER_FIELDS = (
          wId === 1 ? INDENT_HEADER_FIELDS
          : wId === 3 ? PO_HEADER_FIELDS
          : [4, 7].includes(wId) ? TENDER_HEADER_FIELDS
          : wId === 5 ? SO_HEADER_FIELDS
          : wId === 2 ? CP_HEADER_FIELDS
          : INDENT_HEADER_FIELDS
        ).filter(f => curr[f.key] !== undefined || (prev && prev[f.key] !== undefined));

        const MAT_FIELDS = [
          { key: 'materialCode',        label: 'Material Code' },
          { key: 'materialDescription', label: 'Description' },
          { key: 'quantity',            label: 'Qty' },
          { key: 'unitPrice',           label: 'Unit Price' },
          { key: 'totalPrice',          label: 'Total Price' },
          { key: 'uom',                 label: 'UOM' },
          { key: 'budgetCode',          label: 'Budget Code' },
          { key: 'currency',            label: 'Currency' },
          { key: 'conversionRate',      label: 'Conversion Rate' },
          { key: 'modeOfProcurement',   label: 'Mode of Procurement' },
          { key: 'materialCategory',    label: 'Category' },
          { key: 'materialSubCategory', label: 'Sub-Category' },
        ];
        const JOB_FIELDS = [
          { key: 'jobCode',           label: 'Job Code' },
          { key: 'jobDescription',    label: 'Description' },
          { key: 'briefDescription',  label: 'Brief Description' },
          { key: 'quantity',          label: 'Qty' },
          { key: 'estimatedPrice',    label: 'Est. Price' },
          { key: 'totalPrice',        label: 'Total Price' },
          { key: 'uom',               label: 'UOM' },
          { key: 'budgetCode',        label: 'Budget Code' },
          { key: 'currency',          label: 'Currency' },
          { key: 'category',          label: 'Category' },
          { key: 'subCategory',       label: 'Sub-Category' },
          { key: 'modeOfProcurement', label: 'Mode of Procurement' },
        ];
        const PO_FIELDS = [
          { key: 'materialCode',   label: 'Material Code' },
          { key: 'description',    label: 'Description' },
          { key: 'quantity',       label: 'Qty' },
          { key: 'unitPrice',      label: 'Unit Price' },
          { key: 'totalPrice',     label: 'Total Price' },
          { key: 'uom',            label: 'UOM' },
          { key: 'currency',       label: 'Currency' },
        ];
        const TENDER_FIELDS = [
          { key: 'materialCode',   label: 'Material Code' },
          { key: 'description',    label: 'Description' },
          { key: 'quantity',       label: 'Qty' },
          { key: 'unitPrice',      label: 'Unit Price' },
          { key: 'totalPrice',     label: 'Total Price' },
          { key: 'uom',            label: 'UOM' },
        ];
        const SO_FIELDS = [
          { key: 'jobCode',        label: 'Job Code' },
          { key: 'description',    label: 'Description' },
          { key: 'quantity',       label: 'Qty' },
          { key: 'unitPrice',      label: 'Unit Price' },
          { key: 'totalPrice',     label: 'Total Price' },
        ];

        const isJob = (curr.indentType || '').toLowerCase() === 'job';
        let lineLabel = 'Item';
        let lineFields = MAT_FIELDS;
        let descKey = 'materialDescription';
        let currLines = [];
        let prevLines = [];

        if (wId === 1) {
          lineLabel = isJob ? 'Job' : 'Material';
          lineFields = isJob ? JOB_FIELDS : MAT_FIELDS;
          descKey = isJob ? 'jobDescription' : 'materialDescription';
          currLines = isJob ? (curr.jobDetails || []) : (curr.materialDetails || []);
          prevLines = prev ? (isJob ? (prev.jobDetails || []) : (prev.materialDetails || [])) : [];
        } else if (wId === 3) {
          lineLabel = 'PO Item';
          lineFields = PO_FIELDS;
          descKey = 'description';
          currLines = curr.purchaseOrderDetails || curr.poItems || [];
          prevLines = prev ? (prev.purchaseOrderDetails || prev.poItems || []) : [];
        } else if ([4, 7].includes(wId)) {
          lineLabel = 'Tender Item';
          lineFields = TENDER_FIELDS;
          descKey = 'description';
          currLines = curr.tenderItems || curr.items || [];
          prevLines = prev ? (prev.tenderItems || prev.items || []) : [];
        } else if (wId === 5) {
          lineLabel = 'SO Item';
          lineFields = SO_FIELDS;
          descKey = 'description';
          currLines = curr.serviceOrderDetails || curr.items || [];
          prevLines = prev ? (prev.serviceOrderDetails || prev.items || []) : [];
        } else if (wId === 2) {
          lineLabel = 'Item';
          lineFields = MAT_FIELDS;
          descKey = 'description';
          currLines = curr.contingencyItems || curr.items || [];
          prevLines = prev ? (prev.contingencyItems || prev.items || []) : [];
        }

        const headerDiffs = prev
          ? HEADER_FIELDS
              .filter(f => String(prev[f.key] ?? '') !== String(curr[f.key] ?? ''))
              .map(f => ({ ...f, oldVal: prev[f.key], newVal: curr[f.key] }))
          : [];

        const lineDiffs = [];
        const maxLen = Math.max(prevLines.length, currLines.length);
        for (let i = 0; i < maxLen; i++) {
          const p = prevLines[i];
          const c = currLines[i];
          if (!p) {
            lineDiffs.push({ idx: i, type: 'added', item: c });
          } else if (!c) {
            lineDiffs.push({ idx: i, type: 'removed', item: p });
          } else {
            const changed = lineFields
              .filter(f => String(p[f.key] ?? '') !== String(c[f.key] ?? ''))
              .map(f => ({ ...f, oldVal: p[f.key], newVal: c[f.key] }));
            if (changed.length)
              lineDiffs.push({ idx: i, type: 'modified', changes: changed, label: c[descKey] || `Item ${i + 1}` });
          }
        }

        const prevTotal = prev != null ? Number(prev.totalAmount || prev.totalPriceOfAllMaterials || 0) : null;
        const currTotal = Number(curr.totalAmount || curr.totalPriceOfAllMaterials || 0);
        const totalChanged = prev && prevTotal !== currTotal;
        const totalChanges = headerDiffs.length + lineDiffs.length + (totalChanged ? 1 : 0);

        const fmtCurrency = val => val != null ? `₹ ${Number(val).toLocaleString('en-IN', { maximumFractionDigits: 2 })}` : '—';
        const fmtVal = val => (val == null || val === '') ? '—' : String(val);

        return (
          <div style={{ display: 'flex', minHeight: '450px' }}>
            {/* Left: version selector */}
            <div style={{ width: '200px', flexShrink: 0, borderRight: '1px solid #f0f0f0' }}>
              <div style={{ padding: '8px 12px', fontWeight: 600, fontSize: '11px', color: '#aaa', letterSpacing: '1px', borderBottom: '1px solid #f0f0f0' }}>
                VERSIONS
              </div>
              {sorted.map((v, idx) => {
                const isSel = idx === selIdx;
                return (
                  <div
                    key={v.indentId || v.tenderId || v.poId || v.soId || idx}
                    onClick={() => setSelectedVersionIdx(idx)}
                    style={{
                      padding: '10px 14px', cursor: 'pointer',
                      borderLeft: isSel ? '3px solid #1890ff' : '3px solid transparent',
                      background: isSel ? '#e6f7ff' : 'transparent',
                      borderBottom: '1px solid #f5f5f5',
                    }}
                  >
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                      <span style={{ fontWeight: 600, fontSize: '14px' }}>V{v.version}</span>
                      {v.isActive
                        ? <Tag color="green" style={{ fontSize: '10px', margin: 0 }}>Active</Tag>
                        : <Tag color="default" style={{ fontSize: '10px', margin: 0 }}>Old</Tag>}
                    </div>
                    <div style={{ fontSize: '11px', color: '#999', marginTop: '3px' }}>{v.updatedBy || v.createdBy || '—'}</div>
                    <div style={{ fontSize: '11px', color: '#bbb', marginTop: '1px' }}>
                      {(v.updatedDate || v.createdDate) ? new Date(v.updatedDate || v.createdDate).toLocaleDateString('en-IN') : '—'}
                    </div>
                  </div>
                );
              })}
            </div>

            {/* Right: diff panel */}
            <div style={{ flex: 1, padding: '0 16px', overflowY: 'auto', maxHeight: '540px' }}>
              <div style={{ padding: '12px 0', borderBottom: '1px solid #f0f0f0', display: 'flex', alignItems: 'center', gap: '8px', flexWrap: 'wrap' }}>
                {prev ? (
                  <>
                    <span style={{ fontWeight: 600, color: '#888' }}>V{prev.version}</span>
                    <span style={{ color: '#ccc' }}>→</span>
                    <span style={{ fontWeight: 600, color: '#1890ff' }}>V{curr.version}</span>
                    {totalChanges === 0
                      ? <Tag>No changes</Tag>
                      : <Tag color="blue">{totalChanges} change{totalChanges !== 1 ? 's' : ''}</Tag>}
                  </>
                ) : (
                  <span style={{ fontWeight: 600, color: '#52c41a' }}>V{curr.version} — Initial Version</span>
                )}
              </div>

              {!prev && (
                <div style={{ padding: '16px 0', color: '#888', fontSize: '13px' }}>
                  This is the first version. No previous version to compare against.
                  <div style={{ marginTop: '12px' }}>
                    {HEADER_FIELDS.filter(f => curr[f.key]).map(f => (
                      <div key={f.key} style={{ display: 'flex', padding: '6px 0', borderBottom: '1px solid #fafafa' }}>
                        <span style={{ width: '180px', color: '#aaa', fontSize: '12px' }}>{f.label}</span>
                        <span style={{ fontSize: '13px' }}>{fmtVal(curr[f.key])}</span>
                      </div>
                    ))}
                  </div>
                </div>
              )}

              {prev && totalChanges === 0 && (
                <div style={{ padding: '24px 0', color: '#888', fontSize: '13px' }}>
                  No field-level changes detected compared to V{prev.version}.
                </div>
              )}

              {prev && totalChanges > 0 && (
                <>
                  {totalChanged && (
                    <div style={{ marginTop: '16px' }}>
                      <div style={{ fontWeight: 600, fontSize: '11px', color: '#aaa', letterSpacing: '1px', marginBottom: '8px' }}>TOTAL VALUE</div>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '10px', padding: '10px 14px', background: '#fffbe6', border: '1px solid #ffe58f', borderRadius: '6px' }}>
                        <span style={{ fontSize: '12px', color: '#888', flex: 1 }}>Total Value</span>
                        <span style={{ color: '#cf1322', textDecoration: 'line-through', fontSize: '13px' }}>{fmtCurrency(prevTotal)}</span>
                        <span style={{ color: '#bbb' }}>→</span>
                        <span style={{ color: '#389e0d', fontWeight: 600, fontSize: '13px' }}>{fmtCurrency(currTotal)}</span>
                      </div>
                    </div>
                  )}

                  {headerDiffs.length > 0 && (
                    <div style={{ marginTop: '16px' }}>
                      <div style={{ fontWeight: 600, fontSize: '11px', color: '#aaa', letterSpacing: '1px', marginBottom: '8px' }}>GENERAL FIELDS</div>
                      {headerDiffs.map(f => (
                        <div key={f.key} style={{ display: 'flex', alignItems: 'flex-start', gap: '10px', padding: '9px 14px', marginBottom: '4px', background: '#fffbe6', border: '1px solid #ffe58f', borderRadius: '4px' }}>
                          <span style={{ width: '160px', flexShrink: 0, fontSize: '12px', color: '#888', paddingTop: '2px' }}>{f.label}</span>
                          <span style={{ color: '#cf1322', textDecoration: 'line-through', fontSize: '13px' }}>{fmtVal(f.oldVal)}</span>
                          <span style={{ color: '#bbb' }}>→</span>
                          <span style={{ color: '#389e0d', fontWeight: 500, fontSize: '13px' }}>{fmtVal(f.newVal)}</span>
                        </div>
                      ))}
                    </div>
                  )}

                  {lineDiffs.length > 0 && (
                    <div style={{ marginTop: '16px' }}>
                      <div style={{ fontWeight: 600, fontSize: '11px', color: '#aaa', letterSpacing: '1px', marginBottom: '8px' }}>{lineLabel.toUpperCase()} DETAILS</div>
                      {lineDiffs.map((diff, i) => {
                        const borderColor = diff.type === 'added' ? '#b7eb8f' : diff.type === 'removed' ? '#ffa39e' : '#ffe58f';
                        const headerBg   = diff.type === 'added' ? '#f6ffed' : diff.type === 'removed' ? '#fff1f0' : '#fffbe6';
                        const headerColor = diff.type === 'added' ? '#389e0d' : diff.type === 'removed' ? '#cf1322' : '#d48806';
                        const prefix = diff.type === 'added' ? '+ ' : diff.type === 'removed' ? '− ' : '✎ ';
                        return (
                          <div key={i} style={{ marginBottom: '8px', borderRadius: '6px', overflow: 'hidden', border: `1px solid ${borderColor}` }}>
                            <div style={{ padding: '7px 12px', fontSize: '12px', fontWeight: 600, background: headerBg, color: headerColor }}>
                              {prefix}{lineLabel} {diff.idx + 1}
                              {diff.type === 'modified' && diff.label ? ` — ${diff.label}` : ''}
                              {diff.type !== 'modified' && diff.item?.[descKey] ? ` — ${diff.item[descKey]}` : ''}
                            </div>
                            <div style={{ padding: '8px 12px', background: '#fff' }}>
                              {diff.type === 'modified'
                                ? diff.changes.map(c => (
                                    <div key={c.key} style={{ display: 'flex', alignItems: 'center', gap: '10px', padding: '5px 0', borderBottom: '1px solid #f5f5f5' }}>
                                      <span style={{ width: '120px', flexShrink: 0, fontSize: '12px', color: '#aaa' }}>{c.label}</span>
                                      <span style={{ color: '#cf1322', textDecoration: 'line-through', fontSize: '13px' }}>{fmtVal(c.oldVal)}</span>
                                      <span style={{ color: '#bbb' }}>→</span>
                                      <span style={{ color: '#389e0d', fontWeight: 500, fontSize: '13px' }}>{fmtVal(c.newVal)}</span>
                                    </div>
                                  ))
                                : lineFields.map(f => (
                                    <div key={f.key} style={{ display: 'flex', padding: '5px 0', borderBottom: '1px solid #f5f5f5' }}>
                                      <span style={{ width: '120px', flexShrink: 0, fontSize: '12px', color: '#aaa' }}>{f.label}</span>
                                      <span style={{ fontSize: '13px' }}>{fmtVal(diff.item?.[f.key])}</span>
                                    </div>
                                  ))
                              }
                            </div>
                          </div>
                        );
                      })}
                    </div>
                  )}
                </>
              )}
            </div>
          </div>
        );
      })()}
    </Modal>
</>
  );
};
export default QueueModal;
