// import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
// import axios from 'axios';

// export const fetchMasters = createAsyncThunk(
//   'masters/fetchMasters',
//   async () => {
//     try {
//       const [uomResponse, locationResponse, vendorResponse, materialResponse] = await Promise.all([
//         axios.get('/api/uom-master'),
//         axios.get('/api/location-master'),
//         axios.get('/api/vendor-master'),
//         axios.get('/api/material-master')
//       ]);

//       // Extract unique categories and subcategories
//       const categories = [...new Set(materialResponse.data.responseData.map(item => item.category))].map(item => ({label: item, value: item}));
//       const subCategories = [...new Set(materialResponse.data.responseData.map(item => item.subCategory))].map(item => ({label: item, value: item}));

//       return {
//         uomMaster: uomResponse.data.responseData,
//         locationMaster: locationResponse.data.responseData,
//         vendorMaster: vendorResponse.data.responseData,
//         materialMaster: materialResponse.data.responseData,
//         categoryMaster: categories,
//         subCategoryMaster: subCategories
//       };
//     } catch (error) {
//       throw error;
//     }
//   }
// );

// const masterSlice = createSlice({
//   name: 'masters',
//   initialState: {
//     uomMaster: [],
//     locationMaster: [],
//     vendorMaster: [],
//     materialMaster: [],
//     categoryMaster: [],
//     subCategoryMaster: [],
//     loading: false,
//     error: null
//   },
//   reducers: {},
//   extraReducers: (builder) => {
//     builder
//       .addCase(fetchMasters.pending, (state) => {
//         state.loading = true;
//       })
//       .addCase(fetchMasters.fulfilled, (state, action) => {
//         state.loading = false;
//         state.uomMaster = action.payload.uomMaster;
//         state.locationMaster = action.payload.locationMaster;
//         state.vendorMaster = action.payload.vendorMaster;
//         state.materialMaster = action.payload.materialMaster;
//         state.categoryMaster = action.payload.categoryMaster;
//         state.subCategoryMaster = action.payload.subCategoryMaster;
//         state.error = null;
//       })
//       .addCase(fetchMasters.rejected, (state, action) => {
//         state.loading = false;
//         state.error = action.error.message;
//       });
//   }
// });

// export default masterSlice.reducer;

import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import axios from 'axios';

// Axios instance for master-data APIs
const api = axios.create({
  baseURL: 'http://localhost:8081/astro-service/api',
});

/**
 * Thunk: Fetch UOM, locations, vendors, materials in parallel,
 * then build dropdown-friendly arrays.
 */
export const fetchMasters = createAsyncThunk(
  'masters/fetch',
  async (_, { rejectWithValue }) => {
    try {
      const endpoints = ['/uom-master', '/location-master', '/vendor-master', '/material-master'];
      const [uomRes, locRes, vendorRes, matRes] = await Promise.all(
        endpoints.map((ep) => api.get(ep))
      );

      const materials = matRes.data.responseData || [];
      const vendors = vendorRes.data.responseData || [];

      const toOptions = (arr, key) =>
        Array.from(new Set(arr.map((i) => i[key]))).map((val) => ({ label: val, value: val }));

      return {
        uomMaster: uomRes.data.responseData,
        locationMaster: locRes.data.responseData,
        vendorMaster: vendors.map((v) => ({ label: v.vendorId, value: v.vendorId })),
        materialMaster: materials,
        categoryMaster: toOptions(materials, 'category'),
        subCategoryMaster: toOptions(materials, 'subCategory'),
      };
    } catch (err) {
      return rejectWithValue(err.message);
    }
  }
);

const masterSlice = createSlice({
  name: 'masters',
  initialState: {
    uomMaster: [],
    locationMaster: [],
    vendorMaster: [],
    materialMaster: [],
    categoryMaster: [],
    subCategoryMaster: [],
    loading: false,
    error: null,
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchMasters.pending, (state) => {
        state.loading = true;
      })
      .addCase(fetchMasters.fulfilled, (state, { payload }) => {
        state.loading = false;
        state.error = null;
        Object.assign(state, payload);
      })
      .addCase(fetchMasters.rejected, (state, { payload }) => {
        state.loading = false;
        state.error = payload;
      });
  },
});

export default masterSlice.reducer;