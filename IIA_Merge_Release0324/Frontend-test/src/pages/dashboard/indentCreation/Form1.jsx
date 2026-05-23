import React, { forwardRef, useEffect, useRef, useState } from "react";
import axios from "axios";
import {
  Form,
  Input,
  Select,
  Button,
  Upload,
  DatePicker,
  Checkbox,
  Space,
  Row,
  Col,
  message,
  Modal,
  Radio,
  Divider,
  Card,
} from "antd";
import {
  UploadOutlined,
  SearchOutlined,
  RestOutlined,
  SendOutlined,
  SaveOutlined,
  ReloadOutlined,
  PrinterOutlined,
  CheckCircleOutlined,
  PlusOutlined,
  DeleteOutlined,
} from "@ant-design/icons";
import dayjs from "dayjs";
import customParseFormat from "dayjs/plugin/customParseFormat";
import { useSelector } from "react-redux";
import { useLocation } from "react-router-dom";
import LineItem from "../LineItem";
import FormContainer from "../../../components/DKG_FormContainer";
import Heading from "../../../components/DKG_Heading";
import { useReactToPrint } from "react-to-print";
dayjs.extend(customParseFormat);

const { Option } = Select;

const quarterDropdownOptions = [
  { label: "Q1", value: "Q1" },
  { label: "Q2", value: "Q2" },
  { label: "Q3", value: "Q3" },
  { label: "Q4", value: "Q4" },
];

const reasonDropdownOptions = [
  {
    label: "It is in the knowledge of the user department that only a particular firm is the manufacturer of the required goods",
    value: "It is in the knowledge of the user department that only a particular firm is the manufacturer of the required goods",
  },
  {
    label: "In a case of emergency, the required goods are necessarily to be purchased from a particular source",
    value: "In a case of emergency, the required goods are necessarily to be purchased from a particular source",
  },
  {
    label: "For standardization of machinery or spare parts to be compatible to the existing sets of equipment, the required item is to be purchased only from a selected firm",
    value: "For standardization of machinery or spare parts to be compatible to the existing sets of equipment, the required item is to be purchased only from a selected firm",
  },
];

// Job category options
const jobCategoryOptions = [
  { label: "AMC (Annual Maintenance Contract)", value: "AMC" },
  { label: "Rate Contract", value: "Rate Contract" },
  { label: "Repair & Service", value: "Repair And Service" },
  { label: "Internet Service", value: "Internet Service" },
  { label: "Other Service", value: "Other Service" },
];

// Job subcategory options
const jobSubcategoryOptions = [
  { label: "Chemicals", value: "Chemicals" },
  { label: "Computer & Peripherals", value: "Computer & Peripherals" },
  { label: "Electrical", value: "Electrical" },
  { label: "Electronic Items", value: "Electronic Items" },
  { label: "Equipment", value: "Equipment" },
  { label: "Furniture", value: "Furniture" },
  { label: "HARDWARE", value: "HARDWARE" },
  { label: "Miscellaneous", value: "Miscellaneous" },
  { label: "Software", value: "Software" },
  { label: "Stationary", value: "Stationary" },
  { label: "Vehicles", value: "Vehicles" },
];

// Currency options
const currencyOptions = [
  { label: "INR", value: "INR" },
  { label: "USD", value: "USD" },
  { label: "EUR", value: "EUR" },
  { label: "GBP", value: "GBP" },
];

// UOM options
const uomOptions = [
  { label: "Nos", value: "Nos" },
  { label: "Kg", value: "Kg" },
  { label: "Ltr", value: "Ltr" },
  { label: "Mtr", value: "Mtr" },
  { label: "Set", value: "Set" },
  { label: "Pair", value: "Pair" },
  { label: "Box", value: "Box" },
  { label: "Pack", value: "Pack" },
  { label: "Unit", value: "Unit" },
];

const Form1 = () => {
  const auth = useSelector((state) => state.auth);
  const actionPerformer = auth.userId;
  const location = useLocation();
  const [form] = Form.useForm();
  const [preBidRequired, setPreBidRequired] = useState(false);
  const [rateContractIndent, setRateContractIndent] = useState(false);
  const [loading, setLoading] = useState(false);
  const [materialList, setMaterialList] = useState([]);
  const [materialDetailsMap, setMaterialDetailsMap] = useState({});
  const [projects, setProjects] = useState([]);
  const [locations, setLocations] = useState([]);
  const [showSuccessModal, setShowSuccessModal] = useState(false);
  const [generatedIndentId, setGeneratedIndentId] = useState("");
  const [editingIndentId, setEditingIndentId] = useState(null); // set when editing existing indent
  const [isPrintEnabled, setIsPrintEnabled] = useState(false);
  const [showDraftSavedModal, setShowDraftSavedModal] = useState(false);
  const [isBrandPac, setIsBrandPac] = useState(false);
  const [buyBackOption, setBuyBackOption] = useState(false);
  const [hasProprietaryItem, setHasProprietaryItem] = useState(false);

  // Indent Type State
  const [indentType, setIndentType] = useState("material"); // "material" or "job"

  // Material / Job Category Type State
  const [materialCategoryType, setMaterialCategoryType] = useState("all"); // "all", "computer", "non-computer"
  const [jobCategoryType, setJobCategoryType] = useState("all"); // "all", "computer", "non-computer"

  const { userName, email, mobileNumber } = useSelector((state) => state.auth);

  const printRef = useRef();
  const handlePrint = useReactToPrint({
    content: () => printRef.current,
  });

  // Fetch locations
  useEffect(() => {
    const fetchLocations = async () => {
      try {
        const { data } = await axios.get("/api/location-master");

        if (
          data.responseStatus.statusCode === 0 &&
          Array.isArray(data.responseData)
        ) {
          setLocations(data.responseData);
        } else {
          message.error("Failed to load locations");
        }
      } catch (error) {
        console.error("Error fetching locations:", error);
        message.error("Failed to fetch locations");
      }
    };
    fetchLocations();
  }, []);

  // Fetch projects
  useEffect(() => {
    const fetchProjects = async () => {
      setLoading(true);
      try {
        const { data } = await axios.get("/api/project-master");

        if (
          data.responseStatus.statusCode === 0 &&
          Array.isArray(data.responseData)
        ) {
          setProjects(data.responseData);
        } else {
          message.error("Failed to project data");
        }
      } catch (error) {
        console.error("Error fetching projects:", error);
        message.error("Failed to fetch project data");
      } finally {
        setLoading(false);
      }
    };
    fetchProjects();
  }, []);

  // Fetch materials
  useEffect(() => {
    const fetchMaterials = async () => {
      try {
        const { data } = await axios.get("/api/material-master");

        if (!data.responseData) throw new Error("Invalid material data");

        const materialMap = data.responseData.reduce(
          (acc, material) => ({
            ...acc,
            [material.materialCode]: {
              ...material,
              materialDescription: material.description,
              materialCategory: material.category,
              materialSubCategory: material.subCategory,
              currency: material.currency,
              modeOfProcurement: material.modeOfProcurement,
              unitPrice: material.unitPrice,
              vendorNames: material.vendorNames,
            },
          }),
          {}
        );

        setMaterialDetailsMap(materialMap);
        setMaterialList(Object.keys(materialMap));
      } catch (error) {
        message.error("Failed to load materials");
        console.error("Material fetch error:", error);
      }
    };

    fetchMaterials();
  }, []);

  // Filter materials based on category type
  const getFilteredMaterialList = () => {
    if (materialCategoryType === "computer") {
      return materialList.filter((code) => {
        const material = materialDetailsMap[code];
        return (
          material?.materialSubCategory === "Computer & Peripherals" ||
          material?.materialCategory === "Computer & Peripherals"
        );
      });
    } else if (materialCategoryType === "non-computer") {
      return materialList.filter((code) => {
        const material = materialDetailsMap[code];
        return (
          material?.materialSubCategory !== "Computer & Peripherals" &&
          material?.materialCategory !== "Computer & Peripherals"
        );
      });
    }
    return materialList;
  };

 // FIXED — calls API
const [draftBtnLoading, setDraftBtnLoading] = useState(false);

const handleSaveDraft = async () => {
    const values = form.getFieldsValue();
    const payload = {
        indentorName: values.indentorName || null,
        indentorMobileNo: values.indentorMobileNo || null,
        indentorEmailAddress: values.indentorEmail || null,
        consignesLocation: values.consigneeLocation || null,
        quarter: values.quarter || null,
        purpose: values.purpose || null,
        createdBy: actionPerformer || 0,
        indentType: indentType,
        materialCategoryType: indentType === "material" ? materialCategoryType : jobCategoryType,
        materialDetails: indentType === "material"
            ? (values.lineItems || []).map(item => ({ ...item }))
            : null,
        jobDetails: indentType === "job"
            ? (values.jobItems || []).map(item => ({ ...item }))
            : null,
        fileType: "Indent",
    };

    try {
        setDraftBtnLoading(true);
        let data;
        if (editingIndentId) {
            const response = await axios.put(`/api/indents/draft?indentId=${editingIndentId}`, payload);
            data = response.data;
        } else {
            const response = await axios.post("/api/indents/draft", payload);
            data = response.data;
        }
        setEditingIndentId(data.responseData?.indentId);
        setShowDraftSavedModal(true);
    } catch (error) {
        message.error("Error saving draft: " + error.message);
    } finally {
        setDraftBtnLoading(false);
    }
};


  const handleCheckboxChange4 = (e) => {
    setBuyBackOption(e.target.checked);
  };

  const PrintableContent = forwardRef(({ children }, ref) => (
    <div ref={ref} className="printable-content">
      {children}
    </div>
  ));

  const handleSearch = async () => {
    const indentId = form.getFieldValue("indentId");
    if (!indentId) {
      message.error("Please enter an Indent ID");
      return;
    }

    try {
      const { data } = await axios.get(`/api/indents/${indentId}`);

      if (!data.responseData) {
        throw new Error("Invalid API response: responseData is missing");
      }

      const responseData = data.responseData;
      
      // Set indent type from response
      if (responseData.indentType) {
        setIndentType(responseData.indentType);
      }
      if (responseData.materialCategoryType) {
        if (responseData.indentType === 'job') {
          setJobCategoryType(responseData.materialCategoryType);
        } else {
          setMaterialCategoryType(responseData.materialCategoryType);
        }
      }

      const getFileList = (fileName) =>
        fileName ? [{ uid: "-1", name: fileName, status: "done" }] : [];

      const formData = {
        indentId: generatedIndentId || "",
        indentorName: responseData.indentorName || "",
        indentorMobileNo: responseData.indentorMobileNo || "",
        indentorEmail: responseData.indentorEmailAddress || "",
        consigneeLocation: responseData.consignesLocation || "",
        projectName: responseData.projectName || "",
        preBidMeetingRequired: responseData.isPreBidMeetingRequired || false,
        preBidMeetingDetails: responseData.preBidMeetingDate
          ? dayjs(responseData.preBidMeetingDate, "DD/MM/YYYY")
          : null,
        preBidMeetingLocation: responseData.preBidMeetingVenue || "",
        rateContractIndent: responseData.isItARateContractIndent || false,
        estimatedRate: parseFloat(responseData.estimatedRate) || 0,
        periodOfRateContract: parseFloat(responseData.periodOfContract) || 0,
        singleOrMultipleJob: responseData.singleAndMultipleJob || "",
        uploadingPriorApprovalsFileName: getFileList(
          responseData.uploadingPriorApprovalsFileName
        ),
        technicalSpecificationsFileName: getFileList(
          responseData.technicalSpecificationsFileName
        ),
        draftEOIOrRFPFileName: getFileList(responseData.draftEOIOrRFPFileName),
        uploadPACOrBrandPAC: getFileList(
          responseData.uploadPACOrBrandPACFileName
        ),
        lineItems: Array.isArray(responseData.materialDetails)
          ? responseData.materialDetails.map((item) => ({
              materialCode: item.materialCode || "",
              materialDescription: item.materialDescription || "",
              quantity: parseFloat(item.quantity) || 0,
              unitPrice: parseFloat(item.unitPrice) || 0,
              uom: item.uom || "",
              currency: item.currency || "",
              totalPrice: parseFloat(item.totalPrize) || 0,
              budgetCode: item.budgetCode || "",
              materialCategory: item.materialCategory || "",
              materialSubcategory: item.materialSubCategory || "",
              materialOrJobCodeUsedByDept: item.materialAndJob || "",
              modeOfProcurement: item.modeOfProcurement || "",
            }))
          : [],
        jobItems: Array.isArray(responseData.jobDetails)
          ? responseData.jobDetails.map((item) => ({
              jobCode: item.jobCode || "",
              jobCategory: item.category || "",
              jobSubcategory: item.subCategory || "",
              jobDescription: item.jobDescription || "",
              uom: item.uom || "",
              quantity: parseFloat(item.quantity) || 0,
              briefDescription: item.briefDescription || "",
              estimatedPrice: parseFloat(item.estimatedPrice) || 0,
              currency: item.currency || "",
              origin: item.origin || "",
              modeOfProcurement: item.modeOfProcurement || "",
              budgetCode: item.budgetCode || "",
              vendorNames: item.vendorNames || "",
            }))
          : [],
      };

      form.setFieldsValue(formData);
      setEditingIndentId(responseData.indentId || null);
      setPreBidRequired(formData.preBidMeetingRequired);
      setRateContractIndent(formData.rateContractIndent);
      message.success("Form data fetched successfully");
    } catch (error) {
      message.error(`Failed to fetch form data: ${error.message}`);
      console.error("Error fetching data:", error);
    }
  };

  const normFile = (e) => {
    if (Array.isArray(e)) {
      return e;
    }
    return e && e.fileList;
  };

  const uploadFileToServer = async (file, fieldName) => {
    try {
      const formData = new FormData();
      formData.append("file", file);

      const { data } = await axios.post("/file/upload?fileType=Indent", formData, {
        headers: {
          Authorization: `Bearer ${auth.token}`,
        },
      });

      return data.responseData.fileName;
    } catch (error) {
      message.error(`${fieldName} upload failed: ${error.message}`);
      throw error;
    }
  };

  // Handle submit
  const handleSubmit = async (values) => {
    setLoading(true);

    try {
      const uploadFiles = async (fileList, fieldName) => {
        if (!fileList || fileList.length === 0) return "";
        const uploadedNames = await Promise.all(
          fileList.map((file) => {
            if (!file.originFileObj) {
              // Existing file loaded from DB — return its name as-is
              return Promise.resolve(file.name || "");
            }
            return uploadFileToServer(file.originFileObj, fieldName);
          })
        );
        return uploadedNames.filter(Boolean).join(", ");
      };

      const [
        priorApprovalsFile,
        technicalSpecifications,
        draftEOIOrRFP,
        uploadPACOrBrandPAC,
        buyBackDocuments,
      ] = await Promise.all([
        uploadFiles(values.uploadingPriorApprovalsFileName, "Prior Approvals"),
        uploadFiles(values.technicalSpecificationsFileName, "Tender Documents"),
        uploadFiles(values.draftEOIOrRFPFileName, "EOI/RFP"),
        uploadFiles(values.uploadPACOrBrandPACFileName, "Brand PAC"),
        uploadFiles(values.buyBackDocumentsFileName, "Buyback Documents"),
      ]);

      const preBidMeetingDate =
        values.preBidMeetingRequired && values.preBidMeetingDetails
          ? values.preBidMeetingDetails.format("DD/MM/YYYY")
          : null;

      const preBidMeetingVenue = values.preBidMeetingRequired
        ? String(values.preBidMeetingLocation)
        : null;

      // Build payload based on indent type
      let materialDetails = null;
      let jobDetails = null;

      if (indentType === "material") {
        materialDetails = (values.lineItems || []).map((item) => {
          const quantity = Number(item.quantity) || 0;
          const unitPrice = Number(item.unitPrice) || 0;

          return {
            materialCode: String(item.materialCode) || null,
            materialDescription: String(item.materialDescription) || null,
            quantity: quantity,
            unitPrice: unitPrice,
            uom: String(item.uom) || null,
            currency: String(item.currency) || null,
            budgetCode: String(item.budgetCode) || null,
            materialCategory: String(item.materialCategory) || null,
            materialSubCategory: String(item.materialSubcategory) || null,
            modeOfProcurement: String(item.modeOfProcurement) || null,
            vendorNames: item.vendorNames || null,
          };
        });
      } else if (indentType === "job") {
        jobDetails = (values.jobItems || []).map((item) => {
          return {
            jobCode: String(item.jobCode) || null,
            jobDescription: String(item.jobDescription) || null,
            category: String(item.jobCategory) || null,
            subCategory: String(item.jobSubcategory) || null,
            uom: String(item.uom) || null,
            quantity: Number(item.quantity) || 0,
            briefDescription: String(item.briefDescription) || null,
            estimatedPrice: Number(item.estimatedPrice) || 0,
            currency: String(item.currency) || null,
            origin: item.origin ? String(item.origin) : null,
            modeOfProcurement: item.modeOfProcurement ? String(item.modeOfProcurement) : null,
            budgetCode: item.budgetCode ? String(item.budgetCode) : null,
            vendorNames: item.vendorNames ? String(item.vendorNames) : null,
          };
        });
      }

      const payload = {
        indentorEmailAddress: values.indentorEmail || null,
        indentorMobileNo: values.indentorMobileNo || null,
        indentorName: values.indentorName || null,
        consignesLocation: values.consigneeLocation || "Bangalore",
        isItARateContractIndent: values.rateContractIndent,
        isPreBidMeetingRequired: values.preBidMeetingRequired,
        periodOfContract: values.periodOfRateContract || 0,
        rateContractJobCodes: values.singleOrMultipleJob ? [values.singleOrMultipleJob] : [],
        reason: hasProprietaryItem ? values.reason : null,
        proprietaryJustification: hasProprietaryItem
          ? values.proprietaryJustification
          : null,
        quarter: values.quarter || null,
        purpose: values.purpose || null,
        createdBy: actionPerformer || 0,
        estimatedRate: values.estimatedRate || 0,
        fileType: "Indent",
        projectName: values.projectName || null,
        preBidMeetingDate: preBidMeetingDate,
        preBidMeetingVenue: preBidMeetingVenue,
        justification: values.justification || null,
        brandAndModel: values.brandAndModel || null,
        brandPac: values.brandPac || null,
        draftEOIOrRFPFileName: draftEOIOrRFP || null,
        uploadPACOrBrandPACFileName: uploadPACOrBrandPAC || null,
        technicalSpecificationsFileName: technicalSpecifications || null,
        uploadingPriorApprovalsFileName: priorApprovalsFile || null,
        uploadBuyBackFileNames: buyBackDocuments || null,
        buyBack: values.buyBackOption || null,
        // Include indent type and material category type
        indentType: indentType,
        materialCategoryType: indentType === "material" ? materialCategoryType : (indentType === "job" ? jobCategoryType : null),
        // Include appropriate details based on type
        materialDetails: materialDetails,
        jobDetails: jobDetails,
      };

      delete payload.lineItems;
      delete payload.jobItems;

      const isEditing = !!editingIndentId;
      const submitUrl = isEditing
        ? `/api/indents/${editingIndentId}`
        : "/api/indents";
      const { data: responseData } = isEditing
        ? await axios.put(submitUrl, payload)
        : await axios.post(submitUrl, payload);

      if (responseData.responseStatus.statusCode !== 0) {
        throw new Error(
          responseData.responseStatus?.message || "Submission failed"
        );
      }

      setGeneratedIndentId(responseData.responseData.indentId);
      form.setFieldsValue({ indentId: responseData.responseData.indentId });
      setShowSuccessModal(true);
      setIsPrintEnabled(true);
      message.success(isEditing ? "Indent updated successfully!" : "Indent submitted successfully!");
      if (!isEditing) {
        form.resetFields();
        setBuyBackOption(false);
        setIsBrandPac(false);
      }
      setEditingIndentId(null);
    } catch (error) {
      message.error(`Submission Error: ${error.message}`);
      console.error("Detailed Error:", error);
    } finally {
      setLoading(false);
    }
  };

  const calculateTotalPrice = (record) => {
    const quantity = parseFloat(record.quantity) || 0;
    const unitPrice = parseFloat(record.unitPrice) || 0;
    return quantity * unitPrice;
  };

  const handlePriceCalculation = (index, field, value) => {
    const lineItems = form.getFieldValue("lineItems");
    if (lineItems[index]) {
      const totalPrice = calculateTotalPrice({
        ...lineItems[index],
        [field]: value,
      });

      const updatedItems = [...lineItems];
      updatedItems[index] = {
        ...updatedItems[index],
        totalPrice: totalPrice,
      };

      form.setFieldsValue({ lineItems: updatedItems });
    }
  };

  const handleCheckboxChange = (e) => {
    setPreBidRequired(e.target.checked);
  };

  const handleCheckboxChange2 = (e) => {
    setRateContractIndent(e.target.checked);
  };

  const handleCheckboxChange3 = (e) => {
    setIsBrandPac(e.target.checked);
  };

  // Material selection handler
  const handleMaterialSelect = (index, materialCode) => {
    const materialData = materialDetailsMap[materialCode] || {};
    const lineItems = form.getFieldValue("lineItems") || [];
    const updatedItems = [...lineItems];

    updatedItems[index] = {
      ...updatedItems[index],
      materialCode: materialCode,
      materialDescription: materialData.description || "",
      materialCategory: materialData.category || "",
      materialSubcategory: materialData.subCategory || "",
      uom: materialData.uom || "",
      currency: materialData.currency || "",
      unitPrice: materialData.unitPrice || 0,
      modeOfProcurement: materialData.modeOfProcurement
        ? materialData.modeOfProcurement.trim().toUpperCase()
        : "",
      vendorNames: (materialData.vendorNames || []).join(", "),
    };

    form.setFieldsValue({ lineItems: updatedItems });

    // Category validation
    const categories = updatedItems
      .map((item) => item?.materialCategory)
      .filter(Boolean);

    if (categories.length === 0) return;

    const firstCategory = categories[0];
    const allSame = categories.every((cat) => cat === firstCategory);

    if (!allSame) {
      message.error("All materials must be of the same category.");
      updatedItems[index] = {
        ...updatedItems[index],
        materialCode: "",
        materialDescription: "",
        materialCategory: "",
        materialSubcategory: "",
        uom: "",
        currency: "",
        modeOfProcurement: "",
        unitPrice: 0,
      };

      form.setFieldsValue({ lineItems: updatedItems });
      form.setFields([
        {
          name: ["lineItems", index, "materialCode"],
          errors: ["Category must match first material"],
        },
      ]);
    }
  };

  const handleMaterialDescriptionSelect = (index, materialCode) => {
    handleMaterialSelect(index, materialCode);
  };

  // Handle indent type change
  const handleIndentTypeChange = (e) => {
    const newType = e.target.value;
    setIndentType(newType);

    if (newType === "job") {
      setMaterialCategoryType("all");
    } else {
      setJobCategoryType("all");
    }

    // Reset line items
    if (newType === "material") {
      form.setFieldsValue({ lineItems: [{}], jobItems: [] });
    } else {
      form.setFieldsValue({ lineItems: [], jobItems: [{}] });
    }
  };

  // Handle material category type change
  const handleMaterialCategoryTypeChange = (e) => {
    const newCategoryType = e.target.value;
    setMaterialCategoryType(newCategoryType);
    form.setFieldsValue({ lineItems: [{}] });
  };

  // Set initial values
  useEffect(() => {
    form.setFieldsValue({
      indentorEmail: email,
      indentorMobileNo: mobileNumber,
      indentorName: userName,
    });
  }, []);

  // Auto-load indent when navigated from queue (edit mode)
  useEffect(() => {
    const navIndentId = location.state?.indentId;
    if (navIndentId) {
      form.setFieldsValue({ indentId: navIndentId });
      setEditingIndentId(navIndentId);
      // Trigger the same search logic used by the search button
      (async () => {
        try {
          const { data } = await axios.get(`/api/indents/${navIndentId}`);
          const responseData = data.responseData;
          if (!responseData) throw new Error("No indent data");

          const generatedIndentId = responseData.indentId;
          setGeneratedIndentId(generatedIndentId);

          if (responseData.indentType) setIndentType(responseData.indentType);
          if (responseData.materialCategoryType) {
            if (responseData.indentType === 'job') {
              setJobCategoryType(responseData.materialCategoryType);
            } else {
              setMaterialCategoryType(responseData.materialCategoryType);
            }
          }

          const getFileList = (fileName) =>
            fileName ? [{ uid: "-1", name: fileName, status: "done" }] : [];

          form.setFieldsValue({
            indentId: generatedIndentId || "",
            indentorName: responseData.indentorName || "",
            indentorMobileNo: responseData.indentorMobileNo || "",
            indentorEmail: responseData.indentorEmailAddress || "",
            consigneeLocation: responseData.consignesLocation || "",
            projectName: responseData.projectName || "",
            preBidMeetingRequired: responseData.isPreBidMeetingRequired || false,
            rateContractIndent: responseData.isItARateContractIndent || false,
            quarter: responseData.quarter || "",
            purpose: responseData.purpose || "",
            reason: responseData.reason || "",
            uploadingPriorApprovalsFileName: getFileList(responseData.uploadingPriorApprovalsFileName),
            technicalSpecificationsFileName: getFileList(responseData.technicalSpecificationsFileName),
            lineItems: Array.isArray(responseData.materialDetails)
              ? responseData.materialDetails.map((item) => ({
                  materialCode: item.materialCode || "",
                  materialDescription: item.materialDescription || "",
                  quantity: item.quantity || 0,
                  unitPrice: item.unitPrice || 0,
                  totalPrice: item.totalPrice || 0,
                  uom: item.uom || "",
                  currency: item.currency || "",
                  budgetCode: item.budgetCode || "",
                  materialCategory: item.materialCategory || "",
                  materialSubcategory: item.materialSubCategory || "",
                  modeOfProcurement: item.modeOfProcurement || "",
                }))
              : [],
            jobItems: Array.isArray(responseData.jobDetails)
              ? responseData.jobDetails.map((item) => ({
                  jobCode: item.jobCode || "",
                  jobCategory: item.category || "",
                  jobSubcategory: item.subCategory || "",
                  jobDescription: item.jobDescription || "",
                  uom: item.uom || "",
                  quantity: item.quantity || 0,
                  briefDescription: item.briefDescription || "",
                  estimatedPrice: parseFloat(item.estimatedPrice) || 0,
                  currency: item.currency || "",
                  origin: item.origin || "",
                  modeOfProcurement: item.modeOfProcurement || "",
                  budgetCode: item.budgetCode || "",
                  vendorNames: item.vendorNames || "",
                }))
              : [],
          });
          setPreBidRequired(responseData.isPreBidMeetingRequired || false);
          setRateContractIndent(responseData.isItARateContractIndent || false);
        } catch (error) {
          message.error(`Failed to load indent for editing: ${error.message}`);
        }
      })();
    }
  }, [location.state]);

  const { vendorMaster } = useSelector((state) => state.masters);
  const vendorMasterMod = vendorMaster?.map((vendor) => ({
    label: vendor.vendorName,
    value: vendor.vendorName,
  }));

  // Get filtered material list based on category type
  const filteredMaterialList = getFilteredMaterialList();

  return (
    <PrintableContent ref={printRef}>
      <FormContainer>
        <Heading title={"Indent Creation"} />

        {/* Search Section */}
        <Row justify="end">
          <Col>
            <Form form={form} layout="inline" style={{ marginBottom: 16 }}>
              <Form.Item label="Indent ID" name="indentId">
                <Space>
                  <Input
                    placeholder="Enter Indent ID to load"
                    value={editingIndentId || undefined}
                    readOnly={!!editingIndentId}
                    style={editingIndentId ? { backgroundColor: "#f6ffed", borderColor: "#52c41a" } : {}}
                  />
                  {!editingIndentId && (
                    <Button type="primary" onClick={handleSearch}>
                      <SearchOutlined />
                    </Button>
                  )}
                  {editingIndentId && (
                    <Button
                      size="small"
                      onClick={() => {
                        setEditingIndentId(null);
                        form.setFieldsValue({ indentId: "" });
                      }}
                    >
                      Clear
                    </Button>
                  )}
                </Space>
              </Form.Item>
            </Form>
          </Col>
        </Row>

        <Form
          form={form}
          layout="vertical"
          onFinish={handleSubmit}
          onFinishFailed={(errorInfo) => {
            console.error("Validation Failed:", errorInfo);
            message.error("Please fill all required fields");
          }}
          initialValues={{
            indentId: generatedIndentId || "",
            lineItems: [{}],
            jobItems: [{}],
            preBidMeetingRequired: false,
            rateContractIndent: false,
            consigneeLocation: "Bangalore",
          }}
          ref={printRef}
        >
          {/* Indentor Details Section with Inline Toggle Buttons */}
          <div style={{ 
            display: "flex", 
            justifyContent: "space-between", 
            alignItems: "center",
            marginBottom: "16px",
            flexWrap: "wrap",
            gap: "12px"
          }}>
            <h3 style={{ margin: 0, fontSize: "16px", fontWeight: 600, color: "#333" }}>
              Indentor Details
            </h3>
            
            {/* Sleek Toggle Buttons */}
            <div style={{ display: "flex", gap: "16px", alignItems: "center" }}>
              {/* Indent Type Toggle */}
              <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
                <span style={{ fontSize: "13px", color: "#666", fontWeight: 500 }}>Type:</span>
                <Radio.Group
                  value={indentType}
                  onChange={handleIndentTypeChange}
                  size="small"
                  buttonStyle="solid"
                >
                  <Radio.Button value="material" style={{ fontSize: "12px" }}>
                    Material
                  </Radio.Button>
                  <Radio.Button value="job" style={{ fontSize: "12px" }}>
                    Job/Service
                  </Radio.Button>
                </Radio.Group>
              </div>

              {/* Material Category Toggle - Only show for material indent */}
              {indentType === "material" && (
                <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
                  <span style={{ fontSize: "13px", color: "#666", fontWeight: 500 }}>Category:</span>
                  <Radio.Group
                    value={materialCategoryType}
                    onChange={handleMaterialCategoryTypeChange}
                    size="small"
                    buttonStyle="solid"
                  >
                    <Radio.Button value="all" style={{ fontSize: "12px" }}>All</Radio.Button>
                    <Radio.Button value="computer" style={{ fontSize: "12px" }}>Computer</Radio.Button>
                    <Radio.Button value="non-computer" style={{ fontSize: "12px" }}>Non-Computer</Radio.Button>
                  </Radio.Group>
                </div>
              )}

              {/* Job Category Toggle - Only show for job indent */}
              {indentType === "job" && (
                <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
                  <span style={{ fontSize: "13px", color: "#666", fontWeight: 500 }}>Category:</span>
                  <Radio.Group
                    value={jobCategoryType}
                    onChange={(e) => setJobCategoryType(e.target.value)}
                    size="small"
                    buttonStyle="solid"
                  >
                    <Radio.Button value="all" style={{ fontSize: "12px" }}>All</Radio.Button>
                    <Radio.Button value="computer" style={{ fontSize: "12px" }}>Computer</Radio.Button>
                    <Radio.Button value="non-computer" style={{ fontSize: "12px" }}>Non-Computer</Radio.Button>
                  </Radio.Group>
                </div>
              )}
            </div>
          </div>

          <div className="form-section">
            <Form.Item
              label="Indentor Name"
              name="indentorName"
              rules={[{ required: true, message: "Indentor name is required" }]}
            >
              <Input disabled />
            </Form.Item>

            <Form.Item
              label="Indentor Mobile No."
              name="indentorMobileNo"
              rules={[
                {
                  required: true,
                  message: "Indentor mobile number is required",
                },
              ]}
            >
              <Input disabled />
            </Form.Item>
          </div>

          <div className="form-section">
            <Form.Item
              label="Indentor Email"
              name="indentorEmail"
              rules={[{ required: true, message: "Indentor email is required" }]}
            >
              <Input disabled />
            </Form.Item>

            <Form.Item
              label="Consignee Location"
              name="consigneeLocation"
              rules={[
                { required: true, message: "Consignee location is required" },
              ]}
            >
              <Select placeholder="Select location">
                {locations.map((location) => (
                  <Option
                    key={location.locationCode}
                    value={location.locationName}
                  >
                    {location.locationName}
                  </Option>
                ))}
              </Select>
            </Form.Item>

            <Form.Item
              label="Upload Prior Approvals"
              name="uploadingPriorApprovalsFileName"
              valuePropName="fileList"
              getValueFromEvent={normFile}
            >
              <Upload beforeUpload={() => false}>
                <Button icon={<UploadOutlined />}>Upload File</Button>
              </Upload>
            </Form.Item>
          </div>

          {/* Conditional rendering based on Indent Type */}
          {indentType === "material" ? (
            /* Material Details Section */
            <div className="print-section">
              <LineItem
                setHasProprietaryItem={setHasProprietaryItem}
                form={form}
                materialList={filteredMaterialList}
                projects={projects}
                materialDetailsMap={materialDetailsMap}
                calculateTotalPrice={calculateTotalPrice}
                handleMaterialSelect={handleMaterialSelect}
                handlePriceCalculation={handlePriceCalculation}
                handleMaterialDescriptionSelect={handleMaterialDescriptionSelect}
              />
            </div>
          ) : (
            /* Job Details Section */
            <>
              <div style={{ 
                display: "flex", 
                justifyContent: "space-between", 
                alignItems: "center",
                marginBottom: "12px",
                marginTop: "16px"
              }}>
                <h3 style={{ margin: 0, fontSize: "16px", fontWeight: 600, color: "#333" }}>
                  Job Details
                </h3>
                <Button
                  type="dashed"
                  size="small"
                  icon={<PlusOutlined />}
                  onClick={() => {
                    const jobItems = form.getFieldValue("jobItems") || [];
                    form.setFieldsValue({ jobItems: [...jobItems, {}] });
                  }}
                >
                  Add Job
                </Button>
              </div>

              <Form.List name="jobItems">
                {(fields, { add, remove }) => (
                  <>
                    {fields.map(({ key, name, ...restField }, index) => (
                      <div
                        key={key}
                        style={{
                          border: "1px solid #d9d9d9",
                          borderRadius: "6px",
                          padding: "16px",
                          marginBottom: "16px",
                          backgroundColor: "#fafafa",
                          position: "relative",
                        }}
                      >
                        {fields.length > 1 && (
                          <Button
                            type="text"
                            danger
                            icon={<DeleteOutlined />}
                            onClick={() => remove(name)}
                            style={{
                              position: "absolute",
                              top: "8px",
                              right: "8px",
                            }}
                          />
                        )}

                        {/* Row 1: Job Code, Job Category, Job Subcategory */}
                        <Row gutter={16}>
                          <Col span={8}>
                            <Form.Item
                              {...restField}
                              name={[name, "jobCode"]}
                              label="Job Code"
                            >
                              <Input placeholder="Enter Job Code" />
                            </Form.Item>
                          </Col>
                          <Col span={8}>
                            <Form.Item
                              {...restField}
                              name={[name, "jobCategory"]}
                              label={<span><span style={{ color: "red" }}>*</span> Job Category</span>}
                              rules={[{ required: true, message: "Select job category" }]}
                            >
                              <Select placeholder="Select Job Category">
                                {jobCategoryOptions.map((opt) => (
                                  <Option key={opt.value} value={opt.value}>
                                    {opt.label}
                                  </Option>
                                ))}
                              </Select>
                            </Form.Item>
                          </Col>
                          <Col span={8}>
                            <Form.Item
                              {...restField}
                              name={[name, "jobSubcategory"]}
                              label={<span><span style={{ color: "red" }}>*</span> Job Subcategory</span>}
                              rules={[{ required: true, message: "Select job subcategory" }]}
                            >
                              <Select placeholder="Select Job Subcategory">
                                {jobSubcategoryOptions.map((opt) => (
                                  <Option key={opt.value} value={opt.value}>
                                    {opt.label}
                                  </Option>
                                ))}
                              </Select>
                            </Form.Item>
                          </Col>
                        </Row>

                        {/* Row 2: Job Description, UOM, Brief Description */}
                        <Row gutter={16}>
                          <Col span={8}>
                            <Form.Item
                              {...restField}
                              name={[name, "jobDescription"]}
                              label={<span><span style={{ color: "red" }}>*</span> Job Description</span>}
                              rules={[{ required: true, message: "Enter job description" }]}
                            >
                              <Input placeholder="Enter Job Description" />
                            </Form.Item>
                          </Col>
                          <Col span={8}>
                            <Form.Item
                              {...restField}
                              name={[name, "uom"]}
                              label={<span><span style={{ color: "red" }}>*</span> UOM</span>}
                              rules={[{ required: true, message: "Select UOM" }]}
                            >
                              <Select placeholder="Select Unit of Measure">
                                {uomOptions.map((opt) => (
                                  <Option key={opt.value} value={opt.value}>
                                    {opt.label}
                                  </Option>
                                ))}
                              </Select>
                            </Form.Item>
                          </Col>
                          <Col span={8}>
                            <Form.Item
                              {...restField}
                              name={[name, "briefDescription"]}
                              label={<span><span style={{ color: "red" }}>*</span> Brief Description of Job</span>}
                              rules={[{ required: true, message: "Enter brief description" }]}
                            >
                              <Input placeholder="Enter Brief Description" />
                            </Form.Item>
                          </Col>
                        </Row>

                        {/* Row 2b: Quantity */}
                        <Row gutter={16}>
                          <Col span={8}>
                            <Form.Item
                              {...restField}
                              name={[name, "quantity"]}
                              label={<span><span style={{ color: "red" }}>*</span> Quantity</span>}
                              rules={[{ required: true, message: "Enter quantity" }]}
                            >
                              <Input type="number" placeholder="Enter Quantity" />
                            </Form.Item>
                          </Col>
                          <Col span={8}>
                            <Form.Item
                              {...restField}
                              name={[name, "modeOfProcurement"]}
                              label="Mode of Procurement"
                            >
                              <Select placeholder="Select Mode of Procurement" allowClear>
                                {["GEM", "OPEN_TENDER", "LIMITED_TENDER", "SINGLE_TENDER", "PROPRIETARY", "RATE_CONTRACT"].map((opt) => (
                                  <Option key={opt} value={opt}>{opt.replace(/_/g, " ")}</Option>
                                ))}
                              </Select>
                            </Form.Item>
                          </Col>
                          <Col span={8}>
                            <Form.Item
                              {...restField}
                              name={[name, "budgetCode"]}
                              label="Budget Code"
                            >
                              <Input placeholder="Enter Budget Code" />
                            </Form.Item>
                          </Col>
                        </Row>

                        {/* Row 3: Estimated Price, Currency, Origin */}
                        <Row gutter={16}>
                          <Col span={8}>
                            <Form.Item
                              {...restField}
                              name={[name, "estimatedPrice"]}
                              label={<span><span style={{ color: "red" }}>*</span> Estimated Price</span>}
                              rules={[{ required: true, message: "Enter estimated price" }]}
                            >
                              <Input type="number" placeholder="Enter Estimated Price" />
                            </Form.Item>
                          </Col>
                          <Col span={8}>
                            <Form.Item
                              {...restField}
                              name={[name, "currency"]}
                              label={<span><span style={{ color: "red" }}>*</span> Currency</span>}
                              rules={[{ required: true, message: "Select currency" }]}
                            >
                              <Select placeholder="Select Currency">
                                {currencyOptions.map((opt) => (
                                  <Option key={opt.value} value={opt.value}>
                                    {opt.label}
                                  </Option>
                                ))}
                              </Select>
                            </Form.Item>
                          </Col>
                          <Col span={8}>
                            <Form.Item
                              {...restField}
                              name={[name, "origin"]}
                              label={<span><span style={{ color: "red" }}>*</span> Origin</span>}
                              rules={[{ required: true, message: "Select origin" }]}
                            >
                              <Radio.Group>
                                <Radio value="Indigenous">Indigenous</Radio>
                                <Radio value="Imported">Imported</Radio>
                              </Radio.Group>
                            </Form.Item>
                          </Col>
                        </Row>

                        {/* Row 4: Vendor Names */}
                        <Row gutter={16}>
                          <Col span={24}>
                            <Form.Item
                              {...restField}
                              name={[name, "vendorNames"]}
                              label="Vendor Names (comma-separated, required for Proprietary)"
                            >
                              <Input placeholder="e.g. Vendor A, Vendor B" />
                            </Form.Item>
                          </Col>
                        </Row>
                      </div>
                    ))}
                  </>
                )}
              </Form.List>
            </>
          )}

          {/* Project Section */}
          <div className="form-section">
            <Form.Item name="projectName" label="Project Name">
              <Select placeholder="Select project" loading={loading} allowClear>
                {projects.map((project) => (
                  <Option
                    key={project.projectNameDescription}
                    value={project.projectNameDescription}
                  >
                    {project.projectNameDescription}
                  </Option>
                ))}
              </Select>
            </Form.Item>

            <Form.Item
              label="Upload Technical Specifications"
              name="technicalSpecificationsFileName"
              valuePropName="fileList"
              getValueFromEvent={normFile}
            >
              <Upload beforeUpload={() => false}>
                <Button icon={<UploadOutlined />}>
                  Upload Technical Specifications
                </Button>
              </Upload>
            </Form.Item>
          </div>

          {/* Pre-bid Meeting Section */}
          <Form.Item name="preBidMeetingRequired" valuePropName="checked">
            <Checkbox onChange={handleCheckboxChange}>
              Pre-bid Meeting Required
            </Checkbox>
          </Form.Item>
          <div className="form-section">
            {preBidRequired && (
              <Row gutter={20}>
                <Col span={12}>
                  <Form.Item
                    name="preBidMeetingDetails"
                    label="Tentative Meeting Date"
                    rules={[
                      {
                        required: preBidRequired,
                        message: "Meeting date is required",
                      },
                    ]}
                  >
                    <DatePicker
                      format="DD/MM/YYYY"
                      disabledDate={(current) =>
                        current && current < dayjs().startOf("day")
                      }
                    />
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item
                    label="Meeting Location"
                    name="preBidMeetingLocation"
                    rules={[
                      {
                        required: preBidRequired,
                        message: "Pre Bid Meeting Location is required",
                      },
                    ]}
                  >
                    <Select placeholder="Select location">
                      {locations.map((location) => (
                        <Option
                          key={location.locationCode}
                          value={location.locationName}
                        >
                          {location.locationName}
                        </Option>
                      ))}
                    </Select>
                  </Form.Item>
                </Col>
              </Row>
            )}
          </div>

          {/* Buy Back Section */}
          <Form.Item name="buyBackOption" valuePropName="checked">
            <Checkbox onChange={handleCheckboxChange4}>Buy Back</Checkbox>
          </Form.Item>
          {buyBackOption && (
            <Form.Item
              label="Upload Buyback Documents"
              name="buyBackDocuments"
              valuePropName="fileList"
              getValueFromEvent={normFile}
            >
              <Upload beforeUpload={() => false}>
                <Button icon={<UploadOutlined />}>
                  Upload Buyback Documents
                </Button>
              </Upload>
            </Form.Item>
          )}

          {/* Rate Contract Section */}
          <Form.Item name="rateContractIndent" valuePropName="checked">
            <Checkbox onChange={handleCheckboxChange2}>
              Is it a rate contract indent
            </Checkbox>
          </Form.Item>
          <div className="form-section">
            {rateContractIndent && (
              <Row gutter={24}>
                <Col span={8}>
                  <Form.Item
                    name="estimatedRate"
                    label="Estimated Rate"
                    rules={[
                      {
                        required: true,
                        message: "Please enter estimated rate!",
                      },
                    ]}
                  >
                    <Input type="number" placeholder="Enter Estimated Rate" />
                  </Form.Item>
                </Col>
                <Col span={8}>
                  <Form.Item
                    name="periodOfRateContract"
                    label="Period of Rate Contract"
                    rules={[
                      {
                        required: true,
                        message: "Enter Period of Contract!",
                      },
                    ]}
                  >
                    <Input type="number" />
                  </Form.Item>
                </Col>
                <Col span={8}>
                  <Form.Item
                    name="singleOrMultipleJob"
                    label="Single or Multiple Job"
                    rules={[
                      {
                        required: true,
                      },
                    ]}
                  >
                    <Select placeholder="Select">
                      <Option value="Single">Single</Option>
                      <Option value="Multiple">Multiple</Option>
                    </Select>
                  </Form.Item>
                </Col>
              </Row>
            )}
          </div>

          {/* Proprietary Section - Only for Material Indent */}
          {indentType === "material" && hasProprietaryItem && (
            <>
              <Form.Item
                name="reason"
                label="Reason for Proprietary/Single Tender"
                rules={[{ required: true, message: "Please select a reason" }]}
              >
                <Select placeholder="Select reason">
                  {reasonDropdownOptions.map((opt) => (
                    <Option key={opt.value} value={opt.value}>
                      {opt.label}
                    </Option>
                  ))}
                </Select>
              </Form.Item>

              <div className="form-section">
                <Form.Item
                  name="proprietaryJustification"
                  label="Justification"
                  rules={[
                    { required: true, message: "Please provide justification" },
                  ]}
                >
                  <Input.TextArea
                    rows={4}
                    placeholder="Enter detailed justification for proprietary procurement"
                  />
                </Form.Item>
              </div>
            </>
          )}

          {/* Quarter and Purpose Section */}
          <div className="grid grid-cols-2 gap-x-8">
            <Form.Item name="quarter" label="Quarter">
              <Select options={quarterDropdownOptions} />
            </Form.Item>

            <Form.Item name="purpose" label="Purpose">
              <Input />
            </Form.Item>
          </div>

          {/* Draft EOI/RFP Upload */}
          <div className="form-section">
            <Form.Item
              label="Upload draft EOI or RFP"
              name="draftEOIOrRFPFileName"
              valuePropName="fileList"
              getValueFromEvent={normFile}
            >
              <Upload beforeUpload={() => false}>
                <Button icon={<UploadOutlined />}>Upload EOI or RFP</Button>
              </Upload>
            </Form.Item>
          </div>

          {/* Brand PAC Section */}
          <Form.Item name="brandPac" valuePropName="checked">
            <Checkbox onChange={handleCheckboxChange3}>Is it a brand PAC</Checkbox>
          </Form.Item>

          <div className="form-section">
            {isBrandPac && (
              <>
                <Form.Item
                  label="Brand PAC Approval"
                  name="uploadPACOrBrandPACFileName"
                  valuePropName="fileList"
                  getValueFromEvent={normFile}
                >
                  <Upload beforeUpload={() => false}>
                    <Button icon={<UploadOutlined />}>Upload Brand PAC</Button>
                  </Upload>
                </Form.Item>
                <Form.Item label="Brand and Model" name="brandAndModel">
                  <Input />
                </Form.Item>
                <Form.Item
                  label="It is known that as per the Rule 144 of GFR..."
                  name="justification"
                >
                  <Input placeholder="Enter Declaration" />
                </Form.Item>
              </>
            )}
          </div>

          {/* Action Buttons */}
          <Form.Item>
            <div style={{ display: "flex", justifyContent: "space-between" }}>
              <Button type="default" htmlType="reset">
                <ReloadOutlined /> Reset
              </Button>
              <Button type="primary" htmlType="submit" loading={loading}>
                <SendOutlined /> Submit
              </Button>
              <Button type="dashed" htmlType="button" onClick={handleSaveDraft} loading={draftBtnLoading}>
              {/* <Button type="dashed" htmlType="button" onClick={handleSaveDraft}> */}
                <SaveOutlined /> Save Draft
              </Button>
              <Button
                type="default"
                onClick={handlePrint}
                style={{ marginRight: 8 }}
                disabled={!isPrintEnabled}
              >
                <PrinterOutlined /> Print
              </Button>
            </div>
          </Form.Item>

          {/* Success Modal */}
          <Modal
            open={showSuccessModal}
            onOk={() => setShowSuccessModal(false)}
            onCancel={() => setShowSuccessModal(false)}
            footer={[
              <Button
                key="ok"
                type="primary"
                onClick={() => setShowSuccessModal(false)}
              >
                OK
              </Button>,
            ]}
          >
            <div className="flex flex-col items-center py-4">
              <CheckCircleOutlined className="text-green-500 text-4xl mb-4" />
              <h3 className="text-lg font-semibold mb-2">
                Indent saved successfully
              </h3>
              <p className="text-gray-600">Indent ID: {generatedIndentId}</p>
            </div>
          </Modal>

          {/* Draft Saved Modal */}
          <Modal
            open={showDraftSavedModal}
            onCancel={() => setShowDraftSavedModal(false)}
            footer={null}
            centered
          >
            <div style={{ textAlign: "center", padding: "20px" }}>
              <CheckCircleOutlined
                style={{
                  fontSize: "48px",
                  color: "#52c41a",
                  marginBottom: "16px",
                }}
              />
              <h3>Draft Saved Successfully</h3>
              <p>Your changes have been saved locally.</p>
            </div>
          </Modal>
        </Form>
      </FormContainer>
    </PrintableContent>
  );
};

export default Form1;