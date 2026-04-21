import React, { useState, useEffect } from 'react';
import Btn from '../../components/DKG_Btn';
import MyLogo from "../../assets/iia-logo.png";
import FormBody from '../../components/DKG_FormBody';
import FormInputItem from '../../components/DKG_FormInputItem';
import { useDispatch } from 'react-redux';
import { useNavigate, useLocation } from 'react-router-dom';
import FormContainer from '../../components/DKG_FormContainer';
import { fetchMasters } from '../../store/slice/masterSlice';
import { login } from '../../store/slice/authSlice';
import { setVendor } from '../../store/slice/authSlice';
import axios from 'axios';
import { message } from 'antd';

const Login = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const location = useLocation();

  const [formData, setFormData] = useState({
    userId: '',
    password: ''
  });

  const [messageText, setMessageText] = useState('');
  const [successMessage, setSuccessMessage] = useState('');

  // Check if redirected from change password page
  useEffect(() => {
    if (location.state?.message) {
      setSuccessMessage(location.state.message);
      // Pre-fill vendor ID if available
      if (location.state?.vendorId) {
        setFormData(prev => ({
          ...prev,
          userId: location.state.vendorId
        }));
      }
      // Clear the state to prevent showing message on refresh
      window.history.replaceState({}, document.title);
    }
  }, [location.state]);

  const handleFormValueChange = (fieldName, value) => {
    setFormData((prev) => ({
      ...prev,
      [fieldName]: value
    }));
    setMessageText('');
    setSuccessMessage('');
  };

  const handleFormSubmit = async () => {
    const { userId, password } = formData;

    if (!userId || !password) {
      setMessageText('Please enter both Vendor ID and Password');
      return;
    }

    try {
      const response = await axios.get(`/api/vendor-quotation/VendorStatus/${userId}`);
      const data = response.data;
      const status = data.responseData.status;
      const Password = data.responseData.password;
      const isFirstLogin = data.responseData.isFirstLogin;

      // First check password
      if (Password !== password) {
        setMessageText("Incorrect password.");
        return;
      }

      // Handle different statuses
      if (status === "APPROVED") {
        // Check if this is first login - redirect to change password page
        if (isFirstLogin === true) {
          message.info('Please change your temporary password to continue.');
          navigate('/change-password', { 
            state: { 
              vendorId: userId,
              isFirstLogin: true 
            } 
          });
          return;
        }

        // Normal login flow - password already changed
        try {
          dispatch(setVendor(data.responseData));
          navigate(`/vendor/${userId}`);
        } catch (error) {
          setMessageText("Invalid credentials");
        }
      } else if (status === "REJECTED") {
        const comments = data.responseData.comments || "Your request was rejected with no comments provided.";
        setMessageText(`Sorry, your request has been rejected. Reason: ${comments}`);
      } else if (status === "AWAITING_APPROVAL") {
        // For AWAITING_APPROVAL, also check if first login for password change
        if (isFirstLogin === true) {
          message.info('Please change your temporary password. Your registration is still under review.');
          navigate('/change-password', { 
            state: { 
              vendorId: userId,
              isFirstLogin: true,
              pendingApproval: true
            } 
          });
          return;
        }
        setMessageText("Your registration is in review stage. Please wait for sometime...");
      } else if (status === "CHANGE_REQUEST") {
        // Handle change request status
        if (isFirstLogin === true) {
          message.info('Please change your temporary password first.');
          navigate('/change-password', { 
            state: { 
              vendorId: userId,
              isFirstLogin: true 
            } 
          });
          return;
        }
        const comments = data.responseData.comments || "Admin has requested changes to your registration.";
        setMessageText(`Change requested: ${comments}`);
      } else if (status === "NOT_FOUND") {
        setMessageText("Vendor ID not found");
      } else {
        setMessageText("Unknown status");
      }
    } catch (error) {
      console.error('Login error:', error);
      if (error.response?.status === 404) {
        setMessageText("Vendor ID not found");
      } else {
        setMessageText("Error fetching vendor status. Please try again.");
      }
    }
  };

  const handleRegisterRedirect = () => {
    navigate('/app');
  };

  return (
    <>
      <header className='bg-darkBlue text-offWhite p-4 fixed top-0 w-full z-30'>
        <h1>Log In</h1>
      </header>
      <FormContainer className='mt-20 main-content border-none !shadow-none'>
        <main className='w-full p-4 flex flex-col h-fit justify-center items-center gap-8 bg-white relative z-20 rounded-md'>
          <img src={MyLogo} width={200} height={150} alt="Logo" />
          
          {/* Success message banner */}
          {successMessage && (
            <div style={{
              backgroundColor: '#d4edda',
              color: '#155724',
              padding: '12px 20px',
              borderRadius: '8px',
              width: '100%',
              maxWidth: '400px',
              textAlign: 'center',
              border: '1px solid #c3e6cb'
            }}>
              {successMessage}
            </div>
          )}

          <FormBody onFinish={handleFormSubmit} initialValues={formData}>
            <FormInputItem 
              label="Vendor ID" 
              placeholder="COMP001" 
              name='userId' 
              onChange={handleFormValueChange}
              value={formData.userId}
              required 
            />
            <FormInputItem 
              type='password' 
              label="Password" 
              placeholder="*****" 
              name='password' 
              onChange={handleFormValueChange} 
              required 
            />
            {messageText && (
              <p className="text-red-500" style={{ 
                backgroundColor: '#fee2e2', 
                padding: '10px', 
                borderRadius: '6px',
                border: '1px solid #fecaca'
              }}>
                {messageText}
              </p>
            )}
            <div className='custom-btn'>
              <Btn htmlType="submit" text="Sign In"/>
            </div>
          </FormBody>
          <h2 className='text-gray-500 text-center'>
            Account credentials unavailable?<br />
            Request Admin for your credentials.
          </h2>
          <p className='text-gray-500 text-center'>
            New to us? <span className="text-sm text-blue-600 cursor-pointer hover:underline" onClick={handleRegisterRedirect}>Register here</span>
          </p>
        </main>
      </FormContainer>
    </>
  );
};

export default Login;