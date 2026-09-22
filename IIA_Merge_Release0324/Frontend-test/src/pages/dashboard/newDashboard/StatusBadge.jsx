import React from "react";
import { Tag } from "antd";

// Central place mapping the many raw status strings already in use across
// Indent / PO / SO / CP / GRN / workflow modules to one consistent label +
// color, so all 5 dashboards look like one product instead of five.
// Extend this map as real status values are confirmed per module — this is
// a best-effort starting set based on values seen in the codebase
// ("AWAITING APPROVAL", "COMPLETED") plus the reference screenshots'
// vocabulary ("Pending Approval", "In Progress", "Delivered", etc.).
const STATUS_STYLE_MAP = {
  "AWAITING APPROVAL": { label: "Pending Approval", color: "gold" },
  PENDING: { label: "Pending Approval", color: "gold" },
  "PENDING APPROVAL": { label: "Pending Approval", color: "gold" },
  "IN-PROGRESS": { label: "In Progress", color: "blue" },
  "IN PROGRESS": { label: "In Progress", color: "blue" },
  APPROVED: { label: "Approved", color: "green" },
  COMPLETED: { label: "Delivered", color: "green" },
  DELIVERED: { label: "Delivered", color: "cyan" },
  REJECTED: { label: "Rejected", color: "red" },
  CANCELLED: { label: "Cancelled", color: "default" },
  CANCELED: { label: "Cancelled", color: "default" },
};

const StatusBadge = ({ status }) => {
  if (!status) return <Tag>--</Tag>;
  const key = String(status).trim().toUpperCase();
  const style = STATUS_STYLE_MAP[key] || { label: status, color: "default" };
  return <Tag color={style.color}>{style.label}</Tag>;
};

export default StatusBadge;
