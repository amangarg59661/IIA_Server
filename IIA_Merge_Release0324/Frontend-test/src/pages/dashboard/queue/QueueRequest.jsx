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
import { SearchOutlined, HistoryOutlined, TeamOutlined } from "@ant-design/icons";
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
      <Descriptions bordered column={1}>
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
// My Assignments modal
const [assignmentModalOpen, setAssignmentModalOpen] = useState(false);
const [assignedIndents, setAssignedIndents] = useState([]);
const [assignmentLoading, setAssignmentLoading] = useState(false);
const [assignmentSearch, setAssignmentSearch] = useState("");
const [reassignTarget, setReassignTarget] = useState(null); // { indentId, assignedToEmployeeName }
const [reassignEmployee, setReassignEmployee] = useState(null);
const [reassignLoading, setReassignLoading] = useState(false);
  // Version History state
  const [versionHistoryOpen, setVersionHistoryOpen] = useState(false);
  const [versionHistoryList, setVersionHistoryList] = useState([]);
  const [selectedVersionIdx, setSelectedVersionIdx] = useState(0);
  const [versionHistoryLoading, setVersionHistoryLoading] = useState(false);
  // Reliable workflow type for the modal to use -- do NOT rely on `workflowId`
  // being present inside the version-history API response itself (it isn't,
  // at least for the indent endpoint), always set this from the row record.
  const [versionHistoryWorkflowId, setVersionHistoryWorkflowId] = useState(null);

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
const fetchMyAssignments = async () => {
  setAssignmentLoading(true);
  try {
    const res = await axios.get("/api/indents/my-assignments");
    setAssignedIndents(res.data.responseData || []);
  } catch {
    message.error("Failed to load assignments.");
  } finally {
    setAssignmentLoading(false);
  }
};
const handleReassign = async () => {
  if (!reassignEmployee) {
    message.warning("Please select an employee.");
    return;
  }
  const emp = employees.find((e) => e.employeeId === reassignEmployee);
  setReassignLoading(true);
  try {
    await axios.post("/api/indents/assign-employee", {
      indentId: reassignTarget.indentId,
      employeeId: reassignEmployee,
      employeeName: emp ? emp.employeeName : "",
      assignedByUserId: userId,
    });
    message.success("Reassigned successfully.");
    setReassignTarget(null);
    setReassignEmployee(null);
    fetchMyAssignments(); // refresh the list
  } catch {
    message.error("Reassignment failed.");
  } finally {
    setReassignLoading(false);
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

  // Fetch version history based on workflow type
  const fetchVersionHistory = async (record) => {
    if (!record?.requestId) {
      message.error("No Request ID found.");
      return;
    }

    setVersionHistoryLoading(true);
    const workflowIdNum = parseInt(record.workflowId, 10);
    setVersionHistoryWorkflowId(workflowIdNum);

    let endpoint = null;
    let paramKey = null;

    // Map workflow to the correct version-history API
    switch (workflowIdNum) {
      case 1: // Indent
        endpoint = `/api/indents/version-history`;
        paramKey = "indentId";
        break;
      case 4: // Tender
      case 7: // Tender (enhanced)
        endpoint = `/api/tender-requests/version-history`;
        paramKey = "tenderId";
        break;
      case 3: // Purchase Order
        endpoint = `/api/purchase-orders/version-history`;
        paramKey = "poId";
        break;
      case 5: // Service Order
        endpoint = `/api/service-orders/version-history`;
        paramKey = "soId";
        break;
      case 2: // Contingency Purchase
        endpoint = `/api/contigency-purchase/version-history`;
        paramKey = "cpId";
        break;
      default:
        message.warning("Version history is not available for this workflow type.");
        setVersionHistoryLoading(false);
        return;
    }

    try {
      const { data } = await axios.get(endpoint, {
        params: { [paramKey]: record.requestId },
      });
      const list = data?.responseData || [];
      if (list.length === 0) {
        message.info("No version history found for this request.");
        setVersionHistoryLoading(false);
        return;
      }
      setVersionHistoryList(list);
      setSelectedVersionIdx(list.length - 1); // default to latest
      setVersionHistoryOpen(true);
    } catch (error) {
      message.error("Could not load version history.");
      console.error("Version history fetch error:", error);
    } finally {
      setVersionHistoryLoading(false);
    }
  };

  // Fetch workflow transition history
  const fetchWorkflowTransitionHistory = async (requestId) => {
    try {
      const response = await axios.get(
        `/workflowTransitionHistory?requestId=${requestId}`
      );
      if (!response.data.responseData?.[0]?.remarks) {
        console.warn("No remarks found in transition history");
      }
      return response.data.responseData;
    } catch (error) {
      console.error("Error fetching workflow transition history:", error);
      return null;
    }
  };

  // Handle reject
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
        const history = await fetchWorkflowTransitionHistory(record.requestId);

        if (!history || history.length === 0) {
          message.error("No transition history found for this request.");
          return;
        }

        const previousApprovals = history.filter(
          (entry) => entry.action === "APPROVED"
        );
        // if (previousApprovals.length === 0) {
        //   message.error("No previous approval found to revert to.");
        //   return;
        // }

        // const lastApproval = previousApprovals[previousApprovals.length - 1];
        const currentTransition = history[0];

        const payload = {
          action: "REJECTED",
          actionBy: actionPerformer,
          // assignmentRole: lastApproval.assignmentRole,
          remarks: rejectComment,
          requestId: record.requestId,
          workflowTransitionId: currentTransition.workflowTransitionId,
        };

        await axios.post("/performTransitionAction", payload, {
          headers: { "Content-Type": "application/json" },
        });
      }

      message.success(`Request ${record.requestId} rejected and out of queue`);
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

  // Handle cancellation request approve/reject (Purchase Head only)
const handleCancellationApprove = async (record, approvalStatus) => {
  if (approvalStatus === 'REJECTED' && !rejectComment.trim()) {
    message.warning('Please enter a reject comment.');
    return;
  }

  try {
    await axios.post('/api/indents/cancellation/approve', {
      requestId: record.cancellationRequestId,
      approvalStatus,
      approvedBy: auth.userId,
      approvedByName: auth.name,
      approvalRemarks: approvalStatus === 'APPROVED' ? 'Approved' : rejectComment,
    });

    message.success(
      approvalStatus === 'APPROVED'
        ? `Indent ${record.requestId} cancellation approved.`
        : `Cancellation request for ${record.requestId} rejected.`
    );
    setRejectComment('');
    refetchData?.();
  } catch (err) {
    // Backend sends "Cancel the Tender first" — surface it directly
    const errMsg =
      err?.response?.data?.responseStatus?.message ||
      err?.response?.data?.message ||
      'Action failed.';
    message.error(errMsg);
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
    let config = {};
    const workflowIdNum = parseInt(record.workflowId, 10);

    switch (workflowIdNum) {
      case 1:
       endpoint = `/api/indents/byId`;
    config = { params: { indentId: record.requestId } };
    break;
      case 2:
        endpoint = `/api/contigency-purchase/${record.requestId}`;
        break;
      case 3:
        endpoint = `/api/purchase-orders/byId`;
        config = { params: { poId: record.requestId } };
        break;
      case 4:
         endpoint = `/api/tender-requests/byId`;
        config = { params: { tenderId: record.requestId } };
        break;
      case 5:
        endpoint = `/api/service-orders/byId`;
        config = { params: { soId: record.requestId } };
        break;
      case 7:
        endpoint = `/api/tender-requests/byId`;
        config = { params: { tenderId: record.requestId } };
        break;
      case 10:
        // endpoint = `/api/process-controller/VoucherData?processNo=${record.requestId}`;
        endpoint = `/api/process-controller/VoucherData`;
    config = { params: { processNo: record.requestId } };
        break;
      default:
        message.error("Invalid workflow ID.");
        setDetailLoading(false);
        return;
    }

    try {
      const response = await axios.get(endpoint,config);
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
          indentor: apiData.createdBy,
          amount: apiData.amount,
          project: apiData.projectName,
          budgetName: "-",
          indentTitle: "Contingency Purchase",
          procurementMode: "Direct Purchase",
          indentor: apiData.vendorsName
            ? `${apiData.vendorsName} (${apiData.createdBy})`
            : `User ${apiData.createdBy}`,
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

        case 11:
        return {
          indentor: apiData.indentorName,
          budgetName: apiData.budgetCode,
          indentTitle: "JOB",
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
        <div style={{ display: "flex", flexDirection: "column", alignItems: "flex-start", gap: 2 }}>
          <Button type="link" style={{ padding: 0, height: "auto" }} onClick={() => fetchWorkflowDetails(record)}>
            {text}
          </Button>
          {(record.action === "Change requested" ||
  record.status === "CHANGE_REQUEST" ||
  record.approvalStatus === "CHANGE_REQUEST" ||
  String(record.status).toUpperCase() === "CHANGE_REQUEST") && (
            <Tag color="orange" style={{ fontSize: 11, lineHeight: "16px", margin: 0 }}>
              Clarification Requested
            </Tag>
          )}
        </div>
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
const WORKFLOW_IDS_WITH_Mat = [9, 11];
const WORKFLOW_IDS_WITHOUT_Mat = [1,2,3,4,5,6,7,8,10];
  // Main columns
  const columns = [
    {
      title: "Request ID",
      dataIndex: "requestId",
      key: "requestId",
      render: (text, record) => (
        <div style={{ display: "flex", flexDirection: "column", alignItems: "flex-start", gap: 2 }}>
          <Button type="link" style={{ padding: 0, height: "auto" }} onClick={() => fetchWorkflowDetails(record)}>
            {text}
          </Button>
          {(record.action === "Change requested" ||
  record.status === "CHANGE_REQUEST" ||
  record.approvalStatus === "CHANGE_REQUEST" ||
  String(record.status).toUpperCase() === "CHANGE_REQUEST") && (
            <Tag color="orange" style={{ fontSize: 11, lineHeight: "16px", margin: 0 }}>
              Clarification Requested
            </Tag>
          )}
        </div>
      ),
      fixed: "left",
    },
    ...(WORKFLOW_IDS_WITH_Mat.includes(Number(workflowId)) ? [
    {
        title: 'Description',
        dataIndex: 'materialDesc',
        key: "materialDesc",
        render: (_,record) => 
            record.materialDesc || "-",
    }] : []),
    {
      title: "Created by",
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
              <Tag color="purple" style={{marginLeft: 4, fontSize: 10}}>High    </Tag>
            )}
          </span>
        );
      },
    },
    ...(WORKFLOW_IDS_WITHOUT_Mat.includes(Number(workflowId)) ? [
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
        getCommonField(record.workflowId, record, "budgetName") || "-",
    },
    // {
    //   title: "Indentor Title",
    //   dataIndex: "workflowName",
    //   key: "indentTitle",
    //   render: (_, record) =>
    //     getCommonField(record.workflowId, record, "indentTitle") || "-",
    // },
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
      render: (_,record) => record.nextRole || "-",
    }] :[]),
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
                <Space>
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

                {/* View Version History for Purchase Head */}
                {[1, 2, 3, 4, 5, 7].includes(parseInt(record.workflowId, 10)) && (
                  <Button
                    type="link"
                    icon={<HistoryOutlined />}
                    loading={versionHistoryLoading}
                    onClick={() => fetchVersionHistory(record)}
                  >
                    View Version History
                  </Button>
                )}
              </Space>
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
                    {/* <Button type="link" onClick={() => handleApprove(record)}>
                      Approve
                    </Button> */}
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

              // For user ID 29, show all options including Request Change for ALL workflows
            //  if (userId === 61) {
            //  if (userId === 29 ) {
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

                    {/* Reject Button - for all workflows */}
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
                            // Hardcode "Indent Creator" for Material workflow
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
                            // Hardcode "Indent Creator" for Job workflow
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
                              if (
                                record.workflowId &&
                                record.workflowId !== null
                              ) {
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
                          <Button type="link">Request Change</Button>
                        </Popover>
                      )}

                  {/* View Version History - for Store Purchase Officer */}
                  {[1, 2, 3, 4, 5, 7].includes(parseInt(record.workflowId, 10)) && (
                    <Button
                      type="link"
                      icon={<HistoryOutlined />}
                      loading={versionHistoryLoading}
                      onClick={() => fetchVersionHistory(record)}
                    >
                      View Version History
                    </Button>
                  )}
                  </Space>
                );
              }

              // For other users, show default options
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

                  {/* View Version History - available for Indent, Tender, PO, SO workflows */}
                  {[1, 2, 3, 4, 5, 7].includes(parseInt(record.workflowId, 10)) && (
                    <Button
                      type="link"
                      icon={<HistoryOutlined />}
                      loading={versionHistoryLoading}
                      onClick={() => fetchVersionHistory(record)}
                    >
                      View Version History
                    </Button>
                  )}
                </Space>
              );
            },
          },
        ]),
  ];

  const cancellationColumns = [
  {
    title: 'Indent ID',
    dataIndex: 'requestId',
    key: 'requestId',
    fixed: 'left',
  },
  {
    title: 'Requested By',
    dataIndex: 'requestedByName',
    key: 'requestedByName',
  },
  {
    title: 'Reason for Cancellation',
    dataIndex: 'cancellationReason',
    key: 'cancellationReason',
  },
  {
    title: 'Requested On',
    dataIndex: 'createdDate',
    key: 'createdDate',
    render: (val) => val ? new Date(val).toLocaleDateString('en-IN') : '-',
  },
  {
    title: 'Actions',
    key: 'actions',
    fixed: 'right',
    render: (_, record) => (
      <Space>
        <Button
          type="primary"
          size="small"
          onClick={() => handleCancellationApprove(record, 'APPROVED')}
        >
          Approve
        </Button>
        <Popover
          content={
            <div style={{ padding: 12 }}>
              <Input.TextArea
                placeholder="Reject reason"
                rows={3}
                value={rejectComment}
                onChange={(e) => setRejectComment(e.target.value)}
              />
              <Button
                type="primary"
                danger
                style={{ marginTop: 8 }}
                onClick={() => handleCancellationApprove(record, 'REJECTED')}
              >
                Submit
              </Button>
            </div>
          }
          title="Reject Cancellation Request"
          trigger="click"
        >
          <Button danger size="small">Reject</Button>
        </Popover>
      </Space>
    ),
  },
];
const columnsToRender =
  requestType === 'CR' ? cancellationColumns :
  requestType === 'PV' ? pvColumns :
  columns;
//   const columnsToRender = requestType === "PV" ? pvColumns : columns;

  const filteredData = data.filter((item) =>
    item.requestId?.toString().toLowerCase().includes(searchTerm.toLowerCase())
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
        {auth.role !== "Purchase Head" && requestType !== 'CR' && (
          <Button
            type="primary"
            onClick={handleApproveAll}
            disabled={selectedRows.length === 0}
          >
            Approve All
          </Button>
        )}
        {auth.role === "Purchase Head" && requestType !== 'CR' && (
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
        {auth.role === "Purchase Head" && (
  <Button
    icon={<TeamOutlined />}
    style={{ marginBottom: 12 }}
    onClick={() => {
      setAssignmentModalOpen(true);
      fetchMyAssignments();
      fetchEmployees(); // pre-load employee list for reassign
    }}
  >
    Active Assignments
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
              fetchVersionHistory={fetchVersionHistory}
              versionHistoryLoading={versionHistoryLoading}
              // ── new props for the version history Modal now living in QueueModal ──
              versionHistoryOpen={versionHistoryOpen}
              setVersionHistoryOpen={setVersionHistoryOpen}
              versionHistoryList={versionHistoryList}
              selectedVersionIdx={selectedVersionIdx}
              setSelectedVersionIdx={setSelectedVersionIdx}
              versionHistoryWorkflowId={versionHistoryWorkflowId}
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

      {auth.role === "Purchase Head" && (
        <Modal
          title="Active Assignments"
          open={assignmentModalOpen}
          onCancel={() => {
            setAssignmentModalOpen(false);
            setReassignTarget(null);
            setReassignEmployee(null);
            setAssignmentSearch("");
          }}
          footer={null}
          width={820}
          destroyOnClose
        >
          <Input
            placeholder="Search by Indent ID, Indentor, Subject or Assigned To"
            prefix={<SearchOutlined />}
            value={assignmentSearch}
            onChange={(e) => setAssignmentSearch(e.target.value)}
            allowClear
            style={{ marginBottom: 12, width: 400 }}
          />
          <Table
            loading={assignmentLoading}
            dataSource={assignedIndents.filter((item) => {
              if (!assignmentSearch) return true;
              const term = assignmentSearch.toLowerCase();
              return (
                (item.indentId || "").toLowerCase().includes(term) ||
                (item.indentorName || "").toLowerCase().includes(term) ||
                (item.subject || "").toLowerCase().includes(term) ||
                (item.assignedToEmployeeName || "").toLowerCase().includes(term)
              );
            })}
            rowKey="indentId"
            size="small"
            pagination={{ pageSize: 8 }}
            columns={[
              { title: "Indent ID", dataIndex: "indentId", key: "indentId", width: 130 },
              { title: "Indentor", dataIndex: "indentorName", key: "indentorName" },
              { title: "Subject", dataIndex: "subject", key: "subject", ellipsis: true },
              { title: "Assigned To", dataIndex: "assignedToEmployeeName", key: "assignedToEmployeeName" },
              {
                title: "Assigned On",
                dataIndex: "assignedDate",
                key: "assignedDate",
                width: 130,
                render: (val) => val ? new Date(val).toLocaleDateString() : "-",
              },
              {
                title: "Action",
                key: "action",
                width: 90,
                render: (_, record) => (
                  <Button size="small" onClick={() => { setReassignTarget(record); setReassignEmployee(null); }}>
                    Change
                  </Button>
                ),
              },
            ]}
          />

          {reassignTarget && (
            <div style={{ marginTop: 16, padding: "12px 16px", background: "#f5f5f5", borderRadius: 6, borderLeft: "3px solid #1677ff" }}>
              <p style={{ marginBottom: 8 }}>
                Reassigning <strong>{reassignTarget.indentId}</strong>
                {" — currently: "}
                <strong>{reassignTarget.assignedToEmployeeName}</strong>
              </p>
              <Space>
                <Select
                  placeholder={loadingEmployees ? "Loading..." : "Select new employee"}
                  style={{ width: 240 }}
                  onChange={setReassignEmployee}
                  value={reassignEmployee}
                  showSearch
                  optionFilterProp="children"
                >
                  {employees.map((emp) => (
                    <Option key={emp.employeeId} value={emp.employeeId}>
                      {emp.employeeName}
                    </Option>
                  ))}
                </Select>
                <Button type="primary" loading={reassignLoading} onClick={handleReassign}>
                  Confirm
                </Button>
                <Button onClick={() => { setReassignTarget(null); setReassignEmployee(null); }}>
                  Cancel
                </Button>
              </Space>
            </div>
          )}
        </Modal>
      )}
    </div>
  );
};

export default QueueRequest;




















