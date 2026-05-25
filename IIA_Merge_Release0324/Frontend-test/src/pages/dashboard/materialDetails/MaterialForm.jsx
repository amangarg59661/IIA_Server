import React, { useEffect, useState } from "react";
import FormContainer from "../../../components/DKG_FormContainer";
import FormBody from "../../../components/DKG_FormBody";
import FormInputItem from "../../../components/DKG_FormInputItem";
import Heading from "../../../components/DKG_Heading";
import FormDropdownItem from "../../../components/DKG_FormDropdownItem";
import { Option } from "antd/es/mentions";
import {
  Button,
  Checkbox,
  DatePicker,
  Form,
  Input,
  message,
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
import axios from "axios";

const MaterialForm = () => {
  const auth = useSelector((state) => state.auth);
  const actionPerformer = auth.userId;
  const [form] = Form.useForm();
  const [fileList, setFileList] = useState([]);
  const [materialList, setMaterialList] = useState([]);
  const [materialDetailsMap, setMaterialDetailsMap] = useState({});
  const [materialCategories, setMaterialCategories] = useState([]);
  const [materialSubcategories, setMaterialSubcategories] = useState([]);
  const [uomOptions, setUomOptions] = useState([]);
  const [loading, setLoading] = useState(false);

  const fetchInitialData = async () => {
    try {
      const { data } = await axios.get("/api/material-master");

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
      setMaterialList(Object.keys(materialMap));
      const { data: uomData } = await axios.get("/api/uom-master");

      if (!uomData.responseData) throw new Error("Invalid UOM data");

      // Process UOM data
      const processedUom = uomData.responseData.map((uom) => ({
        value: uom.uomCode,
        label: uom.uomName,
      }));
      setUomOptions(processedUom);
    } catch (error) {
      message.error("Failed to load materials");
      console.error("Material fetch error:", error);
    }
  };

  useEffect(() => {
    fetchInitialData();
  }, []);

  const handleSubmit = async (values) => {
    setLoading(true);
    try {
      let uploadedFileName = "";
      if (fileList.length > 0) {
        const formData = new FormData();
        formData.append("file", fileList[0].originFileObj);

        const { data: uploadResult } = await axios.post(
          "/file/upload?fileType=Material",
          formData,
          { headers: { "Content-Type": "multipart/form-data" } }
        );
        uploadedFileName = uploadResult.fileName; // Adjust based on actual API response
      }

      // Prepare payload according to DTO structure
      const payload = {
        ...values,
        endOfLife: values.endOfLife?.format("YYYY-MM-DD") || "",
        uploadImageFileName: uploadedFileName,
        createdBy: actionPerformer, // Replace with actual user ID from your auth system
        updatedBy: "0", // Replace with actual user ID from your auth system
      };

      await axios.post("/api/material-master", payload);

      message.success("Material submitted successfully!");
      form.resetFields();
      setFileList([]);
    } catch (error) {
      message.error(`Submission failed: ${error.message}`);
      console.error("Submission error:", error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <FormContainer>
      <Form form={form} layout="vertical" onFinish={handleSubmit}>
        <Heading title={"Material Details"} />
        <div className="form-section">
          <FormInputItem label="Material Code" name="materialCode" placeholder="auto generated" disabled />
          <Form.Item
            name="category"
            label="Category"
            rules={[
              { required: true, message: "Please select material category!" },
            ]}
          >
            <Select placeholder="Select Material Category">
              {materialCategories.map((category) => (
                <Option key={category} value={category}>
                  {category}
                </Option>
              ))}
            </Select>
          </Form.Item>

          {/* Material Subcategory Dropdown */}
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
            <Select placeholder="Select Material Subcategory">
              {materialSubcategories.map((subCat) => (
                <Option key={subCat} value={subCat}>
                  {subCat}
                </Option>
              ))}
            </Select>
          </Form.Item>
        </div>
        <div className="form-section">
          {/* <FormInputItem label="Material Name" name="materialName" /> */}
          <Form.Item label="Description" name="description" required>
            <Input />
          </Form.Item>
          <Form.Item
            name="uom"
            label="UOM"
            rules={[{ required: true, message: "Please select a UOM!" }]}
          >
            <Select
              placeholder="Select Unit of Measure"
              showSearch
              optionFilterProp="children"
              filterOption={(input, option) =>
                option.children.toLowerCase().includes(input.toLowerCase())
              }
            >
              {uomOptions.map((uom) => (
                <Option key={uom.value} value={uom.value}>
                  {uom.label}
                </Option>
              ))}
            </Select>
          </Form.Item>
          <FormInputItem
            label="Mode of Procurement"
            name="modeOfProcurement"
            required
          />
        </div>
        <div className="form-section">
          <Form.Item label="End of Life" name="endOfLife">
            <DatePicker format="YYYY-MM-DD" />
          </Form.Item>
          <FormInputItem
            type="number"
            label="Depreciation Rate"
            name="depreciationRate"
          />
          <FormInputItem
            type="number"
            label="Stock Levels"
            name="stockLevels"
          />
        </div>
        <div className="form-section">
          <FormInputItem label="Condition of Goods" name="conditionOfGoods" />
          <FormInputItem label="Shelf Life" name="shelfLife" />
          <FormInputItem
            label="Estimated Price with CCY"
            name="estimatedPriceWithCcy"
            required
          />
        </div>
        <div className="form-section">
          <Form.Item
          label = "Upload Image"
          >
            <Upload
                beforeUpload={() => false}
                maxCount={1}
                accept="image/*"
                fileList={fileList}
                onChange={({ fileList }) => setFileList(fileList)}
            >
                <Button icon={<UploadOutlined />}>Select File</Button>
            </Upload>
          </Form.Item>

          <Form.Item
            name="indigenousOrImported"
            label="Origin"
            rules={[{ required: true }]}
            valuePropName="checked" // Add this for proper boolean handling
          >
            <Radio.Group>
              <Radio value={true}>Indigenous</Radio>
              <Radio value={false}>Imported</Radio>
            </Radio.Group>
          </Form.Item>
        </div>
        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            margin: "20px",
          }}
        >
          <Button type="default" htmlType="reset">
            <ReloadOutlined />
            Reset
          </Button>
          <Button type="primary" htmlType="submit" loading={loading}>
            <SendOutlined /> Submit
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
