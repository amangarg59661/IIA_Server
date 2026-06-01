import React, { useState } from 'react';
import { Form, Input, Button, Card, message, Typography } from 'antd';
import { LockOutlined } from '@ant-design/icons';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { clearFirstLogin } from '../../store/slice/authSlice';
import MyLogo from "../../assets/images/iia-logo.png";

const { Title, Text } = Typography;

/**
 * TC_14: Change Password Component
 * Forces users to change password on first login
 * Can also be used from settings page for regular password changes
 */
const ChangePassword = ({ isFirstLogin = true }) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { userId } = useSelector(state => state.auth);

  const handlePasswordChange = async (values) => {
    try {
      setLoading(true);

      const payload = {
        userId: userId,
        oldPassword: values.oldPassword,
        newPassword: values.newPassword
      };

      const response = await axios.post('/api/userMaster/change-password', payload);

      if (response.data.responseStatus?.statusCode === 0) {
        message.success('Password changed successfully!');

        // TC_14: Clear first login flag
        dispatch(clearFirstLogin());

        // Navigate to dashboard
        navigate('/');
      } else {
        message.error(response.data.responseStatus?.message || 'Password change failed');
      }
    } catch (error) {
      console.error('Password change error:', error);
      const errorMessage = error.response?.data?.responseStatus?.message
        || error.response?.data?.message
        || 'Failed to change password. Please try again.';
      message.error(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const validatePassword = (_, value) => {
    if (!value) {
      return Promise.reject(new Error('Please enter your new password'));
    }
    if (value.length < 8) {
      return Promise.reject(new Error('Password must be at least 8 characters'));
    }
    // Optional: Add more password strength validations
    return Promise.resolve();
  };

  const validateConfirmPassword = (_, value) => {
    const newPassword = form.getFieldValue('newPassword');
    if (value && value !== newPassword) {
      return Promise.reject(new Error('Passwords do not match'));
    }
    return Promise.resolve();
  };

  return (
    <div style={{
      minHeight: '100vh',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
      padding: '20px'
    }}>
      <Card
        style={{
          width: '100%',
          maxWidth: '500px',
          boxShadow: '0 8px 24px rgba(0,0,0,0.15)',
          borderRadius: '12px'
        }}
      >
        <div style={{ textAlign: 'center', marginBottom: '24px' }}>
          <img src={MyLogo} width={120} height={90} alt="Logo" style={{ marginBottom: '16px' }} />
          <Title level={3} style={{ margin: 0 }}>
            <LockOutlined /> Change Password
          </Title>
          {isFirstLogin && (
            <Text type="warning" style={{ display: 'block', marginTop: '12px' }}>
              For security reasons, you must change your password on first login
            </Text>
          )}
        </div>

        <Form
          form={form}
          layout="vertical"
          onFinish={handlePasswordChange}
          autoComplete="off"
        >
          <Form.Item
            label="Current Password"
            name="oldPassword"
            rules={[{ required: true, message: 'Please enter your current password' }]}
          >
            <Input.Password
              prefix={<LockOutlined />}
              placeholder="Enter current password"
              size="large"
            />
          </Form.Item>

          <Form.Item
            label="New Password"
            name="newPassword"
            rules={[
              { required: true, message: 'Please enter your new password' },
              { validator: validatePassword }
            ]}
            help="Password must be at least 8 characters"
          >
            <Input.Password
              prefix={<LockOutlined />}
              placeholder="Enter new password"
              size="large"
            />
          </Form.Item>

          <Form.Item
            label="Confirm New Password"
            name="confirmPassword"
            dependencies={['newPassword']}
            rules={[
              { required: true, message: 'Please confirm your new password' },
              { validator: validateConfirmPassword }
            ]}
          >
            <Input.Password
              prefix={<LockOutlined />}
              placeholder="Confirm new password"
              size="large"
            />
          </Form.Item>

          <Form.Item style={{ marginBottom: 0 }}>
            <Button
              type="primary"
              htmlType="submit"
              loading={loading}
              block
              size="large"
              style={{ marginTop: '8px' }}
            >
              Change Password
            </Button>
          </Form.Item>
        </Form>

        {!isFirstLogin && (
          <div style={{ textAlign: 'center', marginTop: '16px' }}>
            <Button type="link" onClick={() => navigate('/')}>
              Cancel and go back
            </Button>
          </div>
        )}
      </Card>
    </div>
  );
};

export default ChangePassword;
