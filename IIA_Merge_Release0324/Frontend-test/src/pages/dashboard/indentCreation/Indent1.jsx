        import { Card, message, Select, Row, Col, Tag, Button, Alert, Space, Modal } from 'antd'
        import React, { useEffect, useRef, useState } from 'react'
        import { useSelector, useDispatch } from 'react-redux'
        import { fetchMasters } from '../../../store/slice/masterSlice'
        import { HistoryOutlined } from '@ant-design/icons'
        import Heading from '../../../components/DKG_Heading'
        import { renderFormFields } from '../../../utils/CommonFunctions'
        import CustomForm from '../../../components/DKG_CustomForm'
        import ButtonContainer from '../../../components/ButtonContainer'
        import { useReactToPrint } from 'react-to-print'
        import axios from 'axios'
        import CustomModal from '../../../components/CustomModal'
        import PurchaseHistoryModal from '../../../components/PurchaseHistoryModal'
        import IndentCancellationModal from '../../../components/IndentCancellationModal'
        import PrintFormate from '../../../utils/PrintFormate'
        import { useLocation } from "react-router-dom";
        import { useNavigate } from "react-router-dom";
        import { useLOVValues } from '../../../hooks/useLOVValues';
    
        const { Option } = Select;
    
        // File upload configuration
        const MAX_FILE_SIZE_MB = 50;
        const MAX_FILE_SIZE_BYTES = MAX_FILE_SIZE_MB * 1024 * 1024;
        const proprietaryLimitedDeclarationLabel = "The budgetary quote was obtained informing the vendor about:  (i) IIA's Payment Terms - 100% payment within 30 days from acceptance (ii). Applicability of providing performance & warranty security. (iii) Applicability of LD Clause."
    
        // Mode of Procurement options — 6 allowed values per backend validation
        const modeOfProcurementOptions = [
            { label: "Open Tender", value: "OPEN_TENDER" },
            { label: "Global Tender", value: "GLOBAL_TENDER" },
            { label: "Limited Tender", value: "LIMITED_TENDER" },
            { label: "Proprietary Purchase", value: "PROPRIETARY" },
            // { label: "BRAND PAC", value: "BRAND_PAC" },
            { label: "GEM Government e-Marketplace", value: "GEM" },
        ];
    
        // Modes that do NOT allow vendor selection
        const NO_VENDOR_MODES = ["OPEN_TENDER", "GLOBAL_TENDER"
            // , "BRAND_PAC"
            , "GEM"];
        // Modes that require vendor selection
        const VENDOR_REQUIRED_MODES = ["LIMITED_TENDER", "PROPRIETARY"];
    
        const reasonDropdown = [
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
            { label: "Other Service", value: "Other Service" }
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
            { label: "Vehicles", value: "Vehicles" }
        ];
    
        // Currency options
        const currencyOptions = [
            { label: "USD", value: "USD" },
            { label: "INR", value: "INR" },
            { label: "EUR", value: "EUR" },
            { label: "GBP", value: "GBP" }
        ];
    
        const Indent1 = () => {
            const navigate = useNavigate();
            const dispatch = useDispatch();
            const { userName, mobileNumber, email, userId, employeeDepartment } = useSelector(state => state.auth)
    
            // Feature 1: Refresh project list on page load to get newly added projects
            useEffect(() => {
                dispatch(fetchMasters());
            }, [dispatch]);
    
            console.log(employeeDepartment);
            const printRef = useRef();
    
            const [submitBtnLoading, setSubmitBtnLoading] = useState(false);
            const location = useLocation();
            const { indentId } = location.state || {};
    
            console.log("Request ID:", indentId);
    
            // Indent Type State (Material or Job)
            const [indentType, setIndentType] = useState("material"); // "material" or "job"
            
            // Material Category Type State (Computer or Non-Computer)
            // Modified by Aman ( changed to non computer by default  so it calls price list only if user tends to make computer category indent)
            const [materialCategoryType, setMaterialCategoryType] = useState("non-computer");
            
            // const [materialCategoryType, setMaterialCategoryType] = useState("computer");
        //  END
            // Job Codes Master State for Rate Contract dropdown
            const [jobCodesMaster, setJobCodesMaster] = useState([]);
    
            // Updated handleCancel - Now opens cancellation request modal instead of direct cancel
            const handleCancel = () => {
                if (!formData.indentId) {
                    message.warning('No indent selected for cancellation');
                    return;
                }
                setCancellationModalOpen(true);
            };
    
            // Handle successful cancellation request submission
            const handleCancellationSuccess = () => {
                // Reset form after successful request - fields will be re-fetched from employee table
                setFormData({
                    indentorName: '',
                    indentorMobileNo: '',
                    indentorEmailAddress: '',
                    indentorDepartment: '',
                    projectName: "",
                    consignesLocation: "",
                    materialDetails: [{}],
                    jobDetails: [{}],
                    rateContractJobCodes: []
                });
                setSearchDone(false);
                setIndentIdDropdown([]);
    
                // Re-trigger employee details fetch
                window.location.reload(); // Simple way to re-fetch employee details
            };
    
            const [formData, setFormData] = useState({
                indentorName: '', // ✅ Will be auto-filled from employee table via API
                indentorMobileNo: '', // ✅ Will be auto-filled from employee table via API
                indentorEmailAddress: '', // ✅ Will be auto-filled from employee table via API
                indentorDepartment: '', // ✅ Will be auto-filled from employee table via API
                projectName: "",
                projectCode: "", // NEW: Project code for workflow routing
                consignesLocation: "",
                materialDetails: [{}],
                jobDetails: [{}],
                rateContractJobCodes: [],
                // Bug fix fields
                isEditable: true,
                isLockedForTender: false,
                lockedReason: null,
                version: 1,
                parentIndentId: null,
                currentStatus: 'DRAFT',
                currentStage: 'INDENT_CREATION',
                approvalLevel: 0,
                // NEW: Backend status fields for workflow display
                totalApprovalLevels: null,
                isFullyApproved: false,
                statusMessage: null,
                currentApprovalLevel: null,
                pendingWith: null,
                // NEW Dynamic Workflow Fields
                isUnderProject: false, // Project/Non-Project classification
                workflowBranchId: null, // Matched workflow branch (set by backend)
                escalatedToDirector: false, // Whether escalated to Director
                escalationReason: null, // Reason for escalation
                modeOfProcurement: null, // MANDATORY: GEM, OPEN_TENDER, etc.
                roProjectDetermination: null, // RO's project status determination
                roProjectDeterminationRemarks: null // RO's remarks on project determination
            })
    
            const { locationMaster, projectMaster, materialMaster, vendorMaster } = useSelector(state => state.masters)
    
            const [materialMasterState, setMaterialMasterState] = useState(materialMaster)
            const [jobMasterState, setJobMasterState] = useState([])
            const [uomOptions, setUomOptions] = useState([])
    
            const [selectedModeOfProcurement, setSelectedModeOfProcurement] = useState("")
            const [indentIdDropdown, setIndentIdDropdown] = useState([]);
            const [searchDone, setSearchDone] = useState(false);
    
            // ✅ NEW: State for department computer price limit
            const [departmentPriceLimit, setDepartmentPriceLimit] = useState(null);
    
            // ✅ NEW: State for project-specific budget codes
            const [projectBudgetCodes, setProjectBudgetCodes] = useState([]);
            const [allBudgetCodes, setAllBudgetCodes] = useState([]);
    
            // ✅ Fetch consignee location values from LOV system (Form ID: 3 = IndentCreation, Designator: consigneeLocation)
            const { lovValues: consigneeLocationLOV, loading: loadingLocations } = useLOVValues(3, 'consigneeLocation');
    
            // ✅ Use LOV values with correct mapping: display lovDisplayValue, send lovValue to backend
            // ✅ TC_13: Filter out inactive items for regular form dropdowns
            const locationDropdown = consigneeLocationLOV
                .filter(item => item.isActive === true)  // TC_13: Only show active items in forms
                .map((item) => ({
                    label: item.lovDisplayValue,  // Show "Bangalore" in dropdown
                    value: item.lovValue          // Send "BANGALORE" to backend
                }))
    
            const projectDropdown = projectMaster.map((item) => {
                return {
                    label: item.projectNameDescription,
                    value: item.projectCode
                }
            })
    
            const vendorDropdown = vendorMaster.map((item) => {
                return {
                    label: item.vendorName,
                    value: item.vendorId
                }
            })
    
            const budgetCodeDropdown = [] // populated dynamically via fetchBudgetCodesByProject when a project is selected
    
            const [modalOpen, setModalOpen] = useState(false);
    
            // Purchase History Modal State
            const [purchaseHistoryModalOpen, setPurchaseHistoryModalOpen] = useState(false);
            const [selectedMaterialForHistory, setSelectedMaterialForHistory] = useState({
                materialCode: '',
                materialDescription: ''
            });
    
            // Cancellation Request Modal State
            const [cancellationModalOpen, setCancellationModalOpen] = useState(false);
    
            // Handle opening purchase history modal
            const handleOpenPurchaseHistory = (materialCode, materialDescription) => {
                if (!materialCode) {
                    message.warning('Please select a material first');
                    return;
                }
                setSelectedMaterialForHistory({
                    materialCode,
                    materialDescription
                });
                setPurchaseHistoryModalOpen(true);
            };
    
            const printComponentRef = useRef();
    
            const handlePrint = useReactToPrint({
                content: () => printComponentRef.current,
                documentTitle: `Indent - ${formData?.indentId || "Draft"}`
            });
    
            const handleSearchIndentIds = async () => {
                const { searchType, searchValue } = formData;
    
                if (!searchValue || !searchType) {
                    message.warning("Please select search type and enter value.");
                    return;
                }
    
                try {
                    const { data } = await axios.get(`/api/indents/search`, {
                        params: {
                            type: searchType,
                            value: searchValue,
                            indentType: indentType,
                            // materialCategoryType only applies to material indents (job indents don't store this column)
                            materialCategoryType: indentType === "material" ? materialCategoryType : undefined
                        }
                    });
    
                    const indentList = data?.responseData || [];
    
                    const dropdownOptions = indentList.map((item) => ({
    label: item.currentStatus === 'DRAFT'
        ? `${item.indentId}  [DRAFT]`
        : item.indentId,
    value: item.indentId
}));
                    setIndentIdDropdown(dropdownOptions);
    
                    if (dropdownOptions.length === 0) {
                        message.warning("No indent IDs found.");
                    } else {
                        message.success(`${dropdownOptions.length} Please Select Indent Id in Indent Id Drop Down.`);
                    }
                } catch (error) {
                    message.error("Error fetching indent IDs.");
                }
            };
    const [draftBtnLoading, setDraftBtnLoading] = useState(false);

    const handleSaveDraft = async () => {
    const payload = {
        ...formData,
        indentType,
        materialCategoryType: materialCategoryType !== "all" ? materialCategoryType : null,
        fileType: "Indent",
        createdBy: userId,
        employeeDepartment: formData.indentorDepartment,
        materialDetails: indentType === "material" ? formData.materialDetails : null,
        jobDetails: indentType === "job" ? formData.jobDetails : null,
        modeOfProcurement: selectedModeOfProcurement || formData.modeOfProcurement,
        isUnderProject: formData.isUnderProject === true || formData.isUnderProject === "true",
        projectCode: (formData.isUnderProject === true || formData.isUnderProject === "true") ? formData.projectCode : null,
    };
    delete payload.singleAndMultipleJob;

   try {
    setDraftBtnLoading(true);
    let response;

    if (formData?.indentId && formData?.currentStatus === "DRAFT") {
        response = await axios.put(`/api/indents/draft`, payload, {
            params: { indentId: formData.indentId }
        });
        message.success("Draft updated successfully");
    } else if (!formData?.indentId) {
        response = await axios.post(`/api/indents/draft`, payload);
        message.success("Draft saved successfully");
    } else {
        message.warning("This indent is already submitted and cannot be saved as a draft.");
        return;
    }

    const savedData = response?.data?.responseData;
    setFormData(prev => ({
        ...prev,
        indentId: savedData?.indentId,
        currentStatus: "DRAFT"
    }));
    setModalOpen(true);

    } catch (error) {
        message.error(error.response?.data?.responseStatus?.message || "Error saving draft.");
    } finally {
        setDraftBtnLoading(false);
    }
};
            const [versionHistoryOpen, setVersionHistoryOpen] = useState(false);
const [versionHistoryList, setVersionHistoryList] = useState([]);
const [selectedVersionIdx, setSelectedVersionIdx] = useState(0);
        const fetchVersionHistory = async (indentId) => {
    try {
        const { data } = await axios.get(`/api/indents/version-history`, {params:{indentId}});
        const list = data?.responseData || [];
        setVersionHistoryList(list);
        setSelectedVersionIdx(list.length - 1); // default to latest version
        setVersionHistoryOpen(true);
    } catch (error) {
        message.error("Could not load version history.");
    }
};
    
            // ✅ NEW: Fetch department for a given employee name
            const fetchDepartmentByName = async (employeeName) => {
                if (!employeeName || employeeName.trim() === '') {
                    return '';
                }
    
                try {
                    const { data } = await axios.get('/api/employee-department-master/department/by-name', {
                        params: { employeeName: employeeName.trim() }
                    });
    
                    return data?.responseData?.departmentName || '';
                } catch (error) {
                    console.error('Error fetching department:', error);
                    return '';
                }
            };
    
            // ✅ NEW: Fetch department computer price limit
            const fetchDepartmentPriceLimit = async (departmentName) => {
                if (!departmentName || departmentName.trim() === '') {
                    setDepartmentPriceLimit(null);
                    return null;
                }
    
                try {
                    const { data } = await axios.get(`/api/department-computer-price-limit/department/${departmentName.trim()}`);
                    const limit = data?.responseData?.priceLimit || null;
                    setDepartmentPriceLimit(limit);
                    return limit;
                } catch (error) {
                    // No price limit configured for this department
                    console.log('No price limit found for department:', departmentName);
                    setDepartmentPriceLimit(null);
                    return null;
                }
            };
    
            // ✅ NEW: Validate unit price against department limit for computer items
            const validateComputerItemPrice = (materialSubCategory, unitPrice, departmentName) => {
                // Only validate for Computer & Peripherals category
                if (materialSubCategory !== "Computer & Peripherals") {
                    return true;
                }
    
                // If no department, cannot validate
                if (!departmentName) {
                    message.warning('Department not found. Please ensure indentor name is correct.');
                    return false;
                }
    
                // If no price limit configured, allow
                if (!departmentPriceLimit) {
                    return true;
                }
    
                // Validate unit price against limit
                const price = Number(unitPrice);
                const limit = Number(departmentPriceLimit);
    
                if (price > limit) {
                    message.error(`Unit price ₹${price.toLocaleString()} exceeds the department limit of ₹${limit.toLocaleString()} for Computer & Peripherals category.`);
                    return false;
                }
    
                return true;
            };
    
            // Auto-fetch department based on indentor name
            const handleIndentorNameChange = async (indentorName) => {
                if (!indentorName || indentorName.trim() === '') {
                    // Clear department if name is empty
                    setFormData({
                        ...formData,
                        indentorName: indentorName,
                        indentorDepartment: ''
                    });
                    setDepartmentPriceLimit(null);
                    return;
                }
    
                const department = await fetchDepartmentByName(indentorName);
    
                setFormData({
                    ...formData,
                    indentorName: indentorName,
                    indentorDepartment: department
                });
    
                if (!department) {
                    message.warning('No department found for this employee name');
                    setDepartmentPriceLimit(null);
                } else {
                    // ✅ Fetch price limit for the department
                    await fetchDepartmentPriceLimit(department);
                }
            };
    
            // Fetch Job Master Data (for both Job Indent and Rate Contract Job Codes dropdown)
            const fetchJobMaster = async () => {
                try {
                    const { data } = await axios.get("/api/job-master/approved");
                    if (data?.responseData) {
                        setJobMasterState(data.responseData);
                        setJobCodesMaster(data.responseData);
                    }
                } catch (error) {
                    console.error("Error fetching job master:", error);
                    message.error("Failed to load job master data");
                }
            };
    
            // Fetch UOM Master Data
            const fetchUomMaster = async () => {
                try {
                    const { data } = await axios.get("/api/uom-master");
                    if (data?.responseData) {
                        const uomList = data.responseData.map(uom => ({
                            label: uom.uomName,
                            value: uom.uomCode
                        }));
                        setUomOptions(uomList);
                    }
                } catch (error) {
                    console.error("Error fetching UOM master:", error);
                }
            };
    
            const fetchAllBudgetCodes = async () => {
            try {
                const { data } = await axios.get('/api/admin/budget');
                const budgetData = data?.responseData || [];
                const budgetOptions = budgetData.map(budget => ({
                    label: budget.budgetName,
                    value: budget.budgetCode
                }));
                setAllBudgetCodes(budgetOptions);
            } catch (error) {
                console.error('Error fetching all budget codes:', error);
                setAllBudgetCodes([]);
            }
        };
    
    
    
            // ✅ NEW: Fetch budget codes based on selected project
            const fetchBudgetCodesByProject = async (projectCode) => {
                if (!projectCode) {
                    setProjectBudgetCodes([]);
                    return;
                }
    
                try {
                    const { data } = await axios.get(`/api/admin/budget/project/${projectCode}/dropdown`);
                    let budgetData = [];
    
                    if (data?.responseData) {
                        budgetData = data.responseData;
                    } else if (data?.data) {
                        budgetData = data.data;
                    } else if (Array.isArray(data)) {
                        budgetData = data;
                    }
    
                    // const budgetOptions = budgetData.map(budget => ({
                    //     label: `${budget.budgetCode} - ${budget.budgetName || budget.budgetCode}`,
                    //     value: budget.budgetCode
                    // }));
                    const budgetOptions = budgetData.map(budget => ({
            label: budget.budgetName || budget.budgetCode,
            value: budget.budgetCode
        }));
    
                    setProjectBudgetCodes(budgetOptions);
                    console.log(`✅ Loaded ${budgetOptions.length} budget codes for project ${projectCode}`);
                } catch (error) {
                    console.error('Error fetching budget codes for project:', error);
                    setProjectBudgetCodes([]);
                }
            };
    
            // ✅ UPDATED: Auto-fetch employee details (name, department, mobile, email) from employee table
            useEffect(() => {
                const fetchEmployeeDetailsByUserId = async () => {
                    if (!userId) {
                        console.warn('User ID not found. Cannot fetch employee details.');
                        return;
                    }
    
                    try {
                        // Call the new API to get employee details by userId
                        const { data } = await axios.get(`/api/employee-department-master/by-user/${userId}`);
                        const employeeData = data?.responseData;
    
                        if (employeeData) {
                            // Auto-fill all employee-related fields from employee table
                            setFormData(prev => ({
                                ...prev,
                                indentorName: employeeData.employeeName || '',
                                indentorDepartment: employeeData.departmentName || '',
                                indentorMobileNo: employeeData.phoneNumber || '',
                                indentorEmailAddress: employeeData.emailAddress || '',
                            }));
    
                            // Fetch price limit for the department (for computer items validation)
                            if (employeeData.departmentName) {
                                await fetchDepartmentPriceLimit(employeeData.departmentName);
                            }
    
                            console.log('Employee details auto-filled:', employeeData);
                        } else {
                            console.warn('No employee data found for userId:', userId);
                            message.warning('Employee details not found. Please contact administrator.');
                        }
                    } catch (error) {
                        console.error('Error fetching employee details:', error);
    
                        // Handle different error scenarios
                        if (error.response?.status === 404) {
                            message.warning('Employee record not found. Please contact administrator to link your account.');
                        } else {
                            message.error('Failed to load employee details. Using login credentials as fallback.');
    
                            // Fallback to Redux state if API fails
                            setFormData(prev => ({
                                ...prev,
                                indentorName: userName || '',
                                indentorMobileNo: mobileNumber || '',
                                indentorEmailAddress: email || '',
                            }));
                        }
                    }
                };
    
                fetchEmployeeDetailsByUserId();
                fetchJobMaster();
                fetchUomMaster();
                fetchAllBudgetCodes();
            }, [userId]);
    
            // Filter materials based on category type (Computer / Non-Computer)
            // Feature 1 & 3: Filter materials by category using API-fetched data
            // "computer" subcategory = Computer category; ALL other subcategories = Non-Computer
            const getFilteredMaterialMaster = () => {
                if (!materialMasterState || materialMasterState.length === 0) {
                    return [];
                }
    
                const isComputerSubCategory = (item) => {
                    const sub = (item.subCategory || '').toLowerCase();
                    return sub === 'computer' || sub === 'computer & peripherals';
                };
    
                if (materialCategoryType === "computer") {
                    return materialMasterState.filter(item => isComputerSubCategory(item));
                } else if (materialCategoryType === "non-computer") {
                    return materialMasterState.filter(item => !isComputerSubCategory(item));
                }
    
                return materialMasterState;
            };
    
            // Filter jobs based on category type (Computer / Non-Computer)
            // "COMPUTER" subcategory = Computer; all other subcategories = Non-Computer
            const getFilteredJobMaster = () => {
                if (!jobMasterState || jobMasterState.length === 0) {
                    return [];
                }
    
                const isComputerSubCategory = (item) => {
                    const sub = (item.subCategory || '').toLowerCase();
                    return sub === 'computer' || sub === 'computer & peripherals';
                };
    
                if (materialCategoryType === "computer") {
                    return jobMasterState.filter(item => isComputerSubCategory(item));
                } else if (materialCategoryType === "non-computer") {
                    return jobMasterState.filter(item => !isComputerSubCategory(item));
                }
    
                return jobMasterState;
            };
    
            // Get job codes dropdown options for Rate Contract (multiple selection)
            const getJobCodesDropdownOptions = () => {
                return jobCodesMaster.map(job => ({
                    label: `${job.jobCode} - ${job.jobDescription}`,
                    value: job.jobCode
                }));
            };
    
            // Get job dropdown options for Job Indent (filtered by computer/non-computer)
            const getJobDropdownOptions = () => {
                const filteredJobs = getFilteredJobMaster();
                return filteredJobs.map(job => ({
                    label: `${job.jobCode} - ${job.jobDescription}`,
                    value: job.jobCode
                }));
            };
    
            // Material Details Input Fields
            const getMaterialInputFields = () => {
                const filteredMaterials = getFilteredMaterialMaster();
    
                return {
                    heading: "Material Details",
                    addButton: true,
                    name: "materialDetails",
                    children: [
                        {
                            name: "materialCode",
                            label: "Material Code",
                            type: "select",
                            required: true,
                            disabled: !formData.isEditable,
                            options: filteredMaterials.map((item) => {
                                return {
                                    label: item.materialCode + " - " + item.description,
                                    value: item.materialCode
                                }
                            })
                        },
                        {
                            name: "purchaseHistoryButton",
                            label: " ",
                            type: "custom",
                            render: (index) => {
                                const materialCode = formData.materialDetails?.[index]?.materialCode;
                                const materialDescription = formData.materialDetails?.[index]?.materialDescription;
                                return (
                                    <div style={{ marginTop: '30px' }}>
                                        <Button
                                            type="default"
                                            icon={<HistoryOutlined />}
                                            onClick={() => handleOpenPurchaseHistory(materialCode, materialDescription)}
                                            disabled={!materialCode}
                                            size="middle"
                                            style={{ width: '100%' }}
                                        >
                                            Purchase History
                                        </Button>
                                    </div>
                                );
                            }
                        },
                        {
                            name: "materialDescription",
                            label: "Material Description",
                            type: "text",
                            span: 2,
                            required: true,
                            disabled: true
                        },
                        {
                            name: "materialCategory",
                            label: "Material Category",
                            type: "text",
                            required: true,
                            disabled: true
                        },
                        {
                            name: "materialSubCategory",
                            label: "Material Sub Category",
                            type: "text",
                            required: true,
                            disabled: true
                        },
                        {
                            name: "uom",
                            label: "UOM",
                            type: "text",
                            required: true,
                            disabled: true
                        },
                        {
                            name: "quantity",
                            label: "Quantity",
                            type: "text",
                            required: true,
                            disabled: !formData.isEditable
                        },
                        {
                            name: "unitPrice",
                            label: (
                                <span>
                                    Unit Price inclusive of all taxes, duties and free door delivery
                                    {departmentPriceLimit && materialCategoryType === "computer" && (
                                        <span style={{
                                            color: '#1890ff',
                                            fontSize: '12px',
                                            fontWeight: 'normal',
                                            marginLeft: '10px'
                                        }}>
                                            (Dept. Limit: ₹{Number(departmentPriceLimit).toLocaleString()})
                                        </span>
                                    )}
                                </span>
                            ),
                            type: "text",
                            required: true,
                            disabled: !formData.isEditable
                        },
                        {
                            name: "currency",
                            label: "Currency",
                            type: "text",
                            required: true,
                            disabled: true
                        },
                        {
                            name: "conversionRate",
                            label: "Conversion Rate (to INR)",
                            type: "text",
                            required:true,
                            // required: false,
                            shouldShow: (data, index) => {
                                const currency = data.materialDetails?.[index]?.currency;
                                return currency && currency !== "INR";
                            },
                            placeholder: "Enter conversion rate to INR",
                            disabled: !formData.isEditable
                        },
                        {
                            name: "totalPrice",
                            label: "Total Price (INR)",
                            type: "text",
                            disabled: true,
                            dependencies: ["quantity", "unitPrice", "conversionRate"],
                            value: (formData, index) => {
                                const quantity = Number(formData.materialDetails[index]?.quantity) || 0;
                                const unitPrice = Number(formData.materialDetails[index]?.unitPrice) || 0;
                                const currency = formData.materialDetails[index]?.currency;
                                const conversionRate = Number(formData.materialDetails[index]?.conversionRate) || 1;
                                if (currency && currency !== "INR" && conversionRate > 0) {
                                    return (quantity * unitPrice * conversionRate).toFixed(2);
                                }
                                return (quantity * unitPrice).toFixed(2);
                            }
                        },
                        {
                            name: "modeOfProcurement",
                            label: "Mode of Procurement",
                            type: "select",
                            span: 2,
                            options: modeOfProcurementOptions,
                            required: true,
                            disabled: !formData.isEditable
                        },
                        {
            name: "budgetCode",
            label: "Budget Code",
            type: "select",
            required: true,
            value: formData.budgetCode ?? undefined,
            options: formData.isUnderProject
                ? (projectBudgetCodes.length > 0 ? projectBudgetCodes : [])
                : allBudgetCodes,
            placeholder: formData.isUnderProject && !formData.projectName
                ? "Please select a project first"
                : "Select budget code",
            disabled: !formData.isEditable || (formData.isUnderProject && !formData.projectName),
        },
                        // {
                        //     name: "budgetCode",
                        //     label: "Budget Code",
                        //     type: "select",
                        //     // required: true,
                        //     options: projectBudgetCodes.length > 0 ? projectBudgetCodes : budgetCodeDropdown,
                        //     placeholder: formData.isUnderProject && !formData.projectName
                        //         ? "Please select a project first"
                        //         : "Select budget code",
                        //     disabled: !formData.isEditable || (formData.isUnderProject && !formData.projectName),
                        // },
                        {
                            name: "vendorNames",
                            label: NO_VENDOR_MODES.includes(selectedModeOfProcurement)
                                ? "Vendor Names (Not applicable for this mode)"
                                : selectedModeOfProcurement === "LIMITED_TENDER"
                                    ? "Vendor Names (Minimum 4 required)"
                                    : selectedModeOfProcurement === "PROPRIETARY"
                                        ? "Vendor Names (Maximum 1 allowed)"
                                        : "Vendor Names",
                            type: selectedModeOfProcurement === "PROPRIETARY" ? "select" : "multiselect",
                            options: vendorDropdown,
                            span: 2,
                            disabled: !formData.isEditable || !VENDOR_REQUIRED_MODES.includes(selectedModeOfProcurement),
                        }
                    ]
                };
            };
    
            // Job Details Input Fields (for Job Indent type)
            const getJobInputFields = () => {
                return {
                    heading: "Job/Service Details",
                    addButton: true,
                    name: "jobDetails",
                    children: [
                        {
                            name: "jobCode",
                            label: "Job Code",
                            type: "select",
                            required: true,
                            disabled: !formData.isEditable,
                            options: getJobDropdownOptions()
                        },
                        {
                            name: "jobDescription",
                            label: "Job Description",
                            type: "text",
                            span: 2,
                            required: true,
                            disabled: true
                        },
                        {
                            name: "category",
                            label: "Job Category",
                            type: "select",
                            required: true,
                            options: jobCategoryOptions,
                            disabled: true
                        },
                        {
                            name: "subCategory",
                            label: "Job Subcategory",
                            type: "select",
                            required: true,
                            options: jobSubcategoryOptions,
                            disabled: true
                        },
                        {
                            name: "uom",
                            label: "UOM",
                            type: "select",
                            required: true,
                            options: uomOptions,
                            disabled: true
                        },
                        {
                            name: "quantity",
                            label: "Quantity",
                            type: "text",
                            required: true,
                            disabled: !formData.isEditable
                        },
                        {
                            name: "estimatedPrice",
                            label: "Estimated Price",
                            type: "text",
                            required: true,
                            disabled: true
                        },
                        {
                            name: "currency",
                            label: "Currency",
                            type: "select",
                            required: true,
                            options: currencyOptions,
                            disabled: true
                        },
                        {
                            name: "conversionRate",
                            label: "Conversion Rate (to INR)",
                            type: "text",
                            required: true,
                            shouldShow: (data, index) => {
                                const currency = data.jobDetails?.[index]?.currency;
                                return currency && currency !== "INR";
                            },
                            placeholder: "Enter conversion rate to INR",
                            disabled: !formData.isEditable
                        },
                        {
                            name: "totalPrice",
                            label: "Total Price (INR)",
                            type: "text",
                            disabled: true,
                            dependencies: ["quantity", "estimatedPrice", "conversionRate"],
                            value: (formData, index) => {
                                const quantity = Number(formData.jobDetails[index]?.quantity) || 0;
                                const estimatedPrice = Number(formData.jobDetails[index]?.estimatedPrice) || 0;
                                const currency = formData.jobDetails[index]?.currency;
                                const conversionRate = Number(formData.jobDetails[index]?.conversionRate) || 1;
                                if (currency && currency !== "INR" && conversionRate > 0) {
                                    return (quantity * estimatedPrice * conversionRate).toFixed(2);
                                }
                                return (quantity * estimatedPrice).toFixed(2);
                            }
                        },
                        {
                            name: "briefDescription",
                            label: "Brief Description of Job",
                            type: "text",
                            span: 2,
                            disabled: true
                        },
                        {
                            name: "modeOfProcurement",
                            label: "Mode of Procurement",
                            type: "select",
                            span: 2,
                            options: modeOfProcurementOptions,
                            required: true,
                            disabled: !formData.isEditable
                        },
                        {
            name: "budgetCode",
            label: "Budget Code",
            type: "select",
            required: true,
            value: formData.budgetCode ?? undefined,
            options: formData.isUnderProject
                ? (projectBudgetCodes.length > 0 ? projectBudgetCodes : [])
                : allBudgetCodes,
            placeholder: formData.isUnderProject && !formData.projectName
                ? "Please select a project first"
                : "Select budget code",
            disabled: !formData.isEditable || (formData.isUnderProject && !formData.projectName),
        },
                        // {
                        //     name: "budgetCode",
                        //     label: "Budget Code",
                        //     type: "select",
                        //     // required: true,
                        //     options: projectBudgetCodes.length > 0 ? projectBudgetCodes : budgetCodeDropdown,
                        //     placeholder: formData.isUnderProject && !formData.projectName
                        //         ? "Please select a project first"
                        //         : "Select budget code",
                        //     disabled: !formData.isEditable || (formData.isUnderProject && !formData.projectName),
                        // },
                        {
                            name: "vendorNames",
                            label: NO_VENDOR_MODES.includes(selectedModeOfProcurement)
                                ? "Vendor Names (Not applicable for this mode)"
                                : selectedModeOfProcurement === "LIMITED_TENDER"
                                    ? "Vendor Names (Minimum 4 required)"
                                    : selectedModeOfProcurement === "PROPRIETARY"
                                        ? "Vendor Names (Maximum 1 allowed)"
                                        : "Vendor Names",
                            type: selectedModeOfProcurement === "PROPRIETARY" ? "select" : "multiselect",
                            options: vendorDropdown,
                            span: 2,
                            disabled: !formData.isEditable || !VENDOR_REQUIRED_MODES.includes(selectedModeOfProcurement),
                        }
                    ]
                };
            };
    
            // Build input fields dynamically based on indent type
            const inputFields = [
                {
                    heading: "Search Indent",
                    colCnt: 2,
                    fieldList: [
                        {
                            name: "searchValue",
                            label: "Search Value",
                            type: "indentSearch",
                            onSearch: () => handleSearchIndentIds(),
                        },
                    ]
                },
                {
                    heading: "Status",
                    colCnt: 4,
                    fieldList: [
                        ...(searchDone ? [
                            {
                                name: "currentStatus",
                                label: "Current Status",
                                type: "custom",
                                disabled: true,
                                render: () => {
                                    const status = formData.currentStatus;
                                    const isApproved = formData.isFullyApproved || status === 'APPROVED';
                                    const isInProgress = status === 'IN_PROGRESS' || status === 'PENDING_APPROVAL';
    
                                    let displayStatus = status || 'DRAFT';
                                    let statusColor = 'gray'; // Default: DRAFT
    
                                    if (isApproved) {
                                        displayStatus = 'APPROVED';
                                        statusColor = '#52c41a'; // Green
                                    } else if (isInProgress) {
                                        displayStatus = 'IN PROGRESS';
                                        statusColor = '#faad14'; // Orange/Yellow
                                    } else if (status === 'DRAFT') {
                                        displayStatus = 'DRAFT';
                                        statusColor = 'gray'; // Gray
                                    }
    
                                    return (
                                        <div style={{
                                            padding: '8px 12px',
                                            backgroundColor: statusColor === 'gray' ? '#f5f5f5' : statusColor + '15',
                                            borderRadius: '4px',
                                            border: `1px solid ${statusColor}`,
                                            color: statusColor,
                                            fontWeight: 600,
                                            marginTop: '24px'
                                        }}>
                                            {displayStatus}
                                        </div>
                                    );
                                }
                            },
                            {
                                name: "statusMessage",
                                label: "Status Message",
                                type: "custom",
                                disabled: true,
                                render: () => {
                                    const statusMsg = formData.statusMessage;
                                    const isApproved = formData.isFullyApproved || formData.currentStatus === 'APPROVED';
    
                                    if (!statusMsg && !isApproved) return null;
    
                                    const displayMessage = statusMsg || (isApproved ? 'Your indent is finally approved.' : '');
                                    const bgColor = isApproved ? '#f6ffed' : '#e6f7ff';
                                    const borderColor = isApproved ? '#b7eb8f' : '#91d5ff';
                                    const textColor = isApproved ? '#52c41a' : '#1890ff';
    
                                    return displayMessage ? (
                                        <div style={{
                                            padding: '8px 12px',
                                            backgroundColor: bgColor,
                                            borderRadius: '4px',
                                            border: `1px solid ${borderColor}`,
                                            color: textColor,
                                            marginTop: '24px'
                                        }}>
                                            {displayMessage}
                                        </div>
                                    ) : null;
                                }
                            },
                            {
                                name: "approvalLevel",
                                label: "Approval Progress",
                                type: "custom",
                                disabled: true,
                                render: () => {
                                    const level = formData.approvalLevel || 0;
                                    const totalLevels = formData.totalApprovalLevels;
                                    const isApproved = formData.isFullyApproved || formData.currentStatus === 'APPROVED';
                                    const pendingWith = formData.pendingWith;
                                    const status = formData.currentStatus;
    
                                    let displayText = `Level ${level}`;
                                    if (totalLevels) {
                                        displayText = `${level} of ${totalLevels} approvals completed`;
                                    }
                                    if (isApproved && totalLevels) {
                                        displayText = `All ${totalLevels} approvals completed`;
                                    }
    
                                    return (
                                        <div style={{
                                            padding: '8px 12px',
                                            backgroundColor: isApproved ? '#f6ffed' : '#f5f5f5',
                                            borderRadius: '4px',
                                            marginTop: '24px',
                                            border: isApproved ? '1px solid #b7eb8f' : 'none'
                                        }}>
                                            <div>{displayText}</div>
                                            {/* Show pendingWith when IN_PROGRESS */}
                                            {(status === 'IN_PROGRESS' || status === 'PENDING_APPROVAL') && pendingWith && (
                                                <div style={{
                                                    marginTop: '4px',
                                                    fontSize: '12px',
                                                    color: '#faad14'
                                                }}>
                                                    Pending with: {pendingWith}
                                                </div>
                                            )}
                                        </div>
                                    );
                                }
                            },
                            {
                                name: "version",
                                label: "Version",
                                type: "text",
                                disabled: true
                            }
                        ] : [])
                    ]
                },
                {
                    heading: "Indentor Details",
                    colCnt: 4,
                    fieldList: [
                        {
                            name: "indentId",
                            label: "Indent ID",
                            type: "select",
                            options: indentIdDropdown,
                        },
                        {
                            name: "indentorName",
                            label: "Indentor Name",
                            type: "text",
                            required: true,
                            disabled: true, // ✅ Auto-filled from employee table, read-only
                        },
                        {
                            name: "indentorMobileNo",
                            label: "Mobile No",
                            type: "text",
                            required: true,
                            disabled: true, // ✅ Auto-filled from employee table, read-only
                        },
                        {
                            name: "indentorEmailAddress",
                            label: "Email",
                            type: "text",
                            required: true,
                            disabled: true, // ✅ Auto-filled from employee table, read-only
                        },
                        {
                            name: "indentorDepartment",
                            label: "Department",
                            type: "text",
                            required: true,
                            disabled: true, // ✅ Auto-filled from employee table, read-only
                        }
                    ]
                },
                {
                    heading: "Project Classification",
                    colCnt: 4,
                    fieldList: [
                        {
                            name: "isUnderProject",
                            label: "Is Under Project?",
                            type: "select",
                            required: true,
                            disabled: !formData.isEditable,
                            options: [
                                { label: "Yes - Project", value: true },
                                { label: "No - Non-Project", value: false }
                            ],
                            span: 2
                        },
                        {
                            name: "projectName",
                            label: "Project Name",
                            type: "select",
                            options: projectDropdown,
                            required: true,
                            disabled: !formData.isEditable,
                            span: 2,
                            shouldShow: (data) => data.isUnderProject === true,
                             onChange: (value) => {
    const selectedProject = projectMaster.find(p => p.projectCode === value);
    setProjectBudgetCodes([]);
    setFormData(prev => ({
        ...prev,
        projectName: value,
        projectCode: selectedProject?.projectCode || value,
        budgetCode: undefined,
        // Clear budgetCode in every material/job detail row
        materialDetails: (prev.materialDetails || []).map(row => ({ ...row, budgetCode: undefined })),
        jobDetails: (prev.jobDetails || []).map(row => ({ ...row, budgetCode: undefined })),
    }));
        fetchBudgetCodesByProject(value);
    }
                        }
                    ]
                },
                {
                    heading: "Location Details",
                    fieldList: [
                        {
                            name: "consignesLocation",
                            label: "Consignee Location",
                            type: "select",
                            options: locationDropdown,
                            required: true,
                            disabled: !formData.isEditable
                        }
                    ]
                },
                // Conditionally add Material or Job Details based on indent type
                indentType === "material" ? getMaterialInputFields() : getJobInputFields(),
                {
                    heading: (
                        <span>
                            Document Uploads 
                            <span style={{ color: '#ff4d4f', fontSize: '12px', fontWeight: 'normal', marginLeft: '10px' }}>
                                (Maximum upload limit: {MAX_FILE_SIZE_MB}MB per file)
                            </span>
                        </span>
                    ),
                    colCnt: 2,
                    fieldList: [
                        {
                            name: "uploadingPriorApprovalsFileName",
                            label: "Upload Prior Approvals if any",
                            type: "multiImage",
                            disabled: !formData.isEditable
                        },
                        {
                            name: "technicalSpecificationsFileName",
                            label: "Upload Technical Specifications/ Budgetary Quote",
                            type: "multiImage",
                            disabled: !formData.isEditable
                        },
                        {
                            name: "draftEOIOrRFPFileName",
                            label: "Draft EOI/RFP",
                            type: "multiImage",
                            disabled: !formData.isEditable
                        },
                        {
                            name: "quarter",
                            label: "Quarter",
                            type: "select",
                            disabled: !formData.isEditable,
                            options: [
                                { label: "Q1", value: "Q1" },
                                { label: "Q2", value: "Q2" },
                                { label: "Q3", value: "Q3" },
                                { label: "Q4", value: "Q4" }
                            ]
                        },
                        {
                            name: "purpose",
                            label: "Purpose",
                            type: "text",
                            span: 2,
                            required: true,
                            disabled: !formData.isEditable
                        },
                        ...(selectedModeOfProcurement === "PROPRIETARY" || selectedModeOfProcurement === "Proprietary/Single Tender" ? [
                            {
                                name: "reason",
                                label: "Reason",
                                type: "select",
                                span: 2,
                                options: reasonDropdown,
                                required: true
                            },
                            {
                                name: "proprietaryJustification",
                                label: "Proprietary Justification",
                                type: "text",
                                span: 2,
                                required: true
                            }
                        ] : []),
                        ...(["PROPRIETARY", "LIMITED_TENDER", "Proprietary/Single Tender"].includes(selectedModeOfProcurement) ? [
                            {
                                name: "proprietaryAndLimitedDeclaration",
                                label: proprietaryLimitedDeclarationLabel,
                                type: "checkbox",
                                span: 2,
                                required: true
                            }
                        ] : []),
                        {
                            name: "buyBack",
                            type: "checkbox",
                            label: "Buy Back",
                            disabled: !formData.isEditable
                        },
                        ...(formData.buyBack ? [{
                            name: "uploadBuyBackFileNames",
                            label: "Upload Buy Back File",
                            type: "multiImage",
                            required: true
                        }, {
                            name: "modelNumber",
                            label: "Model Number",
                            type: "text",
                            required: true,
                        }, {
                            name: "serialNumber",
                            label: "Serial Number",
                            type: "text",
                            required: true,
                        }, {
                            name: "dateOfPurchase",
                            label: "Date Of Purchase",
                            type: "date",
                            required: true,
                        }, {
                            name: "buyBackAmount",
                            label: "Buy Back Amount",
                            type: "text",
                            required: true,
                        }
                        ] : []),
                        {
                            name: "brandPac",
                            type: "checkbox",
                            label: "Is a Brand PAC?",
                            // required: selectedModeOfProcurement === "Brand PAC",
                            disabled: !formData.isEditable
                        },
                        ...(formData.brandPac ? [{
                            name: "uploadPACOrBrandPACFileName",
                            label: "Upload PAC",
                            type: "multiImage",
                            required: true,
                        }, {
                            name: "brandAndModel",
                            label: "Brand and Model",
                            type: "text",
                            required: true,
                        },
                        {
                            name: "justification",
                            label: "It is known that as per the Rule 144 of GFR, where in the Fundamental principles of public buying states that the description of the subject matter of procurement to the extent practicable should not indicate a requirement for a particular trade mark, trade name or brand. However in the subject requirement, it is required to prefer the above mentioned brand for the following reasons:",
                            type: "text",
                            placeholder: "Declaration",
                            required: true,
                            span: 2
                        }
                        ] : []),
                        {
                            name: "isPreBidMeetingRequired",
                            type: "checkbox",
                            label: "Pre-Bid Meeting Required?",
                            disabled: !formData.isEditable
                        },
                        ...(formData.isPreBidMeetingRequired ? [{
                            name: "preBidMeetingDate",
                            label: "Tentative Meeting Date",
                            type: "date",
                            required: true,
                        }, {
                            name: "preBidMeetingVenue",
                            label: "Tentative Meeting Location",
                            type: "select",
                            required: true,
                            options: locationDropdown,
                        }
                        ] : []),
                        {
                            name: "isItARateContractIndent",
                            type: "checkbox",
                            label: "Is it a Rate Contract Indent",
                            disabled: !formData.isEditable
                        },
                        // Rate Contract fields
                        ...(formData.isItARateContractIndent ? [
                            {
                                name: "estimatedRate",
                                label: "Estimated Rate",
                                type: "text",
                                required: true,
                            },
                            {
                                name: "periodOfContract",
                                label: "Contract Period (Months)",
                                type: "text",
                                required: true,
                            },
                            {
                                name: "rateContractJobCodes",
                                label: "Job Codes",
                                type: "multiselect",
                                required: true,
                                options: getJobCodesDropdownOptions(),
                                span: 2,
                                placeholder: "Select one or more job codes",
                            }
                        ] : []),
                    ].filter(Boolean),
                },
            ]
    
            // useEffect(() => {
            //     if (selectedModeOfProcurement === "Brand PAC") {
            //         setFormData({
            //             ...formData,
            //             brandPac: true
            //         })
            //     }
            //     else {
            //         setFormData({
            //             ...formData,
            //             brandPac: false
            //         })
            //     }
            // }, [selectedModeOfProcurement])
    
            const replaceMaterial = (prevMaterial, newMaterial) => {
                const prevMtlrDtl = materialMaster.find((item) => item.materialCode === prevMaterial.materialCode)
                setMaterialMasterState(prev => {
                    let newMaterialMaster = [...prev]
                    newMaterialMaster = newMaterialMaster.filter((item) => item.materialCode !== newMaterial.materialCode)
                    newMaterialMaster.push(prevMtlrDtl)
                    return newMaterialMaster
                })
            }
    
            // Handle Job Selection for Job Indent
            const handleJobSelect = (job) => {
                const filteredJobs = jobMasterState.filter(item => item.category === job.category && item.jobCode !== job.jobCode);
                setJobMasterState(prev => {
                    const jobToAdd = jobMasterState.find(item => item.jobCode === job.jobCode);
                    if (jobToAdd) {
                        return filteredJobs;
                    }
                    return prev;
                });
            };
    
            const handleChange = (fieldName, value) => {
                console.log("Fieldname, value: ", fieldName, value)
    
                if (fieldName === "indentId") {
                    setFormData({
                        ...formData,
                        indentId: value
                    });
                    handleSearch(value);
                    return;
                }
    
                // Handle indentorName with auto-fetch
                if (fieldName === "indentorName") {
                    handleIndentorNameChange(value);
                    return;
                }
    
                // Handle isUnderProject toggle - clear project when switching to Non-Project
                if (fieldName === "isUnderProject") {
                    const isProject = value === true || value === "true";
                    setFormData({
                        ...formData,
                        isUnderProject: isProject,
                        projectName: isProject ? formData.projectName : "",
                        projectCode: isProject ? formData.projectCode : null,
                        budgetCode: undefined,
        // Clear budgetCode in every material/job detail row
        materialDetails: (formData.materialDetails || []).map(row => ({ ...row, budgetCode: undefined })),
        jobDetails: (formData.jobDetails || []).map(row => ({ ...row, budgetCode: undefined })),
                    });
                    return;
                }
    
                // Handle projectName selection - also set projectCode and fetch budget codes
                if (fieldName === "projectName") {
                    const selectedProject = projectMaster.find(p => p.projectCode === value);
                    setProjectBudgetCodes([]);
                    setFormData({
                        ...formData,
                        projectName: value,
                        projectCode: selectedProject?.projectCode || value,
                         budgetCode: undefined, // undefined reliably clears Ant Design Select
        // Clear budgetCode in every material/job detail row
        materialDetails: (formData.materialDetails || []).map(row => ({ ...row, budgetCode: undefined })),
        jobDetails: (formData.jobDetails || []).map(row => ({ ...row, budgetCode: undefined })),
                    });
                    // Fetch budget codes for the selected project
                    fetchBudgetCodesByProject(value);
                    return;
                }
    
                // Handle rateContractJobCodes
                if (fieldName === "rateContractJobCodes") {
                    setFormData({
                        ...formData,
                        rateContractJobCodes: value
                    });
                    return;
                }
    
                if (typeof fieldName === "string") {
                    setFormData(prev => ({
                        ...prev,
                        [fieldName]: value
                    }))
                    return
                }
    
                const name = fieldName[2]
                const index = fieldName[1]
                const section = fieldName[0]
    
                // Handle Material Details
                if (section === "materialDetails") {
                    if (name === "materialCode") {
                        const prevMaterialCode = formData.materialDetails[index]?.materialCode || null;
                        const material = materialMasterState.find((item) => item.materialCode === value)
                        if (prevMaterialCode) {
                            replaceMaterial(formData.materialDetails[index], material)
                        }
                        else {
                            handleMaterialSelect(material)
                        }
                        const { materialDetails } = formData;
                        materialDetails[index].materialCode = value
                        materialDetails[index].materialDescription = material.description
                        materialDetails[index].materialCategory = material.category
                        materialDetails[index].materialSubCategory = material.subCategory
                        materialDetails[index].uom = material.uom
                        materialDetails[index].quantity = ""
                        materialDetails[index].unitPrice = material.unitPrice
                        // Feature 2: Reset conversionRate when material changes; set default 1 for non-INR
                        materialDetails[index].conversionRate = material.currency && material.currency !== "INR" ? "" : null
                        materialDetails[index].currency = material.currency
    
                        setFormData({
                            ...formData,
                            materialDetails: materialDetails
                        })
                    }
                    else if (name === "modeOfProcurement") {
                        const { materialDetails } = formData;
                        const updatedMaterialDetails = materialDetails.map(item => ({ ...item, modeOfProcurement: value, vendorNames: [] }))
    
                        setSelectedModeOfProcurement(value)
    
                        setFormData({
                            ...formData,
                            materialDetails: updatedMaterialDetails
                        })
                    }
                    else if (name === "quantity" || name === "unitPrice" || name === "conversionRate") {
                        const { materialDetails } = formData;
                        materialDetails[index][name] = value
    
                        // Validate unit price for computer items
                        if (name === "unitPrice") {
                            const materialSubCategory = materialDetails[index].materialSubCategory;
                            const isValid = validateComputerItemPrice(
                                materialSubCategory,
                                value,
                                formData.indentorDepartment
                            );
    
                            if (!isValid) {
                                materialDetails[index][name] = value;
                            }
                        }
    
                        // Feature 2: Calculate totalPrice with conversionRate for non-INR
                        const qty = Number(materialDetails[index].quantity || 0);
                        const price = Number(materialDetails[index].unitPrice || 0);
                        const currency = materialDetails[index].currency;
                        const rate = Number(materialDetails[index].conversionRate || 0);
    
                        if (currency && currency !== "INR" && rate > 0) {
                            materialDetails[index].totalPrice = (qty * price * rate).toFixed(2);
                        } else {
                            materialDetails[index].totalPrice = (qty * price).toFixed(2);
                        }
    
                        setFormData({
                            ...formData,
                            materialDetails: materialDetails
                        })
                    }
                    else {
                        const { materialDetails } = formData;
    
                        if (name === "vendorNames" && (formData.materialDetails[index]?.modeOfProcurement === "PROPRIETARY" || formData.materialDetails[index]?.modeOfProcurement === "Proprietary/Single Tender")) {
                            materialDetails[index][name] = []
                            materialDetails[index][name].push(value)
                        } else {
                            materialDetails[index][name] = value
                        }
    
                        setFormData({
                            ...formData,
                            materialDetails: materialDetails
                        })
                    }
                }
                // Handle Job Details
                else if (section === "jobDetails") {
                    if (name === "modeOfProcurement") {
                        const { jobDetails } = formData;
                        const updatedJobDetails = jobDetails.map(item => ({ ...item, modeOfProcurement: value, vendorNames: [] }));
                        setSelectedModeOfProcurement(value);
                        setFormData({
                            ...formData,
                            jobDetails: updatedJobDetails
                        });
                    }
                    else if (name === "vendorNames") {
                        const { jobDetails } = formData;
                        if (formData.jobDetails[index]?.modeOfProcurement === "PROPRIETARY" || formData.jobDetails[index]?.modeOfProcurement === "Proprietary/Single Tender") {
                            jobDetails[index][name] = [];
                            jobDetails[index][name].push(value);
                        } else {
                            jobDetails[index][name] = value;
                        }
                        setFormData({
                            ...formData,
                            jobDetails: jobDetails
                        });
                    }
                    else if (name === "jobCode") {
                        const job = jobMasterState.find((item) => item.jobCode === value)
                        if (job) {
                            const { jobDetails } = formData;
                            jobDetails[index].jobCode = value
                            jobDetails[index].jobDescription = job.jobDescription
                            jobDetails[index].category = job.category
                            jobDetails[index].subCategory = job.subCategory
                            jobDetails[index].uom = job.uom
                            jobDetails[index].quantity = ""
                            jobDetails[index].estimatedPrice = job.estimatedPriceWithCcy
                            jobDetails[index].currency = job.currency
                            jobDetails[index].conversionRate = job.currency && job.currency !== "INR" ? "" : null
                            jobDetails[index].briefDescription = job.briefDescription
    
                            setFormData({
                                ...formData,
                                jobDetails: jobDetails
                            })
                        }
                    }
                    else if (name === "quantity" || name === "estimatedPrice" || name === "conversionRate") {
                        const { jobDetails } = formData;
                        jobDetails[index][name] = value
                        const qty = Number(jobDetails[index].quantity || 0);
                        const price = Number(jobDetails[index].estimatedPrice || 0);
                        const currency = jobDetails[index].currency;
                        const rate = Number(jobDetails[index].conversionRate || 0);
                        if (currency && currency !== "INR" && rate > 0) {
                            jobDetails[index].totalPrice = (qty * price * rate).toFixed(2);
                        } else {
                            jobDetails[index].totalPrice = (qty * price).toFixed(2);
                        }
                        setFormData({
                            ...formData,
                            jobDetails: jobDetails
                        })
                    }
                    else {
                        const { jobDetails } = formData;
                        jobDetails[index][name] = value
                        setFormData({
                            ...formData,
                            jobDetails: jobDetails
                        })
                    }
                }
            }
    
            const handleMaterialSelect = (material) => {
                const { materialCode, category } = material
                const newMaterialMasterState = materialMasterState.filter((item) => {
                    return item.category === category && item.materialCode !== materialCode
                })
                setMaterialMasterState(newMaterialMasterState)
            }
    
            const handleMaterialDeselect = (index) => {
                const { materialDetails } = formData;
                const material = materialDetails[index]
    
                if (material.materialCode) {
                    if (formData.materialDetails.length === 1) {
                        setMaterialMasterState(materialMaster)
                    }
                    else {
                        const newMaterialDtl = materialMaster.find((item) => item.materialCode === material.materialCode)
                        const newMaterialMasterState = [...materialMasterState, newMaterialDtl]
                        setMaterialMasterState(newMaterialMasterState)
                    }
                }
            }
    
            // Handle Job Deselect
            const handleJobDeselect = (index) => {
                const { jobDetails } = formData;
                const job = jobDetails[index]
    
                if (job.jobCode) {
                    fetchJobMaster();
                }
            }
    
            // ✅ UPDATED: Load department from stored indent data or fetch from employee table
            const handleSearch = async (value) => {
                try {
                    const { data } = await axios.get(`/api/indents/indentData`, {params:{indentId :value}})
                    const responseData = data.responseData || {};
    
                    // Ensure rateContractJobCodes is always an array
                    if (!responseData.rateContractJobCodes) {
                        responseData.rateContractJobCodes = [];
                    }
    
                    // ✅ Try to get department from backend response first (multiple possible field names)
                    let department = responseData.employeeDept || responseData.indentorDepartment || responseData.employeeDepartment;
    
                    // ✅ If backend didn't return department, fetch it from employee table using indentor name
                    if (!department && responseData.indentorName) {
                        department = await fetchDepartmentByName(responseData.indentorName);
                    }
    
                    // Set the department in formData
                    if (department) {
                        responseData.indentorDepartment = department;
    
                        // Fetch price limit for computer items validation
                        await fetchDepartmentPriceLimit(department);
                    }
    
                    // ✅ Sync indent type state so the correct detail section renders (material vs job)
                    if (responseData.indentType) {
                        setIndentType(responseData.indentType);
                    }
    
                    // ✅ Sync material category type state (Computer / Non-Computer)
                    if (responseData.materialCategoryType && responseData.materialCategoryType !== "all") {
                        setMaterialCategoryType(responseData.materialCategoryType);
                    } else if (responseData.indentType === "job" && !responseData.materialCategoryType) {
                        // Job indents without a stored category default to "computer"
                        setMaterialCategoryType("computer");
                    }
    
                    setFormData(responseData);
                    setSearchDone(true);
                        if (responseData.isActive === false) {
                    setFormData(prev => ({ ...prev, isEditable: false }));}
                }
                catch (error) {
                    message.error("Error while fetching indent data.")
                }
            }
    
            useEffect(() => {
                if (indentId) {
                    handleSearch(indentId);
                }
            }, [indentId]);
    
            const onFinish = async () => {
                // Bug Fix 1 & 2: Check if indent can be edited
                if (formData?.indentId) {
                    if (formData.isLockedForTender) {
                        message.error({
                            content: formData.lockedReason || 'Indent is locked as tender has been created',
                            duration: 5
                        });
                        return;
                    }
    
                    if (!formData.isEditable) {
                        // Check if indent is fully approved
                        const isApproved = formData.isFullyApproved || formData.currentStatus === 'APPROVED';
                        const statusMsg = formData.statusMessage;
    
                        if (isApproved) {
                            message.success({
                                content: statusMsg || 'Your indent is finally approved.',
                                duration: 5
                            });
                        } else {
                            message.warning({
                                content: statusMsg || 'Indent is currently in approval workflow. It can only be edited when sent back for revision.',
                                duration: 5
                            });
                        }
                        return;
                    }
                }
    
                if (indentType === "material") {
                    // Validate vendor counts based on mode of procurement
                    if (selectedModeOfProcurement === "LIMITED_TENDER" || selectedModeOfProcurement === "Limited Pre Approved Vendor Tender") {
                        let minFourVendorSelected = true;
    
                        formData.materialDetails.forEach((item) => {
                            if (!item.vendorNames || item.vendorNames.length < 4) {
                                message.error("Limited Tender requires a minimum of 4 vendor names.");
                                minFourVendorSelected = false;
                                return;
                            }
                        });
    
                        if (!minFourVendorSelected) return;
                    }
    
                    if (selectedModeOfProcurement === "PROPRIETARY" || selectedModeOfProcurement === "Proprietary/Single Tender") {
                        let proprietaryInvalid = false;
    
                        formData.materialDetails.forEach((item, index) => {
                            if (!item.vendorNames || item.vendorNames.length > 1) {
                                message.error(`Material ${index + 1}: Proprietary Purchase allows maximum 1 vendor name.`);
                                proprietaryInvalid = true;
                                return;
                            }
                            if (!item.vendorNames || item.vendorNames.length < 1) {
                                message.error(`Material ${index + 1}: Proprietary Purchase allows exact 1 vendor name.`);
                                proprietaryInvalid = true;
                                return;
                            }
                        });
    
                        if (proprietaryInvalid) return;
                    }
    
                    if (NO_VENDOR_MODES.includes(selectedModeOfProcurement)) {
                        let hasVendors = false;
    
                        formData.materialDetails.forEach((item) => {
                            if (item.vendorNames && item.vendorNames.length > 0) {
                                hasVendors = true;
                            }
                        });
    
                        if (hasVendors) {
                            message.error(`Vendor names are not allowed for ${selectedModeOfProcurement.replace(/_/g, ' ')}.`);
                            return;
                        }
                    }
    
                    // ✅ NEW: Validate computer item prices before submission
                    let priceLimitExceeded = false;
                    formData.materialDetails.forEach((item) => {
                        if (item.materialSubCategory === "Computer & Peripherals") {
                            const isValid = validateComputerItemPrice(
                                item.materialSubCategory,
                                item.unitPrice,
                                formData.indentorDepartment
                            );
    
                            if (!isValid) {
                                priceLimitExceeded = true;
                            }
                        }
                    });
    
                    if (priceLimitExceeded) {
                        message.error("Cannot submit indent. One or more computer items exceed the department price limit.");
                        return;
                    }
                }
    
                if (indentType === "job") {
                    // Validate vendor counts based on mode of procurement
                    if (selectedModeOfProcurement === "LIMITED_TENDER" || selectedModeOfProcurement === "Limited Pre Approved Vendor Tender") {
                        let minFourVendorSelected = true;
    
                        formData.jobDetails.forEach((item) => {
                            if (!item.vendorNames || item.vendorNames.length < 4) {
                                message.error("Limited Tender requires a minimum of 4 vendor names.");
                                minFourVendorSelected = false;
                            }
                        });
    
                        if (!minFourVendorSelected) return;
                    }
    
                    if (selectedModeOfProcurement === "PROPRIETARY" || selectedModeOfProcurement === "Proprietary/Single Tender") {
                        let proprietaryInvalid = false;
    
                        formData.jobDetails.forEach((item, index) => {
                            if (!item.vendorNames || item.vendorNames.length > 1) {
                                message.error(`Job ${index + 1}: Proprietary Purchase allows maximum 1 vendor name.`);
                                proprietaryInvalid = true;
                            }
                        });
    
                        if (proprietaryInvalid) return;
                    }
    
                    if (NO_VENDOR_MODES.includes(selectedModeOfProcurement)) {
                        let hasVendors = false;
    
                        formData.jobDetails.forEach((item) => {
                            if (item.vendorNames && item.vendorNames.length > 0) {
                                hasVendors = true;
                            }
                        });
    
                        if (hasVendors) {
                            message.error(`Vendor names are not allowed for ${selectedModeOfProcurement.replace(/_/g, ' ')}.`);
                            return;
                        }
                    }
                }
    
                // Validate rate contract job codes
                if (formData.isItARateContractIndent) {
                    if (!formData.rateContractJobCodes || formData.rateContractJobCodes.length === 0) {
                        message.error("Please select at least one job code for Rate Contract Indent.");
                        return;
                    }
                }
    
                const payload = {
                    ...formData,
                    indentType: indentType,
                    materialCategoryType: materialCategoryType !== "all" ? materialCategoryType : null,
                    fileType: "Indent",
                    uploadBuyBackFileNames: formData.buyBack ? formData.uploadBuyBackFileNames : null,
                    uploadPACOrBrandPACFileName: formData.brandPac ? formData.uploadPACOrBrandPACFileName : null,
                    brandAndModel: formData.brandPac ? formData.brandAndModel : null,
                    preBidMeetingDate: formData.isPreBidMeetingRequired ? formData.preBidMeetingDate : null,
                    preBidMeetingVenue: formData.isPreBidMeetingRequired ? formData.preBidMeetingVenue : null,
                    estimatedRate: formData.isItARateContractIndent ? formData.estimatedRate : null,
                    periodOfContract: formData.isItARateContractIndent ? formData.periodOfContract : null,
                    rateContractJobCodes: formData.isItARateContractIndent ? formData.rateContractJobCodes : null,
                    justification: formData.brandPac ? formData.justification : null,
                    reason: (selectedModeOfProcurement === "PROPRIETARY" || selectedModeOfProcurement === "Proprietary/Single Tender") ? formData.reason : null,
                    proprietaryJustification: (selectedModeOfProcurement === "PROPRIETARY" || selectedModeOfProcurement === "Proprietary/Single Tender") ? formData.proprietaryJustification : null,
                    createdBy: userId,
                    employeeDepartment: formData.indentorDepartment, // Use the auto-fetched department
                    materialDetails: indentType === "material" ? formData.materialDetails : null,
                    jobDetails: indentType === "job" ? formData.jobDetails : null,
                    // Dynamic Workflow Fields - sent in format backend branch matching expects
                    isUnderProject: formData.isUnderProject === true || formData.isUnderProject === "true" ? true : false,
                    projectBased: formData.isUnderProject === true || formData.isUnderProject === "true" ? true : false,
                    projectCode: (formData.isUnderProject === true || formData.isUnderProject === "true") ? formData.projectCode : null,
                    modeOfProcurement: selectedModeOfProcurement || formData.modeOfProcurement,
                    // Send fields in format matching branch conditionConfig keys
                    // materialCategoryType "computer" → "COMPUTER", "non-computer" → "NON_COMPUTER", "all"/null → null
                    materialCategory: materialCategoryType === "computer" ? "COMPUTER"
                        : materialCategoryType === "non-computer" ? "NON_COMPUTER"
                        : null,
                    location: formData.consignesLocation,
                };
    
                // Remove old field that's no longer used
                delete payload.singleAndMultipleJob;
    
                // try {
                //     setSubmitBtnLoading(true);
                //     let response;
    
                //     if (formData?.indentId) {
                //         response = await axios.put(`/api/indents` ,payload, {params:{indentId : formData.indentId}} );
                //         const newIndentId = response?.data?.responseData?.indentId; // e.g. IND1111/2
                //         setFormData(prev => ({ ...prev, indentId: newIndentId }));
                //         message.success("Indent updated successfully");
                //         navigate("/queue");
                //     } else {
                //         response = await axios.post("/api/indents", payload);
                //     }
    
                //     setFormData({
                //         ...formData,
                //         indentId: response?.data?.responseData?.indentId
                //     });
    
                //     setModalOpen(true);
                try {
    setSubmitBtnLoading(true);
    let response;

    if (formData?.indentId && formData?.currentStatus === "DRAFT") {
        // Submit a saved draft — runs full validation + budget + workflow on backend
        response = await axios.post(`/api/indents/draft/submit`, payload, {
            params: { indentId: formData.indentId }
        });
        message.success("Indent submitted successfully");
        navigate("/queue");

    } else if (formData?.indentId && formData?.currentStatus !== "DRAFT") {
        // Version update on an already-submitted indent
        response = await axios.put(`/api/indents`, payload, {
            params: { indentId: formData.indentId }
        });
        const newIndentId = response?.data?.responseData?.indentId;
        setFormData(prev => ({ ...prev, indentId: newIndentId }));
        message.success("Indent updated successfully");
        navigate("/queue");

    } else {
    // Fresh submit with no prior draft
    response = await axios.post("/api/indents", payload);
    setFormData({
        ...formData,
        indentId: response?.data?.responseData?.indentId,
        currentStatus: "SUBMITTED"   // ← clears the DRAFT banner
    });
    setModalOpen(true);
}
                } catch (error) {
                    // Handle file size error specifically
                    if (error.response?.status === 413 ||
                        error.response?.data?.responseStatus?.errorType === "FILE_TOO_LARGE") {
                        message.error(`File size too large. Maximum ${MAX_FILE_SIZE_MB}MB per file allowed.`);
                    }
                    // Bug Fix: Handle edit validation errors from backend
                    else if (error.response?.status === 400) {
                        const errorMessage = error.response?.data?.responseStatus?.message || error.response?.data?.message;
    
                        if (errorMessage?.includes("locked for editing")) {
                            message.error({
                                content: "Cannot edit: Tender already created for this indent",
                                duration: 5
                            });
                        } else if (errorMessage?.includes("not editable")) {
                            message.error({
                                content: "Cannot edit: Indent is in approval workflow",
                                duration: 5
                            });
                        } else if (errorMessage?.includes("reporting officer") || errorMessage?.includes("Reporting Officer")) {
                            message.error({
                                content: errorMessage,
                                duration: 8
                            });
                        } else {
                            message.error(errorMessage || "Validation error occurred");
                        }
                    }
                    else {
                        message.error(error.response?.data?.responseStatus?.message || error.message || "Error submitting indent.");
                    }
                } finally {
                    setSubmitBtnLoading(false);
                }
            };
    
            const addMaterialFunc = () => {
                if (indentType === "material") {
                    setFormData({
                        ...formData,
                        materialDetails: [...formData.materialDetails, {}]
                    })
                } else {
                    setFormData({
                        ...formData,
                        jobDetails: [...formData.jobDetails, {}]
                    })
                }
            }
    
            const additionalFunc = {
                "addMaterialSection": addMaterialFunc,
                "materialDeselect": (index) => indentType === "material" ? handleMaterialDeselect(index) : handleJobDeselect(index)
            }
    
            // Handle Indent Type Change
            const handleIndentTypeChange = (value) => {
                setIndentType(value);
                // Keep materialCategoryType as-is so the Computer/Non-Computer selection
                // applies to both material and job/service intents for workflow routing
                setFormData({
                    ...formData,
                    materialDetails: value === "material" ? [{}] : formData.materialDetails,
                    jobDetails: value === "job" ? [{}] : formData.jobDetails
                });
            };
    
            // Handle Material Category Type Change
            // Feature 1: Fetch materials by category from API (includes only SPO-approved materials)
            const fetchMaterialsByCategory = async (categoryType) => {
                try {
                    const response = await axios.get('/api/material-master/materialSearch', {
                        params: { keyword: '', materialCategoryType: categoryType || undefined }
                    });
                    const data = response.data?.responseData || response.data || [];
                    if (Array.isArray(data) && data.length > 0) {
                        setMaterialMasterState(data);
                    } else {
                        // Fallback to Redux master filtered locally
                        setMaterialMasterState(materialMaster);
                    }
                } catch (error) {
                    console.error('Error fetching materials by category:', error);
                    // Fallback to Redux master
                    setMaterialMasterState(materialMaster);
                }
            };
    
            const handleMaterialCategoryTypeChange = (value) => {
                setMaterialCategoryType(value);
    
                setFormData({
                    ...formData,
                    materialDetails: [{}],
                    jobDetails: [{}] 
                });
    
                // Fetch fresh materials from API filtered by category
                fetchMaterialsByCategory(value);
            };
    
            // Fetch materials on initial load based on default category (computer)
            useEffect(() => {
                if (indentType === "material") {
                    fetchMaterialsByCategory(materialCategoryType);
                }
            }, []); // eslint-disable-line react-hooks/exhaustive-deps
    
            return (
                <Card className='a4-container' ref={printRef}>
                    <Heading title="Indent Creation" />
    
                    {/* Bug Fix: Show lock status, approval status, and version information */}
                    {formData?.indentId && (
                        <Space direction="vertical" style={{ width: '100%', marginTop: '16px', marginBottom: '16px' }}>

                        {formData?.currentStatus === "DRAFT" && (
    <Alert
        message="Saved Draft"
        description="This indent is a saved draft. It has not been submitted and is not visible to any approver."
        type="warning"
        showIcon
        closable={false}
    />
)}
                            {/* SUCCESS Banner: Show when indent is fully approved */}
                            {formData.isFullyApproved && (
                                <Alert
                                    message="Indent Approved"
                                    description={formData.statusMessage || "Your indent is finally approved."}
                                    type="success"
                                    showIcon
                                    closable={false}
                                />
                            )}
                            {/* WARNING Banner: Show when locked for tender */}
                            {formData.isLockedForTender && (
                                <Alert
                                    message="Indent Locked"
                                    description={formData.lockedReason || "This indent is locked for editing as tender has been created"}
                                    type="warning"
                                    showIcon
                                    closable={false}
                                />
                            )}
                            {/* INFO Banner: Show when not editable AND not fully approved (in approval workflow) */}
                            {!formData.isEditable && !formData.isLockedForTender && !formData.isFullyApproved && (
                                <Alert
                                    message="Indent Not Editable"
                                    description={formData.statusMessage || "This indent is currently in approval workflow. It can only be edited when sent back for revision."}
                                    type="info"
                                    showIcon
                                    closable={false}
                                />
                            )}
                            {formData.isActive === false && (
            <Alert
                message={`Viewing Old Version (V${formData.version})`}
                description={`This is a superseded version. Load the latest version to make changes.`}
                type="warning"
                showIcon
                closable={false}
            />
        )}
                            {formData.version > 1 && (
                                <Alert
                                    message={`Version ${formData.version}`}
                                    description={`This indent has been revised ${formData.version - 1} time(s)`}
                                    type="info"
                                    showIcon
                                    closable={false}
                                />
                            )}  
                            {formData?.indentId && (
            <Button
                icon={<HistoryOutlined />}
                onClick={() => fetchVersionHistory(formData.indentId)}
                style={{ marginTop: '4px' }}
            >
                View Version History
            </Button>
        )}
                        </Space>
                    )}
    
                    {/* Indent Type and Category Selection */}
                    <Row gutter={16} style={{ marginBottom: '20px', marginTop: '16px' }}>
                        <Col span={6}>
                            <div style={{ marginBottom: '8px' }}>
                                <label style={{ 
                                    fontSize: '14px', 
                                    color: 'rgba(0, 0, 0, 0.85)',
                                    fontWeight: 'normal'
                                }}>
                                    <span style={{ color: '#ff4d4f', marginRight: '4px' }}>*</span>
                                    Indent Type
                                </label>
                            </div>
                            <Select
                                value={indentType}
                                onChange={handleIndentTypeChange}
                                style={{ width: '100%' }}
                                placeholder="Select Indent Type"
                            >
                                <Option value="material">Material Indent</Option>
                                <Option value="job">Job/Service Indent</Option>
                            </Select>
                        </Col>
                        
                        <Col span={6}>
                            <div style={{ marginBottom: '8px' }}>
                                <label style={{
                                    fontSize: '14px',
                                    color: 'rgba(0, 0, 0, 0.85)',
                                    fontWeight: 'normal'
                                }}>
                                    Material Category
                                </label>
                            </div>
                            <Select
                                value={materialCategoryType}
                                onChange={handleMaterialCategoryTypeChange}
                                style={{ width: '100%' }}
                                placeholder="Select Category"
                            >
                                <Option value="computer">Computer</Option>
                                <Option value="non-computer">Non-Computer</Option>
                            </Select>
                        </Col>
                    </Row>
    
                    <CustomForm formData={formData} onFinish={onFinish}>
                        {renderFormFields(inputFields, handleChange, formData, "", null, setFormData, handleSearch, additionalFunc)}
                        {/* <ButtonContainer
                            onFinish={onFinish}
                            formData={formData}
                            draftDataName="indentDraft"
                            submitBtnLoading={submitBtnLoading}
                            submitBtnEnabled
                            printBtnEnabled
                            draftBtnEnabled
                            handlePrint={handlePrint}
                            showCancel={searchDone}
                            onCancel={handleCancel}
                            cancelButtonText="Request Cancellation"
                        /> */}
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
    showCancel={searchDone}
    onCancel={handleCancel}
    cancelButtonText="Request Cancellation"
/>
                    </CustomForm>
                    <CustomModal isOpen={modalOpen} setIsOpen={setModalOpen} title="Indent" processNo={formData?.indentId} />
                    <PurchaseHistoryModal
                        open={purchaseHistoryModalOpen}
                        onClose={() => setPurchaseHistoryModalOpen(false)}
                        materialCode={selectedMaterialForHistory.materialCode}
                        materialDescription={selectedMaterialForHistory.materialDescription}
                    />
                    <IndentCancellationModal
                        open={cancellationModalOpen}
                        onClose={() => setCancellationModalOpen(false)}
                        indentId={formData.indentId}
                        requestedBy={userId}
                        requestedByName={userName}
                        onSuccess={handleCancellationSuccess}
                    />
                 <Modal
    open={versionHistoryOpen}
    onCancel={() => setVersionHistoryOpen(false)}
    title="Indent Version History"
    footer={null}
    width={900}
    destroyOnClose
>
    {(() => {
        // Sort ASC so index 0 = V1, last index = latest
        const sorted = [...versionHistoryList].sort((a, b) => (a.version || 0) - (b.version || 0));
        const selIdx = Math.max(0, Math.min(selectedVersionIdx, sorted.length - 1));
        const curr = sorted[selIdx];
        const prev = selIdx > 0 ? sorted[selIdx - 1] : null;

        if (!curr) return <div style={{ padding: '24px', textAlign: 'center', color: '#999' }}>No versions found.</div>;

        // Fields to diff — header level
        const HEADER_FIELDS = [
            { key: 'indentorName',                    label: 'Indentor' },
            { key: 'indentorMobileNo',                label: 'Mobile No.' },
            { key: 'indentorEmailAddress',            label: 'Email' },
            { key: 'modeOfProcurement',               label: 'Mode of Procurement' },
            { key: 'projectName',                     label: 'Project' },
            { key: 'projectCode',                     label: 'Project Code' },
            { key: 'isUnderProject',                  label: 'Under Project' },
            { key: 'consignesLocation',               label: 'Consignee Location' },
            { key: 'indentType',                      label: 'Indent Type' },
            { key: 'materialCategoryType',            label: 'Material Category' },
            { key: 'purpose',                         label: 'Purpose' },
            { key: 'justification',                   label: 'Justification' },
            { key: 'quarter',                         label: 'Quarter' },
            { key: 'isPreBidMeetingRequired',         label: 'Pre-Bid Meeting Required' },
            { key: 'preBidMeetingDate',               label: 'Pre-Bid Meeting Date' },
            { key: 'preBidMeetingVenue',              label: 'Pre-Bid Meeting Venue' },
            { key: 'isItARateContractIndent',         label: 'Rate Contract Indent' },
            { key: 'estimatedRate',                   label: 'Estimated Rate' },
            { key: 'periodOfContract',                label: 'Period of Contract' },
            { key: 'rateContractJobCodes',            label: 'Rate Contract Job Codes' },
            { key: 'brandPac',                        label: 'Brand PAC' },
            { key: 'brandAndModel',                   label: 'Brand & Model' },
            { key: 'proprietaryJustification',        label: 'Proprietary Justification' },
            { key: 'proprietaryAndLimitedDeclaration',label: 'Proprietary Declaration' },
            { key: 'reason',                          label: 'Reason' },
            { key: 'buyBack',                         label: 'Buy Back' },
            { key: 'buyBackAmount',                   label: 'Buy Back Amount' },
            { key: 'serialNumber',                    label: 'Serial Number' },
            { key: 'modelNumber',                     label: 'Model Number' },
            { key: 'technicalSpecificationsFileName', label: 'Technical Specs File' },
            { key: 'uploadingPriorApprovalsFileName', label: 'Prior Approvals File' },
            { key: 'draftEOIOrRFPFileName',           label: 'Draft EOI/RFP File' },
            { key: 'uploadPACOrBrandPACFileName',     label: 'PAC/Brand PAC File' },
            { key: 'uploadBuyBackFileNames',          label: 'Buy Back File' },
        ];

        // Fields to diff — material line items
        const MAT_FIELDS = [
            { key: 'materialCode',        label: 'Material Code' },
            { key: 'materialDescription', label: 'Description' },
            { key: 'quantity',            label: 'Qty' },
            { key: 'unitPrice',           label: 'Unit Price' },
            { key: 'totalPrice',          label: 'Total Price' },
            { key: 'uom',                 label: 'UOM' },
            { key: 'budgetCode',          label: 'Budget Code' },
            { key: 'currency',            label: 'Currency' },
            { key: 'conversionRate',      label: 'Conversion Rate' },
            { key: 'modeOfProcurement',   label: 'Mode of Procurement' },
            { key: 'materialCategory',    label: 'Category' },
            { key: 'materialSubCategory', label: 'Sub-Category' },
        ];

        // Fields to diff — job line items
        const JOB_FIELDS = [
            { key: 'jobCode',           label: 'Job Code' },
            { key: 'jobDescription',    label: 'Description' },
            { key: 'briefDescription',  label: 'Brief Description' },
            { key: 'quantity',          label: 'Qty' },
            { key: 'estimatedPrice',    label: 'Est. Price' },
            { key: 'totalPrice',        label: 'Total Price' },
            { key: 'uom',               label: 'UOM' },
            { key: 'budgetCode',        label: 'Budget Code' },
            { key: 'currency',          label: 'Currency' },
            { key: 'conversionRate',    label: 'Conversion Rate' },
            { key: 'category',          label: 'Category' },
            { key: 'subCategory',       label: 'Sub-Category' },
            { key: 'origin',            label: 'Origin' },
            { key: 'modeOfProcurement', label: 'Mode of Procurement' },
        ];

        const isJob = (curr.indentType || 'material').toLowerCase() === 'job';
        const lineLabel = isJob ? 'Job' : 'Material';
        const lineFields = isJob ? JOB_FIELDS : MAT_FIELDS;
        const descKey = isJob ? 'jobDescription' : 'materialDescription';
        const prevLines = isJob ? (prev?.jobDetails || []) : (prev?.materialDetails || []);
        const currLines = isJob ? (curr.jobDetails || []) : (curr.materialDetails || []);

        // Compute header diffs
        const headerDiffs = prev
            ? HEADER_FIELDS
                .filter(f => String(prev[f.key] ?? '') !== String(curr[f.key] ?? ''))
                .map(f => ({ ...f, oldVal: prev[f.key], newVal: curr[f.key] }))
            : [];

        // Compute line item diffs (matched by position/index)
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
                const changed = lineFields
                    .filter(f => String(p[f.key] ?? '') !== String(c[f.key] ?? ''))
                    .map(f => ({ ...f, oldVal: p[f.key], newVal: c[f.key] }));
                if (changed.length) lineDiffs.push({ idx: i, type: 'modified', changes: changed, label: c[descKey] || `Item ${i + 1}` });
            }
        }

        // Total value change
        const prevTotal = prev != null ? Number(prev.totalPriceOfAllMaterials || 0) : null;
        const currTotal = Number(curr.totalPriceOfAllMaterials || 0);
        const totalChanged = prev && prevTotal !== currTotal;
        const totalChanges = headerDiffs.length + lineDiffs.length + (totalChanged ? 1 : 0);

        const fmtCurrency = val => val != null ? `₹ ${Number(val).toLocaleString('en-IN', { maximumFractionDigits: 2 })}` : '—';
        const fmtVal = val => (val == null || val === '') ? '—' : String(val);

        return (
            <div style={{ display: 'flex', minHeight: '450px' }}>

                {/* ── Left: version selector ── */}
                <div style={{ width: '190px', flexShrink: 0, borderRight: '1px solid #f0f0f0' }}>
                    <div style={{ padding: '8px 12px', fontWeight: 600, fontSize: '11px', color: '#aaa', letterSpacing: '1px', borderBottom: '1px solid #f0f0f0' }}>
                        VERSIONS
                    </div>
                    {sorted.map((v, idx) => {
                        const isSel = idx === selIdx;
                        return (
                            <div
                                key={v.indentId}
                                onClick={() => setSelectedVersionIdx(idx)}
                                style={{
                                    padding: '10px 14px',
                                    cursor: 'pointer',
                                    borderLeft: isSel ? '3px solid #1890ff' : '3px solid transparent',
                                    background: isSel ? '#e6f7ff' : 'transparent',
                                    borderBottom: '1px solid #f5f5f5',
                                }}
                            >
                                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                                    <span style={{ fontWeight: 600, fontSize: '14px' }}>V{v.version}</span>
                                    {v.isActive
                                        ? <Tag color="green"  style={{ fontSize: '10px', margin: 0 }}>Active</Tag>
                                        : <Tag color="default" style={{ fontSize: '10px', margin: 0 }}>Old</Tag>
                                    }
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
                                <span style={{ fontWeight: 600, color: '#888' }}>V{prev.version}</span>
                                <span style={{ color: '#ccc' }}>→</span>
                                <span style={{ fontWeight: 600, color: '#1890ff' }}>V{curr.version}</span>
                                {totalChanges === 0
                                    ? <Tag>No changes</Tag>
                                    : <Tag color="blue">{totalChanges} change{totalChanges !== 1 ? 's' : ''}</Tag>
                                }
                            </>
                        ) : (
                            <span style={{ fontWeight: 600, color: '#52c41a' }}>V{curr.version} — Initial Version</span>
                        )}
                        <Button
                            type="link"
                            size="small"
                            style={{ marginLeft: 'auto', padding: 0 }}
                            onClick={() => { handleSearch(curr.indentId); setVersionHistoryOpen(false); }}
                        >
                            Load {curr.indentId} ↗
                        </Button>
                    </div>

                    {/* Initial version — no diff to show */}
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

                    {/* Has previous but nothing changed */}
                    {prev && totalChanges === 0 && (
                        <div style={{ padding: '24px 0', color: '#888', fontSize: '13px' }}>
                            No field-level changes detected compared to V{prev.version}.
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
                                        <span style={{ fontSize: '12px', color: '#888', flex: 1 }}>Total Value</span>
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
                                    <div style={{ fontWeight: 600, fontSize: '11px', color: '#aaa', letterSpacing: '1px', marginBottom: '8px' }}>{lineLabel.toUpperCase()} DETAILS</div>
                                    {lineDiffs.map((diff, i) => {
                                        const borderColor = diff.type === 'added' ? '#b7eb8f' : diff.type === 'removed' ? '#ffa39e' : '#ffe58f';
                                        const headerBg   = diff.type === 'added' ? '#f6ffed' : diff.type === 'removed' ? '#fff1f0' : '#fffbe6';
                                        const headerColor = diff.type === 'added' ? '#389e0d' : diff.type === 'removed' ? '#cf1322' : '#d48806';
                                        const prefix = diff.type === 'added' ? '+ ' : diff.type === 'removed' ? '− ' : '✎ ';
                                        return (
                                            <div key={i} style={{ marginBottom: '8px', borderRadius: '6px', overflow: 'hidden', border: `1px solid ${borderColor}` }}>
                                                <div style={{ padding: '7px 12px', fontSize: '12px', fontWeight: 600, background: headerBg, color: headerColor }}>
                                                    {prefix}{lineLabel} {diff.idx + 1}
                                                    {diff.type === 'modified' && diff.label ? ` — ${diff.label}` : ''}
                                                    {diff.type !== 'modified' && diff.item?.[descKey] ? ` — ${diff.item[descKey]}` : ''}
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
                                                        : lineFields.map(f => (
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
                        <PrintFormate ref={printComponentRef} data={formData} />
                    </div>
                </Card>
            )
        }
    
        export default Indent1