import React, { useRef, useState, useEffect } from "react";
import { Button, Card, Form, Input, Select, DatePicker, message } from "antd";
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
      let isBuyBack = false; 
      let buyBackData = {};
      // Loop through all selected indent IDs
      for (const id of indentIds) {
        const res = await axios.get(`/api/indents/IndentDataForTender`,{params: {indentId :id}});
        const indentData = res.data?.responseData;
   
        if (indentData?.materialDetails) {
          allMaterials.push(...indentData.materialDetails);
          
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
       currency: material.currency,             // ✅ add this
  conversionRate: material.conversionRate,
        materialCategory: material.materialCategory,
        materialSubCategory: material.materialSubCategory,
        budgetCode: material.budgetCode,
        totalPrice: material.totalPrice,
        modeOfProcurement: material.modeOfProcurement,
        vendorNames: material.vendorNames,
      }));
  
      // Update formData with the fetched material details
      setFormData((prev) => ({
        ...prev,
        materialDetails: formattedMaterials,
         buyBack: isBuyBack, 
          ...buyBackData,

      }));
  
      form.setFieldsValue({ materialDetails: formattedMaterials,buyBack: isBuyBack,  ...buyBackData,});
  
    } catch (err) {
      console.error("Failed to fetch materials for indent", err);
      message.error("Failed to load indent materials");
    }
  };
  
  
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
    const { data } = await axios.get(
      `/api/tender-requests/base64Files/${value || formData.tenderId}`
    );
    setSearchDone(true); 

    const responseData = data?.responseData || {};

    const indentIds = Array.isArray(responseData.indentIds)
      ? responseData.indentIds
      : responseData.indentIds
      ? [responseData.indentIds]
      : [];

    // Directly use projectName from response
    const formattedIndentIds = indentIds.map((id) => ({
      value: id,
      label: `Indent ${id} (${responseData.projectName || ""})`,
    }));

    setSelectedProjectName(responseData.projectName);

    setFormData((prev) => ({
      ...prev,
      ...responseData,
      indentId: indentIds,
    }));

    form.setFieldsValue({
      ...responseData,
      indentId: formattedIndentIds,
    });

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
        materialCategory: m.materialCategory || "",
        materialSubCategory: m.materialSubCategory || "",
        budgetCode: m.budgetCode || "",
        totalPrice: Number(m.totalPrice) || 0,
        modeOfProcurement: m.modeOfProcurement || "",
        vendorNames: m.vendorNames || "",
      })),
       ...(formData.buyBack
        ? {
            buyBack: formData.buyBack,
            buyBackAmount: formData.buyBackAmount || "",
            modelNumber: formData.modelNumber || "",
            serialNumber: formData.serialNumber || "",
            dateOfPurchase: formData.dateOfPurchase || null,
            uploadBuyBackFileNames: formData.uploadBuyBackFileNames || "",
          }
        : {}),
    };

    let data;

    if (tenderId) {
      // Update
      const response = await axios.put(`/api/tender-requests/${tenderId}`, payload, {
        headers: { Authorization: `Bearer ${token}` },
      });
      data = response.data;
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
    {
      heading: "Material Details",
      name: "materialDetails",
      colCnt: 4,
      children: [
        // Update materialCode field options to be populated dynamically
        {
          name: "materialCode",
          label: "Material Code",
          type: "select",
          span: 2,
          required: true,
        //  options: [], // Will be populated from API data
          showSearch: true,
          disabled: true,
          filterOption: (input, option) =>
            option.label.toLowerCase().includes(input.toLowerCase()),
        },
  
        // Update description field to show API data
        {
          name: "materialDescription",
          label: "Description",
          type: "select",
          span: 2,
          options: [], // Will be populated from API data
          showSearch: true,
          disabled: true,
          filterOption: (input, option) =>
            option.label.toLowerCase().includes(input.toLowerCase()),
          required: true,
        },
        {
          name: "uom",
          label: "UOM",
          type: "text",
          disabled: true,
          required: true,
          disabled: true,
        },
        {
          name: "quantity",
          label: "Quantity",
          type: "text",
        },
        {
          name: "unitPrice",
          label: "Unit Price",
          disabled: true,
          type: "text",
          span:1
        },
        {
          name: "currency",
          label: "Currency",
          disabled: true,
          type: "text",
          required: true,
          span: 1,
          disabled: true,
        },
      ...(
      (formData.materialDetails || []).some(m => m.currency && m.currency !== "INR")
        ? [{
            name: "conversionRate",
            label: "Conversion Rate",
            disabled: true,
            type: "text",
            span: 1,
          }]
        : []
    ),
        {
          name: "budgetCode",
          label: "Budget Code",
          type: "select",
         // required: true,
         disabled: true,
          span: 2,
          options: [],
        },
        {
          name: "totalPrice",
          label: "Total Price",
          type: "text",
          disabled: true,
          span: 2,
          disabled: true,
        },
        {
          name: "materialCategory",
          label: "Material Category",
          type: "text",
          disabled: true,
          span: 2,
        },
        {
          name: "materialSubCategory",
          label: "Material Sub Category",
          type: "text",
          disabled: true,
          span: 2,
        },
        {
          name: "modeOfProcurement",
          label: "Mode of Procurement",
          type: "select",
          disabled: true,
          span: 2,
          options: [],
        },
        {
          name: "vendorNames",
          label: "Vendor Codes",
          disabled: true,
          type: "text",
          span: 2,
          // required: true,
        },
      ],
    },
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


 


  return (
    <Card className="a4-container" ref={printRef}>
      <Heading title="Tender Creation" />

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
      <div style={{ display: "none" }}>
                <TenderPrintFormat ref={printComponentRef} data={formData} />
      </div>
    </Card>
  );
};

export default Tender;
