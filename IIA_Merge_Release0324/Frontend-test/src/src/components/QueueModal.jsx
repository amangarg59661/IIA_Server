import React from "react";
import {
  Modal,
  Typography,
  Row,
  Col,
  Spin,
  Collapse,
  Table,
  Tag,
  Divider,
  Empty,
} from "antd";
import {
  AuditOutlined,
  FilePdfOutlined,
  ProjectOutlined,
  SolutionOutlined,
} from "@ant-design/icons";
import { baseURL } from "../App";

const QueueModal = ({ modalVisible, setModalVisible, detailsData }) => {
  const calculateDaysBetween = (start, end) => {
  const [sDay, sMonth, sYear] = start.split("/").map(Number);
  const [eDay, eMonth, eYear] = end.split("/").map(Number);

  const startDate = new Date(sYear, sMonth - 1, sDay);
  const endDate = new Date(eYear, eMonth - 1, eDay);

  const diffTime = endDate - startDate;
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24)); // Convert milliseconds to days
};
const documentLabels = {
  uploadTenderDocuments: "Tender Documents",
  uploadSpecificTermsAndConditions: "Specific Terms & Conditions",
  uploadGeneralTermsAndConditions: "General Terms & Conditions",
  bidSecurityDeclarationFileName: "Bid Security Declaration Format",
  mllStatusDeclarationFileName: "MII Local Content Declaration Format",
};


  return (
    <Modal
      title="Tender Details"
      open={modalVisible}
      onCancel={() => setModalVisible(false)}
      footer={null}
      width={1000}
      className="custom-modal"
      bodyStyle={{ padding: "24px 24px 8px" }}
    >
      {detailsData ? (
        <>
          <style>{`
            .custom-modal .ant-modal-title { font-size: 18px; font-weight: 600; }
            .detail-section { margin-bottom: 24px; padding: 16px; border: 1px solid #f0f0f0; border-radius: 8px; }
            .detail-item { margin-bottom: 12px; font-size: 14px; }
            .detail-item strong { display: inline-block; width: 220px; color: rgba(0, 0, 0, 0.85); }
            .section-title { margin: 16px 0; font-size: 16px; font-weight: 500; }
            .amount { font-weight: 500; color: #1890ff; }
          `}</style>

          {/* Tender Overview */}
          <div className="detail-section">
            <Typography.Title level={5} className="section-title">
              <AuditOutlined /> Tender Overview
            </Typography.Title>
            <Row gutter={24}>
              <Col span={12}>
                <div className="detail-item"><strong>Tender Inviting Authority:</strong> {"Indian Institute Of Astrophysics"}</div>
                <div className="detail-item"><strong>Tender ID:</strong> {detailsData.tenderId || "N/A"}</div>
                <div className="detail-item"><strong>Title:</strong> {detailsData.titleOfTender || "N/A"}</div>
                <div className="detail-item"><strong>Type of Tender:</strong> {detailsData.bidType || "N/A"}</div>
                <div className="detail-item"><strong>Buyer Email:</strong> {"purchase@iiap.res.in"}</div>

              </Col>
              <Col span={12}>
                <div className="detail-item"><strong>Tender Start Date:</strong> {detailsData.openingDate || "N/A"}</div>
                <div className="detail-item"><strong>Tender End Date:</strong> {detailsData.closingDate || "N/A"}</div>
                <div className="detail-item"><strong>Tender Offer Validity(From Tender End Date):</strong> {" "}
                  {detailsData.openingDate && detailsData.closingDate
                  ? calculateDaysBetween(detailsData.openingDate, detailsData.closingDate)
                  : "N/A"}</div>
                <div className="detail-item">
                  <strong>Estimated Tender Value:</strong>
                  <span className="amount">
                    {detailsData.totalTenderValue ? `₹${detailsData.totalTenderValue.toFixed(2)}` : "N/A"}
                  </span>
                </div>
              </Col>
            </Row>
          </div>
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

          {/* Document Details */}
       { /*  <div className="detail-section">
            <Typography.Title level={5} className="section-title">
              <FilePdfOutlined /> Document Details
            </Typography.Title>
            <Row gutter={24}>
              {Object.entries(documentLabels).map(([key, label], idx) => (
                <Col span={8} key={idx}>
                  <div className="detail-item">
                    <strong>{key.replace("upload", "").replace(/([A-Z])/g, " $1")}:</strong>
                    {detailsData[key]
                      ? detailsData[key].split(",").map((fileName, index) => (
                          <div key={index}>
                            <a
                              href={`${baseURL}/file/view/Tender/${fileName.trim()}`}
                              target="_blank"
                              rel="noopener noreferrer"
                            >
                              {fileName.trim()} (View)
                            </a>
                          </div>
                        ))
                      : "N/A"}
                  </div>
                </Col>
              ))}
            </Row>
          </div>*/}
          {/* Document Details */}
<div className="detail-section">
  <Typography.Title level={5} className="section-title">
    <FilePdfOutlined /> Document Details
  </Typography.Title>
  <Row gutter={24}>
    {Object.entries(documentLabels).map(([key, label], idx) => (
      <Col span={8} key={idx}>
        <div className="detail-item">
          <strong>{label}:</strong>
          {detailsData[key]
            ? detailsData[key].split(",").map((fileName, index) => (
                <div key={index}>
                  <a
                    href={`${baseURL}/file/view/Tender/${fileName.trim()}`}
                    target="_blank"
                    rel="noopener noreferrer"
                  >
                    {fileName.trim()} (View)
                  </a>
                </div>
              ))
            : "N/A"}
        </div>
      </Col>
    ))}
  </Row>
</div>


          {/* Commercial Terms & Payment */}
          <div className="detail-section">
            <Row gutter={24}>
              <Col span={12}>
                <Typography.Title level={5} className="section-title">
                  <AuditOutlined /> Commercial Terms
                </Typography.Title>
                <div className="detail-item"><strong>Inco Terms:</strong> {detailsData.incoTerms || "N/A"}</div>
                <div className="detail-item"><strong>Consignee Address:</strong> {detailsData.consignes || "N/A"}</div>
                <div className="detail-item"><strong>Billing Address:</strong> {detailsData.billinngAddress || "N/A"}</div>
              </Col>
            </Row>
          </div>

          {/* Bid Security */}
          {/*detailsData.bidSecurityDeclaration && (
            <div className="detail-section">
              <Typography.Title level={5} className="section-title">
                <ProjectOutlined /> Bid Security
              </Typography.Title>
              <Row gutter={24}>
                <Col span={12}>
                  <div className="detail-item"><strong>Bid Security Declaration:</strong> {String(detailsData.bidSecurityDeclaration)}</div>
                </Col>
                <Col span={12}>
                  <div className="detail-item">
                    <strong>Declaration File:</strong>
                    {detailsData.bidSecurityDeclarationFileName
                      ? detailsData.bidSecurityDeclarationFileName.split(",").map((fileName, index) => (
                          <div key={index}>
                            <a
                              href={`${baseURL}/file/view/Tender/${fileName.trim()}`}
                              target="_blank"
                              rel="noopener noreferrer"
                            >
                              {fileName.trim()} (View)
                            </a>
                          </div>
                        ))
                      : "N/A"}
                  </div>
                </Col>
              </Row>
            </div>
          )*/}

          {/* MLL Status */}
          {/*detailsData.mllStatusDeclaration && (
            <div className="detail-section">
              <Typography.Title level={5} className="section-title">
                <ProjectOutlined /> Mll Status
              </Typography.Title>
              <Row gutter={24}>
                <Col span={12}>
                  <div className="detail-item"><strong>Mll Status Declaration:</strong> {String(detailsData.mllStatusDeclaration)}</div>
                </Col>
                <Col span={12}>
                  <div className="detail-item">
                    <strong>Declaration File:</strong>
                    {detailsData.mllStatusDeclarationFileName
                      ? detailsData.mllStatusDeclarationFileName.split(",").map((fileName, index) => (
                          <div key={index}>
                            <a
                              href={`${baseURL}/file/view/Tender/${fileName.trim()}`}
                              target="_blank"
                              rel="noopener noreferrer"
                            >
                              {fileName.trim()} (View)
                            </a>
                          </div>
                        ))
                      : "N/A"}
                  </div>
                </Col>
              </Row>
            </div>
          )*/}

          {/* Associated Indents */}
          {detailsData.indentResponseDTO?.length > 0 && (
  <div className="detail-section">
    <Typography.Title level={5} className="section-title">
      <SolutionOutlined /> Material Requirements
    </Typography.Title>

    {detailsData.indentResponseDTO.map((indent, index) => (
      <div key={index} style={{ marginBottom: 24 }}>
        {indent.materialDetails?.length > 0 ? (
          <Table
            dataSource={indent.materialDetails}
            pagination={false}
            bordered
            size="small"
            rowKey="materialCode"
            columns={[
             // { title: "Material Code", dataIndex: "materialCode", width: 120 },
              { title: "Description", dataIndex: "materialDescription", ellipsis: true },
              { title: "Quantity", dataIndex: "quantity", align: "right" },
              { title: "UOM", dataIndex: "uom", width: 100 },
              { title: "Unit Price", dataIndex: "unitPrice", align: "right", render: text => `₹${text?.toFixed(2)}` },
              { title: "Total Price", dataIndex: "totalPrice", align: "right", render: text => <strong>₹{text?.toFixed(2)}</strong> },
             // { title: "Budget Code", dataIndex: "budgetCode", width: 120 },
            ]}
          />
        ) : (
          <div style={{ textAlign: "center", padding: 16 }}>
            <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} description="No material details found" />
          </div>
        )}
      </div>
    ))}
  </div>
)}

        </>
      ) : (
        <div style={{ textAlign: "center", padding: "40px 0" }}>
          <Spin tip="Loading details..." size="large" />
        </div>
      )}
    </Modal>
  );
};

export default QueueModal;
