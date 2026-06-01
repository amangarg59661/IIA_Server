import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import axios from 'axios';

export const fetchMasters = createAsyncThunk(
  'masters/fetchMasters',
  async () => {
    try {
      const [uomResponse, locationResponse, vendorResponse, materialResponse, locatorResponse, projectResponse, userResponse] = await Promise.all([
        axios.get('/api/uom-master'),
        axios.get('/api/location-master'),
        axios.get('/api/vendor-master'),
        axios.get('/api/material-master'),
        axios.get('/api/locator-master'),
        axios.get(`/api/project-master`),
        axios.get(`/api/userMaster`)
      ]);

      // Extract unique categories and subcategories
      const categories = [...new Set(materialResponse.data.responseData.map(item => item.category))].map(item => ({label: item, value: item}));
      const subCategories = [...new Set(materialResponse.data.responseData.map(item => item.subCategory))].map(item => ({label: item, value: item}));

      return {
        uomMaster: uomResponse.data.responseData,
        locationMaster: locationResponse.data.responseData,
        locatorMaster: locatorResponse.data.responseData,
        vendorMaster: vendorResponse.data.responseData,
        materialMaster: materialResponse.data.responseData,
        categoryMaster: categories,
        subCategoryMaster: subCategories,
        projectMaster: projectResponse.data.responseData,
        userMaster: userResponse.data.responseData
      };
    } catch (error) {
      throw error;
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
    locatorMaster: [],
    projectMaster: [],
    userMaster: [],
    loading: false,
    error: null
  },
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(fetchMasters.pending, (state) => {
        state.loading = true;
      })
      .addCase(fetchMasters.fulfilled, (state, action) => {
        state.loading = false;
        state.uomMaster = action.payload.uomMaster;
        state.locationMaster = action.payload.locationMaster;
        state.vendorMaster = action.payload.vendorMaster;
        state.materialMaster = action.payload.materialMaster;
        state.categoryMaster = action.payload.categoryMaster;
        state.subCategoryMaster = action.payload.subCategoryMaster;
        state.locatorMaster = action.payload.locatorMaster;
        state.projectMaster = action.payload.projectMaster;
        state.userMaster = action.payload.userMaster;
        state.error = null;
      })
      .addCase(fetchMasters.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      });
  }
});

export default masterSlice.reducer;