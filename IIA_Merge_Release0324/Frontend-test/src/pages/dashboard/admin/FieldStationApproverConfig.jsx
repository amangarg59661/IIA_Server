import React, { useState, useEffect } from 'react';
import {
  Card, Table, Button, Modal, Form, Input, Select, InputNumber,
  Space, message, Popconfirm, Tag, Switch, Typography, Row, Col,
  Tooltip, Tabs, Statistic, Alert
} from 'antd';
import {
  PlusOutlined, EditOutlined, DeleteOutlined, ReloadOutlined,
  SearchOutlined, InfoCircleOutlined, EnvironmentOutlined, UserSwitchOutlined
} from '@ant-design/icons';
import axios from 'axios';
import { FieldStationApproversService, INCHARGE_TYPE_OPTIONS, formatCurrency } from '../../../services/approvalWorkflowService';
import store from '../../../store';

const { Option } = Select;
const { Text } = Typography;

const FieldStationApproverConfig = () => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [approvers, setApprovers] = useState([]);
  const [filteredApprovers, setFilteredApprovers] = useState([]);
  const [modalVisible, setModalVisible] = useState(false);
  const [editingRecord, setEditingRecord] = useState(null);
  const [locations, setLocations] = useState([]);
  const [employees, setEmployees] = useState([]);
  const [roles, setRoles] = useState([]);

  // Filters
  const [activeTab, setActiveTab] = useState('all');
  const [filterStation, setFilterStation] = useState(null);
  const [searchText, setSearchText] = useState('');

  // Stats
  const [stats, setStats] = useState({ total: 0, engineers: 0, professors: 0, active: 0 });

  // Bangalore locations to exclude
  const BANGALORE_LOCATIONS = ['BANGALORE', 'Bangalore', 'bangalore', 'BENGALURU', 'Bengaluru'];

  useEffect(() => {
    fetchApprovers();
    fetchLocations();
    fetchEmployees();
    fetchRoles();
  }, []);

  useEffect(() => {
    applyFilters();
    calculateStats();
  }, [approvers, activeTab, filterStation, searchText]);

  const fetchApprovers = async () => {
    setLoading(true);
    try {
      const response = await FieldStationApproversService.getAll();
      const data = response.data?.responseData || response.data?.data || response.data || [];
      setApprovers(Array.isArray(data) ? data : []);
    } catch (error) {
      console.error('Error fetching field station approvers:', error);
      message.error('Failed to fetch field station approvers');
    } finally {
      setLoading(false);
    }
  };

  const fetchLocations = async () => {
    try {
      const response = await axios.get('/api/location-master');
      const data = response.data?.responseData || response.data || [];
      // Filter out Bangalore locations for field stations
      const fieldStations = Array.isArray(data)
        ? data.filter(loc => !BANGALORE_LOCATIONS.includes(loc.locationName))
        : [];
      setLocations(fieldStations);
    } catch (error) {
      console.error('Error fetching locations:', error);
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
    const total = approvers.length;
    const engineers = approvers.filter(a => a.inchargeType === 'ENGINEER_INCHARGE').length;
    const professors = approvers.filter(a => a.inchargeType === 'PROFESSOR_INCHARGE').length;
    const active = approvers.filter(a => a.isActive).length;
    setStats({ total, engineers, professors, active });
  };

  const applyFilters = () => {
    let filtered = [...approvers];

    // Tab filter
    if (activeTab === 'engineers') {
      filtered = filtered.filter(a => a.inchargeType === 'ENGINEER_INCHARGE');
    } else if (activeTab === 'professors') {
      filtered = filtered.filter(a => a.inchargeType === 'PROFESSOR_INCHARGE');
    }

    // Station filter
    if (filterStation) {
      filtered = filtered.filter(a => a.fieldStationName === filterStation);
    }

    // Search
    if (searchText) {
      const searchLower = searchText.toLowerCase();
      filtered = filtered.filter(a =>
        a.fieldStationName?.toLowerCase().includes(searchLower) ||
        a.inchargeEmployeeName?.toLowerCase().includes(searchLower)
      );
    }

    setFilteredApprovers(filtered);
  };

  const handleAdd = () => {
    setEditingRecord(null);
    form.resetFields();
    form.setFieldsValue({
      isActive: true
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

  const handleDelete = async (id) => {
    try {
      await FieldStationApproversService.delete(id);
      message.success('Field station approver deleted successfully');
      fetchApprovers();
    } catch (error) {
      message.error(error.response?.data?.message || 'Failed to delete approver');
    }
  };

  const handleEmployeeSelect = (employeeId) => {
    const employee = employees.find(e => e.employeeId === employeeId);
    if (employee) {
      form.setFieldsValue({
        inchargeEmployeeName: `${employee.firstName || ''} ${employee.lastName || ''}`.trim() || employee.employeeName
      });
    }
  };

  const handleSubmit = async (values) => {
    try {
      // Get role name if role is selected
      const selectedRole = roles.find(r => r.roleId === values.inchargeRoleId);

      const payload = {
        ...values,
        roleName: selectedRole?.roleName || (values.inchargeType === 'ENGINEER_INCHARGE' ? 'Engineer In-Charge' : 'Professor In-Charge'),
        createdBy: String(store.getState().auth?.userId || 'admin'),
        updatedBy: String(store.getState().auth?.userId || 'admin')
      };

      if (editingRecord) {
        await FieldStationApproversService.update(editingRecord.id, payload);
        message.success('Field station approver updated successfully');
      } else {
        await FieldStationApproversService.create(payload);
        message.success('Field station approver created successfully');
      }

      setModalVisible(false);
      form.resetFields();
      fetchApprovers();
    } catch (error) {
      message.error(error.response?.data?.message || 'Failed to save approver');
    }
  };

  const columns = [
    {
      title: 'Field Station',
      dataIndex: 'fieldStationName',
      key: 'fieldStationName',
      width: 180,
      sorter: (a, b) => (a.fieldStationName || '').localeCompare(b.fieldStationName || ''),
      render: (text) => (
        <Space>
          <EnvironmentOutlined style={{ color: '#fa8c16' }} />
          <Text strong>{text}</Text>
        </Space>
      )
    },
    {
      title: 'In-Charge Type',
      dataIndex: 'inchargeType',
      key: 'inchargeType',
      width: 180,
      render: (type) => {
        const colorMap = {
          'ENGINEER_INCHARGE': 'blue',
          'PROFESSOR_INCHARGE': 'purple'
        };
        const labelMap = {
          'ENGINEER_INCHARGE': 'Engineer In-Charge',
          'PROFESSOR_INCHARGE': 'Professor In-Charge'
        };
        return <Tag color={colorMap[type]}>{labelMap[type] || type}</Tag>;
      },
      filters: INCHARGE_TYPE_OPTIONS.map(opt => ({ text: opt.label, value: opt.value })),
      onFilter: (value, record) => record.inchargeType === value
    },
    {
      title: 'In-Charge Employee',
      dataIndex: 'inchargeEmployeeName',
      key: 'inchargeEmployeeName',
      width: 200,
      render: (name, record) => (
        <Space>
          <UserSwitchOutlined />
          <span>{name || 'Not Assigned'}</span>
          {record.inchargeEmployeeId && (
            <Text type="secondary" style={{ fontSize: '12px' }}>
              ({record.inchargeEmployeeId})
            </Text>
          )}
        </Space>
      )
    },
    {
      title: 'Role',
      dataIndex: 'roleName',
      key: 'roleName',
      width: 150,
      render: (text) => text || <Text type="secondary">-</Text>
    },
    {
      title: 'Approval Limit',
      dataIndex: 'approvalLimit',
      key: 'approvalLimit',
      width: 130,
      align: 'right',
      render: (amount) => amount ? formatCurrency(amount) : <Text type="secondary">Pass-through</Text>
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
            title="Delete this field station approver?"
            description="This action cannot be undone."
            onConfirm={() => handleDelete(record.id)}
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
      label: `All In-Charges (${stats.total})`,
    },
    {
      key: 'engineers',
      label: `Engineer In-Charges (${stats.engineers})`,
    },
    {
      key: 'professors',
      label: `Professor In-Charges (${stats.professors})`,
    }
  ];

  // Get unique field stations from approvers
  const uniqueStations = [...new Set(approvers.map(a => a.fieldStationName))].filter(Boolean);

  return (
    <div style={{ padding: '24px' }}>
      <Card
        title={
          <Space>
            <EnvironmentOutlined style={{ fontSize: '20px', color: '#fa8c16' }} />
            <span>Field Station In-Charge Configuration</span>
          </Space>
        }
        extra={
          <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
            Add New In-Charge
          </Button>
        }
      >
        {/* Info Alert */}
        <Alert
          message="Field Station Approval Configuration"
          description={
            <div>
              <p style={{ marginBottom: '8px' }}>
                Configure Engineer In-Charge and Professor In-Charge for <Text strong>non-Bangalore locations</Text>.
                These approvers review indents before they proceed to the Administrative Officer.
              </p>
              <ul style={{ marginBottom: 0, paddingLeft: '20px' }}>
                <li><Text strong>Engineer In-Charge</Text>: Technical review authority for field stations</li>
                <li><Text strong>Professor In-Charge</Text>: Academic/research review authority (new role with same responsibilities)</li>
              </ul>
            </div>
          }
          type="info"
          showIcon
          icon={<InfoCircleOutlined />}
          style={{ marginBottom: '24px' }}
        />

        {/* Stats */}
        <Row gutter={16} style={{ marginBottom: '24px' }}>
          <Col span={6}>
            <Card size="small" style={{ backgroundColor: '#fff7e6', borderColor: '#ffd591' }}>
              <Statistic
                title="Total In-Charges"
                value={stats.total}
                prefix={<EnvironmentOutlined />}
              />
            </Card>
          </Col>
          <Col span={6}>
            <Card size="small" style={{ backgroundColor: '#e6f7ff', borderColor: '#91d5ff' }}>
              <Statistic
                title="Engineer In-Charges"
                value={stats.engineers}
                valueStyle={{ color: '#1890ff' }}
              />
            </Card>
          </Col>
          <Col span={6}>
            <Card size="small" style={{ backgroundColor: '#f9f0ff', borderColor: '#d3adf7' }}>
              <Statistic
                title="Professor In-Charges"
                value={stats.professors}
                valueStyle={{ color: '#722ed1' }}
              />
            </Card>
          </Col>
          <Col span={6}>
            <Card size="small" style={{ backgroundColor: '#f6ffed', borderColor: '#b7eb8f' }}>
              <Statistic
                title="Active"
                value={stats.active}
                valueStyle={{ color: '#52c41a' }}
              />
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
              placeholder="Filter by Field Station"
              allowClear
              style={{ width: '100%' }}
              value={filterStation}
              onChange={setFilterStation}
              showSearch
              optionFilterProp="children"
            >
              {uniqueStations.map(station => (
                <Option key={station} value={station}>{station}</Option>
              ))}
            </Select>
          </Col>
          <Col span={8}>
            <Input
              placeholder="Search by station or employee name..."
              prefix={<SearchOutlined />}
              value={searchText}
              onChange={(e) => setSearchText(e.target.value)}
              allowClear
            />
          </Col>
          <Col span={8}>
            <Space>
              <Button onClick={() => { setFilterStation(null); setSearchText(''); }}>
                Clear Filters
              </Button>
              <Button icon={<ReloadOutlined />} onClick={fetchApprovers}>
                Refresh
              </Button>
            </Space>
          </Col>
        </Row>

        {/* Table */}
        <Table
          columns={columns}
          dataSource={filteredApprovers}
          rowKey="id"
          loading={loading}
          pagination={{
            pageSize: 10,
            showSizeChanger: true,
            showTotal: (total) => `Total ${total} records`
          }}
          scroll={{ x: 1100 }}
          size="middle"
        />
      </Card>

      {/* Add/Edit Modal */}
      <Modal
        title={editingRecord ? 'Edit Field Station In-Charge' : 'Add New In-Charge'}
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
          initialValues={{ isActive: true }}
        >
          <Form.Item
            label="Field Station"
            name="fieldStationName"
            rules={[{ required: true, message: 'Please select a field station' }]}
            tooltip="Only non-Bangalore locations are shown"
          >
            <Select
              placeholder="Select field station (non-Bangalore)"
              showSearch
              optionFilterProp="children"
            >
              {locations.map(loc => (
                <Option key={loc.locationId || loc.locationName} value={loc.locationName}>
                  {loc.locationName}
                </Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            label="In-Charge Type"
            name="inchargeType"
            rules={[{ required: true, message: 'Please select in-charge type' }]}
          >
            <Select placeholder="Select in-charge type">
              {INCHARGE_TYPE_OPTIONS.map(opt => (
                <Option key={opt.value} value={opt.value}>{opt.label}</Option>
              ))}
            </Select>
          </Form.Item>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label="In-Charge Employee"
                name="inchargeEmployeeId"
                rules={[{ required: true, message: 'Please select an employee' }]}
              >
                <Select
                  placeholder="Select employee"
                  showSearch
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
                label="Employee Name"
                name="inchargeEmployeeName"
              >
                <Input disabled placeholder="Auto-filled from employee selection" />
              </Form.Item>
            </Col>
          </Row>

          <Form.Item
            label="Role (Optional)"
            name="inchargeRoleId"
            tooltip="Link to a role in the system"
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
            label="Approval Limit (Optional)"
            name="approvalLimit"
            tooltip="Leave empty for pass-through approval (no limit)"
          >
            <InputNumber
              style={{ width: '100%' }}
              min={0}
              formatter={value => value ? `₹ ${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',') : ''}
              parser={value => value.replace(/₹\s?|(,*)/g, '')}
              placeholder="Enter approval limit (optional)"
            />
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

export default FieldStationApproverConfig;
