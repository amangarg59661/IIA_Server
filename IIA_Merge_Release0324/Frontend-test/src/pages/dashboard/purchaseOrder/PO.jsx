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
import { PoDetails } from "./InputFields";
import { useLocation } from "react-router-dom";
import PoFormat from "../../../utils/Po-Format";
import { useLOVValues } from "../../../hooks/useLOVValues";

const PO = () => {
  const printRef = useRef();
  const [modalOpen, setModalOpen] = useState(false);
  const [submitBtnLoading, setSubmitBtnLoading] = useState(false);
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
  // ✅ Fetch LOV values for Purchase Order (Form ID: 8)
  const { lovValues: deliveryPeriodLOV, loading: loadingDeliveryPeriod } = useLOVValues(8, 'deliveryPeriod');
  const { lovValues: warrantyLOV, loading: loadingWarranty } = useLOVValues(8, 'warranty');
  const { lovValues: pbgLOV, loading: loadingPbg } = useLOVValues(8, 'applicablePbgToBeSubmitted'); 

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
      const allMaterials = (tenderDto.indentResponseDTO || []).flatMap(
        (indent) =>
          (indent.materialDetails || []).map((material) => ({
            materialCode: material.materialCode,
            materialDescription: material.materialDescription,
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
      let vendorIdOptions= [];
      let vendorNameOptions=[];
    try {
      const completedResp = await axios.get(`/api/vendor-quotation/completed-vendorNames`, {params:{tenderId}});
      const completedVendorsData = completedResp.data?.responseData || [];
    vendorIdOptions = completedVendorsData.map((vendor) => ({
  label: vendor.vendorId,
  value: vendor.vendorId,
}));
vendorNameOptions = completedVendorsData.map((vendor) => ({
  label: vendor.vendorName,
  value: vendor.vendorName,
}));


  setCompletedVendorIds(vendorIdOptions);
  setCompletedVendorNames(vendorNameOptions);
      
    } catch (e) {
      console.warn("Failed to fetch completed vendors:", e);
     // setCompletedVendors([]); // fallback
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
          //if (field.name === "vendorName")
           // return { ...field, options: vendors };
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
           /* if (field.name === "vendorId")
                return {
                  ...field,
                  options: vendors.map((v) => ({
                    label: v.id,
                    value: v.id,
                  })),
                };*/
              //  if (field.name === "vendorId") {
              //   return {
              //     ...field,
              //     options: completedVendorIds.length
              //     ? completedVendorIds
              //     : vendors.map((v) => ({ label: v.id, value: v.id })),
              //   };
              // }

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
                    { label: "OTHERS (Manual Vendor)", value: "OTHERS" }
                  ],
                };
              }

              // if (field.name === "vendorName") {
              //   return {
              //     ...field,
              //     options: completedVendorNames.length
              //     ? completedVendorNames
              //     : vendors.map((v) => ({ label: v.name, value: v.name })), // fallback
              //   };
              // }

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


                /* if (field.name === "poId") {
                    return {
                        ...field,
                        options: poIdDropdown,
                    };
                }*/
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
    // if (name === "vendorId") {
    //     const selectedVendor = vendors.find((v) => v.id === value);
    //     setFormData((prev) => ({
    //       ...prev,
    //       vendorId: value,
    //       vendorName: selectedVendor?.value || "",
    //       vendorAddress: selectedVendor?.address || "",
    //       vendorAccountNumber: selectedVendor?.accountNumber || "",
    //     vendorsIfscCode: selectedVendor?.ifscCode || "",
    //     vendorAccountName: selectedVendor?.accountName || "",
    //     }));
    //     return;
    //   }

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

/*
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
      };

      const { data } = await axios.post("/api/purchase-orders", payload);
      setGeneratedPOId(data.responseData.poId);
      setModalOpen(true);
    } catch (error) {
      message.error("Failed to create purchase order");
    } finally {
      setSubmitBtnLoading(false);
    }
  };*/
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
    };

    let data;
    if (formData.poId) {
      // Update
      // const response = await axios.put(`/api/purchase-orders/${formData.poId}`, payload);
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
      // const { data } = await axios.get(
      //   `/api/purchase-orders/base64Files/${value ? value : formData.poId}`
      // );
      const { data } = await axios.get(
  `/api/purchase-orders/base64Files`,
  { params: { poId: value || formData.poId } }
);
      const responseData = data?.responseData || {};

      setFormData({
        ...responseData,
        materialDtlList: responseData?.purchaseOrderAttributes || [],
       // comparativeStatementFileName: responseData?.comparativeStatementFileNameList || [],
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

  useEffect(() => {
    const poDraft = localStorage.getItem("poDraft");
    if (poDraft) {
      setFormData(JSON.parse(poDraft));
      message.success("Form loaded from draft.");
    }
  }, []);

  useEffect(() => {
    localStorage.setItem("poDraft", JSON.stringify(formData));
  }, [formData]);

  // --- Printing Function ---


   const printComponentRef = useRef(); 

    const handlePrint = useReactToPrint({
        content: () => printComponentRef.current,
        documentTitle: `Po - ${formData?.poId || "Draft"}`
    });


    const fetchPoVersionHistory = async (pid) => {
  try {
    const basePid = (pid || formData.poId || "").split("/")[0];
    const { data } = await axios.get(`/api/purchase-orders/version-history/` , {params: {poId:basePid}});
    setVersionHistoryList(data?.responseData || []);
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
    {formData.isActive === false && (
      <div style={{ background: '#fff7e6', border: '1px solid #ffd591', padding: '8px 16px', borderRadius: 4, marginBottom: 8 }}>
        ⚠️ Viewing Old Version (V{formData.poVersion}) — This is a superseded version. Load the latest to make changes.
      </div>
    )}
    <Button onClick={() => fetchPoVersionHistory(formData.poId)}>
      View Version History
    </Button>
  </div>
)}
      <CustomForm formData={formData} onFinish={onFinish}>
        {renderFormFields(
         // hydratedPoDetails,
         hydratedPoDetailsWithConditionalFields,
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
          draftDataName="poDraft"
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
      {versionHistoryOpen && (
  <div style={{
    position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
    background: 'rgba(0,0,0,0.5)', zIndex: 1000,
    display: 'flex', alignItems: 'center', justifyContent: 'center'
  }}>
    <div style={{ background: 'white', borderRadius: 8, padding: 24, minWidth: 600, maxHeight: '80vh', overflow: 'auto' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 16 }}>
        <h3 style={{ margin: 0 }}>PO Version History</h3>
        <Button onClick={() => setVersionHistoryOpen(false)}>Close</Button>
      </div>
      <table style={{ width: '100%', borderCollapse: 'collapse' }}>
        <thead>
          <tr style={{ background: '#f0f0f0' }}>
            <th style={{ padding: '8px', border: '1px solid #ddd' }}>Version</th>
            <th style={{ padding: '8px', border: '1px solid #ddd' }}>PO ID</th>
            <th style={{ padding: '8px', border: '1px solid #ddd' }}>Updated By</th>
            <th style={{ padding: '8px', border: '1px solid #ddd' }}>Date</th>
            <th style={{ padding: '8px', border: '1px solid #ddd' }}>Status</th>
          </tr>
        </thead>
        <tbody>
          {versionHistoryList.map((v) => (
            <tr key={v.poId} style={{ background: v.isActive ? '#f6ffed' : 'white' }}>
              <td style={{ padding: '8px', border: '1px solid #ddd', textAlign: 'center' }}>V{v.poVersion}</td>
              <td style={{ padding: '8px', border: '1px solid #ddd' }}>
                <Button type="link" onClick={() => { handleSearch(v.poId); setVersionHistoryOpen(false); }}>
                  {v.poId}
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
       <div style={{ display: "none" }}>
                <PoFormat ref={printComponentRef} po={formData} />
      </div>
    </Card>
  );
};

export default PO;
