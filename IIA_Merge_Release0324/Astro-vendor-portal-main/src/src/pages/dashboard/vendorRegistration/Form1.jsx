import React, { useState, useEffect } from "react";
import {
  Form,
  Input,
  Select,
  Button,
  Checkbox,
  Row,
  Col,
  message,
} from "antd";
import dayjs from "dayjs";
import customParseFormat from "dayjs/plugin/customParseFormat";
import { motion, AnimatePresence } from "framer-motion";
import { useSelector } from "react-redux";
import { useNavigate } from 'react-router-dom';
import axios from "axios";
import { Country, State, City } from "country-state-city";
dayjs.extend(customParseFormat);

const { Option } = Select;

const Form1 = () => {
  // Added by aman 
  const [showStateField, setShowStateField] = useState(true);
const [showCityField, setShowCityField] = useState(true);
// End
  const auth = useSelector((state) => state.auth);
  const actionPerformer = auth.userId;
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const [successMessage, setSuccessMessage] = useState('');
  const [vendorType, setVendorType] = useState('');
  const [countryList, setCountryList] = useState([]);
  const [stateList, setStateList] = useState([]);
  const [cityList, setCityList] = useState([]);

  const [selectedCountry, setSelectedCountry] = useState('');
  const [selectedState, setSelectedState] = useState('');

  const handleSubmit = async (values) => {
    setLoading(true);
    // If state field is hidden, set state to null
let finalState = values.state;
if (!showStateField) finalState = null;

// If city field is hidden, set city to null
let finalCity = values.city;
if (!showCityField) finalCity = null;
    try {
      const payload = {
        vendorName: values.vendorName,
        vendorType: values.vendorType,
        contactNumber: values.contactNo,
        emailAddress: values.emailAddress,
        registeredPlatform: !!values.registeredPlatform,
        pfmsVendorCode: values.pfmsVendorCode,
        primaryBusiness: values.primaryBusiness,
        address: values.address,
        alternateEmailOrPhoneNumber: values.alternateEmailOrPhoneNumber,
        mobileNumber: values.mobileNo,
        panNumber: values.panNo,
        gstNumber: values.gstNo,
        bankName: values.bankName,
        accountNumber: values.accountNo,
        ifscCode: values.ifscCode,
        purchaseHistory: values.purchaseHistory,
        swiftCode: values.swiftCode,
        bicCode: values.bicCode,
        ibanAbaNumber: values.ibanAbaNumber,
        sortCode: values.sortCode,
        bankRoutingNumber: values.bankRoutingNumber,
        bankAddress: values.bankAddress,
        country: values.country,
      //  Added by aman
        state: finalState,
        place: finalCity,   // note: your backend uses "place" for city
        // state: values.state,
        // place: values.city,
        // End
        createdBy: actionPerformer,
        updatedBy: actionPerformer,
      };

      const response = await axios.post(
        "/api/vendor-master-util/register",
        payload,
        {
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${auth.token}`,
          },
        }
      );

      const data = response.data;
      if (data.responseStatus.statusCode === 0) {
        setSuccessMessage(
          "You have registered successfully. Check your email for the credentials and Sign-in again using those credentials for further interactions!"
        );
        form.resetFields();

        // Reset vendor type and location states
        setVendorType('');
        setSelectedCountry('');
        setSelectedState('');
        setStateList([]);
        setCityList([]);

        // Now Call VendorStatus API
        const vendorId = data.responseData?.vendorId;
        if (vendorId) {
          try {
            const statusResponse = await axios.get(
              `/api/vendor-quotation/VendorStatus/${vendorId}`,
              {
                headers: {
                  "Content-Type": "application/json",
                  Authorization: `Bearer ${auth.token}`,
                },
              }
            );
            const statusData = statusResponse.data;

            if (data.responseStatus.statusCode === 0) {
              const emailStatus = statusData.responseData.emailStatus;
              if (emailStatus) {
                alert("Email for credentials has been sent successfully.");
              } else {
                alert("Email not sent properly.");
              }
            } else {
              console.error("Failed to fetch vendor status:", statusData.responseStatus.message);
            }
          } catch (statusError) {
            console.error("Vendor Status API Error:", statusError);
          }
        } else {
          console.error("Vendor ID not available for status check.");
        }

        setTimeout(() => {
          setSuccessMessage('');
          navigate('/');
        }, 10000);
      } else {
        throw new Error(data.responseStatus?.message || "Registration failed");
      }
    } catch (err) {
      console.error("Registration error:", err);
      message.error(`Failed to register vendor: ${err.message}`);
    } finally {
      setLoading(false);
    }
  };

  // Load all countries on component mount
  useEffect(() => {
    const countries = Country.getAllCountries();
    setCountryList(countries);
  }, []);

  // Auto-select India when Domestic vendor type is selected
  useEffect(() => {
    // Added by aman
    setShowStateField(true);
  setShowCityField(true);
  // end
    if (vendorType === 'Domestic') {
      const indiaCode = 'IN';
      setSelectedCountry(indiaCode);
      form.setFieldsValue({ country: indiaCode });

      // Load states of India
      const states = State.getStatesOfCountry(indiaCode);
      setStateList(states);
      setCityList([]);
      form.setFieldsValue({ state: undefined, city: undefined });
    } else if (vendorType === 'International') {
      // Reset country selection for International vendors
      setSelectedCountry('');
      setStateList([]);
      setCityList([]);
      form.setFieldsValue({ country: undefined, state: undefined, city: undefined });
    }
  }, [vendorType, form]);

  const handleCountryChange = (value) => {
    setSelectedCountry(value);
    const states = State.getStatesOfCountry(value);
    setStateList(states);
    // Added by aman 
    // setCityList([]);
    // form.setFieldsValue({ state: undefined, city: undefined });
    if (states.length === 0) {
    // No states for this country → hide state & city, clear values
    setShowStateField(false);
    setShowCityField(false);
    setSelectedState('');
    setCityList([]);
    form.setFieldsValue({ state: undefined, city: undefined });
  } else {
    setShowStateField(true);
    setShowCityField(true); // reset city visibility until state selected
    setCityList([]);
    form.setFieldsValue({ state: undefined, city: undefined });
  }
  // End
  };

  const handleStateChange = (value) => {
    setSelectedState(value);
    const cities = City.getCitiesOfState(selectedCountry, value);
    setCityList(cities);
    // Added by aman
    // form.setFieldsValue({ city: undefined });
     if (cities.length === 0) {
    setShowCityField(false);
    form.setFieldsValue({ city: undefined });
  } else {
    setShowCityField(true);
    form.setFieldsValue({ city: undefined });
  }
  // End
  };

  // Handle vendor type change
  const handleVendorTypeChange = (value) => {
    setVendorType(value);
    // Added by aman
    setShowStateField(true);
  setShowCityField(true);
  // End
    // Clear fields that are dependent on vendor type
    if (value === 'International') {
      form.setFieldsValue({
        panNo: undefined,
        gstNo: undefined,
        ifscCode: undefined,
      });
    } else if (value === 'Domestic') {
      form.setFieldsValue({
        swiftCode: undefined,
        bicCode: undefined,
        ibanAbaNumber: undefined,
        sortCode: undefined,
        bankRoutingNumber: undefined,
        bankAddress: undefined,
      });
    }
  };

  return (
    <>
      <AnimatePresence>
        {successMessage && (
          <motion.div
            initial={{ y: -100, opacity: 0 }}
            animate={{ y: 0, opacity: 1 }}
            exit={{ y: -100, opacity: 0 }}
            transition={{ duration: 0.5 }}
            style={{
              backgroundColor: "#d4edda",
              color: "#155724",
              padding: "12px 20px",
              textAlign: "center",
              fontWeight: "bold",
              marginBottom: "16px",
              borderRadius: "8px",
              position: "sticky",
              top: "0",
              zIndex: 1000,
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
            }}
          >
            <span style={{ flex: 1 }}>{successMessage}</span>
            <button
              onClick={() => setSuccessMessage('')}
              style={{
                background: "none",
                border: "none",
                fontSize: "20px",
                fontWeight: "bold",
                color: "#155724",
                cursor: "pointer",
              }}
            >
              ×
            </button>
          </motion.div>
        )}
      </AnimatePresence>

      <div className="form-container">
        <h2>Vendor Registration</h2>
        <Row justify="end">
          <Col>
            <Form form={form} layout="inline" style={{ marginBottom: 16 }}>
            </Form>
          </Col>
        </Row>
        <Form
          form={form}
          layout="vertical"
          onFinish={handleSubmit}
          onFinishFailed={(errorInfo) => {
            console.error("Validation Failed:", errorInfo);
            message.error("Please fill all required fields");
          }}
        >
          <div className="form-section">
            <Form.Item
              label="Vendor Name"
              name="vendorName"
              rules={[
                { required: true, message: "Vendor name is required" },
                { min: 3, message: "Vendor name must be at least 3 characters" },
                { max: 100, message: "Vendor name cannot exceed 100 characters" },
                { pattern: /^[a-zA-Z\s.&'-]+$/, message: "Vendor name can only contain letters, spaces, and characters . & ' -" }
              ]}
            >
              <Input />
            </Form.Item>

            <Form.Item
              label="Vendor Type"
              name="vendorType"
              rules={[{ required: true, message: "Vendor Type is required" }]}
            >
              <Select onChange={handleVendorTypeChange}>
                <Option value="Domestic">Domestic</Option>
                <Option value="International">International</Option>
              </Select>
            </Form.Item>

            <Form.Item
              label="Vendor Mobile No."
              name="contactNo"
              rules={[
                { required: true, message: "Vendor mobile number is required" },
                { pattern: /^[6-9]\d{9}$/, message: "Please enter a valid 10-digit Indian mobile number starting with 6-9" }
              ]}
            >
              <Input maxLength={10} />
            </Form.Item>
          </div>

          <div className="form-section">
            <Form.Item
              label="Vendor Email"
              name="emailAddress"
              rules={[
                { required: true, message: "Vendor email is required" },
                { type: 'email', message: "Please enter a valid email address" },
                { pattern: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/, message: "Please enter a valid email format" },
                {
                  validator: async (_, value) => {
                    if (vendorType === 'International' && value && value.endsWith(".com")) {
                      const response = await axios.get(`/api/vendor-master-util/check-email/${value}`);
                      if (response.data.responseData.exists) {
                        return Promise.reject("Email already exists!");
                      }
                    }
                    return Promise.resolve();
                  }
                }
              ]}
            >
              <Input />
            </Form.Item>

            <Form.Item
              name="registeredPlatform"
              label="Registered in GeM/ CPP Portal"
              rules={[
                { required: false, message: "Check is required" },
              ]}
            >
              <Checkbox>Yes</Checkbox>
              <Checkbox>No</Checkbox>
            </Form.Item>

            <Form.Item
              label="PFMS Vendor Code"
              name="pfmsVendorCode"
              rules={[
                { pattern: /^[A-Z0-9]{6,20}$/, message: "PFMS Vendor Code must be 6-20 alphanumeric characters in uppercase" }
              ]}
            >
              <Input />
            </Form.Item>
          </div>

          <div className="form-section">
            <Form.Item
              label="Primary Business"
              name="primaryBusiness"
              rules={[{ required: true, message: "Primary Business is required" }]}
            >
              <Select 
                placeholder="Select Primary Business"
                showSearch
                filterOption={(input, option) =>
                  option.children.toLowerCase().includes(input.toLowerCase())
                }
              >
                <Option value="Chemicals">Chemicals</Option>
                <Option value="Computers & Peripherals">Computers & Peripherals</Option>
                <Option value="Electricals">Electricals</Option>
                <Option value="Electronics">Electronics</Option>
                <Option value="Optics">Optics</Option>
                <Option value="Fabrication">Fabrication</Option>
                <Option value="Furniture">Furniture</Option>
                <Option value="Hardware">Hardware</Option>
                <Option value="Instrument/ Equipment & Machinery">Instrument/ Equipment & Machinery</Option>
                <Option value="Software">Software</Option>
                <Option value="Vehicles">Vehicles</Option>
                <Option value="Stationary">Stationary</Option>
                <Option value="Miscellaneous">Miscellaneous</Option>
                <Option value="Services">Services</Option>
              </Select>
            </Form.Item>

            <Form.Item
              label="Alternate Email/Phone Number"
              name="alternateEmailOrPhoneNumber"
              rules={[
                { required: true, message: "Alternate Email/Phone Number is required" },
                {
                  validator: (_, value) => {
                    if (!value) return Promise.resolve();
                    const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
                    const phonePattern = /^[6-9]\d{9}$/;
                    if (emailPattern.test(value) || phonePattern.test(value)) {
                      return Promise.resolve();
                    }
                    return Promise.reject("Please enter a valid email address or 10-digit phone number");
                  }
                }
              ]}
            >
              <Input />
            </Form.Item>

            <Form.Item
              label="PAN Number"
              name="panNo"
              normalize={(value) => (value ? value.toUpperCase() : value)} 
              rules={[
                { required: vendorType === 'Domestic', message: "PAN Number is required" },
                { min: 10, max: 10, message: "PAN Number must be 10 characters" },
                { pattern: /^[A-Z]{5}[0-9]{4}[A-Z]{1}$/, message: "PAN Number must be in format: ABCDE1234F (5 uppercase letters, 4 digits, 1 uppercase letter)" },
                {
                  validator: async (_, value) => {
                    if (vendorType === 'Domestic') {
                      if (!value || value.length < 10) {
                        return Promise.resolve();
                      }

                      if (value.length === 10) {
                        const response = await axios.get(`/api/vendor-master-util/check-panNumber/${value}`);
                        if (response.data.responseData.exists) {
                          return Promise.reject("PAN number already exists");
                        }
                      }
                    }
                    return Promise.resolve();
                  }
                }
              ]}
            >
              <Input disabled={vendorType === 'International'} maxLength={10} style={{ textTransform: 'uppercase' }} />
            </Form.Item>
          </div>

          <div className="form-section">
            <Form.Item
              label="GST Number"
              name="gstNo"
              normalize={(value) => (value ? value.toUpperCase() : value)} 
              rules={[
                { required: vendorType === 'Domestic', message: "GST Number is required" },
                { pattern: /^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$/, message: "GST Number must be in valid format (e.g., 22AAAAA0000A1Z5)" }
              ]}
            >
              <Input disabled={vendorType === 'International'} maxLength={15} style={{ textTransform: 'uppercase' }} />
            </Form.Item>

            <Form.Item
              label="Bank Name"
              name="bankName"
              rules={[
                { required: true, message: "Bank Name is required" },
                { min: 3, message: "Bank name must be at least 3 characters" },
                { max: 100, message: "Bank name cannot exceed 100 characters" },
                { pattern: /^[a-zA-Z\s&'-]+$/, message: "Bank name can only contain letters, spaces, and characters & ' -" }
              ]}
            >
              <Input />
            </Form.Item>

            <Form.Item
              label="Account Number"
              name="accountNo"
              rules={[
                { required: true, message: "Account Number is required" },
                { pattern: /^[0-9]{9,18}$/, message: "Account Number must be 9-18 digits" }
              ]}
            >
              <Input maxLength={18} />
            </Form.Item>
          </div>

          <div className="form-section">
            {vendorType === 'International' && (
              <>
                <Form.Item
                  label="SWIFT Code"
                  name="swiftCode"
                  normalize={(value) => (value ? value.toUpperCase() : value)}
                  rules={[
                    { required: true, message: "SWIFT Code is required" },
                    { pattern: /^[A-Z]{6}[A-Z0-9]{2}([A-Z0-9]{3})?$/, message: "SWIFT Code must be 8 or 11 characters (e.g., AAAABBCC or AAAABBCCXXX)" }
                  ]}
                >
                  <Input maxLength={11} style={{ textTransform: 'uppercase' }} />
                </Form.Item>

                <Form.Item
                  label="BIC Code"
                  name="bicCode"
                  normalize={(value) => (value ? value.toUpperCase() : value)}
                  rules={[
                    { required: true, message: "BIC Code is required" },
                    { pattern: /^[A-Z]{6}[A-Z0-9]{2}([A-Z0-9]{3})?$/, message: "BIC Code must be 8 or 11 characters (e.g., AAAABBCC or AAAABBCCXXX)" }
                  ]}
                >
                  <Input maxLength={11} style={{ textTransform: 'uppercase' }} />
                </Form.Item>

                <Form.Item
                  label="IBAN/ABA Number"
                  name="ibanAbaNumber"
                  normalize={(value) => (value ? value.toUpperCase() : value)}
                  rules={[
                    { required: true, message: "IBAN/ABA Number is required" },
                    {
                      validator: (_, value) => {
                        if (!value) return Promise.resolve();
                        const ibanPattern = /^[A-Z]{2}[0-9]{2}[A-Z0-9]{1,30}$/;
                        const abaPattern = /^[0-9]{9}$/;
                        if (ibanPattern.test(value) || abaPattern.test(value)) {
                          return Promise.resolve();
                        }
                        return Promise.reject("Please enter a valid IBAN (e.g., GB82WEST12345698765432) or ABA number (9 digits)");
                      }
                    }
                  ]}
                >
                  <Input maxLength={34} style={{ textTransform: 'uppercase' }} />
                </Form.Item>
              </>
            )}
          </div>

          <div className="form-section">
            {vendorType === 'International' && (
              <>
                <Form.Item
                  label="Sort Code"
                  name="sortCode"
                  rules={[
                    { required: true, message: "Sort Code is required" },
                    { pattern: /^[0-9]{6}$/, message: "Sort Code must be 6 digits" }
                  ]}
                >
                  <Input maxLength={6} />
                </Form.Item>

                <Form.Item
                  label="Bank Routing Number"
                  name="bankRoutingNumber"
                  rules={[
                    { required: true, message: "Bank Routing Number is required" },
                    { pattern: /^[0-9]{9}$/, message: "Bank Routing Number must be 9 digits" }
                  ]}
                >
                  <Input maxLength={9} />
                </Form.Item>

                <Form.Item
                  label="Bank Address"
                  name="bankAddress"
                  rules={[
                    { required: true, message: "Bank Address is required" },
                    { min: 10, message: "Bank address must be at least 10 characters" },
                    { max: 200, message: "Bank address cannot exceed 200 characters" }
                  ]}
                >
                  <Input />
                </Form.Item>
              </>
            )}
          </div>

          <div className="form-section">
            <Form.Item
              label="IFSC Code"
              name="ifscCode"
              normalize={(value) => (value ? value.toUpperCase() : value)} 
              rules={[
                { required: vendorType === 'Domestic', message: "IFSC Code is required" },
                { pattern: /^[A-Z]{4}0[A-Z0-9]{6}$/, message: "IFSC Code must be in format: ABCD0123456 (4 uppercase letters, 0, then 6 alphanumeric)" }
              ]}
            >
              <Input disabled={vendorType === 'International'} maxLength={11} style={{ textTransform: 'uppercase' }} />
            </Form.Item>
          </div>

          <div className="form-section">
            <Form.Item
              label="Address"
              name="address"
              rules={[
                { required: true, message: "Full Address is required" },
                { min: 10, message: "Address must be at least 10 characters" },
                { max: 250, message: "Address cannot exceed 250 characters" }
              ]}
            >
              <Input />
            </Form.Item>

            <Form.Item 
              label="Country" 
              name="country" 
              rules={[{ required: true, message: 'Country is required' }]}
            >
              <Select
                placeholder="Select Country"
                onChange={handleCountryChange}
                showSearch
                filterOption={(input, option) =>
                  option.children.toLowerCase().includes(input.toLowerCase())
                }
                disabled={vendorType === 'Domestic'}
              >
                {countryList.map((country) => (
                  <Option key={country.isoCode} value={country.isoCode}>
                    {country.name}
                  </Option>
                ))}
              </Select>
            </Form.Item>

{/* Added by aman */}
{showStateField && (
            <Form.Item 
              label="State" 
              name="state" 
              rules={[{ required: true, message: 'State is required' }]}
            >
              <Select
                placeholder="Select State"
                onChange={handleStateChange}
                showSearch
                filterOption={(input, option) =>
                  option.children.toLowerCase().includes(input.toLowerCase())
                }
              >
                {stateList.map((state) => (
                  <Option key={state.isoCode} value={state.isoCode}>
                    {state.name}
                  </Option>
                ))}
              </Select>
            </Form.Item>)}
          
                {showCityField && showStateField && (
            <Form.Item 
              label="City" 
              name="city" 
              rules={[{ required: true, message: 'City is required' }]}
            >
              <Select
                placeholder="Select City"
                showSearch
                filterOption={(input, option) =>
                  option.children.toLowerCase().includes(input.toLowerCase())
                }
              >
                {cityList.map((city, index) => (
                  <Option key={index} value={city.name}>
                    {city.name}
                  </Option>
                ))}
              </Select>
            </Form.Item>)}
          </div>

          <Form.Item>
            <div style={{ display: "flex", justifyContent: "space-between" }}>
              <Button type="default" htmlType="reset">
                Reset
              </Button>
              <Button type="primary" htmlType="submit" loading={loading}>
                Submit
              </Button>
            </div>
          </Form.Item>
        </Form>
      </div>
    </>
  );
};

export default Form1;