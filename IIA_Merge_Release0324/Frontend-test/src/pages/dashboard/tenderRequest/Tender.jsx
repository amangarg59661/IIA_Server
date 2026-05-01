import React, { useRef, useState, useEffect } from "react";
import { Button, Card, Form, Input, Select, DatePicker, message, Modal, Tag } from "antd";
import { SearchOutlined, PrinterOutlined } from "@ant-design/icons";
import { useReactToPrint } from "react-to-print";
import axios from "axios";
import { useSelector } from "react-redux";
import Heading from "../../../components/DKG_Heading";
import CustomForm from "../../../components/DKG_CustomForm";
import { renderFormFields } from "../../../utils/CommonFunctions";
import ButtonContainer from "../../../components/ButtonContainer";
import CustomModal from "../../../components/CustomModal";
import { TenderDetails } from "./InputFields";
import { multiply } from "lodash";
import { useLocation } from "react-router-dom";
import TenderPrintFormat from "../../../utils/TenderPrintFormat";
import { useLOVValues } from "../../../hooks/useLOVValues";

const { Option } = Select;
const incoOptionsDefault = [
  { label: "DAP", value: "DAP" },
  { label: "EXWORKS", value: "EXWORKS" },
  { label: "DDP", value: "DDP" },
  { label: "FCA", value: "FCA" },
  { label: "FOB", value: "FOB" },
  { label: "CIF", value: "CIF" },
  { label: "CIP", value: "CIP" },
  { label: "DPU", value: "DPU" },
  { label: "FAS", value: "FAS" },
  { label: "CFR", value: "CFR" },
  { label: "FOR", value: "FOR" },
  { label: "CPT", value: "CPT" },
  { label: "NA", value: "NA" }
];


const Tender = () => {
  const printRef = useRef();
  const [form] = Form.useForm();

  const [submitBtnLoading, setSubmitBtnLoading] = useState(false);
  const [generatedTenderId, setGeneratedTenderId] = useState("");

  // ✅ Fetch dropdown values from LOV system (Form ID: 9 - TenderRequest)
  const { lovValues: incoTermsLOV, loading: loadingIncoTerms } = useLOVValues(9, 'incoTerms');
  const { lovValues: paymentTermsLOV, loading: loadingPaymentTerms } = useLOVValues(9, 'paymentTerms');
  const [isPrintEnabled, setIsPrintEnabled] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);
  const [searchTenderId, setSearchTenderId] = useState("");
  const [usedIndentIds, setUsedIndentIds] = useState(new Set());
  const [consigneeOptions, setConsigneeOptions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [approvedIndents, setApprovedIndents] = useState([]);
  const [materialOptions, setMaterialOptions] = useState([]);
  const [materialDescOptions, setMaterialDescOptions] = useState([]);
  const [selectedProjectName, setSelectedProjectName] = useState(null);
  const [tenderIdOptions,setTenderIdOptions] = useState([]);
  const [searchDone, setSearchDone] = useState(false);
  const [versionHistoryOpen, setVersionHistoryOpen] = useState(false);
const [versionHistoryList, setVersionHistoryList] = useState([]);
const [selectedVersionIdx, setSelectedVersionIdx] = useState(0);

  const [buyBackFields, setBuyBackFields] = useState([]);

  const { userName, email, mobileNumber, token, userId } = useSelector(
    (state) => state.auth
  );

   const location = useLocation();
        const { tenderId, indentIds } = location.state || {};
    
        console.log("Tender ID:", tenderId); 
  //const [formData, setFormData] = useState({});
  const [formData, setFormData] = useState({
    indentId: [],  
    materialDetails: [],
    billingAddress: "Koramangala, 2nd Block, Bangalore -560034",
    buyBack: false,

  });
  useEffect(() => {
  const fetchTenderIds = async () => {
    try {
      const res = await axios.get("/getApprovedTenderIdForPOAndSO");
      const tenderList = res.data?.responseData || [];

      // Format for dropdown
      const tenderOptions = tenderList.map((id) => ({
        label: id,
        value: id,
      }));

      // Store in state and use in TenderId field options
      setTenderIdOptions(tenderOptions);
    } catch (err) {
      console.error("Failed to fetch tender IDs", err);
    }
  };

  fetchTenderIds();
}, []);

  
  
  useEffect(() => {
  const fetchAllData = async () => {
    setLoading(true);
    try {
      // Fetch approved indents first
      // updated by abhinav
      const approvedResponse = await axios.get(
        `/approved-indents?userId=${userId}`
      );
      ;

      // Fetch locations
      const locationsResponse = await axios.get("/api/location-master");
      setConsigneeOptions(
        (locationsResponse.data.responseData || []).map((location) => ({
          value: location.locationCode,
          label: location.locationName,
        }))
      );

      // Set approved indents as options for the dropdown
      const approvedIds = approvedResponse.data?.responseData || [];
      setApprovedIndents(
        approvedIds.map((indent) => ({
          label: `${indent.indentId} - ${indent.projectName || ""}`,
          value: indent.indentId,
          projectName: indent.projectName,
          indentorName: indent.indentorName || "",
          createdDate: indent.createdDate || "",
          materialDes: indent.materialDes || [],
        }))
      );



    } catch (error) {
      message.error("Failed to load dropdown data");
      console.error("Dropdown fetch error:", error);
    } finally {
      setLoading(false);
    }
  };

  fetchAllData();
}, []);



  // Custom filter function for indent search across all fields
  const filterIndentOption = (input, option) => {
    if (!input) return true;

    const searchTerm = input.toLowerCase();
    // In Ant Design Select, the option object contains the properties directly
    const optionData = option || {};

    // Search in Indent ID (stored in 'value' property)
    if (optionData.value && String(optionData.value).toLowerCase().includes(searchTerm)) {
      return true;
    }

    // Search in Project Name
    if (optionData.projectName && String(optionData.projectName).toLowerCase().includes(searchTerm)) {
      return true;
    }

    // Search in Indentor Name
    if (optionData.indentorName && String(optionData.indentorName).toLowerCase().includes(searchTerm)) {
      return true;
    }

    // Search in Created Date
    if (optionData.createdDate && String(optionData.createdDate).toLowerCase().includes(searchTerm)) {
      return true;
    }

    // Search in Material Description array
    if (Array.isArray(optionData.materialDes)) {
      return optionData.materialDes.some(material =>
        String(material).toLowerCase().includes(searchTerm)
      );
    }

    return false;
  };

  // Now, your indentOptions will use the updated project names:
  const indentOptions = approvedIndents.map((indent) => ({
    value: indent.indentId,
    label: `Indent ${indent.indentId} (${indent.projectName})`,
  }));


  ;
  
  const formatMaterial = (material) => ({
    materialCode: material.materialCode,
    materialDescription: material.materialDescription,
    quantity: material.quantity,
    unitPrice: material.unitPrice,
    uom: material.uom,
    budgetCode: material.budgetCode,
    totalPrice: material.totalPrice,
    materialCategory: material.materialCategory,
   currency: material.currency,
   conversionRate: material.conversionRate,
    materialSubCategory: material.materialSubCategory,
    modeOfProcurement: material.modeOfProcurement,
    vendorNames: material.vendorNames,
  });
 
 
  const handleIndentSearch = async (indentIds) => {
  try {
    const allMaterials = [];
    const allJobs = [];
    let isBuyBack = false;
    let buyBackData = {};
    let indentType = null;

    for (const id of indentIds) {
      const res = await axios.get(`/api/indents/IndentDataForTender`, { params: { indentId: id } });
      const indentData = res.data?.responseData;

      // Track indent type (material vs job)
      if (indentData?.indentType) indentType = indentData.indentType;

      if (indentData?.materialDetails?.length) {
        allMaterials.push(...indentData.materialDetails);
      }

      if (indentData?.jobDetails?.length) {
        allJobs.push(...indentData.jobDetails);
      }

      if (indentData?.buyBack) {
        isBuyBack = true;
        buyBackData = {
          uploadBuyBackFileNames: indentData.uploadBuyBackFile || [],
          modelNumber: indentData.modelNumber || "",
          serialNumber: indentData.serialNumber || "",
          dateOfPurchase: indentData.dateOfPurchase || null,
          buyBackAmount: indentData.buyBackAmount || "",
        };
      }
    }

    const formattedMaterials = allMaterials.map((material) => ({
      materialCode: material.materialCode,
      materialDescription: material.materialDescription,
      uom: material.uom,
      quantity: material.quantity,
      unitPrice: material.unitPrice,
      currency: material.currency,
      conversionRate: material.conversionRate,
      materialCategory: material.materialCategory,
      materialSubCategory: material.materialSubCategory,
      budgetCode: material.budgetCode,
      totalPrice: material.totalPrice,
      modeOfProcurement: material.modeOfProcurement,
      vendorNames: material.vendorNames,
      origin: material.origin,           // ← was missing
      briefDescription: material.briefDescription, // ← was missing
    }));

    const formattedJobs = allJobs.map((job) => ({
      jobCode: job.jobCode,
      jobDescription: job.jobDescription,
      category: job.category,
      subCategory: job.subCategory,
      uom: job.uom,
      quantity: job.quantity,
      estimatedPrice: job.estimatedPrice,
      totalPrice: job.totalPrice,
      currency: job.currency,
      briefDescription: job.briefDescription,
      origin: job.origin,
      modeOfProcurement: job.modeOfProcurement,
      budgetCode: job.budgetCode,
      vendorNames: job.vendorNames,
    }));

    setFormData((prev) => ({
      ...prev,
      indentType,
      materialDetails: formattedMaterials,
      jobDetails: formattedJobs,
      buyBack: isBuyBack,
      ...buyBackData,
    }));

    form.setFieldsValue({
      materialDetails: formattedMaterials,
      jobDetails: formattedJobs,
      buyBack: isBuyBack,
      ...buyBackData,
    });

  } catch (err) {
    console.error("Failed to fetch indent data", err);
    message.error("Failed to load indent details");
  }
};
  // const handleIndentSearch = async (indentIds) => {
  //   try {
  //     const allMaterials = [];
  //     let isBuyBack = false; 
  //     let buyBackData = {};
  //     console.log("test4");
  //     // Loop through all selected indent IDs
  //     for (const id of indentIds) {
  //       const res = await axios.get(`/api/indents/IndentDataForTender`,{params: {indentId :id}});
  //       const indentData = res.data?.responseData;
   
  //       if (indentData?.materialDetails) {
  //         allMaterials.push(...indentData.materialDetails);
          
  //       }
  //        if (indentData?.buyBack) {
  //       isBuyBack = true;
  //        buyBackData = {
  //         uploadBuyBackFileNames: indentData.uploadBuyBackFile || [],
  //         modelNumber: indentData.modelNumber || "",
  //         serialNumber: indentData.serialNumber || "",
  //         dateOfPurchase: indentData.dateOfPurchase || null,
  //         buyBackAmount: indentData.buyBackAmount || "",
  //       };
        
  //     }

       
  //     }
      
  
  //     const formattedMaterials = allMaterials.map((material) => ({
  //       materialCode: material.materialCode,
  //       materialDescription: material.materialDescription,
  //       uom: material.uom,
  //       quantity: material.quantity,
  //       unitPrice: material.unitPrice,
  //      currency: material.currency,             // ✅ add this
  // conversionRate: material.conversionRate,
  //       materialCategory: material.materialCategory,
  //       materialSubCategory: material.materialSubCategory,
  //       budgetCode: material.budgetCode,
  //       totalPrice: material.totalPrice,
  //       modeOfProcurement: material.modeOfProcurement,
  //       vendorNames: material.vendorNames,
  //     }));
  
  //     // Update formData with the fetched material details
  //     setFormData((prev) => ({
  //       ...prev,
  //       materialDetails: formattedMaterials,
  //        buyBack: isBuyBack, 
  //         ...buyBackData,

  //     }));
  
  //     form.setFieldsValue({ materialDetails: formattedMaterials,buyBack: isBuyBack,  ...buyBackData,});
  
  //   } catch (err) {
  //     console.error("Failed to fetch materials for indent", err);
  //     message.error("Failed to load indent materials");
  //   }
  // };
  
  
  /*
  const handleChange = (fieldName, value) => {
    if (fieldName === "indentId") {
      setFormData((prev) => ({ ...prev, [fieldName]: value }));
      handleIndentSearch(value); // value is now an array, it should be passed directly
      return;
    }
    setFormData((prev) => ({ ...prev, [fieldName]: value }));
  };*/
  const handleChange = (fieldName, value) => {
    if (fieldName === "tenderId") {
        handleSearch(value);
        setFormData((prev) => ({ ...prev, [fieldName]: value }));
      return;
    }
    if (fieldName === "indentId") {
      const selectedIndents = approvedIndents.filter(indent =>
        value.includes(indent.value)
      );
  
      const projectNames = [...new Set(selectedIndents.map(i => i.projectName))];
  
      if (projectNames.length > 1) {
        message.error("All selected indents must be under the same project");
        return;
      }
  
      if (!selectedProjectName || projectNames[0] === selectedProjectName) {
        setSelectedProjectName(projectNames[0]);
        setFormData(prev => ({ ...prev, [fieldName]: value }));
        handleIndentSearch(value);
      } else {
        message.error(`Selected indent belongs to a different project: ${projectNames[0]}`);
      }
  
      return;
    }
  
    setFormData(prev => ({ ...prev, [fieldName]: value }));
  };

  

  
  /*
  const handleSearch = async () => {
    if (!searchTenderId) {
      message.error("Please enter a Tender ID");
      return;
    }
    try {
      const res = await axios.get(`api/tenders/${searchTenderId}`);
      if (res.data.responseData) {
        setFormData(res.data.responseData);
        message.success("Tender details loaded successfully");
      }
    } catch (error) {
      message.error("Failed to fetch tender details");
      console.error("Search error:", error);
    }
  };*/
  const handleSearch = async (value) => {
  try {
    // const { data } = await axios.get(
    //   `/api/tender-requests/base64Files/${value || formData.tenderId}`
    // );
    console.log("yy");
        const { data } = await axios.get(
  `/api/tender-requests/base64Files`,
  { params: { tenderId: value || formData.tenderId } }
);
    setSearchDone(true); 
    console.log("hello");
    const tenderResponse = data?.responseData || {};
// ADD: set form read-only if this is an old version
if (tenderResponse.isActive === false) {
  message.warning("You are viewing an older version of this tender. Load the latest version to make changes.");
}
    
  console.log("Aman");
    const indentIds = Array.isArray(tenderResponse.indentIds)
      ? tenderResponse.indentIds
      : tenderResponse.indentIds
      ? [tenderResponse.indentIds]
      : [];

    // Directly use projectName from response
    const formattedIndentIds = indentIds.map((id) => ({
      value: id,
      label: `Indent ${id} (${tenderResponse.projectName || ""})`,
    }));

    setSelectedProjectName(tenderResponse.projectName);
    console.log("test 1");
    setFormData((prev) => ({
      ...prev,
      ...tenderResponse,
      indentId: indentIds,
    }));
    console.log("test2");

    form.setFieldsValue({
      ...tenderResponse,
      indentId: formattedIndentIds,
    });
    console.log("test 3");
    if (indentIds.length) {
      await handleIndentSearch(indentIds);
    }
  } catch (error) {
    console.error("Search error:", error);
    message.error(
      error?.response?.data?.responseStatus?.message || "Error fetching tender data."
    );
  }
};

  useEffect(() => {
      if (tenderId) {
          handleSearch(tenderId); 
      }else if (indentIds?.length) {
        handleChange("indentId", indentIds); 
      }
      }, [tenderId, indentIds]);


/*
  const handlePrint = useReactToPrint({
    content: () => printRef.current,
  });*/
     const printComponentRef = useRef(); 
  
      const handlePrint = useReactToPrint({
          content: () => printComponentRef.current,
          documentTitle: `Tender - ${formData?.tenderId || "Draft"}`
      });
/*
  const onFinish = async () => {
    const { materialDetails, indentMaterials, ...filteredData } = formData;
  
    const payload = {
      ...filteredData,
      createdBy: userId,
      lastUpdatedBy: userId,
      fileType:"Tender",
    };
  
    try {
      setSubmitBtnLoading(true);
      const { data } = await axios.post("/api/tender-requests", payload, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
  
      if (data?.responseData?.tenderId) {
        setGeneratedTenderId(data.responseData.tenderId);
        setIsPrintEnabled(true);
        setModalOpen(true);
        message.success("Tender created successfully!");
      }
    } catch (error) {
      message.error("Failed to submit tender");
      console.error("Submission error:", error);
    } finally {
      setSubmitBtnLoading(false);
    }
  };
  */
  const onFinish = async () => {
  try {
    // TC_48: Check if tender is locked
    if (formData.isLocked && tenderId) {
      message.error({
        content: `This tender is locked. ${formData.lockedReason || 'Cannot update tender after Purchase Order has been created.'}`,
        duration: 5
      });
      return;
    }

    // TC_46: Prompt for update reason when updating
    if (tenderId) {
      const updateReason = prompt("Please enter the reason for updating this tender:");
      if (!updateReason || updateReason.trim() === "") {
        message.warning("Update reason is required when modifying a tender.");
        return;
      }
      formData.updateReason = updateReason.trim();
    }

    setSubmitBtnLoading(true);

    // const payload = {
    //   ...formData,
    //   createdBy: userId,
    //   lastUpdatedBy: userId,
    //   fileType: "Tender",
    //   materialDetails: (formData.materialDetails || []).map((m) => ({
    //     materialCode: m.materialCode || "",
    //     materialDescription: m.materialDescription || "",
    //     uom: m.uom || "",
    //     quantity: Number(m.quantity) || 0,
    //     unitPrice: Number(m.unitPrice) || 0,
    //     materialCategory: m.materialCategory || "",
    //     materialSubCategory: m.materialSubCategory || "",
    //     budgetCode: m.budgetCode || "",
    //     totalPrice: Number(m.totalPrice) || 0,
    //     modeOfProcurement: m.modeOfProcurement || "",
    //     vendorNames: m.vendorNames || "",
    //   })),
    //    ...(formData.buyBack
    //     ? {
    //         buyBack: formData.buyBack,
    //         buyBackAmount: formData.buyBackAmount || "",
    //         modelNumber: formData.modelNumber || "",
    //         serialNumber: formData.serialNumber || "",
    //         dateOfPurchase: formData.dateOfPurchase || null,
    //         uploadBuyBackFileNames: formData.uploadBuyBackFileNames || "",
    //       }
    //     : {}),
    // };
    const payload = {
  ...formData,
  createdBy: userId,
  lastUpdatedBy: userId,
  fileType: "Tender",
  materialDetails: (formData.materialDetails || []).map((m) => ({
    materialCode: m.materialCode || "",
    materialDescription: m.materialDescription || "",
    uom: m.uom || "",
    quantity: Number(m.quantity) || 0,
    unitPrice: Number(m.unitPrice) || 0,
    currency: m.currency || "",
    conversionRate: m.conversionRate || null,
    materialCategory: m.materialCategory || "",
    materialSubCategory: m.materialSubCategory || "",
    budgetCode: m.budgetCode || "",
    totalPrice: Number(m.totalPrice) || 0,
    modeOfProcurement: m.modeOfProcurement || "",
    vendorNames: m.vendorNames || "",
    origin: m.origin || "",
    briefDescription: m.briefDescription || "",
  })),
  jobDetails: (formData.jobDetails || []).map((j) => ({
    jobCode: j.jobCode || "",
    jobDescription: j.jobDescription || "",
    category: j.category || "",
    subCategory: j.subCategory || "",
    uom: j.uom || "",
    quantity: Number(j.quantity) || 0,
    estimatedPrice: Number(j.estimatedPrice) || 0,
    totalPrice: Number(j.totalPrice) || 0,
    currency: j.currency || "",
    briefDescription: j.briefDescription || "",
    origin: j.origin || "",
    modeOfProcurement: j.modeOfProcurement || "",
    budgetCode: j.budgetCode || "",
    vendorNames: j.vendorNames || "",
  })),
  ...(formData.buyBack ? {
    buyBack: formData.buyBack,
    buyBackAmount: formData.buyBackAmount || "",
    modelNumber: formData.modelNumber || "",
    serialNumber: formData.serialNumber || "",
    dateOfPurchase: formData.dateOfPurchase || null,
    uploadBuyBackFileNames: formData.uploadBuyBackFileNames || "",
  } : {}),
};

    let data;

    if (tenderId) {
      // Update
      // const response = await axios.put(`/api/tender-requests/${tenderId}`, payload, {
      //   headers: { Authorization: `Bearer ${token}` },
      // });
      const response = await axios.put(`/api/tender-requests`, payload, {
  headers: { Authorization: `Bearer ${token}` },
  params: { tenderId: formData.tenderId || tenderId }
});
      data = response.data;
      const newTenderId = data?.responseData?.tenderId; // e.g. T1001/2
if (newTenderId) {
  setFormData(prev => ({ ...prev, tenderId: newTenderId }));
}
      message.success({
        content: `Tender updated successfully to version ${data?.responseData?.tenderVersion || 'N/A'}. Vendors have been notified.`,
        duration: 5
      });
    } else {
      //Create
      const response = await axios.post("/api/tender-requests", payload, {
        headers: { Authorization: `Bearer ${token}` },
      });
      data = response.data;
      message.success("Tender created successfully");
    }

    if (data?.responseData?.tenderId) {
      setGeneratedTenderId(data.responseData.tenderId);
      setIsPrintEnabled(true);
      setModalOpen(true);
    }
  } catch (error) {
    // TC_48 & TC_50: Handle lock and validation errors
    const errorMessage = error?.response?.data?.errorMessage || error?.response?.data?.responseStatus?.message || "Failed to submit tender";
    message.error({
      content: errorMessage,
      duration: 7
    });
    console.error("Tender submit error:", error);
  } finally {
    setSubmitBtnLoading(false);
  }
};
 const handleSearchTenderIds = async () => {
  const { searchType, searchValue } = formData;

  if (!searchValue || !searchType) {
    message.warning("Please select search type and enter value.");
    return;
  }

  try {
    const { data } = await axios.get(`/api/tender-requests/search`, {
      params: {
        type: searchType,
        value: searchValue
      }
    });

    const tenderList = data?.responseData || [];

    const dropdownOptions = tenderList.map((item) => ({
      label: item.tenderId,
      value: item.tenderId
    }));

    setTenderIdOptions(dropdownOptions);

    if (dropdownOptions.length === 0) {
      message.warning("No Tender IDs found.");
    } else {
      message.success(`${dropdownOptions.length} Please Select Tender Id in Tender Id Drop Down.`);
    }
  } catch (error) {
    message.error("Error fetching Tender IDs.");
  }
};
useEffect(() => {
  if (formData.buyBack) {
    setBuyBackFields([
      {
        name: "uploadBuyBackFileNames",
        label: "Upload Buy Back File",
        type: "multiImage",
        required: true,
      },
      {
        name: "modelNumber",
        label: "Model Number",
        type: "text",
        required: true,
      },
      {
        name: "serialNumber",
        label: "Serial Number",
        type: "text",
        required: true,
      },
      {
        name: "dateOfPurchase",
        label: "Date Of Purchase",
        type: "date",
        required: true,
      },
      {
        name: "buyBackAmount",
        label: "Buy Back Amount",
        type: "text",
        required: true,
      },
    ]);
  } else {
    setBuyBackFields([]);
  }
}, [formData.buyBack]);


console.log("uday"+formData.buyBack);


  const TenderDetails = [
      {
            heading: "Search Tender",
            colCnt: 2,
            fieldList: [
        {
            name: "searchValue",
            label: "Search Value",
            type: "indentSearch",
            onSearch: () => handleSearchTenderIds(),
      // formData.searchType === "submittedDate" ? "date" : "text"
        },
    ]
    },
    {
      heading: "Tender Search",
      colCnt: 1,
      fieldList: [{
        name: "tenderId",
        label: "Tender Id",
        //type: "search",
        type: "select",
        options: tenderIdOptions || [],
        span: 1
      }]
    },
     {
        heading: "Status & Version",
        colCnt:4,
        fieldList:[
            ...(searchDone ? [
    {
        name: "processStage",
        label: "Process Stage",
        type: "text",
        disabled: true,
        span: 1
    },
    {
        name: "status",
        label: "Status",
        type: "text",
        disabled: true,
        span: 1
    },
    {
        name: "tenderVersion",
        label: "Tender Version",
        type: "text",
        disabled: true,
        span: 1
    },
    {
        name: "isLocked",
        label: "Locked Status",
        type: "text",
        disabled: true,
        span: 1,
        render: (value) => value ? "🔒 Locked" : "Unlocked"
    }
] : [])
        ]
    },
    {
      heading: "Tender Basic Details",
      colCnt: 4,
      fieldList: [
        {
          name: "titleOfTender",
          label: "Title of the Tender",
          type: "text",
          required: true,
          span: 2
        },
        {
          name: "openingDate",
          label: "Start Date",
          type: "date",
          required: true,
          span: 1
        },
        {
          name: "closingDate",
          label: "Closing Date",
          type: "date",
          required: true,
          span: 1
        },
      ]
    },
    {
      heading: "Indent Selection",
      colCnt: 2,
      fieldList: [
          {
              name: "indentId",
              label: "Select Indent ID",
              type: "multiIndentselect", // or "select" if single-select
              mode: "multiple",
              required: true,
              options: approvedIndents, // This will be overridden dynamically
              onChange: (val) => handleChange("indentId", val),
              showSearch: true,
              filterOption: filterIndentOption,
            },
      ]
    },
    // {
    //   heading: "Material Details",
    //   name: "materialDetails",
    //   colCnt: 4,
    //   children: [
    //     // Update materialCode field options to be populated dynamically
    //     {
    //       name: "materialCode",
    //       label: "Material Code",
    //       type: "select",
    //       span: 2,
    //       required: true,
    //     //  options: [], // Will be populated from API data
    //       showSearch: true,
    //       disabled: true,
    //       filterOption: (input, option) =>
    //         option.label.toLowerCase().includes(input.toLowerCase()),
    //     },
  
    //     // Update description field to show API data
    //     {
    //       name: "materialDescription",
    //       label: "Description",
    //       type: "select",
    //       span: 2,
    //       options: [], // Will be populated from API data
    //       showSearch: true,
    //       disabled: true,
    //       filterOption: (input, option) =>
    //         option.label.toLowerCase().includes(input.toLowerCase()),
    //       required: true,
    //     },
    //     {
    //       name: "uom",
    //       label: "UOM",
    //       type: "text",
    //       disabled: true,
    //       required: true,
    //       disabled: true,
    //     },
    //     {
    //       name: "quantity",
    //       label: "Quantity",
    //       type: "text",
    //     },
    //     {
    //       name: "unitPrice",
    //       label: "Unit Price",
    //       disabled: true,
    //       type: "text",
    //       span:1
    //     },
    //     {
    //       name: "currency",
    //       label: "Currency",
    //       disabled: true,
    //       type: "text",
    //       required: true,
    //       span: 1,
    //       disabled: true,
    //     },
    //   ...(
    //   (formData.materialDetails || []).some(m => m.currency && m.currency !== "INR")
    //     ? [{
    //         name: "conversionRate",
    //         label: "Conversion Rate",
    //         disabled: true,
    //         type: "text",
    //         span: 1,
    //       }]
    //     : []
    // ),
    //     {
    //       name: "budgetCode",
    //       label: "Budget Code",
    //       type: "select",
    //      // required: true,
    //      disabled: true,
    //       span: 2,
    //       options: [],
    //     },
    //     {
    //       name: "totalPrice",
    //       label: "Total Price",
    //       type: "text",
    //       disabled: true,
    //       span: 2,
         
    //     },
    //     {
    //       name: "materialCategory",
    //       label: "Material Category",
    //       type: "text",
    //       disabled: true,
    //       span: 2,
    //     },
    //     {
    //       name: "materialSubCategory",
    //       label: "Material Sub Category",
    //       type: "text",
    //       disabled: true,
    //       span: 2,
    //     },
    //     {
    //       name: "modeOfProcurement",
    //       label: "Mode of Procurement",
    //       type: "select",
    //       disabled: true,
    //       span: 2,
    //       options: [],
    //     },
    //     {
    //       name: "vendorNames",
    //       label: "Vendor Codes",
    //       disabled: true,
    //       type: "text",
    //       span: 2,
    //       // required: true,
    //     },
    //   ],
    // },
    ...(formData.indentType === "job"
  ? [{
      heading: "Job Details",
      name: "jobDetails",
      colCnt: 4,
      children: [
        { name: "jobCode",        label: "Job Code",        type: "text", disabled: true, span: 2 },
        { name: "jobDescription", label: "Job Description", type: "text", disabled: true, span: 2 },
        { name: "category",       label: "Category",        type: "text", disabled: true, span: 1 },
        { name: "subCategory",    label: "Sub Category",    type: "text", disabled: true, span: 1 },
        { name: "uom",            label: "UOM",             type: "text", disabled: true, span: 1 },
        { name: "quantity",       label: "Quantity",        type: "text", disabled: true, span: 1 },
        { name: "estimatedPrice", label: "Estimated Price", type: "text", disabled: true, span: 1 },
        { name: "currency",       label: "Currency",        type: "text", disabled: true, span: 1 },
        { name: "totalPrice",     label: "Total Price",     type: "text", disabled: true, span: 2 },
        { name: "briefDescription", label: "Brief Description", type: "text", disabled: true, span: 2 },
        { name: "origin",         label: "Origin",          type: "text", disabled: true, span: 1 },
        { name: "modeOfProcurement", label: "Mode of Procurement", type: "text", disabled: true, span: 1 },
        { name: "budgetCode",     label: "Budget Code",     type: "text", disabled: true, span: 2 },
        { name: "vendorNames",    label: "Vendor Codes",    type: "text", disabled: true, span: 2 },
      ],
    }]
  : [{
      heading: "Material Details",
      name: "materialDetails",
      colCnt: 4,
      children: [
        { name: "materialCode",        label: "Material Code",        type: "select", span: 2, disabled: true, showSearch: true, filterOption: (input, option) => option.label.toLowerCase().includes(input.toLowerCase()) },
        { name: "materialDescription", label: "Description",          type: "select", span: 2, disabled: true, showSearch: true, options: [], filterOption: (input, option) => option.label.toLowerCase().includes(input.toLowerCase()), required: true },
        { name: "uom",                 label: "UOM",                  type: "text",   disabled: true, required: true },
        { name: "quantity",            label: "Quantity",             type: "text", disabled:true },
        { name: "unitPrice",           label: "Unit Price",           type: "text",   disabled: true, span: 1 },
        { name: "currency",            label: "Currency",             type: "text",   disabled: true, span: 1 },
        ...((formData.materialDetails || []).some(m => m.currency && m.currency !== "INR")
          ? [{ name: "conversionRate", label: "Conversion Rate",      type: "text",   disabled: true, span: 1 }]
          : []),
        { name: "origin",              label: "Origin",               type: "text",   disabled: true, span: 1 },
        { name: "briefDescription",    label: "Brief Description",    type: "text",   disabled: true, span: 2 },
        { name: "budgetCode",          label: "Budget Code",          type: "select", disabled: true, span: 2, options: [] },
        { name: "totalPrice",          label: "Total Price",          type: "text",   disabled: true, span: 2 },
        { name: "materialCategory",    label: "Material Category",    type: "text",   disabled: true, span: 2 },
        { name: "materialSubCategory", label: "Material Sub Category",type: "text",   disabled: true, span: 2 },
        { name: "modeOfProcurement",   label: "Mode of Procurement",  type: "select", disabled: true, span: 2, options: [] },
        { name: "vendorNames",         label: "Vendor Codes",         type: "text",   disabled: true, span: 2 },
      ],
    }]
),
    {
      heading: "Tender Attachments",
      colCnt: 3,
      fieldList: [
        {
          name: "uploadTenderDocuments",
          label: "Tender Documents",
         // type: "image", //should be a multiple file upload field (.png, .jpeg, .pdf, .doc, etc. )
          type: "multiImage",
          span: 1
        },
        {
          name: "uploadGeneralTermsAndConditions",
          label: "General Terms & Conditions",
        //  type: "image", //should be a multiple file upload field (.png, .jpeg, .pdf, .doc, etc. )
          type: "multiImage",
          required: true,
          span: 1
        },
        {
          name: "uploadSpecificTermsAndConditions",
          label: "Specific Terms & Conditions",
          //type: "image", //should be a multiple file upload field (.png, .jpeg, .pdf, .doc, etc. )
          type: "multiImage",
          span: 1
        }
      ]
    },
    {
      heading: "Submission Details",
      colCnt: 3,
      fieldList: [
        {
          name: "bidType",
          label: "Bid Type",
          type: "select",
          required: true,
          span: 1,
          options: [
            { value: "Single", label: "Single Bid" },
            { value: "Double", label: "Two Bid" }
          ]
        },
       /* {
          name: "lastDate",
          label: "Last Date of Submission",
          type: "date",
          required: true,
          span: 1
        },*/
      /*  {
          name: "applicableTaxes",
          label: "Applicable Taxes",
          type: "text",
        //  required: true,
          span: 1
        }*/
      ]
    },
    {
      heading: "Pre-bid Meeting Details",
      colCnt: 3,
      fieldList: [
        {
          name: "preBidMeetingStatus",
          label: "Pre-bid Meeting Status",
          type: "select",
          span: 1,
          options: [
            { value: "NOT_CONDUCTED", label: "Not Conducted" },
            { value: "SCHEDULED", label: "Scheduled" },
            { value: "CONDUCTED", label: "Conducted" }
          ],
          required: false
        },
        {
          name: "preBidMeetingDate",
          label: "Pre-bid Meeting Date",
          type: "date",
          span: 1,
          required: false
        },
        {
          name: "preBidMeetingDiscussion",
          label: "Discussion Points",
          type: "text",
          span: 3,
          required: false,
          placeholder: "Enter discussion points from the pre-bid meeting..."
        }
      ]
    },
    {
      heading: "Commercial Terms",
      colCnt: 3,
      fieldList: [
        {
          name: "incoTerms",
          label: "INCO Terms",
         // type: "text",
         type:"select",
         options: incoTermsLOV.length > 0
           ? incoTermsLOV.map(lov => ({ label: lov.lovDisplayValue, value: lov.lovValue }))
           : incoOptionsDefault,
          required: true,
          span: 1
        },
        {
          name: "consignes",
          label: "Consignee Address",
          type: "select",
          required: true,
          options: consigneeOptions, // will be overridden
        },
        {
          name: "billingAddress",
          label: "Billing Address",
          type: "text",
          required: true,
          span: 1,
          disabled:true,
          //defaultValue:"Koramangala, 2nd Block, Bangalore -560034"
        }
      ]
    },
    {
      heading: "Payment & Performance",
      colCnt: 3,
      fieldList: [
        {
          name: "paymentTerms",
          label: "Payment Terms",
          type: "select",
          required: true,
          span: 2,
           options: paymentTermsLOV.length > 0
             ? paymentTermsLOV.map(lov => ({ label: lov.lovDisplayValue, value: lov.lovValue }))
             : [
               { value: "100% payment within 30 days from the date of acceptance.", label: "100% payment within 30 days from the date of acceptance." },
               { value: "Quarterly in advance on submission of invoice (in case of AMCs)", label: "Quarterly in advance on submission of invoice (in case of AMCs)" }
             ]
        },
        {
          name: "ldClause",
          label: "LD Clause",
          type: "checkbox",
          required: true,
          span: 1
        },
       /* {
          name: "applicablePerformance",
          label: "Performance Security",
          type: "text",
          required: true,
          span: 1
        }*/
      ]
    },
     ...(buyBackFields.length
    ? [
        {
          heading: "Buy Back Details",
          colCnt: 3,
          fieldList: buyBackFields,
        },
      ]
    : []),
    {
      heading: "Declarations",
      colCnt: 2,
      fieldList: [
        {
          name: "bidSecurityDeclaration",
          label: "Bid Security Declaration",
          type: "checkbox", //should be a checkbox field (true or false)
          span: 1
        },
        ...(formData.bidSecurityDeclaration ? [/*{
          name: "bidSecurityDownload",
          type: "downloadFile",
          fileName: "bid.pdf",
          downloadText: "Download Bid Security Template",
          required: true,
          span: 2,
          },*/{
                    name: "bidSecurityDeclarationFileName",
                    label: "Upload Bid Security Declaration",
                    type: "multiImage",
                    required: true,
                }] : []),
        {
          name: "mllStatusDeclaration",
          label: "MII Status Declaration",
          type: "checkbox", // should be a checkbox field (true or false)
          span: 1
        },
        ...(formData.mllStatusDeclaration ? [/*{
          name: "mllStatusDeclaration",
          type: "downloadFile",
          fileName: "mll.pdf",
          downloadText: "Download Mll Security Template",
          required: true,
          span: 2,
          },*/{
                    name: "mllStatusDeclarationFileName",
                    label: "Upload MII Security Declaration",
                    type: "multiImage",
                    required: true,
                }] : []),
        
      ]
    }
  ];
  const handleCancel = async (remarks) => {
  try {
    const payload = {
      tenderId: formData.tenderId,
      cancelStatus: true,
      cancelRemarks: remarks,
      actionBy: userId,
    };

    const response = await axios.put("/api/tender-requests/tender/cancel", payload);

    // Use responseData field from backend
    message.success({
      content: `${response.data.responseData}. Vendors have been notified via email.`,
      duration: 5
    });

    // Reset form
    setFormData({});
    setSearchDone(false);

  } catch (error) {
    console.error(error);
    const errorMsg = error.response?.data?.errorMessage || error.response?.data?.responseData || "Failed to cancel the Tender. Please try again.";

    // TC_50: Check for active PO error
    if (errorMsg.includes("active Purchase Order") || errorMsg.includes("PO exists")) {
      message.error({
        content: (
          <div>
            <strong>Cannot Cancel Tender</strong>
            <p>{errorMsg}</p>
            <p style={{marginTop: 8}}>Please cancel the Purchase Order first before cancelling this tender.</p>
          </div>
        ),
        duration: 10
      });
    } else {
      message.error({
        content: errorMsg,
        duration: 7
      });
    }
  }
};


 const fetchVersionHistory = async (tid) => {
  try {
    const baseTid = (tid || formData.tenderId || "").split("/")[0];
    // const { data } = await axios.get(`/api/tender-requests/version-history{baseTid}`);
    const { data } = await axios.get(`/api/tender-requests/version-history`, { params: { tenderId: baseTid } });
    const list = data?.responseData || [];
    setVersionHistoryList(list);
     setSelectedVersionIdx(list.length - 1);
    setVersionHistoryOpen(true);
  } catch (error) {
    message.error("Could not load version history.");
  }
};


  return (
    <Card className="a4-container" ref={printRef}>
      <Heading title="Tender Creation" />
        {/* Version info and history */}
{formData?.tenderId && (
  <div style={{ marginBottom: 16 }}>
    {formData.isActive === false && (
      <div style={{ background: '#fff7e6', border: '1px solid #ffd591', padding: '8px 16px', borderRadius: 4, marginBottom: 8 }}>
        ⚠️ Viewing Old Version (V{formData.tenderVersion}) — This is a superseded version. Load the latest to make changes.
      </div>
    )}
    {formData.tenderVersion > 1 && (
      <div style={{ background: '#e6f7ff', border: '1px solid #91d5ff', padding: '8px 16px', borderRadius: 4, marginBottom: 8 }}>
        Version {formData.tenderVersion} — This tender has been revised {formData.tenderVersion - 1} time(s).
      </div>
    )}
    <Button icon={<SearchOutlined />} onClick={() => fetchVersionHistory(formData.tenderId)}>
      View Version History
    </Button>
  </div>
)}

      {/* Form Start */}
      <CustomForm
        formData={formData}
        onFinish={onFinish}
        onFinishFailed={() => message.error("Please check required fields")}
      >
        {renderFormFields(
          TenderDetails,
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
          draftDataName="tenderDraft"
          submitBtnLoading={submitBtnLoading}
          submitBtnEnabled
         // printBtnEnabled={isPrintEnabled}
          printBtnEnabled
          draftBtnEnabled
          handlePrint={handlePrint}
          showCancel={searchDone}        // <-- show only if search is done
          onCancel={handleCancel} 
        />
      </CustomForm>

      <CustomModal
        isOpen={modalOpen}
        setIsOpen={setModalOpen}
        title="Tender Submission Successful"
        processNo={generatedTenderId}
      />
      <Modal
    open={versionHistoryOpen}
    onCancel={() => setVersionHistoryOpen(false)}
    title="Tender Version History"
    footer={null}
    width={900}
    destroyOnClose
>
    {(() => {
        const sorted = [...versionHistoryList].sort((a, b) => (a.tenderVersion || 0) - (b.tenderVersion || 0));
        const selIdx = Math.max(0, Math.min(selectedVersionIdx, sorted.length - 1));
        const curr = sorted[selIdx];
        const prev = selIdx > 0 ? sorted[selIdx - 1] : null;

        if (!curr) return <div style={{ padding: '24px', textAlign: 'center', color: '#999' }}>No versions found.</div>;

        const HEADER_FIELDS = [
            { key: 'titleOfTender',        label: 'Title' },
            { key: 'modeOfProcurement',    label: 'Mode of Procurement' },
            { key: 'bidType',              label: 'Bid Type' },
            { key: 'openingDate',          label: 'Opening Date' },
            { key: 'closingDate',          label: 'Closing Date' },
            { key: 'lastDateOfSubmission', label: 'Last Date of Submission' },
            { key: 'applicableTaxes',      label: 'Applicable Taxes' },
            { key: 'incoTerms',            label: 'Inco Terms' },
            { key: 'paymentTerms',         label: 'Payment Terms' },
            { key: 'ldClause',             label: 'LD Clause' },
            { key: 'projectName',          label: 'Project Name' },
            { key: 'singleAndMultipleVendors', label: 'Vendor Type' },
        ];

        const headerDiffs = prev
            ? HEADER_FIELDS.filter(f => String(prev[f.key] ?? '') !== String(curr[f.key] ?? ''))
                .map(f => ({ ...f, oldVal: prev[f.key], newVal: curr[f.key] }))
            : [];

        // Indent IDs diff
        const prevIndents = prev?.indentIds || [];
        const currIndents = curr.indentIds || [];
        const addedIndents = currIndents.filter(id => !prevIndents.includes(id));
        const removedIndents = prevIndents.filter(id => !currIndents.includes(id));
        const indentsChanged = addedIndents.length > 0 || removedIndents.length > 0;

        // Total value diff
        const prevTotal = prev ? Number(prev.totalTenderValue || 0) : null;
        const currTotal = Number(curr.totalTenderValue || 0);
        const totalChanged = prev && prevTotal !== currTotal;

        const totalChanges = headerDiffs.length + (indentsChanged ? 1 : 0) + (totalChanged ? 1 : 0);
        const fmtCurrency = val => val != null ? `₹ ${Number(val).toLocaleString('en-IN', { maximumFractionDigits: 2 })}` : '—';
        const fmtVal = val => (val == null || val === '') ? '—' : String(val);

        return (
            <div style={{ display: 'flex', minHeight: '450px' }}>

                {/* ── Left: version list ── */}
                <div style={{ width: '190px', flexShrink: 0, borderRight: '1px solid #f0f0f0' }}>
                    <div style={{ padding: '8px 12px', fontWeight: 600, fontSize: '11px', color: '#aaa', letterSpacing: '1px', borderBottom: '1px solid #f0f0f0' }}>
                        VERSIONS
                    </div>
                    {sorted.map((v, idx) => {
                        const isSel = idx === selIdx;
                        return (
                            <div key={v.tenderId} onClick={() => setSelectedVersionIdx(idx)} style={{
                                padding: '10px 14px', cursor: 'pointer',
                                borderLeft: isSel ? '3px solid #1890ff' : '3px solid transparent',
                                background: isSel ? '#e6f7ff' : 'transparent',
                                borderBottom: '1px solid #f5f5f5',
                            }}>
                                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                                    <span style={{ fontWeight: 600, fontSize: '14px' }}>V{v.tenderVersion}</span>
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

                {/* ── Right: diff panel ── */}
                <div style={{ flex: 1, padding: '0 16px', overflowY: 'auto', maxHeight: '520px' }}>

                    {/* Comparison heading */}
                    <div style={{ padding: '12px 0', borderBottom: '1px solid #f0f0f0', display: 'flex', alignItems: 'center', gap: '8px', flexWrap: 'wrap' }}>
                        {prev ? (
                            <>
                                <span style={{ fontWeight: 600, color: '#888' }}>V{prev.tenderVersion}</span>
                                <span style={{ color: '#ccc' }}>→</span>
                                <span style={{ fontWeight: 600, color: '#1890ff' }}>V{curr.tenderVersion}</span>
                                {totalChanges === 0
                                    ? <Tag>No changes</Tag>
                                    : <Tag color="blue">{totalChanges} change{totalChanges !== 1 ? 's' : ''}</Tag>}
                            </>
                        ) : (
                            <span style={{ fontWeight: 600, color: '#52c41a' }}>V{curr.tenderVersion} — Initial Version</span>
                        )}
                        <Button type="link" size="small" style={{ marginLeft: 'auto', padding: 0 }}
                            onClick={() => { handleSearch(curr.tenderId); setVersionHistoryOpen(false); }}>
                            Load {curr.tenderId} ↗
                        </Button>
                    </div>

                    {/* Initial version */}
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

                    {/* No changes */}
                    {prev && totalChanges === 0 && (
                        <div style={{ padding: '24px 0', color: '#888', fontSize: '13px' }}>
                            No field-level changes detected compared to V{prev.tenderVersion}.
                        </div>
                    )}

                    {/* Diff sections */}
                    {prev && totalChanges > 0 && (
                        <>
                            {/* Total value */}
                            {totalChanged && (
                                <div style={{ marginTop: '16px' }}>
                                    <div style={{ fontWeight: 600, fontSize: '11px', color: '#aaa', letterSpacing: '1px', marginBottom: '8px' }}>TOTAL VALUE</div>
                                    <div style={{ display: 'flex', alignItems: 'center', gap: '10px', padding: '10px 14px', background: '#fffbe6', border: '1px solid #ffe58f', borderRadius: '6px' }}>
                                        <span style={{ fontSize: '12px', color: '#888', flex: 1 }}>Total Tender Value</span>
                                        <span style={{ color: '#cf1322', textDecoration: 'line-through', fontSize: '13px' }}>{fmtCurrency(prevTotal)}</span>
                                        <span style={{ color: '#bbb' }}>→</span>
                                        <span style={{ color: '#389e0d', fontWeight: 600, fontSize: '13px' }}>{fmtCurrency(currTotal)}</span>
                                    </div>
                                </div>
                            )}

                            {/* Header fields */}
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

                            {/* Indent IDs changed */}
                            {indentsChanged && (
                                <div style={{ marginTop: '16px' }}>
                                    <div style={{ fontWeight: 600, fontSize: '11px', color: '#aaa', letterSpacing: '1px', marginBottom: '8px' }}>LINKED INDENTS</div>
                                    {addedIndents.map(id => (
                                        <div key={id} style={{ padding: '7px 14px', marginBottom: '4px', background: '#f6ffed', border: '1px solid #b7eb8f', borderRadius: '4px', color: '#389e0d', fontSize: '13px' }}>
                                            + {id}
                                        </div>
                                    ))}
                                    {removedIndents.map(id => (
                                        <div key={id} style={{ padding: '7px 14px', marginBottom: '4px', background: '#fff1f0', border: '1px solid #ffa39e', borderRadius: '4px', color: '#cf1322', fontSize: '13px' }}>
                                            − {id}
                                        </div>
                                    ))}
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
                <TenderPrintFormat ref={printComponentRef} data={formData} />
      </div>
    </Card>
  );
};

export default Tender;
