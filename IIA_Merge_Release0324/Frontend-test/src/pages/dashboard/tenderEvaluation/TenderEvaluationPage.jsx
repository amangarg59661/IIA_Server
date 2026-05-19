import React, { useCallback, useEffect, useState } from "react";
import {
  Badge, Button, Input, message, Modal, Select, Space, Spin,
  Table, Tag, Tooltip, Typography, Upload,
} from "antd";
import {
  CheckCircleOutlined, CloseCircleOutlined, EyeOutlined,
  QuestionCircleOutlined, UploadOutlined,
} from "@ant-design/icons";
import axios from "axios";
import { useSelector } from "react-redux";

const { Option } = Select;
const { TextArea } = Input;
const { Title, Text } = Typography;

// ── Status display helpers ────────────────────────────────────────────────────
const STATUS_COLOR = {
  PENDING_INITIATION:                       "default",
  INITIATED:                                "processing",
  PENDING_TECHNICAL:                        "warning",
  PENDING_FINANCIAL:                        "warning",
  PENDING_APPROVAL:                         "warning",
  PENDING_SPO_APPROVAL:                     "warning",
  PENDING_VENDOR_CLARIFICATION:             "error",
  PENDING_INDENTOR_CLARIFICATION:           "error",
  PENDING_MEMBER_REVOTE:                    "error",
  PENDING_COMMITTEE_FORMATION:              "warning",
  PENDING_DIRECTOR_APPROVAL:               "warning",
  APPROVED:                                 "success",
  REJECTED:                                 "error",
};

const evalStatusLabel = (s) => {
  if (!s) return "Pending Initiation";
  const map = {
    PENDING_INITIATION:                  "Pending Initiation",
    PENDING_TECHNICAL:                   "Pending Technical Evaluation",
    PENDING_FINANCIAL:                   "Pending Financial Evaluation",
    PENDING_APPROVAL:                    "Pending Evaluator Approval",
    PENDING_SPO_APPROVAL:               "Pending SPO Approval",
    PENDING_VENDOR_CLARIFICATION:        "Clarification from Vendor",
    PENDING_INDENTOR_CLARIFICATION:      "Clarification from Evaluator",
    PENDING_MEMBER_REVOTE:               "Pending Committee Re-vote",
    PENDING_COMMITTEE_FORMATION:         "Pending Committee Formation",
    PENDING_DIRECTOR_APPROVAL:           "Pending Director Approval",
    APPROVED:                            "Tender Evaluation Completed",
    REJECTED:                            "Rejected",
  };
  return map[s] || s;
};

// ── Main Component ─────────────────────────────────────────────────────────────
const TenderEvaluationPage = () => {
  const auth = useSelector((s) => s.auth);

  // Filters
  const [tenderTypeFilter, setTenderTypeFilter] = useState("ALL");
  const [bidTypeFilter,    setBidTypeFilter]    = useState("ALL");
  const [tenderIdFilter,   setTenderIdFilter]   = useState(null);

  // Data
  const [tenderList,    setTenderList]    = useState([]);
  const [selectedEval,  setSelectedEval]  = useState(null);
  const [loading,       setLoading]       = useState(false);
  const [actionLoading, setActionLoading] = useState(false);

  // Clarification dialog
  const [clarDlgOpen,    setClarDlgOpen]    = useState(false);
  const [clarVendorId,   setClarVendorId]   = useState(null);
  const [clarRemarks,    setClarRemarks]    = useState("");
  const [clarTarget,     setClarTarget]     = useState("VENDOR");

  // Reject dialog
  const [rejectDlgOpen,  setRejectDlgOpen]  = useState(false);
  const [rejectVendorId, setRejectVendorId] = useState(null);
  const [rejectRemarks,  setRejectRemarks]  = useState("");

  // Initiate confirmation dialog (no sheet uploaded)
  const [noSheetDlgOpen, setNoSheetDlgOpen] = useState(false);
  const [pendingInitiateTenderId, setPendingInitiateTenderId] = useState(null);

  // Comparison sheet upload state
  const [sheetFile, setSheetFile] = useState(null);
  const [finSheetFile, setFinSheetFile] = useState(null);

  // Clarification history dialog
  const [clarHistoryOpen,  setClarHistoryOpen]  = useState(false);
  const [clarHistoryData,  setClarHistoryData]  = useState([]);
  const [clarHistoryTender, setClarHistoryTender] = useState(null);

  // ── Data fetching ─────────────────────────────────────────────────────────
  const loadTenderList = useCallback(async () => {
    setLoading(true);
    try {
      const res = await axios.get("/getApprovedTender");
      const tenders = res.data?.responseData || [];

      // Enrich each tender with its evaluation status
      const enriched = await Promise.all(
        tenders.map(async (t) => {
          try {
            const evalRes = await axios.get(`/api/tender-evaluation/status`, { params: { tenderId: t.tenderId } });
            const evalData = evalRes.data?.responseData;
            return {
              ...t,
              evaluationStatus: evalData?.evaluationStatus || null,
              bidType:          evalData?.bidType         || t.bidType,
              evalData,
            };
          } catch {
            return { ...t, evaluationStatus: null };
          }
        })
      );
      setTenderList(enriched);
    } catch {
      message.error("Failed to load approved tenders.");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { loadTenderList(); }, [loadTenderList]);

  const loadEval = async (tenderId) => {
    setLoading(true);
    try {
      const res = await axios.get(`/api/tender-evaluation/status`, {
        params: { tenderId, userId: auth.userId, role: auth.role },
      });
      setSelectedEval(res.data?.responseData);
    } catch {
      message.error("Failed to load evaluation details.");
    } finally {
      setLoading(false);
    }
  };

  // ── Filter logic ──────────────────────────────────────────────────────────
  const filteredTenders = tenderList.filter((t) => {
    if (tenderIdFilter && t.tenderId !== tenderIdFilter) return false;
    if (bidTypeFilter !== "ALL") {
      const bt = (t.bidType || "").toUpperCase();
      if (bidTypeFilter === "SINGLE_BID" && !bt.includes("SINGLE")) return false;
      if (bidTypeFilter === "DOUBLE_BID" && !bt.includes("DOUBLE")) return false;
    }
    if (tenderTypeFilter === "INITIATED") {
      if (!t.evaluationStatus || t.evaluationStatus === "PENDING_INITIATION") return false;
    } else if (tenderTypeFilter === "PENDING") {
      if (t.evaluationStatus && t.evaluationStatus !== "PENDING_INITIATION") return false;
    }
    // Default ALL: show where PO not generated and eval not completed
    if (tenderTypeFilter === "ALL" && t.evaluationStatus === "APPROVED") return false;
    return true;
  });

  // ── File to base64 ────────────────────────────────────────────────────────
  const fileToBase64 = (file) =>
    new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload  = () => resolve(reader.result);
      reader.onerror = reject;
      reader.readAsDataURL(file);
    });

  // ── Actions ───────────────────────────────────────────────────────────────
  const handleInitiate = async (tenderId, hasSheet) => {
    if (!hasSheet) {
      // Check if comparison sheet exists in eval
      const evalEntry = tenderList.find((t) => t.tenderId === tenderId);
      const noSheet   = !evalEntry?.evalData?.comparisonSheetFileName;
      const isDouble  = (evalEntry?.bidType || "").toUpperCase().includes("DOUBLE");
      if (noSheet && isDouble) {
        message.error("Technical Comparison Sheet is required for Double Bid before initiation.");
        return;
      }
      if (noSheet && !isDouble) {
        setPendingInitiateTenderId(tenderId);
        setNoSheetDlgOpen(true);
        return;
      }
    }
    await doInitiate(tenderId);
  };

  const doInitiate = async (tenderId) => {
    setActionLoading(true);
    try {
      await axios.post(`/api/tender-evaluation/initiate`, null, {
        params: { tenderId, userId: auth.userId },
      });
      message.success("Evaluation initiated.");
      setNoSheetDlgOpen(false);
      await loadEval(tenderId);
      await loadTenderList();
    } catch (e) {
      message.error(e?.response?.data?.message || "Failed to initiate evaluation.");
    } finally {
      setActionLoading(false);
    }
  };

  const handleAccept = async (tenderId, vendorId) => {
    setActionLoading(true);
    try {
      await axios.post(`/api/tender-evaluation/select-vendor`, {
        vendorId,
        remarks: "Accepted by evaluator",
        actionByUserId: auth.userId,
      }, { params: { tenderId } });

      // Optimistically mark indentor/pp status
      await axios.post(`/api/tender-evaluation/approve/indentor-purchase`, {
        decision: "APPROVED",
        remarks: "Accepted",
        approverUserId: auth.userId,
      }, { params: { tenderId } });

      message.success("Vendor accepted.");
      await loadEval(tenderId);
    } catch (e) {
      message.error(e?.response?.data?.message || "Accept failed.");
    } finally {
      setActionLoading(false);
    }
  };

  const handleReject = async () => {
    if (!rejectRemarks.trim()) {
      message.warning("Rejection remarks are mandatory.");
      return;
    }
    setActionLoading(true);
    try {
      await axios.post(
        `/api/tender-evaluation/approve/indentor-purchase`,
        {
          decision:       "REJECTED",
          remarks:        rejectRemarks,
          approverUserId: auth.userId,
        },
        { params: { tenderId: selectedEval.tenderId } }
      );
      message.success("Vendor rejected.");
      setRejectDlgOpen(false);
      setRejectRemarks("");
      await loadEval(selectedEval.tenderId);
    } catch (e) {
      message.error(e?.response?.data?.message || "Reject failed.");
    } finally {
      setActionLoading(false);
    }
  };

  const handleSPOConfirm = async (decision) => {
    setActionLoading(true);
    try {
      await axios.post(`/api/tender-evaluation/approve/spo`, {
        decision,
        remarks: decision === "APPROVED" ? "SPO Confirmed" : "SPO Rejected",
        spoUserId: auth.userId,
      }, { params: { tenderId: selectedEval.tenderId } });
      message.success(decision === "APPROVED" ? "Evaluation Confirmed." : "Rejected.");
      await loadEval(selectedEval.tenderId);
      await loadTenderList();
    } catch (e) {
      message.error(e?.response?.data?.message || "SPO action failed.");
    } finally {
      setActionLoading(false);
    }
  };

  const handleSeekClarification = async () => {
    if (!clarRemarks.trim()) {
      message.warning("Clarification remarks are required.");
      return;
    }
    setActionLoading(true);
    try {
      await axios.post(
        `/api/tender-evaluation/seek-clarification`,
        {
          requestedByRole:   auth.role,
          requestedByUserId: auth.userId,
          clarificationTarget: clarTarget,
          targetVendorId:    clarVendorId,
          remarks:           clarRemarks,
        },
        { params: { tenderId: selectedEval.tenderId } }
      );
      message.success("Clarification sent.");
      setClarDlgOpen(false);
      setClarRemarks("");
      await loadEval(selectedEval.tenderId);
    } catch (e) {
      message.error(e?.response?.data?.message || "Clarification failed.");
    } finally {
      setActionLoading(false);
    }
  };

  const handleUploadSheet = async (file, isFinancial = false) => {
    const base64 = await fileToBase64(file);
    const fieldName = isFinancial ? "uploadCommeriallyQualifiedVendorsFileName" : "uploadQualifiedVendorsFileName";
    try {
      await axios.put(`/api/tender-evaluation`, {
        [fieldName]: base64,
        updatedBy:   String(auth.userId),
      }, { params: { tenderId: selectedEval.tenderId } });
      message.success(`${isFinancial ? "Financial" : "Technical"} Comparison Sheet uploaded.`);
      await loadEval(selectedEval.tenderId);
    } catch (e) {
      message.error("Upload failed.");
    }
    return false; // prevent antd default upload
  };

  const loadClarificationHistory = async (tenderId) => {
    setClarHistoryTender(tenderId);
    setClarHistoryOpen(true);
    try {
      const res = await axios.get(`/api/tender-evaluation/clarification-history`, { params: { tenderId } });
      setClarHistoryData(res.data?.responseData || []);
    } catch {
      setClarHistoryData([]);
    }
  };

  // ── Table columns ──────────────────────────────────────────────────────────
  const isSPO         = auth.role === "Store Purchase Officer" || auth.role === "SPO";
  const isEvaluator   = !isSPO; // Indentor or PP
  const isDoubleFinancial =
    selectedEval?.bidType === "DOUBLE_BID" && selectedEval?.financialBidPhase;

  const vendorColumns = [
    { title: "Vendor ID",   dataIndex: "vendorId",   key: "vendorId",   width: 100 },
    { title: "Vendor Name", dataIndex: "vendorName", key: "vendorName", width: 160 },
    {
      title: "Technical Document",
      dataIndex: "quotationFileName",
      key: "techDoc",
      render: (f) => f
        ? <Button size="small" type="link" icon={<EyeOutlined />}
            href={`/file/view/Tender/${f}`} target="_blank">View</Button>
        : <Text type="secondary">N/A</Text>,
    },
    {
      title: "Financial Document",
      dataIndex: "priceBidFileName",
      key: "finDoc",
      render: (f, r) => {
        if (selectedEval?.bidType === "DOUBLE_BID" && !r.financialBidVisible)
          return <Text type="secondary" disabled>Hidden</Text>;
        return f
          ? <Button size="small" type="link" icon={<EyeOutlined />}
              href={`/file/view/Tender/${f}`} target="_blank">View</Button>
          : <Text type="secondary">N/A</Text>;
      },
    },
    {
      title: "Current Status",
      dataIndex: "status",
      key: "status",
      render: (s) => <Tag color={s === "Completed" ? "green" : "blue"}>{s || "Pending"}</Tag>,
    },
    {
      title: selectedEval?.indentCategory === "MULTIPLE_INDENT"
        ? "Purchase Personnel Status" : "Indentor Status",
      dataIndex: "indentorStatus",
      key: "indentorStatus",
      render: (s) => s
        ? <Tag color={s === "ACCEPTED" ? "green" : s === "REJECTED" ? "red" : "orange"}>{s}</Tag>
        : <Text type="secondary">Pending</Text>,
    },
    {
      title: "SPO Status",
      dataIndex: "spoStatus",
      key: "spoStatus",
      render: (s) => s
        ? <Tag color={s === "ACCEPTED" ? "green" : s === "REJECTED" ? "red" : "orange"}>{s}</Tag>
        : <Text type="secondary">Pending</Text>,
    },
    ...(isEvaluator && selectedEval?.evaluationStatus &&
      !["APPROVED", "REJECTED", "PENDING_SPO_APPROVAL"].includes(selectedEval.evaluationStatus)
      ? [
          {
            title: "Accept",
            key: "accept",
            render: (_, r) => (
              <Button
                size="small" type="primary" icon={<CheckCircleOutlined />}
                disabled={
                  r.indentorStatus === "ACCEPTED" ||
                  r.indentorStatus === "REJECTED" ||
                  r.status === "CHANGE_REQUESTED"
                }
                onClick={() => handleAccept(selectedEval.tenderId, r.vendorId)}
              >
                Accept
              </Button>
            ),
          },
          {
            title: "Reject",
            key: "reject",
            render: (_, r) => (
              <Button
                size="small" danger icon={<CloseCircleOutlined />}
                disabled={r.indentorStatus === "REJECTED" || r.status === "CHANGE_REQUESTED"}
                onClick={() => { setRejectVendorId(r.vendorId); setRejectDlgOpen(true); }}
              >
                Reject
              </Button>
            ),
          },
          {
            title: "Seek Clarification",
            key: "clarify",
            render: (_, r) => (
              <Button
                size="small" icon={<QuestionCircleOutlined />}
                disabled={r.indentorStatus === "REJECTED"}
                onClick={() => {
                  setClarVendorId(r.vendorId);
                  setClarTarget("VENDOR");
                  setClarDlgOpen(true);
                }}
              >
                Clarify
              </Button>
            ),
          },
        ]
      : []),
    ...(isSPO && selectedEval?.evaluationStatus === "PENDING_SPO_APPROVAL"
      ? [
          {
            title: "Confirm Evaluation",
            key: "spo_accept",
            render: (_, r) => (
              <Button
                size="small" type="primary" icon={<CheckCircleOutlined />}
                disabled={r.spoStatus === "ACCEPTED" || r.spoStatus === "REJECTED"}
                onClick={() => handleSPOConfirm("APPROVED")}
              >
                Confirm Evaluation
              </Button>
            ),
          },
          {
            title: "Seek Revision",
            key: "spo_clarify",
            render: (_, r) => (
              <Button
                size="small" icon={<QuestionCircleOutlined />}
                disabled={r.spoStatus === "REJECTED"}
                onClick={() => {
                  setClarVendorId(r.vendorId);
                  setClarTarget("VENDOR");
                  setClarDlgOpen(true);
                }}
              >
                Seek Revision
              </Button>
            ),
          },
        ]
      : []),
  ];

  // ── Tender list columns ────────────────────────────────────────────────────
  const tenderListColumns = [
    {
      title: "Tender ID - Title",
      key: "tenderLabel",
      render: (_, r) => (
        <Button type="link" onClick={() => loadEval(r.tenderId)}>
          {r.tenderId} - {r.titleOfTender || r.tenderId}
        </Button>
      ),
    },
    { title: "Bid Type", dataIndex: "bidType", key: "bidType",
      render: (v) => v || "--" },
    { title: "Amount (₹)", dataIndex: "totalValue", key: "totalValue",
      render: (v) => v ? `₹ ${Number(v).toLocaleString("en-IN")}` : "--" },
    {
      title: "Current Status",
      key: "evalStatus",
      render: (_, r) => {
        const s = r.evaluationStatus;
        return (
          <Badge status={STATUS_COLOR[s] || "default"}
            text={evalStatusLabel(s)} />
        );
      },
    },
    {
      title: "Actions",
      key: "actions",
      render: (_, r) => (
        <Space>
          <Button size="small" onClick={() => loadEval(r.tenderId)}>View</Button>
          <Button size="small" onClick={() => loadClarificationHistory(r.tenderId)}>
            History
          </Button>
        </Space>
      ),
    },
  ];

  // ── Render ─────────────────────────────────────────────────────────────────
  return (
    <div className="p-4">
      <Title level={4}>Tender Evaluation</Title>

      {/* Filters */}
      <Space wrap className="mb-4">
        <Select
          value={tenderTypeFilter}
          onChange={setTenderTypeFilter}
          style={{ width: 220 }}
          placeholder="Tender Type"
        >
          <Option value="ALL">All (Pending PO, Eval Not Completed)</Option>
          <Option value="INITIATED">Initiated Tender</Option>
          <Option value="PENDING">Pending Initiation Tender</Option>
        </Select>

        <Select
          value={bidTypeFilter}
          onChange={setBidTypeFilter}
          style={{ width: 160 }}
          placeholder="Bid Type"
        >
          <Option value="ALL">All Bid Types</Option>
          <Option value="SINGLE_BID">Single Bid</Option>
          <Option value="DOUBLE_BID">Double Bid</Option>
        </Select>

        <Select
          showSearch
          allowClear
          value={tenderIdFilter}
          onChange={setTenderIdFilter}
          style={{ width: 300 }}
          placeholder="Tender ID"
          optionFilterProp="label"
          options={tenderList.map((t) => ({
            label: `${t.tenderId} - ${t.titleOfTender || t.tenderId}`,
            value: t.tenderId,
          }))}
        />

        <Button onClick={loadTenderList} loading={loading}>Refresh</Button>
      </Space>

      <Spin spinning={loading}>
        <Table
          dataSource={filteredTenders}
          columns={tenderListColumns}
          rowKey="tenderId"
          size="small"
          bordered
          pagination={{ pageSize: 10 }}
        />
      </Spin>

      {/* ── Evaluation Detail Panel ── */}
      {selectedEval && (
        <div className="mt-6 border rounded p-4 bg-gray-50">
          <Space className="mb-2" wrap>
            <Title level={5} style={{ marginBottom: 0 }}>
              Tender: {selectedEval.tenderId}
            </Title>
            <Badge
              status={STATUS_COLOR[selectedEval.evaluationStatus] || "default"}
              text={evalStatusLabel(selectedEval.evaluationStatus)}
            />
            <Tag>{selectedEval.bidType || "N/A"}</Tag>
            <Tag>{selectedEval.indentCategory || "N/A"}</Tag>
          </Space>

          {/* Upload Comparison Sheets */}
          {!isSPO && (
            <Space wrap className="mb-4">
              <Upload
                beforeUpload={(f) => { handleUploadSheet(f, false); return false; }}
                showUploadList={false}
              >
                <Button icon={<UploadOutlined />} size="small">
                  Upload Technical Comparison Sheet
                  {selectedEval.comparisonSheetFileName ? " (Uploaded)" : " (Optional for Single Bid)"}
                </Button>
              </Upload>

              {selectedEval.bidType === "DOUBLE_BID" && selectedEval.financialBidPhase && (
                <Upload
                  beforeUpload={(f) => { handleUploadSheet(f, true); return false; }}
                  showUploadList={false}
                >
                  <Button icon={<UploadOutlined />} size="small" type="dashed">
                    Upload Financial Comparison Sheet
                    {selectedEval.financialComparisonSheetFileName ? " (Uploaded)" : " (Required)"}
                  </Button>
                </Upload>
              )}
            </Space>
          )}

          {/* Initiate button */}
          {!selectedEval.evaluationStatus && !isSPO && (
            <Button
              type="primary"
              loading={actionLoading}
              onClick={() => handleInitiate(selectedEval.tenderId, !!selectedEval.comparisonSheetFileName)}
              className="mb-4"
            >
              Initiate Evaluation
            </Button>
          )}

          {/* SPO Confirm / Reject buttons */}
          {isSPO && selectedEval.evaluationStatus === "PENDING_SPO_APPROVAL" && (
            <Space className="mb-4">
              <Button type="primary" loading={actionLoading}
                onClick={() => handleSPOConfirm("APPROVED")}>
                Confirm Evaluation
              </Button>
              <Button danger loading={actionLoading}
                onClick={() => { setClarTarget("INDENTOR"); setClarDlgOpen(true); }}>
                Seek Revision / Clarification
              </Button>
            </Space>
          )}

          {/* Vendor Table */}
          {selectedEval.vendors?.length > 0 && (
            <Table
              dataSource={selectedEval.vendors}
              columns={vendorColumns}
              rowKey="vendorId"
              size="small"
              bordered
              scroll={{ x: true }}
              pagination={false}
            />
          )}
        </div>
      )}

      {/* ── Seek Clarification Dialog ── */}
      <Modal
        title="Seek Clarification"
        open={clarDlgOpen}
        onCancel={() => { setClarDlgOpen(false); setClarRemarks(""); }}
        onOk={handleSeekClarification}
        confirmLoading={actionLoading}
        okText="Send"
      >
        <Space direction="vertical" style={{ width: "100%" }}>
          <Select
            value={clarTarget}
            onChange={setClarTarget}
            style={{ width: "100%" }}
          >
            <Option value="VENDOR">To Vendor</Option>
            <Option value="INDENTOR">To Evaluator</Option>
            <Option value="PURCHASE_PERSONNEL">To Purchase Personnel</Option>
            <Option value="ALL_VENDORS">To All Vendors</Option>
          </Select>
          <TextArea
            rows={4}
            placeholder="Enter clarification remarks..."
            value={clarRemarks}
            onChange={(e) => setClarRemarks(e.target.value)}
          />
        </Space>
      </Modal>

      {/* ── Reject Dialog ── */}
      <Modal
        title="Reject Vendor"
        open={rejectDlgOpen}
        onCancel={() => { setRejectDlgOpen(false); setRejectRemarks(""); }}
        onOk={handleReject}
        confirmLoading={actionLoading}
        okText="Reject"
        okButtonProps={{ danger: true }}
      >
        <TextArea
          rows={4}
          placeholder="Rejection remarks (mandatory)..."
          value={rejectRemarks}
          onChange={(e) => setRejectRemarks(e.target.value)}
        />
      </Modal>

      {/* ── No Sheet Confirmation Dialog ── */}
      <Modal
        title="No Comparison Sheet Uploaded"
        open={noSheetDlgOpen}
        onCancel={() => { setNoSheetDlgOpen(false); setPendingInitiateTenderId(null); }}
        onOk={() => {
          setNoSheetDlgOpen(false);
          doInitiate(pendingInitiateTenderId);
          setPendingInitiateTenderId(null);
        }}
        okText="Yes, Initiate Without Sheet"
        cancelText="No, Upload Sheet First"
      >
        <p>
          No Technical Comparison Sheet has been uploaded. For Single Bid, the sheet is optional.
          <br />
          Do you want to initiate the evaluation without uploading the sheet?
        </p>
      </Modal>

      {/* ── Clarification History Dialog ── */}
      <Modal
        title={`Clarification History — ${clarHistoryTender}`}
        open={clarHistoryOpen}
        onCancel={() => setClarHistoryOpen(false)}
        footer={null}
        width={900}
      >
        <Table
          dataSource={clarHistoryData}
          rowKey="id"
          size="small"
          bordered
          pagination={false}
          columns={[
            { title: "Round", dataIndex: "roundNumber", key: "roundNumber", width: 60 },
            { title: "Requested By", dataIndex: "requestedByRole", key: "requestedByRole" },
            { title: "Target", dataIndex: "clarificationTarget", key: "clarificationTarget" },
            { title: "Question", dataIndex: "questionRemarks", key: "questionRemarks",
              render: (t) => <Text style={{ whiteSpace: "pre-wrap" }}>{t}</Text> },
            { title: "Response", dataIndex: "responseText", key: "responseText",
              render: (t) => t || "--" },
            { title: "Responded By", dataIndex: "respondedByRole", key: "respondedByRole",
              render: (t) => t || "--" },
            {
              title: "Requested At", dataIndex: "requestedAt", key: "requestedAt",
              render: (t) => t ? new Date(t).toLocaleString("en-IN") : "--",
            },
            {
              title: "Responded At", dataIndex: "respondedAt", key: "respondedAt",
              render: (t) => t ? new Date(t).toLocaleString("en-IN") : "--",
            },
          ]}
        />
      </Modal>
    </div>
  );
};

export default TenderEvaluationPage;
