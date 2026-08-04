import { Card, message } from "antd";
import React, { useEffect, useRef, useState } from "react";
import Heading from "../../../components/DKG_Heading";
import CustomForm from "../../../components/DKG_CustomForm";
import { renderFormFields } from "../../../utils/CommonFunctions";
import { invoiceFields } from "./InvoiceFields";
import ButtonContainer from "../../../components/ButtonContainer";
import { useReactToPrint } from "react-to-print";
import axios from "axios";
import { useSelector } from "react-redux";
import CustomModal from "../../../components/CustomModal";
import { useLOVValues } from "../../../hooks/useLOVValues";

const Invoice = () => {
const printRef = useRef();
  const handlePrint = useReactToPrint({
    content: () => printRef.current,
  });

  const [modalOpen, setModalOpen] = useState(false);
  const [submitBtnLoading, setSubmitBtnLoading] = useState(false);
  const [grnIds, setGrnIds] = useState([]);
  const [selectedPoId, setSelectedPoId] = useState(""); // Purchase Order ID
  const [selectedSoId, setSelectedSoId] = useState("");
  const [selectedGrnId, setSelectedGrnId] = useState("");
  const [formData, setFormData] = useState({
    grnNo: "",
    materialDtlList: [],
    tdsDtlList: [],
    deductionDtlList: [],
    grnType: "GRN",
    processId: "" 
    
  });
  // const [formData, setFormData] = useState({
  //   grnNo: "",
  //   materialDtlList: [],
  //   grnType: "GRN",
  //   processId: "" 
    
  // });
  const [poOptions, setPoOptions] = useState([]);
  const [soOptions, setSoOptions] = useState([]);
    const [searchDone, setSearchDone] = useState(false);
  const [cpOptions, setCpOptions] = useState([]);
  const userId = useSelector(state => state.auth.userId);

   const { lovValues: tdsSectionLOV } = useLOVValues(12, 'tdsSection');
  const tdsSectionDropdown = tdsSectionLOV
    .filter(item => item.isActive === true)
    .map(item => ({ label: item.lovDisplayValue, value: item.lovValue }));
 
  useEffect(() => {
 /* const fetchPoIds = async () => {
    try {
      const { data } = await axios.get("/api/process-controller/approvedGrnPoIds");
      const ids = data?.responseData || [];

     
     // const options = ids.map(id => ({ value: id, label: id }));
     const options = ids.map(id => ({ value: id, label: `PO${id}` }));
      setPoOptions(options);
    } catch (error) {
      message.error("Failed to fetch Purchase Order IDs");
    }
  };*/
  const fetchPoIds = async () => {
  try {
    const { data } = await axios.get("/api/process-controller/approvedGrnPoIds");
    const poList = data?.responseData || [];

   const options = poList.map(item => ({
  value: item.poId,
  label: item.poId,
  searchText: (
    item.poId +
    " " +
    item.vendorName +
    " " +
    (item.projectName || "") +
    " " +
    item.createdDate +
    " " +
    item.materialDescriptions.join(" ")
  ).toLowerCase()
}));


    setPoOptions(options);

  } catch (error) {
    message.error("Failed to fetch Purchase Order IDs");
  }
};

  fetchPoIds();
}, []);
 useEffect(() => {
  const fetchSoIds = async () => {
    try {
      const { data } = await axios.get("/api/process-controller/approvedSoIds");
      const ids = data?.responseData || [];

     
      const options = ids.map(id => ({ value: id, label: id }));
      setSoOptions(options);
    } catch (error) {
      message.error("Failed to fetch Purchase Order IDs");
    }
  };

  fetchSoIds();
}, []);
 useEffect(() => {
  const fetchCpIds = async () => {
    try {
      const { data } = await axios.get("/api/process-controller/approvedCpIds");
      const ids = data?.responseData || [];
      const options = ids.map(id => ({ value: id, label: id }));
      setCpOptions(options);
    } catch (error) {
      message.error("Failed to fetch Contingency Purchase IDs");
    }
  };
  fetchCpIds();
}, []);
  useEffect(() => {
  const draft = localStorage.getItem("grnDraft");
  if (draft) {
    setFormData(JSON.parse(draft));
    message.success("Form loaded from draft.");
  } else {
    // setFormData(prev => ({
    //   ...prev,
    //   poDtlList: [{}],         
    //   vendorDtlList: [{}],     
    //   materialDtlList: [{}]    
    // }));
    setFormData(prev => ({
      ...prev,
      poDtlList: [{}],         
      vendorDtlList: [{}],     
      materialDtlList: [{}],
      tdsDtlList: [{}],
      deductionDtlList: [{}]
    }));
  }
}, []);
const fetchGrnIds = async (poId) => {
  try {
    const response = await axios.get(`/api/process-controller/paymentVoucherGrnId?grnProcessId=${poId}`);
     const ids = response.data?.responseData || [];
    
    
    setGrnIds(ids.map(id => ({ value: id, label: id })));
  } catch (err) {
    console.error("Error fetching GRN IDs", err);
  }
};
useEffect(() => {
  if (selectedPoId) {
    fetchGrnIds(selectedPoId);
  }
}, [selectedPoId]);
const fetchServiceOrderData = async (soId) => {
  try {
    const { data } = await axios.get(`/api/process-controller/paymentVoucherSOData?processNo=${soId}`);
    const res = data?.responseData;

    if (res) {
      setFormData(prev => ({
        ...prev,
        vendorName: res.vendorName,
        vendorInvoiceNumber: res.vendorInvoiceName,
        vendorInvoiceDate: res.vendorInvoiceDate,
        currency: res.materialsList?.[0]?.currency || "INR",
        exchangeRate: res.materialsList?.[0]?.exchangeRate || 0,
        totalAmount: res.totalAmount,
        paymentVoucherType: res.paymentVoucherType,
        partialAmount: res.partialAmountAlreadypaid || null,
        partialBalanceAmount: res.partialBalanceAmount || null,
        // Modifeied by Aman
        // advanceAmount: res.advanceAmountAlreadyPaid || null,
        advanceAmountpaid: res.advanceAmountAlreadyPaid || null,
        // End
        advanceBalanceAmount: res.advanceBalanceAmount || null,
        materialDtlList: res.materialsList?.map(mat => ({
          materialCode: mat.materialCode,
          materialDescription: mat.materialDescription,
          quantity: mat.quantity,
          rate: mat.unitPrice,
          currency: mat.currency,
          exchangeRate: mat.exchangeRate,
          gst: mat.gst,
          amount: mat.amount,
        })) || []
      }));
    }
  } catch (error) {
    message.error("Failed to fetch Service Order data");
    console.error(error);
  }
};

const fetchCpData = async (cpId) => {
  try {
    const { data } = await axios.get(`/api/process-controller/paymentVoucherCPData?processNo=${cpId}`);
    const res = data?.responseData;

    if (res) {
      setFormData(prev => ({
        ...prev,
        vendorName: res.vendorName,
        vendorInvoiceNumber: res.vendorInvoiceName,
        vendorInvoiceDate: res.vendorInvoiceDate,
        currency: res.materialsList?.[0]?.currency || "INR",
        exchangeRate: res.materialsList?.[0]?.exchangeRate || 0,
        totalAmount: res.totalAmount,
        paymentVoucherType: res.paymentVoucherType,
        partialAmount: res.partialAmountAlreadypaid || null,
        partialBalanceAmount: res.partialBalanceAmount || null,
        advanceAmountpaid: res.advanceAmountAlreadyPaid || null,
        advanceBalanceAmount: res.advanceBalanceAmount || null,
        materialDtlList: res.materialsList?.map(mat => ({
          materialCode: mat.materialCode,
          materialDescription: mat.materialDescription,
          quantity: mat.quantity,
          rate: mat.unitPrice,
          currency: mat.currency,
          exchangeRate: mat.exchangeRate,
          gst: mat.gst,
          amount: mat.amount,
        })) || []
      }));
    }
  } catch (error) {
    message.error("Failed to fetch Contingency Purchase data");
    console.error(error);
  }
};

const fetchPoData = async (poId) => {
  try {
    const { data } = await axios.get(`/api/purchase-orders/byId`, { params: { poId } });
    const res = data?.responseData;
    if (res) {
      const firstAttr = res.purchaseOrderAttributes?.[0];
      const { data: advData } = await axios.get(
        `/api/process-controller/paymentVoucherPOAdvanceStatus`,
        { params: { poId } }
      );
      const advPaid = advData?.responseData?.advanceAmountAlreadyPaid || 0;

      setFormData(prev => ({
        ...prev,
        vendorName: res.vendorName,
        currency: firstAttr?.currency || "INR",
        exchangeRate: firstAttr?.exchangeRate || 0,
        totalAmount: res.totalValueOfPo,
        advanceAmountpaid: advPaid,
        advanceBalanceAmount: (res.totalValueOfPo || 0) - advPaid,
        materialDtlList: res.purchaseOrderAttributes?.map(attr => ({
          materialCode: attr.materialCode,
          materialDescription: attr.materialDescription,
          quantity: attr.quantity,
          rate: attr.rate,
          currency: attr.currency,
          exchangeRate: attr.exchangeRate,
          gst: attr.gst,
          amount: (attr.quantity || 0) * (attr.rate || 0),
        })) || []
      }));
    }
  } catch (error) {
    message.error("Failed to fetch Purchase Order data");
    console.error(error);
  }
};

const fetchSoAdvanceData = async (soId) => {
  try {
    const { data } = await axios.get(`/api/service-orders/byId`, { params: { soId } });
    const res = data?.responseData;
    if (res) {
      const firstMat = res.materials?.[0];
      const { data: advData } = await axios.get(
        `/api/process-controller/paymentVoucherSOAdvanceStatus`,
        { params: { soId } }
      );
      const advPaid = advData?.responseData?.advanceAmountAlreadyPaid || 0;

      setFormData(prev => ({
        ...prev,
        vendorName: res.vendorName,
        currency: firstMat?.currency || "INR",
        exchangeRate: firstMat?.exchangeRate || 0,
        totalAmount: res.totalValueOfSo,
        advanceAmountpaid: advPaid,
        advanceBalanceAmount: (res.totalValueOfSo || 0) - advPaid,
        materialDtlList: res.materials?.map(mat => ({
          materialCode: mat.materialCode,
          materialDescription: mat.materialDescription,
          quantity: mat.quantity,
          rate: mat.rate,
          currency: mat.currency,
          exchangeRate: mat.exchangeRate,
          gst: mat.gst,
          amount: (mat.quantity || 0) * (mat.rate || 0),
        })) || []
      }));
    }
  } catch (error) {
    message.error("Failed to fetch Service Order data");
    console.error(error);
  }
};

const fetchPaymentVoucherData = async (grnNumber) => {
  try {
    const { data } = await axios.get(`/api/process-controller/paymentVoucherData?processNo=${grnNumber}`);
    const res = data?.responseData;

    if (res) {
      setFormData(prev => ({
        ...prev,
        vendorName: res.vendorName,
        vendorInvoiceNumber: res.vendorInvoiceName,
        vendorInvoiceDate: res.vendorInvoiceDate,
        currency: res.materialsList?.[0]?.currency || "INR",
        exchangeRate: res.materialsList?.[0]?.exchangeRate || 0,
        totalAmount: res.totalAmount,
        paymentVoucherType: res.paymentVoucherType,
        partialAmount: res.partialAmountAlreadypaid || null,
        partialBalanceAmount: res.partialBalanceAmount || null,
        // advanceAmount: res.advanceAmountAlreadyPaid || null,
        // Modifeied by Aman
        advanceAmountpaid: res.advanceAmountAlreadyPaid || null,
        // End
        advanceBalanceAmount: res.advanceBalanceAmount || null,
        materialDtlList: res.materialsList?.map(mat => ({
          materialCode: mat.materialCode,
          materialDescription: mat.materialDescription,
          quantity: mat.quantity,
          rate: mat.unitPrice,
          currency: mat.currency,
          exchangeRate: mat.exchangeRate,
          gst: mat.gst,
          amount: mat.amount,
        })) || []
      }));
    
    }
  } catch (error) {
    message.error("Failed to fetch Payment Voucher Data");
    console.error(error);
  }
};

const handleSearchVoucher = async () => {
  const value = formData.searchValue;
  if (!value) {
    message.warning("Enter a Payment Voucher Number to search.");
    return;
  }
  try {
    const { data } = await axios.get(`/api/process-controller/VoucherData`, { params: { processNo: value } });
    const res = data?.responseData;
    if (res) {
      setFormData(prev => ({
        ...prev,
        ...res,
        purchaseOrderids: res.purchaseOrderId || "",
        ServiceOrderDetails: res.serviceOrderDetails || "",
        materialDtlList: res.materials?.map(mat => ({
          materialCode: mat.materialCode,
          materialDescription: mat.materialDescription,
          quantity: mat.quantity,
          rate: mat.unitPrice,
          currency: mat.currency,
          exchangeRate: mat.exchangeRate,
          gst: mat.gst,
        })) || [],
        tdsDtlList: res.tdsList?.map(t => ({
          tdsSection: t.tdsSection,
          tdsAmount: t.tdsAmount,
          remarks: t.remarks,
        })) || [],
        deductionDtlList: res.deductions?.map(d => ({
          deductionName: d.deductionName,
          deductionAmount: d.deductionAmount,
          remarks: d.remarks,
        })) || [],
      }));
      setSearchDone(true);
    } else {
      message.warning("No payment voucher found for that number.");
    }
  } catch (error) {
    message.error("Failed to fetch payment voucher.");
    console.error(error);
  }
};

useEffect(() => {
  if (selectedGrnId) {
    fetchPaymentVoucherData(selectedGrnId);
  }
}, [selectedGrnId]);
const recomputeNetAmount = (data) => {
  const tdsTotal = (data.tdsDtlList || []).reduce((sum, row) => sum + parseFloat(row.tdsAmount || 0), 0);
  const deductionTotal = (data.deductionDtlList || []).reduce((sum, row) => sum + parseFloat(row.deductionAmount || 0), 0);
  let baseAmount = 0;
  if (data.paymentVoucherType === "Partial") {
    baseAmount = parseFloat(data.partialAmount || 0);
  } else if (data.paymentVoucherType === "Advance") {
    baseAmount = parseFloat(data.advanceAmount || 0);
  } else {
    baseAmount = parseFloat(data.totalAmount || 0);
  }
  return baseAmount - tdsTotal - deductionTotal;
};

  const handleChange = (fieldName, value) => {
    if (typeof fieldName === "string") {
     // setFormData(prev => ({ ...prev, [fieldName]: value }));
    //  setFormData(prev => {
    //   let updated = { ...prev, [fieldName]: value };

     
    //   const tds = parseFloat(updated.tdsAmount || 0);
    //   let baseAmount = 0;

    //   if (updated.paymentVoucherType === "Partial") {
    //     baseAmount = parseFloat(updated.partialAmount || 0);
    //   } else if (updated.paymentVoucherType === "Advance") {
    //     baseAmount = parseFloat(updated.advanceAmount || 0);
    //   } else {
    //     baseAmount = parseFloat(updated.totalAmount || 0);
    //   }

    //   updated.paymentVoucherNetAmount = baseAmount - tds;
      

    //   return updated;
    // });
    // setFormData(prev => {
    //   let updated = { ...prev, [fieldName]: value };

     
    //   const tdsTotal = (updated.tdsDtlList || []).reduce(
    //     (sum, row) => sum + parseFloat(row.tdsAmount || 0), 0
    //   );
    //   const deductionTotal = (updated.deductionDtlList || []).reduce(
    //     (sum, row) => sum + parseFloat(row.deductionAmount || 0), 0
    //   );
    //   let baseAmount = 0;

    //   if (updated.paymentVoucherType === "Partial") {
    //     baseAmount = parseFloat(updated.partialAmount || 0);
    //   } else if (updated.paymentVoucherType === "Advance") {
    //     baseAmount = parseFloat(updated.advanceAmount || 0);
    //   } else {
    //     baseAmount = parseFloat(updated.totalAmount || 0);
    //   }

    //   updated.paymentVoucherNetAmount = baseAmount - tdsTotal - deductionTotal;
      

    //   return updated;
    // });
      setFormData(prev => {
      let updated = { ...prev, [fieldName]: value };
      updated.paymentVoucherNetAmount = recomputeNetAmount(updated);
      return updated;
    });
  //      if (fieldName === "purchaseOrderids") {
  //     setSelectedPoId(value);
  //   }
  //   if (fieldName === "grnNumber") {
  //   setSelectedGrnId(value); // triggers useEffect to call API
  // }
  //  if (fieldName === "ServiceOrderDetails") {
  //     setSelectedSoId(value);
  //     fetchServiceOrderData(value);
  //   }
  //   if (fieldName === "cpDetails") {
  //     fetchCpData(value);
  //   }
  //  if (fieldName === "paymentVoucherType") {
  //     if (value !== "Partial") {
  //       setFormData(prev => ({ ...prev, partialAmount: null, partialBalanceAmount: null }));
  //     }
  //     if (value !== "Advance") {
  //       setFormData(prev => ({ ...prev, advanceAmount: null, advanceBalanceAmount: null }));
  //     }
  //   }
  if (fieldName === "purchaseOrderids") {
      setSelectedPoId(value);
      if (formData.paymentVoucherType === "Advance") {
        fetchPoData(value);
      }
    }
    if (fieldName === "grnNumber") {
    setSelectedGrnId(value); // triggers useEffect to call API
  }
   if (fieldName === "ServiceOrderDetails") {
      setSelectedSoId(value);
      if (formData.paymentVoucherType === "Advance") {
        fetchSoAdvanceData(value);
      } else {
        fetchServiceOrderData(value);
      }
    }
    if (fieldName === "cpDetails") {
      fetchCpData(value);
    }
   if (fieldName === "paymentVoucherType") {
      if (value !== "Partial") {
        setFormData(prev => ({ ...prev, partialAmount: null, partialBalanceAmount: null }));
      }
      if (value !== "Advance") {
        setFormData(prev => ({ ...prev, advanceAmount: null, advanceBalanceAmount: null }));
      }
      if (value === "Advance" && formData.paymentVoucherIsFor === "Purchase Order" && selectedPoId) {
        fetchPoData(selectedPoId);
      }
      if (value === "Advance" && formData.paymentVoucherIsFor === "Service Order" && selectedSoId) {
        fetchSoAdvanceData(selectedSoId);
      }
    }
      } else {
      const [listName, idx, key] = fieldName;
      setFormData(prev => {
        const prevList = [...(prev[listName] || [])];
        prevList[idx] = { ...prevList[idx] };
        prevList[idx][key] = value;
        return { ...prev, [listName]: prevList };
      });
    }
    // } else {
    //   setFormData(prev => {
    //     const prevMaterialDtlList = [...prev.materialDtlList];
    //     prevMaterialDtlList[fieldName[1]] = { ...prevMaterialDtlList[fieldName[1]] };
    //     prevMaterialDtlList[fieldName[1]][fieldName[2]] = value;
    //     return { ...prev, materialDtlList: prevMaterialDtlList };
    //   });
    // }
  };
/*
  const handleSearch = async () => {
    try {
      const processStage = "GRN";
      const processNo = formData.grnNo;

      if (!processNo) {
        message.warning("Please enter a valid GRN number.");
        return;
      }

      const { data } = await axios.get(
        `/api/process-controller/getSubProcessDtls?processStage=${processStage}&processNo=${processNo}`
      );

      const grnData = data?.responseData?.grnDtls;

      if (!grnData) {
        message.error("No GRN data found.");
        return;
      }

      setFormData({
        ...data?.responseData,
        giNo: grnData?.giNo || "",
        grnType: "GRN",
        grnNo: grnData?.grnNo,
        grnDate: grnData?.grnDate,
        installationDate: grnData?.installationDate,
        commissioningDate: grnData?.commissioningDate,
        indentorName: grnData?.createdBy,
        materialDtlList: grnData?.materialDtlList?.map(material => ({
          ...material,
          acceptedQuantity: material.quantity || 0,
          locatorId: material.locatorId || 0,
          depriciationRate: material.depriciationRate || 0,
          bookValue: material.bookValue || 0,
        })) || []
      });
    } catch (error) {
      message.error(
        error?.response?.data?.responseStatus?.message || "Error fetching GRN data."
      );
    }
  };*/
   const handleSearch = async (value) => {
    try {
      const { data } = await axios.get(
        `/api/purchase-orders/byId`,{params:{poId: value ? value : formData.poId}}
      );
      const responseData = data?.responseData || {};

      setFormData({
        ...responseData,
        materialDtlList: responseData?.purchaseOrderAttributes || [],
        poDtlList: responseData?.purchaseOrderDetails || [],
        vendorDtlList: responseData?.vendorDetails || [],
      });
    } catch (error) {
      ;
      message.error(
        error?.response?.data?.responseStatus?.message || "Error fetching data."
      );
    }
  };


  const { locationId } = useSelector(state => state.auth);
const onFinish = async () => {
  try {
    const total = parseFloat(formData.totalAmount || 0);
    const partial = parseFloat(formData.partialAmount || 0);
    const advance = parseFloat(formData.advanceAmount || 0);

    
    if (partial > total) {
      message.error("Partial amount cannot exceed Total amount.");
      return;
    }
    if (advance > total) {
      message.error("Advance amount cannot exceed Total amount.");
      return;
    }
    setSubmitBtnLoading(true);

    // Prepare DTO in the same structure as backend expects
    const payload = {
    
      paymentVoucherDate: formData.paymentVoucherDate,
      paymentVoucherIsFor: formData.paymentVoucherIsFor,
      purchaseOrderId: formData.purchaseOrderids || "",
      grnNumber: formData.grnNumber || "",
      serviceOrderDetails: formData.ServiceOrderDetails || "",
      paymentVoucherType: formData.paymentVoucherType,
      vendorName: formData.vendorName,
      vendorInvoiceNumber: formData.vendorInvoiceNumber,
      vendorInvoiceDate: formData.vendorInvoiceDate,
      currency: formData.currency,
      exchangeRate: formData.exchangeRate,
      status: formData.status,
      remarks: formData.remarks,
      partialAmount : formData.partialAmount,
      totalAmount: formData.totalAmount,
      advanceAmount: formData.advanceAmount,
      serviceOrderDetails: formData.ServiceOrderDetails,
      cpDetails: formData.cpDetails || "",
      createdBy: userId,
      paymentVoucherNetAmount: formData.paymentVoucherNetAmount,
      tdsList: formData.tdsDtlList?.map(t => ({
        tdsSection: t.tdsSection,
        tdsAmount: t.tdsAmount,
        remarks: t.remarks
      })) || [],
      deductions: formData.deductionDtlList?.map(d => ({
        deductionName: d.deductionName,
        deductionAmount: d.deductionAmount,
        remarks: d.remarks
      })) || [],
      materials: formData.materialDtlList?.map(mat => ({
        materialCode: mat.materialCode,
        materialDescription: mat.materialDescription,
        quantity: mat.quantity,
        unitPrice: mat.rate,
        currency: mat.currency,
        exchangeRate: mat.exchangeRate,
        gst: mat.gst
      })) || []
    };
    // const payload = {
    
    //   paymentVoucherDate: formData.paymentVoucherDate,
    //   paymentVoucherIsFor: formData.paymentVoucherIsFor,
    //   purchaseOrderId: formData.purchaseOrderids || "",
    //   grnNumber: formData.grnNumber || "",
    //   serviceOrderDetails: formData.ServiceOrderDetails || "",
    //   paymentVoucherType: formData.paymentVoucherType,
    //   vendorName: formData.vendorName,
    //   vendorInvoiceNumber: formData.vendorInvoiceNumber,
    //   vendorInvoiceDate: formData.vendorInvoiceDate,
    //   currency: formData.currency,
    //   exchangeRate: formData.exchangeRate,
    //   status: formData.status,
    //   remarks: formData.remarks,
    //   partialAmount : formData.partialAmount,
    //   totalAmount: formData.totalAmount,
    //   advanceAmount: formData.advanceAmount,
    //   serviceOrderDetails: formData.ServiceOrderDetails,
    //   cpDetails: formData.cpDetails || "",
    //   createdBy: userId,
    //   tdsAmount: formData.tdsAmount,
    //   paymentVoucherNetAmount: formData.paymentVoucherNetAmount,
    //   materials: formData.materialDtlList?.map(mat => ({
    //     materialCode: mat.materialCode,
    //     materialDescription: mat.materialDescription,
    //     quantity: mat.quantity,
    //     unitPrice: mat.rate,
    //     currency: mat.currency,
    //     exchangeRate: mat.exchangeRate,
    //     gst: mat.gst
    //   })) || []
    // };

    // Call backend API
    const { data } = await axios.post("/api/process-controller/savePaymentVoucher", payload);

    message.success("Payment Voucher saved successfully!");
    console.log("Saved Data:", data);

    // Optionally clear form or show modal
    setFormData(prev => ({
      ...prev,
      processId: data?.responseData?.processNo || prev.processId,
      paymentVoucherNumber: data?.responseData?.paymentVoucherNumber || prev.paymentVoucherNumber
    }));

    setModalOpen(true);

  } catch (error) {
    message.error(error?.response?.data?.responseStatus?.message || "Failed to save Payment Voucher.");
    console.error("Save Error:", error);
  } finally {
    setSubmitBtnLoading(false);
  }
};

  useEffect(() => {
    const draft = localStorage.getItem("grnDraft");
    if (draft) {
      setFormData(JSON.parse(draft));
      message.success("Form loaded from draft.");
    }
  }, []);
  console.log(grnIds);
  return (
    <Card className="a4-container" ref={printRef}>
      <Heading title="Invoice" />
      <CustomForm formData={formData} onFinish={onFinish}>
        
        
        {/* {renderFormFields(invoiceFields(formData, poOptions, grnIds, setSelectedPoId, soOptions, cpOptions), handleChange, formData, "", null, setFormData, handleSearch)} */}
        {renderFormFields(invoiceFields(formData, poOptions, grnIds, setSelectedPoId, soOptions, cpOptions, setFormData, tdsSectionDropdown, recomputeNetAmount, handleSearchVoucher, searchDone), handleChange, formData, "", null, setFormData, handleSearch)}

        
        {/* {renderFormFields(grvFields, handleChange, formData, "", null, setFormData, handleSearch)} */}
        <ButtonContainer
          onFinish={onFinish}
          formData={formData}
          draftDataName="grnDraft"
          submitBtnLoading={submitBtnLoading}
          submitBtnEnabled
          printBtnEnabled
          draftBtnEnabled
          handlePrint={handlePrint}
        />
      </CustomForm>
      <CustomModal isOpen={modalOpen} setIsOpen={setModalOpen} title="Invoice" processNo={formData?.processId} />
    </Card>
  );
};
export default Invoice;
