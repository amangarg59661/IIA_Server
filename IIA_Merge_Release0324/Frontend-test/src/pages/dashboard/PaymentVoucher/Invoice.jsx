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
    grnType: "GRN",
    processId: "" 
    
  });
  const [poOptions, setPoOptions] = useState([]);
  const [soOptions, setSoOptions] = useState([]);
  const userId = useSelector(state => state.auth.userId);
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
  const draft = localStorage.getItem("grnDraft");
  if (draft) {
    setFormData(JSON.parse(draft));
    message.success("Form loaded from draft.");
  } else {
    setFormData(prev => ({
      ...prev,
      poDtlList: [{}],         
      vendorDtlList: [{}],     
      materialDtlList: [{}]    
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

useEffect(() => {
  if (selectedGrnId) {
    fetchPaymentVoucherData(selectedGrnId);
  }
}, [selectedGrnId]);


  const handleChange = (fieldName, value) => {
    if (typeof fieldName === "string") {
     // setFormData(prev => ({ ...prev, [fieldName]: value }));
     setFormData(prev => {
      let updated = { ...prev, [fieldName]: value };

     
      const tds = parseFloat(updated.tdsAmount || 0);
      let baseAmount = 0;

      if (updated.paymentVoucherType === "Partial") {
        baseAmount = parseFloat(updated.partialAmount || 0);
      } else if (updated.paymentVoucherType === "Advance") {
        baseAmount = parseFloat(updated.advanceAmount || 0);
      } else {
        baseAmount = parseFloat(updated.totalAmount || 0);
      }

      updated.paymentVoucherNetAmount = baseAmount - tds;
      

      return updated;
    });
       if (fieldName === "purchaseOrderids") {
      setSelectedPoId(value);
    }
    if (fieldName === "grnNumber") {
    setSelectedGrnId(value); // triggers useEffect to call API
  }
   if (fieldName === "ServiceOrderDetails") {
      setSelectedSoId(value);
      fetchServiceOrderData(value); 
    }
   if (fieldName === "paymentVoucherType") {
      if (value !== "Partial") {
        setFormData(prev => ({ ...prev, partialAmount: null, partialBalanceAmount: null }));
      }
      if (value !== "Advance") {
        setFormData(prev => ({ ...prev, advanceAmount: null, advanceBalanceAmount: null }));
      }
    }
    } else {
      setFormData(prev => {
        const prevMaterialDtlList = [...prev.materialDtlList];
        prevMaterialDtlList[fieldName[1]] = { ...prevMaterialDtlList[fieldName[1]] };
        prevMaterialDtlList[fieldName[1]][fieldName[2]] = value;
        return { ...prev, materialDtlList: prevMaterialDtlList };
      });
    }
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
      createdBy: userId,
      tdsAmount: formData.tdsAmount,
      paymentVoucherNetAmount: formData.paymentVoucherNetAmount,
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
        
        
        {renderFormFields(invoiceFields(formData, poOptions, grnIds, setSelectedPoId, soOptions), handleChange, formData, "", null, setFormData, handleSearch)}

        
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
