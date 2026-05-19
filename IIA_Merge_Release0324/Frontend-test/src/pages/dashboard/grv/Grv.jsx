import { Card, message } from "antd";
import React, { useEffect, useRef, useState } from "react";
import Heading from "../../../components/DKG_Heading";
import CustomForm from "../../../components/DKG_CustomForm";
import { renderFormFields } from "../../../utils/CommonFunctions";
import ButtonContainer from "../../../components/ButtonContainer";
import { useReactToPrint } from "react-to-print";
import axios from "axios";
import { useSelector } from "react-redux";
import CustomModal from "../../../components/CustomModal";
import { grvFields } from "./InputFields";


const Grv = () => {
  const printRef = useRef();
  const handlePrint = useReactToPrint({
    content: () => printRef.current,
  });

  const [modalOpen, setModalOpen] = useState(false);
  const [submitBtnLoading, setSubmitBtnLoading] = useState(false);
  const [formData, setFormData] = useState({
    giNo: "",
    materialDtlList: []
  });

  const handleChange = (fieldName, value) => {
    

    if(typeof fieldName === 'string')
      setFormData(prev => ({...prev, [fieldName]: value}))
    else{
      setFormData(prev => {
        const prevMaterialDtlList = [...prev.materialDtlList];
        prevMaterialDtlList[fieldName[1]][fieldName[2]] = value;
        return {...prev, materialDtlList: prevMaterialDtlList}  
      })
    }
  }
/*
  const handleSearch = async () => {
    try {
      const {data} = await axios.get(`/api/process-controller/getSubProcessDtls?processNo=${formData.giNo}&processStage=GI`);
      setFormData(prev => ({
        ...data?.responseData.giDtls,
        giNo: data.responseData?.giDtls?.inspectionNo,
        // grvNo: prev.grvNo,
        // date: prev.date
      }));
    } catch(error) {
      message.error(error?.response?.data?.responseStatus?.message || "Error fetching GI data.");
    }
  }*/
 const handleSearch = async () => {
  try {
    let apiUrl = "";

    // Check if giNo is provided (condition is true for GI)
    const isGiSearch = formData.giNo ? true : false;

    // Determine the API URL based on the condition
    if (isGiSearch) {
      apiUrl = `/api/process-controller/getSubProcessDtls?processNo=${formData.giNo}&processStage=GI`;
    } else if (formData.grvNo) { // If GI condition is false, check for GRV
      apiUrl = `/api/process-controller/getSubProcessDtls?processNo=${formData.grvNo}&processStage=GRV`;
    } else {
      message.error("Neither GI No nor GRV No is provided.");
      return;
    }

    // Fetch data from the appropriate API
    const { data } = await axios.get(apiUrl);

    // Update formData based on the response
    // if (isGiSearch) {
    //   // Update formData with GI details
    //   setFormData(prev => ({
    //     ...data?.responseData?.giDtls,
    //     giNo: data?.responseData?.giDtls?.inspectionNo,
    //     date: data?.responseData?.giDtls?.date, // If you want to set date
    //      materialDtlList: (data?.responseData?.giDtls?.materialDtlList || [])
    //   .filter(item => Number(item.rejectedQuantity) > 0),
    //   }));
    // }
    if (isGiSearch) {
  const list = data?.responseData?.giDtls?.materialDtlList || [];
  console.log("materialDtlList before filter:", list);
  console.log("after filter:", list.filter(item => Number(item.rejectedQuantity) > 0));

  setFormData(prev => ({
    ...data?.responseData?.giDtls,
    giNo: data?.responseData?.giDtls?.inspectionNo,
    date: data?.responseData?.giDtls?.date,
    materialDtlList: list.filter(item => Number(item.rejectedQuantity) > 0),
  }));
}
     else if (formData.grvNo) {
      // Update formData with GRV details
      setFormData(prev => ({
        ...data?.responseData?.grvDtls,
        grvNo: data?.responseData?.grvDtls?.grvNo,
        date: data?.responseData?.grvDtls?.date,
        materialDtlList: data?.responseData?.grvDtls?.materialDtlList.map(item => ({
          ...item,
          returnQuantity: item.returnQuantity, // Set return quantity
          returnType: item.returnType, // Set return type
        }))
      }));
    }

  } catch (error) {
    message.error(error?.response?.data?.responseStatus?.message || "Error fetching data.");
  }
};



  const {userId, locationId} = useSelector(state => state.auth);

  const onFinish = async () => {
    const payload = {...formData, locationId, createdBy: userId};

    try {
      setSubmitBtnLoading(true);
      const {data} = await axios.post("/api/process-controller/saveGrv", payload);

      setFormData(prev => ({
        ...prev,
        grvNo: data?.responseData?.processNo
      }));

      localStorage.removeItem("grvDraft");
      setModalOpen(true);
    } catch(error) {
      message.error(error?.response?.data?.responseStatus?.message || "Failed to save GRV.");
    } finally {
      setSubmitBtnLoading(false);
    }
  };

  useEffect(() => {
    const draft = localStorage.getItem("grvDraft");
    if(draft) {
      setFormData(JSON.parse(draft));
      message.success("Form loaded from draft.");
    }
  }, []);

  return (
    <Card className="a4-container" ref={printRef}>
      <Heading title="Goods Return Voucher" />
      <CustomForm formData={formData} onFinish={onFinish}>
        {renderFormFields(grvFields, handleChange, formData, "", null, setFormData, handleSearch)}
       
        <ButtonContainer
          onFinish={onFinish}
          formData={formData}
          draftDataName="grvDraft"
          submitBtnLoading={submitBtnLoading}
          submitBtnEnabled
          printBtnEnabled
          draftBtnEnabled
          handlePrint={handlePrint}
        />
      </CustomForm>
      <CustomModal isOpen={modalOpen} setIsOpen={setModalOpen} title="Goods Return Voucher" processNo={formData?.grvNo} />
    </Card>
  );
};

export default Grv;