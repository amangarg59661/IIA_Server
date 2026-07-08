import React, { useState, useEffect } from 'react';
import Btn from '../../components/DKG_Btn';
import MyLogo from "../../assets/iia-logo.png";
import FormBody from '../../components/DKG_FormBody';
import FormInputItem from '../../components/DKG_FormInputItem';
import { useDispatch } from 'react-redux';
import { useNavigate, useLocation } from 'react-router-dom';
import FormContainer from '../../components/DKG_FormContainer';
import { fetchMasters } from '../../store/slice/masterSlice';
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

  const [showPassword, setShowPassword] = useState(false);
  const [messageText, setMessageText] = useState('');
  const [successMessage, setSuccessMessage] = useState('');

  useEffect(() => {
    if (location.state?.message) {
      setSuccessMessage(location.state.message);
      if (location.state?.vendorId) {
        setFormData(prev => ({
          ...prev,
          userId: location.state.vendorId
        }));
      }
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
      // Server-side login — password validated on backend
      const response = await axios.post(
        '/api/vendor-quotation/vendor-login',
        {
          vendorId: userId,
          password: password
        },
        {
          headers: {
            Authorization: ''
          }
        }
      );
      const data = response.data;
      const responseData = data.responseData;
      const status = responseData.status;

      if (status === "APPROVED" || status === "SUCCESS") {
        if (responseData.isFirstLogin === true) {
          message.info('Please change your temporary password to continue.');
          navigate('/change-password', {
            state: {
              vendorId: userId,
              isFirstLogin: true
            }
          });
          return;
        }

        dispatch(setVendor(responseData));
        navigate(`/vendor/${userId}`);
      } else if (status === "REJECTED") {
        const comments = responseData.comments || "Your request was rejected with no comments provided.";
        setMessageText(`Sorry, your request has been rejected. Reason: ${comments}`);
      } else if (status === "INVALID_CREDENTIALS") {
        setMessageText(`Incorrect Username Or Password.`);
      } else if (status === "AWAITING_APPROVAL") {
        if (responseData.isFirstLogin === true) {
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
        if (responseData.isFirstLogin === true) {
          message.info('Please change your temporary password first.');
          navigate('/change-password', {
            state: {
              vendorId: userId,
              isFirstLogin: true
            }
          });
          return;
        }
        const comments = responseData.comments || "Admin has requested changes to your registration.";
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
      } else if (error.response?.data?.responseStatus?.message) {
        setMessageText(error.response.data.responseStatus.message);
      } else {
        setMessageText("Error during login. Please try again.");
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
            <div className="relative w-full">
              <FormInputItem
                type={showPassword ? 'text' : 'password'}
                label="Password"
                placeholder="*****"
                name='password'
                onChange={handleFormValueChange}
                required
              />
              <button
                type="button"
                onClick={() => setShowPassword((prev) => !prev)}
                className="absolute right-3 top-[50%] -translate-y-1/2 text-sm text-gray-600"
                aria-label={showPassword ? 'Hide password' : 'Show password'}
              >
                {showPassword ? '🙈' : '👁️'}
              </button>
            </div>
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
