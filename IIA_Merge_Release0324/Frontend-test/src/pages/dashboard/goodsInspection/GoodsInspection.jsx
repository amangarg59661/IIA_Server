import { Card, message } from "antd";
import React, { useCallback, useEffect, useRef, useState } from "react";
import Heading from "../../../components/DKG_Heading";
import CustomForm from "../../../components/DKG_CustomForm";
import { renderFormFields } from "../../../utils/CommonFunctions";
// import { generalDtls } from "./InputFields";
import ButtonContainer from "../../../components/ButtonContainer";
import { useReactToPrint } from "react-to-print";
import axios from "axios";
import { useSelector } from "react-redux";
import CustomModal from "../../../components/CustomModal";
import { useLocation } from "react-router-dom";
import { Input } from 'antd'; 
import GprnSearchDropdown from "../../../components/GprnSearchDropdown";
const GoodsInspection = () => {
  const printRef = useRef();
  const handlePrint = useReactToPrint({
    content: () => printRef.current,
  });

  const location = useLocation();
  const processNo = location?.state?.processNo || null;

  const [modalOpen, setModalOpen] = useState(false);
  const [submitBtnLoading, setSubmitBtnLoading] = useState(false);
  const [formData, setFormData] = useState({
    gprnNo: processNo || "",
    materialDtlList: []
  });

  const handleChange = (fieldName, value) => {
    if(typeof fieldName === 'string')
      setFormData(prev => ({...prev, [fieldName]: value}))
    else{
      setFormData(prev => {
        const prevMaterialDtlList = [...prev.materialDtlList];
        if(fieldName[2] === "acceptedQuantity"){
          const acceptedQuantity = parseFloat(value);
          const rejectedQuantity = parseFloat(prevMaterialDtlList[fieldName[1]].receivedQuantity) - acceptedQuantity;

          if(rejectedQuantity + acceptedQuantity !== parseFloat(prevMaterialDtlList[fieldName[1]].receivedQuantity)){
            message.error("Total accepted quantity must be equal to received quantity.");
            prevMaterialDtlList[fieldName[1]].acceptedQuantity = 0;
            prevMaterialDtlList[fieldName[1]].rejectedQuantity = 0;
            prevMaterialDtlList[fieldName[1]].rejectReason = '';
            return {...prev, materialDtlList: prevMaterialDtlList};
          }
          
          prevMaterialDtlList[fieldName[1]].rejectedQuantity = rejectedQuantity;
          prevMaterialDtlList[fieldName[1]].acceptedQuantity = acceptedQuantity;
          
          // Clear rejection reason if rejected quantity is 0
          if (rejectedQuantity <= 0) {
            prevMaterialDtlList[fieldName[1]].rejectReason = '';
          }
          
          return {...prev, materialDtlList: prevMaterialDtlList};
        }
        prevMaterialDtlList[fieldName[1]][fieldName[2]] = value;
        return {...prev, materialDtlList: prevMaterialDtlList}  
      })
    }
  }
const handleGISearch = async () => {
  try {
    const { data } = await axios.get(`/api/process-controller/getSubProcessDtls?processStage=GI&processNo=${formData?.giNo}`);
    setFormData(prev => ({
      ...prev,
      ...data?.responseData,
      giNo: data.responseData?.processId,
    }));
  } catch (error) {
    message.error(error?.response?.data?.responseStatus?.message || "Error fetching GI data.");
  }
};

  /*

  const handleSearch = useCallback(async () => {
    try {
      
      const {data} = await axios.get(`/api/process-controller/getSubProcessDtls?processStage=GPRN&processNo=${formData?.gprnNo}`);
      setFormData({...data?.responseData, gprnNo: data.responseData?.processId});
    } catch(error) {
      message.error(error?.response?.data?.responseStatus?.message || "Error fetching data.");
    }
  }, [formData.gprnNo])*/
  const mergeData = (giDtls = {}, gprnDtls = {}) => {
  return {
    // From giDtls
    inspectionNo: giDtls.inspectionNo || '',
    gprnNo: giDtls.gprnNo || '',
    installationDate: giDtls.installationDate || '',
    commissioningDate: giDtls.commissioningDate || '',
    materialDtlList: giDtls.materialDtlList || [],

    // Selected fields from gprnDtls
    date: gprnDtls.date || '',
    challanNo: gprnDtls.challanNo || '',
    vendorId: gprnDtls.vendorId || '',
    vendorEmail: gprnDtls.vendorEmail || '',
    vendorName: gprnDtls.vendorName || '',
    vendorContact: gprnDtls.vendorContact || '',
    fieldStation: gprnDtls.fieldStation || '',
    indentorName: gprnDtls.indentorName || '',
    locationId: gprnDtls.locationId || giDtls.locationId || '',
    deliveryDate: gprnDtls.deliveryDate,
    supplyExpectedDate: gprnDtls.supplyExpectedDate,
    poId: gprnDtls.poId,
    gprnNo:gprnDtls.processId,
    consigneeDetail:gprnDtls.consigneeDetail

  };
};

  const handleSearch = useCallback(async (value, isGprnSearch) => {
  if (!value) {
    message.warning("Please enter a valid value.");
    return;
  }

  try {
    let data;
    
    if (!isGprnSearch) {
      // GI API call
      const response = await axios.get(`/api/process-controller/getSubProcessDtls?processStage=GI&processNo=${value}`);
      data = response.data?.responseData?.giDtls;
      const formData = mergeData(response.data?.responseData?.giDtls, response.data?.responseData?.gprnDtls);
      formData.giNo = data?.inspectionNo || "";
      setFormData(formData);
    } else{
      // GPRN API call
      const response = await axios.get(`/api/process-controller/getSubProcessDtls?processStage=GPRN&processNo=${value}`);
      data = response.data?.responseData;
  

    // Update form data based on the API response
    setFormData({
      ...data,
      gprnNo: data?.processId, // Update gprnNo with the processId from the response
    });}
  } catch (error) {
    message.error(error?.response?.data?.responseStatus?.message || "Error fetching data.");
  }
}, []);


  // const {userId} = useSelector(state => state.auth);
  const { userId, role } = useSelector(state => state.auth);

  const onFinish = async () => {
    // const payload = {...formData, createdBy: userId};
    const payload = {...formData, createdBy: userId, role};
    // const {data} = await axios.post("/api/process-controller/saveGi", payload);
    try {
      setSubmitBtnLoading(true);
      const isUpdate = !!formData.giNo; 
     const apiUrl = formData.giNo 
      ? "/api/process-controller/updateGi"  // For update
      : "/api/process-controller/saveGi";  // For create

    const { data } = await axios.post(apiUrl, payload);

      setFormData(prev => ({
        ...prev,
        giNo: data?.responseData?.processNo
      }));

      localStorage.removeItem("goodsInspectionDraft");
     // setModalOpen(true);
    if (isUpdate) {
      message.success("Updated Successfully.");
    } else {
      setModalOpen(true);
    }
    } catch(error) {
      message.error(error?.response?.data?.responseStatus?.message || "Failed to save Goods Inspection.");
    } finally {
      setSubmitBtnLoading(false);
    }
  };


  const generalDtls = [
    {
        heading: "Order Details", // optional
        colCnt: 5, // optional
        fieldList: [
          /*  {
            name: "gprnNo",
            label: "GPRN No",
            type:"search",
            //disabled: true,
            required: true,
            onSearch: () => handleSearch(formData.gprnNo, true),
            span: 2,
            // render: () => (
            // <Input
            // placeholder="Enter GPRN No"
            // value={formData.gprnNo}  
            // onChange={(e) => handleChange("gprnNo", e.target.value)}
            // onBlur={(e) => handleSearch(e.target.value, true)}
            // onPressEnter={(e) => handleSearch(e.target.value, true)}
            // />
            // )
          },*/
         {
          name: "gprnNo",
          label: "GPRN No",
          required: true,
          span: 2,
          type: "custom",
          render: () => (
            <GprnSearchDropdown
              label="GPRN No" 
              userId={userId}
              role={role}
              value={formData.gprnNo}
              onChange={(val) => {
              handleChange("gprnNo", val); 
              handleSearch(val, true); 
          }}
          />
        ),
      },
            {
              name: "poId",
              label: "PO Id.",
              type: "text",
              disabled: true,
              
              // required: true
          },
            {
                name: "giNo",
                label: "Gi No.",
               // disabled: true,
                span: 2,
                type: "search",
                onSearch: () => handleSearch(formData.giNo, false)
                //  render: () => (
                // <Input
                // placeholder="Enter GI No"
                // value={formData.giNo}
                // onChange={(e) => handleChange("giNo", e.target.value)}
                // onBlur={(e) => handleSearch(e.target.value, false)}

                // onPressEnter={(e) => handleSearch(e.target.value, false)}
                // />
                // )
            },
            {
                name: "date",
                label: "Date",
                type: "date",
                required: true
            },
            {
                name: "installationDate",
                label: "Installation Date",
                type: "date",
               // required: true
            },
            {
                name: "commissioningDate",
                label: "Commission Date",
                type: "date",
               // required: true
            },
            // {
            //     name: "project",
            //     label: "Project",
            //     type: "text",
            //     required: true,
            //     span: 2 // optional
            // }

        ]
    },
    {
        heading: "Vendor Details",
        // Modified by aman
        colCnt: 4,
        // conCnt:10,
        // End
        fieldList: [
            {
                name: "vendorId",
                label: "Vendor ID",
                type: "text",
                span: 2,
                required: true
            },
            {
                name: "vendorName",
                label: "Vendor Name",
                type: "text",
                // Modified by Aman
                span: 2,
                // span: 3,
                // End
                required: true
            },
            {
                name: "vendorEmail",
                label: "Vendor Email",
                type: "text",
                // Modified by Aman
                span: 2,
                // span: 3,
                // End
                required: true
            },
            {
                name: "vendorContact",
                label: "Vendor Contact",
                type: "text",
                span: 2,
                required: true
            }
        ]
    },
    {
        heading: "Delivery & Invoice Details",
        colCnt: 5,
        fieldList: [
            {
                name: "challanNo",
                label: "Challan/Invoice No.",
                type: "text",
                required: true,
                span: 2
            },
            {
                name: "deliveryDate",
                label: "Delivery Date",
                type: "date",
                required: true,
                span: 1
            },
            {
                name: "supplyExpectedDate",
                label: "Date of Supply",
                type: "date",
                required: true,
                span: 1
            },
            {
                name: "fieldStation",
                label: "Field Station",
                type: "text",
                required: true,
                span: 2
            },
            {
                name: "indentorName",
                label: "Indentor Name",
                type: "text",
                required: true,
                span: 2
            },
            {
              name: "gprnAmount",
              label: "Gprn Amount",
              type: "text",
              span: 2,
            },
             {
              name: "poAmount",
              label: "Po Amount",
              type: "text",
              span: 2,
            },
        ]
    },
    {
        heading: "Material Details",
        name: "materialDtlList",
        colCnt: 4,
        children: [
            {
                name: "materialCode",
                label: "Material Code",
                type: "text",
                span: 2,
                required: true
            },
            {
                name: "materialDesc",
                label: "Material Description",
                type: "text",
                span: 3,
                required: true
            },
            {
                name: "uomId",
                label: "UOM",
                type: "text",
                span: 1,
                required: true
            },

            // {
            //     name: "warranty",
            //     label: "Warranty",
            //     type: "text",
            //     span: 2,
            //     required: true
            // },
            // {
            //     name: "orderedQuantity",
            //     label: "Ordered Quantity",
            //     type: "text",
            //     required: true
            // },
            // {
            //     name: "quantityDelivered",
            //     label: "Quantity Delivered",
            //     type: "text",
            //     required: true
            // },
            {
                name: "receivedQuantity",
                label: "Received Quantity",
                type: "text",
                disabled: true,
                required: true
            },
            {
                name: "acceptedQuantity",
                label: "Accepted Quantity",
                type: "text",
                required: true
            },
            {
                name: "rejectedQuantity",
                label: "Rejected Quantity",
                type: "text",
                disabled: true,
                required: true
            },
          /*  {
                name: "rejectionType",
                label: "Rejection Type",
                type: "select",
                span: 2,
                options: [
                    {
                        label: "Permanent",
                        value: "permanent"
                    },
                    {
                        label: "Replacement",
                        value: "replacement"
                    }
                ],
            },
            {
              name: "rejectReason",
              label: "Reason for Rejection",
              type: "text",
              span: 2,
            },*/
          /*    ...(formData.materialDtlList?.some(item => parseFloat(item.rejectedQuantity) > 0)
      ? [
          {
            name: "rejectionType",
            label: "Rejection Type",
            type: "select",
            span: 2,
            options: [
              { label: "Permanent", value: "permanent" },
              { label: "Replacement", value: "replacement" },
            ],
          },
          {
            name: "rejectReason",
            label: "Reason for Rejection",
            type: "text",
            span: 2,
          },
        ]
      : []),*/
      ...(formData.materialDtlList?.some(item => parseFloat(item.rejectedQuantity) > 0)
  ? [
      {
        name: "rejectionType",
        label: "Rejection Type",
        type: "select",
        span: 2,
        required: formData.materialDtlList?.some(item => parseFloat(item.rejectedQuantity) > 0),
        options: [
          { label: "Permanent", value: "permanent" },
          { label: "Replacement", value: "replacement" },
        ],
      },
      {
        name: "rejectReason",
        label: "Reason for Rejection",
        type: "text",
        span: 2,
        required: formData.materialDtlList?.some(item => parseFloat(item.rejectedQuantity) > 0),
      },
    ]
  : []),

            {
                name: "installationReportBase64",
                label: "Installation Report",
                type: "image",
                span: 2,
               // required: true,
               // accept: "image/*"
            },
            // {
            //     name: "unitPrice",
            //     label: "Unit Price",
            //     type: "text",
            //     required: true
            // },
            // {
            //     name: "makeNo",
            //     label: "Make No.",
            //     type: "text",
            //     span: 2,
            //     required: true
            // },
            // {
            //     name: "modelNo",
            //     label: "Model No.",
            //     type: "text",
            //     span: 2,
            //     required: true
            // },
            // {
            //     name: "serialNo",
            //     label: "Serial No.",
            //     type: "text",
            //     span: 2,
            //     required: true
            // },
            // {
            //     name: "note",
            //     label: "Note",
            //     type: "text",
            //     span: 5,
            //     required: true
            // },
            // {
            //     name: "photographPath",
            //     label: "Photograph",
            //     type: "text",
            //     required: true
            // }
        ]
    },
     
    {
        heading: "Consignee & Warranty Information",
        colCnt: 3,
        fieldList: [
            {
                name: "consigneeDetail",
                label: "Consignee Details",
                type: "text",
                required: true,
                span: 2
            },
            // {
            //     name: "warrantyYears",
            //     label: "Warranty Years",
            //     type: "text",
            //     required: true
            // }
        ]
    },
    // {
    //     heading: "Quantity & Acceptance Details",
    //     colCnt: 4,
    //     fieldList: [
    //         {
    //             name: "receivedQty",
    //             label: "Received Quantity",
    //             type: "text",
    //             required: true
    //         },
    //         {
    //             name: "pendingQty",
    //             label: "Pending Quantity",
    //             type: "text",
    //             required: true
    //         },
    //         {
    //             name: "acceptedQty",
    //             label: "Accepted Quantity",
    //             type: "text",
    //             required: true
    //         },
    //         {
    //             name: "receivedBy",
    //             label: "Received By",
    //             type: "text",
    //             required: true
    //         }
    //     ]
    // },
    // {
    //     heading: "Goods Installation Details",
    //     colCnt: 4,
    //     fieldList: [
    //         {
    //             name: "goodsInpectionNo", // required
    //             label: "Goods Inpection No", // optional
    //             type:"text", // required
    //             disabled: true, //optional
    //             required: true // option
    //         },
    //         {
    //             name: "installationDate",
    //             label: "Installation Date",
    //             type: "date",
    //             required: true
    //         },
    //         {
    //             name: "commissioningDate",
    //             label: "Commissioning Date",
    //             type: "date",
    //             required: true
    //         },
            // {
            //     name: "uploadInstallationReport",
            //     label: "Upload Installation Report",
            //     type: "text",
            // }
        // ]
    // },
    // {
    //     heading: "Quantity Details",
    //     colCnt: 4,
    //     fieldList: [
    //         {
    //             name: "acceptedQuantity",
    //             label: "Accepted Quantity",
    //             type: "text",
    //             required: true
    //         },
    //         {
    //             name: "rejectedQuantity",
    //             label: "Rejected Quantity",
    //             type: "text",
    //             required: true
    //         }
    //     ]
    // },
    // {
    //     heading: "Return Details",
    //     colCnt: 4,
    //     fieldList:[
    //         {
    //             name: "goodsReturn",
    //             label: "Goods Return",
    //             type: "text",
    //             required: true,
    //             span: 2
    //         }
    //     ]
    // }
]



  useEffect(() => {
    const draft = localStorage.getItem("goodsInspectionDraft");
    if(draft) {
      setFormData(JSON.parse(draft));
      message.success("Form loaded from draft.");
    }
  }, []);

  useEffect(() => {
    if(processNo) {
      // setFormData({gprnNo: processNo})
     // handleSearch();
     handleSearch(processNo, false);
    }
  }, [processNo, handleSearch])



  


  return (
    <Card className="a4-container" ref={printRef}>
      <Heading title="Goods Inspection" />
      <CustomForm formData={formData} onFinish={onFinish}>
        {renderFormFields(generalDtls, handleChange, formData, "", null, setFormData, handleSearch)}
        <ButtonContainer
          onFinish={onFinish}
          formData={formData}
          draftDataName="goodsInspectionDraft"
          submitBtnLoading={submitBtnLoading}
          submitBtnEnabled
          printBtnEnabled
          draftBtnEnabled
          handlePrint={handlePrint}
        />
      </CustomForm>
      <CustomModal isOpen={modalOpen} setIsOpen={setModalOpen} title="Goods Inspection" processNo={formData?.giNo} />
    </Card>
  );
};

export default GoodsInspection;
