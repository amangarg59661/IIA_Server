// import React, { useRef, useState, useEffect } from 'react';
// import { Button, Card, Form, Input, Select, DatePicker, message } from 'antd';
// import { useReactToPrint } from 'react-to-print';
// import axios from 'axios';
// import { useSelector } from 'react-redux';
// import Heading from '../../../components/DKG_Heading';
// import CustomForm from '../../../components/DKG_CustomForm';
// import { renderFormFields } from '../../../utils/CommonFunctions';
// import ButtonContainer from '../../../components/ButtonContainer';
// import CustomModal from '../../../components/CustomModal';
// import { CpDetails } from './InputFields';
// import { useMemo } from 'react';
// import { useLOVValues } from '../../../hooks/useLOVValues';


// const { Option } = Select;


// const ContingencyPurchase = () => {
//   const printRef = useRef();
//   const [form] = Form.useForm();
//   const [modalOpen, setModalOpen] = useState(false);
//   const [submitBtnLoading, setSubmitBtnLoading] = useState(false);
//   const [generatedCpId, setGeneratedCpId] = useState('');
//   const [isPrintEnabled, setIsPrintEnabled] = useState(false);

//   const [employees, setEmployees] = useState([]);

//   // Redux selectors
//   const auth = useSelector((state) => state.auth);
//   const actionPerformer = auth.userId;

//   // Data states
//   const [projects, setProjects] = useState([]);
//   const [vendors, setVendors] = useState([]);
//   const [materialDetailsMap, setMaterialDetailsMap] = useState({});
//   const [materialOptions, setMaterialOptions] = useState([]);
//   const [materialDescOptions, setMaterialDescOptions] = useState([]);
//    const [cpIdDropdown, setCpIdDropdown] = useState([]);

//   // Form data state
//   const [formData, setFormData] = useState({ materialDetails: [{}] });

//   // ✅ Fetch dropdown values from LOV system (Form ID: 2 - ContingencyPurchase)
//   const { lovValues: gstPercentageLOV, loading: loadingGst } = useLOVValues(2, 'gstPercentage');
//   const { lovValues: paymentToLOV, loading: loadingPaymentTo } = useLOVValues(2, 'paymentTo');
//   const { lovValues: budgetCodeLOV, loading: loadingBudgetCode } = useLOVValues(2, 'budgetCode');
//   const { lovValues: materialCategoryLOV, loading: loadingMaterialCategory } = useLOVValues(2, 'materialCategory');
//   const { lovValues: materialSubCategoryLOV, loading: loadingMaterialSubCategory } = useLOVValues(2, 'materialSubCategory');
//   const { lovValues: countryOfOriginLOV, loading: loadingCountryOfOrigin } = useLOVValues(2, 'countryOfOrigin');

//   // --- Dynamic Field Population ---
//   const populateDropdowns = async () => {
//     try {
//       const [projectResponse, materialResponse] = await Promise.all([
//         axios.get('/api/project-master'),
//         axios.get('/api/material-master'),
//       ]);
//       const vendorResponse = await axios.get('/api/vendor-master');

//       // Format projects
//       const formattedProjects = (projectResponse.data?.responseData || []).map(project => ({
//         label: project.projectNameDescription,
//         value: project.projectCode,
//       }));
//       setProjects(formattedProjects);

//       // Format vendors
//       const formattedVendors = (vendorResponse.data?.responseData || []).map(vendor => ({
//         label: vendor.vendorName,
//         value: vendor.vendorName,
//       }));
//       setVendors(formattedVendors);

//       const employeeResponse = await axios.get('/api/employee-department-master');
//       const formattedEmployees = (employeeResponse.data?.responseData || []).map(employee => ({
//       label: employee.employeeName,
//       value: employee.employeeId,
//       }));
//       setEmployees(formattedEmployees);
//       ;


     

//       // Format materials
//       const materials = materialResponse.data?.responseData || [];
//       const materialMap = {};
//       const descMap = {};

//       materials.forEach(material => {
//         materialMap[material.materialCode] = {
//           materialDescription: material.description,
//           uom: material.uom,
//           unitPrice: material.unitPrice,
//           currency: material.currency,
//           materialCategory: material.category,
//           materialSubCategory: material.subCategory
//         };

//         descMap[material.description] = {
//           materialCode: material.materialCode,
//           uom: material.uom,
//           unitPrice: material.unitPrice,
//           currency: material.currency,
//           materialCategory: material.category,
//           materialSubCategory: material.subCategory
//         };
//       });

//       setMaterialOptions(materials.map(m => ({ label: m.materialCode, value: m.materialCode })));
//       setMaterialDescOptions(materials.map(m => ({ label: m.description, value: m.description })));
//       setMaterialDetailsMap({ ...materialMap, ...descMap });
//     } catch (error) {
//       console.error('Error fetching data:', error);
//       message.error('Failed to load dropdown data');
//     }
//   };
//   //  Calculates total price with GST
// const calculateTotalPrice = (quantity, unitPrice, gst = 0) => {
//   const base = (quantity || 0) * (unitPrice || 0);
//   const gstAmount = (base * gst) / 100;
//   return base + gstAmount;
// };

//  const handleSearchCpIds = async () => {
//   const { searchType, searchValue } = formData;

//   if (!searchValue || !searchType) {
//     message.warning("Please select search type and enter value.");
//     return;
//   }

//   try {
//     const { data } = await axios.get(`/api/contigency-purchase/search`, {
//       params: {
//         type: searchType,
//         value: searchValue
//       }
//     });

//     const cpList = data?.responseData || [];

//     const dropdownOptions = cpList.map((item) => ({
//       label: item.cpId,
//       value: item.cpId
//     }));

//     setCpIdDropdown(dropdownOptions);

//     if (dropdownOptions.length === 0) {
//       message.warning("No Cp IDs found.");
//     } else {
//       message.success(`${dropdownOptions.length} Please Select Cp Id in Cp Id Drop Down.`);
//     }
//   } catch (error) {
//     message.error("Error fetching Cp IDs.");
//   }
// };


 
  

//   const handleChange = (name, value) => {
//      if (name === 'cpId') {
//         setFormData(prev => ({ ...prev, cpId: value }));
//         handleSearch(value);
//         return;
//     }
//     if (Array.isArray(name)) {
//       const [section, index, field] = name;
  
//       if (section === 'materialDetails') {
//         setFormData(prev => {
//           const updatedMaterials = [...prev.materialDetails];
//           const materialData = materialDetailsMap[value] || {};
//           const currentMaterial = updatedMaterials[index] || {};
//             if (field === 'materialCode' || field === 'materialDescription') {
//               const isByCode = field === 'materialCode';
//               const selectedValue = value;
            
//               const materialData = materialDetailsMap[selectedValue] || {};
//               const newMaterialCode = isByCode ? selectedValue : materialData.materialCode;
//               const newDescription = isByCode ? materialData.materialDescription : selectedValue;
            
//               // Check for duplicate materialCode
//               const isDuplicate = updatedMaterials.some((mat, idx) => mat.materialCode === newMaterialCode && idx !== index);
//               if (isDuplicate) {
//                 message.warning('Material Code must be unique');
//                 return prev;
//               }
            
//              /* // Check for consistent materialCategory
//               const selectedCategory = materialData.materialCategory;
//               const existingCategories = updatedMaterials
//                 .filter((_, idx) => idx !== index && _.materialCategory)
//                 .map(mat => mat.materialCategory);
            
//               const isCategoryMismatch = existingCategories.some(cat => cat !== selectedCategory);
//               if (existingCategories.length && isCategoryMismatch) {
//                 message.warning('All materials must belong to the same category');
//                 return prev;
//               }*/
            
//               updatedMaterials[index] = {
//                 ...currentMaterial,
//                 materialCode: newMaterialCode,
//                 materialDescription: newDescription,
//                 ...materialData,
//                // totalPrice: calculateTotalPrice(currentMaterial.quantity, materialData.unitPrice),
//                 totalPrice: calculateTotalPrice(currentMaterial.quantity, materialData.unitPrice, materialData.gst || 0),
//                 gst: materialData.gst || 0, 
//               };
//             }
            
  
         
//          // Quantity, Unit Price or GST change
// else if (field === 'quantity' || field === 'unitPrice' || field === 'gst') {
//   const quantity = field === 'quantity' ? value : currentMaterial.quantity || 0;
//   const unitPrice = field === 'unitPrice' ? value : currentMaterial.unitPrice || 0;
//   const gst = field === 'gst' ? value : currentMaterial.gst || 0;

//   const total = calculateTotalPrice(quantity, unitPrice, gst);

//   // ⚠️ Warn if total exceeds 50,000
//   if (total > 50000) {
//     message.warning('Total price including GST cannot exceed ₹50,000');
//     return prev;
//   }

//   updatedMaterials[index] = {
//     ...currentMaterial,
//     [field]: value,
//     totalPrice: total,
//     gst: gst,
//   };
// }

  
//           // Other fields
//           else {
//             updatedMaterials[index] = {
//               ...currentMaterial,
//               [field]: value,
//             };
//           }
  
//           return { ...prev, materialDetails: updatedMaterials };
//         });
//       }
//     } else {
//       setFormData(prev => ({ ...prev, [name]: value }));
//     }
//   };
  
//  /* const calculateTotalPrice = (quantity, unitPrice) => {
//     return (quantity || 0) * (unitPrice || 0);
//   };*/
  

//   const onFinish = async () => {
//   // Add validation before submission
//   if (!formData.materialDetails || formData.materialDetails.length === 0) {
//     message.error("Please add at least one material detail.");
//     return;
//   }
//    if (!formData.declarationOne || !formData.declarationTwo) {
//     message.error("Please accept both declarations to submit.");
//     return;
//   }
//   const grandTotal = formData.materialDetails.reduce((sum, material) => {
//     return sum + (material.totalPrice || 0);
//   }, 0);

//   if (grandTotal > 50000) {
//     message.error("Total value of all materials (including GST) must not exceed ₹50,000.");
//     return;
//   }
//   const hasChinaOrigin = formData.materialDetails.some(
//     (material) => material.countryOfOrigin?.toLowerCase() === "china"
//   );

//   if (hasChinaOrigin) {
//     message.error("Procurement from China is not allowed.");
//     return;
//   }


//   const cpMaterials = formData.materialDetails.map(material => ({
//     materialCode: material.materialCode,
//     materialDescription: material.materialDescription,
//     quantity: material.quantity,
//     unitPrice: material.unitPrice,
//     uom: material.uom,
//     totalPrice: material.totalPrice,
//     budgetCode: material.budgetCode,
//     materialCategory: material.materialCategory,
//     materialSubCategory: material.materialSubCategory,
//     currency: material.currency,
//     gst: material.gst,
//     countryOfOrigin: material.countryOfOrigin,
//   }));

//   const payload = {
//     ...formData,
//     createdBy: actionPerformer,
//     fileType: "CP",
//     cpMaterials
//   };
//   delete payload.materialDetails;
//   try {
//     setSubmitBtnLoading(true);
//     const { data } = await axios.post("/api/contigency-purchase", payload);

//     setFormData(prev => ({
//       ...prev,
//       cpId: data?.responseData?.contigencyId
//     }));

//     localStorage.removeItem("cpDraft");
//     setModalOpen(true);
//   } catch (error) {
//     message.error(
//       error?.response?.data?.responseStatus?.message ||
//       "Failed to submit purchase."
//     );
//     ;
//   } finally {
//     setSubmitBtnLoading(false);
//   }
// };

// const handleSearch = async (value) => {
//   try {
//     const { data } = await axios.get(
//       `/api/contigency-purchase/${value || formData.cpId}`
//     );

//    /* setFormData({
//       ...data?.responseData,
//       materialDetails: data?.responseData?.cpMaterials || [],
//     });*/
//        setFormData((prev) => ({
//       ...prev,
//       ...data?.responseData,
//       materialDetails: data?.responseData?.cpMaterials || [],
//     }));
//   } catch (error) {
//     ;
//     message.error(
//       error?.response?.data?.responseStatus?.message || "Error fetching CP details."
//     );
//   }
// };

//   // --- Draft Handling ---
//   useEffect(() => {
//     const draft = localStorage.getItem('cpDraft');
//     if (draft) {
//       setFormData(JSON.parse(draft));
//       message.success('Loaded draft data');
//     }
//   }, []);

//   useEffect(() => {
//     localStorage.setItem('cpDraft', JSON.stringify(formData));
//   }, [formData]);

//   const handlePrint = useReactToPrint({
//     content: () => printRef.current,
//   });

//   const addMaterialRow = () => {
//     ;
  
//     const currentMaterialDetails = formData.materialDetails || [];
  
//     // If materialDetails is empty, allow adding the first row
//     if (currentMaterialDetails.length === 0) {
//       setFormData({
//         ...formData,
//         materialDetails: [
//           {
//             materialCode: '',
//             materialDescription: '',
//             materialCategory: '',
//             materialSubCategory: '',
//             uom: '',
//             unitPrice: '',
//             quantity: '',
//             currency: '',
//             totalPrice: '',
//             gst: '',
//             countryOfOrigin:'',
//           },
//         ],
//       });
//       return;
//     }
  
//     const lastMaterial = currentMaterialDetails[currentMaterialDetails.length - 1];
  
//     // Validate the last row is fully filled
//     if (
//       !lastMaterial.materialCode ||
//       !lastMaterial.materialCategory ||
//       !lastMaterial.materialSubCategory ||
//       !lastMaterial.uom ||
//       !lastMaterial.unitPrice ||
//       !lastMaterial.currency ||
//       !lastMaterial.totalPrice ||
//       !lastMaterial.countryOfOrigin
//     ) {
//       message.error("Please fill all the fields of the last row before adding a new row");
//       return;
//     }
  
//     // Append a new empty row
//     setFormData(prev => ({
//       ...prev,
//       materialDetails: [
//         ...prev.materialDetails,
//         {
//           materialCode: '',
//           materialDescription: '',
//           materialCategory: '',
//           materialSubCategory: '',
//           uom: '',
//           unitPrice: '',
//           quantity: '',
//           currency: '',
//           totalPrice: '',
//           gst: '',
//           countryOfOrigin:'',
//         },
//       ],
//     }));
//   };
  
  

//   useEffect(()=>{
//     populateDropdowns();
//   },[formData.paymentTo]);
// /*
// const hydratedCpDetails = useMemo(() => {
//   return CpDetails.map(section => {
//     if (section.fieldList) {
//       return {
//         ...section,
//         fieldList: section.fieldList.map(field => {
//           if (field.name === 'projectName') return { ...field, options: projects };
//           if (field.name === 'vendorName') return { ...field, options: vendors };
//           return field;
//         })
//       };
//     }

//     if (section.children) {
//       return {
//         ...section,
//         children: section.children.map(child => {
//           if (child.name === 'materialCode') return { ...child, options: materialOptions };
//           if (child.name === 'materialDescription') return { ...child, options: materialDescOptions };
//           return child;
//         })
//       };
//     }

//     return section;
//   });
// }, [formData,CpDetails, projects, vendors, materialOptions, materialDescOptions]); // <- Dependencies
// */
// const hydratedCpDetails = useMemo(() => {
//   // ✅ Pass LOV values to CpDetails
//   const lovData = {
//     gstPercentageLOV,
//     paymentToLOV,
//     budgetCodeLOV,
//     materialCategoryLOV,
//     materialSubCategoryLOV,
//     countryOfOriginLOV
//   };

//   return CpDetails(formData, lovData).map(section => {  // Call the function here
//     if (section.fieldList) {
//       return {
//         ...section,
//         fieldList: section.fieldList.map(field => {
//           if (field.name === 'cpId') return { ...field, options: cpIdDropdown, };
//           if (field.name === 'projectName') return { ...field, options: projects };
//           if (field.name === 'vendorName') return { ...field, options: vendors };
//           if (field.name === 'paymentToVendor') return { ...field, options: vendors };
//           if (field.name === 'paymentToEmployee') return { ...field, options: employees }; 
//           if (field.name === 'searchValue') return { ...field, onSearch: handleSearchCpIds };

//           return field;
//         })
//       };
//     }

//     if (section.children) {
//       return {
//         ...section,
//         children: section.children.map(child => {
//           if (child.name === 'materialCode') return { ...child, options: materialOptions };
//           if (child.name === 'materialDescription') return { ...child, options: materialDescOptions };
//           return child;
//         })
//       };
//     }

//     return section;
//   });
// }, [formData, projects, vendors, materialOptions, materialDescOptions, employees, cpIdDropdown, gstPercentageLOV, paymentToLOV, budgetCodeLOV, materialCategoryLOV, materialSubCategoryLOV, countryOfOriginLOV]);




//   return (
//     <Card className="a4-container" ref={printRef}>
//       <Heading title="Contingency Purchase" />
//       <CustomForm formData={formData} onFinish={onFinish}>
//         {renderFormFields(
//           hydratedCpDetails,
//           handleChange,
//           formData,
//           "",
//           null,
//           setFormData,
//           handleSearch,
//           {
//             addMaterialSection: addMaterialRow,
//           //  materialDeselect: materialDeselectRow 
//           }
//         )}
//         <ButtonContainer
//           onFinish={onFinish}
//           formData={formData}
//           draftDataName="cpDraft"
//           submitBtnLoading={submitBtnLoading}
//           submitBtnEnabled
//           printBtnEnabled
//           draftBtnEnabled
//           handlePrint={handlePrint}
//         />
//       </CustomForm>
//       <CustomModal
//         isOpen={modalOpen}
//         setIsOpen={setModalOpen}
//         title="Contingency Purchase"
//         processNo={formData?.cpId}
//       />
//     </Card>
//   );
// };
// /*
// const ContingencyPurchase = () => {
//   const printRef = useRef();
//   const [form] = Form.useForm();
//   const [modalOpen, setModalOpen] = useState(false);
//   const [submitBtnLoading, setSubmitBtnLoading] = useState(false);
//   const [generatedCpId, setGeneratedCpId] = useState('');
//   const [isPrintEnabled, setIsPrintEnabled] = useState(false);

//   // Redux selectors
//   const auth = useSelector((state) => state.auth);
//   const actionPerformer = auth.userId;

//   // Data states
//   const [projects, setProjects] = useState([]);
//   const [vendors, setVendors] = useState([]);
//   const [materialDetailsMap, setMaterialDetailsMap] = useState({});
//   const [materialOptions, setMaterialOptions] = useState([]);
//   const [materialDescOptions, setMaterialDescOptions] = useState([]);

//   // Form data state
//   const [formData, setFormData] = useState({ materialDetails: [{}] });

//   // --- Dynamic Field Population ---
//   const populateDropdowns = async () => {
//     try {
//       const [projectResponse, materialResponse] = await Promise.all([
//         axios.get('/api/project-master'),
//         axios.get('/api/material-master'),
//       ]);
//       const vendorResponse = await axios.get('/api/vendor-master');

//       // Format projects
//       const formattedProjects = (projectResponse.data?.responseData || []).map(project => ({
//         label: project.projectNameDescription,
//         value: project.projectCode,
//       }));
//       setProjects(formattedProjects);

//       // Format vendors
//       const formattedVendors = (vendorResponse.data?.responseData || []).map(vendor => ({
//         label: vendor.vendorName,
//         value: vendor.vendorName,
//       }));
//       setVendors(formattedVendors);

//       // Format materials
//       const materials = materialResponse.data?.responseData || [];
//       const materialMap = {};
//       const descMap = {};

//       materials.forEach(material => {
//         materialMap[material.materialCode] = {
//           materialDescription: material.description,
//           uom: material.uom,
//           unitPrice: material.unitPrice,
//           currency: material.currency,
//           materialCategory: material.category,
//           materialSubCategory: material.subCategory
//         };

//         descMap[material.description] = {
//           materialCode: material.materialCode,
//           uom: material.uom,
//           unitPrice: material.unitPrice,
//           currency: material.currency,
//           materialCategory: material.category,
//           materialSubCategory: material.subCategory
//         };
//       });

//       setMaterialOptions(materials.map(m => ({ label: m.materialCode, value: m.materialCode })));
//       setMaterialDescOptions(materials.map(m => ({ label: m.description, value: m.description })));
//       setMaterialDetailsMap({ ...materialMap, ...descMap });
//     } catch (error) {
//       console.error('Error fetching data:', error);
//       message.error('Failed to load dropdown data');
//     }
//   };

//   // --- handleChange Function ---
//   const handleChange = (name, value) => {
//     if (Array.isArray(name)) {
//       const [section, index, field] = name;
      
//       if (section === 'materialDetails') {
//         setFormData(prev => {
//           const updatedMaterials = [...prev.materialDetails];
//           const materialData = materialDetailsMap[value] || {};

//           if (field === 'materialCode' || field === 'materialDescription') {
//             updatedMaterials[index] = {
//               ...updatedMaterials[index],
//               [field]: value,
//               ...materialData,
//               totalPrice: (materialData.unitPrice || 0) * (updatedMaterials[index]?.quantity || 0)
//             };
//           } else if (field === 'quantity' || field === 'unitPrice') {
//             const quantity = field === 'quantity' ? value : updatedMaterials[index]?.quantity || 0;
//             const unitPrice = field === 'unitPrice' ? value : updatedMaterials[index]?.unitPrice || 0;
//             const calculateTotalPrice = quantity * unitPrice;
//             if (calculateTotalPrice > 50000) {
//               message.warning('Total price cannot exceed 50,000');
//               return prev; 
//             }
//             updatedMaterials[index] = {
//               ...updatedMaterials[index],
//               [field]: value,
//              // totalPrice: quantity * unitPrice
//              totalPrice: calculateTotalPrice
//             };
//           } else {
//             updatedMaterials[index] = {
//               ...updatedMaterials[index],
//               [field]: value
//             };
//           }
           
//           return { ...prev, materialDetails: updatedMaterials };
//         });
//       }
//     } else {
//       setFormData(prev => ({ ...prev, [name]: value }));
//     }
//   };

//   // --- Form Submission ---

//   const onFinish = async () => {
//     // Get first material detail item or empty object
//     const material = formData.materialDetails[0] || {};
    
//     const payload = {
//       vendorsName: formData.vendorName,
//       vendorsInvoiceNo: formData.vendorInvoiceNo,
//       remarksForPurchase: formData.remarks,
//       projectName: formData.projectName,
//       projectDetail: formData.projectDetail,
//       date: formData.date,
//       createdBy: actionPerformer,
//       amountToBePaid: formData.amountToBePaid,
//       predifinedPurchaseStatement: formData.predefinedPurchaseStatement,
//       uploadCopyOfInvoice: formData.uploadCopyOfInvoice,
//       // Material fields from first array item
//       materialCode: material.materialCode,
//       materialDescription: material.materialDescription,
//       quantity: material.quantity,
//       unitPrice: material.unitPrice,
//       // Additional fields from material details
//       currency: material.currency,
//       materialCategory: material.materialCategory,
//       materialSubCategory: material.materialSubCategory,
//       fileType: 'CP',
//     };
  
//     try {
//       setSubmitBtnLoading(true);
//       const { data } = await axios.post('/api/contigency-purchase', payload);
      
//       setFormData(prev => ({
//         ...prev,
//         cpId: data?.responseData?.contigencyId
//       }));
//       setModalOpen(true);
//       localStorage.removeItem('cpDraft');
//     } catch (error) {
//       message.error(error?.response?.data?.message || 'Failed to submit purchase');
//     } finally {
//       setSubmitBtnLoading(false);
//     }
//   };
  
//   const handleSearch = async (value) => {
//     try {
//       const { data } = await axios.get(
//         `/api/contigency-purchase/${value || formData.cpId}`
//       );
  
//       const cpData = data.responseData;
//       setFormData({
//         ...cpData,
//         materialDetails: [{
//           materialCode: cpData.materialCode,
//           materialDescription: cpData.materialDescription,
//           quantity: cpData.quantity,
//           unitPrice: cpData.unitPrice,
//           uom: cpData.uom,
//           currency: cpData.currency,
//           materialCategory: cpData.materialCategory,
//           materialSubCategory: cpData.materialSubCategory,
//           totalPrice: cpData.quantity * cpData.unitPrice
//         }],
//         predefinedPurchaseStatement: cpData.predifinedPurchaseStatement,
//         uploadCopyOfInvoice: cpData.uploadCopyOfInvoice,
//         amountToBePaid: cpData.amountToBePaid,
//         remarks: cpData.remarksForPurchase,
//         // date: dayjs(cpData.date, 'DD/MM/YYYY'),
//         vendorName: cpData.vendorsName,
//         vendorInvoiceNo: cpData.vendorsInvoiceNo,
//         projectName: cpData.projectName
//       });
//     } catch (error) {
//       console.error("Search Error:", error);
//       message.error(error?.response?.data?.responseStatus?.message || "Error fetching CP details");
//     }
//   };

//   // --- Draft Handling ---
//   useEffect(() => {
//     const draft = localStorage.getItem('cpDraft');
//     if (draft) {
//       setFormData(JSON.parse(draft));
//       message.success('Loaded draft data');
//     }
//   }, []);

//   useEffect(() => {
//     localStorage.setItem('cpDraft', JSON.stringify(formData));
//   }, [formData]);

//   const handlePrint = useReactToPrint({
//     content: () => printRef.current,
//   });

//   useEffect(()=>{
//     populateDropdowns();
//   },[]);

//   // --- Prepare Hydrated Fields ---
//   const hydratedCpDetails = CpDetails.map(section => {
//     if (section.fieldList) {
//       return {
//         ...section,
//         fieldList: section.fieldList.map(field => {
//           if (field.name === 'projectName') return { ...field, options: projects };
//           if (field.name === 'vendorName') return { ...field, options: vendors }; // Add this line
//           return field;
//         })
//       };
//     }
//     if (section.children) {
//       return {
//         ...section,
//         children: section.children.map(child => {
//           if (child.name === 'materialCode') return { ...child, options: materialOptions };
//           if (child.name === 'materialDescription') return { ...child, options: materialDescOptions };
//           return child;
//         })
//       };
//     }
//     return section;
//   });

//   return (
//     <Card className="a4-container" ref={printRef}>
//       <Heading title="Contingency Purchase" />
//       <CustomForm formData={formData} onFinish={onFinish}>
//         {renderFormFields(
//           hydratedCpDetails,
//           handleChange,
//           formData,
//           "",
//           null,
//           setFormData,
//           handleSearch
//         )}
//         <ButtonContainer
//           onFinish={onFinish}
//           formData={formData}
//           draftDataName="cpDraft"
//           submitBtnLoading={submitBtnLoading}
//           submitBtnEnabled
//           printBtnEnabled
//           draftBtnEnabled
//           handlePrint={handlePrint}
//         />
//       </CustomForm>
//       <CustomModal
//         isOpen={modalOpen}
//         setIsOpen={setModalOpen}
//         title="Contingency Purchase"
//         processNo={formData?.cpId}
//       />
//     </Card>
//   );
// };*/

// export default ContingencyPurchase;

import React, { useRef, useState, useEffect } from 'react';
import { Button, Card, Form, Input, Select, DatePicker, message } from 'antd';
import { useReactToPrint } from 'react-to-print';
import axios from 'axios';
import { useSelector } from 'react-redux';
import countryList from 'country-list';
import Heading from '../../../components/DKG_Heading';
import CustomForm from '../../../components/DKG_CustomForm';
import { renderFormFields } from '../../../utils/CommonFunctions';
import ButtonContainer from '../../../components/ButtonContainer';
import CustomModal from '../../../components/CustomModal';
import { CpDetails } from './InputFields';
import { useMemo } from 'react';
import { useLOVValues } from '../../../hooks/useLOVValues';


const { Option } = Select;


const ContingencyPurchase = () => {
  const printRef = useRef();
  const [form] = Form.useForm();
  const [modalOpen, setModalOpen] = useState(false);
  const [submitBtnLoading, setSubmitBtnLoading] = useState(false);
  const [generatedCpId, setGeneratedCpId] = useState('');
  const [isPrintEnabled, setIsPrintEnabled] = useState(false);

  const [draftBtnLoading, setDraftBtnLoading] = useState(false);
  const [employees, setEmployees] = useState([]);

  // Redux selectors
  const auth = useSelector((state) => state.auth);
  const actionPerformer = auth.userId;

  // Data states
  const [projects, setProjects] = useState([]);
  const [vendors, setVendors] = useState([]);
  const [materialDetailsMap, setMaterialDetailsMap] = useState({});
  const [materialOptions, setMaterialOptions] = useState([]);
  const [cpIdDropdown, setCpIdDropdown] = useState([]);
  const [allBudgetCodes, setAllBudgetCodes] = useState([]);
  const [projectBudgetCodes, setProjectBudgetCodes] = useState([]);
  const [allCountries, setAllCountries] = useState([]);

  // Form data state
  const [formData, setFormData] = useState({ materialDetails: [{}] });

  const [cpLimit, setCpLimit] = useState(50000);

  // ✅ Fetch dropdown values from LOV system (Form ID: 2 - ContingencyPurchase)
  const { lovValues: gstPercentageLOV, loading: loadingGst } = useLOVValues(2, 'gstPercentage');
  const { lovValues: paymentToLOV, loading: loadingPaymentTo } = useLOVValues(2, 'paymentTo');
  const { lovValues: budgetCodeLOV, loading: loadingBudgetCode } = useLOVValues(2, 'budgetCode');
  const { lovValues: materialCategoryLOV, loading: loadingMaterialCategory } = useLOVValues(2, 'materialCategory');
  const { lovValues: materialSubCategoryLOV, loading: loadingMaterialSubCategory } = useLOVValues(2, 'materialSubCategory');
  const { lovValues: countryOfOriginLOV, loading: loadingCountryOfOrigin } = useLOVValues(2, 'countryOfOrigin');

  // --- Dynamic Field Population ---
  const populateDropdowns = async () => {
    try {
      const [projectResponse] = await Promise.all([
        axios.get('/api/project-master'),
      ]);
      const vendorResponse = await axios.get('/api/vendor-master');

      // Format projects
      const formattedProjects = (projectResponse.data?.responseData || []).map(project => ({
        label: project.projectNameDescription,
        value: project.projectCode,
      }));
      setProjects(formattedProjects);

      // Format vendors
      const formattedVendors = (vendorResponse.data?.responseData || []).map(vendor => ({
        label: vendor.vendorName,
        value: vendor.vendorName,
      }));
      setVendors(formattedVendors);

      const employeeResponse = await axios.get('/api/employee-department-master');
      const formattedEmployees = (employeeResponse.data?.responseData || []).map(employee => ({
      label: employee.employeeName,
      value: employee.employeeId,
      }));
      setEmployees(formattedEmployees);
      ;


     

      // Format materials — same endpoint & label format as Indent1
      const materialResponse = await axios.get('/api/material-master/materialSearch', {
        params: { keyword: '' }
      });
      const materials = materialResponse.data?.responseData || materialResponse.data || [];
      const materialMap = {};

      materials.forEach(material => {
        materialMap[material.materialCode] = {
          materialDescription: material.description,
          uom: material.uom,
          unitPrice: material.unitPrice,
          currency: material.currency,
          materialCategory: material.category,
          materialSubCategory: material.subCategory
        };
      });

      // Label: "CODE - Description" same as Indent1
      setMaterialOptions(materials.map(m => ({
        label: `${m.materialCode} - ${m.description}`,
        value: m.materialCode
      })));
      setMaterialDetailsMap(materialMap);
    } catch (error) {
      console.error('Error fetching data:', error);
      message.error('Failed to load dropdown data');
    }
  };

  // Fetch all budget codes (used when no project is selected)
  const fetchAllBudgetCodes = async () => {
    try {
      const { data } = await axios.get('/api/admin/budget');
      const budgetData = data?.responseData || [];
      setAllBudgetCodes(budgetData.map(budget => ({
        label: budget.budgetName || budget.budgetCode,
        value: budget.budgetCode
      })));
    } catch (error) {
      console.error('Error fetching all budget codes:', error);
      setAllBudgetCodes([]);
    }
  };

  // Fetch project-specific budget codes when a project is selected
  const fetchBudgetCodesByProject = async (projectCode) => {
    if (!projectCode) {
      setProjectBudgetCodes([]);
      return;
    }
    try {
      const { data } = await axios.get(`/api/admin/budget/project/${projectCode}/dropdown`);
      const budgetData = data?.responseData || data?.data || (Array.isArray(data) ? data : []);
      setProjectBudgetCodes(budgetData.map(budget => ({
        label: budget.budgetName || budget.budgetCode,
        value: budget.budgetCode
      })));
    } catch (error) {
      console.error('Error fetching budget codes for project:', error);
      setProjectBudgetCodes([]);
    }
  };

  // Country of Origin — populated from 'country-list' npm package (no API needed)
  const fetchAllCountries = () => {
    try {
      const countries = countryList.getData(); // returns [{ code, name }]
      setAllCountries(countries.map(c => ({
        label: c.name,
        value: c.code,
      })));
    } catch (error) {
      console.error('Error loading country list:', error);
      setAllCountries([]);
    }
  };

  //  Calculates total price with GST
const calculateTotalPrice = (quantity, unitPrice, gst = 0) => {
  const base = (quantity || 0) * (unitPrice || 0);
  const gstAmount = (base * gst) / 100;
  return base + gstAmount;
};

 const handleSearchCpIds = async () => {
  const { searchType, searchValue } = formData;

  if (!searchValue || !searchType) {
    message.warning("Please select search type and enter value.");
    return;
  }

  try {
    const { data } = await axios.get(`/api/contigency-purchase/search`, {
      params: {
        type: searchType,
        value: searchValue
      }
    });

    const cpList = data?.responseData || [];

    const dropdownOptions = cpList.map((item) => ({
      label: item.cpId,
      value: item.cpId
    }));

    setCpIdDropdown(dropdownOptions);

    if (dropdownOptions.length === 0) {
      message.warning("No Cp IDs found.");
    } else {
      message.success(`${dropdownOptions.length} Please Select Cp Id in Cp Id Drop Down.`);
    }
  } catch (error) {
    message.error("Error fetching Cp IDs.");
  }
};


 
  

  const handleChange = (name, value) => {
    if (name === 'vendorName') {
    if (value === 'Others') {
      setFormData(prev => ({
        ...prev,
        vendorName: 'Others',
        vendorInvoiceNo: '',   // clear dependent fields if needed
      }));
      return;
    }
    setFormData(prev => ({ ...prev, vendorName: value }));
    return;
  }
     if (name === 'cpId') {
        setFormData(prev => ({ ...prev, cpId: value }));
        handleSearch(value);
        return;
    }
    if (Array.isArray(name)) {
      const [section, index, field] = name;
  
      if (section === 'materialDetails') {
        setFormData(prev => {
          const updatedMaterials = [...prev.materialDetails];
          const materialData = materialDetailsMap[value] || {};
          const currentMaterial = updatedMaterials[index] || {};
            if (field === 'materialCode') {
              const details = materialDetailsMap[value] || {};

              const isDuplicate = updatedMaterials.some((mat, idx) => mat.materialCode === value && idx !== index);
              if (isDuplicate) {
                message.warning('Material Code must be unique');
                return prev;
              }

              updatedMaterials[index] = {
                ...currentMaterial,
                materialCode: value,
                materialDescription: details.materialDescription || '',
                uom: details.uom || '',
                unitPrice: details.unitPrice || '',
                currency: details.currency || '',
                materialCategory: details.materialCategory || '',
                materialSubCategory: details.materialSubCategory || '',
                quantity: '',
                totalPrice: '',
                gst: currentMaterial.gst || 0,
              };
            }
            else if (field === 'materialDescription') {
              updatedMaterials[index] = { ...currentMaterial, materialDescription: value };
            }
            
  
         
         // Quantity, Unit Price or GST change
else if (field === 'quantity' || field === 'unitPrice' || field === 'gst') {
  const quantity = field === 'quantity' ? value : currentMaterial.quantity || 0;
  const unitPrice = field === 'unitPrice' ? value : currentMaterial.unitPrice || 0;
  const gst = field === 'gst' ? value : currentMaterial.gst || 0;

  const total = calculateTotalPrice(quantity, unitPrice, gst);

  if (total > cpLimit) {
    message.warning(`Total price including GST cannot exceed ₹${cpLimit.toLocaleString('en-IN')}`);
    return prev;
  }

  updatedMaterials[index] = {
    ...currentMaterial,
    [field]: value,
    totalPrice: total,
    gst: gst,
  };
}

  
          // Other fields
          else {
            updatedMaterials[index] = {
              ...currentMaterial,
              [field]: value,
            };
          }
  
          return { ...prev, materialDetails: updatedMaterials };
        });
      }
    } else {
      // Intercept projectName — fetch project-specific budget codes and reset budgetCode
      if (name === 'projectName') {
        setFormData(prev => ({ ...prev, projectName: value, budgetCode: null }));
        fetchBudgetCodesByProject(value);
        return;
      }
      setFormData(prev => ({ ...prev, [name]: value }));
    }
  };
  
 /* const calculateTotalPrice = (quantity, unitPrice) => {
    return (quantity || 0) * (unitPrice || 0);
  };*/
  

  const buildCpPayload = () => {
    const cpMaterials = (formData.materialDetails || []).map(material => ({
      materialCode: material.materialCode,
      materialDescription: material.materialDescription,
      quantity: material.quantity,
      unitPrice: material.unitPrice,
      uom: material.uom,
      totalPrice: material.totalPrice,
      budgetCode: material.budgetCode,
      materialCategory: material.materialCategory,
      materialSubCategory: material.materialSubCategory,
      currency: material.currency,
      gst: material.gst,
      countryOfOrigin: material.countryOfOrigin,
    }));
    const payload = {
      ...formData,
      createdBy: actionPerformer,
      fileType: "CP",
      cpMaterials
    };
    delete payload.materialDetails;
    return payload;
  };

  const handleSaveDraft = async () => {
    try {
      setDraftBtnLoading(true);
      const payload = buildCpPayload();
      let response;

      if (formData?.cpId && formData?.currentStatus === "DRAFT") {
        response = await axios.put(`/api/contigency-purchase/draft`, payload, {
          params: { cpId: formData.cpId },
        });
        message.success("Draft updated successfully.");
      } else if (!formData?.cpId) {
        response = await axios.post(`/api/contigency-purchase/draft`, payload);
        message.success("Draft saved successfully.");
      } else {
        message.warning("This Contingency Purchase has already been submitted and cannot be saved as a draft.");
        return;
      }

      const savedData = response?.data?.responseData;
      setFormData((prev) => ({
        ...prev,
        cpId: savedData?.contigencyId,
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

  const onFinish = async () => {
  // Add validation before submission
  if (!formData.materialDetails || formData.materialDetails.length === 0) {
    message.error("Please add at least one material detail.");
    return;
  }
   if (!formData.declarationOne || !formData.declarationTwo) {
    message.error("Please accept both declarations to submit.");
    return;
  }
  const grandTotal = formData.materialDetails.reduce((sum, material) => {
    return sum + (material.totalPrice || 0);
  }, 0);

  if (grandTotal > cpLimit) {
    message.error(`Total value of all materials (including GST) must not exceed ₹${cpLimit.toLocaleString('en-IN')}.`);
    return;
  }
  const hasChinaOrigin = formData.materialDetails.some(
    (material) => material.countryOfOrigin?.toLowerCase() === "china"
  );

  if (hasChinaOrigin) {
    message.error("Procurement from China is not allowed.");
    return;
  }

  if (formData?.cpId && formData?.currentStatus === "DRAFT") {
    try {
      setSubmitBtnLoading(true);
      const payload = buildCpPayload();
      const response = await axios.post(
        `/api/contigency-purchase/draft/submit`,
        payload,
        { params: { cpId: formData.cpId } }
      );
      const submittedCpId = response.data?.responseData?.contigencyId;
      setFormData((prev) => ({
        ...prev,
        cpId: submittedCpId,
        currentStatus: null,
      }));
      message.success("Contingency Purchase submitted successfully.");
      setModalOpen(true);
      return;
    } catch (error) {
      message.error(
        error?.response?.data?.responseStatus?.message || "Failed to submit draft."
      );
      return;
    } finally {
      setSubmitBtnLoading(false);
    }
  }

  const payload = buildCpPayload();
  try {
    setSubmitBtnLoading(true);
    const { data } = await axios.post("/api/contigency-purchase", payload);

    setFormData(prev => ({
      ...prev,
      cpId: data?.responseData?.contigencyId
    }));

    setModalOpen(true);
  } catch (error) {
    message.error(
      error?.response?.data?.responseStatus?.message ||
      "Failed to submit purchase."
    );
  } finally {
    setSubmitBtnLoading(false);
  }
};

const handleSearch = async (value) => {
  try {
    const { data } = await axios.get(
      `/api/contigency-purchase/${value || formData.cpId}`
    );

       setFormData((prev) => ({
      ...prev,
      ...data?.responseData,
      materialDetails: data?.responseData?.cpMaterials || [],
    }));
  } catch (error) {
    message.error(
      error?.response?.data?.responseStatus?.message || "Error fetching CP details."
    );
  }
};

  // --- Draft Handling (server-side) ---
  useEffect(() => {
    const loadUserDrafts = async () => {
      try {
        const { data } = await axios.get(`/api/contigency-purchase/drafts`, {
          params: { userId: actionPerformer },
        });
        const drafts = data?.responseData || [];
        if (drafts.length > 0) {
          const latest = drafts[0];
          setFormData((prev) => ({
            ...prev,
            ...latest,
            cpId: latest.contigencyId,
            materialDetails: latest.cpMaterials || [],
            currentStatus: "DRAFT",
          }));
          message.info("Draft loaded from server.");
        }
      } catch (_) {}
    };
    if (actionPerformer) loadUserDrafts();
  }, [actionPerformer]);

  const handlePrint = useReactToPrint({
    content: () => printRef.current,
  });

  const addMaterialRow = () => {
    ;
  
    const currentMaterialDetails = formData.materialDetails || [];
  
    // If materialDetails is empty, allow adding the first row
    if (currentMaterialDetails.length === 0) {
      setFormData({
        ...formData,
        materialDetails: [
          {
            materialCode: '',
            materialDescription: '',
            materialCategory: '',
            materialSubCategory: '',
            uom: '',
            unitPrice: '',
            quantity: '',
            currency: '',
            totalPrice: '',
            gst: '',
            countryOfOrigin:'',
          },
        ],
      });
      return;
    }
  
    const lastMaterial = currentMaterialDetails[currentMaterialDetails.length - 1];
  
    // Validate the last row is fully filled
    if (
      !lastMaterial.materialCode ||
      !lastMaterial.materialCategory ||
      !lastMaterial.materialSubCategory ||
      !lastMaterial.uom ||
      !lastMaterial.unitPrice ||
      !lastMaterial.currency ||
      !lastMaterial.totalPrice ||
      !lastMaterial.countryOfOrigin
    ) {
      message.error("Please fill all the fields of the last row before adding a new row");
      return;
    }
  
    // Append a new empty row
    setFormData(prev => ({
      ...prev,
      materialDetails: [
        ...prev.materialDetails,
        {
          materialCode: '',
          materialDescription: '',
          materialCategory: '',
          materialSubCategory: '',
          uom: '',
          unitPrice: '',
          quantity: '',
          currency: '',
          totalPrice: '',
          gst: '',
          countryOfOrigin:'',
        },
      ],
    }));
  };
  
  

  useEffect(()=>{
    populateDropdowns();
    fetchAllBudgetCodes();
    fetchAllCountries();
    axios.get('/getWorkflowLimit?workflowId=2')
      .then(res => {
        const limit = res.data?.responseData;
        if (limit != null) setCpLimit(Number(limit));
      })
      .catch(err => console.error('Error fetching CP limit:', err));
  },[formData.paymentTo]);
/*
const hydratedCpDetails = useMemo(() => {
  return CpDetails.map(section => {
    if (section.fieldList) {
      return {
        ...section,
        fieldList: section.fieldList.map(field => {
          if (field.name === 'projectName') return { ...field, options: projects };
          if (field.name === 'vendorName') return { ...field, options: vendors };
          return field;
        })
      };
    }

    if (section.children) {
      return {
        ...section,
        children: section.children.map(child => {
          if (child.name === 'materialCode') return { ...child, options: materialOptions };
          if (child.name === 'materialDescription') return { ...child, options: materialDescOptions };
          return child;
        })
      };
    }

    return section;
  });
}, [formData,CpDetails, projects, vendors, materialOptions, materialDescOptions]); // <- Dependencies
*/

const hydratedCpDetails = useMemo(() => {
  // ✅ Pass LOV values to CpDetails
  const lovData = {
    gstPercentageLOV,
    paymentToLOV,
    budgetCodeLOV,
    materialCategoryLOV,
    materialSubCategoryLOV,
    countryOfOriginLOV
  };

  // Resolve country code → label for declaration text only
  // formData itself is NOT mutated — API calls still use the code (e.g. "IN")
  const firstMaterial = formData.materialDetails?.[0] || {};
  const resolvedCountryName =
    allCountries.find(c => c.value === firstMaterial.countryOfOrigin)?.label
    || firstMaterial.countryOfOrigin
    || '';

  const formDataForDeclaration = {
    ...formData,
    materialDetails: [
      { ...firstMaterial, countryOfOrigin: resolvedCountryName },
      ...(formData.materialDetails || []).slice(1),
    ],
  };

  return CpDetails(formDataForDeclaration, lovData).map(section => {
    if (section.fieldList) {
      return {
        ...section,
        fieldList: section.fieldList.map(field => {
          if (field.name === 'cpId') return { ...field, options: cpIdDropdown };
          if (field.name === 'projectName') return { ...field, options: projects };
          if (field.name === 'vendorName') {
            const isOthers = formData.vendorName === 'Others';
            return {
              ...field,
              type: isOthers ? 'text' : 'select',
              options: isOthers ? [] : [...vendors, { label: 'Others', value: 'Others' }],
              props: { readOnly: false }
            };
          }
          if (field.name === 'paymentToVendor') return { ...field, options: vendors };
          if (field.name === 'paymentToEmployee') return { ...field, options: employees };
          if (field.name === 'searchValue') return { ...field, onSearch: handleSearchCpIds };

          return field;
        })
      };
    }

    if (section.children) {
      return {
        ...section,
        children: section.children.map(child => {
          if (child.name === 'materialCode') return {
            ...child,
            options: materialOptions,
            showSearch: true,
          };

          if (child.name === 'budgetCode') return {
            ...child,
            options: formData.projectName
              ? (projectBudgetCodes.length > 0 ? projectBudgetCodes : [])
              : allBudgetCodes,
            placeholder: formData.projectName
              ? 'Select budget code'
              : 'Select budget code (or select a project for filtered codes)',
            disabled: child.disabled || false,
          };

          if (child.name === 'countryOfOrigin') return {
            ...child,
            options: allCountries,
            showSearch: true,
          };

          return child;
        })
      };
    }

    return section;
  });
}, [formData, projects, vendors, materialOptions, employees, cpIdDropdown, allBudgetCodes, projectBudgetCodes, allCountries, gstPercentageLOV, paymentToLOV, materialCategoryLOV, materialSubCategoryLOV]);
// const hydratedCpDetails = useMemo(() => {
//   // ✅ Pass LOV values to CpDetails
//   const lovData = {
//     gstPercentageLOV,
//     paymentToLOV,
//     budgetCodeLOV,
//     materialCategoryLOV,
//     materialSubCategoryLOV,
//     countryOfOriginLOV
//   };

//   return CpDetails(formData, lovData).map(section => {  // Call the function here
//     if (section.fieldList) {
//       return {
//         ...section,
//         fieldList: section.fieldList.map(field => {
//           if (field.name === 'cpId') return { ...field, options: cpIdDropdown, };
//           if (field.name === 'projectName') return { ...field, options: projects };
//           if (field.name === 'vendorName') {const isOthers = formData.vendorName === 'Others'; 
//             return { ...field, type: isOthers ? 'text' : 'select',          // switch to free text when Others
//     options: isOthers ? [] : [...vendors, { label: 'Others', value: 'Others' }],
//     props: {
//       readOnly: false                              // always editable
//     } };}
//           if (field.name === 'paymentToVendor') return { ...field, options: vendors };
//           if (field.name === 'paymentToEmployee') return { ...field, options: employees }; 
//           if (field.name === 'searchValue') return { ...field, onSearch: handleSearchCpIds };
          

//           return field;
//         })
//       };
//     }

//     if (section.children) {
//       return {
//         ...section,
//         children: section.children.map(child => {
//           if (child.name === 'materialCode') return {
//             ...child,
//             options: materialOptions,   // "CODE - Description" array, loaded on mount
//             showSearch: true,           // local filter as user types — same as Indent1
//           };
//           // materialDescription is now a read-only text field — no options needed

//           // Budget Code: project-specific if project selected, else all budgets
//           if (child.name === 'budgetCode') return {
//             ...child,
//             options: formData.projectName
//               ? (projectBudgetCodes.length > 0 ? projectBudgetCodes : [])
//               : allBudgetCodes,
//             placeholder: formData.projectName
//               ? 'Select budget code'
//               : 'Select budget code (or select a project for filtered codes)',
//             disabled: child.disabled || false,
//           };

//           // Country of Origin: all countries from API
//           if (child.name === 'countryOfOrigin') return {
//             ...child,
//             options: allCountries,
//             showSearch: true,
//           };

//           return child;
//         })
//       };
//     }

//     return section;
//   });
// }, [formData, projects, vendors, materialOptions, employees, cpIdDropdown, allBudgetCodes, projectBudgetCodes, allCountries, gstPercentageLOV,formData.vendorName, paymentToLOV, materialCategoryLOV, materialSubCategoryLOV]);




  return (
    <Card className="a4-container" ref={printRef}>
      <Heading title="Contingency Purchase" />
      {formData.currentStatus === "DRAFT" && formData.cpId && (
        <div style={{ background: '#fffbe6', border: '1px solid #ffe58f', padding: '8px 16px', borderRadius: 4, marginBottom: 8, display: 'flex', alignItems: 'center', gap: 8 }}>
          <span>📝</span>
          <span>
            <strong>Saved Draft</strong> — This Contingency Purchase ({formData.cpId}) is a saved draft and has <strong>not</strong> been submitted for approval.
            Click <strong>Submit</strong> when ready to send it through the workflow.
          </span>
        </div>
      )}
      <CustomForm formData={formData} onFinish={onFinish}>
        {renderFormFields(
          hydratedCpDetails,
          handleChange,
          formData,
          "",
          null,
          setFormData,
          handleSearch,
          {
            addMaterialSection: addMaterialRow,
          }
        )}
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
        title="Contingency Purchase"
        processNo={formData?.cpId}
      />
    </Card>
  );
};
/*
const ContingencyPurchase = () => {
  const printRef = useRef();
  const [form] = Form.useForm();
  const [modalOpen, setModalOpen] = useState(false);
  const [submitBtnLoading, setSubmitBtnLoading] = useState(false);
  const [generatedCpId, setGeneratedCpId] = useState('');
  const [isPrintEnabled, setIsPrintEnabled] = useState(false);

  // Redux selectors
  const auth = useSelector((state) => state.auth);
  const actionPerformer = auth.userId;

  // Data states
  const [projects, setProjects] = useState([]);
  const [vendors, setVendors] = useState([]);
  const [materialDetailsMap, setMaterialDetailsMap] = useState({});
  const [materialOptions, setMaterialOptions] = useState([]);
  const [materialDescOptions, setMaterialDescOptions] = useState([]);

  // Form data state
  const [formData, setFormData] = useState({ materialDetails: [{}] });

  // --- Dynamic Field Population ---
  const populateDropdowns = async () => {
    try {
      const [projectResponse, materialResponse] = await Promise.all([
        axios.get('/api/project-master'),
        axios.get('/api/material-master'),
      ]);
      const vendorResponse = await axios.get('/api/vendor-master');

      // Format projects
      const formattedProjects = (projectResponse.data?.responseData || []).map(project => ({
        label: project.projectNameDescription,
        value: project.projectCode,
      }));
      setProjects(formattedProjects);

      // Format vendors
      const formattedVendors = (vendorResponse.data?.responseData || []).map(vendor => ({
        label: vendor.vendorName,
        value: vendor.vendorName,
      }));
      setVendors(formattedVendors);

      // Format materials
      const materials = materialResponse.data?.responseData || [];
      const materialMap = {};
      const descMap = {};

      materials.forEach(material => {
        materialMap[material.materialCode] = {
          materialDescription: material.description,
          uom: material.uom,
          unitPrice: material.unitPrice,
          currency: material.currency,
          materialCategory: material.category,
          materialSubCategory: material.subCategory
        };

        descMap[material.description] = {
          materialCode: material.materialCode,
          uom: material.uom,
          unitPrice: material.unitPrice,
          currency: material.currency,
          materialCategory: material.category,
          materialSubCategory: material.subCategory
        };
      });

      setMaterialOptions(materials.map(m => ({ label: m.materialCode, value: m.materialCode })));
      setMaterialDescOptions(materials.map(m => ({ label: m.description, value: m.description })));
      setMaterialDetailsMap({ ...materialMap, ...descMap });
    } catch (error) {
      console.error('Error fetching data:', error);
      message.error('Failed to load dropdown data');
    }
  };

  // --- handleChange Function ---
  const handleChange = (name, value) => {
    if (Array.isArray(name)) {
      const [section, index, field] = name;
      
      if (section === 'materialDetails') {
        setFormData(prev => {
          const updatedMaterials = [...prev.materialDetails];
          const materialData = materialDetailsMap[value] || {};

          if (field === 'materialCode' || field === 'materialDescription') {
            updatedMaterials[index] = {
              ...updatedMaterials[index],
              [field]: value,
              ...materialData,
              totalPrice: (materialData.unitPrice || 0) * (updatedMaterials[index]?.quantity || 0)
            };
          } else if (field === 'quantity' || field === 'unitPrice') {
            const quantity = field === 'quantity' ? value : updatedMaterials[index]?.quantity || 0;
            const unitPrice = field === 'unitPrice' ? value : updatedMaterials[index]?.unitPrice || 0;
            const calculateTotalPrice = quantity * unitPrice;
            if (calculateTotalPrice > 50000) {
              message.warning('Total price cannot exceed 50,000');
              return prev; 
            }
            updatedMaterials[index] = {
              ...updatedMaterials[index],
              [field]: value,
             // totalPrice: quantity * unitPrice
             totalPrice: calculateTotalPrice
            };
          } else {
            updatedMaterials[index] = {
              ...updatedMaterials[index],
              [field]: value
            };
          }
           
          return { ...prev, materialDetails: updatedMaterials };
        });
      }
    } else {
      setFormData(prev => ({ ...prev, [name]: value }));
    }
  };

  // --- Form Submission ---

  const onFinish = async () => {
    // Get first material detail item or empty object
    const material = formData.materialDetails[0] || {};
    
    const payload = {
      vendorsName: formData.vendorName,
      vendorsInvoiceNo: formData.vendorInvoiceNo,
      remarksForPurchase: formData.remarks,
      projectName: formData.projectName,
      projectDetail: formData.projectDetail,
      date: formData.date,
      createdBy: actionPerformer,
      amountToBePaid: formData.amountToBePaid,
      predifinedPurchaseStatement: formData.predefinedPurchaseStatement,
      uploadCopyOfInvoice: formData.uploadCopyOfInvoice,
      // Material fields from first array item
      materialCode: material.materialCode,
      materialDescription: material.materialDescription,
      quantity: material.quantity,
      unitPrice: material.unitPrice,
      // Additional fields from material details
      currency: material.currency,
      materialCategory: material.materialCategory,
      materialSubCategory: material.materialSubCategory,
      fileType: 'CP',
    };
  
    try {
      setSubmitBtnLoading(true);
      const { data } = await axios.post('/api/contigency-purchase', payload);
      
      setFormData(prev => ({
        ...prev,
        cpId: data?.responseData?.contigencyId
      }));
      setModalOpen(true);
      localStorage.removeItem('cpDraft');
    } catch (error) {
      message.error(error?.response?.data?.message || 'Failed to submit purchase');
    } finally {
      setSubmitBtnLoading(false);
    }
  };
  
  const handleSearch = async (value) => {
    try {
      const { data } = await axios.get(
        `/api/contigency-purchase/${value || formData.cpId}`
      );
  
      const cpData = data.responseData;
      setFormData({
        ...cpData,
        materialDetails: [{
          materialCode: cpData.materialCode,
          materialDescription: cpData.materialDescription,
          quantity: cpData.quantity,
          unitPrice: cpData.unitPrice,
          uom: cpData.uom,
          currency: cpData.currency,
          materialCategory: cpData.materialCategory,
          materialSubCategory: cpData.materialSubCategory,
          totalPrice: cpData.quantity * cpData.unitPrice
        }],
        predefinedPurchaseStatement: cpData.predifinedPurchaseStatement,
        uploadCopyOfInvoice: cpData.uploadCopyOfInvoice,
        amountToBePaid: cpData.amountToBePaid,
        remarks: cpData.remarksForPurchase,
        // date: dayjs(cpData.date, 'DD/MM/YYYY'),
        vendorName: cpData.vendorsName,
        vendorInvoiceNo: cpData.vendorsInvoiceNo,
        projectName: cpData.projectName
      });
    } catch (error) {
      console.error("Search Error:", error);
      message.error(error?.response?.data?.responseStatus?.message || "Error fetching CP details");
    }
  };

  // --- Draft Handling ---
  useEffect(() => {
    const draft = localStorage.getItem('cpDraft');
    if (draft) {
      setFormData(JSON.parse(draft));
      message.success('Loaded draft data');
    }
  }, []);

  useEffect(() => {
    localStorage.setItem('cpDraft', JSON.stringify(formData));
  }, [formData]);

  const handlePrint = useReactToPrint({
    content: () => printRef.current,
  });

  useEffect(()=>{
    populateDropdowns();
  },[]);

  // --- Prepare Hydrated Fields ---
  const hydratedCpDetails = CpDetails.map(section => {
    if (section.fieldList) {
      return {
        ...section,
        fieldList: section.fieldList.map(field => {
          if (field.name === 'projectName') return { ...field, options: projects };
          if (field.name === 'vendorName') return { ...field, options: vendors }; // Add this line
          return field;
        })
      };
    }
    if (section.children) {
      return {
        ...section,
        children: section.children.map(child => {
          if (child.name === 'materialCode') return { ...child, options: materialOptions };
          if (child.name === 'materialDescription') return { ...child, options: materialDescOptions };
          return child;
        })
      };
    }
    return section;
  });

  return (
    <Card className="a4-container" ref={printRef}>
      <Heading title="Contingency Purchase" />
      <CustomForm formData={formData} onFinish={onFinish}>
        {renderFormFields(
          hydratedCpDetails,
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
          draftDataName="cpDraft"
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
        title="Contingency Purchase"
        processNo={formData?.cpId}
      />
    </Card>
  );
};*/

export default ContingencyPurchase;