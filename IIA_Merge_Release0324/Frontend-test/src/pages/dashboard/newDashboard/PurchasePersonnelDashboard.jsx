import React, { useState } from "react";
import {
  Row,
  Col,
  Card,
  Table,
  DatePicker,
  List,
  Typography,
  Space,
  Button,
} from "antd";
import {
  FileTextOutlined,
  CheckCircleOutlined,
  ClockCircleOutlined,
  CloseCircleOutlined,
  RightOutlined,
  FileDoneOutlined,
  TeamOutlined,
  BarChartOutlined,
} from "@ant-design/icons";
import {
  PieChart,
  Pie,
  Cell,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from "recharts";
import dayjs from "dayjs";
import DashboardKpiCard from "./DashboardKpiCard";
import StatusBadge from "./StatusBadge";
import useDashboardData from "./useDashboardData";

const { Title } = Typography;
const { RangePicker } = DatePicker;
const PO_STATUS_LABELS = {
  DRAFT: "Draft",
  REJECTED: "Rejected",
  ACCEPTED: "Approved",
};
const normalizePoStatus = (rawStatus) =>
  rawStatus == null ? "In Progress" : PO_STATUS_LABELS[rawStatus] || rawStatus;

const STATUS_COLORS = {
  Approved: "#52c41a",
  "In Progress": "#1890ff",
  Draft: "#faad14",
  Rejected: "#f5222d",
};

/*
 * Purchase Personnel dashboard — role: "Purchase personnel".
 *
 * REUSE (real, existing today):
 *   GET /api/dashboard/dashboardPendingSummary?roleName=Purchase personnel
 *     -> feeds Pending Actions
 *
 * NEW — none of these existed as of the last plan review:
 *   GET /api/dashboard/purchasePersonnelSummary?startDate=&endDate=
 *     -> { totalPos, approvedPos, pendingApprovals, rejectedPos }
 *   GET /api/dashboard/poStatusBreakdown?startDate=&endDate=
 *     -> [{ status, count }]   (status values should follow PurchaseOrder.currentStatus)
 *   GET /api/dashboard/procurementTransactions?startDate=&endDate=
 *     -> [{ poNumber, dateTime, item, vendor, amount, status }]
 *
 * Quick Links intentionally don't navigate anywhere yet — this file doesn't
 * have access to the app's router/route config, so wire the onClick
 * handlers once real paths are confirmed rather than guessing them.
 *
 * NOTE: RangePicker's `value` here uses dayjs, matching antd v5's default.
 * If this project's antd is still on moment, swap the dayjs import/calls
 * for moment equivalents.
 */
const PurchasePersonnelDashboard = () => {
  const [range, setRange] = useState([
    dayjs().startOf("month"),
    dayjs().endOf("month"),
  ]);
  const [startDate, endDate] = range;
  const dateParams = {
    startDate: startDate?.format("YYYY-MM-DD"),
    endDate: endDate?.format("YYYY-MM-DD"),
  };

  const {
    data: summary,
    loading: summaryLoading,
    error: summaryError,
  } = useDashboardData(
    "/api/dashboard/purchasePersonnelSummary",
    dateParams,
    [dateParams.startDate, dateParams.endDate],
    true
  );
  const summaryUnavailable = !summaryLoading && !summary && !!summaryError;

  const { data: statusBreakdown, loading: statusLoading } = useDashboardData(
    "/api/dashboard/poStatusBreakdown",
    dateParams,
    [dateParams.startDate, dateParams.endDate],
    true
  );

  const normalizedStatusBreakdown = React.useMemo(() => {
    const counts = new Map();
    (statusBreakdown || []).forEach((s) => {
      const label = normalizePoStatus(s.status);
      counts.set(label, (counts.get(label) || 0) + (s.count || 0));
    });
    return Array.from(counts, ([status, count]) => ({ status, count }));
  }, [statusBreakdown]);

  const { data: pendingSummary, loading: pendingLoading } = useDashboardData(
    "/api/dashboard/dashboardPendingSummary",
    { roleName: "Purchase personnel" },
    [],
    true
  );

  const { data: transactions, loading: transactionsLoading } =
    useDashboardData(
      "/api/dashboard/procurementTransactions",
      dateParams,
      [dateParams.startDate, dateParams.endDate],
      true
    );

  const totalPos = normalizedStatusBreakdown.reduce(
    (sum, s) => sum + s.count,
    0
  );

  const transactionColumns = [
    { title: "Date & Time", dataIndex: "dateTime", key: "dateTime" },
    { title: "PO Number", dataIndex: "poNumber", key: "poNumber" },
    { title: "Item/Service", dataIndex: "item", key: "item" },
    { title: "Vendor", dataIndex: "vendor", key: "vendor" },
    {
      title: "Amount (₹)",
      dataIndex: "amount",
      key: "amount",
      render: (v) => (v != null ? Number(v).toLocaleString("en-IN") : "--"),
    },
    {
      title: "Status",
      dataIndex: "status",
      key: "status",
      render: (s) => <StatusBadge status={s} />,
    },
  ];

  const quickLinks = [
    { label: "Create PO", icon: <FileTextOutlined /> },
    { label: "My Approvals", icon: <CheckCircleOutlined /> },
    { label: "My Orders", icon: <FileDoneOutlined /> },
    { label: "Suppliers", icon: <TeamOutlined /> },
    { label: "Reports", icon: <BarChartOutlined /> },
  ];

  return (
    <div className="flex flex-col gap-4">
      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          flexWrap: "wrap",
          gap: 8,
        }}
      >
        <Title level={4} style={{ margin: 0 }}>
          Purchase Personnel Dashboard
        </Title>
        <RangePicker value={range} onChange={(v) => v && setRange(v)} allowClear={false} />
      </div>

      <Row gutter={[16, 16]}>
        <Col xs={24} sm={12} lg={6}>
          <DashboardKpiCard
            icon={<FileTextOutlined />}
            title="Total POs"
            value={summary?.totalPos}
            subtitle="This Period"
            loading={summaryLoading}
            unavailable={summaryUnavailable}
          />
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <DashboardKpiCard
            icon={<CheckCircleOutlined />}
            title="Approved POs"
            value={summary?.approvedPos}
            subtitle="This Period"
            loading={summaryLoading}
            unavailable={summaryUnavailable}
          />
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <DashboardKpiCard
            icon={<ClockCircleOutlined />}
            title="Pending Approvals"
            value={summary?.pendingApprovals}
            subtitle="This Period"
            loading={summaryLoading}
            unavailable={summaryUnavailable}
          />
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <DashboardKpiCard
            icon={<CloseCircleOutlined />}
            title="Rejected POs"
            value={summary?.rejectedPos}
            subtitle="This Period"
            loading={summaryLoading}
            unavailable={summaryUnavailable}
          />
        </Col>
      </Row>

      <Row gutter={[16, 16]}>
        <Col xs={24} lg={15}>
          <Card title="Purchase Order Status" size="small" loading={statusLoading}>
            {normalizedStatusBreakdown.length ? (
              <div style={{ height: 260 }}>
                <ResponsiveContainer width="100%" height="100%">
                  <PieChart>
                    <Pie
                    data={normalizedStatusBreakdown}
                      // data={statusBreakdown}
                      dataKey="count"
                      nameKey="status"
                      innerRadius={55}
                      outerRadius={90}
                      label={({ percent }) => `${(percent * 100).toFixed(0)}%`}
                    >
                      {normalizedStatusBreakdown.map((entry, i) => (
                        <Cell
                          key={i}
                          fill={STATUS_COLORS[entry.status] || "#8c8c8c"}
                        />
                      ))}
                    </Pie>
                    <Tooltip />
                    <Legend />
                  </PieChart>
                </ResponsiveContainer>
                <div
                  style={{
                    textAlign: "center",
                    marginTop: -8,
                    color: "#8c8c8c",
                    fontSize: 12,
                  }}
                >
                  Total POs: {totalPos}
                </div>
              </div>
            ) : (
              <div style={{ textAlign: "center", color: "#8c8c8c", padding: "40px 0" }}>
                No PO status data for this period
              </div>
            )}
          </Card>
        </Col>
        <Col xs={24} lg={9}>
          <Card title="Pending Actions" size="small" loading={pendingLoading}>
            <List
              dataSource={pendingSummary || []}
              locale={{ emptyText: "Nothing pending" }}
              renderItem={(item) => (
                <List.Item extra={<RightOutlined style={{ color: "#bfbfbf" }} />}>
                  <List.Item.Meta
                    title={item.processName}
                    description={`${item.pendingCount} item${
                      item.pendingCount === 1 ? "" : "s"
                    }`}
                  />
                </List.Item>
              )}
            />
          </Card>
        </Col>
      </Row>

      <Card
        title="Recent Procurement Transactions"
        size="small"
        loading={transactionsLoading}
      >
        <Table
          dataSource={transactions || []}
          columns={transactionColumns}
          rowKey="poNumber"
          pagination={false}
          size="small"
          locale={{ emptyText: "No transactions for this period" }}
        />
      </Card>

      <Card size="small" title="Quick Links">
        <Space wrap size="middle">
          {quickLinks.map(({ label, icon }) => (
            // TODO: wire onClick to the app's actual route for each link
            <Button key={label} icon={icon}>
              {label}
            </Button>
          ))}
        </Space>
      </Card>
    </div>
  );
};

export default PurchasePersonnelDashboard;
