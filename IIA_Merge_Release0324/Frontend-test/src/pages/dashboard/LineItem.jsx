import React, { useEffect, useState } from "react";
import { Form, Input, Select, Button, Space, Row, Col, message } from "antd";
import { DeleteOutlined, PlusOutlined } from "@ant-design/icons";
import CustomSelect from "../../components/CustomSelect";
import { useSelector } from "react-redux";
import { has } from "lodash";
import axios from "axios";

const { Option } = Select;

const LineItem = ({
  setHasProprietaryItem,
  form,
  materialList,
  projects,
  materialDetailsMap,
  calculateTotalPrice,
  handleMaterialSelect,
  handlePriceCalculation,
  handleMaterialDescriptionSelect,
  showAndRemove = true,
}) => {
  const [materialsList, setMaterialsList] = useState([]);
  const [materialDetailMap, setMaterialDetailMap] = useState({});
  const [materialCategories, setMaterialCategories] = useState([]);
  const [materialSubcategories, setMaterialSubcategories] = useState([]);
  const [uomOptions, setUomOptions] = useState([]);
  const [vendors, setVendors] = useState([]);

  useEffect(() => {
    const fetchVendors = async () => {
      try {
        const { data } = await axios.get("/api/vendor-master");

        if (data.responseStatus.statusCode === 0) {
          // Filter active vendors if needed
          const activeVendors = data.responseData.filter(
            (vendor) => vendor.status === "Active"
          );
          setVendors(activeVendors);
        }
      } catch (error) {
        console.error("Error fetching vendors:", error);
        message.error("Failed to load vendor data");
      }
    };

    fetchVendors();
  }, []);
  const vendorOptions = vendors.map((vendor) => ({
    label: `${vendor.vendorId} - ${vendor.vendorName}`,
    value: vendor.vendorName,
    // Include additional fields if needed:
    vendorId: vendor.vendorId,
    contact: vendor.contactNo,
    email: vendor.emailAddress,
  }));

  const vendorSelect = (mode, name) => {
    switch (mode) {
      case "Proprietary/Single Tender":
        return (
          <Form.Item
            name={[name, "vendorNames"]}
            rules={[
              {
                required: true,
                message: "Please select a vendor",
                validator: (_, value) => {
                  const currentMode = form.getFieldValue([
                    "lineItems",
                    name,
                    "modeOfProcurement",
                  ]);

                  if (currentMode === "Proprietary/Single Tender" && !value) {
                    return Promise.reject("Please select a vendor");
                  }
                  return Promise.resolve();
                },
              },
            ]}
          >
            <CustomSelect
              options={vendorOptions}
              placeholder="Select Vendor"
              showSearch
              optionFilterProp="label"
              filterOption={(input, option) =>
                option.label.toLowerCase().includes(input.toLowerCase()) ||
                option.vendorCode.toLowerCase().includes(input.toLowerCase())
              }
            />
          </Form.Item>
        );

      case "Limited Pre Approved Vendor Tender":
        return (
          <Form.Item
            name={[name, "vendorNames"]}
            rules={[
              {
                required: true,
                message: "Please select at least 4 vendors",
                validator: (_, value) => {
                  const currentMode = form.getFieldValue([
                    "lineItems",
                    name,
                    "modeOfProcurement",
                  ]);

                  if (currentMode === "Limited Pre Approved Vendor Tender") {
                    if (!value || value.length < 4) {
                      return Promise.reject("Minimum 4 vendors required");
                    }
                  }
                  return Promise.resolve();
                },
              },
            ]}
          >
            <Select
              options={vendorOptions}
              mode="multiple"
              placeholder="Select at least 4 vendors"
              showSearch
              optionFilterProp="label"
              filterOption={(input, option) =>
                option.label.toLowerCase().includes(input.toLowerCase()) ||
                option.vendorCode.toLowerCase().includes(input.toLowerCase())
              }
            />
          </Form.Item>
        );

        case "GEM":  // Add this case to return null
      return null;


        case "BRAND PAC":  // Add this case to return null
      return null;

      case "OPEN TENDER":  // Add this case to return null
      return null;

      case "GLOBAL TENDER":  // Add this case to return null
      return null;


      default:
        return (
          <Form.Item name={[name, "vendorNames"]}>
            <Input disabled placeholder="Not applicable" />
          </Form.Item>
        );
    }
  };
  const handleModeOfProcurementChange = (value, index) => {
    if (value === "Proprietary/Single Tender") {
      // setHasProprietaryItem(true);
      ;

      setHasProprietaryItem(true);
    }

    const str = "Proprietary/Single Tender";

    ;
    const lineItems = form.getFieldValue("lineItems");
    const currentItem = lineItems[index];

    // Clear vendor names when mode changes
    if (currentItem) {
      currentItem.vendorNames = undefined;
      form.setFieldsValue({ lineItems });
    }
  };

  ;

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

      setMaterialDetailMap(materialMap);
      setMaterialsList(Object.keys(materialMap));
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

  // Add new state
  const [procurementMode, setProcurementMode] = useState("");
  const { vendorMaster } = useSelector((state) => state.masters);
  const vendorMasterMod = vendorMaster?.map((vendor) => ({
    label: vendor.vendorName,
    value: vendor.vendorName,
  }));
  return (
    <div>
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
                                    idx !== name && item.materialCode === value
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
                              message: "Please select a material description!",
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
                            min={1}
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
                            min = {1}
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
                          label="Currency"
                          name={[name, "currency"]}
                          rules={[
                            { required: true, message: "Currency is required" },
                          ]}
                        >
                          <Input disabled />
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
                          name={[name, index, "modeOfProcurement"]}
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
                            <Option value="GEM">GEM</Option>
                            <Option value="BRAND PAC">Brand PAC</Option>
                            <Option value="Proprietary/Single Tender">
                              Proprietary/Single Tender
                            </Option>
                            <Option value="Limited Pre Approved Vendor Tender">
                              Limited Pre Approved Vendor Tender
                            </Option>
                            <Option value="Open Tender">Open Tender</Option>
                            <Option value="Global Tender">Global Tender</Option>
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
                                <Option key={vendor.value} value={vendor.value}>
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
                                <Option key={vendor.value} value={vendor.value}>
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
                <Button
                  type="dashed"
                  onClick={() => add()}
                  icon={<PlusOutlined />}
                  style={{ width: "32%" }}
                >
                  Add Material
                </Button>
              </Form.Item>
            )}
          </>
        )}
      </Form.List>
    </div>
  );
};

export default LineItem;
