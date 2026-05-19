import React, { useState, useEffect } from 'react';
import { Card, Form, Input, Button, Select, message, Row, Col, Table, Tag, Modal, Descriptions, Divider, AutoComplete, Spin, Tabs, Space, Tooltip, Switch } from 'antd';
import { TeamOutlined, EyeInvisibleOutlined, EyeOutlined, CheckCircleOutlined, UserOutlined, SearchOutlined, EditOutlined, ReloadOutlined, PlusOutlined } from '@ant-design/icons';
import axios from 'axios';
import dayjs from 'dayjs';

const { Option } = Select;
const { TabPane } = Tabs;

const UserCreation = () => {
  const [form] = Form.useForm();
  const [editForm] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [roles, setRoles] = useState([]);
  const [departments, setDepartments] = useState([]);
  const [recentUsers, setRecentUsers] = useState([]);
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [successModalVisible, setSuccessModalVisible] = useState(false);
  const [createdUser, setCreatedUser] = useState(null);
  // TC_16: Employee search and validation
  const [employeeOptions, setEmployeeOptions] = useState([]);
  const [searchingEmployee, setSearchingEmployee] = useState(false);
  const [selectedEmployee, setSelectedEmployee] = useState(null);
  const [employeeValidated, setEmployeeValidated] = useState(false);

  // Search and Edit states
  const [activeTab, setActiveTab] = useState('search');
  const [searchKeyword, setSearchKeyword] = useState('');
  const [searchResults, setSearchResults] = useState([]);
  const [searchLoading, setSearchLoading] = useState(false);
  const [editModalVisible, setEditModalVisible] = useState(false);
  const [editingUser, setEditingUser] = useState(null);
  const [editLoading, setEditLoading] = useState(false);
  const [showEditPassword, setShowEditPassword] = useState(false);

  useEffect(() => {
    fetchRoles();
    fetchDepartments();
    fetchRecentUsers();
    fetchAllUsers();
  }, []);

  const fetchRoles = async () => {
    try {
      const response = await axios.get('/api/employee-department-master/roles');
      if (response.data.responseData) {
        setRoles(response.data.responseData);
      }
    } catch (error) {
      console.error('Failed to fetch roles:', error);
    }
  };

  const fetchDepartments = async () => {
    try {
      const response = await axios.get('/api/employee-department-master/departments');
      if (response.data.responseData) {
        setDepartments(response.data.responseData);
      }
    } catch (error) {
      console.error('Failed to fetch departments:', error);
    }
  };

  const fetchRecentUsers = async () => {
    try {
      const response = await axios.get('/api/userMaster');
      if (response.data.responseData) {
        // Get last 10 users, sorted by creation date
        const sortedUsers = response.data.responseData
          .sort((a, b) => new Date(b.createdDate) - new Date(a.createdDate))
          .slice(0, 10);
        setRecentUsers(sortedUsers);
      }
    } catch (error) {
      console.error('Failed to fetch recent users:', error);
    }
  };

  // Fetch all users with roles for search tab
  const fetchAllUsers = async () => {
    try {
      setSearchLoading(true);
      const response = await axios.get('/api/userMaster/list');
      if (response.data.responseData) {
        setSearchResults(response.data.responseData);
      }
    } catch (error) {
      console.error('Failed to fetch users:', error);
      // Fallback to old endpoint if new one doesn't exist
      try {
        const fallbackResponse = await axios.get('/api/userMaster');
        if (fallbackResponse.data.responseData) {
          setSearchResults(fallbackResponse.data.responseData);
        }
      } catch (fallbackError) {
        console.error('Fallback also failed:', fallbackError);
      }
    } finally {
      setSearchLoading(false);
    }
  };

  // Search users by keyword
  const handleSearch = async () => {
    if (!searchKeyword.trim()) {
      fetchAllUsers();
      return;
    }

    try {
      setSearchLoading(true);
      const response = await axios.get('/api/userMaster/search', {
        params: { keyword: searchKeyword.trim() }
      });
      if (response.data.responseData) {
        setSearchResults(response.data.responseData);
      }
    } catch (error) {
      console.error('Search failed:', error);
      message.error('Failed to search users');
    } finally {
      setSearchLoading(false);
    }
  };

  // Handle edit button click
  const handleEdit = (user) => {
    setEditingUser(user);

    // Parse roles - handle both string and array formats
    let userRoles = [];
    if (user.roleNames && typeof user.roleNames === 'string') {
      userRoles = user.roleNames.split(',').map(r => r.trim()).filter(r => r);
    } else if (Array.isArray(user.roleNames)) {
      userRoles = user.roleNames;
    } else if (user.roleName) {
      userRoles = user.roleName.split(',').map(r => r.trim()).filter(r => r);
    }

    editForm.setFieldsValue({
      userName: user.userName,
      email: user.email,
      mobileNumber: user.mobileNumber,
      employeeId: user.employeeId,
      roleNames: userRoles,
      password: '' // Don't pre-fill password
    });
    setEditModalVisible(true);
  };

  // Handle edit form submit
  const handleEditSubmit = async (values) => {
    try {
      setEditLoading(true);

      const payload = {
        userName: values.userName,
        email: values.email,
        mobileNumber: values.mobileNumber || null,
        employeeId: values.employeeId || null,
        roleNames: values.roleNames,
        createdBy: 'admin'
      };

      // Only include password if provided
      if (values.password && values.password.trim()) {
        payload.password = values.password;
      }

      await axios.put(`/api/userMaster/${editingUser.userId}`, payload);

      message.success('User updated successfully');
      setEditModalVisible(false);
      setEditingUser(null);
      editForm.resetFields();

      // Refresh lists
      fetchAllUsers();
      fetchRecentUsers();
    } catch (error) {
      const errorMessage = error.response?.data?.responseStatus?.message
        || error.response?.data?.message
        || 'Failed to update user';
      message.error(errorMessage);
    } finally {
      setEditLoading(false);
    }
  };

  // Handle toggle user active/inactive status
  const handleToggleStatus = async (userId) => {
    try {
      const response = await axios.put(`/api/userMaster/${userId}/toggle-status`);
      const updatedUser = response.data.responseData;
      // Update the user in the search results without a full refetch
      setSearchResults(prev => prev.map(u =>
        u.userId === userId ? { ...u, isActive: updatedUser.isActive } : u
      ));
      message.success(`User ${updatedUser.isActive ? 'activated' : 'deactivated'} successfully`);
    } catch (error) {
      message.error(error.response?.data?.responseStatus?.message || 'Failed to update user status');
    }
  };

  const validatePassword = (_, value) => {
    if (!value) {
      return Promise.reject(new Error('Please enter password'));
    }
    if (value.length < 8) {
      return Promise.reject(new Error('Password must be at least 8 characters'));
    }
    if (!/(?=.*[a-z])/.test(value)) {
      return Promise.reject(new Error('Password must contain at least one lowercase letter'));
    }
    if (!/(?=.*[A-Z])/.test(value)) {
      return Promise.reject(new Error('Password must contain at least one uppercase letter'));
    }
    if (!/(?=.*\d)/.test(value)) {
      return Promise.reject(new Error('Password must contain at least one number'));
    }
    if (!/(?=.*[@$!%*?&#])/.test(value)) {
      return Promise.reject(new Error('Password must contain at least one special character'));
    }
    return Promise.resolve();
  };

  const validateEditPassword = (_, value) => {
    // Password is optional during edit
    if (!value || !value.trim()) {
      return Promise.resolve();
    }
    // If password is provided, validate it
    return validatePassword(_, value);
  };

  const validateConfirmPassword = (_, value) => {
    if (!value) {
      return Promise.reject(new Error('Please confirm password'));
    }
    if (value !== form.getFieldValue('password')) {
      return Promise.reject(new Error('Passwords do not match'));
    }
    return Promise.resolve();
  };

  // TC_16: Employee search autocomplete
  const handleEmployeeSearch = async (searchValue) => {
    if (!searchValue || searchValue.length < 2) {
      setEmployeeOptions([]);
      return;
    }

    try {
      setSearchingEmployee(true);
      const response = await axios.get('/api/employee-department-master/employeeSearch', {
        params: { keyword: searchValue }
      });

      if (response.data.responseStatus?.statusCode === 0) {
        const employees = response.data.responseData || [];
        const options = employees.map(emp => ({
          value: emp.employeeId,
          label: `${emp.employeeId} - ${emp.employeeName} (${emp.departmentName})`,
          employee: emp
        }));
        setEmployeeOptions(options);
      }
    } catch (error) {
      console.error('Employee search error:', error);
    } finally {
      setSearchingEmployee(false);
    }
  };

  // TC_16: Handle employee selection from autocomplete
  const handleEmployeeSelect = async (value, option) => {
    const employee = option.employee;
    setSelectedEmployee(employee);
    setEmployeeValidated(true);

    // Auto-fill employee details
    form.setFieldsValue({
      employeeId: employee.employeeId,
      userName: employee.employeeName,
      email: employee.emailAddress,
      mobileNumber: employee.phoneNumber
    });

    // Check if user already exists for this employee
    try {
      const response = await axios.get(`/api/employee-department-master/user-exists/${employee.employeeId}`);
      if (response.data.responseData?.exists) {
        message.warning(`User already exists for employee ${employee.employeeId}`);
        setEmployeeValidated(false);
      } else {
        message.success(`Employee ${employee.employeeId} selected successfully`);
      }
    } catch (error) {
      console.error('Error checking user existence:', error);
    }
  };

  // TC_16: Validate employee ID exists
  const validateEmployeeId = async (_, value) => {
    if (!value) {
      setEmployeeValidated(false);
      return Promise.resolve(); // Employee ID is optional
    }

    try {
      const response = await axios.get(`/api/employee-department-master/${value}`);
      if (response.data.responseStatus?.statusCode === 0) {
        const employee = response.data.responseData;
        if (employee) {
          setEmployeeValidated(true);
          setSelectedEmployee(employee);
          // Auto-fill email, userName, and mobileNumber from employee table
          form.setFieldsValue({
            userName: form.getFieldValue('userName') || employee.employeeName,
            email: employee.emailAddress,
            mobileNumber: employee.phoneNumber || form.getFieldValue('mobileNumber')
          });
          return Promise.resolve();
        }
      }
      setEmployeeValidated(false);
      return Promise.reject(new Error('Employee ID does not exist. Please register the employee first.'));
    } catch (error) {
      setEmployeeValidated(false);
      if (error.response?.status === 404) {
        return Promise.reject(new Error('Employee ID does not exist. Please register the employee first.'));
      }
      return Promise.reject(new Error('Error validating employee ID'));
    }
  };

  const handleSubmit = async (values) => {
    try {
      setLoading(true);

      const payload = {
        userName: values.userName,
        email: values.email,
        password: values.password,
        employeeId: values.employeeId || null,
        roleNames: values.roleNames,
        mobileNumber: values.mobileNumber || null,
        createdBy: 'admin'
      };

      // Check if user exists for employee ID
      if (values.employeeId) {
        const checkResponse = await axios.get(`/api/employee-department-master/user-exists/${values.employeeId}`);
        if (checkResponse.data.responseData?.exists) {
          message.warning('User already exists for this employee ID');
          setLoading(false);
          return;
        }
      }

      const response = await axios.post('/api/userMaster', payload);
      const userData = response.data?.responseData;

      if (userData) {
        setCreatedUser(userData);
        setSuccessModalVisible(true);
        form.resetFields();
        setSelectedEmployee(null);
        setEmployeeValidated(false);
        fetchRecentUsers();
        fetchAllUsers();
      }
    } catch (error) {
      const errorMessage = error.response?.data?.responseStatus?.message
        || error.response?.data?.message
        || 'Failed to create user';

      if (errorMessage.includes('Employee ID does not exist')) {
        message.error({
          content: 'Employee ID does not exist in the system. Please register the employee first.',
          duration: 5
        });
      } else if (errorMessage.includes('User already exists')) {
        message.error({
          content: 'User already exists for this employee ID.',
          duration: 5
        });
      } else {
        message.error(errorMessage);
      }
    } finally {
      setLoading(false);
    }
  };

  const handleClear = () => {
    form.resetFields();
  };

  const handleCloseModal = () => {
    setSuccessModalVisible(false);
    setCreatedUser(null);
  };

  // Filter roles helper
  const filterRoles = (roleList) => {
    return roleList.filter((role) => {
      if (!role || !role.roleId) return false;
      const roleName = (role.roleName || '').trim();
      if (!roleName || roleName.length === 0) return false;
      const excludedRoles = ['heas sag', 'heas_sag'];
      return !excludedRoles.includes(roleName.toLowerCase());
    });
  };

  // Search results table columns
  const searchColumns = [
    {
      title: 'User ID',
      dataIndex: 'userId',
      key: 'userId',
      width: 80,
      sorter: (a, b) => a.userId - b.userId
    },
    {
      title: 'Username',
      dataIndex: 'userName',
      key: 'userName',
      width: 150,
      sorter: (a, b) => (a.userName || '').localeCompare(b.userName || '')
    },
    {
      title: 'Email',
      dataIndex: 'email',
      key: 'email',
      width: 200
    },
    {
      title: 'Mobile',
      dataIndex: 'mobileNumber',
      key: 'mobileNumber',
      width: 120,
      render: (mobile) => mobile || '-'
    },
    {
      title: 'Employee ID',
      dataIndex: 'employeeId',
      key: 'employeeId',
      width: 120,
      render: (empId) => empId ? <Tag color="green">{empId}</Tag> : '-'
    },
    {
      title: 'Employee Name',
      dataIndex: 'employeeName',
      key: 'employeeName',
      width: 150,
      render: (name) => name || '-'
    },
    {
      title: 'Role(s)',
      dataIndex: 'roleNames',
      key: 'roleNames',
      width: 200,
      render: (roleNames, record) => {
        let roles = [];
        if (roleNames && typeof roleNames === 'string') {
          roles = roleNames.split(',').map(r => r.trim()).filter(r => r);
        } else if (Array.isArray(roleNames)) {
          roles = roleNames;
        } else if (record.roleName) {
          roles = record.roleName.split(',').map(r => r.trim()).filter(r => r);
        }

        if (roles.length === 0) return '-';

        return (
          <div style={{ display: 'flex', gap: '4px', flexWrap: 'wrap' }}>
            {roles.slice(0, 3).map((r, idx) => (
              <Tag key={idx} color="blue">{r}</Tag>
            ))}
            {roles.length > 3 && (
              <Tooltip title={roles.slice(3).join(', ')}>
                <Tag color="default">+{roles.length - 3} more</Tag>
              </Tooltip>
            )}
          </div>
        );
      }
    },
    {
      title: 'Created Date',
      dataIndex: 'createdDate',
      key: 'createdDate',
      width: 120,
      render: (date) => date ? dayjs(date).format('DD-MM-YYYY') : '-',
      sorter: (a, b) => new Date(a.createdDate) - new Date(b.createdDate)
    },
    {
      title: 'Status',
      dataIndex: 'isActive',
      key: 'isActive',
      width: 90,
      filters: [
        { text: 'Active', value: true },
        { text: 'Inactive', value: false }
      ],
      onFilter: (value, record) => record.isActive === value,
      render: (isActive) => (
        <Tag color={isActive === false ? 'red' : 'green'}>
          {isActive === false ? 'Inactive' : 'Active'}
        </Tag>
      )
    },
    {
      title: 'Actions',
      key: 'actions',
      width: 140,
      fixed: 'right',
      render: (_, record) => (
        <Space>
          <Tooltip title="Edit User">
            <Button
              type="primary"
              icon={<EditOutlined />}
              size="small"
              onClick={() => handleEdit(record)}
            />
          </Tooltip>
          <Tooltip title={record.isActive === false ? 'Activate User' : 'Deactivate User'}>
            <Switch
              checked={record.isActive !== false}
              onChange={() => handleToggleStatus(record.userId)}
              checkedChildren="On"
              unCheckedChildren="Off"
              size="small"
            />
          </Tooltip>
        </Space>
      )
    }
  ];

  const recentUsersColumns = [
    {
      title: 'Username',
      dataIndex: 'userName',
      key: 'userName',
      width: 150
    },
    {
      title: 'Email',
      dataIndex: 'email',
      key: 'email',
      width: 200
    },
    {
      title: 'Role(s)',
      dataIndex: 'roleName',
      key: 'roleName',
      width: 200,
      render: (role, record) => {
        let roles = [];
        if (record.roleNames && Array.isArray(record.roleNames) && record.roleNames.length > 0) {
          roles = record.roleNames;
        } else if (role) {
          roles = role.split(',').map(r => r.trim()).filter(r => r);
        }

        if (roles.length === 0) return '-';

        return (
          <div style={{ display: 'flex', gap: '4px', flexWrap: 'wrap' }}>
            {roles.map((r, idx) => (
              <Tag key={idx} color="blue">{r}</Tag>
            ))}
          </div>
        );
      }
    },
    {
      title: 'Created Date',
      dataIndex: 'createdDate',
      key: 'createdDate',
      width: 150,
      render: (date) => date ? dayjs(date).format('DD-MM-YYYY HH:mm') : '-'
    }
  ];

  return (
    <div style={{ padding: '24px' }}>
      <Card
        title={
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <TeamOutlined style={{ fontSize: '20px', color: '#1890ff' }} />
            <span>User Management</span>
          </div>
        }
        bordered={false}
      >
        <Tabs activeKey={activeTab} onChange={setActiveTab}>
          {/* Search Users Tab */}
          <TabPane
            tab={
              <span>
                <SearchOutlined />
                Search Users
              </span>
            }
            key="search"
          >
            <div style={{ marginBottom: '16px' }}>
              <Row gutter={16} align="middle">
                <Col flex="auto">
                  <Input
                    placeholder="Search by username, email, mobile, employee ID, or employee name..."
                    value={searchKeyword}
                    onChange={(e) => setSearchKeyword(e.target.value)}
                    onPressEnter={handleSearch}
                    prefix={<SearchOutlined />}
                    allowClear
                    size="large"
                  />
                </Col>
                <Col>
                  <Space>
                    <Button
                      type="primary"
                      icon={<SearchOutlined />}
                      onClick={handleSearch}
                      loading={searchLoading}
                      size="large"
                    >
                      Search
                    </Button>
                    <Button
                      icon={<ReloadOutlined />}
                      onClick={() => {
                        setSearchKeyword('');
                        fetchAllUsers();
                      }}
                      size="large"
                    >
                      Reset
                    </Button>
                  </Space>
                </Col>
              </Row>
            </div>

            <style>{`.inactive-user-row { opacity: 0.55; }`}</style>
            <Table
              columns={searchColumns}
              dataSource={searchResults}
              rowKey="userId"
              loading={searchLoading}
              rowClassName={(record) => record.isActive === false ? 'inactive-user-row' : ''}
              pagination={{
                pageSize: 10,
                showSizeChanger: true,
                showTotal: (total, range) => `${range[0]}-${range[1]} of ${total} users`
              }}
              scroll={{ x: 1200 }}
              size="middle"
            />
          </TabPane>

          {/* Create User Tab */}
          <TabPane
            tab={
              <span>
                <PlusOutlined />
                Create User
              </span>
            }
            key="create"
          >
            <Row gutter={24}>
              <Col xs={24} lg={14}>
                <h3 style={{ fontSize: '16px', fontWeight: 600, marginBottom: '16px' }}>
                  Create New User Account
                </h3>

                <Form form={form} layout="vertical" onFinish={handleSubmit}>
                  <Form.Item
                    label="Username"
                    name="userName"
                    rules={[
                      { required: true, message: 'Please enter username' },
                      { min: 3, message: 'Username must be at least 3 characters' }
                    ]}
                  >
                    <Input placeholder="Enter username" />
                  </Form.Item>

                  <Form.Item
                    label="Email Address"
                    name="email"
                    rules={[
                      { required: true, message: 'Please enter email address' },
                      { type: 'email', message: 'Please enter valid email' }
                    ]}
                  >
                    <Input placeholder="Enter email address" />
                  </Form.Item>

                  <Form.Item
                    label="Password"
                    name="password"
                    rules={[{ validator: validatePassword }]}
                  >
                    <Input
                      type={showPassword ? 'text' : 'password'}
                      placeholder="Enter password"
                      suffix={
                        showPassword ? (
                          <EyeOutlined onClick={() => setShowPassword(false)} style={{ cursor: 'pointer' }} />
                        ) : (
                          <EyeInvisibleOutlined onClick={() => setShowPassword(true)} style={{ cursor: 'pointer' }} />
                        )
                      }
                    />
                  </Form.Item>

                  <Form.Item
                    label="Confirm Password"
                    name="confirmPassword"
                    rules={[{ validator: validateConfirmPassword }]}
                  >
                    <Input
                      type={showConfirmPassword ? 'text' : 'password'}
                      placeholder="Confirm password"
                      suffix={
                        showConfirmPassword ? (
                          <EyeOutlined onClick={() => setShowConfirmPassword(false)} style={{ cursor: 'pointer' }} />
                        ) : (
                          <EyeInvisibleOutlined onClick={() => setShowConfirmPassword(true)} style={{ cursor: 'pointer' }} />
                        )
                      }
                    />
                  </Form.Item>

                  <Form.Item
                    label="Employee ID (Optional)"
                    name="employeeId"
                    tooltip="Search and select an existing employee to link to this user account"
                    help={selectedEmployee ? `Selected: ${selectedEmployee.employeeName} - ${selectedEmployee.departmentName}` : "Start typing employee ID or name to search"}
                    rules={[{ validator: validateEmployeeId }]}
                  >
                    <AutoComplete
                      options={employeeOptions}
                      onSearch={handleEmployeeSearch}
                      onSelect={handleEmployeeSelect}
                      placeholder="Search by employee ID or name"
                      notFoundContent={searchingEmployee ? <Spin size="small" /> : "No employees found"}
                      suffixIcon={<SearchOutlined />}
                      allowClear
                      onClear={() => {
                        setSelectedEmployee(null);
                        setEmployeeValidated(false);
                        setEmployeeOptions([]);
                      }}
                      style={{ width: '100%' }}
                    />
                  </Form.Item>

                  <Form.Item
                    label="User Roles"
                    name="roleNames"
                    rules={[{ required: true, message: 'Please select at least one role' }]}
                    tooltip="Select one or more roles for this user"
                  >
                    <Select
                      mode="multiple"
                      placeholder="Select one or more roles"
                      showSearch
                      filterOption={(input, option) =>
                        (option?.children ?? '').toLowerCase().includes(input.toLowerCase())
                      }
                    >
                      {filterRoles(roles).map((role) => (
                        <Option key={role.roleId} value={role.roleName.trim()}>
                          {role.roleName.trim()}
                        </Option>
                      ))}
                    </Select>
                  </Form.Item>

                  <Form.Item label="Mobile Number (Optional)" name="mobileNumber">
                  <Input placeholder="Enter mobile or landline number" maxLength={15} />
                    {/* <Input placeholder="Enter mobile number" maxLength={10} /> */}
                  </Form.Item>

                  <Form.Item style={{ marginTop: '32px', marginBottom: 0 }}>
                    <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px' }}>
                      <Button size="large" onClick={handleClear}>
                        Clear Form
                      </Button>
                      <Button type="primary" size="large" htmlType="submit" loading={loading}>
                        Create User
                      </Button>
                    </div>
                  </Form.Item>
                </Form>

                <div style={{ marginTop: '24px', padding: '16px', backgroundColor: '#f6f8fa', borderRadius: '4px' }}>
                  <h4 style={{ fontSize: '14px', fontWeight: 600, marginBottom: '8px' }}>Password Requirements:</h4>
                  <ul style={{ margin: 0, paddingLeft: '20px', fontSize: '13px', color: '#666' }}>
                    <li>At least 8 characters long</li>
                    <li>At least one uppercase letter (A-Z)</li>
                    <li>At least one lowercase letter (a-z)</li>
                    <li>At least one number (0-9)</li>
                    <li>At least one special character (@$!%*?&#)</li>
                  </ul>
                </div>
              </Col>

              <Col xs={24} lg={10}>
                <Card title="Recently Created Users" bordered={false} size="small">
                  {recentUsers.length === 0 ? (
                    <div style={{ textAlign: 'center', padding: '40px', color: '#999' }}>
                      No users created yet
                    </div>
                  ) : (
                    <Table
                      columns={recentUsersColumns}
                      dataSource={recentUsers}
                      rowKey="userId"
                      pagination={false}
                      size="small"
                      scroll={{ x: 500 }}
                    />
                  )}
                </Card>
              </Col>
            </Row>
          </TabPane>
        </Tabs>
      </Card>

      {/* Edit User Modal */}
      <Modal
        title={
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <EditOutlined style={{ fontSize: '20px', color: '#1890ff' }} />
            <span>Edit User - ID: {editingUser?.userId}</span>
          </div>
        }
        open={editModalVisible}
        onCancel={() => {
          setEditModalVisible(false);
          setEditingUser(null);
          editForm.resetFields();
        }}
        footer={null}
        width={600}
        destroyOnClose
      >
        <Form
          form={editForm}
          layout="vertical"
          onFinish={handleEditSubmit}
          style={{ marginTop: '16px' }}
        >
          <Form.Item
            label="Username"
            name="userName"
            rules={[
              { required: true, message: 'Please enter username' },
              { min: 3, message: 'Username must be at least 3 characters' }
            ]}
          >
            <Input placeholder="Enter username" />
          </Form.Item>

          <Form.Item
            label="Email Address"
            name="email"
            rules={[
              { required: true, message: 'Please enter email address' },
              { type: 'email', message: 'Please enter valid email' }
            ]}
          >
            <Input placeholder="Enter email address" />
          </Form.Item>

          <Form.Item
            label="New Password (Leave blank to keep current)"
            name="password"
            rules={[{ validator: validateEditPassword }]}
          >
            <Input
              type={showEditPassword ? 'text' : 'password'}
              placeholder="Enter new password (optional)"
              suffix={
                showEditPassword ? (
                  <EyeOutlined onClick={() => setShowEditPassword(false)} style={{ cursor: 'pointer' }} />
                ) : (
                  <EyeInvisibleOutlined onClick={() => setShowEditPassword(true)} style={{ cursor: 'pointer' }} />
                )
              }
            />
          </Form.Item>

          <Form.Item
            label="Employee ID"
            name="employeeId"
          >
            <Input placeholder="Enter employee ID" />
          </Form.Item>

          <Form.Item
            label="User Roles"
            name="roleNames"
            rules={[{ required: true, message: 'Please select at least one role' }]}
          >
            <Select
              mode="multiple"
              placeholder="Select one or more roles"
              showSearch
              filterOption={(input, option) =>
                (option?.children ?? '').toLowerCase().includes(input.toLowerCase())
              }
            >
              {filterRoles(roles).map((role) => (
                <Option key={role.roleId} value={role.roleName.trim()}>
                  {role.roleName.trim()}
                </Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item label="Mobile Number" name="mobileNumber">
          <Input placeholder="Enter mobile or landline number" maxLength={15} />
            {/* <Input placeholder="Enter mobile number" maxLength={10} /> */}
          </Form.Item>

          <Form.Item style={{ marginTop: '24px', marginBottom: 0 }}>
            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px' }}>
              <Button onClick={() => {
                setEditModalVisible(false);
                setEditingUser(null);
                editForm.resetFields();
              }}>
                Cancel
              </Button>
              <Button type="primary" htmlType="submit" loading={editLoading}>
                Update User
              </Button>
            </div>
          </Form.Item>
        </Form>
      </Modal>

      {/* Success Modal */}
      <Modal
        title={
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <CheckCircleOutlined style={{ fontSize: '24px', color: '#52c41a' }} />
            <span>User Created Successfully!</span>
          </div>
        }
        open={successModalVisible}
        onCancel={handleCloseModal}
        footer={[
          <Button key="close" type="primary" onClick={handleCloseModal}>
            Close
          </Button>
        ]}
        width={600}
      >
        {createdUser && (
          <>
            <div style={{
              backgroundColor: '#e6f7ff',
              padding: '16px',
              borderRadius: '8px',
              border: '2px solid #1890ff',
              marginBottom: '20px'
            }}>
              <div style={{ fontSize: '12px', color: '#1890ff', marginBottom: '4px' }}>
                User ID (Auto-Generated)
              </div>
              <div style={{ fontSize: '24px', fontWeight: 'bold', color: '#1890ff' }}>
                {createdUser.userId}
              </div>
            </div>

            <Divider />

            <h4 style={{ marginBottom: '12px', color: '#1890ff' }}>User Information</h4>
            <Descriptions bordered column={1} size="small">
              <Descriptions.Item label="Username">{createdUser.userName}</Descriptions.Item>
              <Descriptions.Item label="Email Address">{createdUser.email}</Descriptions.Item>
              <Descriptions.Item label="Mobile Number">
                {createdUser.mobileNumber || '-'}
              </Descriptions.Item>
              {createdUser.employeeId && (
                <Descriptions.Item label="Linked Employee ID">
                  <Tag color="green">{createdUser.employeeId}</Tag>
                </Descriptions.Item>
              )}
            </Descriptions>

            <Divider />

            <h4 style={{ marginBottom: '12px', color: '#1890ff' }}>Assigned Roles</h4>
            <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap', marginBottom: '16px' }}>
              {(createdUser.roleNames && Array.isArray(createdUser.roleNames) && createdUser.roleNames.length > 0
                ? createdUser.roleNames
                : (createdUser.roleName?.split(',') || [])
              ).map((role, index) => (
                <Tag key={index} color="blue" style={{ fontSize: '14px', padding: '4px 12px' }}>
                  {typeof role === 'string' ? role.trim() : role}
                </Tag>
              ))}
            </div>

            <Divider />

            <h4 style={{ marginBottom: '12px', color: '#1890ff' }}>System Information</h4>
            <Descriptions bordered column={2} size="small">
              <Descriptions.Item label="Created By">{createdUser.createdBy}</Descriptions.Item>
              <Descriptions.Item label="Created Date">
                {dayjs(createdUser.createdDate).format('DD-MM-YYYY HH:mm:ss')}
              </Descriptions.Item>
            </Descriptions>
          </>
        )}
      </Modal>
    </div>
  );
};

export default UserCreation;
