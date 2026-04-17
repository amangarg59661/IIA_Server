import { Card, message } from "antd";
import React, { useCallback, useEffect, useRef, useState } from "react";
import Heading from "../../../components/DKG_Heading";
import CustomForm from "../../../components/DKG_CustomForm";
import ButtonContainer from "../../../components/ButtonContainer";
import { useReactToPrint } from "react-to-print";
import axios from "axios";
import { useSelector } from "react-redux";
import CustomModal from "../../../components/CustomModal";
import { assetFields } from "./InputFields";
import { renderFormFields } from "../../../utils/CommonFunctions";
import { locatorMaster } from "../grn/InputFields";
import { Input, Modal, Table, Row, Col, Button } from "antd";
import { SearchOutlined } from "@ant-design/icons";
import { useLOVValues } from "../../../hooks/useLOVValues";


const Asset = () => {
  // ✅ Fetch dropdown values from LOV system (Form ID: 1 - AssetMaster)
  const { lovValues: locatorLOV, loading: loadingLocator } = useLOVValues(1, 'locator');
  const printRef = useRef();
  const handlePrint = useReactToPrint({
    content: () => printRef.current,
  });

  const [assetIdList, setAssetIdList] = useState([]);
const [searchParams, setSearchParams] = useState({ keyword: "" });

const [searchResults, setSearchResults] = useState([]);
const [isSearchModalOpen, setIsSearchModalOpen] = useState(false);
const [selectedRow, setSelectedRow] = useState(null);

const [existingSerials, setExistingSerials] = useState([]);
const [remainingToEnter, setRemainingToEnter] = useState(0);
const fetchExistingSerials = async (assetCode, assetId, custodianId, locatorId, quantity) => {
  try {
    const { data } = await axios.get(
      "/api/asset/exstingSerialNoOnCustodainIdofAsset",
      { params: { assetCode, assetId, custodianId, locatorId, quantity } }
    );

    const result = data?.responseData;
    if (result) {
      setExistingSerials(result.existingSerials || []);
      setRemainingToEnter(result.remainingToEnter || 0);
      message.success(`Found ${result.existingCount} existing serials`);
    }
  } catch (error) {
    message.error("Failed to load existing serial numbers");
  }
};



const assetFields = [
  {
        heading: "Custodian Details",
        colCnt: 6,
        fieldList: [
            {
                name: "custodianId",
                label: "Custodian Id",
                type: "text",
                span: 2,
                // disabled: true,
                // required: true
            },
            {
                name: "locatorId",
                label: "Field Station",
                type: "select",
                span: 2,
                required: true
            },
            {
                name: "poId",
                label: "Po Id",
                type: "text",
                disabled: true,
                span: 2,
                required: true
            },
        ]
    },
    {
        heading: "Asset Details",
        colCnt: 6,
        fieldList: [
            {
                name: "assetId",
                label: "Asset ID",
                type: "select",
                span: 2,
                options: assetIdList,
                disabled: true,
                // required: true
            },
            {
                name: "materialCode",
                label: "Material Code",
                type: "text",
                disabled: true,
                span: 2,
                required: true
            },
            {
                name: "materialDesc",
                label: "Material Description",
                type: "text",
                disabled: true,
                span: 2,
                required: true
            },
            {
                name: "assetDesc",
                label: "Asset Description",
                type: "text",
                span: 2,
                required: true
            },
            {
                name: "uomId",
                label: "UOM",
                type: "text",
                span: 3,
                disabled: true,
                required: true
            }
        ]
    },
    {
        heading: "Technical Details",
        // Modified by Aman
        // colCnt:4
        colCnt: 4,
        // End
        fieldList: [
            {
                name: "makeNo",
                label: "Make No",
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
            },
            ...(existingSerials.length > 0
  ? [
      {
        name: "existingSerialNumbers",
        label: "Existing Serial Numbers",
        type: "select",
        span: 2,
        options: existingSerials.map(serial => ({
          label: serial,
          value: serial
        })),
      },
    ]
  : []),

            {
                name: "serialNo",
                label: "Serial Numbers (comma-separated)",
                type: "text",
                span: 2,
                required: true,
                disabled: remainingToEnter === 0, 
            },
            {
                name: "componentName",
                label: "Component Name",
                type: "text",
                span: 3,
                required: false
            },
            {
                name: "componentId",
                label: "Component ID",
                type: "text",
                span: 3,
                required: false
            }
        ]
    },
    {
        heading: "Quantity and Price Details",
        colCnt: 6,
        fieldList: [
            {
                name: "quantity",
                label: "Quantity",
                type: "text",
                disabled: true,
                span: 2,
                required: true
            },
            {
                name: "unitPrice",
                label: "Unit Price",
                type: "text",
                disabled: true,
                span: 2,
                required: true
            },
            {
                name: "stockLevels",
                label: "Stock Levels",
                type: "text",
                span: 2,
                required: true
            },
            {
                name: "depriciationRate",
                label: "Depreciation Rate",
                type: "text",
                span: 2,
                required: true
            },
            {
                name: "locatorId",
                label: "Locator",
                type: "select",
                options: locatorLOV.length > 0
                    ? locatorLOV.map(lov => ({ label: lov.lovDisplayValue, value: lov.lovValue }))
                    : locatorMaster,
                span: 2,
                required: true
            }
        ]
    },
    {
        heading: "Additional Details",
        colCnt: 6,
        fieldList: [
            {
                name: "endOfLife",
                label: "End of Life",
                type: "date",
                span: 2,
                required: true
            },
            {
                name: "shelfLife",
                label: "Shelf Life",
                type: "text",
                span: 2,
                required: true
            },
            {
                name: "conditionOfGoods",
                label: "Condition of Goods",
                type: "text",
                span: 2,
                required: true
            }
        ]
    }
];

  const [modalOpen, setModalOpen] = useState(false);
  const [submitBtnLoading, setSubmitBtnLoading] = useState(false);

  const [formData, setFormData] = useState({
    assetId: null,
    materialCode: "",
    materialDesc: "",
    assetDesc: "",
    makeNo: "",
    serialNo: "",
    modelNo: "",
    uomId: "",
    componentName: "",
    componentId: null,
    initQuantity: null,
    unitPrice: null,
    depriciationRate: null,
    endOfLife: null,
    stockLevels: null,
    conditionOfGoods: "",
    shelfLife: "",
    locatorId: null
  });
  const handleSearch = async (value) => {
   console.log("handleSearch received value:", value);
   try {
    const { data } = await axios.get(`/api/asset/getAssetDtl`, {
      params: { assetId: value }
    });
    setFormData(data.responseData || {});
    } catch (error) {
    message.error("Error while fetching Asset data.");
    }
  };

  const populateAssetDtls = async (assetId) => {
    try{
      const {data} = await axios.get(`/api/asset/getAssetDtl?assetId=${assetId}`);
      setFormData(data.responseData);
    }
    catch(error) {
      message.error(error?.response?.data?.responseStatus?.message || 'Failed to load asset details.');
    }
  }

  const handleChange = (fieldName, value) => {

  setFormData(prev => ({ ...prev, [fieldName]: value }));
};




  const {userId, locationId} = useSelector(state => state.auth);
 // 🔍 Auto Search function for keyword
const handleSearchAssets = async (keyword) => {
  try {
    const { data } = await axios.get(
      "/api/asset/search",
      { params: { keyword } }
    );
    setSearchResults(data.responseData || []);
    setIsSearchModalOpen(true);
  } catch (error) {
    message.error("Failed to search assets");
  }
};



  const updateAsset = async (payload) => {
    try {
      setSubmitBtnLoading(true);
      const {data} = await axios.post('/api/asset/update', payload);
      message.success('Asset updated successfully');
    } catch(error) {
      message.error(error?.response?.data?.responseStatus?.message || 'Failed to update asset.');
    } finally {
      setSubmitBtnLoading(false);
    }
  }
/*
  const onFinish = async () => {
    const payload = {
      ...formData,
      locationId,
      createdBy: userId
    };

    if(formData?.assetId) {
      
      updateAsset(payload);
      return;
    }

    try {
      setSubmitBtnLoading(true);
      const {data} = await axios.post('/api/asset/save', payload);

      setFormData(prev => ({
        ...prev,
        assetId: data?.responseData?.processNo
      }));

      localStorage.removeItem("assetDraft");
      setModalOpen(true);
      message.success('Asset created successfully');
    } catch(error) {
      message.error(error?.response?.data?.responseStatus?.message || 'Failed to create asset.');
    } finally {
      setSubmitBtnLoading(false);
    }
  };*//*
  const onFinish = async () => {
   console.log("Serial array:", formData.serialNoArray);
console.log("Count:", formData.serialNoArray?.length);

if (formData.serialNoArray?.length !== Number(formData.quantity)) {
  message.error("Number of serial numbers must equal the quantity!");
  return;
}

  const payload = {
    assetId: formData.assetId,
    assetCode: formData.assetCode,
    poId: formData.poId,
    custodianId: formData.custodianId,
    locatorId: formData.locatorId,
    materialCode: formData.materialCode,
    materialDesc: formData.materialDesc,
    assetDesc: formData.assetDesc,
    makeNo: formData.makeNo,
    modelNo: formData.modelNo,
    uomId: formData.uomId,
    quantity: formData.quantity,
    unitPrice: formData.unitPrice,
    serialNumbers: formData.serialNoArray || []
  };

  try {
    setSubmitBtnLoading(true);
    const { data } = await axios.post(
      "/api/asset/update-serials",
      payload
    );
    message.success("Asset serials updated successfully!");
  } catch (error) {
    message.error(
      error?.response?.data?.responseStatus?.message ||
      "Failed to update asset serials."
    );
  } finally {
    setSubmitBtnLoading(false);
  }
};
*/
const onFinish = async () => {
  console.log("Serial array:", formData.serialNoArray);
  console.log("Count:", formData.serialNoArray?.length);

  const serialCount = formData.serialNoArray?.length || 0;

  // Check which API to call
  const isExistingAsset = existingSerials.length > 0;

  // Validation based on type
  if (isExistingAsset) {
   /* if (serialCount !== remainingToEnter) {
      message.error(`Please enter exactly ${remainingToEnter} new serial numbers.`);
      return;
    }*/
   if (serialCount > remainingToEnter) {
  message.error(`You can only enter up to ${remainingToEnter} serial numbers.`);
  return;
}

  } else {
    if (serialCount !== Number(formData.quantity)) {
      message.error("Number of serial numbers must equal the total quantity!");
      return;
    }
  }

  const payload = {
    assetId: formData.assetId,
    assetCode: formData.assetCode,
    poId: formData.poId,
    custodianId: formData.custodianId,
    locatorId: formData.locatorId,
    materialCode: formData.materialCode,
    materialDesc: formData.materialDesc,
    assetDesc: formData.assetDesc,
    makeNo: formData.makeNo,
    modelNo: formData.modelNo,
    uomId: formData.uomId,
    quantity: isExistingAsset ? remainingToEnter : formData.quantity,
    unitPrice: formData.unitPrice,
    serialNumbers: formData.serialNoArray || [],
  };

  try {
    setSubmitBtnLoading(true);

    const apiUrl = isExistingAsset
      ? "/api/asset/updateRemaining-serials"
      : "/api/asset/update-serials";

    const { data } = await axios.post(apiUrl, payload);

    message.success(
      isExistingAsset
        ? "Remaining serials added successfully!"
        : "Asset serials updated successfully!"
    );
  } catch (error) {
    message.error(
      error?.response?.data?.responseStatus?.message ||
        "Failed to update asset serials."
    );
  } finally {
    setSubmitBtnLoading(false);
  }
};


  useEffect(() => {
    const draft = localStorage.getItem("assetDraft");
    if(draft) {
      setFormData(JSON.parse(draft));
      message.success("Form loaded from draft.");
    }
  }, []);

  const populateData = useCallback(async () => {
    try{
      const {data} = await axios.get(`/api/asset/assetIds`);
      const assetIdOption = data.responseData?.map(item => ({label: item, value: item}));

      setAssetIdList(assetIdOption);
    }
    catch(error) {
      message.error(error?.response?.data?.responseStatus?.message || 'Failed to load asset ids.');
    }
  }, [])

  useEffect(() => {
    populateData();
  }, [])


useEffect(() => {
  if (formData.serialNo !== undefined && formData.serialNo !== null) {
    const serialArray = formData.serialNo
      .split(",")
      .map(s => s.trim())
      .filter(Boolean);
    setFormData(prev => ({
      ...prev,
      serialNoArray: serialArray
    }));
  }
}, [formData.serialNo]); 

  return (
    <Card className="a4-container" ref={printRef}>
      <Heading title="Asset Master" />
      {/* 🔍 Search Filters */}
      {/* 🔍 Single Search Field (auto search) */}
<Row gutter={16} style={{ marginBottom: 20 }}>
  <Col span={8}>
    <Input
      placeholder="Search Asset..."
      prefix={<SearchOutlined />}
      value={searchParams.keyword}
      onChange={(e) => {
        const keyword = e.target.value;
        setSearchParams({ keyword });
        if (keyword.length >= 2) handleSearchAssets(keyword); // Auto search after 2+ chars
      }}
      allowClear
    />
  </Col>
</Row>

      
      <CustomForm formData={formData} onFinish={onFinish}>
        {/*renderFormFields(assetFields, handleChange, handleSearch, formData)*/}
        {renderFormFields(assetFields, handleChange, formData, "", null, setFormData, handleSearch)}
        <ButtonContainer
          onFinish={onFinish}
          formData={formData}
          draftDataName="assetDraft"
          submitBtnLoading={submitBtnLoading}
          submitBtnEnabled
          printBtnEnabled
          draftBtnEnabled
          handlePrint={handlePrint}
        />
      </CustomForm>
      <CustomModal isOpen={modalOpen} setIsOpen={setModalOpen} title="Asset Master" processNo={formData?.assetId} />
      {/* 🔍 Search Results Modal */}
<Modal
  open={isSearchModalOpen}
  title="Asset Search Results"
  onCancel={() => setIsSearchModalOpen(false)}
  footer={null}
  width={800}
>
  <Table
  rowKey={(record) => `${record.assetId}-${record.locatorId}-${record.custodianId}`}
  dataSource={searchResults}
  pagination={false}
  columns={[
    { title: "Asset Code", dataIndex: "assetCode" },
    { title: "Asset ID", dataIndex: "assetId" },
    { title: "PO ID", dataIndex: "poId" },
    { title: "Custodian ID", dataIndex: "custodianId" },
    { title: "Locator ID", dataIndex: "locatorId" },
    { title: "Quantity", dataIndex: "quantity" },
    {
      title: "Action",
      render: (_, record) => {
        const isSelected =
          selectedRow &&
          selectedRow.assetId === record.assetId &&
          selectedRow.locatorId === record.locatorId &&
          selectedRow.custodianId === record.custodianId;

        return isSelected ? (
          <Button
            danger
            size="small"
            onClick={() => {
              setSelectedRow(null);
              message.info(`Deselected Asset ${record.assetId}`);
            }}
          >
            Deselect
          </Button>
        ) : (
         <Button
  type="primary"
  size="small"
  onClick={async () => {
    setSelectedRow(record);

    try {
      const response = await axios.get(
        "/api/asset/asset-full-details",
        {
          params: {
            assetId: record.assetId,
            assetCode: record.assetCode,
            custodianId: record.custodianId,
            locatorId: record.locatorId,
          },
        }
      );

      const assetData = response?.data?.responseData?.[0];
      if (assetData) {
        setFormData((prev) => ({
          ...prev,
          ...assetData,
          quantity: record.quantity,
        }));
        await fetchExistingSerials(
  record.assetCode,
  record.assetId,
  record.custodianId,
  record.locatorId,
  record.quantity
);

        message.success(`Asset ${record.assetId} details loaded`);
      } else {
        message.warning("No details found for selected asset");
      }
    } catch (error) {
      message.error("Failed to fetch full asset details");
      console.error(error);
    }

    setIsSearchModalOpen(false);
  }}
>
  Select
</Button>

        );
      },
    },
  ]}
/>


  <div style={{ marginTop: 16, textAlign: "right" }}>
    <Button
      type="primary"
      onClick={() => {
        if (!selectedRow) return message.warning("Please select an asset");
        setFormData(prev => ({
          ...prev,
          assetId: selectedRow.assetId,
          locatorId: selectedRow.locatorId
        }));
        setIsSearchModalOpen(false);
        message.success(`Asset ${selectedRow.assetId} selected`);
      }}
      style={{ marginRight: 8 }}
    >
      Select
    </Button>

    <Button
      onClick={() => {
        setSelectedRow(null);
        message.info("Selection cleared");
      }}
    >
      Deselect
    </Button>
  </div>
</Modal>

    </Card>
  );
};

export default Asset;
