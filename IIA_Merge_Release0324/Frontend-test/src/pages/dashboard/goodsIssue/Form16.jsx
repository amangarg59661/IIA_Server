import { Button, Form, Input, InputNumber, Select, Upload } from "antd";
import { Option } from "antd/es/mentions";
import { UploadOutlined } from "@ant-design/icons";
import React, { useEffect } from "react";

const Form16 = () => {
  const [form] = Form.useForm();

  const onFinish = (values) => {
    ;
  };

  return (
    <div className="form-container">
      {/* <h2>Goods Issue</h2> */}
      <Form form={form} layout="vertical" onFinish={onFinish}>
        <h6>For Capital Goods</h6>
        <div className="form-section">
          <Form.Item
            label="Goods Issue No."
            name="goodsIssueNo"
            rules={[{ required: true, message: "This field is required" }]}
          >
            <Input disabled placeholder="Auto-generated" />
          </Form.Item>
          <Form.Item
            label="PO No."
            name="poNo"
            rules={[{ required: true, message: "This field is required" }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="GRIN No."
            name="grinNo"
            rules={[{ required: true, message: "This field is required" }]}
          >
            <Input />
          </Form.Item>
        </div>
        <div className="form-section">
          <Form.Item
            label="Consignee Details"
            name="consigneeDetails"
            rules={[{ required: true, message: "This field is required" }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="Indentor Name"
            name="indentorName"
            rules={[{ required: true, message: "This field is required" }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="Employee ID"
            name="employeeId"
            rules={[{ required: true, message: "This field is required" }]}
          >
            <InputNumber style={{ width: "100%" }} />
          </Form.Item>
        </div>
        <Form.Item
          label="Field Station"
          name="fieldStation"
          rules={[{ required: true, message: "This field is required" }]}
          style={{ width: "32%" }}
        >
          <Input />
        </Form.Item>
        <h6>For Consumable Goods</h6>
        <div className="form-section">
          <Form.Item
            label="Material Code"
            name="materialCode"
            rules={[{ required: true, message: "This field is required" }]}
          >
            <Input disabled placeholder="Auto-generated" />
          </Form.Item>
          <Form.Item label="UOM" name="UOM" rules={[{ required: true }]}>
            <Select>
              <Option value="KG">KG</Option>
              <Option value="Litre">Litre</Option>
            </Select>
          </Form.Item>
          <Form.Item
            label="Required Quantity"
            name="requiredQuantity"
            rules={[{ required: true, message: "This field is required" }]}
          >
            <InputNumber
              placeholder="Enter Quantity to be issued"
              style={{ width: "100%" }}
            />
          </Form.Item>
        </div>
        <Form.Item
          label="Locator"
          name="locator"
          rules={[{ required: true, message: "This field is required" }]}
          style={{ width: "32%" }}
        >
          <Input />
        </Form.Item>
        <h6>Common</h6>
        <div className="form-section">

            <Form.Item 
            label="Notes" 
            name="notes"
            >
            <Input.TextArea rows={1} placeholder="Additional remarks" />
            </Form.Item>
            <Form.Item label="Attach Photograph" name="photograph">
            <Upload listType="picture" beforeUpload={() => false}>
                <Button icon={<UploadOutlined />}>Upload</Button>
            </Upload>
            </Form.Item>
        </div>
        <Form.Item>
            <div style={{ display: "flex", justifyContent: "space-between" }}>
          <Button type="default" htmlType="reset">
            Reset
          </Button>
          <Button type="primary" htmlType="submit">
            Submit
          </Button>
          <Button type="dashed" htmlType="button">
            Save Draft
          </Button>
        </div>
        </Form.Item>
      </Form>
    </div>
  );
};

export default Form16;
