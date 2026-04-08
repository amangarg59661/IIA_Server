import React, { useState, useEffect } from 'react';
import { Card, Row, Col, Statistic, Input, Button, Table, Space, message, Modal, Form, Tag, Popconfirm, Select, DatePicker } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, ReloadOutlined, SearchOutlined, DollarOutlined   } from '@ant-design/icons';
import axios from 'axios';
import dayjs from 'dayjs';
import { useLOVValuesByFormName } from '../../../hooks/useLOVValues';

const { Option } = Select;

const BudgetManagement = () => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [budgets, setBudgets] = useState([]);
  const [summary, setSummary] = useState({
    totalAllocated: 0,
    totalSpent: 0,
    totalRemaining: 0
  });
  const [searchText, setSearchText] = useState('');
  const [modalVisible, setModalVisible] = useState(false);
  const [editingBudget, setEditingBudget] = useState(null);

  // ✅ Fetch Budget Status LOV values dynamically
  const { lovValues: statusLOVs, loading: statusLoading } = useLOVValuesByFormName('Budget', 'status');

  useEffect(() => {
    fetchBudgets();
    fetchSummary();
  }, []);

  const fetchBudgets = async () => {
    try {
      setLoading(true);
      const response = await axios.get('/api/admin/budget');
      console.log('Budget API Response:', response.data);

      // Handle multiple response formats
      let budgetData = [];
      if (response.data.status === 'success') {
        budgetData = response.data.data || [];
      } else if (response.data.responseData) {
        budgetData = response.data.responseData || [];
      } else if (Array.isArray(response.data)) {
        budgetData = response.data;
      }

      setBudgets(budgetData);
      console.log('Budgets set:', budgetData);
    } catch (error) {
      message.error('Failed to fetch budgets');
      console.error('Fetch budgets error:', error);
    } finally {
      setLoading(false);
    }
  };

  const fetchSummary = async () => {
    try {
      const response = await axios.get('/api/admin/budget/summary');
      console.log('Summary API Response:', response.data);

      // Handle multiple response formats
      let summaryData = {
        totalAllocated: 0,
        totalSpent: 0,
        totalRemaining: 0
      };

      if (response.data.status === 'success') {
        summaryData = response.data.data || summaryData;
      } else if (response.data.responseData) {
        summaryData = response.data.responseData || summaryData;
      } else if (response.data.totalAllocated !== undefined) {
        summaryData = response.data;
      }

      setSummary(summaryData);
      console.log('Summary set:', summaryData);
    } catch (error) {
      console.error('Failed to fetch summary:', error);
      // Don't show error message, just log it
    }
  };

  const handleAddNew = () => {
    setEditingBudget(null);
    form.resetFields();
    form.setFieldsValue({
      status: 'Active',
      fiscalYear: new Date().getFullYear().toString(),
      allocatedAmount: 0,
      spentAmount: 0
    });
    setModalVisible(true);
  };

  const handleEdit = (record) => {
    setEditingBudget(record);
    form.setFieldsValue({
      budgetCode: record.budgetCode,
      budgetName: record.budgetName,
      category: record.category,
      allocatedAmount: record.allocatedAmount,
      spentAmount: record.spentAmount,
      fiscalYear: record.fiscalYear,
      startDate: record.startDate ? dayjs(record.startDate) : null,
      endDate: record.endDate ? dayjs(record.endDate) : null,
      status: record.status,
      departmentName: record.departmentName,
      projectCode: record.projectCode
    });
    setModalVisible(true);
  };

  const handleDelete = async (budgetCode) => {
    try {
      await axios.delete(`/api/admin/budget/${budgetCode}`);
      message.success('Budget deleted successfully');
      fetchBudgets();
      fetchSummary();
    } catch (error) {
      message.error(error.response?.data?.message || 'Failed to delete budget');
    }
  };

  const handleSubmit = async (values) => {
    try {
      const payload = {
        budgetCode: values.budgetCode,
        budgetName: values.budgetName,
        category: values.category,
        allocatedAmount: parseFloat(values.allocatedAmount),
        spentAmount: parseFloat(values.spentAmount || 0),
        fiscalYear: values.fiscalYear,
        startDate: values.startDate ? values.startDate.format('YYYY-MM-DD') : null,
        endDate: values.endDate ? values.endDate.format('YYYY-MM-DD') : null,
        status: values.status,
        departmentName: values.departmentName,
        projectCode: values.projectCode,
        createdBy: 'admin' // Replace with actual user from auth state
      };

      console.log('Submitting budget payload:', payload);

      let response;
      if (editingBudget) {
        response = await axios.put(`/api/admin/budget/${editingBudget.budgetCode}`, payload);
        console.log('Update response:', response.data);
        message.success('Budget updated successfully');
      } else {
        response = await axios.post('/api/admin/budget', payload);
        console.log('Create response:', response.data);
        message.success('Budget created successfully');
      }

      setModalVisible(false);
      form.resetFields();

      // Add a small delay to ensure backend has processed the data
      setTimeout(() => {
        fetchBudgets();
        fetchSummary();
      }, 500);

    } catch (error) {
      console.error('Submit error:', error);
      console.error('Error response:', error.response?.data);
      message.error(error.response?.data?.message || error.response?.data?.responseStatus?.message || 'Failed to save budget');
    }
  };

  const filteredBudgets = budgets.filter(
    (item) =>
      item.budgetCode?.toLowerCase().includes(searchText.toLowerCase()) ||
      item.budgetName?.toLowerCase().includes(searchText.toLowerCase()) ||
      item.category?.toLowerCase().includes(searchText.toLowerCase())
  );

  const columns = [
    {
      title: 'Budget Code',
      dataIndex: 'budgetCode',
      key: 'budgetCode',
      width: 150
    },
    {
      title: 'Category',
      dataIndex: 'category',
      key: 'category',
      width: 200
    },
    {
      title: 'Allocated',
      dataIndex: 'allocatedAmount',
      key: 'allocatedAmount',
      width: 130,
      render: (amount) => `₹${parseFloat(amount || 0).toLocaleString()}`
    },
    {
      title: 'Spent',
      dataIndex: 'spentAmount',
      key: 'spentAmount',
      width: 130,
      render: (amount) => (
        <span style={{ color: '#ff4d4f' }}>
          ₹{parseFloat(amount || 0).toLocaleString()}
        </span>
      )
    },
    {
      title: 'Remaining',
      dataIndex: 'remainingAmount',
      key: 'remainingAmount',
      width: 130,
      render: (amount) => (
        <span style={{ color: '#52c41a' }}>
          ₹{parseFloat(amount || 0).toLocaleString()}
        </span>
      )
    },
    {
      title: 'Fiscal Year',
      dataIndex: 'fiscalYear',
      key: 'fiscalYear',
      width: 100
    },
    {
      title: 'Status',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status) => {
        // Find the LOV for this status to get its color
        const lov = statusLOVs.find((l) => l.lovValue === status);
        const color = lov?.colorCode || 'blue';
        const displayValue = lov?.lovDisplayValue || status;

        return (
          <Tag
            color={color}
            style={{
              backgroundColor: color,
              color: 'white',
              border: 'none'
            }}
          >
            {displayValue}
          </Tag>
        );
      }
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
          >
            Edit
          </Button>
          <Popconfirm
            title="Are you sure you want to delete this budget?"
            onConfirm={() => handleDelete(record.budgetCode)}
            okText="Yes"
            cancelText="No"
          >
            <Button type="link" danger icon={<DeleteOutlined />}>
              Delete
            </Button>
          </Popconfirm>
        </Space>
      )
    }
  ];

  // Calculate remaining amount dynamically in form
  const onValuesChange = (changedValues, allValues) => {
    if (changedValues.allocatedAmount !== undefined || changedValues.spentAmount !== undefined) {
      const allocated = parseFloat(allValues.allocatedAmount || 0);
      const spent = parseFloat(allValues.spentAmount || 0);
      const remaining = allocated - spent;
      form.setFieldsValue({ remainingAmount: remaining });
    }
  };

  return (
    <div style={{ padding: '24px' }}>
      <h2 style={{ marginBottom: '24px', fontSize: '24px', fontWeight: 600 }}>Budget</h2>

      {/* Summary Cards */}
      <Row gutter={16} style={{ marginBottom: '24px' }}>
        <Col xs={24} sm={8}>
          <Card>
            <Statistic
              title="Total Allocated"
              value={summary.totalAllocated}
              // Modified by Aman
              prefix="₹" 
              //  End
              precision={2}
              valueStyle={{ color: '#1890ff' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={8}>
          <Card>
            <Statistic
              title="Total Spent"
              value={summary.totalSpent}
              // Modified by AMan
              prefix="₹" 
              // End
              precision={2}
              valueStyle={{ color: '#ff4d4f' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={8}>
          <Card>
            <Statistic
              title="Total Remaining"
              value={summary.totalRemaining}
              // Modified by AMan
              prefix="₹" 
              // End
              precision={2}
              valueStyle={{ color: '#52c41a' }}
            />
          </Card>
        </Col>
      </Row>

      <Card bordered={false}>
        {/* Search and Actions */}
        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '16px', flexWrap: 'wrap', gap: '8px' }}>
          <Input
            placeholder="Search budgets by code or category..."
            prefix={<SearchOutlined />}
            style={{ width: '350px' }}
            value={searchText}
            onChange={(e) => setSearchText(e.target.value)}
          />
          <Space>
            <Button icon={<ReloadOutlined />} onClick={() => { fetchBudgets(); fetchSummary(); }}>
              Refresh
            </Button>
            <Button type="primary" icon={<PlusOutlined />} onClick={handleAddNew}>
              Add New Budget
            </Button>
          </Space>
        </div>

        {/* Results Count */}
        <div style={{ marginBottom: '16px' }}>
          <span style={{ fontWeight: 500 }}>Showing {filteredBudgets.length} budget items</span>
        </div>

        {/* Table */}
        <Table
          columns={columns}
          dataSource={filteredBudgets}
          rowKey="budgetId"
          loading={loading}
          pagination={{
            pageSize: 10,
            showSizeChanger: true,
            showTotal: (total) => `Total ${total} items`
          }}
          scroll={{ x: 1300 }}
        />
      </Card>

      {/* Add/Edit Modal */}
      <Modal
        title={editingBudget ? 'Edit Budget' : 'Add New Budget'}
        open={modalVisible}
        onCancel={() => {
          setModalVisible(false);
          form.resetFields();
        }}
        footer={null}
        width={700}
      >
        <Form form={form} layout="vertical" onFinish={handleSubmit} onValuesChange={onValuesChange}>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
            <Form.Item
              label="Budget Code"
              name="budgetCode"
              rules={[{ required: true, message: 'Please enter budget code' }]}
            >
              <Input placeholder="Enter budget code" disabled={!!editingBudget} />
            </Form.Item>

            <Form.Item
              label="Category"
              name="category"
              rules={[{ required: true, message: 'Please enter category' }]}
            >
              <Input placeholder="Enter category" />
            </Form.Item>

            <Form.Item
              label="Allocated Amount"
              name="allocatedAmount"
              rules={[{ required: true, message: 'Please enter allocated amount' }]}
            >
              <Input type="number" placeholder="0" min={0} />
            </Form.Item>

            <Form.Item
              label="Spent Amount"
              name="spentAmount"
            >
              <Input type="number" placeholder="0" min={0} />
            </Form.Item>

            <Form.Item
              label="Fiscal Year"
              name="fiscalYear"
              rules={[{ required: true, message: 'Please enter fiscal year' }]}
            >
              <Input placeholder="2024" />
            </Form.Item>

            <Form.Item
              label="Status"
              name="status"
              rules={[{ required: true, message: 'Please select status' }]}
              tooltip="Status values are managed dynamically from LOV Management"
            >
              <Select loading={statusLoading} placeholder="Select status">
                {statusLOVs.map((lov) => (
                  <Option key={lov.lovId} value={lov.lovValue}>
                    {lov.lovDisplayValue || lov.lovValue}
                    {lov.colorCode && (
                      <span
                        style={{
                          marginLeft: '8px',
                          display: 'inline-block',
                          width: '12px',
                          height: '12px',
                          borderRadius: '50%',
                          backgroundColor: lov.colorCode
                        }}
                      />
                    )}
                  </Option>
                ))}
              </Select>
            </Form.Item>

            <Form.Item label="Budget Name" name="budgetName">
              <Input placeholder="Enter budget name" />
            </Form.Item>

            <Form.Item label="Department" name="departmentName">
              <Input placeholder="Enter department" />
            </Form.Item>

            <Form.Item label="Start Date" name="startDate">
              <DatePicker style={{ width: '100%' }} format="DD-MM-YYYY" />
            </Form.Item>

            <Form.Item label="End Date" name="endDate">
              <DatePicker style={{ width: '100%' }} format="DD-MM-YYYY" />
            </Form.Item>

            <Form.Item label="Project Code" name="projectCode">
              <Input placeholder="Enter project code (optional)" />
            </Form.Item>

            <Form.Item label="Remaining Amount" name="remainingAmount">
              <Input disabled placeholder="Auto-calculated" />
            </Form.Item>
          </div>

          <div style={{ padding: '12px', backgroundColor: '#f0f5ff', borderRadius: '4px', marginBottom: '16px' }}>
            <span style={{ color: '#1890ff', fontWeight: 500 }}>
              Remaining Amount: ₹
              {(
                parseFloat(form.getFieldValue('allocatedAmount') || 0) -
                parseFloat(form.getFieldValue('spentAmount') || 0)
              ).toFixed(2)}
            </span>
          </div>

          <Form.Item style={{ marginBottom: 0, textAlign: 'right' }}>
            <Space>
              <Button onClick={() => setModalVisible(false)}>Cancel</Button>
              <Button type="primary" htmlType="submit">
                {editingBudget ? 'Update' : 'Add'} Budget
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default BudgetManagement;
