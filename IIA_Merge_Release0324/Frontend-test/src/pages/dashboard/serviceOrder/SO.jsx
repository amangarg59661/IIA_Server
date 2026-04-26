import React, { useRef, useState, useEffect } from "react";
import { Button, Card, Form, message } from "antd";
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
const [searchDone, setSearchDone] = useState(false);
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

  // Fetch initial data
  const populateDropdowns = async () => {
    try {
      const [vendorResponse, approvedTendersResponse] = await Promise.all([
        axios.get("/api/vendor-master"),
        axios.get("/getApprovedTenderIdForPOAndSO"),
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

  const handleTenderSelect = async (tenderId) => {
    try {
      ;

      // 1. Fetch the full tender DTO by tenderId using axios and relative path
      const tenderRes = await axios.get(`/api/tender-requests/byId`, { params: { tenderId } });
      // const tenderRes = await axios.get(`/api/tender-requests/${tenderId}`);
      const tenderDto = tenderRes.data.responseData;
      ;

      // 2. Extract all material details from indentResponseDTO
      const allMaterials = (tenderDto.indentResponseDTO || []).flatMap(
        (indent) =>
          (indent.materialDetails || []).map((material) => ({
            materialCode: material.materialCode,
            materialDescription: material.materialDescription,
            budgetCode: material.budgetCode,
            quantity: material.quantity,
            rate: material.unitPrice,
            uom: material.uom,
            currency: material.currency || "INR",
            gst: material.gst || "",
            duties: material.duties || "",
            // Add other fields as needed
          }))
      );

      // 3. Update state and form
      setMaterials(allMaterials);

      setFormData((prev) => ({
        ...prev,
        tenderId,
        materialDtlList: allMaterials,
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
          if (field.name === "vendorName")
            return { ...field, options: vendors };
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
          if (field.name === "vendorId")
            return {
              ...field,
              options: vendors.map((v) => ({
                label: v.id,
                value: v.id,
              })),
            };
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
          vendorsAccountName: selectedVendor?.accountName || "",
        }));
        return;
      }
    if (Array.isArray(name)) {
      const [section, index, field] = name;
      if (section === "materialDtlList") {
        setFormData((prev) => {
          const updated = [...(prev.materialDtlList || [])];
          updated[index] = { ...updated[index], [field]: value };
          return { ...prev, materialDtlList: updated };
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
        materials: (formData.materialDtlList || []).map((m) => ({
          budgetCode: m.budgetCode || "",
          currency: m.currency || "",
          duties: Number(m.duties) || 0,
          exchangeRate: Number(m.exchangeRate) || 0,
          gst: Number(m.gst) || 0,
          materialCode: m.materialCode || "",
          materialDescription: m.materialDescription || "",
          budgetCode: m.budgetCode || "",
          quantity: Number(m.quantity) || 0,
          rate: Number(m.rate) || 0,
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
        materialDtlList: responseData?.materials || [],
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

  const fetchSoVersionHistory = async (sid) => {
  try {
    const baseSid = (sid || formData.soId || "").split("/")[0];
    const { data } = await axios.get(`/api/service-orders/version-history/${baseSid}`);
    setVersionHistoryList(data?.responseData || []);
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
      {versionHistoryOpen && (
  <div style={{
    position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
    background: 'rgba(0,0,0,0.5)', zIndex: 1000,
    display: 'flex', alignItems: 'center', justifyContent: 'center'
  }}>
    <div style={{ background: 'white', borderRadius: 8, padding: 24, minWidth: 600, maxHeight: '80vh', overflow: 'auto' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 16 }}>
        <h3 style={{ margin: 0 }}>SO Version History</h3>
        <Button onClick={() => setVersionHistoryOpen(false)}>Close</Button>
      </div>
      <table style={{ width: '100%', borderCollapse: 'collapse' }}>
        <thead>
          <tr style={{ background: '#f0f0f0' }}>
            <th style={{ padding: '8px', border: '1px solid #ddd' }}>Version</th>
            <th style={{ padding: '8px', border: '1px solid #ddd' }}>SO ID</th>
            <th style={{ padding: '8px', border: '1px solid #ddd' }}>Updated By</th>
            <th style={{ padding: '8px', border: '1px solid #ddd' }}>Date</th>
            <th style={{ padding: '8px', border: '1px solid #ddd' }}>Status</th>
          </tr>
        </thead>
        <tbody>
          {versionHistoryList.map((v) => (
            <tr key={v.soId} style={{ background: v.isActive ? '#f6ffed' : 'white' }}>
              <td style={{ padding: '8px', border: '1px solid #ddd', textAlign: 'center' }}>V{v.soVersion}</td>
              <td style={{ padding: '8px', border: '1px solid #ddd' }}>
                <Button type="link" onClick={() => { handleSearch(v.soId); setVersionHistoryOpen(false); }}>
                  {v.soId}
                </Button>
              </td>
              <td style={{ padding: '8px', border: '1px solid #ddd' }}>{v.updatedBy || '-'}</td>
              <td style={{ padding: '8px', border: '1px solid #ddd' }}>
                {v.updatedDate ? new Date(v.updatedDate).toLocaleDateString() : '-'}
              </td>
              <td style={{ padding: '8px', border: '1px solid #ddd' }}>
                {v.isActive
                  ? <span style={{ color: 'green' }}>● Active</span>
                  : <span style={{ color: '#999' }}>○ Superseded</span>
                }
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  </div>
)}
    </Card>
  );
};

export default SO;
