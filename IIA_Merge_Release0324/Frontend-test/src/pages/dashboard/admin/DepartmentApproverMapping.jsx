import React, { useState, useEffect } from 'react';
import {
  Card, Table, Button, Modal, Form, Input, Select, InputNumber,
  Space, message, Popconfirm, Tag, Switch, Typography, Row, Col,
  Tooltip, Tabs, Statistic
} from 'antd';
import {
  PlusOutlined, EditOutlined, DeleteOutlined, ReloadOutlined,
  SearchOutlined, InfoCircleOutlined, TeamOutlined, UserOutlined
} from '@ant-design/icons';
import axios from 'axios';
import { DepartmentApproversService, APPROVER_TYPE_OPTIONS, formatCurrency, DEFAULT_APPROVAL_LIMITS } from '../../../services/approvalWorkflowService';
import store from '../../../store';

const { Option } = Select;
const { Text } = Typography;

const STANDARD_DEPARTMENTS = [
  'Admin',
  'Academic',
  'Non-Technical',
  'BGS',
  'Technical and Engineering'
];

const DepartmentApproverMapping = () => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [mappings, setMappings] = useState([]);
  const [filteredMappings, setFilteredMappings] = useState([]);
  const [modalVisible, setModalVisible] = useState(false);
  const [editingRecord, setEditingRecord] = useState(null);
  const [departments, setDepartments] = useState([]);
  const [employees, setEmployees] = useState([]);
  const [roles, setRoles] = useState([]);

  // Filters
  const [activeTab, setActiveTab] = useState('all');
  const [filterDepartment, setFilterDepartment] = useState(null);
  const [searchText, setSearchText] = useState('');

  // Stats
  const [stats, setStats] = useState({ total: 0, deans: 0, headSegs: 0, active: 0 });

  useEffect(() => {
    fetchMappings();
    fetchDepartments();
    fetchEmployees();
    fetchRoles();
  }, []);

  useEffect(() => {
    applyFilters();
    calculateStats();
  }, [mappings, activeTab, filterDepartment, searchText]);

  const fetchMappings = async () => {
    setLoading(true);
    try {
      const response = await DepartmentApproversService.getAll();
      const data = response.data?.responseData || response.data?.data || response.data || [];
      setMappings(Array.isArray(data) ? data : []);
    } catch (error) {
      console.error('Error fetching mappings:', error);
      message.error('Failed to fetch department approver mappings');
    } finally {
      setLoading(false);
    }
  };

  const fetchDepartments = async () => {
    try {
      const response = await axios.get('/api/employee-department-master/departments');
      const data = response.data?.responseData || response.data || [];
      setDepartments(Array.isArray(data) ? data : []);
    } catch (error) {
      console.error('Error fetching departments:', error);
    }
  };

  const fetchEmployees = async () => {
    try {
      const response = await axios.get('/api/employee-department-master');
      const data = response.data?.responseData || response.data || [];
      setEmployees(Array.isArray(data) ? data : []);
    } catch (error) {
      console.error('Error fetching employees:', error);
    }
  };

  const fetchRoles = async () => {
    try {
      const response = await axios.get('/api/employee-department-master/roles');
      const data = response.data?.responseData || response.data || [];
      setRoles(Array.isArray(data) ? data : []);
    } catch (error) {
      console.error('Error fetching roles:', error);
    }
  };

  const calculateStats = () => {
    const total = mappings.length;
    const deans = mappings.filter(m => m.approverType === 'DEAN').length;
    const headSegs = mappings.filter(m => m.approverType === 'HEAD_SEG').length;
    const active = mappings.filter(m => m.isActive).length;
    setStats({ total, deans, headSegs, active });
  };

  const applyFilters = () => {
    let filtered = [...mappings];

    // Tab filter
    if (activeTab === 'deans') {
      filtered = filtered.filter(m => m.approverType === 'DEAN');
    } else if (activeTab === 'headsegs') {
      filtered = filtered.filter(m => m.approverType === 'HEAD_SEG');
    }

    // Department filter
    if (filterDepartment) {
      filtered = filtered.filter(m => m.departmentName === filterDepartment);
    }

    // Search
    if (searchText) {
      const searchLower = searchText.toLowerCase();
      filtered = filtered.filter(m =>
        m.departmentName?.toLowerCase().includes(searchLower) ||
        m.approverEmployeeName?.toLowerCase().includes(searchLower)
      );
    }

    setFilteredMappings(filtered);
  };

  const handleAdd = () => {
    setEditingRecord(null);
    form.resetFields();
    form.setFieldsValue({
      isActive: true,
      approvalLimit: DEFAULT_APPROVAL_LIMITS.DEAN // Default to Dean limit
    });
    setModalVisible(true);
  };

  const handleEdit = (record) => {
    setEditingRecord(record);
    form.setFieldsValue({
      ...record
    });
    setModalVisible(true);
  };

  const handleDelete = async (mappingId) => {
    try {
      await DepartmentApproversService.delete(mappingId);
      message.success('Mapping deleted successfully');
      fetchMappings();
    } catch (error) {
      message.error(error.response?.data?.message || 'Failed to delete mapping');
    }
  };

  const handleApproverTypeChange = (type) => {
    // Auto-set approval limit based on type
    const limit = type === 'DEAN' ? DEFAULT_APPROVAL_LIMITS.DEAN : DEFAULT_APPROVAL_LIMITS.HEAD_SEG;
    form.setFieldsValue({ approvalLimit: limit });
  };

  const handleEmployeeSelect = (employeeId) => {
    const employee = employees.find(e => e.employeeId === employeeId);
    if (employee) {
      form.setFieldsValue({
        approverName: `${employee.firstName || ''} ${employee.lastName || ''}`.trim() || employee.employeeName
      });
    }
  };

  const handleSubmit = async (values) => {
    try {
      const payload = {
        ...values,
        createdBy: String(store.getState().auth?.userId || 'admin'),
        updatedBy: String(store.getState().auth?.userId || 'admin')
      };

      if (editingRecord) {
        await DepartmentApproversService.update(editingRecord.mappingId, payload);
        message.success('Mapping updated successfully');
      } else {
        await DepartmentApproversService.create(payload);
        message.success('Mapping created successfully');
      }

      setModalVisible(false);
      form.resetFields();
      fetchMappings();
    } catch (error) {
      message.error(error.response?.data?.message || 'Failed to save mapping');
    }
  };

  const columns = [
    {
      title: 'Department',
      dataIndex: 'departmentName',
      key: 'departmentName',
      width: 200,
      sorter: (a, b) => (a.departmentName || '').localeCompare(b.departmentName || ''),
      render: (text) => <Text strong>{text}</Text>
    },
    {
      title: 'Approver Type',
      dataIndex: 'approverType',
      key: 'approverType',
      width: 130,
      render: (type) => {
        const colorMap = {
          'DEAN': 'purple',
          'HEAD_SEG': 'blue'
        };
        const labelMap = {
          'DEAN': 'Dean',
          'HEAD_SEG': 'Head SEG'
        };
        return <Tag color={colorMap[type]}>{labelMap[type] || type}</Tag>;
      },
      filters: APPROVER_TYPE_OPTIONS.map(opt => ({ text: opt.label, value: opt.value })),
      onFilter: (value, record) => record.approverType === value
    },
    {
      title: 'Approver Name / ID',
      dataIndex: 'approverName',
      key: 'approverName',
      width: 200,
      render: (name, record) => (
        <Space>
          <UserOutlined />
          <span>{name || 'Not Assigned'}</span>
          {record.approverEmployeeId && (
            <Text type="secondary" style={{ fontSize: '12px' }}>
              ({record.approverEmployeeId})
            </Text>
          )}
        </Space>
      )
    },
    {
      title: 'Approval Limit',
      dataIndex: 'approvalLimit',
      key: 'approvalLimit',
      width: 150,
      align: 'right',
      render: (amount) => (
        <Text strong style={{ color: '#1890ff' }}>
          {formatCurrency(amount)}
        </Text>
      ),
      sorter: (a, b) => (a.approvalLimit || 0) - (b.approvalLimit || 0)
    },
    {
      title: 'Status',
      dataIndex: 'isActive',
      key: 'isActive',
      width: 100,
      render: (isActive) => (
        <Tag color={isActive ? 'green' : 'red'}>
          {isActive ? 'Active' : 'Inactive'}
        </Tag>
      ),
      filters: [
        { text: 'Active', value: true },
        { text: 'Inactive', value: false }
      ],
      onFilter: (value, record) => record.isActive === value
    },
    {
      title: 'Actions',
      key: 'actions',
      width: 100,
      fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          <Tooltip title="Edit">
            <Button
              type="link"
              icon={<EditOutlined />}
              onClick={() => handleEdit(record)}
              size="small"
            />
          </Tooltip>
          <Popconfirm
            title="Delete this mapping?"
            description="This action cannot be undone."
            onConfirm={() => handleDelete(record.mappingId)}
            okText="Delete"
            cancelText="Cancel"
            okButtonProps={{ danger: true }}
          >
            <Tooltip title="Delete">
              <Button type="link" danger icon={<DeleteOutlined />} size="small" />
            </Tooltip>
          </Popconfirm>
        </Space>
      )
    }
  ];

  const tabItems = [
    {
      key: 'all',
      label: `All Mappings (${stats.total})`,
    },
    {
      key: 'deans',
      label: `Deans Only (${stats.deans})`,
    },
    {
      key: 'headsegs',
      label: `Head SEGs Only (${stats.headSegs})`,
    }
  ];

  return (
    <div style={{ padding: '24px' }}>
      <Card
        title={
          <Space>
            <TeamOutlined style={{ fontSize: '20px', color: '#722ed1' }} />
            <span>Department Approver Mappings</span>
          </Space>
        }
        extra={
          <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
            Add New Mapping
          </Button>
        }
      >
        {/* Info Alert */}
        <div style={{
          marginBottom: '24px',
          padding: '12px 16px',
          backgroundColor: '#f9f0ff',
          border: '1px solid #d3adf7',
          borderRadius: '6px'
        }}>
          <InfoCircleOutlined style={{ color: '#722ed1', marginRight: '8px' }} />
          <Text>
            Map departments to their respective Dean or Head SEG for Non-Computer category approvals.
            Dean can approve up to <Text strong>₹1,50,000</Text> and Head SEG up to <Text strong>₹1,00,000</Text>.
            Amounts exceeding these limits are automatically escalated to the Director.
          </Text>
        </div>

        {/* Stats */}
        <Row gutter={16} style={{ marginBottom: '24px' }}>
          <Col span={6}>
            <Card size="small" style={{ backgroundColor: '#f6ffed', borderColor: '#b7eb8f' }}>
              <Statistic title="Total Mappings" value={stats.total} />
            </Card>
          </Col>
          <Col span={6}>
            <Card size="small" style={{ backgroundColor: '#f9f0ff', borderColor: '#d3adf7' }}>
              <Statistic title="Deans" value={stats.deans} valueStyle={{ color: '#722ed1' }} />
            </Card>
          </Col>
          <Col span={6}>
            <Card size="small" style={{ backgroundColor: '#e6f7ff', borderColor: '#91d5ff' }}>
              <Statistic title="Head SEGs" value={stats.headSegs} valueStyle={{ color: '#1890ff' }} />
            </Card>
          </Col>
          <Col span={6}>
            <Card size="small" style={{ backgroundColor: '#fff7e6', borderColor: '#ffd591' }}>
              <Statistic title="Active" value={stats.active} valueStyle={{ color: '#fa8c16' }} />
            </Card>
          </Col>
        </Row>

        {/* Tabs */}
        <Tabs
          activeKey={activeTab}
          onChange={setActiveTab}
          items={tabItems}
          style={{ marginBottom: '16px' }}
        />

        {/* Filters */}
        <Row gutter={16} style={{ marginBottom: '16px' }}>
          <Col span={8}>
            <Select
              placeholder="Filter by Department"
              allowClear
              style={{ width: '100%' }}
              value={filterDepartment}
              onChange={setFilterDepartment}
              showSearch
              optionFilterProp="children"
            >
              {STANDARD_DEPARTMENTS.map(dept => (
                <Option key={dept} value={dept}>{dept}</Option>
              ))}
            </Select>
          </Col>
          <Col span={8}>
            <Input
              placeholder="Search by department or employee name..."
              prefix={<SearchOutlined />}
              value={searchText}
              onChange={(e) => setSearchText(e.target.value)}
              allowClear
            />
          </Col>
          <Col span={8}>
            <Space>
              <Button onClick={() => { setFilterDepartment(null); setSearchText(''); }}>
                Clear Filters
              </Button>
              <Button icon={<ReloadOutlined />} onClick={fetchMappings}>
                Refresh
              </Button>
            </Space>
          </Col>
        </Row>

        {/* Table */}
        <Table
          columns={columns}
          dataSource={filteredMappings}
          rowKey="mappingId"
          loading={loading}
          pagination={{
            pageSize: 10,
            showSizeChanger: true,
            showTotal: (total) => `Total ${total} records`
          }}
          scroll={{ x: 1000 }}
          size="middle"
        />
      </Card>

      {/* Add/Edit Modal */}
      <Modal
        title={editingRecord ? 'Edit Department Approver Mapping' : 'Add New Mapping'}
        open={modalVisible}
        onCancel={() => {
          setModalVisible(false);
          form.resetFields();
        }}
        footer={null}
        width={600}
        destroyOnClose
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={handleSubmit}
          initialValues={{ isActive: true, approvalLimit: DEFAULT_APPROVAL_LIMITS.DEAN }}
        >
          <Form.Item
            label="Department"
            name="departmentName"
            rules={[{ required: true, message: 'Please select a department' }]}
          >
            <Select
              placeholder="Select department"
              showSearch
              optionFilterProp="children"
            >
              {STANDARD_DEPARTMENTS.map(dept => (
                <Option key={dept} value={dept}>{dept}</Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            label="Approver Type"
            name="approverType"
            rules={[{ required: true, message: 'Please select approver type' }]}
          >
            <Select
              placeholder="Select approver type"
              onChange={handleApproverTypeChange}
            >
              {APPROVER_TYPE_OPTIONS.map(opt => (
                <Option key={opt.value} value={opt.value}>{opt.label}</Option>
              ))}
            </Select>
          </Form.Item>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label="Approver Employee (optional)"
                name="approverEmployeeId"
              >
                <Select
                  placeholder="Select employee"
                  showSearch
                  allowClear
                  optionFilterProp="children"
                  onChange={handleEmployeeSelect}
                >
                  {employees.map(emp => (
                    <Option key={emp.employeeId} value={emp.employeeId}>
                      {`${emp.firstName || ''} ${emp.lastName || ''}`.trim() || emp.employeeName} ({emp.employeeId})
                    </Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="Approver Name (optional)"
                name="approverName"
              >
                <Input placeholder="e.g. Dr. Sharma" />
              </Form.Item>
            </Col>
          </Row>

          <Form.Item
            label="Approval Limit"
            name="approvalLimit"
            rules={[{ required: true, message: 'Please enter approval limit' }]}
            tooltip="Dean: ₹1,50,000 | Head SEG: ₹1,00,000"
          >
            <InputNumber
              style={{ width: '100%' }}
              min={0}
              formatter={value => `₹ ${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')}
              parser={value => value.replace(/₹\s?|(,*)/g, '')}
              placeholder="Enter approval limit"
            />
          </Form.Item>

          <Form.Item
            label="Role"
            name="approverRoleId"
            tooltip="Optional: Link to a role in the system"
          >
            <Select
              placeholder="Select role (optional)"
              allowClear
              showSearch
              optionFilterProp="children"
            >
              {roles.map(role => (
                <Option key={role.roleId} value={role.roleId}>
                  {role.roleName}
                </Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            label="Remarks (optional)"
            name="remarks"
          >
            <Input.TextArea rows={2} placeholder="e.g. Dean handles Admin dept" />
          </Form.Item>

          <Form.Item
            label="Status"
            name="isActive"
            valuePropName="checked"
          >
            <Switch checkedChildren="Active" unCheckedChildren="Inactive" />
          </Form.Item>

          <Form.Item style={{ marginBottom: 0, textAlign: 'right' }}>
            <Space>
              <Button onClick={() => setModalVisible(false)}>Cancel</Button>
              <Button type="primary" htmlType="submit">
                {editingRecord ? 'Update' : 'Create'}
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default DepartmentApproverMapping;
