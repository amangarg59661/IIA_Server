import React, { useState } from "react";
import { Row, Col, Card, Table, DatePicker, List, Typography, Alert } from "antd";
import {
  AppstoreOutlined,
  CheckCircleOutlined,
  FileDoneOutlined,
  ClockCircleOutlined,
  RightOutlined,
} from "@ant-design/icons";
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  CartesianGrid,
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell,
  Legend,
} from "recharts";
import dayjs from "dayjs";
import DashboardKpiCard from "./DashboardKpiCard";
import StatusBadge from "./StatusBadge";
import useDashboardData from "./useDashboardData";

const { Title, Text } = Typography;
const { RangePicker } = DatePicker;
const STOCK_STATUS_COLORS = {
  "In Stock": "#52c41a",
  "Low Stock": "#faad14",
  "Out of Stock": "#f5222d",
};

/*
 * Store Person ("Inventory Personnel" in the reference screenshots) dashboard
 * — role: "Store Person".
 *
 * REUSE (real, existing today):
 *   GET /api/dashboard/dashboardPendingSummary?roleName=Store Person
 *     -> feeds Pending Actions (IGP/OGP/GT/GRN etc. already tracked)
 *   GET /api/dashboard/dashboardCycleSummary
 *     -> already returns GRN status counts; GRN Completed KPI reads the
 *        GRN/"COMPLETED" bucket out of this rather than a new endpoint
 *
 * NEW — none of these existed as of the last plan review:
 *   GET /api/dashboard/stockSummary
 *     -> { totalStockItems, stockLevelsByCategory: [{category, quantity}],
 *          stockStatus: [{status, count}] }
 *     Backed by OhqMasterConsumableEntity / OhqConsumableStoreStockEntity.
 *   GET /api/dashboard/poGrnPaymentStatus?startDate=&endDate=
 *     -> { posApproved, grnCompletedPos, paymentVoucherPendingPos }
 *   GET /api/dashboard/recentGatePasses?startDate=&endDate=
 *     -> [{ dateTime, gatePassNo, itemCategory, quantity, issuedTo }]
 *   GET /api/dashboard/recentTransferRequests?startDate=&endDate=
 *     -> [{ dateTime, requestNo, itemCategory, from, to, status }]
 */
const StorePersonDashboard = () => {
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
    data: stock,
    loading: stockLoading,
    error: stockError,
  } = useDashboardData("/api/dashboard/stockSummary", {}, [], true);
  const stockUnavailable = !stockLoading && !stock && !!stockError;

  const { data: cycleSummary, loading: cycleLoading } = useDashboardData(
    "/api/dashboard/dashboardCycleSummary",
    {},
    [],
    true
  );
  const grnCompleted = (cycleSummary || [])
    .filter(
      (c) =>
        c.processName === "GRN" &&
        String(c.status).toUpperCase().includes("COMPLET")
    )
    .reduce((sum, c) => sum + (c.count || 0), 0);

  const {
    data: poGrnStatus,
    loading: poGrnLoading,
    error: poGrnError,
  } = useDashboardData(
    "/api/dashboard/poGrnPaymentStatus",
    dateParams,
    [dateParams.startDate, dateParams.endDate],
    true
  );
  const poGrnUnavailable = !poGrnLoading && !poGrnStatus && !!poGrnError;

  const { data: pendingSummary, loading: pendingLoading } = useDashboardData(
    "/api/dashboard/dashboardPendingSummary",
    { roleName: "Store Person" },
    [],
    true
  );

  const { data: gatePasses, loading: gatePassesLoading } = useDashboardData(
    "/api/dashboard/recentGatePasses",
    dateParams,
    [dateParams.startDate, dateParams.endDate],
    true
  );

  const { data: transfers, loading: transfersLoading } = useDashboardData(
    "/api/dashboard/recentTransferRequests",
    dateParams,
    [dateParams.startDate, dateParams.endDate],
    true
  );

  const totalStockStatus = (stock?.stockStatus || []).reduce(
    (sum, s) => sum + (s.count || 0),
    0
  );
  const yetToBeReceived =
    poGrnStatus?.posApproved != null && poGrnStatus?.grnCompletedPos != null
      ? Math.max(poGrnStatus.posApproved - poGrnStatus.grnCompletedPos, 0)
      : undefined;

  const gatePassColumns = [
    { title: "Date & Time", dataIndex: "dateTime", key: "dateTime" },
    { title: "Gate Pass No.", dataIndex: "gatePassNo", key: "gatePassNo" },
    { title: "Item Category", dataIndex: "itemCategory", key: "itemCategory" },
    { title: "Quantity", dataIndex: "quantity", key: "quantity" },
    { title: "Issued To", dataIndex: "issuedTo", key: "issuedTo" },
  ];

  const transferColumns = [
    { title: "Date & Time", dataIndex: "dateTime", key: "dateTime" },
    { title: "Request No.", dataIndex: "requestNo", key: "requestNo" },
    { title: "Item Category", dataIndex: "itemCategory", key: "itemCategory" },
    {
      title: "From → To",
      key: "route",
      render: (_, r) => `${r.from} → ${r.to}`,
    },
    {
      title: "Status",
      dataIndex: "status",
      key: "status",
      render: (s) => <StatusBadge status={s} />,
    },
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
          Inventory Personnel Dashboard
        </Title>
        <RangePicker value={range} onChange={(v) => v && setRange(v)} allowClear={false} />
      </div>

      {(stockError || poGrnError) && (
        <Alert
          type="warning"
          showIcon
          message="Some inventory metrics couldn't be loaded right now."
        />
      )}

      <Row gutter={[16, 16]}>
        <Col xs={24} sm={12} lg={6}>
          <DashboardKpiCard
            icon={<AppstoreOutlined />}
            title="Total Stock Items"
            value={stock?.totalStockItems}
            subtitle="Across Categories"
            loading={stockLoading}
            unavailable={stockUnavailable}
          />
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <DashboardKpiCard
            icon={<CheckCircleOutlined />}
            title="GRN Completed"
            value={grnCompleted}
            subtitle="This Period"
            loading={cycleLoading}
          />
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <DashboardKpiCard
            icon={<FileDoneOutlined />}
            title="POs Approved"
            value={poGrnStatus?.posApproved}
            subtitle="This Period"
            loading={poGrnLoading}
            unavailable={poGrnUnavailable}
          />
        </Col>
        {/* <Col xs={24} sm={12} lg={6}>
          <DashboardKpiCard
            icon={<ClockCircleOutlined />}
            title="Yet to be Received"
            value={yetToBeReceived}
            subtitle="POs"
            loading={poGrnLoading}
            unavailable={poGrnUnavailable}
          />
        </Col> */}
      </Row>

      <Row gutter={[16, 16]}>
        <Col xs={24} lg={13}>
          <Card
            title="Stock Level (Consumables Only)"
            size="small"
            loading={stockLoading}
          >
            {stock?.stockLevelsByCategory?.length ? (
              <div style={{ height: 240 }}>
                <ResponsiveContainer width="100%" height="100%">
                  <BarChart data={stock.stockLevelsByCategory}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="category" tick={{ fontSize: 11 }} />
                    <YAxis allowDecimals={false} />
                    <Tooltip />
                    <Bar dataKey="quantity" fill="#2f54eb" />
                  </BarChart>
                </ResponsiveContainer>
              </div>
            ) : (
              <Text type="secondary">No consumable stock data yet</Text>
            )}
          </Card>
        </Col>
        <Col xs={24} lg={11}>
          <Card
            title="Consumables Stock Status"
            size="small"
            loading={stockLoading}
          >
            {stock?.stockStatus?.length ? (
              <div style={{ height: 240 }}>
                <ResponsiveContainer width="100%" height="100%">
                  <PieChart>
                    <Pie
                      data={stock.stockStatus}
                      dataKey="count"
                      nameKey="status"
                      innerRadius={50}
                      outerRadius={80}
                      label={({ percent }) => `${(percent * 100).toFixed(0)}%`}
                    >
                      {stock.stockStatus.map((entry, i) => (
                        <Cell
                          key={i}
                          fill={STOCK_STATUS_COLORS[entry.status] || "#8c8c8c"}
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
                  Total Items: {totalStockStatus}
                </div>
              </div>
            ) : (
              <Text type="secondary">No stock status data yet</Text>
            )}
          </Card>
        </Col>
      </Row>

      <Row gutter={[16, 16]}>
        <Col xs={24} lg={12}>
          <Card
            title="POs – GRN & Payment Status"
            size="small"
            loading={poGrnLoading}
          >
            <Row gutter={16}>
              <Col span={12}>
                <DashboardKpiCard
                  icon={<CheckCircleOutlined />}
                  title="GRN Completed"
                  value={poGrnStatus?.grnCompletedPos}
                  subtitle="POs"
                  loading={poGrnLoading}
                  unavailable={poGrnUnavailable}
                />
              </Col>
              <Col span={12}>
                <DashboardKpiCard
                  icon={<ClockCircleOutlined />}
                  title="Payment Voucher Pending"
                  value={poGrnStatus?.paymentVoucherPendingPos}
                  subtitle="POs"
                  loading={poGrnLoading}
                  unavailable={poGrnUnavailable}
                />
              </Col>
            </Row>
          </Card>
        </Col>
        <Col xs={24} lg={12}>
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

      <Row gutter={[16, 16]}>
        <Col xs={24} lg={12}>
          <Card
            title="Recent Gate Passes Issued"
            size="small"
            loading={gatePassesLoading}
          >
            <Table
              dataSource={gatePasses || []}
              columns={gatePassColumns}
              rowKey="gatePassNo"
              pagination={false}
              size="small"
              locale={{ emptyText: "No gate passes for this period" }}
            />
          </Card>
        </Col>
        <Col xs={24} lg={12}>
          <Card
            title="Recent Transfer Requests"
            size="small"
            loading={transfersLoading}
          >
            <Table
              dataSource={transfers || []}
              columns={transferColumns}
              rowKey="requestNo"
              pagination={false}
              size="small"
              locale={{ emptyText: "No transfer requests for this period" }}
            />
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default StorePersonDashboard;
