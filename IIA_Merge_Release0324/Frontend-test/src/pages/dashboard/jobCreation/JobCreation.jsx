import React, { useEffect, useState } from "react";
import axios from "axios";
import FormInputItem from "../../../components/DKG_FormInputItem";
import { Button, Form, Select, message } from "antd";
import { ReloadOutlined, SaveOutlined, SendOutlined } from "@ant-design/icons";
import { Option } from "antd/es/mentions";
import Heading from "../../../components/DKG_Heading";
import FormContainer from "../../../components/DKG_FormContainer";

const JobCreation = () => {
  const [form] = Form.useForm();
  const [jobCategories, setJobCategories] = useState([]);
  const [uomOptions, setUomOptions] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const fetchInitialData = async () => {
      try {
        // Fetch Job Categories
        const { data: jobData } = await axios.get("/api/job-master");

        if (!jobData.responseData) throw new Error("Invalid job data");
        const uniqueCategories = [...new Set(jobData.responseData.map(item => item.category))];
        setJobCategories(uniqueCategories);

        // Fetch UOM Options
        const { data: uomData } = await axios.get("/api/uom-master");

        if (!uomData.responseData) throw new Error("Invalid UOM data");
        const processedUom = uomData.responseData.map(uom => ({
          value: uom.uomCode,
          label: uom.uomName,
        }));
        setUomOptions(processedUom);

      } catch (error) {
        message.error("Failed to load initial data");
        console.error("Initialization error:", error);
      }
    };

    fetchInitialData();
  }, []);

  const handleSubmit = async (values) => {
    setLoading(true);
    try {
      // Add your submit logic here
      ;
      message.success("Job created successfully!");
      form.resetFields();
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
        <Heading title={"Job Creation"} />
        <div className="form-section">
          <Form.Item label="Type of Creation" name="typeOfCreation">
            <Select placeholder="Select Creation Type">
              <Option value="material">Material</Option>
              <Option value="job">Job</Option>
              <Option value="work">Work</Option>
            </Select>
          </Form.Item>
          
          <FormInputItem label="Work Code" name="workCode" />
          
          <Form.Item label="Job Category" name="jobCategory">
            <Select showSearch placeholder="Select Job Category">
              {jobCategories.map(category => (
                <Option key={category} value={category}>
                  {category}
                </Option>
              ))}
            </Select>
          </Form.Item>
        </div>

        <div className="form-section">
          <Form.Item label="UOM" name="uom">
            <Select
              showSearch
              placeholder="Select Unit of Measure"
              optionFilterProp="children"
              filterOption={(input, option) =>
                option.children.toLowerCase().includes(input.toLowerCase())
              }
            >
              {uomOptions.map(uom => (
                <Option key={uom.value} value={uom.value}>
                  {uom.label}
                </Option>
              ))}
            </Select>
          </Form.Item>

          <FormInputItem label="Value" name="value" />
          
          <Form.Item
            label="Mode Of Procurement"
            name="modeOfProcurement"
            style={{ width: "32%" }}
          >
            <Select>
              <Option value="proprietary">Proprietary</Option>
              <Option value="limitedPreferred">Limited Preferred Vendor Tender</Option>
              <Option value="openTender">Open Tender</Option>
              <Option value="brandSpecific">Brand Specific</Option>
              <Option value="global">Global</Option>
            </Select>
          </Form.Item>
        </div>

        <div style={{
          display: "flex",
          justifyContent: "space-between",
          margin: "20px",
        }}>
          <Button type="default" htmlType="reset">
            <ReloadOutlined /> Reset
          </Button>
          <Button type="primary" htmlType="submit" loading={loading}>
            <SendOutlined /> Submit
          </Button>
          <Button type="dashed" htmlType="button">
            <SaveOutlined /> Save Draft
          </Button>
        </div>
      </Form>
    </FormContainer>
  );
};

export default JobCreation;