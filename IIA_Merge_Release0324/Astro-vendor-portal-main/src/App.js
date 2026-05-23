import React, { useEffect } from 'react';
import Routes from './pages/route/Routes';
import axios from 'axios';
import { useDispatch } from 'react-redux';
import { fetchMasters } from './store/slice/masterSlice';
import store from './store';
import { logout } from './store/slice/authSlice';

export const baseURL = "http://localhost:8081/astro-service";
axios.defaults.baseURL = baseURL;

// Send Authorization header with every request
axios.interceptors.request.use((config) => {
  const token = store.getState().auth?.token || localStorage.getItem('vendorToken');
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
      store.dispatch(logout());
      window.location.href = '/';
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
