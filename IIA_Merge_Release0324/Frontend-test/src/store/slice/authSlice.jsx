import { createSlice } from '@reduxjs/toolkit';

const initialState = {
  vendorId: null,
  emailSent: false,
  createdDate: null,
  status: null,
  isFirstLogin: null,
  isTempPassword: null,
  token: localStorage.getItem('vendorToken') || null,
  loading: false,
  error: null,
};

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    logout: (state) => {
      localStorage.removeItem('vendorToken');
      return { ...initialState, token: null };
    },

    setVendor: (state, { payload }) => {
      state.vendorId = payload.vendorId;
      state.emailSent = payload.emailSent;
      state.createdDate = payload.createdDate;
      state.status = payload.status;
      state.isFirstLogin = payload.isFirstLogin;
      state.isTempPassword = payload.isTempPassword;
      state.token = payload.token || null;

      // Persist token
      if (payload.token) {
        localStorage.setItem('vendorToken', payload.token);
      }
    },

    setPasswordChanged: (state) => {
      state.isFirstLogin = false;
      state.isTempPassword = false;
    },
  },
});

export const { logout, setVendor, setPasswordChanged } = authSlice.actions;
export default authSlice.reducer;
