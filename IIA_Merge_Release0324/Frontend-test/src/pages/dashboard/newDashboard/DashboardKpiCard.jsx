import React from "react";
import { Card, Statistic, Skeleton, Typography } from "antd";

const { Text, Link } = Typography;

/**
 * Shared KPI tile used across all 5 role dashboards (Indentor, Approver,
 * SPO, Purchase Personnel, Store Person) so they read as one product
 * instead of five different templates.
 *
 * Deliberately never falls back to 0 or a guessed number:
 * - while loading, shows a skeleton
 * - if the metric has no real backend source yet (`unavailable`), shows
 *   "Not available" instead of fabricating a value
 * - otherwise shows `value`, or "--" if the field came back null
 */
const DashboardKpiCard = ({
  icon,
  title,
  value,
  suffix,
  subtitle,
  subtitleColor,
  loading,
  unavailable,
  actionLabel,
  onAction,
}) => {
  return (
    <Card size="small" hoverable={!!onAction} onClick={onAction}>
      <div style={{ display: "flex", alignItems: "flex-start", gap: 12 }}>
        {icon && (
          <div
            style={{
              width: 36,
              height: 36,
              borderRadius: 8,
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              background: "#f0f5ff",
              color: "#2f54eb",
              fontSize: 18,
              flexShrink: 0,
            }}
          >
            {icon}
          </div>
        )}
        <div style={{ flex: 1, minWidth: 0 }}>
          {loading ? (
            <Skeleton active title={false} paragraph={{ rows: 2 }} />
          ) : unavailable ? (
            <>
              <div>
                <Text type="secondary" style={{ fontSize: 12 }}>
                  {title}
                </Text>
              </div>
              <div style={{ fontSize: 18, fontWeight: 600, color: "#bfbfbf" }}>
                Not available
              </div>
            </>
          ) : (
            <Statistic
              title={
                <Text type="secondary" style={{ fontSize: 12 }}>
                  {title}
                </Text>
              }
              value={value ?? "--"}
              suffix={suffix}
            />
          )}
          {!loading && !unavailable && subtitle && (
            <Text style={{ fontSize: 12, color: subtitleColor || "#8c8c8c" }}>
              {subtitle}
            </Text>
          )}
          {!loading && actionLabel && (
            <div>
              <Link onClick={onAction} style={{ fontSize: 12 }}>
                {actionLabel} →
              </Link>
            </div>
          )}
        </div>
      </div>
    </Card>
  );
};

export default DashboardKpiCard;
