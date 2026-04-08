import React, { useState, useEffect } from 'react';
import { Card, Form, Input, Button, Select, DatePicker, message, Row, Col, Divider, Modal, Descriptions, Tag, Tabs, Spin } from 'antd';
import { UserOutlined, CheckCircleOutlined, SearchOutlined, PlusOutlined, EditOutlined } from '@ant-design/icons';
import axios from 'axios';
import dayjs from 'dayjs';
import { useLOVValues } from '../../../hooks/useLOVValues';
import AdvancedEmployeeSearch from '../../../components/AdvancedEmployeeSearch'; // TC_15

const { Option } = Select;

const EmployeeRegistration = () => {
  const [form] = Form.useForm();
  const [editForm] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [successModalVisible, setSuccessModalVisible] = useState(false);
  const [createdEmployee, setCreatedEmployee] = useState(null);

  // Edit mode states
  const [editModalVisible, setEditModalVisible] = useState(false);
  const [editingEmployee, setEditingEmployee] = useState(null);
  const [editLoading, setEditLoading] = useState(false);

  // New LOV states for Reporting Officers, States, and Cities
  const [reportingOfficers, setReportingOfficers] = useState([]);
  const [loadingReportingOfficers, setLoadingReportingOfficers] = useState(false);
  const [states, setStates] = useState([]);
  const [loadingStates, setLoadingStates] = useState(false);
  const [cities, setCities] = useState([]);
  const [loadingCities, setLoadingCities] = useState(false);
  const [selectedState, setSelectedState] = useState('');

  // Edit form cities (separate from create form)
  const [editCities, setEditCities] = useState([]);
  const [loadingEditCities, setLoadingEditCities] = useState(false);
  const [editSelectedState, setEditSelectedState] = useState('');

  // ✅ Fetch dropdown values from LOV system (Form ID: 4 - EmployeeRegistration)
  // Note: Designator names must be lowercase to match backend API
  const { lovValues: departmentLOV, loading: loadingDepartments } = useLOVValues(4, 'department');
  const { lovValues: designationLOV, loading: loadingDesignations } = useLOVValues(4, 'designation');
  const { lovValues: locationLOV, loading: loadingLocations } = useLOVValues(4, 'location');
  // Added by Aman
  const { lovValues: Employment_typeLOV, loading: loadingEmployemnet_type } = useLOVValues(4,'Employment_type');
  // End

  // Fetch Reporting Officers on mount
  useEffect(() => {
    fetchReportingOfficers();
    fetchStates();
  }, []);

  // Fetch Reporting Officers LOV
  const fetchReportingOfficers = async () => {
    try {
      setLoadingReportingOfficers(true);
      const response = await axios.get('/api/lov/employee/reporting-officers');
      if (response.data.status === 'success' && response.data.data) {
        setReportingOfficers(response.data.data);
      } else if (response.data.responseData) {
        setReportingOfficers(response.data.responseData);
      }
    } catch (error) {
      console.error('Failed to fetch reporting officers:', error);
      message.error('Failed to load reporting officers');
    } finally {
      setLoadingReportingOfficers(false);
    }
  };

  // Fetch States LOV
  const fetchStates = async () => {
    try {
      setLoadingStates(true);
      const response = await axios.get('/api/lov/employee/states');
      if (response.data.status === 'success' && response.data.data) {
        setStates(response.data.data);
      } else if (response.data.responseData) {
        setStates(response.data.responseData);
      }
    } catch (error) {
      console.error('Failed to fetch states:', error);
      message.error('Failed to load states');
    } finally {
      setLoadingStates(false);
    }
  };

  // Fetch Cities LOV based on selected State (for create form)
  const fetchCities = async (stateName) => {
    if (!stateName) {
      setCities([]);
      return;
    }
    try {
      setLoadingCities(true);
      const response = await axios.get(`/api/lov/employee/cities?state=${encodeURIComponent(stateName)}`);
      if (response.data.status === 'success' && response.data.data) {
        setCities(response.data.data);
      } else if (response.data.responseData) {
        setCities(response.data.responseData);
      }
    } catch (error) {
      console.error('Failed to fetch cities:', error);
      setCities([]);
    } finally {
      setLoadingCities(false);
    }
  };

  // Fetch Cities LOV for edit form
  const fetchEditCities = async (stateName) => {
    if (!stateName) {
      setEditCities([]);
      return;
    }
    try {
      setLoadingEditCities(true);
      const response = await axios.get(`/api/lov/employee/cities?state=${encodeURIComponent(stateName)}`);
      if (response.data.status === 'success' && response.data.data) {
        setEditCities(response.data.data);
      } else if (response.data.responseData) {
        setEditCities(response.data.responseData);
      }
    } catch (error) {
      console.error('Failed to fetch cities for edit:', error);
      setEditCities([]);
    } finally {
      setLoadingEditCities(false);
    }
  };

  // Handle State change for cascading City dropdown (create form)
  const handleStateChange = (value) => {
    setSelectedState(value);
    form.setFieldValue('city', undefined); // Reset city when state changes
    fetchCities(value);
  };

  // Handle State change for edit form
  const handleEditStateChange = (value) => {
    setEditSelectedState(value);
    editForm.setFieldValue('city', undefined); // Reset city when state changes
    fetchEditCities(value);
  };

  // Handle Edit Employee
  const handleEditEmployee = async (employeeId) => {
    try {
      setEditLoading(true);
      const response = await axios.get(`/api/employee-department-master/${employeeId}`);

      if (response.data.responseStatus?.statusCode === 0 || response.data.responseData) {
        const employeeData = response.data.responseData;
        setEditingEmployee(employeeData);

        // Fetch cities for the employee's state
        if (employeeData.state) {
          setEditSelectedState(employeeData.state);
          await fetchEditCities(employeeData.state);
        }

        // Open modal first so the form is mounted, then set values
        setEditModalVisible(true);

        // Defer setFieldsValue so the form DOM is fully mounted after modal opens
        setTimeout(() => {
          editForm.setFieldsValue({
            employeeId: employeeData.employeeId,
            firstName: employeeData.firstName,
            lastName: employeeData.lastName,
            emailAddress: employeeData.emailAddress,
            phoneNumber: employeeData.phoneNumber,
            dateOfBirth: employeeData.dateOfBirth ? dayjs(employeeData.dateOfBirth) : null,
            jobTitle: employeeData.jobTitle,
            designation: employeeData.designation,
            departmentName: employeeData.departmentName,
            reportingOfficerId: employeeData.reportingOfficerId,
            employmentType: employeeData.employmentType,
            hireDate: employeeData.hireDate ? dayjs(employeeData.hireDate) : null,
            endDate: employeeData.endDate ? dayjs(employeeData.endDate) : null,
            streetAddress: employeeData.streetAddress,
            city: employeeData.city,
            state: employeeData.state,
            pinCode: employeeData.pinCode,
            location: employeeData.location,
            status: employeeData.status || 'Active'
          });
        }, 0);
      } else {
        message.error('Failed to fetch employee details');
      }
    } catch (error) {
      console.error('Error fetching employee:', error);
      message.error('Error fetching employee details');
    } finally {
      setEditLoading(false);
    }
  };

  // Handle Edit Form Submit
  const handleEditSubmit = async (values) => {
    try {
      setEditLoading(true);

      // Find selected reporting officer name
      const selectedOfficer = reportingOfficers.find(ro => ro.employeeId === values.reportingOfficerId);

      const payload = {
        employeeName: `${values.firstName} ${values.lastName}`,
        firstName: values.firstName,
        lastName: values.lastName,
        departmentName: values.departmentName,
        location: values.location || values.city,
        designation: values.designation,
        phoneNumber: values.phoneNumber,
        emailAddress: values.emailAddress,
        address: `${values.streetAddress || ''}, ${values.city || ''}, ${values.state || ''} ${values.pinCode || ''}`.trim(),
        streetAddress: values.streetAddress,
        city: values.city,
        state: values.state,
        pinCode: values.pinCode,
        reportingOfficerId: values.reportingOfficerId,
        reportingOfficerName: selectedOfficer?.employeeName || '',
        status: values.status || 'Active',
        createdBy: editingEmployee?.createdBy || null,
        updatedBy: 'admin',
        // Added by Aman 
        employeeId: values.employeeId,
        // End
      };

      await axios.put(`/api/employee-department-master/${editingEmployee.employeeId}`, payload);

      message.success('Employee updated successfully');
      setEditModalVisible(false);
      setEditingEmployee(null);
      editForm.resetFields();
      setEditSelectedState('');
      setEditCities([]);
    } catch (error) {
      const errorMessage = error.response?.data?.responseStatus?.message
        || error.response?.data?.message
        || 'Failed to update employee';
      message.error(errorMessage);
    } finally {
      setEditLoading(false);
    }
  };

  const handleSubmit = async (values) => {
    try {
      setLoading(true);

      // Find selected reporting officer name
      const selectedOfficer = reportingOfficers.find(ro => ro.employeeId === values.reportingOfficerId);

      const payload = {
        // ❌ DO NOT send employeeId - it's auto-generated by backend
        // Added by Aman 
        employeeId: values.employeeId,
        // End
        firstName: values.firstName,
        lastName: values.lastName,
        emailAddress: values.emailAddress,
        phoneNumber: values.phoneNumber,
        dateOfBirth: values.dateOfBirth ? values.dateOfBirth.format('YYYY-MM-DD') : null,
        jobTitle: values.jobTitle,
        designation: values.designation,
        departmentName: values.departmentName,
        // Updated: Manager → Reporting Officer
        reportingOfficerId: values.reportingOfficerId,
        reportingOfficerName: selectedOfficer?.employeeName || '',
        employmentType: values.employmentType,
        hireDate: values.hireDate ? values.hireDate.format('YYYY-MM-DD') : null,
        endDate: values.endDate ? values.endDate.format('YYYY-MM-DD') : null,
        streetAddress: values.streetAddress,
        city: values.city,
        state: values.state,
        // Updated: zipCode → pinCode
        pinCode: values.pinCode,
        location: values.location || values.city,
        // For backward compatibility with existing backend
        employeeName: `${values.firstName} ${values.lastName}`,
        address: `${values.streetAddress || ''}, ${values.city || ''}, ${values.state || ''} ${values.pinCode || ''}`.trim(),
        status: 'Active',
        createdBy: 'admin', // Replace with actual user from auth state
        
      };

      const response = await axios.post('/api/employee-department-master', payload);
      const employeeData = response.data?.responseData;

      if (employeeData) {
        // ✅ Show success modal with employee details
        setCreatedEmployee(employeeData);
        setSuccessModalVisible(true);
        form.resetFields();
        setSelectedState('');
        setCities([]);
      }
    } catch (error) {
      // Added by Aman
       // ✅ Fix: was error.response?.d.message (typo), and missing responseStatus path
      message.error(
        error.response?.data?.responseStatus?.message ||
        'Failed to register employee'
      );
      // End
    } finally {
      setLoading(false);
    }
  };

  const handleClear = () => {
    form.resetFields();
  };

  const handleCloseModal = () => {
    setSuccessModalVisible(false);
    setCreatedEmployee(null);
  };

  return (
    <div style={{ padding: '24px' }}>
      <Card
        title={
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <UserOutlined style={{ fontSize: '20px', color: '#1890ff' }} />
            <span>Employee Management</span>
          </div>
        }
        bordered={false}
      >
        {/* TC_15: Add tabs for Search and Register */}
        <Tabs
          defaultActiveKey="search"
          items={[
            {
              key: 'search',
              label: (
                <span>
                  <SearchOutlined />
                  Search Employees
                </span>
              ),
              children: <AdvancedEmployeeSearch onEditEmployee={handleEditEmployee} />
            },
            {
              key: 'register',
              label: (
                <span>
                  <PlusOutlined />
                  Register New Employee
                </span>
              ),
              children: (
                <Form form={form} layout="vertical" onFinish={handleSubmit}>
          {/* Personal Information Section */}
          <div style={{ marginBottom: '24px' }}>
            <h3 style={{ fontSize: '16px', fontWeight: 600, marginBottom: '16px', color: '#1890ff' }}>
              Personal Information
            </h3>
            <Row gutter={16}>
              <Col xs={24} sm={12} lg={8}>
                <Form.Item
                  label="Employee ID"
                  name="employeeId"
                  // Modified By Aman
                  // ❌ NO required validation - auto-generated by backend
                  // help="Auto-generated by system"
                  rules={[{ required: true, message: 'Please enter Employee ID' }]}
                >
                  <Input
                    placeholder="Enter Employee ID"
                    // disabled={true}  // ✅ Always disabled - cannot be edited
                  />
                  {/* End */}
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} lg={8}>
                <Form.Item
                  label="First Name"
                  name="firstName"
                  rules={[{ required: true, message: 'Please enter first name' }]}
                >
                  <Input placeholder="Enter first name" />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} lg={8}>
                <Form.Item
                  label="Last Name"
                  name="lastName"
                  rules={[{ required: true, message: 'Please enter last name' }]}
                >
                  <Input placeholder="Enter last name" />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} lg={8}>
                <Form.Item
                  label="Email"
                  name="emailAddress"
                  rules={[
                    { required: true, message: 'Please enter email' },
                    { type: 'email', message: 'Please enter valid email' }
                  ]}
                >
                  <Input placeholder="Enter email address" />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} lg={8}>
                <Form.Item
                // Modified by Aman 
                  label="Phone Number/Landline Number"
                  name="phoneNumber"
                  rules={[
                    { required: true, message: 'Please enter phone number' },
                    // { pattern: /^\d{10}$/, message: 'Please enter 10-digit phone number' }
                    // End
                  ]}
                >
                  <Input placeholder="Enter phone number" maxLength={10} />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} lg={8}>
                <Form.Item
                  label="Date of Birth"
                  name="dateOfBirth"
                  rules={[{ required: true, message: 'Please select date of birth' }]}
                >
                  <DatePicker
                    style={{ width: '100%' }}
                    format="DD-MM-YYYY"
                    disabledDate={(current) => current && current > dayjs().endOf('day')}
                  />
                </Form.Item>
              </Col>
            </Row>
          </div>

          <Divider />

          {/* Employment Information Section */}
          <div style={{ marginBottom: '24px' }}>
            <h3 style={{ fontSize: '16px', fontWeight: 600, marginBottom: '16px', color: '#1890ff' }}>
              Employment Information
            </h3>
            <Row gutter={16}>
              <Col xs={24} sm={12} lg={8}>
                <Form.Item
                  label="Department"
                  name="departmentName"
                  rules={[{ required: true, message: 'Please select department' }]}
                  tooltip="Department values are managed from LOV Management"
                >
                  <Select placeholder="Select department" loading={loadingDepartments} showSearch>
                    {departmentLOV.map((dept) => (
                      <Option key={dept.lovId} value={dept.lovValue}>
                        {dept.lovDisplayValue}
                      </Option>
                    ))}
                  </Select>
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} lg={8}>
                <Form.Item
                  label="Designation"
                  name="designation"
                  rules={[{ required: true, message: 'Please select designation' }]}
                  tooltip="Designation values are managed from LOV Management"
                >
                  <Select placeholder="Select designation" loading={loadingDesignations} showSearch>
                    {designationLOV.map((desig) => (
                      <Option key={desig.lovId} value={desig.lovValue}>
                        {desig.lovDisplayValue}
                      </Option>
                    ))}
                  </Select>
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} lg={8}>
                <Form.Item
                  label="Job Title"
                  name="jobTitle"
                >
                  <Input placeholder="Enter job title" />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} lg={8}>
              {/* Modified By aman */}
                 <Form.Item label="Employment Type" name="Employment_Type">
                  <Select placeholder="Enter Employment Type" loading={loadingEmployemnet_type} showSearch>
                    {Employment_typeLOV.map((emt) => (
                      <Option key={emt.lovId} value={emt.lovValue}>
                        {emt.lovDisplayValue}
                      </Option>
                    ))}
                  </Select>
                </Form.Item>
                {/* End */}
              </Col>
              <Col xs={24} sm={12} lg={8}>
                <Form.Item
                  label="Reporting Officer"
                  name="reportingOfficerId"
                  rules={[{ required: true, message: 'Please select reporting officer' }]}
                  tooltip="Select the employee's reporting officer"
                >
                  <Select
                    placeholder="Select reporting officer"
                    loading={loadingReportingOfficers}
                    showSearch
                    filterOption={(input, option) =>
                      (option?.children ?? '').toLowerCase().includes(input.toLowerCase())
                    }
                  >
                    {reportingOfficers.map((officer) => (
                      <Option key={officer.employeeId} value={officer.employeeId}>
                        {officer.displayValue || `${officer.employeeId} - ${officer.employeeName}`}
                      </Option>
                    ))}
                  </Select>
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} lg={8}>
                <Form.Item
                  label="Hire Date"
                  name="hireDate"
                  rules={[{ required: true, message: 'Please select hire date' }]}
                >
                  <DatePicker style={{ width: '100%' }} format="DD-MM-YYYY" />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} lg={8}>
                <Form.Item label="End Date (Optional)" name="endDate">
                  <DatePicker style={{ width: '100%' }} format="DD-MM-YYYY" />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} lg={8}>
                <Form.Item label="Annual Salary" name="annualSalary">
                  <Input type="number" placeholder="Enter annual salary" min={0} />
                </Form.Item>
              </Col>
            </Row>
          </div>

          <Divider />

          {/* Address Information Section */}
          <div style={{ marginBottom: '24px' }}>
            <h3 style={{ fontSize: '16px', fontWeight: 600, marginBottom: '16px', color: '#1890ff' }}>
              Address Information
            </h3>
            <Row gutter={16}>
              <Col xs={24}>
                <Form.Item label="Street Address" name="streetAddress">
                  <Input placeholder="Enter street address" />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} lg={8}>
                <Form.Item
                  label="State"
                  name="state"
                  rules={[{ required: true, message: 'Please select state' }]}
                >
                  <Select
                    placeholder="Select state"
                    loading={loadingStates}
                    showSearch
                    filterOption={(input, option) =>
                      (option?.children ?? '').toLowerCase().includes(input.toLowerCase())
                    }
                    onChange={handleStateChange}
                  >
                    {states.map((state) => (
                      <Option key={state.value} value={state.value}>
                        {state.displayValue || state.value}
                      </Option>
                    ))}
                  </Select>
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} lg={8}>
                <Form.Item
                  label="City"
                  name="city"
                  rules={[{ required: true, message: 'Please select city' }]}
                >
                  <Select
                    placeholder={selectedState ? "Select city" : "Select state first"}
                    loading={loadingCities}
                    showSearch
                    disabled={!selectedState}
                    filterOption={(input, option) =>
                      (option?.children ?? '').toLowerCase().includes(input.toLowerCase())
                    }
                    notFoundContent={loadingCities ? <Spin size="small" /> : "No cities found"}
                  >
                    {cities.map((city) => (
                      <Option key={city.value} value={city.value}>
                        {city.displayValue || city.value}
                      </Option>
                    ))}
                  </Select>
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} lg={8}>
                <Form.Item
                  label="Pin Code"
                  name="pinCode"
                  rules={[
                    { pattern: /^\d{6}$/, message: 'Please enter valid 6-digit pin code' }
                  ]}
                >
                  <Input placeholder="Enter pin code" maxLength={6} />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} lg={8}>
                <Form.Item label="Location" name="location">
                  <Select placeholder="Select office location" loading={loadingLocations} showSearch>
                    {locationLOV.map((loc) => (
                      <Option key={loc.lovId} value={loc.lovValue}>
                        {loc.lovDisplayValue}
                      </Option>
                    ))}
                  </Select>
                </Form.Item>
              </Col>
            </Row>
          </div>

          {/* Action Buttons */}
          <Form.Item style={{ marginTop: '32px', marginBottom: 0 }}>
            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px' }}>
              <Button size="large" onClick={handleClear}>
                Clear Form
              </Button>
              <Button type="primary" size="large" htmlType="submit" loading={loading}>
                Register Employee
              </Button>
            </div>
          </Form.Item>
        </Form>
              )
            }
          ]}
        />
      </Card>

      {/* Success Modal */}
      <Modal
        title={
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <CheckCircleOutlined style={{ fontSize: '24px', color: '#52c41a' }} />
            <span>Employee Created Successfully!</span>
          </div>
        }
        open={successModalVisible}
        onCancel={handleCloseModal}
        footer={[
          <Button key="close" type="primary" onClick={handleCloseModal}>
            Close
          </Button>
        ]}
        width={700}
      >
        {createdEmployee && (
          <>
            {/* Employee ID Highlighted */}
            <div style={{
              backgroundColor: '#e6f7ff',
              padding: '16px',
              borderRadius: '8px',
              border: '2px solid #1890ff',
              marginBottom: '20px'
            }}>
              <div style={{ fontSize: '12px', color: '#1890ff', marginBottom: '4px' }}>
                Employee ID (Auto-Generated)
              </div>
              <div style={{ fontSize: '24px', fontWeight: 'bold', color: '#1890ff' }}>
                {createdEmployee.employeeId}
              </div>
            </div>

            <Divider />

            {/* Personal Information */}
            <h4 style={{ marginBottom: '12px', color: '#1890ff' }}>Personal Information</h4>
            <Descriptions bordered column={2} size="small">
              <Descriptions.Item label="Full Name">{createdEmployee.employeeName}</Descriptions.Item>
              <Descriptions.Item label="Email Address">{createdEmployee.emailAddress}</Descriptions.Item>
              <Descriptions.Item label="Phone Number">{createdEmployee.phoneNumber}</Descriptions.Item>
              <Descriptions.Item label="Status">
                <Tag color={createdEmployee.status === 'Active' ? 'green' : 'red'}>
                  {createdEmployee.status}
                </Tag>
              </Descriptions.Item>
            </Descriptions>

            <Divider />

            {/* Employment Information */}
            <h4 style={{ marginBottom: '12px', color: '#1890ff' }}>Employment Information</h4>
            <Descriptions bordered column={2} size="small">
              <Descriptions.Item label="Department">{createdEmployee.departmentName}</Descriptions.Item>
              <Descriptions.Item label="Designation">{createdEmployee.designation}</Descriptions.Item>
              <Descriptions.Item label="Location">{createdEmployee.location}</Descriptions.Item>
              <Descriptions.Item label="Address" span={2}>
                {createdEmployee.address}
              </Descriptions.Item>
            </Descriptions>

            <Divider />

            {/* System Information */}
            <h4 style={{ marginBottom: '12px', color: '#1890ff' }}>System Information</h4>
            <Descriptions bordered column={2} size="small">
              <Descriptions.Item label="Created By">{createdEmployee.createdBy}</Descriptions.Item>
              <Descriptions.Item label="Created Date">
                {dayjs(createdEmployee.createdDate).format('DD-MM-YYYY HH:mm:ss')}
              </Descriptions.Item>
            </Descriptions>
          </>
        )}
      </Modal>

      {/* Edit Employee Modal */}
      <Modal
        title={
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <EditOutlined style={{ fontSize: '20px', color: '#1890ff' }} />
            <span>Edit Employee - {editingEmployee?.employeeId}</span>
          </div>
        }
        open={editModalVisible}
        onCancel={() => {
          setEditModalVisible(false);
          setEditingEmployee(null);
          editForm.resetFields();
          setEditSelectedState('');
          setEditCities([]);
        }}
        footer={null}
        width={900}
        destroyOnClose
      >
        <Form
          form={editForm}
          layout="vertical"
          onFinish={handleEditSubmit}
          style={{ marginTop: '16px' }}
        >
          {/* Personal Information Section */}
          <div style={{ marginBottom: '24px' }}>
            <h3 style={{ fontSize: '16px', fontWeight: 600, marginBottom: '16px', color: '#1890ff' }}>
              Personal Information
            </h3>
            <Row gutter={16}>
              <Col xs={24} sm={12} lg={8}>
                <Form.Item label="Employee ID" name="employeeId">
                  <Input disabled />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} lg={8}>
                <Form.Item
                  label="First Name"
                  name="firstName"
                  rules={[{ required: true, message: 'Please enter first name' }]}
                >
                  <Input placeholder="Enter first name" />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} lg={8}>
                <Form.Item
                  label="Last Name"
                  name="lastName"
                  rules={[{ required: true, message: 'Please enter last name' }]}
                >
                  <Input placeholder="Enter last name" />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} lg={8}>
                <Form.Item
                  label="Email"
                  name="emailAddress"
                  rules={[
                    { required: true, message: 'Please enter email' },
                    { type: 'email', message: 'Please enter valid email' }
                  ]}
                >
                  <Input placeholder="Enter email address" />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} lg={8}>
                <Form.Item
                  label="Phone Number"
                  name="phoneNumber"
                  rules={[
                    { required: true, message: 'Please enter phone number' },
                    { pattern: /^\d{10}$/, message: 'Please enter 10-digit phone number' }
                  ]}
                >
                  <Input placeholder="Enter phone number" maxLength={10} />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} lg={8}>
                <Form.Item label="Date of Birth" name="dateOfBirth">
                  <DatePicker
                    style={{ width: '100%' }}
                    format="DD-MM-YYYY"
                    disabledDate={(current) => current && current > dayjs().endOf('day')}
                  />
                </Form.Item>
              </Col>
            </Row>
          </div>

          <Divider />

          {/* Employment Information Section */}
          <div style={{ marginBottom: '24px' }}>
            <h3 style={{ fontSize: '16px', fontWeight: 600, marginBottom: '16px', color: '#1890ff' }}>
              Employment Information
            </h3>
            <Row gutter={16}>
              <Col xs={24} sm={12} lg={8}>
                <Form.Item
                  label="Department"
                  name="departmentName"
                  rules={[{ required: true, message: 'Please select department' }]}
                >
                  <Select placeholder="Select department" loading={loadingDepartments} showSearch>
                    {departmentLOV.map((dept) => (
                      <Option key={dept.lovId} value={dept.lovValue}>
                        {dept.lovDisplayValue}
                      </Option>
                    ))}
                  </Select>
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} lg={8}>
                <Form.Item
                  label="Designation"
                  name="designation"
                  rules={[{ required: true, message: 'Please select designation' }]}
                >
                  <Select placeholder="Select designation" loading={loadingDesignations} showSearch>
                    {designationLOV.map((desig) => (
                      <Option key={desig.lovId} value={desig.lovValue}>
                        {desig.lovDisplayValue}
                      </Option>
                    ))}
                  </Select>
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} lg={8}>
                <Form.Item label="Job Title" name="jobTitle">
                  <Input placeholder="Enter job title" />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} lg={8}>
                <Form.Item label="Employment Type" name="employmentType">
                  <Input placeholder="Enter employment type" />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} lg={8}>
                <Form.Item
                  label="Reporting Officer"
                  name="reportingOfficerId"
                  rules={[{ required: true, message: 'Please select reporting officer' }]}
                >
                  <Select
                    placeholder="Select reporting officer"
                    loading={loadingReportingOfficers}
                    showSearch
                    filterOption={(input, option) =>
                      (option?.children ?? '').toLowerCase().includes(input.toLowerCase())
                    }
                  >
                    {reportingOfficers.map((officer) => (
                      <Option key={officer.employeeId} value={officer.employeeId}>
                        {officer.displayValue || `${officer.employeeId} - ${officer.employeeName}`}
                      </Option>
                    ))}
                  </Select>
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} lg={8}>
                <Form.Item label="Hire Date" name="hireDate">
                  <DatePicker style={{ width: '100%' }} format="DD-MM-YYYY" />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} lg={8}>
                <Form.Item label="End Date" name="endDate">
                  <DatePicker style={{ width: '100%' }} format="DD-MM-YYYY" />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} lg={8}>
                <Form.Item label="Status" name="status">
                  <Select placeholder="Select status">
                    <Option value="Active">Active</Option>
                    <Option value="Inactive">Inactive</Option>
                  </Select>
                </Form.Item>
              </Col>
            </Row>
          </div>

          <Divider />

          {/* Address Information Section */}
          <div style={{ marginBottom: '24px' }}>
            <h3 style={{ fontSize: '16px', fontWeight: 600, marginBottom: '16px', color: '#1890ff' }}>
              Address Information
            </h3>
            <Row gutter={16}>
              <Col xs={24}>
                <Form.Item label="Street Address" name="streetAddress">
                  <Input placeholder="Enter street address" />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} lg={8}>
                <Form.Item
                  label="State"
                  name="state"
                  rules={[{ required: true, message: 'Please select state' }]}
                >
                  <Select
                    placeholder="Select state"
                    loading={loadingStates}
                    showSearch
                    filterOption={(input, option) =>
                      (option?.children ?? '').toLowerCase().includes(input.toLowerCase())
                    }
                    onChange={handleEditStateChange}
                  >
                    {states.map((state) => (
                      <Option key={state.value} value={state.value}>
                        {state.displayValue || state.value}
                      </Option>
                    ))}
                  </Select>
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} lg={8}>
                <Form.Item
                  label="City"
                  name="city"
                  rules={[{ required: true, message: 'Please select city' }]}
                >
                  <Select
                    placeholder={editSelectedState ? "Select city" : "Select state first"}
                    loading={loadingEditCities}
                    showSearch
                    disabled={!editSelectedState}
                    filterOption={(input, option) =>
                      (option?.children ?? '').toLowerCase().includes(input.toLowerCase())
                    }
                    notFoundContent={loadingEditCities ? <Spin size="small" /> : "No cities found"}
                  >
                    {editCities.map((city) => (
                      <Option key={city.value} value={city.value}>
                        {city.displayValue || city.value}
                      </Option>
                    ))}
                  </Select>
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} lg={8}>
                <Form.Item
                  label="Pin Code"
                  name="pinCode"
                  rules={[
                    { pattern: /^\d{6}$/, message: 'Please enter valid 6-digit pin code' }
                  ]}
                >
                  <Input placeholder="Enter pin code" maxLength={6} />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} lg={8}>
                <Form.Item label="Location" name="location">
                  <Select placeholder="Select office location" loading={loadingLocations} showSearch>
                    {locationLOV.map((loc) => (
                      <Option key={loc.lovId} value={loc.lovValue}>
                        {loc.lovDisplayValue}
                      </Option>
                    ))}
                  </Select>
                </Form.Item>
              </Col>
            </Row>
          </div>

          {/* Action Buttons */}
          <Form.Item style={{ marginTop: '24px', marginBottom: 0 }}>
            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px' }}>
              <Button onClick={() => {
                setEditModalVisible(false);
                setEditingEmployee(null);
                editForm.resetFields();
                setEditSelectedState('');
                setEditCities([]);
              }}>
                Cancel
              </Button>
              <Button type="primary" htmlType="submit" loading={editLoading}>
                Update Employee
              </Button>
            </div>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default EmployeeRegistration;
