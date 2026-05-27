import React, { useEffect, useState } from 'react';
import { Table, Button, Modal, Form, Input, Select, Tag, message, Popconfirm, Card, Typography, Spin } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import axios from 'axios';

const { Option } = Select;
const { Title, Text } = Typography;

const CommitteeManagement = () => {
  const [members, setMembers]     = useState([]);
  const [users, setUsers]         = useState([]);   // for the userId dropdown
  const [loading, setLoading]     = useState(false);
  const [usersLoading, setUsersLoading] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [form] = Form.useForm();

  // ── fetch committee members ──────────────────────────────────
  const fetchMembers = async () => {
    try {
      setLoading(true);
      const res = await axios.get('/api/admin/techno-financial-committee');
      setMembers(res.data?.responseData || []);
    } catch (e) {
      console.error('Failed to load committee members:', e);
      message.error('Failed to load committee members.');
    } finally {
      setLoading(false);
    }
  };

  // ── fetch all system users for the userId dropdown ──────────
  const fetchUsers = async () => {
    try {
      setUsersLoading(true);
      const res = await axios.get('/api/userMaster');
      const raw = res.data?.responseData || res.data || [];
      // Normalize: handle both array and wrapped formats
      const list = Array.isArray(raw) ? raw : [];
      setUsers(list);
    } catch (e) {
      console.error('Failed to load users:', e);
      // Non-fatal — admin can still type the user ID
    } finally {
      setUsersLoading(false);
    }
  };

  useEffect(() => {
    fetchMembers();
    fetchUsers();
  }, []);

  // ── open Add modal ───────────────────────────────────────────
  const openAdd = () => {
    setEditingId(null);
    form.resetFields();
    setModalOpen(true);
  };

  // ── open Edit modal ──────────────────────────────────────────
  const openEdit = (record) => {
    setEditingId(record.id);
    form.setFieldsValue({
      ...record,
      userId: record.userId,
    });
    setModalOpen(true);
  };

  // ── deactivate member ────────────────────────────────────────
  const handleDeactivate = async (id) => {
    try {
      await axios.delete(`/api/admin/techno-financial-committee/${id}`);
      message.success('Member deactivated.');
      fetchMembers();
    } catch (e) {
      message.error(e?.response?.data?.responseStatus?.message || 'Failed to deactivate.');
    }
  };

  // ── submit Add / Edit form ───────────────────────────────────
  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      // Ensure userId is an integer
      values.userId = parseInt(values.userId, 10);

      if (editingId) {
        await axios.put(`/api/admin/techno-financial-committee/${editingId}`, values);
        message.success('Member updated.');
      } else {
        await axios.post('/api/admin/techno-financial-committee', values);
        message.success('Member added successfully.');
      }
      setModalOpen(false);
      fetchMembers();
    } catch (e) {
      if (e?.response) {
        message.error(e?.response?.data?.responseStatus?.message || 'Operation failed.');
      }
      // antd validation errors handled automatically
    }
  };

  // ── helpers ──────────────────────────────────────────────────
  const roleColor = (role) => {
    if (role === 'CHAIRMAN')    return 'gold';
    if (role === 'CO_CHAIRMAN') return 'orange';
    return 'blue';
  };

  const committeeTypeLabel = (type) => {
    if (type === 'STEC_I')  return 'STEC-I';
    if (type === 'STEC_II') return 'STEC-II';
    return type || '—';
  };

  const committeeTypeColor = (type) => {
    if (type === 'STEC_I')  return 'purple';
    if (type === 'STEC_II') return 'geekblue';
    return 'default';
  };

  // label for user dropdown: "Name (ID)"
  const userLabel = (u) => {
    const name = u.userName || u.name || u.employeeName || '';
    return name ? `${name} (ID: ${u.userId})` : `User ID: ${u.userId}`;
  };

  // ── table columns ────────────────────────────────────────────
  const columns = [
    {
      title: 'Name',
      dataIndex: 'memberName',
      key: 'memberName',
      render: (name, r) => (
        <span>
          {name}
          {r.role === 'CHAIRMAN'    && <Tag color="gold"   style={{ marginLeft: 8 }}>Chairman</Tag>}
          {r.role === 'CO_CHAIRMAN' && <Tag color="orange" style={{ marginLeft: 8 }}>Co-Chairman</Tag>}
        </span>
      ),
    },
    { title: 'Designation',  dataIndex: 'designation',  key: 'designation' },
    { title: 'Email',        dataIndex: 'emailAddress', key: 'emailAddress' },
    { title: 'Employee ID',  dataIndex: 'employeeId',   key: 'employeeId' },
    { title: 'User ID',      dataIndex: 'userId',       key: 'userId' },
    {
      title: 'Committee',
      dataIndex: 'committeeType',
      key: 'committeeType',
      render: (type) => (
        <Tag color={committeeTypeColor(type)}>{committeeTypeLabel(type)}</Tag>
      ),
    },
    {
      title: 'Role',
      dataIndex: 'role',
      key: 'role',
      render: (r) => <Tag color={roleColor(r)}>{r}</Tag>,
    },
    {
      title: 'Status',
      dataIndex: 'isActive',
      key: 'isActive',
      render: (active) => (
        <Tag color={active ? 'green' : 'red'}>{active ? 'Active' : 'Inactive'}</Tag>
      ),
    },
    {
      title: 'Actions',
      key: 'actions',
      render: (_, record) => (
        <div style={{ display: 'flex', gap: 8 }}>
          <Button icon={<EditOutlined />} size="small" onClick={() => openEdit(record)}>
            Edit
          </Button>
          <Popconfirm
            title="Deactivate this member?"
            onConfirm={() => handleDeactivate(record.id)}
            okText="Yes"
            cancelText="No"
          >
            <Button icon={<DeleteOutlined />} size="small" danger>
              Deactivate
            </Button>
          </Popconfirm>
        </div>
      ),
    },
  ];

  // ── render ───────────────────────────────────────────────────
  return (
    <div style={{ padding: 24 }}>
      <Card>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
          <div>
            <Title level={4} style={{ margin: 0 }}>Standing Tender Evaluation Committee (STEC)</Title>
            <Text type="secondary">
              STEC-I: ₹10 Lakh – ₹50 Lakh &nbsp;|&nbsp;
              STEC-II: ₹50 Lakh – ₹1 Crore &nbsp;|&nbsp;
              Above ₹1 Crore: ad hoc by Director. &nbsp;
              Chair &amp; Co-Chair of STEC-I and STEC-II must be different persons.
              Director has overriding authority for all above-₹10L tenders.
            </Text>
          </div>
          <Button type="primary" icon={<PlusOutlined />} onClick={openAdd}>
            Add Member
          </Button>
        </div>

        {/* ── STEC-I summary ── */}
        <div style={{ display: 'flex', gap: 24, marginBottom: 16, flexWrap: 'wrap' }}>
          {['STEC_I', 'STEC_II'].map(type => {
            const group = members.filter(m => m.committeeType === type);
            const chair   = group.find(m => m.role === 'CHAIRMAN');
            const cochair = group.find(m => m.role === 'CO_CHAIRMAN');
            return (
              <Card
                key={type}
                size="small"
                title={<Tag color={committeeTypeColor(type)}>{committeeTypeLabel(type)}</Tag>}
                style={{ flex: 1, minWidth: 280, borderColor: type === 'STEC_I' ? '#722ed1' : '#1677ff' }}
              >
                <p style={{ margin: '2px 0' }}>
                  <b>Chair:</b> {chair ? chair.memberName : <Text type="danger">⚠ Not set</Text>}
                </p>
                <p style={{ margin: '2px 0' }}>
                  <b>Co-Chair:</b> {cochair ? cochair.memberName : <Text type="danger">⚠ Not set</Text>}
                </p>
                <p style={{ margin: '2px 0' }}>
                  <b>Members:</b> {group.filter(m => m.role === 'MEMBER').length} configured
                </p>
              </Card>
            );
          })}
        </div>

        <Table
          columns={columns}
          dataSource={members}
          rowKey="id"
          loading={loading}
          pagination={false}
          bordered
          size="small"
          locale={{ emptyText: loading ? 'Loading...' : 'No committee members configured. Click "Add Member" to begin.' }}
        />
      </Card>

      {/* ── Add / Edit Modal ── */}
      <Modal
        title={editingId ? 'Edit Committee Member' : 'Add Committee Member'}
        open={modalOpen}
        onOk={handleSubmit}
        onCancel={() => setModalOpen(false)}
        okText={editingId ? 'Update' : 'Add'}
        width={560}
      >
        <Form form={form} layout="vertical">

          {/* User selection dropdown */}
          <Form.Item
            name="userId"
            label="Select System User"
            rules={[{ required: true, message: 'Please select a user' }]}
          >
            {usersLoading ? (
              <Spin size="small" />
            ) : users.length > 0 ? (
              <Select
                showSearch
                placeholder="Search and select a user"
                optionFilterProp="children"
                filterOption={(input, option) =>
                  (option?.children?.toString() || '').toLowerCase().includes(input.toLowerCase())
                }
                onChange={(val) => {
                  // Auto-fill member name if user selected
                  const found = users.find(u => u.userId === val);
                  if (found) {
                    const name = found.userName || found.name || found.employeeName || '';
                    const emp  = found.employeeId || found.employee_id || '';
                    const des  = found.designation || '';
                    const email= found.email || found.emailAddress || '';
                    if (name)  form.setFieldValue('memberName',   name);
                    if (emp)   form.setFieldValue('employeeId',   emp);
                    if (des)   form.setFieldValue('designation',  des);
                    if (email) form.setFieldValue('emailAddress', email);
                  }
                }}
              >
                {users.map(u => (
                  <Option key={u.userId} value={u.userId}>
                    {userLabel(u)}
                  </Option>
                ))}
              </Select>
            ) : (
              <Input type="number" placeholder="Enter User ID manually" />
            )}
          </Form.Item>

          <Form.Item
            name="memberName"
            label="Full Name"
            rules={[{ required: true, message: 'Name is required' }]}
          >
            <Input placeholder="e.g. Dr. Ramesh Kumar" />
          </Form.Item>

          <Form.Item
            name="committeeType"
            label="Committee Type"
            rules={[{ required: true, message: 'Committee type is required' }]}
          >
            <Select placeholder="Select committee">
              <Option value="STEC_I">STEC-I (₹10 Lakh – ₹50 Lakh)</Option>
              <Option value="STEC_II">STEC-II (₹50 Lakh – ₹1 Crore)</Option>
            </Select>
          </Form.Item>

          <Form.Item
            name="role"
            label="Role in Committee"
            rules={[{ required: true, message: 'Role is required' }]}
          >
            <Select placeholder="Select role">
              <Option value="CHAIRMAN">Chairman (Chair)</Option>
              <Option value="CO_CHAIRMAN">Co-Chairman (Co-Chair)</Option>
              <Option value="MEMBER">Member</Option>
            </Select>
          </Form.Item>

          <Form.Item name="employeeId" label="Employee ID">
            <Input placeholder="Employee ID" />
          </Form.Item>

          <Form.Item name="designation" label="Designation">
            <Input placeholder="e.g. Deputy Director (Finance)" />
          </Form.Item>

          <Form.Item
            name="emailAddress"
            label="Email Address"
            rules={[{ type: 'email', message: 'Invalid email' }]}
          >
            <Input placeholder="member@iia.org.in" />
          </Form.Item>

          {editingId && (
            <Form.Item name="isActive" label="Status">
              <Select>
                <Option value={true}>Active</Option>
                <Option value={false}>Inactive</Option>
              </Select>
            </Form.Item>
          )}
        </Form>
      </Modal>
    </div>
  );
};

export default CommitteeManagement;