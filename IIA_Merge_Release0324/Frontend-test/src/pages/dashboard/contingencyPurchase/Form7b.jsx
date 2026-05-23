import React, { useEffect, useState } from "react";
import {
  Button,
  Col,
  DatePicker,
  Form,
  Input,
  message,
  Modal,
  Row,
  Select,
  Space,
  Spin,
  Upload,
} from "antd";
import {
  DeleteOutlined,
  MinusCircleOutlined,
  PlusOutlined,
  SearchOutlined,
  UploadOutlined,
} from "@ant-design/icons";
import { Option } from "antd/es/mentions";
import dayjs from "dayjs";
import axios from "axios";
import { useSelector } from "react-redux";
import { values } from "lodash";
// import LineItem from "../LineItem";
import CustomModal from "../../../components/CustomModal";

const Form7b = () => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [contingencyId, setContingencyId] = useState("");
  const [projects, setProjects] = useState([]);
  const [materialList, setMaterialList] = useState([]);
  const [materialDetailsMap, setMaterialDetailsMap] = useState({});
  const [vendors, setVendors] = useState([]);
  const [vendorLoading, setVendorLoading] = useState(false);
  const [generatedContingencyId, setGeneratedContingencyId] = useState("");
  const [showSuccessModal, setShowSuccessModal] = useState(false);

  const auth = useSelector((state) => state.auth);
  const actionPerformer = auth.userId;

  const { vendorMaster } = useSelector((state) => state.masters);
  const vendorMasterMod = vendorMaster?.map((vendor) => ({
    label: vendor.vendorName,
    value: vendor.vendorName,
  }));

  useEffect(() => {
    const fetchVendors = async () => {
      setVendorLoading(true);
      try {
        const { data } = await axios.get("/api/vendor-master");

        if (
          data.responseStatus.statusCode === 0 &&
          Array.isArray(data.responseData)
        ) {
          setVendors(data.responseData);
        } else {
          message.error("Failed to fetch vendors");
        }
      } catch (error) {
        console.error("Error fetching vendors:", error);
        message.error("Failed to fetch vendor data");
      } finally {
        setVendorLoading(false);
      }
    };

    fetchVendors();
  }, []);

  useEffect(() => {
    const fetchProjects = async () => {
      setLoading(true);
      try {
        const { data } = await axios.get("/api/project-master");

        if (
          data.responseStatus.statusCode === 0 &&
          Array.isArray(data.responseData)
        ) {
          setProjects(data.responseData);
        } else {
          message.error("Failed to project data");
        }
      } catch (error) {
        console.error("Error fetching projects:", error);
        message.error("Failed to fetch project data");
      } finally {
        setLoading(false);
      }
    };
    fetchProjects();
  }, []);

  const fetchContingencyData = async () => {
    if (!contingencyId) {
      message.warning("Please enter a Contingency ID.");
      return;
    }

    try {
      const { data } = await axios.get(
        `/api/contigency-purchase/${contingencyId}`
      );

      if (data.responseData) {
        const purchase = data.responseData;
        const getFileList = (fileName) =>
          fileName ? [{ uid: "-1", name: fileName, status: "done" }] : [];
        const formattedData = {
          vendorName: purchase.vendorsName,
          vendorInvoiceNo: purchase.vendorsInvoiceNo,
          date: purchase.date ? dayjs(purchase.date, "DD/MM/YYYY") : undefined,
          remarks: purchase.remarksForPurchase,
          amountToBePaid: purchase.amountToBePaid,
          predefinedPurchaseStatement: purchase.predifinedPurchaseStatement,
          projectDetail: purchase.projectDetail,
          uploadCopyOfInvoice: getFileList(purchase.uploadedFileName),
          lineItems: [
            {
              materialCode: purchase.materialCode,
              materialDescription: purchase.materialDescription,
              quantity: purchase.quantity,
              unitPrice: purchase.unitPrice,
              totalPrice: purchase.quantity * purchase.unitPrice,
            },
          ],
        };
        form.setFieldsValue(formattedData);
        message.success("Contingency data fetched successfully!");
      } else {
        message.error("No contingency purchase found with this ID.");
      }
    } catch (error) {
      console.error("Error fetching contingency data:", error);
      message.error("Failed to fetch contingency data.");
    }
  };
  const showAndRemove = true;

  const normFile = (e) => {
    // When uploading, an array of file objects is expected.
    // If e is already an array, return it. Otherwise, return e.fileList.
    if (Array.isArray(e)) {
      return e;
    }
    return e && e.fileList;
  };

  // Submit contingency purchase data
  // Add this utility function at the top of your file
  const uploadFileToServer = async (file, fieldName) => {
    try {
      const formData = new FormData();
      formData.append("file", file);

      const { data } = await axios.post(
        "/file/upload?fileType=CP",
        formData,
        { headers: { "Content-Type": "multipart/form-data" } }
      );

      return data.responseData.fileName;
    } catch (error) {
      const errorMsg = error.response?.data?.responseStatus?.message || "File upload failed";
      console.error(`File upload error (${fieldName}):`, error);
      throw new Error(`Failed to upload ${fieldName}: ${errorMsg}`);
    }
  };
  // Modified submit function
  const submitContingencyData = async (values) => {
    setLoading(true);
    try {
      const hasInvalidTotal = values.lineItems?.some((item) => {
        const total = item.quantity * item.unitPrice;
        return total > 50000;
      });

      if (hasInvalidTotal) {
        message.error("One or more items exceed the ₹50,000 limit");
        return;
      }
      //   const lineItem = values.lineItems[0];

      const totalAmount = values.lineItems.reduce(
        (sum, item) => sum + (item.totalPrice || 0),
        0
      );
      const amountToBePaid = parseFloat(values.amountToBePaid) || 0;

      if (amountToBePaid > totalAmount) {
        message.error("Amount to be paid cannot exceed total amount");
        return;
      }

      // Handle file upload first
      const uploadFiles = async (fileList, fieldName) => {
        if (!fileList || fileList.length === 0) return "";
        const uploadedNames = await Promise.all(
          fileList.map((file) =>
            uploadFileToServer(file.originFileObj, fieldName)
          )
        );
        return uploadedNames.join(", ");
      };

      const uploadedFileName = await uploadFiles(
        values.uploadCopyOfInvoice,
        "Invoice Copy"
      );

      // Build payload with file name
      const payload = {
        // contigencyId: contingencyId || null,
        vendorsName: values.vendorName,
        vendorsInvoiceNo: values.vendorInvoiceNo,
        lineItems: values.lineItems.map((item) => ({
          materialCode: item.materialCode,
          materialDescription: item.materialDescription,
          quantity: item.quantity,
          unitPrice: item.unitPrice,
          uom: item.uom,
          budgetCode: item.budgetCode,
          materialCategory: item.materialCategory,
          totalPrice: item.totalPrice,
        })),
        remarksForPurchase: values.remarks,
        amountToBePaid: parseFloat(values.amountToBePaid) || 0,
        predifinedPurchaseStatement: values.predefinedPurchaseStatement || null,
        projectName: values.projectName || null,
        date: values.date?.format("DD/MM/YYYY"),
        createdBy: actionPerformer,
        updatedBy: null,
        uploadCopyOfInvoice: uploadedFileName || "", // Changed field name to match DTO
        fileType: "CP", // Add fileType as per DTO
      };

      // Submit as JSON
      const { data: responseData } = await axios.post(
        "/api/contigency-purchase",
        payload
      );

    // Fix 1: Check correct response structure
    if (responseData.responseData?.contigencyId) {
      setGeneratedContingencyId(responseData.responseData?.contigencyId);
      setShowSuccessModal(true);  // Fix 2: Ensure modal state is set
      message.success("Contingency purchase created successfully");
    }
    else {
      throw new Error("No contingency ID in response");
    }
    } catch (error) {
      console.error("Submission Error:", error);
      message.error(`Error: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    const fetchMaterials = async () => {
      try {
        const { data } = await axios.get("/api/material-master");
        if (!data.responseData)
          throw new Error("Invalid material master response");

        // ✅ Extract material codes and details
        const materials = data.responseData;
        setMaterialList(materials.map((mat) => mat.materialCode));

        // ✅ Create a lookup object for quick access
        const materialMap = {};
        materials.forEach((mat) => {
          materialMap[mat.materialCode] = mat;
        });
        setMaterialDetailsMap(materialMap);
      } catch (error) {
        message.error("Error fetching material master data.");
        console.error("Material fetch error:", error);
      }
    };

    fetchMaterials();
  }, []);

  const handleMaterialSelect = (index, materialCode) => {
    const materialData = materialDetailsMap[materialCode] || {};
    const lineItems = form.getFieldValue("lineItems") || [];
    const updatedItems = [...lineItems];

    updatedItems[index] = {
      ...updatedItems[index],
      materialCode,
      materialDescription: materialData.description || "",
      materialCategory: materialData.category || "",
      materialSubcategory: materialData.subCategory || "",
      uom: materialData.uom || "",
      unitPrice: materialData.unitPrice || 0, // Auto-fill unit rate if available
    };

    form.setFieldsValue({ lineItems: updatedItems });

    // Category validation
    const categories = updatedItems
      .map((item) => item?.materialCategory)
      .filter(Boolean);

    if (categories.length > 0) {
      const firstCategory = categories[0];
      const allSame = categories.every((cat) => cat === firstCategory);

      if (!allSame) {
        message.error("All materials must be of the same category");
        form.setFields([
          {
            name: ["lineItems", index, "materialCode"],
            errors: ["Category must match first item"],
          },
        ]);
      }
    }
  };

  const handleMaterialDescriptionSelect = (index, materialCode) => {
    handleMaterialSelect(index, materialCode); // Reuse the same handler
  };

  const handleModeOfProcurementChange = (value, index) => {
    const lineItems = form.getFieldValue("lineItems");
    const currentItem = lineItems[index];

    // Clear vendor names when mode changes
    if (currentItem) {
      currentItem.vendorNames = undefined;
      form.setFieldsValue({ lineItems });
    }
  };
  // Calculate total price dynamically
  const handlePriceCalculation = (name) => {
    const values = form.getFieldValue(["lineItems", name]);
    if (values?.quantity && values?.unitRate) {
      const total = values.quantity * values.unitRate;

      // Set validation error if total exceeds 50,000
      if (total > 50000) {
        form.setFields([
          {
            name: ["lineItems", name, "totalPrice"],
            errors: ["Total price cannot exceed ₹50,000"],
          },
        ]);
      } else {
        form.setFields([
          {
            name: ["lineItems", name, "totalPrice"],
            errors: [],
          },
        ]);
      }

      form.setFieldValue(["lineItems", name, "totalPrice"], total);
    }
  };

  return (
    <div className="form-container">
      <h2>Contingency Purchase</h2>

      <div className="form-section" style={{ marginBottom: "20px" }}>
        <Row justify="end">
          <Col>
            <Form form={form} layout="inline" style={{ marginBottom: 16 }}>
              <Form.Item label="Contingency ID">
                <Input
                  placeholder="Enter Contingency ID"
                  value={contingencyId}
                  onChange={(e) => setContingencyId(e.target.value)}
                  style={{ width: "200px", marginRight: "10px" }}
                />
                <Button
                  type="primary"
                  onClick={() => fetchContingencyData(contingencyId)}
                  disabled={!contingencyId}
                >
                  <SearchOutlined />
                </Button>
              </Form.Item>
            </Form>
          </Col>
        </Row>
      </div>

      <Form form={form} layout="vertical" onFinish={submitContingencyData}>
        <Form.Item
          label="Date"
          name="date"
          rules={[{ required: true, message: "Please select date" }]}
        >
          <DatePicker format="DD/MM/YYYY" />
        </Form.Item>

        <Form.List name="lineItems" initialValue={[{}]}>
          {(fields, { add, remove }) => (
            <>
              {fields.map(({ key, name, ...restField }, index) => {
                //   const modeOfProcurement = form.getFieldValue([
                //     "lineItems",
                //     name,
                //     "modeOfProcurement",
                //   ]);
                return (
                  <div
                    key={key}
                    className="line-item"
                    style={{
                      border: "1px solid #ccc",
                      padding: "20px",
                      paddingBottom: "5px",
                      marginBottom: "20px",
                      position: "relative",
                    }}
                  >
                    {showAndRemove && (
                      <DeleteOutlined
                        onClick={() => remove(name)}
                        style={{
                          position: "absolute",
                          top: "10px",
                          right: "10px",
                          fontSize: "18px",
                          cursor: "pointer",
                        }}
                      />
                    )}
                    <Space
                      style={{
                        display: "flex",
                        marginBottom: 20,
                        flexWrap: "wrap",
                      }}
                      align="start"
                    >
                      <Row gutter={16}>
                        <Col span={8}>
                          <Form.Item
                            name={[name, "materialCode"]}
                            label="Material Code"
                            rules={[
                              {
                                required: true,
                                message: "Please select a material code!",
                              },
                              // Add uniqueness validation
                              ({ getFieldValue }) => ({
                                validator(_, value) {
                                  const lineItems =
                                    getFieldValue("lineItems") || [];
                                  const duplicates = lineItems.filter(
                                    (item, idx) =>
                                      idx !== name &&
                                      item.materialCode === value
                                  );
                                  if (duplicates.length > 0) {
                                    return Promise.reject(
                                      "Material code must be unique across items"
                                    );
                                  }
                                  return Promise.resolve();
                                },
                              }),
                            ]}
                          >
                            <Select
                              placeholder="Select Material Code"
                              showSearch
                              optionFilterProp="children"
                              filterOption={(input, option) =>
                                option.children
                                  .toLowerCase()
                                  .includes(input.toLowerCase())
                              }
                              onChange={(value) =>
                                handleMaterialSelect(index, value)
                              }
                            >
                              {materialList.map((code) => (
                                <Option key={code} value={code}>
                                  {code}
                                </Option>
                              ))}
                            </Select>
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
                                  "Please select a material description!",
                              },
                              ({ getFieldValue }) => ({
                                validator(_, value) {
                                  const lineItems =
                                    getFieldValue("lineItems") || [];
                                  const currentMaterial =
                                    materialDetailsMap[value];

                                  // Get descriptions for all items
                                  const descriptions = lineItems.map(
                                    (item) =>
                                      materialDetailsMap[item.materialCode]
                                        ?.description
                                  );

                                  // Check if current description exists in other items
                                  const duplicates = descriptions.filter(
                                    (desc, idx) =>
                                      idx !== name &&
                                      desc === currentMaterial?.description
                                  );

                                  if (duplicates.length > 0) {
                                    return Promise.reject(
                                      "Material description must be unique"
                                    );
                                  }

                                  return Promise.resolve();
                                },
                              }),
                            ]}
                          >
                            <Select
                              placeholder="Select Material Description"
                              showSearch
                              onChange={(value) =>
                                handleMaterialDescriptionSelect(index, value)
                              }
                              optionFilterProp="children"
                              filterOption={(input, option) =>
                                option.children
                                  .toLowerCase()
                                  .includes(input.toLowerCase())
                              }
                            >
                              {Object.values(materialDetailsMap).map(
                                (material) => (
                                  <Option
                                    key={material.materialCode}
                                    value={material.materialCode}
                                  >
                                    {material.description}
                                  </Option>
                                )
                              )}
                            </Select>
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
                              onChange={(e) =>
                                handlePriceCalculation(
                                  index,
                                  "quantity",
                                  e.target.value
                                )
                              }
                            />
                          </Form.Item>
                        </Col>
                        <Col span={8}>
                          <Form.Item
                            {...restField}
                            name={[name, "unitPrice"]}
                            label="Unit Price"
                            rules={[
                              {
                                required: true,
                                message: "Please enter unit price!",
                              },
                            ]}
                          >
                            <Input
                              type="number"
                              placeholder="Enter Unit Price"
                              onChange={(e) =>
                                handlePriceCalculation(
                                  index,
                                  "unitPrice",
                                  e.target.value
                                )
                              }
                            />
                          </Form.Item>
                        </Col>
                        <Col span={8}>
                          <Form.Item
                            {...restField}
                            name={[name, "uom"]}
                            label="UOM"
                            rules={[
                              { required: true, message: "Please select UOM!" },
                            ]}
                          >
                            <Input placeholder="Enter UOM" disabled />
                          </Form.Item>
                        </Col>
                        <Col span={8}>
                          <Form.Item
                            {...restField}
                            name={[name, "budgetCode"]}
                            label="Budget Code"
                            rules={[
                              {
                                required: true,
                                message: "Please select a budget code!",
                              },
                            ]}
                          >
                            <Select placeholder="Select Budget Code">
                              {projects.map((project) => (
                                <Option
                                  key={project.projectCode}
                                  value={project.projectCode}
                                >
                                  {project.budgetType}
                                </Option>
                              ))}
                            </Select>
                          </Form.Item>
                        </Col>
                        <Col span={8}>
                          <Form.Item
                            {...restField}
                            name={[name, "materialCategory"]}
                            label="Material Category"
                            rules={[
                              {
                                required: true,
                                message: "Please enter material category!",
                              },
                            ]}
                          >
                            <Input placeholder="Enter Material Category" />
                          </Form.Item>
                        </Col>
                        <Col span={8}>
                          <Form.Item
                            {...restField}
                            name={[name, "materialSubcategory"]}
                            label="Material Subcategory"
                            rules={[
                              {
                                required: true,
                                message: "Please enter material subcategory!",
                              },
                            ]}
                          >
                            <Input placeholder="Enter Material Subcategory" />
                          </Form.Item>
                        </Col>
                        <Col span={8}>
                          <Form.Item
                            {...restField}
                            name={[name, "modeOfProcurement"]}
                            label="Mode of Procurement"
                            rules={[
                              {
                                required: true,
                                message: "Mode of procurement is required",
                              },
                            ]}
                          >
                            <Select
                              placeholder="Select mode"
                              onChange={(value) =>
                                handleModeOfProcurementChange(value, index)
                              }
                            >
                              <Option value="BRAND PAC">Brand PAC</Option>
                              <Option value="Proprietary/Single Tender">
                                Proprietary/Single Tender
                              </Option>
                              <Option value="Limited Pre Approved Vendor Tender">
                                Limited Pre Approved Vendor Tender
                              </Option>
                              <Option value="Open Tender">Open Tender</Option>
                              <Option value="Global Tender">
                                Global Tender
                              </Option>
                            </Select>
                          </Form.Item>
                          {form.getFieldValue([
                            "lineItems",
                            index,
                            "modeOfProcurement",
                          ]) === "Proprietary/Single Tender" && (
                            <Form.Item
                              {...restField}
                              name={[name, "vendorNames"]}
                              rules={[
                                {
                                  required: true,
                                  message: "Vendor name is required",
                                },
                              ]}
                            >
                              <Select placeholder="Select vendor">
                                {vendorMasterMod?.map((vendor) => (
                                  <Option
                                    key={vendor.value}
                                    value={vendor.value}
                                  >
                                    {vendor.label}
                                  </Option>
                                ))}
                              </Select>
                            </Form.Item>
                          )}

                          {form.getFieldValue([
                            "lineItems",
                            index,
                            "modeOfProcurement",
                          ]) === "BRAND PAC" && (
                            <Form.Item
                              {...restField}
                              name={[name, "vendorNames"]}
                              // rules={[
                              //   {
                              //     required: true,
                              //     message: "Vendor name is required",
                              //   },
                              // ]}
                            >
                              <Input disabled placeholder="Enter vendor name" />
                            </Form.Item>
                          )}

                          {form.getFieldValue([
                            "lineItems",
                            index,
                            "modeOfProcurement",
                          ]) === "Limited Pre Approved Vendor Tender" && (
                            <Form.Item
                              {...restField}
                              name={[name, "vendorNames"]}
                              rules={[
                                {
                                  required: true,
                                  validator: (_, value) => {
                                    if (!value || value.length < 4) {
                                      return Promise.reject(
                                        "Please select at least 4 vendors"
                                      );
                                    }
                                    return Promise.resolve();
                                  },
                                },
                              ]}
                            >
                              <Select
                                mode="multiple"
                                placeholder="Select vendors"
                                maxTagCount={4}
                              >
                                {vendorMasterMod?.map((vendor) => (
                                  <Option
                                    key={vendor.value}
                                    value={vendor.value}
                                  >
                                    {vendor.label}
                                  </Option>
                                ))}
                              </Select>
                            </Form.Item>
                          )}
                        </Col>
                        <Col span={8}>
                          <Form.Item
                            {...restField}
                            name={[name, "totalPrice"]}
                            label="Total Price"
                            shouldUpdate
                          >
                            <Input placeholder="Auto-calculated" />
                          </Form.Item>
                        </Col>
                        <Col span={8}>
                          {/* <Form.Item
                          {...restField}
                          name={[name, "vendorNames"]}
                          label="Vendor Names"
                          dependencies={[
                            ["lineItems", name, "modeOfProcurement"],
                          ]}
                        >
                          {vendorSelect(modeOfProcurement, name)}
                        </Form.Item> */}
                        </Col>
                      </Row>
                      {/* <MinusCircleOutlined onClick={() => remove(name)} /> */}
                    </Space>
                  </div>
                );
              })}
              {showAndRemove && (
                <Form.Item>
                  {/* <Button
                    type="dashed"
                    onClick={() => add()}
                    icon={<PlusOutlined />}
                    style={{ width: "32%" }}
                  >
                    Add Material
                  </Button> */}
                </Form.Item>
              )}
            </>
          )}
        </Form.List>

        <div className="form-section">
          <Form.Item
            label="Vendor Name"
            name="vendorName"
            rules={[{ required: true, message: "Please enter vendor name" }]}
          >
            <Select
              placeholder="Select Vendor"
              loading={vendorLoading}
              showSearch
              optionFilterProp="children"
              filterOption={(input, option) =>
                option.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
              }
            >
              {vendors.map((vendor) => (
                <Option key={vendor.vendorId} value={vendor.vendorName}>
                  {vendor.vendorName}
                </Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            label="Vendor Invoice No."
            name="vendorInvoiceNo"
            rules={[
              { required: true, message: "Please enter vendor invoice number" },
            ]}
          >
            <Input placeholder="Enter Vendor Invoice No." />
          </Form.Item>
        </div>

        <div className="form-section">
          <Form.Item
            label="Remarks for purchase"
            name="remarks"
            rules={[{ required: true, message: "Please enter remarks" }]}
          >
            <Input.TextArea placeholder="Enter remarks for purchase" rows={1} />
          </Form.Item>

          <Form.Item
            label="Amount to be paid"
            name="amountToBePaid"
            rules={[
              { required: true, message: "Please enter amount to be paid" },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  const lineItems = getFieldValue("lineItems") || [];
                  const totalAmount = lineItems.reduce(
                    (sum, item) => sum + (item.totalPrice || 0),
                    0
                  );
                  if (value && parseFloat(value) > totalAmount) {
                    return Promise.reject(
                      "Amount to be paid cannot exceed total amount"
                    );
                  }
                  return Promise.resolve();
                },
              }),
            ]}
          >
            <Input
              placeholder="Enter amount to be paid"
              type="number"
              step="0.01"
            />
          </Form.Item>
        </div>

        <div className="form-section">
          <Form.Item
            name="uploadCopyOfInvoice"
            label="Upload copy of Invoice"
            rules={[{ required: true }]}
            valuePropName="fileList"
            getValueFromEvent={normFile}
          >
            <Upload beforeUpload={() => false} maxCount={1}>
              <Button icon={<UploadOutlined />}>Upload Invoice Copy</Button>
            </Upload>
          </Form.Item>

          <Form.Item
            label="Purchase statement"
            name="predefinedPurchaseStatement"
          >
            <Input.TextArea rows={1} />
          </Form.Item>
        </div>

        <Form.Item
          name="projectName"
          label="Project Name"
          style={{ width: "32%" }}
          //   rules={[{ required: true, message: "Please enter project detail" }]}
        >
          <Select placeholder="Select project" loading={loading} allowClear>
            {projects.map((project) => (
              <Option
                key={project.projectNameDescription}
                value={project.projectNameDescription}
              >
                {project.projectNameDescription}
              </Option>
            ))}
          </Select>
        </Form.Item>

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
          title="CP Created"
          onCancel={() => setShowSuccessModal(false)}
          footer={[
            <Button key="close" onClick={() => setShowSuccessModal(false)}>
              Close
            </Button>,
          ]}
        >
          {generatedContingencyId ? (
            <p>Generated CP ID: {generatedContingencyId}</p>
          ) : (
            <Spin tip="Generating ID..." size="small" />
          )}
        </Modal>
      </Form>
    </div>
  );
};

export default Form7b;
