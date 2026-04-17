import React, { useEffect, useState } from "react";
import FormContainer from "../../components/DKG_FormContainer";
import { Form, message } from "antd";
import FormInputItem from "../../components/DKG_FormInputItem";
import CustomSelect from "../../components/CustomSelect";
import Heading from "../../components/DKG_Heading";
import { useSelector } from "react-redux";
import { modeOfProcurementList } from "../../utils/Constants";
import axios from "axios";
import Btn from "../../components/DKG_Btn";
import { Option } from "antd/es/mentions";
import TextAreaComponent from "../../components/DKG_TextAreaComponent";
import {
  Button,
  Radio,
  Select,
  Modal,
  Input,
} from "antd";
import {
  ReloadOutlined,
  SaveOutlined,
  SendOutlined,
} from "@ant-design/icons";
import { useLOVValues } from "../../hooks/useLOVValues";



const JobForm = ({ jobCode }) => {
  const [loading, setLoading] = useState(false);
  const [form] = Form.useForm();
  const [procurementMode, setProcurementMode] = useState("");
  const { categoryMaster, uomMaster, vendorMaster } = useSelector(
    (state) => state.masters
  );
  const { userId } = useSelector((state) => state.auth);
  const [jobList, setJobList] = useState([]);
  const [jobDetailsMap, setJobDetailsMap] = useState({});
  const [jobCategories, setJobCategories] = useState([]);
  const [jobSubcategories, setJobSubcategories] = useState([]);
  const [fileList, setFileList] = useState([]);
  const auth = useSelector((state) => state.auth);
  const actionPerformer = auth.userId;

  const [showJobCodePopup, setShowJobCodePopup] = useState(false);
  const [generatedJobCode, setGeneratedJobCode] = useState("");
  const [isEditMode, setIsEditMode] = useState(false);
  const [selectedJobCode, setSelectedJobCode] = useState(null);
  const [jobSearchList, setJobSearchList] = useState([]);

  // ✅ Fetch dropdown values from LOV system (Form ID: 5 - JobMaster)
  const { lovValues: jobCategoryLOV, loading: loadingJobCategory } = useLOVValues(5, 'jobCategory');
  const { lovValues: jobSubcategoryLOV, loading: loadingJobSubcategory } = useLOVValues(5, 'jobSubcategory');
  // Added by aman 
 const [approvalStatus, setApprovalStatus] = useState("");
  // End
  const { lovValues: uomLOV, loading: loadingUom } = useLOVValues(5, 'uom');
  const { lovValues: currencyLOV, loading: loadingCurrency } = useLOVValues(5, 'currency');

  // Fetch data with Axios
  const fetchInitialData = async () => {
    try {
      const jobResponse = await axios.get(`api/job-master`);
      const data = jobResponse.data;

      if (!data.responseData) throw new Error("Invalid job data");

      const categories = [
        ...new Set(data.responseData.map((item) => item.category)),
      ];
      const subCategories = [
        ...new Set(data.responseData.map((item) => item.subCategory)),
      ];

      setJobCategories(categories);
      setJobSubcategories(subCategories);

      const jobMap = data.responseData.reduce(
        (acc, job) => ({
          ...acc,
          [job.jobCode]: {
            ...job,
            jobDescription: job.description,
            jobCategory: job.category,
            jobSubCategory: job.subCategory,
          },
        }),
        {}
      );

      setJobDetailsMap(jobMap);
      setJobList(Object.keys(jobMap));

      // Pre-load approved jobs for the search dropdown (same pattern as Material Master)
      const approvedResponse = await axios.get(`/api/job-master/approved`);
      const approvedData = approvedResponse.data;
      if (Array.isArray(approvedData?.responseData)) {
        setJobSearchList(
          approvedData.responseData.map((item) => ({
            label: `${item.jobCode} - ${item.jobDescription}`,
            value: item.jobCode,
          }))
        );
      }

    } catch (error) {
      console.error("Material fetch error:", error);
      message.error("Failed to load data from server");
    }
  };

  useEffect(() => {
    fetchInitialData();
  }, []);

  // Search approved jobs by keyword (jobCode or description)
  const searchJobs = async (searchText) => {
    if (!searchText || searchText.trim().length < 1) return;
    try {
      const response = await axios.get(`/api/job-master/search?keyword=${searchText}`);
      const data = response.data;
      if (Array.isArray(data?.responseData)) {
        setJobSearchList(
          data.responseData.map((item) => ({
            label: `${item.jobCode} - ${item.jobDescription}`,
            value: item.jobCode,
          }))
        );
      }
    } catch (error) {
      console.error("Job search error:", error);
    }
  };

  // Load existing job data when jobCode prop is provided (edit mode)
  useEffect(() => {
    if (!jobCode) return;
    const loadJobData = async () => {
      try {
        const response = await axios.get(`/api/job-master/${jobCode}`);
        const jobData = response.data?.responseData;
        if (!jobData) return;
        setIsEditMode(true);
        form.setFieldsValue({
          jobCode: jobData.jobCode,
          category: jobData.category,
          subCategory: jobData.subCategory,
          description: jobData.jobDescription,
          uom: jobData.uom,
          briefDescription: jobData.briefDescription,
          estimatedPrice: jobData.estimatedPriceWithCcy,
          currency: jobData.currency,
          approvalStatus: jobData.approvalStatus,  
        });
       setApprovalStatus(jobData.approvalStatus);
      } catch (error) {
        message.error("Failed to load job data for editing.");
        console.error("Job load error:", error);
      }
    };
    loadJobData();
    
  }, [jobCode]);

  // Submit job data with Axios
  const handleSubmit = async (values) => {
    setLoading(true);
    try {
      const payload = {
        category: values.category,
        createdBy: actionPerformer,
        currency: values.currency,
        jobDescription: values.description,
        indigenousOrImported: values.indigenousOrImported,
        subCategory: values.subCategory,
        uom: values.uom,
        assetId: values.assetId,
        value: values.value,
        estimatedPriceWithCcy: values.estimatedPrice,
        briefDescription: values.briefDescription,
        updatedBy: String(actionPerformer),
        jobStatus: values.jobStatus || null,
reasonForDeactive: values.reasonForDeactive || null,
      };

      const activeJobCode = selectedJobCode || jobCode;
      let response;
      if (isEditMode && activeJobCode) {
        response = await axios.put(`/api/job-master/${activeJobCode}`, payload);
      } else {
        response = await axios.post(`api/job-master`, payload);
      }
      const result = response.data;

      if (!result.responseData) {
        throw new Error(result.responseStatus?.message || "Operation failed");
      }

      if (isEditMode) {
        message.success("Job updated successfully!");
      } else {
        setGeneratedJobCode(result.responseData?.jobCode);
        setShowJobCodePopup(true);
        message.success("Job created successfully!");
      }
    } catch (error) {
      console.error("Submission error:", error);
      message.error(
        error.response?.data?.responseStatus?.message ||
          "Failed to create job"
      );
    } finally {
      setLoading(false);
    }
  };

  // Job Code Popup
  const JobCodePopup = () => (
    <Modal
      title="Job Created Successfully"
      open={showJobCodePopup}
      onOk={() => setShowJobCodePopup(false)}
      onCancel={() => setShowJobCodePopup(false)}
      okText="OK"
    >
      <p>
        Generated Job Code: <strong>{generatedJobCode}</strong>
      </p>
      <p>Job created successfully! Job Code will be assigned after approval.</p>
    </Modal>
  );

  return (
    <FormContainer>
      <JobCodePopup />
      <Form
        onFinish={handleSubmit}
        form={form}
        layout="vertical"
        onValuesChange={(changedValues) => {
          if (changedValues.modeOfProcurement) {
            setProcurementMode(changedValues.modeOfProcurement);
          }
        }}
      >
        <Heading title={"Job Details"} />
        <div className="form-section">
          <Form.Item label="Search Job">
            <Select
              showSearch
              placeholder="Type to search job..."
              filterOption={false}
              onSearch={searchJobs}
              options={jobSearchList}
              onChange={async (selectedCode) => {
                if (!selectedCode) return;
                setSelectedJobCode(selectedCode);
                try {
                  const response = await axios.get(`/api/job-master/${selectedCode}`);
                  const jobData = response.data?.responseData;
                  if (jobData) {
                    setIsEditMode(true);
                    form.setFieldsValue({
                      jobCode: jobData.jobCode,
                      category: jobData.category,
                      subCategory: jobData.subCategory,
                      description: jobData.jobDescription,
                      uom: jobData.uom,
                      briefDescription: jobData.briefDescription,
                      estimatedPrice: jobData.estimatedPriceWithCcy,
                      currency: jobData.currency,
                       approvalStatus: jobData.approvalStatus,
                    });
                  }
                  setApprovalStatus(jobData.approvalStatus);
                } catch (error) {
                  message.error("Failed to load job details.");
                }
              }}
              allowClear
              onClear={() => {
                setSelectedJobCode(null);
                setIsEditMode(false);
                form.resetFields();
              }}
            />
          </Form.Item>
          <FormInputItem label="Job Code" name="jobCode" disabled />
          {isEditMode && (
  <FormInputItem
    label="Approval Status"
    name="approvalStatus"
    disabled
  />
)}
          
        </div>

        <div className="form-section">
<Form.Item
            name="category"
            label="Job Category"
            rules={[
              { required: true, message: "Please select job category!" },
            ]}
          >
            <Select placeholder="Select Job Category" loading={loadingJobCategory}>
              {jobCategoryLOV.map((item) => (
                <Option key={item.lovId || item.lovValue} value={item.lovValue}>
                  {item.lovDisplayValue}
                </Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            name="subCategory"
            label="Job Subcategory"
            rules={[{ required: true, message: "Please select subcategory!" }]}
          >
            <Select placeholder="Select Job Subcategory" loading={loadingJobSubcategory}>
              {jobSubcategoryLOV.map((item) => (
                <Option key={item.lovId || item.lovValue} value={item.lovValue}>
                  {item.lovDisplayValue}
                </Option>
              ))}
            </Select>
          </Form.Item>
        
        
          <Form.Item
            label="Job Description"
            name="description"
            rules={[{ required: true, message: "Please enter description!" }]}
          >
            <Input />
          </Form.Item>
              </div>
              <div className="form-section">
          <Form.Item
            name="uom"
            label="UOM"
            rules={[{ required: true, message: "Please select UOM!" }]}
          >
            <Select placeholder="Select Unit of Measure" loading={loadingUom}>
              {uomLOV.map(lov => (
                <Option key={lov.lovId || lov.lovValue} value={lov.lovValue}>
                  {lov.lovDisplayValue}
                </Option>
              ))}
            </Select>
          </Form.Item>

          <TextAreaComponent
            label="Brief Description of Job"
            name="briefDescription"
            required
          />
          <FormInputItem
            type="number"
            name="estimatedPrice"
            label="Estimated Price"
            required
          />
        </div>

        <div className="form-section">
          
          <Form.Item
            name="currency"
            label="Currency"
            rules={[{ required: true, message: "Please select currency!" }]}
          >
            <Select placeholder="Select Currency" loading={loadingCurrency}>
              {currencyLOV.map((item) => (
                <Option key={item.lovId || item.lovValue} value={item.lovValue}>
                  {item.lovDisplayValue}
                </Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            name="indigenousOrImported"
            label="Origin"
            rules={[{ required: true, message: "Please select origin!" }]}
          >
            <Radio.Group>
              <Radio value="indigenous">Indigenous</Radio>
              <Radio value="imported">Imported</Radio>
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
            <ReloadOutlined /> Reset
          </Button>
          <Button type="primary" htmlType="submit" loading={loading}>
            <SendOutlined /> {isEditMode ? "Update" : "Create"}
          </Button>
          <Button type="dashed" htmlType="button">
            <SaveOutlined /> Save Draft
          </Button>
        </div>
      </Form>
    </FormContainer>
  );
};

export default JobForm;
