// import React, { useState, useEffect } from 'react';
// import {
//   Card, Table, Button, Modal, Form, Input, Select, InputNumber,
//   Space, message, Popconfirm, Tag, Switch, Typography, Row, Col, Tooltip
// } from 'antd';
// import {
//   PlusOutlined, EditOutlined, DeleteOutlined, ReloadOutlined,
//   SearchOutlined, InfoCircleOutlined, DollarOutlined
// } from '@ant-design/icons';
// import axios from 'axios';
// import { ApprovalLimitsService, CATEGORY_OPTIONS, formatCurrency } from '../../../services/approvalWorkflowService';
// import store from '../../../store';

// const { Option } = Select;
// const { Title, Text } = Typography;

// const ApprovalLimitsConfig = () => {
//   const [form] = Form.useForm();
//   const [loading, setLoading] = useState(false);
//   const [approvalLimits, setApprovalLimits] = useState([]);
//   const [filteredLimits, setFilteredLimits] = useState([]);
//   const [modalVisible, setModalVisible] = useState(false);
//   const [editingRecord, setEditingRecord] = useState(null);
//   const [roles, setRoles] = useState([]);

//   // Filters
//   const [filterRole, setFilterRole] = useState(null);
//   const [filterCategory, setFilterCategory] = useState(null);
//   const [filterDepartment, setFilterDepartment] = useState(null);
//   const [searchText, setSearchText] = useState('');

//   useEffect(() => {
//     fetchApprovalLimits();
//     fetchRoles();
//   }, []);

//   useEffect(() => {
//     applyFilters();
//   }, [approvalLimits, filterRole, filterCategory, filterDepartment, searchText]);

//   const fetchApprovalLimits = async () => {
//     setLoading(true);
//     try {
//       const response = await ApprovalLimitsService.getAll();
//       const data = response.data?.responseData || response.data?.data || response.data || [];
//       setApprovalLimits(Array.isArray(data) ? data : []);
//     } catch (error) {
//       console.error('Error fetching approval limits:', error);
//       message.error('Failed to fetch approval limits');
//     } finally {
//       setLoading(false);
//     }
//   };

//   const fetchRoles = async () => {
//     try {
//       const response = await axios.get('/api/employee-department-master/roles');
//       const data = response.data?.responseData || response.data || [];
//       setRoles(Array.isArray(data) ? data : []);
//     } catch (error) {
//       console.error('Error fetching roles:', error);
//     }
//   };

//   const applyFilters = () => {
//     let filtered = [...approvalLimits];

//     if (filterRole) {
//       filtered = filtered.filter(item => item.roleName === filterRole);
//     }
//     if (filterCategory) {
//       filtered = filtered.filter(item => item.category === filterCategory);
//     }
//     if (filterDepartment) {
//       filtered = filtered.filter(item => item.departmentName === filterDepartment);
//     }
//     if (searchText) {
//       const searchLower = searchText.toLowerCase();
//       filtered = filtered.filter(item =>
//         item.roleName?.toLowerCase().includes(searchLower) ||
//         item.category?.toLowerCase().includes(searchLower) ||
//         item.departmentName?.toLowerCase().includes(searchLower)
//       );
//     }

//     setFilteredLimits(filtered);
//   };

//   const handleAdd = () => {
//     setEditingRecord(null);
//     form.resetFields();
//     form.setFieldsValue({
//       isActive: true,
//       priority: 1,
//       minAmount: 0
//     });
//     setModalVisible(true);
//   };

//   const handleEdit = (record) => {
//     setEditingRecord(record);
//     form.setFieldsValue({
//       ...record,
//       roleId: record.roleId,
//       escalationRoleId: record.escalationRoleId
//     });
//     setModalVisible(true);
//   };

//   const handleDelete = async (limitId) => {
//     try {
//       await ApprovalLimitsService.delete(limitId);
//       message.success('Approval limit deleted successfully');
//       fetchApprovalLimits();
//     } catch (error) {
//       message.error(error.response?.data?.message || 'Failed to delete approval limit');
//     }
//   };

//   const handleStatusChange = async (record, isActive) => {
//     try {
//       await ApprovalLimitsService.updateStatus(record.limitId, isActive, String(store.getState().auth?.userId || 'admin'));
//       message.success(`Approval limit ${isActive ? 'activated' : 'deactivated'} successfully`);
//       fetchApprovalLimits();
//     } catch (error) {
//       message.error('Failed to update status');
//     }
//   };

//   const handleSubmit = async (values) => {
//     try {
//       const selectedRole = roles.find(r => r.roleId === values.roleId);
//       const escalationRole = roles.find(r => r.roleId === values.escalationRoleId);

//       const payload = {
//         ...values,
//         roleName: selectedRole?.roleName || values.roleName,
//         escalationRoleName: escalationRole?.roleName || null,
//         createdBy: String(store.getState().auth?.userId || 'admin'),
//         updatedBy: String(store.getState().auth?.userId || 'admin')
//       };

//       if (editingRecord) {
//         await ApprovalLimitsService.update(editingRecord.limitId, payload);
//         message.success('Approval limit updated successfully');
//       } else {
//         await ApprovalLimitsService.create(payload);
//         message.success('Approval limit created successfully');
//       }

//       setModalVisible(false);
//       form.resetFields();
//       fetchApprovalLimits();
//     } catch (error) {
//       message.error(error.response?.data?.message || 'Failed to save approval limit');
//     }
//   };

//   const columns = [
//     {
//       title: 'Role',
//       dataIndex: 'roleName',
//       key: 'roleName',
//       width: 150,
//       render: (text) => <Tag color="blue">{text}</Tag>,
//       sorter: (a, b) => (a.roleName || '').localeCompare(b.roleName || '')
//     },
//     {
//       title: 'Category',
//       dataIndex: 'category',
//       key: 'category',
//       width: 130,
//       render: (category) => {
//         const colorMap = {
//           'COMPUTER': 'cyan',
//           'NON_COMPUTER': 'orange',
//           'PROJECT': 'purple',
//           'ALL': 'green'
//         };
//         return <Tag color={colorMap[category] || 'default'}>{category?.replace('_', ' ')}</Tag>;
//       }
//     },
//     {
//       title: 'Department',
//       dataIndex: 'departmentName',
//       key: 'departmentName',
//       width: 150,
//       render: (text) => text || <Text type="secondary">All Departments</Text>
//     },
//     {
//       title: 'Location',
//       dataIndex: 'location',
//       key: 'location',
//       width: 120,
//       render: (text) => text || <Text type="secondary">All Locations</Text>
//     },
//     {
//       title: 'Min Amount',
//       dataIndex: 'minAmount',
//       key: 'minAmount',
//       width: 120,
//       align: 'right',
//       render: (amount) => formatCurrency(amount),
//       sorter: (a, b) => (a.minAmount || 0) - (b.minAmount || 0)
//     },
//     {
//       title: 'Max Amount',
//       dataIndex: 'maxAmount',
//       key: 'maxAmount',
//       width: 120,
//       align: 'right',
//       render: (amount) => amount ? formatCurrency(amount) : <Text type="secondary">Unlimited</Text>,
//       sorter: (a, b) => (a.maxAmount || Infinity) - (b.maxAmount || Infinity)
//     },
//     {
//       title: 'Escalation To',
//       dataIndex: 'escalationRoleName',
//       key: 'escalationRoleName',
//       width: 130,
//       render: (text) => text ? <Tag color="red">{text}</Tag> : <Text type="secondary">-</Text>
//     },
//     {
//       title: 'Priority',
//       dataIndex: 'priority',
//       key: 'priority',
//       width: 80,
//       align: 'center',
//       sorter: (a, b) => (a.priority || 0) - (b.priority || 0)
//     },
//     {
//       title: 'Status',
//       dataIndex: 'isActive',
//       key: 'isActive',
//       width: 100,
//       render: (isActive, record) => (
//         <Switch
//           checked={isActive}
//           onChange={(checked) => handleStatusChange(record, checked)}
//           checkedChildren="Active"
//           unCheckedChildren="Inactive"
//           size="small"
//         />
//       )
//     },
//     {
//       title: 'Actions',
//       key: 'actions',
//       width: 100,
//       fixed: 'right',
//       render: (_, record) => (
//         <Space size="small">
//           <Tooltip title="Edit">
//             <Button
//               type="link"
//               icon={<EditOutlined />}
//               onClick={() => handleEdit(record)}
//               size="small"
//             />
//           </Tooltip>
//           <Popconfirm
//             title="Delete this approval limit?"
//             description="This action cannot be undone."
//             onConfirm={() => handleDelete(record.limitId)}
//             okText="Delete"
//             cancelText="Cancel"
//             okButtonProps={{ danger: true }}
//           >
//             <Tooltip title="Delete">
//               <Button type="link" danger icon={<DeleteOutlined />} size="small" />
//             </Tooltip>
//           </Popconfirm>
//         </Space>
//       )
//     }
//   ];

//   const clearFilters = () => {
//     setFilterRole(null);
//     setFilterCategory(null);
//     setFilterDepartment(null);
//     setSearchText('');
//   };

//   return (
//     <div style={{ padding: '24px' }}>
//       <Card
//         title={
//           <Space>
//             <DollarOutlined style={{ fontSize: '20px', color: '#1890ff' }} />
//             <span>Approval Limits Configuration</span>
//           </Space>
//         }
//         extra={
//           <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
//             Add New Limit
//           </Button>
//         }
//       >
//         {/* Info Alert */}
//         <div style={{
//           marginBottom: '24px',
//           padding: '12px 16px',
//           backgroundColor: '#e6f7ff',
//           border: '1px solid #91d5ff',
//           borderRadius: '6px'
//         }}>
//           <InfoCircleOutlined style={{ color: '#1890ff', marginRight: '8px' }} />
//           <Text>
//             Configure approval limits for each role based on category, department, and location.
//             When indent value exceeds the limit, it automatically escalates to the configured escalation role.
//           </Text>
//         </div>

//         {/* Filters */}
//         <Row gutter={16} style={{ marginBottom: '16px' }}>
//           <Col span={5}>
//             <Select
//               placeholder="Filter by Role"
//               allowClear
//               style={{ width: '100%' }}
//               value={filterRole}
//               onChange={setFilterRole}
//               showSearch
//               optionFilterProp="children"
//             >
//               {[...new Set(approvalLimits.map(a => a.roleName))].filter(Boolean).map(role => (
//                 <Option key={role} value={role}>{role}</Option>
//               ))}
//             </Select>
//           </Col>
//           <Col span={5}>
//             <Select
//               placeholder="Filter by Category"
//               allowClear
//               style={{ width: '100%' }}
//               value={filterCategory}
//               onChange={setFilterCategory}
//             >
//               {CATEGORY_OPTIONS.map(opt => (
//                 <Option key={opt.value} value={opt.value}>{opt.label}</Option>
//               ))}
//             </Select>
//           </Col>
//           <Col span={5}>
//             <Select
//               placeholder="Filter by Department"
//               allowClear
//               style={{ width: '100%' }}
//               value={filterDepartment}
//               onChange={setFilterDepartment}
//               showSearch
//               optionFilterProp="children"
//             >
//               {[...new Set(approvalLimits.map(a => a.departmentName))].filter(Boolean).map(dept => (
//                 <Option key={dept} value={dept}>{dept}</Option>
//               ))}
//             </Select>
//           </Col>
//           <Col span={5}>
//             <Input
//               placeholder="Search..."
//               prefix={<SearchOutlined />}
//               value={searchText}
//               onChange={(e) => setSearchText(e.target.value)}
//               allowClear
//             />
//           </Col>
//           <Col span={4}>
//             <Space>
//               <Button onClick={clearFilters}>Clear Filters</Button>
//               <Button icon={<ReloadOutlined />} onClick={fetchApprovalLimits}>Refresh</Button>
//             </Space>
//           </Col>
//         </Row>

//         {/* Summary */}
//         <div style={{ marginBottom: '16px' }}>
//           <Text type="secondary">
//             Showing {filteredLimits.length} of {approvalLimits.length} approval limits
//           </Text>
//         </div>

//         {/* Table */}
//         <Table
//           columns={columns}
//           dataSource={filteredLimits}
//           rowKey="limitId"
//           loading={loading}
//           pagination={{
//             pageSize: 10,
//             showSizeChanger: true,
//             showTotal: (total) => `Total ${total} records`
//           }}
//           scroll={{ x: 1300 }}
//           size="middle"
//         />
//       </Card>

//       {/* Add/Edit Modal */}
//       <Modal
//         title={editingRecord ? 'Edit Approval Limit' : 'Add New Approval Limit'}
//         open={modalVisible}
//         onCancel={() => {
//           setModalVisible(false);
//           form.resetFields();
//         }}
//         footer={null}
//         width={700}
//         destroyOnClose
//       >
//         <Form
//           form={form}
//           layout="vertical"
//           onFinish={handleSubmit}
//           initialValues={{ isActive: true, priority: 1, minAmount: 0 }}
//         >
//           <Row gutter={16}>
//             <Col span={12}>
//               <Form.Item
//                 label="Role"
//                 name="roleId"
//                 rules={[{ required: true, message: 'Please select a role' }]}
//               >
//                 <Select
//                   placeholder="Select role"
//                   showSearch
//                   optionFilterProp="children"
//                 >
//                   {roles.map(role => (
//                     <Option key={role.roleId} value={role.roleId}>
//                       {role.roleName}
//                     </Option>
//                   ))}
//                 </Select>
//               </Form.Item>
//             </Col>
//             <Col span={12}>
//               <Form.Item
//                 label="Category"
//                 name="category"
//                 rules={[{ required: true, message: 'Please select a category' }]}
//               >
//                 <Select placeholder="Select category">
//                   {CATEGORY_OPTIONS.map(opt => (
//                     <Option key={opt.value} value={opt.value}>{opt.label}</Option>
//                   ))}
//                 </Select>
//               </Form.Item>
//             </Col>
//           </Row>

//           <Row gutter={16}>
//             <Col span={12}>
//               <Form.Item
//                 label="Department (Optional)"
//                 name="departmentName"
//                 tooltip="Leave empty to apply to all departments"
//               >
//                 <Input placeholder="Enter department name e.g. Admin" allowClear />
//               </Form.Item>
//             </Col>
//             <Col span={12}>
//               <Form.Item
//                 label="Location (Optional)"
//                 name="location"
//                 tooltip="Leave empty to apply to all locations"
//               >
//                 <Input placeholder="Enter location e.g. Bangalore" allowClear />
//               </Form.Item>
//             </Col>
//           </Row>

//           <Row gutter={16}>
//             <Col span={12}>
//               <Form.Item
//                 label="Minimum Amount"
//                 name="minAmount"
//                 rules={[{ required: true, message: 'Please enter minimum amount' }]}
//               >
//                 <InputNumber
//                   style={{ width: '100%' }}
//                   min={0}
//                   formatter={value => `₹ ${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')}
//                   parser={value => value.replace(/₹\s?|(,*)/g, '')}
//                   placeholder="Enter minimum amount"
//                 />
//               </Form.Item>
//             </Col>
//             <Col span={12}>
//               <Form.Item
//                 label="Maximum Amount"
//                 name="maxAmount"
//                 tooltip="Leave empty for unlimited/budget-based approval"
//               >
//                 <InputNumber
//                   style={{ width: '100%' }}
//                   min={0}
//                   formatter={value => value ? `₹ ${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',') : ''}
//                   parser={value => value.replace(/₹\s?|(,*)/g, '')}
//                   placeholder="Enter max amount (empty = unlimited)"
//                 />
//               </Form.Item>
//             </Col>
//           </Row>

//           <Row gutter={16}>
//             <Col span={12}>
//               <Form.Item
//                 label="Escalation Role"
//                 name="escalationRoleId"
//                 tooltip="Role to escalate to when amount exceeds limit"
//               >
//                 <Select
//                   placeholder="Select escalation role (optional)"
//                   allowClear
//                   showSearch
//                   optionFilterProp="children"
//                 >
//                   {roles.map(role => (
//                     <Option key={role.roleId} value={role.roleId}>
//                       {role.roleName}
//                     </Option>
//                   ))}
//                 </Select>
//               </Form.Item>
//             </Col>
//             <Col span={12}>
//               <Form.Item
//                 label="Priority"
//                 name="priority"
//                 rules={[{ required: true, message: 'Please enter priority' }]}
//                 tooltip="Lower number = higher priority. Used when multiple limits match."
//               >
//                 <InputNumber
//                   style={{ width: '100%' }}
//                   min={1}
//                   max={100}
//                   placeholder="Enter priority (1 = highest)"
//                 />
//               </Form.Item>
//             </Col>
//           </Row>

//           <Form.Item
//             label="Status"
//             name="isActive"
//             valuePropName="checked"
//           >
//             <Switch checkedChildren="Active" unCheckedChildren="Inactive" />
//           </Form.Item>

//           <Form.Item style={{ marginBottom: 0, textAlign: 'right' }}>
//             <Space>
//               <Button onClick={() => setModalVisible(false)}>Cancel</Button>
//               <Button type="primary" htmlType="submit">
//                 {editingRecord ? 'Update' : 'Create'}
//               </Button>
//             </Space>
//           </Form.Item>
//         </Form>
//       </Modal>
//     </div>
//   );
// };

// export default ApprovalLimitsConfig;


import React, { useState, useEffect } from 'react';
import {
  Card, Table, Button, Modal, Form, Input, Select, InputNumber,
  Space, message, Popconfirm, Tag, Switch, Typography, Row, Col, Tooltip
} from 'antd';
import {
  PlusOutlined, EditOutlined, DeleteOutlined, ReloadOutlined,
  SearchOutlined, InfoCircleOutlined, DollarOutlined
} from '@ant-design/icons';
import axios from 'axios';
import { ApprovalLimitsService, CATEGORY_OPTIONS, formatCurrency } from '../../../services/approvalWorkflowService';
import store from '../../../store';

const { Option } = Select;
const { Title, Text } = Typography;

// Static workflow list — keep in sync with the same list in ApprovalWorkflow.jsx
const WORKFLOWS = [
  { id: 1, name: 'Indent Approval Workflow', key: 'INDENT' },
  { id: 2, name: 'Contingency Purchase Workflow', key: 'CP' },
  { id: 3, name: 'Purchase Order Workflow', key: 'PO' },
  { id: 4, name: 'Tender Approver Workflow', key: 'TENDER_APPROVER' },
  { id: 5, name: 'Service Order Workflow', key: 'SO' },
  { id: 7, name: 'Tender Evaluator Workflow', key: 'TENDER_EVALUATOR' },
  { id: 10, name: 'Payment Voucher Workflow', key: 'Payment' },
];

const ApprovalLimitsConfig = () => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [approvalLimits, setApprovalLimits] = useState([]);
  const [filteredLimits, setFilteredLimits] = useState([]);
  const [modalVisible, setModalVisible] = useState(false);
  const [editingRecord, setEditingRecord] = useState(null);
  const [roles, setRoles] = useState([]);

  // Filters
  const [selectedWorkflow, setSelectedWorkflow] = useState(null);
  const [filterRole, setFilterRole] = useState(null);
  const [filterCategory, setFilterCategory] = useState(null);
  const [filterDepartment, setFilterDepartment] = useState(null);
  const [searchText, setSearchText] = useState('');

  useEffect(() => {
    fetchRoles();
  }, []);

  // NOTE: no useEffect watching selectedWorkflow anymore.
  // Fetch fires only from handleWorkflowChange (explicit user action) —
  // nothing runs on page load, and nothing runs until a workflow is picked.

  useEffect(() => {
    applyFilters();
  }, [approvalLimits, filterRole, filterCategory, filterDepartment, searchText]);

  const fetchApprovalLimits = async (workflowId) => {
    if (!workflowId) return; // guard: backend requires workflowId, never call without it
    setLoading(true);
    try {
      const response = await ApprovalLimitsService.getAll(workflowId);
      const data = response.data?.responseData || response.data?.data || response.data || [];
      setApprovalLimits(Array.isArray(data) ? data : []);
    } catch (error) {
      console.error('Error fetching approval limits:', error);
      message.error('Failed to fetch approval limits');
    } finally {
      setLoading(false);
    }
  };

  const handleWorkflowChange = (workflowId) => {
    setSelectedWorkflow(workflowId);
    if (workflowId) {
      fetchApprovalLimits(workflowId);
    } else {
      setApprovalLimits([]); // cleared selection → empty table, no call
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

  const applyFilters = () => {
    let filtered = [...approvalLimits];

    if (filterRole) {
      filtered = filtered.filter(item => item.roleName === filterRole);
    }
    if (filterCategory) {
      filtered = filtered.filter(item => item.category === filterCategory);
    }
    if (filterDepartment) {
      filtered = filtered.filter(item => item.departmentName === filterDepartment);
    }
    if (searchText) {
      const searchLower = searchText.toLowerCase();
      filtered = filtered.filter(item =>
        item.roleName?.toLowerCase().includes(searchLower) ||
        item.category?.toLowerCase().includes(searchLower) ||
        item.departmentName?.toLowerCase().includes(searchLower)
      );
    }

    setFilteredLimits(filtered);
  };

  const handleAdd = () => {
    setEditingRecord(null);
    form.resetFields();
    form.setFieldsValue({
      isActive: true,
      priority: 1,
      minAmount: 0,
      workflowId: selectedWorkflow || undefined
    });
    setModalVisible(true);
  };

  const handleEdit = (record) => {
    setEditingRecord(record);
    form.setFieldsValue({
      ...record,
      roleId: record.roleId,
      escalationRoleId: record.escalationRoleId
    });
    setModalVisible(true);
  };

  const handleDelete = async (limitId) => {
    try {
      await ApprovalLimitsService.delete(limitId);
      message.success('Approval limit deleted successfully');
      fetchApprovalLimits(selectedWorkflow);
    } catch (error) {
      message.error(error.response?.data?.message || 'Failed to delete approval limit');
    }
  };

  const handleStatusChange = async (record, isActive) => {
    try {
      await ApprovalLimitsService.updateStatus(record.limitId, isActive, String(store.getState().auth?.userId || 'admin'));
      message.success(`Approval limit ${isActive ? 'activated' : 'deactivated'} successfully`);
      fetchApprovalLimits(selectedWorkflow);
    } catch (error) {
      message.error('Failed to update status');
    }
  };

  const handleSubmit = async (values) => {
    try {
      const selectedRole = roles.find(r => r.roleId === values.roleId);
      const escalationRole = roles.find(r => r.roleId === values.escalationRoleId);

      const payload = {
        ...values,
        roleName: selectedRole?.roleName || values.roleName,
        escalationRoleName: escalationRole?.roleName || null,
        createdBy: String(store.getState().auth?.userId || 'admin'),
        updatedBy: String(store.getState().auth?.userId || 'admin')
      };

      if (editingRecord) {
        await ApprovalLimitsService.update(editingRecord.limitId, payload);
        message.success('Approval limit updated successfully');
      } else {
        await ApprovalLimitsService.create(payload);
        message.success('Approval limit created successfully');
      }

      setModalVisible(false);
      form.resetFields();
      fetchApprovalLimits(selectedWorkflow);
    } catch (error) {
      message.error(error.response?.data?.message || 'Failed to save approval limit');
    }
  };

  const columns = [
    {
      title: 'Role',
      dataIndex: 'roleName',
      key: 'roleName',
      width: 150,
      render: (text) => <Tag color="blue">{text}</Tag>,
      sorter: (a, b) => (a.roleName || '').localeCompare(b.roleName || '')
    },
    {
      title: 'Category',
      dataIndex: 'category',
      key: 'category',
      width: 130,
      render: (category) => {
        const colorMap = {
          'COMPUTER': 'cyan',
          'NON_COMPUTER': 'orange',
          'PROJECT': 'purple',
          'ALL': 'green'
        };
        return <Tag color={colorMap[category] || 'default'}>{category?.replace('_', ' ')}</Tag>;
      }
    },
    {
      title: 'Department',
      dataIndex: 'departmentName',
      key: 'departmentName',
      width: 150,
      render: (text) => text || <Text type="secondary">All Departments</Text>
    },
    {
      title: 'Location',
      dataIndex: 'location',
      key: 'location',
      width: 120,
      render: (text) => text || <Text type="secondary">All Locations</Text>
    },
    {
      title: 'Min Amount',
      dataIndex: 'minAmount',
      key: 'minAmount',
      width: 120,
      align: 'right',
      render: (amount) => formatCurrency(amount),
      sorter: (a, b) => (a.minAmount || 0) - (b.minAmount || 0)
    },
    {
      title: 'Max Amount',
      dataIndex: 'maxAmount',
      key: 'maxAmount',
      width: 120,
      align: 'right',
      render: (amount) => amount ? formatCurrency(amount) : <Text type="secondary">Unlimited</Text>,
      sorter: (a, b) => (a.maxAmount || Infinity) - (b.maxAmount || Infinity)
    },
    {
      title: 'Escalation To',
      dataIndex: 'escalationRoleName',
      key: 'escalationRoleName',
      width: 130,
      render: (text) => text ? <Tag color="red">{text}</Tag> : <Text type="secondary">-</Text>
    },
    // {
    //   title: 'Priority',
    //   dataIndex: 'priority',
    //   key: 'priority',
    //   width: 80,
    //   align: 'center',
    //   sorter: (a, b) => (a.priority || 0) - (b.priority || 0)
    // },
    // {
    //   title: 'Status',
    //   dataIndex: 'isActive',
    //   key: 'isActive',
    //   width: 100,
    //   render: (isActive, record) => (
    //     <Switch
    //       checked={isActive}
    //       onChange={(checked) => handleStatusChange(record, checked)}
    //       checkedChildren="Active"
    //       unCheckedChildren="Inactive"
    //       size="small"
    //     />
    //   )
    // },
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
          {/* <Popconfirm
            title="Delete this approval limit?"
            description="This action cannot be undone."
            onConfirm={() => handleDelete(record.limitId)}
            okText="Delete"
            cancelText="Cancel"
            okButtonProps={{ danger: true }}
          >
            <Tooltip title="Delete">
              <Button type="link" danger icon={<DeleteOutlined />} size="small" />
            </Tooltip>
          </Popconfirm> */}
        </Space>
      )
    }
  ];

  const clearFilters = () => {
    setSelectedWorkflow(null);
    setApprovalLimits([]);
    setFilterRole(null);
    setFilterCategory(null);
    setFilterDepartment(null);
    setSearchText('');
  };

  return (
    <div style={{ padding: '24px' }}>
      <Card
        title={
          <Space>
            <DollarOutlined style={{ fontSize: '20px', color: '#1890ff' }} />
            <span>Approval Limits Configuration</span>
          </Space>
        }
        // extra={
        //   <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd} disabled={!selectedWorkflow}>
        //     Add New Limit
        //   </Button>
        // }
      >
        {/* Info Alert */}
        <div style={{
          marginBottom: '24px',
          padding: '12px 16px',
          backgroundColor: '#e6f7ff',
          border: '1px solid #91d5ff',
          borderRadius: '6px'
        }}>
          <InfoCircleOutlined style={{ color: '#1890ff', marginRight: '8px' }} />
          <Text>
            Select a workflow to view its approval limits. Configure limits for each role based on
            category, department, and location. When indent value exceeds the limit, it automatically
            escalates to the configured escalation role.
          </Text>
        </div>

        {/* Filters */}
        <Row gutter={16} style={{ marginBottom: '16px' }}>
          <Col span={4}>
            <Select
              placeholder="Select Workflow"
              allowClear
              style={{ width: '100%' }}
              value={selectedWorkflow}
              onChange={handleWorkflowChange}
            >
              {WORKFLOWS.map(wf => (
                <Option key={wf.id} value={wf.id}>{wf.name}</Option>
              ))}
            </Select>
          </Col>
          <Col span={4}>
            <Select
              placeholder="Filter by Role"
              allowClear
              style={{ width: '100%' }}
              value={filterRole}
              onChange={setFilterRole}
              showSearch
              optionFilterProp="children"
            >
              {[...new Set(approvalLimits.map(a => a.roleName))].filter(Boolean).map(role => (
                <Option key={role} value={role}>{role}</Option>
              ))}
            </Select>
          </Col>
          <Col span={4}>
            <Select
              placeholder="Filter by Category"
              allowClear
              style={{ width: '100%' }}
              value={filterCategory}
              onChange={setFilterCategory}
            >
              {CATEGORY_OPTIONS.map(opt => (
                <Option key={opt.value} value={opt.value}>{opt.label}</Option>
              ))}
            </Select>
          </Col>
          <Col span={4}>
            <Select
              placeholder="Filter by Department"
              allowClear
              style={{ width: '100%' }}
              value={filterDepartment}
              onChange={setFilterDepartment}
              showSearch
              optionFilterProp="children"
            >
              {[...new Set(approvalLimits.map(a => a.departmentName))].filter(Boolean).map(dept => (
                <Option key={dept} value={dept}>{dept}</Option>
              ))}
            </Select>
          </Col>
          <Col span={4}>
            <Input
              placeholder="Search..."
              prefix={<SearchOutlined />}
              value={searchText}
              onChange={(e) => setSearchText(e.target.value)}
              allowClear
            />
          </Col>
          <Button onClick={clearFilters}>Clear Filters</Button>
        </Row>
        <Row>
          <Col span={4}>
            <Space>
              
              <Button
                icon={<ReloadOutlined />}
                onClick={() => fetchApprovalLimits(selectedWorkflow)}
                disabled={!selectedWorkflow}
              >
                Refresh
              </Button>
            </Space>
          </Col>
        </Row>

        {/* Summary */}
        <div style={{ marginBottom: '16px' }}>
          <Text type="secondary">
            {selectedWorkflow
              ? `Showing ${filteredLimits.length} of ${approvalLimits.length} approval limits`
              : 'Select a workflow above to load approval limits'}
          </Text>
        </div>

        {/* Table */}
        <Table
          columns={columns}
          dataSource={filteredLimits}
          rowKey="limitId"
          loading={loading}
          pagination={{
            pageSize: 10,
            showSizeChanger: true,
            showTotal: (total) => `Total ${total} records`
          }}
          scroll={{ x: 1300 }}
          size="middle"
        />
      </Card>

      {/* Add/Edit Modal */}
      <Modal
        title={editingRecord ? 'Edit Approval Limit' : 'Add New Approval Limit'}
        open={modalVisible}
        onCancel={() => {
          setModalVisible(false);
          form.resetFields();
        }}
        footer={null}
        width={700}
        destroyOnClose
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={handleSubmit}
          initialValues={{ isActive: true, priority: 1, minAmount: 0 }}
        >
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label="Workflow"
                name="workflowId"
                rules={[{ required: true, message: 'Please select a workflow' }]}
              >
                <Select
                  placeholder="Select workflow"
                  showSearch
                  optionFilterProp="children"
                >
                  {WORKFLOWS.map(wf => (
                    <Option key={wf.id} value={wf.id}>{wf.name}</Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="Role"
                name="roleId"
                rules={[{ required: true, message: 'Please select a role' }]}
              >
                <Select
                  placeholder="Select role"
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
            </Col>
          </Row>

          <Row gutter={16}>
            {/* <Col span={12}>
              <Form.Item
                label="Category"
                name="category"
                rules={[{ required: true, message: 'Please select a category' }]}
              >
                <Select placeholder="Select category">
                  {CATEGORY_OPTIONS.map(opt => (
                    <Option key={opt.value} value={opt.value}>{opt.label}</Option>
                  ))}
                </Select>
              </Form.Item>
            </Col> */}
            {/* <Col span={12}>
              <Form.Item
                label="Department (Optional)"
                name="departmentName"
                tooltip="Leave empty to apply to all departments"
              >
                <Input placeholder="Enter department name e.g. Admin" allowClear />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label="Location (Optional)"
                name="location"
                tooltip="Leave empty to apply to all locations"
              >
                <Input placeholder="Enter location e.g. Bangalore" allowClear />
              </Form.Item>
            </Col> */}
            <Col span={12}>
              <Form.Item
                label="Minimum Amount"
                name="minAmount"
                rules={[{ required: true, message: 'Please enter minimum amount' }]}
              >
                <InputNumber
                  style={{ width: '100%' }}
                  min={0}
                  formatter={value => `₹ ${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')}
                  parser={value => value.replace(/₹\s?|(,*)/g, '')}
                  placeholder="Enter minimum amount"
                />
              </Form.Item>
            </Col>
          
            <Col span={12}>
              <Form.Item
                label="Maximum Amount"
                name="maxAmount"
                tooltip="Leave empty for unlimited/budget-based approval"
              >
                <InputNumber
                  style={{ width: '100%' }}
                  min={0}
                  formatter={value => value ? `₹ ${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',') : ''}
                  parser={value => value.replace(/₹\s?|(,*)/g, '')}
                  placeholder="Enter max amount (empty = unlimited)"
                />
              </Form.Item>
            </Col>
            {/* <Col span={12}>
              <Form.Item
                label="Escalation Role"
                name="escalationRoleId"
                tooltip="Role to escalate to when amount exceeds limit"
              >
                <Select
                  placeholder="Select escalation role (optional)"
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
            </Col> */}
          </Row>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label="Priority"
                name="priority"
                rules={[{ required: true, message: 'Please enter priority' }]}
                tooltip="Lower number = highest priority. Used when multiple limits match."
              >
                <InputNumber
                  style={{ width: '100%' }}
                  min={1}
                  max={100}s
                  placeholder="Enter priority (1 = highest)"
                />
              </Form.Item>
            </Col>
            {/* <Col span={12}>
              <Form.Item
                label="Status"
                name="isActive"
                valuePropName="checked"
              >
                <Switch checkedChildren="Active" unCheckedChildren="Inactive" />
              </Form.Item> */}
            {/* </Col> */}
          </Row>

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

export default ApprovalLimitsConfig;
