import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { Form, Input, Button, message, Card } from 'antd';
import { LockOutlined, EyeInvisibleOutlined, EyeTwoTone } from '@ant-design/icons';
import axios from 'axios';
import MyLogo from "../../assets/iia-logo.png";
import FormContainer from '../../components/DKG_FormContainer';

const ChangePassword = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);

  // Get vendorId from navigation state
  const vendorId = location.state?.vendorId || '';

  // If no vendorId, redirect to login
  React.useEffect(() => {
    if (!vendorId) {
      message.error('Session expired. Please login again.');
      navigate('/');
    }
  }, [vendorId, navigate]);

  const handleSubmit = async (values) => {
    const { currentPassword, newPassword, confirmPassword } = values;

    // Client-side validation
    if (newPassword !== confirmPassword) {
      message.error('New password and confirm password do not match');
      return;
    }

    if (currentPassword === newPassword) {
      message.error('New password must be different from current password');
      return;
    }

    setLoading(true);

    try {
      const response = await axios.post('/api/vendor-quotation/change-password', {
        vendorId: vendorId,
        currentPassword: currentPassword,
        newPassword: newPassword,
        confirmPassword: confirmPassword
      });

      const data = response.data;

      if (data.responseData.success) {
        message.success(data.responseData.message);
        // Redirect to login page after successful password change
        setTimeout(() => {
          navigate('/', { 
            state: { 
              message: 'Password changed successfully. Please login with your new password.',
              vendorId: vendorId 
            } 
          });
        }, 1500);
      } else {
        message.error(data.responseData.message || 'Failed to change password');
      }
    } catch (error) {
      console.error('Change password error:', error);
      message.error(error.response?.data?.responseData?.message || 'Failed to change password. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const passwordRules = [
    { required: true, message: 'Please enter your password' },
    { min: 8, message: 'Password must be at least 8 characters long' },
    {
      pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&#])[A-Za-z\d@$!%*?&#]{8,}$/,
      message: 'Password must contain at least one uppercase, one lowercase, one number and one special character'
    }
  ];

  return (
    <>
      <header className='bg-darkBlue text-offWhite p-4 fixed top-0 w-full z-30'>
        <h1>Change Password</h1>
      </header>
      
      <FormContainer className='mt-20 main-content border-none !shadow-none'>
        <main className='w-full p-4 flex flex-col h-fit justify-center items-center gap-6 bg-white relative z-20 rounded-md'>
          <img src={MyLogo} width={200} height={150} alt="Logo" />
          
          <Card 
            style={{ 
              width: '100%', 
              maxWidth: '450px',
              boxShadow: '0 4px 12px rgba(0, 0, 0, 0.1)',
              borderRadius: '12px'
            }}
          >
            <div style={{ textAlign: 'center', marginBottom: '24px' }}>
              <h2 style={{ color: '#1a365d', marginBottom: '8px' }}>
                Password Change Required
              </h2>
              <p style={{ color: '#666', fontSize: '14px' }}>
                For security reasons, please change your temporary password before continuing.
              </p>
              <p style={{ color: '#1890ff', fontSize: '14px', fontWeight: '500', marginTop: '8px' }}>
                Vendor ID: {vendorId}
              </p>
            </div>

            <Form
              form={form}
              layout="vertical"
              onFinish={handleSubmit}
              autoComplete="off"
            >
              <Form.Item
                label="Current Password (Temporary Password)"
                name="currentPassword"
                rules={[{ required: true, message: 'Please enter your current password' }]}
              >
                <Input.Password
                  prefix={<LockOutlined />}
                  placeholder="Enter your temporary password"
                  iconRender={(visible) => (visible ? <EyeTwoTone /> : <EyeInvisibleOutlined />)}
                  size="large"
                />
              </Form.Item>

              <Form.Item
                label="New Password"
                name="newPassword"
                rules={passwordRules}
                hasFeedback
              >
                <Input.Password
                  prefix={<LockOutlined />}
                  placeholder="Enter new password"
                  iconRender={(visible) => (visible ? <EyeTwoTone /> : <EyeInvisibleOutlined />)}
                  size="large"
                />
              </Form.Item>

              <Form.Item
                label="Confirm New Password"
                name="confirmPassword"
                dependencies={['newPassword']}
                hasFeedback
                rules={[
                  { required: true, message: 'Please confirm your new password' },
                  ({ getFieldValue }) => ({
                    validator(_, value) {
                      if (!value || getFieldValue('newPassword') === value) {
                        return Promise.resolve();
                      }
                      return Promise.reject(new Error('Passwords do not match'));
                    },
                  }),
                ]}
              >
                <Input.Password
                  prefix={<LockOutlined />}
                  placeholder="Confirm new password"
                  iconRender={(visible) => (visible ? <EyeTwoTone /> : <EyeInvisibleOutlined />)}
                  size="large"
                />
              </Form.Item>

              <div style={{ 
                backgroundColor: '#f0f5ff', 
                padding: '12px', 
                borderRadius: '8px', 
                marginBottom: '16px',
                fontSize: '12px',
                color: '#666'
              }}>
                <strong>Password Requirements:</strong>
                <ul style={{ margin: '8px 0 0 16px', padding: 0 }}>
                  <li>At least 8 characters long</li>
                  <li>At least one uppercase letter (A-Z)</li>
                  <li>At least one lowercase letter (a-z)</li>
                  <li>At least one number (0-9)</li>
                  <li>At least one special character (@$!%*?&#)</li>
                </ul>
              </div>

              <Form.Item>
                <Button
                  type="primary"
                  htmlType="submit"
                  loading={loading}
                  block
                  size="large"
                  style={{ 
                    backgroundColor: '#1a365d',
                    borderColor: '#1a365d',
                    height: '48px',
                    fontSize: '16px',
                    fontWeight: '500'
                  }}
                >
                  {loading ? 'Changing Password...' : 'Change Password'}
                </Button>
              </Form.Item>
            </Form>
          </Card>

          <p style={{ color: '#999', fontSize: '12px', textAlign: 'center' }}>
            After changing your password, you will be redirected to the login page.
          </p>
        </main>
      </FormContainer>
    </>
  );
};

export default ChangePassword;