import React, { useRef, useState, useEffect } from "react";
import { Button, Card, Form, message, Modal, Tag } from "antd";
import { useReactToPrint } from "react-to-print";
import axios from "axios";
import { useSelector } from "react-redux";
import Heading from "../../../components/DKG_Heading";
import CustomForm from "../../../components/DKG_CustomForm";
import { renderFormFields } from "../../../utils/CommonFunctions";
import ButtonContainer from "../../../components/ButtonContainer";
import CustomModal from "../../../components/CustomModal";
import { SoDetails } from "./InputFields";

const SO = () => {
  const printRef = useRef();
  const [modalOpen, setModalOpen] = useState(false);
  const [submitBtnLoading, setSubmitBtnLoading] = useState(false);
  const [generatedSOId, setGeneratedSOId] = useState("");
const [versionHistoryOpen, setVersionHistoryOpen] = useState(false);
const [versionHistoryList, setVersionHistoryList] = useState([]);
const [selectedVersionIdx, setSelectedVersionIdx] = useState(0);
const [searchDone, setSearchDone] = useState(false);
  // Redux selectors
  const auth = useSelector((state) => state.auth);
  const actionPerformer = auth.userId;

  // Data states
  const [vendors, setVendors] = useState([]);
  const [tenders, setTenders] = useState([]);
  const [jobs, setJobs] = useState([]);
  const [completedVendorIds, setCompletedVendorIds] = useState([]);
  const [completedVendorNames, setCompletedVendorNames] = useState([]);
  const [formData, setFormData] = useState({
    jobDtlList: [],
    consignesAddress: "Bangalore",
    billingAddress: "Koramangala, Bangalore - 560034",
  });

  // Fetch initial data
  const populateDropdowns = async () => {
    try {
      const [vendorResponse, approvedTendersResponse] = await Promise.all([
        axios.get("/api/vendor-master"),
        axios.get("/getApprovedTenderIdForSO"),
      ]);

      // Format options
      const formattedVendors = (vendorResponse.data?.responseData || []).map(
        (vendor) => ({
          label: vendor.vendorName,
          value: vendor.vendorName,
          id: vendor.vendorId,
          address: vendor.address,
          accountNumber: vendor.accountNo,
          ifscCode: vendor.ifscCode,
          swiftCode: vendor.swiftCode,
          vendorType: vendor.vendorType,
          accountName: vendor.vendorName,
        })
      );
      setVendors(formattedVendors);

      // Get approved tender IDs
      const approvedTenderIds =
        approvedTendersResponse.data?.responseData || [];

      const tendersForDropdown = approvedTenderIds.map((tenderId) => ({
        label: tenderId,
        value: tenderId,
      }));
      setTenders(tendersForDropdown);
    } catch (error) {
      message.error("Failed to load dropdown data");
    }
  };

  const handleTenderSelect = async (tenderId) => {
    try {
      const tenderRes = await axios.get(`/api/tender-requests/byId`, { params: { tenderId } });
      const tenderDto = tenderRes.data.responseData;

      // Extract job details from indentResponseDTO (SO = job indents only)
      const allJobs = (tenderDto.indentResponseDTO || []).flatMap(
        (indent) =>
          (indent.jobDetails || []).map((job) => ({
            ...job,
            rate: job.estimatedPrice,
            currency: job.currency || "INR",
            gst: job.gst || "",
            duties: job.duties || "",
          }))
      );

      setJobs(allJobs);

      // Fetch approved vendors from evaluation (same pattern as PO)
      let vendorIdOptions = [];
      let vendorNameOptions = [];
      try {
        let completedVendorsData = [];
        try {
          const evalApprovedResp = await axios.get(
            `/api/tender-evaluation/approved-vendors`, { params: { tenderId } }
          );
          completedVendorsData = evalApprovedResp.data?.responseData || [];
        } catch (evalErr) {
          if (evalErr?.response?.status === 404 || evalErr?.response?.status === 400) {
            const completedResp = await axios.get(
              `/api/vendor-quotation/completed-vendorNames`, { params: { tenderId } }
            );
            completedVendorsData = completedResp.data?.responseData || [];
          } else {
            throw evalErr;
          }
        }
        vendorIdOptions = completedVendorsData.map((vendor) => ({
          label: vendor.vendorId,
          value: vendor.vendorId,
        }));
        vendorNameOptions = completedVendorsData.map((vendor) => ({
          label: vendor.vendorName || vendor.vendorId,
          value: vendor.vendorName || vendor.vendorId,
        }));
        setCompletedVendorIds(vendorIdOptions);
        setCompletedVendorNames(vendorNameOptions);
      } catch (e) {
        console.warn("Failed to fetch approved vendors:", e);
        setCompletedVendorIds([]);
        setCompletedVendorNames([]);
      }

      setFormData((prev) => ({
        ...prev,
        tenderId,
        jobDtlList: allJobs,
        incoTerms: tenderDto.incoTerms,
        paymentTerms: tenderDto.paymentTerms,
      }));
    } catch (error) {
      console.error("Tender selection error:", error);
      message.error("Failed to load tender details");
    }
  };

  // Hydrate form configuration
  const hydratedSoDetails = SoDetails.map((section) => {
    if (section.fieldList) {
      return {
        ...section,
        fieldList: section.fieldList.map((field) => {
          if (field.name === "vendorName") {
            return {
              ...field,
              type: "text",
              options: completedVendorNames.length
                ? completedVendorNames
                : vendors.map((v) => ({ label: v.value, value: v.value })),
              props: { readOnly: false },
            };
          }
          if (field.name === "tenderId")
            return {
              ...field,
              options: tenders,
              props: {
                onChange: (value) => {
                  handleTenderSelect(value);
                },
                showSearch: true,
              },
            };
          if (field.name === "vendorId") {
            const vendorOptions = completedVendorIds.length
              ? completedVendorIds
              : vendors.map((v) => ({ label: v.id, value: v.id }));
            return { ...field, options: vendorOptions };
          }
          if (field.name === "vendorsZRSCCode") {
            return {
              ...field,
              shouldShow: () => !formData.vendorType || formData.vendorType?.toLowerCase() === "domestic",
            };
          }
          if (field.name === "vendorSwiftCode") {
            return {
              ...field,
              shouldShow: () => formData.vendorType?.toLowerCase() === "international",
            };
          }
          return field;
        }),
      };
    }
    if (section.name === "jobDtlList") {
      return {
        ...section,
        children: section.children.map((child) => ({
          ...child,
          options: child.name === "jobCode" ? jobs : child.options,
        })),
      };
    }
    return section;
  });

  // Handle form changes
  const handleChange = (name, value) => {
    if (name === "vendorName") {
      const selectedVendor = vendors.find((v) => v.value === value);
      setFormData((prev) => ({
        ...prev,
        vendorName: value,
        vendorId: selectedVendor?.id || "",
        vendorAddress: selectedVendor?.address || "",
        vendorsAccountNo: selectedVendor?.accountNumber || "",
        vendorsZRSCCode: selectedVendor?.ifscCode || "",
        vendorSwiftCode: selectedVendor?.swiftCode || "",
        vendorType: selectedVendor?.vendorType || "",
        vendorsAccountName: selectedVendor?.accountName || "",
      }));
      return;
    }
    if (name === "vendorId") {
        const selectedVendor = vendors.find((v) => v.id === value);
        setFormData((prev) => ({
          ...prev,
          vendorId: value,
          vendorName: selectedVendor?.value || "",
          vendorAddress: selectedVendor?.address || "",
          vendorsAccountNo: selectedVendor?.accountNumber || "",
          vendorsZRSCCode: selectedVendor?.ifscCode || "",
          vendorSwiftCode: selectedVendor?.swiftCode || "",
          vendorType: selectedVendor?.vendorType || "",
          vendorsAccountName: selectedVendor?.accountName || "",
        }));
        return;
      }
    if (Array.isArray(name)) {
      const [section, index, field] = name;
      if (section === "jobDtlList") {
        setFormData((prev) => {
          const updated = [...(prev.jobDtlList || [])];
          updated[index] = { ...updated[index], [field]: value };
          return { ...prev, jobDtlList: updated };
        });
      }
    } else {
      setFormData((prev) => ({ ...prev, [name]: value }));
    }
  };

  // Form submission
  const onFinish = async () => {
    try {
      if (!formData.tenderId) {
        message.error("Please select a Tender ID before submitting.");
        return;
      }
      setSubmitBtnLoading(true);
      const payload = {
        ...formData,
        createdBy: actionPerformer,
        materials: (formData.jobDtlList || []).map((j) => ({
          budgetCode: j.budgetCode || "",
          currency: j.currency || "",
          duties: Number(j.duties) || 0,
          exchangeRate: Number(j.exchangeRate) || 0,
          gst: Number(j.gst) || 0,
          jobCode: j.jobCode || "",
          jobDescription: j.jobDescription || "",
          quantity: Number(j.quantity) || 0,
          rate: Number(j.rate) || 0,
        })),
        applicablePBGToBeSubmitted: formData.applicablePBGToBeSubmitted || "",
      };

      // const { data } = await axios.post("/api/service-orders", payload);
      // setGeneratedSOId(data.responseData.soId);
      // setModalOpen(true);
      // Replace the existing onFinish axios.post block:
let data;
if (formData.soId) {
  // Update
  const response = await axios.put(`/api/service-orders`, payload, {
    params: { soId: formData.soId }
  });
  data = response.data;
  const newSoId = data?.responseData?.soId;
  if (newSoId) {
    setFormData(prev => ({ ...prev, soId: newSoId }));
  }
  message.success("Service Order updated successfully");
} else {
  // Create
  const response = await axios.post("/api/service-orders", payload);
  data = response.data;
  message.success("Service Order created successfully");
}
setGeneratedSOId(data.responseData.soId);
setModalOpen(true);
    } catch (error) {
      message.error("Failed to create service order");
    } finally {
      setSubmitBtnLoading(false);
    }
  };

  const handleSearch = async (value) => {
    try {
      // const { data } = await axios.get(
      //   `/api/service-orders/${value ? value : formData.soId}`
      // );
      const { data } = await axios.get(
  `/api/service-orders/byId`,
  { params: { soId: value || formData.soId } }
);
      const responseData = data?.responseData || {};

      setFormData({
        ...responseData,
        jobDtlList: responseData?.materials || [],
      });

      setSearchDone(true);
if (responseData.isActive === false) {
  message.warning("You are viewing an older version of this SO. Load the latest version to make changes.");
}
    } catch (error) {
      ;
      message.error(
        error?.response?.data?.responseStatus?.message || "Error fetching data."
      );
    }
  };

  // Load initial data
  useEffect(() => {
    populateDropdowns();
  }, []);

  useEffect(() => {
    const soDraft = localStorage.getItem("soDraft");
    if (soDraft) {
      setFormData(JSON.parse(soDraft));
      message.success("Form loaded from draft.");
    }
  }, []);

  useEffect(() => {
    localStorage.setItem("soDraft", JSON.stringify(formData));
  }, [formData]);

  // --- Printing Function ---
  const handlePrint = useReactToPrint({
    content: () => printRef.current,
  });

  // TO:
const fetchSoVersionHistory = async (sid) => {
  try {
    const baseSid = (sid || formData.soId || "").split("/")[0];
    const { data } = await axios.get(`/api/service-orders/version-history`, { params: { soId: baseSid } });
    const list = data?.responseData || [];
    setVersionHistoryList(list);
    setSelectedVersionIdx(list.length - 1);
    setVersionHistoryOpen(true);
  } catch (error) {
    message.error("Could not load SO version history.");
  }
};

  return (
    <Card className="a4-container" ref={printRef}>
      <Heading title="Service Order Creation" />
      {formData?.soId && (
  <div style={{ marginBottom: 16 }}>
    {formData.isActive === false && (
      <div style={{ background: '#fff7e6', border: '1px solid #ffd591', padding: '8px 16px', borderRadius: 4, marginBottom: 8 }}>
        ⚠️ Viewing Old Version (V{formData.soVersion}) — This is a superseded version. Load the latest to make changes.
      </div>
    )}
    <Button onClick={() => fetchSoVersionHistory(formData.soId)}>
      View Version History
    </Button>
  </div>
)}
      <CustomForm formData={formData} onFinish={onFinish}>
        {renderFormFields(
          hydratedSoDetails,
          handleChange,
          formData,
          "",
          null,
          setFormData,
          handleSearch
        )}
        <ButtonContainer
          onFinish={onFinish}
          formData={formData}
          draftDataName="soDraft"
          submitBtnLoading={submitBtnLoading}
          submitBtnEnabled
          printBtnEnabled
          draftBtnEnabled
          handlePrint={handlePrint}
        />
      </CustomForm>

      <CustomModal
        isOpen={modalOpen}
        setIsOpen={setModalOpen}
        title="Service Order"
        processNo={generatedSOId}
      />
     <Modal
    open={versionHistoryOpen}
    onCancel={() => setVersionHistoryOpen(false)}
    title="SO Version History"
    footer={null}
    width={900}
    destroyOnClose
>
    {(() => {
        const sorted = [...versionHistoryList].sort((a, b) => (a.soVersion || 0) - (b.soVersion || 0));
        const selIdx = Math.max(0, Math.min(selectedVersionIdx, sorted.length - 1));
        const curr = sorted[selIdx];
        const prev = selIdx > 0 ? sorted[selIdx - 1] : null;

        if (!curr) return <div style={{ padding: '24px', textAlign: 'center', color: '#999' }}>No versions found.</div>;

        const HEADER_FIELDS = [
            { key: 'vendorName',               label: 'Vendor Name' },
            { key: 'vendorId',                 label: 'Vendor ID' },
            { key: 'vendorAddress',            label: 'Vendor Address' },
            { key: 'vendorsAccountNo',         label: 'Vendor Account No.' },
            { key: 'vendorsZRSCCode',          label: 'Vendor IFSC Code' },
            { key: 'vendorSwiftCode',          label: 'Vendor SWIFT Code' },
            { key: 'vendorType',               label: 'Vendor Type' },
            { key: 'vendorsAccountName',       label: 'Vendor Account Name' },
            { key: 'tenderId',                 label: 'Tender ID' },
            { key: 'jobCompletionPeriod',      label: 'Job Completion Period' },
            { key: 'startDateAmc',             label: 'AMC Start Date' },
            { key: 'endDateAmc',               label: 'AMC End Date' },
            { key: 'incoTerms',                label: 'Inco Terms' },
            { key: 'paymentTerms',             label: 'Payment Terms' },
            { key: 'ifLdClauseApplicable',     label: 'LD Clause' },
            { key: 'consignesAddress',         label: 'Consignee Address' },
            { key: 'billingAddress',           label: 'Billing Address' },
            { key: 'applicablePBGToBeSubmitted', label: 'Applicable PBG' },
            { key: 'projectName',              label: 'Project Name' },
        ];

        const LINE_FIELDS = [
            { key: 'jobCode',            label: 'Job Code' },
            { key: 'jobDescription',     label: 'Description' },
            { key: 'quantity',            label: 'Quantity' },
            { key: 'rate',                label: 'Rate' },
            { key: 'currency',            label: 'Currency' },
            { key: 'exchangeRate',        label: 'Exchange Rate' },
            { key: 'gst',                 label: 'GST' },
            { key: 'duties',              label: 'Duties' },
            { key: 'budgetCode',          label: 'Budget Code' },
        ];

        const headerDiffs = prev
            ? HEADER_FIELDS.filter(f => String(prev[f.key] ?? '') !== String(curr[f.key] ?? ''))
                .map(f => ({ ...f, oldVal: prev[f.key], newVal: curr[f.key] }))
            : [];

        const prevLines = prev?.materials || [];
        const currLines = curr.materials || [];
        const lineDiffs = [];
        const maxLen = Math.max(prevLines.length, currLines.length);
        for (let i = 0; i < maxLen; i++) {
            const p = prevLines[i];
            const c = currLines[i];
            if (!p) {
                lineDiffs.push({ idx: i, type: 'added', item: c });
            } else if (!c) {
                lineDiffs.push({ idx: i, type: 'removed', item: p });
            } else {
                const changed = LINE_FIELDS
                    .filter(f => String(p[f.key] ?? '') !== String(c[f.key] ?? ''))
                    .map(f => ({ ...f, oldVal: p[f.key], newVal: c[f.key] }));
                if (changed.length) lineDiffs.push({ idx: i, type: 'modified', changes: changed, label: c.jobDescription || `Item ${i + 1}` });
            }
        }

        const prevTotal = prev ? Number(prev.totalValue || 0) : null;
        const currTotal = Number(curr.totalValue || 0);
        const totalChanged = prev && prevTotal !== currTotal;
        const totalChanges = headerDiffs.length + lineDiffs.length + (totalChanged ? 1 : 0);

        const fmtCurrency = val => val != null ? `₹ ${Number(val).toLocaleString('en-IN', { maximumFractionDigits: 2 })}` : '—';
        const fmtVal = val => (val == null || val === '') ? '—' : String(val);

        return (
            <div style={{ display: 'flex', minHeight: '450px' }}>

                {/* Left: version list */}
                <div style={{ width: '190px', flexShrink: 0, borderRight: '1px solid #f0f0f0' }}>
                    <div style={{ padding: '8px 12px', fontWeight: 600, fontSize: '11px', color: '#aaa', letterSpacing: '1px', borderBottom: '1px solid #f0f0f0' }}>
                        VERSIONS
                    </div>
                    {sorted.map((v, idx) => {
                        const isSel = idx === selIdx;
                        return (
                            <div key={v.soId} onClick={() => setSelectedVersionIdx(idx)} style={{
                                padding: '10px 14px', cursor: 'pointer',
                                borderLeft: isSel ? '3px solid #1890ff' : '3px solid transparent',
                                background: isSel ? '#e6f7ff' : 'transparent',
                                borderBottom: '1px solid #f5f5f5',
                            }}>
                                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                                    <span style={{ fontWeight: 600, fontSize: '14px' }}>V{v.soVersion}</span>
                                    {v.isActive
                                        ? <Tag color="green" style={{ fontSize: '10px', margin: 0 }}>Active</Tag>
                                        : <Tag color="default" style={{ fontSize: '10px', margin: 0 }}>Old</Tag>}
                                </div>
                                <div style={{ fontSize: '11px', color: '#999', marginTop: '3px' }}>{v.updatedBy || v.createdBy || '—'}</div>
                                <div style={{ fontSize: '11px', color: '#bbb', marginTop: '1px' }}>
                                    {v.updatedDate ? new Date(v.updatedDate).toLocaleDateString('en-IN') : '—'}
                                </div>
                            </div>
                        );
                    })}
                </div>

                {/* Right: diff panel */}
                <div style={{ flex: 1, padding: '0 16px', overflowY: 'auto', maxHeight: '520px' }}>

                    <div style={{ padding: '12px 0', borderBottom: '1px solid #f0f0f0', display: 'flex', alignItems: 'center', gap: '8px', flexWrap: 'wrap' }}>
                        {prev ? (
                            <>
                                <span style={{ fontWeight: 600, color: '#888' }}>V{prev.soVersion}</span>
                                <span style={{ color: '#ccc' }}>→</span>
                                <span style={{ fontWeight: 600, color: '#1890ff' }}>V{curr.soVersion}</span>
                                {totalChanges === 0
                                    ? <Tag>No changes</Tag>
                                    : <Tag color="blue">{totalChanges} change{totalChanges !== 1 ? 's' : ''}</Tag>}
                            </>
                        ) : (
                            <span style={{ fontWeight: 600, color: '#52c41a' }}>V{curr.soVersion} — Initial Version</span>
                        )}
                        <Button type="link" size="small" style={{ marginLeft: 'auto', padding: 0 }}
                            onClick={() => { handleSearch(curr.soId); setVersionHistoryOpen(false); }}>
                            Load {curr.soId} ↗
                        </Button>
                    </div>

                    {!prev && (
                        <div style={{ padding: '16px 0', color: '#888', fontSize: '13px' }}>
                            This is the first version. No previous version to compare against.
                            <div style={{ marginTop: '12px' }}>
                                {HEADER_FIELDS.filter(f => curr[f.key]).map(f => (
                                    <div key={f.key} style={{ display: 'flex', padding: '6px 0', borderBottom: '1px solid #fafafa' }}>
                                        <span style={{ width: '180px', color: '#aaa', fontSize: '12px' }}>{f.label}</span>
                                        <span style={{ fontSize: '13px' }}>{fmtVal(curr[f.key])}</span>
                                    </div>
                                ))}
                            </div>
                        </div>
                    )}

                    {prev && totalChanges === 0 && (
                        <div style={{ padding: '24px 0', color: '#888', fontSize: '13px' }}>
                            No field-level changes detected compared to V{prev.soVersion}.
                        </div>
                    )}

                    {prev && totalChanges > 0 && (
                        <>
                            {totalChanged && (
                                <div style={{ marginTop: '16px' }}>
                                    <div style={{ fontWeight: 600, fontSize: '11px', color: '#aaa', letterSpacing: '1px', marginBottom: '8px' }}>TOTAL VALUE</div>
                                    <div style={{ display: 'flex', alignItems: 'center', gap: '10px', padding: '10px 14px', background: '#fffbe6', border: '1px solid #ffe58f', borderRadius: '6px' }}>
                                        <span style={{ fontSize: '12px', color: '#888', flex: 1 }}>Total SO Value</span>
                                        <span style={{ color: '#cf1322', textDecoration: 'line-through', fontSize: '13px' }}>{fmtCurrency(prevTotal)}</span>
                                        <span style={{ color: '#bbb' }}>→</span>
                                        <span style={{ color: '#389e0d', fontWeight: 600, fontSize: '13px' }}>{fmtCurrency(currTotal)}</span>
                                    </div>
                                </div>
                            )}

                            {headerDiffs.length > 0 && (
                                <div style={{ marginTop: '16px' }}>
                                    <div style={{ fontWeight: 600, fontSize: '11px', color: '#aaa', letterSpacing: '1px', marginBottom: '8px' }}>GENERAL FIELDS</div>
                                    {headerDiffs.map(f => (
                                        <div key={f.key} style={{ display: 'flex', alignItems: 'flex-start', gap: '10px', padding: '9px 14px', marginBottom: '4px', background: '#fffbe6', border: '1px solid #ffe58f', borderRadius: '4px' }}>
                                            <span style={{ width: '160px', flexShrink: 0, fontSize: '12px', color: '#888', paddingTop: '2px' }}>{f.label}</span>
                                            <span style={{ color: '#cf1322', textDecoration: 'line-through', fontSize: '13px' }}>{fmtVal(f.oldVal)}</span>
                                            <span style={{ color: '#bbb' }}>→</span>
                                            <span style={{ color: '#389e0d', fontWeight: 500, fontSize: '13px' }}>{fmtVal(f.newVal)}</span>
                                        </div>
                                    ))}
                                </div>
                            )}

                            {lineDiffs.length > 0 && (
                                <div style={{ marginTop: '16px' }}>
                                    <div style={{ fontWeight: 600, fontSize: '11px', color: '#aaa', letterSpacing: '1px', marginBottom: '8px' }}>LINE ITEMS</div>
                                    {lineDiffs.map((diff, i) => {
                                        const borderColor = diff.type === 'added' ? '#b7eb8f' : diff.type === 'removed' ? '#ffa39e' : '#ffe58f';
                                        const headerBg   = diff.type === 'added' ? '#f6ffed' : diff.type === 'removed' ? '#fff1f0' : '#fffbe6';
                                        const headerColor = diff.type === 'added' ? '#389e0d' : diff.type === 'removed' ? '#cf1322' : '#d48806';
                                        const prefix = diff.type === 'added' ? '+ ' : diff.type === 'removed' ? '− ' : '✎ ';
                                        return (
                                            <div key={i} style={{ marginBottom: '8px', borderRadius: '6px', overflow: 'hidden', border: `1px solid ${borderColor}` }}>
                                                <div style={{ padding: '7px 12px', fontSize: '12px', fontWeight: 600, background: headerBg, color: headerColor }}>
                                                    {prefix}Item {diff.idx + 1}{diff.type === 'modified' && diff.label ? ` — ${diff.label}` : ''}{diff.type !== 'modified' && diff.item?.jobDescription ? ` — ${diff.item.jobDescription}` : ''}
                                                </div>
                                                <div style={{ padding: '8px 12px', background: '#fff' }}>
                                                    {diff.type === 'modified'
                                                        ? diff.changes.map(c => (
                                                            <div key={c.key} style={{ display: 'flex', alignItems: 'center', gap: '10px', padding: '5px 0', borderBottom: '1px solid #f5f5f5' }}>
                                                                <span style={{ width: '120px', flexShrink: 0, fontSize: '12px', color: '#aaa' }}>{c.label}</span>
                                                                <span style={{ color: '#cf1322', textDecoration: 'line-through', fontSize: '13px' }}>{fmtVal(c.oldVal)}</span>
                                                                <span style={{ color: '#bbb' }}>→</span>
                                                                <span style={{ color: '#389e0d', fontWeight: 500, fontSize: '13px' }}>{fmtVal(c.newVal)}</span>
                                                            </div>
                                                        ))
                                                        : LINE_FIELDS.map(f => (
                                                            <div key={f.key} style={{ display: 'flex', padding: '5px 0', borderBottom: '1px solid #f5f5f5' }}>
                                                                <span style={{ width: '120px', flexShrink: 0, fontSize: '12px', color: '#aaa' }}>{f.label}</span>
                                                                <span style={{ fontSize: '13px' }}>{fmtVal(diff.item?.[f.key])}</span>
                                                            </div>
                                                        ))
                                                    }
                                                </div>
                                            </div>
                                        );
                                    })}
                                </div>
                            )}
                        </>
                    )}
                </div>
            </div>
        );
    })()}
</Modal>
    </Card>
  );
};

export default SO;
