import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import axios from 'axios';

const initialState = {
  userId: null,
  userName: null,
  email: null,
  mobileNumber: null,
  employeeDepartment: null,
  roles: [],
  role: "",
  roleId: null,
  isFirstLogin: false,
  token: localStorage.getItem('token') || null,
  loading: false,
  error: null
};


export const login = createAsyncThunk(
  'auth/login',
  async (formData, thunkAPI) => {
    try {
      const response = await axios.post(
        '/login',
        formData
      );
      const data = response.data;

      if (data.responseStatus?.statusCode !== 0) {
        return thunkAPI.rejectWithValue(
          data.responseStatus?.message || 'Login failed'
        );
      }

      return data.responseData;
    } catch (error) {
      return thunkAPI.rejectWithValue(
        error.response?.data || error.message
      );
    }
  }
);

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    logout(state) {
      state.userRoleId = null;
      state.roleId = null;
      state.role = null;
      state.userId = null;
      state.readPermission = false;
      state.writePermission = false;
      state.loading = false;
      state.error = null;
      state.userName = null;
      state.mobileNumber = null;
      state.email = null;
      state.locationId = null;
      state.employeeDepartment = null;
      state.token = null;
      localStorage.removeItem('token');
    },
     changeRole(state, action) {
      state.role = action.payload;
      state.roleId = state.roles.find(r => r.roleName === action.payload)?.roleId || null;
    },
    clearFirstLogin(state) {
      state.isFirstLogin = false;
    }
  },
  extraReducers: (builder) => {
    builder
      .addCase(login.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
     .addCase(login.fulfilled, (state, action) => {
        state.loading = false;
        const {
          userId,
          userName,
          email,
          mobileNumber,
          employeeDepartment,
          roles,
          isFirstLogin,
          token
        } = action.payload;

        state.userId = userId;
        state.userName = userName;
        state.email = email;
        state.mobileNumber = mobileNumber;
        state.employeeDepartment = employeeDepartment;
        state.roles = roles || [];
        state.role = roles?.[0]?.roleName || "";
        state.roleId = roles?.[0]?.roleId || null;
        state.isFirstLogin = isFirstLogin || false;
        state.token = token || null;

        // Persist token
        if (token) {
          localStorage.setItem('token', token);
        }
      })
      .addCase(login.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || action.error.message;
      });
  }
});

export const { logout, changeRole, clearFirstLogin } = authSlice.actions;
export default authSlice.reducer;
