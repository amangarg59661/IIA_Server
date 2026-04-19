// import React, { useState, useEffect, useCallback } from "react";
// import {
//   Table,
//   Input,
//   Button,
//   Space,
//   Typography,
//   Popover,
//   Tag,
//   message,
//   Spin,
//   Select,
//   Descriptions, 
//   Badge,
//   Modal
// } from "antd";
// import { SearchOutlined } from "@ant-design/icons";
// import axios from "axios";
// import { useSelector } from "react-redux";
// import QueueModal from "./QueueModal";
// import { useNavigate } from "react-router-dom";
// import { baseURL } from '../../../App';

// const { Text } = Typography;
// const { Option } = Select;

// const MaterialDetailModal = ({ visible, setVisible, materialData }) => {
//   if (!materialData) return null;

//   return (
//     <Modal
//       title="Material Details"
//       open={visible}
//       onCancel={() => setVisible(false)}
//       footer={[
//         <Button key="close" onClick={() => setVisible(false)}>
//           Close
//         </Button>,
//       ]}
//       width={700}
//     >
//       <Descriptions bordered column={2}>
//         <Descriptions.Item label="Material Code" span={2}>
//           {materialData.materialCode}
//         </Descriptions.Item>

//         <Descriptions.Item label="Description" span={2}>
//           {materialData.description}
//         </Descriptions.Item>

//         <Descriptions.Item label="Category">
//           {materialData.category}
//         </Descriptions.Item>

//         <Descriptions.Item label="Sub Category">
//           {materialData.subCategory}
//         </Descriptions.Item>

//         <Descriptions.Item label="UOM">
//           {materialData.uom}
//         </Descriptions.Item>

//         <Descriptions.Item label="Unit Price">
//           {materialData.currency} {materialData.unitPrice}
//         </Descriptions.Item>

//         <Descriptions.Item label="Origin">
//           {materialData.indigenousOrImported ? "Indigenous" : "Imported"}
//         </Descriptions.Item>

//         <Descriptions.Item label="Created By">
//           {materialData.createdBy}
//         </Descriptions.Item>

//         <Descriptions.Item label="Created Date">
//           {new Date(materialData.createdDate).toLocaleString()}
//         </Descriptions.Item>

//         <Descriptions.Item label="Updated Date">
//           {new Date(materialData.updatedDate).toLocaleString()}
//         </Descriptions.Item>

//         <Descriptions.Item label="Status" span={2}>
//           <Badge
//             status={
//               materialData.approvalStatus === "APPROVED"
//                 ? "success"
//                 : materialData.approvalStatus === "REJECTED"
//                 ? "error"
//                 : "warning"
//             }
//             text={materialData.approvalStatus.replace("_", " ")}
//           />
//         </Descriptions.Item>

//         <Descriptions.Item label="Comments" span={2}>
//           {materialData.comments || "No comments"}
//         </Descriptions.Item>
//         {/* Added by aman */}
//             <Descriptions.Item label = "Asset" span = {2}>
//               {materialData.asset_Flag ? "Yes" : "No"}
//             </Descriptions.Item>
//         {/* End */}

// <Descriptions.Item label="Upload Documents" span={2}>
//   <div className="detail-item">
//     {materialData.uploadImageFileName
//       ? materialData.uploadImageFileName
//           .split(",")
//           .map((fileName, index, array) => {
//             const trimmed = fileName.trim();
//             return (
//               <span key={index}>
//                 <a
//                   href={`${baseURL}/file/view/Material/${trimmed}`}
//                   target="_blank"
//                   rel="noopener noreferrer"
//                 >
//                   {trimmed} (View)
//                 </a>
//                 {index < array.length - 1 && ", "}
//               </span>
//             );
//           })
//       : "N/A"}
//   </div>
// </Descriptions.Item>
//       </Descriptions>
//     </Modal>
//   );
// };

// const VendorDetailModal = ({ visible, setVisible, vendorData }) => {
//   if (!vendorData) return null;
  
//   return (
//     <Modal
//       title="Vendor Details"
//       open={visible}
//       onCancel={() => setVisible(false)}
//       footer={[
//         <Button key="close" onClick={() => setVisible(false)}>
//           Close
//         </Button>
//       ]}
//       width={700}
//     >
//       <Descriptions bordered column={2}>
//         <Descriptions.Item label="Vendor Code" span={2}>
//           {vendorData.vendorId}
//         </Descriptions.Item>
//         <Descriptions.Item label="Vendor Name" span={2}>
//           {vendorData.vendorName}
//         </Descriptions.Item>
//         <Descriptions.Item label="Vendor Type">
//           {vendorData.vendorType}
//         </Descriptions.Item>
//         <Descriptions.Item label="Contact Number">
//           {vendorData.contactNumber}
//         </Descriptions.Item>
//         <Descriptions.Item label="Email Address">
//           {vendorData.emailAddress}
//         </Descriptions.Item>
//         <Descriptions.Item label="PFMS Vendor Code">
//           {vendorData.pfmsVendorCode} 
//         </Descriptions.Item>
//         <Descriptions.Item label="Primary Business">
//           {vendorData.primaryBusiness}
//         </Descriptions.Item>
//         <Descriptions.Item label="Address">
//           {vendorData.address}
//         </Descriptions.Item>
//         <Descriptions.Item label="Alternate Email/Phone Number">
//           {vendorData.alternateEmailOrPhoneNumber}
//         </Descriptions.Item>
//         <Descriptions.Item label="Fax Number">
//           {vendorData.faxNumber}
//         </Descriptions.Item>
//         <Descriptions.Item label="Pan Number">
//           {vendorData.panNumber}
//         </Descriptions.Item>
//         <Descriptions.Item label="GST Number">
//           {vendorData.gstNumber}
//         </Descriptions.Item>
//         <Descriptions.Item label="Bank Name">
//           {vendorData.bankName}
//         </Descriptions.Item>
//         <Descriptions.Item label="Account Number">
//           {vendorData.accountNumber}
//         </Descriptions.Item>
//         <Descriptions.Item label="IFSC Code">
//           {vendorData.ifscCode}
//         </Descriptions.Item>
//         <Descriptions.Item label="Swift Code">
//           {vendorData.swiftCode}
//         </Descriptions.Item>
//         <Descriptions.Item label="Bic Code">
//           {vendorData.bicCode}
//         </Descriptions.Item>
//         <Descriptions.Item label="IBAN ABA Number">
//           {vendorData.ibanAbaNumber}
//         </Descriptions.Item>
//         <Descriptions.Item label="Sort Code">
//           {vendorData.sortCode}
//         </Descriptions.Item>
//         <Descriptions.Item label="Bank Routing Number">
//           {vendorData.bankRoutingNumber}
//         </Descriptions.Item>
//         <Descriptions.Item label="Bank Address">
//           {vendorData.bankAddress}
//         </Descriptions.Item>
//         <Descriptions.Item label="Country">
//           {vendorData.country}
//         </Descriptions.Item>
//         <Descriptions.Item label="State">
//           {vendorData.state}
//         </Descriptions.Item>
//         <Descriptions.Item label="Place">
//           {vendorData.place}
//         </Descriptions.Item>
//         <Descriptions.Item label="Registered Platform">
//           {vendorData.registeredPlatform ? "True" : "False"}
//         </Descriptions.Item>
//         <Descriptions.Item label="Created By">
//           {vendorData.createdBy}
//         </Descriptions.Item>
//         <Descriptions.Item label="Created Date">
//           {new Date(vendorData.createdDate).toLocaleString()}
//         </Descriptions.Item>
//         <Descriptions.Item label="Status" span={2}>
//           <Badge 
//             status={vendorData.approvalStatus === "APPROVED" ? "success" : 
//                    vendorData.approvalStatus === "REJECTED" ? "error" : "warning"} 
//             text={vendorData.approvalStatus.replace("_", " ")} 
//           />
//         </Descriptions.Item>
//       </Descriptions>
//     </Modal>
//   );
// };

// const JobDetailModal = ({ visible, setVisible, jobData }) => {
//   if (!jobData) return null;

//   return (
//     <Modal
//       title="Job Details"
//       open={visible}
//       onCancel={() => setVisible(false)}
//       footer={[
//         <Button key="close" onClick={() => setVisible(false)}>
//           Close
//         </Button>,
//       ]}
//       width={700}
//     >
//       <Descriptions bordered column={2}>
//         <Descriptions.Item label="Job Code" span={2}>
//           {jobData.jobCode}
//         </Descriptions.Item>
//         <Descriptions.Item label="Job Description" span={2}>
//           {jobData.jobDescription}
//         </Descriptions.Item>
//         <Descriptions.Item label="Category">
//           {jobData.category}
//         </Descriptions.Item>
//         <Descriptions.Item label="Sub Category">
//           {jobData.subCategory}
//         </Descriptions.Item>
//         <Descriptions.Item label="UOM">
//           {jobData.uom}
//         </Descriptions.Item>
//         <Descriptions.Item label="Estimated Price">
//           {jobData.currency} {jobData.estimatedPriceWithCcy}
//         </Descriptions.Item>
//         <Descriptions.Item label="Asset ID">
//           {jobData.assetId || "N/A"}
//         </Descriptions.Item>
//         <Descriptions.Item label="Value">
//           {jobData.value || "N/A"}
//         </Descriptions.Item>
//         <Descriptions.Item label="Brief Description" span={2}>
//           {jobData.briefDescription || "N/A"}
//         </Descriptions.Item>
//         <Descriptions.Item label="Created By">
//           {jobData.createdBy}
//         </Descriptions.Item>
//         <Descriptions.Item label="Created Date">
//           {jobData.createdDate ? new Date(jobData.createdDate).toLocaleString() : "N/A"}
//         </Descriptions.Item>
//         <Descriptions.Item label="Status" span={2}>
//           <Badge
//             status={
//               jobData.approvalStatus === "APPROVED"
//                 ? "success"
//                 : jobData.approvalStatus === "REJECTED"
//                 ? "error"
//                 : "warning"
//             }
//             text={(jobData.approvalStatus || "").replace(/_/g, " ")}
//           />
//         </Descriptions.Item>
//         <Descriptions.Item label="Comments" span={2}>
//           {jobData.comments || "No comments"}
//         </Descriptions.Item>
//       </Descriptions>
//     </Modal>
//   );
// };

// const FilterComponent = ({ onSearch, searchTerm, onReset }) => (
//   <div style={{ marginBottom: 16 }}>
//     <Space>
//       <Input
//         placeholder="Search by Request ID"
//         prefix={<SearchOutlined />}
//         value={searchTerm}
//         onChange={(e) => onSearch(e.target.value)}
//         style={{ width: 300 }}
//         onPressEnter={() => onSearch(searchTerm)}
//         allowClear
//       />
//       <Button
//         type="primary"
//         icon={<SearchOutlined />}
//         onClick={() => onSearch(searchTerm)}
//       >
//         Search
//       </Button>
//       <Button onClick={onReset}>Reset</Button>
//     </Space>
//   </div>
// );

// const QueueRequest = ({ workflowId, requestType }) => {
//   const auth = useSelector((state) => state.auth);
//   const { userId } = useSelector((state) => state.auth);
//   const actionPerformer = auth.userId;
//   const navigate = useNavigate();

//   // State declarations
//   const [data, setData] = useState([]);
//   const [loading, setLoading] = useState(false);
//   const [error, setError] = useState(null);
//   const [rejectComment, setRejectComment] = useState("");
//   const [requestChangeComment, setRequestChangeComment] = useState("");
//   const [detailsData, setDetailsData] = useState(null);
//   const [modalVisible, setModalVisible] = useState(false);
//   const [selectedRecord, setSelectedRecord] = useState(null);
//   const [previousRoles, setPreviousRoles] = useState([]);
//   const [selectedRole, setSelectedRole] = useState(null);
//   const [loadingPreviousRoles, setLoadingPreviousRoles] = useState(false);
//   const [searchTerm, setSearchTerm] = useState("");
//   const [historyVisible, setHistoryVisible] = useState(false);
//   const [queueData, setQueueData] = useState([]);
//   const [selectedRowKeys, setSelectedRowKeys] = useState([]);
//   const [selectedRows, setSelectedRows] = useState([]);
//   const [workflowCounts, setWorkflowCounts] = useState({});
//   const [materialHistoryVisible, setMaterialHistoryVisible] = useState(false);
//   const [selectedMaterialCode, setSelectedMaterialCode] = useState(null);
//   const [employees, setEmployees] = useState([]);
//   const [selectedEmployee, setSelectedEmployee] = useState(null);
//   const [loadingEmployees, setLoadingEmployees] = useState(false);
//   const [materialModalOpen, setMaterialModalOpen] = useState(false);
//   const [materialDtl, setMaterialDtl] = useState(null);
//   const [vendorModalOpen, setVendorModalOpen] = useState(false);
//   const [vendorDtl, setVendorDtl] = useState(null);
//   const [jobModalOpen, setJobModalOpen] = useState(false);
//   const [jobDtl, setJobDtl] = useState(null);

//   // Fetch data when component mounts or role changes
//   useEffect(() => {
//     if (auth && auth.role) {
//       fetchData(auth.role);
//     }
//   }, [auth.role, workflowId]);

//   // Fetch employees for assignment
// const fetchEmployees = () => {
//   if (employees.length === 0) {
//     setLoadingEmployees(true);
//     axios
//       .get("/api/employee-department-master/employeeName/byDepartment", {
//         params: { department: "Purchase" }
//       })
//       .then((res) => setEmployees(res.data.responseData))
//       .finally(() => setLoadingEmployees(false));
//   }
// };

//   // Handle employee assignment
//   const handleAssign = (indentId) => {
//     if (!selectedEmployee) {
//       message.warning("Please select an employee");
//       return;
//     }

//     const emp = employees.find((e) => e.employeeId === selectedEmployee);

//     axios
//       .post("/api/indents/assign-employee", {
//         indentId,
//         employeeId: selectedEmployee,
//         employeeName: emp ? emp.employeeName : "",
//       })
//       .then(() => {
//         message.success("Indent assigned successfully");
//         setSelectedEmployee(null);
//       })
//       .catch(() => {
//         message.error("Failed to assign indent");
//       });
//   };

//   // Fetch previous roles for request change
//   const fetchPreviousRoles = async (workflowId, requestId) => {
//     setLoadingPreviousRoles(true);
//     try {
//       const response = await axios.get(
//         `/allPreviousWorkflowRole?workflowId=${encodeURIComponent(
//           workflowId
//         )}&requestId=${encodeURIComponent(requestId)}`
//       );
//       const roles = response.data.responseData || [];
//       const filteredRoles = roles.filter(
//         (role) =>
//           role.trim().toLowerCase() !== (auth?.role || "").trim().toLowerCase()
//       );
//       setPreviousRoles(filteredRoles);
//     } catch (error) {
//       message.error("Failed to fetch previous roles.");
//       console.error("Fetch previous roles error:", error);
//     } finally {
//       setLoadingPreviousRoles(false);
//     }
//   };

//   // Handle bulk approve
//   const handleApproveAll = async () => {
//     if (selectedRows.length === 0) {
//       message.warning("No records selected.");
//       return;
//     }

//     const vendors = selectedRows.filter((r) => r.workflowName === "Vendor Workflow");
//     const materials = selectedRows.filter((r) => r.workflowName === "Material Workflow");
//     const jobs = selectedRows.filter((r) => r.workflowName === "Job Workflow");
//     const others = selectedRows.filter(
//       (r) =>
//         r.workflowName !== "Vendor Workflow" &&
//         r.workflowName !== "Material Workflow" &&
//         r.workflowName !== "Job Workflow"
//     );

//     try {
//       // Bulk approve vendors
//       if (vendors.length > 0) {
//         const vendorPayload = vendors.map((record) => ({
//           action: "APPROVED",
//           actionBy: actionPerformer,
//           remarks: "Vendor approved",
//           requestId: record.requestId,
//         }));
//         await axios.post("/api/vendor-master-util/performBulkAction", vendorPayload);
//       }

//       // Bulk approve materials
//       if (materials.length > 0) {
//         const materialPayload = materials.map((record) => ({
//           action: "APPROVED",
//           actionBy: actionPerformer,
//           remarks: "Material approved",
//           requestId: record.requestId,
//         }));
//         await axios.post("/api/material-master-util/performBulkActionForMaterial", materialPayload);
//       }

//       // Bulk approve jobs
//       if (jobs.length > 0) {
//         const jobPayload = jobs.map((record) => ({
//           action: "APPROVED",
//           actionBy: actionPerformer,
//           remarks: "Job approved",
//           requestId: record.requestId,
//         }));
//         for (const p of jobPayload) {
//           await axios.post("/api/job-master/performAction", p);
//         }
//       }

//       // Bulk approve others
//       if (others.length > 0) {
//         const otherPayload = others.map((record) => ({
//           action: "APPROVED",
//           actionBy: actionPerformer,
//           assignmentRole: null,
//           remarks: "Approved successfully",
//           requestId: record.requestId,
//           workflowTransitionId: record.workflowTransitionId,
//           roleName: auth.role,
//         }));
//         await axios.post("/performAllTransitionAction", otherPayload);
//       }

//       message.success("All selected records approved.");
//       const updatedData = data.filter((item) => !selectedRowKeys.includes(item.key));
//       setData(updatedData);

//       const updatedCounts = {};
//       updatedData.forEach((item) => {
//         const id = item.workflowId;
//         updatedCounts[id] = (updatedCounts[id] || 0) + 1;
//       });
//       setWorkflowCounts(updatedCounts);

//       setSelectedRowKeys([]);
//       setSelectedRows([]);
//     } catch (error) {
//       console.error("Bulk approval error:", error);
//       message.error("Failed to approve selected records.");
//     }
//   };

//   // Row selection
//   const rowSelection = {
//     selectedRowKeys,
//     onChange: (newSelectedRowKeys, newSelectedRows) => {
//       setSelectedRowKeys(newSelectedRowKeys);
//       setSelectedRows(newSelectedRows);
//     },
//   };

//   // Handle single approve
//   const handleApprove = async (record) => {
//     try {
//       if (record.workflowName === "Vendor Workflow") {
//         await axios.post("/api/vendor-master-util/performAction", {
//           action: "APPROVED",
//           actionBy: actionPerformer,
//           remarks: "Vendor approved",
//           requestId: record.requestId,
//         });
//       } else if (record.workflowName === "Material Workflow") {
//         await axios.post("/api/material-master-util/performActionForMaterial", {
//           action: "APPROVED",
//           actionBy: actionPerformer,
//           remarks: "Material approved",
//           requestId: record.requestId,
//         });
//       } else if (record.workflowName === "Job Workflow") {
//         await axios.post("/api/job-master/performAction", {
//           action: "APPROVED",
//           actionBy: actionPerformer,
//           remarks: "Job approved",
//           requestId: record.requestId,
//         });
//       } else {
//         const workflowTransitionId = record.workflowTransitionId;
//         if (!workflowTransitionId) {
//           message.error("Workflow transition ID not found for this request.");
//           return;
//         }

//         const payload = {
//           action: "APPROVED",
//           actionBy: actionPerformer,
//           assignmentRole: null,
//           remarks: "Approved successfully",
//           requestId: record.requestId,
//           workflowTransitionId: record.workflowTransitionId,
//           roleName: auth.role,
//         };
//         await axios.post("/performTransitionAction", payload);
//       }

//       message.success(`Request ${record.requestId} processed`);
//       const updatedData = data.filter((item) => item.key !== record.key);
//       setData(updatedData);

//       const updatedCounts = {};
//       updatedData.forEach((item) => {
//         const id = item.workflowId;
//         updatedCounts[id] = (updatedCounts[id] || 0) + 1;
//       });
//       setWorkflowCounts(updatedCounts);
//     } catch (error) {
//       message.error("Failed to approve");
//       console.error("Approval error:", error);
//     }
//   };

//   // Fetch workflow transition history
//   const fetchWorkflowTransitionHistory = async (requestId) => {
//     try {
//       const response = await axios.get(
//         `/workflowTransitionHistory?requestId=${requestId}`
//       );
//       if (!response.data.responseData?.[0]?.remarks) {
//         console.warn("No remarks found in transition history");
//       }
//       return response.data.responseData;
//     } catch (error) {
//       console.error("Error fetching workflow transition history:", error);
//       return null;
//     }
//   };

//   // Handle reject
//   const handleReject = async (record) => {
//     if (!rejectComment.trim()) {
//       message.warning("Please enter a reject comment.");
//       return;
//     }

//     try {
//       if (record.workflowName === "Vendor Workflow") {
//         await axios.post("/api/vendor-master-util/performAction", {
//           action: "REJECTED",
//           actionBy: actionPerformer,
//           remarks: rejectComment,
//           requestId: record.requestId,
//         });
//       } else if (record.workflowName === "Material Workflow") {
//         await axios.post("/api/material-master-util/performActionForMaterial", {
//           action: "REJECTED",
//           actionBy: actionPerformer,
//           remarks: rejectComment,
//           requestId: record.requestId,
//         });
//       } else if (record.workflowName === "Job Workflow") {
//         await axios.post("/api/job-master/performAction", {
//           action: "REJECTED",
//           actionBy: actionPerformer,
//           remarks: rejectComment,
//           requestId: record.requestId,
//         });
//       } else {
//         const history = await fetchWorkflowTransitionHistory(record.requestId);

//         if (!history || history.length === 0) {
//           message.error("No transition history found for this request.");
//           return;
//         }

//         const previousApprovals = history.filter(
//           (entry) => entry.action === "APPROVED"
//         );
//         if (previousApprovals.length === 0) {
//           message.error("No previous approval found to revert to.");
//           return;
//         }

//         const lastApproval = previousApprovals[previousApprovals.length - 1];
//         const currentTransition = history[0];

//         const payload = {
//           action: "REJECTED",
//           actionBy: actionPerformer,
//           assignmentRole: lastApproval.assignmentRole,
//           remarks: rejectComment,
//           requestId: record.requestId,
//           workflowTransitionId: currentTransition.workflowTransitionId,
//         };

//         await axios.post("/performTransitionAction", payload, {
//           headers: { "Content-Type": "application/json" },
//         });
//       }

//       message.success(`Request ${record.requestId} rejected and out of queue`);
//       const updatedData = data.filter((item) => item.key !== record.key);
//       setData(updatedData);
//       setRejectComment("");

//       const updatedCounts = {};
//       updatedData.forEach((item) => {
//         const id = item.workflowId;
//         updatedCounts[id] = (updatedCounts[id] || 0) + 1;
//       });
//       setWorkflowCounts(updatedCounts);
//     } catch (error) {
//       let backendMessage = "Failed to reject";

//       if (error.response?.data?.responseStatus?.message) {
//         backendMessage = error.response.data.responseStatus.message;
//       } else if (error.response?.data?.message) {
//         backendMessage = error.response.data.message;
//       } else if (error.message) {
//         backendMessage = error.message;
//       }

//       message.error(backendMessage);
//       console.error("Rejection error:", error);
//     }
//   };

//   // Handle request change submit
//   const handleRequestChangeSubmit = async (record) => {
//     if (!requestChangeComment.trim()) {
//       message.warning("Please enter request change comments.");
//       return;
//     }

//     try {
//       if (record.workflowName === "Vendor Workflow") {
//         // For vendors: No role selection, just comments
//         await axios.post("/api/vendor-master-util/performAction", {
//           action: "CHANGE REQUEST",
//           actionBy: actionPerformer,
//           remarks: requestChangeComment,
//           requestId: record.requestId,
//         });
//       } else if (record.workflowName === "Material Workflow") {
//         // For materials: Hardcoded "Indent Creator" role
//         await axios.post("/api/material-master-util/performActionForMaterial", {
//           action: "CHANGE REQUEST",
//           actionBy: actionPerformer,
//           remarks: requestChangeComment,
//           requestId: record.requestId,
//         });
//       } else if (record.workflowName === "Job Workflow") {
//         await axios.post("/api/job-master/performAction", {
//           action: "CHANGE REQUEST",
//           actionBy: actionPerformer,
//           remarks: requestChangeComment,
//           requestId: record.requestId,
//         });
//       } else {
//         // For regular workflows: Need role selection
//         if (!selectedRole) {
//           message.warning("Please select a role.");
//           return;
//         }

//         const workflowTransitionId = record.workflowTransitionId;
//         if (!workflowTransitionId) {
//           message.error("Workflow transition ID not found for this request.");
//           return;
//         }

//         const payload = {
//           action: "Change requested",
//           actionBy: actionPerformer,
//           assignmentRole: selectedRole,
//           remarks: requestChangeComment,
//           requestId: record.requestId,
//           workflowTransitionId: record.workflowTransitionId,
//         };

//         await axios.post("/performTransitionAction", payload, {
//           headers: { "Content-Type": "application/json" },
//         });
//       }

//       message.success("Request change submitted successfully.");
//       const updatedData = data.filter((item) => item.key !== record.key);
//       setData(updatedData);

//       const updatedCounts = {};
//       updatedData.forEach((item) => {
//         const id = item.workflowId;
//         updatedCounts[id] = (updatedCounts[id] || 0) + 1;
//       });
//       setWorkflowCounts(updatedCounts);

//       setRequestChangeComment("");
//       setSelectedRole(null);
//       setPreviousRoles([]);
//     } catch (error) {
//       message.error("Failed to submit request change.");
//       console.error("Request change error:", error);
//     }
//   };

//   // Fetch workflow details
//   const fetchWorkflowDetails = async (record) => {
//     if (!record.requestId) {
//       message.error("No ID found.");
//       return;
//     }

//     if (record.workflowName === "Material Workflow") {
//       try {
//         const { data } = await axios.get(
//           `/api/material-master-util/${record.requestId}`
//         );
//         setMaterialDtl(data.responseData);
//         setMaterialModalOpen(true);
//         return;
//       } catch (error) {
//         message.error("Failed to fetch material details.");
//         console.error("Material fetch error:", error);
//         return;
//       }
//     }

//     if (record.workflowName === "Vendor Workflow") {
//       try {
//         const { data } = await axios.get(
//           `/api/vendor-master-util/${record.requestId}`
//         );
//         setVendorDtl(data.responseData);
//         setVendorModalOpen(true);
//         return;
//       } catch (error) {
//         message.error("Failed to fetch vendor details.");
//         console.error("Vendor fetch error:", error);
//         return;
//       }
//     }

//     if (record.workflowName === "Job Workflow") {
//       try {
//         const { data } = await axios.get(
//           `/api/job-master/${record.requestId}`
//         );
//         setJobDtl(data.responseData);
//         setJobModalOpen(true);
//         return;
//       } catch (error) {
//         message.error("Failed to fetch job details.");
//         console.error("Job fetch error:", error);
//         return;
//       }
//     }

//     setSelectedRecord(record);
//     setLoading(true);

//     let endpoint = "";
//     const workflowIdNum = parseInt(record.workflowId, 10);

//     switch (workflowIdNum) {
//       case 1:
//         endpoint = `/api/indents/${record.requestId}`;
//         break;
//       case 2:
//         endpoint = `/api/contigency-purchase/${record.requestId}`;
//         break;
//       case 3:
//         endpoint = `/api/purchase-orders/${record.requestId}`;
//         break;
//       case 4:
//         endpoint = `/api/tender-requests/${record.requestId}`;
//         break;
//       case 5:
//         endpoint = `/api/service-orders/${record.requestId}`;
//         break;
//       case 7:
//         endpoint = `/api/tender-requests/${record.requestId}`;
//         break;
//       case 10:
//         endpoint = `/api/process-controller/VoucherData?processNo=${record.requestId}`;
//         break;
//       default:
//         message.error("Invalid workflow ID.");
//         setLoading(false);
//         return;
//     }

//     try {
//       const response = await axios.get(endpoint);
//       setDetailsData(response.data.responseData);
//       setQueueData(response.data.responseData);
//       setModalVisible(true);
//     } catch (err) {
//       message.error("Failed to fetch details.");
//       console.error("Fetch details error:", err);
//     } finally {
//       setLoading(false);
//     }
//   };

//   // Fetch data
//   const fetchData = async (roleName) => {
//     if (!roleName) return;
//     setLoading(true);
//     try {
//       const isPurchaseHead = roleName === "Purchase Head";

//       let responseData = [];
//       if (isPurchaseHead && requestType === "C") {
//         const cancelResponse = await axios.get("/allCancledIndents");
//         responseData = cancelResponse.data.responseData;
//       } else {
//         const params = new URLSearchParams();
//         params.append("roleName", roleName);
//         if (userId) {
//           params.append("userId", userId);
//         }
//         const response = await axios.get(
//           isPurchaseHead
//             ? `/completedIndentWorkflowTransition?${params.toString()}`
//             : `/pendingWorkflowTransitionQueue?${params.toString()}`
//         );
//         responseData = response.data.responseData;
//       }

//       const formattedData = responseData
//         .map((item) => ({
//           key: item.requestId,
//           requestId: item.requestId,
//           workflowId: item.workflowId,
//           workflowName: item.workflowName,
//           createdDate: new Date(item.createdDate),
//           remarks: item.transitionHistory?.[0]?.remarks || "No remarks",
//           status: item.status,
//           action: item.action,
//           amount: item.amount,
//           paymentType: item.paymentType,
//           poNo: item.poNO,
//           vendorName: item.vendorName,

//           ...(item.workflowId === 1 && {
//             indentorName: item.indentorName,
//             amount: item.amount,
//             projectName: item.projectName,
//             budgetName: item.budgetName,
//             modeOfProcurement: item.modeOfProcurement,
//             consignee: item.consignee,
//             status: item.status,
//             action: item.action,
//           }),
//           ...(item.workflowId === 2 && {
//             createdBy: item.createdBy,
//             amount: item.amount,
//             projectName: item.projectName,
//             consignee: item.deliveryLocation,
//           }),
//           ...(item.workflowId === 3 && {
//             createdBy: item.createdBy,
//             amount: item.amount,
//             projectName: item.projectName,
//             budgetCode: item.budgetCode,
//             procurementType: item.procurementType,
//             modeOfProcurement: item.modeOfProcurement,
//             consignee: item.consignee,
//           }),
//           ...((item.workflowId === 4 || item.workflowId === 7) && {
//             createdBy: item.createdBy,
//             projectName: item.projectName,
//             budgetCode: item.budgetCode,
//             modeOfProcurement: item.modeOfProcurement,
//             consignee: item.consignee,
//             amount: item.amount,
//           }),
//           ...(item.workflowId === 5 && {
//             createdBy: item.createdBy,
//             projectName: item.projectName,
//             budgetCode: item.budgetCode,
//             procurementType: item.procurementType,
//             consignee: item.consignee,
//           }),
//           ...(item.workflowId === 9 && {
//             indentorName: item.indentorName,
//             amount: item.amount,
//           }),
//           ...(item.workflowId === 10 && {
//             indentorName: item.indentorName,
//             amount: item.amount,
//             poNo: item.poNo,
//             vendorName: item.vendorName,
//             paymentType: item.paymentType,
//           }),
//           status: item.nextAction,
//           workflowTransitionId: item.workflowTransitionId,
//           assignedToUserId: item.assignedToUserId,
//           assignedToEmployeeName: item.assignedToEmployeeName,
//         }))
//         .sort((a, b) => b.createdDate - a.createdDate);

//       let filteredData = [];

//       if (workflowId != null) {
//         filteredData = formattedData.filter((item) => item.workflowId === workflowId);
//       } else if (requestType === "V") {
//         filteredData = formattedData.filter(
//           (item) => item.workflowName === "Vendor Workflow"
//         );
//       } else if (requestType === "M") {
//         filteredData = formattedData.filter(
//           (item) => item.workflowName === "Material Workflow"
//         );
//       } else if (requestType === "J") {
//         filteredData = formattedData.filter(
//           (item) => item.workflowName === "Job Workflow"
//         );
//       } else if (requestType === "Tender") {
//         filteredData = formattedData.filter(
//           (item) => item.workflowId === 4 || item.workflowId === 7
//         );
//       } else if (requestType === "C") {
//         filteredData = formattedData.filter(
//           (item) => item.action === "Indentor Cancelled"
//         );
//       } else if (requestType === "PV") {
//         filteredData = formattedData.filter((item) => item.workflowId === 10);
//       }

//       setData(filteredData);

//       const workflowCounts = {};
//       filteredData.forEach((item) => {
//         const id = item.workflowId;
//         workflowCounts[id] = (workflowCounts[id] || 0) + 1;
//       });

//       setWorkflowCounts(workflowCounts);
//     } catch (err) {
//       setError(err.message);
//       message.error("Failed to fetch queue data from the API.");
//       console.error("fetchData error:", err);
//     } finally {
//       setLoading(false);
//     }
//   };

//   // Get common field helper
//   const getCommonField = (workflowId, apiData, field) => {
//     switch (workflowId) {
//       case 1:
//         return {
//           indentor: apiData.indentorName,
//           amount: apiData.amount,
//           project: apiData.projectName,
//           budgetName: apiData.budgetName,
//           indentTitle: apiData.workflowName,
//           modeOfProcurement: apiData.modeOfProcurement,
//           consignee: apiData.consignee,
//         }[field];

//       case 2:
//         return {
//           indentor: apiData.createdBy,
//           amount: apiData.amount,
//           project: apiData.projectName,
//           budgetName: "-",
//           indentTitle: "Contingency Purchase",
//           procurementMode: "Direct Purchase",
//           indentor: apiData.vendorsName
//             ? `${apiData.vendorsName} (${apiData.createdBy})`
//             : `User ${apiData.createdBy}`,
//           consignee: apiData.consignee,
//         }[field];

//       case 3:
//         return {
//           indentor: apiData.createdBy,
//           amount: apiData.amount,
//           project:
//             apiData.tenderDetails?.indentResponseDTO?.[0]?.projectName || "N/A",
//           budgetName: apiData.budgetCode,
//           indentTitle: "Purchase Order",
//           modeOfProcurement: apiData.modeOfProcurement,
//           procurementMode: apiData.procurementType,
//           consignee: apiData.consignee,
//         }[field];

//       case 4:
//         return {
//           indentor: apiData.createdBy,
//           project: apiData.projectName,
//           budgetName: apiData.budgetCode,
//           indentTitle: "Tender",
//           modeOfProcurement: apiData.modeOfProcurement,
//           consignee: apiData.consignee,
//           amount: apiData.amount,
//         }[field];

//       case 5:
//         return {
//           indentor: apiData.createdBy,
//           project: apiData.projectName,
//           budgetName: apiData.budgetCode,
//           indentTitle: "Service Order",
//           procurementMode: apiData.procurementType,
//           consignee: apiData.consignee,
//         }[field];

//       case 7:
//         return {
//           indentor: apiData.createdBy,
//           project: apiData.projectName,
//           budgetName: apiData.budgetCode,
//           indentTitle: "Tender",
//           modeOfProcurement: apiData.modeOfProcurement,
//           consignee: apiData.consignee,
//           amount: apiData.amount,
//         }[field];

//       case 9:
//         return {
//           indentor: apiData.indentorName,
//           budgetName: apiData.budgetCode,
//           indentTitle: "Material",
//           amount: apiData.amount,
//         }[field];

//       case 10:
//         return {
//           paymentType: apiData.paymentType,
//           poNo: apiData.poNo,
//           vendorName: apiData.vendorName,
//           amount: apiData.amount,
//         }[field];

//       default:
//         return "-";
//     }
//   };

//   // Payment Voucher columns
//   const pvColumns = [
//     {
//       title: "Request ID",
//       dataIndex: "requestId",
//       key: "requestId",
//       render: (text, record) => (
//         <Button type="link" onClick={() => fetchWorkflowDetails(record)}>
//           {text}
//         </Button>
//       ),
//       fixed: "left",
//     },
//     {
//       title: "Amount",
//       dataIndex: "amount",
//       key: "amount",
//       render: (_, record) =>
//         getCommonField(record.workflowId, record, "amount")
//           ? `₹${getCommonField(record.workflowId, record, "amount")}`
//           : "-",
//     },
//     {
//       title: "PO No",
//       dataIndex: "poNo",
//       key: "poNo",
//       render: (_, record) =>
//         getCommonField(record.workflowId, record, "poNo") || "-",
//     },
//     {
//       title: "Vendor Name",
//       dataIndex: "vendorName",
//       key: "vendorName",
//       render: (_, record) =>
//         getCommonField(record.workflowId, record, "vendorName") || "-",
//     },
//     {
//       title: "Invoice Type",
//       dataIndex: "paymentType",
//       key: "paymentType",
//       render: (_, record) =>
//         getCommonField(record.workflowId, record, "paymentType") || "-",
//     },
//     {
//       title: "Status",
//       dataIndex: "status",
//       key: "status",
//       render: (status) => (
//         <Tag color={status === "Approved" ? "green" : "volcano"}>{status}</Tag>
//       ),
//     },
//     {
//       title: "Actions",
//       key: "actions",
//       fixed: "right",
//       render: (_, record) => {
//         if (record.status === "Approved") return null;

//         return (
//           <Space>
//             <Button type="link" onClick={() => handleApprove(record)}>
//               Approve
//             </Button>

//             <Popover
//               content={
//                 <div style={{ padding: 12 }}>
//                   <Input.TextArea
//                     placeholder="Reject Comments"
//                     rows={3}
//                     value={rejectComment}
//                     onChange={(e) => setRejectComment(e.target.value)}
//                   />
//                   <Button
//                     type="primary"
//                     onClick={() => handleReject(record)}
//                     style={{ marginTop: 8 }}
//                   >
//                     Submit
//                   </Button>
//                 </div>
//               }
//               title="Reject"
//               trigger="click"
//             >
//               <Button danger type="link">
//                 Reject
//               </Button>
//             </Popover>

//             <Popover
//               content={
//                 <div style={{ padding: 12, width: 300 }}>
//                   <Select
//                     placeholder={
//                       loadingPreviousRoles ? "Loading roles..." : "Select a role"
//                     }
//                     value={selectedRole}
//                     onChange={setSelectedRole}
//                     style={{ width: "100%", marginBottom: 8 }}
//                     loading={loadingPreviousRoles}
//                     disabled={loadingPreviousRoles || previousRoles.length === 0}
//                   >
//                     {previousRoles.map((role) => (
//                       <Select.Option key={role} value={role}>
//                         {role}
//                       </Select.Option>
//                     ))}
//                   </Select>
//                   {previousRoles.length === 0 && !loadingPreviousRoles && (
//                     <Text type="secondary">No previous roles available.</Text>
//                   )}

//                   <Input.TextArea
//                     placeholder="Request Change Comments"
//                     rows={3}
//                     value={requestChangeComment}
//                     onChange={(e) => setRequestChangeComment(e.target.value)}
//                     style={{ marginTop: 8 }}
//                   />
//                   <Button
//                     type="primary"
//                     onClick={() => handleRequestChangeSubmit(record)}
//                     style={{ marginTop: 8 }}
//                     disabled={
//                       !selectedRole ||
//                       !requestChangeComment.trim() ||
//                       loadingPreviousRoles
//                     }
//                   >
//                     Submit
//                   </Button>
//                 </div>
//               }
//               title="Request Change"
//               trigger="click"
//               onOpenChange={(visible) => {
//                 if (visible) {
//                   fetchPreviousRoles(record.workflowId, record.requestId);
//                 } else {
//                   setPreviousRoles([]);
//                   setSelectedRole(null);
//                   setRequestChangeComment("");
//                   setLoadingPreviousRoles(false);
//                 }
//               }}
//             >
//               <Button type="link">Request Change</Button>
//             </Popover>
//           </Space>
//         );
//       },
//     },
//   ];

//   // Main columns
//   const columns = [
//     {
//       title: "Request ID",
//       dataIndex: "requestId",
//       key: "requestId",
//       render: (text, record) => (
//         <Button type="link" onClick={() => fetchWorkflowDetails(record)}>
//           {text}
//         </Button>
//       ),
//       fixed: "left",
//     },
//     {
//       title: "Indentor",
//       dataIndex: "indentor",
//       key: "indentor",
//       render: (_, record) =>
//         getCommonField(record.workflowId, record, "indentor") || "-",
//     },
//     {
//       title: "Amount",
//       key: "amount",
//       render: (_, record) => {
//         const amount = getCommonField(record.workflowId, record, "amount");
//         if (!amount) return "-";

//         // TC_52: Highlight tenders >10 lakh with enhanced workflow indicator
//         const isTender = [4, 7].includes(parseInt(record.workflowId, 10));
//         const isHighValue = parseFloat(amount) > 1000000;

//         return (
//           <span>
//             ₹{amount}
//             {isTender && isHighValue && (
//               <Tag color="purple" style={{marginLeft: 4, fontSize: 10}}>Enhanced</Tag>
//             )}
//           </span>
//         );
//       },
//     },
//     {
//       title: "Project",
//       dataIndex: "projectName",
//       key: "project",
//       render: (_, record) =>
//         getCommonField(record.workflowId, record, "project") || "-",
//     },
//     {
//       title: "Budget Name",
//       dataIndex: "budgetName",
//       key: "budgetName",
//       render: (_, record) =>
//         getCommonField(record.workflowId, record, "budget") || "-",
//     },
//     // {
//     //   title: "Indentor Title",
//     //   dataIndex: "workflowName",
//     //   key: "indentTitle",
//     //   render: (_, record) =>
//     //     getCommonField(record.workflowId, record, "indentTitle") || "-",
//     // },
//     {
//       title: "Mode of Procurement",
//       dataIndex: "modeOfProcurement",
//       key: "modeOfProcurement",
//       render: (_, record) =>
//         getCommonField(record.workflowId, record, "modeOfProcurement") || "-",
//     },
//     {
//       title: "Consignee",
//       dataIndex: "consignee",
//       key: "consignee",
//       render: (_, record) =>
//         getCommonField(record.workflowId, record, "consignee") || "-",
//     },
//     {
//       title: "Assigned To",
//       dataIndex: "assignedToEmployeeName",
//       key: "assignedTo",
//       render: (text) => text || "-",
//     },
//     {
//       title: "Status",
//       dataIndex: "status",
//       key: "status",
//       render: (status, record) => {
//         let finalStatus = status;

//         if (auth.role === "Purchase Head") {
//           finalStatus =
//             record.action === "Indentor Cancelled"
//               ? "Indentor Cancelled"
//               : "Completed";
//         }

//         return (
//           <Tag
//             color={
//               finalStatus === "Approved" || finalStatus === "Completed"
//                 ? "green"
//                 : "volcano"
//             }
//           >
//             {finalStatus}
//           </Tag>
//         );
//       },
//     },
//     ...(auth.role === "Purchase Head"
//       ? [
//           {
//             title: "Actions",
//             key: "actions",
//             fixed: "right",
//             render: (_, record) => {
//               if (record.action === "Indentor Cancelled") {
//                 return (
//                   <Popover
//                     content={
//                       <div style={{ padding: 12 }}>
//                         <Input.TextArea
//                           placeholder="Reject Comments"
//                           rows={3}
//                           value={rejectComment}
//                           onChange={(e) => setRejectComment(e.target.value)}
//                         />
//                         <Button
//                           type="primary"
//                           onClick={() => handleReject(record)}
//                           style={{ marginTop: 8 }}
//                         >
//                           Submit
//                         </Button>
//                       </div>
//                     }
//                     title="Reject"
//                     trigger="click"
//                   >
//                     <Button danger type="link">
//                       Cancel
//                     </Button>
//                   </Popover>
//                 );
//               }

//               return (
//                 <Popover
//                   content={
//                     <div style={{ padding: 12, width: 300 }}>
//                       <Select
//                         placeholder={
//                           loadingEmployees
//                             ? "Loading employees..."
//                             : "Select an employee"
//                         }
//                         value={selectedEmployee}
//                         onChange={(value) => setSelectedEmployee(value)}
//                         style={{ width: "100%", marginBottom: 8 }}
//                         loading={loadingEmployees}
//                         onFocus={fetchEmployees}
//                       >
//                         {employees.map((emp) => (
//                           <Option key={emp.employeeId} value={emp.employeeId}>
//                             {emp.employeeName}
//                           </Option>
//                         ))}
//                       </Select>
//                       <Button
//                         type="primary"
//                         onClick={() => handleAssign(record.requestId)}
//                         style={{ marginTop: 8 }}
//                         disabled={!selectedEmployee}
//                       >
//                         Assign
//                       </Button>
//                     </div>
//                   }
//                   title="Assign Indent"
//                   trigger="click"
//                   onOpenChange={(visible) => {
//                     if (!visible) setSelectedEmployee(null);
//                   }}
//                 >
//                   <Button type="link">Assign Indent</Button>
//                 </Popover>
//               );
//             },
//           },
//         ]
//       : [
//           {
//             title: "Actions",
//             key: "actions",
//             fixed: "right",
//             render: (_, record) => {
//               // For user ID 18, only show edit button for materials
//               if (userId === 18 && record.workflowName === "Material Workflow") {
//                 return (
//                   <Button
//                     type="primary"
//                     onClick={() =>
//                       navigate("/masters", {
//                         state: { materialCode: record.requestId, master: "Material" },
//                       })
//                     }
//                   >
//                     Edit
//                   </Button>
//                 );
//               }

//               // For Indent Creator role - Material Workflow
//               if (
//                 auth.role === "Indent Creator" &&
//                 record.workflowName === "Material Workflow"
//               ) {
//                 if (record.status === "Approved") return null;
//                 return (
//                   <Space>
//                     <Button
//                       type="primary"
//                       onClick={() =>
//                         navigate("/masters", {
//                           state: {
//                             materialCode: record.requestId,
//                             master: "Material",
//                           },
//                         })
//                       }
//                     >
//                       Edit
//                     </Button>
//                     <Button type="link" onClick={() => handleApprove(record)}>
//                       Approve
//                     </Button>
//                   </Space>
//                 );
//               }

//               // For Indent Creator role - Job Workflow
//               if (
//                 auth.role === "Indent Creator" &&
//                 record.workflowName === "Job Workflow"
//               ) {
//                 if (record.status === "Approved") return null;
//                 return (
//                   <Space>
//                     <Button
//                       type="primary"
//                       onClick={() =>
//                         navigate("/masters", {
//                           state: {
//                             jobCode: record.requestId,
//                             master: "Job",
//                           },
//                         })
//                       }
//                     >
//                       Edit
//                     </Button>
//                     <Button type="link" onClick={() => handleApprove(record)}>
//                       Approve
//                     </Button>
//                   </Space>
//                 );
//               }

//               // For Indent Creator role - other workflows (indents, etc.)
//               if (
//                 auth.role === "Indent Creator" &&
//                 record.workflowName !== "Material Workflow" &&
//                 record.workflowName !== "Job Workflow"
//               ) {
//                 if (record.status === "Approved") return null;

//                 return (
//                   <Space>
//                     <Button
//                       type="primary"
//                       onClick={() =>
//                         navigate("/procurement/indent/creation", {
//                           state: { indentId: record.requestId },
//                         })
//                       }
//                     >
//                       Edit
//                     </Button>
//                     <Button type="link" onClick={() => handleApprove(record)}>
//                       Approve
//                     </Button>
//                   </Space>
//                 );
//               }

//               // For Tender Creator role
//               if (auth.role === "Tender Creator") {
//                 if (record.status === "Approved") return null;

//                 return (
//                   <Space>
//                     <Button
//                       type="primary"
//                       onClick={() =>
//                         navigate("/procurement/tender/request", {
//                           state: { tenderId: record.requestId },
//                         })
//                       }
//                     >
//                       Edit
//                     </Button>
//                     <Button type="link" onClick={() => handleApprove(record)}>
//                       Approve
//                     </Button>
//                   </Space>
//                 );
//               }

//               // For PO Creator role
//               if (auth.role === "PO Creator") {
//                 if (record.status === "Approved") return null;

//                 return (
//                   <Space>
//                     <Button
//                       type="primary"
//                       onClick={() =>
//                         navigate("/procurement/purchaseOrder", {
//                           state: { poId: record.requestId },
//                         })
//                       }
//                     >
//                       Edit
//                     </Button>
//                     <Button type="link" onClick={() => handleApprove(record)}>
//                       Approve
//                     </Button>
//                   </Space>
//                 );
//               }

//               // For user ID 29, show all options including Request Change for ALL workflows
//             //  if (userId === 61) {
//             //  if (userId === 29 ) {
//              if (auth.role === "Store Purchase Officer" || userId === 29) {
//                 if (record.status === "Approved") return null;

//                 return (
//                   <Space>
//                     {/* Edit button for materials only */}
//                     {record.workflowName === "Material Workflow" && (
//                       <Button
//                         type="primary"
//                         onClick={() =>
//                           navigate("/masters", {
//                             state: {
//                               materialCode: record.requestId,
//                               master: "Material",
//                             },
//                           })
//                         }
//                       >
//                         Edit
//                       </Button>
//                     )}

//                     {/* Approve Button - for all workflows */}
//                     <Button type="link" onClick={() => handleApprove(record)}>
//                       Approve
//                     </Button>

//                     {/* Reject Button - for all workflows */}
//                     <Popover
//                       content={
//                         <div style={{ padding: 12 }}>
//                           <Input.TextArea
//                             placeholder="Reject Comments"
//                             rows={3}
//                             value={rejectComment}
//                             onChange={(e) => setRejectComment(e.target.value)}
//                           />
//                           <Button
//                             type="primary"
//                             onClick={() => handleReject(record)}
//                             style={{ marginTop: 8 }}
//                           >
//                             Submit
//                           </Button>
//                         </div>
//                       }
//                       title="Reject"
//                       trigger="click"
//                     >
//                       <Button danger type="link">
//                         Reject
//                       </Button>
//                     </Popover>

//                     {/* Request Change for VENDOR Workflow - No role dropdown, just comments */}
//                   {record.workflowName === "Vendor Workflow" && (
//                       <Popover
//                         content={
//                           <div style={{ padding: 12, width: 300 }}>
//                             <Input.TextArea
//                               placeholder="Request Change Comments"
//                               rows={3}
//                               value={requestChangeComment}
//                               onChange={(e) =>
//                                 setRequestChangeComment(e.target.value)
//                               }
//                             />
//                             <Button
//                               type="primary"
//                               onClick={() => handleRequestChangeSubmit(record)}
//                               style={{ marginTop: 8 }}
//                               disabled={!requestChangeComment.trim()}
//                             >
//                               Submit
//                             </Button>
//                           </div>
//                         }
//                         title="Request Change"
//                         trigger="click"
//                         onOpenChange={(visible) => {
//                           if (!visible) {
//                             setRequestChangeComment("");
//                           }
//                         }}
//                       >
//                         {/* <Button type="link">Request Change</Button> */}
//                       </Popover>
//                     )}   

//                     {/* Request Change for MATERIAL Workflow - Hardcoded "Indent Creator" */}
//                     {record.workflowName === "Material Workflow" && (
//                       <Popover
//                         content={
//                           <div style={{ padding: 12, width: 300 }}>
//                             <Select
//                               placeholder="Select a role"
//                               value={selectedRole}
//                               onChange={setSelectedRole}
//                               style={{ width: "100%", marginBottom: 8 }}
//                             >
//                               {previousRoles.map((role) => (
//                                 <Select.Option key={role} value={role}>
//                                   {role}
//                                 </Select.Option>
//                               ))}
//                             </Select>

//                             {previousRoles.length === 0 && (
//                               <Text type="secondary">
//                                 No previous roles available.
//                               </Text>
//                             )}

//                             <Input.TextArea
//                               placeholder="Request Change Comments"
//                               rows={3}
//                               value={requestChangeComment}
//                               onChange={(e) =>
//                                 setRequestChangeComment(e.target.value)
//                               }
//                               style={{ marginTop: 8 }}
//                             />

//                             <Button
//                               type="primary"
//                               onClick={() => handleRequestChangeSubmit(record)}
//                               style={{ marginTop: 8 }}
//                               disabled={
//                                 !selectedRole || !requestChangeComment.trim()
//                               }
//                             >
//                               Submit
//                             </Button>
//                           </div>
//                         }
//                         title="Request Change"
//                         trigger="click"
//                         onOpenChange={(visible) => {
//                           if (visible) {
//                             // Hardcode "Indent Creator" for Material workflow
//                             setPreviousRoles(["Indent Creator"]);
//                             setSelectedRole("Indent Creator");
//                             setLoadingPreviousRoles(false);
//                           } else {
//                             setPreviousRoles([]);
//                             setSelectedRole(null);
//                             setRequestChangeComment("");
//                             setLoadingPreviousRoles(false);
//                           }
//                         }}
//                       >
//                         <Button type="link">Request Change</Button>
//                       </Popover>
//                     )}

//                     {/* Request Change for JOB Workflow - Hardcoded "Indent Creator" */}
//                     {record.workflowName === "Job Workflow" && (
//                       <Popover
//                         content={
//                           <div style={{ padding: 12, width: 300 }}>
//                             <Select
//                               placeholder="Select a role"
//                               value={selectedRole}
//                               onChange={setSelectedRole}
//                               style={{ width: "100%", marginBottom: 8 }}
//                             >
//                               {previousRoles.map((role) => (
//                                 <Select.Option key={role} value={role}>
//                                   {role}
//                                 </Select.Option>
//                               ))}
//                             </Select>

//                             {previousRoles.length === 0 && (
//                               <Text type="secondary">
//                                 No previous roles available.
//                               </Text>
//                             )}

//                             <Input.TextArea
//                               placeholder="Request Change Comments"
//                               rows={3}
//                               value={requestChangeComment}
//                               onChange={(e) =>
//                                 setRequestChangeComment(e.target.value)
//                               }
//                               style={{ marginTop: 8 }}
//                             />

//                             <Button
//                               type="primary"
//                               onClick={() => handleRequestChangeSubmit(record)}
//                               style={{ marginTop: 8 }}
//                               disabled={
//                                 !selectedRole || !requestChangeComment.trim()
//                               }
//                             >
//                               Submit
//                             </Button>
//                           </div>
//                         }
//                         title="Request Change"
//                         trigger="click"
//                         onOpenChange={(visible) => {
//                           if (visible) {
//                             // Hardcode "Indent Creator" for Job workflow
//                             setPreviousRoles(["Indent Creator"]);
//                             setSelectedRole("Indent Creator");
//                             setLoadingPreviousRoles(false);
//                           } else {
//                             setPreviousRoles([]);
//                             setSelectedRole(null);
//                             setRequestChangeComment("");
//                             setLoadingPreviousRoles(false);
//                           }
//                         }}
//                       >
//                         <Button type="link">Seek Clarification</Button>
//                       </Popover>
//                     )}

//                     {/* Request Change for REGULAR Workflows - Fetch roles from API */}
//                     {record.workflowName !== "Vendor Workflow" &&
//                       record.workflowName !== "Material Workflow" &&
//                       record.workflowName !== "Job Workflow" && (
//                         <Popover
//                           content={
//                             <div style={{ padding: 12, width: 300 }}>
//                               <Select
//                                 placeholder={
//                                   loadingPreviousRoles
//                                     ? "Loading roles..."
//                                     : "Select a role"
//                                 }
//                                 value={selectedRole}
//                                 onChange={setSelectedRole}
//                                 style={{ width: "100%", marginBottom: 8 }}
//                                 loading={loadingPreviousRoles}
//                                 disabled={
//                                   loadingPreviousRoles ||
//                                   previousRoles.length === 0
//                                 }
//                               >
//                                 {previousRoles.map((role) => (
//                                   <Select.Option key={role} value={role}>
//                                     {role}
//                                   </Select.Option>
//                                 ))}
//                               </Select>

//                               {previousRoles.length === 0 &&
//                                 !loadingPreviousRoles && (
//                                   <Text type="secondary">
//                                     No previous roles available.
//                                   </Text>
//                                 )}

//                               <Input.TextArea
//                                 placeholder="Request Change Comments"
//                                 rows={3}
//                                 value={requestChangeComment}
//                                 onChange={(e) =>
//                                   setRequestChangeComment(e.target.value)
//                                 }
//                                 style={{ marginTop: 8 }}
//                               />

//                               <Button
//                                 type="primary"
//                                 onClick={() => handleRequestChangeSubmit(record)}
//                                 style={{ marginTop: 8 }}
//                                 disabled={
//                                   !selectedRole ||
//                                   !requestChangeComment.trim() ||
//                                   loadingPreviousRoles
//                                 }
//                               >
//                                 Submit
//                               </Button>
//                             </div>
//                           }
//                           title="Request Change"
//                           trigger="click"
//                           onOpenChange={(visible) => {
//                             if (visible) {
//                               if (
//                                 record.workflowId &&
//                                 record.workflowId !== null
//                               ) {
//                                 fetchPreviousRoles(
//                                   record.workflowId,
//                                   record.requestId
//                                 );
//                               } else {
//                                 message.error("Cannot load previous roles");
//                                 setPreviousRoles([]);
//                                 setLoadingPreviousRoles(false);
//                               }
//                             } else {
//                               setPreviousRoles([]);
//                               setSelectedRole(null);
//                               setRequestChangeComment("");
//                               setLoadingPreviousRoles(false);
//                             }
//                           }}
//                         >
//                           <Button type="link">Request Change</Button>
//                         </Popover>
//                       )}
//                   </Space>
//                 );
//               }

//               // For other users, show default options
//               if (record.status === "Approved") return null;

//               return (
//                 <Space>
//                   <Button type="link" onClick={() => handleApprove(record)}>
//                     Approve
//                   </Button>

//                   <Popover
//                     content={
//                       <div style={{ padding: 12 }}>
//                         <Input.TextArea
//                           placeholder="Reject Comments"
//                           rows={3}
//                           value={rejectComment}
//                           onChange={(e) => setRejectComment(e.target.value)}
//                         />
//                         <Button
//                           type="primary"
//                           onClick={() => handleReject(record)}
//                           style={{ marginTop: 8 }}
//                         >
//                           Submit
//                         </Button>
//                       </div>
//                     }
//                     title="Reject"
//                     trigger="click"
//                   >
//                     <Button danger type="link">
//                       Reject
//                     </Button>
//                   </Popover>

//                   {/* Request Change for Material Workflow - Hardcoded Role */}
//                   {record.workflowName === "Material Workflow" && (
//                     <Popover
//                       content={
//                         <div style={{ padding: 12, width: 300 }}>
//                           <Select
//                             placeholder="Select a role"
//                             value={selectedRole}
//                             onChange={setSelectedRole}
//                             style={{ width: "100%", marginBottom: 8 }}
//                           >
//                             {previousRoles.map((role) => (
//                               <Select.Option key={role} value={role}>
//                                 {role}
//                               </Select.Option>
//                             ))}
//                           </Select>

//                           {previousRoles.length === 0 && (
//                             <Text type="secondary">
//                               No previous roles available.
//                             </Text>
//                           )}

//                           <Input.TextArea
//                             placeholder="Request Change Comments"
//                             rows={3}
//                             value={requestChangeComment}
//                             onChange={(e) =>
//                               setRequestChangeComment(e.target.value)
//                             }
//                             style={{ marginTop: 8 }}
//                           />

//                           <Button
//                             type="primary"
//                             onClick={() => handleRequestChangeSubmit(record)}
//                             style={{ marginTop: 8 }}
//                             disabled={
//                               !selectedRole || !requestChangeComment.trim()
//                             }
//                           >
//                             Submit
//                           </Button>
//                         </div>
//                       }
//                       title="Request Change"
//                       trigger="click"
//                       onOpenChange={(visible) => {
//                         if (visible) {
//                           setPreviousRoles(["Indent Creator"]);
//                           setSelectedRole("Indent Creator");
//                           setLoadingPreviousRoles(false);
//                         } else {
//                           setPreviousRoles([]);
//                           setSelectedRole(null);
//                           setRequestChangeComment("");
//                           setLoadingPreviousRoles(false);
//                         }
//                       }}
//                     >
//                       <Button type="link">Request Change</Button>
//                     </Popover>
//                   )}

//                   {/* Request Change for JOB Workflow - Hardcoded "Indent Creator" */}
//                   {record.workflowName === "Job Workflow" && (
//                     <Popover
//                       content={
//                         <div style={{ padding: 12, width: 300 }}>
//                           <Select
//                             placeholder="Select a role"
//                             value={selectedRole}
//                             onChange={setSelectedRole}
//                             style={{ width: "100%", marginBottom: 8 }}
//                           >
//                             {previousRoles.map((role) => (
//                               <Select.Option key={role} value={role}>
//                                 {role}
//                               </Select.Option>
//                             ))}
//                           </Select>

//                           {previousRoles.length === 0 && (
//                             <Text type="secondary">
//                               No previous roles available.
//                             </Text>
//                           )}

//                           <Input.TextArea
//                             placeholder="Request Change Comments"
//                             rows={3}
//                             value={requestChangeComment}
//                             onChange={(e) =>
//                               setRequestChangeComment(e.target.value)
//                             }
//                             style={{ marginTop: 8 }}
//                           />

//                           <Button
//                             type="primary"
//                             onClick={() => handleRequestChangeSubmit(record)}
//                             style={{ marginTop: 8 }}
//                             disabled={
//                               !selectedRole || !requestChangeComment.trim()
//                             }
//                           >
//                             Submit
//                           </Button>
//                         </div>
//                       }
//                       title="Request Change"
//                       trigger="click"
//                       onOpenChange={(visible) => {
//                         if (visible) {
//                           setPreviousRoles(["Indent Creator"]);
//                           setSelectedRole("Indent Creator");
//                           setLoadingPreviousRoles(false);
//                         } else {
//                           setPreviousRoles([]);
//                           setSelectedRole(null);
//                           setRequestChangeComment("");
//                           setLoadingPreviousRoles(false);
//                         }
//                       }}
//                     >
//                       <Button type="link">Request Change</Button>
//                     </Popover>
//                   )}

//                   {/* Request Change for Regular Workflows */}
//                   {record.workflowName !== "Vendor Workflow" &&
//                     record.workflowName !== "Material Workflow" &&
//                     record.workflowName !== "Job Workflow" && (
//                       <Popover
//                         content={
//                           <div style={{ padding: 12, width: 300 }}>
//                             <Select
//                               placeholder={
//                                 loadingPreviousRoles
//                                   ? "Loading roles..."
//                                   : "Select a role"
//                               }
//                               value={selectedRole}
//                               onChange={setSelectedRole}
//                               style={{ width: "100%", marginBottom: 8 }}
//                               loading={loadingPreviousRoles}
//                               disabled={
//                                 loadingPreviousRoles ||
//                                 previousRoles.length === 0
//                               }
//                             >
//                               {previousRoles.map((role) => (
//                                 <Select.Option key={role} value={role}>
//                                   {role}
//                                 </Select.Option>
//                               ))}
//                             </Select>

//                             {previousRoles.length === 0 &&
//                               !loadingPreviousRoles && (
//                                 <Text type="secondary">
//                                   No previous roles available.
//                                 </Text>
//                               )}

//                             <Input.TextArea
//                               placeholder="Request Change Comments"
//                               rows={3}
//                               value={requestChangeComment}
//                               onChange={(e) =>
//                                 setRequestChangeComment(e.target.value)
//                               }
//                               style={{ marginTop: 8 }}
//                             />

//                             <Button
//                               type="primary"
//                               onClick={() => handleRequestChangeSubmit(record)}
//                               style={{ marginTop: 8 }}
//                               disabled={
//                                 !selectedRole ||
//                                 !requestChangeComment.trim() ||
//                                 loadingPreviousRoles
//                               }
//                             >
//                               Submit
//                             </Button>
//                           </div>
//                         }
//                         title="Request Change"
//                         trigger="click"
//                         onOpenChange={(visible) => {
//                           if (visible) {
//                             if (record.workflowId && record.workflowId !== null) {
//                               fetchPreviousRoles(
//                                 record.workflowId,
//                                 record.requestId
//                               );
//                             } else {
//                               message.error("Cannot load previous roles");
//                               setPreviousRoles([]);
//                               setLoadingPreviousRoles(false);
//                             }
//                           } else {
//                             setPreviousRoles([]);
//                             setSelectedRole(null);
//                             setRequestChangeComment("");
//                             setLoadingPreviousRoles(false);
//                           }
//                         }}
//                       >
//                         <Button type="link">Seek Clarification</Button>
//                       </Popover>
//                     )}
//                 </Space>
//               );
//             },
//           },
//         ]),
//   ];

//   const columnsToRender = requestType === "PV" ? pvColumns : columns;

//   const filteredData = data.filter((item) =>
//     item.requestId.toString().toLowerCase().includes(searchTerm.toLowerCase())
//   );

//   const handleSearch = useCallback((value) => {
//     setSearchTerm(value);
//   }, []);

//   const handleReset = useCallback(() => {
//     setSearchTerm("");
//   }, []);

//   return (
//     <div style={{ padding: 24 }}>
//       <FilterComponent
//         onSearch={handleSearch}
//         searchTerm={searchTerm}
//         onReset={handleReset}
//       />
//       <Space style={{ marginBottom: 16 }}>
//         {auth.role !== "Purchase Head" && (
//           <Button
//             type="primary"
//             onClick={handleApproveAll}
//             disabled={selectedRows.length === 0}
//           >
//             Approve All
//           </Button>
//         )}
//         {auth.role === "Purchase Head" && (
//           <Button
//             type="primary"
//             disabled={selectedRows.length === 0}
//             onClick={() => {
//               const selectedProjectNames = [
//                 ...new Set(selectedRows.map((row) => row.projectName)),
//               ];

//               if (selectedProjectNames.length > 1) {
//                 message.error("Selected indents belong to different projects");
//                 return;
//               }

//               navigate("/procurement/tender/request", {
//                 state: {
//                   indentIds: selectedRows.map((row) => row.requestId),
//                 },
//               });
//             }}
//           >
//             Multiple Indent Ids Tender Creation
//           </Button>
//         )}
//         {Object.entries(workflowCounts).map(([id, count]) => (
//           <Tag key={id} color="blue">
//           {id.replace(" Workflow", "").toUpperCase()} ({count})
//             Pending RequestIds Count: {count}
//           </Tag>
//         ))}
//       </Space>

//       {loading ? (
//         <Spin size="large" tip="Loading..." style={{ marginTop: 24 }} />
//       ) : error ? (
//         <Text type="danger">{error}</Text>
//       ) : (
//         <Table
//           rowSelection={rowSelection}
//           rowKey="key"
//           columns={columnsToRender}
//           dataSource={filteredData}
//         />
//       )}

//       <QueueModal
//         modalVisible={modalVisible}
//         setModalVisible={setModalVisible}
//         selectedRecord={selectedRecord}
//         detailsData={detailsData}
//         historyVisible={historyVisible}
//         setHistoryVisible={setHistoryVisible}
//         materialHistoryVisible={materialHistoryVisible}
//         setMaterialHistoryVisible={setMaterialHistoryVisible}
//         selectedMaterialCode={selectedMaterialCode}
//         setSelectedMaterialCode={setSelectedMaterialCode}
//       />
//       <MaterialDetailModal
//         visible={materialModalOpen}
//         setVisible={setMaterialModalOpen}
//         materialData={materialDtl}
//       />
//       <VendorDetailModal
//         visible={vendorModalOpen}
//         setVisible={setVendorModalOpen}
//         vendorData={vendorDtl}
//       />
//       <JobDetailModal
//         visible={jobModalOpen}
//         setVisible={setJobModalOpen}
//         jobData={jobDtl}
//       />
//     </div>
//   );
// };

// export default QueueRequest;


import React, { useState, useCallback } from "react";
import {
  Table,
  Input,
  Button,
  Space,
  Typography,
  Popover,
  Tag,
  message,
  Spin,
  Select,
  Descriptions, 
  Badge,
  Modal
} from "antd";
import { SearchOutlined } from "@ant-design/icons";
import axios from "axios";
import { useSelector } from "react-redux";
import QueueModal from "./QueueModal";
import { useNavigate } from "react-router-dom";
import { baseURL } from '../../../App';

const { Text } = Typography;
const { Option } = Select;

const MaterialDetailModal = ({ visible, setVisible, materialData }) => {
  if (!materialData) return null;

  return (
    <Modal
      title="Material Details"
      open={visible}
      onCancel={() => setVisible(false)}
      footer={[
        <Button key="close" onClick={() => setVisible(false)}>
          Close
        </Button>,
      ]}
      width={700}
    >
      <Descriptions bordered column={2}>
        <Descriptions.Item label="Material Code" span={2}>
          {materialData.materialCode}
        </Descriptions.Item>

        <Descriptions.Item label="Description" span={2}>
          {materialData.description}
        </Descriptions.Item>

        <Descriptions.Item label="Category">
          {materialData.category}
        </Descriptions.Item>

        <Descriptions.Item label="Sub Category">
          {materialData.subCategory}
        </Descriptions.Item>

        <Descriptions.Item label="UOM">
          {materialData.uom}
        </Descriptions.Item>

        <Descriptions.Item label="Unit Price">
          {materialData.currency} {materialData.unitPrice}
        </Descriptions.Item>

        <Descriptions.Item label="Origin">
          {materialData.indigenousOrImported ? "Indigenous" : "Imported"}
        </Descriptions.Item>

        <Descriptions.Item label="Created By">
          {materialData.createdBy}
        </Descriptions.Item>

        <Descriptions.Item label="Created Date">
          {new Date(materialData.createdDate).toLocaleString()}
        </Descriptions.Item>

        <Descriptions.Item label="Updated Date">
          {new Date(materialData.updatedDate).toLocaleString()}
        </Descriptions.Item>

        <Descriptions.Item label="Status" span={2}>
          <Badge
            status={
              materialData.approvalStatus === "APPROVED"
                ? "success"
                : materialData.approvalStatus === "REJECTED"
                ? "error"
                : "warning"
            }
            text={materialData.approvalStatus.replace("_", " ")}
          />
        </Descriptions.Item>

        <Descriptions.Item label="Comments" span={2}>
          {materialData.comments || "No comments"}
        </Descriptions.Item>
        {/* Added by aman */}
            <Descriptions.Item label = "Asset" span = {2}>
              {materialData.asset_Flag ? "Yes" : "No"}
            </Descriptions.Item>
        {/* End */}

<Descriptions.Item label="Upload Documents" span={2}>
  <div className="detail-item">
    {materialData.uploadImageFileName
      ? materialData.uploadImageFileName
          .split(",")
          .map((fileName, index, array) => {
            const trimmed = fileName.trim();
            return (
              <span key={index}>
                <a
                  href={`${baseURL}/file/view/Material/${trimmed}`}
                  target="_blank"
                  rel="noopener noreferrer"
                >
                  {trimmed} (View)
                </a>
                {index < array.length - 1 && ", "}
              </span>
            );
          })
      : "N/A"}
  </div>
</Descriptions.Item>
      </Descriptions>
    </Modal>
  );
};

const VendorDetailModal = ({ visible, setVisible, vendorData }) => {
  if (!vendorData) return null;
  
  return (
    <Modal
      title="Vendor Details"
      open={visible}
      onCancel={() => setVisible(false)}
      footer={[
        <Button key="close" onClick={() => setVisible(false)}>
          Close
        </Button>
      ]}
      width={700}
    >
      <Descriptions bordered column={2}>
        <Descriptions.Item label="Vendor Code" span={2}>
          {vendorData.vendorId}
        </Descriptions.Item>
        <Descriptions.Item label="Vendor Name" span={2}>
          {vendorData.vendorName}
        </Descriptions.Item>
        <Descriptions.Item label="Vendor Type">
          {vendorData.vendorType}
        </Descriptions.Item>
        <Descriptions.Item label="Contact Number">
          {vendorData.contactNumber}
        </Descriptions.Item>
        <Descriptions.Item label="Email Address">
          {vendorData.emailAddress}
        </Descriptions.Item>
        <Descriptions.Item label="PFMS Vendor Code">
          {vendorData.pfmsVendorCode} 
        </Descriptions.Item>
        <Descriptions.Item label="Primary Business">
          {vendorData.primaryBusiness}
        </Descriptions.Item>
        <Descriptions.Item label="Address">
          {vendorData.address}
        </Descriptions.Item>
        <Descriptions.Item label="Alternate Email/Phone Number">
          {vendorData.alternateEmailOrPhoneNumber}
        </Descriptions.Item>
        <Descriptions.Item label="Fax Number">
          {vendorData.faxNumber}
        </Descriptions.Item>
        <Descriptions.Item label="Pan Number">
          {vendorData.panNumber}
        </Descriptions.Item>
        <Descriptions.Item label="GST Number">
          {vendorData.gstNumber}
        </Descriptions.Item>
        <Descriptions.Item label="Bank Name">
          {vendorData.bankName}
        </Descriptions.Item>
        <Descriptions.Item label="Account Number">
          {vendorData.accountNumber}
        </Descriptions.Item>
        <Descriptions.Item label="IFSC Code">
          {vendorData.ifscCode}
        </Descriptions.Item>
        <Descriptions.Item label="Swift Code">
          {vendorData.swiftCode}
        </Descriptions.Item>
        <Descriptions.Item label="Bic Code">
          {vendorData.bicCode}
        </Descriptions.Item>
        <Descriptions.Item label="IBAN ABA Number">
          {vendorData.ibanAbaNumber}
        </Descriptions.Item>
        <Descriptions.Item label="Sort Code">
          {vendorData.sortCode}
        </Descriptions.Item>
        <Descriptions.Item label="Bank Routing Number">
          {vendorData.bankRoutingNumber}
        </Descriptions.Item>
        <Descriptions.Item label="Bank Address">
          {vendorData.bankAddress}
        </Descriptions.Item>
        <Descriptions.Item label="Country">
          {vendorData.country}
        </Descriptions.Item>
        <Descriptions.Item label="State">
          {vendorData.state}
        </Descriptions.Item>
        <Descriptions.Item label="Place">
          {vendorData.place}
        </Descriptions.Item>
        <Descriptions.Item label="Registered Platform">
          {vendorData.registeredPlatform ? "True" : "False"}
        </Descriptions.Item>
        <Descriptions.Item label="Created By">
          {vendorData.createdBy}
        </Descriptions.Item>
        <Descriptions.Item label="Created Date">
          {new Date(vendorData.createdDate).toLocaleString()}
        </Descriptions.Item>
        <Descriptions.Item label="Status" span={2}>
          <Badge 
            status={vendorData.approvalStatus === "APPROVED" ? "success" : 
                   vendorData.approvalStatus === "REJECTED" ? "error" : "warning"} 
            text={vendorData.approvalStatus.replace("_", " ")} 
          />
        </Descriptions.Item>
      </Descriptions>
    </Modal>
  );
};

const JobDetailModal = ({ visible, setVisible, jobData }) => {
  if (!jobData) return null;

  return (
    <Modal
      title="Job Details"
      open={visible}
      onCancel={() => setVisible(false)}
      footer={[
        <Button key="close" onClick={() => setVisible(false)}>
          Close
        </Button>,
      ]}
      width={700}
    >
      <Descriptions bordered column={2}>
        <Descriptions.Item label="Job Code" span={2}>
          {jobData.jobCode}
        </Descriptions.Item>
        <Descriptions.Item label="Job Description" span={2}>
          {jobData.jobDescription}
        </Descriptions.Item>
        <Descriptions.Item label="Category">
          {jobData.category}
        </Descriptions.Item>
        <Descriptions.Item label="Sub Category">
          {jobData.subCategory}
        </Descriptions.Item>
        <Descriptions.Item label="UOM">
          {jobData.uom}
        </Descriptions.Item>
        <Descriptions.Item label="Estimated Price">
          {jobData.currency} {jobData.estimatedPriceWithCcy}
        </Descriptions.Item>
        <Descriptions.Item label="Asset ID">
          {jobData.assetId || "N/A"}
        </Descriptions.Item>
        <Descriptions.Item label="Value">
          {jobData.value || "N/A"}
        </Descriptions.Item>
        <Descriptions.Item label="Brief Description" span={2}>
          {jobData.briefDescription || "N/A"}
        </Descriptions.Item>
        <Descriptions.Item label="Created By">
          {jobData.createdBy}
        </Descriptions.Item>
        <Descriptions.Item label="Created Date">
          {jobData.createdDate ? new Date(jobData.createdDate).toLocaleString() : "N/A"}
        </Descriptions.Item>
        <Descriptions.Item label="Status" span={2}>
          <Badge
            status={
              jobData.approvalStatus === "APPROVED"
                ? "success"
                : jobData.approvalStatus === "REJECTED"
                ? "error"
                : "warning"
            }
            text={(jobData.approvalStatus || "").replace(/_/g, " ")}
          />
        </Descriptions.Item>
        <Descriptions.Item label="Comments" span={2}>
          {jobData.comments || "No comments"}
        </Descriptions.Item>
      </Descriptions>
    </Modal>
  );
};

const FilterComponent = ({ onSearch, searchTerm, onReset }) => (
  <div style={{ marginBottom: 16 }}>
    <Space>
      <Input
        placeholder="Search by Request ID"
        prefix={<SearchOutlined />}
        value={searchTerm}
        onChange={(e) => onSearch(e.target.value)}
        style={{ width: 300 }}
        onPressEnter={() => onSearch(searchTerm)}
        allowClear
      />
      <Button
        type="primary"
        icon={<SearchOutlined />}
        onClick={() => onSearch(searchTerm)}
      >
        Search
      </Button>
      <Button onClick={onReset}>Reset</Button>
    </Space>
  </div>
);

// data, loading, and refetchData are now provided by the parent Queue1.
// QueueRequest no longer fetches from the API itself.
const QueueRequest = ({ workflowId, requestType, data = [], loading = false, refetchData }) => {
  const auth = useSelector((state) => state.auth);
  const { userId } = auth;
  const actionPerformer = auth.userId;
  const navigate = useNavigate();

  // State declarations (fetch-related state removed — owned by Queue1 now)
  const [error, setError] = useState(null);
  const [detailLoading, setDetailLoading] = useState(false); // for the detail modal fetch only
  const [rejectComment, setRejectComment] = useState("");
  const [requestChangeComment, setRequestChangeComment] = useState("");
  const [detailsData, setDetailsData] = useState(null);
  const [modalVisible, setModalVisible] = useState(false);
  const [selectedRecord, setSelectedRecord] = useState(null);
  const [previousRoles, setPreviousRoles] = useState([]);
  const [selectedRole, setSelectedRole] = useState(null);
  const [loadingPreviousRoles, setLoadingPreviousRoles] = useState(false);
  const [searchTerm, setSearchTerm] = useState("");
  const [historyVisible, setHistoryVisible] = useState(false);
  const [queueData, setQueueData] = useState([]);
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);
  const [selectedRows, setSelectedRows] = useState([]);
  const [workflowCounts] = useState({});
  const [materialHistoryVisible, setMaterialHistoryVisible] = useState(false);
  const [selectedMaterialCode, setSelectedMaterialCode] = useState(null);
  const [employees, setEmployees] = useState([]);
  const [selectedEmployee, setSelectedEmployee] = useState(null);
  const [loadingEmployees, setLoadingEmployees] = useState(false);
  const [materialModalOpen, setMaterialModalOpen] = useState(false);
  const [materialDtl, setMaterialDtl] = useState(null);
  const [vendorModalOpen, setVendorModalOpen] = useState(false);
  const [vendorDtl, setVendorDtl] = useState(null);
  const [jobModalOpen, setJobModalOpen] = useState(false);
  const [jobDtl, setJobDtl] = useState(null);

  // Fetch employees for assignment
  const fetchEmployees = () => {
    if (employees.length === 0) {
      setLoadingEmployees(true);
      axios
        .get("/api/employee-department-master/employeeName/byDepartment", {
          params: { department: "Purchase" }
        })
        .then((res) => setEmployees(res.data.responseData))
        .finally(() => setLoadingEmployees(false));
    }
  };

  // Handle employee assignment
  const handleAssign = (indentId) => {
    if (!selectedEmployee) {
      message.warning("Please select an employee");
      return;
    }

    const emp = employees.find((e) => e.employeeId === selectedEmployee);

    axios
      .post("/api/indents/assign-employee", {
        indentId,
        employeeId: selectedEmployee,
        employeeName: emp ? emp.employeeName : "",
      })
      .then(() => {
        message.success("Indent assigned successfully");
        setSelectedEmployee(null);
      })
      .catch(() => {
        message.error("Failed to assign indent");
      });
  };

  // Fetch previous roles for request change
  const fetchPreviousRoles = async (workflowId, requestId) => {
    setLoadingPreviousRoles(true);
    try {
      const response = await axios.get(
        `/allPreviousWorkflowRole?workflowId=${encodeURIComponent(
          workflowId
        )}&requestId=${encodeURIComponent(requestId)}`
      );
      const roles = response.data.responseData || [];
      const filteredRoles = roles.filter(
        (role) =>
          role.trim().toLowerCase() !== (auth?.role || "").trim().toLowerCase()
      );
      setPreviousRoles(filteredRoles);
    } catch (error) {
      message.error("Failed to fetch previous roles.");
      console.error("Fetch previous roles error:", error);
    } finally {
      setLoadingPreviousRoles(false);
    }
  };

  // Handle bulk approve
  const handleApproveAll = async () => {
    if (selectedRows.length === 0) {
      message.warning("No records selected.");
      return;
    }

    const vendors = selectedRows.filter((r) => r.workflowName === "Vendor Workflow");
    const materials = selectedRows.filter((r) => r.workflowName === "Material Workflow");
    const jobs = selectedRows.filter((r) => r.workflowName === "Job Workflow");
    const others = selectedRows.filter(
      (r) =>
        r.workflowName !== "Vendor Workflow" &&
        r.workflowName !== "Material Workflow" &&
        r.workflowName !== "Job Workflow"
    );

    try {
      // Bulk approve vendors
      if (vendors.length > 0) {
        const vendorPayload = vendors.map((record) => ({
          action: "APPROVED",
          actionBy: actionPerformer,
          remarks: "Vendor approved",
          requestId: record.requestId,
        }));
        await axios.post("/api/vendor-master-util/performBulkAction", vendorPayload);
      }

      // Bulk approve materials
      if (materials.length > 0) {
        const materialPayload = materials.map((record) => ({
          action: "APPROVED",
          actionBy: actionPerformer,
          remarks: "Material approved",
          requestId: record.requestId,
        }));
        await axios.post("/api/material-master-util/performBulkActionForMaterial", materialPayload);
      }

      // Bulk approve jobs
      if (jobs.length > 0) {
        const jobPayload = jobs.map((record) => ({
          action: "APPROVED",
          actionBy: actionPerformer,
          remarks: "Job approved",
          requestId: record.requestId,
        }));
        for (const p of jobPayload) {
          await axios.post("/api/job-master/performAction", p);
        }
      }

      // Bulk approve others
      if (others.length > 0) {
        const otherPayload = others.map((record) => ({
          action: "APPROVED",
          actionBy: actionPerformer,
          assignmentRole: null,
          remarks: "Approved successfully",
          requestId: record.requestId,
          workflowTransitionId: record.workflowTransitionId,
          roleName: auth.role,
        }));
        await axios.post("/performAllTransitionAction", otherPayload);
      }

      message.success("All selected records approved.");
      setSelectedRowKeys([]);
      setSelectedRows([]);
      refetchData?.();
    } catch (error) {
      console.error("Bulk approval error:", error);
      message.error("Failed to approve selected records.");
    }
  };

  // Row selection
  const rowSelection = {
    selectedRowKeys,
    onChange: (newSelectedRowKeys, newSelectedRows) => {
      setSelectedRowKeys(newSelectedRowKeys);
      setSelectedRows(newSelectedRows);
    },
  };

  // Handle single approve
  const handleApprove = async (record) => {
    try {
      if (record.workflowName === "Vendor Workflow") {
        await axios.post("/api/vendor-master-util/performAction", {
          action: "APPROVED",
          actionBy: actionPerformer,
          remarks: "Vendor approved",
          requestId: record.requestId,
        });
      } else if (record.workflowName === "Material Workflow") {
        await axios.post("/api/material-master-util/performActionForMaterial", {
          action: "APPROVED",
          actionBy: actionPerformer,
          remarks: "Material approved",
          requestId: record.requestId,
        });
      } else if (record.workflowName === "Job Workflow") {
        await axios.post("/api/job-master/performAction", {
          action: "APPROVED",
          actionBy: actionPerformer,
          remarks: "Job approved",
          requestId: record.requestId,
        });
      } else {
        const workflowTransitionId = record.workflowTransitionId;
        if (!workflowTransitionId) {
          message.error("Workflow transition ID not found for this request.");
          return;
        }

        const payload = {
          action: "APPROVED",
          actionBy: actionPerformer,
          assignmentRole: null,
          remarks: "Approved successfully",
          requestId: record.requestId,
          workflowTransitionId: record.workflowTransitionId,
          roleName: auth.role,
        };
        await axios.post("/performTransitionAction", payload);
      }

      message.success(`Request ${record.requestId} processed`);
      refetchData?.();
    } catch (error) {
      message.error("Failed to approve");
      console.error("Approval error:", error);
    }
  };

  // Handle reject
  // CHANGED: Rejection is now a terminal action — no history fetch, no previous-role lookup,
  // no assignmentRole. We simply send the current workflowTransitionId and let the backend
  // close the transition, cancel all siblings, and mark the request as REJECTED.
  // Vendor / Material / Job paths are unchanged (they use their own dedicated endpoints).
  const handleReject = async (record) => {
    if (!rejectComment.trim()) {
      message.warning("Please enter a reject comment.");
      return;
    }

    try {
      if (record.workflowName === "Vendor Workflow") {
        await axios.post("/api/vendor-master-util/performAction", {
          action: "REJECTED",
          actionBy: actionPerformer,
          remarks: rejectComment,
          requestId: record.requestId,
        });
      } else if (record.workflowName === "Material Workflow") {
        await axios.post("/api/material-master-util/performActionForMaterial", {
          action: "REJECTED",
          actionBy: actionPerformer,
          remarks: rejectComment,
          requestId: record.requestId,
        });
      } else if (record.workflowName === "Job Workflow") {
        await axios.post("/api/job-master/performAction", {
          action: "REJECTED",
          actionBy: actionPerformer,
          remarks: rejectComment,
          requestId: record.requestId,
        });
      } else {
        // Terminal rejection for all other workflow types (Indent, Tender, PO, SO, CP, PV…).
        // No history fetch needed — the backend terminates the workflow in one shot.
        if (!record.workflowTransitionId) {
          message.error("Workflow transition ID not found for this request.");
          return;
        }

        const payload = {
          action: "REJECTED",
          actionBy: actionPerformer,
          remarks: rejectComment,
          requestId: record.requestId,
          workflowTransitionId: record.workflowTransitionId,
          // assignmentRole intentionally omitted — rejection no longer routes backward
        };

        await axios.post("/performTransitionAction", payload, {
          headers: { "Content-Type": "application/json" },
        });
      }

      message.success(`Request ${record.requestId} rejected and terminated`);
      setRejectComment("");
      refetchData?.();
    } catch (error) {
      let backendMessage = "Failed to reject";

      if (error.response?.data?.responseStatus?.message) {
        backendMessage = error.response.data.responseStatus.message;
      } else if (error.response?.data?.message) {
        backendMessage = error.response.data.message;
      } else if (error.message) {
        backendMessage = error.message;
      }

      message.error(backendMessage);
      console.error("Rejection error:", error);
    }
  };

  // Handle request change submit
  const handleRequestChangeSubmit = async (record) => {
    if (!requestChangeComment.trim()) {
      message.warning("Please enter request change comments.");
      return;
    }

    try {
      if (record.workflowName === "Vendor Workflow") {
        // For vendors: No role selection, just comments
        await axios.post("/api/vendor-master-util/performAction", {
          action: "CHANGE REQUEST",
          actionBy: actionPerformer,
          remarks: requestChangeComment,
          requestId: record.requestId,
        });
      } else if (record.workflowName === "Material Workflow") {
        // For materials: Hardcoded "Indent Creator" role
        await axios.post("/api/material-master-util/performActionForMaterial", {
          action: "CHANGE REQUEST",
          actionBy: actionPerformer,
          remarks: requestChangeComment,
          requestId: record.requestId,
        });
      } else if (record.workflowName === "Job Workflow") {
        await axios.post("/api/job-master/performAction", {
          action: "CHANGE REQUEST",
          actionBy: actionPerformer,
          remarks: requestChangeComment,
          requestId: record.requestId,
        });
      } else {
        // For regular workflows: Need role selection
        if (!selectedRole) {
          message.warning("Please select a role.");
          return;
        }

        const workflowTransitionId = record.workflowTransitionId;
        if (!workflowTransitionId) {
          message.error("Workflow transition ID not found for this request.");
          return;
        }

        const payload = {
          action: "Change requested",
          actionBy: actionPerformer,
          assignmentRole: selectedRole,
          remarks: requestChangeComment,
          requestId: record.requestId,
          workflowTransitionId: record.workflowTransitionId,
        };

        await axios.post("/performTransitionAction", payload, {
          headers: { "Content-Type": "application/json" },
        });
      }

      message.success("Request change submitted successfully.");
      setRequestChangeComment("");
      setSelectedRole(null);
      setPreviousRoles([]);
      refetchData?.();
    } catch (error) {
      message.error("Failed to submit request change.");
      console.error("Request change error:", error);
    }
  };

  // Fetch workflow details
  const fetchWorkflowDetails = async (record) => {
    if (!record.requestId) {
      message.error("No ID found.");
      return;
    }

    if (record.workflowName === "Material Workflow") {
      try {
        const { data } = await axios.get(
          `/api/material-master-util/${record.requestId}`
        );
        setMaterialDtl(data.responseData);
        setMaterialModalOpen(true);
        return;
      } catch (error) {
        message.error("Failed to fetch material details.");
        console.error("Material fetch error:", error);
        return;
      }
    }

    if (record.workflowName === "Vendor Workflow") {
      try {
        const { data } = await axios.get(
          `/api/vendor-master-util/${record.requestId}`
        );
        setVendorDtl(data.responseData);
        setVendorModalOpen(true);
        return;
      } catch (error) {
        message.error("Failed to fetch vendor details.");
        console.error("Vendor fetch error:", error);
        return;
      }
    }

    if (record.workflowName === "Job Workflow") {
      try {
        const { data } = await axios.get(
          `/api/job-master/${record.requestId}`
        );
        setJobDtl(data.responseData);
        setJobModalOpen(true);
        return;
      } catch (error) {
        message.error("Failed to fetch job details.");
        console.error("Job fetch error:", error);
        return;
      }
    }

    setSelectedRecord(record);
    setDetailLoading(true);

    let endpoint = "";
    const workflowIdNum = parseInt(record.workflowId, 10);

    switch (workflowIdNum) {
      case 1:
        endpoint = `/api/indents/${record.requestId}`;
        break;
      case 2:
        endpoint = `/api/contigency-purchase/${record.requestId}`;
        break;
      case 3:
        endpoint = `/api/purchase-orders/${record.requestId}`;
        break;
      case 4:
        endpoint = `/api/tender-requests/${record.requestId}`;
        break;
      case 5:
        endpoint = `/api/service-orders/${record.requestId}`;
        break;
      case 7:
        endpoint = `/api/tender-requests/${record.requestId}`;
        break;
      case 10:
        endpoint = `/api/process-controller/VoucherData?processNo=${record.requestId}`;
        break;
      default:
        message.error("Invalid workflow ID.");
        setDetailLoading(false);
        return;
    }

    try {
      const response = await axios.get(endpoint);
      setDetailsData(response.data.responseData);
      setQueueData(response.data.responseData);
      setModalVisible(true);
    } catch (err) {
      message.error("Failed to fetch details.");
      console.error("Fetch details error:", err);
    } finally {
      setDetailLoading(false);
    }
  };

  // fetchData removed — Queue1 (parent) owns the API call.
  // Use refetchData() prop to trigger a parent re-fetch after actions.

  // Get common field helper
  const getCommonField = (workflowId, apiData, field) => {
    switch (workflowId) {
      case 1:
        return {
          indentor: apiData.indentorName,
          amount: apiData.amount,
          project: apiData.projectName,
          budgetName: apiData.budgetName,
          indentTitle: apiData.workflowName,
          modeOfProcurement: apiData.modeOfProcurement,
          consignee: apiData.consignee,
        }[field];

      case 2:
        return {
          indentor: apiData.vendorsName
            ? `${apiData.vendorsName} (${apiData.createdBy})`
            : `User ${apiData.createdBy}`,
          amount: apiData.amount,
          project: apiData.projectName,
          budgetName: "-",
          indentTitle: "Contingency Purchase",
          procurementMode: "Direct Purchase",
          consignee: apiData.consignee,
        }[field];

      case 3:
        return {
          indentor: apiData.createdBy,
          amount: apiData.amount,
          project:
            apiData.tenderDetails?.indentResponseDTO?.[0]?.projectName || "N/A",
          budgetName: apiData.budgetCode,
          indentTitle: "Purchase Order",
          modeOfProcurement: apiData.modeOfProcurement,
          procurementMode: apiData.procurementType,
          consignee: apiData.consignee,
        }[field];

      case 4:
        return {
          indentor: apiData.createdBy,
          project: apiData.projectName,
          budgetName: apiData.budgetCode,
          indentTitle: "Tender",
          modeOfProcurement: apiData.modeOfProcurement,
          consignee: apiData.consignee,
          amount: apiData.amount,
        }[field];

      case 5:
        return {
          indentor: apiData.createdBy,
          project: apiData.projectName,
          budgetName: apiData.budgetCode,
          indentTitle: "Service Order",
          procurementMode: apiData.procurementType,
          consignee: apiData.consignee,
        }[field];

      case 7:
        return {
          indentor: apiData.createdBy,
          project: apiData.projectName,
          budgetName: apiData.budgetCode,
          indentTitle: "Tender",
          modeOfProcurement: apiData.modeOfProcurement,
          consignee: apiData.consignee,
          amount: apiData.amount,
        }[field];

      case 9:
        return {
          indentor: apiData.indentorName,
          budgetName: apiData.budgetCode,
          indentTitle: "Material",
          amount: apiData.amount,
        }[field];

      case 10:
        return {
          paymentType: apiData.paymentType,
          poNo: apiData.poNo,
          vendorName: apiData.vendorName,
          amount: apiData.amount,
        }[field];

      default:
        return "-";
    }
  };

  // Payment Voucher columns
  const pvColumns = [
    {
      title: "Request ID",
      dataIndex: "requestId",
      key: "requestId",
      render: (text, record) => (
        <Button type="link" onClick={() => fetchWorkflowDetails(record)}>
          {text}
        </Button>
      ),
      fixed: "left",
    },
    {
      title: "Amount",
      dataIndex: "amount",
      key: "amount",
      render: (_, record) =>
        getCommonField(record.workflowId, record, "amount")
          ? `₹${getCommonField(record.workflowId, record, "amount")}`
          : "-",
    },
    {
      title: "PO No",
      dataIndex: "poNo",
      key: "poNo",
      render: (_, record) =>
        getCommonField(record.workflowId, record, "poNo") || "-",
    },
    {
      title: "Vendor Name",
      dataIndex: "vendorName",
      key: "vendorName",
      render: (_, record) =>
        getCommonField(record.workflowId, record, "vendorName") || "-",
    },
    {
      title: "Invoice Type",
      dataIndex: "paymentType",
      key: "paymentType",
      render: (_, record) =>
        getCommonField(record.workflowId, record, "paymentType") || "-",
    },
    {
      title: "Status",
      dataIndex: "status",
      key: "status",
      render: (status) => (
        <Tag color={status === "Approved" ? "green" : "volcano"}>{status}</Tag>
      ),
    },
    {
      title: "Actions",
      key: "actions",
      fixed: "right",
      render: (_, record) => {
        if (record.status === "Approved") return null;

        return (
          <Space>
            <Button type="link" onClick={() => handleApprove(record)}>
              Approve
            </Button>

            <Popover
              content={
                <div style={{ padding: 12 }}>
                  <Input.TextArea
                    placeholder="Reject Comments"
                    rows={3}
                    value={rejectComment}
                    onChange={(e) => setRejectComment(e.target.value)}
                  />
                  <Button
                    type="primary"
                    onClick={() => handleReject(record)}
                    style={{ marginTop: 8 }}
                  >
                    Submit
                  </Button>
                </div>
              }
              title="Reject"
              trigger="click"
            >
              <Button danger type="link">
                Reject
              </Button>
            </Popover>

            <Popover
              content={
                <div style={{ padding: 12, width: 300 }}>
                  <Select
                    placeholder={
                      loadingPreviousRoles ? "Loading roles..." : "Select a role"
                    }
                    value={selectedRole}
                    onChange={setSelectedRole}
                    style={{ width: "100%", marginBottom: 8 }}
                    loading={loadingPreviousRoles}
                    disabled={loadingPreviousRoles || previousRoles.length === 0}
                  >
                    {previousRoles.map((role) => (
                      <Select.Option key={role} value={role}>
                        {role}
                      </Select.Option>
                    ))}
                  </Select>
                  {previousRoles.length === 0 && !loadingPreviousRoles && (
                    <Text type="secondary">No previous roles available.</Text>
                  )}

                  <Input.TextArea
                    placeholder="Request Change Comments"
                    rows={3}
                    value={requestChangeComment}
                    onChange={(e) => setRequestChangeComment(e.target.value)}
                    style={{ marginTop: 8 }}
                  />
                  <Button
                    type="primary"
                    onClick={() => handleRequestChangeSubmit(record)}
                    style={{ marginTop: 8 }}
                    disabled={
                      !selectedRole ||
                      !requestChangeComment.trim() ||
                      loadingPreviousRoles
                    }
                  >
                    Submit
                  </Button>
                </div>
              }
              title="Request Change"
              trigger="click"
              onOpenChange={(visible) => {
                if (visible) {
                  fetchPreviousRoles(record.workflowId, record.requestId);
                } else {
                  setPreviousRoles([]);
                  setSelectedRole(null);
                  setRequestChangeComment("");
                  setLoadingPreviousRoles(false);
                }
              }}
            >
              <Button type="link">Request Change</Button>
            </Popover>
          </Space>
        );
      },
    },
  ];

  // Main columns
  const columns = [
    {
      title: "Request ID",
      dataIndex: "requestId",
      key: "requestId",
      render: (text, record) => (
        <Button type="link" onClick={() => fetchWorkflowDetails(record)}>
          {text}
        </Button>
      ),
      fixed: "left",
    },
    {
      title: "Indentor",
      dataIndex: "indentor",
      key: "indentor",
      render: (_, record) =>
        getCommonField(record.workflowId, record, "indentor") || "-",
    },
    {
      title: "Amount",
      key: "amount",
      render: (_, record) => {
        const amount = getCommonField(record.workflowId, record, "amount");
        if (!amount) return "-";

        // TC_52: Highlight tenders >10 lakh with enhanced workflow indicator
        const isTender = [4, 7].includes(parseInt(record.workflowId, 10));
        const isHighValue = parseFloat(amount) > 1000000;

        return (
          <span>
            ₹{amount}
            {isTender && isHighValue && (
              <Tag color="purple" style={{marginLeft: 4, fontSize: 10}}>Enhanced</Tag>
            )}
          </span>
        );
      },
    },
    {
      title: "Project",
      dataIndex: "projectName",
      key: "project",
      render: (_, record) =>
        getCommonField(record.workflowId, record, "project") || "-",
    },
    {
      title: "Budget Name",
      dataIndex: "budgetName",
      key: "budgetName",
      render: (_, record) =>
        getCommonField(record.workflowId, record, "budget") || "-",
    },
    {
      title: "Mode of Procurement",
      dataIndex: "modeOfProcurement",
      key: "modeOfProcurement",
      render: (_, record) =>
        getCommonField(record.workflowId, record, "modeOfProcurement") || "-",
    },
    {
      title: "Consignee",
      dataIndex: "consignee",
      key: "consignee",
      render: (_, record) =>
        getCommonField(record.workflowId, record, "consignee") || "-",
    },
    {
      title: "Assigned To",
      dataIndex: "assignedToEmployeeName",
      key: "assignedTo",
      render: (text) => text || "-",
    },
    {
      title: "Status",
      dataIndex: "status",
      key: "status",
      render: (status, record) => {
        let finalStatus = status;

        if (auth.role === "Purchase Head") {
          finalStatus =
            record.action === "Indentor Cancelled"
              ? "Indentor Cancelled"
              : "Completed";
        }

        return (
          <Tag
            color={
              finalStatus === "Approved" || finalStatus === "Completed"
                ? "green"
                : "volcano"
            }
          >
            {finalStatus}
          </Tag>
        );
      },
    },
    ...(auth.role === "Purchase Head"
      ? [
          {
            title: "Actions",
            key: "actions",
            fixed: "right",
            render: (_, record) => {
              if (record.action === "Indentor Cancelled") {
                return (
                  <Popover
                    content={
                      <div style={{ padding: 12 }}>
                        <Input.TextArea
                          placeholder="Reject Comments"
                          rows={3}
                          value={rejectComment}
                          onChange={(e) => setRejectComment(e.target.value)}
                        />
                        <Button
                          type="primary"
                          onClick={() => handleReject(record)}
                          style={{ marginTop: 8 }}
                        >
                          Submit
                        </Button>
                      </div>
                    }
                    title="Reject"
                    trigger="click"
                  >
                    <Button danger type="link">
                      Cancel
                    </Button>
                  </Popover>
                );
              }

              return (
                <Popover
                  content={
                    <div style={{ padding: 12, width: 300 }}>
                      <Select
                        placeholder={
                          loadingEmployees
                            ? "Loading employees..."
                            : "Select an employee"
                        }
                        value={selectedEmployee}
                        onChange={(value) => setSelectedEmployee(value)}
                        style={{ width: "100%", marginBottom: 8 }}
                        loading={loadingEmployees}
                        onFocus={fetchEmployees}
                      >
                        {employees.map((emp) => (
                          <Option key={emp.employeeId} value={emp.employeeId}>
                            {emp.employeeName}
                          </Option>
                        ))}
                      </Select>
                      <Button
                        type="primary"
                        onClick={() => handleAssign(record.requestId)}
                        style={{ marginTop: 8 }}
                        disabled={!selectedEmployee}
                      >
                        Assign
                      </Button>
                    </div>
                  }
                  title="Assign Indent"
                  trigger="click"
                  onOpenChange={(visible) => {
                    if (!visible) setSelectedEmployee(null);
                  }}
                >
                  <Button type="link">Assign Indent</Button>
                </Popover>
              );
            },
          },
        ]
      : [
          {
            title: "Actions",
            key: "actions",
            fixed: "right",
            render: (_, record) => {
              // For user ID 18, only show edit button for materials
              if (userId === 18 && record.workflowName === "Material Workflow") {
                return (
                  <Button
                    type="primary"
                    onClick={() =>
                      navigate("/masters", {
                        state: { materialCode: record.requestId, master: "Material" },
                      })
                    }
                  >
                    Edit
                  </Button>
                );
              }

              // For Indent Creator role - Material Workflow
              if (
                auth.role === "Indent Creator" &&
                record.workflowName === "Material Workflow"
              ) {
                if (record.status === "Approved") return null;
                return (
                  <Space>
                    <Button
                      type="primary"
                      onClick={() =>
                        navigate("/masters", {
                          state: {
                            materialCode: record.requestId,
                            master: "Material",
                          },
                        })
                      }
                    >
                      Edit
                    </Button>
                    <Button type="link" onClick={() => handleApprove(record)}>
                      Approve
                    </Button>
                  </Space>
                );
              }

              // For Indent Creator role - Job Workflow
              if (
                auth.role === "Indent Creator" &&
                record.workflowName === "Job Workflow"
              ) {
                if (record.status === "Approved") return null;
                return (
                  <Space>
                    <Button
                      type="primary"
                      onClick={() =>
                        navigate("/masters", {
                          state: {
                            jobCode: record.requestId,
                            master: "Job",
                          },
                        })
                      }
                    >
                      Edit
                    </Button>
                    <Button type="link" onClick={() => handleApprove(record)}>
                      Approve
                    </Button>
                  </Space>
                );
              }

              // For Indent Creator role - other workflows (indents, etc.)
              if (
                auth.role === "Indent Creator" &&
                record.workflowName !== "Material Workflow" &&
                record.workflowName !== "Job Workflow"
              ) {
                if (record.status === "Approved") return null;

                return (
                  <Space>
                    <Button
                      type="primary"
                      onClick={() =>
                        navigate("/procurement/indent/creation", {
                          state: { indentId: record.requestId },
                        })
                      }
                    >
                      Edit
                    </Button>
                    <Button type="link" onClick={() => handleApprove(record)}>
                      Approve
                    </Button>
                  </Space>
                );
              }

              // For Tender Creator role
              if (auth.role === "Tender Creator") {
                if (record.status === "Approved") return null;

                return (
                  <Space>
                    <Button
                      type="primary"
                      onClick={() =>
                        navigate("/procurement/tender/request", {
                          state: { tenderId: record.requestId },
                        })
                      }
                    >
                      Edit
                    </Button>
                    <Button type="link" onClick={() => handleApprove(record)}>
                      Approve
                    </Button>
                  </Space>
                );
              }

              // For PO Creator role
              if (auth.role === "PO Creator") {
                if (record.status === "Approved") return null;

                return (
                  <Space>
                    <Button
                      type="primary"
                      onClick={() =>
                        navigate("/procurement/purchaseOrder", {
                          state: { poId: record.requestId },
                        })
                      }
                    >
                      Edit
                    </Button>
                    <Button type="link" onClick={() => handleApprove(record)}>
                      Approve
                    </Button>
                  </Space>
                );
              }

              // For Store Purchase Officer (or userId === 29) — full action set
              if (auth.role === "Store Purchase Officer" || userId === 29) {
                if (record.status === "Approved") return null;

                return (
                  <Space>
                    {/* Edit button for materials only */}
                    {record.workflowName === "Material Workflow" && (
                      <Button
                        type="primary"
                        onClick={() =>
                          navigate("/masters", {
                            state: {
                              materialCode: record.requestId,
                              master: "Material",
                            },
                          })
                        }
                      >
                        Edit
                      </Button>
                    )}

                    {/* Approve Button - for all workflows */}
                    <Button type="link" onClick={() => handleApprove(record)}>
                      Approve
                    </Button>

                    {/* Reject Button - terminal rejection for all workflows */}
                    <Popover
                      content={
                        <div style={{ padding: 12 }}>
                          <Input.TextArea
                            placeholder="Reject Comments"
                            rows={3}
                            value={rejectComment}
                            onChange={(e) => setRejectComment(e.target.value)}
                          />
                          <Button
                            type="primary"
                            onClick={() => handleReject(record)}
                            style={{ marginTop: 8 }}
                          >
                            Submit
                          </Button>
                        </div>
                      }
                      title="Reject"
                      trigger="click"
                    >
                      <Button danger type="link">
                        Reject
                      </Button>
                    </Popover>

                    {/* Request Change for VENDOR Workflow - No role dropdown, just comments */}
                    {record.workflowName === "Vendor Workflow" && (
                      <Popover
                        content={
                          <div style={{ padding: 12, width: 300 }}>
                            <Input.TextArea
                              placeholder="Request Change Comments"
                              rows={3}
                              value={requestChangeComment}
                              onChange={(e) =>
                                setRequestChangeComment(e.target.value)
                              }
                            />
                            <Button
                              type="primary"
                              onClick={() => handleRequestChangeSubmit(record)}
                              style={{ marginTop: 8 }}
                              disabled={!requestChangeComment.trim()}
                            >
                              Submit
                            </Button>
                          </div>
                        }
                        title="Request Change"
                        trigger="click"
                        onOpenChange={(visible) => {
                          if (!visible) {
                            setRequestChangeComment("");
                          }
                        }}
                      >
                        {/* <Button type="link">Request Change</Button> */}
                      </Popover>
                    )}

                    {/* Request Change for MATERIAL Workflow - Hardcoded "Indent Creator" */}
                    {record.workflowName === "Material Workflow" && (
                      <Popover
                        content={
                          <div style={{ padding: 12, width: 300 }}>
                            <Select
                              placeholder="Select a role"
                              value={selectedRole}
                              onChange={setSelectedRole}
                              style={{ width: "100%", marginBottom: 8 }}
                            >
                              {previousRoles.map((role) => (
                                <Select.Option key={role} value={role}>
                                  {role}
                                </Select.Option>
                              ))}
                            </Select>

                            {previousRoles.length === 0 && (
                              <Text type="secondary">
                                No previous roles available.
                              </Text>
                            )}

                            <Input.TextArea
                              placeholder="Request Change Comments"
                              rows={3}
                              value={requestChangeComment}
                              onChange={(e) =>
                                setRequestChangeComment(e.target.value)
                              }
                              style={{ marginTop: 8 }}
                            />

                            <Button
                              type="primary"
                              onClick={() => handleRequestChangeSubmit(record)}
                              style={{ marginTop: 8 }}
                              disabled={
                                !selectedRole || !requestChangeComment.trim()
                              }
                            >
                              Submit
                            </Button>
                          </div>
                        }
                        title="Request Change"
                        trigger="click"
                        onOpenChange={(visible) => {
                          if (visible) {
                            setPreviousRoles(["Indent Creator"]);
                            setSelectedRole("Indent Creator");
                            setLoadingPreviousRoles(false);
                          } else {
                            setPreviousRoles([]);
                            setSelectedRole(null);
                            setRequestChangeComment("");
                            setLoadingPreviousRoles(false);
                          }
                        }}
                      >
                        <Button type="link">Request Change</Button>
                      </Popover>
                    )}

                    {/* Request Change for JOB Workflow - Hardcoded "Indent Creator" */}
                    {record.workflowName === "Job Workflow" && (
                      <Popover
                        content={
                          <div style={{ padding: 12, width: 300 }}>
                            <Select
                              placeholder="Select a role"
                              value={selectedRole}
                              onChange={setSelectedRole}
                              style={{ width: "100%", marginBottom: 8 }}
                            >
                              {previousRoles.map((role) => (
                                <Select.Option key={role} value={role}>
                                  {role}
                                </Select.Option>
                              ))}
                            </Select>

                            {previousRoles.length === 0 && (
                              <Text type="secondary">
                                No previous roles available.
                              </Text>
                            )}

                            <Input.TextArea
                              placeholder="Request Change Comments"
                              rows={3}
                              value={requestChangeComment}
                              onChange={(e) =>
                                setRequestChangeComment(e.target.value)
                              }
                              style={{ marginTop: 8 }}
                            />

                            <Button
                              type="primary"
                              onClick={() => handleRequestChangeSubmit(record)}
                              style={{ marginTop: 8 }}
                              disabled={
                                !selectedRole || !requestChangeComment.trim()
                              }
                            >
                              Submit
                            </Button>
                          </div>
                        }
                        title="Request Change"
                        trigger="click"
                        onOpenChange={(visible) => {
                          if (visible) {
                            setPreviousRoles(["Indent Creator"]);
                            setSelectedRole("Indent Creator");
                            setLoadingPreviousRoles(false);
                          } else {
                            setPreviousRoles([]);
                            setSelectedRole(null);
                            setRequestChangeComment("");
                            setLoadingPreviousRoles(false);
                          }
                        }}
                      >
                        <Button type="link">Seek Clarification</Button>
                      </Popover>
                    )}

                    {/* Request Change for REGULAR Workflows - Fetch roles from API */}
                    {record.workflowName !== "Vendor Workflow" &&
                      record.workflowName !== "Material Workflow" &&
                      record.workflowName !== "Job Workflow" && (
                        <Popover
                          content={
                            <div style={{ padding: 12, width: 300 }}>
                              <Select
                                placeholder={
                                  loadingPreviousRoles
                                    ? "Loading roles..."
                                    : "Select a role"
                                }
                                value={selectedRole}
                                onChange={setSelectedRole}
                                style={{ width: "100%", marginBottom: 8 }}
                                loading={loadingPreviousRoles}
                                disabled={
                                  loadingPreviousRoles ||
                                  previousRoles.length === 0
                                }
                              >
                                {previousRoles.map((role) => (
                                  <Select.Option key={role} value={role}>
                                    {role}
                                  </Select.Option>
                                ))}
                              </Select>

                              {previousRoles.length === 0 &&
                                !loadingPreviousRoles && (
                                  <Text type="secondary">
                                    No previous roles available.
                                  </Text>
                                )}

                              <Input.TextArea
                                placeholder="Request Change Comments"
                                rows={3}
                                value={requestChangeComment}
                                onChange={(e) =>
                                  setRequestChangeComment(e.target.value)
                                }
                                style={{ marginTop: 8 }}
                              />

                              <Button
                                type="primary"
                                onClick={() => handleRequestChangeSubmit(record)}
                                style={{ marginTop: 8 }}
                                disabled={
                                  !selectedRole ||
                                  !requestChangeComment.trim() ||
                                  loadingPreviousRoles
                                }
                              >
                                Submit
                              </Button>
                            </div>
                          }
                          title="Request Change"
                          trigger="click"
                          onOpenChange={(visible) => {
                            if (visible) {
                              if (record.workflowId && record.workflowId !== null) {
                                fetchPreviousRoles(record.workflowId, record.requestId);
                              } else {
                                message.error("Cannot load previous roles");
                                setPreviousRoles([]);
                                setLoadingPreviousRoles(false);
                              }
                            } else {
                              setPreviousRoles([]);
                              setSelectedRole(null);
                              setRequestChangeComment("");
                              setLoadingPreviousRoles(false);
                            }
                          }}
                        >
                          <Button type="link">Request Change</Button>
                        </Popover>
                      )}
                  </Space>
                );
              }

              // For all other users — default action set
              if (record.status === "Approved") return null;

              return (
                <Space>
                  <Button type="link" onClick={() => handleApprove(record)}>
                    Approve
                  </Button>

                  {/* Reject Button - terminal rejection */}
                  <Popover
                    content={
                      <div style={{ padding: 12 }}>
                        <Input.TextArea
                          placeholder="Reject Comments"
                          rows={3}
                          value={rejectComment}
                          onChange={(e) => setRejectComment(e.target.value)}
                        />
                        <Button
                          type="primary"
                          onClick={() => handleReject(record)}
                          style={{ marginTop: 8 }}
                        >
                          Submit
                        </Button>
                      </div>
                    }
                    title="Reject"
                    trigger="click"
                  >
                    <Button danger type="link">
                      Reject
                    </Button>
                  </Popover>

                  {/* Request Change for Material Workflow - Hardcoded Role */}
                  {record.workflowName === "Material Workflow" && (
                    <Popover
                      content={
                        <div style={{ padding: 12, width: 300 }}>
                          <Select
                            placeholder="Select a role"
                            value={selectedRole}
                            onChange={setSelectedRole}
                            style={{ width: "100%", marginBottom: 8 }}
                          >
                            {previousRoles.map((role) => (
                              <Select.Option key={role} value={role}>
                                {role}
                              </Select.Option>
                            ))}
                          </Select>

                          {previousRoles.length === 0 && (
                            <Text type="secondary">
                              No previous roles available.
                            </Text>
                          )}

                          <Input.TextArea
                            placeholder="Request Change Comments"
                            rows={3}
                            value={requestChangeComment}
                            onChange={(e) =>
                              setRequestChangeComment(e.target.value)
                            }
                            style={{ marginTop: 8 }}
                          />

                          <Button
                            type="primary"
                            onClick={() => handleRequestChangeSubmit(record)}
                            style={{ marginTop: 8 }}
                            disabled={
                              !selectedRole || !requestChangeComment.trim()
                            }
                          >
                            Submit
                          </Button>
                        </div>
                      }
                      title="Request Change"
                      trigger="click"
                      onOpenChange={(visible) => {
                        if (visible) {
                          setPreviousRoles(["Indent Creator"]);
                          setSelectedRole("Indent Creator");
                          setLoadingPreviousRoles(false);
                        } else {
                          setPreviousRoles([]);
                          setSelectedRole(null);
                          setRequestChangeComment("");
                          setLoadingPreviousRoles(false);
                        }
                      }}
                    >
                      <Button type="link">Request Change</Button>
                    </Popover>
                  )}

                  {/* Request Change for JOB Workflow - Hardcoded "Indent Creator" */}
                  {record.workflowName === "Job Workflow" && (
                    <Popover
                      content={
                        <div style={{ padding: 12, width: 300 }}>
                          <Select
                            placeholder="Select a role"
                            value={selectedRole}
                            onChange={setSelectedRole}
                            style={{ width: "100%", marginBottom: 8 }}
                          >
                            {previousRoles.map((role) => (
                              <Select.Option key={role} value={role}>
                                {role}
                              </Select.Option>
                            ))}
                          </Select>

                          {previousRoles.length === 0 && (
                            <Text type="secondary">
                              No previous roles available.
                            </Text>
                          )}

                          <Input.TextArea
                            placeholder="Request Change Comments"
                            rows={3}
                            value={requestChangeComment}
                            onChange={(e) =>
                              setRequestChangeComment(e.target.value)
                            }
                            style={{ marginTop: 8 }}
                          />

                          <Button
                            type="primary"
                            onClick={() => handleRequestChangeSubmit(record)}
                            style={{ marginTop: 8 }}
                            disabled={
                              !selectedRole || !requestChangeComment.trim()
                            }
                          >
                            Submit
                          </Button>
                        </div>
                      }
                      title="Request Change"
                      trigger="click"
                      onOpenChange={(visible) => {
                        if (visible) {
                          setPreviousRoles(["Indent Creator"]);
                          setSelectedRole("Indent Creator");
                          setLoadingPreviousRoles(false);
                        } else {
                          setPreviousRoles([]);
                          setSelectedRole(null);
                          setRequestChangeComment("");
                          setLoadingPreviousRoles(false);
                        }
                      }}
                    >
                      <Button type="link">Request Change</Button>
                    </Popover>
                  )}

                  {/* Request Change for Regular Workflows */}
                  {record.workflowName !== "Vendor Workflow" &&
                    record.workflowName !== "Material Workflow" &&
                    record.workflowName !== "Job Workflow" && (
                      <Popover
                        content={
                          <div style={{ padding: 12, width: 300 }}>
                            <Select
                              placeholder={
                                loadingPreviousRoles
                                  ? "Loading roles..."
                                  : "Select a role"
                              }
                              value={selectedRole}
                              onChange={setSelectedRole}
                              style={{ width: "100%", marginBottom: 8 }}
                              loading={loadingPreviousRoles}
                              disabled={
                                loadingPreviousRoles ||
                                previousRoles.length === 0
                              }
                            >
                              {previousRoles.map((role) => (
                                <Select.Option key={role} value={role}>
                                  {role}
                                </Select.Option>
                              ))}
                            </Select>

                            {previousRoles.length === 0 &&
                              !loadingPreviousRoles && (
                                <Text type="secondary">
                                  No previous roles available.
                                </Text>
                              )}

                            <Input.TextArea
                              placeholder="Request Change Comments"
                              rows={3}
                              value={requestChangeComment}
                              onChange={(e) =>
                                setRequestChangeComment(e.target.value)
                              }
                              style={{ marginTop: 8 }}
                            />

                            <Button
                              type="primary"
                              onClick={() => handleRequestChangeSubmit(record)}
                              style={{ marginTop: 8 }}
                              disabled={
                                !selectedRole ||
                                !requestChangeComment.trim() ||
                                loadingPreviousRoles
                              }
                            >
                              Submit
                            </Button>
                          </div>
                        }
                        title="Request Change"
                        trigger="click"
                        onOpenChange={(visible) => {
                          if (visible) {
                            if (record.workflowId && record.workflowId !== null) {
                              fetchPreviousRoles(
                                record.workflowId,
                                record.requestId
                              );
                            } else {
                              message.error("Cannot load previous roles");
                              setPreviousRoles([]);
                              setLoadingPreviousRoles(false);
                            }
                          } else {
                            setPreviousRoles([]);
                            setSelectedRole(null);
                            setRequestChangeComment("");
                            setLoadingPreviousRoles(false);
                          }
                        }}
                      >
                        <Button type="link">Seek Clarification</Button>
                      </Popover>
                    )}
                </Space>
              );
            },
          },
        ]),
  ];

  const columnsToRender = requestType === "PV" ? pvColumns : columns;

  const filteredData = data.filter((item) =>
    item.requestId.toString().toLowerCase().includes(searchTerm.toLowerCase())
  );

  const handleSearch = useCallback((value) => {
    setSearchTerm(value);
  }, []);

  const handleReset = useCallback(() => {
    setSearchTerm("");
  }, []);

  return (
    <div style={{ padding: 24 }}>
      <FilterComponent
        onSearch={handleSearch}
        searchTerm={searchTerm}
        onReset={handleReset}
      />
      <Space style={{ marginBottom: 16 }}>
        {auth.role !== "Purchase Head" && (
          <Button
            type="primary"
            onClick={handleApproveAll}
            disabled={selectedRows.length === 0}
          >
            Approve All
          </Button>
        )}
        {auth.role === "Purchase Head" && (
          <Button
            type="primary"
            disabled={selectedRows.length === 0}
            onClick={() => {
              const selectedProjectNames = [
                ...new Set(selectedRows.map((row) => row.projectName)),
              ];

              if (selectedProjectNames.length > 1) {
                message.error("Selected indents belong to different projects");
                return;
              }

              navigate("/procurement/tender/request", {
                state: {
                  indentIds: selectedRows.map((row) => row.requestId),
                },
              });
            }}
          >
            Multiple Indent Ids Tender Creation
          </Button>
        )}
        {Object.entries(workflowCounts).map(([id, count]) => (
          <Tag key={id} color="blue">
          {id.replace(" Workflow", "").toUpperCase()} ({count})
            Pending RequestIds Count: {count}
          </Tag>
        ))}
      </Space>

      {loading ? (
        <Spin size="large" tip="Loading..." style={{ marginTop: 24 }} />
      ) : error ? (
        <Text type="danger">{error}</Text>
      ) : (
        <Table
          rowSelection={rowSelection}
          rowKey="key"
          columns={columnsToRender}
          dataSource={filteredData}
        />
      )}

      <QueueModal
        modalVisible={modalVisible}
        setModalVisible={setModalVisible}
        selectedRecord={selectedRecord}
        detailsData={detailsData}
        historyVisible={historyVisible}
        setHistoryVisible={setHistoryVisible}
        materialHistoryVisible={materialHistoryVisible}
        setMaterialHistoryVisible={setMaterialHistoryVisible}
        selectedMaterialCode={selectedMaterialCode}
        setSelectedMaterialCode={setSelectedMaterialCode}
      />
      <MaterialDetailModal
        visible={materialModalOpen}
        setVisible={setMaterialModalOpen}
        materialData={materialDtl}
      />
      <VendorDetailModal
        visible={vendorModalOpen}
        setVisible={setVendorModalOpen}
        vendorData={vendorDtl}
      />
      <JobDetailModal
        visible={jobModalOpen}
        setVisible={setJobModalOpen}
        jobData={jobDtl}
      />
    </div>
  );
};

export default QueueRequest;
