import { Card, Form, message } from "antd";
import { useCallback, useEffect, useRef, useState } from "react";
import { renderFormFields } from "../../../utils/CommonFunctions";
import Heading from "../../../components/DKG_Heading";
import DKG_CustomForm from "../../../components/DKG_CustomForm";
import { useSelector } from "react-redux";
import MaterialSearch from "../../../components/MaterialSearch";
import axios from "axios";
import ItemGtSearch from "../../../components/ItemGtSearch";
import ButtonContainer from "../../../components/ButtonContainer";
import { useReactToPrint } from "react-to-print";
import CustomModal from "../../../components/CustomModal";
import dayjs from "dayjs";

const Form17 = () => {
  const [form] = Form.useForm();
  const {userId} = useSelector(state => state.auth)
  const [modalOpen, setModalOpen] = useState(false)
  const [formData, setFormData] = useState({materialDtlList: [], senderCustodianId: userId, senderLocationId: "BNG"});
  const onFinish = async () => {
    if(!formData.gtDate) {
      message.error("Please enter the Goods Transfer Date.");
      return;
    }
    if(!formData.materialDtlList?.length) {
      message.error("Please add at least one material.");
      return;
    }
   /* formData.materialDtlList.forEach(item => {
      if(!item.quantity || item.quantity <= 0) {
        message.error(`Please enter the quantity for material ${item.materialDesc}.`);
        return;
      }
    })*/
    for (const item of formData.materialDtlList) {
    if (!item.quantity || item.quantity <= 0) {
      message.error(`Please enter the quantity for material ${item.materialDesc}.`);
      return; 
    }
  }
    try{
      const {data} = await axios.post("/api/process-controller/createGt", {...formData, createdBy: userId})
      setFormData({...formData, gtId: data?.responseData?.processNo});
      setModalOpen(true);
      // message.success("Goods Transfer created successfully.");
    } catch(error) {
      message.error(error?.response?.data?.responseStatus?.message || "Error creating Goods Transfer.");
    }
  };
  const { materialMaster, locationMaster, locatorMaster, userMaster } =
    useSelector((state) => state.masters) || [];

  const materialMasterObj = materialMaster?.reduce((acc, item) => {
    acc[item.materialCode] = item.description;
    return acc;
  }, {});
  const indentList = userMaster
    ?.filter((item) => item.roleName.includes("Indent Creator"))
    .map((item) => ({ label: item.userName, value: item.userId }));

  const formattedLocations = locationMaster?.map((item) => ({
    label: item.locationName,
    value: item.locationCode,
  }));
    const handleGtSearch = async (gtId) => {
    if (!gtId) {
      message.error("Please enter a Goods Transfer ID.");
      return;
    }

    try {
      const { data } = await axios.get(`/api/process-controller/SearchById`, {
        params: { gtId },
      });

      if (data?.responseData) {
        setFormData(data.responseData);
        form.setFieldsValue(data.responseData); 
        message.success(`Goods Transfer ${gtId} loaded successfully.`);
      } else {
        message.warning(`No Goods Transfer found for ID: ${gtId}`);
      }
    } catch (error) {
      message.error(
        error?.response?.data?.responseStatus?.message ||
          "Error fetching Goods Transfer details."
      );
    }
  };


  const [ldd, setLdd] = useState([]);
  const [fsDd, setFsDd] = useState([]);
  const transferDtls = [
    {
      fieldList: [
        {
          name: "gtId",
          label: "Goods Transfer ID",
          type: "search",
          // required: true,
          onSearch: (value) => handleGtSearch(value),
          span: 2,
        },
      ]
    },
    {
      heading: "Transfer Information",
      colCnt: 4,
      fieldList: [
        {
          name: "senderLocationId",
          label: "Sender Field Station",
          type: "select",
          options: formattedLocations,
          required: true,
          span: 2,
        },
        {
          name: "senderCustodianId",
          label: "Sender Custodian",
          type: "select",
          options: indentList,
          required: true,
          span: 2,
        },
        {
          name: "receiverLocationId",
          label: "Receiver Field Station",
          type: "select",
          options: formattedLocations,
          required: true,
          span: 2,
        },
        {
          name: "receiverCustodianId",
          label: "Receiver Custodian",
          type: "select",
          options: indentList,
          required: true,
          span: 2,
        },
        {
          name: "gtDate",
          label: "Date",
          type: "date",
          required: true,
          span: 1,
        },
      ],
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
          // required: true,
        },
        {
          name: "materialDesc",
          label: "Material Description",
          type: "text",
          span: 3,
          // required: true,
        },
        {
          name: "assetId",
          label: "Asset Id",
          type: "text",
          span: 2,
          // required: true,
        },
         {
          name: "assetCode",
          label: "Asset Code",
          type: "text",
          span: 2,
          // required: true,
        },
        {
          name: "assetDesc",
          label: "Asset Description",
          type: "text",
          span: 3,
          // required: true,
        },
        // {
        //   name: "uomId",
        //   label: "UOM",
        //   type: "text",
        //   span: 1,
        //   required: true,
        // },
        {
          name: "receiverLocatorId",
          label: "Receiver Locator",
          type: "select",
          options: ldd || [],
          span: 2,
          required: true,
        },
        {
          name: "senderLocatorId",
          label: "Sender Locator",
          type: "text",
          span: 2,
          required: true,
        },
        {
          name: "quantity",
          label: "Quantity",
          type: "text",
          span: 2,
          required: true,
        },
        {
          name: "unitPrice",
          label: "Unit Price",
          type: "text",
          span: 2,
          required: true
        },
        {
          name: "depriciationRate",
          label: "Depriciation Rate",
          type: "text",
          span: 2,
          required: true
        },
          {
          name: "bookValue",
          label: "Book Value",
          type: "text",
          span: 2,
          required: true
        },
         {
          name: "poId",
          label: "Po Id",
          type: "text",
          span: 2,
          required: true
        },
         {
          name: "modelNo",
          label: "Model No",
          type: "text",
          span: 2,
          required: true
        },{
          name: "serialNo",
          label: "Serial No",
          type: "text",
          span: 2,
          required: true
        },{
          name: "reasonForTransfer",
          label: "Reason for Transfer",
          type: "text",
          span: 2,
          required: true
        }

      ],
    },
  ];
  
  const [submitBtnLoading, setSubmitBtnLoading] = useState(false);


  const handleChange = (fieldName, value) => {
    if (typeof fieldName === "string") {
      setFormData((prev) => ({ ...prev, [fieldName]: value }));
    } else {
      setFormData((prev) => {
        const prevMaterialDtlList = prev.materialDtlList;
        prevMaterialDtlList[fieldName[1]][fieldName[2]] = value;

        // Calculate total amount when receivedQuantity changes
        // if (fieldName[2] === 'receivedQuantity') {
        //   const unitPrice = parseFloat(prevMaterialDtlList[fieldName[1]].unitPrice || 0);
        //   const quantity = parseFloat(value || 0);
        //   prevMaterialDtlList[fieldName[1]].totalAmount = (unitPrice * quantity).toFixed(2);
        // }

        // let totQuant = 0
        // prevMaterialDtlList.forEach(mat => {
        //   totQuant += parseFloat(mat.receivedQuantity || 0)
        // })

        return { ...prev, materialDtlList: prevMaterialDtlList };
      });
    }
  };

  const [assetList, setAssetList] = useState([]);
  const [materialList, setMaterialList] = useState([]);

  const [filteredAsset, setFilteredAsset] = useState([]);
  const [filteredMaterial, setFilteredMaterial] = useState([]);
/*
  const populateData = async () => {
    try {
      const [data, data1, data2] = await Promise.all([
        axios.get("/api/process-controller/getAssetOhqDetails"),
        axios.get("/api/process-controller/getAssetOhqConsumable"),
        axios.get("/api/reports/asset"),
      ]);

      const assetObj = data2.data.responseData.reduce((acc, item) => {
        acc[item.assetId] = item.assetDesc
        return acc
      }, {} ) || {}

      console.log("Asset obj: ", assetObj);

      setAssetList(data.data.responseData.map(item => ({...item, assetDesc: assetObj[item.assetId], modelNo: item.modelNo,
    poId: item.poId,serialNo: item.serialNo})) || []);
      setMaterialList(data1.data.responseData.map(item => ({...item, materialDesc: materialMasterObj[item.materialCode]})) || []);
    } catch (error) {
      message.error("Error fetching goods details.");
    }
  };
*/
const populateData = async () => {
  try {
    const { data } = await axios.get("/api/asset/assetDataForGt");
    setAssetList(data.responseData || []);
  } catch (error) {
    message.error("Error fetching asset data for GT.");
  }
};

  console.log("Assetlist: ", assetList.filter(item => !item.custodianId && !item.locatorId));

  const handleSearch = async () => {};
/*
    const populateItemQtyDtls = useCallback(async () => {
    try {
      const { data } = await axios.get('/api/process-controller/getIsnAssetOhqDtls');
       if (data?.responseData) {
      const flatAssets = data.responseData.flatMap(asset =>
        asset.qtyList.map(qtyItem => ({
          assetId: asset.assetId,
          assetDesc: asset.assetDesc,
         
        
          unitPrice: asset.unitPrice,
          depriciationRate: asset.depriciationRate,
         
          uomId: asset.uomId,
          locatorId: qtyItem.locatorId,
          quantity: qtyItem.quantity,
          bookValue: qtyItem.bookValue,
          custodianId: qtyItem.custodianId,
          poId: asset.poId,        
    modelNo: asset.modelNo,     
    makeNo: asset.makeNo, 
        }))
      );
      setAssetList(flatAssets);
    }
      // if (data?.responseData) {
      //   setAssetList(data.responseData);
      // }
    } catch (error) {
      message.error(error?.response?.data?.responseStatus?.message || "Error fetching item quantity details.");
    }
  }, [])
*/

  useEffect(() => {
    if (formData.receiverLocationId) {
      setLdd(
        locatorMaster.filter(
          (item) => item.locationId === formData.receiverLocationId
        )
      );
    } else {
      setLdd([]);
    }
  }, [formData.receiverLocationId]);

  console.log("LDD: ", formData.senderCustodianId);

  useEffect(() => {
    populateData();
  }, []);
/*
  useEffect(() => {
    if (formData.senderLocationId && formData.senderCustodianId) {
      // Create a quick lookup map from locatorId to locationId
      const locatorMap =
        locatorMaster?.reduce((acc, loc) => {
          acc[loc.value] = loc.locationId;
          return acc;
        }, {}) || {};

      const filtered = materialList.filter((item) => {
        const locationId = locatorMap[item.locatorId];
        return (
          locationId === formData.senderLocationId &&
          parseInt(item.custodianId) === parseInt(formData.senderCustodianId)
        );
      });
      const filteredAsset = assetList.filter((item) => {
        const locationId = locatorMap[item.locatorId];
        return (
          locationId === formData.senderLocationId &&
          parseInt(item.custodianId) === parseInt(formData.senderCustodianId)
        );
      });

      setFilteredMaterial(filtered);
      setFilteredAsset(filteredAsset);
    } else {
      setFilteredMaterial([]);
    }
  }, [
    formData.senderLocationId,
    formData.senderCustodianId,
    locatorMaster,
    materialList,
  ]);*/
  console.log(assetList);
  useEffect(() => {
  if (formData.senderLocationId && formData.senderCustodianId) {
    const locatorMap =
      locatorMaster?.reduce((acc, loc) => {
        acc[loc.value] = loc.locationId;
        return acc;
      }, {}) || {};

    const filtered = materialList.filter((item) => {
      const locationId = locatorMap[item.locatorId];
      return (
        (!item.locatorId || locationId === formData.senderLocationId) &&
        (!item.custodianId || parseInt(item.custodianId) === parseInt(formData.senderCustodianId))
      );
    });

    const filteredAsset = assetList.filter((item) => {
      const locationId = locatorMap[item.locatorId];
      return (
        (!item.locatorId || locationId === formData.senderLocationId) &&
        (!item.custodianId || parseInt(item.custodianId) === parseInt(formData.senderCustodianId))
      );
    });

    setFilteredMaterial(filtered);
    setFilteredAsset(filteredAsset);
  } else {
    setFilteredMaterial([]);
  }
}, [
  formData.senderLocationId,
  formData.senderCustodianId,
  locatorMaster,
  materialList,
  assetList,
]);


  console.log("Filtered asset: ", filteredAsset);
  console.log("Filtered material: ", filteredMaterial);
  console.log("Sender Location:", formData.senderLocationId);
console.log("Sender Custodian:", formData.senderCustodianId);


  const materialColumn = [
    {
      dataIndex: "materialCode",
      title: "Material Code",
    },
    {
      title: "Material Description",
      dataIndex: "materialDesc",
    },
    { 
      dataIndex: "locatorId",
      title: "Locator Id"
    },
    {
      dataIndex: "quantity",
      title: "Quantity"
    }
  ]
  const assetColumn = [
    {
      dataIndex: "assetCode",
      title: "Asset Code",
    },
    {
      dataIndex: "assetDesc",
      title: "Asset Description",
    },
     { dataIndex: "modelNo", title: "Model No" },
      { dataIndex: "serialNo", title: "Serial No" },
      
    { dataIndex: "poId", title: "PO Id" },
    { 
      dataIndex: "locatorId",
      title: "Locator Id"
    },
    {
      dataIndex: "quantity",
      title: "Quantity"
    }
  ]

  const printRef = useRef();

  const handlePrint = useReactToPrint({
    content: () => printRef.current,
  });
/*
  useEffect(() => {
    populateItemQtyDtls();
  }, [])
*/
  return (
    <Card className="a4-container" ref={printRef}>
      <Heading title="Goods Transfer" />
      {/*filteredMaterial.length > 0 && (
        <MaterialSearch
          customCols = {materialColumn}
          itemsArray={filteredMaterial}
          setFormData={setFormData}
        />
      )*/}
      {filteredAsset.length > 0 && (
        <ItemGtSearch itemsArray={filteredAsset} setFormData={setFormData} />
      )}
      <DKG_CustomForm form={form} formData={formData} onFinish={onFinish}>
        {renderFormFields(
          transferDtls,
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
          draftDataName="isnDraft"
          submitBtnLoading={submitBtnLoading}
          submitBtnEnabled
          printBtnEnabled
          draftBtnEnabled
          handlePrint={handlePrint}
        />
      </DKG_CustomForm>
      <CustomModal isOpen={modalOpen} setIsOpen={setModalOpen} title="Goods Transfer" processNo={formData.gtId} />
    </Card>
  );
};

export default Form17;