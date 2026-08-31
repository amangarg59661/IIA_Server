import React, { useEffect, useState } from "react";
import { message, Table, Button, Space } from "antd";
import axios from "axios";
import TableComponent from "../../../components/DKG_Table";
import { useSelector } from "react-redux";

const AssetDisposalApproval = () => {
  const { role, userId } = useSelector((state) => state?.auth);
  const [dataSource, setDataSource] = useState([]);
  const [loading, setLoading] = useState(false);

  // Fetch Asset Disposal data
  const fetchAssetDisposalData = async () => {
    try {
      setLoading(true);
      const { data } = await axios.get("/api/process-controller/AssetDisposalApproval");

      const rows = (data?.responseData || []).map((item) => ({
        ...item,
        status: item.status || "AWAITING APPROVAL",
        details: item.materialDtlList || [],
      }));

      setDataSource(rows);
    } catch (error) {
      console.error(error);
      message.error(
        error?.response?.data?.responseStatus?.message ||
          "Error fetching Asset Disposal data."
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAssetDisposalData();
  }, []);

  // Approve Asset Disposal
  const handleApprove = async (record) => {
    try {
    await axios.post("/api/process-controller/approveAssetDisposal", null, {
  params: { disposalId: record.disposalId , actionBy: userId},
});

      message.success("Asset Disposal approved successfully");
      fetchAssetDisposalData();
    } catch (error) {
      console.error(error);
      message.error(
        error?.response?.data?.responseStatus?.message ||
          "Failed to approve Asset Disposal"
      );
    }
  };

  // Reject Asset Disposal
  const handleReject = async (record) => {
    try {
    await axios.post("/api/process-controller/rejectAssetDisposal", null, {
      params: { disposalId: record.disposalId , actionBy: userId},
    });
      message.success("Asset Disposal rejected successfully");
      fetchAssetDisposalData();
    } catch (error) {
      console.error(error);
      message.error(
        error?.response?.data?.responseStatus?.message ||
          "Failed to reject Asset Disposal"
      );
    }
  };

  // Table Columns
  const columns = [
    { title: "Disposal ID", dataIndex: "disposalId", key: "id", searchable: true, fixed: "left" },
    { title: "Location", dataIndex: "locationId", key: "locationId", searchable: true },
    { title: "Custodian ID", dataIndex: "custodianId", key: "custodianId", searchable: true },
     { title: "Custodian Name", dataIndex: "custodianName", key: "custodianName", searchable: true },
    { title: "Disposal Date", dataIndex: "disposalDate", key: "disposalDate", searchable: true },
    { title: "Status", dataIndex: "status", key: "status" },
    {
      title: "Material Details",
      dataIndex: "details",
      key: "details",
      render: (details = []) => (
        <Table
          dataSource={details}
          pagination={false}
          rowKey={(r, idx) => r.assetId || `row-${idx}`}
          columns={[
            { title: "Asset ID", dataIndex: "assetId", key: "assetId" },
            { title: "Asset Code", dataIndex: "assetCode", key: "assetCode" },
            { title: "Asset Description", dataIndex: "assetDesc", key: "assetDesc" },
            { title: "Quantity", dataIndex: "quantity", key: "quantity" },
            { title: "Disposal Category", dataIndex: "disposalCategory", key: "disposalCategory" },
            { title: "Disposal Mode", dataIndex: "disposalMode", key: "disposalMode" },
            { title: "Sales Note File", dataIndex: "salesNoteFilename", key: "salesNoteFilename" },
            { title: "Locator", dataIndex: "locatorId", key: "locatorId" },
            { title: "Book Value", dataIndex: "bookValue", key: "bookValue" },
            { title: "Depreciation Rate", dataIndex: "depriciationRate", key: "depriciationRate" },
            { title: "Unit Price", dataIndex: "unitPrice", key: "unitPrice" },
            { title: "PO Id", dataIndex: "poId", key: "poId" },
            { title: "PO Value", dataIndex: "poValue", key: "poValue" },
            { title: "Po Date", dataIndex: "poDate", key: "poDate" },
            { title: "Serial No", dataIndex: "serialNo", key: "serialNo" },
            { title: "Model No", dataIndex: "modelNo", key: "modelNo" },
            { title: "Reason For Disposal", dataIndex: "reasonForDisposal", key: "reasonForDisposal" },
          ]}
        />
      ),
    },
    {
      title: "Actions",
      key: "actions",
      fixed: "right",
      render: (_, record) => {
        if (record.status === "AWAITING APPROVAL") {
          return (
            <Space>
              <Button type="primary" onClick={() => handleApprove(record)}>Approve</Button>
              <Button danger onClick={() => handleReject(record)}>Reject</Button>
            </Space>
          );
        }
        return null;
      },
    },
  ];

  return (
    <div>
      <TableComponent
        dataSource={dataSource}
        columns={columns}
        loading={loading}
        storageKey="AssetDisposalApproval_REPORT_COLUMNS"
      />
    </div>
  );
};

export default AssetDisposalApproval;
