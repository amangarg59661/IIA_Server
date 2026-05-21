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
  PENDING_INITIATION:                    "default",
  INITIATED:                             "processing",
  PENDING_TECHNICAL:                     "warning",
  PENDING_FINANCIAL:                     "warning",
  PENDING_FINANCIAL_SHEET_UPLOAD:        "warning",
  PENDING_APPROVAL:                      "warning",
  PENDING_SPO_APPROVAL:                  "warning",
  PENDING_VENDOR_CLARIFICATION:          "error",
  PENDING_INDENTOR_CLARIFICATION:        "error",
  PENDING_MEMBER_REVOTE:                 "error",
  PENDING_COMMITTEE_FORMATION:           "warning",
  PENDING_DIRECTOR_APPROVAL:             "warning",
  APPROVED:                              "success",
  REJECTED:                              "error",
};

// context-aware label; pass indentCategory when available
const evalStatusLabel = (s, indentCategory) => {
  if (!s || s === "PENDING_INITIATION") return "Pending Initiation";
  if (s === "PENDING_APPROVAL") {
    return indentCategory === "MULTIPLE_INDENT"
      ? "Pending Approval from Purchase Personnel"
      : "Pending Approval from Indentor";
  }
  const map = {
    PENDING_TECHNICAL:               "Pending Technical Evaluation",
    PENDING_FINANCIAL:               "Pending Financial Evaluation",
    PENDING_FINANCIAL_SHEET_UPLOAD:  "Pending Financial Comparison Sheet Upload",
    PENDING_SPO_APPROVAL:            "Pending SPO Approval",
    PENDING_VENDOR_CLARIFICATION:    "Clarification from Vendor",
    PENDING_INDENTOR_CLARIFICATION:  "Clarification from Evaluator",
    PENDING_MEMBER_REVOTE:           "Pending Committee Re-vote",
    PENDING_COMMITTEE_FORMATION:     "Pending Committee Formation",
    PENDING_DIRECTOR_APPROVAL:       "Pending Director Approval",
    APPROVED:                        "Tender Evaluation Completed",
    REJECTED:                        "Rejected",
  };
  return map[s] || s;
};

// Per-vendor current-status label
const vendorCurrentStatusLabel = (vendor) => {
  if (vendor.status === "CHANGE_REQUESTED") return "Seek Clarification Sent";
  if (vendor.status === "CHANGE_RESPONDED") return "Clarification Responded";
  if (vendor.status === "Completed")        return "Evaluation Completed";
  return vendor.indentorStatus || "Pending";
};

// Phases where the evaluator (Indentor/PP) is expected to act
const EVALUATOR_ACTIVE_STATUSES = [
  "PENDING_APPROVAL",
  "PENDING_FINANCIAL",
  "PENDING_VENDOR_CLARIFICATION",
  "PENDING_INDENTOR_CLARIFICATION",
];

const isActiveEvaluatorPhase = (status) =>
  EVALUATOR_ACTIVE_STATUSES.includes(status);

// ── Main Component ─────────────────────────────────────────────────────────────
const TenderEvaluationPage = () => {
  const auth = useSelector((s) => s.auth);

  // Role flags (normalize to handle case/spelling variations)
  const normalizedRole = (auth.role || "").toLowerCase().trim();
  const isSPO       = normalizedRole === "store purchase officer" || normalizedRole === "spo";
  const isPP        = normalizedRole === "purchase personnel" || normalizedRole === "purchase person";
  const isChairman  = normalizedRole.includes("chairman");
  const isDirector  = normalizedRole.includes("director");
  const isCommitteeMember = normalizedRole.includes("committee") || normalizedRole.includes("member");
  const isEvaluator = !isSPO; // Indentor or PP

  // Map display role → backend role code for API calls
  const backendRole = (() => {
    if (isSPO) return "SPO";
    if (isPP) return "PURCHASE_PERSONNEL";
    if (isDirector) return "DIRECTOR";
    if (isChairman) return "CHAIRMAN";
    if (isCommitteeMember) return "COMMITTEE_MEMBER";
    return "INDENTOR";
  })();

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
  const [clarDlgOpen,  setClarDlgOpen]  = useState(false);
  const [clarVendorId, setClarVendorId] = useState(null);
  const [clarRemarks,  setClarRemarks]  = useState("");
  const [clarTarget,   setClarTarget]   = useState("VENDOR");

  // Reject dialog (evaluator per-vendor)
  const [rejectDlgOpen,  setRejectDlgOpen]  = useState(false);
  const [rejectVendorId, setRejectVendorId] = useState(null);
  const [rejectRemarks,  setRejectRemarks]  = useState("");
  const [rejectIsSPO,    setRejectIsSPO]    = useState(false);

  // Reject Evaluation dialog (whole evaluation, any role)
  const [rejectEvalDlgOpen,  setRejectEvalDlgOpen]  = useState(false);
  const [rejectEvalRemarks,  setRejectEvalRemarks]  = useState("");

  // Committee member vote
  const [committeeVote,    setCommitteeVote]    = useState(null);
  const [committeeRemarks, setCommitteeRemarks] = useState("");

  // Chairman / Director decision
  const [chairmanDecisionInput,  setChairmanDecisionInput]  = useState(null);
  const [chairmanRemarksInput,   setChairmanRemarksInput]   = useState("");
  const [directorDecisionInput,  setDirectorDecisionInput]  = useState(null);
  const [directorRemarksInput,   setDirectorRemarksInput]   = useState("");

  // Initiate without sheet confirmation
  const [noSheetDlgOpen,        setNoSheetDlgOpen]        = useState(false);
  const [pendingInitiateTenderId, setPendingInitiateTenderId] = useState(null);

  // Clarification history dialog
  const [clarHistoryOpen,   setClarHistoryOpen]   = useState(false);
  const [clarHistoryData,   setClarHistoryData]   = useState([]);
  const [clarHistoryTender, setClarHistoryTender] = useState(null);

  // ── Data fetching ─────────────────────────────────────────────────────────
  const loadTenderList = useCallback(async () => {
    setLoading(true);
    try {
      const res = await axios.get("/getApprovedTender");
      const tenders = res.data?.responseData || [];

      const enriched = await Promise.all(
        tenders.map(async (t) => {
          try {
            const evalRes = await axios.get('/api/tender-evaluation/status', { params: { tenderId: t.tenderId } });
            const evalData = evalRes.data?.responseData;
            return {
              ...t,
              evaluationStatus: evalData?.evaluationStatus || null,
              bidType:          evalData?.bidType          || t.bidType,
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
      const res = await axios.get('/api/tender-evaluation/status', {
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

  // Initiation
  const handleInitiate = async (tenderId) => {
    const evalEntry = tenderList.find((t) => t.tenderId === tenderId)
      || selectedEval;
    const noSheet  = !evalEntry?.evalData?.comparisonSheetFileName
      && !selectedEval?.comparisonSheetFileName;
    const isDouble = ((evalEntry?.bidType || selectedEval?.bidType) || "")
      .toUpperCase().includes("DOUBLE");

    if (isDouble && noSheet) {
      message.error(
        "Technical Comparison Sheet is required for Double Bid before initiation."
      );
      return;
    }
    if (!isDouble && noSheet) {
      setPendingInitiateTenderId(tenderId);
      setNoSheetDlgOpen(true);
      return;
    }
    await doInitiate(tenderId);
  };

  const doInitiate = async (tenderId) => {
    setActionLoading(true);
    try {
      await axios.post('/api/tender-evaluation/initiate', null, {
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

  // Evaluator per-vendor Accept
  const handleIndentorAccept = async (tenderId, vendorId) => {
    setActionLoading(true);
    try {
      await axios.post(
        '/api/tender-evaluation/vendor/indentor-decision',
        { decision: "ACCEPTED", remarks: "Accepted", userId: auth.userId },
        { params: { tenderId, vendorId } }
      );
      message.success("Vendor accepted.");
      await loadEval(tenderId);
    } catch (e) {
      message.error(e?.response?.data?.message || "Accept failed.");
    } finally {
      setActionLoading(false);
    }
  };

  // Evaluator per-vendor Reject (opens dialog)
  const openRejectDialog = (vendorId, spoReject = false) => {
    setRejectVendorId(vendorId);
    setRejectIsSPO(spoReject);
    setRejectRemarks("");
    setRejectDlgOpen(true);
  };

  const handleRejectConfirm = async () => {
    if (!rejectRemarks.trim()) {
      message.warning("Rejection remarks are mandatory.");
      return;
    }
    setActionLoading(true);
    try {
      if (rejectIsSPO) {
        await axios.post(
          '/api/tender-evaluation/vendor/spo-decision',
          { decision: "REJECTED", remarks: rejectRemarks, userId: auth.userId },
          { params: { tenderId: selectedEval.tenderId, vendorId: rejectVendorId } }
        );
      } else {
        await axios.post(
          '/api/tender-evaluation/vendor/indentor-decision',
          { decision: "REJECTED", remarks: rejectRemarks, userId: auth.userId },
          { params: { tenderId: selectedEval.tenderId, vendorId: rejectVendorId } }
        );
      }
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

  // Evaluator Confirm Evaluation
  const handleEvaluatorConfirm = async () => {
    setActionLoading(true);
    try {
      await axios.post(
        '/api/tender-evaluation/confirm-by-indentor',
        { indentorUserId: auth.userId },
        { params: { tenderId: selectedEval.tenderId } }
      );
      message.success("Evaluation confirmed.");
      await loadEval(selectedEval.tenderId);
      await loadTenderList();
    } catch (e) {
      message.error(e?.response?.data?.message || "Confirm failed.");
    } finally {
      setActionLoading(false);
    }
  };

  // SPO per-vendor Accept
  const handleSPOAccept = async (tenderId, vendorId) => {
    setActionLoading(true);
    try {
      await axios.post(
        '/api/tender-evaluation/vendor/spo-decision',
        { decision: "ACCEPTED", remarks: "SPO Accepted", userId: auth.userId },
        { params: { tenderId, vendorId } }
      );
      message.success("Vendor accepted by SPO.");
      await loadEval(tenderId);
    } catch (e) {
      message.error(e?.response?.data?.message || "SPO accept failed.");
    } finally {
      setActionLoading(false);
    }
  };

  // SPO Confirm Evaluation
  const handleSPOConfirmEval = async () => {
    setActionLoading(true);
    try {
      await axios.post('/api/tender-evaluation/approve/spo', {
        decision:  "APPROVED",
        remarks:   "SPO Confirmed",
        spoUserId: auth.userId,
      }, { params: { tenderId: selectedEval.tenderId } });
      message.success("Evaluation confirmed by SPO.");
      await loadEval(selectedEval.tenderId);
      await loadTenderList();
    } catch (e) {
      message.error(e?.response?.data?.message || "SPO confirm failed.");
    } finally {
      setActionLoading(false);
    }
  };

  // Committee member: submit vote (confirm locks it)
  const handleCommitteeVoteConfirm = async () => {
    if (!committeeVote) {
      message.warning("Please select your vote before confirming.");
      return;
    }
    setActionLoading(true);
    try {
      await axios.post('/api/tender-evaluation/committee/vote', {
        committeeUserId: auth.userId,
        vote: committeeVote,
        remarks: committeeRemarks,
      }, { params: { tenderId: selectedEval.tenderId } });
      message.success("Vote confirmed. Awaiting Chairman decision.");
      setCommitteeVote(null);
      setCommitteeRemarks("");
      await loadEval(selectedEval.tenderId);
    } catch (e) {
      message.error(e?.response?.data?.message || "Vote failed.");
    } finally {
      setActionLoading(false);
    }
  };

  // Chairman: confirm decision → advances to PENDING_DIRECTOR_APPROVAL
  const handleChairmanConfirm = async () => {
    if (!chairmanDecisionInput) {
      message.warning("Please select your decision before confirming.");
      return;
    }
    setActionLoading(true);
    try {
      await axios.post('/api/tender-evaluation/committee/chairman-decision', {
        chairmanUserId: auth.userId,
        decision: chairmanDecisionInput,
        remarks: chairmanRemarksInput,
        isOverride: chairmanDecisionInput === "OVERRIDE",
      }, { params: { tenderId: selectedEval.tenderId } });
      message.success(
        chairmanDecisionInput === "REJECTED"
          ? "Evaluation rejected by Chairman."
          : "Evaluation sent to Director for final approval."
      );
      setChairmanDecisionInput(null);
      setChairmanRemarksInput("");
      await loadEval(selectedEval.tenderId);
      await loadTenderList();
    } catch (e) {
      message.error(e?.response?.data?.message || "Chairman decision failed.");
    } finally {
      setActionLoading(false);
    }
  };

  // Director: confirm decision → APPROVED (final) or REJECTED
  const handleDirectorConfirm = async () => {
    if (!directorDecisionInput) {
      message.warning("Please select your decision before confirming.");
      return;
    }
    setActionLoading(true);
    try {
      await axios.post('/api/tender-evaluation/director/approve', {
        decision: directorDecisionInput,
        remarks: directorRemarksInput,
        directorUserId: auth.userId,
      }, { params: { tenderId: selectedEval.tenderId } });
      message.success(
        directorDecisionInput === "REJECTED"
          ? "Evaluation rejected by Director."
          : "Tender Evaluation approved (final)."
      );
      setDirectorDecisionInput(null);
      setDirectorRemarksInput("");
      await loadEval(selectedEval.tenderId);
      await loadTenderList();
    } catch (e) {
      message.error(e?.response?.data?.message || "Director decision failed.");
    } finally {
      setActionLoading(false);
    }
  };

  // Seek Clarification submit
  const handleSeekClarification = async () => {
    if (!clarRemarks.trim()) {
      message.warning("Clarification remarks are required.");
      return;
    }
    setActionLoading(true);
    try {
      await axios.post(
        '/api/tender-evaluation/seek-clarification',
        {
          requestedByRole:     backendRole,
          requestedByUserId:   auth.userId,
          clarificationTarget: clarTarget || null,
          targetVendorId:      clarVendorId || null,
          remarks:             clarRemarks,
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

  // Acknowledge Clarification (evaluator)
  const handleAcknowledgeClarification = async () => {
    setActionLoading(true);
    try {
      await axios.post(
        '/api/tender-evaluation/respond-clarification',
        {
          respondedByRole: backendRole,
          respondedById:   String(auth.userId),
          responseText:    "ACKNOWLEDGED",
        },
        { params: { tenderId: selectedEval.tenderId } }
      );
      message.success("Clarification acknowledged.");
      await loadEval(selectedEval.tenderId);
    } catch (e) {
      message.error(e?.response?.data?.message || "Acknowledge failed.");
    } finally {
      setActionLoading(false);
    }
  };

  // Reject entire evaluation
  const handleRejectEvalConfirm = async () => {
    if (!rejectEvalRemarks.trim()) {
      message.warning("Rejection remarks are mandatory.");
      return;
    }
    setActionLoading(true);
    try {
      await axios.post(
        '/api/tender-evaluation/reject',
        { rejectedByRole: backendRole, userId: auth.userId, remarks: rejectEvalRemarks },
        { params: { tenderId: selectedEval.tenderId } }
      );
      message.success("Evaluation rejected.");
      setRejectEvalDlgOpen(false);
      setRejectEvalRemarks("");
      await loadEval(selectedEval.tenderId);
      await loadTenderList();
    } catch (e) {
      message.error(e?.response?.data?.message || "Reject failed.");
    } finally {
      setActionLoading(false);
    }
  };

  // Reopen rejected evaluation
  const handleReopenEval = async () => {
    setActionLoading(true);
    try {
      await axios.post('/api/tender-evaluation/reopen', null, {
        params: { tenderId: selectedEval.tenderId, userId: auth.userId },
      });
      message.success("Evaluation reopened.");
      await loadEval(selectedEval.tenderId);
      await loadTenderList();
    } catch (e) {
      message.error(e?.response?.data?.message || "Reopen failed.");
    } finally {
      setActionLoading(false);
    }
  };

  // Upload comparison sheet
  const handleUploadSheet = async (file, isFinancial = false) => {
    const base64    = await fileToBase64(file);
    const fieldName = isFinancial
      ? "uploadCommeriallyQualifiedVendorsFileName"
      : "uploadQualifiedVendorsFileName";
    try {
      await axios.put('/api/tender-evaluation', {
        [fieldName]: base64,
        updatedBy:   String(auth.userId),
      }, { params: { tenderId: selectedEval.tenderId } });
      message.success(
        `${isFinancial ? "Financial" : "Technical"} Comparison Sheet uploaded.`
      );
      await loadEval(selectedEval.tenderId);
    } catch {
      message.error("Upload failed.");
    }
    return false;
  };

  // Clarification history
  const loadClarificationHistory = async (tenderId) => {
    setClarHistoryTender(tenderId);
    setClarHistoryOpen(true);
    try {
      const res = await axios.get(
        '/api/tender-evaluation/clarification-history', { params: { tenderId } }
      );
      setClarHistoryData(res.data?.responseData || []);
    } catch {
      setClarHistoryData([]);
    }
  };

  // ── Derived state for selected eval ───────────────────────────────────────
  const evalStatus     = selectedEval?.evaluationStatus || null;
  const indentCategory = selectedEval?.indentCategory;
  const bidType        = selectedEval?.bidType || "";
  const isDoubleBid    = bidType.toUpperCase().includes("DOUBLE");
  const financialPhase = !!selectedEval?.financialBidPhase;

  // All eligible vendors for evaluator confirm check
  const vendors = selectedEval?.vendors || [];

  const evaluatorConfirmEnabled =
    vendors.length > 0 &&
    vendors.every(
      (v) =>
        (v.indentorStatus === "ACCEPTED" || v.indentorStatus === "REJECTED") &&
        v.status !== "CHANGE_REQUESTED"
    );

  const spoConfirmEnabled =
    vendors.length > 0 &&
    vendors
      .filter((v) => v.indentorStatus === "ACCEPTED")
      .every(
        (v) =>
          (v.spoStatus === "ACCEPTED" || v.spoStatus === "REJECTED") &&
          v.status !== "CHANGE_REQUESTED"
      ) &&
    vendors.filter((v) => v.indentorStatus === "ACCEPTED").length > 0;

  // Acknowledge button: evaluator, clarification pending from vendor, none CHANGE_REQUESTED
  const showAcknowledge =
    isEvaluator &&
    selectedEval?.clarificationPendingFrom === "VENDOR" &&
    vendors.every((v) => v.status !== "CHANGE_REQUESTED");

  // Show Initiate button: PP only, no status or PENDING_INITIATION
  const showInitiate =
    isPP &&
    !isSPO &&
    (!evalStatus || evalStatus === "PENDING_INITIATION");

  // Show evaluator action buttons
  const showEvaluatorActions =
    isEvaluator && isActiveEvaluatorPhase(evalStatus);

  // Show SPO per-vendor actions
  const showSPOActions =
    isSPO && evalStatus === "PENDING_SPO_APPROVAL";

  // Show evaluator Confirm Evaluation button
  const showEvaluatorConfirm =
    isEvaluator &&
    evalStatus &&
    !["PENDING_SPO_APPROVAL", "APPROVED", "REJECTED"].includes(evalStatus);

  // Above 10L flags
  const isAbove10L = selectedEval?.amountCategory && selectedEval.amountCategory !== "UNDER_10_LAKH";

  // Committee member: vote during PENDING_APPROVAL (above 10L, not chairman)
  const showCommitteeMemberActions =
    isCommitteeMember && !isChairman && isAbove10L && evalStatus === "PENDING_APPROVAL";

  // Chairman: decide after reviewing votes (above 10L, PENDING_APPROVAL)
  const showChairmanActions =
    isChairman && isAbove10L && evalStatus === "PENDING_APPROVAL";

  // Director: final approval (above 10L, PENDING_DIRECTOR_APPROVAL)
  const showDirectorActions =
    isDirector && evalStatus === "PENDING_DIRECTOR_APPROVAL";

  // Column 6: evaluator status label
  const evalStatusColTitle =
    indentCategory === "MULTIPLE_INDENT"
      ? "Purchase Personnel Status"
      : "Indentor Status";

  // ── Vendor Table Columns ──────────────────────────────────────────────────
  const vendorColumns = [
    { title: "Vendor ID",   dataIndex: "vendorId",   key: "vendorId",   width: 100 },
    { title: "Vendor Name", dataIndex: "vendorName", key: "vendorName", width: 160 },
    {
      title: "Technical Document",
      dataIndex: "quotationFileName",
      key: "techDoc",
      render: (f) =>
        f ? (
          <Button
            size="small" type="link" icon={<EyeOutlined />}
            href={`/file/view/Tender/${f}`} target="_blank"
          >
            View
          </Button>
        ) : (
          <Text type="secondary">N/A</Text>
        ),
    },
    {
      title: "Financial Document",
      dataIndex: "priceBidFileName",
      key: "finDoc",
      render: (f) => {
        // Hidden during double-bid technical phase
        if (isDoubleBid && !financialPhase)
          return <Text type="secondary" disabled>Hidden</Text>;
        return f ? (
          <Button
            size="small" type="link" icon={<EyeOutlined />}
            href={`/file/view/Tender/${f}`} target="_blank"
          >
            View
          </Button>
        ) : (
          <Text type="secondary">N/A</Text>
        );
      },
    },
    {
      title: "Current Status",
      key: "currentStatus",
      render: (_, r) => {
        const label = vendorCurrentStatusLabel(r);
        const color =
          r.status === "CHANGE_REQUESTED" ? "orange"
          : r.status === "CHANGE_RESPONDED" ? "blue"
          : r.status === "Completed" ? "green"
          : "default";
        return <Tag color={color}>{label}</Tag>;
      },
    },
    {
      title: evalStatusColTitle,
      dataIndex: "indentorStatus",
      key: "indentorStatus",
      render: (s) =>
        s ? (
          <Tag color={s === "ACCEPTED" ? "green" : s === "REJECTED" ? "red" : "orange"}>
            {s}
          </Tag>
        ) : (
          <Text type="secondary">Pending</Text>
        ),
    },
    {
      title: "SPO Status",
      dataIndex: "spoStatus",
      key: "spoStatus",
      render: (s) =>
        s ? (
          <Tag color={s === "ACCEPTED" ? "green" : s === "REJECTED" ? "red" : "orange"}>
            {s}
          </Tag>
        ) : (
          <Text type="secondary">Pending</Text>
        ),
    },
    // Evaluator per-row actions
    ...(showEvaluatorActions
      ? [
          {
            title: "Accept",
            key: "eval_accept",
            render: (_, r) => (
              <Button
                size="small" type="primary" icon={<CheckCircleOutlined />}
                disabled={
                  r.indentorStatus === "ACCEPTED" ||
                  r.status === "CHANGE_REQUESTED"
                }
                loading={actionLoading}
                onClick={() => handleIndentorAccept(selectedEval.tenderId, r.vendorId)}
              >
                {r.indentorStatus === "REJECTED" ? "Change to Accept" : "Accept"}
              </Button>
            ),
          },
          {
            title: "Reject",
            key: "eval_reject",
            render: (_, r) => (
              <Button
                size="small" danger icon={<CloseCircleOutlined />}
                disabled={
                  r.indentorStatus === "REJECTED" ||
                  r.status === "CHANGE_REQUESTED"
                }
                onClick={() => openRejectDialog(r.vendorId, false)}
              >
                {r.indentorStatus === "ACCEPTED" ? "Change to Reject" : "Reject"}
              </Button>
            ),
          },
          {
            title: "Seek Clarification",
            key: "eval_clarify",
            render: (_, r) => (
              <Button
                size="small" icon={<QuestionCircleOutlined />}
                disabled={r.status === "CHANGE_REQUESTED"}
                onClick={() => {
                  setClarVendorId(r.vendorId);
                  setClarTarget("VENDOR");
                  setClarDlgOpen(true);
                }}
              >
                Seek Clarification
              </Button>
            ),
          },
        ]
      : []),
    // SPO per-row actions
    ...(showSPOActions
      ? [
          {
            title: "SPO Accept",
            key: "spo_accept",
            render: (_, r) => (
              <Button
                size="small" type="primary" icon={<CheckCircleOutlined />}
                disabled={
                  r.indentorStatus !== "ACCEPTED" ||
                  r.spoStatus === "ACCEPTED" ||
                  r.status === "CHANGE_REQUESTED"
                }
                loading={actionLoading}
                onClick={() => handleSPOAccept(selectedEval.tenderId, r.vendorId)}
              >
                {r.spoStatus === "REJECTED" ? "Change to Accept" : "Accept"}
              </Button>
            ),
          },
          {
            title: "SPO Reject",
            key: "spo_reject",
            render: (_, r) => (
              <Button
                size="small" danger icon={<CloseCircleOutlined />}
                disabled={
                  r.indentorStatus !== "ACCEPTED" ||
                  r.spoStatus === "REJECTED" ||
                  r.status === "CHANGE_REQUESTED"
                }
                onClick={() => openRejectDialog(r.vendorId, true)}
              >
                {r.spoStatus === "ACCEPTED" ? "Change to Reject" : "Reject"}
              </Button>
            ),
          },
          {
            title: "Seek Revision / Clarification",
            key: "spo_clarify",
            render: (_, r) => (
              <Button
                size="small" icon={<QuestionCircleOutlined />}
                onClick={() => {
                  setClarVendorId(r.vendorId);
                  setClarTarget("VENDOR");
                  setClarDlgOpen(true);
                }}
              >
                Seek Clarification
              </Button>
            ),
          },
        ]
      : []),
  ];

  // ── Tender List Columns ────────────────────────────────────────────────────
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
    {
      title: "Bid Type",
      dataIndex: "bidType",
      key: "bidType",
      render: (v) => v || "--",
    },
    {
      title: "Amount (₹)",
      dataIndex: "totalValue",
      key: "totalValue",
      render: (v) => (v ? `₹ ${Number(v).toLocaleString("en-IN")}` : "--"),
    },
    {
      title: "Current Status",
      key: "evalStatus",
      render: (_, r) => {
        const s = r.evaluationStatus;
        return (
          <Badge
            status={STATUS_COLOR[s] || "default"}
            text={evalStatusLabel(s, r.evalData?.indentCategory)}
          />
        );
      },
    },
    {
      title: "Actions",
      key: "actions",
      render: (_, r) => (
        <Space>
          <Button size="small" onClick={() => loadEval(r.tenderId)}>
            View
          </Button>
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

      {/* ── Filters ── */}
      <Space wrap className="mb-4">
        <Select
          value={tenderTypeFilter}
          onChange={setTenderTypeFilter}
          style={{ width: 260 }}
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
          style={{ width: 320 }}
          placeholder="Tender ID"
          optionFilterProp="label"
          options={tenderList.map((t) => ({
            label: `${t.tenderId} - ${t.titleOfTender || t.tenderId}`,
            value: t.tenderId,
          }))}
        />

        <Button onClick={loadTenderList} loading={loading}>
          Refresh
        </Button>
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
          {/* Header info */}
          <Space className="mb-3" wrap>
            <Title level={5} style={{ marginBottom: 0 }}>
              Tender: {selectedEval.tenderId}
            </Title>
            <Badge
              status={STATUS_COLOR[evalStatus] || "default"}
              text={evalStatusLabel(evalStatus, indentCategory)}
            />
            <Tag>{bidType || "N/A"}</Tag>
            <Tag>{indentCategory || "N/A"}</Tag>
          </Space>

          {/* ── Sheet uploads (non-SPO) ── */}
          {!isSPO && (
            <Space wrap className="mb-3">
              {/* Technical sheet: visible when null/PENDING_INITIATION */}
              {(!evalStatus || evalStatus === "PENDING_INITIATION") && (
                <Upload
                  beforeUpload={(f) => { handleUploadSheet(f, false); return false; }}
                  showUploadList={false}
                >
                  <Button icon={<UploadOutlined />} size="small">
                    Upload Technical Comparison Sheet
                    {selectedEval.comparisonSheetFileName ? " (Uploaded)" : ""}
                  </Button>
                </Upload>
              )}

              {/* Financial sheet: DOUBLE_BID and PENDING_FINANCIAL_SHEET_UPLOAD */}
              {isDoubleBid && evalStatus === "PENDING_FINANCIAL_SHEET_UPLOAD" && (
                <Upload
                  beforeUpload={(f) => { handleUploadSheet(f, true); return false; }}
                  showUploadList={false}
                >
                  <Button icon={<UploadOutlined />} size="small" type="dashed">
                    Upload Financial Comparison Sheet
                    {selectedEval.financialComparisonSheetFileName
                      ? " (Uploaded)"
                      : " (Required)"}
                  </Button>
                </Upload>
              )}
            </Space>
          )}

          {/* ── Initiate Evaluation button (PP only) ── */}
          {showInitiate && (
            <Button
              type="primary"
              loading={actionLoading}
              className="mb-3 mr-2"
              onClick={() => handleInitiate(selectedEval.tenderId)}
            >
              Initiate Evaluation
            </Button>
          )}

          {/* ── Evaluator table-level action buttons ── */}
          {showEvaluatorActions && (
            <Space wrap className="mb-3">
              {/* Seek Clarification from All Vendors */}
              <Button
                icon={<QuestionCircleOutlined />}
                onClick={() => {
                  setClarVendorId(null);
                  setClarTarget("ALL_VENDORS");
                  setClarDlgOpen(true);
                }}
              >
                Seek Clarification from All Vendors
              </Button>

              {/* Confirm Evaluation */}
              {showEvaluatorConfirm && (
                <Tooltip
                  title={
                    !evaluatorConfirmEnabled
                      ? "All vendors must have a decision (Accepted/Rejected) and none pending clarification"
                      : ""
                  }
                >
                  <Button
                    type="primary"
                    loading={actionLoading}
                    disabled={!evaluatorConfirmEnabled}
                    onClick={handleEvaluatorConfirm}
                  >
                    Confirm Evaluation
                  </Button>
                </Tooltip>
              )}

              {/* Acknowledge Clarification */}
              {showAcknowledge && (
                <Button
                  type="default"
                  loading={actionLoading}
                  onClick={handleAcknowledgeClarification}
                >
                  Acknowledge Clarification
                </Button>
              )}
            </Space>
          )}

          {/* Confirm Evaluation button visible even outside active evaluator phase if conditions met */}
          {isEvaluator &&
            !showEvaluatorActions &&
            showEvaluatorConfirm && (
              <Space wrap className="mb-3">
                <Tooltip
                  title={
                    !evaluatorConfirmEnabled
                      ? "All vendors must have a decision (Accepted/Rejected) and none pending clarification"
                      : ""
                  }
                >
                  <Button
                    type="primary"
                    loading={actionLoading}
                    disabled={!evaluatorConfirmEnabled}
                    onClick={handleEvaluatorConfirm}
                  >
                    Confirm Evaluation
                  </Button>
                </Tooltip>
                {showAcknowledge && (
                  <Button
                    type="default"
                    loading={actionLoading}
                    onClick={handleAcknowledgeClarification}
                  >
                    Acknowledge Clarification
                  </Button>
                )}
              </Space>
            )}

          {/* ── SPO table-level actions ── */}
          {showSPOActions && (
            <Space wrap className="mb-3">
              <Tooltip
                title={
                  !spoConfirmEnabled
                    ? "All Indentor-accepted vendors must have an SPO decision and none pending clarification"
                    : ""
                }
              >
                <Button
                  type="primary"
                  loading={actionLoading}
                  disabled={!spoConfirmEnabled}
                  onClick={handleSPOConfirmEval}
                >
                  Confirm Evaluation
                </Button>
              </Tooltip>

              <Button
                icon={<QuestionCircleOutlined />}
                onClick={() => {
                  setClarVendorId(null);
                  setClarTarget("INDENTOR");
                  setClarDlgOpen(true);
                }}
              >
                Seek Revision / Clarification
              </Button>
            </Space>
          )}

          {/* ── Committee Member Actions (above 10L) ── */}
          {showCommitteeMemberActions && (
            <div className="mb-3 border rounded p-3 bg-white">
              <Title level={5} style={{ marginBottom: 8 }}>Committee Vote</Title>
              <Space direction="vertical" style={{ width: "100%" }}>
                {selectedEval?.committeeVotes?.find(
                  (v) => v.committeeUserId === auth.userId
                )?.vote ? (
                  <Text>
                    Your current vote:{" "}
                    <Tag
                      color={
                        selectedEval.committeeVotes.find(
                          (v) => v.committeeUserId === auth.userId
                        ).vote === "APPROVED"
                          ? "green"
                          : "red"
                      }
                    >
                      {selectedEval.committeeVotes.find(
                        (v) => v.committeeUserId === auth.userId
                      ).vote}
                    </Tag>
                  </Text>
                ) : null}
                <Select
                  value={committeeVote}
                  onChange={setCommitteeVote}
                  placeholder="Select your vote"
                  style={{ width: 200 }}
                >
                  <Option value="APPROVED">Approve</Option>
                  <Option value="REJECTED">Reject</Option>
                </Select>
                <TextArea
                  rows={2}
                  placeholder="Vote remarks (optional)..."
                  value={committeeRemarks}
                  onChange={(e) => setCommitteeRemarks(e.target.value)}
                />
                <Tooltip
                  title={
                    !evaluatorConfirmEnabled
                      ? "All vendors must have a decision (Accepted/Rejected) and none pending clarification"
                      : !committeeVote
                      ? "Please select your vote"
                      : ""
                  }
                >
                  <Button
                    type="primary"
                    loading={actionLoading}
                    disabled={!committeeVote || !evaluatorConfirmEnabled}
                    onClick={handleCommitteeVoteConfirm}
                  >
                    Confirm Evaluation
                  </Button>
                </Tooltip>
              </Space>
            </div>
          )}

          {/* ── Chairman Actions (above 10L) ── */}
          {showChairmanActions && (
            <div className="mb-3 border rounded p-3 bg-white">
              <Title level={5} style={{ marginBottom: 8 }}>Chairman Decision</Title>
              {selectedEval?.committeeVotes?.length > 0 && (
                <div className="mb-2">
                  <Text strong>Committee Votes: </Text>
                  {selectedEval.committeeVotes.map((v) => (
                    <Tag
                      key={v.committeeUserId}
                      color={
                        v.vote === "APPROVED"
                          ? "green"
                          : v.vote === "REJECTED"
                          ? "red"
                          : "default"
                      }
                    >
                      {v.committeeMemberName}: {v.vote || "Not voted"}
                    </Tag>
                  ))}
                </div>
              )}
              <Space direction="vertical" style={{ width: "100%" }}>
                <Select
                  value={chairmanDecisionInput}
                  onChange={setChairmanDecisionInput}
                  placeholder="Select decision"
                  style={{ width: 240 }}
                >
                  <Option value="APPROVED">Approve</Option>
                  <Option value="REJECTED">Reject</Option>
                  <Option value="OVERRIDE">Override (casting vote)</Option>
                </Select>
                <TextArea
                  rows={2}
                  placeholder="Decision remarks..."
                  value={chairmanRemarksInput}
                  onChange={(e) => setChairmanRemarksInput(e.target.value)}
                />
                <Tooltip
                  title={
                    !evaluatorConfirmEnabled
                      ? "All vendors must have a decision (Accepted/Rejected) and none pending clarification"
                      : !chairmanDecisionInput
                      ? "Please select your decision"
                      : ""
                  }
                >
                  <Button
                    type="primary"
                    loading={actionLoading}
                    disabled={!chairmanDecisionInput || !evaluatorConfirmEnabled}
                    onClick={handleChairmanConfirm}
                  >
                    Confirm Evaluation
                  </Button>
                </Tooltip>
              </Space>
            </div>
          )}

          {/* ── Director Actions (above 10L) ── */}
          {showDirectorActions && (
            <div className="mb-3 border rounded p-3 bg-white">
              <Title level={5} style={{ marginBottom: 8 }}>Director Approval</Title>
              {selectedEval?.chairmanDecision && (
                <div className="mb-2">
                  <Text strong>Chairman Decision: </Text>
                  <Tag
                    color={
                      selectedEval.chairmanDecision === "APPROVED" ||
                      selectedEval.chairmanDecision === "OVERRIDE"
                        ? "green"
                        : "red"
                    }
                  >
                    {selectedEval.chairmanDecision}
                  </Tag>
                  {selectedEval.chairmanRemarks && (
                    <Text type="secondary"> — {selectedEval.chairmanRemarks}</Text>
                  )}
                </div>
              )}
              <Space direction="vertical" style={{ width: "100%" }}>
                <Select
                  value={directorDecisionInput}
                  onChange={setDirectorDecisionInput}
                  placeholder="Select decision"
                  style={{ width: 200 }}
                >
                  <Option value="APPROVED">Approve</Option>
                  <Option value="REJECTED">Reject</Option>
                  <Option value="OVERRIDE">Override</Option>
                </Select>
                <TextArea
                  rows={2}
                  placeholder="Decision remarks..."
                  value={directorRemarksInput}
                  onChange={(e) => setDirectorRemarksInput(e.target.value)}
                />
                <Tooltip
                  title={
                    !evaluatorConfirmEnabled
                      ? "All vendors must have a decision (Accepted/Rejected) and none pending clarification"
                      : !directorDecisionInput
                      ? "Please select your decision"
                      : ""
                  }
                >
                  <Button
                    type="primary"
                    loading={actionLoading}
                    disabled={!directorDecisionInput || !evaluatorConfirmEnabled}
                    onClick={handleDirectorConfirm}
                  >
                    Confirm Evaluation
                  </Button>
                </Tooltip>
              </Space>
            </div>
          )}

          {/* ── Reject / Reopen Evaluation ── */}
          {evalStatus && evalStatus !== "APPROVED" && evalStatus !== "REJECTED" && (
            <Button
              danger
              loading={actionLoading}
              className="mb-3 mr-2"
              onClick={() => { setRejectEvalRemarks(""); setRejectEvalDlgOpen(true); }}
            >
              Reject Evaluation
            </Button>
          )}
          {evalStatus === "REJECTED" && (
            <Space wrap className="mb-3">
              <Text type="danger">
                Rejected by: {selectedEval?.rejectedByRole || "Unknown"}
                {selectedEval?.approvalRemarks ? ` — "${selectedEval.approvalRemarks}"` : ""}
              </Text>
              <Button
                type="primary"
                loading={actionLoading}
                onClick={handleReopenEval}
              >
                Reopen Evaluation
              </Button>
            </Space>
          )}

          {/* ── Vendor Table ── */}
          {vendors.length > 0 && (
            <Table
              dataSource={vendors}
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
          {/* INDENTOR under 10L & COMMITTEE_MEMBER: auto-routed, no target selection */}
          {(backendRole === "INDENTOR" || backendRole === "COMMITTEE_MEMBER") ? (
            <Text type="secondary">
              {backendRole === "COMMITTEE_MEMBER"
                ? "Clarification will be sent to Chairman for review."
                : "Clarification will be routed automatically based on procurement mode."}
            </Text>
          ) : (
            <Select
              value={clarTarget}
              onChange={(val) => { setClarTarget(val); if (val !== "VENDOR") setClarVendorId(null); }}
              style={{ width: "100%" }}
            >
              {/* SPO options */}
              {isSPO && <Option value="VENDOR">To Vendor</Option>}
              {isSPO && <Option value="INDENTOR">To Indentor</Option>}
              {/* CHAIRMAN options */}
              {isChairman && <Option value="SPECIFIC_MEMBER">To Specific Member</Option>}
              {isChairman && <Option value="ALL_MEMBERS">To All Members</Option>}
              {isChairman && <Option value="VENDOR">To Vendor / PP (auto-routed)</Option>}
              {/* DIRECTOR options */}
              {isDirector && <Option value="VENDOR">To Vendor / PP (auto-routed)</Option>}
              {isDirector && <Option value="INDENTOR">To Indentor</Option>}
              {isDirector && <Option value="CHAIRMAN">To Chairman</Option>}
              {isDirector && <Option value="SPECIFIC_MEMBER">To Specific Member</Option>}
              {isDirector && <Option value="ALL_MEMBERS">To All Members</Option>}
              {isDirector && <Option value="PURCHASE_PERSONNEL">To Purchase Personnel</Option>}
            </Select>
          )}
          {/* Vendor selector when target is VENDOR and vendorId not pre-set */}
          {clarTarget === "VENDOR" && !clarVendorId && vendors.length > 0 && (
            <Select
              placeholder="Select vendor..."
              onChange={setClarVendorId}
              style={{ width: "100%" }}
              allowClear
            >
              {vendors.map((v) => (
                <Option key={v.vendorId} value={v.vendorId}>{v.vendorName || v.vendorId}</Option>
              ))}
            </Select>
          )}
          {clarVendorId && (
            <Text type="secondary">Vendor: {clarVendorId}</Text>
          )}
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
        onOk={handleRejectConfirm}
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

      {/* ── Reject Evaluation Dialog ── */}
      <Modal
        title="Reject Evaluation"
        open={rejectEvalDlgOpen}
        onCancel={() => { setRejectEvalDlgOpen(false); setRejectEvalRemarks(""); }}
        onOk={handleRejectEvalConfirm}
        confirmLoading={actionLoading}
        okText="Reject Evaluation"
        okButtonProps={{ danger: true }}
      >
        <Text type="secondary" className="mb-2" style={{ display: "block" }}>
          This will reject the entire tender evaluation. You can reopen it later if needed.
        </Text>
        <TextArea
          rows={4}
          placeholder="Rejection remarks (mandatory)..."
          value={rejectEvalRemarks}
          onChange={(e) => setRejectEvalRemarks(e.target.value)}
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
          No Technical Comparison Sheet has been uploaded. For Single Bid, the sheet is
          optional.
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
        width={960}
      >
        <Table
          dataSource={clarHistoryData}
          rowKey="id"
          size="small"
          bordered
          pagination={false}
          columns={[
            { title: "Round",        dataIndex: "roundNumber",         key: "roundNumber",         width: 60 },
            { title: "Requested By", dataIndex: "requestedByRole",     key: "requestedByRole" },
            { title: "Target",       dataIndex: "clarificationTarget", key: "clarificationTarget" },
            {
              title: "Question",
              dataIndex: "questionRemarks",
              key: "questionRemarks",
              render: (t) => <Text style={{ whiteSpace: "pre-wrap" }}>{t}</Text>,
            },
            {
              title: "Response",
              dataIndex: "responseText",
              key: "responseText",
              render: (t) => t || "--",
            },
            {
              title: "Responded By",
              dataIndex: "respondedByRole",
              key: "respondedByRole",
              render: (t) => t || "--",
            },
            {
              title: "Requested At",
              dataIndex: "requestedAt",
              key: "requestedAt",
              render: (t) => (t ? new Date(t).toLocaleString("en-IN") : "--"),
            },
            {
              title: "Responded At",
              dataIndex: "respondedAt",
              key: "respondedAt",
              render: (t) => (t ? new Date(t).toLocaleString("en-IN") : "--"),
            },
          ]}
        />
      </Modal>
    </div>
  );
};

export default TenderEvaluationPage;
