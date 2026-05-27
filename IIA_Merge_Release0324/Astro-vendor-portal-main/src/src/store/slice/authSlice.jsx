// import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
// import axios from 'axios';

// const initialState = {
//   userRoleId: null,
//   roleId: null,
//   role: null,
//   userId: null,
//   readPermission: false,
//   writePermission: false,
//   loading: false,
//   error: null,
//   locationId: null,
//   userName: null,
//   mobileNumber: null,
//   email: null
// };

// export const login = createAsyncThunk(
//   'auth/login',
//   async (formData, thunkAPI) => {
//     try {
//       const response = await axios.post(
//         '/login',
//         formData
//       );
//       const data = response.data;
      
//       // Check for successful status (assuming statusCode === 0 means success)
//       if (data.responseStatus?.statusCode !== 0) {
//         return thunkAPI.rejectWithValue(
//           data.responseStatus?.message || 'Login failed'
//         );
//       }
      
//       // Return the responseData that contains userRoleId, roleId, role, userId, etc.
//       return data.responseData;
//     } catch (error) {
//       return thunkAPI.rejectWithValue(
//         error.response?.data || error.message
//       );
//     }
//   }
// );

// const authSlice = createSlice({
//   name: 'auth',
//   initialState,
//   reducers: {
//     logout(state) {
//       // Reset all authentication-related state fields
//       state.userRoleId = null;
//       state.roleId = null;
//       state.role = null;
//       state.userId = null;
//       state.readPermission = false;
//       state.writePermission = false;
//       state.loading = false;
//       state.error = null;
//       state.userName = null;
//       state.mobileNumber = null;
//       state.email = null;
//       state.locationId = null;
//     }
//   },
//   extraReducers: (builder) => {
//     builder
//       .addCase(login.pending, (state) => {
//         state.loading = true;
//         state.error = null;
//       })
//       .addCase(login.fulfilled, (state, action) => {
//         state.loading = false;
//         const {
//           userRoleId,
//           roleId,
//           role,
//           userId,
//           readPermission,
//           writePermission
//         } = action.payload;
//         state.userRoleId = userRoleId;
//         state.roleId = roleId;
//         state.role = role;
//         state.userId = userId;
//         state.readPermission = readPermission;
//         state.writePermission = writePermission;
//         state.locationId = "BNG"
//         state.userName = action?.payload?.userName;
//         state.email = action?.payload?.email;
//         state.mobileNumber = action?.payload?.mobileNumber;
//       })
//       .addCase(login.rejected, (state, action) => {
//         state.loading = false;
//         state.error = action.payload || action.error.message;
//       });
//   }
// });

// export const { logout } = authSlice.actions;
// export default authSlice.reducer;

// store/slice/authSlice.jsx
/*import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import axios from 'axios';

export const login = createAsyncThunk(
  'auth/login',
  async ({ vendorId, password }, { rejectWithValue }) => {
    try {
      const payload = { vendorId, password };
      console.log('→ sending to vendor-login:', payload);
      const { data } = await axios.post(
        `http://localhost:8081/astro-service/api/vendor-quotation/VendorStatus/${vendorId}`,
        payload
      );

      if (data.responseStatus?.statusCode !== 0) {
        return rejectWithValue(data.responseStatus.message || 'Login failed');
      }
      return data.responseData;
    } catch (err) {
      const msg = err.response?.data?.message || err.message;
      return rejectWithValue(msg);
    }
  }
);

const initialState = {
  vendorId: null,
  emailAddress: null,
  emailSent: false,
  createdDate: null,
  userRoleId: null,
  roleId: null,
  role: null,
  readPermission: false,
  writePermission: false,
  loading: false,
  error: null,
};

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    logout: () => initialState,
  },
  extraReducers: (builder) => {
    builder
      .addCase(login.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(login.fulfilled, (state, { payload }) => {
        console.log("Login successful. Payload:", payload); 
        state.loading = false;
        Object.assign(state, {
          vendorId: payload.vendorId,
         // emailAddress: payload.emailAddress,
          emailSent: payload.emailSent,
          createdDate: payload.createdDate,
          status: payload.status,
        //  userRoleId: payload.userRoleId,
        //  roleId: payload.roleId,
        //  role: payload.role,
        //  readPermission: payload.readPermission,
         // writePermission: payload.writePermission,
        });
      })
      .addCase(login.rejected, (state, { payload, error }) => {
        state.loading = false;
        state.error = payload || error.message;
      });
  },
});

export const { logout } = authSlice.actions;
export default authSlice.reducer;
*/
import { createSlice } from '@reduxjs/toolkit';

const initialState = {
  vendorId: null,
  emailSent: false,
  createdDate: null,
  status: null,
  isFirstLogin: null,
  isTempPassword: null,
  loading: false,
  error: null,
};

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    logout: () => initialState,

    setVendor: (state, { payload }) => {
      state.vendorId = payload.vendorId;
      state.emailSent = payload.emailSent;
      state.createdDate = payload.createdDate;
      state.status = payload.status;
      state.isFirstLogin = payload.isFirstLogin;
      state.isTempPassword = payload.isTempPassword;
    },

    // New action to update first login status after password change
    setPasswordChanged: (state) => {
      state.isFirstLogin = false;
      state.isTempPassword = false;
    },
  },
});

export const { logout, setVendor, setPasswordChanged } = authSlice.actions;
export default authSlice.reducer;