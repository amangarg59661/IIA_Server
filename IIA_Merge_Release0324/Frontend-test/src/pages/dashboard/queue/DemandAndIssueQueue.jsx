import React, { useEffect, useState } from "react";
import { message, Table, Button, Space } from "antd";
import axios from "axios";
import TableComponent from "../../../components/DKG_Table";
import { useSelector } from "react-redux";

const DemandAndIssueQueue = () => {
  const { role, userId } = useSelector((state) => state?.auth);
  const [dataSource, setDataSource] = useState([]);
  const [loading, setLoading] = useState(false);

  // Fetch Pending DI data
  const fetchDemandAndIssueQueue = async () => {
    try {
      setLoading(true);
      const { data } = await axios.get("/api/process-controller/getPendingDi");

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
          "Error fetching pending Demand & Issue data."
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDemandAndIssueQueue();
  }, []);

  // Approve DI
  const handleApprove = async (record) => {
    try {
      await axios.post("/api/process-controller/approveDi", null, {
        params: { diId: record.id ,actionBy: userId },
      });
      message.success("Demand & Issue approved successfully");
      fetchDemandAndIssueQueue();
    } catch (error) {
      console.error(error);
      message.error(
        error?.response?.data?.responseStatus?.message ||
          "Failed to approve Demand & Issue"
      );
    }
  };

  // Reject DI
  const handleReject = async (record) => {
    try {
      await axios.post("/api/process-controller/rejectDi", null, {
        params: { diId: record.id , actionBy: userId},
      });
      message.success("Demand & Issue rejected successfully");
      fetchDemandAndIssueQueue();
    } catch (error) {
      console.error(error);
      message.error(
        error?.response?.data?.responseStatus?.message ||
          "Failed to reject Demand & Issue"
      );
    }
  };

  // Table Columns
  const columns = [
    { title: "DI ID", dataIndex: "id", key: "id", searchable: true, fixed: "left" },
    { title: "Location", dataIndex: "senderLocationId", key: "senderLocationId", searchable: true },
    { title: "User Id", dataIndex: "senderCustodianId", key: "senderCustodianId", searchable: true },
    { title: "User Name", dataIndex: "senderCustodianName", key: "senderCustodianName", searchable: true },
    { title: "DI Date", dataIndex: "diDate", key: "diDate", searchable: true },
    { title: "Status", dataIndex: "status", key: "status" },
    {
      title: "Material Details",
      dataIndex: "details",
      key: "details",
      render: (details = []) => (
        <Table
          dataSource={details}
          pagination={false}
          rowKey={(r, idx) => r.materialCode || `${r.assetId || "row"}-${idx}`}
          columns={[
            { title: "Material Code", dataIndex: "materialCode", key: "materialCode" },
            { title: "Material Description", dataIndex: "materialDesc", key: "materialDesc" },
            { title: "Asset ID", dataIndex: "assetId", key: "assetId" },
            { title: "Asset Description", dataIndex: "assetDesc", key: "assetDesc" },
            { title: "Quantity", dataIndex: "quantity", key: "quantity" },
            { title: "Sender Locator", dataIndex: "senderLocatorId", key: "senderLocatorId" },
            { title: "Receiver Locator", dataIndex: "receiverLocatorId", key: "receiverLocatorId" },
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
        storageKey="DemandAndIssueQueue_REPORT_COLUMNS"
      />
    </div>
  );
};

export default DemandAndIssueQueue;
