import { Card, message } from "antd";
import React, { useCallback, useEffect, useRef, useState } from "react";
import Heading from "../../../components/DKG_Heading";
import CustomForm from "../../../components/DKG_CustomForm";
import ButtonContainer from "../../../components/ButtonContainer";
import { useReactToPrint } from "react-to-print";
import axios from "axios";
import { useSelector } from "react-redux";
import CustomModal from "../../../components/CustomModal";
import { assetDisposalFields } from "./InputFields";
import AssetSearch from "../../../components/AssetSearch";
import { renderFormFields } from "../../../utils/CommonFunctions";
import dayjs from "dayjs";
import AssetDisposalSearch from "../../../components/AssetDisposalSearch";

const AssetDisposal = () => {
  const printRef = useRef();
  const handlePrint = useReactToPrint({ content: () => printRef.current });

  const [modalOpen, setModalOpen] = useState(false);
  const [submitBtnLoading, setSubmitBtnLoading] = useState(false);
  const [assetList, setAssetList] = useState([]);
  const [filteredAssetList, setFilteredAssetList] = useState([]);

  const { userId, locationId } = useSelector((state) => state.auth);
  const { locationMaster, userMaster } = useSelector((state) => state.masters) || [];
const [isEditable, setIsEditable] = useState(false);

  const formattedLocations = locationMaster?.map((item) => ({
    label: item.locationName,
    value: item.locationCode,
  }));

  const indentList = userMaster
    ?.filter((item) => item.roleName.includes( "Indent Creator"))
    .map((item) => ({ label: item.userName, value: item.userId.toString() }));

  const [formData, setFormData] = useState({
    disposalDate: null,
    vendorId: "",
    materialDtlList: [],
    assetSearch: "",
    custodianId: userId?.toString(),
  });

  // Fetch all assets for disposal
  const populateAssetList = useCallback(async () => {
    try {
      const { data } = await axios.get("/api/process-controller/getAssetOhqForDisposal");
      if (data?.responseData) {
        const formattedAssets = data.responseData.map((item) => ({
          assetId: item.assetId,
          assetCode: item.assetCode,
          aseetDescription: item.aseetDescription,
          locatorId: item.locatorId,
          quantity: item.quantity,
          unitPrice: item.unitPrice,
          bookValue: item.bookValue,
          depriciationRate: item.depriciationRate,
          custodianId: item.custodianId ? item.custodianId.toString() : null,
          poValue: item.poValue,
          ohqId : item.ohqId,
          poId: item.poId,
          poDate: item?.gprnDate ? dayjs(item.gprnDate, "DD/MM/YYYY") : null,
          serialNo: item.serialNo, 
          modelNo: item.modelNo, 
          serialNumbers: item.serialNumbers || [],
        }));
        setAssetList(formattedAssets);
      }
    } catch (error) {
      console.error("Error fetching asset details:", error);
      message.error(
        error?.response?.data?.responseStatus?.message || "Error fetching asset details."
      );
    }
  }, []);
  console.log(assetList);

  useEffect(() => {
    populateAssetList();
  }, [populateAssetList]);

  // Filter assets whenever search text or custodian changes
  useEffect(() => {
    const searchText = (formData.assetSearch || "").toLowerCase();

    const filtered = (assetList || []).filter((item) => {
      // Only show assets for selected custodian
      if (!formData.custodianId) return false;

      const matchesCustodian = item.custodianId === formData.custodianId;
      const matchesSearch =
        !searchText ||
        item.assetId.toString().toLowerCase().includes(searchText) ||
        item.aseetDescription.toLowerCase().includes(searchText);

      return matchesCustodian && matchesSearch;
    });

    setFilteredAssetList(filtered);
  }, [assetList, formData.custodianId, formData.assetSearch]);

const handleAssetSearch = async (value) => {
  setFormData((prev) => ({ ...prev, assetSearch: value }));

  if (!value) {
    // Clear form if search is empty
    setFormData((prev) => ({ ...prev, materialDtlList: [], disposalDate: null, custodianId: userId?.toString() }));
    return;
  }

  try {
    const { data } = await axios.get("/api/process-controller/SearchByDisposalId", {
      params: { disposalId: value },
    });

    if (data?.responseData) {
      const disposalData = data.responseData;

      // Auto-fill form fields
      setFormData((prev) => ({
        ...prev,
         disposalId: disposalData.disposalId, 
        disposalDate: disposalData.disposalDate,
        custodianId: disposalData.custodianId || prev.custodianId,
        locationId: disposalData.locationId || prev.locationId,
        // materialDtlList: disposalData.materialDtlList || [],
        materialDtlList: (disposalData.materialDtlList || []).map((item) => ({
  ...item,
  poDate: item?.gprnDate
    ? dayjs(item.gprnDate, "DD/MM/YYYY")
    : item.poDate || null,
}))
        // any other fields you want to fill from disposalData
      }));

      // Also update filteredAssetList if needed for asset table
      setFilteredAssetList(disposalData.materialDtlList || []);
       if (disposalData.action === "Approved" && disposalData.status === "For Disposal") {
        setIsEditable(true);
      } else {
        setIsEditable(false);
      }
    } else {
      // No data found
      message.warning("No disposal found with this ID");
      setFormData((prev) => ({ ...prev, materialDtlList: [] }));
      setFilteredAssetList([]);
    }
  } catch (error) {
    console.error("Error fetching disposal by ID:", error);
    message.error(
      error?.response?.data?.responseStatus?.message || "Failed to fetch disposal data."
    );
  }
};

  const handleCustodianChange = (value) => {
    setFormData((prev) => ({ ...prev, custodianId: value.toString() }));
  };

  const handleChange = (fieldName, value) => {
    if (typeof fieldName === "string") {
      setFormData((prev) => ({ ...prev, [fieldName]: value }));
    } else {
      setFormData((prev) => {
        const prevMaterialDtlList = [...prev.materialDtlList];
        prevMaterialDtlList[fieldName[1]][fieldName[2]] = value;
        return { ...prev, materialDtlList: prevMaterialDtlList };
      });
    }
  };
/*
  const onFinish = async () => {
    const payload = { ...formData, locationId, createdBy: userId };
    try {
      setSubmitBtnLoading(true);
      const { data } = await axios.post("/api/asset/dispose", payload);
      setFormData((prev) => ({
        ...prev,
        disposalId: data?.responseData?.processNo,
      }));
      localStorage.removeItem("assetDisposalDraft");
      setModalOpen(true);
    } catch (error) {
      message.error(
        error?.response?.data?.responseStatus?.message || "Failed to save Asset Disposal."
      );
    } finally {
      setSubmitBtnLoading(false);
    }
  };*/
const onFinish = async () => {
  const payload = {
    ...formData,
    locationId: formData.locationId || locationId,
    createdBy: userId,
  
  };

  try {
    setSubmitBtnLoading(true);

    let response;
    if (isEditable && formData.disposalId) {
      response = await axios.put("/api/process-controller", payload);
    } else {
      response = await axios.post("/api/asset/dispose", payload);
    }

    const processNo = response?.data?.responseData?.processNo;
    setFormData((prev) => ({
      ...prev,
      disposalId: processNo || prev.disposalId,
    }));

    localStorage.removeItem("assetDisposalDraft");
    setModalOpen(true);
    message.success(
      isEditable
        ? "Asset Disposal updated successfully."
        : "Asset Disposal created successfully."
    );
  } catch (error) {
    message.error(
      error?.response?.data?.responseStatus?.message ||
        "Failed to save Asset Disposal."
    );
  } finally {
    setSubmitBtnLoading(false);
  }
};


  useEffect(() => {
    const draft = localStorage.getItem("assetDisposalDraft");
    if (draft) {
      setFormData(JSON.parse(draft));
      message.success("Form loaded from draft.");
    }
  }, []);
console.log(formData);
  return (
    <Card className="a4-container" ref={printRef}>
      <Heading title="Asset Disposal" />

      <CustomForm formData={formData} onFinish={onFinish}>
        {renderFormFields(
          [
            {
              heading: "Search Assets",
              colCnt: 4,
              fieldList: [
                {
                  name: "assetSearch",
                  label: "Search Asset Disposal",
                  type: "search",
                  span: 2,
                  onSearch: handleAssetSearch,
                },
                {
                  name: "custodianId",
                  label: "Select Custodian",
                  type: "select",
                  options: indentList,
                  onChange: handleCustodianChange,
                  defaultValue: userId?.toString(),
                  span: 2,
                },
              ],
            },
          ],
          handleChange,
          formData
        )}
      </CustomForm>

    { /* <AssetSearch
        customCols={[
          { dataIndex: "assetId", title: "Asset ID" },
           { dataIndex: "assetCode", title: "Asset Code" },
          { dataIndex: "aseetDescription", title: "Asset Description" },
          { dataIndex: "locatorId", title: "Locator Id" },
          { dataIndex: "quantity", title: "Quantity" },
          { dataIndex: "custodianId", title: "Custodian" },
          { dataIndex: "serialNumber", title: "Serial Number" },
        ]}
        assetsArray={filteredAssetList || []}
        setFormData={setFormData}
        custodianId={formData.custodianId}
      />*/}
      <AssetDisposalSearch
  customCols={[
    { dataIndex: "assetCode", title: "Asset Code" },
    { dataIndex: "aseetDescription", title: "Asset Description" },
    { dataIndex: "locatorId", title: "Locator" },
    { dataIndex: "quantity", title: "Quantity" },
    { dataIndex: "unitPrice", title: "Unit Price" },
    { dataIndex: "bookValue", title: "Book Value" },
  ]}
  assetsArray={filteredAssetList || []}
  setFormData={setFormData}
  custodianId={formData.custodianId}
/>



      <CustomForm formData={formData} onFinish={onFinish}>
        {renderFormFields(assetDisposalFields(formattedLocations,isEditable), handleChange, formData)}
        <ButtonContainer
          onFinish={onFinish}
          formData={formData}
          draftDataName="assetDisposalDraft"
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
        title="Asset Disposal"
        processNo={formData?.disposalId}
      />
    </Card>
  );
};

export default AssetDisposal;
