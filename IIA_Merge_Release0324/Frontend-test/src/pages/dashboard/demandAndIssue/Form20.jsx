import { Card, Form, message } from "antd";
import { useEffect, useRef, useState } from "react";
import { renderFormFields } from "../../../utils/CommonFunctions";
import Heading from "../../../components/DKG_Heading";
import DKG_CustomForm from "../../../components/DKG_CustomForm";
import { useSelector } from "react-redux";
import MaterialSearch from "../../../components/MaterialSearch";
import axios from "axios";
import ButtonContainer from "../../../components/ButtonContainer";
import { useReactToPrint } from "react-to-print";
import CustomModal from "../../../components/CustomModal";
import { useLocation } from "react-router-dom";
const typeOptions = [
  { label: "Demand & Issue", value: "DI" },
  { label: "Issue Note", value: "IN" },
];

const Form17 = () => {
  const [form] = Form.useForm();
  const { userId } = useSelector((state) => state.auth);
  const [modalOpen, setModalOpen] = useState(false);
  const [searchDone, setSearchDone] = useState(false);
 const location = useLocation();
  const [formData, setFormData] = useState({
    materialDtlList: [],
    senderCustodianId: userId,
    senderLocationId: "",
  });
  const [formType, setFormType] = useState("DemandAndIssue"); // default type


  const roleName = useSelector((state) =>state.auth.role);
  const { materialMaster, locationMaster, userMaster } =
    useSelector((state) => state.masters) || [];

  const materialMasterObj = materialMaster?.reduce((acc, item) => {
    acc[item.materialCode] = item.description;
    return acc;
  }, {});

  const indentList = userMaster
    ?.filter((item) => item.roleName === "Indent Creator")
    .map((item) => ({ label: item.userName, value: item.userId }));

  const formattedLocations = locationMaster?.map((item) => ({
    label: item.locationName,
    value: item.locationCode,
  }));

  const [materialList, setMaterialList] = useState([]);
  const [filteredMaterial, setFilteredMaterial] = useState([]);
  const [submitBtnLoading, setSubmitBtnLoading] = useState(false);

  // Fetch materials
  const populateData = async () => {
    try {
      const { data } = await axios.get("/api/process-controller/getStoreStockOhqConsumable");

      if (data?.responseData?.length) {
        setMaterialList(
          data.responseData.map((item) => ({
            ...item,
            materialDesc: materialMasterObj?.[item.materialCode] || item.materialCode,
          }))
        );
      } else {
        setMaterialList([]);
      }
    } catch (error) {
      console.error("Material Fetch Error:", error);
      message.error("Error fetching material details.");
    }
  };

  useEffect(() => {
    populateData();
  }, []);

  useEffect(() => {
    setFilteredMaterial(materialList); // Show all consumable materials
  }, [materialList]);

  // Handle form field change
  const handleChange = (fieldName, value) => {
    if (typeof fieldName === "string") {
      setFormData((prev) => ({ ...prev, [fieldName]: value }));
    } else {
      setFormData((prev) => {
        const prevMaterialDtlList = prev.materialDtlList;
        prevMaterialDtlList[fieldName[1]][fieldName[2]] = value;
        return { ...prev, materialDtlList: prevMaterialDtlList };
      });
    }
  };
/*
  // Submit form
  const onFinish = async () => {
    if (!formData.diDate) {
      message.error("Please enter the Demand & Issue Date.");
      return;
    }
    if (!formData.materialDtlList?.length) {
      message.error("Please add at least one material.");
      return;
    }
    for (const item of formData.materialDtlList) {
      if (!item.quantity || item.quantity <= 0) {
        message.error(`Please enter a valid quantity for ${item.materialDesc}.`);
        return;
      }
    }

    try {
      setSubmitBtnLoading(true);
      const { data } = await axios.post("/api/process-controller/createDi", {
        ...formData,
        createdBy: userId,
      });

      setFormData({ ...formData, diId: data?.responseData?.processNo });
      setModalOpen(true);
      message.success("Demand & Issue created successfully!");
    } catch (error) {
      message.error(error?.response?.data?.responseStatus?.message || "Error creating Demand & Issue.");
    } finally {
      setSubmitBtnLoading(false);
    }
  };*/
  const onFinish = async () => {
  if (!formData.diDate) { message.error("Please enter the DI Date."); return; }
  if (!formData.materialDtlList?.length) { message.error("Add at least one material."); return; }

  try {
    setSubmitBtnLoading(true);

    
    const isUpdate = !!formData.diId;
      if (isUpdate && roleName !== "Store Person") {
      message.warning("Only Store Person can raise a Issue Note.");
      return;
    }

    const url = isUpdate
      ?  `/api/process-controller/issueNote`  // call PUT
      : "/api/process-controller/createDi";                   // call POST

    const { data } = await axios({
      method: isUpdate ? "put" : "post",
      url: url,
      data: { ...formData, createdBy: userId },
    });

    setFormData({ ...formData, diId: data?.responseData?.processNo });
    setModalOpen(true);

    message.success(isUpdate
      ? "Issue Note updated successfully!"
      : "Demand & Issue created successfully!"
    );

  } catch (error) {
    message.error(error?.response?.data?.responseStatus?.message || "Error processing request.");
  } finally {
    setSubmitBtnLoading(false);
  }
};



  // Table columns
  const materialColumn = [
    { dataIndex: "materialCode", title: "Material Code" },
    { dataIndex: "materialDesc", title: "Material Description" },
    { dataIndex: "quantity", title: "Quantity" },
  ];

  // Print
  const printRef = useRef();
  const handlePrint = useReactToPrint({ content: () => printRef.current });
  useEffect(() => {
    if (location.state?.diId) {
      fetchDIDataById(location.state.diId);
    }
  }, [location.state]);
  const fetchDIDataById = async (diId) => {
    try {
      const { data } = await axios.get(`/api/process-controller/SearchByDiId`, {
        params: { diId },
      });
      if (data?.responseData) {
        setFormData({
          ...data.responseData,
          diId: data.responseData.diId,
          materialDtlList: data.responseData.materialDtlList || [],
        });
        setSearchDone(true);
      } else {
        message.warning("No data found for this DI ID");
      }
    } catch (err) {
      message.error("Error fetching DI data");
    }
  };
const handleFetchDIData = async () => {
  if (!formData.diId) {
    message.error("Enter Demand Issue ID");
    return;
  }

  try {
const { data } = await axios.get(`/api/process-controller/SearchByDiId`, {
  params: { diId: formData.diId },
});
    if (data?.responseData) {
      setFormData((prev) => ({
        ...prev,
        ...data.responseData,
        diId: data.responseData.diId,
        materialDtlList: data.responseData.materialDtlList || [],
      }));
      setSearchDone(true);
      message.success("Data loaded successfully!");
    } else {
      message.warning("No data found for this DI ID");
    }
  } catch (err) {
    message.error("Error fetching DI data");
  }
};



  // Form fields without receiver location/custodian & locator fields
  const transferDtls = [
    {
      fieldList: [
       
        { name: "diId", label: "Demand Issue ID", type: "search",  onSearch: handleFetchDIData, span: 2 },
     

      ],
    },
    {
        heading: "Status",
        colCnt:2,
        fieldList:[
            ...(searchDone ? [
    {
        name: "status",
        label: "Status",
        type: "text",
        disabled: true
    }
] : [])
        ]
    },
    {
      heading: "",
      colCnt: 4,
      fieldList: [
        { name: "senderLocationId", label: "Field Station", type: "select", options: formattedLocations, required: true, span: 2 },
        { name: "senderCustodianId", label: "Sender Custodian", type: "select", options: indentList, required: true, span: 2 },
        { name: "diDate", label: "Date", type: "date", required: true, span: 1 },
      ],
    },
    {
      heading: "Material Details",
      name: "materialDtlList",
      colCnt: 4,
      children: [
        { name: "materialCode", label: "Material Code", type: "text", span: 2 },
        { name: "materialDesc", label: "Material Description", type: "text", span: 2 },
        { name: "quantity", label: "Quantity", type: "text", span: 2, required: true },
        { name: "uom", label: "UOM", type: "text", span: 2, required: true },
       
      ],
    },
  ];

  return (
    <Card className="a4-container" ref={printRef}>
      <Heading title="Demand And Issue" />
      {!searchDone && filteredMaterial?.length > 0 ? (
        <MaterialSearch
          customCols={materialColumn}
          itemsArray={filteredMaterial}
          setFormData={setFormData}
         
        />
      ) : (
        <p></p>
      )}

      <DKG_CustomForm form={form} formData={formData} onFinish={onFinish}>
        {renderFormFields(transferDtls, handleChange, formData, "", null, setFormData)}
        <ButtonContainer
          onFinish={onFinish}
          formData={formData}
          draftDataName="isnDraft"
          submitBtnLoading={submitBtnLoading}
          submitBtnEnabled
          printBtnEnabled
          draftBtnEnabled
          handlePrint={handlePrint}
        />
      </DKG_CustomForm>
      <CustomModal isOpen={modalOpen} setIsOpen={setModalOpen} title="Demand And Issue" processNo={formData.diId} />
    </Card>
  );
};

export default Form17;
