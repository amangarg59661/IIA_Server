
import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { message, Spin, Tag, Table, Checkbox, Popover, Input, Button, Modal, Select, Alert, Divider, Card, Badge, Typography, Steps, List, Space } from 'antd';
import { PlusOutlined, MinusCircleOutlined } from '@ant-design/icons';
import { useSelector } from 'react-redux';
import { useNavigate, useLocation } from 'react-router-dom';
import Heading from '../../../components/DKG_Heading';
import FormContainer from '../../../components/DKG_FormContainer';
import FormBody from '../../../components/DKG_FormBody';
import CustomForm from "../../../components/DKG_CustomForm";
import { renderFormFields } from "../../../utils/CommonFunctions";
import Btn from '../../../components/DKG_Btn';
import { baseURL } from '../../../App';
import TenderEvaluationHistory from '../queue/TenderEvaluationHistory';

const { Option } = Select;
const { Text, Title } = Typography;

const TenderEvaluator = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { tenderId: initialTenderId } = location.state || {};
  const { userId } = useSelector(state => state.auth);
 

  const [formData, setFormData] = useState({
    tenderId: initialTenderId || '',
    bidType: '',
    totalValue: null,
    comparationStatementFileName: [],
    financialComparisionSheetFileName: [],
  });
  const [quotationData, setQuotationData] = useState([]);
  const [notSubmittedVendors, setNotSubmittedVendors] = useState([]);
  const [selectedVendor, setSelectedVendor] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [rejectComment, setRejectComment] = useState('');
  const [rejectedVendorId, setRejectedVendorId] = useState(null);
  const [historyVisible, setHistoryVisible] = useState(false);
  const [selectedVendorForHistory, setSelectedVendorForHistory] = useState(null);
  const [historyLoading, setHistoryLoading] = useState(false);
  const [historyData, setHistoryData] = useState([]);
  const [loadingTender, setLoadingTender] = useState(false);
  const [loadingQuotations, setLoadingQuotations] = useState(false);
  const [hasComparisonSheet, setHasComparisonSheet] = useState(false);
  const [approvedTenderIdsWithTitle, setApprovedTenderIdsWithTitle] = useState([]);
  const [bidTypeFilter, setBidTypeFilter] = useState('ALL');

  // ── New: Full evaluation flow state ──
  const [evalStatus, setEvalStatus] = useState(null); // full TenderEvaluationStatusDto
  const [evalLoading, setEvalLoading] = useState(false);
  const [initiating, setInitiating] = useState(false);

  // Technical eval modal (double bid)
  const [techModal, setTechModal] = useState(false);
  const [techVendor, setTechVendor] = useState(null);
  const [techDecision, setTechDecision] = useState('APPROVED');
  const [techRemarks, setTechRemarks] = useState('');

  // Select vendor modal
  const [selectVendorModal, setSelectVendorModal] = useState(false);
  const [selectedApprovedVendorId, setSelectedApprovedVendorId] = useState('');
  const [selectVendorRemarks, setSelectVendorRemarks] = useState('');

  // Approval modals
  const [approvalModal, setApprovalModal] = useState(false);
  const [approvalType, setApprovalType] = useState(''); // 'indentor-purchase' | 'spo' | 'director'
  const [approvalDecision, setApprovalDecision] = useState('APPROVED');
  const [approvalRemarks, setApprovalRemarks] = useState('');

  // Chairman modals
  const [chairmanModal, setChairmanModal] = useState(false);
  const [chairmanDecision, setChairmanDecision] = useState('APPROVED');
  const [chairmanRemarks, setChairmanRemarks] = useState('');
  const [chairmanIsOverride, setChairmanIsOverride] = useState(false);

  // Expert assignment modal
  const [expertModal, setExpertModal] = useState(false);
  const [expertUserId, setExpertUserId] = useState('');
  const [expertName, setExpertName] = useState('');

  // Committee vote modal
  const [voteModal, setVoteModal] = useState(false);
  const [myVote, setMyVote] = useState('APPROVED');
  const [myVoteRemarks, setMyVoteRemarks] = useState('');

  // ── Seek Clarification modal (all approvers) ──
  const [clarificationModal, setClarificationModal] = useState(false);
  const [clarifRequestedByRole, setClarifRequestedByRole] = useState('');
  const [clarifTarget, setClarifTarget] = useState('VENDOR');
  const [clarifTargetUserId, setClarifTargetUserId] = useState('');
  const [clarifTargetUserName, setClarifTargetUserName] = useState('');
  const [clarifRemarks, setClarifRemarks] = useState('');

  // ── Respond Clarification modal ──
  const [respondModal, setRespondModal] = useState(false);
  const [respondText, setRespondText] = useState('');
  const [respondRole, setRespondRole] = useState('');

  // ── Director forms ad-hoc committee (>1CR) ──
  const [committeeFormModal, setCommitteeFormModal] = useState(false);
  const [adHocChairmanId, setAdHocChairmanId] = useState('');
  const [adHocChairmanName, setAdHocChairmanName] = useState('');
  const [adHocCoChairmanId, setAdHocCoChairmanId] = useState('');
  const [adHocCoChairmanName, setAdHocCoChairmanName] = useState('');
  const [adHocMembers, setAdHocMembers] = useState([{ userId: '', memberName: '', designation: '' }]);

  // ── Clarification history ──
  const [clarificationHistory, setClarificationHistory] = useState([]);
  const [showClarifHistory, setShowClarifHistory] = useState(false);
  const [clarifHistoryLoading, setClarifHistoryLoading] = useState(false);
  // vendor-specific seek clarification
  const [clarifTargetVendorId, setClarifTargetVendorId] = useState('');

  const tenderId = formData.tenderId;
  const bidType = formData.bidType;
  const role = useSelector(state => state.auth.role);
  const normalizedRole = (role || '').toLowerCase().trim();
  const isBelow10L = formData.totalValue != null && formData.totalValue < 1000000;
  const hasMultipleIndents = formData.indentNumber > 1;
  const isPurchasePersonnelRole = normalizedRole === 'purchase personnel' || normalizedRole === 'purchase person';
  const isIndentCreatorRole = normalizedRole === 'indent creator';
  const canTakeAction = isPurchasePersonnelRole || (isIndentCreatorRole && isBelow10L && !hasMultipleIndents);

// SPO can act only if comparison sheet is already submitted
const isSpoRole = normalizedRole === 'store purchase officer';
const canSpoAct = isSpoRole && hasComparisonSheet;

const showEvaluationSection = true;

// Whether the evaluation is a double-bid (from the eval status, not form data, for accuracy)
const isDoubleBidEval = evalStatus?.bidType === 'DOUBLE_BID';
const isMultipleIndentEval = evalStatus?.indentCategory === 'MULTIPLE_INDENT';

// Financial phase: financialBidPhase=true AND status is PENDING_FINANCIAL or beyond (not PENDING_FINANCIAL_SHEET_UPLOAD)
const isFinancialPhase = Boolean(evalStatus?.financialBidPhase) &&
  evalStatus?.evaluationStatus !== 'PENDING_FINANCIAL_SHEET_UPLOAD';

// Accept/Reject/Seek-Clarification in quotation table:
//   Technical phase: Indentor (single-indent) or PP (multiple-indent), under 10L only
//   Financial phase: same roles can act again for financial evaluation
const showActionButtons =
  ((isIndentCreatorRole && isBelow10L && !isMultipleIndentEval && evalStatus !== null) ||
   (isPurchasePersonnelRole && isBelow10L && isMultipleIndentEval && evalStatus !== null)) ||
  (isFinancialPhase && evalStatus !== null &&
    (evalStatus.evaluationStatus === 'PENDING_FINANCIAL') &&
    (isIndentCreatorRole || isPurchasePersonnelRole));

const canPerformActions = showActionButtons;

  useEffect(() => {
    const fetchApprovedTenders = async () => {
      try {
        // Indent Creator should only see tenders for their own indents
        const params = isIndentCreatorRole ? { userId } : {};
        const response = await axios.get("/api/tender-requests/approvedTender/TenderEvaluation", { params });
        setApprovedTenderIdsWithTitle(response.data.responseData);
      } catch (error) {
        console.error("Error fetching approved tenders:", error);
      }
    };

    fetchApprovedTenders();
  }, [role, userId]);

  const fetchQuotationsAndPending = async (tid) => {
  setLoadingQuotations(true);
   console.log("Inside fetchQuotationsAndPending:", tenderId);
  try {
    const [qRes, nsvRes] = await Promise.all([
      axios.get(`/api/vendor-quotation`, {
        params: { userRole: role , tenderId: tid} 
      }),
      axios.get(`/api/vendor-quotation/NotSubmitVendors`, {params:{tenderId:tid}})
    ]);
    setQuotationData(qRes.data?.responseData || []);
    setNotSubmittedVendors(nsvRes.data?.responseData || []);
  } catch (err) {
    message.error('Failed to fetch quotation-related data');
  } finally {
    setLoadingQuotations(false);
  }
};

const handleSearchTender = async () => {
  if (!tenderId || !tenderId.trim()) {
    return message.warning('Please enter Tender ID to search');
  }
  try {
    setLoadingTender(true);

     const baseQuotationResp = await axios.get(`/api/vendor-quotation`, {
      params: { userRole: role , tenderId : tenderId }
    });
    const baseQuotationList = baseQuotationResp.data?.responseData || [];
    setQuotationData(baseQuotationList);

    // flow for Store Purchase Officer: require comparison sheet
    if (isSpoRole) {
      const compResp = await axios.get(`/api/vendor-quotation/getAllVendorQuotations`,{params :{tenderId: tenderId}});
      const { vendor = [], uploadQualifiedVendorsFileName } = compResp.data?.responseData || {};

     /* if (!uploadQualifiedVendorsFileName) {
        // Block SPO until comparison statement exists
        setHasComparisonSheet(false);
        setQuotationData([]); // hide stale
        message.warning("Comparison Statement not submitted yet by Indentor / Purchase personnel.");
        return;
      }*/

      // Comparison sheet exists: enable SPO actions
      setHasComparisonSheet(true);
     // setQuotationData(vendor); // override with richer data (status/remarks)
      setFormData(prev => ({
        ...prev,
        comparationStatementFileName: [uploadQualifiedVendorsFileName],
      }));
      const approvedRes = await axios.get('/getApprovedTenderId', {
        params: { tenderId: tenderId.trim() }
      });
      const approved = approvedRes.data?.responseData || approvedRes.data;
      if (approved) {
        setFormData(prev => ({
          ...prev,
          tenderId: approved.tenderId || prev.tenderId,
          bidType: approved.bidType || prev.bidType,
          totalValue: approved.totalValue || prev.totalValue,
          indentNumber: approved.indentNumber || prev.indentNumber,
          modeOfProcurement: approved.modeOfProcurement || prev.modeOfProcurement || '',
        }));
      }
      await fetchQuotationsAndPending(tenderId);
      return;
    }

    // 3. Non-SPO path: Purchase Personal / Indentor
    const approvedRes = await axios.get('/getApprovedTenderId', {
      params: { tenderId: tenderId.trim()}
    });
    const approved = approvedRes.data?.responseData || approvedRes.data;
     
    if (!approved) {
      message.warning('No approved tender found for given ID');
      return;
    }

    const updatedFormData = {
      tenderId: approved.tenderId || tenderId.trim(),
      bidType: approved.bidType || '',
      totalValue: approved.totalValue || null,
      indentNumber: approved.indentNumber || 0,
      modeOfProcurement: approved.modeOfProcurement || '',
    };
    setFormData(updatedFormData);

    await fetchQuotationsAndPending(updatedFormData.tenderId);
    console.log("Calling fetchQuotationsAndPending with ID:", updatedFormData.tenderId);

    const isBelow10LLocal = updatedFormData.totalValue < 1000000;
    const hasMultipleIndentsLocal = updatedFormData.indentNumber > 1;

    const isAuthorized =
      isPurchasePersonnelRole ||
      (isIndentCreatorRole && !hasMultipleIndentsLocal);


    if (!isAuthorized) {
      setQuotationData([]);
      setNotSubmittedVendors([]);
      message.warning("You don't have permission to evaluate this tender based on your role and tender details.");
      return;
    }

   
  } catch (err) {
   // message.error('Failed to fetch approved tender');
  } finally {
    setLoadingTender(false);
  }
};
// quotation fetch is triggered via useEffect[tenderId] below — no separate effect needed


  const fetchVendorHistory = async (vendorId) => {
    try {
      setHistoryLoading(true);
      const res = await axios.get(`/api/vendor-quotation/vendorHistory`, {params :{tenderId : tenderId , vendorId :vendorId}});
      setHistoryData(res.data?.responseData || []);
    } catch (error) {
      message.error("Failed to fetch quotation history");
      setHistoryData([]);
    } finally {
      setHistoryLoading(false);
    }
  };
/*
 const handleChange = (key, value) => {
  if (key === "comparationStatementFileName" && Array.isArray(value) && value.length > 0) {
    const fileName = value[0]?.name || ""; // Only extract file name
    setFormData(prev => ({
      ...prev,
      [key]: [fileName] // Still store as array (since your code expects an array)
    }));
  } else {
    setFormData(prev => ({ ...prev, [key]: value }));
  }
};*/
const handleChange = (key, value) => {
  setFormData(prev => ({ ...prev, [key]: value }));
};


  const handleSubmit = async () => {
  /*if (!formData.comparationStatementFileName || formData.comparationStatementFileName.length === 0) {
    return message.warning("Please upload the Comparison Statement file.");
  }*/

  const payload = {
    tenderId: tenderId,
    uploadQualifiedVendorsFileName: formData.comparationStatementFileName?.[0] || null,
    createdBy: userId, 
    fileType: "Tender",
  };

  try {
    setIsSubmitting(true);
    await axios.post(`/api/tender-evaluation`, payload);
    message.success("Tender evaluation submitted successfully");
  } catch (error) {
    console.error(error);
    message.error("Failed to submit tender evaluation");
  } finally {
    setIsSubmitting(false);
  }
};


  const handleReject = async (record) => {
  if (!rejectComment.trim()) {
    return message.warning("Please enter a rejection comment.");
  }
  try {
    await axios.post('/api/tender-evaluation/vendor/indentor-decision',
      { decision: "REJECTED", remarks: rejectComment, userId },
      { params: { tenderId, vendorId: record.vendorId } }
    );
    message.success(`Vendor ${record.vendorId} rejected`);
    setRejectComment('');
    setRejectedVendorId(null);
    await fetchQuotationsAndPending(tenderId);
    await fetchEvalStatus(tenderId);
  } catch (err) {
    message.error(err?.response?.data?.message || "Failed to reject vendor");
  }
};


  const handleChangeRequest = async (record) => {
    if (!rejectComment.trim()) {
      return message.warning("Please enter a change request comment.");
    }
    try {
      await axios.post('/api/tender-evaluation/seek-clarification', {
        requestedByRole: isIndentCreatorRole ? 'INDENTOR' : 'PURCHASE_PERSONNEL',
        requestedByUserId: userId,
        clarificationTarget: 'VENDOR',
        targetVendorId: record.vendorId,
        remarks: rejectComment,
      }, { params: { tenderId } });
      message.success(`Clarification request sent to vendor ${record.vendorId}`);
      setRejectComment('');
      setRejectedVendorId(null);
      await fetchQuotationsAndPending(tenderId);
      await fetchEvalStatus(tenderId);
    } catch (err) {
      message.error(err?.response?.data?.message || "Failed to send change request");
    }
  };

  const handleAccept = async (record) => {
    try {
      await axios.post('/api/tender-evaluation/vendor/indentor-decision',
        { decision: "ACCEPTED", remarks: "Accepted", userId },
        { params: { tenderId, vendorId: record.vendorId } }
      );
      message.success(`Vendor ${record.vendorId} accepted`);
      await fetchQuotationsAndPending(tenderId);
      await fetchEvalStatus(tenderId);
    } catch (err) {
      message.error(err?.response?.data?.message || "Failed to accept vendor quotation");
    }
  };

 const handleSpoReview = async (record, actionType) => {
  if (['CHANGE_REQUEST_TO_INTENTOR', 'REJECT'].includes(actionType) && !rejectComment.trim()) {
    return message.warning(actionType === 'REJECT' ? "Please enter a rejection comment." : "Please enter a change request comment.");
  }

  try {
    if (actionType === 'ACCEPT') {
      await axios.post('/api/tender-evaluation/vendor/spo-decision',
        { decision: "ACCEPTED", remarks: "SPO Accepted", userId },
        { params: { tenderId, vendorId: record.vendorId } }
      );
    } else if (actionType === 'REJECT') {
      await axios.post('/api/tender-evaluation/vendor/spo-decision',
        { decision: "REJECTED", remarks: rejectComment, userId },
        { params: { tenderId, vendorId: record.vendorId } }
      );
    } else if (actionType === 'CHANGE_REQUEST_TO_INTENTOR') {
      await axios.post('/api/tender-evaluation/seek-clarification', {
        requestedByRole: 'SPO',
        requestedByUserId: userId,
        clarificationTarget: 'INDENTOR',
        remarks: rejectComment,
      }, { params: { tenderId } });
    }
    message.success(`SPO action '${actionType}' performed for vendor ${record.vendorId}`);
    setRejectComment('');
    setRejectedVendorId(null);
    await fetchQuotationsAndPending(tenderId);
    await fetchEvalStatus(tenderId);
  } catch (err) {
    message.error(err?.response?.data?.message || "Failed to perform SPO review action");
  }
};
// ─── New evaluation flow handlers ────────────────────────────────

const fetchEvalStatus = async (tid) => {
  if (!tid) return;
  try {
    setEvalLoading(true);
    const res = await axios.get('/api/tender-evaluation/status', {
      params: { tenderId: tid, userId, role }
    });
    setEvalStatus(res.data?.responseData || null);
  } catch (e) {
    // not yet initiated – silently ignore
    setEvalStatus(null);
  } finally {
    setEvalLoading(false);
  }
};

const handleInitiateEvaluation = async () => {
  if (!tenderId) return message.warning('Select a tender first.');
  if (!formData.comparationStatementFileName?.[0]) {
    return message.warning('Please upload the Comparison Statement before initiating evaluation.');
  }
  try {
    setInitiating(true);
    // Save comparison sheet: try PUT (update) first, fall back to POST (create)
    if (formData.comparationStatementFileName?.[0]) {
      try {
        await axios.put('/api/tender-evaluation', {
          uploadQualifiedVendorsFileName: formData.comparationStatementFileName[0],
          updatedBy: String(userId),
        }, { params: { tenderId } });
      } catch {
        await axios.post('/api/tender-evaluation', {
          tenderId,
          uploadQualifiedVendorsFileName: formData.comparationStatementFileName[0],
          createdBy: userId,
          fileType: 'Tender',
        });
      }
    }
    const res = await axios.post('/api/tender-evaluation/initiate', null, {
      params: { tenderId, userId }
    });
    setEvalStatus(res.data?.responseData);
    message.success('Evaluation initiated successfully.');
  } catch (e) {
    message.error(e?.response?.data?.responseStatus?.message || 'Failed to initiate evaluation.');
  } finally {
    setInitiating(false);
  }
};

const handleSaveFinancialComparisonSheet = async () => {
  if (!formData.financialComparisionSheetFileName?.[0]) {
    return message.warning('Please select the Financial Comparison Statement file first.');
  }
  try {
    await axios.put('/api/tender-evaluation', {
      uploadCommeriallyQualifiedVendorsFileName: formData.financialComparisionSheetFileName[0],
      updatedBy: String(userId),
    }, { params: { tenderId } });
    await fetchEvalStatus(tenderId);
    await handleSearchTender();
    message.success('Financial Comparison Sheet uploaded successfully.');
  } catch (e) {
    message.error('Failed to upload Financial Comparison Sheet.');
  }
};

const handleConfirmByIndentor = async () => {
  if (!tenderId) return message.warning('Select a tender first.');
  try {
    setIsSubmitting(true);
    const res = await axios.post('/api/tender-evaluation/confirm-by-indentor', {
      indentorUserId: userId,
    }, { params: { tenderId } });
    setEvalStatus(res.data?.responseData);
    message.success('Evaluation confirmed. Forwarded to Store Purchase Officer for approval.');
  } catch (e) {
    message.error(e?.response?.data?.responseStatus?.message || 'Failed to confirm evaluation.');
  } finally {
    setIsSubmitting(false);
  }
};

const handleSaveTechnicalEval = async () => {
  if (!techVendor) return;
  try {
    await axios.put('/api/tender-evaluation/technical', {
      vendorId: techVendor.vendorId,
      decision: techDecision,
      remarks: techRemarks,
      evaluatedByUserId: userId,
    }, { params: { tenderId, vendorId: techVendor.vendorId } });
    message.success('Technical evaluation saved.');
    setTechModal(false);
    setTechRemarks('');
    await fetchEvalStatus(tenderId);
  } catch (e) {
    message.error('Failed to save technical evaluation.');
  }
};

const handleSelectVendorSubmit = async () => {
  if (!selectedApprovedVendorId) return message.warning('Select a vendor.');
  try {
    await axios.post('/api/tender-evaluation/select-vendor', {
      vendorId: selectedApprovedVendorId,
      remarks: selectVendorRemarks,
      actionByUserId: userId,
    }, { params: { tenderId } });
    message.success('Approved vendor selected. Forwarded for approval.');
    setSelectVendorModal(false);
    await fetchEvalStatus(tenderId);
  } catch (e) {
    message.error('Failed to select vendor.');
  }
};

const handleApprovalSubmit = async () => {
  try {
    const body = { decision: approvalDecision, remarks: approvalRemarks };
    if (approvalType === 'indentor-purchase') {
      body.approverUserId = userId;
      await axios.post('/api/tender-evaluation/approve/indentor-purchase', body, { params: { tenderId } });
    } else if (approvalType === 'spo') {
      body.spoUserId = userId;
      await axios.post('/api/tender-evaluation/approve/spo', body, { params: { tenderId } });
    } else if (approvalType === 'director') {
      body.directorUserId = userId;
      await axios.post('/api/tender-evaluation/director/approve', body, { params: { tenderId } });
    }
    message.success(`${approvalDecision === 'APPROVED' ? 'Approved' : 'Rejected'} successfully.`);
    setApprovalModal(false);
    setApprovalRemarks('');
    await fetchEvalStatus(tenderId);
  } catch (e) {
    message.error('Failed to submit approval.');
  }
};

const handleCastVote = async () => {
  try {
    await axios.post('/api/tender-evaluation/committee/vote', {
      vote: myVote,
      remarks: myVoteRemarks,
      committeeUserId: userId,
    }, { params: { tenderId } });
    message.success('Vote cast successfully.');
    setVoteModal(false);
    setMyVoteRemarks('');
    await fetchEvalStatus(tenderId);
  } catch (e) {
    message.error('Failed to cast vote.');
  }
};

const handleAssignExpertSubmit = async () => {
  if (!expertUserId || !expertName) return message.warning('Enter expert details.');
  try {
    await axios.post('/api/tender-evaluation/committee/expert', {
      expertUserId: parseInt(expertUserId),
      expertName,
      chairmanUserId: userId,
    }, { params: { tenderId } });
    message.success('Expert assigned.');
    setExpertModal(false);
    await fetchEvalStatus(tenderId);
  } catch (e) {
    message.error('Failed to assign expert.');
  }
};

const handleChairmanDecisionSubmit = async () => {
  if (!chairmanRemarks) return message.warning('Please enter remarks.');
  try {
    await axios.post('/api/tender-evaluation/committee/chairman-decision', {
      decision: chairmanDecision,
      remarks: chairmanRemarks,
      chairmanUserId: userId,
      isOverride: chairmanIsOverride,
      overrideReason: chairmanIsOverride ? chairmanRemarks : null,
    }, { params: { tenderId } });
    message.success('Chairman decision submitted.');
    setChairmanModal(false);
    await fetchEvalStatus(tenderId);
  } catch (e) {
    message.error('Failed to submit chairman decision.');
  }
};

// fetchEvalStatus + fetchClarificationHistory are called in the unified useEffect[tenderId] below

// ── Seek Clarification ────────────────────────────────────────────
const openClarificationModal = (requestedByRole) => {
  setClarifRequestedByRole(requestedByRole);
  setClarifTarget('VENDOR');
  setClarifTargetVendorId('');
  setClarifTargetUserId('');
  setClarifTargetUserName('');
  setClarifRemarks('');
  setClarificationModal(true);
};

// Per-vendor row: opens clarification modal pre-targeted at that specific vendor
const openVendorClarificationModal = (vendorId, requestedByRole) => {
  setClarifRequestedByRole(requestedByRole || (isIndentCreatorRole ? 'INDENTOR' : 'PURCHASE_PERSONNEL'));
  setClarifTarget('VENDOR');
  setClarifTargetVendorId(vendorId);
  setClarifTargetUserId('');
  setClarifTargetUserName('');
  setClarifRemarks('');
  setClarificationModal(true);
};

// SPO-specific: opens clarification modal where SPO can choose INDENTOR or VENDOR
const openRevisionModal = () => {
  setClarifRequestedByRole('SPO');
  setClarifTarget('INDENTOR');
  setClarifTargetVendorId('');
  setClarifTargetUserId('');
  setClarifTargetUserName('');
  setClarifRemarks('');
  setClarificationModal(true);
};

const handleSeekClarification = async () => {
  if (!clarifRemarks.trim()) return message.warning('Please enter clarification remarks.');
  try {
    await axios.post('/api/tender-evaluation/seek-clarification', {
      requestedByRole: clarifRequestedByRole,
      requestedByUserId: userId,
      clarificationTarget: clarifTarget,
      targetVendorId: clarifTargetVendorId || null,
      targetUserId: clarifTargetUserId ? parseInt(clarifTargetUserId) : null,
      targetUserName: clarifTargetUserName || null,
      remarks: clarifRemarks,
    }, { params: { tenderId } });
    message.success('Clarification request sent successfully.');
    setClarificationModal(false);
    setClarifRemarks('');
    setClarifTargetVendorId('');
    await fetchEvalStatus(tenderId);
    await fetchClarificationHistory(tenderId);
    await fetchQuotationsAndPending(tenderId);
  } catch (e) {
    message.error(e?.response?.data?.responseStatus?.message || 'Failed to send clarification request.');
  }
};

const handleRespondClarification = async () => {
  if (!respondText.trim()) return message.warning('Please enter your response.');
  try {
    await axios.post('/api/tender-evaluation/respond-clarification', {
      respondedByRole: respondRole,
      respondedById: String(userId),
      responseText: respondText,
      responseFileName: null,
    }, { params: { tenderId } });
    message.success('Clarification response submitted.');
    setRespondModal(false);
    setRespondText('');
    await fetchEvalStatus(tenderId);
    await fetchClarificationHistory(tenderId);
  } catch (e) {
    message.error(e?.response?.data?.responseStatus?.message || 'Failed to submit response.');
  }
};

const handleAcknowledgeVendorClarification = async () => {
  try {
    const respondedByRole =
      isIndentCreatorRole         ? 'INDENTOR'
      : isSpoRole ? 'SPO'
      : role === 'Committee Chairman'   ? 'CHAIRMAN'
      : role === 'Director'             ? 'DIRECTOR'
      : 'PURCHASE_PERSONNEL';
    await axios.post('/api/tender-evaluation/respond-clarification', {
      respondedByRole,
      respondedById: String(userId),
      responseText: 'Vendor clarification acknowledged.',
      responseFileName: null,
    }, { params: { tenderId } });
    message.success('Vendor clarification acknowledged. Evaluation resumed.');
    await fetchEvalStatus(tenderId);
    await fetchQuotationsAndPending(tenderId);
  } catch (e) {
    message.error(e?.response?.data?.responseStatus?.message || 'Failed to acknowledge clarification.');
  }
};

// ── Director Forms Ad-Hoc Committee ──────────────────────────────
const handleDirectorFormCommittee = async () => {
  if (!adHocChairmanId || !adHocChairmanName) return message.warning('Chairman details are required.');
  const validMembers = adHocMembers.filter(m => m.userId && m.memberName);
  try {
    await axios.post('/api/tender-evaluation/director/form-committee', {
      directorUserId: userId,
      chairmanUserId: parseInt(adHocChairmanId),
      chairmanName: adHocChairmanName,
      coChairmanUserId: adHocCoChairmanId ? parseInt(adHocCoChairmanId) : null,
      coChairmanName: adHocCoChairmanName || null,
      members: validMembers.map(m => ({
        userId: parseInt(m.userId),
        memberName: m.memberName,
        designation: m.designation || '',
      })),
    }, { params: { tenderId } });
    message.success('Ad-hoc committee formed. Committee members can now cast their votes.');
    setCommitteeFormModal(false);
    await fetchEvalStatus(tenderId);
  } catch (e) {
    message.error(e?.response?.data?.responseStatus?.message || 'Failed to form committee.');
  }
};

// ── Clarification history ─────────────────────────────────────────
const fetchClarificationHistory = async (tid) => {
  if (!tid) return;
  setClarifHistoryLoading(true);
  try {
    const res = await axios.get('/api/tender-evaluation/clarification-history', { params: { tenderId: tid } });
    setClarificationHistory(res.data?.responseData || []);
  } catch (e) {
    setClarificationHistory([]);
  } finally {
    setClarifHistoryLoading(false);
  }
};

// ─────────────────────────────────────────────────────────────────

// Use evalStatus.bidType as primary source (reliable after initiation),
// fall back to formData.bidType (available before initiation from tender data).
const isDouble = evalStatus?.bidType === 'DOUBLE_BID'
  || (!evalStatus && (formData.bidType || '').toLowerCase().includes('double'));
const isSingle = evalStatus?.bidType === 'SINGLE_BID'
  || (!evalStatus && (formData.bidType || '').toLowerCase().includes('single'));


const priceBidColumn = {
  title: 'Financial Document',
  dataIndex: 'priceBidFileName',
  key: 'priceBidFileName',
  render: (fileName, record) => {
    const evalVendor = evalStatus?.vendors?.find(v => v.vendorId === record.vendorId);
    if (isDoubleBidEval && evalVendor && !evalVendor.financialBidVisible) {
      return <span style={{ color: '#999' }}>Hidden (Technical phase)</span>;
    }
    if (!fileName) return 'No File';
    return (
      <a
        href={`${baseURL}/file/view/Tender/${fileName}`}
        target="_blank"
        rel="noopener noreferrer"
      >
        View
      </a>
    );
  },
};

const priceBidColumnForSingleBid = {
  title: 'Financial Document',
  dataIndex: 'priceBidFileName',
  key: 'priceBidFileName',
  render: (fileName, record) => {
    if (!fileName) return 'No File';
    return (
      <a
        href={`${baseURL}/file/view/Tender/${fileName}`}
        target="_blank"
        rel="noopener noreferrer"
      >
        View
      </a>
    );
  },
};

const showVendorResponse = quotationData.some(item => item.vendorResponse);
const showClarificationFile = quotationData.some(item => item.clarificationFileName);

const baseColumns = [
  {
    title: 'Vendor ID',
    dataIndex: 'vendorId',
    key: 'vendorId',
    render: (vid) => (
  <a
    style={{ color: '#1890ff' }}
    onClick={() => {
      setSelectedVendorForHistory(vid);
      setHistoryVisible(true);
    }}
  >
    {vid}
  </a>
)

  },
    {
      title: 'Vendor Name',
      dataIndex: 'vendorName',
      key: 'vendorName',
    },
 /* {
    title: 'Quotation File Name',
    dataIndex: 'quotationFileName',
    key: 'quotationFileName',
  },*/
  {
    title: 'Technical Document',
    key: 'view',
    render: (_, record) => (
      record.quotationFileName ? (
        <a
          href={`${baseURL}/file/view/Tender/${record.quotationFileName}`}
          target="_blank"
          rel="noopener noreferrer"
        >
          View
        </a>
      ) : 'No File'
    )
  },
   ...(showVendorResponse
    ? [
        {
          title: 'Vendor Response',
          dataIndex: 'vendorResponse',
          key: 'vendorResponse',
        },
      ]
    : []),
  ...(showClarificationFile
    ? [
        {
          title: 'Clarification File',
          dataIndex: 'clarificationFileName',
          key: 'clarificationFileName',
          render: (file) =>
            file ? (
              <a
        href={`${baseURL}/file/view/Tender/${file}`}
        target="_blank"
        rel="noopener noreferrer"
      >
        View
      </a>
            ) : (
              'N/A'
            ),
        },
      ]
    : []),
  ...(!isDouble ? [priceBidColumnForSingleBid] : []),

 


];

let columns = [];

if (isSpoRole) {
  columns = [
    ...baseColumns,
  /* {
          title: `${role} Status`,
          key: 'status',
          dataIndex: 'status',
          render: (status) => status || 'N/A',
        },*/
         {
          title: 'Indentor Status',
          key: 'indentorStatus',
          dataIndex: 'indentorStatus',
          render: (indentorStatus, record) => {
            if (indentorStatus === 'CHANGE_REQUESTED') return 'Pending Clarification';
            if (indentorStatus === 'REJECTED' || indentorStatus === 'Rejected') return (
              <Popover
                content={record.remarks ? <span>{record.remarks}</span> : <span style={{ color: '#888' }}>No reason provided</span>}
                title="Rejection Reason"
              >
                <span style={{ color: 'red', cursor: 'help', borderBottom: '1px dashed red' }}>Rejected ⓘ</span>
              </Popover>
            );
            if (indentorStatus === 'Completed' || indentorStatus === 'ACCEPTED') return <span style={{ color: 'green' }}>Accepted</span>;
            return indentorStatus || 'N/A';
          }
        },
        {
          title: `${role} Status`,
          key: 'sopStatus',
          dataIndex: 'sopStatus',
          render: (sopStatus) =>{
            if (sopStatus === 'CHANGE_REQUESTED_TO_INTENTOR') return 'Pending Clarification';
            if (sopStatus === 'REJECTED' || sopStatus === 'Rejected') return 'Rejected';
            if (sopStatus === 'ACCEPTED' || sopStatus === 'Completed') return 'Accepted';
            return sopStatus || 'Pending';
          }
        },
        {
          title: 'Qualification Status',
          key: 'qualStatus',
          dataIndex: 'sopStatus',
          render: (sopStatus) =>{
            if (sopStatus === 'CHANGE_REQUESTED_TO_INTENTOR') return 'Pending Clarification';
            if (sopStatus === 'REJECTED' || sopStatus === 'Rejected') return 'Disqualified';
            if (sopStatus === 'ACCEPTED' || sopStatus === 'Completed') return 'Qualified';
            return sopStatus || 'Pending';
          }
        },
          

      /*   ...(formData.bidType === 'Double'
    ? [
        {
          title: 'Price Bid',
          dataIndex: 'priceBidFileName',
          key: 'priceBidFileName',
          render: (fileName, record) => {
            if (record.status !== 'Completed') return null; // only show when Completed
            if (!fileName) return 'No File';
            return (
             <a
          href={`${baseURL}/file/view/Tender/${record.priceBidFileName}`}
          target="_blank"
          rel="noopener noreferrer"
        >
          View
        </a>
            );
          },
        },
      ]
    : [])*/,
       /*  {
          title: `Indentor Status`,
          key: 'indentorStatus',
          dataIndex: 'indentorStatus',
          render: (indentorStatus) => indentorStatus || 'N/A',
        },*/
     /*   {
          title: 'Remarks',
          key: 'remarks',
          dataIndex: 'remarks',
          render: (remarks) => remarks || 'N/A',
        },*/
    /*{
      title: `${role} Status`,
      key: 'spoStatus',
      dataIndex: 'spoStatus',
      render: (spoStatus) => spoStatus || 'N/A',
    },*/
  /*  {
      title: 'SPO Remarks',
      key: 'spoRemarks',
      dataIndex: 'spoRemarks',
      render: (r) => r || '-',
    }*/,
    ...(isFinancialPhase ? [
    {
      title: 'Indentor/PP Financial Status',
      key: 'financialIndentorStatus',
      dataIndex: 'financialIndentorStatus',
      render: (val) => {
        if (val === 'ACCEPTED' || val === 'Completed') return <Tag color="green">Accepted</Tag>;
        if (val === 'REJECTED' || val === 'Rejected') return <Tag color="red">Rejected</Tag>;
        if (val === 'CHANGE_REQUESTED') return <Tag color="orange">Pending Clarification</Tag>;
        return val || <Tag>Pending</Tag>;
      }
    },
    {
      title: 'SPO Financial Status',
      key: 'financialSpoStatus',
      dataIndex: 'financialSpoStatus',
      render: (val) => {
        if (val === 'ACCEPTED' || val === 'Completed') return <Tag color="green">Qualified</Tag>;
        if (val === 'REJECTED' || val === 'Rejected') return <Tag color="red">Disqualified</Tag>;
        if (val === 'CHANGE_REQUESTED_TO_INTENTOR') return <Tag color="orange">Pending Clarification</Tag>;
        return val || <Tag>Pending</Tag>;
      }
    },
    ] : []),
   {
  title: 'SPO Actions',
  key: 'spoActions',
  render: (_, record) => {
    const finPhase = isFinancialPhase;
    const indStatus = finPhase ? record.financialIndentorStatus : record.indentorStatus;
    const spStatus = finPhase ? record.financialSpoStatus : record.sopStatus;
    const spoCanAct = evalStatus?.evaluationStatus === 'PENDING_SPO_APPROVAL'
      && indStatus === 'ACCEPTED'
      && !spStatus
      && record.status !== 'CHANGE_REQUESTED';
    const pendingToIndentor = record.changeRequestToIndentor;

    return (
      <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap' }}>
        <Button
          size="small"
          disabled={!spoCanAct || spStatus === 'ACCEPTED'}
          onClick={() => handleSpoReview(record, 'ACCEPT')}
        >
          {spStatus === 'ACCEPTED' ? 'Accepted' : 'SPO Accept'}
        </Button>
<Popover
  content={
    <div style={{ padding: 12 }}>
      <Input.TextArea
        placeholder="Enter reject reason"
        rows={3}
        value={rejectedVendorId === record.vendorId ? rejectComment : ''}
        onChange={(e) => {
          setRejectedVendorId(record.vendorId);
          setRejectComment(e.target.value);
        }}
      />
      <Button
        type="primary"
        disabled={!spoCanAct || spStatus === 'REJECTED'}
        onClick={() => handleSpoReview(record, 'REJECT')}
        style={{ marginTop: 8 }}
      >
        Submit
      </Button>
    </div>
  }
  title="SPO Reject Reason"
  trigger="click"
>
  <Button
    size="small"
    style={{ color: 'red' }}
    disabled={!spoCanAct || spStatus === 'REJECTED'}
  >
    {spStatus === 'REJECTED' ? 'Rejected' : 'SPO Reject'}
  </Button>
</Popover>

        <Popover
          content={
            <div style={{ padding: 12 }}>
              <Input.TextArea
                placeholder="Enter change request to Indentor"
                rows={3}
                value={rejectedVendorId === record.vendorId ? rejectComment : ''}
                onChange={(e) => {
                  setRejectedVendorId(record.vendorId);
                  setRejectComment(e.target.value);
                }}
              />
              <Button
                type="primary"
                disabled={!spoCanAct }
                onClick={() => handleSpoReview(record, 'CHANGE_REQUEST_TO_INTENTOR')}
                style={{ marginTop: 8 }}
              >
                Submit
              </Button>
            </div>
          }
          title="Seek Revision"
          trigger="click"
        >
          <Button size="small" style={{ color: '#fa8c16' }}>
            {pendingToIndentor ? 'Change Requested' : 'Seek Revision'}
          </Button>
        </Popover>
      </div>
    );
  }
}

  ];
} else {
  columns = [
    ...baseColumns,
    /*{
      title: 'Status',
      key: 'status',
      dataIndex: 'status',
      render: (status) => status || 'N/A',
    },
     {
      title: `${role} Status`,
      key: 'indentorStatus',
      dataIndex: 'indentorStatus',
      render: (indentorStatus) => indentorStatus || 'N/A',
    },
     {
      title: `Store Purchase Officer Status`,
      key: 'sopStatus',
      dataIndex: 'sopStatus',
      render: (sopStatus) => sopStatus || 'N/A',
    },*/
    {
      title: 'Status',
      key: 'status',
      dataIndex: 'status',
      render: (status) =>
        status === 'CHANGE_REQUESTED' ? 'Pending Clarification' : (status || 'N/A'),
    },
    {
      title: `${role} Status`,
      key: 'indentorStatus',
      dataIndex: 'indentorStatus',
      render: (indentorStatus) => {
        if (indentorStatus === 'CHANGE_REQUESTED') return <Tag color="orange">Pending Clarification</Tag>;
        if (indentorStatus === 'REJECTED' || indentorStatus === 'Rejected') return <Tag color="red">Rejected</Tag>;
        if (indentorStatus === 'ACCEPTED' || indentorStatus === 'Completed') return <Tag color="green">Accepted</Tag>;
        return indentorStatus || 'N/A';
      }
    },
    {
      title: 'Store Purchase Officer Status',
      key: 'sopStatus',
      dataIndex: 'sopStatus',
      render: (sopStatus) => {
        if (sopStatus === 'CHANGE_REQUESTED_TO_INTENTOR') return <Tag color="orange">Pending Clarification</Tag>;
        if (sopStatus === 'REJECTED' || sopStatus === 'Rejected') return <Tag color="red">Disqualified</Tag>;
        if (sopStatus === 'ACCEPTED' || sopStatus === 'Completed') return <Tag color="green">Qualified</Tag>;
        return sopStatus || 'N/A';
      }
    },

    ...(isFinancialPhase ? [
    {
      title: 'Indentor/PP Financial Status',
      key: 'financialIndentorStatus',
      dataIndex: 'financialIndentorStatus',
      render: (val) => {
        if (val === 'ACCEPTED' || val === 'Completed') return <Tag color="green">Accepted</Tag>;
        if (val === 'REJECTED' || val === 'Rejected') return <Tag color="red">Rejected</Tag>;
        if (val === 'CHANGE_REQUESTED') return <Tag color="orange">Pending Clarification</Tag>;
        return val || <Tag>Pending</Tag>;
      }
    },
    {
      title: 'SPO Financial Status',
      key: 'financialSpoStatus',
      dataIndex: 'financialSpoStatus',
      render: (val) => {
        if (val === 'ACCEPTED' || val === 'Completed') return <Tag color="green">Qualified</Tag>;
        if (val === 'REJECTED' || val === 'Rejected') return <Tag color="red">Disqualified</Tag>;
        if (val === 'CHANGE_REQUESTED_TO_INTENTOR') return <Tag color="orange">Pending Clarification</Tag>;
        return val || <Tag>Pending</Tag>;
      }
    },
    ] : []),

  ...(showActionButtons ? [
  {
    title: 'Accept',
    key: 'accept',
    render: (_, record) => {
      const status = isFinancialPhase ? record.financialIndentorStatus : record.indentorStatus;
      return status === 'ACCEPTED' ? (
        <Tag color="green">Accepted</Tag>
      ) : (
        <Button
          onClick={() => handleAccept(record)}
          size="small"
          disabled={
            status === 'ACCEPTED' ||
            status === 'REJECTED' ||
            record.status === 'CHANGE_REQUESTED'
          }
          title={record.status === 'CHANGE_REQUESTED' ? 'Cannot accept while clarification is pending for this vendor' : ''}
        >
          Accept
        </Button>
      );
    },
  },
  {
    title: 'Reject',
    key: 'reject',
    render: (_, record) => {
      const status = isFinancialPhase ? record.financialIndentorStatus : record.indentorStatus;
      return status === 'REJECTED' ? (
        <span style={{ color: 'red' }}>Rejected</span>
      ) : (
        <Popover
          content={
            <div style={{ padding: 12 }}>
              <Input.TextArea
                placeholder="Enter reject comment"
                rows={3}
                value={rejectedVendorId === record.vendorId ? rejectComment : ''}
                onChange={(e) => { setRejectedVendorId(record.vendorId); setRejectComment(e.target.value); }}
              />
              <Button
                type="primary"
                onClick={() => handleReject(record)}
                style={{ marginTop: 8 }}
                disabled={
                  status === 'REJECTED' ||
                  record.status === 'CHANGE_REQUESTED'
                }
              >
                Submit
              </Button>
            </div>
          }
          title="Reject Vendor"
          trigger="click"
        >
          <Button danger type="link" disabled={
            status === 'REJECTED' ||
            record.status === 'CHANGE_REQUESTED'
          }>
            Reject
          </Button>
        </Popover>
      );
    },
  },
  {
    title: 'Seek Clarification',
    key: 'seekClarification',
    render: (_, record) => {
      const status = isFinancialPhase ? record.financialIndentorStatus : record.indentorStatus;
      return record.status === 'CHANGE_REQUESTED' ? (
        <Tag color="orange">Pending</Tag>
      ) : (
        <Button
          type="link"
          style={{ color: '#fa8c16', padding: 0 }}
          disabled={status === 'REJECTED' || record.status === 'CHANGE_REQUESTED'}
          onClick={() => openVendorClarificationModal(record.vendorId)}
        >
          Seek Clarification
        </Button>
      );
    },
  },
  ] : []),

  ];
}
// Unified effect: all tender-level fetches triggered once when tenderId changes
useEffect(() => {
  if (tenderId && tenderId.trim()) {
    handleSearchTender();
    fetchEvalStatus(tenderId);
    fetchClarificationHistory(tenderId);
  }
}, [tenderId]); // eslint-disable-line

  const filteredTenderOptions = bidTypeFilter === 'ALL'
    ? approvedTenderIdsWithTitle
    : approvedTenderIdsWithTitle.filter(t => {
        const bt = (t.bidType || '').toLowerCase();
        if (bidTypeFilter === 'SINGLE_BID') return bt.includes('single');
        if (bidTypeFilter === 'DOUBLE_BID') return bt.includes('double');
        return true;
      });

  const TenderDetails = [
    {
      heading: "Tender Search",
      colCnt: 1,
      fieldList: [
        {
          name: "tenderId",
          label: "Tender Id",
         // type: "search",
          span: 1,
          type: "selectTenderId",
                   // required: true,
                    options: filteredTenderOptions.map((item) => {
                        return {
                            label: item.tenderId + " - " + item.title,
                            value: item.tenderId
                        }
                    }),
                  //  onSearch: () => handleSearchTender(),
        onChange: (selectedValue) => {
          handleChange("tenderId", selectedValue); 
        },
        }
      ]
    },

  ];

  const onFinish = () => {
    if (!tenderId) return message.error('Tender ID required');
    if (!quotationData.length) return message.error('No quotations to submit');
    message.success('Evaluation submitted');
  };

  const evalStatusColor = {
    PENDING_TECHNICAL: 'orange',
    PENDING_FINANCIAL: 'blue',
    PENDING_APPROVAL: 'purple',
    PENDING_SPO_APPROVAL: 'geekblue',
    PENDING_DIRECTOR_APPROVAL: 'volcano',
    PENDING_COMMITTEE_FORMATION: 'magenta',
    PENDING_VENDOR_CLARIFICATION: 'gold',
    PENDING_INDENTOR_CLARIFICATION: 'lime',
    PENDING_MEMBER_REVOTE: 'cyan',
    APPROVED: 'green',
    REJECTED: 'red',
  };

  // True for any tier that uses a committee (STEC-I, STEC-II, or Director ad hoc)
  const isAbove10L = evalStatus?.amountCategory !== 'UNDER_10_LAKH' && evalStatus?.amountCategory != null;
  const isAbove1Crore = evalStatus?.amountCategory === 'ABOVE_1_CRORE';
  const isChairman = role === 'Committee Chairman';
  const isCommitteeMember = role === 'Committee Member';
  const isDirector = role === 'Director';
  // (uses same definition as top-level isFinancialPhase — exclude PENDING_FINANCIAL_SHEET_UPLOAD)

  // Clarification pending states
  const isPendingVendorClarif = evalStatus?.evaluationStatus === 'PENDING_VENDOR_CLARIFICATION';
  const isPendingIndentorClarif = evalStatus?.evaluationStatus === 'PENDING_INDENTOR_CLARIFICATION';
  const isPendingMemberRevote = evalStatus?.evaluationStatus === 'PENDING_MEMBER_REVOTE';
  const isPendingCommitteeFormation = evalStatus?.evaluationStatus === 'PENDING_COMMITTEE_FORMATION';
  const isAnyClarificationPending = isPendingVendorClarif || isPendingIndentorClarif || isPendingMemberRevote;
  // Per-vendor: any vendor still has CHANGE_REQUESTED status
  const anyVendorPendingClarif = quotationData.some(q => q.status === 'CHANGE_REQUESTED');
  // All vendors have been accepted or rejected by Indentor/PP
  const allVendorsDecided = quotationData.length > 0 &&
    quotationData.every(q => {
      const st = isFinancialPhase ? q.financialIndentorStatus : q.indentorStatus;
      return st === 'ACCEPTED' || st === 'REJECTED';
    });
  // All Indentor-accepted vendors must have an SPO decision before SPO can do final approval
  const allVendorsSpoDecided = quotationData.length > 0 &&
    quotationData
      .filter(q => {
        const st = isFinancialPhase ? q.financialIndentorStatus : q.indentorStatus;
        return st === 'ACCEPTED';
      })
      .every(q => {
        const sp = isFinancialPhase ? q.financialSpoStatus : q.sopStatus;
        return sp === 'ACCEPTED' || sp === 'REJECTED';
      });
  // FIX: True only if this logged-in user is actually pre-assigned to this tender's committee.
  // Prevents a STEC-I member from seeing the vote panel for a STEC-II tender (and vice versa).
  const isVotingMember = evalStatus?.committeeVotes?.some(
    v => String(v.committeeUserId) === String(userId)
  );

  // Human-readable committee type label
  const committeeLabel = {
    STEC_I:  'STEC-I  (₹10 Lakh – ₹50 Lakh)',
    STEC_II: 'STEC-II (₹50 Lakh – ₹1 Crore)',
    ADHOC:   'Ad Hoc Committee (Above ₹1 Crore — constituted by Director)',
  }[evalStatus?.committeeType] || '';

  const amountCategoryLabel = {
    UNDER_10_LAKH:              'Below ₹10 Lakhs (Indentor)',
    ABOVE_10_LAKH_UPTO_50_LAKH: '₹10 Lakh – ₹50 Lakh (STEC-I)',
    ABOVE_50_LAKH_UPTO_1_CRORE: '₹50 Lakh – ₹1 Crore (STEC-II)',
    ABOVE_1_CRORE:              'Above ₹1 Crore (Director Ad Hoc)',
  }[evalStatus?.amountCategory] || evalStatus?.amountCategory?.replace(/_/g, ' ');

  return (
    <FormContainer>
      <Heading
        title={
          loadingTender
            ? 'Loading...'
            : `Tender Evaluation for Tender ID: ${formData.tenderId || '-'} and Bid Type: ${formData.bidType || '-'}`
        }
        subTitle={
          formData.totalValue
            ? `Approved Total Value: ₹${Number(formData.totalValue).toLocaleString('en-IN')}`
            : undefined
        }
      />

      <CustomForm
        formData={formData}
        onFinish={onFinish}
        onFinishFailed={() => message.error('Please check required fields')}
      >
        {/* ── Bid Type Filter ── */}
        <div style={{ marginBottom: 12, display: 'flex', alignItems: 'center', gap: 10 }}>
          <span style={{ fontWeight: 500 }}>Filter by Bid Type:</span>
          <Select
            value={bidTypeFilter}
            onChange={(val) => {
              setBidTypeFilter(val);
              setFormData(prev => ({ ...prev, tenderId: '' }));
              setQuotationData([]);
              setEvalStatus(null);
              setNotSubmittedVendors([]);
            }}
            style={{ width: 200 }}
          >
            <Option value="ALL">All Bid Types</Option>
            <Option value="SINGLE_BID">Single Bid</Option>
            <Option value="DOUBLE_BID">Double Bid</Option>
          </Select>
        </div>

        {renderFormFields(TenderDetails, handleChange, formData, '', null, setFormData, null)}

        {/* ── Evaluation Status Banner ── */}
        {tenderId && (
          <div style={{ margin: '12px 0', display: 'flex', alignItems: 'center', gap: 12, flexWrap: 'wrap' }}>
            {evalLoading && <Spin size="small" />}
            {evalStatus?.evaluationStatus ? (
              <>
                <Tag color={evalStatusColor[evalStatus.evaluationStatus] || 'default'} style={{ fontSize: 13 }}>
                  Status: {evalStatus.evaluationStatus?.replace(/_/g, ' ')}
                </Tag>
                <Tag color="cyan">{amountCategoryLabel}</Tag>
                {evalStatus.committeeType && (
                  <Tag color={evalStatus.committeeType === 'STEC_I' ? 'purple' : evalStatus.committeeType === 'STEC_II' ? 'geekblue' : 'volcano'}>
                    {committeeLabel}
                  </Tag>
                )}
                <Tag color="purple">Bid: {evalStatus.bidType?.replace(/_/g, ' ')}</Tag>
                <Tag>{evalStatus.indentCategory?.replace(/_/g, ' ')}</Tag>
                {evalStatus.approvedVendorName && (
                  <Tag color="green">Approved: {evalStatus.approvedVendorName}</Tag>
                )}
                {evalStatus.vendorPortalRegistered && (
                  <Tag color="green">Vendor Portal: Registered</Tag>
                )}
              </>
            ) : !evalLoading && isPurchasePersonnelRole && (
              <Button type="primary" loading={initiating} onClick={handleInitiateEvaluation}>
                Initiate Evaluation
              </Button>
            )}
          </div>
        )}

        {showEvaluationSection && (
          <FormBody layout="vertical" style={{ marginTop: 16 }}>
            {notSubmittedVendors.length > 0 && (
              <Alert
                type="warning"
                showIcon
                message={`Vendors who have NOT submitted quotations: ${notSubmittedVendors.join(', ')}`}
                style={{ marginBottom: 12 }}
              />
            )}
            {(loadingQuotations || loadingTender) && <Spin tip="Loading..." style={{ marginBottom: 12 }} />}

            {/* ── Indentor: waiting for PP to initiate evaluation ── */}
            {isIndentCreatorRole && !evalLoading && evalStatus === null && tenderId && (
              <Alert
                type="info"
                showIcon
                message="Evaluation Not Yet Initiated"
                description="The Purchase Personnel has not yet uploaded the Comparison Sheet and initiated the evaluation. You will be able to Accept, Reject, or Seek Clarification from vendors only after the Purchase Personnel initiates the evaluation."
                style={{ marginBottom: 12 }}
              />
            )}

            {/* ── Main Quotation Table ── */}
            <Table
              dataSource={quotationData}
              columns={columns}
              rowKey="vendorId"
              pagination={false}
            />

            {/* ── Seek Clarification to All Vendors (PP / Indentor) ── */}
            {evalStatus?.evaluationStatus && !['APPROVED', 'REJECTED'].includes(evalStatus.evaluationStatus) &&
              (isPurchasePersonnelRole || isIndentCreatorRole) &&
              quotationData.length > 0 && (
                <div style={{ marginTop: 8, marginBottom: 4 }}>
                  <Button
                    style={{ color: '#fa8c16', borderColor: '#fa8c16' }}
                    disabled={isAnyClarificationPending}
                    onClick={() => {
                      setClarifRequestedByRole(isIndentCreatorRole ? 'INDENTOR' : 'PURCHASE_PERSONNEL');
                      setClarifTarget('ALL_VENDORS');
                      setClarifTargetVendorId('');
                      setClarifTargetUserId('');
                      setClarifTargetUserName('');
                      setClarifRemarks('');
                      setClarificationModal(true);
                    }}
                  >
                    Seek Clarification (All Vendors)
                  </Button>
                </div>
              )}

            {/* ── Clarification History Panel ── */}
            {tenderId && clarificationHistory.length > 0 && (
              <div style={{ marginTop: 16 }}>
                <Button
                  type="link"
                  style={{ padding: 0, fontWeight: 600 }}
                  loading={clarifHistoryLoading}
                  onClick={() => setShowClarifHistory(v => !v)}
                >
                  {showClarifHistory ? 'Hide' : 'Show'} Clarification History ({clarificationHistory.length} round{clarificationHistory.length !== 1 ? 's' : ''})
                </Button>
                {showClarifHistory && (
                  <div style={{ marginTop: 8, border: '1px solid #d9d9d9', borderRadius: 4, padding: 12, background: '#fafafa' }}>
                    {[...clarificationHistory].reverse().map((h, idx) => (
                      <Card
                        key={h.id || idx}
                        size="small"
                        style={{ marginBottom: 8 }}
                        title={`Round ${h.roundNumber} — ${h.clarificationTarget?.replace(/_/g, ' ')} — by ${h.requestedByRole?.replace(/_/g, ' ')}`}
                      >
                        <p><strong>Question:</strong> {h.questionRemarks}</p>
                        <p style={{ color: '#888', fontSize: 12 }}>{h.requestedAt ? new Date(h.requestedAt).toLocaleString() : ''}</p>
                        {h.responseText ? (
                          <>
                            <Divider style={{ margin: '8px 0' }} />
                            <p><strong>Response ({h.respondedByRole?.replace(/_/g, ' ')}):</strong> {h.responseText}</p>
                            <p style={{ color: '#888', fontSize: 12 }}>{h.respondedAt ? new Date(h.respondedAt).toLocaleString() : ''}</p>
                          </>
                        ) : (
                          <p style={{ color: '#fa8c16' }}>Awaiting response...</p>
                        )}
                      </Card>
                    ))}
                  </div>
                )}
              </div>
            )}

            <TenderEvaluationHistory
              tenderId={tenderId}
              vendorId={selectedVendorForHistory}
              open={historyVisible}
              onCancel={() => setHistoryVisible(false)}
            />

            {/* ── Technical Comparison Sheet: View (all roles) + Upload (PP, not initiated) ── */}
            {evalStatus?.comparisonSheetFileName && (
                <div style={{ marginTop: 12, marginBottom: 4, padding: '8px 14px', background: '#e6f7ff', border: '1px solid #91d5ff', borderRadius: 4, display: 'inline-flex', alignItems: 'center', gap: 10 }}>
                  <span style={{ fontWeight: 600 }}>Technical Comparison Sheet:</span>
                  <a
                    href={`${baseURL}/file/view/Tender/${evalStatus.comparisonSheetFileName}`}
                    target="_blank"
                    rel="noopener noreferrer"
                  >
                    View Document
                  </a>
                </div>
              )}
            {isPurchasePersonnelRole && !evalStatus?.evaluationStatus && (
                <div style={{ marginTop: 16 }}>
                  {renderFormFields(
                    [{ heading: '', colCnt: 1, fieldList: [{ name: 'comparationStatementFileName', label: 'Technical Comparison Sheet (PDF / Excel / Word)', type: 'multiImage', accept: '.pdf,.xlsx,.xls,.doc,.docx', span: 1 }] }],
                    handleChange, formData, '', null, setFormData, null
                  )}
                </div>
              )}

            {/* ── Financial Comparison Sheet: View (all roles) + Upload (PP, PENDING_FINANCIAL_SHEET_UPLOAD) ── */}
            {evalStatus?.financialComparisonSheetFileName && (
                <div style={{ marginTop: 12, marginBottom: 4, padding: '8px 14px', background: '#fff7e6', border: '1px solid #ffd591', borderRadius: 4, display: 'inline-flex', alignItems: 'center', gap: 10 }}>
                  <span style={{ fontWeight: 600 }}>Financial Comparison Sheet:</span>
                  <a
                    href={`${baseURL}/file/view/Tender/${evalStatus.financialComparisonSheetFileName}`}
                    target="_blank"
                    rel="noopener noreferrer"
                  >
                    View Document
                  </a>
                </div>
              )}
            {isPurchasePersonnelRole && evalStatus?.evaluationStatus === 'PENDING_FINANCIAL_SHEET_UPLOAD' && (
                <div style={{ marginTop: 16 }}>
                  {renderFormFields(
                    [{ heading: '', colCnt: 1, fieldList: [{ name: 'financialComparisionSheetFileName', label: 'Financial Comparison Sheet (PDF / Excel / Word)', type: 'multiImage', accept: '.pdf,.xlsx,.xls,.doc,.docx', span: 1 }] }],
                    handleChange, formData, '', null, setFormData, null
                  )}
                  <Button type="primary" style={{ marginTop: 8 }} onClick={handleSaveFinancialComparisonSheet}>
                    Upload Financial Comparison Sheet
                  </Button>
                </div>
              )}

            {/* ── Indent Creator actions (under 10L, SINGLE indent only) — Confirm or Seek Clarification to Vendor ── */}
            {isIndentCreatorRole &&
              evalStatus?.amountCategory === 'UNDER_10_LAKH' &&
              evalStatus?.evaluationStatus === 'PENDING_FINANCIAL' &&
              !isMultipleIndentEval && (
                <Card title="Indent Creator — Review &amp; Confirm" size="small" style={{ marginTop: 16 }}>
                  {isAnyClarificationPending && (
                    <Alert type="warning" showIcon style={{ marginBottom: 8 }}
                      message="Cannot confirm while a clarification response is pending." />
                  )}
                  {!allVendorsDecided && quotationData.length > 0 && (
                    <Alert type="warning" showIcon style={{ marginBottom: 8 }}
                      message="Please Accept or Reject all vendors before confirming." />
                  )}
                  <Space wrap>
                    <Button type="primary" loading={isSubmitting}
                      disabled={isAnyClarificationPending || anyVendorPendingClarif || !allVendorsDecided}
                      onClick={handleConfirmByIndentor}>
                      Confirm Evaluation Status
                    </Button>
                    {/* <Button style={{ color: '#fa8c16', borderColor: '#fa8c16' }}
                      disabled={isAnyClarificationPending}
                      onClick={() => openClarificationModal('INDENTOR')}>
                      Seek Clarification (Vendor)
                    </Button>
                    <Button style={{ color: '#fa8c16', borderColor: '#fa8c16' }}
                      disabled={isAnyClarificationPending}
                      onClick={() => { setClarifRequestedByRole('INDENTOR'); setClarifTarget('ALL_VENDORS'); setClarifTargetVendorId(''); setClarifTargetUserId(''); setClarifTargetUserName(''); setClarifRemarks(''); setClarificationModal(true); }}>
                      Seek Clarification (All Vendors)
                    </Button> */}
                  </Space>
                </Card>
              )}

            {/* ── Purchase Personnel Confirm (Multiple Indent Under 10L — Cases 3 & 4) ── */}
            {evalStatus?.evaluationStatus === 'PENDING_FINANCIAL' &&
              evalStatus?.amountCategory === 'UNDER_10_LAKH' &&
              isMultipleIndentEval &&
              isPurchasePersonnelRole && (
                <Card title="Purchase Personnel — Review &amp; Confirm" size="small" style={{ marginTop: 16 }}>
                  {isAnyClarificationPending && (
                    <Alert type="warning" showIcon style={{ marginBottom: 8 }}
                      message="Cannot confirm while a clarification response is pending." />
                  )}
                  {!allVendorsDecided && quotationData.length > 0 && (
                    <Alert type="warning" showIcon style={{ marginBottom: 8 }}
                      message="Please Accept or Reject all vendors before confirming." />
                  )}
                  <Space wrap>
                    <Button type="primary" loading={isSubmitting}
                      disabled={isAnyClarificationPending || anyVendorPendingClarif || !allVendorsDecided}
                      onClick={handleConfirmByIndentor}>
                      Confirm Evaluation Status
                    </Button>
                    <Button style={{ color: '#fa8c16', borderColor: '#fa8c16' }}
                      disabled={isAnyClarificationPending}
                      onClick={() => {
                        setClarifRequestedByRole('PURCHASE_PERSONNEL');
                        setClarifTarget('ALL_VENDORS');
                        setClarifTargetVendorId('');
                        setClarifTargetUserId('');
                        setClarifTargetUserName('');
                        setClarifRemarks('');
                        setClarificationModal(true);
                      }}>
                      Seek Clarification (All Vendors)
                    </Button>
                  </Space>
                </Card>
              )}

            {/* ── Select Approved Vendor ── (Above 10L only — committee/director flow) */}
            {evalStatus?.evaluationStatus === 'PENDING_FINANCIAL' &&
              evalStatus?.amountCategory !== 'UNDER_10_LAKH' &&
              role !== 'Store Purchase Officer' &&
              (isPurchasePersonnelRole ||
               (isIndentCreatorRole && evalStatus?.amountCategory !== 'UNDER_10_LAKH')) && (
                <div style={{ marginTop: 12 }}>
                  <Button type="primary" onClick={() => setSelectVendorModal(true)}>
                    Select Approved Vendor
                  </Button>
                </div>
              )}


            {/* ── Clarification Pending Banner ── */}
            {isAnyClarificationPending && (
              <Alert
                type="warning"
                showIcon
                message={
                  isPendingVendorClarif ? 'Clarification Pending — Waiting for Vendor Response'
                  : isPendingIndentorClarif ? 'Clarification Pending — Waiting for Indentor / Purchase Personnel Response'
                  : 'Clarification Pending — Committee Member(s) Need to Re-vote'
                }
                description={[
                  evalStatus.clarificationPendingFromName
                    ? `Waiting from: ${evalStatus.clarificationPendingFromName}`
                    : null,
                  evalStatus.clarificationRemarks ? `Question: "${evalStatus.clarificationRemarks}"` : null,
                ].filter(Boolean).join(' — ') || ''}
                style={{ marginTop: 12 }}
              />
            )}

            {/* ── Indentor/PP Responds to Clarification ── */}
            {isPendingIndentorClarif &&
              (isIndentCreatorRole || isPurchasePersonnelRole) && (
                <Card title="Respond to Clarification Request" size="small" style={{ marginTop: 16 }}>
                  <Alert type="warning" showIcon style={{ marginBottom: 8 }}
                    message={`Clarification requested by: ${evalStatus.clarificationRequestedByRole?.replace(/_/g, ' ')}`}
                    description={`Question: ${evalStatus.clarificationRemarks}`} />
                  <Button type="primary" onClick={() => { setRespondRole('INDENTOR'); setRespondModal(true); }}>
                    Submit Response
                  </Button>
                </Card>
              )}

            {/* ── Indentor / SPO / PP / Chairman / Director Acknowledges Vendor Clarification ── */}
            {isPendingVendorClarif &&
              (isIndentCreatorRole || isPurchasePersonnelRole || isSpoRole || isChairman || isDirector) && (
                <Card title="Vendor Clarification Response" size="small" style={{ marginTop: 16 }}>
                  <Alert type="warning" showIcon style={{ marginBottom: 8 }}
                    message="Clarification Sent to Vendor"
                    description={evalStatus.clarificationRemarks ? `"${evalStatus.clarificationRemarks}"` : ''} />
                  {/* Show per-vendor response status */}
                  {quotationData.filter(q => q.status === 'CHANGE_REQUESTED' || q.status === 'CHANGE_RESPONDED' || q.vendorResponse).map(q => (
                    <div key={q.vendorId} style={{ marginBottom: 8, padding: '8px 12px', background: q.status === 'CHANGE_REQUESTED' ? '#fff7e6' : '#f6ffed', border: `1px solid ${q.status === 'CHANGE_REQUESTED' ? '#ffd591' : '#b7eb8f'}`, borderRadius: 4 }}>
                      <strong>{q.vendorName || q.vendorId}</strong>:{' '}
                      {q.status === 'CHANGE_REQUESTED'
                        ? <span style={{ color: '#fa8c16' }}>Waiting for response...</span>
                        : <span style={{ color: '#52c41a' }}>{q.vendorResponse || 'Response submitted'}</span>}
                      {q.clarificationFileName && (
                        <a href={`${baseURL}/file/view/Tender/${q.clarificationFileName}`} target="_blank" rel="noopener noreferrer" style={{ marginLeft: 8 }}>
                          View File
                        </a>
                      )}
                    </div>
                  ))}
                  {quotationData.every(q => q.status !== 'CHANGE_REQUESTED') && quotationData.every(q => !q.vendorResponse) && (
                    <Alert type="info" showIcon style={{ marginBottom: 8 }}
                      message="Waiting for vendor(s) to submit clarification response." />
                  )}
                  <Button
                    type="primary"
                    style={{ marginTop: 8 }}
                    disabled={anyVendorPendingClarif}
                    onClick={handleAcknowledgeVendorClarification}
                  >
                    Acknowledge &amp; Resume Evaluation
                  </Button>
                  {anyVendorPendingClarif && (
                    <span style={{ marginLeft: 10, color: '#888', fontSize: 12 }}>
                      Button enables once all pending vendors submit their response
                    </span>
                  )}
                </Card>
              )}

            {/* ── Under 10L: Indentor / Purchase Dept Approval ──
                Case 2 (single-indent, double-bid financial phase): Indentor approves financial bids.
                Case 4 (multiple-indent, double-bid financial phase): PP approves (Indentor has no role). */}
            {evalStatus?.evaluationStatus === 'PENDING_APPROVAL' &&
              !isAbove10L &&
              (isPurchasePersonnelRole ||
               (isIndentCreatorRole && evalStatus?.indentCategory !== 'MULTIPLE_INDENT')) && (
                <Card
                  title={isFinancialPhase ? 'Financial Bid Evaluation — Indentor / Purchase Dept' : 'Indentor / Purchase Dept Approval'}
                  size="small" style={{ marginTop: 16 }}>
                  <Alert type="info" showIcon message={`Approved Vendor: ${evalStatus.approvedVendorName || evalStatus.approvedVendorId}`} style={{ marginBottom: 8 }} />
                  {isFinancialPhase && (
                    <Alert type="info" showIcon style={{ marginBottom: 8 }}
                      message="Please review the financial (price) bids of technically approved vendors and approve or reject." />
                  )}
                  <Space wrap>
                    <Button type="primary"
                      onClick={() => { setApprovalType('indentor-purchase'); setApprovalDecision('APPROVED'); setApprovalModal(true); }}>
                      {isFinancialPhase ? 'Approve Financial Bid' : 'Approve'}
                    </Button>
                    <Button danger
                      onClick={() => { setApprovalType('indentor-purchase'); setApprovalDecision('REJECTED'); setApprovalModal(true); }}>
                      Reject
                    </Button>
                    <Button style={{ color: '#fa8c16', borderColor: '#fa8c16' }}
                      onClick={() => openClarificationModal('INDENTOR')}>
                      Seek Clarification
                    </Button>
                  </Space>
                </Card>
              )}

            {/* ── Under 10L: Store Purchase Officer Final Approval ── */}
            {(evalStatus?.evaluationStatus === 'PENDING_SPO_APPROVAL') &&
              !isAbove10L &&
              isSpoRole && (
                <Card title={
                  isFinancialPhase
                    ? 'SPO Final Approval — Financial Bid'
                    : isDoubleBidEval
                      ? 'Store Purchase Officer — Technical Bid Review'
                      : 'Store Purchase Officer — Final Approval'
                } size="small" style={{ marginTop: 16 }}>
                  {isDoubleBidEval && !isFinancialPhase && (
                    <Alert type="warning" showIcon style={{ marginBottom: 8 }}
                      message="Double Bid — Technical Phase"
                      description="Review the technical bid evaluations above. Accepting will unlock the financial (price) bids for the next round." />
                  )}
                  {evalStatus.approvedVendorName && (
                    <Alert type="info" showIcon message={`Approved Vendor: ${evalStatus.approvedVendorName}`} style={{ marginBottom: 8 }} />
                  )}
                  {isAnyClarificationPending && (
                    <Alert type="warning" showIcon style={{ marginBottom: 8 }}
                      message="Cannot accept while a clarification response is pending." />
                  )}
                  {!allVendorsSpoDecided && (
                    <Alert type="warning" showIcon style={{ marginBottom: 8 }}
                      message="Please Accept or Reject all vendors before final submission. Every vendor accepted by the Indentor must have an SPO decision." />
                  )}
                  <Space wrap>
                    <Button type="primary"
                      disabled={isAnyClarificationPending || !allVendorsSpoDecided}
                      onClick={() => { setApprovalType('spo'); setApprovalDecision('APPROVED'); setApprovalModal(true); }}>
                      {isDoubleBidEval && !isFinancialPhase ? 'Approve Technical Phase' : 'Confirm Evaluation'}
                    </Button>
                    {/* <Button danger
                      onClick={() => { setApprovalType('spo'); setApprovalDecision('REJECTED'); setApprovalModal(true); }}>
                      SPO Reject
                    </Button> */}
                    <Button style={{ color: '#fa8c16', borderColor: '#fa8c16' }}
                      onClick={openRevisionModal}>
                      Send for Revision
                    </Button>
                    {/* <Button style={{ color: '#1890ff', borderColor: '#1890ff' }}
                      onClick={() => openClarificationModal('SPO')}>
                      Seek Clarification (Vendor)
                    </Button> */}
                  </Space>
                </Card>
              )}

            {/* ── Above 10L: Committee Member Vote ──
                PENDING_APPROVAL: single-bid committee vote OR financial phase vote for double-bid.
                PENDING_TECHNICAL: first (technical) committee vote for double-bid cases (6/8/10). */}
            {isAbove10L && isCommitteeMember && isVotingMember &&
              (evalStatus?.evaluationStatus === 'PENDING_APPROVAL' ||
               evalStatus?.evaluationStatus === 'PENDING_MEMBER_REVOTE' ||
               (evalStatus?.evaluationStatus === 'PENDING_TECHNICAL' && isDoubleBidEval)) && (
              <Card title={`${committeeLabel || 'Committee'} — Cast Your Vote${evalStatus?.evaluationStatus === 'PENDING_MEMBER_REVOTE' ? ' (Re-vote Requested)' : evalStatus?.evaluationStatus === 'PENDING_TECHNICAL' ? ' (Technical Bid Phase)' : isFinancialPhase ? ' (Financial Bid Phase)' : ''}`} size="small" style={{ marginTop: 16 }}>
                <Alert type="info" showIcon message={`Approved Vendor: ${evalStatus.approvedVendorName || evalStatus.approvedVendorId}`} style={{ marginBottom: 8 }} />
                {isFinancialPhase && (
                  <Alert type="info" showIcon style={{ marginBottom: 8 }}
                    message="You are now evaluating the FINANCIAL bids. Please review the Financial Document of technically approved vendors." />
                )}
                {evalStatus.committeeVotes && (
                  <Table
                    size="small"
                    dataSource={evalStatus.committeeVotes}
                    rowKey="committeeUserId"
                    pagination={false}
                    style={{ marginBottom: 12 }}
                    columns={[
                      { title: 'Member', dataIndex: 'committeeMemberName' },
                      { title: 'Vote', dataIndex: 'vote', render: (v) => v ? <Tag color={v === 'APPROVED' ? 'green' : 'red'}>{v}</Tag> : <Tag color="orange">Pending</Tag> },
                      { title: 'Remarks', dataIndex: 'voteRemarks', render: (r) => r || '-' },
                    ]}
                  />
                )}
                {isVotingMember && !evalStatus.committeeVotes?.find(v => String(v.committeeUserId) === String(userId))?.vote && (
                  <Button type="primary" onClick={() => setVoteModal(true)}>Cast My Vote</Button>
                )}
              </Card>
            )}

            {/* ── Above 10L: Chairman Panel ──
                Also shows at PENDING_TECHNICAL for double-bid (expert assignment + technical decision). */}
            {isAbove10L && isChairman &&
              (evalStatus?.evaluationStatus === 'PENDING_APPROVAL' ||
               evalStatus?.evaluationStatus === 'PENDING_MEMBER_REVOTE' ||
               (evalStatus?.evaluationStatus === 'PENDING_TECHNICAL' && isDoubleBidEval)) && (
              <Card title={`${committeeLabel || 'Committee'} — Chairman Decision & Expert Assignment${evalStatus?.evaluationStatus === 'PENDING_MEMBER_REVOTE' ? ' (Re-vote Requested)' : evalStatus?.evaluationStatus === 'PENDING_TECHNICAL' ? ' (Technical Bid Phase)' : isFinancialPhase ? ' (Financial Bid Phase)' : ''}`} size="small" style={{ marginTop: 16 }}>
                <Alert type="info" showIcon message={`Approved Vendor: ${evalStatus.approvedVendorName || evalStatus.approvedVendorId}`} style={{ marginBottom: 8 }} />
                {evalStatus.expertName && (
                  <Alert type="success" showIcon message={`Expert Assigned: ${evalStatus.expertName}`} style={{ marginBottom: 8 }} />
                )}
                {evalStatus.committeeVotes && (
                  <Table
                    size="small"
                    dataSource={evalStatus.committeeVotes}
                    rowKey="committeeUserId"
                    pagination={false}
                    style={{ marginBottom: 12 }}
                    columns={[
                      { title: 'Member', dataIndex: 'committeeMemberName' },
                      { title: 'Vote', dataIndex: 'vote', render: (v) => v ? <Tag color={v === 'APPROVED' ? 'green' : 'red'}>{v}</Tag> : <Tag color="orange">Pending</Tag> },
                      { title: 'Remarks', dataIndex: 'voteRemarks', render: (r) => r || '-' },
                    ]}
                  />
                )}
                <Space wrap>
                  <Button onClick={() => setExpertModal(true)}>
                    {evalStatus.expertName ? 'Change Expert' : 'Assign Expert'}
                  </Button>
                  <Button type="primary"
                    onClick={() => { setChairmanDecision('APPROVED'); setChairmanIsOverride(false); setChairmanModal(true); }}>
                    Approve (Follow Committee)
                  </Button>
                  <Button danger
                    onClick={() => { setChairmanDecision('REJECTED'); setChairmanIsOverride(false); setChairmanModal(true); }}>
                    Reject
                  </Button>
                  <Button style={{ color: '#fa8c16', borderColor: '#fa8c16' }}
                    onClick={() => { setChairmanDecision('APPROVED'); setChairmanIsOverride(true); setChairmanModal(true); }}>
                    Override Committee
                  </Button>
                  <Button style={{ color: '#1890ff', borderColor: '#1890ff' }}
                    onClick={() => openClarificationModal('CHAIRMAN')}>
                    Seek Clarification
                  </Button>
                </Space>
              </Card>
            )}

            {/* ── Above 1 CR: Director Forms Ad-Hoc Committee ── */}
            {isPendingCommitteeFormation && isDirector && (
              <Card title="Form Ad-Hoc Committee (Above ₹1 Crore)" size="small" style={{ marginTop: 16 }}>
                <Alert type="warning" showIcon style={{ marginBottom: 8 }}
                  message="This tender exceeds ₹1 Crore. You need to constitute an ad-hoc committee by selecting a Chairman, Co-Chairman, and members." />
                <Button type="primary" onClick={() => setCommitteeFormModal(true)}>
                  Form Ad-Hoc Committee
                </Button>
              </Card>
            )}

            {/* ── Above 10L: Director Approval ── */}
            {isAbove10L && isDirector && evalStatus?.evaluationStatus === 'PENDING_DIRECTOR_APPROVAL' && (
              <Card title={`Director — Final Approval${isFinancialPhase ? ' (Financial Bid Phase)' : ''}`} size="small" style={{ marginTop: 16 }}>
                {isAbove1Crore ? (
                  <Alert type="warning" showIcon style={{ marginBottom: 8 }}
                    message="Above ₹1 Crore — Ad-hoc committee has voted. You have the final overriding authority." />
                ) : (
                  <Alert type="warning" showIcon style={{ marginBottom: 8 }}
                    message="You have overriding authority over committee decisions for all tenders above ₹10 Lakhs." />
                )}
                {!isAbove1Crore && evalStatus.chairmanDecision && (
                  <Alert type="info" showIcon style={{ marginBottom: 8 }}
                    message={`Chairman Decision: ${evalStatus.chairmanDecision}${evalStatus.chairmanOverrideUsed ? ' (Override)' : ''}`} />
                )}
                <Alert type="info" showIcon message={`Approved Vendor: ${evalStatus.approvedVendorName || evalStatus.approvedVendorId}`} style={{ marginBottom: 8 }} />
                <Space wrap>
                  <Button type="primary"
                    onClick={() => { setApprovalType('director'); setApprovalDecision('APPROVED'); setApprovalModal(true); }}>
                    Approve
                  </Button>
                  <Button danger
                    onClick={() => { setApprovalType('director'); setApprovalDecision('REJECTED'); setApprovalModal(true); }}>
                    Reject
                  </Button>
                  <Button style={{ color: '#fa8c16', borderColor: '#fa8c16' }}
                    onClick={() => { setApprovalType('director'); setApprovalDecision('OVERRIDE'); setApprovalModal(true); }}>
                    Override &amp; Approve
                  </Button>
                  <Button style={{ color: '#1890ff', borderColor: '#1890ff' }}
                    onClick={() => openClarificationModal('DIRECTOR')}>
                    Seek Clarification
                  </Button>
                </Space>
              </Card>
            )}

            {/* ── Final Approved State ── */}
            {evalStatus?.evaluationStatus === 'APPROVED' && (
              <Alert
                type="success"
                showIcon
                message={`Tender Evaluation APPROVED — Vendor: ${evalStatus.approvedVendorName || evalStatus.approvedVendorId}`}
                description={`Vendor Portal Registration: ${evalStatus.vendorPortalRegistered ? 'Done' : 'Pending'}`}
                style={{ marginTop: 16 }}
              />
            )}
            {evalStatus?.evaluationStatus === 'REJECTED' && (
              <Alert type="error" showIcon message="Tender Evaluation REJECTED" style={{ marginTop: 16 }} />
            )}
          </FormBody>
        )}
      </CustomForm>

      {/* ── Technical Evaluation Modal ── */}
      <Modal
        title={`Technical Bid Evaluation — ${techVendor?.vendorName || techVendor?.vendorId}`}
        open={techModal}
        onOk={handleSaveTechnicalEval}
        onCancel={() => setTechModal(false)}
        okText="Save Decision"
      >
        <div style={{ marginBottom: 12 }}>
          <Text strong>Decision:</Text>
          <Select value={techDecision} onChange={setTechDecision} style={{ width: '100%', marginTop: 4 }}>
            <Option value="APPROVED">Approved</Option>
            <Option value="REJECTED">Rejected</Option>
          </Select>
        </div>
        <div>
          <Text strong>Remarks:</Text>
          <Input.TextArea rows={3} value={techRemarks} onChange={e => setTechRemarks(e.target.value)} style={{ marginTop: 4 }} />
        </div>
      </Modal>

      {/* ── Select Approved Vendor Modal ── */}
      <Modal
        title="Select Approved Vendor"
        open={selectVendorModal}
        onOk={handleSelectVendorSubmit}
        onCancel={() => setSelectVendorModal(false)}
        okText="Submit"
      >
        <div style={{ marginBottom: 12 }}>
          <Text strong>Approved Vendor:</Text>
          <Select value={selectedApprovedVendorId} onChange={setSelectedApprovedVendorId} style={{ width: '100%', marginTop: 4 }}>
            {(evalStatus?.vendors || [])
              .filter(v => evalStatus?.bidType === 'DOUBLE_BID' ? v.technicalStatus === 'APPROVED' : true)
              .map(v => (
                <Option key={v.vendorId} value={v.vendorId}>{v.vendorName || v.vendorId}</Option>
              ))}
          </Select>
        </div>
        <div>
          <Text strong>Remarks (e.g. L1 Vendor):</Text>
          <Input.TextArea rows={2} value={selectVendorRemarks} onChange={e => setSelectVendorRemarks(e.target.value)} style={{ marginTop: 4 }} />
        </div>
      </Modal>

      {/* ── Approval Modal (Indentor / Purchase / SPO / Director) ── */}
      <Modal
        title={`${approvalType === 'spo' ? 'SPO Final' : approvalType === 'director' ? 'Director' : 'Indentor / Purchase Dept'} — ${approvalDecision}`}
        open={approvalModal}
        onOk={handleApprovalSubmit}
        onCancel={() => setApprovalModal(false)}
        okText="Confirm"
        okButtonProps={{ danger: approvalDecision === 'REJECTED' }}
      >
        <Text strong>Remarks:</Text>
        <Input.TextArea rows={3} value={approvalRemarks} onChange={e => setApprovalRemarks(e.target.value)} style={{ marginTop: 4 }} />
      </Modal>

      {/* ── Committee Vote Modal ── */}
      <Modal
        title="Cast Committee Vote"
        open={voteModal}
        onOk={handleCastVote}
        onCancel={() => setVoteModal(false)}
        okText="Submit Vote"
      >
        <div style={{ marginBottom: 12 }}>
          <Text strong>Vote:</Text>
          <Select value={myVote} onChange={setMyVote} style={{ width: '100%', marginTop: 4 }}>
            <Option value="APPROVED">Approve</Option>
            <Option value="REJECTED">Reject</Option>
          </Select>
        </div>
        <div>
          <Text strong>Remarks:</Text>
          <Input.TextArea rows={3} value={myVoteRemarks} onChange={e => setMyVoteRemarks(e.target.value)} style={{ marginTop: 4 }} />
        </div>
      </Modal>

      {/* ── Assign Expert Modal (Chairman) ── */}
      <Modal
        title="Assign Dynamic Expert"
        open={expertModal}
        onOk={handleAssignExpertSubmit}
        onCancel={() => setExpertModal(false)}
        okText="Assign"
      >
        <div style={{ marginBottom: 12 }}>
          <Text strong>Expert User ID:</Text>
          <Input value={expertUserId} onChange={e => setExpertUserId(e.target.value)} style={{ marginTop: 4 }} placeholder="Enter user ID" />
        </div>
        <div>
          <Text strong>Expert Name:</Text>
          <Input value={expertName} onChange={e => setExpertName(e.target.value)} style={{ marginTop: 4 }} placeholder="Enter expert name" />
        </div>
      </Modal>

      {/* ── Chairman Decision Modal ── */}
      <Modal
        title={chairmanIsOverride ? 'Chairman Override Committee Decision' : 'Chairman Final Decision'}
        open={chairmanModal}
        onOk={handleChairmanDecisionSubmit}
        onCancel={() => setChairmanModal(false)}
        okText="Submit"
      >
        {chairmanIsOverride && (
          <Alert type="warning" showIcon message="You are overriding the committee decision. Please provide a detailed reason." style={{ marginBottom: 12 }} />
        )}
        <div style={{ marginBottom: 12 }}>
          <Text strong>Decision:</Text>
          <Select value={chairmanDecision} onChange={setChairmanDecision} style={{ width: '100%', marginTop: 4 }}>
            <Option value="APPROVED">Approve</Option>
            <Option value="REJECTED">Reject</Option>
          </Select>
        </div>
        <div>
          <Text strong>{chairmanIsOverride ? 'Override Reason:' : 'Remarks:'}</Text>
          <Input.TextArea rows={4} value={chairmanRemarks} onChange={e => setChairmanRemarks(e.target.value)} style={{ marginTop: 4 }} />
        </div>
      </Modal>

      {/* ── Seek Clarification / Send for Revision Modal ── */}
      <Modal
        title={
          clarifRequestedByRole === 'SPO' && clarifTarget === 'INDENTOR'
            ? 'Send for Revision (to Indent Creator)'
            : clarifRequestedByRole === 'SPO' && clarifTarget === 'VENDOR'
            ? 'Send for Revision to Vendor Portal'
            : clarifTarget === 'ALL_VENDORS'
            ? 'Seek Clarification — All Vendors'
            : 'Seek Clarification'
        }
        open={clarificationModal}
        onOk={handleSeekClarification}
        onCancel={() => setClarificationModal(false)}
        okText="Send Request"
        width={520}
      >
        <div style={{ marginBottom: 12 }}>
          <Text strong>Send Clarification To:</Text>
          <Select value={clarifTarget} onChange={setClarifTarget} style={{ width: '100%', marginTop: 4 }}>
            <Option value="VENDOR">Specific Vendor</Option>
            <Option value="ALL_VENDORS">All Vendors (Simultaneously)</Option>
            {clarifRequestedByRole === 'SPO' && <Option value="INDENTOR">Indentor / Purchase Personnel</Option>}
            {clarifRequestedByRole === 'CHAIRMAN' && <Option value="INDENTOR">Purchase Personnel</Option>}
            {clarifRequestedByRole === 'DIRECTOR' && <Option value="INDENTOR">Indentor / Purchase Personnel</Option>}
            {(clarifRequestedByRole === 'CHAIRMAN' || clarifRequestedByRole === 'DIRECTOR') && (
              <>
                <Option value="SPECIFIC_MEMBER">Specific Committee Member</Option>
                <Option value="ALL_MEMBERS">All Committee Members (Re-vote)</Option>
              </>
            )}
          </Select>
        </div>
        {clarifTarget === 'VENDOR' && (
          <div style={{ marginBottom: 12 }}>
            <Text strong>Target Vendor ID:</Text>
            <Input value={clarifTargetVendorId} onChange={e => setClarifTargetVendorId(e.target.value)}
              style={{ marginTop: 4 }} placeholder="Enter vendor ID (leave blank for approved vendor)" />
          </div>
        )}
        {(clarifTarget === 'SPECIFIC_MEMBER' || clarifTarget === 'INDENTOR') && (
          <>
            <div style={{ marginBottom: 8 }}>
              <Text strong>{clarifTarget === 'INDENTOR' ? 'Indentor User ID (optional):' : 'Member User ID:'}</Text>
              <Input value={clarifTargetUserId} onChange={e => setClarifTargetUserId(e.target.value)}
                style={{ marginTop: 4 }} placeholder={clarifTarget === 'INDENTOR' ? 'Enter Indentor user ID (if known)' : 'Enter user ID of the member'} />
            </div>
            <div style={{ marginBottom: 12 }}>
              <Text strong>{clarifTarget === 'INDENTOR' ? 'Indentor Name (optional):' : 'Member Name:'}</Text>
              <Input value={clarifTargetUserName} onChange={e => setClarifTargetUserName(e.target.value)}
                style={{ marginTop: 4 }} placeholder={clarifTarget === 'INDENTOR' ? 'Enter Indentor name (if known)' : 'Member name'} />
            </div>
          </>
        )}
        {clarifTarget === 'ALL_VENDORS' && (
          <Alert type="info" showIcon style={{ marginBottom: 12 }}
            message="This will send a clarification request to all vendors for this tender simultaneously. Each vendor must respond before evaluation resumes." />
        )}
        <div>
          <Text strong>Clarification Question / Remarks:</Text>
          <Input.TextArea rows={4} value={clarifRemarks}
            onChange={e => setClarifRemarks(e.target.value)}
            style={{ marginTop: 4 }} placeholder="Enter your clarification question..." />
        </div>
      </Modal>

      {/* ── Respond to Clarification Modal ── */}
      <Modal
        title="Respond to Clarification"
        open={respondModal}
        onOk={handleRespondClarification}
        onCancel={() => setRespondModal(false)}
        okText="Submit Response"
      >
        {evalStatus?.clarificationRemarks && (
          <Alert type="warning" showIcon style={{ marginBottom: 12 }}
            message={`Clarification Question: "${evalStatus.clarificationRemarks}"`} />
        )}
        <div>
          <Text strong>Your Response:</Text>
          <Input.TextArea rows={5} value={respondText}
            onChange={e => setRespondText(e.target.value)}
            style={{ marginTop: 4 }} placeholder="Enter your clarification response..." />
        </div>
      </Modal>

      {/* ── Director Forms Ad-Hoc Committee Modal ── */}
      <Modal
        title="Form Ad-Hoc Committee (Above ₹1 Crore)"
        open={committeeFormModal}
        onOk={handleDirectorFormCommittee}
        onCancel={() => setCommitteeFormModal(false)}
        okText="Constitute Committee"
        width={620}
      >
        <Alert type="info" showIcon style={{ marginBottom: 16 }}
          message="Select a Chairman, Co-Chairman (optional), and committee members for this tender evaluation." />

        <div style={{ marginBottom: 12, display: 'flex', gap: 8 }}>
          <div style={{ flex: 1 }}>
            <Text strong>Chairman User ID:</Text>
            <Input value={adHocChairmanId} onChange={e => setAdHocChairmanId(e.target.value)}
              style={{ marginTop: 4 }} placeholder="User ID" />
          </div>
          <div style={{ flex: 2 }}>
            <Text strong>Chairman Name:</Text>
            <Input value={adHocChairmanName} onChange={e => setAdHocChairmanName(e.target.value)}
              style={{ marginTop: 4 }} placeholder="Full Name" />
          </div>
        </div>

        <div style={{ marginBottom: 16, display: 'flex', gap: 8 }}>
          <div style={{ flex: 1 }}>
            <Text strong>Co-Chairman User ID (Optional):</Text>
            <Input value={adHocCoChairmanId} onChange={e => setAdHocCoChairmanId(e.target.value)}
              style={{ marginTop: 4 }} placeholder="User ID" />
          </div>
          <div style={{ flex: 2 }}>
            <Text strong>Co-Chairman Name:</Text>
            <Input value={adHocCoChairmanName} onChange={e => setAdHocCoChairmanName(e.target.value)}
              style={{ marginTop: 4 }} placeholder="Full Name" />
          </div>
        </div>

        <Text strong>Committee Members:</Text>
        {adHocMembers.map((member, idx) => (
          <div key={idx} style={{ display: 'flex', gap: 8, marginTop: 8 }}>
            <Input style={{ width: 90 }} placeholder="User ID"
              value={member.userId}
              onChange={e => {
                const updated = [...adHocMembers];
                updated[idx].userId = e.target.value;
                setAdHocMembers(updated);
              }} />
            <Input style={{ flex: 2 }} placeholder="Member Name"
              value={member.memberName}
              onChange={e => {
                const updated = [...adHocMembers];
                updated[idx].memberName = e.target.value;
                setAdHocMembers(updated);
              }} />
            <Input style={{ flex: 1 }} placeholder="Designation"
              value={member.designation}
              onChange={e => {
                const updated = [...adHocMembers];
                updated[idx].designation = e.target.value;
                setAdHocMembers(updated);
              }} />
            <Button danger icon={<MinusCircleOutlined />}
              onClick={() => setAdHocMembers(adHocMembers.filter((_, i) => i !== idx))} />
          </div>
        ))}
        <Button type="dashed" icon={<PlusOutlined />} style={{ marginTop: 12 }}
          onClick={() => setAdHocMembers([...adHocMembers, { userId: '', memberName: '', designation: '' }])}>
          Add Member
        </Button>
      </Modal>
    </FormContainer>
  );
};

export default TenderEvaluator;
