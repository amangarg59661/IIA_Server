import React, { useEffect, useState } from "react";
import { Form, Input, Button, Row, Col, message } from "antd";
import { useSelector } from "react-redux";
import { useParams } from "react-router-dom";
import axios from "axios";

const Form3 = () => {
  const { vendorId } = useParams();
  const auth = useSelector((state) => state.auth);
  const actionPerformer = auth.userId;
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [fetching, setFetching] = useState(true);

  // 1) Fetch purchase-order data on mount
  useEffect(() => {
    const fetchVendorDetails = async () => {
      try {
       const res = await axios.get(
        `/api/vendor-master/approvedVendorData/${vendorId}`,
          {
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${auth.token}`,
            },
          }
        );
        const data = res.data;
        //if (!res.ok) throw new Error(data.responseStatus?.message || res.statusText);
        
        // assume the API returns an array of DTOs, take the first
        const dto = Array.isArray(data.responseData) ? data.responseData[0] : data.responseData;

        // 2) Populate the form fields
        form.setFieldsValue({

          purchaseOrder:        dto.purchaseOrderNumber,
          deliveryAndAcceptanceStatus: dto.deliveryAndAcceptanceStatus,
          paymentStatus:        dto.paymentStatus,
          paymentUTRNumber:     dto.paymentUtrNumber,
          date:                 dto.paymentDate,
        });
      } catch (err) {
        console.error("Fetch error:", err);
        message.error("Could not load approved-vendor details: " + err.message);
      } finally {
        setFetching(false);
      }
    };

    fetchVendorDetails();
  }, [vendorId, auth.token, form]);

  // 3) Submit handler (if you still need to submit something)
  const handleSubmit = async (values) => {
    setLoading(true);
    try {
      // POST logic will submit here…
    } finally {
      setLoading(false);
    }
  };

  if (fetching) {
    return <div>Loading purchase order details…</div>;
  }

  return (
    <div className="form-container">
      <h2>Purchase Order Details</h2>
      <Form
        form={form}
        layout="vertical"
        onFinish={handleSubmit}
        onFinishFailed={() => message.error("Please fill all required fields")}
      >
        <Form.Item label="Purchase Order (PO) Number" name="purchaseOrder">
          <Input disabled />
        </Form.Item>

        <Form.Item
          label="Delivery/Acceptance Status"
          name="deliveryAndAcceptanceStatus"
        >
          <Input disabled />
        </Form.Item>

        <Form.Item label="Payment Status" name="paymentStatus">
          <Input disabled />
        </Form.Item>

        <Form.Item label="Payment UTR Number" name="paymentUTRNumber">
          <Input disabled />
        </Form.Item>

        <Form.Item label="Payment Date" name="date">
          <Input disabled />
        </Form.Item>

        <Form.Item>
          <Row justify="space-between">
            {/* <Col>
              <Button type="default" htmlType="reset" onClick={() => form.resetFields()}>
                Reset
              </Button>
            </Col> */}
            <Col>
              <Button type="primary" htmlType="submit" loading={loading}>
                OK
              </Button>
            </Col>
          </Row>
        </Form.Item>
      </Form>
    </div>
  );
};

export default Form3;