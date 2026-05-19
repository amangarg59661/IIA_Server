import { Card, Form, message, Select } from "antd";
import React, { useEffect, useRef, useState } from "react";
import Heading from "../../../components/DKG_Heading";
import CustomForm from "../../../components/DKG_CustomForm";
import { renderFormFields } from "../../../utils/CommonFunctions";
import ButtonContainer from "../../../components/ButtonContainer";
import { useReactToPrint } from "react-to-print";
import axios from "axios";
import { useSelector } from "react-redux";
import CustomModal from "../../../components/CustomModal";
import { gtOgpFields, ogpFields, ogpFieldsGiRejected, ogpFieldsPo } from "./InputFields";
import { Modal } from "antd";  
import { assetDisposalFields } from "./InputFields";

import { set } from "lodash";
const confirmReturnable = () => {
  return new Promise((resolve, reject) => {
    Modal.confirm({
      title: "Confirm Return Date",
      content: "Please confirm the Return Date. Do you want to proceed?",
      onOk() {
        resolve(true);
      },
      onCancel() {
        resolve(false);
      },
    });
  });
};

const Ogp = () => {
    const { materialMaster, locationMaster, userMaster } =
    useSelector((state) => state.masters) || [];

    const locationMasterObj = locationMaster?.reduce((acc, obj) => {
      acc[obj?.locationCode] = obj.locationName;
      return acc;
    }, {});

    const umObj = userMaster?.reduce((obj, item) => {
    obj[item.userId] = item.userName;
    return obj;
  }, {});
  const printRef = useRef();
  const handlePrint = useReactToPrint({
    content: () => printRef.current,
  });
const senderName = useSelector(state => state.auth.userName)

  const [modalOpen, setModalOpen] = useState(false);
  const [submitBtnLoading, setSubmitBtnLoading] = useState(false);
  const [formData, setFormData] = useState({
    issueNoteId: "",
    ogpDate: null,
    materialDtlList: [],
    senderName: senderName
  });

  const handleChange = (fieldName, value) => {
    if (fieldName === "issueNoteId" && formData.type === "Rejected Items GI") {
    setFormData(prev => ({ ...prev, issueNoteId: value }));
    console.log("issueNoteId:", value); 

    handleSearchRejectedGI(value);
    return;
}
   if (fieldName === "ogpType") {
    if (value === "Non Returnable") {
      setFormData((prev) => ({ ...prev, [fieldName]: value, dateOfReturn: null }));
    } else {
      setFormData((prev) => ({ ...prev, [fieldName]: value }));
    }
    return;
  }

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

  const {locatorMaster} = useSelector(state => state?.masters);

  const locatorMasterObj = locatorMaster?.reduce((acc, obj) => {
    const { value, label } = obj;
    acc[value] = label;
    return acc;
  }, {});

const handleSearchRejectedGI = async (issueNoteId) => {
  if (!issueNoteId) return;

  try {
    const { data } = await axios.get(
      `/api/process-controller/getSubProcessDtls?processNo=${issueNoteId}&processStage=GI`
    );

    const giDtls = data?.responseData?.giDtls;
    const gprnDtls = data?.responseData?.gprnDtls;

    setFormData(prev => ({
      ...prev,
      ...giDtls,
      issueNoteId: giDtls?.inspectionNo,
      type: "Rejected Items GI",
      ogpType: prev.ogpType,
      locationId: gprnDtls?.locationId,
      senderName: gprnDtls?.receivedName,
      ogpDate: prev.ogpDate,
      receiverName: gprnDtls?.vendorName,
      receiverLocation: gprnDtls?.consigneeDetail,
    }));
  } catch (error) {
    message.error(error?.response?.data?.responseStatus?.message || "Error fetching Rejected GI data.");
  }
};



  const handleSearch = async () => {

    if(formData.type === "Goods Transfer"){
      try{
        const {data} = await axios.get(`/api/process-controller/getSubProcessDtls?processNo=${formData.gtId}&processStage=GT`);
        const gtData = data?.responseData;
        console.log("Data.res: ", gtData);
        setFormData(prev => {
          return {
            ...prev, 
            ...gtData,
            gtId: formData.gtId,
            senderLocationIdDesc: locationMasterObj[gtData?.senderLocationId],
            receiverLocationIdDesc: locationMasterObj[gtData?.receiverLocationId],
            senderCustodianIdDesc: umObj[gtData?.senderCustodianId],
            receiverCustodianIdDesc: umObj[gtData?.receiverCustodianId],
            gtDate: gtData?.gtDate,
            materialDtlList: gtData?.materialDtlList?.map(item => ({...item, receiverLocatorIdDesc: locatorMasterObj[item.receiverLocatorId], senderLocatorIdDesc: locatorMasterObj[item.senderLocatorId]}))
          }
        })
        }
      catch(error){
        message.error(error?.response?.data?.responseStatus?.message || "Error fetching GT data.");
      }
        return
    }

    if(formData.type === "PO"){
      const {data} = await axios.get(`api/purchase-orders/byId`, {params: {poId:formData.issueNoteId}})
          setFormData(prev => ({
            ...data?.responseData,
            type: "PO",
            ogpType: prev.ogpType,
            issueNoteId: data.responseData?.poId,
            ogpDate: prev.ogpDate,
            senderName: prev.senderName,
            materialDtlList: data?.responseData?.purchaseOrderAttributes || []
          }));

          return;
    }
   /* if(formData.type === "Rejected Items GI"){
      const {data} = await axios.get(`/api/process-controller/getSubProcessDtls?processNo=${formData.issueNoteId}&processStage=GI`);
      setFormData(prev => ({
        ...data?.responseData?.giDtls,
        issueNoteId: data?.responseData?.giDtls?.inspectionNo,
        type: "Rejected Items GI",
        ogpType: prev.ogpType,
        locationId: data?.responseData?.gprnDtls?.locationId,
        senderName: prev.senderName,
        ogpDate: prev.ogpDate,
      }))
      return
    }*/
     if (formData.type === "Asset Disposal") {
      if (!formData.auctionId) {
        message.error("Please enter Disposal ID before searching.");
        return;
      }
      const { data } = await axios.get(`/api/process-controller/SearchByAuctionId?auctionId=${formData.auctionId}`);
      const disposalData = data?.responseData;
      setFormData(prev => ({
        ...prev,
        ...disposalData,
        type: "Asset Disposal",
      }));
      return;
    }

    try {
      const {data} = await axios.get(`/api/process-controller/getSubProcessDtls?processNo=${formData.issueNoteId}&processStage=ISN`);
      setFormData(prev => ({
        ...data?.responseData,
        issueNoteId: data.responseData?.issueNoteNo,
        type: "Goods Issue",
        ogpType: prev.ogpType,
        senderName: prev.senderName,
        ogpDate: prev.ogpDate,
        materialDtlList: data?.responseData?.materialDtlList?.map(item => ({...item, locatorDesc: locatorMasterObj[parseInt(item.locatorId)]}))
      }));
    } catch(error) {
      message.error(error?.response?.data?.responseStatus?.message || "Error fetching ISN data.");
    }
  }

  const {userId} = useSelector(state => state.auth);

  console.log("Fprmdata:" , formData);


  const onFinish = async () => {

    if (!formData.ogpDate) {
      message.error("Please enter the OGP Date before submitting.");
      return;
    }
    if (formData.ogpType === "Returnable") {
    if (!formData.dateOfReturn) {
      message.error("Please enter the Return Date before submitting.");
      return;
    }
    const confirmed = await confirmReturnable();
    if (!confirmed) return;
  }
  if(formData.type === "Goods Transfer"){
    const pd = {...formData, createdBy: userId};
    try{
      setSubmitBtnLoading(true);
      const {data} = await axios.post("/api/process-controller/saveGtOgp", pd);
       setFormData(prev => ({
        ...prev,
        ogpId: data?.responseData?.processNo
      }));
      localStorage.removeItem("ogpDraft");
      setModalOpen(true);
    }
    catch(error) {
      message.error(error?.response?.data?.responseStatus?.message || "Failed to save OGP.");
    } finally {
      setSubmitBtnLoading(false);
    }
    return
  }
  if (formData.type === "Asset Disposal") {
    const pd = { ...formData, createdBy: userId };
    try {
      setSubmitBtnLoading(true);
      const { data } = await axios.post("/api/process-controller/saveAssetDisposal", pd);
      setFormData(prev => ({
        ...prev,
        ogpId: data?.responseData?.processNo
      }));
      localStorage.removeItem("ogpDraft");
      setModalOpen(true);
    } catch (error) {
      message.error(error?.response?.data?.responseStatus?.message || "Failed to save Asset Disposal OGP.");
    } finally {
      setSubmitBtnLoading(false);
    }
    return;
  }

    const payload = {...formData, giId: formData.issueNoteId, createdBy: userId};

    try {
      setSubmitBtnLoading(true);
      
      const endpoint = formData.type === "PO" ? "/api/process-controller/savePoOgp" : formData.type === "Rejected Items GI" ? "/api/process-controller/saveOgpRejectedGi" : "/api/process-controller/saveOgp";
      const {data} = await axios.post(endpoint, payload);

      setFormData(prev => ({
        ...prev,
        ogpId: data?.responseData?.processNo
      }));

      localStorage.removeItem("ogpDraft");
      setModalOpen(true);
    } catch(error) {
      message.error(error?.response?.data?.responseStatus?.message || "Failed to save OGP.");
    } finally {
      setSubmitBtnLoading(false);
    }
  };

  useEffect(() => {
    const draft = localStorage.getItem("ogpDraft");
    if(draft) {
      setFormData(JSON.parse(draft));
      message.success("Form loaded from draft.");
    }
  }, []);

  const getFilteredOgpFields = () => {
  if (formData.ogpType === "Non Returnable") {
    return ogpFields.map(section => {
      if (!section.fieldList) return section;
      return {
        ...section,
        fieldList: section.fieldList.filter(f => f.name !== "dateOfReturn")
      }
    });
  }
  return ogpFields;
};



const getFilteredOgpFieldsPo = () => {
  if (formData.ogpType === "Non Returnable") {
    return ogpFieldsPo.map(section => {
      if (!section.fieldList) return section;
      return {
        ...section,
        fieldList: section.fieldList.filter(f => f.name !== "dateOfReturn")
      }
    });
  }
  return ogpFieldsPo;
};

const getFilteredOgpFieldsRejectedGI = () => {
  return ogpFieldsGiRejected.map(section => {
    if (!section.fieldList) return section;
    return {
      ...section,
      fieldList: section.fieldList.filter(f => f.name !== "dateOfReturn" || formData.ogpType === "Returnable")
    };
  });
};


  

  return (
    <Card className="a4-container" ref={printRef}>
      <Heading title="Outward Gate Pass" />
      <CustomForm formData={formData} onFinish={onFinish}>
        <h1 className="font-semibold">Order Details</h1>
        <div className="grid md:gap-x-4 md:gap-y-2 md:grid-cols-3">
          <Form.Item name="type" label="Type">
            <Select options={[{label: "PO", value: "PO"}, {label: "Goods Issue", value: "Goods Issue"}, {label: "Rejected Items GI", value: "Rejected Items GI"}, {label: "Goods Transfer", value: "Goods Transfer"},{label: "Asset Disposal", value: "Asset Disposal"}]} onChange={(val) => handleChange("type", val)}/>
          </Form.Item>
        </div>
        
        {
          formData.type === "PO" && renderFormFields(getFilteredOgpFieldsPo(), handleChange, formData, "", null, setFormData, handleSearch)
        }
        {
          formData.type === "Goods Issue" && renderFormFields(getFilteredOgpFields(), handleChange, formData, "", null, setFormData, handleSearch)
        }
        {
          formData.type === "Rejected Items GI" && renderFormFields(getFilteredOgpFieldsRejectedGI(), handleChange, formData, "", null, setFormData,  handleSearchRejectedGI)
        }
        {
          formData.type === "Goods Transfer" && renderFormFields(gtOgpFields, handleChange, formData, "", null, setFormData, handleSearch)
        }
        {
          formData.type === "Asset Disposal" && renderFormFields(assetDisposalFields, handleChange, formData, "", null, setFormData, handleSearch)
        }
        <ButtonContainer
          onFinish={onFinish}
          formData={formData}
          draftDataName="ogpDraft"
          submitBtnLoading={submitBtnLoading}
          submitBtnEnabled
          printBtnEnabled
          draftBtnEnabled
          handlePrint={handlePrint}
        />
      </CustomForm>
      <CustomModal isOpen={modalOpen} setIsOpen={setModalOpen} title="Outward Gate Pass" processNo={formData?.ogpId} />
    </Card>
  );
};

export default Ogp;
