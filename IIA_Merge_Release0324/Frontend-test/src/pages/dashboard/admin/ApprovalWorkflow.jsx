import React, { useState, useEffect } from 'react';
import { Card, Select, Input, InputNumber, Button, Table, Space, message, Modal, Form, Tag, Popconfirm, Switch, Tooltip, Alert, Collapse } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, ReloadOutlined, SearchOutlined, SettingOutlined, InfoCircleOutlined } from '@ant-design/icons';
import axios from 'axios';

const { Option } = Select;
const { TextArea } = Input;
const { Panel } = Collapse;

const ApprovalWorkflow = () => {
  const [form] = Form.useForm();
  const [branchForm] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [workflows] = useState([
    // Modified by aman changed id values to match db 
    { id: 1, name: 'Indent Approval Workflow', key: 'INDENT' },
    { id: 2, name: 'Contingency Purchase Workflow', key: 'CP' },
    { id: 3, name: 'Purchase Order Workflow', key: 'PO' },
    { id: 4, name: 'Tender Approver Workflow', key: 'TENDER_APPROVER' },
    { id: 5 , name: 'Service Order Workflow' , key:'SO'},
    { id: 7, name: 'Tender Evaluator Workflow', key: 'TENDER_EVALUATOR' },
    {id:10 , name:'Payment Voucher Workflow', key:'Payment'},
  ]);
  // End
  const [branches, setBranches] = useState([]);
  const [approvers, setApprovers] = useState([]);
  const [roles, setRoles] = useState([]);
  const [selectedWorkflow, setSelectedWorkflow] = useState(null);
  const [selectedBranch, setSelectedBranch] = useState(null);
  const [searchText, setSearchText] = useState('');
  const [modalVisible, setModalVisible] = useState(false);
  const [branchModalVisible, setBranchModalVisible] = useState(false);
  const [editingApprover, setEditingApprover] = useState(null);
  const [editingBranch, setEditingBranch] = useState(null);
  const [activeTab, setActiveTab] = useState('approvers');

  const conditionTypes = [
    { value: 'DEFAULT', label: 'Default (No conditions)' },
    { value: 'AMOUNT', label: 'Amount-Based' },
    { value: 'CATEGORY', label: 'Category-Based (Computer/Non-Computer)' },
    { value: 'LOCATION', label: 'Location-Based (Bangalore/Non-Bangalore)' },
    { value: 'PROJECT', label: 'Project-Based (Under Project/Not)' },
    { value: 'MODE_OF_PROCUREMENT', label: 'Mode of Procurement Based' },
    { value: 'COMPOSITE', label: 'Composite (Multiple conditions)' },
    { value: 'AMOUNT_WITH_ROLE', label: 'Amount with Role' },
    { value: 'AMOUNT_WITH_PROJECT', label: 'Amount with Project' },
    { value: 'BID_TYPE', label: 'Bid Type' },
    { value: 'INDENT_COUNT', label: 'Indent Count' },
    { value: 'COMMITTEE', label: 'Committee-Based' }
  ];

  // Comprehensive configuration examples for each condition type
  const configExamples = {
    DEFAULT: {
      description: 'No configuration needed. This branch acts as a fallback when no other conditions match.',
      examples: [
        { label: 'Default (No Config)', config: null }
      ]
    },
    AMOUNT: {
      description: 'Route based on indent/order amount. You can set minimum and/or maximum amount thresholds.',
      examples: [
        { label: 'Amount Range (50K to 1L)', config: { minAmount: 50000, maxAmount: 100000 } },
        { label: 'Above 1 Lakh', config: { minAmount: 100000 } },
        { label: 'Below 50K', config: { maxAmount: 50000 } },
        { label: 'Above 5 Lakhs', config: { minAmount: 500000 } },
        { label: 'Between 1L to 5L', config: { minAmount: 100000, maxAmount: 500000 } }
      ]
    },
    CATEGORY: {
      description: 'Route based on material category type. Choose between COMPUTER or NON_COMPUTER categories.',
      examples: [
        { label: 'Computer Category', config: { materialCategory: 'COMPUTER' } },
        { label: 'Non-Computer Category', config: { materialCategory: 'NON_COMPUTER' } }
      ]
    },
    LOCATION: {
      description: 'Route based on consignee location. Use specific city names or NON_BANGALORE for other locations.',
      examples: [
        { label: 'Bangalore Location', config: { location: 'BANGALORE' } },
        { label: 'Non-Bangalore Location', config: { location: 'NON_BANGALORE' } },
        { label: 'Mumbai Location', config: { location: 'MUMBAI' } },
        { label: 'Delhi Location', config: { location: 'DELHI' } },
        { label: 'Chennai Location', config: { location: 'CHENNAI' } }
      ]
    },
    PROJECT: {
      description: 'Route based on whether the indent is under a project or not.',
      examples: [
        { label: 'Under Project', config: { projectBased: true } },
        { label: 'Not Under Project', config: { projectBased: false } }
      ]
    },
    COMPOSITE: {
      description: 'Combine multiple conditions (AND logic). All conditions must match for this branch to be selected.',
      examples: [
        { label: 'Project + Computer + Bangalore', config: { projectBased: true, materialCategory: 'COMPUTER', location: 'BANGALORE' } },
        { label: 'Project + Non-Computer', config: { projectBased: true, materialCategory: 'NON_COMPUTER' } },
        { label: 'Non-Project + Bangalore', config: { projectBased: false, location: 'BANGALORE' } },
        { label: 'Computer + Amount Above 1L', config: { materialCategory: 'COMPUTER', minAmount: 100000 } },
        { label: 'Project + Computer + Non-Bangalore', config: { projectBased: true, materialCategory: 'COMPUTER', location: 'NON_BANGALORE' } },
        { label: 'Non-Project + Non-Computer + Mumbai', config: { projectBased: false, materialCategory: 'NON_COMPUTER', location: 'MUMBAI' } }
      ]
    },
    AMOUNT_WITH_ROLE: {
      description: 'Different amount thresholds for different roles. Useful when approval limits vary by designation.',
      examples: [
        { label: 'Role-based Amount Limits', config: { role: ['Dean', 'Head SEG'], minAmountHeadSEG: 100000, minAmountDean: 150000 } },
        { label: 'Director Approval Above 10L', config: { role: ['Director'], minAmountDirector: 1000000 } },
        { label: 'Multiple Role Thresholds', config: { role: ['AO', 'Dean', 'Director'], minAmountAO: 50000, minAmountDean: 200000, minAmountDirector: 500000 } }
      ]
    },
    AMOUNT_WITH_PROJECT: {
      description: 'Combine amount conditions with project-based routing. Check if amount exceeds project sanction limit.',
      examples: [
        { label: 'Project Below Sanction Limit', config: { minAmount: 50000, projectBased: true, aboveProjectSanctionLimit: false } },
        { label: 'Project Above Sanction Limit', config: { minAmount: 50000, projectBased: true, aboveProjectSanctionLimit: true } },
        { label: 'Non-Project Amount Based', config: { minAmount: 100000, projectBased: false } }
      ]
    },
    BID_TYPE: {
      description: 'Route based on tender bid type and department.',
      examples: [
        { label: 'Double Bid - Purchase', config: { bidType: 'DOUBLE_BID', department: 'PURCHASE' } },
        { label: 'Single Bid - Purchase', config: { bidType: 'SINGLE_BID', department: 'PURCHASE' } },
        { label: 'Double Bid - Stores', config: { bidType: 'DOUBLE_BID', department: 'STORES' } },
        { label: 'Any Bid Type', config: { bidType: 'ANY' } }
      ]
    },
    INDENT_COUNT: {
      description: 'Route based on number of items/indents in the request.',
      examples: [
        { label: 'Single Item Indent', config: { indentCount: 1 } },
        { label: 'Multiple Items (2+)', config: { indentCount: 2, comparison: 'GTE' } },
        { label: 'Bulk Order (5+)', config: { indentCount: 5, comparison: 'GTE' } }
      ]
    },
    COMMITTEE: {
      description: 'Route to specific committee for approval.',
      examples: [
        { label: 'Techno-Financial Committee', config: { committee: 'TECHNO_FINANCIAL' } },
        { label: 'Purchase Committee', config: { committee: 'PURCHASE_COMMITTEE' } },
        { label: 'Computer Committee', config: { committee: 'COMPUTER_COMMITTEE' } },
        { label: 'Works Committee', config: { committee: 'WORKS_COMMITTEE' } }
      ]
    },
    MODE_OF_PROCUREMENT: {
      description: 'Route based on the selected mode of procurement. Use this for different approval flows based on how the procurement is done.',
      examples: [
        { label: 'GeM (Government e-Marketplace)', config: { modeOfProcurement: 'GEM' } },
        { label: 'Open Tender', config: { modeOfProcurement: 'OPEN_TENDER' } },
        { label: 'Limited Tender', config: { modeOfProcurement: 'LIMITED_TENDER' } },
        { label: 'Single Tender', config: { modeOfProcurement: 'SINGLE_TENDER' } },
        { label: 'Proprietary Purchase', config: { modeOfProcurement: 'PROPRIETARY' } },
        { label: 'Rate Contract', config: { modeOfProcurement: 'RATE_CONTRACT' } },
        { label: 'Direct Purchase', config: { modeOfProcurement: 'DIRECT_PURCHASE' } },
        { label: 'Emergency Purchase', config: { modeOfProcurement: 'EMERGENCY_PURCHASE' } },
        { label: 'GeM + Computer Category', config: { modeOfProcurement: 'GEM', materialCategory: 'COMPUTER' } },
        { label: 'Open Tender + Project', config: { modeOfProcurement: 'OPEN_TENDER', projectBased: true } }
      ]
    }
  };

  // Helper function to get example config as string for placeholder
  const getExamplePlaceholder = (conditionType) => {
    if (!conditionType || conditionType === 'DEFAULT') return 'No configuration needed for DEFAULT';
    const example = configExamples[conditionType]?.examples?.[0];
    return example?.config ? JSON.stringify(example.config, null, 2) : '{}';
  };

  useEffect(() => {
    fetchRoles();
  }, []);

  const fetchRoles = async () => {
    try {
      const response = await axios.get('/api/employee-department-master/roles');
      if (response.data.responseData) {
        setRoles(response.data.responseData);
      }
    } catch (error) {
      console.error('Error fetching roles:', error);
    }
  };

  const fetchBranches = async (workflowId) => {
    try {
      setLoading(true);
      const response = await axios.get(`/api/admin/approvers/workflows/${workflowId}/branches`);

      let branchData = [];
      if (response.data.status === 'success') {
        branchData = response.data.data || [];
      } else if (response.data.responseData) {
        branchData = response.data.responseData || [];
      } else if (Array.isArray(response.data)) {
        branchData = response.data;
      }

      setBranches(branchData);
      setSelectedBranch(null);
      setApprovers([]);

      if (branchData.length === 0) {
        message.info('No branches found for this workflow. Click "Manage Branches" to create one.');
      }
    } catch (error) {
      message.error('Failed to fetch branches');
      console.error('Fetch branches error:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleAddBranch = () => {
    if (!selectedWorkflow) {
      message.warning('Please select a workflow first');
      return;
    }
    setEditingBranch(null);
    branchForm.resetFields();
    branchForm.setFieldsValue({
      isActive: true,
      displayOrder: branches.length + 1,
      conditionType: 'DEFAULT'
    });
    setBranchModalVisible(true);
  };

  const handleEditBranch = (record) => {
    setEditingBranch(record);
    branchForm.setFieldsValue({
      branchCode: record.branchCode,
      branchName: record.branchName,
      branchDescription: record.branchDescription,
      conditionType: record.conditionType || 'DEFAULT',
      conditionConfig: record.conditionConfig,
      displayOrder: record.displayOrder,
      isActive: record.isActive
    });
    setBranchModalVisible(true);
  };

  const handleDeleteBranch = async (branchId) => {
    try {
      await axios.delete(`/api/admin/approvers/branches/${branchId}`);
      message.success('Branch deleted successfully');
      fetchBranches(selectedWorkflow);
    } catch (error) {
      message.error(error.response?.data?.message || 'Failed to delete branch');
    }
  };

  const handleSubmitBranch = async (values) => {
    try {
      let conditionConfig = values.conditionConfig || null;

      if (conditionConfig && values.conditionType !== 'DEFAULT') {
        try {
          JSON.parse(conditionConfig);
        } catch (e) {
          message.error('Invalid JSON format in Condition Config. Please check your syntax.');
          return;
        }
      }

      if (values.conditionType === 'DEFAULT') {
        conditionConfig = null;
      }

      // Feature 2: Frontend validation - Check for duplicate condition config
      const normalizeConfig = (config) => {
        if (!config) return null;
        try {
          const parsed = typeof config === 'string' ? JSON.parse(config) : config;
          return JSON.stringify(parsed, Object.keys(parsed).sort());
        } catch {
          return config;
        }
      };

      const newConfigNormalized = normalizeConfig(conditionConfig);
      const duplicateBranch = branches.find(branch => {
        // Skip the branch being edited
        if (editingBranch && branch.branchId === editingBranch.branchId) return false;
        const existingConfigNormalized = normalizeConfig(branch.conditionConfig);
        return existingConfigNormalized === newConfigNormalized && branch.conditionType === values.conditionType;
      });

      if (duplicateBranch) {
        message.error(`This condition already exists in branch "${duplicateBranch.branchName}". Please use different conditions.`);
        return;
      }

      const payload = {
        branchCode: values.branchCode,
        branchName: values.branchName,
        branchDescription: values.branchDescription,
        conditionType: values.conditionType || 'DEFAULT',
        conditionConfig: conditionConfig,
        displayOrder: values.displayOrder,
        isActive: values.isActive,
        createdBy: 'admin'
      };

      if (editingBranch) {
        await axios.put(`/api/admin/approvers/branches/${editingBranch.branchId}`, payload);
        message.success('Branch updated successfully');
      } else {
        await axios.post(`/api/admin/approvers/workflows/${selectedWorkflow}/branches`, payload);
        message.success('Branch created successfully');
      }

      setBranchModalVisible(false);
      branchForm.resetFields();
      fetchBranches(selectedWorkflow);
    } catch (error) {
      console.error('Branch submit error:', error);
      const errorMsg = error.response?.data?.message || 'Failed to save branch';
      // Feature 2: User-friendly duplicate config error
      if (errorMsg.includes('same condition configuration already exists')) {
        message.error('This condition already exists in another branch. Please use different conditions or modify the existing branch.');
      } else {
        message.error(errorMsg);
      }
    }
  };

  const fetchApprovers = async (workflowId, branchId) => {
    try {
      setLoading(true);
      const response = await axios.get(`/api/admin/approvers/workflow/${workflowId}/branch/${branchId}`);

      let approverData = [];
      if (response.data.status === 'success') {
        approverData = response.data.data || [];
      } else if (response.data.responseData) {
        approverData = response.data.responseData || [];
      } else if (Array.isArray(response.data)) {
        approverData = response.data;
      }

      setApprovers(approverData);
    } catch (error) {
      message.error('Failed to fetch approvers');
      console.error('Fetch approvers error:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleWorkflowChange = (workflowId) => {
    setSelectedWorkflow(workflowId);
    setActiveTab('approvers');
    fetchBranches(workflowId);
  };

  const handleBranchChange = (branchId) => {
    setSelectedBranch(branchId);
    fetchApprovers(selectedWorkflow, branchId);
  };

  // Feature 3: Auto-increment approval levels using backend API
  const handleAddNew = async () => {
    if (!selectedWorkflow || !selectedBranch) {
      message.warning('Please select a workflow and branch first');
      return;
    }
    setEditingApprover(null);
    form.resetFields();

    // Call backend to get next approval level and sequence
    let nextLevel = 1;
    let nextSequence = 1;
    try {
      const response = await axios.get(`/api/admin/approvers/workflow/${selectedWorkflow}/branch/${selectedBranch}/next-level`);
      if (response.data) {
        nextLevel = response.data.nextApprovalLevel || 1;
        nextSequence = response.data.nextApprovalSequence || 1;
      }
    } catch (error) {
      // Fallback to local calculation if API fails
      const usedLevels = approvers.map(a => a.approvalLevel).filter(l => l);
      const usedSequences = approvers.map(a => a.approvalSequence).filter(s => s);
      nextLevel = usedLevels.length > 0 ? Math.max(...usedLevels) + 1 : 1;
      nextSequence = usedSequences.length > 0 ? Math.max(...usedSequences) + 1 : 1;
    }

    form.setFieldsValue({
      status: 'Active',
      approvalLevel: nextLevel,
      approvalSequence: nextSequence,
      isParallelApproval: false,
      isMandatory: true
    });
    setModalVisible(true);
  };

  const handleEdit = (record) => {
    setEditingApprover(record);
    form.setFieldsValue({
      roleId: record.roleId,
      approvalLevel: record.approvalLevel,
      approvalSequence: record.approvalSequence,
      isParallelApproval: record.isParallelApproval || false,
      isMandatory: record.isMandatory !== undefined ? record.isMandatory : true,
      autoApproveHours: record.autoApproveHours || null,
      status: record.status,
      conditionCheckType: record.conditionCheckType || null
    });
    setModalVisible(true);
  };

  const handleDelete = async (approverId) => {
    try {
      await axios.delete(`/api/admin/approvers/${approverId}`);
      message.success('Approver deleted successfully');
      fetchApprovers(selectedWorkflow, selectedBranch);
    } catch (error) {
      message.error(error.response?.data?.message || 'Failed to delete approver');
    }
  };

  const handleStatusChange = async (approverId, newStatus) => {
    try {
      await axios.put(`/api/admin/approvers/${approverId}/status?status=${newStatus}&updatedBy=admin`);
      message.success(`Approver ${newStatus.toLowerCase()} successfully`);
      fetchApprovers(selectedWorkflow, selectedBranch);
    } catch (error) {
      message.error(error.response?.data?.message || 'Failed to update status');
    }
  };

  // Feature 3: Use with-shift endpoint to auto-shift existing levels when needed
  const handleSubmit = async (values) => {
    try {
      const selectedRole = roles.find(r => r.roleId === values.roleId);
      const payload = {
        workflowId: selectedWorkflow,
        branchId: selectedBranch,
        roleId: values.roleId,
        roleName: selectedRole?.roleName || '',
        approvalLevel: values.approvalLevel,
        approvalSequence: values.approvalSequence,
        isParallelApproval: values.isParallelApproval || false,
        isMandatory: values.isMandatory !== undefined ? values.isMandatory : true,
        autoApproveHours: values.autoApproveHours || null,
        status: values.status,
        conditionCheckType: values.conditionCheckType || null,
        createdBy: 'admin'
      };

      if (editingApprover) {
        await axios.put(`/api/admin/approvers/${editingApprover.approverId}`, payload);
        message.success('Approver updated successfully');
      } else {
        // Check if the level already exists - if so, use with-shift endpoint
        const levelExists = approvers.some(a => a.approvalLevel === values.approvalLevel);
        if (levelExists) {
          await axios.post('/api/admin/approvers/with-shift', payload);
          message.success('Approver added. Existing approvers shifted to next levels.');
        } else {
          await axios.post('/api/admin/approvers', payload);
          message.success('Approver created successfully');
        }
      }

      setModalVisible(false);
      form.resetFields();
      fetchApprovers(selectedWorkflow, selectedBranch);
    } catch (error) {
      message.error(error.response?.data?.message || 'Failed to save approver');
    }
  };

  const filteredApprovers = approvers.filter(
    (item) =>
      item.approverCode?.toLowerCase().includes(searchText.toLowerCase()) ||
      item.roleName?.toLowerCase().includes(searchText.toLowerCase())
  );

  const columns = [
    {
      title: 'Code',
      dataIndex: 'approverCode',
      key: 'approverCode',
      width: 150
    },
    {
      title: 'Role',
      dataIndex: 'roleName',
      key: 'roleName',
      width: 180,
      render: (roleName, record) => (
        <Space size={4} wrap>
          <Tag color="blue">{roleName}</Tag>
          {record.conditionCheckType && record.conditionCheckType !== 'NONE' && (
            <Tag color={
              record.conditionCheckType === 'DEPARTMENT_BASED' ? 'purple' :
              record.conditionCheckType === 'LIMIT_CHECK' ? 'volcano' :
              record.conditionCheckType === 'BUDGET_CHECK' ? 'gold' : 'default'
            }>
              {record.conditionCheckType === 'DEPARTMENT_BASED' ? 'Dept Based' :
               record.conditionCheckType === 'LIMIT_CHECK' ? 'Limit Check' :
               record.conditionCheckType === 'BUDGET_CHECK' ? 'Budget Check' :
               record.conditionCheckType}
            </Tag>
          )}
        </Space>
      )
    },
    {
      title: 'Level',
      dataIndex: 'approvalLevel',
      key: 'approvalLevel',
      width: 80,
      sorter: (a, b) => a.approvalLevel - b.approvalLevel,
      render: (level) => <Tag color="purple">L{level}</Tag>
    },
    {
      title: 'Sequence',
      dataIndex: 'approvalSequence',
      key: 'approvalSequence',
      width: 100,
      sorter: (a, b) => a.approvalSequence - b.approvalSequence
    },
    {
      title: 'Parallel',
      dataIndex: 'isParallelApproval',
      key: 'isParallelApproval',
      width: 100,
      render: (isParallel) =>
        isParallel ? <Tag color="cyan">Yes (OR)</Tag> : <Tag>No (AND)</Tag>
    },
    {
      title: 'Mandatory',
      dataIndex: 'isMandatory',
      key: 'isMandatory',
      width: 100,
      render: (isMandatory) =>
        isMandatory ? <Tag color="orange">Yes</Tag> : <Tag>No</Tag>
    },
    {
      title: 'Auto-Approve',
      dataIndex: 'autoApproveHours',
      key: 'autoApproveHours',
      width: 120,
      render: (hours) =>
        hours ? <Tag color="volcano">{hours} hrs</Tag> : <span style={{ color: '#999' }}>—</span>
    },
    {
      title: 'Status',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status, record) => (
        <Switch
          checked={status === 'Active'}
          onChange={(checked) => handleStatusChange(record.approverId, checked ? 'Active' : 'Inactive')}
          checkedChildren="Active"
          unCheckedChildren="Inactive"
        />
      )
    },
    {
      title: 'Actions',
      key: 'actions',
      width: 150,
      fixed: 'right',
      render: (_, record) => (
        <Space>
          <Button
            type="link"
            icon={<EditOutlined />}
            onClick={() => handleEdit(record)}
            size="small"
          />
          <Popconfirm
            title="Are you sure you want to delete this approver?"
            onConfirm={() => handleDelete(record.approverId)}
            okText="Yes"
            cancelText="No"
          >
            <Button type="link" danger icon={<DeleteOutlined />} size="small" />
          </Popconfirm>
        </Space>
      )
    }
  ];

  const branchColumns = [
    { title: 'Branch Code', dataIndex: 'branchCode', key: 'branchCode', width: 200 },
    { title: 'Branch Name', dataIndex: 'branchName', key: 'branchName', width: 250 },
    { title: 'Description', dataIndex: 'branchDescription', key: 'branchDescription', ellipsis: true },
    {
      title: 'Condition Type',
      dataIndex: 'conditionType',
      key: 'conditionType',
      width: 180,
      render: (type) => <Tag color="geekblue">{type || 'DEFAULT'}</Tag>
    },
    {
      title: 'Condition Config',
      dataIndex: 'conditionConfig',
      key: 'conditionConfig',
      width: 200,
      ellipsis: true,
      render: (config) => config ? (
        <Tooltip title={<pre style={{margin: 0}}>{JSON.stringify(JSON.parse(config), null, 2)}</pre>}>
          <code style={{fontSize: '11px'}}>{config.substring(0, 30)}...</code>
        </Tooltip>
      ) : '-'
    },
    {
      title: 'Order',
      dataIndex: 'displayOrder',
      key: 'displayOrder',
      width: 80,
      sorter: (a, b) => a.displayOrder - b.displayOrder
    },
    {
      title: 'Status',
      dataIndex: 'isActive',
      key: 'isActive',
      width: 100,
      render: (isActive) => <Tag color={isActive ? 'green' : 'red'}>{isActive ? 'Active' : 'Inactive'}</Tag>
    },
    {
      title: 'Actions',
      key: 'actions',
      width: 150,
      fixed: 'right',
      render: (_, record) => (
        <Space>
          <Button type="link" icon={<EditOutlined />} onClick={() => handleEditBranch(record)} size="small" />
          <Popconfirm
            title="Are you sure you want to delete this branch?"
            onConfirm={() => handleDeleteBranch(record.branchId)}
            okText="Yes"
            cancelText="No"
          >
            <Button type="link" danger icon={<DeleteOutlined />} size="small" />
          </Popconfirm>
        </Space>
      )
    }
  ];

  return (
    <div style={{ padding: '24px' }}>
      <Card title="Workflow & Approval Management" bordered={false}>
        <Alert
          message="Complete Workflow Configuration System"
          description="Configure workflows, branches (conditions), and multi-level approvers with parallel/sequential logic. All workflows support dynamic routing based on amount, category, location, and project conditions."
          type="info"
          showIcon
          icon={<InfoCircleOutlined />}
          style={{ marginBottom: '24px' }}
        />

        {/* Filters Section */}
        <div style={{ marginBottom: '24px' }}>
          <div style={{ display: 'flex', gap: '16px', flexWrap: 'wrap', alignItems: 'center' }}>
            <div style={{ flex: '1', minWidth: '200px' }}>
              <label style={{ display: 'block', marginBottom: '8px', fontWeight: 500 }}>Workflow</label>
              <Select
                style={{ width: '100%' }}
                placeholder="Select workflow"
                onChange={handleWorkflowChange}
                value={selectedWorkflow}
              >
                {workflows.map((workflow) => (
                  <Option key={workflow.id} value={workflow.id}>
                    {workflow.name}
                  </Option>
                ))}
              </Select>
            </div>

            <div style={{ flex: '1', minWidth: '200px' }}>
              <label style={{ display: 'block', marginBottom: '8px', fontWeight: 500 }}>Branch (Condition)</label>
              <div style={{ display: 'flex', gap: '8px' }}>
                <Select
                  style={{ flex: 1 }}
                  placeholder="Select branch"
                  onChange={handleBranchChange}
                  value={selectedBranch}
                  disabled={!selectedWorkflow}
                  loading={loading}
                >
                  {branches.map((branch) => (
                    <Option key={branch.branchId} value={branch.branchId}>
                      {branch.branchName}
                    </Option>
                  ))}
                </Select>
                <Button
                  icon={<SettingOutlined />}
                  onClick={() => setActiveTab('branches')}
                  disabled={!selectedWorkflow}
                  title="Manage Branches"
                >
                  Manage
                </Button>
              </div>
            </div>
          </div>
        </div>

        {/* Branch Management Tab */}
        {selectedWorkflow && activeTab === 'branches' && (
          <div style={{ marginBottom: '24px', padding: '20px', backgroundColor: '#f0f5ff', borderRadius: '8px', border: '1px solid #adc6ff' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
              <h3 style={{ margin: 0, color: '#1890ff' }}>
                <SettingOutlined /> Workflow Branches Configuration
              </h3>
              <Space>
                <Button onClick={() => setActiveTab('approvers')}>Back to Approvers</Button>
                <Button type="primary" icon={<PlusOutlined />} onClick={handleAddBranch}>
                  Add Branch
                </Button>
              </Space>
            </div>
            <Table
              columns={branchColumns}
              dataSource={branches}
              rowKey="branchId"
              loading={loading}
              pagination={false}
              scroll={{ x: 1200 }}
            />
          </div>
        )}

        {/* Search and Actions */}
        {selectedBranch && activeTab === 'approvers' && (
          <>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '16px', flexWrap: 'wrap', gap: '8px' }}>
              <Input
                placeholder="Search approvers..."
                prefix={<SearchOutlined />}
                style={{ width: '300px' }}
                value={searchText}
                onChange={(e) => setSearchText(e.target.value)}
              />
              <Space>
                <Button icon={<ReloadOutlined />} onClick={() => fetchApprovers(selectedWorkflow, selectedBranch)}>
                  Refresh
                </Button>
                <Button type="primary" icon={<PlusOutlined />} onClick={handleAddNew}>
                  Add Approver
                </Button>
              </Space>
            </div>

            <div style={{ marginBottom: '16px' }}>
              <span style={{ fontWeight: 500 }}>Showing {filteredApprovers.length} approvers</span>
              <span style={{ marginLeft: '16px', color: '#666' }}>
                (Sorted by Level → Sequence)
              </span>
            </div>

            <Table
              columns={columns}
              dataSource={filteredApprovers.sort((a, b) => {
                if (a.approvalLevel !== b.approvalLevel) return a.approvalLevel - b.approvalLevel;
                return a.approvalSequence - b.approvalSequence;
              })}
              rowKey="approverId"
              loading={loading}
              pagination={{
                pageSize: 20,
                showSizeChanger: true,
                showTotal: (total) => `Total ${total} approvers`
              }}
              scroll={{ x: 1100 }}
            />
          </>
        )}

        {!selectedBranch && activeTab === 'approvers' && (
          <div style={{ textAlign: 'center', padding: '60px', color: '#999', backgroundColor: '#fafafa', borderRadius: '8px' }}>
            {selectedWorkflow && branches.length === 0 ? (
              <div>
                <p style={{ fontSize: '16px', marginBottom: '16px' }}>No branches found for this workflow.</p>
                <Button type="primary" icon={<PlusOutlined />} onClick={handleAddBranch} size="large">
                  Create First Branch
                </Button>
              </div>
            ) : (
              <p style={{ fontSize: '16px' }}>Please select a workflow and branch to view/manage approvers</p>
            )}
          </div>
        )}
      </Card>

      {/* Add/Edit Approver Modal */}
      <Modal
        title={editingApprover ? 'Edit Approver' : 'Add New Approver'}
        open={modalVisible}
        onCancel={() => {
          setModalVisible(false);
          form.resetFields();
        }}
        footer={null}
        width={600}
      >
        <Form form={form} layout="vertical" onFinish={handleSubmit}>
          <Form.Item
            label="Approver Role"
            name="roleId"
            rules={[{ required: true, message: 'Please select a role' }]}
          >
            <Select placeholder="Select role" showSearch filterOption={(input, option) =>
              option.children.toLowerCase().includes(input.toLowerCase())
            }>
              {roles.map((role) => (
                <Option key={role.roleId} value={role.roleId}>
                  {role.roleName}
                </Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            label="Approval Level"
            name="approvalLevel"
            rules={[{ required: true, message: 'Please enter level' }]}
            tooltip="Level defines approval hierarchy (1, 2, 3, etc.). Lower numbers are processed first."
          >
            <Input type="number" placeholder="Enter level (1, 2, 3...)" min={1} />
          </Form.Item>

          <Form.Item
            label="Approval Sequence"
            name="approvalSequence"
            rules={[
              { required: true, message: 'Please enter sequence' },
              () => ({
                validator(_, value) {
                  if (editingApprover && value === editingApprover.approvalSequence) {
                    return Promise.resolve();
                  }
                  const usedSequences = approvers.map(a => a.approvalSequence);
                  if (usedSequences.includes(value)) {
                    return Promise.reject(new Error(`Sequence ${value} already used. Next available: ${Math.max(...usedSequences) + 1}`));
                  }
                  return Promise.resolve();
                },
              }),
            ]}
            tooltip="Unique sequence number for ordering approvers"
          >
            <Input type="number" placeholder="Enter sequence" min={1} />
          </Form.Item>

          <Form.Item
            label="Parallel Approval (OR Logic)"
            name="isParallelApproval"
            valuePropName="checked"
            tooltip="If enabled, any ONE approver at this level can approve. Otherwise, ALL approvers must approve (AND logic)."
          >
            <Switch
              checkedChildren="Yes (OR)"
              unCheckedChildren="No (AND)"
            />
          </Form.Item>

          <Form.Item
            label="Mandatory Approval"
            name="isMandatory"
            valuePropName="checked"
            tooltip="If enabled, this approver MUST approve. If disabled, this is an optional approval."
          >
            <Switch
              checkedChildren="Required"
              unCheckedChildren="Optional"
            />
          </Form.Item>

          <Form.Item
            label="Auto-Approve After (Hours)"
            name="autoApproveHours"
            tooltip="If set, the request will be auto-approved after the specified hours of inaction."
            rules={[
              {
                type: 'number',
                min: 1,
                message: 'Must be at least 1 hour',
                transform: (value) => value ? Number(value) : undefined
              }
            ]}
          >
            <InputNumber
              min={1}
              placeholder="e.g. 24"
              style={{ width: '100%' }}
            />
          </Form.Item>
          <div style={{ marginTop: '-16px', marginBottom: '16px', color: '#888', fontSize: '12px' }}>
            If set, the request will be automatically approved and forwarded to the next level if this approver does not act within the specified hours. Leave empty for manual-only approval.
          </div>

          <Form.Item
            label="Condition Type"
            name="conditionCheckType"
            tooltip="Controls how this approver is selected at runtime. 'Department Based' routes to Dean or Head SEG based on the indentor's department."
          >
            <Select placeholder="None (Default)" allowClear>
              <Option value="DEPARTMENT_BASED">Department Based (Dean / Head SEG)</Option>
              <Option value="LIMIT_CHECK">Amount Limit Check</Option>
              <Option value="BUDGET_CHECK">Budget Check</Option>
            </Select>
          </Form.Item>

          <Form.Item
            label="Status"
            name="status"
            rules={[{ required: true, message: 'Please select status' }]}
          >
            <Select>
              <Option value="Active">Active</Option>
              <Option value="Inactive">Inactive</Option>
            </Select>
          </Form.Item>

          <Form.Item style={{ marginBottom: 0, textAlign: 'right' }}>
            <Space>
              <Button onClick={() => setModalVisible(false)}>Cancel</Button>
              <Button type="primary" htmlType="submit">
                {editingApprover ? 'Update' : 'Add'} Approver
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Modal>

      {/* Add/Edit Branch Modal */}
      <Modal
        title={editingBranch ? 'Edit Branch' : 'Add New Branch'}
        open={branchModalVisible}
        onCancel={() => {
          setBranchModalVisible(false);
          branchForm.resetFields();
        }}
        footer={null}
        width={700}
      >
        <Form form={branchForm} layout="vertical" onFinish={handleSubmitBranch}>
          <Form.Item
            label="Branch Code"
            name="branchCode"
            rules={[{ required: true, message: 'Please enter branch code' }]}
            tooltip="Unique identifier (e.g., INDENT_PROJECT_COMPUTER_BANGALORE)"
          >
            <Input placeholder="INDENT_DEFAULT or INDENT_AMOUNT_50000" disabled={!!editingBranch} />
          </Form.Item>

          <Form.Item
            label="Branch Name"
            name="branchName"
            rules={[{ required: true, message: 'Please enter branch name' }]}
          >
            <Input placeholder="Under Project - Computer - Bangalore" />
          </Form.Item>

          <Form.Item
            label="Description"
            name="branchDescription"
            tooltip="Explain when this branch is used"
          >
            <TextArea rows={2} placeholder="For indents under project with computer category in Bangalore location" />
          </Form.Item>

          <Form.Item
            label="Condition Type"
            name="conditionType"
            tooltip="Type of routing condition"
          >
            <Select placeholder="Select condition type">
              {conditionTypes.map(ct => (
                <Option key={ct.value} value={ct.value}>{ct.label}</Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            label="Condition Configuration (JSON)"
            name="conditionConfig"
            tooltip="JSON configuration defining the conditions"
          >
            <TextArea
              rows={4}
              placeholder={getExamplePlaceholder(branchForm.getFieldValue('conditionType'))}
            />
          </Form.Item>

          {/* Dynamic Configuration Examples - Updates when condition type changes */}
          <Form.Item noStyle shouldUpdate={(prevValues, currentValues) => prevValues.conditionType !== currentValues.conditionType}>
            {({ getFieldValue }) => {
              const conditionType = getFieldValue('conditionType');
              const exampleData = configExamples[conditionType];

              if (!conditionType || conditionType === 'DEFAULT' || !exampleData) {
                return null;
              }

              return (
                <Collapse style={{ marginBottom: '16px' }} defaultActiveKey={['1']}>
                  <Panel header={<><InfoCircleOutlined /> View Configuration Examples for {conditionTypes.find(ct => ct.value === conditionType)?.label}</>} key="1">
                    <div style={{ marginBottom: '12px', padding: '8px', backgroundColor: '#e6f7ff', borderRadius: '4px', borderLeft: '3px solid #1890ff' }}>
                      <strong>Description:</strong> {exampleData.description}
                    </div>
                    <div style={{ maxHeight: '300px', overflow: 'auto' }}>
                      {exampleData.examples.map((example, index) => (
                        <div key={index} style={{ marginBottom: '12px', padding: '12px', backgroundColor: '#f5f5f5', borderRadius: '4px', border: '1px solid #d9d9d9' }}>
                          <div style={{ marginBottom: '8px', fontWeight: 500, color: '#1890ff' }}>
                            {index + 1}. {example.label}
                          </div>
                          <pre style={{ margin: 0, fontSize: '12px', backgroundColor: '#fff', padding: '8px', borderRadius: '4px', overflow: 'auto' }}>
                            {example.config ? JSON.stringify(example.config, null, 2) : 'null (no configuration needed)'}
                          </pre>
                          <Button
                            type="link"
                            size="small"
                            style={{ padding: 0, marginTop: '4px' }}
                            onClick={() => {
                              if (example.config) {
                                branchForm.setFieldsValue({ conditionConfig: JSON.stringify(example.config, null, 2) });
                                message.success(`Example "${example.label}" copied to configuration`);
                              }
                            }}
                          >
                            Use this example
                          </Button>
                        </div>
                      ))}
                    </div>
                  </Panel>
                </Collapse>
              );
            }}
          </Form.Item>

          <Form.Item
            label="Display Order"
            name="displayOrder"
            rules={[{ required: true, message: 'Please enter display order' }]}
            tooltip="Order in which branches are checked (lower = higher priority)"
          >
            <Input type="number" placeholder="1, 2, 3..." min={1} />
          </Form.Item>

          <Form.Item
            label="Status"
            name="isActive"
            valuePropName="checked"
          >
            <Switch checkedChildren="Active" unCheckedChildren="Inactive" />
          </Form.Item>

          <Alert
            message="💡 Configuration Tip"
            description="Start with a DEFAULT branch (no conditions) as fallback. Then add specific branches for special routing (AMOUNT, CATEGORY, COMPOSITE)."
            type="info"
            showIcon
            style={{ marginBottom: '16px' }}
          />

          <Form.Item style={{ marginBottom: 0, textAlign: 'right' }}>
            <Space>
              <Button onClick={() => setBranchModalVisible(false)}>Cancel</Button>
              <Button type="primary" htmlType="submit">
                {editingBranch ? 'Update' : 'Create'} Branch
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default ApprovalWorkflow;


// import React, { useState, useEffect } from 'react';
// import { Card, Select, Input, InputNumber, Button, Table, Space, message, Modal, Form, Tag, Popconfirm, Switch, Tooltip, Alert } from 'antd';
// import { PlusOutlined, EditOutlined, DeleteOutlined, ReloadOutlined, SearchOutlined, SettingOutlined, InfoCircleOutlined } from '@ant-design/icons';
// import axios from 'axios';
// import ConditionBuilder from './ConditionBuilder';

// const { Option } = Select;

// const ApprovalWorkflow = () => {
//   const [form] = Form.useForm();
//   const [branchForm] = Form.useForm();
//   const [loading, setLoading] = useState(false);
//   const [workflows] = useState([
//     // Modified by aman changed id values to match db 
//     { id: 1, name: 'Indent Approval Workflow', key: 'INDENT' },
//     { id: 2, name: 'Contingency Purchase Workflow', key: 'CP' },
//     { id: 3, name: 'Purchase Order Workflow', key: 'PO' },
//     { id: 4, name: 'Tender Approver Workflow', key: 'TENDER_APPROVER' },
//     { id: 5, name: 'Service Order Workflow', key: 'SO' },
//     { id: 7, name: 'Tender Evaluator Workflow', key: 'TENDER_EVALUATOR' },
//     { id: 10, name: 'Payment Voucher Workflow', key: 'Payment' },
//   ]);
//   // End
//   const [branches, setBranches] = useState([]);
//   const [approvers, setApprovers] = useState([]);
//   const [roles, setRoles] = useState([]);
//   const [selectedWorkflow, setSelectedWorkflow] = useState(null);
//   const [selectedBranch, setSelectedBranch] = useState(null);
//   const [searchText, setSearchText] = useState('');
//   const [modalVisible, setModalVisible] = useState(false);
//   const [branchModalVisible, setBranchModalVisible] = useState(false);
//   const [editingApprover, setEditingApprover] = useState(null);
//   const [editingBranch, setEditingBranch] = useState(null);
//   const [activeTab, setActiveTab] = useState('approvers');

//   const conditionTypes = [
//     { value: 'DEFAULT', label: 'Default (No conditions)' },
//     { value: 'AMOUNT', label: 'Amount-Based' },
//     { value: 'CATEGORY', label: 'Category-Based (Computer/Non-Computer)' },
//     { value: 'LOCATION', label: 'Location-Based (Bangalore/Non-Bangalore)' },
//     { value: 'PROJECT', label: 'Project-Based (Under Project/Not)' },
//     { value: 'MODE_OF_PROCUREMENT', label: 'Mode of Procurement Based' },
//     { value: 'COMPOSITE', label: 'Composite (Multiple conditions)' },
//     { value: 'AMOUNT_WITH_ROLE', label: 'Amount with Role' },
//     { value: 'AMOUNT_WITH_PROJECT', label: 'Amount with Project' },
//     { value: 'BID_TYPE', label: 'Bid Type' },
//     { value: 'INDENT_COUNT', label: 'Indent Count' },
//     { value: 'COMMITTEE', label: 'Committee-Based' },
//   ];

//   useEffect(() => {
//     fetchRoles();
//   }, []);

//   const fetchRoles = async () => {
//     try {
//       const response = await axios.get('/api/employee-department-master/roles');
//       if (response.data.responseData) {
//         setRoles(response.data.responseData);
//       }
//     } catch (error) {
//       console.error('Error fetching roles:', error);
//     }
//   };

//   const fetchBranches = async (workflowId) => {
//     try {
//       setLoading(true);
//       const response = await axios.get(`/api/admin/approvers/workflows/${workflowId}/branches`);

//       let branchData = [];
//       if (response.data.status === 'success') {
//         branchData = response.data.data || [];
//       } else if (response.data.responseData) {
//         branchData = response.data.responseData || [];
//       } else if (Array.isArray(response.data)) {
//         branchData = response.data;
//       }

//       setBranches(branchData);
//       setSelectedBranch(null);
//       setApprovers([]);

//       if (branchData.length === 0) {
//         message.info('No branches found for this workflow. Click "Manage Branches" to create one.');
//       }
//     } catch (error) {
//       message.error('Failed to fetch branches');
//       console.error('Fetch branches error:', error);
//     } finally {
//       setLoading(false);
//     }
//   };

//   const handleAddBranch = () => {
//     if (!selectedWorkflow) {
//       message.warning('Please select a workflow first');
//       return;
//     }
//     setEditingBranch(null);
//     branchForm.resetFields();
//     branchForm.setFieldsValue({
//       isActive: true,
//       displayOrder: branches.length + 1,
//       conditionType: 'COMPOSITE',
//     });
//     setBranchModalVisible(true);
//   };

//   const handleEditBranch = (record) => {
//     setEditingBranch(record);
//     branchForm.setFieldsValue({
//       branchCode: record.branchCode,
//       branchName: record.branchName,
//       branchDescription: record.branchDescription,
//       conditionType: record.conditionType || 'DEFAULT',
//       conditionConfig: record.conditionConfig,
//       displayOrder: record.displayOrder,
//       isActive: record.isActive,
//     });
//     setBranchModalVisible(true);
//   };

//   const handleDeleteBranch = async (branchId) => {
//     try {
//       await axios.delete(`/api/admin/approvers/branches/${branchId}`);
//       message.success('Branch deleted successfully');
//       fetchBranches(selectedWorkflow);
//     } catch (error) {
//       message.error(error.response?.data?.message || 'Failed to delete branch');
//     }
//   };

//   const handleSubmitBranch = async (values) => {
//     try {
//       let conditionConfig = values.conditionConfig || null;

//       // Validate JSON if present and not DEFAULT
//       if (conditionConfig && values.conditionType !== 'DEFAULT') {
//         try {
//           JSON.parse(conditionConfig);
//         } catch (e) {
//           message.error('Invalid condition configuration. Please check your inputs.');
//           return;
//         }
//       }

//       // if (values.conditionType === 'DEFAULT') {
//       //   conditionConfig = null;
//       // }

//       // Frontend validation - Check for duplicate condition config
//       const normalizeConfig = (config) => {
//         if (!config) return null;
//         try {
//           const parsed = typeof config === 'string' ? JSON.parse(config) : config;
//           return JSON.stringify(parsed, Object.keys(parsed).sort());
//         } catch {
//           return config;
//         }
//       };

//       const newConfigNormalized = normalizeConfig(conditionConfig);
//       const duplicateBranch = branches.find(branch => {
//         if (editingBranch && branch.branchId === editingBranch.branchId) return false;
//         const existingConfigNormalized = normalizeConfig(branch.conditionConfig);
//         return existingConfigNormalized === newConfigNormalized && branch.conditionType === values.conditionType;
//       });

//       if (duplicateBranch) {
//         message.error(`This condition already exists in branch "${duplicateBranch.branchName}". Please use different conditions.`);
//         return;
//       }

//       const payload = {
//         branchCode: values.branchCode,
//         branchName: values.branchName,
//         branchDescription: values.branchDescription,
//         conditionType: values.conditionType || 'DEFAULT',
//         conditionConfig: conditionConfig,
//         displayOrder: values.displayOrder,
//         isActive: values.isActive,
//         createdBy: 'admin',
//       };

//       if (editingBranch) {
//         await axios.put(`/api/admin/approvers/branches/${editingBranch.branchId}`, payload);
//         message.success('Branch updated successfully');
//       } else {
//         await axios.post(`/api/admin/approvers/workflows/${selectedWorkflow}/branches`, payload);
//         message.success('Branch created successfully');
//       }

//       setBranchModalVisible(false);
//       branchForm.resetFields();
//       fetchBranches(selectedWorkflow);
//     } catch (error) {
//       console.error('Branch submit error:', error);
//       const errorMsg = error.response?.data?.message || 'Failed to save branch';
//       if (errorMsg.includes('same condition configuration already exists')) {
//         message.error('This condition already exists in another branch. Please use different conditions or modify the existing branch.');
//       } else {
//         message.error(errorMsg);
//       }
//     }
//   };

//   const fetchApprovers = async (workflowId, branchId) => {
//     try {
//       setLoading(true);
//       const response = await axios.get(`/api/admin/approvers/workflow/${workflowId}/branch/${branchId}`);

//       let approverData = [];
//       if (response.data.status === 'success') {
//         approverData = response.data.data || [];
//       } else if (response.data.responseData) {
//         approverData = response.data.responseData || [];
//       } else if (Array.isArray(response.data)) {
//         approverData = response.data;
//       }

//       setApprovers(approverData);
//     } catch (error) {
//       message.error('Failed to fetch approvers');
//       console.error('Fetch approvers error:', error);
//     } finally {
//       setLoading(false);
//     }
//   };

//   const handleWorkflowChange = (workflowId) => {
//     setSelectedWorkflow(workflowId);
//     setActiveTab('approvers');
//     fetchBranches(workflowId);
//   };

//   const handleBranchChange = (branchId) => {
//     setSelectedBranch(branchId);
//     fetchApprovers(selectedWorkflow, branchId);
//   };

//   // Auto-increment approval levels using backend API
//   const handleAddNew = async () => {
//     if (!selectedWorkflow || !selectedBranch) {
//       message.warning('Please select a workflow and branch first');
//       return;
//     }
//     setEditingApprover(null);
//     form.resetFields();

//     let nextLevel = 1;
//     let nextSequence = 1;
//     try {
//       const response = await axios.get(`/api/admin/approvers/workflow/${selectedWorkflow}/branch/${selectedBranch}/next-level`);
//       if (response.data) {
//         nextLevel = response.data.nextApprovalLevel || 1;
//         nextSequence = response.data.nextApprovalSequence || 1;
//       }
//     } catch (error) {
//       const usedLevels = approvers.map(a => a.approvalLevel).filter(l => l);
//       const usedSequences = approvers.map(a => a.approvalSequence).filter(s => s);
//       nextLevel = usedLevels.length > 0 ? Math.max(...usedLevels) + 1 : 1;
//       nextSequence = usedSequences.length > 0 ? Math.max(...usedSequences) + 1 : 1;
//     }

//     form.setFieldsValue({
//       status: 'Active',
//       approvalLevel: nextLevel,
//       approvalSequence: nextSequence,
//       isParallelApproval: false,
//       isMandatory: true,
//     });
//     setModalVisible(true);
//   };

//   const handleEdit = (record) => {
//     setEditingApprover(record);
//     form.setFieldsValue({
//       roleId: record.roleId,
//       approvalLevel: record.approvalLevel,
//       approvalSequence: record.approvalSequence,
//       isParallelApproval: record.isParallelApproval || false,
//       isMandatory: record.isMandatory !== undefined ? record.isMandatory : true,
//       autoApproveHours: record.autoApproveHours || null,
//       status: record.status,
//       conditionCheckType: record.conditionCheckType || null,
//     });
//     setModalVisible(true);
//   };

//   const handleDelete = async (approverId) => {
//     try {
//       await axios.delete(`/api/admin/approvers/${approverId}`);
//       message.success('Approver deleted successfully');
//       fetchApprovers(selectedWorkflow, selectedBranch);
//     } catch (error) {
//       message.error(error.response?.data?.message || 'Failed to delete approver');
//     }
//   };

//   const handleStatusChange = async (approverId, newStatus) => {
//     try {
//       await axios.put(`/api/admin/approvers/${approverId}/status?status=${newStatus}&updatedBy=admin`);
//       message.success(`Approver ${newStatus.toLowerCase()} successfully`);
//       fetchApprovers(selectedWorkflow, selectedBranch);
//     } catch (error) {
//       message.error(error.response?.data?.message || 'Failed to update status');
//     }
//   };

//   const handleSubmit = async (values) => {
//     try {
//       const selectedRole = roles.find(r => r.roleId === values.roleId);
//       const payload = {
//         workflowId: selectedWorkflow,
//         branchId: selectedBranch,
//         roleId: values.roleId,
//         roleName: selectedRole?.roleName || '',
//         approvalLevel: values.approvalLevel,
//         approvalSequence: values.approvalSequence,
//         isParallelApproval: values.isParallelApproval || false,
//         isMandatory: values.isMandatory !== undefined ? values.isMandatory : true,
//         autoApproveHours: values.autoApproveHours || null,
//         status: values.status,
//         conditionCheckType: values.conditionCheckType || null,
//         createdBy: 'admin',
//       };

//       if (editingApprover) {
//         await axios.put(`/api/admin/approvers/${editingApprover.approverId}`, payload);
//         message.success('Approver updated successfully');
//       } else {
//         const levelExists = approvers.some(a => a.approvalLevel === values.approvalLevel);
//         if (levelExists) {
//           await axios.post('/api/admin/approvers/with-shift', payload);
//           message.success('Approver added. Existing approvers shifted to next levels.');
//         } else {
//           await axios.post('/api/admin/approvers', payload);
//           message.success('Approver created successfully');
//         }
//       }

//       setModalVisible(false);
//       form.resetFields();
//       fetchApprovers(selectedWorkflow, selectedBranch);
//     } catch (error) {
//       message.error(error.response?.data?.message || 'Failed to save approver');
//     }
//   };

//   const filteredApprovers = approvers.filter(
//     (item) =>
//       item.approverCode?.toLowerCase().includes(searchText.toLowerCase()) ||
//       item.roleName?.toLowerCase().includes(searchText.toLowerCase())
//   );

//   const columns = [
//     {
//       title: 'Code',
//       dataIndex: 'approverCode',
//       key: 'approverCode',
//       width: 150,
//     },
//     {
//       title: 'Role',
//       dataIndex: 'roleName',
//       key: 'roleName',
//       width: 180,
//       render: (roleName, record) => (
//         <Space size={4} wrap>
//           <Tag color="blue">{roleName}</Tag>
//           {record.conditionCheckType && record.conditionCheckType !== 'NONE' && (
//             <Tag color={
//               record.conditionCheckType === 'DEPARTMENT_BASED' ? 'purple' :
//               record.conditionCheckType === 'LIMIT_CHECK' ? 'volcano' :
//               record.conditionCheckType === 'BUDGET_CHECK' ? 'gold' : 'default'
//             }>
//               {record.conditionCheckType === 'DEPARTMENT_BASED' ? 'Dept Based' :
//                record.conditionCheckType === 'LIMIT_CHECK' ? 'Limit Check' :
//                record.conditionCheckType === 'BUDGET_CHECK' ? 'Budget Check' :
//                record.conditionCheckType}
//             </Tag>
//           )}
//         </Space>
//       ),
//     },
//     {
//       title: 'Level',
//       dataIndex: 'approvalLevel',
//       key: 'approvalLevel',
//       width: 80,
//       sorter: (a, b) => a.approvalLevel - b.approvalLevel,
//       render: (level) => <Tag color="purple">L{level}</Tag>,
//     },
//     {
//       title: 'Sequence',
//       dataIndex: 'approvalSequence',
//       key: 'approvalSequence',
//       width: 100,
//       sorter: (a, b) => a.approvalSequence - b.approvalSequence,
//     },
//     {
//       title: 'Parallel',
//       dataIndex: 'isParallelApproval',
//       key: 'isParallelApproval',
//       width: 100,
//       render: (isParallel) =>
//         isParallel ? <Tag color="cyan">Yes (OR)</Tag> : <Tag>No (AND)</Tag>,
//     },
//     {
//       title: 'Mandatory',
//       dataIndex: 'isMandatory',
//       key: 'isMandatory',
//       width: 100,
//       render: (isMandatory) =>
//         isMandatory ? <Tag color="orange">Yes</Tag> : <Tag>No</Tag>,
//     },
//     {
//       title: 'Auto-Approve',
//       dataIndex: 'autoApproveHours',
//       key: 'autoApproveHours',
//       width: 120,
//       render: (hours) =>
//         hours ? <Tag color="volcano">{hours} hrs</Tag> : <span style={{ color: '#999' }}>—</span>,
//     },
//     {
//       title: 'Status',
//       dataIndex: 'status',
//       key: 'status',
//       width: 100,
//       render: (status, record) => (
//         <Switch
//           checked={status === 'Active'}
//           onChange={(checked) => handleStatusChange(record.approverId, checked ? 'Active' : 'Inactive')}
//           checkedChildren="Active"
//           unCheckedChildren="Inactive"
//         />
//       ),
//     },
//     {
//       title: 'Actions',
//       key: 'actions',
//       width: 150,
//       fixed: 'right',
//       render: (_, record) => (
//         <Space>
//           <Button
//             type="link"
//             icon={<EditOutlined />}
//             onClick={() => handleEdit(record)}
//             size="small"
//           />
//           <Popconfirm
//             title="Are you sure you want to delete this approver?"
//             onConfirm={() => handleDelete(record.approverId)}
//             okText="Yes"
//             cancelText="No"
//           >
//             <Button type="link" danger icon={<DeleteOutlined />} size="small" />
//           </Popconfirm>
//         </Space>
//       ),
//     },
//   ];

//   const branchColumns = [
//     { title: 'Branch Code', dataIndex: 'branchCode', key: 'branchCode', width: 200 },
//     { title: 'Branch Name', dataIndex: 'branchName', key: 'branchName', width: 250 },
//     { title: 'Description', dataIndex: 'branchDescription', key: 'branchDescription', ellipsis: true },
//     {
//       title: 'Condition Type',
//       dataIndex: 'conditionType',
//       key: 'conditionType',
//       width: 180,
//       render: (type) => <Tag color="geekblue">{type || 'DEFAULT'}</Tag>,
//     },
//     {
//       title: 'Condition Config',
//       dataIndex: 'conditionConfig',
//       key: 'conditionConfig',
//       width: 200,
//       ellipsis: true,
//       render: (config) => config ? (
//         <Tooltip title={<pre style={{ margin: 0 }}>{JSON.stringify(JSON.parse(config), null, 2)}</pre>}>
//           <code style={{ fontSize: '11px' }}>{config.substring(0, 30)}...</code>
//         </Tooltip>
//       ) : '-',
//     },
//     {
//       title: 'Order',
//       dataIndex: 'displayOrder',
//       key: 'displayOrder',
//       width: 80,
//       sorter: (a, b) => a.displayOrder - b.displayOrder,
//     },
//     {
//       title: 'Status',
//       dataIndex: 'isActive',
//       key: 'isActive',
//       width: 100,
//       render: (isActive) => <Tag color={isActive ? 'green' : 'red'}>{isActive ? 'Active' : 'Inactive'}</Tag>,
//     },
//     {
//       title: 'Actions',
//       key: 'actions',
//       width: 150,
//       fixed: 'right',
//       render: (_, record) => (
//         <Space>
//           <Button type="link" icon={<EditOutlined />} onClick={() => handleEditBranch(record)} size="small" />
//           <Popconfirm
//             title="Are you sure you want to delete this branch?"
//             onConfirm={() => handleDeleteBranch(record.branchId)}
//             okText="Yes"
//             cancelText="No"
//           >
//             <Button type="link" danger icon={<DeleteOutlined />} size="small" />
//           </Popconfirm>
//         </Space>
//       ),
//     },
//   ];

//   return (
//     <div style={{ padding: '24px' }}>
//       <Card title="Workflow & Approval Management" bordered={false}>
//         <Alert
//           message="Complete Workflow Configuration System"
//           description="Configure workflows, branches (conditions), and multi-level approvers with parallel/sequential logic. All workflows support dynamic routing based on amount, category, location, and project conditions."
//           type="info"
//           showIcon
//           icon={<InfoCircleOutlined />}
//           style={{ marginBottom: '24px' }}
//         />

//         {/* Filters Section */}
//         <div style={{ marginBottom: '24px' }}>
//           <div style={{ display: 'flex', gap: '16px', flexWrap: 'wrap', alignItems: 'center' }}>
//             <div style={{ flex: '1', minWidth: '200px' }}>
//               <label style={{ display: 'block', marginBottom: '8px', fontWeight: 500 }}>Workflow</label>
//               <Select
//                 style={{ width: '100%' }}
//                 placeholder="Select workflow"
//                 onChange={handleWorkflowChange}
//                 value={selectedWorkflow}
//               >
//                 {workflows.map((workflow) => (
//                   <Option key={workflow.id} value={workflow.id}>
//                     {workflow.name}
//                   </Option>
//                 ))}
//               </Select>
//             </div>

//             <div style={{ flex: '1', minWidth: '200px' }}>
//               <label style={{ display: 'block', marginBottom: '8px', fontWeight: 500 }}>Branch (Condition)</label>
//               <div style={{ display: 'flex', gap: '8px' }}>
//                 <Select
//                   style={{ flex: 1 }}
//                   placeholder="Select branch"
//                   onChange={handleBranchChange}
//                   value={selectedBranch}
//                   disabled={!selectedWorkflow}
//                   loading={loading}
//                 >
//                   {branches.map((branch) => (
//                     <Option key={branch.branchId} value={branch.branchId}>
//                       {branch.branchName}
//                     </Option>
//                   ))}
//                 </Select>
//                 <Button
//                   icon={<SettingOutlined />}
//                   onClick={() => setActiveTab('branches')}
//                   disabled={!selectedWorkflow}
//                   title="Manage Branches"
//                 >
//                   Manage
//                 </Button>
//               </div>
//             </div>
//           </div>
//         </div>

//         {/* Branch Management Tab */}
//         {selectedWorkflow && activeTab === 'branches' && (
//           <div style={{ marginBottom: '24px', padding: '20px', backgroundColor: '#f0f5ff', borderRadius: '8px', border: '1px solid #adc6ff' }}>
//             <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
//               <h3 style={{ margin: 0, color: '#1890ff' }}>
//                 <SettingOutlined /> Workflow Branches Configuration
//               </h3>
//               <Space>
//                 <Button onClick={() => setActiveTab('approvers')}>Back to Approvers</Button>
//                 <Button type="primary" icon={<PlusOutlined />} onClick={handleAddBranch}>
//                   Add Branch
//                 </Button>
//               </Space>
//             </div>
//             <Table
//               columns={branchColumns}
//               dataSource={branches}
//               rowKey="branchId"
//               loading={loading}
//               pagination={false}
//               scroll={{ x: 1200 }}
//             />
//           </div>
//         )}

//         {/* Search and Actions */}
//         {selectedBranch && activeTab === 'approvers' && (
//           <>
//             <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '16px', flexWrap: 'wrap', gap: '8px' }}>
//               <Input
//                 placeholder="Search approvers..."
//                 prefix={<SearchOutlined />}
//                 style={{ width: '300px' }}
//                 value={searchText}
//                 onChange={(e) => setSearchText(e.target.value)}
//               />
//               <Space>
//                 <Button icon={<ReloadOutlined />} onClick={() => fetchApprovers(selectedWorkflow, selectedBranch)}>
//                   Refresh
//                 </Button>
//                 <Button type="primary" icon={<PlusOutlined />} onClick={handleAddNew}>
//                   Add Approver
//                 </Button>
//               </Space>
//             </div>

//             <div style={{ marginBottom: '16px' }}>
//               <span style={{ fontWeight: 500 }}>Showing {filteredApprovers.length} approvers</span>
//               <span style={{ marginLeft: '16px', color: '#666' }}>(Sorted by Level → Sequence)</span>
//             </div>

//             <Table
//               columns={columns}
//               dataSource={filteredApprovers.sort((a, b) => {
//                 if (a.approvalLevel !== b.approvalLevel) return a.approvalLevel - b.approvalLevel;
//                 return a.approvalSequence - b.approvalSequence;
//               })}
//               rowKey="approverId"
//               loading={loading}
//               pagination={{
//                 pageSize: 20,
//                 showSizeChanger: true,
//                 showTotal: (total) => `Total ${total} approvers`,
//               }}
//               scroll={{ x: 1100 }}
//             />
//           </>
//         )}

//         {!selectedBranch && activeTab === 'approvers' && (
//           <div style={{ textAlign: 'center', padding: '60px', color: '#999', backgroundColor: '#fafafa', borderRadius: '8px' }}>
//             {selectedWorkflow && branches.length === 0 ? (
//               <div>
//                 <p style={{ fontSize: '16px', marginBottom: '16px' }}>No branches found for this workflow.</p>
//                 <Button type="primary" icon={<PlusOutlined />} onClick={handleAddBranch} size="large">
//                   Create First Branch
//                 </Button>
//               </div>
//             ) : (
//               <p style={{ fontSize: '16px' }}>Please select a workflow and branch to view/manage approvers</p>
//             )}
//           </div>
//         )}
//       </Card>

//       {/* ─── Add/Edit Approver Modal ─────────────────────────────────────────── */}
//       <Modal
//         title={editingApprover ? 'Edit Approver' : 'Add New Approver'}
//         open={modalVisible}
//         onCancel={() => {
//           setModalVisible(false);
//           form.resetFields();
//         }}
//         footer={null}
//         width={600}
//       >
//         <Form form={form} layout="vertical" onFinish={handleSubmit}>
//           <Form.Item
//             label="Approver Role"
//             name="roleId"
//             rules={[{ required: true, message: 'Please select a role' }]}
//           >
//             <Select
//               placeholder="Select role"
//               showSearch
//               filterOption={(input, option) =>
//                 option.children.toLowerCase().includes(input.toLowerCase())
//               }
//             >
//               {roles.map((role) => (
//                 <Option key={role.roleId} value={role.roleId}>
//                   {role.roleName}
//                 </Option>
//               ))}
//             </Select>
//           </Form.Item>

//           <Form.Item
//             label="Approval Level"
//             name="approvalLevel"
//             rules={[{ required: true, message: 'Please enter level' }]}
//             tooltip="Level defines approval hierarchy (1, 2, 3, etc.). Lower numbers are processed first."
//           >
//             <Input type="number" placeholder="Enter level (1, 2, 3...)" min={1} />
//           </Form.Item>

//           <Form.Item
//             label="Approval Sequence"
//             name="approvalSequence"
//             rules={[
//               { required: true, message: 'Please enter sequence' },
//               () => ({
//                 validator(_, value) {
//                   if (editingApprover && value === editingApprover.approvalSequence) {
//                     return Promise.resolve();
//                   }
//                   const usedSequences = approvers.map(a => a.approvalSequence);
//                   if (usedSequences.includes(value)) {
//                     return Promise.reject(new Error(`Sequence ${value} already used. Next available: ${Math.max(...usedSequences) + 1}`));
//                   }
//                   return Promise.resolve();
//                 },
//               }),
//             ]}
//             tooltip="Unique sequence number for ordering approvers"
//           >
//             <Input type="number" placeholder="Enter sequence" min={1} />
//           </Form.Item>

//           <Form.Item
//             label="Parallel Approval (OR Logic)"
//             name="isParallelApproval"
//             valuePropName="checked"
//             tooltip="If enabled, any ONE approver at this level can approve. Otherwise, ALL approvers must approve (AND logic)."
//           >
//             <Switch checkedChildren="Yes (OR)" unCheckedChildren="No (AND)" />
//           </Form.Item>

//           <Form.Item
//             label="Mandatory Approval"
//             name="isMandatory"
//             valuePropName="checked"
//             tooltip="If enabled, this approver MUST approve. If disabled, this is an optional approval."
//           >
//             <Switch checkedChildren="Required" unCheckedChildren="Optional" />
//           </Form.Item>

//           <Form.Item
//             label="Auto-Approve After (Hours)"
//             name="autoApproveHours"
//             tooltip="If set, the request will be auto-approved after the specified hours of inaction."
//             rules={[
//               {
//                 type: 'number',
//                 min: 1,
//                 message: 'Must be at least 1 hour',
//                 transform: (value) => value ? Number(value) : undefined,
//               },
//             ]}
//           >
//             <InputNumber min={1} placeholder="e.g. 24" style={{ width: '100%' }} />
//           </Form.Item>
//           <div style={{ marginTop: '-16px', marginBottom: '16px', color: '#888', fontSize: '12px' }}>
//             If set, the request will be automatically approved and forwarded to the next level if this approver does not act within the specified hours. Leave empty for manual-only approval.
//           </div>

//           <Form.Item
//             label="Condition Type"
//             name="conditionCheckType"
//             tooltip="Controls how this approver is selected at runtime. 'Department Based' routes to Dean or Head SEG based on the indentor's department."
//           >
//             <Select placeholder="None (Default)" allowClear>
//               <Option value="DEPARTMENT_BASED">Department Based (Dean / Head SEG)</Option>
//               <Option value="LIMIT_CHECK">Amount Limit Check</Option>
//               <Option value="BUDGET_CHECK">Budget Check</Option>
//             </Select>
//           </Form.Item>

//           <Form.Item
//             label="Status"
//             name="status"
//             rules={[{ required: true, message: 'Please select status' }]}
//           >
//             <Select>
//               <Option value="Active">Active</Option>
//               <Option value="Inactive">Inactive</Option>
//             </Select>
//           </Form.Item>

//           <Form.Item style={{ marginBottom: 0, textAlign: 'right' }}>
//             <Space>
//               <Button onClick={() => setModalVisible(false)}>Cancel</Button>
//               <Button type="primary" htmlType="submit">
//                 {editingApprover ? 'Update' : 'Add'} Approver
//               </Button>
//             </Space>
//           </Form.Item>
//         </Form>
//       </Modal>

//       {/* ─── Add/Edit Branch Modal ───────────────────────────────────────────── */}
//       <Modal
//         title={editingBranch ? 'Edit Branch' : 'Add New Branch'}
//         open={branchModalVisible}
//         onCancel={() => {
//           setBranchModalVisible(false);
//           branchForm.resetFields();
//         }}
//         footer={null}
//         width={700}
//         destroyOnClose
//       >
//         <Form form={branchForm} layout="vertical" onFinish={handleSubmitBranch}>
//           <Form.Item
//             label="Branch Code"
//             name="branchCode"
//             rules={[{ required: true, message: 'Please enter branch code' }]}
//             tooltip="Unique identifier (e.g., INDENT_PROJECT_COMPUTER_BANGALORE)"
//           >
//             <Input
//               placeholder="INDENT_DEFAULT or INDENT_AMOUNT_50000"
//               disabled={!!editingBranch}
//             />
//           </Form.Item>

//           <Form.Item
//             label="Branch Name"
//             name="branchName"
//             rules={[{ required: true, message: 'Please enter branch name' }]}
//           >
//             <Input placeholder="Under Project - Computer - Bangalore" />
//           </Form.Item>

//           <Form.Item
//             label="Description"
//             name="branchDescription"
//             tooltip="Explain when this branch is used"
//           >
//             <Input.TextArea
//               rows={2}
//               placeholder="For indents under project with computer category in Bangalore location"
//             />
//           </Form.Item>

//           {/* Condition Type selector */}
//           {/* <Form.Item
//             label="Condition Type"
//             name="conditionType"
//             tooltip="Type of routing condition for this branch"
//           >
//             <Select placeholder="Select condition type">
//               {conditionTypes.map(ct => (
//                 <Option key={ct.value} value={ct.value}>{ct.label}</Option>
//               ))}
//             </Select>
//           </Form.Item> */}

//           {/* ── ConditionBuilder replaces the old JSON TextArea + Collapse examples ── */}
//           {/* <Form.Item
//             label="Condition Configuration"
//             name="conditionConfig"
//             tooltip="Select the conditions you want and fill in values. JSON is generated automatically."
//           >
//             {/*
//               shouldUpdate ensures ConditionBuilder re-renders when conditionType changes.
//               Form.Item with name="conditionConfig" wires value + onChange automatically.
//             */}
//             {/* <Form.Item
//               noStyle
//               shouldUpdate={(prev, curr) => prev.conditionType !== curr.conditionType}
//             >
//               {({ getFieldValue }) => (
//                 <ConditionBuilder conditionType={getFieldValue('conditionType')} />
//               )}
//             </Form.Item>
//           </Form.Item> */} 
//           <Form.Item
//   label="Condition Configuration"
//   name="conditionConfig"
//   tooltip="Check the conditions you want to apply. All active conditions must match (AND logic)."
// >
//   <ConditionBuilder />
// </Form.Item>
//           {/* ── End ConditionBuilder ─────────────────────────────────────────── */}

//           <Form.Item
//             label="Display Order"
//             name="displayOrder"
//             rules={[{ required: true, message: 'Please enter display order' }]}
//             tooltip="Order in which branches are checked (lower = higher priority)"
//           >
//             <Input type="number" placeholder="1, 2, 3..." min={1} />
//           </Form.Item>

//           <Form.Item
//             label="Status"
//             name="isActive"
//             valuePropName="checked"
//           >
//             <Switch checkedChildren="Active" unCheckedChildren="Inactive" />
//           </Form.Item>

//           <Alert
//             message="💡 Tip"
//             description="Start with a DEFAULT branch (no conditions) as fallback. Then add specific branches for special routing."
//             type="info"
//             showIcon
//             style={{ marginBottom: '16px' }}
//           />

//           <Form.Item style={{ marginBottom: 0, textAlign: 'right' }}>
//             <Space>
//               <Button onClick={() => setBranchModalVisible(false)}>Cancel</Button>
//               <Button type="primary" htmlType="submit">
//                 {editingBranch ? 'Update' : 'Create'} Branch
//               </Button>
//             </Space>
//           </Form.Item>
//         </Form>
//       </Modal>
//     </div>
//   );
// };

// export default ApprovalWorkflow;
