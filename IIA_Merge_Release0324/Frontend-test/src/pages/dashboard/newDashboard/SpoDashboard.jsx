import React from "react";
import { Row, Col, Card, Table, Typography, Tooltip as AntTooltip } from "antd";
import {
  WalletOutlined,
  FileDoneOutlined,
  DatabaseOutlined,
  TeamOutlined,
  InfoCircleOutlined,
} from "@ant-design/icons";
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  CartesianGrid,
  ResponsiveContainer,
} from "recharts";
import DashboardKpiCard from "./DashboardKpiCard";
import useDashboardData from "./useDashboardData";

const { Title, Text } = Typography;

/*
 * SPO / Procurement & Inventory Officer dashboard — role: "Store Purchase Officer".
 *
 * None of these endpoints existed as of the last plan review — all NEW:
 *   GET /api/dashboard/spoSummary
 *     -> { totalSpendYtd, spendYoyChangePercent, activePos, inventoryValue,
 *          supplierPerformancePercent }   (supplierPerformancePercent should
 *          be null until a real metric exists — see note below)
 *   GET /api/dashboard/spoSpendByCategory
 *     -> [{ category, amount }]
 *     Currently only reliably computable from Contingency Purchase line
 *     items (CpMaterials.materialCategory) — PurchaseOrderAttributes has no
 *     category field, so PO-driven spend can't be broken out by category
 *     without a join to a material master table I haven't seen yet.
 *   GET /api/dashboard/spoRecentActivity
 *     -> [{ time, activity, details }]
 *
 * Supplier Performance: confirmed (against VendorMasterServiceImpl and
 * VendorMasterUtilServiceImpl) that no rating/score/on-time-delivery field
 * exists anywhere in the vendor module today. This card is written to show
 * "Not available" rather than a fabricated percentage — decide separately
 * whether to build a real on-time-delivery approximation, substitute a
 * different metric, or drop the card.
 */
const SpoDashboard = () => {
  const {
    data: summary,
    loading: summaryLoading,
    error: summaryError,
  } = useDashboardData("/api/dashboard/spoSummary", {}, [], true);
  const summaryUnavailable = !summaryLoading && !summary && !!summaryError;

  const { data: spendByCategory, loading: spendLoading } = useDashboardData(
    "/api/dashboard/spoSpendByCategory",
    {},
    [],
    true
  );

  const { data: activity, loading: activityLoading } = useDashboardData(
    "/api/dashboard/spoRecentActivity",
    {},
    [],
    true
  );

  const formatCurrency = (v) =>
    v != null ? `₹${Number(v).toLocaleString("en-IN")}` : undefined;

  const activityColumns = [
    { title: "Time", dataIndex: "time", key: "time" },
    { title: "Activity", dataIndex: "activity", key: "activity" },
    { title: "Details", dataIndex: "details", key: "details" },
  ];

  return (
    <div className="flex flex-col gap-4">
      <Title level={4} style={{ margin: 0 }}>
        Procurement & Inventory Officer Dashboard
      </Title>

      <Row gutter={[16, 16]}>
        <Col xs={24} sm={12} lg={6}>
          <DashboardKpiCard
            icon={<WalletOutlined />}
            title="Total Spend (YTD)"
            value={formatCurrency(summary?.totalSpendYtd)}
            subtitle={
              summary?.spendYoyChangePercent != null
                ? `${summary.spendYoyChangePercent > 0 ? "↑" : "↓"} ${Math.abs(
                    summary.spendYoyChangePercent
                  )}% vs last year`
                : undefined
            }
            loading={summaryLoading}
            unavailable={summaryUnavailable}
          />
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <DashboardKpiCard
            icon={<FileDoneOutlined />}
            title="Active POs"
            value={summary?.activePos}
            loading={summaryLoading}
            unavailable={summaryUnavailable}
          />
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <DashboardKpiCard
            icon={<DatabaseOutlined />}
            title="Inventory Value"
            value={formatCurrency(summary?.inventoryValue)}
            loading={summaryLoading}
            unavailable={summaryUnavailable}
          />
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <DashboardKpiCard
            icon={<TeamOutlined />}
            title={
              <span>
                Supplier Performance{" "}
                <AntTooltip title="No real data source exists for this yet">
                  <InfoCircleOutlined style={{ color: "#bfbfbf" }} />
                </AntTooltip>
              </span>
            }
            value={summary?.supplierPerformancePercent}
            suffix={summary?.supplierPerformancePercent != null ? "%" : undefined}
            subtitle={
              summary?.supplierPerformancePercent != null
                ? "On Time Delivery"
                : undefined
            }
            loading={summaryLoading}
            unavailable={!summaryLoading && summary?.supplierPerformancePercent == null}
          />
        </Col>
      </Row>

      <Row gutter={[16, 16]}>
        <Col xs={24} lg={9}>
          <Card
            title="Procurement Spend by Category"
            size="small"
            loading={spendLoading}
          >
            {spendByCategory && spendByCategory.length ? (
              <div style={{ height: 260 }}>
                <ResponsiveContainer width="100%" height="100%">
                  <BarChart
                    data={spendByCategory}
                    layout="vertical"
                    margin={{ left: 24 }}
                  >
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis type="number" />
                    <YAxis type="category" dataKey="category" width={100} />
                    <Tooltip />
                    <Bar dataKey="amount" fill="#2f54eb" />
                  </BarChart>
                </ResponsiveContainer>
              </div>
            ) : (
              <Text type="secondary">No category spend data yet</Text>
            )}
          </Card>
        </Col>
        <Col xs={24} lg={15}>
          <Card title="Recent Activities" size="small" loading={activityLoading}>
            <Table
              dataSource={activity || []}
              columns={activityColumns}
              rowKey={(r, idx) => `${r.time}-${idx}`}
              pagination={false}
              size="small"
              locale={{ emptyText: "No recent activity" }}
            />
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default SpoDashboard;
