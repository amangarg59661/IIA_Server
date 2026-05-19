import React, { useState, useEffect } from 'react';
import { Card, Select, Input, Button, Table, Space, message, Modal, Form, Tag, Popconfirm } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, ReloadOutlined, SearchOutlined } from '@ant-design/icons';
import axios from 'axios';

const { Option } = Select;

const ListOfValues = () => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [forms, setForms] = useState([]);
  const [designators, setDesignators] = useState([]);
  const [lovValues, setLovValues] = useState([]);
  const [selectedForm, setSelectedForm] = useState(null);
  const [selectedDesignator, setSelectedDesignator] = useState(null);
  const [searchText, setSearchText] = useState('');
  const [modalVisible, setModalVisible] = useState(false);
  const [editingLOV, setEditingLOV] = useState(null);
  const [locationOptions, setLocationOptions] = useState([]);
const [locationLoading, setLocationLoading] = useState(false);

  useEffect(() => {
    fetchForms();
  }, []);

  const fetchLocationOptions = async () => {
  try {
    setLocationLoading(true);
    const response = await axios.get('/api/location-master');
    const options = response.data.responseData || [];
    setLocationOptions(options);
  } catch (error) {
    message.error('Failed to fetch locations');
    console.error('Fetch locations error:', error);
  } finally {
    setLocationLoading(false);
  }
};

  const fetchForms = async () => {
    try {
      setLoading(true);
      const response = await axios.get('/api/admin/lov/forms');
      console.log('Forms API Response:', response.data);

      // Handle multiple response formats
      let formData = [];
      if (response.data.status === 'success') {
        formData = response.data.data || [];
      } else if (response.data.responseData) {
        formData = response.data.responseData || [];
      } else if (Array.isArray(response.data)) {
        formData = response.data;
      }

      setForms(formData);
      console.log('Forms set:', formData);
    } catch (error) {
      message.error('Failed to fetch forms');
      console.error('Fetch forms error:', error);
    } finally {
      setLoading(false);
    }
  };

  const fetchDesignators = async (formId) => {
    try {
      setLoading(true);
      const response = await axios.get(`/api/admin/lov/forms/${formId}/designators`);
      console.log('Designators API Response:', response.data);

      // Handle multiple response formats
      let designatorData = [];
      if (response.data.status === 'success') {
        designatorData = response.data.data || [];
      } else if (response.data.responseData) {
        designatorData = response.data.responseData || [];
      } else if (Array.isArray(response.data)) {
        designatorData = response.data;
      }

      setDesignators(designatorData);
      setSelectedDesignator(null);
      setLovValues([]);
      console.log('Designators set:', designatorData);
    } catch (error) {
      message.error('Failed to fetch designators');
      console.error('Fetch designators error:', error);
    } finally {
      setLoading(false);
    }
  };

  const fetchLOVValues = async (designatorId) => {
    try {
      setLoading(true);
      const response = await axios.get(`/api/admin/lov/designators/${designatorId}/values`);
      console.log('LOV Values API Response:', response.data);

      // Handle multiple response formats
      let lovData = [];
      if (response.data.status === 'success') {
        lovData = response.data.data || [];
      } else if (response.data.responseData) {
        lovData = response.data.responseData || [];
      } else if (Array.isArray(response.data)) {
        lovData = response.data;
      }

      // ✅ FIX: Backend returns "value" and "displayValue" but table expects "lovValue" and "lovDisplayValue"
      // Normalize the data to use consistent field names
      const normalizedData = lovData.map(item => ({
        lovId: item.lovId,
        lovValue: item.lovValue || item.value,           // Support both formats
        lovDisplayValue: item.lovDisplayValue || item.displayValue, // Support both formats
        description: item.description || item.lovDescription,
        colorCode: item.colorCode,
        displayOrder: item.displayOrder,
        isActive: item.isActive,
        isDefault: item.isDefault
      }));

      setLovValues(normalizedData);
      console.log('✅ LOV Values normalized and set:', normalizedData);
    } catch (error) {
      message.error('Failed to fetch LOV values');
      console.error('Fetch LOV values error:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleFormChange = (formId) => {
    setSelectedForm(formId);
    fetchDesignators(formId);
  };

  const handleDesignatorChange = (designatorId) => {
    setSelectedDesignator(designatorId);
    fetchLOVValues(designatorId);
  };

  const handleAddNew = () => {
    if (!selectedDesignator) {
      message.warning('Please select a form and designator first');
      return;
    }
    setEditingLOV(null);
    form.resetFields();
    form.setFieldsValue({ isActive: true });  
    if (selectedDesignator === 10) fetchLocationOptions(); // ✅

  setModalVisible(true);
};

  const handleEdit = (record) => {
    setEditingLOV(record);
    form.setFieldsValue({
      lovValue: record.lovValue,
      lovDisplayValue: record.lovDisplayValue,
      colorCode: record.colorCode,
      displayOrder: record.displayOrder,
      isActive: record.isActive,
      locationCode: record.locationCode 
    });
    if (selectedDesignator === 10) fetchLocationOptions();
    setModalVisible(true);
  };

  const handleDelete = async (lovId) => {
    try {
      await axios.delete(`/api/admin/lov/values/${lovId}`);
      message.success('LOV value deleted successfully');
      fetchLOVValues(selectedDesignator);
    } catch (error) {
      message.error(error.response?.data?.message || 'Failed to delete LOV value');
    }
  };

  const handleSubmit = async (values) => {
    try {
      const payload = {
        ...values,
        designatorId: selectedDesignator,
        // locationCode: locationCode,
        createdBy: 'admin' // Replace with actual user from auth state
      };

      console.log('Submitting LOV payload:', payload);

      let response;
      if (editingLOV) {
        response = await axios.put(`/api/admin/lov/values/${editingLOV.lovId}`, payload);
        console.log('Update response:', response.data);
        message.success('LOV value updated successfully');
      } else {
        response = await axios.post('/api/admin/lov/values', payload);
        console.log('Create response:', response.data);
        message.success('LOV value created successfully');
      }

      setModalVisible(false);
      form.resetFields();

      // Immediately refresh to show the new value
      // Backend now flushes data immediately, so no delay needed
      fetchLOVValues(selectedDesignator);
    } catch (error) {
      console.error('Submit LOV error:', error);
      console.error('Error response:', error.response?.data);
      message.error(error.response?.data?.message || error.response?.data?.responseStatus?.message || 'Failed to save LOV value');
    }
  };

  const filteredLOVValues = lovValues.filter(
    (item) =>
      item.lovValue?.toLowerCase().includes(searchText.toLowerCase()) ||
      item.lovDisplayValue?.toLowerCase().includes(searchText.toLowerCase())
  );

  const columns = [
    {
      title: 'Value',
      dataIndex: 'lovValue',
      key: 'lovValue',
      width: 200
    },
    {
      title: 'Display Value',
      dataIndex: 'lovDisplayValue',
      key: 'lovDisplayValue',
      width: 200
    },
    {
      title: 'Color Code',
      dataIndex: 'colorCode',
      key: 'colorCode',
      width: 150,
      render: (color) => (
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <div
            style={{
              width: '24px',
              height: '24px',
              backgroundColor: color || '#fff',
              border: '1px solid #d9d9d9',
              borderRadius: '4px'
            }}
          />
          <span>{color}</span>
        </div>
      )
    },
    {
      title: 'Display Order',
      dataIndex: 'displayOrder',
      key: 'displayOrder',
      width: 120,
      sorter: (a, b) => a.displayOrder - b.displayOrder
    },
    {
      title: 'Status',
      dataIndex: 'isActive',
      key: 'isActive',
      width: 100,
      render: (isActive) => (
        <Tag color={isActive ? 'green' : 'red'}>{isActive ? 'Active' : 'Inactive'}</Tag>
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
          >
            Edit
          </Button>
          <Popconfirm
            title="Are you sure you want to delete this LOV value?"
            onConfirm={() => handleDelete(record.lovId)}
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

  return (
    <div style={{ padding: '24px' }}>
      <Card title="List of Values Management" bordered={false}>
        {/* Filters Section */}
        <div style={{ marginBottom: '24px' }}>
          <div style={{ display: 'flex', gap: '16px', flexWrap: 'wrap', alignItems: 'center' }}>
            <div style={{ flex: '1', minWidth: '200px' }}>
              <label style={{ display: 'block', marginBottom: '8px', fontWeight: 500 }}>Forms</label>
              <Select
                style={{ width: '100%' }}
                placeholder="Select form type"
                onChange={handleFormChange}
                value={selectedForm}
                loading={loading}
              >
                {forms.map((form) => (
                  <Option key={form.formId} value={form.formId}>
                    {form.formDisplayName}
                  </Option>
                ))}
              </Select>
            </div>

            <div style={{ flex: '1', minWidth: '200px' }}>
              <label style={{ display: 'block', marginBottom: '8px', fontWeight: 500 }}>Designator</label>
              <Select
                style={{ width: '100%' }}
                placeholder="Select designator"
                onChange={handleDesignatorChange}
                value={selectedDesignator}
                disabled={!selectedForm}
                loading={loading}
              >
                {designators.map((designator) => (
                  <Option key={designator.designatorId} value={designator.designatorId}>
                    {designator.designatorDisplayName}
                  </Option>
                ))}
              </Select>
            </div>
          </div>
        </div>

        {/* Search and Actions */}
        {selectedDesignator && (
          <>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '16px', flexWrap: 'wrap', gap: '8px' }}>
              <Input
                placeholder="Search in results..."
                prefix={<SearchOutlined />}
                style={{ width: '300px' }}
                value={searchText}
                onChange={(e) => setSearchText(e.target.value)}
              />
              <Space>
                <Button icon={<ReloadOutlined />} onClick={() => fetchLOVValues(selectedDesignator)}>
                  Refresh
                </Button>
                <Button type="primary" icon={<PlusOutlined />} onClick={handleAddNew}>
                  Add New
                </Button>
              </Space>
            </div>

            {/* Results Count */}
            <div style={{ marginBottom: '16px' }}>
              <span style={{ fontWeight: 500 }}>Showing {filteredLOVValues.length} results</span>
            </div>

            {/* Table */}
            <Table
              columns={columns}
              dataSource={filteredLOVValues}
              rowKey="lovId"
              loading={loading}
              pagination={{
                pageSize: 10,
                showSizeChanger: true,
                showTotal: (total) => `Total ${total} items`
              }}
              scroll={{ x: 1000 }}
            />
          </>
        )}

        {!selectedDesignator && (
          <div style={{ textAlign: 'center', padding: '40px', color: '#999' }}>
            Please select a form and designator to view LOV values
          </div>
        )}
      </Card>

      {/* Add/Edit Modal */}
      <Modal
        title={editingLOV ? 'Edit Entry' : 'Add New Entry'}
        open={modalVisible}
        onCancel={() => {
          setModalVisible(false);
          form.resetFields();
        }}
        footer={null}
        width={500}
      >
        <Form form={form} layout="vertical" onFinish={handleSubmit}>
          <Form.Item
  label="Code"
  name="lovValue"
  rules={[
    { required: true, message: 'Please enter code' },
    ...(selectedDesignator === 10
      ? [{ pattern: /^\d+$/, message: 'Only numbers are allowed' }]
      : [])
  ]}
>
  <Input
    placeholder="Enter code"
    type={selectedDesignator === 10 ? 'number' : 'text'}
    onKeyDown={selectedDesignator === 10
      ? (e) => {
          if (!/[\d]/.test(e.key) && !['Backspace', 'Delete', 'Tab', 'ArrowLeft', 'ArrowRight'].includes(e.key)) {
            e.preventDefault();
          }
        }
      : undefined}
  />
</Form.Item>

          <Form.Item
            label="Name"
            name="lovDisplayValue"
            rules={[{ required: true, message: 'Please enter name' }]}
          >
            <Input placeholder="Enter name" />
          </Form.Item>

          <Form.Item label="Description" name="description">
            <Input.TextArea placeholder="Enter description" rows={3} />
          </Form.Item>
          {/* Modified by Aman for department vise limit for computer category */}
          {selectedDesignator === 10 && (
  <Form.Item
    label="Location"
    name="locationCode"
    rules={[{ required: true, message: 'Please select a location' }]}
  >
    <Select
      placeholder="Select a location"
      loading={locationLoading}
      showSearch
      optionFilterProp="children"
    >
      {locationOptions.map((loc) => (
        <Option key={loc.locationCode} value={loc.locationCode}>
          {loc.locationName}
        </Option>
      ))}
    </Select>
  </Form.Item>
)}
          {selectedDesignator === 13 && (
        <Form.Item
            label="Limit"
            name="departmentLimit"
            rules={[{ 
              // Modified by aman
              // required: true,
              // End
               message: 'Please enter Department limit' }]}
          >
            <Input placeholder="Department Limit" />
          </Form.Item>
          
         ) }
         
         {/* END */}

          <Form.Item label="Color Code" name="colorCode">
            <Input type="color" style={{ width: '100px' }} />
          </Form.Item>

          <Form.Item
            label="Display Order"
            name="displayOrder"
            rules={[{ required: true, message: 'Please enter display order' }]}
          >
            <Input type="number" placeholder="Enter display order" />
          </Form.Item>

          <Form.Item label="Status" name="isActive" valuePropName="checked">
            <Select defaultValue={true}>
              <Option value={true}>Active</Option>
              <Option value={false}>Inactive</Option>
            </Select>
          </Form.Item>

          <Form.Item style={{ marginBottom: 0, textAlign: 'right' }}>
            <Space>
              <Button onClick={() => setModalVisible(false)}>Cancel</Button>
              <Button type="primary" htmlType="submit">
                {editingLOV ? 'Update' : 'Add'} Entry
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default ListOfValues;
