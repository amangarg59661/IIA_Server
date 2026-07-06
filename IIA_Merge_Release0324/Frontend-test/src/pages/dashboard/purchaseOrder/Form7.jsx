import React, { useEffect, useState } from "react";
import axios from "axios";
import {
  Button,
  Checkbox,
  Col,
  Form,
  Input,
  message,
  Modal,
  Row,
  Select,
  Space,
} from "antd";
import {
  MinusCircleOutlined,
  PlusOutlined,
  SearchOutlined,
} from "@ant-design/icons";
import TextArea from "antd/es/input/TextArea";
import { useSelector } from "react-redux";
import CustomModal from "../../../components/CustomModal";

// Option can be destructured from Select for cleaner code
const { Option } = Select;

const Form7 = () => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [searching, setSearching] = useState(false);
  const [tenders, setTenders] = useState([]); // Store Tender IDs
  const [selectedIndentId, setSelectedIndentId] = useState(""); // Store Indent ID
  const [materialDetails, setMaterialDetails] = useState([]); // Store Material Details
  const [vendors, setVendors] = useState([]);
  const [loadingVendors, setLoadingVendors] = useState(false);
  const [showLineItems, setShowLineItems] = useState(false);
  const [generatedPOId, setGeneratedPOId] = useState("");
  const [showSuccessModal, setShowSuccessModal] = useState(false);
  const auth = useSelector((state) => state.auth);
  const actionPerformer = auth.userId;
  const [isManualVendor, setIsManualVendor] = useState(false); // added new by abhinav

  // const [isLocked, setIsLocked] = useState(false);

  // Fetch vendors
  useEffect(() => {
    const fetchVendors = async () => {
      setLoadingVendors(true);
      try {
        const { data } = await axios.get("/api/vendor-master");

        if (data.responseData) {
          setVendors(data.responseData);
        } else {
          message.error("Failed to fetch vendors");
        }
      } catch (error) {
        console.error("Error fetching vendors:", error);
        message.error("Error fetching vendor data");
      } finally {
        setLoadingVendors(false);
      }
    };

    fetchVendors();
  }, []);

  // Handle vendor selection
  // const handleVendorSelect = (vendorId) => {
  //   const selectedVendor = vendors.find((v) => v.vendorId === vendorId);
  //   if (!selectedVendor) return;

  //   form.setFieldsValue({
  //     vendorName: selectedVendor.vendorName,
  //     vendorAddress: selectedVendor.address,
  //     vendorAccountNo: selectedVendor.accountNo,
  //     vendorsIFSCCode: selectedVendor.ifscCode,
  //     vendorAccountName: selectedVendor.vendorName,
  //     vendorId: selectedVendor.vendorId,
  //     vendorGST: selectedVendor.gstNo,
  //     vendorPAN: selectedVendor.panNo,
  //     vendorContact: selectedVendor.mobileNo || selectedVendor.contactNo,
  //   });
  // };

  // updated by abhinav - added OTHERS option in vendor dropdown and handle manual entry of vendor details start
  const handleVendorSelect = (vendorId) => {

    if (vendorId === "OTHERS") {

      setIsManualVendor(true);

      form.setFieldsValue({
        vendorId: "OTHERS",
        vendorName: "",
        vendorAddress: "",
        vendorAccountNo: "",
        vendorsIFSCCode: "",
        vendorAccountName: ""
      });

      return;
    }

    setIsManualVendor(false);

    const selectedVendor = vendors.find(v => v.vendorId === vendorId);

    if (!selectedVendor) return;

    form.setFieldsValue({
      vendorName: selectedVendor.vendorName,
      vendorAddress: selectedVendor.address,
      vendorAccountNo: selectedVendor.accountNo,
      vendorsIFSCCode: selectedVendor.ifscCode,
      vendorAccountName: selectedVendor.vendorName
    });

  };
  // updated by abhinav - added OTHERS option in vendor dropdown and handle manual entry of vendor details end

  // **1. Fetch All Tender IDs**
  useEffect(() => {
    const fetchData = async () => {
      try {
        // Get approved tender IDs
        const { data: approvedData } = await axios.get("/getApprovedTenderIdForPO");
        const approvedIds = approvedData.responseData || [];

        // Get all tender details
        const { data: tendersData } = await axios.get("/api/tender-requests");

        // Filter and combine data
        const approvedTenders = (tendersData.responseData || [])
          .filter((tender) => approvedIds.includes(tender.tenderId))
          .map((tender) => ({
            ...tender,
            indentIds: tender.indentIds || [],
          }));

        setTenders(approvedTenders);
      } catch (error) {
        message.error("Failed to load tender data");
        console.error("Fetch error:", error);
      }
    };

    fetchData();
  }, []);

  // 2. Handle tender selection and fetch materials
  const handleTenderSelect = async (tenderId) => {
    setShowLineItems(false);
    try {
      // Clear previous data
      setMaterialDetails([]);
      form.resetFields(["lineItems"]);

      // Find selected tender
      const selectedTender = tenders.find((t) => t.tenderId === tenderId);
      if (!selectedTender) return;

      // Fetch materials for all indents
      const allMaterials = await Promise.all(
        selectedTender.indentIds.map(async (indentId) => {
          try {
            const { data } = await axios.get(`/api/indents/${indentId}`);

            return (data.responseData?.materialDetails || []).map(
              (material) => ({
                materialCode: material.materialCode,
                materialDescription: material.materialDescription,
                quantity: material.quantity,
                unitRate: material.unitPrice,
                uom: material.uom,
              })
            );
          } catch (error) {
            console.error(`Error fetching indent ${indentId}:`, error);
            return [];
          }
        })
      );

      // Flatten and set materials
      const flattenedMaterials = allMaterials.flat();
      setMaterialDetails(flattenedMaterials);

      // Update form fields
      form.setFieldsValue({
        lineItems: flattenedMaterials,
        incoTerms: selectedTender.incoTerms,
        paymentTerms: selectedTender.paymentTerms,
      });
      setShowLineItems(true);
    } catch (error) {
      message.error("Failed to load tender details");
      console.error("Tender selection error:", error);
    }
  };

  const handlePOSearch = async (poId) => {
    setShowLineItems(false);
    if (!poId) {
      message.warning("Please enter a PO ID");
      return;
    }

    setSearching(true);
    try {
      const { data } = await axios.get(`/api/purchase-orders/${poId}`);

      if (!data.responseData) {
        message.warning("No purchase order found for this PO ID");
        form.resetFields();
        return;
      }

      const poDetails = data.responseData;

      setSelectedIndentId(poDetails.indentId); // added new by abhinav



      // 1. Extract all material details from indents
      const allMaterials =
        poDetails.tenderDetails?.indentResponseDTO?.flatMap((indent) =>
          indent.materialDetails.map((material) => ({
            materialCode: material.materialCode,
            materialDescription: material.materialDescription,
            quantity: material.quantity,
            unitRate: material.unitPrice, // Map unitPrice to unitRate
            uom: material.uom,
            currency: "INR", // Default value
            exchangeRate: 1, // Default value
            gst: 0, // Default value
            duties: 0, // Default value
            freightCharges: 0, // Default value
          }))
        ) || [];

      // 2. Set form values
      form.setFieldsValue({
        tenderID: poDetails.tenderId,
        indentID: poDetails.indentId,
        consigneeAddress: poDetails.consignesAddress,
        billingAddress: poDetails.billingAddress,
        deliveryPeriod: poDetails.deliveryPeriod,
        warranty: poDetails.warranty,
        ifLDClauseApplicable: poDetails.ifLdClauseApplicable,
        incoTerms: poDetails.incoTerms,
        paymentTerms: poDetails.paymentTerms,
        vendorName: poDetails.vendorName,
        vendorAddress: poDetails.vendorAddress,
        applicablePBG: poDetails.applicablePbgToBeSubmitted,
        transporterDetails: poDetails.transporterAndFreightForWarderDetails,
        vendorAccountNo: poDetails.vendorAccountNumber,
        vendorsIFSCCode: poDetails.vendorsIfscCode,
        vendorAccountName: poDetails.vendorAccountName,
        lineItems: allMaterials, // Set the material details directly
      });

      // 3. Update state
      setMaterialDetails(allMaterials);
      setShowLineItems(true);
    } catch (error) {
      message.error("Failed to fetch PO data");
      console.error("Error fetching data:", error);
    } finally {
      setSearching(false);
    }
  };

  // **3. Fetch Material Details from Indent API**
  const fetchMaterialDetails = async (indentId) => {
    try {
      const { data } = await axios.get(`/api/indents/${indentId}`);

      if (!data.responseData?.materialDetails) {
        message.error("No Material Details found");
        return;
      }

      const formatMaterial = (item, index) => ({
        key: index,
        materialCode: item.materialCode,
        materialDescription: item.materialDescription,
        quantity: item.quantity,
        unitRate: item.unitPrice,
        uom: item.uom,
        totalPrice: item.totalPrice || item.totalPrize, // Handle field name mismatch
      });

      const formattedMaterials =
        data.responseData.materialDetails.map(formatMaterial);
      setMaterialDetails((prev) => [...prev, ...formattedMaterials]);
      form.setFieldsValue({ lineItems: [...formattedMaterials] });
    } catch (error) {
      console.error("Error fetching Material Details:", error);
      message.error("Error fetching Material Details");
    }
  };
  // Function to handle form submission
  const submitPOData = async (values) => {
    setLoading(true);
    try {
      const updatedLineItems = values.lineItems.map((item) => ({
        materialCode: item.materialCode,
        materialDescription: item.materialDescription,
        quantity: parseFloat(item.quantity) || 0,
        rate: parseFloat(item.unitRate) || 0,
        currency: item.currency || "INR",
        exchangeRate: parseFloat(item.exchangeRate) || 1,
        gst: parseFloat(item.gst) || 0,
        duties: parseFloat(item.duties) || 0,
        freightCharge: parseFloat(item.freightCharges) || 0,
      }));

      const payload = {
        poId: values.poId,
        tenderId: values.tenderID,
        indentId: selectedIndentId,
        consignesAddress: values.consigneeAddress || "Banglore",
        billingAddress:
          values.billingAddress || "Koramangala, Bangalore - 560034",
        deliveryPeriod: parseFloat(values.deliveryPeriod) || 0,
        warranty: parseFloat(values.warranty) || 0,
        ifLdClauseApplicable: !!values.ifLDClauseApplicable, // Ensure boolean
        // incoterms: values.incoTerms,
        // paymentterms: values.paymentTerms,
        // updated by abhinav
        incoTerms: values.incoTerms,
        paymentTerms: values.paymentTerms,
        vendorName: values.vendorName,
        vendorId: values.vendorId,
        vendorAddress: values.vendorAddress,
        applicablePbgToBeSubmitted: values.applicablePBG,
        transposterAndFreightForWarderDetails: values.transporterDetails,
        vendorAccountNumber: values.vendorAccountNo,
        vendorsIfscCode: values.vendorsIFSCCode,
        vendorAccountName: values.vendorAccountName,
        purchaseOrderAttributes: updatedLineItems,
        createdBy: actionPerformer,
        updatedBy: null,
      };

      // const response = await fetch(
      //   "http://103.181.158.220:8081/astro-service/api/purchase-orders",
      //   {
      //     method: "POST",
      //     headers: {
      //       "Content-Type": "application/json", // Explicit JSON content type
      //     },
      //     body: JSON.stringify(payload),
      //   }
      // );

      // updated by abhinav
      const isUpdate = values.poId && values.poId !== "";

      const url = isUpdate
        ? `/api/purchase-orders/${values.poId}`
        : "/api/purchase-orders";

      const { data: responseData } = isUpdate
        ? await axios.put(url, payload)
        : await axios.post(url, payload);

      if (responseData.responseStatus?.statusCode === 0) {
        // Set generated ID and update form
        setGeneratedPOId(responseData.responseData.poId);
        form.setFieldsValue({ poId: responseData.responseData.poId });
        setShowSuccessModal(true);
        // setIsPrintEnabled(true);
        message.success("PO created successfully!");
      } else {
        throw new Error(
          responseData.responseStatus?.message || "Submission failed"
        );
      }
    } catch (error) {
      message.error(`Failed to submit PO: ${error.message}`);
      console.error("Submission Error:", error);
    } finally {
      setLoading(false);
      setShowLineItems(false);
    }
  };

  return (
    <div className="form-container">
      <h2>Purchase Order (PO)</h2>
      <Form
        form={form}
        layout="vertical"
        onFinish={submitPOData}
        initialValues={{
          date: null,
          billingAddress: "Koramangala, Bangalore - 560034",
        }}
      >
        <Row justify="end">
          <Col>
            {/* The PO ID field uses an Input.Search component.
              When the user presses Enter or clicks the search icon,
              the entered PO ID is passed to handleTenderSearch. */}
            <Form.Item
              label="PO ID"
              name="poId" // Changed from "poID" to "poId"
            //   rules={[{ required: true, message: "Please enter PO ID" }]}
            >
              <Input.Search
                placeholder="Enter PO ID"
                onSearch={handlePOSearch}
                enterButton={<SearchOutlined />}
                loading={searching}
              />
            </Form.Item>
          </Col>
        </Row>
        <div className="form-section">
          <Form.Item
            label="Tender ID"
            name="tenderID"
            rules={[{ required: true, message: "Please select a Tender ID" }]}
          >
            <Select
              placeholder="Select a Tender ID"
              onChange={handleTenderSelect}
              showSearch
            >
              {tenders.map((tender) => (
                <Option key={tender.tenderId} value={tender.tenderId}>
                  {tender.tenderId} - {tender.titleOfTender}
                </Option>
              ))}
            </Select>
          </Form.Item>

          {/* Consignee Address */}
          <Form.Item label="Consignee Address" name="consigneeAddress">
            <TextArea
              rows={1}
              placeholder="Enter consignee address"
              defaultValue={"Bangalore"}
            />
          </Form.Item>

          {/* Billing Address */}
          <Form.Item
            label="Billing Address"
            name="billingAddress"
            rules={[
              { required: true, message: "Please enter billing address" },
            ]}
          >
            <TextArea
              rows={2}
              placeholder="Enter billing address"
              defaultValue={"Koramangala, Bangalore - 560034"}
            />
          </Form.Item>

          {/* Delivery Period */}
          <Form.Item
            label="Delivery Period"
            name="deliveryPeriod"
            rules={[
              { required: true, message: "Please specify the delivery period" },
            ]}
          >
            <Input type="number" placeholder="Enter delivery period" />
          </Form.Item>
        </div>
        {showLineItems && (
          <div>
            <Form.List name="lineItems" initialValue={[{}]}>
              {(fields, { add, remove }) => (
                <>
                  {fields.map(({ key, name, fieldKey, ...restField }) => (
                    <div
                      key={key}
                      style={{
                        border: "1px solid #ccc",
                        padding: "20px",
                        marginBottom: "5px",
                      }}
                    >
                      <Space
                        style={{
                          display: "flex",
                          marginBottom: 5,
                          flexWrap: "wrap",
                        }}
                        align="start"
                      >
                        <Row gutter={16}>
                          <Col span={8}>
                            {/* Note: When using Form.List, use an array for the name */}
                            <Form.Item
                              {...restField}
                              name={[name, "materialCode"]}
                              label="Material Code"
                              rules={[
                                {
                                  required: true,
                                  message: "Please select a material code!",
                                },
                              ]}
                            >
                              <Input placeholder="Enter Material Code" />
                            </Form.Item>
                          </Col>

                          <Col span={8}>
                            <Form.Item
                              {...restField}
                              name={[name, "materialDescription"]}
                              label="Material Description"
                              rules={[
                                {
                                  required: true,
                                  message:
                                    "Please enter a material description!",
                                },
                              ]}
                            >
                              <Input placeholder="Enter Material Description" />
                            </Form.Item>
                          </Col>

                          <Col span={8}>
                            <Form.Item
                              {...restField}
                              name={[name, "quantity"]}
                              label="Quantity"
                              rules={[
                                {
                                  required: true,
                                  message: "Please enter quantity!",
                                },
                              ]}
                            >
                              <Input
                                type="number"
                                placeholder="Enter Quantity"
                              />
                            </Form.Item>
                          </Col>

                          <Col span={8}>
                            <Form.Item
                              {...restField}
                              name={[name, "unitRate"]}
                              label="Unit Rate"
                              rules={[
                                {
                                  required: true,
                                  message: "Please enter the unit rate",
                                },
                              ]}
                            >
                              <Input
                                type="number"
                                step="0.01"
                                placeholder="Enter unit rate"
                              />
                            </Form.Item>
                          </Col>

                          <Col span={8}>
                            <Form.Item
                              {...restField}
                              name={[name, "currency"]}
                              label="Currency"
                              rules={[
                                {
                                  required: true,
                                  message: "Please select a currency",
                                },
                              ]}
                            >
                              <Select placeholder="Select currency">
                                <Option value="USD">USD</Option>
                                <Option value="INR">INR</Option>
                              </Select>
                            </Form.Item>
                          </Col>

                          <Col span={8}>
                            <Form.Item
                              {...restField}
                              name={[name, "exchangeRate"]}
                              label="Exchange Rate"
                            >
                              <Input
                                type="number"
                                step="0.01"
                                placeholder="Enter exchange rate"
                              />
                            </Form.Item>
                          </Col>

                          <Col span={8}>
                            <Form.Item
                              {...restField}
                              name={[name, "gst"]}
                              label="GST (%)"
                              rules={[
                                {
                                  required: true,
                                  message: "Please specify GST percentage",
                                },
                              ]}
                            >
                              <Input
                                type="number"
                                step="0.01"
                                placeholder="Enter GST percentage"
                              />
                            </Form.Item>
                          </Col>

                          <Col span={8}>
                            <Form.Item
                              {...restField}
                              name={[name, "duties"]}
                              label="Duties (%)"
                              rules={[
                                {
                                  required: true,
                                  message: "Please specify duties",
                                },
                              ]}
                            >
                              <Input
                                type="number"
                                step="0.01"
                                placeholder="Enter duties percentage"
                              />
                            </Form.Item>
                          </Col>

                          <Col span={8}>
                            <Form.Item
                              {...restField}
                              name={[name, "freightCharges"]}
                              label="Freight Charges"
                            >
                              <Input
                                type="number"
                                step="0.01"
                                placeholder="Enter freight charges"
                              />
                            </Form.Item>
                          </Col>
                        </Row>
                        {/* <MinusCircleOutlined onClick={() => remove(name)} /> */}
                      </Space>
                    </div>
                  ))}
                  {/* <Form.Item>
                    <Button
                        type="dashed"
                        onClick={() => add()}
                        icon={<PlusOutlined />}
                        style={{ width: "32%" }}
                    >
                        Add Item
                    </Button>
                    </Form.Item> */}
                </>
              )}
            </Form.List>
          </div>
        )}

        <div className="form-section">
          {/* Warranty */}
          <Form.Item label="Warranty" name="warranty">
            <Input placeholder="Enter warranty terms" />
          </Form.Item>

          {/* If LD clause applicable */}
          <Form.Item name="ifLDClauseApplicable" valuePropName="checked">
            <Checkbox>If LD clause applicable?</Checkbox>
          </Form.Item>

          <Form.Item label="INCO Terms" name="incoTerms">
            <Input.TextArea rows={1} placeholder="Enter INCO Terms" />
          </Form.Item>

          <Form.Item label="Payment Terms" name="paymentTerms">
            <Input.TextArea rows={1} placeholder="Enter Payment Terms" />
          </Form.Item>
        </div>
        <div className="form-section">
          <Form.Item label="Applicable PBG" name="applicablePBG">
            <TextArea rows={1} />
          </Form.Item>

          {/* Transporter / Freight Forwarder Details */}
          <Form.Item label="Transporter Details" name="transporterDetails">
            <TextArea rows={1} />
          </Form.Item>
        </div>
        <div className="form-section">
          {/* Vendor Name */}
          <Form.Item
            label="Vendor Name"
            name="vendorName"
            rules={[{ required: true, message: "Please enter vendor name" }]}
          >
          <Input placeholder="Enter vendor name" disabled={!isManualVendor} />
          </Form.Item>
          <Form.Item
            label="Vendor"
            name="vendorId"
            rules={[{ required: true, message: "Please select vendor" }]}
          >
          <Select
            showSearch
            placeholder="Select vendor"
            loading={loadingVendors}
            onChange={handleVendorSelect}
            optionFilterProp="children"
          >

          <Option value="OTHERS" label="OTHERS (Manual Vendor)">
            OTHERS (Manual Vendor)
          </Option>

          {vendors.map((vendor) => (
            <Option
              key={vendor.vendorId}
              value={vendor.vendorId}

            >
              {vendor.vendorId} - {vendor.vendorName}
            </Option>
          ))}

          </Select>
          </Form.Item>

          {/* Vendor Address */}
          <Form.Item
            label="Vendor Address"
            name="vendorAddress"
            rules={[{ required: true, message: "Please enter vendor address" }]}
          >
            <TextArea rows={1} placeholder="Enter vendor address" disabled={!isManualVendor} />
          </Form.Item>

          {/* Applicable PBG to be submitted */}
        </div>

        <div className="form-section">
          {/* Vendor's A/C no */}
          <Form.Item
            label="Vendor's A/C no"
            name="vendorAccountNo"
            rules={[
              {
                required: true,
                message: "Please enter vendor's account number",
              },
            ]}
          >
            <Input placeholder="Enter vendor's account number" disabled={!isManualVendor} />
          </Form.Item>

          {/* Vendor's IFSC Code */}
          <Form.Item
            label="Vendor's IFSC code"
            name="vendorsIFSCCode"
            rules={[
              { required: true, message: "Please enter vendor's IFSC code" },
            ]}
          >
            <Input placeholder="Enter vendor's IFSC code" disabled={!isManualVendor} />
          </Form.Item>

          {/* Vendor's A/C Name */}
          <Form.Item
            label="Vendor's A/C Name"
            name="vendorAccountName"
            rules={[
              { required: true, message: "Please enter vendor's account name" },
            ]}
          >
            <Input placeholder="Enter vendor's account name" disabled={!isManualVendor} />
          </Form.Item>
        </div>

        {/* Submit Button Section */}
        <div className="form-section">
          <Button type="default" htmlType="reset">
            Reset
          </Button>
          <Button type="primary" htmlType="submit" loading={loading}>
            Submit
          </Button>
          <Button type="dashed" htmlType="button">
            Save Draft
          </Button>
        </div>
        <Modal
          open={showSuccessModal}
          title="Purchase Order Created"
          onCancel={() => setShowSuccessModal(false)}
          footer={[
            // <Button
            //   key="print"
            //   type="primary"
            //   onClick={handlePrint}
            //   disabled={!isPrintEnabled}
            // >
            //   Print
            // </Button>,
            <Button key="close" onClick={() => setShowSuccessModal(false)}>
              Close
            </Button>,
          ]}
        >
          <p>Generated PO ID: {generatedPOId}</p>
        </Modal>
      </Form>
    </div>
  );
};

export default Form7;
