import React, { useRef, useState, useEffect } from "react";
import { Button, Card, Form, message , Tag, Modal} from "antd";
import { HistoryOutlined } from "@ant-design/icons";
import { useReactToPrint } from "react-to-print";
import axios from "axios";
import { useSelector } from "react-redux";
import Heading from "../../../components/DKG_Heading";
import CustomForm from "../../../components/DKG_CustomForm";
import { renderFormFields } from "../../../utils/CommonFunctions";
import ButtonContainer from "../../../components/ButtonContainer";
import CustomModal from "../../../components/CustomModal";
import { PoDetails } from "./InputFields";
import { useLocation } from "react-router-dom";
import PoFormat from "../../../utils/Po-Format";
import { useLOVValues } from "../../../hooks/useLOVValues";

const PO = () => {
  const printRef = useRef();
  const [modalOpen, setModalOpen] = useState(false);
  const [submitBtnLoading, setSubmitBtnLoading] = useState(false);
  // [DRAFT] loading state for the Save Draft button
  const [draftBtnLoading, setDraftBtnLoading] = useState(false);
  const [generatedPOId, setGeneratedPOId] = useState("");
  const [poIdDropdown, setPoIdDropdown] = useState([]);
  const [searchDone, setSearchDone] = useState(false);
  const [completedVendors, setCompletedVendors] = useState([]);
  const [completedVendorIds, setCompletedVendorIds] = useState([]);
  const [completedVendorNames, setCompletedVendorNames] = useState([]);


  // Redux selectors
  const auth = useSelector((state) => state.auth);
  const actionPerformer = auth.userId;
  

  // Data states
  const [vendors, setVendors] = useState([]);
  const [tenders, setTenders] = useState([]);
  const [materials, setMaterials] = useState([]);
  const [formData, setFormData] = useState({
    materialDtlList: [],
    consignesAddress: "Bangalore",
    billingAddress: "Koramangala, Bangalore - 560034",
  });
  const location = useLocation();
      const { poId } = location.state || {};

      console.log("PO ID:", poId);
const [versionHistoryOpen, setVersionHistoryOpen] = useState(false);
const [versionHistoryList, setVersionHistoryList] = useState([]);
const [selectedVersionIdx, setSelectedVersionIdx] = useState(0);
  // ✅ Fetch LOV values for Purchase Order (Form ID: 8)
  const { lovValues: deliveryPeriodLOV, loading: loadingDeliveryPeriod } = useLOVValues(8, 'deliveryPeriod');
  const { lovValues: warrantyLOV, loading: loadingWarranty } = useLOVValues(8, 'warranty');
  const { lovValues: pbgLOV, loading: loadingPbg } = useLOVValues(8, 'applicablePbgToBeSubmitted'); 

  // Fetch initial data
  const populateDropdowns = async () => {
    try {
      const [vendorResponse, approvedTendersResponse] = await Promise.all([
        axios.get("/api/vendor-master"),
        axios.get("/getApprovedTenderIdForPO"),
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
   const handleSearchPoIds = async () => {
  const { searchType, searchValue } = formData;

  if (!searchValue || !searchType) {
    message.warning("Please select search type and enter value.");
    return;
  }

  try {
    const { data } = await axios.get(`/api/purchase-orders/search`, {
      params: {
        type: searchType,
        value: searchValue
      }
    });

    const poList = data?.responseData || [];

    const dropdownOptions = poList.map((item) => ({
      label: item.poId,
      value: item.poId
    }));

    setPoIdDropdown(dropdownOptions);

    if (dropdownOptions.length === 0) {
      message.warning("No po IDs found.");
    } else {
      message.success(`${dropdownOptions.length} Please Select PO Id in Po Id Drop Down.`);
    }
  } catch (error) {
    message.error("Error fetching indent IDs.");
  }
};



  const handleTenderSelect = async (tenderId) => {
    try {
      ;

      // 1. Fetch the full tender DTO by tenderId using axios and relative path
      const tenderRes = await axios.get(`/api/tender-requests/byId` , {params:{tenderId}});
      const tenderDto = tenderRes.data.responseData;
      ;

      // 2. Extract all material details from indentResponseDTO
      // AFTER — spread the entire material first, then override what you need
const allMaterials = (tenderDto.indentResponseDTO || []).flatMap(
  (indent) =>
    (indent.materialDetails || []).map((material) => ({
      ...material,                          // ← captures budgetCode + everything else
      rate: material.unitPrice,             // API uses unitPrice, form uses rate
      currency: material.currency || "INR",
      gst: material.gst || "",
      duties: material.duties || "",
    }))
);

      // 3. Update state and form
      setMaterials(allMaterials);
      let vendorIdOptions= [];
      let vendorNameOptions=[];
    try {
      // BR_PO_001: Fetch only SPO-approved vendors from Tender Evaluation.
      // Falls back to completed quotation vendors if evaluation not done yet.
      let completedVendorsData = [];
      try {
        const evalApprovedResp = await axios.get(
          `/api/tender-evaluation/approved-vendors`, { params: { tenderId } }
        );
        completedVendorsData = evalApprovedResp.data?.responseData || [];
      } catch (evalErr) {
        // If evaluation endpoint fails (e.g. eval not started), fall back
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

    // Auto-fill vendor details from vendorId
    const selectedVendor = vendors.find((v) => v.id === tenderDto.vendorId);

      setFormData((prev) => ({
        ...prev,
        tenderId,
        materialDtlList: allMaterials,
        incoTerms: tenderDto.incoTerms,
        paymentTerms: tenderDto.paymentTerms,
        vendorId: tenderDto.vendorId,
        vendorName: selectedVendor?.value || "",
        vendorAddress: selectedVendor?.address || "",
        vendorAccountNumber: selectedVendor?.accountNumber || "",
        vendorsIfscCode: selectedVendor?.ifscCode || "",
        vendorAccountName: selectedVendor?.accountName || "",
      }));
    } catch (error) {
      console.error("Tender selection error:", error);
      message.error("Failed to load tender details");
    }
  };

  // Hydrate form configuration
  const hydratedPoDetails = PoDetails.map((section) => {
    if (section.fieldList) {
      return {
        ...section,
        fieldList: section.fieldList.map((field) => {
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

              // updated by abhinav
              if (field.name === "vendorId") {

                const vendorOptions = completedVendorIds.length
                  ? completedVendorIds
                  : vendors.map((v) => ({
                      label: v.id,
                      value: v.id,
                    }));

                return {
                  ...field,
                  options: [
                    ...vendorOptions,
                    // { label: "OTHERS (Manual Vendor)", value: "OTHERS" }
                  ],
                };
              }

              // updated by abhinav
              if (field.name === "vendorName") {

                const isManualVendor = formData.vendorId === "OTHERS";

                return {
                  ...field,
                  type: "text",   // always text
                  options: isManualVendor
                    ? []
                    : (completedVendorNames.length
                        ? completedVendorNames
                        : vendors.map((v) => ({
                            label: v.value,
                            value: v.value
                          }))
                      ),
                  props: {
                    readOnly: !isManualVendor
                  }
                };
              }

               if (field.name === "poId") {
                return {
                ...field,
                options: poIdDropdown,
                props: {
                  onChange: (value) => handleSearch(value),
                },
                };
              }

                if (field.name === "searchValue") {
                    return {
                        ...field,
                        onSearch: handleSearchPoIds,
                    };
                }

          // ✅ LOV Integration for Purchase Order fields
          if (field.name === "deliveryPeriod") {
            return {
              ...field,
              options: deliveryPeriodLOV.length > 0
                ? deliveryPeriodLOV.map(lov => ({ label: lov.lovDisplayValue, value: lov.lovValue }))
                : field.options
            };
          }

          if (field.name === "warranty") {
            return {
              ...field,
              options: warrantyLOV.length > 0
                ? warrantyLOV.map(lov => ({ label: lov.lovDisplayValue, value: lov.lovValue }))
                : field.options
            };
          }

          if (field.name === "applicablePbgToBeSubmitted") {
            return {
              ...field,
              options: pbgLOV.length > 0
                ? pbgLOV.map(lov => ({ label: lov.lovDisplayValue, value: lov.lovValue }))
                : field.options
            };
          }

          return field;
        }),
      };
    }
    if (section.name === "materialDtlList") {
      return {
        ...section,
        children: section.children.map((child) => ({
          ...child,
          options: child.name === "materialCode" ? materials : child.options,
        })),
      };
    }
    return section;
  });

  // added by abhinav
  const handleChange = (name, value) => {
    if (name === "vendorName") {

      if (formData.vendorId === "OTHERS") {
        setFormData(prev => ({
          ...prev,
          vendorName: value
        }));
        return;
      }
      const selectedVendor = vendors.find((v) => v.value === value);
      setFormData((prev) => ({
        ...prev,
        vendorName: value,
        vendorId: selectedVendor?.id || "",
        vendorAddress: selectedVendor?.address || "",
        vendorAccountNumber: selectedVendor?.accountNumber || "",
        vendorsIfscCode: selectedVendor?.ifscCode || "",
        vendorAccountName: selectedVendor?.accountName || "",
      }));
      return;
    }

    // updated by abhinav
    if (name === "vendorId") {

      if (value === "OTHERS") {
        setFormData((prev) => ({
          ...prev,
          vendorId: "OTHERS",
          vendorName: "",
          vendorAddress: "",
          vendorAccountNumber: "",
          vendorsIfscCode: "",
          vendorAccountName: "",
        }));
        return;
      }

      const selectedVendor = vendors.find((v) => v.id === value);

      setFormData((prev) => ({
        ...prev,
        vendorId: value,
        vendorName: selectedVendor?.value || "",
        vendorAddress: selectedVendor?.address || "",
        vendorAccountNumber: selectedVendor?.accountNumber || "",
        vendorsIfscCode: selectedVendor?.ifscCode || "",
        vendorAccountName: selectedVendor?.accountName || "",
      }));
    }
    if (Array.isArray(name)) {
      const [section, index, field] = name;
      if (section === "materialDtlList") {
        setFormData((prev) => {
          const updated = [...(prev.materialDtlList || [])];
          updated[index] = { ...updated[index], [field]: value };
          const item = updated[index];
          const rate = parseFloat(item.rate || 0);
          const exchangeRate = parseFloat(item.exchangeRate || 0);

      if (item.currency && item.currency !== "INR" && rate > 0 && exchangeRate > 0) {
        updated[index].inrEquivalent = (rate * exchangeRate).toFixed(2);
      } else {
        updated[index].inrEquivalent = "";
      }
      // Per-item estimated total in INR
const baseRate   = item.currency && item.currency !== "INR"
  ? parseFloat(item.rate || 0) * parseFloat(item.exchangeRate || 1)
  : parseFloat(item.rate || 0);
const baseAmount = baseRate * parseFloat(item.quantity || 0);
updated[index].estimatedItemTotal = (
  baseAmount
  + baseAmount * parseFloat(item.gst    || 0) / 100
  + baseAmount * parseFloat(item.duties || 0) / 100
  + parseFloat(item.freightCharge || 0)
).toFixed(2);

     
          return { ...prev, materialDtlList: updated };
        });
      }
    } else {
      setFormData((prev) => ({ ...prev, [name]: value }));
    }
  };

  useEffect(() => {
        if (poId) {
        handleSearch(poId); 
    }
    }, [poId]);

  // ─────────────────────────────────────────────────────────────────
  // DRAFT SUPPORT — API-based (replaces the old localStorage approach)
  // ─────────────────────────────────────────────────────────────────

  /**
   * Shared payload builder used by both onFinish and handleSaveDraft
   * so the shape is always identical.
   */
  const buildPoPayload = () => ({
    ...formData,
    createdBy: actionPerformer,
    purchaseOrderAttributes: (formData.materialDtlList || []).map((m) => ({
      budgetCode: m.budgetCode || "",
      currency: m.currency || "",
      duties: Number(m.duties) || 0,
      exchangeRate: Number(m.exchangeRate) || 0,
      freightCharge: Number(m.freightCharge) || 0,
      gst: Number(m.gst) || 0,
      materialCode: m.materialCode || "",
      materialDescription: m.materialDescription || "",
      quantity: Number(m.quantity) || 0,
      rate: Number(m.rate) || 0,
    })),
  });

  /**
   * Save or update a PO draft via the backend.
   * - No tender/validation requirement (backend is lenient for DRAFT status).
   * - If formData already has a DRAFT poId → PUT /draft (update in-place).
   * - Otherwise → POST /draft (create new draft row).
   */
  const handleSaveDraft = async () => {
    try {
      setDraftBtnLoading(true);
      const payload = buildPoPayload();
      let response;

      if (formData?.poId && formData?.currentStatus === "DRAFT") {
        // Update existing draft in-place (same poId, no versioning)
        response = await axios.put(`/api/purchase-orders/draft`, payload, {
          params: { poId: formData.poId },
        });
        message.success("Draft updated successfully.");
      } else if (!formData?.poId) {
        // Save a brand-new draft
        response = await axios.post(`/api/purchase-orders/draft`, payload);
        message.success("Draft saved successfully.");
      } else {
        // Already a submitted PO — cannot downgrade to draft
        message.warning("This Purchase Order has already been submitted and cannot be saved as a draft.");
        return;
      }

      const savedData = response?.data?.responseData;
      // Persist the generated poId and DRAFT status back into form state
      setFormData((prev) => ({
        ...prev,
        poId: savedData?.poId,
        currentStatus: "DRAFT",
      }));
    } catch (error) {
      message.error(
        error?.response?.data?.responseStatus?.message || "Error saving draft."
      );
    } finally {
      setDraftBtnLoading(false);
    }
  };

  // ─────────────────────────────────────────────────────────────────

  const onFinish = async () => {
    try {
      // [DRAFT] If this is a saved draft, promote it via the draft/submit endpoint
      // which runs full validation + budget check + workflow initiation.
      if (formData?.poId && formData?.currentStatus === "DRAFT") {
        setSubmitBtnLoading(true);
        if (!formData.tenderId) {
          message.error("Please select a Tender ID before submitting.");
          return;
        }
        const payload = buildPoPayload();
        const response = await axios.post(
          `/api/purchase-orders/draft/submit`,
          payload,
          { params: { poId: formData.poId } }
        );
        const submittedPoId = response.data?.responseData?.poId;
        setFormData((prev) => ({
          ...prev,
          poId: submittedPoId,
          currentStatus: null,
        }));
        message.success("Purchase Order submitted successfully.");
        setGeneratedPOId(submittedPoId || formData.poId);
        setModalOpen(true);
        return; // stop here — non-draft paths handled below
      }

      // ── Normal create / update path ──────────────────────────────
      if (!formData.tenderId) {
        message.error("Please select a Tender ID before submitting.");
        return;
      }
      setSubmitBtnLoading(true);

      console.log("materialDtlList at submit:", JSON.stringify(formData.materialDtlList, null, 2));

      const payload = buildPoPayload();

      let data;
      if (formData.poId) {
        // Update (creates a new versioned PO row)
        const response = await axios.put(`/api/purchase-orders`, payload, {
          params: { poId: formData.poId }
        });
        data = response.data;
        const newPoId = data?.responseData?.poId;
        if (newPoId) {
          setFormData(prev => ({ ...prev, poId: newPoId }));
        }
        message.success("Purchase Order updated successfully");
      } else {
        // Create
        const response = await axios.post("/api/purchase-orders", payload);
        data = response.data;
        message.success("Purchase Order created successfully");
      }

      setGeneratedPOId(data.responseData.poId);
      setModalOpen(true);
    } catch (error) {
      message.error("Failed to submit purchase order");
    } finally {
      setSubmitBtnLoading(false);
    }
  };


  const handleSearch = async (value) => {
    try {
      const { data } = await axios.get(
        `/api/purchase-orders/base64Files`,
        { params: { poId: value || formData.poId } }
      );
      const responseData = data?.responseData || {};

      setFormData({
        ...responseData,
        materialDtlList: responseData?.purchaseOrderAttributes || [],
        comparativeStatementFileName: Array.isArray(responseData?.comparativeStatementFileNameList)
          ? responseData.comparativeStatementFileNameList
          : [],
      });
      console.log(formData);
      setSearchDone(true);
      if (responseData.isActive === false) {
        message.warning("You are viewing an older version of this PO. Load the latest version to make changes.");
      }
    } catch (error) {
      ;
      message.error(
        error?.response?.data?.responseStatus?.message || "Error fetching data."
      );
    }
  };

  const hydratedPoDetailsWithConditionalFields = hydratedPoDetails.map((section) => {
  if (section.name === "materialDtlList") {
    return {
      ...section,
      children: section.children.map((child) => {
        if (child.name === "exchangeRate") {
          return {
            ...child,
            required: (formData?.materialDtlList || []).some(
              (m) => m.currency && m.currency !== "INR"
            ),
            shouldShow: () =>
              (formData?.materialDtlList || []).some(
                (m) => m.currency && m.currency !== "INR"
              ),
          };
        }

        if (child.name === "inrEquivalent") {
          return {
            ...child,
            shouldShow: () =>
              (formData?.materialDtlList || []).some(
                (m) => m.currency && m.currency !== "INR"
              ),
          };
        }

        return child;
      }),
    };
  }
   
  if (section.fieldList) {
    return {
      ...section,
      fieldList: section.fieldList.map((field) => {
        if (field.name === "processStage" || field.name === "status") {
          return {
            ...field,
            shouldShow: () => searchDone,
          };
        }

        if (field.name === "gemContractFileName") {
          return {
            ...field,
              shouldShow: () =>
              formData.status === "Completed" &&
              formData.tenderDetails?.modeOfProcurement === "GEM",
          };
      }
     if (field.name === "buyBackAmount") {
  return {
    ...field,
    shouldShow: () => formData?.indentResponseDTO?.buyBack === "true"
  };
}

        if (
          field.name === "typeOfSecurity" ||
          field.name === "securityNumber" ||
          field.name === "securityDate" ||
          field.name === "expiryDate"
        ) {
          return {
            ...field,
            shouldShow: () => formData.status === "Completed",
          };
        }
        return field;
      }),
    };
  }
  return section;
});
 

  // Load initial data
  useEffect(() => {
    populateDropdowns();
  }, []);

  // --- Printing Function ---

   const printComponentRef = useRef(); 

    const handlePrint = useReactToPrint({
        content: () => printComponentRef.current,
        documentTitle: `Po - ${formData?.poId || "Draft"}`
    });


   const fetchPoVersionHistory = async (pid) => {
  try {
    const basePid = (pid || formData.poId || "").split("/")[0];
    const { data } = await axios.get(`/api/purchase-orders/version-history`, { params: { poId: basePid } });
    const list = data?.responseData || [];
    setVersionHistoryList(list);
    setSelectedVersionIdx(list.length - 1);
    setVersionHistoryOpen(true);
  } catch (error) {
    message.error("Could not load PO version history.");
  }
};

  return (
    <Card className="a4-container" ref={printRef}>
      <Heading title="Purchase Order Creation" />
      {formData?.poId && (
  <div style={{ marginBottom: 16 }}>
    {/* Old-version warning */}
    {formData.isActive === false && (
      <div style={{ background: '#fff7e6', border: '1px solid #ffd591', padding: '8px 16px', borderRadius: 4, marginBottom: 8 }}>
        ⚠️ Viewing Old Version (V{formData.poVersion}) — This is a superseded version. Load the latest to make changes.
      </div>
    )}
    {/* [DRAFT] banner — shown whenever the loaded PO is still in DRAFT status */}
    {formData.currentStatus === "DRAFT" && (
      <div style={{ background: '#fffbe6', border: '1px solid #ffe58f', padding: '8px 16px', borderRadius: 4, marginBottom: 8, display: 'flex', alignItems: 'center', gap: 8 }}>
        <span>📝</span>
        <span>
          <strong>Saved Draft</strong> — This Purchase Order ({formData.poId}) is a saved draft and has <strong>not</strong> been submitted for approval.
          Click <strong>Submit</strong> when ready to send it through the workflow.
        </span>
      </div>
    )}
    <Button icon={<HistoryOutlined />} onClick={() => fetchPoVersionHistory(formData.poId)}>
      View Version History
    </Button>
  </div>
)}
      <CustomForm formData={formData} onFinish={onFinish}>
        {renderFormFields(
         hydratedPoDetailsWithConditionalFields,
          handleChange,
          formData,
          "",
          null,
          setFormData,
          handleSearch
        )}
        
        {/* [DRAFT] onDraft + draftBtnLoading replace the old draftDataName="poDraft" */}
        <ButtonContainer
          onFinish={onFinish}
          formData={formData}
          onDraft={handleSaveDraft}
          draftBtnLoading={draftBtnLoading}
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
        title="Purchase Order"
        processNo={generatedPOId}
      />
    

<Modal
    open={versionHistoryOpen}
    onCancel={() => setVersionHistoryOpen(false)}
    title="PO Version History"
    footer={null}
    width={900}
    destroyOnClose
>
    {(() => {
        const sorted = [...versionHistoryList].sort((a, b) => (a.poVersion || 0) - (b.poVersion || 0));
        const selIdx = Math.max(0, Math.min(selectedVersionIdx, sorted.length - 1));
        const curr = sorted[selIdx];
        const prev = selIdx > 0 ? sorted[selIdx - 1] : null;

        if (!curr) return <div style={{ padding: '24px', textAlign: 'center', color: '#999' }}>No versions found.</div>;

        const HEADER_FIELDS = [
            { key: 'vendorName',          label: 'Vendor Name' },
            { key: 'vendorId',            label: 'Vendor ID' },
            { key: 'vendorAddress',       label: 'Vendor Address' },
            { key: 'vendorAccountNumber', label: 'Vendor Account No.' },
            { key: 'vendorsIfscCode',     label: 'Vendor IFSC Code' },
            { key: 'vendorAccountName',   label: 'Vendor Account Name' },
            { key: 'deliveryPeriod',      label: 'Delivery Period' },
            { key: 'deliveryDate',        label: 'Delivery Date' },
            { key: 'incoTerms',           label: 'Inco Terms' },
            { key: 'paymentTerms',        label: 'Payment Terms' },
            { key: 'warranty',            label: 'Warranty' },
            { key: 'ifLdClauseApplicable', label: 'LD Clause' },
            { key: 'consignesAddress',    label: 'Consignee Address' },
            { key: 'billingAddress',      label: 'Billing Address' },
            { key: 'projectName',         label: 'Project Name' },
            { key: 'quotationNumber',     label: 'Quotation Number' },
            { key: 'quotationDate',       label: 'Quotation Date' },
            { key: 'buyBackAmount',       label: 'Buy Back Amount' },
            { key: 'additionalTermsAndConditions', label: 'Additional T&C' },
            { key: 'applicablePbgToBeSubmitted',   label: 'Applicable PBG' },
            { key: 'transporterAndFreightForWarderDetails', label: 'Transporter/Freight Details' },
            { key: 'comparativeStatementFileName', label: 'Comparative Statement File' },
            { key: 'gemContractFileName',          label: 'GeM Contract File' },
            { key: 'typeOfSecurity',      label: 'Type of Security' },
            { key: 'securityNumber',      label: 'Security Number' },
            { key: 'securityDate',        label: 'Security Date' },
            { key: 'expiryDate',          label: 'Expiry Date' },
        ];

        const LINE_FIELDS = [
            { key: 'materialCode',        label: 'Material Code' },
            { key: 'materialDescription', label: 'Description' },
            { key: 'quantity',            label: 'Quantity' },
            { key: 'rate',                label: 'Rate' },
            { key: 'currency',            label: 'Currency' },
            { key: 'exchangeRate',        label: 'Exchange Rate' },
            { key: 'gst',                 label: 'GST' },
            { key: 'duties',              label: 'Duties' },
            { key: 'freightCharge',       label: 'Freight' },
            { key: 'budgetCode',          label: 'Budget Code' },
            { key: 'uom',                 label: 'UOM' },
        ];

        const headerDiffs = prev
            ? HEADER_FIELDS.filter(f => String(prev[f.key] ?? '') !== String(curr[f.key] ?? ''))
                .map(f => ({ ...f, oldVal: prev[f.key], newVal: curr[f.key] }))
            : [];

        const prevLines = prev?.purchaseOrderAttributes || [];
        const currLines = curr.purchaseOrderAttributes || [];
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
                if (changed.length) lineDiffs.push({ idx: i, type: 'modified', changes: changed, label: c.materialDescription || `Item ${i + 1}` });
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
                            <div key={v.poId} onClick={() => setSelectedVersionIdx(idx)} style={{
                                padding: '10px 14px', cursor: 'pointer',
                                borderLeft: isSel ? '3px solid #1890ff' : '3px solid transparent',
                                background: isSel ? '#e6f7ff' : 'transparent',
                                borderBottom: '1px solid #f5f5f5',
                            }}>
                                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                                    <span style={{ fontWeight: 600, fontSize: '14px' }}>V{v.poVersion}</span>
                                    {v.currentStatus === 'DRAFT'
                                        ? <Tag color="gold" style={{ fontSize: '10px', margin: 0 }}>Draft</Tag>
                                        : v.isActive
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
                                <span style={{ fontWeight: 600, color: '#888' }}>V{prev.poVersion}</span>
                                <span style={{ color: '#ccc' }}>→</span>
                                <span style={{ fontWeight: 600, color: '#1890ff' }}>V{curr.poVersion}</span>
                                {totalChanges === 0
                                    ? <Tag>No changes</Tag>
                                    : <Tag color="blue">{totalChanges} change{totalChanges !== 1 ? 's' : ''}</Tag>}
                            </>
                        ) : (
                            <span style={{ fontWeight: 600, color: '#52c41a' }}>V{curr.poVersion} — Initial Version</span>
                        )}
                        <Button type="link" size="small" style={{ marginLeft: 'auto', padding: 0 }}
                            onClick={() => { handleSearch(curr.poId); setVersionHistoryOpen(false); }}>
                            Load {curr.poId} ↗
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
                            No field-level changes detected compared to V{prev.poVersion}.
                        </div>
                    )}

                    {prev && totalChanges > 0 && (
                        <>
                            {/* Total value */}
                            {totalChanged && (
                                <div style={{ marginTop: '16px' }}>
                                    <div style={{ fontWeight: 600, fontSize: '11px', color: '#aaa', letterSpacing: '1px', marginBottom: '8px' }}>TOTAL VALUE</div>
                                    <div style={{ display: 'flex', alignItems: 'center', gap: '10px', padding: '10px 14px', background: '#fffbe6', border: '1px solid #ffe58f', borderRadius: '6px' }}>
                                        <span style={{ fontSize: '12px', color: '#888', flex: 1 }}>Total PO Value</span>
                                        <span style={{ color: '#cf1322', textDecoration: 'line-through', fontSize: '13px' }}>{fmtCurrency(prevTotal)}</span>
                                        <span style={{ color: '#bbb' }}>→</span>
                                        <span style={{ color: '#389e0d', fontWeight: 600, fontSize: '13px' }}>{fmtCurrency(currTotal)}</span>
                                    </div>
                                </div>
                            )}

                            {/* Header field changes */}
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

                            {/* Line item changes */}
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
                                                    {prefix}Item {diff.idx + 1}{diff.type === 'modified' && diff.label ? ` — ${diff.label}` : ''}{diff.type !== 'modified' && diff.item?.materialDescription ? ` — ${diff.item.materialDescription}` : ''}
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
       <div style={{ display: "none" }}>
                <PoFormat ref={printComponentRef} po={formData} />
      </div>
    </Card>
  );
};

export default PO;
