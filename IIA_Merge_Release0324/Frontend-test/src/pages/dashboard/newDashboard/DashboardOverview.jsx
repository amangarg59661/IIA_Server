
// import React, { useEffect, useState } from "react";
// import { Card, Row, Col, Statistic, Table, Tag, Spin, Empty, Modal } from "antd";
// import axios from "axios";
// import { useSelector } from "react-redux";
// import { BarChart, Bar, XAxis, YAxis, Tooltip, CartesianGrid, ResponsiveContainer, PieChart, Pie, Cell, Legend } from "recharts";

// const PURCHASE_STORES_ROLES = [
//   "Store Purchase Officer", "Purchase personnel", "Store Person", "Purchase Head",
//   "CP Creator", "PO Creator", "SO Creator",
// ];

// const daysColor = (days) => (days >= 7 ? "#cf1322" : days >= 3 ? "#d48806" : "#3f8600");

// const DashboardOverview = () => {
//   const auth = useSelector((state) => state.auth);
//   const roleName = auth.role;
//   const userId = auth.userId;
//   const isPurchaseAndStores = PURCHASE_STORES_ROLES.includes(roleName);

//   const [pending, setPending] = useState([]);
//   const [activity, setActivity] = useState([]);
//   const [cycleStatus, setCycleStatus] = useState([]);
//   const [loading, setLoading] = useState(false);
//   const [detailModal, setDetailModal] = useState({ open: false, processName: null, items: [], loading: false });


//   const PIE_COLORS = ["#0088FE", "#00C49F", "#FFBB28", "#FF8042", "#8884d8"];

//   const openProcessDetail = async (processName) => {
//     setDetailModal({ open: true, processName, items: [], loading: true });
//     try {
//       const { data } = await axios.get("/api/dashboard/dashboardPendingDetail", {
//         params: { roleName, processName },
//       });
//       setDetailModal({ open: true, processName, items: data.responseData || [], loading: false });
//     } catch (err) {
//       console.error("Failed to fetch pending detail", err);
//       setDetailModal({ open: true, processName, items: [], loading: false });
//     }
//   };

//   useEffect(() => {
//     if (!roleName || !userId) return;
//     setLoading(true);
//     const calls = [
//       axios.get("/api/dashboard/dashboardPendingSummary", { params: { roleName } }),
//       axios.get("/api/dashboard/dashboardTodayActivity", { params: { roleName, userId } }),
//     ];
//     if (isPurchaseAndStores) {
//       calls.push(axios.get("/api/dashboard/dashboardCycleSummary"));
//     }
//     Promise.all(calls)
//       .then(([pendingRes, activityRes, cycleRes]) => {
//         setPending(pendingRes.data.responseData || []);
//         setActivity(activityRes.data.responseData || []);
//         if (cycleRes) setCycleStatus(cycleRes.data.responseData || []);
//       })
//       .catch((err) => console.error("Dashboard overview fetch failed", err))
//       .finally(() => setLoading(false));
//   }, [roleName, userId, isPurchaseAndStores]);

//   const activityColumns = [
//     { title: "Process", dataIndex: "processName", key: "processName" },
//     { title: "Request", dataIndex: "requestId", key: "requestId" },
//     { title: "Action", dataIndex: "action", key: "action" },
//     {
//       title: "Type",
//       dataIndex: "activityType",
//       key: "activityType",
//       render: (t) => <Tag color={t === "CREATED" ? "blue" : "green"}>{t}</Tag>,
//     },
//     {
//       title: "Time",
//       dataIndex: "activityTime",
//       key: "activityTime",
//       render: (t) => (t ? new Date(t).toLocaleString("en-IN", { hour: "2-digit", minute: "2-digit" }) : "--"),
//     },
//   ];

//   const cycleColumns = [
//     { title: "Process", dataIndex: "processName", key: "processName" },
//     { title: "Status", dataIndex: "status", key: "status" },
//     { title: "Count", dataIndex: "count", key: "count" },
//   ];

//   return (
//     <Spin spinning={loading}>
//       <div className="flex flex-col gap-4 mb-4">
//         <Card title="Pending Approvals by Process" size="small">
//           {pending.length ? (
//             <>
//               <Row gutter={[16, 16]}>
//                 {pending.map((p) => (
//                   <Col key={p.processName} xs={24} sm={12} md={8} lg={6}>
//                     <Card size="small" hoverable onClick={() => openProcessDetail(p.processName)}>
//                       <Statistic title={p.processName} value={p.pendingCount} />
//                       <div style={{ color: daysColor(p.oldestPendingDays), fontSize: 12, marginTop: 4 }}>
//                         Oldest pending: {p.oldestPendingDays} day{p.oldestPendingDays === 1 ? "" : "s"}
//                       </div>
//                     </Card>
//                   </Col>
//                 ))}
//               </Row>
//               <div style={{ height: 260, marginTop: 16 }}>
//                 <ResponsiveContainer width="100%" height="100%">
//                   <BarChart data={pending}>
//                     <CartesianGrid strokeDasharray="3 3" />
//                     <XAxis dataKey="processName" tick={{ fontSize: 11 }} interval={0} angle={-20} textAnchor="end" height={60} />
//                     <YAxis allowDecimals={false} />
//                     <Tooltip />
//                     <Bar dataKey="pendingCount" fill="#8884d8" cursor="pointer" onClick={(data) => openProcessDetail(data.processName)} />
//                   </BarChart>
//                 </ResponsiveContainer>
//               </div>
//             </>
//           ) : (
//             <Empty description="Nothing pending" />
//           )}
//         </Card>

//         {/* <Card title="Today's Activity" size="small">
//           <Table
//             dataSource={activity}
//             columns={activityColumns}
//             rowKey={(r, idx) => `${r.processName}-${r.requestId}-${idx}`}
//             pagination={false}
//             size="small"
//             locale={{ emptyText: "No activity yet today" }}
//           />
//         </Card> */}
//         <Card title="Today's Activity" size="small">
//           {activity.length > 0 && (
//             <div style={{ height: 220, marginBottom: 16 }}>
//               <ResponsiveContainer width="100%" height="100%">
//                 <PieChart>
//                   <Pie
//                     data={[
//                       { name: "Created", value: activity.filter((a) => a.activityType === "CREATED").length },
//                       { name: "Actioned", value: activity.filter((a) => a.activityType === "ACTIONED").length },
//                     ].filter((d) => d.value > 0)}
//                     dataKey="value"
//                     nameKey="name"
//                     outerRadius={80}
//                     label
//                   >
//                     {[0, 1].map((i) => (
//                       <Cell key={`cell-${i}`} fill={PIE_COLORS[i % PIE_COLORS.length]} />
//                     ))}
//                   </Pie>
//                   <Tooltip />
//                   <Legend />
//                 </PieChart>
//               </ResponsiveContainer>
//             </div>
//           )}
//           <Table
//             dataSource={activity}
//             columns={activityColumns}
//             rowKey={(r, idx) => `${r.processName}-${r.requestId}-${idx}`}
//             pagination={false}
//             size="small"
//             locale={{ emptyText: "No activity yet today" }}
//           />
//         </Card>

//         {isPurchaseAndStores && (
//           <Card title="Full-Cycle Status Summary" size="small">
//             <Table
//               dataSource={cycleStatus}
//               columns={cycleColumns}
//               rowKey={(r, idx) => `${r.processName}-${r.status}-${idx}`}
//               pagination={false}
//               size="small"
//               locale={{ emptyText: "No records" }}
//             />
//           </Card>
//         )}
//       </div>
//     {/* </Spin> */}
//   {/* ); */}
// {/* }; */}
// <Modal
//         title={detailModal.processName ? `Pending — ${detailModal.processName}` : "Pending details"}
//         open={detailModal.open}
//         onCancel={() => setDetailModal({ open: false, processName: null, items: [], loading: false })}
//         footer={null}
//         width={600}
//       >
//         <Spin spinning={detailModal.loading}>
//           <Table
//             dataSource={detailModal.items}
//             rowKey={(r, idx) => `${r.requestId}-${idx}`}
//             pagination={false}
//             size="small"
//             locale={{ emptyText: "No pending items" }}
//             columns={[
//               { title: "Request", dataIndex: "requestId", key: "requestId" },
//               {
//                 title: "Pending For",
//                 dataIndex: "pendingDays",
//                 key: "pendingDays",
//                 render: (d) => <Tag color={d >= 7 ? "red" : d >= 3 ? "gold" : "green"}>{d} day{d === 1 ? "" : "s"}</Tag>,
//               },
//             ]}
//           />
//         </Spin>
//       </Modal>
//     </Spin>
//   );
// };
// export default DashboardOverview;


import React from "react";
import { useSelector } from "react-redux";
import { Alert } from "antd";
import IndentorDashboard from "./IndentorDashboard";
import ApproverDashboard from "./ApproverDashboard";
import SpoDashboard from "./SpoDashboard";
import PurchasePersonnelDashboard from "./PurchasePersonnelDashboard";
import StorePersonDashboard from "./StorePersonDashboard";

// Role -> dashboard mapping, per the finalized plan:
//   Indentor           = Indent Creator
//   SPO / P&I Officer  = Store Purchase Officer
//   Purchase Personnel = Purchase personnel
//   Store Person       = Store Person
//   Approver           = every other role (Purchase Head, CP/PO/SO Creator,
//                         Reporting Officer, Project Head, Administrative
//                         Officer, Director, Dean, Head SEG, Engineer
//                         In-Charge, Professor In-Charge, Computer Committee
//                         Chairman, Account/Accounts Officer, Billing
//                         Section Personnel, etc.)
const ROLE_TO_DASHBOARD = {
  "Indent Creator": IndentorDashboard,
  "Store Purchase Officer": SpoDashboard,
  "Purchase personnel": PurchasePersonnelDashboard,
  "Store Person": StorePersonDashboard,
};

const DashboardOverview = () => {
  const auth = useSelector((state) => state.auth);
  const roleName = auth?.role;

  if (!roleName) {
    return <Alert type="info" showIcon message="Loading your dashboard..." />;
  }

  const RoleDashboard = ROLE_TO_DASHBOARD[roleName] || ApproverDashboard;
  return <RoleDashboard />;
};

export default DashboardOverview;
