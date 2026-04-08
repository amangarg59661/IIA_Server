import React, { useEffect, useState } from "react";
import FormContainer from "../../components/DKG_FormContainer";
import FormInputItem from "../../components/DKG_FormInputItem";
import Heading from "../../components/DKG_Heading";
import CustomSelect from "../../components/CustomSelect";
import { Option } from "antd/es/mentions";
import {
  Button,
  DatePicker,
  Form,
  Input,
  message,
  Modal,
  Radio,
  Select,
  Upload,
} from "antd";
import {
  ReloadOutlined,
  SaveOutlined,
  SendOutlined,
  UploadOutlined,
} from "@ant-design/icons";
import { useSelector } from "react-redux";
import { modeOfProcurementList } from "../../utils/Constants";
import { useLocation, useParams } from "react-router-dom";
import dayjs from "dayjs";
import TextAreaComponent from "../../components/DKG_TextAreaComponent";
import axios from "axios";
import ImageUploadBase64 from "../../components/ImageUploadBas64";
import { useLOVValues } from "../../hooks/useLOVValues";

const MaterialForm = ({materialCode}) => {
  const auth = useSelector((state) => state.auth);
  const actionPerformer = auth.userId;
  
  // const { materialCode } = useParams(); // Get material code from URL
  const [isEditMode, setIsEditMode] = useState(false);
  const [existingData, setExistingData] = useState(null);
  const location = useLocation();
  const [form] = Form.useForm();
  const [fileList, setFileList] = useState([]);
  const [materialList, setMaterialList] = useState([]);
  const [materialDetailsMap, setMaterialDetailsMap] = useState({});
  const [materialCategories, setMaterialCategories] = useState([]);
  const [materialSubcategories, setMaterialSubcategories] = useState([]);
  const [loading, setLoading] = useState(false);
  const [showMaterialCodePopup, setShowMaterialCodePopup] = useState(false);
  const [generatedMaterialCode, setGeneratedMaterialCode] = useState("");
 const [uploadedFiles, setUploadedFiles] = useState([]);
 const [selectedMaterialCode, setSelectedMaterialCode] = useState(null);


   const [materialStatus, setMaterialStatus] = useState("");

  // ✅ Fetch dropdown values from LOV system (Form ID: 6 - MaterialMaster)
  const { lovValues: categoryLOV, loading: loadingCategory } = useLOVValues(6, 'category');
  const { lovValues: subcategoryLOV, loading: loadingSubcategory } = useLOVValues(6, 'subcategory');
  const { lovValues: uomLOV, loading: loadingUom } = useLOVValues(6, 'uom');
  const { lovValues: currencyLOV, loading: loadingCurrency } = useLOVValues(6, 'currency');

  useEffect(() => {
    if (materialCode) {
      const fetchMaterialData = async () => {
        try {
         /* const response = await fetch(
            `/api/material-master-util/${materialCode}`
          );
          const data = await response.json();*/
          const response = await axios.get(`/api/material-master-util/base64/${materialCode}`);
          const data = response.data;
          if (data.responseStatus?.statusCode === 0) {
            const materialData = data.responseData;
            setExistingData(materialData);
            const originValue = materialData.indigenousOrImported ? 'indigenous' : 'imported';
            form.setFieldsValue({
              ...materialData,
              materialCode: materialData.materialCode, // Show existing code
              indigenousOrImported: originValue,//seting the origin if true then inigenous,flase Imported
              status: materialData.status,
              // Added by Aman
              
              Asset_Flag: materialData.asset_Flag ?'true' : 'false',

              // End
              materialStatus: materialData.materialStatus,
              reasonForDeactive: materialData.reasonForDeactive,
            });
            setMaterialStatus(materialData.materialStatus);
             if (materialData.materialFile && Array.isArray(materialData.materialFile)) {
  const fileListData = materialData.materialFile.map((base64Data, index) => {
    const isPdf = base64Data.startsWith("data:application/pdf");
    const fileName =
      materialData.uploadImageFileName?.split(",")[index]?.trim() ||
      `file_${index + 1}.${isPdf ? "pdf" : "jpg"}`;

    return {
      uid: `${index}`,
      name: fileName,
      status: "done",
      url: base64Data, // preview directly from base64
      base64: base64Data, // keep actual base64 for upload
    };
  });

  setFileList(fileListData);
  setUploadedFiles(fileListData.map((f) => f.base64));
            // Parse comma-separated file names and set fileList
         /* if (materialData.uploadImageFileName) {
            const fileArray = materialData.uploadImageFileName
              .split(",")
              .map((fileName, index) => ({
                uid: `${index}`,
                name: fileName.trim(),
                status: "done",
                url: `/file/view/Material/${fileName.trim()}`, // Update if different
              }));
            setFileList(fileArray);*/
          }
            setIsEditMode(true);
          }
        } catch (error) {
          message.error("Failed to load material data");
          console.error("Fetch error:", error);
        }
      };
      fetchMaterialData();
    }
  }, [materialCode, form]);

  const fetchInitialData = async () => {
    try {
      const response = await axios.get("/api/material-master");
      const data = response.data;


      if (!data.responseData) throw new Error("Invalid material data");

      // Extract unique categories and subcategories
      const categories = [
        ...new Set(data.responseData.map((item) => item.category)),
      ];
      const subCategories = [
        ...new Set(data.responseData.map((item) => item.subCategory)),
      ];

      setMaterialCategories(categories);
      setMaterialSubcategories(subCategories);

      // Create material map for other fields
      const materialMap = data.responseData.reduce(
        (acc, material) => ({
          ...acc,
          [material.materialCode]: {
            ...material,
            materialDescription: material.description,
            materialCategory: material.category,
            materialSubCategory: material.subCategory,
          },
        }),
        {}
      );

      setMaterialDetailsMap(materialMap);
    //  setMaterialList(Object.keys(materialMap));
     setMaterialList(
      Object.values(materialMap).map((item) => ({
        label: `${item.materialCode} - ${item.description}`,
        value: item.materialCode,
      }))
    );
    } catch (error) {
      message.error("Failed to load materials");
      console.error("Material fetch error:", error);
    }
  };

  useEffect(() => {
    fetchInitialData();
  }, []);

 /* // Debounced material search API call
const searchMaterials = async (searchText) => {
  if (!searchText || searchText.trim().length < 2) {
    return []; // wait for at least 2 characters
  }

  try {
    const response = await axios.get(`/api/material-master/search?keyword=${searchText}`);
    const data = response.data;
    if (data?.responseData) {
      return data.responseData.map((item) => ({
        label: `${item.materialCode} - ${item.description}`,
        value: item.materialCode,
      }));
    }
  } catch (error) {
    console.error("Material search error:", error);
  }

  return [];
};*/
const searchMaterials = async (searchText) => {
  if (!searchText || searchText.trim().length < 2) {
    return [];
  }

  try {
    const response = await axios.get(`/api/material-master/materialSearch?keyword=${searchText}`);
    const data = response.data;

    if (Array.isArray(data?.responseData)) {
      return data.responseData.map((item) => ({
        label: `${item.materialCode} - ${item.description}`,
        value: item.materialCode,
      }));
    } else {
      return [];
    }
  } catch (error) {
    console.error("Material search error:", error);
    return [];
  }
};




  // Add new state
  const [procurementMode, setProcurementMode] = useState("");
  const { vendorMaster } = useSelector((state) => state.masters);

  // Add vendor options mapping
  const vendorMasterMod = vendorMaster?.map((vendor) => ({
    label: vendor.vendorName,
    value: vendor.vendorName,
  }));

  // Modify handleSubmit to include validations
  const handleSubmit = async (values) => {
    setLoading(true);
    
    try {
      //const finalMaterialCode = isEditMode ? materialCode : values.materialCode;
      const finalMaterialCode = selectedMaterialCode || materialCode;

      if (values.modeOfProcurement === "Proprietary/Single Tender") {
        if (!values?.vendorNames) {
          message.error("Please select vendor name");
          return;
        }
      } else if (
        values.modeOfProcurement === "Limited Pre Approved Vendor Tender"
      ) {
        if (values?.vendorNames?.length !== 4) {
          message.error("Please select 4 vendor names");
          return;
        }
      }

      let vendorNames = null;
      if (values.modeOfProcurement === "Proprietary/Single Tender") {
        vendorNames = [values.vendorNames];
      } else if (
        values.modeOfProcurement === "Limited Pre Approved Vendor Tender"
      ) {
        vendorNames = values.vendorNames;
      }
/*
      let uploadedFileName = values.uploadImageFileName;
      if (fileList.length > 0 && fileList[0].originFileObj) {
        const formData = new FormData();
        formData.append("file", fileList[0].originFileObj);

        const uploadResponse = await fetch(
          "http://103.181.158.220:8081/astro-service/file/upload?fileType=Material",
          { method: "POST", body: formData }
        );
        const uploadResult = await uploadResponse.json();
        uploadedFileName = uploadResult.fileName;
      }
        */
      const existingFiles = fileList
      .filter(file => !file.originFileObj)
      .map(file => file.name); // these are already uploaded filenames
      let uploadedFileNames = [];

      if (fileList.length > 0) {
        for (const file of fileList) {
          if (file.originFileObj) {
            const formData = new FormData();
            formData.append("file", file.originFileObj);
      
          /*  const uploadResponse = await fetch(
              "/file/upload?fileType=Material",
              { method: "POST", body: formData }
            );
      
            const uploadResult = await uploadResponse.json();*/
            const uploadResponse = await axios.post(
              "/file/upload?fileType=Material",
              formData
              );
            const uploadResult = uploadResponse.data;

            if (uploadResult?.responseData?.fileName) {
              uploadedFileNames.push(uploadResult.responseData.fileName);
            }
      
      
           // if (uploadResult.fileName) {
             // uploadedFileNames.push(uploadResult.fileName);
            //}
      
            ;
          }
        }
      }
      
      const finalFileNames = [...existingFiles, ...uploadedFileNames];
            // Convert array to comma-separated string
     //const uploadedFileNameString = uploadedFileNames.join(",");

     const uploadedFileNameString = finalFileNames.join(",");
     const originBoolean =
        values.indigenousOrImported === "indigenous" ? true : false;
      // Ensure createdBy is a valid number (backend expects Integer)
      const createdByValue = isEditMode
        ? existingData.createdBy
        : (actionPerformer || auth?.userId || 1);

      if (!createdByValue) {
        message.error('User session not found. Please re-login.');
        setLoading(false);
        return;
      }

      const payload = {

        category: values.category,
        createdBy: Number(createdByValue),
        currency: values.currency,
        description: values.description,
        // estimatedPriceWithCcy: values.estimatedPriceWithCcy,
       // indigenousOrImported: values.indigenousOrImported,originBoolean
        indigenousOrImported: originBoolean,
        // Added by aman
        asset_Flag: values.Asset_Flag ,
        // End
        subCategory: values.subCategory,
        unitPrice: values.unitPrice,
        uom: values.uom,
        updatedBy: String(actionPerformer), // Convert to string
       // uploadImageFileName: uploadedFileNameString,
        uploadImageFileName: uploadedFiles,
        briefDescription: values.briefDescription,
        materialStatus: values.materialStatus || null,
        reasonForDeactive: values.reasonForDeactive || null,
      };

      const url = isEditMode
          ? `/api/material-master-util/update/${finalMaterialCode}`
          : "/api/material-master-util/register";


    /*  const response = await fetch(url, {
        method: isEditMode ? "PUT" : "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });*/
      const response = isEditMode
      ? await axios.put(url, payload)
      : await axios.post(url, payload);


     /* if (!response.ok) {
        const errorData = await response.json();
        throw new Error(
          errorData.responseStatus?.message || "Operation failed"
        );
      }*/
      //const errorMsg = error.response?.data?.responseStatus?.message || error.message;
      //message.error(`Submission failed: ${errorMsg}`);


      if (isEditMode) {
        message.success("Material updated successfully!");
        // Refresh data after update
        location.state?.reload && window.location.reload();
      } else {
       // const result = await response.json();
       // setGeneratedMaterialCode(result.responseData?.materialCode);
       setGeneratedMaterialCode(response.data.responseData?.materialCode);
        setShowMaterialCodePopup(true);
      }
    } catch (error) {
      const errorMsg = error.response?.data?.responseStatus?.message || error.message;
      message.error(`Submission failed: ${errorMsg}`);
      console.error("Submission error:", error);
    } finally {
      setLoading(false);
    }
  };

  const MaterialCodePopup = () => (
    <Modal
      title={isEditMode ? "Material Updated" : "Material Created Successfully"}
      visible={showMaterialCodePopup}
      onOk={() => setShowMaterialCodePopup(false)}
      onCancel={() => setShowMaterialCodePopup(false)}
      okText="OK"
    >
      {!isEditMode && generatedMaterialCode && (
        <p>
          Generated Material Code: <strong>{generatedMaterialCode}</strong>
        </p>
      )}
      {isEditMode ? (
        <p>Material details updated successfully!</p>
      ) : (
        !generatedMaterialCode && (
          <p>
            Material created successfully! Code will be assigned after approval.
          </p>
        )
      )}
    </Modal>
  );
   console.log("materialStatus"+ materialStatus);
  return (
    <FormContainer>
      <MaterialCodePopup />
      <Form
        form={form}
        layout="vertical"
        onFinish={handleSubmit}
        onValuesChange={(changedValues) => {
          if (changedValues.modeOfProcurement) {
            setProcurementMode(changedValues.modeOfProcurement);
          }
        }}
      >
        <Heading title={"Material Details"} />
        <div className="form-section">
           <Form.Item label="Search Material">
  <Select
    showSearch
    placeholder="Type to search material..."
    filterOption={false}
    onSearch={async (value) => {
  const results = await searchMaterials(value);
  setMaterialList(Array.isArray(results) ? results : []);
}}
options={Array.isArray(materialList) ? materialList : []}
    onChange={async (selectedCode) => 
      {
       setSelectedMaterialCode(selectedCode);
      

  try {
    const response = await axios.get(`/api/material-master/base64/${selectedCode}`);
    const data = response.data;

    if (data?.responseData) {
      const materialData = data.responseData;
      setExistingData(materialData);
      setMaterialStatus(materialData.materialStatus);

      // Convert boolean to radio value for Indigenous/Imported
      const originValue = materialData.indigenousOrImported ? "indigenous" : "imported";

      // Autofill material fields
      console.log("Asset_Flag raw value:", materialData.Asset_Flag, typeof materialData.Asset_Flag);
      form.setFieldsValue({
        materialCode: materialData.materialCode,
        category: materialData.category,
        subCategory: materialData.subCategory,
        description: materialData.description,
        uom: materialData.uom,
        unitPrice: materialData.unitPrice,
        currency: materialData.currency,
        indigenousOrImported: originValue,
        briefDescription: materialData.briefDescription,
        status: materialData.status,
        materialStatus: materialData.materialStatus,
        reasonForDeactive: materialData.reasonForDeactive,
        // Added by Aman
        Asset_Flag: materialData.asset_Flag ? 'true' :'false',
        // end

      });

     if (materialData.materialFile && Array.isArray(materialData.materialFile)) {
  const fileListData = materialData.materialFile.map((base64Data, index) => {
    const isPdf = base64Data.startsWith("data:application/pdf");
    const fileName =
      materialData.uploadImageFileName?.split(",")[index]?.trim() ||
      `file_${index + 1}.${isPdf ? "pdf" : "jpg"}`;

    return {
      uid: `${index}`,
      name: fileName,
      status: "done",
      url: base64Data, // preview directly from base64
      base64: base64Data, // keep actual base64 for upload
    };
  });

  setFileList(fileListData);
  setUploadedFiles(fileListData.map((f) => f.base64)); // important!
} else {
  setFileList([]);
  setUploadedFiles([]);
}


      message.success("Material details loaded successfully!");
    }
  } catch (error) {
    console.error("Error fetching material details:", error);
    message.error("Failed to load material details");
  }
    setIsEditMode(true);
}}
   
    style={{ width: "100%" }}
  />
</Form.Item>
 <FormInputItem
  label="Status"
  name="status"
  placeholder="Status"
  disabled
/>
 <FormInputItem
            label="Material Code"
            name="materialCode"
            placeholder={isEditMode ? materialCode : "Auto-generated"}
            disabled
          />
{form.getFieldValue("status") === "APPROVED" && (
  <CustomSelect
    label="Material Status"
    name="materialStatus"
    options={[
      { label: "Active", value: "Active" },
      { label: "Deactive", value: "Deactive" },
    ]}
    onChange={(name, value) => setMaterialStatus(value)}
    rules={[{ required: true }]}
  />
)}
        </div>
  
       
{form.getFieldValue("status") === "APPROVED" &&
 materialStatus === "Deactive" && (
    <FormInputItem
      label="Reason for Deactive"
      name="reasonForDeactive"
      rules={[{ required: true, message: "Please provide a reason for Deactive" }]}
    />
)}
        
        <div className="form-section">
          <Form.Item
            name="category"
            label="Category"
            rules={[
              { required: true, message: "Please select material category!" },
            ]}
          >
            <Select placeholder="Select Material Category" loading={loadingCategory}>
              {categoryLOV.map((item) => (
                <Option key={item.lovId || item.lovValue} value={item.lovValue}>
                  {item.lovDisplayValue}
                </Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            name="subCategory"
            label="Subcategory"
            rules={[
              {
                required: true,
                message: "Please select material subcategory!",
              },
            ]}
          >
            <Select placeholder="Select Material Subcategory" loading={loadingSubcategory}>
              {subcategoryLOV.map((item) => (
                <Option key={item.lovId || item.lovValue} value={item.lovValue}>
                  {item.lovDisplayValue}
                </Option>
              ))}
            </Select>
          </Form.Item>
           <Form.Item label="Description" name="description" required>
            <Input />
          </Form.Item>
        </div>

        <div className="form-section">
         
          <Form.Item
            name="uom"
            label="UOM"
            rules={[{ required: true, message: "Please select a UOM!" }]}
          >
            <Select
              placeholder="Select Unit of Measure"
              showSearch
              loading={loadingUom}
              optionFilterProp="children"
              filterOption={(input, option) =>
                option.children.toLowerCase().includes(input.toLowerCase())
              }
            >
              {uomLOV.map(lov => (
                <Option key={lov.lovId || lov.lovValue} value={lov.lovValue}>
                  {lov.lovDisplayValue}
                </Option>
              ))}
            </Select>
          </Form.Item>
          {/* <CustomSelect 
            name="modeOfProcurement" 
            label="Mode Of Procurement" 
            options={modeOfProcurementList} 
            required 
          /> */}
          {/* <FormInputItem
            type="number"
            name="unitPrice"
            label="Unit Price"
            required
          /> */}
          <TextAreaComponent
            label="Brief Description of Material"
            name="briefDescription"
            required
          />
          <FormInputItem
            type="number"
            name="unitPrice"
            label="Estimated Price"
            required
          />
        </div>

        {procurementMode === "Proprietary/Single Tender" && (
          <div className="form-section">
            <CustomSelect
              name="vendorNames"
              label="Vendor Name"
              options={vendorMasterMod}
            />
          </div>
        )}
        {procurementMode === "Limited Pre Approved Vendor Tender" && (
          <div className="form-section">
            <CustomSelect
              name="vendorNames"
              label="Vendor Names"
              options={vendorMasterMod}
              multiselect
            />
          </div>
        )}

        <div className="form-section">
          {/* <Form.Item label="End of Life" name="endOfLife">
            <DatePicker format="YYYY-MM-DD" />
          </Form.Item> */}
          {/* <FormInputItem
            type="number"
            label="Depreciation Rate"
            name="depreciationRate"
          />
          <FormInputItem
            type="number"
            label="Stock Levels"
            name="stockLevels"
          /> */}
        </div>

        <div className="form-section">
          {/* <FormInputItem label="Condition of Goods" name="conditionOfGoods" />
          <FormInputItem label="Shelf Life" name="shelfLife" /> */}
          
          <Form.Item
            name="currency"
            label="Currency"
            rules={[{ required: true }]}
          >
            <Select placeholder="Select Currency" loading={loadingCurrency}>
              {currencyLOV.map((item) => (
                <Option key={item.lovId || item.lovValue} value={item.lovValue}>
                  {item.lovDisplayValue}
                </Option>
              ))}
            </Select>
          </Form.Item>

           {/* Added by aman */}
                    <Form.Item
                      name="Asset_Flag"
                      label="Asset"
                      rules={[{ required: true }]}
                    >
                      <Radio.Group>
                        <Radio value="true">Yes</Radio>
                        <Radio value="false">No</Radio>
                      </Radio.Group>
                    </Form.Item>
                    {/* ENd */}

          <Form.Item
            name="indigenousOrImported"
            label="Origin"
            rules={[{ required: true }]}
          >
            <Radio.Group>
              <Radio value="indigenous">Indigenous</Radio>
              <Radio value="imported">Imported</Radio>
            </Radio.Group>
          </Form.Item>
           <Form.Item>
 <ImageUploadBase64
  label="Upload Document"
  name="uploadingPriorApprovalsFileName"
  multiple={true}
  value={uploadedFiles} // <- always an array
  onChange={(name, files) => setUploadedFiles(files)}
/>

</Form.Item>
        </div>

        <div className="form-section">
         {/* <Form.Item label="Upload Document">
            <Upload
              beforeUpload={() => false}
              multiple={true}
              //   accept="image/*"
              fileList={fileList}
              onPreview={(file) => window.open(file.url, "_blank")}
              onChange={({ fileList }) => setFileList(fileList)}
            >
              <Button icon={<UploadOutlined />}>Select File</Button>
            </Upload>
          </Form.Item> */}
          

        </div>

        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            margin: "20px",
          }}
        >
         <Button
  type="default"
  htmlType="button"
  onClick={() => {
    form.resetFields();
    setFileList([]);
    setUploadedFiles([]);
    setExistingData(null);
    setIsEditMode(false);
    form.setFieldsValue({
      materialCode: "",
      status: "",
    });
  }}
>
  <ReloadOutlined />
  Reset
</Button>

         {/* <Button type="primary" htmlType="submit" loading={loading}>
            <SendOutlined /> {
              materialCode ? "Update" : "Create"
            }
          </Button>*/}
        <Button
  type="primary"
  htmlType="submit"
  loading={loading}
  disabled={
    // Role-based restrictions only apply when editing from the Queue (materialCode prop),
    // not when editing via Masters page search (selectedMaterialCode)
    isEditMode && !!materialCode && (
      (auth.role === "Indent Creator" && form.getFieldValue("status") !== "CHANGE_REQUEST") ||
      (auth.role === "Purchase personnel" && form.getFieldValue("status") !== "APPROVED")
    )
  }
>
  <SendOutlined /> {isEditMode ? "Update" : "Create"}
</Button>


          <Button type="dashed" htmlType="button">
            <SaveOutlined />
            Save Draft
          </Button>
        </div>
      </Form>
    </FormContainer>
  );
};

export default MaterialForm;
