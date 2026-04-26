import { Card, message } from 'antd'
import React, { useCallback, useEffect, useRef, useState } from 'react'
import Heading from '../../../components/DKG_Heading'
import CustomForm from '../../../components/DKG_CustomForm';
import { renderFormFields } from '../../../utils/CommonFunctions';
import ButtonContainer from '../../../components/ButtonContainer';
import { useReactToPrint } from 'react-to-print';
import { useSelector } from 'react-redux';
import axios from 'axios';
import CustomModal from '../../../components/CustomModal';
import dayjs from 'dayjs';
import { useLocation } from 'react-router-dom';
import PrintFormate from '../../../utils/PrintFormate'
import GprnPrintFormat from '../../../utils/GprnPrintFormat';
import GprnPoSearch from '../../../components/GprnPoSearch';
// Added by aman 
import {useLOVValues}  from "../../../hooks/useLOVValues.js";
// End





const GPRN = () => {
  //const printRef = useRef();
 /* const handlePrint = useReactToPrint({
    content: () => printRef.current,
  });*/

  const location = useLocation()

  const {roleId} = useSelector(state => state.auth)

  const processNo = location?.state?.processNo || null;

  const [fsDd, setFsDd] = useState([]);
  const [errorField, setErrorField] = useState(null);

  const [modalOpen, setModalOpen] = useState(false);
  const [submitBtnLoading, setSubmitBtnLoading] = useState(false);
  const [formData, setFormData] = useState({
    date: dayjs().format('DD/MM/YYYY'),
    deliveryDate: dayjs().format('DD/MM/YYYY'),
    supplyExpectedDate: dayjs().format('DD/MM/YYYY'),
  });
 // modified by Aman to make dynamic 
  const { lovValues: warrantyLOV, loading: loadingWarranty } = useLOVValues(8, 'warranty');
  // End
  const handleChange = (fieldName, value) => {
    if (typeof fieldName === 'string') {

      if (fieldName === "poId") {
        setFormData(prev => ({ ...prev, poId: value }))
        handleSearch(value)
        return;
      }
      setFormData(prev => ({ ...prev, [fieldName]: value }))
    }
    else {
      setFormData(prev => {
        const prevMaterialDtlList = prev.materialDtlList
        prevMaterialDtlList[fieldName[1]][fieldName[2]] = value

        // Calculate total amount when receivedQuantity changes
        // if (fieldName[2] === 'receivedQuantity') {
        //   const unitPrice = parseFloat(prevMaterialDtlList[fieldName[1]].unitPrice || 0);
        //   const quantity = parseFloat(value || 0);
        //   prevMaterialDtlList[fieldName[1]].totalAmount = (unitPrice * quantity).toFixed(2);
        // }

        if (fieldName[2] === 'receivedQuantity') {
  const pendingQty = parseFloat(prevMaterialDtlList[fieldName[1]].orderedQuantity || 0);
  const enteredQty = parseFloat(value || 0);

  if (enteredQty > pendingQty) {
    const rowIndex = fieldName[1];
    message.error(`Received quantity cannot exceed pending quantity (${pendingQty})`);
    setErrorField({ rowIndex, field: 'receivedQuantity' });
    return prev; // ❗ prevent update
  }
  setErrorField(null);

  const unitPrice = parseFloat(prevMaterialDtlList[fieldName[1]].unitPrice || 0);
  prevMaterialDtlList[fieldName[1]].totalAmount = (unitPrice * enteredQty).toFixed(2);
}

        let totQuant = 0
        prevMaterialDtlList.forEach(mat => {
          totQuant += parseFloat(mat.receivedQuantity || 0)
        })

        return { ...prev, materialDtlList: prevMaterialDtlList, totalQuantity: totQuant }
      })
    }
  }

  const { userId } = useSelector(state => state.auth)

  const onFinish = async () => {
  //  const locationId = formData.fieldStation;
    const selectedLocation = fsDd.find(
      loc => loc.label === formData.fieldStation || loc.value === formData.fieldStation
    );

    const locationId = selectedLocation ? selectedLocation.value : null;

    if (!locationId) {
      message.error("Invalid Field Station selected. Please recheck.");
      return;
    }
    const payload = { ...formData, fieldStation: locationId, locationId, createdBy: userId }

    if(processNo) {
      payload.processNo = processNo
    }

    try {
      const url = processNo ? `/api/process-controller/updateGprn` : `/api/process-controller/saveGprn`;
      setSubmitBtnLoading(true)
      const { data } = await axios.post(url, payload)

      setFormData({
        ...formData,
        gprnNo: data?.responseData?.processNo
      })

      localStorage.removeItem("gprnDraft")
      setModalOpen(true)

    }
    catch (error) {
      message.error(error?.response?.data?.responseStatus?.message || "Failed to save GPRN.");
      ;
    }
    finally {
      setSubmitBtnLoading(false)
    }
  }

  const handleSearch = useCallback(async (value) => {
    try {
      const { data } = await axios.get(`api/purchase-orders/byId`, {params: {poId : value ? value : formData.poId}})

      const { data: vendorData } = await axios.get(`/api/vendor-master/${data?.responseData?.vendorId}`)
      const indentId = data?.responseData?.indentIds[0];
      const { data: indentData } = await axios.get(`/api/indents/byId` , {params: {indentId}})

       // Resolve locationId at search time using current fsDd
    const consigneesAddress = data?.responseData?.consignesAddress;
    const matchedLocation = fsDd.find(
      loc => loc.label === consigneesAddress || loc.value === consigneesAddress
    );
    const resolvedFieldStation = matchedLocation ? matchedLocation.value : consigneesAddress;

      setFormData({
        poId: data?.responseData?.poId,
        vendorId: data?.responseData?.vendorId,
        vendorName: vendorData?.responseData?.vendorName,
        vendorEmail: vendorData?.responseData?.emailAddress,
        vendorContactNo: vendorData?.responseData?.contactNo,
        project: data?.responseData?.projectName || "N/A",
        indentorName: indentData?.responseData?.indentorName,
        indentId: indentData?.responseData?.createdBy,
        consigneeDetail: data?.responseData?.consignesAddress,
        fieldStation: resolvedFieldStation,
        materialDtlList: data?.responseData?.purchaseOrderAttributes?.map((mat, idx) => ({ ...mat, materialDesc: mat.materialDescription, uomId: mat.uom,warrantyTerms: data?.responseData?.warranty, orderedQuantity: mat.totalQuantity - mat.receivedQuantity,totalQuantity: mat.totalQuantity, quantityDelivered: mat.receivedQuantity || 0 , receivedQuantity:"" })),
        date: dayjs().format('DD/MM/YYYY'),
        deliveryDate: data?.responseData?.deliveryDate || "", 
        supplyExpectedDate: dayjs().format('DD/MM/YYYY'),
      })
    }
    catch (error) {
      
      message.error(error?.response?.data?.responseStatus?.message || "Error fetching data.");
    }
  }, [formData.poId])

  useEffect(() => {
    const gprnDraft = localStorage.getItem("gprnDraft");
    if (gprnDraft) {
      setFormData(JSON.parse(gprnDraft))
      message.success("Form loaded from draft.")
    }
  }, [])

  const [pendingGprnList, setPendingGprnList] = useState([])

  const [userDd, setUserDd] = useState([]);

  const populatePendingGprn = async () => {
    try {
      const [gprnResponse, locationResponse, userResponse] = await Promise.all([
        axios.get("/api/process-controller/getPendingAllPoDataForGprn"),
        axios.get("/api/location-master"),
        axios.get("/api/userMaster")
      ]);

      // const formattedList = (gprnResponse.data?.responseData?.pendingGprnList || []).map(item => ({
      //   label: item,
      //   value: item
      // }));
      const rawList = gprnResponse.data?.responseData?.pendingGprnList || [];

const formattedPoList = rawList.map(item => ({
  poId: item.poId,
  vendorName: item.vendorName,
  projectName: item.projectName,
  createdDate: item.createdDate,
  indentIds: item.indentIds,     // array
  materials: item.materials  // each material {materialDesc, orderQty, receivedQty, pendingQty}
}));

setPendingGprnList(formattedPoList);

      
      const formattedLocations = (locationResponse.data?.responseData || []).map(location => ({
        label: location.locationName,
        value: location.locationCode
      }));

      const formattedUsers = (userResponse.data?.responseData || []).map(user => ({
        label: user.userName,
        value: user.userId
      }));

      //setPendingGprnList(formattedList);
      setFsDd(formattedLocations);
      setUserDd(formattedUsers);
    }
    catch (error) {
      
      message.error(error?.response?.data?.responseStatus?.message || "Error fetching data.");
    }
  }

  const handleGprnSearch = () => {
    console.log("CALLED", formData.gprnNo)
  }

  const searchByProcessNo = async (processNo) => {
    try {
      const { data } = await axios.get(`/api/process-controller/getSubProcessDtls?processStage=GPRN&processNo=${processNo ? processNo : formData.gprnNo}`)
      const { data: vendorData } = await axios.get(`/api/vendor-master/${data?.responseData?.vendorId}`)
      setFormData({
        ...data?.responseData,
        vendorName: vendorData?.responseData?.vendorName,
        vendorEmail: vendorData?.responseData?.emailAddress,
        vendorContactNo: vendorData?.responseData?.contactNo,
        materialDtlList: data?.responseData?.materialDtlList.map((mat, idx) => ({...mat, totalAmount: (mat.unitPrice || 0) * (mat.receivedQuantity || 0) })),
      })
    }
    catch (error) {
     message.error(error?.response?.data?.responseStatus?.message || "Error fetching data.");
    }
  }

  useEffect(() => {
    populatePendingGprn();
  }, [])

  useEffect(() => {
    if (processNo) {
      searchByProcessNo(processNo)
    }
  }, [processNo, handleSearch])

  const generalDtls = [
    {
      heading: "Purchase & Order Details", // optional
      colCnt: 5, // optional
      fieldList: [
         {
           name: "poId",
          label: "PO No.",
           type: "text",
         //  options: pendingGprnList,
         },
       
        {
          name: "gprnNo", // required
          label: "GPRN No", // optional
          type: "search", // required
          onSearch: () => searchByProcessNo()
          // disabled: true, //optional
        },
        {
          name: "date",
          label: "Date",
          type: "date",
          required: true
        },
        {
          name: "project",
          label: "Project",
          type: "text",
          // required: true,
          span: 2 // optional
        }

      ]
    },
    {
      heading: "Vendor Details",
      colCnt: 4,
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
          span: 2,
          required: true
        },
        {
          name: "vendorEmail",
          label: "Vendor Email",
          type: "text",
          span: 2,
          required: true
        },
        {
          name: "vendorContactNo",
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
          type: "select",
          options: fsDd,
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
          required: true,
          disabled: true
        },
        {
          name: "materialDesc",
          label: "Description",
          type: "text",
          span: 3,
          required: true,
          disabled: true,
        },
        {
          name: "uomId",
          label: "UOM",
          type: "text",
          span: 1,
          required: true,
          disabled: true
        },
        {
          name: "warrantyTerms",
          label: "Warranty",
          type: "select",
          span: 2,
          required: true,
          // Modified by aman to make dynamic
          options: warrantyLOV.length > 0
    ? warrantyLOV.map(lov => ({ label: lov.lovDisplayValue, value: lov.lovValue }))
    : []
    // 
          
        },
        {
          name: "totalQuantity",
          label: "Ordered Quantity",
          type: "text",
          disabled: true,
        },
        {
          name: "orderedQuantity",
          label: "Pending Quantity",
          type: "text",
          span: 3,
          required: true,
          disabled: true
        },
        {
          name: "quantityDelivered",
          label: "Delivered Quantity",
          type: "text",
          span:3,
          required: true,
          disabled:true,
        },
        {
          name: "receivedQuantity",
          label: "Received Quantity",
          type: "text",
          required: true,
          
        },
        {
          name: "unitPrice",
          label: "Unit Price",
          type: "text",
          required: true,
          disabled: true
        },
        {
          name: "totalAmount",
          label: "Total Amount",
          type: "text",
          span:2,
          required: true,
          disabled: true,
        },
        {
          name: "makeNo",
          label: "Make",
          type: "text",
          span: 2,
          // required: true,
          // disabled: true
        },
        {
          name: "modelNo",
          label: "Model No.",
          type: "text",
          span: 2,
          // required: true,
          // disabled: true
        },
        {
          name: "serialNo",
          label: "Serial No.",
          type: "text",
          span: 2,
          // required: true,
          // disabled: true
        },
        {
          name: "category",
          label: "Category",
          type: "text",
          required: true,

        },
        {
          name: "note",
          label: "Note",
          type: "text",
          span: 5,
          // required: true,
        },
        {
          name: "imageBase64",
          label: "Material Photograph",
          type: "multiImage",  // changed from "image" to "multiImage"
          span: 3,
          required: true,
          accept: "image/*",
          multiple: true  // added multiple property
        }
      ]
    },
    {
      heading: "Consignee & Warranty Information",
      colCnt: 3,
      fieldList: [
        {
          name: "consigneeDetail",
          label: "Consignee Details",
          type: "select",
          options: fsDd,
          required: true,
          span: 2
        },
        // {
        //   name: "warrantyYears",
        //   label: "Warranty Years",
        //   type: "text",
        //   required: true
        // },
        // {
        //   name: "warranty",
        //   label: "Warranty",
        //   type: "text",
        //   span: 3
        // }
      ]
    },
    {
      heading: "Acceptance Details",
      colCnt: 4,
      fieldList: [
        {
          name: "receivedBy",
          label: "Received By",
          type: "select",
          options: userDd,
          required: true
        }
      ]
    }
  ]
  
      const printComponentRef = useRef(); 
      const handlePrint = useReactToPrint({
  content: () => printComponentRef.current,
  documentTitle: `GPRN - ${formData?.gprnNo || "Draft"}`
});

if(roleId === 16){
  return (
    <Card className='a4-container'>
      <h1 className="font-semibold text-center text-xl text-red-500">
      You dont have access to create GPRN
      </h1>
    </Card>
  )
}


  return (
    <Card className='a4-container'>
      <Heading title="Goods Provisional Receipt Note" />
       {pendingGprnList?.length > 0 && (
      <GprnPoSearch
        poArray={pendingGprnList}
        setFormData={setFormData}
        handleSearch={handleSearch}
      />
    )}
      <CustomForm formData={formData} onFinish={onFinish}>
        {renderFormFields(generalDtls, handleChange, formData, "", null, setFormData, handleSearch)}
        <ButtonContainer
          onFinish={onFinish}
          formData={formData}
          draftDataName="gprnDraft"
          submitBtnLoading={submitBtnLoading}
          submitBtnEnabled
          printBtnEnabled
          draftBtnEnabled
          handlePrint={handlePrint}
        />
      </CustomForm>
      <CustomModal isOpen={modalOpen} setIsOpen={setModalOpen} title="GPRN" processNo={processNo ? formData?.processNo : formData?.gprnNo} update />

      <div style={{ display: "none" }}>
               <GprnPrintFormat ref={printComponentRef} formData={formData} />

             </div>
    </Card>
  )
}

export default GPRN
