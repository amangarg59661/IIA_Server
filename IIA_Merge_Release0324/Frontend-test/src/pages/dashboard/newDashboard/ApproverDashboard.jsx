import React, { useState } from "react";
import { useSelector } from "react-redux";
import axios from "axios";
import {
  Row,
  Col,
  Card,
  Table,
  Button,
  Space,
  Alert,
  Typography,
  message,
  Popconfirm,
} from "antd";
import {
  ClockCircleOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  FieldTimeOutlined,
} from "@ant-design/icons";
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  Legend,
  CartesianGrid,
  ResponsiveContainer,
} from "recharts";
import DashboardKpiCard from "./DashboardKpiCard";
import useDashboardData from "./useDashboardData";

const { Title } = Typography;

/*
 * Approver dashboard — covers every role NOT mapped to Indentor / SPO /
 * Purchase Personnel / Store Person (Purchase Head, CP/PO/SO Creator,
 * Reporting Officer, Project Head, Administrative Officer, Director, Dean,
 * Head SEG, Engineer In-Charge, Professor In-Charge, Computer Committee
 * Chairman, Account/Accounts Officer, Billing Section Personnel, etc.)
 *
 * CONFIRMED REAL endpoint:
 *   GET /api/dashboard/dashboardPendingSummary?roleName=
 *     -> used for the Pending Approvals KPI total (sum of pendingCount)
 *
 * ASSUMED but NOT yet confirmed against a live controller — I've seen the
 * underlying service methods (WorkflowServiceImpl.allPendingWorkflowTransitionINQueue
 * / performTransitionAction) but not the controller that exposes them, so
 * verify these paths before relying on this in production:
 *   GET  /api/dashboard/approverQueue?roleName=&userId=
 *     -> QueueResponse[] : { workflowTransitionId, requestId, itemName,
 *                            indentorName, amount, ... }
 *     (thin wrapper around WorkflowServiceImpl.allPendingWorkflowTransitionINQueue)
 *   POST /api/workflow/performTransitionAction
 *     -> body: { workflowTransitionId, requestId, actionBy, action: "APPROVE"|"REJECT", remarks }
 *   GET  /api/dashboard/approverSummary?userId=
 *     -> { approvedThisMonth, rejectedThisMonth, avgApprovalTimeDays }
 *   GET  /api/dashboard/approvalTrend?userId=
 *     -> [{ month, approved, rejected }]
 *
 * Note: performTransitionAction returns no usable payload on the backend
 * today (confirmed — it always returns null), so we refetch the queue after
 * every action instead of trusting the response body.
 */
const ApproverDashboard = () => {
  const auth = useSelector((state) => state.auth);
  const roleName = auth?.role;
  const userId = auth?.userId;
  const [actingId, setActingId] = useState(null);

  const { data: pendingSummary, loading: pendingSummaryLoading } =
    useDashboardData(
      "/api/dashboard/dashboardPendingSummary",
      { roleName },
      [roleName],
      !!roleName
    );
  const pendingTotal = (pendingSummary || []).reduce(
    (sum, p) => sum + (p.pendingCount || 0),
    0
  );

  const {
    data: approverSummary,
    loading: approverSummaryLoading,
    error: approverSummaryError,
  } = useDashboardData(
    "/api/dashboard/approverSummary",
    { userId },
    [userId],
    !!userId
  );
  const approverSummaryUnavailable =
    !approverSummaryLoading && !approverSummary && !!approverSummaryError;

  const {
    data: queue,
    loading: queueLoading,
    error: queueError,
    refetch: refetchQueue,
  } = useDashboardData(
    "/api/dashboard/approverQueue",
    { roleName, userId },
    [roleName, userId],
    !!roleName && !!userId
  );

  const { data: trend, loading: trendLoading } = useDashboardData(
    "/api/dashboard/approvalTrend",
    { userId },
    [userId],
    !!userId
  );

  const handleAction = async (record, action) => {
    setActingId(record.workflowTransitionId);
    try {
      await axios.post("/api/workflow/performTransitionAction", {
        workflowTransitionId: record.workflowTransitionId,
        requestId: record.requestId,
        actionBy: userId,
        action,
        remarks: "",
      });
      message.success(
        action === "APPROVE" ? "Request approved" : "Request rejected"
      );
      refetchQueue();
    } catch (err) {
      console.error("performTransitionAction failed", err);
      message.error("Action failed — please try again.");
    } finally {
      setActingId(null);
    }
  };

  const columns = [
    { title: "PR No.", dataIndex: "requestId", key: "requestId" },
    {
      title: "Item",
      dataIndex: "itemName",
      key: "itemName",
      render: (v) => v || "--",
    },
    {
      title: "Requester",
      dataIndex: "indentorName",
      key: "indentorName",
      render: (v) => v || "--",
    },
    {
      title: "Amount",
      dataIndex: "amount",
      key: "amount",
      render: (v) => (v != null ? `₹${Number(v).toLocaleString("en-IN")}` : "--"),
    },
    {
      title: "Action",
      key: "action",
      render: (_, record) => (
        <Space>
          <Popconfirm
            title="Approve this request?"
            onConfirm={() => handleAction(record, "APPROVE")}
          >
            <Button
              size="small"
              type="primary"
              loading={actingId === record.workflowTransitionId}
            >
              Approve
            </Button>
          </Popconfirm>
          <Popconfirm
            title="Reject this request?"
            onConfirm={() => handleAction(record, "REJECT")}
          >
            <Button
              size="small"
              danger
              loading={actingId === record.workflowTransitionId}
            >
              Reject
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div className="flex flex-col gap-4">
      <Title level={4} style={{ margin: 0 }}>
        Approver Dashboard
      </Title>

      {(approverSummaryError || queueError) && (
        <Alert
          type="warning"
          showIcon
          message="Some approval metrics couldn't be loaded right now."
        />
      )}

      <Row gutter={[16, 16]}>
        <Col xs={24} sm={12} lg={6}>
          <DashboardKpiCard
            icon={<ClockCircleOutlined />}
            title="Pending Approvals"
            value={pendingTotal}
            subtitle="Requires Action"
            subtitleColor="#d48806"
            loading={pendingSummaryLoading}
          />
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <DashboardKpiCard
            icon={<CheckCircleOutlined />}
            title="Approved Requests"
            value={approverSummary?.approvedThisMonth}
            subtitle="This Month"
            loading={approverSummaryLoading}
            unavailable={approverSummaryUnavailable}
          />
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <DashboardKpiCard
            icon={<CloseCircleOutlined />}
            title="Rejected Requests"
            value={approverSummary?.rejectedThisMonth}
            subtitle="This Month"
            loading={approverSummaryLoading}
            unavailable={approverSummaryUnavailable}
          />
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <DashboardKpiCard
            icon={<FieldTimeOutlined />}
            title="Avg. Approval Time"
            value={approverSummary?.avgApprovalTimeDays}
            suffix={approverSummary?.avgApprovalTimeDays != null ? " days" : undefined}
            subtitle="This Month"
            loading={approverSummaryLoading}
            unavailable={approverSummaryUnavailable}
          />
        </Col>
      </Row>

      <Row gutter={[16, 16]}>
        <Col xs={24} lg={15}>
          <Card
            title="Pending Approval Requests"
            size="small"
            loading={queueLoading}
          >
            <Table
              dataSource={queue || []}
              columns={columns}
              rowKey={(r) => r.workflowTransitionId ?? r.requestId}
              pagination={false}
              size="small"
              locale={{ emptyText: "No pending approvals" }}
            />
          </Card>
        </Col>
        <Col xs={24} lg={9}>
          <Card title="Approval Trend" size="small" loading={trendLoading}>
            {trend && trend.length ? (
              <div style={{ height: 260 }}>
                <ResponsiveContainer width="100%" height="100%">
                  <BarChart data={trend}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="month" />
                    <YAxis allowDecimals={false} />
                    <Tooltip />
                    <Legend />
                    <Bar dataKey="approved" name="Approved" fill="#52c41a" />
                    <Bar dataKey="rejected" name="Rejected" fill="#f5222d" />
                  </BarChart>
                </ResponsiveContainer>
              </div>
            ) : (
              <div style={{ textAlign: "center", color: "#8c8c8c", padding: "40px 0" }}>
                No trend data yet
              </div>
            )}
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default ApproverDashboard;
