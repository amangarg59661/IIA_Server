
import React, { useEffect, useState, useRef, useMemo, useCallback } from 'react';
import axios from 'axios';
import { message, Spin, Tag, Table, Checkbox, Popover, Popconfirm, Input, Button, Modal, Select, Alert, Divider, Card, Badge, Typography, Steps, List, Space } from 'antd';
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

  // Chairman forward member clarification modal
  const [chairmanForwardModal, setChairmanForwardModal] = useState(false);
  const [chairmanForwardRemarks, setChairmanForwardRemarks] = useState('');

  // Member responses modal (View button in table for chairman/above 10L)
  const [memberResponsesModal, setMemberResponsesModal] = useState(false);
  const [memberResponsesVendorId, setMemberResponsesVendorId] = useState(null);
  const [viewedVendorIds, setViewedVendorIds] = useState(new Set());

  // Expert assignment modal
  const [expertModal, setExpertModal] = useState(false);
  const [expertUserId, setExpertUserId] = useState('');
  const [expertName, setExpertName] = useState('');
  const [eligibleExperts, setEligibleExperts] = useState([]);
  const [eligibleExpertsLoading, setEligibleExpertsLoading] = useState(false);
  const [expertSearchText, setExpertSearchText] = useState('');
  const expertSearchTimer = useRef(null);
  const dirUserSearchTimer = useRef(null);

  // ── Per-vendor reject reason modal (above 10L) ──
  const [vendorRejectModal, setVendorRejectModal] = useState(false);
  const [vendorRejectTarget, setVendorRejectTarget] = useState(null); // { vendorId, handler }
  const [vendorRejectRemarks, setVendorRejectRemarks] = useState('');

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
  // Director committee members table (above 1CR)
  const [dirCommitteeMembers, setDirCommitteeMembers] = useState([]);
  const [dirCommitteeLoading, setDirCommitteeLoading] = useState(false);
  const [addMemberUserId, setAddMemberUserId] = useState(null);
  const [addMemberRole, setAddMemberRole] = useState('MEMBER');
  const [dirEligibleUsers, setDirEligibleUsers] = useState([]);
  const [dirEligibleUsersLoading, setDirEligibleUsersLoading] = useState(false);
  const [dirUserSearchText, setDirUserSearchText] = useState('');

  // ── Clarification history ──
  const [clarificationHistory, setClarificationHistory] = useState([]);
  const [showClarifHistory, setShowClarifHistory] = useState(false);
  const [clarifHistoryLoading, setClarifHistoryLoading] = useState(false);
  // vendor-specific seek clarification
  const [clarifTargetVendorId, setClarifTargetVendorId] = useState('');
  // SPO row-level target toggle (INDENTOR or VENDOR)
  const [spoRowTarget, setSpoRowTarget] = useState('INDENTOR');

  // PP per-vendor clarification response (GEM/OPEN/GLOBAL)
  const [ppVendorResponses, setPpVendorResponses] = useState({});
  const [ppVendorFiles, setPpVendorFiles] = useState({});
  const [ppSubmitting, setPpSubmitting] = useState({});

  // Chairman: per-member clarification resolve
  const [selectedClarifIds, setSelectedClarifIds] = useState([]);
  const [chairmanClarifForwardRemarks, setChairmanClarifForwardRemarks] = useState('');
  const [chairmanClarifForwardModal, setChairmanClarifForwardModal] = useState(false);

  // Indentor per-vendor clarification response (SPO/Chairman/Director → Indentor)
  const [indentorOpenQuestions, setIndentorOpenQuestions] = useState([]);
  const [indentorResponses, setIndentorResponses] = useState({});
  const [indentorFiles, setIndentorFiles] = useState({});
  const [indentorSubmitting, setIndentorSubmitting] = useState({});

  // Registered vendor mapping (OPEN_TENDER / GLOBAL_TENDER / GEM)
  const [allRegisteredVendors, setAllRegisteredVendors] = useState([]);
  const [selectedRegisteredVendors, setSelectedRegisteredVendors] = useState({});

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


// ── PP: hide Confirm when responding on behalf of vendors ──

// Whether the evaluation is a double-bid (from the eval status, not form data, for accuracy)
const isDoubleBidEval = evalStatus?.bidType === 'DOUBLE_BID';
const isMultipleIndentEval = evalStatus?.indentCategory === 'MULTIPLE_INDENT';

// Financial phase: financialBidPhase=true AND status is PENDING_FINANCIAL or beyond (not PENDING_FINANCIAL_SHEET_UPLOAD)
// const isFinancialPhase = Boolean(evalStatus?.financialBidPhase) &&
//   evalStatus?.evaluationStatus !== 'PENDING_FINANCIAL_SHEET_UPLOAD';
const isFinancialPhase = Boolean(evalStatus?.financialBidPhase) &&
  (evalStatus?.evaluationStatus !== 'PENDING_FINANCIAL_SHEET_UPLOAD' ||
   (isPurchasePersonnelRole && isDoubleBidEval));

// Accept/Reject/Seek-Clarification in quotation table:
//   Technical phase: Indentor (single-indent) or PP (multiple-indent), under 10L only
//   Financial phase: same roles can act again for financial evaluation
const isOpenGlobalGem = ['OPEN_TENDER', 'GLOBAL_TENDER', 'GEM'].includes(formData.modeOfProcurement);

// PP on GEM/OPEN/GLOBAL single-indent under-10L during vendor clarification only responds on behalf
// of vendors — no Accept/Reject/Seek Clarification buttons needed
const ppOnlyRespondingForVendor = isPurchasePersonnelRole && isOpenGlobalGem && !isMultipleIndentEval
  && evalStatus?.evaluationStatus === 'PENDING_VENDOR_CLARIFICATION';

const isDocUploadPhase = evalStatus?.evaluationStatus === 'PENDING_DOCUMENT_UPLOAD'
  || evalStatus?.evaluationStatus === 'PENDING_FINANCIAL_SHEET_UPLOAD';

const showActionButtons = !isDocUploadPhase && (
  ((isIndentCreatorRole && isBelow10L && !isMultipleIndentEval && evalStatus !== null
    && evalStatus?.evaluationStatus !== 'PENDING_SPO_APPROVAL') ||
   (isPurchasePersonnelRole && isBelow10L && isMultipleIndentEval && evalStatus !== null
    && evalStatus?.evaluationStatus !== 'PENDING_SPO_APPROVAL') ||
   (isPurchasePersonnelRole && isBelow10L && evalStatus !== null
    && !ppOnlyRespondingForVendor
    && ['PENDING_INDENTOR_CLARIFICATION', 'PENDING_VENDOR_CLARIFICATION'].includes(evalStatus?.evaluationStatus))) ||

  (isFinancialPhase && evalStatus !== null &&
    (['PENDING_FINANCIAL', 'PENDING_INDENTOR_CLARIFICATION', 'PENDING_VENDOR_CLARIFICATION'].includes(evalStatus.evaluationStatus)) &&
    (isIndentCreatorRole || (isPurchasePersonnelRole && !ppOnlyRespondingForVendor))));

const canPerformActions = showActionButtons;
const showRegisteredVendorColumn = isOpenGlobalGem && evalStatus?.evaluationStatus === 'APPROVED' && isPurchasePersonnelRole;
// ── PP: Seek Clarification before evaluation is initiated (Limited/Prop, under 10L) ──
const isLimitedOrProp = ['LIMITED_TENDER', 'PROPRIETARY'].includes(formData.modeOfProcurement);

const showPpPreInitiateClarif = isPurchasePersonnelRole
  && (evalStatus?.evaluationStatus === 'PENDING_DOCUMENT_UPLOAD'
      || evalStatus?.evaluationStatus === 'PENDING_FINANCIAL_SHEET_UPLOAD')
  && isBelow10L
  && quotationData.length > 0;

  // ── PP: line-level Seek Clarification & Reject — during document upload phases ──
const showPpTechLineClarif = isPurchasePersonnelRole
  && (evalStatus?.evaluationStatus === 'PENDING_DOCUMENT_UPLOAD'
      || evalStatus?.evaluationStatus === 'PENDING_FINANCIAL_SHEET_UPLOAD'
      || evalStatus?.evaluationStatus === 'PENDING_VENDOR_CLARIFICATION')
  && quotationData.length > 0;

const showPpFinLineClarif = isPurchasePersonnelRole
  && isDoubleBidEval
  && (evalStatus?.evaluationStatus === 'PENDING_FINANCIAL_SHEET_UPLOAD'
      || (evalStatus?.evaluationStatus === 'PENDING_VENDOR_CLARIFICATION' && evalStatus?.financialBidPhase));
const showEvaluationSection = true;
// GEM/OPEN/GLOBAL multi-indent under-10L vendor clarification: disable Accept, restrict Reject to clarification seeker
const isGemMultiVendorClarif = evalStatus?.evaluationStatus === 'PENDING_VENDOR_CLARIFICATION'
  && isOpenGlobalGem && isMultipleIndentEval && isBelow10L;
const ppIsClarifSeeker = evalStatus?.clarificationRequestedByRole === 'PURCHASE_PERSONNEL';
const spoIsClarifSeeker = evalStatus?.clarificationRequestedByRole === 'SPO';
// GEM/OPEN/GLOBAL multi-indent under-10L indentor clarification: PP Accept must stay enabled
const isGemMultiIndentorClarif = evalStatus?.evaluationStatus === 'PENDING_INDENTOR_CLARIFICATION'
  && isOpenGlobalGem && isMultipleIndentEval && isBelow10L;
// PP must Accept/Reject vendors before responding to indentor clarification (multi-indent under 10L)
const ppIndentorClarifPending = isPurchasePersonnelRole && isMultipleIndentEval && isBelow10L
  && evalStatus?.evaluationStatus === 'PENDING_INDENTOR_CLARIFICATION';
// ── Double bid: separate control flags per phase ──
const showTechActionButtons = isDoubleBidEval && !isFinancialPhase &&
  ((isIndentCreatorRole && isBelow10L && !isMultipleIndentEval) ||
   (isPurchasePersonnelRole && isBelow10L && isMultipleIndentEval) ||
   (isPurchasePersonnelRole && isBelow10L && !ppOnlyRespondingForVendor
    && ['PENDING_INDENTOR_CLARIFICATION', 'PENDING_VENDOR_CLARIFICATION'].includes(evalStatus?.evaluationStatus))) &&
  evalStatus !== null &&
  evalStatus?.evaluationStatus !== 'PENDING_SPO_APPROVAL';

const showFinActionButtons = isDoubleBidEval && isFinancialPhase &&
  ['PENDING_FINANCIAL', 'PENDING_INDENTOR_CLARIFICATION', 'PENDING_VENDOR_CLARIFICATION']
    .includes(evalStatus?.evaluationStatus) &&
  ((isIndentCreatorRole && !isMultipleIndentEval)  ||
   (isPurchasePersonnelRole && isMultipleIndentEval));

const showSpoTechActions = isDoubleBidEval && !isFinancialPhase &&
  isSpoRole && ['PENDING_SPO_APPROVAL', 'PENDING_VENDOR_CLARIFICATION','PENDING_INDENTOR_CLARIFICATION'].includes(evalStatus?.evaluationStatus);

const showSpoFinActions = isDoubleBidEval && isFinancialPhase &&
  isSpoRole && ['PENDING_SPO_APPROVAL', 'PENDING_VENDOR_CLARIFICATION', 'PENDING_INDENTOR_CLARIFICATION'].includes(evalStatus?.evaluationStatus);

  useEffect(() => {
    if (showRegisteredVendorColumn && allRegisteredVendors.length === 0) {
      axios.get(`${baseURL}/api/vendor-master`)
        .then(res => {
          const vendors = res.data?.responseData || res.data || [];
          setAllRegisteredVendors(vendors);
        })
        .catch(() => message.error('Failed to load registered vendors'));
    }
  }, [showRegisteredVendorColumn]);

  const handleMapRegisteredVendor = async (tenderId, vendorId) => {
    const registeredVendorId = selectedRegisteredVendors[vendorId];
    if (!registeredVendorId) {
      message.warning('Please select a registered vendor first');
      return;
    }
    try {
      await axios.put(`${baseURL}/api/tender-evaluation/vendor/map-registered`, null, {
        params: { tenderId, vendorId, registeredVendorId }
      });
      message.success('Registered vendor mapped successfully');
      await handleSearchTender();
    } catch (err) {
      message.error(err.response?.data?.message || 'Failed to map registered vendor');
    }
  };

  useEffect(() => {
    const fetchApprovedTenders = async () => {
      try {
        const params = { role, userId };
        const response = await axios.get("/api/tender-requests/approvedTender/TenderEvaluation", { params });
        setApprovedTenderIdsWithTitle(response.data.responseData || []);
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

  // Block INDENTOR clarification if any VENDOR clarification is pending
  if (actionType === 'CHANGE_REQUEST_TO_INTENTOR'
      && evalStatus?.evaluationStatus === 'PENDING_VENDOR_CLARIFICATION') {
    return message.warning('Please wait for vendor response before seeking clarification from Indentor.');
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
            clarificationTarget: isMultipleIndentEval ? 'PURCHASE_PERSONNEL' : 'INDENTOR',
        // clarificationTarget: 'INDENTOR',
        targetVendorId: record.vendorId,
        remarks: rejectComment,
      }, { params: { tenderId } });
    }
    message.success(`SPO action '${actionType}' performed for vendor ${record.vendorId}`);
    setRejectComment('');
    setRejectedVendorId(null);
    await fetchQuotationsAndPending(tenderId);
    await fetchEvalStatus(tenderId);
    await fetchIndentorOpenQuestions(tenderId);
  } catch (err) {
    message.error(err?.response?.data?.message || "Failed to perform SPO review action");
  }
};

const handleSpoRejectIndentorClarification = async (record) => {
  if (!rejectComment.trim()) return message.warning('Please enter a rejection reason.');
  try {
    await axios.post('/api/tender-evaluation/reject-indentor-clarification',
      { decision: 'REJECTED', remarks: rejectComment, userId },
      { params: { tenderId, vendorId: record.vendorId } }
    );
    message.success(`Indentor clarification rejected for vendor ${record.vendorId}`);
    setRejectComment('');
    setRejectedVendorId(null);
    await fetchQuotationsAndPending(tenderId);
    await fetchEvalStatus(tenderId);
    await fetchIndentorOpenQuestions(tenderId);
    await fetchClarificationHistory(tenderId);
  } catch (err) {
    message.error(err?.response?.data?.message || 'Failed to reject indentor clarification.');
  }
};

const handleSpoVendorClarification = async (record) => {
  if (!rejectComment.trim()) return message.warning('Please enter a clarification question for vendor.');

  // Block VENDOR clarification if any INDENTOR clarification is pending
  if (evalStatus?.evaluationStatus === 'PENDING_INDENTOR_CLARIFICATION') {
    return message.warning('Please wait for Indentor response before seeking clarification from Vendor.');
  }

  try {
    await axios.post('/api/tender-evaluation/seek-clarification', {
      requestedByRole: 'SPO',
      requestedByUserId: userId,
      clarificationTarget: 'VENDOR',
      targetVendorId: record.vendorId,
      remarks: rejectComment,
    }, { params: { tenderId } });
    message.success(`Clarification request sent to vendor ${record.vendorId}`);
    setRejectComment('');
    setRejectedVendorId(null);
    setSpoRowTarget('INDENTOR');
    await fetchQuotationsAndPending(tenderId);
    await fetchEvalStatus(tenderId);
    await fetchClarificationHistory(tenderId);
  } catch (err) {
    message.error(err?.response?.data?.responseStatus?.message || 'Failed to send clarification to vendor.');
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
  try {
    setInitiating(true);
    const res = await axios.post('/api/tender-evaluation/begin', null, {
      params: { tenderId, userId }
    });
    setEvalStatus(res.data?.responseData);
    message.success('Evaluation initiated. Please upload the Comparison Statement.');
  } catch (e) {
    message.error(e?.response?.data?.responseStatus?.message || 'Failed to initiate evaluation.');
  } finally {
    setInitiating(false);
  }
};

const handleSubmitDocumentAndProceed = async () => {
  if (!tenderId) return;
  if (!formData.comparationStatementFileName?.[0]) {
    return message.warning('Please upload the Comparison Statement before proceeding.');
  }
  try {
    setInitiating(true);
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
    const res = await axios.post('/api/tender-evaluation/initiate', null, {
      params: { tenderId, userId }
    });
    setEvalStatus(res.data?.responseData);
    message.success('Document submitted. Evaluation proceeding.');
  } catch (e) {
    message.error(e?.response?.data?.responseStatus?.message || 'Failed to proceed.');
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
    } else if (approvalType === 'chairman') {
      body.chairmanUserId = userId;
      body.isOverride = false;
      await axios.post('/api/tender-evaluation/committee/chairman-decision', body, { params: { tenderId } });
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

const fetchEligibleExperts = async () => {
  if (!tenderId) return;
  setEligibleExpertsLoading(true);
  try {
    const res = await axios.get('/api/admin/techno-financial-committee/eligible-experts', { params: { tenderId } });
    setEligibleExperts(res.data?.responseData || []);
  } catch (e) {
    message.error('Failed to load eligible experts.');
  } finally {
    setEligibleExpertsLoading(false);
  }
};

const openExpertModal = () => {
  setExpertUserId('');
  setExpertName('');
  setExpertSearchText('');
  fetchEligibleExperts();
  setExpertModal(true);
};

const handleExpertSearch = useCallback((searchValue) => {
  if (expertSearchTimer.current) clearTimeout(expertSearchTimer.current);
  expertSearchTimer.current = setTimeout(() => {
    setExpertSearchText(searchValue.toLowerCase());
  }, 300);
}, []);

const filteredExperts = useMemo(() => {
  if (!expertSearchText) return eligibleExperts;
  return eligibleExperts.filter(e => {
    const name = (e.userName || '').toLowerCase();
    const id = String(e.userId || '').toLowerCase();
    const role = (e.roleName || '').toLowerCase();
    const empId = (e.employeeId || '').toLowerCase();
    const dept = (e.department || '').toLowerCase();
    return name.includes(expertSearchText) || id.includes(expertSearchText)
      || role.includes(expertSearchText) || empId.includes(expertSearchText)
      || dept.includes(expertSearchText);
  });
}, [eligibleExperts, expertSearchText]);

const handleExpertSelect = (selectedUserId) => {
  setExpertUserId(selectedUserId);
  const selected = eligibleExperts.find(e => e.userId === selectedUserId);
  setExpertName(selected?.userName || '');
};

const handleAssignExpertSubmit = async () => {
  if (!expertUserId) return message.warning('Select an expert.');
  try {
    await axios.post('/api/admin/techno-financial-committee/nominate', {
      tenderId,
      userId: expertUserId,
      nominatedBy: userId,
      expert: true,
      expertName,
    });
    message.success('Expert assigned.');
    setExpertModal(false);
    await fetchEvalStatus(tenderId);
  } catch (e) {
    message.error(e.response?.data?.message || 'Failed to assign expert.');
  }
};

const handleChairmanConfirmCommittee = async () => {
  try {
    const res = await axios.post('/api/tender-evaluation/chairman/confirm-committee', null, {
      params: { tenderId, chairmanUserId: userId },
    });
    message.success('Committee confirmed. Evaluation proceeding.');
    await fetchEvalStatus(tenderId);
  } catch (e) {
    message.error(e.response?.data?.message || 'Failed to confirm committee.');
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

const handleCommitteeVendorDecision = async (vendorId, decision, remarks = '') => {
  try {
    await axios.post('/api/tender-evaluation/committee/vendor-decision', null, {
      params: { tenderId, vendorId, decision, remarks, committeeUserId: userId }
    });
    message.success(`Vendor ${decision.toLowerCase()} successfully.`);
    await fetchEvalStatus(tenderId);
    await fetchQuotationsAndPending(tenderId);
  } catch (e) {
    message.error(e.response?.data?.message || 'Failed to save decision.');
  }
};

const handleChairmanVendorResolve = async (vendorId, decision, remarks = '') => {
  try {
    await axios.post('/api/tender-evaluation/chairman/vendor-resolve', null, {
      params: { tenderId, vendorId, decision, remarks, chairmanUserId: userId }
    });
    message.success(`Vendor resolved: ${decision}`);
    await fetchEvalStatus(tenderId);
    await fetchQuotationsAndPending(tenderId);
  } catch (e) {
    message.error(e.response?.data?.message || 'Failed to resolve vendor.');
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
  setClarifTarget(isMultipleIndentEval ? 'PURCHASE_PERSONNEL' : 'INDENTOR');
  // setClarifTarget('INDENTOR');
  setClarifTargetVendorId('');
  setClarifTargetUserId('');
  setClarifTargetUserName('');
  setClarifRemarks('');
  setClarificationModal(true);
};

const handleSeekClarification = async () => {
  if (!clarifRemarks.trim()) return message.warning('Please enter clarification remarks.');

  // Cross-type validation: can't mix VENDOR and INDENTOR clarification types
  if (['SPO', 'CHAIRMAN', 'DIRECTOR'].includes(clarifRequestedByRole)) {
    const targetIsVendor = ['VENDOR', 'ALL_VENDORS'].includes(clarifTarget);
    const targetIsIndentor = ['INDENTOR', 'PURCHASE_PERSONNEL'].includes(clarifTarget);
    if (targetIsVendor && evalStatus?.evaluationStatus === 'PENDING_INDENTOR_CLARIFICATION') {
      return message.warning('Please wait for Indentor response before seeking clarification from Vendor.');
    }
    if (targetIsIndentor && evalStatus?.evaluationStatus === 'PENDING_VENDOR_CLARIFICATION') {
      return message.warning('Please wait for vendor response before seeking clarification from Indentor.');
    }
  }

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
    await fetchIndentorOpenQuestions(tenderId);
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

// ── Chairman: resolve member clarification ───────────────────────
const handleChairmanForwardToVendor = async () => {
  if (!chairmanForwardRemarks.trim()) return message.warning('Please enter your question for the vendor.');
  try {
    await axios.post('/api/tender-evaluation/chairman/resolve-member-clarification', {
      action: 'FORWARD_TO_VENDOR',
      remarks: chairmanForwardRemarks,
      chairmanUserId: userId,
    }, { params: { tenderId } });
    message.success('Clarification forwarded to vendor.');
    setChairmanForwardModal(false);
    setChairmanForwardRemarks('');
    await fetchEvalStatus(tenderId);
    await fetchClarificationHistory(tenderId);
    await fetchQuotationsAndPending(tenderId);
  } catch (e) {
    message.error(e?.response?.data?.responseStatus?.message || 'Failed to forward clarification.');
  }
};

const handleRejectMemberClarification = () => {
  const isDirectorRequest = evalStatus?.clarificationRequestedByRole === 'DIRECTOR';
  Modal.confirm({
    title: isDirectorRequest ? 'Dismiss Director Clarification Request?' : 'Cancel Member Clarification Request?',
    content: isDirectorRequest
      ? 'This will dismiss the Director\'s clarification request and return to Director approval.'
      : 'The requesting member\'s vote will be reset and they will have to re-evaluate.',
    onOk: async () => {
      try {
        await axios.post('/api/tender-evaluation/chairman/resolve-member-clarification', {
          action: 'REJECT',
          remarks: isDirectorRequest ? 'Clarification request dismissed by Chairman.' : 'Clarification request cancelled by Chairman.',
          chairmanUserId: userId,
        }, { params: { tenderId } });
        message.success(isDirectorRequest ? 'Clarification request dismissed.' : 'Clarification request cancelled. Member must re-vote.');
        await fetchEvalStatus(tenderId);
        await fetchClarificationHistory(tenderId);
      } catch (e) {
        message.error(e?.response?.data?.responseStatus?.message || 'Failed to reject clarification.');
      }
    },
  });
};

const handleResetAllVotes = () => {
  Modal.confirm({
    title: 'Reset All Committee Member Votes?',
    content: 'All members will have to re-evaluate and vote again.',
    onOk: async () => {
      try {
        await axios.post('/api/tender-evaluation/seek-clarification', {
          requestedByRole: 'CHAIRMAN',
          requestedByUserId: userId,
          clarificationTarget: 'ALL_MEMBERS',
          remarks: 'Chairman has reset all committee votes for re-evaluation.',
        }, { params: { tenderId } });
        message.success('All member votes have been reset.');
        await fetchEvalStatus(tenderId);
        await fetchClarificationHistory(tenderId);
      } catch (e) {
        message.error(e?.response?.data?.responseStatus?.message || 'Failed to reset votes.');
      }
    },
  });
};

// ── Chairman: resolve selected member clarifications (per-history-record) ──
const handleForwardSelectedClarifs = async () => {
  if (!chairmanClarifForwardRemarks.trim()) return message.warning('Please enter your question for the vendor.');
  if (selectedClarifIds.length === 0) return message.warning('Please select at least one clarification.');
  try {
    await axios.post('/api/tender-evaluation/chairman/resolve-member-clarification', {
      action: 'FORWARD_TO_VENDOR',
      remarks: chairmanClarifForwardRemarks,
      chairmanUserId: userId,
      historyIds: selectedClarifIds,
    }, { params: { tenderId } });
    message.success('Clarification forwarded to vendor.');
    setChairmanClarifForwardModal(false);
    setChairmanClarifForwardRemarks('');
    setSelectedClarifIds([]);
    await fetchEvalStatus(tenderId);
    await fetchClarificationHistory(tenderId);
    await fetchQuotationsAndPending(tenderId);
  } catch (e) {
    message.error(e?.response?.data?.responseStatus?.message || 'Failed to forward clarification.');
  }
};

const handleDismissSelectedClarifs = (ids) => {
  Modal.confirm({
    title: 'Dismiss Selected Clarification(s)?',
    content: 'The member(s) will be able to re-vote on the vendor after dismissal.',
    onOk: async () => {
      try {
        await axios.post('/api/tender-evaluation/chairman/resolve-member-clarification', {
          action: 'DISMISS',
          remarks: 'Dismissed by Chairman.',
          chairmanUserId: userId,
          historyIds: ids,
        }, { params: { tenderId } });
        message.success('Clarification dismissed.');
        setSelectedClarifIds(prev => prev.filter(id => !ids.includes(id)));
        await fetchEvalStatus(tenderId);
        await fetchClarificationHistory(tenderId);
      } catch (e) {
        message.error(e?.response?.data?.responseStatus?.message || 'Failed to dismiss clarification.');
      }
    },
  });
};

// ── Above 10L Gated Flow handlers ─────────────────────────────────
const handlePpSubmitDocument = async () => {
  try {
    await axios.post('/api/tender-evaluation/pp/submit-document', null, { params: { tenderId } });
    message.success('Document submitted. Forwarded to Chairman for review.');
    await fetchEvalStatus(tenderId);
  } catch (e) {
    message.error(e?.response?.data?.responseStatus?.message || 'Failed to submit document.');
  }
};

const handleConfirmCommitteeVotes = async () => {
  Modal.confirm({
    title: 'Confirm Your Votes?',
    content: 'Once confirmed, you cannot change your vendor decisions.',
    onOk: async () => {
      try {
        await axios.post('/api/tender-evaluation/committee/confirm-votes', null, {
          params: { tenderId, committeeUserId: userId }
        });
        message.success('Votes confirmed and locked.');
        await fetchEvalStatus(tenderId);
      } catch (e) {
        message.error(e?.response?.data?.responseStatus?.message || 'Failed to confirm votes.');
      }
    },
  });
};

const openVendorRejectModal = (vendorId, handler) => {
  setVendorRejectTarget({ vendorId, handler });
  setVendorRejectRemarks('');
  setVendorRejectModal(true);
};

const handleVendorRejectConfirm = async () => {
  if (!vendorRejectRemarks.trim()) return message.warning('Please enter a rejection reason.');
  const { vendorId, handler } = vendorRejectTarget;
  setVendorRejectModal(false);
  await handler(vendorId, 'REJECTED', vendorRejectRemarks);
  setVendorRejectTarget(null);
  setVendorRejectRemarks('');
};

const handleChairmanVendorVote = async (vendorId, decision, remarks = '') => {
  try {
    await axios.post('/api/tender-evaluation/chairman/vendor-vote', null, {
      params: { tenderId, vendorId, decision, remarks, chairmanUserId: userId }
    });
    message.success(`Chairman vote: ${decision}`);
    await fetchEvalStatus(tenderId);
  } catch (e) {
    message.error(e?.response?.data?.responseStatus?.message || 'Failed to submit vote.');
  }
};

const handleDirectorVendorVote = async (vendorId, decision, remarks = '') => {
  try {
    await axios.post('/api/tender-evaluation/director/vendor-vote', null, {
      params: { tenderId, vendorId, decision, remarks, directorUserId: userId }
    });
    message.success(`Director vote: ${decision}`);
    await fetchEvalStatus(tenderId);
  } catch (e) {
    message.error(e?.response?.data?.responseStatus?.message || 'Failed to submit vote.');
  }
};

const handleApproveCommitteeResponse = async () => {
  const vendorVotesMap = evalStatus?.committeeVendorVotes || {};
  const vendorIds = Object.keys(vendorVotesMap);
  if (vendorIds.length === 0) return message.warning('No committee votes found.');
  Modal.confirm({
    title: 'Approve Committee Response?',
    content: 'This will copy the Chairman\'s per-vendor decisions (including rejection reasons) as your Director decisions. You can review them in the table before confirming.',
    onOk: async () => {
      try {
        for (const vid of vendorIds) {
          const chairmanVote = vendorVotesMap[vid]?.find(v => v.voterRole === 'CHAIRMAN' && v.decision && v.phase === currentVotePhase);
          if (chairmanVote) {
            await axios.post('/api/tender-evaluation/director/vendor-vote', null, {
              params: { tenderId, vendorId: vid, decision: chairmanVote.decision, directorUserId: userId }
            });
          }
        }
        message.success('Chairman decisions copied to Director. Review in the table, then Confirm Evaluation.');
        await fetchEvalStatus(tenderId);
      } catch (e) {
        message.error(e?.response?.data?.responseStatus?.message || 'Failed to approve committee response.');
      }
    },
  });
};

const handlePpRespondForVendor = async (vendorId) => {
  const responseText = ppVendorResponses[vendorId];
  if (!responseText || !responseText.trim()) return message.warning('Please enter a clarification response.');
  setPpSubmitting(prev => ({ ...prev, [vendorId]: true }));
  try {
    let responseFileName = null;
    const fileObj = ppVendorFiles[vendorId];
    if (fileObj) {
      const fd = new FormData();
      fd.append('file', fileObj);
      const uploadResp = await axios.post('/file/upload?fileType=Tender', fd, {
        headers: { 'Content-Type': 'multipart/form-data', Accept: 'application/json' },
      });
      responseFileName = uploadResp.data.responseData.fileName;
    }
    await axios.post('/api/tender-evaluation/respond-clarification', {
      respondedByRole: 'PURCHASE_PERSONNEL',
      respondedById: String(userId),
      responseText: responseText.trim(),
      responseFileName,
      vendorId,
    }, { params: { tenderId } });
    message.success(`Clarification response submitted for vendor ${vendorId}`);
    setPpVendorResponses(prev => { const n = { ...prev }; delete n[vendorId]; return n; });
    setPpVendorFiles(prev => { const n = { ...prev }; delete n[vendorId]; return n; });
    await fetchEvalStatus(tenderId);
    await fetchQuotationsAndPending(tenderId);
    await fetchClarificationHistory(tenderId);
  } catch (e) {
    message.error(e?.response?.data?.responseStatus?.message || 'Failed to submit response.');
  } finally {
    setPpSubmitting(prev => ({ ...prev, [vendorId]: false }));
  }
};

// ── Director Ad-Hoc Committee Management (Above 1CR) ────────────
const fetchDirCommitteeMembers = async () => {
  if (!tenderId) return;
  setDirCommitteeLoading(true);
  try {
    const res = await axios.get('/api/tender-evaluation/director/committee-members', { params: { tenderId } });
    setDirCommitteeMembers(res.data?.responseData || []);
  } catch (e) {
    setDirCommitteeMembers([]);
  } finally {
    setDirCommitteeLoading(false);
  }
};

const fetchDirEligibleUsers = async () => {
  if (!tenderId) return;
  setDirEligibleUsersLoading(true);
  try {
    const res = await axios.get('/api/admin/techno-financial-committee/eligible-experts', { params: { tenderId } });
    setDirEligibleUsers(res.data?.responseData || []);
  } catch (e) {
    message.error('Failed to load eligible users.');
  } finally {
    setDirEligibleUsersLoading(false);
  }
};

const handleDirUserSearch = useCallback((searchValue) => {
  if (dirUserSearchTimer.current) clearTimeout(dirUserSearchTimer.current);
  dirUserSearchTimer.current = setTimeout(() => {
    setDirUserSearchText(searchValue.toLowerCase());
  }, 300);
}, []);

const filteredDirUsers = useMemo(() => {
  if (!dirUserSearchText) return dirEligibleUsers;
  return dirEligibleUsers.filter(e => {
    const name = (e.userName || '').toLowerCase();
    const id = String(e.userId || '').toLowerCase();
    const role = (e.roleName || '').toLowerCase();
    const empId = (e.employeeId || '').toLowerCase();
    const dept = (e.department || '').toLowerCase();
    return name.includes(dirUserSearchText) || id.includes(dirUserSearchText)
      || role.includes(dirUserSearchText) || empId.includes(dirUserSearchText)
      || dept.includes(dirUserSearchText);
  });
}, [dirEligibleUsers, dirUserSearchText]);

const handleDirAddMember = async () => {
  if (!addMemberUserId) return message.warning('Select a user to add.');
  try {
    await axios.post('/api/tender-evaluation/director/add-member', null, {
      params: { tenderId, userId: addMemberUserId, directorUserId: userId, role: addMemberRole },
    });
    message.success(`${addMemberRole === 'CHAIRMAN' ? 'Chairman' : 'Member'} added to committee.`);
    setAddMemberUserId(null);
    setAddMemberRole('MEMBER');
    await fetchDirCommitteeMembers();
    await fetchDirEligibleUsers();
  } catch (e) {
    message.error(e?.response?.data?.responseStatus?.message || 'Failed to add member.');
  }
};

const handleDirRemoveMember = async (memberUserId) => {
  try {
    await axios.delete('/api/tender-evaluation/director/remove-member', {
      params: { tenderId, userId: memberUserId, directorUserId: userId },
    });
    message.success('Member removed from committee.');
    await fetchDirCommitteeMembers();
    await fetchDirEligibleUsers();
  } catch (e) {
    message.error(e?.response?.data?.responseStatus?.message || 'Failed to remove member.');
  }
};

const handleDirectorConfirmCommittee = async () => {
  const hasChairman = dirCommitteeMembers.some(m => m.role === 'CHAIRMAN');
  if (!hasChairman) return message.warning('A Chairman must be assigned before confirming.');
  if (dirCommitteeMembers.length < 2) return message.warning('Add at least one member besides the Chairman.');
  try {
    await axios.post('/api/tender-evaluation/director/confirm-committee', null, {
      params: { tenderId, directorUserId: userId },
    });
    message.success('Committee confirmed. Moved to Chairman Review.');
    await fetchEvalStatus(tenderId);
    await fetchDirCommitteeMembers();
  } catch (e) {
    message.error(e?.response?.data?.responseStatus?.message || 'Failed to confirm committee.');
  }
};

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
    message.success('Ad-hoc committee formed successfully.');
    setCommitteeFormModal(false);
    await fetchEvalStatus(tenderId);
    await fetchDirCommitteeMembers();
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


// AFTER
const fetchIndentorOpenQuestions = async (tid) => {
  if (!tid) return;
  try {
    const res = await axios.get('/api/tender-evaluation/open-indentor-clarifications', { params: { tenderId: tid } });
    const all = res.data?.responseData || [];
    // Indentor sees INDENTOR-targeted rows; PP sees PURCHASE_PERSONNEL-targeted rows (internal SPO→PP only)
    const filtered = all.filter(h =>
      isPurchasePersonnelRole
        ? h.clarificationTarget === 'PURCHASE_PERSONNEL'
        : (h.clarificationTarget === 'INDENTOR' || h.clarificationTarget === 'PURCHASE_PERSONNEL')
    );
    setIndentorOpenQuestions(filtered);
  } catch (e) {
    console.error('Failed to fetch indentor clarifications:', e);
    setIndentorOpenQuestions([]);
  }
};
// // ── Fetch open indentor clarifications (per-vendor cards) ──
// const fetchIndentorOpenQuestions = async (tid) => {
//   if (!tid) return;
//   try {
//     const res = await axios.get('/api/tender-evaluation/open-indentor-clarifications', { params: { tenderId: tid } });
//     setIndentorOpenQuestions(res.data?.responseData || []);
//   } catch (e) {
//     console.error('Failed to fetch indentor clarifications:', e);
//     setIndentorOpenQuestions([]);
//   }
// };

// ── Per-question indentor clarification response submit ──
const handleIndentorRespondPerQuestion = async (historyId) => {
  const responseText = indentorResponses[historyId];
  if (!responseText || !responseText.trim()) return message.warning('Please enter a response.');
  setIndentorSubmitting(prev => ({ ...prev, [historyId]: true }));
  try {
    let responseFileName = null;
    const fileObj = indentorFiles[historyId];
    if (fileObj) {
      const fd = new FormData();
      fd.append('file', fileObj);
      const uploadResp = await axios.post('/file/upload?fileType=Tender', fd, {
        headers: { 'Content-Type': 'multipart/form-data', Accept: 'application/json' },
      });
      responseFileName = uploadResp.data.responseData.fileName;
    }
    await axios.post('/api/tender-evaluation/respond-clarification', {
      respondedByRole: isPurchasePersonnelRole ? 'PURCHASE_PERSONNEL' : 'INDENTOR',
      respondedById: String(userId),
      responseText: responseText.trim(),
      responseFileName,
      clarificationHistoryId: historyId,
    }, { params: { tenderId } });
    message.success('Response submitted successfully.');
    setIndentorResponses(prev => { const n = { ...prev }; delete n[historyId]; return n; });
    setIndentorFiles(prev => { const n = { ...prev }; delete n[historyId]; return n; });
    await fetchEvalStatus(tenderId);
    await fetchIndentorOpenQuestions(tenderId);
    await fetchQuotationsAndPending(tenderId);
    await fetchClarificationHistory(tenderId);
  } catch (e) {
    message.error(e?.response?.data?.responseStatus?.message || 'Failed to submit response.');
  } finally {
    setIndentorSubmitting(prev => ({ ...prev, [historyId]: false }));
  }
};

// ─────────────────────────────────────────────────────────────────
// ── Indentor status column label ──
const isAbove10L = evalStatus?.amountCategory !== 'UNDER_10_LAKH' && evalStatus?.amountCategory != null;
const isAdHocChairman = evalStatus?.adHocChairmanUserId != null
  && String(evalStatus.adHocChairmanUserId) === String(userId);
const isChairman = role === 'Committee Chairman' || isAdHocChairman;
const isDirector = role === 'Director';
const isCommitteeMember = role === 'Committee Member' && !isAdHocChairman;
const isVotingMember = evalStatus?.committeeVotes?.some(
  v => String(v.committeeUserId) === String(userId)
);

const indentorStatusLabel =
  (evalStatus?.amountCategory !== 'UNDER_10_LAKH' && evalStatus?.amountCategory != null)
    ? 'Committee Status'
  : isMultipleIndentEval ? 'Purchase Personnel Status'
  : 'Indentor Status';

const isAssignedExpert = evalStatus?.expertUserId != null && String(evalStatus.expertUserId) === String(userId);

const showCommitteeMemberTechActions = isDoubleBidEval && !isFinancialPhase &&
  (isCommitteeMember || isAssignedExpert) && isAbove10L &&
  (evalStatus?.evaluationStatus === 'PENDING_APPROVAL' ||
   evalStatus?.evaluationStatus === 'PENDING_MEMBER_REVOTE' ||
   evalStatus?.evaluationStatus === 'PENDING_MEMBER_VOTING' ||
   evalStatus?.evaluationStatus === 'PENDING_TECHNICAL' ||
   evalStatus?.evaluationStatus === 'PENDING_CHAIRMAN_CLARIFICATION_REVIEW');

const showCommitteeMemberFinActions = isDoubleBidEval && isFinancialPhase &&
  (isCommitteeMember || isAssignedExpert) && isAbove10L &&
  (evalStatus?.evaluationStatus === 'PENDING_APPROVAL' ||
   evalStatus?.evaluationStatus === 'PENDING_MEMBER_REVOTE' ||
   evalStatus?.evaluationStatus === 'PENDING_MEMBER_VOTING' ||
   evalStatus?.evaluationStatus === 'PENDING_CHAIRMAN_CLARIFICATION_REVIEW');

// Single bid: committee member inline Accept/Reject (above 10L)
const showCommitteeMemberSingleBidActions = !isDoubleBidEval &&
  (isCommitteeMember || isAssignedExpert) && isAbove10L &&
  (evalStatus?.evaluationStatus === 'PENDING_APPROVAL' ||
   evalStatus?.evaluationStatus === 'PENDING_MEMBER_REVOTE' ||
   evalStatus?.evaluationStatus === 'PENDING_MEMBER_VOTING' ||
   evalStatus?.evaluationStatus === 'PENDING_CHAIRMAN_CLARIFICATION_REVIEW');
const showChairmanInlineActions = isChairman && isAbove10L &&
  (evalStatus?.evaluationStatus === 'PENDING_CHAIRMAN_VOTING' ||
   evalStatus?.evaluationStatus === 'PENDING_MEMBER_VOTING' ||
   evalStatus?.evaluationStatus === 'PENDING_MEMBER_REVOTE' ||
   (['PENDING_VENDOR_CLARIFICATION', 'PENDING_INDENTOR_CLARIFICATION'].includes(evalStatus?.evaluationStatus) &&
    evalStatus?.clarificationRequestedByRole === 'CHAIRMAN'));

const showDirectorInlineActions = isAbove10L && isDirector &&
  (evalStatus?.evaluationStatus === 'PENDING_DIRECTOR_APPROVAL' ||
   evalStatus?.evaluationStatus === 'PENDING_MEMBER_VOTING' ||
   evalStatus?.evaluationStatus === 'PENDING_MEMBER_REVOTE' ||
   evalStatus?.evaluationStatus === 'PENDING_CHAIRMAN_CLARIFICATION_REVIEW' ||
   (['PENDING_VENDOR_CLARIFICATION', 'PENDING_INDENTOR_CLARIFICATION'].includes(evalStatus?.evaluationStatus) &&
    evalStatus?.clarificationRequestedByRole === 'DIRECTOR'));

// Above 10L gated flow computed values
const currentVotePhase = isFinancialPhase ? 'FINANCIAL' : 'TECHNICAL';
const myVendorVotes = Object.values(evalStatus?.committeeVendorVotes || {})
  .map(votes => votes.find(v => String(v.committeeUserId) === String(userId) && v.voterRole === 'MEMBER' && v.phase === currentVotePhase))
  .filter(Boolean);
const allMyVendorsDecided = myVendorVotes.length > 0 && myVendorVotes.every(v => v?.decision);
const myVotesConfirmed = myVendorVotes.length > 0 && myVendorVotes.every(v => v?.confirmed);

// Per-member per-vendor pending clarifications (above 10L)
const pendingMemberClarifs = evalStatus?.pendingMemberClarifications || [];
const myPendingClarifVendorIds = new Set(
  pendingMemberClarifs
    .filter(c => String(c.memberId) === String(userId))
    .map(c => c.vendorId)
    .filter(Boolean)
);
const anyMemberClarifPending = pendingMemberClarifs.length > 0;
const isChairmanClarifReview = evalStatus?.evaluationStatus === 'PENDING_CHAIRMAN_CLARIFICATION_REVIEW';

const allChairmanVendorsDecided = (() => {
  const votesMap = evalStatus?.committeeVendorVotes || {};
  const vendorIds = Object.keys(votesMap).filter(vid =>
    votesMap[vid]?.some(v => v.phase === currentVotePhase)
  );
  if (vendorIds.length === 0) return false;
  return vendorIds.every(vid =>
    votesMap[vid]?.some(v => v.voterRole === 'CHAIRMAN' && v.decision && v.phase === currentVotePhase)
  );
})();

const allDirectorVendorsDecided = (() => {
  const votesMap = evalStatus?.committeeVendorVotes || {};
  const vendorIds = Object.keys(votesMap).filter(vid =>
    votesMap[vid]?.some(v => v.phase === currentVotePhase)
  );
  if (vendorIds.length === 0) return false;
  return vendorIds.every(vid =>
    votesMap[vid]?.some(v => v.voterRole === 'DIRECTOR' && v.decision && v.phase === currentVotePhase)
  );
})();

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
const showClarificationFile = quotationData.some(item => item.clarificationFileName) || clarificationHistory.some(h => h.targetVendorId);

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
  render: (file, record) => {
    const latest = [...clarificationHistory]
      .filter(h => h.targetVendorId === record.vendorId)
      .sort((a, b) => b.roundNumber - a.roundNumber)[0];
    // only show if latest round itself has a file — no fallback to older rounds
    return latest?.responseFileName ? (
      <a href={`${baseURL}/file/view/Tender/${latest.responseFileName}`} target="_blank" rel="noopener noreferrer">
        View
      </a>
    ) : null;
  },
},
      ]
    : []),
  ...(!isDouble ? [priceBidColumnForSingleBid] : [priceBidColumn]),

 


];

let columns = [];

if (isSpoRole) {
  columns = [
    ...baseColumns,
  /* {
          title: indentorStatusLabel,
          key: 'status',
          dataIndex: 'status',
          render: (status) => status || 'N/A',
        },*/
         {
          title: indentorStatusLabel,
          key: 'indentorStatus',
          dataIndex: 'indentorStatus',
          render: (indentorStatus, record) => {
            if (indentorStatus === 'CHANGE_REQUESTED') return 'Pending Clarification';
            if (indentorStatus === 'REJECTED' || indentorStatus === 'Rejected') return (
              <Popover
                content={record.indentorRemarks ? <span>{record.indentorRemarks}</span> : <span style={{ color: '#888' }}>No reason provided</span>}
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
          title: 'SPO Status',
          key: 'sopStatus',
          dataIndex: 'sopStatus',
          render: (sopStatus, record) =>{
            const indStatus = isFinancialPhase ? record.financialIndentorStatus : record.indentorStatus;
            if (indStatus === 'REJECTED' || indStatus === 'Rejected') return 'Auto-Rejected';
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
          render: (sopStatus, record) =>{
            const indStatus = isFinancialPhase ? record.financialIndentorStatus : record.indentorStatus;
            if (indStatus === 'REJECTED' || indStatus === 'Rejected') return 'Disqualified';
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
      width: 150,
      render: (val, record) => {
        if (val === 'CHANGE_REQUESTED') return <Tag color="orange">Pending Clarification</Tag>;
        if (val === 'REJECTED' || val === 'Rejected') return (
          <Popover
            content={record.financialIndentorRemarks ? <span>{record.financialIndentorRemarks}</span> : <span style={{ color: '#888' }}>No reason provided</span>}
            title="Rejection Reason"
          >
            <span style={{ color: 'red', cursor: 'help', borderBottom: '1px dashed red' }}>Rejected ⓘ</span>
          </Popover>
        );
        if (val === 'ACCEPTED' || val === 'Completed') return <Tag color="green">Accepted</Tag>;
        return val || <Tag>Pending</Tag>;
      }
    },
    {
      title: 'SPO Financial Status',
      key: 'financialSpoStatus',
      dataIndex: 'financialSpoStatus',
      width: 130,
      render: (val, record) => {
        if (record.financialIndentorStatus === 'REJECTED' || record.financialIndentorStatus === 'Rejected') return <Tag color="red">Auto-Rejected</Tag>;
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
    const techRejected = finPhase && (record.indentorStatus === 'REJECTED' || record.sopStatus === 'REJECTED');
    if (techRejected) return <Tag color="default">N/A (Technical Rejected)</Tag>;
    const indStatus = finPhase ? record.financialIndentorStatus : record.indentorStatus;
    const spStatus = finPhase ? record.financialSpoStatus : record.sopStatus;
    // const spoCanAct = evalStatus?.evaluationStatus === 'PENDING_SPO_APPROVAL'
    //   && indStatus === 'ACCEPTED'
    //   && (!spStatus || spStatus === 'PENDING')
    //   && record.status !== 'CHANGE_REQUESTED';
    const spoCanAct = ['PENDING_SPO_APPROVAL', 'PENDING_VENDOR_CLARIFICATION', 'PENDING_INDENTOR_CLARIFICATION'].includes(evalStatus?.evaluationStatus)
      && indStatus === 'ACCEPTED'
      && (!spStatus || spStatus === 'PENDING'
        //  || spStatus === 'REJECTED'
        )
      && (record.status !== 'CHANGE_REQUESTED' || record.status === 'CHANGE_RESPONDED');
    // Reject always allowed even if vendor hasn't responded — prevents flow from getting stuck
   const spoCanReject = ['PENDING_SPO_APPROVAL', 'PENDING_VENDOR_CLARIFICATION', 'PENDING_INDENTOR_CLARIFICATION'].includes(evalStatus?.evaluationStatus)
  && (record.indentorStatus === 'ACCEPTED' || record.indentorStatus === null || record.indentorStatus === 'REJECTED')
  && (!record.sopStatus || record.sopStatus === 'PENDING' || record.sopStatus === 'CHANGE_REQUESTED');
    const pendingToIndentor = record.changeRequestToIndentor;
    const vendorHasOpenIndentorClarif = indentorOpenQuestions.some(q => q.targetVendorId === record.vendorId);
    const vendorPpClarifPending = record.status === 'CHANGE_REQUESTED' && !vendorHasOpenIndentorClarif;
    const ppHasRespondedForVendor = record.status === 'CHANGE_RESPONDED';

    return (
      <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap' }}>
        <Button
          size="small"
          disabled={!spoCanAct || spStatus === 'ACCEPTED' || (vendorHasOpenIndentorClarif && !ppHasRespondedForVendor) || (vendorPpClarifPending && !ppHasRespondedForVendor) || isGemMultiVendorClarif }
          onClick={() => handleSpoReview(record, 'ACCEPT')}
          title={isGemMultiVendorClarif ? 'Cannot accept while vendor clarification is pending' : (vendorHasOpenIndentorClarif && !ppHasRespondedForVendor) ? 'Indentor must respond to clarification first' : (vendorPpClarifPending && !ppHasRespondedForVendor) ? 'Cannot accept — PP must respond to clarification for this vendor first': ''}
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
        disabled={!spoCanReject || spStatus === 'REJECTED' || (isGemMultiVendorClarif && !spoIsClarifSeeker)}
        // disabled={!spoCanAct || spStatus === 'REJECTED'}
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
    disabled={!spoCanReject || spStatus === 'REJECTED' || (isGemMultiVendorClarif && !spoIsClarifSeeker)}
    // disabled={!spoCanAct || spStatus === 'REJECTED'}
  >
    {spStatus === 'REJECTED' ? 'Rejected' : 'SPO Reject'}
  </Button>
</Popover>

        <Popover
          content={
            <div style={{ padding: 12, minWidth: 280 }}>
              <div style={{ marginBottom: 8 }}>
                <Text strong style={{ fontSize: 12 }}>Send To:</Text>
                <Select
                  size="small"
                  value={rejectedVendorId === record.vendorId ? (spoRowTarget || 'INDENTOR') : 'INDENTOR'}
                  onChange={(val) => { setRejectedVendorId(record.vendorId); setSpoRowTarget(val); }}
                  style={{ width: '100%', marginTop: 4 }}
                >
                  <Option value="INDENTOR">Indentor / Purchase Personnel</Option>
                  <Option value="VENDOR">Vendor (ask vendor directly)</Option>
                </Select>
              </div>
              <Input.TextArea
                placeholder={
                  (rejectedVendorId === record.vendorId ? spoRowTarget : 'INDENTOR') === 'VENDOR'
                    ? 'Enter clarification question for vendor'
                    : 'Enter change request to Indentor'
                }
                rows={3}
                value={rejectedVendorId === record.vendorId ? rejectComment : ''}
                onChange={(e) => {
                  setRejectedVendorId(record.vendorId);
                  setRejectComment(e.target.value);
                }}
              />
              <Button
                type="primary"
                disabled={
                  !['PENDING_SPO_APPROVAL', 'PENDING_INDENTOR_CLARIFICATION', 'PENDING_VENDOR_CLARIFICATION'].includes(evalStatus?.evaluationStatus)
                  || record.status === 'CHANGE_REQUESTED'
                  || record.changeRequestToIndentor
                }
                onClick={() => {
                  const target = spoRowTarget || 'INDENTOR';
                  if (target === 'VENDOR') {
                    handleSpoVendorClarification(record);
                  } else {
                    handleSpoReview(record, 'CHANGE_REQUEST_TO_INTENTOR');
                  }
                }}
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
            {pendingToIndentor ? 'Change Requested' : record.status === 'CHANGE_REQUESTED' ? 'Clarification Sent' : 'Seek Revision'}
          </Button>
        </Popover>
        {pendingToIndentor && (
          <Popover
            content={
              <div style={{ padding: 12 }}>
                <Input.TextArea
                  placeholder="Enter reason for rejecting indentor clarification"
                  rows={3}
                  value={rejectedVendorId === record.vendorId ? rejectComment : ''}
                  onChange={(e) => {
                    setRejectedVendorId(record.vendorId);
                    setRejectComment(e.target.value);
                  }}
                />
                <Button
                  type="primary"
                  danger
                  onClick={() => handleSpoRejectIndentorClarification(record)}
                  style={{ marginTop: 8 }}
                >
                  Submit
                </Button>
              </div>
            }
            title="Reject Indentor Clarification"
            trigger="click"
          >
            <Button size="small" style={{ color: 'red' }}>
              Reject Clarification
            </Button>
          </Popover>
        )}
      </div>
    );
  }
},
...(showRegisteredVendorColumn ? [{
  title: 'Registered Vendor ID',
  key: 'registeredVendor',
  width: 250,
  render: (_, record) => {
    if (isAbove10L) {
      const dv = evalStatus?.committeeVendorVotes?.[record.vendorId]?.find(v => v.voterRole === 'DIRECTOR' && v.decision && v.phase === currentVotePhase);
      if (!dv || dv.decision !== 'ACCEPTED') return <Tag color="default">-</Tag>;
    } else {
      if (record.status !== 'Completed') return <Tag color="default">-</Tag>;
    }
    if (record.registeredVendorId) {
      return <Tag color="green">{record.registeredVendorName} ({record.registeredVendorId})</Tag>;
    }
    return (
      <div style={{ display: 'flex', gap: 4, alignItems: 'center' }}>
        <Select
          showSearch
          placeholder="Select vendor"
          style={{ width: 160 }}
          value={selectedRegisteredVendors[record.vendorId] || undefined}
          onChange={val => setSelectedRegisteredVendors(prev => ({ ...prev, [record.vendorId]: val }))}
          filterOption={(input, option) => {
            const text = Array.isArray(option?.children) ? option.children.join('') : String(option?.children || '');
            return text.toLowerCase().includes(input.toLowerCase());
          }}
        >
          {allRegisteredVendors.map(v => (
            <Option key={v.vendorId} value={v.vendorId}>{v.vendorName} ({v.vendorId})</Option>
          ))}
        </Select>
        <Button size="small" type="primary"
          onClick={() => handleMapRegisteredVendor(tenderId, record.vendorId)}>
          Submit
        </Button>
      </div>
    );
  }
}] : []),

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
      title: 'Document Upload Status',
      key: 'documentUploadStatuss',
      dataIndex: 'documentUploadStatuss',
    },
    {
  title: indentorStatusLabel,
  key: 'indentorStatus',
  dataIndex: 'indentorStatus',
  width: isAbove10L ? 200 : undefined,
  render: (indentorStatus, record) => {
    if (isAbove10L && (isChairman || isDirector)) {
      return (
        <Button size="small" type="primary" ghost
          onClick={() => {
            setMemberResponsesVendorId(record.vendorId);
            setViewedVendorIds(prev => new Set(prev).add(record.vendorId));
            setMemberResponsesModal(true);
          }}>
          View Votes
        </Button>
      );
    }
    if (isAbove10L && (isCommitteeMember || isAssignedExpert)) {
      const myVote = evalStatus?.committeeVendorVotes?.[record.vendorId]
        ?.find(v => String(v.committeeUserId) === String(userId) && v.voterRole === 'MEMBER' && v.phase === currentVotePhase);
      if (myVote?.decision) return <Tag color={myVote.decision === 'ACCEPTED' ? 'green' : 'red'}>{myVote.decision}</Tag>;
      return <Tag color="orange">Pending</Tag>;
    }
    if (indentorStatus === 'CHANGE_REQUESTED') return <Tag color="orange">Pending Clarification</Tag>;
    if (indentorStatus === 'REJECTED' || indentorStatus === 'Rejected') return <Tag color="red">Rejected</Tag>;
    if (indentorStatus === 'ACCEPTED' || indentorStatus === 'Completed') return <Tag color="green">Accepted</Tag>;
    return indentorStatus || 'N/A';
  }
},
    // {
    //   title: indentorStatusLabel,
    //   key: 'indentorStatus',
    //   dataIndex: 'indentorStatus',
    //   render: (indentorStatus) => {
    //     if (indentorStatus === 'CHANGE_REQUESTED') return <Tag color="orange">Pending Clarification</Tag>;
    //     if (indentorStatus === 'REJECTED' || indentorStatus === 'Rejected') return <Tag color="red">Rejected</Tag>;
    //     if (indentorStatus === 'ACCEPTED' || indentorStatus === 'Completed') return <Tag color="green">Accepted</Tag>;
    //     return indentorStatus || 'N/A';
    //   }
    // },
    {
      title: isAbove10L ? 'Chairman Status' : 'Store Purchase Officer Status',
      key: 'sopStatus',
      dataIndex: 'sopStatus',
      render: (sopStatus, record) => {
        if (isAbove10L) {
          const cv = evalStatus?.committeeVendorVotes?.[record.vendorId]?.find(v => v.voterRole === 'CHAIRMAN' && v.decision && v.phase === currentVotePhase);
          if (cv?.decision === 'ACCEPTED') return <Tag color="green">Accepted</Tag>;
          if (cv?.decision === 'REJECTED') return <Tag color="red">Rejected</Tag>;
          return <Tag color="orange">Pending</Tag>;
        }
        if (sopStatus === 'CHANGE_REQUESTED_TO_INTENTOR') return <Tag color="orange">Pending Clarification</Tag>;
        if (sopStatus === 'REJECTED' || sopStatus === 'Rejected') return <Tag color="red">Disqualified</Tag>;
        if (sopStatus === 'ACCEPTED' || sopStatus === 'Completed') return <Tag color="green">Qualified</Tag>;
        return sopStatus || 'N/A';
      }
    },
    ...(isAbove10L ? [{
      title: 'Director Status',
      key: 'singleDirectorStatus',
      width: 140,
      render: (_, record) => {
        const dv = evalStatus?.committeeVendorVotes?.[record.vendorId]?.find(v => v.voterRole === 'DIRECTOR' && v.decision && v.phase === currentVotePhase);
        if (dv?.decision === 'ACCEPTED') return <Tag color="green">Accepted</Tag>;
        if (dv?.decision === 'REJECTED') return <Tag color="red">Rejected</Tag>;
        return <Tag color="orange">Pending</Tag>;
      },
    }] : []),

    ...(isFinancialPhase ? [
    {
      title: 'Indentor/PP Financial Status',
      key: 'financialIndentorStatus',
      dataIndex: 'financialIndentorStatus',
      width: 150,
      render: (val) => {
        if (val === 'CHANGE_REQUESTED') return <Tag color="orange">Pending Clarification</Tag>;
        if (val === 'ACCEPTED' || val === 'Completed') return <Tag color="green">Accepted</Tag>;
        if (val === 'REJECTED' || val === 'Rejected') return <Tag color="red">Rejected</Tag>;
        return val || <Tag>Pending</Tag>;
      }
    },
    {
      title: isAbove10L ? 'Chairman Financial Status' : 'SPO Financial Status',
      key: 'financialSpoStatus',
      dataIndex: 'financialSpoStatus',
      width: 130,
      render: (val, record) => {
        if (isAbove10L) {
          const cv = evalStatus?.committeeVendorVotes?.[record.vendorId]?.find(v => v.voterRole === 'CHAIRMAN' && v.decision && v.phase === currentVotePhase);
          if (cv?.decision === 'ACCEPTED') return <Tag color="green">Accepted</Tag>;
          if (cv?.decision === 'REJECTED') return <Tag color="red">Rejected</Tag>;
          return <Tag color="orange">Pending</Tag>;
        }
        if (val === 'ACCEPTED' || val === 'Completed') return <Tag color="green">Qualified</Tag>;
        if (val === 'REJECTED' || val === 'Rejected') return <Tag color="red">Disqualified</Tag>;
        if (val === 'CHANGE_REQUESTED_TO_INTENTOR') return <Tag color="orange">Pending Clarification</Tag>;
        return val || <Tag>Pending</Tag>;
      }
    },
    ...(isAbove10L ? [{
      title: 'Director Financial Status',
      key: 'singleFinDirectorStatus',
      width: 140,
      render: (_, record) => {
        const dv = evalStatus?.committeeVendorVotes?.[record.vendorId]?.find(v => v.voterRole === 'DIRECTOR' && v.decision && v.phase === currentVotePhase);
        if (dv?.decision === 'ACCEPTED') return <Tag color="green">Accepted</Tag>;
        if (dv?.decision === 'REJECTED') return <Tag color="red">Rejected</Tag>;
        return <Tag color="orange">Pending</Tag>;
      },
    }] : []),
    ] : []),

  ...(showActionButtons ? [
  {
    title: 'Accept',
    key: 'accept',
    render: (_, record) => {
      const status = isFinancialPhase ? record.financialIndentorStatus : record.indentorStatus;
      const techRejected = isFinancialPhase && (record.indentorStatus === 'REJECTED' || record.sopStatus === 'REJECTED');
      if (techRejected) return <Tag color="default">N/A (Technical Rejected)</Tag>;
      const vendorHasIndentorClarif = indentorOpenQuestions.some(q => q.targetVendorId === record.vendorId);
      const vendorClarifPending = record.status === 'CHANGE_REQUESTED' && !vendorHasIndentorClarif;
      const spoRequestedReview = isGemMultiIndentorClarif && record.changeRequestToIndentor;
      return (status === 'ACCEPTED' && !spoRequestedReview) ? (
        <Tag color="green">Accepted</Tag>
      ) : (
        <Button
          onClick={() => handleAccept(record)}
          size="small"
          disabled={
            (status === 'ACCEPTED' && !spoRequestedReview) ||
            status === 'REJECTED' ||
            (vendorClarifPending && !isGemMultiIndentorClarif && !ppIndentorClarifPending) ||
            isGemMultiVendorClarif
          }
          title={isGemMultiVendorClarif ? 'Cannot accept while vendor clarification is pending' : vendorHasIndentorClarif ? 'Respond to clarification for this vendor first' : vendorClarifPending ? 'Cannot accept while clarification is pending for this vendor' : ''}
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
      const techRejected = isFinancialPhase && (record.indentorStatus === 'REJECTED' || record.sopStatus === 'REJECTED');
      if (techRejected) return <Tag color="default">N/A</Tag>;
      const vendorHasIndentorClarif = indentorOpenQuestions.some(q => q.targetVendorId === record.vendorId);
      const vendorClarifPending = record.status === 'CHANGE_REQUESTED' && !vendorHasIndentorClarif;
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
                  (isGemMultiVendorClarif && !ppIsClarifSeeker)
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
            (isGemMultiVendorClarif && !ppIsClarifSeeker)
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
      // ── PP pre-initiate: Limited/Prop, under 10L, not yet initiated ──
    if (showPpPreInitiateClarif) {
      return (
        <Button
          type="link"
          style={{ color: '#fa8c16', padding: 0 }}
          onClick={() => openVendorClarificationModal(record.vendorId, 'PURCHASE_PERSONNEL')}
        >
          Seek Clarification
        </Button>
      );
    }
      // ── PP visibility gate ──
  if (isPurchasePersonnelRole && (
    (evalStatus?.evaluationStatus &&
      !['APPROVED', 'REJECTED'].includes(evalStatus.evaluationStatus) &&
      !(isMultipleIndentEval))
  )) return null;

      const status = isFinancialPhase ? record.financialIndentorStatus : record.indentorStatus;
      const techRejected = isFinancialPhase && (record.indentorStatus === 'REJECTED' || record.sopStatus === 'REJECTED');
      const vendorHasIndentorClarif = indentorOpenQuestions.some(q => q.targetVendorId === record.vendorId);
      const vendorClarifPending = record.status === 'CHANGE_REQUESTED' && !vendorHasIndentorClarif;
      if (techRejected) return <Tag color="default">N/A</Tag>;
      return vendorHasIndentorClarif ? (
        <Tag color="orange">Clarification Pending</Tag>
      ) : vendorClarifPending ? (
        <Tag color="orange">Pending</Tag>
      ) : (
        <Button
          type="link"
          style={{ color: '#fa8c16', padding: 0 }}
          disabled={status === 'REJECTED' || vendorClarifPending}
          onClick={() => openVendorClarificationModal(record.vendorId)}
        >
          Seek Clarification
        </Button>
      );
    },
  },
  ] : []),
...(showRegisteredVendorColumn ? [{
  title: 'Registered Vendor ID',
  key: 'registeredVendor',
  width: 250,
  render: (_, record) => {
    if (isAbove10L) {
      const dv = evalStatus?.committeeVendorVotes?.[record.vendorId]?.find(v => v.voterRole === 'DIRECTOR' && v.decision && v.phase === currentVotePhase);
      if (!dv || dv.decision !== 'ACCEPTED') return <Tag color="default">-</Tag>;
    } else {
      if (record.status !== 'Completed') return <Tag color="default">-</Tag>;
    }
    if (record.registeredVendorId) {
      return <Tag color="green">{record.registeredVendorName} ({record.registeredVendorId})</Tag>;
    }
    return (
      <div style={{ display: 'flex', gap: 4, alignItems: 'center' }}>
        <Select
          showSearch
          placeholder="Select vendor"
          style={{ width: 160 }}
          value={selectedRegisteredVendors[record.vendorId] || undefined}
          onChange={val => setSelectedRegisteredVendors(prev => ({ ...prev, [record.vendorId]: val }))}
          filterOption={(input, option) => {
            const text = Array.isArray(option?.children) ? option.children.join('') : String(option?.children || '');
            return text.toLowerCase().includes(input.toLowerCase());
          }}
        >
          {allRegisteredVendors.map(v => (
            <Option key={v.vendorId} value={v.vendorId}>{v.vendorName} ({v.vendorId})</Option>
          ))}
        </Select>
        <Button size="small" type="primary"
          onClick={() => handleMapRegisteredVendor(tenderId, record.vendorId)}>
          Submit
        </Button>
      </div>
    );
  }
}] : []),

...((showPpPreInitiateClarif || showPpTechLineClarif) && !showActionButtons ? [
  {
    title: 'Seek Clarification',
    key: 'ppLineClarif',
    render: (_, record) => (
      <Button
        type="link"
        style={{ color: '#fa8c16', padding: 0 }}
        disabled={record.status === 'CHANGE_REQUESTED'}
        onClick={() => openVendorClarificationModal(record.vendorId, 'PURCHASE_PERSONNEL')}
      >
        {record.status === 'CHANGE_REQUESTED' ? 'Pending' : 'Seek Clarification'}
      </Button>
    ),
  },
  {
    title: 'Reject',
    key: 'ppLineReject',
    render: (_, record) => {
      const st = record.indentorStatus;
      return st === 'REJECTED' ? (
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
                disabled={st === 'REJECTED'}
              >
                Submit
              </Button>
            </div>
          }
          title="Reject Vendor"
          trigger="click"
        >
          <Button danger type="link" disabled={st === 'REJECTED'}>
            Reject
          </Button>
        </Popover>
      );
    },
  },
] : []),

...(showCommitteeMemberSingleBidActions ? [
  {
    title: 'My Decision',
    key: 'committeeSingleDecision',
    width: 120,
    render: (_, record) => {
      const myVote = evalStatus.committeeVendorVotes?.[record.vendorId]?.find(
        v => String(v.committeeUserId) === String(userId) && v.voterRole === 'MEMBER' && v.phase === currentVotePhase
      );
      if (myVote?.decision) return <Tag color={myVote.decision === 'ACCEPTED' ? 'green' : 'red'}>{myVote.decision}</Tag>;
      return <Tag color="orange">Pending</Tag>;
    },
  },
  {
    title: 'Action',
    key: 'committeeSingleAction',
    width: 220,
    render: (_, record) => {
      const myVote = evalStatus.committeeVendorVotes?.[record.vendorId]?.find(
        v => String(v.committeeUserId) === String(userId) && v.voterRole === 'MEMBER' && v.phase === currentVotePhase
      );
      const myClarifPending = myPendingClarifVendorIds.has(record.vendorId);
      const vendorClarifReview = isChairmanClarifReview && evalStatus?.clarificationTargetVendorId === record.vendorId;
      const myConfirmed = myVote?.confirmed === true;
      const btnDisabled = myClarifPending || vendorClarifReview || myConfirmed;
      return (
        <Space size={4}>
          {myVote?.decision
            ? <Tag color={myVote.decision === 'ACCEPTED' ? 'green' : 'red'}>{myVote.decision}</Tag>
            : <>
                <Button size="small" type="primary" disabled={btnDisabled} onClick={() => handleCommitteeVendorDecision(record.vendorId, 'ACCEPTED')}>Accept</Button>
                <Button size="small" danger disabled={btnDisabled} onClick={() => openVendorRejectModal(record.vendorId, handleCommitteeVendorDecision)}>Reject</Button>
              </>
          }
          <Button size="small" style={{ color: btnDisabled ? undefined : '#fa8c16', borderColor: btnDisabled ? undefined : '#fa8c16' }}
            disabled={btnDisabled}
            onClick={() => openVendorClarificationModal(record.vendorId, 'COMMITTEE_MEMBER')}>Clarify</Button>
        </Space>
      );
    },
  },
] : []),
...(showChairmanInlineActions ? [{
  title: 'Chairman Action',
  key: 'chairmanSingleAction',
  width: 300,
  render: (_, record) => {
    const isChairmanVotingPhase = evalStatus?.evaluationStatus === 'PENDING_CHAIRMAN_VOTING' ||
      (['PENDING_VENDOR_CLARIFICATION', 'PENDING_INDENTOR_CLARIFICATION'].includes(evalStatus?.evaluationStatus) &&
       evalStatus?.clarificationRequestedByRole === 'CHAIRMAN');
    const vendorVotes = evalStatus?.committeeVendorVotes?.[record.vendorId];
    const cv = vendorVotes?.find(v => v.voterRole === 'CHAIRMAN' && v.decision && v.phase === currentVotePhase);
    const viewed = viewedVendorIds.has(record.vendorId);
    const vendorUnderClarif = record.status === 'CHANGE_REQUESTED' || clarificationHistory.some(h => (h.targetVendorId === record.vendorId || (!h.targetVendorId && ['VENDOR','ALL_VENDORS'].includes(h.clarificationTarget))) && ['VENDOR','ALL_VENDORS'].includes(h.clarificationTarget) && !h.responseText) || (evalStatus?.evaluationStatus === 'PENDING_VENDOR_CLARIFICATION' && (!evalStatus?.clarificationTargetVendorId || evalStatus.clarificationTargetVendorId === record.vendorId));
    const voteHandler = isChairmanVotingPhase ? handleChairmanVendorVote : handleChairmanVendorResolve;
    if (cv) {
      return (
        <Space size={4}>
          <Tag color={cv.decision === 'ACCEPTED' ? 'green' : 'red'}>{cv.decision}</Tag>
          {cv.decision === 'ACCEPTED'
            ? <Button size="small" danger onClick={() => openVendorRejectModal(record.vendorId, voteHandler)}>Change to Reject</Button>
            : <Button size="small" type="primary" disabled={vendorUnderClarif} onClick={() => voteHandler(record.vendorId, 'ACCEPTED')}>Change to Accept</Button>}
          <Button size="small" disabled={vendorUnderClarif || cv.decision === 'REJECTED'} style={{ color: (vendorUnderClarif || cv.decision === 'REJECTED') ? undefined : '#fa8c16', borderColor: (vendorUnderClarif || cv.decision === 'REJECTED') ? undefined : '#fa8c16' }}
            onClick={() => openVendorClarificationModal(record.vendorId, 'CHAIRMAN')}>{vendorUnderClarif ? 'Pending Clarification' : 'Clarify'}</Button>
        </Space>
      );
    }
    return (
      <Space size={4}>
        <Button size="small" type="primary" disabled={!viewed || vendorUnderClarif} onClick={() => voteHandler(record.vendorId, 'ACCEPTED')}>Accept</Button>
        <Button size="small" danger disabled={!viewed && !vendorUnderClarif} onClick={() => openVendorRejectModal(record.vendorId, voteHandler)}>Reject</Button>
        <Button size="small" disabled={!viewed || vendorUnderClarif} style={{ color: (viewed && !vendorUnderClarif) ? '#fa8c16' : undefined, borderColor: (viewed && !vendorUnderClarif) ? '#fa8c16' : undefined }}
          onClick={() => openVendorClarificationModal(record.vendorId, 'CHAIRMAN')}>{vendorUnderClarif ? 'Pending Clarification' : 'Clarify'}</Button>
      </Space>
    );
  },
}] : []),

...(showDirectorInlineActions ? [{
  title: 'Director Action',
  key: 'directorSingleAction',
  width: 280,
  render: (_, record) => {
    const dv = evalStatus?.committeeVendorVotes?.[record.vendorId]?.find(v => v.voterRole === 'DIRECTOR' && v.decision && v.phase === currentVotePhase);
    const vendorUnderClarif = record.status === 'CHANGE_REQUESTED' || clarificationHistory.some(h => (h.targetVendorId === record.vendorId || (!h.targetVendorId && ['VENDOR','ALL_VENDORS'].includes(h.clarificationTarget))) && ['VENDOR','ALL_VENDORS'].includes(h.clarificationTarget) && !h.responseText) || (evalStatus?.evaluationStatus === 'PENDING_VENDOR_CLARIFICATION' && (!evalStatus?.clarificationTargetVendorId || evalStatus.clarificationTargetVendorId === record.vendorId));
    if (dv) {
      return (
        <Space size={4}>
          <Tag color={dv.decision === 'ACCEPTED' ? 'green' : 'red'}>{dv.decision}</Tag>
          {dv.decision === 'ACCEPTED'
            ? <Button size="small" danger onClick={() => openVendorRejectModal(record.vendorId, handleDirectorVendorVote)}>Change to Reject</Button>
            : <Button size="small" type="primary" disabled={vendorUnderClarif} onClick={() => handleDirectorVendorVote(record.vendorId, 'ACCEPTED')}>Change to Accept</Button>}
          <Button size="small" disabled={vendorUnderClarif || dv.decision === 'REJECTED'} style={{ color: (vendorUnderClarif || dv.decision === 'REJECTED') ? undefined : '#fa8c16', borderColor: (vendorUnderClarif || dv.decision === 'REJECTED') ? undefined : '#fa8c16' }}
            onClick={() => openVendorClarificationModal(record.vendorId, 'DIRECTOR')}>{vendorUnderClarif ? 'Pending Clarification' : 'Clarify'}</Button>
        </Space>
      );
    }
    return (
      <Space size={4}>
        <Button size="small" type="primary" disabled={vendorUnderClarif} onClick={() => handleDirectorVendorVote(record.vendorId, 'ACCEPTED')}>Accept</Button>
        <Button size="small" danger onClick={() => openVendorRejectModal(record.vendorId, handleDirectorVendorVote)}>Reject</Button>
        <Button size="small" disabled={vendorUnderClarif} style={{ color: vendorUnderClarif ? undefined : '#fa8c16', borderColor: vendorUnderClarif ? undefined : '#fa8c16' }}
          onClick={() => openVendorClarificationModal(record.vendorId, 'DIRECTOR')}>{vendorUnderClarif ? 'Pending Clarification' : 'Clarify'}</Button>
      </Space>
    );
  },
}] : []),
  ];
}

// ═══════════════════════════════════════════════════════════════════
// ── Double Bid: Dedicated Column Arrays (no isFinancialPhase ternaries) ──
// ═══════════════════════════════════════════════════════════════════

// ── Vendor info columns shared by all double-bid tables ──
const vendorInfoColumns = [
  {
    title: 'Vendor ID',
    dataIndex: 'vendorId',
    key: 'vendorId',
    width: 120,
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
    ),
  },
  {
    title: 'Vendor Name',
    dataIndex: 'vendorName',
    key: 'vendorName',
    width: 150,
  },
];

// ── Technical Bid Columns (Indentor / Purchase Personnel) ──
const doubleBidTechColumns = [
  ...vendorInfoColumns,
  {
    title: 'Technical Document',
    key: 'techDoc',
    width: 130,
    render: (_, record) =>
      record.quotationFileName ? (
        <a
          href={`${baseURL}/file/view/Tender/${record.quotationFileName}`}
          target="_blank"
          rel="noopener noreferrer"
        >
          View
        </a>
      ) : 'No File',
  },
  ...(showVendorResponse
    ? [{
        title: 'Vendor Response',
        dataIndex: 'vendorResponse',
        key: 'vendorResponse',
      }]
    : []),
  ...(showClarificationFile
    ? [{
        title: 'Clarification File',
        dataIndex: 'clarificationFileName',
        key: 'clarificationFileName',
        render: (file, record) => {
          const latest = [...clarificationHistory]
            .filter(h => h.targetVendorId === record.vendorId)
            .sort((a, b) => b.roundNumber - a.roundNumber)[0];
          return latest?.responseFileName ? (
            <a href={`${baseURL}/file/view/Tender/${latest.responseFileName}`} target="_blank" rel="noopener noreferrer">View</a>
          ) : null;
        },
      }]
    : []),
  {
    title: indentorStatusLabel,
    key: 'indentorStatus',
    dataIndex: 'indentorStatus',
    width: isAbove10L ? 200 : 150,
    render: (val, record) => {
      if (isAbove10L) {
        const vendorVotes = evalStatus?.committeeVendorVotes?.[record.vendorId];
        const myVote = vendorVotes?.find(v => String(v.committeeUserId) === String(userId) && v.voterRole === 'MEMBER' && v.phase === 'TECHNICAL');
        if (isChairman || isAssignedExpert || isCommitteeMember || isDirector) {
          return (
            <Button size="small" type="primary" ghost
              onClick={() => { setMemberResponsesVendorId(record.vendorId); setViewedVendorIds(prev => new Set(prev).add(record.vendorId)); setMemberResponsesModal(true); }}>
              View Votes
            </Button>
          );
        }
        if (isCommitteeMember && myVote?.decision) {
          return <Tag color={myVote.decision === 'ACCEPTED' ? 'green' : 'red'}>{myVote.decision}</Tag>;
        }
        if (isCommitteeMember) return <Tag color="orange">Pending</Tag>;
      }
      if (val === 'CHANGE_REQUESTED') return <Tag color="orange">Pending Clarification</Tag>;
      if (val === 'REJECTED' || val === 'Rejected') return <Tag color="red">Rejected</Tag>;
      if (val === 'ACCEPTED' || val === 'Completed') return <Tag color="green">Accepted</Tag>;
      return val || 'N/A';
    },
  },
  {
    title: isAbove10L ? 'Chairman Status' : 'SPO Status',
    key: 'sopStatus',
    width: 150,
    dataIndex: 'sopStatus',
    render: (val, record) => {
      if (isAbove10L) {
        const cv = evalStatus?.committeeVendorVotes?.[record.vendorId]?.find(v => v.voterRole === 'CHAIRMAN' && v.decision && v.phase === 'TECHNICAL');
        if (cv?.decision === 'ACCEPTED') return <Tag color="green">Accepted</Tag>;
        if (cv?.decision === 'REJECTED') return <Tag color="red">Rejected</Tag>;
        return <Tag color="orange">Pending</Tag>;
      }
      if (val === 'CHANGE_REQUESTED_TO_INTENTOR') return <Tag color="orange">Pending Clarification</Tag>;
      if (val === 'REJECTED' || val === 'Rejected') return <Tag color="red">Disqualified</Tag>;
      if (val === 'ACCEPTED' || val === 'Completed') return <Tag color="green">Qualified</Tag>;
      return val || 'N/A';
    },
  },
  ...(isAbove10L ? [{
    title: 'Director Status',
    key: 'directorStatus',
    width: 140,
    render: (_, record) => {
      const dv = evalStatus?.committeeVendorVotes?.[record.vendorId]?.find(v => v.voterRole === 'DIRECTOR' && v.decision && v.phase === 'TECHNICAL');
      if (dv?.decision === 'ACCEPTED') return <Tag color="green">Accepted</Tag>;
      if (dv?.decision === 'REJECTED') return <Tag color="red">Rejected</Tag>;
      return <Tag color="orange">Pending</Tag>;
    },
  }] : []),
  ...(showChairmanInlineActions ? [{
    title: 'Chairman Action',
    key: 'chairmanAction',
    width: 300,
    render: (_, record) => {
      const isChairmanVotingPhase = evalStatus?.evaluationStatus === 'PENDING_CHAIRMAN_VOTING' ||
        (['PENDING_VENDOR_CLARIFICATION', 'PENDING_INDENTOR_CLARIFICATION'].includes(evalStatus?.evaluationStatus) &&
         evalStatus?.clarificationRequestedByRole === 'CHAIRMAN');
      const vendorVotes = evalStatus?.committeeVendorVotes?.[record.vendorId];
      const cv = vendorVotes?.find(v => v.voterRole === 'CHAIRMAN' && v.decision && v.phase === 'TECHNICAL');
      const viewed = viewedVendorIds.has(record.vendorId);
      const vendorUnderClarif = record.status === 'CHANGE_REQUESTED' || clarificationHistory.some(h => (h.targetVendorId === record.vendorId || (!h.targetVendorId && ['VENDOR','ALL_VENDORS'].includes(h.clarificationTarget))) && ['VENDOR','ALL_VENDORS'].includes(h.clarificationTarget) && !h.responseText) || (evalStatus?.evaluationStatus === 'PENDING_VENDOR_CLARIFICATION' && (!evalStatus?.clarificationTargetVendorId || evalStatus.clarificationTargetVendorId === record.vendorId));
      const voteHandler = isChairmanVotingPhase ? handleChairmanVendorVote : handleChairmanVendorResolve;
      if (cv) {
        return (
          <Space size={4}>
            <Tag color={cv.decision === 'ACCEPTED' ? 'green' : 'red'}>{cv.decision}</Tag>
            {cv.decision === 'ACCEPTED'
              ? <Button size="small" danger onClick={() => openVendorRejectModal(record.vendorId, voteHandler)}>Change to Reject</Button>
              : <Button size="small" type="primary" disabled={vendorUnderClarif} onClick={() => voteHandler(record.vendorId, 'ACCEPTED')}>Change to Accept</Button>}
            <Button size="small" disabled={vendorUnderClarif || cv.decision === 'REJECTED'} style={{ color: (vendorUnderClarif || cv.decision === 'REJECTED') ? undefined : '#fa8c16', borderColor: (vendorUnderClarif || cv.decision === 'REJECTED') ? undefined : '#fa8c16' }}
              onClick={() => openVendorClarificationModal(record.vendorId, 'CHAIRMAN')}>{vendorUnderClarif ? 'Pending Clarification' : 'Clarify'}</Button>
          </Space>
        );
      }
      return (
        <Space size={4}>
          <Button size="small" type="primary" disabled={!viewed || vendorUnderClarif} onClick={() => voteHandler(record.vendorId, 'ACCEPTED')}>Accept</Button>
          <Button size="small" danger disabled={!viewed && !vendorUnderClarif} onClick={() => openVendorRejectModal(record.vendorId, voteHandler)}>Reject</Button>
          <Button size="small" disabled={!viewed || vendorUnderClarif} style={{ color: (viewed && !vendorUnderClarif) ? '#fa8c16' : undefined, borderColor: (viewed && !vendorUnderClarif) ? '#fa8c16' : undefined }}
            onClick={() => openVendorClarificationModal(record.vendorId, 'CHAIRMAN')}>{vendorUnderClarif ? 'Pending Clarification' : 'Clarify'}</Button>
        </Space>
      );
    },
  }] : []),
  ...(showDirectorInlineActions ? [{
    title: 'Director Action',
    key: 'directorAction',
    width: 280,
    render: (_, record) => {
      const dv = evalStatus?.committeeVendorVotes?.[record.vendorId]?.find(v => v.voterRole === 'DIRECTOR' && v.decision && v.phase === 'TECHNICAL');
      const vendorUnderClarif = record.status === 'CHANGE_REQUESTED' || clarificationHistory.some(h => (h.targetVendorId === record.vendorId || (!h.targetVendorId && ['VENDOR','ALL_VENDORS'].includes(h.clarificationTarget))) && ['VENDOR','ALL_VENDORS'].includes(h.clarificationTarget) && !h.responseText) || (evalStatus?.evaluationStatus === 'PENDING_VENDOR_CLARIFICATION' && (!evalStatus?.clarificationTargetVendorId || evalStatus.clarificationTargetVendorId === record.vendorId));
      if (dv) {
        return (
          <Space size={4}>
            <Tag color={dv.decision === 'ACCEPTED' ? 'green' : 'red'}>{dv.decision}</Tag>
            {dv.decision === 'ACCEPTED'
              ? <Button size="small" danger onClick={() => openVendorRejectModal(record.vendorId, handleDirectorVendorVote)}>Change to Reject</Button>
              : <Button size="small" type="primary" disabled={vendorUnderClarif} onClick={() => handleDirectorVendorVote(record.vendorId, 'ACCEPTED')}>Change to Accept</Button>}
            <Button size="small" disabled={vendorUnderClarif || dv.decision === 'REJECTED'} style={{ color: (vendorUnderClarif || dv.decision === 'REJECTED') ? undefined : '#fa8c16', borderColor: (vendorUnderClarif || dv.decision === 'REJECTED') ? undefined : '#fa8c16' }}
              onClick={() => openVendorClarificationModal(record.vendorId, 'DIRECTOR')}>{vendorUnderClarif ? 'Pending Clarification' : 'Clarify'}</Button>
          </Space>
        );
      }
      return (
        <Space size={4}>
          <Button size="small" type="primary" disabled={vendorUnderClarif} onClick={() => handleDirectorVendorVote(record.vendorId, 'ACCEPTED')}>Accept</Button>
          <Button size="small" danger onClick={() => openVendorRejectModal(record.vendorId, handleDirectorVendorVote)}>Reject</Button>
          <Button size="small" disabled={vendorUnderClarif} style={{ color: vendorUnderClarif ? undefined : '#fa8c16', borderColor: vendorUnderClarif ? undefined : '#fa8c16' }}
            onClick={() => openVendorClarificationModal(record.vendorId, 'DIRECTOR')}>{vendorUnderClarif ? 'Pending Clarification' : 'Clarify'}</Button>
        </Space>
      );
    },
  }] : []),
  ...(showTechActionButtons
    ? [
        {
          title: 'Accept',
          key: 'techAccept',
          render: (_, record) => {
            const st = record.indentorStatus;
            const vendorHasIndentorClarif = indentorOpenQuestions.some(q => q.targetVendorId === record.vendorId);
            const vendorClarifPending = record.status === 'CHANGE_REQUESTED' && !vendorHasIndentorClarif;
            const spoRequestedReview = isGemMultiIndentorClarif && record.changeRequestToIndentor;
            return (st === 'ACCEPTED' && !spoRequestedReview) ? (
              <Tag color="green">Accepted</Tag>
            ) : (
              <Button
                size="small"
                onClick={() => handleAccept(record)}
                disabled={
                  (st === 'ACCEPTED' && !spoRequestedReview) || st === 'REJECTED' ||
                  (vendorClarifPending && !isGemMultiIndentorClarif && !ppIndentorClarifPending) ||
                  isGemMultiVendorClarif
                }
                title={isGemMultiVendorClarif ? 'Cannot accept while vendor clarification is pending' : vendorHasIndentorClarif ? 'Respond to clarification for this vendor first' : ''}
              >
                Accept
              </Button>
            );
          },
        },
        {
          title: 'Reject',
          key: 'techReject',
          render: (_, record) => {
            const st = record.indentorStatus;
            const vendorHasIndentorClarif = indentorOpenQuestions.some(q => q.targetVendorId === record.vendorId);
            const vendorClarifPending = record.status === 'CHANGE_REQUESTED' && !vendorHasIndentorClarif;
            return st === 'REJECTED' ? (
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
                      disabled={st === 'REJECTED' || (isGemMultiVendorClarif && !ppIsClarifSeeker)}
                    >
                      Submit
                    </Button>
                  </div>
                }
                title="Reject Vendor"
                trigger="click"
              >
                <Button danger type="link" disabled={
                  st === 'REJECTED' ||
                  (isGemMultiVendorClarif && !ppIsClarifSeeker)
                }>
                  Reject
                </Button>
              </Popover>
            );
          },
        },
        {
          title: 'Seek Clarification',
          key: 'techClarif',
          render: (_, record) => {
            // ── PP visibility gate ──
  if (isPurchasePersonnelRole && (
    (evalStatus?.evaluationStatus &&
      !['APPROVED', 'REJECTED'].includes(evalStatus.evaluationStatus) &&
      !(isMultipleIndentEval))
  )) return null;
            const st = record.indentorStatus;
            const vendorHasIndentorClarif = indentorOpenQuestions.some(q => q.targetVendorId === record.vendorId);
            const vendorClarifPending = record.status === 'CHANGE_REQUESTED' && !vendorHasIndentorClarif;
            return vendorHasIndentorClarif ? (
              <Tag color="orange">Clarification Pending</Tag>
            ) : (vendorClarifPending || record.changeRequestToIndentor) ? (
              <Tag color="orange">Pending</Tag>
            ) : (
              <Button
                type="link"
                style={{ color: '#fa8c16', padding: 0 }}
                disabled={st === 'REJECTED' || vendorClarifPending || record.changeRequestToIndentor}
                onClick={() => openVendorClarificationModal(record.vendorId)}
              >
                Seek Clarification
              </Button>
            );
          },
        },
      ]
    : []),
  ...(showRegisteredVendorColumn ? [{
    title: 'Registered Vendor ID',
    key: 'registeredVendor',
    width: 250,
    render: (_, record) => {
      if (isAbove10L) {
        const dv = evalStatus?.committeeVendorVotes?.[record.vendorId]?.find(v => v.voterRole === 'DIRECTOR' && v.decision && v.phase === currentVotePhase);
        if (!dv || dv.decision !== 'ACCEPTED') return <Tag color="default">-</Tag>;
      } else {
        if (record.status !== 'Completed') return <Tag color="default">-</Tag>;
      }
      if (record.registeredVendorId) {
        return <Tag color="green">{record.registeredVendorName} ({record.registeredVendorId})</Tag>;
      }
      return (
        <div style={{ display: 'flex', gap: 4, alignItems: 'center' }}>
          <Select showSearch placeholder="Select vendor" style={{ width: 160 }}
            value={selectedRegisteredVendors[record.vendorId] || undefined}
            onChange={val => setSelectedRegisteredVendors(prev => ({ ...prev, [record.vendorId]: val }))}
            filterOption={(input, option) => {
              const text = Array.isArray(option?.children) ? option.children.join('') : String(option?.children || '');
              return text.toLowerCase().includes(input.toLowerCase());
            }}>
            {allRegisteredVendors.map(v => (
              <Option key={v.vendorId} value={v.vendorId}>{v.vendorName} ({v.vendorId})</Option>
            ))}
          </Select>
          <Button size="small" type="primary" onClick={() => handleMapRegisteredVendor(tenderId, record.vendorId)}>Submit</Button>
        </div>
      );
    },
  }] : []),
  ...(showPpTechLineClarif && !showActionButtons
  ? [
      {
        title: 'Seek Clarification',
        key: 'ppTechLineClarif',
        render: (_, record) => (
          <Button
            type="link"
            style={{ color: '#fa8c16', padding: 0 }}
            disabled={record.status === 'CHANGE_REQUESTED'}
            onClick={() => openVendorClarificationModal(record.vendorId, 'PURCHASE_PERSONNEL')}
          >
            {record.status === 'CHANGE_REQUESTED' ? 'Pending' : 'Seek Clarification'}
          </Button>
        ),
      },
      {
        title: 'Reject',
        key: 'ppTechLineReject',
        render: (_, record) => {
          const st = record.indentorStatus;
          return st === 'REJECTED' ? (
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
                    disabled={st === 'REJECTED'}
                  >
                    Submit
                  </Button>
                </div>
              }
              title="Reject Vendor"
              trigger="click"
            >
              <Button danger type="link" disabled={st === 'REJECTED'}>
                Reject
              </Button>
            </Popover>
          );
        },
      },
    ]
  : []),
  ...(showCommitteeMemberTechActions ? [
    {
      title: 'My Decision',
      key: 'committeeTechDecision',
      width: 120,
      render: (_, record) => {
        const myVote = evalStatus.committeeVendorVotes?.[record.vendorId]?.find(
          v => String(v.committeeUserId) === String(userId) && v.voterRole === 'MEMBER' && v.phase === 'TECHNICAL'
        );
        if (myVote?.decision) return <Tag color={myVote.decision === 'ACCEPTED' ? 'green' : 'red'}>{myVote.decision}</Tag>;
        return <Tag color="orange">Pending</Tag>;
      },
    },
    {
      title: 'Action',
      key: 'committeeTechAction',
      width: 220,
      render: (_, record) => {
        const myVote = evalStatus.committeeVendorVotes?.[record.vendorId]?.find(
          v => String(v.committeeUserId) === String(userId) && v.voterRole === 'MEMBER' && v.phase === 'TECHNICAL'
        );
        const myClarifPending = myPendingClarifVendorIds.has(record.vendorId);
        const vendorClarifReview = isChairmanClarifReview && evalStatus?.clarificationTargetVendorId === record.vendorId;
        const myConfirmed = myVote?.confirmed === true;
        const btnDisabled = myClarifPending || vendorClarifReview || myConfirmed;
        return (
          <Space size={4}>
            {myVote?.decision
              ? <Tag color={myVote.decision === 'ACCEPTED' ? 'green' : 'red'}>{myVote.decision}</Tag>
              : <>
                  <Button size="small" type="primary" disabled={btnDisabled} onClick={() => handleCommitteeVendorDecision(record.vendorId, 'ACCEPTED')}>Accept</Button>
                  <Button size="small" danger disabled={btnDisabled} onClick={() => openVendorRejectModal(record.vendorId, handleCommitteeVendorDecision)}>Reject</Button>
                </>
            }
            <Button size="small" style={{ color: btnDisabled ? undefined : '#fa8c16', borderColor: btnDisabled ? undefined : '#fa8c16' }}
              disabled={btnDisabled}
              onClick={() => openVendorClarificationModal(record.vendorId, 'COMMITTEE_MEMBER')}>Clarify</Button>
          </Space>
        );
      },
    },
  ] : []),
];

// ── Financial Bid Columns (Indentor / Purchase Personnel) ──
const doubleBidFinColumns = [
  ...vendorInfoColumns,
  {
    title: 'Financial Document',
    dataIndex: 'priceBidFileName',
    key: 'priceBidFileName',
    width: 130,
    render: (fileName) =>
      fileName ? (
        <a href={`${baseURL}/file/view/Tender/${fileName}`} target="_blank" rel="noopener noreferrer">View</a>
      ) : 'No File',
  },
  ...(showVendorResponse
    ? [{
        title: 'Vendor Response',
        dataIndex: 'vendorResponse',
        key: 'finVendorResponse',
      }]
    : []),
  ...(showClarificationFile
    ? [{
        title: 'Clarification File',
        dataIndex: 'clarificationFileName',
        key: 'finClarificationFile',
        render: (file, record) => {
          const latest = [...clarificationHistory]
            .filter(h => h.targetVendorId === record.vendorId)
            .sort((a, b) => b.roundNumber - a.roundNumber)[0];
          return latest?.responseFileName ? (
            <a href={`${baseURL}/file/view/Tender/${latest.responseFileName}`} target="_blank" rel="noopener noreferrer">View</a>
          ) : null;
        },
      }]
    : []),
  {
    title: indentorStatusLabel,
    key: 'financialIndentorStatus',
    dataIndex: 'financialIndentorStatus',
    width: isAbove10L ? 200 : 150,
    render: (val, record) => {
      if (isAbove10L) {
        const vendorVotes = evalStatus?.committeeVendorVotes?.[record.vendorId];
        const myVote = vendorVotes?.find(v => String(v.committeeUserId) === String(userId) && v.voterRole === 'MEMBER' && v.phase === 'FINANCIAL');
        if (isChairman || isAssignedExpert || isCommitteeMember || isDirector) {
          return (
            <Button size="small" type="primary" ghost
              onClick={() => { setMemberResponsesVendorId(record.vendorId); setViewedVendorIds(prev => new Set(prev).add(record.vendorId)); setMemberResponsesModal(true); }}>
              View Votes
            </Button>
          );
        }
        if (isCommitteeMember && myVote?.decision) {
          return <Tag color={myVote.decision === 'ACCEPTED' ? 'green' : 'red'}>{myVote.decision}</Tag>;
        }
        if (isCommitteeMember) return <Tag color="orange">Pending</Tag>;
      }
      if (val === 'ACCEPTED' || val === 'Completed') return <Tag color="green">Accepted</Tag>;
      if (val === 'REJECTED' || val === 'Rejected') return <Tag color="red">Rejected</Tag>;
      if (val === 'CHANGE_REQUESTED') return <Tag color="orange">Pending Clarification</Tag>;
      return val || <Tag>Pending</Tag>;
    },
  },
  {
    title: isAbove10L ? 'Chairman Status' : 'SPO Status',
    key: 'financialSpoStatus',
    dataIndex: 'financialSpoStatus',
    width: 150,
    render: (val, record) => {
      if (isAbove10L) {
        const cv = evalStatus?.committeeVendorVotes?.[record.vendorId]?.find(v => v.voterRole === 'CHAIRMAN' && v.decision && v.phase === 'FINANCIAL');
        if (cv?.decision === 'ACCEPTED') return <Tag color="green">Accepted</Tag>;
        if (cv?.decision === 'REJECTED') return <Tag color="red">Rejected</Tag>;
        return <Tag color="orange">Pending</Tag>;
      }
      if (val === 'ACCEPTED' || val === 'Completed') return <Tag color="green">Qualified</Tag>;
      if (val === 'REJECTED' || val === 'Rejected') return <Tag color="red">Disqualified</Tag>;
      if (val === 'CHANGE_REQUESTED_TO_INTENTOR') return <Tag color="orange">Pending Clarification</Tag>;
      return val || <Tag>Pending</Tag>;
    },
  },
  ...(isAbove10L ? [{
    title: 'Director Status',
    key: 'finDirectorStatus',
    width: 140,
    render: (_, record) => {
      const dv = evalStatus?.committeeVendorVotes?.[record.vendorId]?.find(v => v.voterRole === 'DIRECTOR' && v.decision && v.phase === 'FINANCIAL');
      if (dv?.decision === 'ACCEPTED') return <Tag color="green">Accepted</Tag>;
      if (dv?.decision === 'REJECTED') return <Tag color="red">Rejected</Tag>;
      return <Tag color="orange">Pending</Tag>;
    },
  }] : []),
  ...(showFinActionButtons
    ? [
        {
          title: 'Accept',
          key: 'finAccept',
          render: (_, record) => {
            const st = record.financialIndentorStatus;
            const vendorHasIndentorClarif = indentorOpenQuestions.some(q => q.targetVendorId === record.vendorId);
            const vendorClarifPending = record.status === 'CHANGE_REQUESTED' && !vendorHasIndentorClarif;
            const spoRequestedReview = isGemMultiIndentorClarif && record.changeRequestToIndentor;
            return (st === 'ACCEPTED' && !spoRequestedReview) ? (
              <Tag color="green">Accepted</Tag>
            ) : (
              <Button
                size="small"
                onClick={() => handleAccept(record)}
                disabled={
                  (st === 'ACCEPTED' && !spoRequestedReview) || st === 'REJECTED' ||
                  (vendorClarifPending && !isGemMultiIndentorClarif && !ppIndentorClarifPending) ||
                  isGemMultiVendorClarif
                }
                title={isGemMultiVendorClarif ? 'Cannot accept while vendor clarification is pending' : vendorHasIndentorClarif ? 'Respond to clarification for this vendor first' : ''}
              >
                Accept
              </Button>
            );
          },
        },
        {
          title: 'Reject',
          key: 'finReject',
          render: (_, record) => {
            const st = record.financialIndentorStatus;
            const vendorHasIndentorClarif = indentorOpenQuestions.some(q => q.targetVendorId === record.vendorId);
            const vendorClarifPending = record.status === 'CHANGE_REQUESTED' && !vendorHasIndentorClarif;
            return st === 'REJECTED' ? (
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
                      disabled={st === 'REJECTED' || (isGemMultiVendorClarif && !ppIsClarifSeeker)}
                    >
                      Submit
                    </Button>
                  </div>
                }
                title="Reject Vendor"
                trigger="click"
              >
                <Button danger type="link" disabled={
                  st === 'REJECTED' ||
                  (isGemMultiVendorClarif && !ppIsClarifSeeker)
                }>
                  Reject
                </Button>
              </Popover>
            );
          },
        },
        {
          title: 'Seek Clarification',
          key: 'finClarif',
          render: (_, record) => {
            // ── PP visibility gate ──
  if (isPurchasePersonnelRole && (
    (evalStatus?.evaluationStatus &&
      !['APPROVED', 'REJECTED'].includes(evalStatus.evaluationStatus) &&
      !(isMultipleIndentEval))
  )) return null;
            const st = record.financialIndentorStatus;
            const vendorHasIndentorClarif = indentorOpenQuestions.some(q => q.targetVendorId === record.vendorId);
            const vendorClarifPending = record.status === 'CHANGE_REQUESTED' && !vendorHasIndentorClarif;
            return vendorHasIndentorClarif ? (
              <Tag color="orange">Clarification Pending</Tag>
            ) : vendorClarifPending ? (
              <Tag color="orange">Pending</Tag>
            ) : (
              <Button
                type="link"
                style={{ color: '#fa8c16', padding: 0 }}
                disabled={st === 'REJECTED' || vendorClarifPending}
                onClick={() => openVendorClarificationModal(record.vendorId)}
              >
                Seek Clarification
              </Button>
            );
          },
        },
      ]
    : []),
    ...(showPpFinLineClarif
  ? [
      {
        title: 'Seek Clarification',
        key: 'ppFinLineClarif',
        render: (_, record) => {
          const vendorClarifSent = record.financialStatus === 'CHANGE_REQUESTED' || record.status === 'CHANGE_REQUESTED';
          return (
          <Button
            type="link"
            style={{ color: '#fa8c16', padding: 0 }}
            disabled={vendorClarifSent}
            onClick={() => openVendorClarificationModal(record.vendorId, 'PURCHASE_PERSONNEL')}
          >
            {vendorClarifSent ? 'Pending' : 'Seek Clarification'}
          </Button>
          );
        },
      },
      {
        title: 'Reject',
        key: 'ppFinLineReject',
        render: (_, record) => {
          const techRejected = record.indentorStatus === 'REJECTED' || record.sopStatus === 'REJECTED';
          if (techRejected) return <Tag color="default">N/A</Tag>;
          const st = record.financialIndentorStatus;
          return st === 'REJECTED' ? (
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
                    disabled={st === 'REJECTED'}
                  >
                    Submit
                  </Button>
                </div>
              }
              title="Reject Vendor"
              trigger="click"
            >
              <Button danger type="link" disabled={st === 'REJECTED'}>
                Reject
              </Button>
            </Popover>
          );
        },
      },
    ]
  : []),
  ...(showChairmanInlineActions ? [{
    title: 'Chairman Action',
    key: 'chairmanFinAction',
    width: 300,
    render: (_, record) => {
      const isChairmanVotingPhase = evalStatus?.evaluationStatus === 'PENDING_CHAIRMAN_VOTING' ||
        (['PENDING_VENDOR_CLARIFICATION', 'PENDING_INDENTOR_CLARIFICATION'].includes(evalStatus?.evaluationStatus) &&
         evalStatus?.clarificationRequestedByRole === 'CHAIRMAN');
      const vendorVotes = evalStatus?.committeeVendorVotes?.[record.vendorId];
      const cv = vendorVotes?.find(v => v.voterRole === 'CHAIRMAN' && v.decision && v.phase === 'FINANCIAL');
      const viewed = viewedVendorIds.has(record.vendorId);
      const vendorUnderClarif = record.status === 'CHANGE_REQUESTED' || clarificationHistory.some(h => (h.targetVendorId === record.vendorId || (!h.targetVendorId && ['VENDOR','ALL_VENDORS'].includes(h.clarificationTarget))) && ['VENDOR','ALL_VENDORS'].includes(h.clarificationTarget) && !h.responseText) || (evalStatus?.evaluationStatus === 'PENDING_VENDOR_CLARIFICATION' && (!evalStatus?.clarificationTargetVendorId || evalStatus.clarificationTargetVendorId === record.vendorId));
      const voteHandler = isChairmanVotingPhase ? handleChairmanVendorVote : handleChairmanVendorResolve;
      if (cv) {
        return (
          <Space size={4}>
            <Tag color={cv.decision === 'ACCEPTED' ? 'green' : 'red'}>{cv.decision}</Tag>
            {cv.decision === 'ACCEPTED'
              ? <Button size="small" danger onClick={() => openVendorRejectModal(record.vendorId, voteHandler)}>Change to Reject</Button>
              : <Button size="small" type="primary" disabled={vendorUnderClarif} onClick={() => voteHandler(record.vendorId, 'ACCEPTED')}>Change to Accept</Button>}
            <Button size="small" disabled={vendorUnderClarif || cv.decision === 'REJECTED'} style={{ color: (vendorUnderClarif || cv.decision === 'REJECTED') ? undefined : '#fa8c16', borderColor: (vendorUnderClarif || cv.decision === 'REJECTED') ? undefined : '#fa8c16' }}
              onClick={() => openVendorClarificationModal(record.vendorId, 'CHAIRMAN')}>{vendorUnderClarif ? 'Pending Clarification' : 'Clarify'}</Button>
          </Space>
        );
      }
      return (
        <Space size={4}>
          <Button size="small" type="primary" disabled={!viewed || vendorUnderClarif} onClick={() => voteHandler(record.vendorId, 'ACCEPTED')}>Accept</Button>
          <Button size="small" danger disabled={!viewed && !vendorUnderClarif} onClick={() => openVendorRejectModal(record.vendorId, voteHandler)}>Reject</Button>
          <Button size="small" disabled={!viewed || vendorUnderClarif} style={{ color: (viewed && !vendorUnderClarif) ? '#fa8c16' : undefined, borderColor: (viewed && !vendorUnderClarif) ? '#fa8c16' : undefined }}
            onClick={() => openVendorClarificationModal(record.vendorId, 'CHAIRMAN')}>{vendorUnderClarif ? 'Pending Clarification' : 'Clarify'}</Button>
        </Space>
      );
    },
  }] : []),
  ...(showDirectorInlineActions ? [{
    title: 'Director Action',
    key: 'directorFinAction',
    width: 280,
    render: (_, record) => {
      const dv = evalStatus?.committeeVendorVotes?.[record.vendorId]?.find(v => v.voterRole === 'DIRECTOR' && v.decision && v.phase === 'FINANCIAL');
      const vendorUnderClarif = record.status === 'CHANGE_REQUESTED' || clarificationHistory.some(h => (h.targetVendorId === record.vendorId || (!h.targetVendorId && ['VENDOR','ALL_VENDORS'].includes(h.clarificationTarget))) && ['VENDOR','ALL_VENDORS'].includes(h.clarificationTarget) && !h.responseText) || (evalStatus?.evaluationStatus === 'PENDING_VENDOR_CLARIFICATION' && (!evalStatus?.clarificationTargetVendorId || evalStatus.clarificationTargetVendorId === record.vendorId));
      if (dv) {
        return (
          <Space size={4}>
            <Tag color={dv.decision === 'ACCEPTED' ? 'green' : 'red'}>{dv.decision}</Tag>
            {dv.decision === 'ACCEPTED'
              ? <Button size="small" danger onClick={() => openVendorRejectModal(record.vendorId, handleDirectorVendorVote)}>Change to Reject</Button>
              : <Button size="small" type="primary" disabled={vendorUnderClarif} onClick={() => handleDirectorVendorVote(record.vendorId, 'ACCEPTED')}>Change to Accept</Button>}
            <Button size="small" disabled={vendorUnderClarif || dv.decision === 'REJECTED'} style={{ color: (vendorUnderClarif || dv.decision === 'REJECTED') ? undefined : '#fa8c16', borderColor: (vendorUnderClarif || dv.decision === 'REJECTED') ? undefined : '#fa8c16' }}
              onClick={() => openVendorClarificationModal(record.vendorId, 'DIRECTOR')}>{vendorUnderClarif ? 'Pending Clarification' : 'Clarify'}</Button>
          </Space>
        );
      }
      return (
        <Space size={4}>
          <Button size="small" type="primary" disabled={vendorUnderClarif} onClick={() => handleDirectorVendorVote(record.vendorId, 'ACCEPTED')}>Accept</Button>
          <Button size="small" danger onClick={() => openVendorRejectModal(record.vendorId, handleDirectorVendorVote)}>Reject</Button>
          <Button size="small" disabled={vendorUnderClarif} style={{ color: vendorUnderClarif ? undefined : '#fa8c16', borderColor: vendorUnderClarif ? undefined : '#fa8c16' }}
            onClick={() => openVendorClarificationModal(record.vendorId, 'DIRECTOR')}>{vendorUnderClarif ? 'Pending Clarification' : 'Clarify'}</Button>
        </Space>
      );
    },
  }] : []),
  ...(showCommitteeMemberFinActions ? [
    {
      title: 'My Decision',
      key: 'committeeFinDecision',
      width: 120,
      render: (_, record) => {
        const myVote = evalStatus.committeeVendorVotes?.[record.vendorId]?.find(
          v => String(v.committeeUserId) === String(userId) && v.voterRole === 'MEMBER' && v.phase === 'FINANCIAL'
        );
        if (myVote?.decision) return <Tag color={myVote.decision === 'ACCEPTED' ? 'green' : 'red'}>{myVote.decision}</Tag>;
        return <Tag color="orange">Pending</Tag>;
      },
    },
    {
      title: 'Action',
      key: 'committeeFinAction',
      width: 220,
      render: (_, record) => {
        const myVote = evalStatus.committeeVendorVotes?.[record.vendorId]?.find(
          v => String(v.committeeUserId) === String(userId) && v.voterRole === 'MEMBER' && v.phase === 'FINANCIAL'
        );
        const myClarifPending = myPendingClarifVendorIds.has(record.vendorId);
        const vendorClarifReview = isChairmanClarifReview && evalStatus?.clarificationTargetVendorId === record.vendorId;
        const myConfirmed = myVote?.confirmed === true;
        const btnDisabled = myClarifPending || vendorClarifReview || myConfirmed;
        return (
          <Space size={4}>
            {myVote?.decision
              ? <Tag color={myVote.decision === 'ACCEPTED' ? 'green' : 'red'}>{myVote.decision}</Tag>
              : <>
                  <Button size="small" type="primary" disabled={btnDisabled} onClick={() => handleCommitteeVendorDecision(record.vendorId, 'ACCEPTED')}>Accept</Button>
                  <Button size="small" danger disabled={btnDisabled} onClick={() => openVendorRejectModal(record.vendorId, handleCommitteeVendorDecision)}>Reject</Button>
                </>
            }
            <Button size="small" style={{ color: btnDisabled ? undefined : '#fa8c16', borderColor: btnDisabled ? undefined : '#fa8c16' }}
              disabled={btnDisabled}
              onClick={() => openVendorClarificationModal(record.vendorId, 'COMMITTEE_MEMBER')}>Clarify</Button>
          </Space>
        );
      },
    },
  ] : []),
];

// ── SPO Technical Bid Columns ──
const spoTechColumns = [
  ...vendorInfoColumns,
  {
    title: 'Technical Document',
    key: 'techDoc',
    width: 130,
    render: (_, record) =>
      record.quotationFileName ? (
        <a href={`${baseURL}/file/view/Tender/${record.quotationFileName}`} target="_blank" rel="noopener noreferrer">View</a>
      ) : 'No File',
  },
  {
    title: indentorStatusLabel,
    key: 'indentorStatus',
    dataIndex: 'indentorStatus',
    width: 150,
    render: (indentorStatus, record) => {
      if (indentorStatus === 'CHANGE_REQUESTED') return 'Pending Clarification';
      if (indentorStatus === 'REJECTED' || indentorStatus === 'Rejected') return (
        <Popover
          content={record.indentorRemarks ? <span>{record.indentorRemarks}</span> : <span style={{ color: '#888' }}>No reason provided</span>}
          title="Rejection Reason"
        >
          <span style={{ color: 'red', cursor: 'help', borderBottom: '1px dashed red' }}>Rejected ⓘ</span>
        </Popover>
      );
      if (indentorStatus === 'Completed' || indentorStatus === 'ACCEPTED') return <span style={{ color: 'green' }}>Accepted</span>;
      return indentorStatus || 'N/A';
    },
  },
  {
    title: 'SPO Status',
    key: 'sopStatus',
    dataIndex: 'sopStatus',
    width: 130,
    render: (sopStatus, record) => {
      if (record.indentorStatus === 'REJECTED' || record.indentorStatus === 'Rejected') return 'Auto-Rejected';
      if (sopStatus === 'CHANGE_REQUESTED_TO_INTENTOR') return 'Pending Clarification';
      if (sopStatus === 'REJECTED' || sopStatus === 'Rejected') return 'Rejected';
      if (sopStatus === 'ACCEPTED' || sopStatus === 'Completed') return 'Accepted';
      return sopStatus || 'Pending';
    },
  },
  {
    title: 'Qualification Status',
    key: 'qualStatus',
    dataIndex: 'sopStatus',
    render: (sopStatus, record) => {
      if (record.indentorStatus === 'REJECTED' || record.indentorStatus === 'Rejected') return 'Disqualified';
      if (sopStatus === 'CHANGE_REQUESTED_TO_INTENTOR') return 'Pending Clarification';
      if (sopStatus === 'REJECTED' || sopStatus === 'Rejected') return 'Disqualified';
      if (sopStatus === 'ACCEPTED' || sopStatus === 'Completed') return 'Qualified';
      return sopStatus || 'Pending';
    },
  },
  ...(showSpoTechActions
    ? [{
        title: 'SPO Actions',
        key: 'spoTechActions',
        render: (_, record) => {
          // const spoCanAct = evalStatus?.evaluationStatus === 'PENDING_SPO_APPROVAL'
          //   && record.indentorStatus === 'ACCEPTED'
          //   && (!record.sopStatus || record.sopStatus === 'PENDING')
          //   && record.status !== 'CHANGE_REQUESTED';
           const spoCanAct = ['PENDING_SPO_APPROVAL', 'PENDING_VENDOR_CLARIFICATION', 'PENDING_INDENTOR_CLARIFICATION'].includes(evalStatus?.evaluationStatus)
            && record.indentorStatus === 'ACCEPTED'
            && (!record.sopStatus || record.sopStatus === 'PENDING')
            && (record.status !== 'CHANGE_REQUESTED' || record.status === 'CHANGE_RESPONDED');
          const spoCanReject = ['PENDING_SPO_APPROVAL', 'PENDING_VENDOR_CLARIFICATION', 'PENDING_INDENTOR_CLARIFICATION'].includes(evalStatus?.evaluationStatus)
            && (record.indentorStatus === 'ACCEPTED' || record.indentorStatus === null || record.indentorStatus === 'REJECTED')
            && (!record.sopStatus || record.sopStatus === 'PENDING' || record.sopStatus === 'CHANGE_REQUESTED');
          const pendingToIndentor = record.changeRequestToIndentor;
          const vendorHasOpenIndentorClarif = indentorOpenQuestions.some(q => q.targetVendorId === record.vendorId);
    const vendorPpClarifPending = record.status === 'CHANGE_REQUESTED' && !vendorHasOpenIndentorClarif;
    const ppHasRespondedForVendor = record.status === 'CHANGE_RESPONDED';

          return (
            <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap' }}>
              <Button
                size="small"
                disabled={!spoCanAct || record.sopStatus === 'ACCEPTED' || (vendorHasOpenIndentorClarif && !ppHasRespondedForVendor) || (vendorPpClarifPending && !ppHasRespondedForVendor) || isGemMultiVendorClarif}
                onClick={() => handleSpoReview(record, 'ACCEPT')}
                title={isGemMultiVendorClarif ? 'Cannot accept while vendor clarification is pending' : (vendorHasOpenIndentorClarif && !ppHasRespondedForVendor) ? 'Indentor must respond to clarification first' : (vendorPpClarifPending && !ppHasRespondedForVendor) ? 'Cannot accept — PP must respond to clarification for this vendor first' : ''}
              >
                {record.sopStatus === 'ACCEPTED' ? 'Accepted' : 'SPO Accept'}
              </Button>
              <Popover
                content={
                  <div style={{ padding: 12 }}>
                    <Input.TextArea
                      placeholder="Enter reject reason"
                      rows={3}
                      value={rejectedVendorId === record.vendorId ? rejectComment : ''}
                      onChange={(e) => { setRejectedVendorId(record.vendorId); setRejectComment(e.target.value); }}
                    />
                    <Button type="primary"
                      // disabled={!spoCanAct || record.sopStatus === 'REJECTED'}
                      disabled={!spoCanReject || record.sopStatus === 'REJECTED' || (isGemMultiVendorClarif && !spoIsClarifSeeker)}
                      onClick={() => handleSpoReview(record, 'REJECT')}
                      style={{ marginTop: 8 }}>
                      Submit
                    </Button>
                  </div>
                }
                title="SPO Reject Reason"
                trigger="click"
              >
                <Button size="small" style={{ color: 'red' }}
                  // disabled={!spoCanAct || record.sopStatus === 'REJECTED'}>
                  disabled={!spoCanReject || record.sopStatus === 'REJECTED' || (isGemMultiVendorClarif && !spoIsClarifSeeker)}>
                  {record.sopStatus === 'REJECTED' ? 'Rejected' : 'SPO Reject'}
                </Button>
              </Popover>
              <Popover
                content={
                  <div style={{ padding: 12, minWidth: 280 }}>
                    <div style={{ marginBottom: 8 }}>
                      <Text strong style={{ fontSize: 12 }}>Send To:</Text>
                      <Select size="small"
                        value={rejectedVendorId === record.vendorId ? (spoRowTarget || 'INDENTOR') : 'INDENTOR'}
                        onChange={(val) => { setRejectedVendorId(record.vendorId); setSpoRowTarget(val); }}
                        style={{ width: '100%', marginTop: 4 }}>
                        <Option value="INDENTOR">Indentor / Purchase Personnel</Option>
                        <Option value="VENDOR">Vendor (ask vendor directly)</Option>
                      </Select>
                    </div>
                    <Input.TextArea
                      placeholder={
                        (rejectedVendorId === record.vendorId ? spoRowTarget : 'INDENTOR') === 'VENDOR'
                          ? 'Enter clarification question for vendor'
                          : 'Enter change request to Indentor'
                      }
                      rows={3}
                      value={rejectedVendorId === record.vendorId ? rejectComment : ''}
                      onChange={(e) => { setRejectedVendorId(record.vendorId); setRejectComment(e.target.value); }}
                    />
                    <Button type="primary"
                      disabled={
                        !['PENDING_SPO_APPROVAL', 'PENDING_INDENTOR_CLARIFICATION', 'PENDING_VENDOR_CLARIFICATION'].includes(evalStatus?.evaluationStatus)
                        || record.status === 'CHANGE_REQUESTED'
                        || record.changeRequestToIndentor
                      }
                      onClick={() => {
                        const target = spoRowTarget || 'INDENTOR';
                        if (target === 'VENDOR') {
                          handleSpoVendorClarification(record);
                        } else {
                          handleSpoReview(record, 'CHANGE_REQUEST_TO_INTENTOR');
                        }
                      }}
                      style={{ marginTop: 8 }}>
                      Submit
                    </Button>
                  </div>
                }
                title="Seek Revision"
                trigger="click"
              >
                <Button size="small" style={{ color: '#fa8c16' }}>
                  {pendingToIndentor ? 'Change Requested' : record.status === 'CHANGE_REQUESTED' ? 'Clarification Sent' : 'Seek Revision'}
                </Button>
              </Popover>
              {pendingToIndentor && (
                <Popover
                  content={
                    <div style={{ padding: 12 }}>
                      <Input.TextArea
                        placeholder="Enter reason for rejecting indentor clarification"
                        rows={3}
                        value={rejectedVendorId === record.vendorId ? rejectComment : ''}
                        onChange={(e) => {
                          setRejectedVendorId(record.vendorId);
                          setRejectComment(e.target.value);
                        }}
                      />
                      <Button
                        type="primary"
                        danger
                        onClick={() => handleSpoRejectIndentorClarification(record)}
                        style={{ marginTop: 8 }}
                      >
                        Submit
                      </Button>
                    </div>
                  }
                  title="Reject Indentor Clarification"
                  trigger="click"
                >
                  <Button size="small" style={{ color: 'red' }}>
                    Reject Clarification
                  </Button>
                </Popover>
              )}
            </div>
          );
        },
      }]
    : []),
  ...(showRegisteredVendorColumn ? [{
    title: 'Registered Vendor ID',
    key: 'registeredVendor',
    width: 250,
    render: (_, record) => {
      if (isAbove10L) {
        const dv = evalStatus?.committeeVendorVotes?.[record.vendorId]?.find(v => v.voterRole === 'DIRECTOR' && v.decision && v.phase === currentVotePhase);
        if (!dv || dv.decision !== 'ACCEPTED') return <Tag color="default">-</Tag>;
      } else {
        if (record.status !== 'Completed') return <Tag color="default">-</Tag>;
      }
      if (record.registeredVendorId) {
        return <Tag color="green">{record.registeredVendorName} ({record.registeredVendorId})</Tag>;
      }
      return (
        <div style={{ display: 'flex', gap: 4, alignItems: 'center' }}>
          <Select showSearch placeholder="Select vendor" style={{ width: 160 }}
            value={selectedRegisteredVendors[record.vendorId] || undefined}
            onChange={val => setSelectedRegisteredVendors(prev => ({ ...prev, [record.vendorId]: val }))}
            filterOption={(input, option) => {
              const text = Array.isArray(option?.children) ? option.children.join('') : String(option?.children || '');
              return text.toLowerCase().includes(input.toLowerCase());
            }}>
            {allRegisteredVendors.map(v => (
              <Option key={v.vendorId} value={v.vendorId}>{v.vendorName} ({v.vendorId})</Option>
            ))}
          </Select>
          <Button size="small" type="primary" onClick={() => handleMapRegisteredVendor(tenderId, record.vendorId)}>Submit</Button>
        </div>
      );
    },
  }] : []),
];

// ── SPO Financial Bid Columns ──
const spoFinColumns = [
  ...vendorInfoColumns,
  {
    title: 'Financial Document',
    dataIndex: 'priceBidFileName',
    key: 'priceBidFileName',
    width: 130,
    render: (fileName) =>
      fileName ? (
        <a href={`${baseURL}/file/view/Tender/${fileName}`} target="_blank" rel="noopener noreferrer">View</a>
      ) : 'No File',
  },
  ...(showVendorResponse
    ? [{
        title: 'Vendor Response',
        dataIndex: 'vendorResponse',
        key: 'spoFinVendorResponse',
      }]
    : []),
  ...(showClarificationFile
    ? [{
        title: 'Clarification File',
        dataIndex: 'clarificationFileName',
        key: 'spoFinClarificationFile',
        render: (file, record) => {
          const latest = [...clarificationHistory]
            .filter(h => h.targetVendorId === record.vendorId)
            .sort((a, b) => b.roundNumber - a.roundNumber)[0];
          return latest?.responseFileName ? (
            <a href={`${baseURL}/file/view/Tender/${latest.responseFileName}`} target="_blank" rel="noopener noreferrer">View</a>
          ) : null;
        },
      }]
    : []),
  {
    title: indentorStatusLabel,
    key: 'financialIndentorStatus',
    dataIndex: 'financialIndentorStatus',
    width: 150,
    render: (val, record) => {
      if (val === 'CHANGE_REQUESTED') return <Tag color="orange">Pending Clarification</Tag>;
      if (val === 'REJECTED' || val === 'Rejected') return (
        <Popover
          content={record.financialIndentorRemarks ? <span>{record.financialIndentorRemarks}</span> : <span style={{ color: '#888' }}>No reason provided</span>}
          title="Rejection Reason"
        >
          <span style={{ color: 'red', cursor: 'help', borderBottom: '1px dashed red' }}>Rejected ⓘ</span>
        </Popover>
      );
      if (val === 'ACCEPTED' || val === 'Completed') return <Tag color="green">Accepted</Tag>;
      return val || <Tag>Pending</Tag>;
    },
  },
  {
    title: 'SPO Status',
    key: 'financialSpoStatus',
    dataIndex: 'financialSpoStatus',
    width: 130,
    render: (val, record) => {
      if (record.financialIndentorStatus === 'REJECTED' || record.financialIndentorStatus === 'Rejected') return <Tag color="red">Auto-Rejected</Tag>;
      if (val === 'ACCEPTED' || val === 'Completed') return <Tag color="green">Qualified</Tag>;
      if (val === 'REJECTED' || val === 'Rejected') return <Tag color="red">Disqualified</Tag>;
      if (val === 'CHANGE_REQUESTED_TO_INTENTOR') return <Tag color="orange">Pending Clarification</Tag>;
      return val || <Tag>Pending</Tag>;
    },
  },
  {
    title: 'Qualification Status',
    key: 'finQualStatus',
    dataIndex: 'financialSpoStatus',
    render: (val, record) => {
      if (record.financialIndentorStatus === 'REJECTED' || record.financialIndentorStatus === 'Rejected') return 'Disqualified';
      if (val === 'REJECTED' || val === 'Rejected') return 'Disqualified';
      if (val === 'ACCEPTED' || val === 'Completed') return 'Qualified';
      if (val === 'CHANGE_REQUESTED_TO_INTENTOR') return 'Pending Clarification';
      return val || 'Pending';
    },
  },
  ...(showSpoFinActions
    ? [{
        title: 'SPO Actions',
        key: 'spoFinActions',
        render: (_, record) => {
           const spoCanAct = ['PENDING_SPO_APPROVAL', 'PENDING_VENDOR_CLARIFICATION','PENDING_INDENTOR_CLARIFICATION'].includes(evalStatus?.evaluationStatus)
            && record.financialIndentorStatus === 'ACCEPTED'
            && (!record.financialSpoStatus || record.financialSpoStatus === 'PENDING')
            && (record.status !== 'CHANGE_REQUESTED' || record.status === 'CHANGE_RESPONDED');
         const spoCanReject = ['PENDING_SPO_APPROVAL', 'PENDING_VENDOR_CLARIFICATION', 'PENDING_INDENTOR_CLARIFICATION'].includes(evalStatus?.evaluationStatus)
  && (record.financialIndentorStatus === 'ACCEPTED' || record.financialIndentorStatus === 'PENDING' || record.financialIndentorStatus === 'Pending' || record.financialIndentorStatus === null || record.indentorStatus === 'REJECTED')
  && (!record.financialSpoStatus || record.financialSpoStatus === 'PENDING' || record.financialSpoStatus === 'CHANGE_REQUESTED');
          const pendingToIndentor = record.changeRequestToIndentor;
          const vendorHasOpenIndentorClarif = indentorOpenQuestions.some(q => q.targetVendorId === record.vendorId);
    const vendorPpClarifPending = record.status === 'CHANGE_REQUESTED' && !vendorHasOpenIndentorClarif;
    const ppHasRespondedForVendor = record.status === 'CHANGE_RESPONDED';

          return (
            <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap' }}>
              <Button
                size="small"
                disabled={!spoCanAct || record.financialSpoStatus === 'ACCEPTED' || (vendorHasOpenIndentorClarif && !ppHasRespondedForVendor) || (vendorPpClarifPending && !ppHasRespondedForVendor) || isGemMultiVendorClarif}
                onClick={() => handleSpoReview(record, 'ACCEPT')}
                title={isGemMultiVendorClarif ? 'Cannot accept while vendor clarification is pending' : (vendorHasOpenIndentorClarif && !ppHasRespondedForVendor) ? 'Indentor must respond to clarification first' : (vendorPpClarifPending && !ppHasRespondedForVendor) ? 'Cannot accept — PP must respond to clarification for this vendor first' : ''}
              >
                {record.financialSpoStatus === 'ACCEPTED' ? 'Accepted' : 'SPO Accept'}
              </Button>
              <Popover
                content={
                  <div style={{ padding: 12 }}>
                    <Input.TextArea
                      placeholder="Enter reject reason"
                      rows={3}
                      value={rejectedVendorId === record.vendorId ? rejectComment : ''}
                      onChange={(e) => { setRejectedVendorId(record.vendorId); setRejectComment(e.target.value); }}
                    />
                    <Button type="primary"
                      // disabled={!spoCanAct || record.financialSpoStatus === 'REJECTED'}
                       disabled={!spoCanReject || record.financialSpoStatus === 'REJECTED' || (isGemMultiVendorClarif && !spoIsClarifSeeker)}
                      onClick={() => handleSpoReview(record, 'REJECT')}
                      style={{ marginTop: 8 }}>
                      Submit
                    </Button>
                  </div>
                }
                title="SPO Reject Reason"
                trigger="click"
              >
                <Button size="small" style={{ color: 'red' }}
                  // disabled={!spoCanAct || record.financialSpoStatus === 'REJECTED'}>
                   disabled={!spoCanReject || record.financialSpoStatus === 'REJECTED' || (isGemMultiVendorClarif && !spoIsClarifSeeker)}>
                  {record.financialSpoStatus === 'REJECTED' ? 'Rejected' : 'SPO Reject'}
                </Button>
              </Popover>
              <Popover
                content={
                  <div style={{ padding: 12, minWidth: 280 }}>
                    <div style={{ marginBottom: 8 }}>
                      <Text strong style={{ fontSize: 12 }}>Send To:</Text>
                      <Select size="small"
                        value={rejectedVendorId === record.vendorId ? (spoRowTarget || 'INDENTOR') : 'INDENTOR'}
                        onChange={(val) => { setRejectedVendorId(record.vendorId); setSpoRowTarget(val); }}
                        style={{ width: '100%', marginTop: 4 }}>
                        <Option value="INDENTOR">Indentor / Purchase Personnel</Option>
                        <Option value="VENDOR">Vendor (ask vendor directly)</Option>
                      </Select>
                    </div>
                    <Input.TextArea
                      placeholder={
                        (rejectedVendorId === record.vendorId ? spoRowTarget : 'INDENTOR') === 'VENDOR'
                          ? 'Enter clarification question for vendor'
                          : 'Enter change request to Indentor'
                      }
                      rows={3}
                      value={rejectedVendorId === record.vendorId ? rejectComment : ''}
                      onChange={(e) => { setRejectedVendorId(record.vendorId); setRejectComment(e.target.value); }}
                    />
                    <Button type="primary"
                      disabled={
                        !['PENDING_SPO_APPROVAL', 'PENDING_INDENTOR_CLARIFICATION', 'PENDING_VENDOR_CLARIFICATION'].includes(evalStatus?.evaluationStatus)
                        || record.status === 'CHANGE_REQUESTED'
                        || record.changeRequestToIndentor
                      }
                      onClick={() => {
                        const target = spoRowTarget || 'INDENTOR';
                        if (target === 'VENDOR') {
                          handleSpoVendorClarification(record);
                        } else {
                          handleSpoReview(record, 'CHANGE_REQUEST_TO_INTENTOR');
                        }
                      }}
                      style={{ marginTop: 8 }}>
                      Submit
                    </Button>
                  </div>
                }
                title="Seek Revision"
                trigger="click"
              >
                <Button size="small" style={{ color: '#fa8c16' }}>
                  {pendingToIndentor ? 'Change Requested' : record.status === 'CHANGE_REQUESTED' ? 'Clarification Sent' : 'Seek Revision'}
                </Button>
              </Popover>
              {pendingToIndentor && (
                <Popover
                  content={
                    <div style={{ padding: 12 }}>
                      <Input.TextArea
                        placeholder="Enter reason for rejecting indentor clarification"
                        rows={3}
                        value={rejectedVendorId === record.vendorId ? rejectComment : ''}
                        onChange={(e) => {
                          setRejectedVendorId(record.vendorId);
                          setRejectComment(e.target.value);
                        }}
                      />
                      <Button
                        type="primary"
                        danger
                        onClick={() => handleSpoRejectIndentorClarification(record)}
                        style={{ marginTop: 8 }}
                      >
                        Submit
                      </Button>
                    </div>
                  }
                  title="Reject Indentor Clarification"
                  trigger="click"
                >
                  <Button size="small" style={{ color: 'red' }}>
                    Reject Clarification
                  </Button>
                </Popover>
              )}
            </div>
          );
        },
      }]
    : []),
];

// Unified effect: all tender-level fetches triggered once when tenderId changes
useEffect(() => {
  if (tenderId && tenderId.trim()) {
    handleSearchTender();
    fetchEvalStatus(tenderId);
    fetchClarificationHistory(tenderId);
    fetchIndentorOpenQuestions(tenderId);
    fetchDirCommitteeMembers();
    fetchDirEligibleUsers();
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
    PENDING_DIRECTOR_REVIEW: 'magenta',
    PENDING_CHAIRMAN_REVIEW: 'magenta',
    PENDING_VENDOR_CLARIFICATION: 'gold',
    PENDING_INDENTOR_CLARIFICATION: 'lime',
    PENDING_MEMBER_REVOTE: 'cyan',
    PENDING_DOCUMENT_UPLOAD: 'warning',
    APPROVED: 'green',
    REJECTED: 'red',
  };

  const isAbove1Crore = evalStatus?.amountCategory === 'ABOVE_1_CRORE';
  

  // Clarification pending states
  const isPendingVendorClarif = evalStatus?.evaluationStatus === 'PENDING_VENDOR_CLARIFICATION';
  const isPendingIndentorClarif = evalStatus?.evaluationStatus === 'PENDING_INDENTOR_CLARIFICATION';
  const isPendingMemberRevote = evalStatus?.evaluationStatus === 'PENDING_MEMBER_REVOTE';
  const isPendingCommitteeFormation = evalStatus?.evaluationStatus === 'PENDING_COMMITTEE_FORMATION';
  const isPendingDirectorReview = evalStatus?.evaluationStatus === 'PENDING_DIRECTOR_REVIEW';
  const isAnyClarificationPending = isPendingVendorClarif || isPendingIndentorClarif || isPendingMemberRevote;
  const ppRespondingOnBehalfOfVendor = isPurchasePersonnelRole
  && isPendingVendorClarif
  && (evalStatus?.clarificationPendingFrom === 'PURCHASE_PERSONNEL' || isOpenGlobalGem)
  && quotationData.some(q => q.status === 'CHANGE_REQUESTED' || q.status === 'CHANGE_RESPONDED');
  // Per-vendor: any vendor still has CHANGE_REQUESTED status
  const anyVendorPendingClarif = quotationData.some(q => q.status === 'CHANGE_REQUESTED');
  // All eligible vendors have been accepted or rejected by Indentor/PP
  // In financial phase, skip vendors already rejected in technical
  const allVendorsDecided = quotationData.length > 0 &&
    quotationData
      .filter(q => !(isFinancialPhase && (q.indentorStatus === 'REJECTED' || q.sopStatus === 'REJECTED')))
      .every(q => {
        const st = isFinancialPhase ? q.financialIndentorStatus : q.indentorStatus;
        return st === 'ACCEPTED' || st === 'REJECTED';
      });
  // All Indentor-accepted vendors must have an SPO decision before SPO can do final approval
  // In financial phase, vendor must be accepted in BOTH rounds to need SPO financial decision
  const allVendorsSpoDecided = quotationData.length > 0 &&
    quotationData
      .filter(q => {
        if (isFinancialPhase) {
          return q.indentorStatus === 'ACCEPTED' && q.financialIndentorStatus === 'ACCEPTED';
        }
        return q.indentorStatus === 'ACCEPTED';
      })
      .every(q => {
        const sp = isFinancialPhase ? q.financialSpoStatus : q.sopStatus;
        return sp === 'ACCEPTED' || sp === 'REJECTED';
      });
  // ── Double bid: phase-specific computed state ──
  const financialVendors = isDoubleBidEval
    ? quotationData.filter(q => {
        if (!isAbove10L) return q.indentorStatus === 'ACCEPTED' && q.sopStatus === 'ACCEPTED';
        const dirTechVote = evalStatus?.committeeVendorVotes?.[q.vendorId]
          ?.find(v => v.voterRole === 'DIRECTOR' && v.phase === 'TECHNICAL');
        return dirTechVote?.decision === 'ACCEPTED';
      })
    : [];

  const allVendorsTechDecided = isDoubleBidEval && quotationData.length > 0 &&
    quotationData.every(q => q.indentorStatus === 'ACCEPTED' || q.indentorStatus === 'REJECTED');

  const allVendorsFinDecided = isDoubleBidEval && financialVendors.length > 0 &&
    financialVendors.every(q =>
      q.financialIndentorStatus === 'ACCEPTED' || q.financialIndentorStatus === 'REJECTED'
    );

  const techAcceptedVendors = quotationData.filter(q => q.indentorStatus === 'ACCEPTED');
  const allVendorsSpoTechDecided = isDoubleBidEval && quotationData.length > 0 &&
    (techAcceptedVendors.length === 0
      ? quotationData.every(q => q.indentorStatus === 'REJECTED' || q.sopStatus === 'REJECTED')
      : techAcceptedVendors.every(q => q.sopStatus === 'ACCEPTED' || q.sopStatus === 'REJECTED'));

  const finAcceptedVendors = financialVendors.filter(q => q.financialIndentorStatus === 'ACCEPTED');
  const allVendorsSpoFinDecided = isDoubleBidEval && financialVendors.length > 0 &&
    (finAcceptedVendors.length === 0
      ? financialVendors.every(q => q.financialIndentorStatus === 'REJECTED' || q.financialSpoStatus === 'REJECTED')
      : finAcceptedVendors.every(q => q.financialSpoStatus === 'ACCEPTED' || q.financialSpoStatus === 'REJECTED'));

  const anyTechVendorPendingClarif = isDoubleBidEval &&
    quotationData.some(q => q.status === 'CHANGE_REQUESTED');

  const anyFinVendorPendingClarif = isDoubleBidEval &&
    financialVendors.some(q => q.status === 'CHANGE_REQUESTED');

  // Human-readable committee type label
  const committeeLabel = {
    STEC_I:  'STEC-I  (₹10 Lakh – ₹50 Lakh)',
    STEC_II: 'STEC-II (₹50 Lakh – ₹1 Crore)',
    ADHOC:   'Ad Hoc Committee (Above ₹1 Crore — constituted by Director)',
  }[evalStatus?.committeeType] || '';

  const amountCategoryLabel = {
    UNDER_10_LAKH:              'Below ₹10 Lakhs',
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
            {evalStatus?.evaluationStatus && evalStatus.evaluationStatus !== 'PENDING_INITIATION' ? (
              <>
                <Tag color={evalStatusColor[evalStatus.evaluationStatus] || 'default'} style={{ fontSize: 13 }}>
                  Status: {evalStatus.evaluationStatus === 'PENDING_FINANCIAL' && evalStatus.bidType === 'SINGLE_BID'
                    ? 'PENDING EVALUATION'
                    : evalStatus.evaluationStatus === 'PENDING_DIRECTOR_REVIEW'
                    ? 'PENDING DIRECTOR REVIEW'
                    : evalStatus.evaluationStatus === 'PENDING_CHAIRMAN_REVIEW'
                    ? 'PENDING CHAIRMAN REVIEW'
                    : evalStatus.evaluationStatus === 'PENDING_INDENTOR_CLARIFICATION' && evalStatus.indentCategory === 'MULTIPLE_INDENT'
                    ? 'PENDING PP CLARIFICATION'
                    : evalStatus.evaluationStatus?.replace(/_/g, ' ')}
                </Tag>
                <Tag color="cyan">{amountCategoryLabel}</Tag>
                {evalStatus.committeeType && (
                  <Tag color={evalStatus.committeeType === 'STEC_I' ? 'purple' : evalStatus.committeeType === 'STEC_II' ? 'geekblue' : 'volcano'}>
                    {committeeLabel}
                  </Tag>
                )}
                {evalStatus.bidType && <Tag color="purple"> {evalStatus.bidType?.replace(/_/g, ' ')}</Tag>}
                {evalStatus.indentCategory && <Tag>{evalStatus.indentCategory?.replace(/_/g, ' ')}</Tag>}
                {/* {evalStatus.approvedVendorName && (
                  <Tag color="green">Approved: {evalStatus.approvedVendorName}</Tag>
                )} */}
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

        {/* ── Final Approved State — after Tags, before evaluation tables ── */}
        {evalStatus?.evaluationStatus === 'APPROVED' && (
          <>
            <Alert
              type="success"
              showIcon
              message={`Tender Evaluation APPROVED`}
              description={`Vendor Portal Registration By Purchase Personnel: ${evalStatus.vendorPortalRegistered ? 'Done' : 'Pending'}`}
              style={{ marginTop: 16 }}
            />
            {isDoubleBidEval && quotationData.length > 0 && (
              <Table
                dataSource={quotationData}
                rowKey="vendorId"
                size="small"
                pagination={false}
                style={{ marginTop: 16 }}
                columns={[
                  {
                    title: 'Vendor ID',
                    dataIndex: 'vendorId',
                    key: 'vendorId',
                    width: 120,
                  },
                  {
                    title: 'Vendor Name',
                    dataIndex: 'vendorName',
                    key: 'vendorName',
                    width: 200,
                  },
                  {
                    title: 'Technical Status',
                    key: 'technicalStatus',
                    width: 140,
                    render: (_, record) => {
                      if (isAbove10L) {
                        const dv = evalStatus?.committeeVendorVotes?.[record.vendorId]
                          ?.find(v => v.voterRole === 'DIRECTOR' && v.phase === 'TECHNICAL' && v.decision);
                        if (dv?.decision === 'ACCEPTED') return <Tag color="green">Accepted</Tag>;
                        if (dv?.decision === 'REJECTED') return <Tag color="red">Rejected</Tag>;
                        return <Tag color="orange">Pending</Tag>;
                      }
                      const spo = record.sopStatus;
                      if (spo === 'ACCEPTED' || spo === 'Completed') return <Tag color="green">Accepted</Tag>;
                      if (spo === 'REJECTED' || spo === 'Rejected') return <Tag color="red">Rejected</Tag>;
                      const ind = record.indentorStatus;
                      if (ind === 'REJECTED' || ind === 'Rejected') return <Tag color="red">Rejected</Tag>;
                      if (ind === 'ACCEPTED' || ind === 'Completed') return <Tag color="green">Accepted</Tag>;
                      return <Tag>Pending</Tag>;
                    },
                  },
                  {
                    title: 'Financial Status',
                    key: 'financialStatus',
                    width: 140,
                    render: (_, record) => {
                      if (isAbove10L) {
                        const techDv = evalStatus?.committeeVendorVotes?.[record.vendorId]
                          ?.find(v => v.voterRole === 'DIRECTOR' && v.phase === 'TECHNICAL' && v.decision);
                        if (techDv?.decision === 'REJECTED') return <Tag color="red">Rejected</Tag>;
                        const finDv = evalStatus?.committeeVendorVotes?.[record.vendorId]
                          ?.find(v => v.voterRole === 'DIRECTOR' && v.phase === 'FINANCIAL' && v.decision);
                        if (finDv?.decision === 'ACCEPTED') return <Tag color="green">Accepted</Tag>;
                        if (finDv?.decision === 'REJECTED') return <Tag color="red">Rejected</Tag>;
                        return <Tag color="orange">Pending</Tag>;
                      }
                      const techRejected = record.sopStatus === 'REJECTED' || record.sopStatus === 'Rejected'
                        || record.indentorStatus === 'REJECTED' || record.indentorStatus === 'Rejected';
                      if (techRejected) return <Tag color="red">Rejected</Tag>;
                      const val = record.financialSpoStatus || record.financialIndentorStatus;
                      if (val === 'ACCEPTED' || val === 'Completed') return <Tag color="green">Accepted</Tag>;
                      if (val === 'REJECTED' || val === 'Rejected') return <Tag color="red">Rejected</Tag>;
                      return <Tag>Pending</Tag>;
                    },
                  },
                  ...(isOpenGlobalGem ? [{
                    title: 'Vendor Selection',
                    key: 'vendorSelection',
                    width: 250,
                    render: (_, record) => {
                      if (isAbove10L) {
                        const finDv = evalStatus?.committeeVendorVotes?.[record.vendorId]
                          ?.find(v => v.voterRole === 'DIRECTOR' && v.phase === 'FINANCIAL' && v.decision);
                        if (!finDv || finDv.decision !== 'ACCEPTED') return <Tag color="default">-</Tag>;
                      } else {
                        const finAccepted = record.financialSpoStatus === 'ACCEPTED' || record.financialSpoStatus === 'Completed';
                        if (!finAccepted) return <Tag color="default">-</Tag>;
                      }
                      if (record.registeredVendorId) {
                        return <Tag color="green">{record.registeredVendorName} ({record.registeredVendorId})</Tag>;
                      }
                      if (!isPurchasePersonnelRole) return <Tag>Not Mapped</Tag>;
                      return (
                        <div style={{ display: 'flex', gap: 4, alignItems: 'center' }}>
                          <Select
                            showSearch
                            placeholder="Select vendor"
                            style={{ width: 160 }}
                            value={selectedRegisteredVendors[record.vendorId] || undefined}
                            onChange={val => setSelectedRegisteredVendors(prev => ({ ...prev, [record.vendorId]: val }))}
                            filterOption={(input, option) => {
                              const text = Array.isArray(option?.children) ? option.children.join('') : String(option?.children || '');
                              return text.toLowerCase().includes(input.toLowerCase());
                            }}
                          >
                            {allRegisteredVendors.map(v => (
                              <Option key={v.vendorId} value={v.vendorId}>{v.vendorName} ({v.vendorId})</Option>
                            ))}
                          </Select>
                          <Button size="small" type="primary"
                            onClick={() => handleMapRegisteredVendor(tenderId, record.vendorId)}>
                            Submit
                          </Button>
                        </div>
                      );
                    },
                  }] : []),
                ]}
              />
            )}
          </>
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
            {isIndentCreatorRole && !evalLoading && evalStatus?.evaluationStatus === 'PENDING_DOCUMENT_UPLOAD' && tenderId && (
              <Alert
                type="info"
                showIcon
                message="Pending Document Upload"
                description="The Purchase Personnel has initiated the evaluation and is uploading the Comparison Statement. The evaluation will proceed after document submission."
                style={{ marginBottom: 12 }}
              />
            )}

            {/* ── Single Bid: Original Table (unchanged) ── */}
            {!isDoubleBidEval && (
              <Table
                dataSource={quotationData}
                columns={columns}
                rowKey="vendorId"
                pagination={false}
              />
            )}

            {/* ── Double Bid: Dual Tables (hidden at APPROVED — consolidated table shown below) ── */}
            {isDoubleBidEval && evalStatus?.evaluationStatus !== 'APPROVED' && (
              <>
                <Card
                  title={
                    <span>
                      Technical Bid Evaluation
                      {isFinancialPhase && <Tag color="green" style={{ marginLeft: 8 }}>Completed</Tag>}
                    </span>
                  }
                  size="small"
                  style={{ marginTop: 16 }}
                >
                  <Table
                    dataSource={quotationData}
                    columns={isSpoRole ? spoTechColumns : doubleBidTechColumns}
                    rowKey="vendorId"
                    size="small"
                    bordered
                    pagination={false}
                    scroll={{ x: 'max-content' }}
                    tableLayout="auto"
                  />
                </Card>

                {/* {isFinancialPhase && (
                  <Card title="Financial Bid Evaluation" size="small" style={{ marginTop: 16 }}>
                    {financialVendors.length === 0 ? (
                      <Alert type="info" message="No vendors qualified in technical evaluation." />
                    ) : ( */}
                    {isFinancialPhase && (
  <Card title="Financial Bid Evaluation" size="small" style={{ marginTop: 16 }}>
    {evalStatus?.evaluationStatus === 'PENDING_FINANCIAL_SHEET_UPLOAD' && isPurchasePersonnelRole && (
      <Alert
        type="info"
        showIcon
        style={{ marginBottom: 12 }}
        message="Financial sheet upload pending. Review qualified vendors below before uploading."
      />
    )}
    {financialVendors.length === 0 ? (
      <Alert type="info" message="No vendors qualified in technical evaluation." />
    ) : (
                      <Table
                        dataSource={financialVendors}
                        columns={isSpoRole ? spoFinColumns : doubleBidFinColumns}
                        rowKey="vendorId"
                        size="small"
                        bordered
                        pagination={false}
                        scroll={{ x: 'max-content' }}
                        tableLayout="auto"
                      />
                    )}
                  </Card>
                )}
              </>
            )}

            {/* ── Seek Clarification to All Vendors (PP / Indentor) ── */}
            {/* {evalStatus?.evaluationStatus && !['APPROVED', 'REJECTED'].includes(evalStatus.evaluationStatus) &&
              (isPurchasePersonnelRole || isIndentCreatorRole) &&
              quotationData.length > 0 && ( */}
              {((isPurchasePersonnelRole && (
    evalStatus?.evaluationStatus &&
      !['APPROVED', 'REJECTED'].includes(evalStatus.evaluationStatus) &&
      isMultipleIndentEval
  )) ||
  (isIndentCreatorRole &&
    evalStatus?.evaluationStatus &&
    !['APPROVED', 'REJECTED'].includes(evalStatus.evaluationStatus)
  )) &&
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
                        title={`Round ${h.roundNumber} — ${h.clarificationTarget?.replace(/_/g, ' ')}${h.targetVendorId ? ` (Vendor: ${h.targetVendorId})` : ''} — by ${h.requestedByRole?.replace(/_/g, ' ')}`}
                      >
                        <p style={{ wordBreak: 'break-word', whiteSpace: 'pre-wrap', margin: '4px 0' }}>
  <strong>Question:</strong> {h.questionRemarks}
</p>
                        <p style={{ color: '#888', fontSize: 12 }}>{h.requestedAt ? new Date(h.requestedAt).toLocaleString() : ''}</p>
                        {h.responseText ? (
                          <>
                            <Divider style={{ margin: '8px 0' }} />
                            <p style={{ wordBreak: 'break-word', whiteSpace: 'pre-wrap', margin: '4px 0' }}>
  <strong>Response ({h.respondedByRole?.replace(/_/g, ' ')}):</strong> {h.responseText}
</p>
                            {h.responseFileName && (
                              <p><strong>File:</strong> <a href={`${baseURL}/file/view/Tender/${h.responseFileName}`} target="_blank" rel="noopener noreferrer">{h.responseFileName}</a></p>
                            )}
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
            {isPurchasePersonnelRole && evalStatus?.evaluationStatus === 'PENDING_DOCUMENT_UPLOAD' && (
                <div style={{ marginTop: 16 }}>
                  {renderFormFields(
                    [{ heading: '', colCnt: 1, fieldList: [{ name: 'comparationStatementFileName', label: 'Technical Comparison Sheet (PDF / Excel / Word)', type: 'multiImage', accept: '.pdf,.xlsx,.xls,.doc,.docx', span: 1 }] }],
                    handleChange, formData, '', null, setFormData, null
                  )}
                  {evalStatus?.evaluationStatus === 'PENDING_DOCUMENT_UPLOAD' && (
                    <Button
                      type="primary"
                      loading={initiating}
                      style={{ marginTop: 8 }}
                      onClick={handleSubmitDocumentAndProceed}
                      disabled={!formData.comparationStatementFileName?.[0] && !evalStatus?.comparisonSheetFileName}
                    >
                      Submit & Proceed
                    </Button>
                  )}
                </div>
              )}
              {/* ── PP: Seek Clarification (All Vendors) — Limited/Prop, under 10L, pre-initiate ── */}
{showPpPreInitiateClarif && (
  <div style={{ marginTop: 8 }}>
    <Button
      style={{ color: '#fa8c16', borderColor: '#fa8c16' }}
      onClick={() => {
        setClarifRequestedByRole('PURCHASE_PERSONNEL');
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
              (['PENDING_FINANCIAL', 'PENDING_TECHNICAL', 'PENDING_VENDOR_CLARIFICATION', 'PENDING_INDENTOR_CLARIFICATION'].includes(evalStatus?.evaluationStatus)) &&
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
                      disabled={
                        isAnyClarificationPending ||
                        (isDoubleBidEval
                          ? (isFinancialPhase ? anyFinVendorPendingClarif || !allVendorsFinDecided : anyTechVendorPendingClarif || !allVendorsTechDecided)
                          : (anyVendorPendingClarif || !allVendorsDecided))
                      }
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
            {(['PENDING_FINANCIAL', 'PENDING_TECHNICAL', 'PENDING_VENDOR_CLARIFICATION', 'PENDING_INDENTOR_CLARIFICATION'].includes(evalStatus?.evaluationStatus)) &&
              evalStatus?.amountCategory === 'UNDER_10_LAKH' &&
              isMultipleIndentEval &&
              isPurchasePersonnelRole && !ppRespondingOnBehalfOfVendor && (
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
                      disabled={
                        isAnyClarificationPending ||
                        (isDoubleBidEval
                          ? (isFinancialPhase ? anyFinVendorPendingClarif || !allVendorsFinDecided : anyTechVendorPendingClarif || !allVendorsTechDecided)
                          : (anyVendorPendingClarif || !allVendorsDecided))
                      }
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
            {isAnyClarificationPending && !(ppRespondingOnBehalfOfVendor) && (
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
                  // evalStatus.clarificationRemarks ? `Question: "${evalStatus.clarificationRemarks}"` : null,
                ].filter(Boolean).join(' — ') || ''}
                style={{ marginTop: 12 }}
              />
            )}

            {/* ── Indentor/PP Responds to Clarification ── */}
            {isPendingVendorClarif  &&
              (isIndentCreatorRole || isPurchasePersonnelRole) &&
              (evalStatus?.clarificationPendingFrom === 'PURCHASE_PERSONNEL' || isOpenGlobalGem) ? (
                /* PP per-vendor clarification card (GEM/OPEN/GLOBAL) */
                isPurchasePersonnelRole && (
                <Card title="Respond to Vendor Clarification (on behalf of vendors)" size="small" style={{ marginTop: 16 }}>
                  {/* <Alert type="warning" showIcon style={{ marginBottom: 8 }}
                    message={`Clarification requested by: ${evalStatus.clarificationRequestedByRole?.replace(/_/g, ' ')}`}
                    description={evalStatus.clarificationRemarks ? `Question: "${evalStatus.clarificationRemarks}"` : ''} /> */}
                  {quotationData.filter(q => q.status === 'CHANGE_REQUESTED' || q.status === 'CHANGE_RESPONDED').length === 0 && (
                    <Alert type="info" showIcon style={{ marginBottom: 8 }}
                      message="No vendors are pending clarification response." />
                  )}
                  {quotationData.filter(q => q.status === 'CHANGE_REQUESTED' || q.status === 'CHANGE_RESPONDED').map(q => (
                    <div key={q.vendorId} style={{
                      marginBottom: 12, padding: 12,
                      background: q.status === 'CHANGE_REQUESTED' ? '#fff7e6' : '#f6ffed',
                      border: `1px solid ${q.status === 'CHANGE_REQUESTED' ? '#ffd591' : '#b7eb8f'}`,
                      borderRadius: 4
                    }}>
                      <div style={{ marginBottom: 8 }}>
                        <strong>{q.vendorName || q.vendorId}</strong>
                        {q.status === 'CHANGE_RESPONDED'
                          ? <Tag color="green" style={{ marginLeft: 8 }}>Response Submitted</Tag>
                          : <Tag color="orange" style={{ marginLeft: 8 }}>Pending Response</Tag>}
                      </div>
                      {q.remarks && (
  <Alert type="warning" showIcon style={{ marginBottom: 8 }}
    message={`Clarification Question: "${q.remarks}"`} />
)}
                      {q.status === 'CHANGE_REQUESTED' && (
                        <>
                          <div style={{ marginBottom: 8 }}>
                            <Text strong style={{ fontSize: 12 }}>Upload Clarification Document:</Text>
                            <input
                              type="file"
                              style={{ display: 'block', marginTop: 4 }}
                              onChange={(e) => {
                                const file = e.target.files[0];
                                setPpVendorFiles(prev => ({ ...prev, [q.vendorId]: file || null }));
                              }}
                            />
                            {ppVendorFiles[q.vendorId] && (
                              <span style={{ fontSize: 12, color: '#52c41a' }}>{ppVendorFiles[q.vendorId].name}</span>
                            )}
                          </div>
                          <div style={{ marginBottom: 8 }}>
                            <Text strong style={{ fontSize: 12 }}>Clarification Response:</Text>
                            <Input.TextArea
                              rows={3}
                              placeholder="Enter clarification response on behalf of vendor"
                              value={ppVendorResponses[q.vendorId] || ''}
                              onChange={(e) => setPpVendorResponses(prev => ({ ...prev, [q.vendorId]: e.target.value }))}
                              style={{ marginTop: 4 }}
                            />
                          </div>
                          <Button
                            type="primary"
                            size="small"
                            loading={ppSubmitting[q.vendorId]}
                            onClick={() => handlePpRespondForVendor(q.vendorId)}
                          >
                            Submit Response for {q.vendorName || q.vendorId}
                          </Button>
                        </>
                      )}
                      {q.status === 'CHANGE_RESPONDED' && q.vendorResponse && (
                        <div style={{ fontSize: 12, color: '#666' }}>Response: {q.vendorResponse}</div>
                      )}
                    </div>
                  ))}
                </Card>
                )
              ) : isPendingIndentorClarif &&
              (evalStatus?.clarificationPendingFrom === 'PURCHASE_PERSONNEL'
                ? isPurchasePersonnelRole
                : (isIndentCreatorRole || isPurchasePersonnelRole)) ? (
                /* Per-vendor clarification cards for indentor/PP responding to SPO/Chairman/Director */
                <Card title="Respond to Clarification Request" size="small" style={{ marginTop: 16 }}>
                  <Alert type="warning" showIcon style={{ marginBottom: 12 }}
                    message={`Clarification requested by: ${evalStatus.clarificationRequestedByRole?.replace(/_/g, ' ')}`} />
                  {indentorOpenQuestions.length === 0 && (
                    <Alert type="info" showIcon style={{ marginBottom: 8 }}
                      message="No pending clarification questions." />
                  )}
                  {indentorOpenQuestions.map((q) => {
                    const targetVendor = quotationData.find(v => v.vendorId === q.targetVendorId);
                    const vendorStatus = isFinancialPhase ? targetVendor?.financialIndentorStatus : targetVendor?.indentorStatus;
                    console.log( isFinancialPhase, targetVendor, vendorStatus, q.targetVendorId);
                    const vendorDecided = vendorStatus === 'ACCEPTED' || vendorStatus === 'REJECTED';
                    console.log('vendorDecided', vendorDecided, targetVendor?.vendorName, vendorStatus);
                    return (
                      <div key={q.id} style={{
                        marginBottom: 12, padding: 12,
                        background: '#fff7e6',
                        border: '1px solid #ffd591',
                        borderRadius: 4
                      }}>
                        <div style={{ marginBottom: 8 }}>
                          <strong>{targetVendor?.vendorName || q.targetVendorId || 'All Vendors'}</strong>
                          {vendorDecided
                            ? <Tag color="orange" style={{ marginLeft: 8 }}>Pending Response</Tag>
                            : <Tag color="default" style={{ marginLeft: 8 }}>Accept/Reject vendor first</Tag>}
                        </div>
                        <Alert type="warning" showIcon style={{ marginBottom: 8 }}
                          message={`Question (Round ${q.roundNumber}): "${q.questionRemarks}"`}
                          description={
                            <span>
                              Asked by: {q.requestedByRole?.replace(/_/g, ' ')}
                              {q.requestedAt ? ` | ${new Date(q.requestedAt).toLocaleString('en-IN')}` : ''}
                            </span>
                          } />
                        {targetVendor?.quotationFileName && (
                          <div style={{ marginBottom: 8 }}>
                            <a href={`${baseURL}/file/view/Tender/${targetVendor.quotationFileName}`} target="_blank" rel="noopener noreferrer">
                              View Vendor Quotation
                            </a>
                          </div>
                        )}
                        {vendorDecided ? (
                          <>
                            <div style={{ marginBottom: 8 }}>
                              <Text strong style={{ fontSize: 12 }}>Upload Supporting Document:</Text>
                              <input
                                type="file"
                                style={{ display: 'block', marginTop: 4 }}
                                onChange={(e) => {
                                  const file = e.target.files[0];
                                  setIndentorFiles(prev => ({ ...prev, [q.id]: file || null }));
                                }}
                              />
                              {indentorFiles[q.id] && (
                                <span style={{ fontSize: 12, color: '#52c41a' }}>{indentorFiles[q.id].name}</span>
                              )}
                            </div>
                            <div style={{ marginBottom: 8 }}>
                              <Text strong style={{ fontSize: 12 }}>Your Response:</Text>
                              <Input.TextArea
                                rows={3}
                                placeholder="Enter your clarification response..."
                                value={indentorResponses[q.id] || ''}
                                onChange={(e) => setIndentorResponses(prev => ({ ...prev, [q.id]: e.target.value }))}
                                style={{ marginTop: 4 }}
                              />
                            </div>
                            <Button
                              type="primary"
                              size="small"
                              loading={indentorSubmitting[q.id]}
                              disabled={!indentorResponses[q.id]?.trim()}
                              onClick={() => handleIndentorRespondPerQuestion(q.id)}
                            >
                              Submit Response{targetVendor ? ` for ${targetVendor.vendorName || q.targetVendorId}` : ''}
                            </Button>
                          </>
                        ) : (
                          <Alert type="info" showIcon
                            message="Please accept or reject this vendor in the table above before submitting your clarification response." />
                        )}
                      </div>
                    );
                  })}
                </Card>
              ) : null}

            {/* ── Indentor / SPO / PP / Chairman / Director Acknowledges Vendor Clarification ── */}
            {(isPendingVendorClarif || quotationData.some(q => q.status === 'CHANGE_REQUESTED' || q.status === 'CHANGE_RESPONDED')) &&
              (isIndentCreatorRole || isPurchasePersonnelRole || isSpoRole || isChairman || isCommitteeMember || isDirector) && !(ppRespondingOnBehalfOfVendor) && (
                <Card title="Vendor Clarification Response" size="small" style={{ marginTop: 16 }}>
                  {/* <Alert type="warning" showIcon style={{ marginBottom: 8 }}
                    message="Clarification Sent to Vendor"
                    description={evalStatus.clarificationRemarks ? `"${evalStatus.clarificationRemarks}"` : ''} /> */}
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
            {(['PENDING_SPO_APPROVAL', 'PENDING_VENDOR_CLARIFICATION', 'PENDING_INDENTOR_CLARIFICATION'].includes(evalStatus?.evaluationStatus)) &&
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
                      disabled={
                        isAnyClarificationPending ||
                        (isDoubleBidEval
                          ? (isFinancialPhase ? !allVendorsSpoFinDecided : !allVendorsSpoTechDecided)
                          : !allVendorsSpoDecided)
                      }
                      onClick={() => { setApprovalType('spo'); setApprovalDecision('APPROVED'); setApprovalModal(true); }}>
                      {isDoubleBidEval && !isFinancialPhase ? 'Approve Technical Phase' : 'Confirm Evaluation'}
                    </Button>
                    {/* <Button danger
                      onClick={() => { setApprovalType('spo'); setApprovalDecision('REJECTED'); setApprovalModal(true); }}>
                      SPO Reject
                    </Button> */}
                    <Button style={{ color: '#fa8c16', borderColor: '#fa8c16' }}
                      onClick={() => {
                        setClarifRequestedByRole('SPO');
                        // setClarifTarget('INDENTOR');
                        setClarifTarget(isMultipleIndentEval ? 'PURCHASE_PERSONNEL' : 'INDENTOR');
                        setClarifTargetVendorId('');
                        setClarifTargetUserId('');
                        setClarifTargetUserName('');
                        setClarifRemarks('');
                        setClarificationModal(true);
                      }}>
                      Revision from Indentor (All Vendors)
                    </Button>
                    <Button style={{ color: '#1890ff', borderColor: '#1890ff' }}
                      onClick={() => {
                        setClarifRequestedByRole('SPO');
                        setClarifTarget('ALL_VENDORS');
                        setClarifTargetVendorId('');
                        setClarifTargetUserId('');
                        setClarifTargetUserName('');
                        setClarifRemarks('');
                        setClarificationModal(true);
                      }}>
                      Ask All Vendors for Clarification
                    </Button>
                  </Space>
                </Card>
              )}

            {/* ── Director Clarification: Chairman Review (legacy global status) ── */}
            {isAbove10L && isChairman && evalStatus?.evaluationStatus === 'PENDING_CHAIRMAN_CLARIFICATION_REVIEW'
              && evalStatus?.clarificationRequestedByRole === 'DIRECTOR' && (
              <Card title="Director Clarification Request" size="small" style={{ marginTop: 16 }}>
                <Alert type="warning" showIcon style={{ marginBottom: 12 }}
                  message={`Director requests clarification${evalStatus.clarificationTargetVendorId ? ` regarding Vendor: ${evalStatus.clarificationTargetVendorId}` : ''}`}
                  description={evalStatus.clarificationRemarks} />
                <Space wrap>
                  <Button type="primary" onClick={() => { setRespondText(''); setRespondRole('CHAIRMAN'); setRespondModal(true); }}>
                    Respond to Director
                  </Button>
                  <Button onClick={() => { setChairmanForwardRemarks(''); setChairmanForwardModal(true); }}>
                    Forward to Vendor
                  </Button>
                  <Button danger onClick={handleRejectMemberClarification}>
                    Dismiss Request
                  </Button>
                </Space>
              </Card>
            )}
            {isAbove10L && !isChairman && evalStatus?.evaluationStatus === 'PENDING_CHAIRMAN_CLARIFICATION_REVIEW'
              && evalStatus?.clarificationRequestedByRole === 'DIRECTOR' && (
              <Alert type="info" showIcon style={{ marginTop: 16 }}
                message="Director Clarification Pending"
                description="Director has requested clarification from Chairman. Waiting for Chairman to respond." />
            )}

            {/* ── Member Clarifications: Chairman Review (per-member per-vendor) ── */}
            {isAbove10L && isChairman && anyMemberClarifPending && (() => {
              const grouped = {};
              pendingMemberClarifs.forEach(c => {
                const key = c.vendorId || 'GENERAL';
                if (!grouped[key]) grouped[key] = [];
                grouped[key].push(c);
              });
              return (
                <Card title={`Member Clarification Requests (${pendingMemberClarifs.length})`} size="small" style={{ marginTop: 16 }}>
                  <Alert type="info" showIcon style={{ marginBottom: 12 }}
                    message="Committee members have questions. Review, then forward selected ones to the vendor or dismiss." />
                  {Object.entries(grouped).map(([vendorKey, clarifs]) => (
                    <div key={vendorKey} style={{ border: '1px solid #d9d9d9', borderRadius: 8, padding: 12, marginBottom: 12, background: '#fafafa' }}>
                      <div style={{ fontWeight: 'bold', marginBottom: 8 }}>
                        Vendor: {vendorKey === 'GENERAL' ? 'General' : (quotationData.find(q => q.vendorId === vendorKey)?.vendorName || vendorKey)}
                      </div>
                      {clarifs.map(c => (
                        <div key={c.historyId} style={{ display: 'flex', alignItems: 'flex-start', gap: 8, marginBottom: 6, padding: '4px 0', borderBottom: '1px solid #f0f0f0' }}>
                          <input type="checkbox"
                            checked={selectedClarifIds.includes(c.historyId)}
                            onChange={e => {
                              setSelectedClarifIds(prev =>
                                e.target.checked ? [...prev, c.historyId] : prev.filter(id => id !== c.historyId)
                              );
                            }} />
                          <div style={{ flex: 1 }}>
                            <div><strong>{c.memberName}</strong>: {c.remarks}</div>
                            <small style={{ color: '#888' }}>{c.requestedAt ? new Date(c.requestedAt).toLocaleString('en-IN') : ''}</small>
                          </div>
                          <Button size="small" danger onClick={() => handleDismissSelectedClarifs([c.historyId])}>Dismiss</Button>
                        </div>
                      ))}
                    </div>
                  ))}
                  <Space wrap style={{ marginTop: 8 }}>
                    <Button type="primary"
                      disabled={selectedClarifIds.length === 0}
                      onClick={() => { setChairmanClarifForwardRemarks(''); setChairmanClarifForwardModal(true); }}>
                      Forward Selected to Vendor ({selectedClarifIds.length})
                    </Button>
                    <Button
                      disabled={selectedClarifIds.length === 0}
                      danger
                      onClick={() => handleDismissSelectedClarifs(selectedClarifIds)}>
                      Dismiss Selected ({selectedClarifIds.length})
                    </Button>
                  </Space>
                </Card>
              );
            })()}
            {isAbove10L && !isChairman && (isCommitteeMember || isAssignedExpert) && myPendingClarifVendorIds.size > 0 && (
              <Alert type="info" showIcon style={{ marginTop: 16 }}
                message="Clarification Pending"
                description={`Your clarification request for ${myPendingClarifVendorIds.size} vendor(s) is waiting for Chairman review.`} />
            )}

            {/* ── Above 10L: PP Document Submission ── */}
            {isAbove10L && evalStatus?.evaluationStatus === 'PENDING_PP_DOCUMENT' && isPurchasePersonnelRole && (
              <Card title="Submit Comparison Statement" size="small" style={{ marginTop: 16 }}>
                <Alert type="info" showIcon style={{ marginBottom: 12 }}
                  message="Upload the comparison statement document, then click Submit to forward to Chairman for committee review." />
                <Button type="primary" onClick={handlePpSubmitDocument}
                  disabled={!evalStatus?.comparisonSheetFileName}>
                  Submit Document
                </Button>
              </Card>
            )}
            {isAbove10L && evalStatus?.evaluationStatus === 'PENDING_PP_DOCUMENT' && !isPurchasePersonnelRole && (
              <Alert type="info" showIcon style={{ marginTop: 16 }}
                message="Pending PP Document"
                description="Waiting for Purchase Personnel to submit comparison statement." />
            )}

            {/* ── Above 10L: Member Voting + Confirm ── */}
            {isAbove10L && evalStatus?.evaluationStatus === 'PENDING_MEMBER_VOTING' && (isCommitteeMember || isAssignedExpert) && (
              <Card title={`Committee Voting${isFinancialPhase ? ' — Financial Phase' : ' — Technical Phase'}`} size="small" style={{ marginTop: 16 }}>
                <Alert type="info" showIcon style={{ marginBottom: 12 }}
                  message="Vote on each vendor using Accept/Reject buttons in the table, then confirm your votes." />
                {anyVendorPendingClarif && (
                  <Alert type="warning" showIcon style={{ marginBottom: 8 }}
                    message="Cannot confirm while a vendor clarification is pending." />
                )}
                {myPendingClarifVendorIds.size > 0 && (
                  <Alert type="warning" showIcon style={{ marginBottom: 8 }}
                    message={`You have ${myPendingClarifVendorIds.size} pending clarification(s) with Chairman. Cannot confirm until resolved.`} />
                )}
                <Space wrap>
                  <Button type="primary"
                    disabled={!allMyVendorsDecided || myVotesConfirmed || anyVendorPendingClarif || myPendingClarifVendorIds.size > 0}
                    onClick={handleConfirmCommitteeVotes}>
                    {myVotesConfirmed ? 'Votes Confirmed ✓' : 'Confirm My Votes'}
                  </Button>
                  <Button style={{ color: '#1890ff', borderColor: '#1890ff' }}
                    onClick={() => openClarificationModal('COMMITTEE_MEMBER')}>
                    Seek Clarification from Chairman
                  </Button>
                </Space>
                {myVotesConfirmed && (
                  <Alert type="success" showIcon style={{ marginTop: 8 }}
                    message="Your votes are confirmed and locked. Waiting for other members to confirm." />
                )}
              </Card>
            )}
            {isAbove10L && evalStatus?.evaluationStatus === 'PENDING_MEMBER_VOTING' && !isCommitteeMember && !isAssignedExpert && (
              <Alert type="info" showIcon style={{ marginTop: 16 }}
                message="Pending Member Voting"
                description="Committee members are evaluating vendors. Please wait." />
            )}

            {/* ── Above 10L: Chairman Voting ── */}
            {isAbove10L && isChairman && (evalStatus?.evaluationStatus === 'PENDING_CHAIRMAN_VOTING' ||
              (['PENDING_VENDOR_CLARIFICATION', 'PENDING_INDENTOR_CLARIFICATION'].includes(evalStatus?.evaluationStatus) &&
               evalStatus?.clarificationRequestedByRole === 'CHAIRMAN')) && (
              <Card title={`Chairman Voting${isFinancialPhase ? ' — Financial Phase' : ''}`} size="small" style={{ marginTop: 16 }}>
                <Alert type="info" showIcon style={{ marginBottom: 12 }}
                  message="All committee members have confirmed their votes. Review votes using View button, then Accept/Reject each vendor." />
                {isAnyClarificationPending && (
                  <Alert type="warning" showIcon style={{ marginBottom: 8 }}
                    message="Cannot confirm while a clarification response is pending." />
                )}
                {!allChairmanVendorsDecided && !isAnyClarificationPending && (
                  <Alert type="warning" showIcon style={{ marginBottom: 8 }}
                    message="Please Accept or Reject all vendors before confirming evaluation." />
                )}
                <Space wrap>
                  <Button type="primary"
                    disabled={!allChairmanVendorsDecided || isAnyClarificationPending}
                    onClick={() => { setApprovalType('chairman'); setApprovalDecision('APPROVED'); setApprovalModal(true); }}>
                    {isDoubleBidEval && !isFinancialPhase ? 'Approve Technical Phase' : 'Confirm Evaluation'}
                  </Button>
                  <Button danger
                    disabled={!allChairmanVendorsDecided || isAnyClarificationPending}
                    onClick={() => { setApprovalType('chairman'); setApprovalDecision('REJECTED'); setApprovalModal(true); }}>
                    Reject
                  </Button>
                  <Button style={{ color: '#1890ff', borderColor: '#1890ff' }}
                    onClick={() => openClarificationModal('CHAIRMAN')}>
                    Seek Clarification
                  </Button>
                  <Button style={{ color: '#fa541c', borderColor: '#fa541c' }}
                    disabled={isAnyClarificationPending}
                    onClick={handleResetAllVotes}>
                    Re-vote (All Members)
                  </Button>
                </Space>
              </Card>
            )}
            {isAbove10L && !isChairman && (evalStatus?.evaluationStatus === 'PENDING_CHAIRMAN_VOTING' ||
              (['PENDING_VENDOR_CLARIFICATION', 'PENDING_INDENTOR_CLARIFICATION'].includes(evalStatus?.evaluationStatus) &&
               evalStatus?.clarificationRequestedByRole === 'CHAIRMAN')) && (
              <Alert type="info" showIcon style={{ marginTop: 16 }}
                message="Pending Chairman Voting"
                description="All members confirmed. Waiting for Chairman to vote on each vendor." />
            )}

            {/* ── Above 10L: Director Voting ── */}
            {isAbove10L && isDirector && (evalStatus?.evaluationStatus === 'PENDING_DIRECTOR_APPROVAL' ||
              (['PENDING_VENDOR_CLARIFICATION', 'PENDING_INDENTOR_CLARIFICATION'].includes(evalStatus?.evaluationStatus) &&
               evalStatus?.clarificationRequestedByRole === 'DIRECTOR')) && (
              <Card title={`Director Final Approval${isFinancialPhase ? ' — Financial Phase' : ''}`} size="small" style={{ marginTop: 16 }}>
                <Alert type="info" showIcon style={{ marginBottom: 12 }}
                  message="Chairman has voted. Review all decisions, then Accept/Reject each vendor as final approver." />
                {isAnyClarificationPending && (
                  <Alert type="warning" showIcon style={{ marginBottom: 8 }}
                    message="Cannot confirm while a clarification response is pending." />
                )}
                {!allDirectorVendorsDecided && !isAnyClarificationPending && (
                  <Alert type="warning" showIcon style={{ marginBottom: 8 }}
                    message="Please Accept or Reject all vendors before confirming evaluation." />
                )}
                <Space wrap>
                  <Button type="primary"
                    disabled={!allDirectorVendorsDecided || isAnyClarificationPending}
                    onClick={() => { setApprovalType('director'); setApprovalDecision('APPROVED'); setApprovalModal(true); }}>
                    {isDoubleBidEval && !isFinancialPhase ? 'Approve Technical Phase' : 'Confirm Evaluation'}
                  </Button>
                  <Button danger
                    disabled={!allDirectorVendorsDecided || isAnyClarificationPending}
                    onClick={() => { setApprovalType('director'); setApprovalDecision('REJECTED'); setApprovalModal(true); }}>
                    Reject
                  </Button>
                  <Button style={{ color: '#1890ff', borderColor: '#1890ff' }}
                    onClick={() => openClarificationModal('DIRECTOR')}>
                    Seek Clarification
                  </Button>
                </Space>
              </Card>
            )}
            {isAbove10L && !isDirector && (evalStatus?.evaluationStatus === 'PENDING_DIRECTOR_APPROVAL' ||
              (['PENDING_VENDOR_CLARIFICATION', 'PENDING_INDENTOR_CLARIFICATION'].includes(evalStatus?.evaluationStatus) &&
               evalStatus?.clarificationRequestedByRole === 'DIRECTOR')) && (
              <Alert type="info" showIcon style={{ marginTop: 16 }}
                message="Pending Director Approval"
                description="Waiting for Director to give final approval on each vendor." />
            )}

            {/* ── Above 10L: Waiting for Chairman (non-chairman info) ── */}
            {isAbove10L && !isChairman && evalStatus?.evaluationStatus === 'PENDING_CHAIRMAN_REVIEW' && (
              <Alert type="info" showIcon style={{ marginTop: 16 }}
                message="Waiting for Committee Chairman"
                description="The Chairman is reviewing the committee composition and assigning an expert. Please wait." />
            )}

            {/* ── Above 10L: Chairman Reviews Committee (PENDING_CHAIRMAN_REVIEW) ── */}
            {isAbove10L && isChairman && evalStatus?.evaluationStatus === 'PENDING_CHAIRMAN_REVIEW' && (
              <Card title={`${committeeLabel || 'Committee'} — Chairman: Review Committee & Add Expert`} size="small" style={{ marginTop: 16 }}>
                <Alert type="info" showIcon style={{ marginBottom: 12 }}
                  message="Review the committee members below (read-only). You may add an expert, then confirm to proceed." />
                {evalStatus.committeeVotes && (
                  <Table size="small" dataSource={evalStatus.committeeVotes} rowKey="committeeUserId"
                    pagination={false} style={{ marginBottom: 12 }}
                    columns={[
                      { title: 'Member', dataIndex: 'committeeMemberName' },
                      { title: 'Role', dataIndex: 'role', render: (r) => (
                        <Tag color={r === 'CHAIRMAN' ? 'blue' : r === 'CO_CHAIRMAN' ? 'cyan' : 'default'}>{r || 'MEMBER'}</Tag>
                      )},
                    ]} />
                )}
                <Space wrap>
                  <Button onClick={openExpertModal}>
                    {evalStatus.expertName ? 'Change Expert' : 'Add Expert'}
                  </Button>
                  <Button type="primary" onClick={handleChairmanConfirmCommittee}>
                    Confirm Committee &amp; Proceed
                  </Button>
                </Space>
              </Card>
            )}

            {/* ── Above 10L: Committee Member — Confirm & Clarify ──
                PENDING_APPROVAL: single-bid committee vote OR financial phase vote for double-bid.
                PENDING_TECHNICAL: first (technical) committee vote for double-bid cases (6/8/10).
                Votes are cast per-vendor in the table; this card only confirms or seeks clarification. */}
            {isAbove10L && isCommitteeMember && isVotingMember &&
              (evalStatus?.evaluationStatus === 'PENDING_APPROVAL' ||
               evalStatus?.evaluationStatus === 'PENDING_MEMBER_REVOTE' ||
               (evalStatus?.evaluationStatus === 'PENDING_TECHNICAL' && isDoubleBidEval)) && (
              <Card title={`${committeeLabel || 'Committee'} — Confirm Votes${evalStatus?.evaluationStatus === 'PENDING_MEMBER_REVOTE' ? ' (Re-vote Requested)' : evalStatus?.evaluationStatus === 'PENDING_TECHNICAL' ? ' (Technical Bid Phase)' : isFinancialPhase ? ' (Financial Bid Phase)' : ''}`} size="small" style={{ marginTop: 16 }}>
                <Alert type="info" showIcon style={{ marginBottom: 12 }}
                  message="Vote on each vendor using Accept/Reject buttons in the table, then confirm your votes." />
                {anyVendorPendingClarif && (
                  <Alert type="warning" showIcon style={{ marginBottom: 8 }}
                    message="Cannot confirm while a vendor clarification is pending." />
                )}
                {myPendingClarifVendorIds.size > 0 && (
                  <Alert type="warning" showIcon style={{ marginBottom: 8 }}
                    message={`You have ${myPendingClarifVendorIds.size} pending clarification(s) with Chairman. Cannot confirm until resolved.`} />
                )}
                <Space wrap>
                  <Button type="primary"
                    disabled={!allMyVendorsDecided || myVotesConfirmed || anyVendorPendingClarif || myPendingClarifVendorIds.size > 0}
                    onClick={handleConfirmCommitteeVotes}>
                    {myVotesConfirmed ? 'Votes Confirmed ✓' : 'Confirm My Votes'}
                  </Button>
                  <Button style={{ color: '#1890ff', borderColor: '#1890ff' }}
                    onClick={() => openClarificationModal('COMMITTEE_MEMBER')}>
                    Seek Clarification from Chairman
                  </Button>
                </Space>
                {myVotesConfirmed && (
                  <Alert type="success" showIcon style={{ marginTop: 8 }}
                    message="Your votes are confirmed and locked. Waiting for other members to confirm." />
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
                      { title: 'Role', dataIndex: 'role', render: (r) => (
                        <Tag color={r === 'CHAIRMAN' ? 'blue' : r === 'CO_CHAIRMAN' ? 'cyan' : 'default'}>{r || 'MEMBER'}</Tag>
                      )},
                      { title: 'Vote', dataIndex: 'vote', render: (v) => v ? <Tag color={v === 'APPROVED' ? 'green' : 'red'}>{v}</Tag> : <Tag color="orange">Pending</Tag> },
                      { title: 'Remarks', dataIndex: 'voteRemarks', render: (r) => r || '-' },
                    ]}
                  />
                )}
                <Space wrap>
                  <Button type="primary"
                    onClick={() => { setChairmanDecision('APPROVED'); setChairmanIsOverride(false); setChairmanModal(true); }}>
                    Approve (Follow Committee)
                  </Button>
                  <Button danger
                    onClick={() => { setChairmanDecision('REJECTED'); setChairmanIsOverride(false); setChairmanModal(true); }}>
                    Reject
                  </Button>
                  {/* <Button style={{ color: '#fa8c16', borderColor: '#fa8c16' }}
                    onClick={() => { setChairmanDecision('APPROVED'); setChairmanIsOverride(true); setChairmanModal(true); }}>
                    Override Committee
                  </Button> */}
                  <Button style={{ color: '#1890ff', borderColor: '#1890ff' }}
                    onClick={() => openClarificationModal('CHAIRMAN')}>
                    Seek Clarification
                  </Button>
                  <Button style={{ color: '#fa541c', borderColor: '#fa541c' }}
                    onClick={handleResetAllVotes}>
                    Reset All Member Votes
                  </Button>
                </Space>
              </Card>
            )}

            {/* ── Above 1 CR: Director Reviews & Forms Ad-Hoc Committee ── */}
            {isPendingDirectorReview && isDirector && (
              <Card title="Director Review — Form Ad-Hoc Committee (Above ₹1 Crore)" size="small" style={{ marginTop: 16 }}>
                <Alert type="warning" showIcon style={{ marginBottom: 8 }}
                  message="This tender exceeds ₹1 Crore. Constitute an ad-hoc committee by selecting a Chairman, Co-Chairman, and members." />

                {/* Committee Members Table */}
                {dirCommitteeMembers.length > 0 && (
                  <Table size="small" dataSource={dirCommitteeMembers} rowKey="userId"
                    loading={dirCommitteeLoading} pagination={false} style={{ marginBottom: 12 }}
                    columns={[
                      { title: 'User ID', dataIndex: 'userId', width: 80 },
                      { title: 'Name', dataIndex: 'memberName' },
                      { title: 'Role', dataIndex: 'role', render: (r) => (
                        <Tag color={r === 'CHAIRMAN' ? 'blue' : r === 'CO_CHAIRMAN' ? 'cyan' : 'default'}>{r}</Tag>
                      )},
                      { title: 'Expert', dataIndex: 'isExpert', render: (v) => v ? <Tag color="purple">Expert</Tag> : '-' },
                      { title: 'Action', key: 'action', render: (_, record) =>
                        record.role !== 'CO_CHAIRMAN' ? (
                          <Button danger size="small" icon={<MinusCircleOutlined />}
                            onClick={() => handleDirRemoveMember(record.userId)}>
                            Remove
                          </Button>
                        ) : null
                      },
                    ]}
                  />
                )}

                {/* Add Member */}
                <Space style={{ marginBottom: 12 }} wrap>
                  <Select
                    showSearch
                    filterOption={false}
                    value={addMemberUserId}
                    onChange={val => setAddMemberUserId(val)}
                    onSearch={handleDirUserSearch}
                    onDropdownVisibleChange={open => { if (open) fetchDirEligibleUsers(); }}
                    placeholder="Search by name, employee ID, or role"
                    loading={dirEligibleUsersLoading}
                    style={{ width: 320 }}
                    allowClear
                    onClear={() => { setAddMemberUserId(null); setDirUserSearchText(''); }}
                  >
                    {filteredDirUsers.map(u => (
                      <Option key={u.userId} value={u.userId}>
                        {u.userName} ({u.department || u.roleName || 'N/A'}) — EmpID: {u.employeeId || u.userId}
                      </Option>
                    ))}
                  </Select>
                  <Select value={addMemberRole} onChange={val => setAddMemberRole(val)} style={{ width: 130 }}>
                    <Option value="MEMBER">Member</Option>
                    <Option value="CHAIRMAN">Chairman</Option>
                  </Select>
                  <Button icon={<PlusOutlined />} onClick={handleDirAddMember}>Add</Button>
                </Space>

                <div>
                  <Popconfirm
                    title="Confirm Committee"
                    description="Are you sure you want to confirm this committee? Status will move to Chairman Review."
                    onConfirm={handleDirectorConfirmCommittee}
                    okText="Yes, Confirm"
                    cancelText="Cancel"
                  >
                    <Button type="primary" disabled={dirCommitteeMembers.length === 0}>
                      Confirm Committee
                    </Button>
                  </Popconfirm>
                </div>
              </Card>
            )}
            {isPendingDirectorReview && !isDirector && (
              <Alert type="info" showIcon style={{ marginTop: 16 }}
                message="Waiting for Director Review"
                description="The Director is constituting an ad-hoc committee for this tender. Please wait." />
            )}

            {/* ── Above 10L: Director Approval ── */}
            {isAbove10L && isDirector && (evalStatus?.evaluationStatus === 'PENDING_DIRECTOR_APPROVAL' ||
              (['PENDING_VENDOR_CLARIFICATION', 'PENDING_INDENTOR_CLARIFICATION'].includes(evalStatus?.evaluationStatus) &&
               evalStatus?.clarificationRequestedByRole === 'DIRECTOR')) && (
              <Card title={`Director — Final Approval${isFinancialPhase ? ' (Financial Bid Phase)' : ''}`} size="small" style={{ marginTop: 16 }}>
                {isAbove1Crore ? (
                  <Alert type="warning" showIcon style={{ marginBottom: 8 }}
                    message="Above ₹1 Crore — Ad-hoc committee has voted. You have the final overriding authority." />
                ) : (
                  <Alert type="warning" showIcon style={{ marginBottom: 8 }}
                    message="You have overriding authority over committee decisions for all tenders above ₹10 Lakhs." />
                )}
                {isAnyClarificationPending && (
                  <Alert type="warning" showIcon style={{ marginBottom: 8 }}
                    message="Cannot confirm while a clarification response is pending." />
                )}
                {!isAbove1Crore && evalStatus.chairmanDecision && (
                  <Alert type="info" showIcon style={{ marginBottom: 8 }}
                    message={`Chairman Decision: ${evalStatus.chairmanDecision}${evalStatus.chairmanOverrideUsed ? ' (Override)' : ''}`} />
                )}
                <Alert type="info" showIcon message={`Approved Vendor: ${evalStatus.approvedVendorName || evalStatus.approvedVendorId}`} style={{ marginBottom: 8 }} />
                <Space wrap>
                  <Button style={{ background: '#52c41a', borderColor: '#52c41a', color: '#fff' }}
                    disabled={isAnyClarificationPending}
                    onClick={handleApproveCommitteeResponse}>
                    Approve Committee Response
                  </Button>
                  <Button type="primary"
                    disabled={!allDirectorVendorsDecided || isAnyClarificationPending}
                    onClick={() => { setApprovalType('director'); setApprovalDecision('APPROVED'); setApprovalModal(true); }}>
                    Confirm Evaluation
                  </Button>
                  {/* <Button danger
                    disabled={isAnyClarificationPending}
                    onClick={() => { setApprovalType('director'); setApprovalDecision('REJECTED'); setApprovalModal(true); }}>
                    Reject
                  </Button> */}
                  {/* <Button style={{ color: '#fa8c16', borderColor: '#fa8c16' }}
                    onClick={() => { setApprovalType('director'); setApprovalDecision('OVERRIDE'); setApprovalModal(true); }}>
                    Override &amp; Approve
                  </Button> */}
                  <Button style={{ color: '#1890ff', borderColor: '#1890ff' }}
                    onClick={() => openClarificationModal('DIRECTOR')}>
                    Seek Clarification
                  </Button>
                </Space>
              </Card>
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

      {/* ── Per-Vendor Reject Reason Modal (Above 10L) ── */}
      <Modal
        title="Reject Vendor — Enter Reason"
        open={vendorRejectModal}
        onOk={handleVendorRejectConfirm}
        onCancel={() => { setVendorRejectModal(false); setVendorRejectTarget(null); setVendorRejectRemarks(''); }}
        okText="Confirm Reject"
        okButtonProps={{ danger: true }}
      >
        <div style={{ marginBottom: 8 }}>
          <Text strong>Vendor: </Text>
          <span>{vendorRejectTarget?.vendorId}</span>
        </div>
        <Text strong>Rejection Reason:</Text>
        <Input.TextArea rows={3} value={vendorRejectRemarks} onChange={e => setVendorRejectRemarks(e.target.value)}
          style={{ marginTop: 4 }} placeholder="Enter reason for rejection..." />
      </Modal>

      {/* ── Approval Modal (Indentor / Purchase / SPO / Director) ── */}
      <Modal
        title={`${approvalType === 'spo' ? 'SPO Final' : approvalType === 'chairman' ? 'Chairman' : approvalType === 'director' ? 'Director' : 'Indentor / Purchase Dept'} — ${approvalDecision}`}
        open={approvalModal}
        onOk={handleApprovalSubmit}
        onCancel={() => setApprovalModal(false)}
        okText="Confirm"
        okButtonProps={{ danger: approvalDecision === 'REJECTED' }}
      >
        <Text strong>Remarks:</Text>
        <Input.TextArea rows={3} value={approvalRemarks} onChange={e => setApprovalRemarks(e.target.value)} style={{ marginTop: 4 }} />
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
          <Text strong>Select Expert:</Text>
          <Select
            showSearch
            loading={eligibleExpertsLoading}
            placeholder="Search by name or user ID"
            value={expertUserId || undefined}
            onChange={handleExpertSelect}
            onSearch={handleExpertSearch}
            filterOption={false}
            style={{ width: '100%', marginTop: 4 }}
            notFoundContent={eligibleExpertsLoading ? <Spin size="small" /> : 'No experts found'}
          >
            {filteredExperts.map(e => (
              <Option key={e.userId} value={e.userId}>
                {e.userName} ({e.userId}){e.roleName ? ` — ${e.roleName}` : ''}
              </Option>
            ))}
          </Select>
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

      {/* ── Chairman Forward Member Clarification Modal ── */}
      <Modal
        title="Forward Clarification to Vendor"
        open={chairmanForwardModal}
        onOk={handleChairmanForwardToVendor}
        onCancel={() => setChairmanForwardModal(false)}
        okText="Forward to Vendor"
        width={520}
      >
        <Alert type="info" showIcon style={{ marginBottom: 12 }}
          message={`Member's Question: "${evalStatus?.clarificationRemarks || ''}"`} />
        <div style={{ marginBottom: 12 }}>
          <Text strong>Vendor:</Text>
          <div style={{ marginTop: 4 }}>{evalStatus?.clarificationTargetVendorId || 'All Vendors'}</div>
        </div>
        <div>
          <Text strong>Your Question to Vendor:</Text>
          <Input.TextArea rows={4} value={chairmanForwardRemarks}
            onChange={e => setChairmanForwardRemarks(e.target.value)}
            style={{ marginTop: 4 }} placeholder="Write your question to the vendor..." />
        </div>
      </Modal>

      {/* ── Chairman Forward Selected Member Clarifications Modal ── */}
      <Modal
        title="Forward Selected Clarifications to Vendor"
        open={chairmanClarifForwardModal}
        onOk={handleForwardSelectedClarifs}
        onCancel={() => setChairmanClarifForwardModal(false)}
        okText="Forward to Vendor"
        width={560}
      >
        <Alert type="info" showIcon style={{ marginBottom: 12 }}
          message={`${selectedClarifIds.length} clarification(s) selected to forward`} />
        {selectedClarifIds.map(id => {
          const c = pendingMemberClarifs.find(x => x.historyId === id);
          return c ? (
            <div key={id} style={{ background: '#fafafa', padding: 8, borderRadius: 4, marginBottom: 6 }}>
              <strong>{c.memberName}</strong> (Vendor: {quotationData.find(q => q.vendorId === c.vendorId)?.vendorName || c.vendorId}): {c.remarks}
            </div>
          ) : null;
        })}
        <div style={{ marginTop: 12 }}>
          <Text strong>Your Question to Vendor:</Text>
          <Input.TextArea rows={4} value={chairmanClarifForwardRemarks}
            onChange={e => setChairmanClarifForwardRemarks(e.target.value)}
            style={{ marginTop: 4 }} placeholder="Write your combined question to the vendor..." />
        </div>
      </Modal>

      {/* ── Member Responses Modal (Chairman View per-vendor) ── */}
      <Modal
        title={`Committee Votes — ${quotationData.find(q => q.vendorId === memberResponsesVendorId)?.vendorName || memberResponsesVendorId || ''}`}
        open={memberResponsesModal}
        onCancel={() => { setMemberResponsesModal(false); setMemberResponsesVendorId(null); }}
        footer={null}
        width={700}
      >
        {(() => {
          const allVotes = evalStatus?.committeeVendorVotes?.[memberResponsesVendorId] || [];
          const expertId = evalStatus?.expertUserId;
          const techVotes = allVotes.filter(v => v.phase === 'TECHNICAL');
          const finVotes = allVotes.filter(v => v.phase === 'FINANCIAL');
          const voteSections = [];
          if (techVotes.length > 0) voteSections.push({ label: 'Technical Phase', votes: techVotes });
          if (finVotes.length > 0) voteSections.push({ label: 'Financial Phase', votes: finVotes });
          if (voteSections.length === 0) voteSections.push({ label: null, votes: allVotes });
          const voteColumns = [
            { title: 'Member', dataIndex: 'memberName', render: (name, record) => (
              <span>
                {name}
                {String(record.committeeUserId) === String(expertId) && <Tag color="purple" style={{ marginLeft: 6 }}>Expert</Tag>}
                {record.voterRole === 'CHAIRMAN' && <Tag color="blue" style={{ marginLeft: 6 }}>Chairman</Tag>}
                {record.voterRole === 'DIRECTOR' && <Tag color="gold" style={{ marginLeft: 6 }}>Director</Tag>}
              </span>
            )},
            { title: 'Decision', dataIndex: 'decision', render: (d) => d ? <Tag color={d === 'ACCEPTED' ? 'green' : 'red'}>{d}</Tag> : <Tag color="orange">Pending</Tag> },
            { title: 'Remarks', dataIndex: 'remarks', render: (r) => r || '-' },
            { title: 'Status', key: 'confirmed', render: (_, record) =>
              record.voterRole === 'CHAIRMAN' || record.voterRole === 'DIRECTOR'
                ? null
                : record.confirmed ? <Tag color="green">Confirmed</Tag> : <Tag color="orange">Draft</Tag>
            },
          ];
          return (
            <>
              {evalStatus?.expertName && (
                <Alert type="info" showIcon style={{ marginBottom: 12 }}
                  message={`Expert: ${evalStatus.expertName} (ID: ${evalStatus.expertUserId})`} />
              )}
              {voteSections.map((section, idx) => {
                const acceptCount = section.votes.filter(v => v.decision === 'ACCEPTED').length;
                const rejectCount = section.votes.filter(v => v.decision === 'REJECTED').length;
                const pendingCount = section.votes.filter(v => !v.decision).length;
                return (
                  <div key={idx} style={{ marginBottom: idx < voteSections.length - 1 ? 20 : 0 }}>
                    {section.label && <h4 style={{ marginBottom: 8 }}>{section.label}</h4>}
                    <div style={{ marginBottom: 12 }}>
                      <Tag color="green">{acceptCount} Accept</Tag>
                      <Tag color="red">{rejectCount} Reject</Tag>
                      {pendingCount > 0 && <Tag color="orange">{pendingCount} Pending</Tag>}
                    </div>
                    <Table
                      size="small"
                      dataSource={section.votes}
                      rowKey={(r) => `${r.committeeUserId}-${r.phase || ''}`}
                      pagination={false}
                      style={{ marginBottom: 16 }}
                      rowClassName={(record) => String(record.committeeUserId) === String(expertId) ? 'expert-row' : ''}
                      columns={voteColumns}
                    />
                  </div>
                );
              })}
            </>
          );
        })()}
      </Modal>

      {/* ── Seek Clarification / Send for Revision Modal ── */}
      <Modal
        title={
          clarifRequestedByRole === 'COMMITTEE_MEMBER'
            ? 'Seek Clarification from Chairman'
            : clarifRequestedByRole === 'SPO' && clarifTarget === 'INDENTOR'
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
        okText={clarifRequestedByRole === 'COMMITTEE_MEMBER' ? 'Send to Chairman' : 'Send Request'}
        width={520}
      >
        <div style={{ marginBottom: 12 }}>
          <Text strong>{clarifRequestedByRole === 'COMMITTEE_MEMBER' ? 'Regarding:' : 'Send Clarification To:'}</Text>
          <Select value={clarifTarget} onChange={setClarifTarget} style={{ width: '100%', marginTop: 4 }}>
            <Option value="VENDOR">Specific Vendor</Option>
            <Option value="ALL_VENDORS">All Vendors (Simultaneously)</Option>
            {clarifRequestedByRole === 'SPO' && (
  <>
    <Option value="INDENTOR">Indentor</Option>
    <Option value="PURCHASE_PERSONNEL">Purchase Personnel (Multiple Indent)</Option>
  </>
)}
            {clarifRequestedByRole === 'DIRECTOR' && <Option value="CHAIRMAN">Chairman</Option>}
            {(clarifRequestedByRole === 'CHAIRMAN' || clarifRequestedByRole === 'DIRECTOR') && (
              <>
                <Option value="SPECIFIC_MEMBER">Specific Committee Member</Option>
                <Option value="ALL_MEMBERS">All Committee Members</Option>
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
        {clarifTarget === 'INDENTOR' && quotationData.length > 0 && (
          <div style={{ marginBottom: 12 }}>
            <Text strong>Regarding Vendor (optional):</Text>
            <Select
              value={clarifTargetVendorId || undefined}
              onChange={val => setClarifTargetVendorId(val || '')}
              allowClear
              placeholder="Select vendor (leave blank for general clarification)"
              style={{ width: '100%', marginTop: 4 }}
            >
              {quotationData.map(q => (
                <Option key={q.vendorId} value={q.vendorId}>{q.vendorName || q.vendorId}</Option>
              ))}
            </Select>
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
