import React from "react";
import { useSelector } from "react-redux";
import { Row, Col, Card, Table, Alert, Empty, Typography } from "antd";
import {
  FileTextOutlined,
  CheckCircleOutlined,
  InboxOutlined,
  AppstoreOutlined,
} from "@ant-design/icons";
import {
  PieChart,
  Pie,
  Cell,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from "recharts";
import DashboardKpiCard from "./DashboardKpiCard";
import StatusBadge from "./StatusBadge";
import useDashboardData from "./useDashboardData";

const { Title } = Typography;
const PIE_COLORS = ["#2f54eb", "#722ed1", "#13c2c2", "#fa8c16", "#8c8c8c"];

/*
 * Indentor ("End User") dashboard — role: "Indent Creator".
 *
 * None of the endpoints below existed as of the last plan review, so every
 * one is wired defensively (loading / error / empty) rather than assuming
 * success. Confirm exact paths + response shapes against the real
 * controllers before treating this as production-ready:
 *
 *   GET /api/dashboard/indentorSummary?userId=
 *     -> { myPurchaseRequests, myApprovedRequestsThisMonth,
 *          myReceivedItemsThisMonth, inventoryStatusPercent }
 *   GET /api/indent/myRequests?userId=&limit=5
 *     -> [{ requestId, item, status, requestedOn }]
 *   GET /api/dashboard/indentorTopItems?userId=
 *     -> [{ itemName, count }]
 */
const IndentorDashboard = () => {
  const auth = useSelector((state) => state.auth);
  const userId = auth?.userId;

  const {
    data: summary,
    loading: summaryLoading,
    error: summaryError,
  } = useDashboardData(
    "/api/dashboard/indentorSummary",
    { userId },
    [userId],
    !!userId
  );

  const { data: recentRequests, loading: requestsLoading } = useDashboardData(
    "/api/dashboard/indentorRecentRequests",
    { userId, limit: 5 },
    [userId],
    !!userId
  );

  const { data: topItems, loading: topItemsLoading } = useDashboardData(
    "/api/dashboard/indentorTopItems",
    { userId },
    [userId],
    !!userId
  );

  const totalTopItems = (topItems || []).reduce(
    (sum, i) => sum + (i.count || 0),
    0
  );
  const summaryUnavailable = !summaryLoading && !summary && !!summaryError;

  const columns = [
    { title: "PR No.", dataIndex: "requestId", key: "requestId" },
    { title: "Item", dataIndex: "item", key: "item" },
    {
      title: "Status",
      dataIndex: "status",
      key: "status",
      render: (s) => <StatusBadge status={s} />,
    },
    { title: "Requested On", dataIndex: "requestedOn", key: "requestedOn" },
  ];

  return (
    <div className="flex flex-col gap-4">
      <Title level={4} style={{ margin: 0 }}>
        End User Dashboard
      </Title>

      {summaryError && (
        <Alert
          type="warning"
          showIcon
          message="Couldn't load your request summary right now."
        />
      )}

      <Row gutter={[16, 16]}>
        <Col xs={24} sm={12} lg={6}>
          <DashboardKpiCard
            icon={<FileTextOutlined />}
            title="My Purchase Requests"
            value={summary?.myPurchaseRequests}
            subtitle="Pending"
            loading={summaryLoading}
            unavailable={summaryUnavailable}
          />
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <DashboardKpiCard
            icon={<CheckCircleOutlined />}
            title="My Approved Requests"
            value={summary?.myApprovedRequestsThisMonth}
            subtitle="This Month"
            loading={summaryLoading}
            unavailable={summaryUnavailable}
          />
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <DashboardKpiCard
            icon={<InboxOutlined />}
            title="My Received Items"
            value={summary?.myReceivedItemsThisMonth}
            subtitle="This Month"
            loading={summaryLoading}
            unavailable={summaryUnavailable}
          />
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <DashboardKpiCard
            icon={<AppstoreOutlined />}
            title="Inventory Status (My Dept.)"
            value={summary?.inventoryStatusPercent}
            suffix={summary?.inventoryStatusPercent != null ? "%" : undefined}
            subtitle="In Stock"
            loading={summaryLoading}
            unavailable={summaryUnavailable}
          />
        </Col>
      </Row>

      <Row gutter={[16, 16]}>
        <Col xs={24} lg={15}>
          <Card
            title="Recent Purchase Requests"
            size="small"
            loading={requestsLoading}
          >
            <Table
              dataSource={recentRequests || []}
              columns={columns}
              rowKey="requestId"
              pagination={false}
              size="small"
              locale={{ emptyText: "No recent requests" }}
            />
          </Card>
        </Col>
        <Col xs={24} lg={9}>
          <Card
            title="Top Requested Items"
            size="small"
            loading={topItemsLoading}
          >
            {topItems && topItems.length ? (
              <div style={{ height: 340 }}>
                <ResponsiveContainer width="100%" height="100%">
                  <PieChart>
                    <Pie
                      data={topItems}
                      dataKey="count"
                      nameKey="itemName"
                      innerRadius={50}
                      outerRadius={80}
                      label={({ percent }) => `${(percent * 100).toFixed(0)}%`}
                    >
                      {topItems.map((_, i) => (
                        <Cell key={i} fill={PIE_COLORS[i % PIE_COLORS.length]} />
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
                  Total Requests: {totalTopItems}
                </div>
              </div>
            ) : (
              <Empty description="No request data yet" />
            )}
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default IndentorDashboard;
