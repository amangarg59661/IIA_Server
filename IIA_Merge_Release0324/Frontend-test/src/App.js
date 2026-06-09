import React, { useEffect } from 'react';
import Routes from './pages/route/Routes';
import axios from 'axios';
import { useDispatch } from 'react-redux';
import { fetchMasters } from './store/slice/masterSlice';
import store from './store';
import { logout } from './store/slice/authSlice';
import { message } from 'antd';

export const baseURL = "http://localhost:8081/astro-service";
axios.defaults.baseURL = baseURL;

// Re-entry guard: prevent multiple 401s from triggering multiple logouts
let isLoggingOut = false;

// Send Authorization header with every request
axios.interceptors.request.use((config) => {
  const token = store.getState().auth?.token || localStorage.getItem('token');
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`;
  }
  return config;
});

// Handle 401 responses — token expired or invalid
axios.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Skip if already logging out or on login page
      if (isLoggingOut || window.location.pathname === '/login') {
        return Promise.reject(error);
      }
      isLoggingOut = true;
      store.dispatch(logout());
      message.error('Session expired. Redirecting to login...');
      setTimeout(() => {
        window.location.href = '/login';
      }, 1500);
    }
    return Promise.reject(error);
  }
);

function App() {
  const dispatch = useDispatch();

  useEffect(() => {
    dispatch(fetchMasters());
  }, [dispatch])

  return (
    <Routes />
  );
}

export default App;
