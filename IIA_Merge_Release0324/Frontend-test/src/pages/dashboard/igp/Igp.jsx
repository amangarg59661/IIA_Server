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
import { igpFields, igpPoFields } from "./InputFields";
import { useLocation } from "react-router-dom";
import MaterialSearch from "../../../components/MaterialSearch";


const Igp = () => {
  const printRef = useRef();
  const handlePrint = useReactToPrint({
    content: () => printRef.current,
  });

  const location = useLocation();
  const state = location.state;
  ;

  const [modalOpen, setModalOpen] = useState(false);
  const [submitBtnLoading, setSubmitBtnLoading] = useState(false);
  const [formData, setFormData] = useState({
    ogpId: "",
    igpDate: null,
    materialDtlList: [],
    igpType: "materialIn"
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

  const {locatorMaster, userMaster} = useSelector(state => state?.masters);

  const indentList = userMaster?.filter(item => item.roleName === "Indent Creator").map(item => 
    ({
  label: item.departmentName
    ? `${item.userName} - ${item.departmentName}`
    : item.userName,
  value: item.userId
}));
    // ({label: item.userName, value: item.userId}));


  const {locationMaster} = useSelector(state => state.masters);

  const locationMasterOption = locationMaster?.map(item => ({label: item.locationName, value: item.locationCode}))

  const materialInFields = [
    {
        heading: "IGP Details",
        colCnt: 5,
        fieldList: [
            {
                name: "igpId",
                label: "IGP No",
                type: "text",
                disabled: true,
                span: 2,
                // required: true
            },
            {
                name: "igpDate",
                label: "IGP Date",
                type: "date",
                required: true
            }
        ]
    },
    {
        heading: "Material Details",
        name: "materialDtlList",
        // colCnt: 6,
        children: [
            {
                name: "materialCode",
                label: "Material Code",
                type: "text",
                // span: 1,
                disabled: true,
                required: true
            },
            {
                name: "description",
                label: "Material Description",
                type: "text",
                // span: 2,
                disabled: true,
                required: true
            },
            {
                name: "quantity",
                label: "Quantity",
                type: "text",
                // span: 1,
                required: true
            },
            {
                name: "uom",
                label: "UOM",
                type: "text",
                // span: 2,
                disabled: true,
                required: true
            },
            {
                name: "category",
                label: "Category",
                type: "text",
                // span: 2,
                disabled: true,
                required: true
            },
            {
                name: "subCategory",
                label: "Sub Category",
                type: "text",
                // span: 2,
                disabled: true,
                required: true
            }, {
                name: "unitPrice",
                label: "Unit Price",
                type: "text",
                // span: 2,
                disabled: true,
                required: true
            }
        ]
    },
    {
         heading: "IGP Details",
         fieldList: [
            {
                name: "indentId",
                label: "Custodian / Indentor Name",
                type: "select",
                options: indentList,
                required: true
            },
            {
                name: "locationId",
                label: "Field Station",
                type: "select",
                options: locationMasterOption,
                required: true
            }
         ]
    }
];

console.log("Inentlist: ", indentList);








  const locatorMasterObj = locatorMaster?.reduce((acc, obj) => {
    const { value, label } = obj;
    acc[value] = label;
    return acc;
  }, {});

  const handleSearch = async () => {
    
    try {
      const endpoint = formData.igpType === "PO" 
        ? `/api/process-controller/getPoOgp?processNo=${formData.ogpId}`
        : `/api/process-controller/getSubProcessDtls?processNo=${formData.ogpId}&processStage=OGP`;

      const {data} = await axios.get(endpoint);
      setFormData(prev => ({
        ...data?.responseData,
        ogpId: data.responseData?.ogpId,
        igpDate: prev.igpDate,
        igpType: prev.igpType,
        materialDtlList: data?.responseData?.materialDtlList?.map(item => ({
          ...item,
          locatorDesc: locatorMasterObj[parseInt(item.locatorId)]
        }))
      }));
    } catch(error) {
      message.error(error?.response?.data?.responseStatus?.message || "Error fetching OGP data.");
    }
  }


  const {userId} = useSelector(state => state.auth);

  const onFinish = async () => {
    const payload = {...formData, createdBy: userId};

    if(formData.igpType === "materialIn") {
      try{
        const {data} = await axios.post("/api/process-controller/saveMaterialIgp", payload)
        setFormData(prev => ({
          ...prev,
          igpId: data?.responseData?.processNo
        }))
        setModalOpen(true);
        message.success("Material IGP saved successfully.");
      } catch(error) {
        message.error(error?.response?.data?.responseStatus?.message || "Error saving Material IGP.");
      }
      return
    }

    try {
      setSubmitBtnLoading(true);
      const {data} = await axios.post("/api/process-controller/saveIgp", payload);

      setFormData(prev => ({
        ...prev,
        igpId: data?.responseData?.processNo
      }));

      localStorage.removeItem("igpDraft");
      setModalOpen(true);
    } catch(error) {
      message.error(error?.response?.data?.responseStatus?.message || "Failed to save IGP.");
    } finally {
      setSubmitBtnLoading(false);
    }
  };

  const {materialMaster} = useSelector(state => state.masters) || []

  useEffect(() => {
    const draft = localStorage.getItem("igpDraft");
    if(draft) {
      setFormData(JSON.parse(draft));
      message.success("Form loaded from draft.");
    }
  }, []);

  // First useEffect to set initial data from state
  useEffect(() => {
    if(state?.processNo && state?.type) {
      setFormData(prev => ({
        ...prev,
        igpType: state.type,
        ogpId: state.processNo
      }));
    }
  }, [state]);

  // Second useEffect to handle search when formData changes
  useEffect(() => {
    if (formData.igpType && formData.ogpId) {
      handleSearch();
    }
  }, [formData.igpType, formData.ogpId]);

  console.log("MMST: ", materialMaster);

  return (
    <Card className="a4-container" ref={printRef}>
      <Heading title="Inward Gate Pass" />
      <CustomForm formData={formData} onFinish={onFinish}>
        {/* {renderFormFields(igpFields, handleChange, formData, "", null, setFormData, handleSearch)} */}
        <h1 className="font-semibold">Order Details</h1>
        <div className="grid md:gap-x-4 md:gap-y-2 md:grid-cols-3">
          <Form.Item name="igpType" label="Type">
            <Select options={[{label: "PO", value: "PO"}, {label: "Goods Issue", value: "Goods Issue"}, {label: "Material Inward", value: "materialIn"}]} onChange={(val) => handleChange("igpType", val)}/>
          </Form.Item>
        </div>
        {formData.igpType === "materialIn" && (
          <>
          <MaterialSearch itemsArray={materialMaster} setFormData={setFormData} />
          {renderFormFields(materialInFields, handleChange, formData, "", null, setFormData, handleSearch)}
          </>
        )
        }

        {
          formData.igpType === "PO" && renderFormFields(igpPoFields, handleChange, formData, "", null, setFormData, handleSearch)
        }
        {
          formData.igpType === "Goods Issue" && renderFormFields(igpFields, handleChange, formData, "", null, setFormData, handleSearch)
        }
        <ButtonContainer
          onFinish={onFinish}
          formData={formData}
          draftDataName="igpDraft"
          submitBtnLoading={submitBtnLoading}
          submitBtnEnabled
          printBtnEnabled
          draftBtnEnabled
          handlePrint={handlePrint}
        />
      </CustomForm>
      <CustomModal isOpen={modalOpen} setIsOpen={setModalOpen} title="Inward Gate Pass" processNo={formData?.igpId} />
    </Card>
  );
};

export default Igp;
