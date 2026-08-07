import React, { useEffect } from 'react';
import Routes from './pages/route/Routes';
import axios from 'axios';
import { useDispatch } from 'react-redux';
import { fetchMasters } from './store/slice/masterSlice';
import store from './store';
import { logout } from './store/slice/authSlice';
import { message } from 'antd';

export const baseURL = "http://localhost:8081/astro-service";
// export const baseURL = "/astro-service";
// axios.defaults.baseURL = baseURL;
axios.defaults.baseURL = baseURL;

// Re-entry guard: prevent multiple 401s from triggering multiple logouts
// let isLoggingOut = false;

// // Send Authorization header with every request
// axios.interceptors.request.use((config) => {
//   const token = store.getState().auth?.token || localStorage.getItem('token');
//   if (token) {
//     config.headers['Authorization'] = `Bearer ${token}`;
//   }
//   return config;
// });
// Re-entry guard: prevent multiple 401s from triggering multiple logouts
let isLoggingOut = false;

// Endpoints that must NOT carry an Authorization header — no token exists yet
// (or a valid one shouldn't matter) when hitting these.
const PUBLIC_ENDPOINTS = ['/login'];

// Send Authorization header with every request, except public/unauthenticated ones.
axios.interceptors.request.use((config) => {
  const isPublicEndpoint =
    config.skipAuthHeader || PUBLIC_ENDPOINTS.some((endpoint) => config.url?.includes(endpoint));

  if (!isPublicEndpoint) {
    const token = store.getState().auth?.token || localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
  }
  return config;
});
// Handle 401 responses — token expired or invalid
axios.interceptors.response.use(
  (response) => response,
  (error) => {
    // if (error.response?.status === 401) {
    //   // Skip if already logging out or on login page
    //   if (isLoggingOut || window.location.pathname === '/login') {
    //     return Promise.reject(error);
    //   }
    //   isLoggingOut = true;
    //   store.dispatch(logout());
    if (error.response?.status === 401) {
      // Skip if already logging out or on login page
      if (isLoggingOut || window.location.pathname === '/login') {
        return Promise.reject(error);
      }
      isLoggingOut = true;
      // Clear the stale token so it can't leak into the next login attempt
      localStorage.removeItem('token');
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
