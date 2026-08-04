import { useState, useEffect } from 'react';
import axios from 'axios';

/**
 * Custom hook to fetch LOV (List of Values) dynamically from backend
 * Uses the simplified frontend API endpoints
 *
 * @param {number} formId - The form ID (1-9)
 * @param {string} designatorName - The field name (e.g., 'category', 'uom')
 * @returns {Object} { lovValues, loading, error, refetch }
 */

// Form ID to Form Name mapping
const FORM_ID_TO_NAME = {
  1: 'AssetMaster',
  2: 'ContingencyPurchase',
  3: 'IndentCreation',
  4: 'EmployeeRegistration',
  5: 'JobMaster',
  6: 'MaterialMaster',
  7: 'VendorMaster',
  8: 'PurchaseOrder',
  9: 'TenderRequest',
  12: 'PaymentVoucher',
};

export const useLOVValues = (formId, designatorName) => {
  const [lovValues, setLovValues] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchLOVValues = async () => {
    if (!formId || !designatorName) {
      setLovValues([]);
      return;
    }

    try {
      setLoading(true);
      setError(null);

      // Get form name from form ID
      const formName = FORM_ID_TO_NAME[formId];

      if (!formName) {
        console.warn(`Invalid form ID: ${formId}`);
        setLovValues([]);
        setLoading(false);
        return;
      }

      // Use the simplified frontend API endpoint
      const response = await axios.get(`/api/lov/${formName}/${designatorName}`);

      console.log(`✅ LOV values fetched for ${formName}.${designatorName}:`, response.data);

      let lovData = [];

      // Handle different response formats
      if (response.data.status === 'success' && response.data.data) {
        lovData = response.data.data;
      } else if (response.data.responseData) {
        lovData = response.data.responseData;
      } else if (Array.isArray(response.data)) {
        lovData = response.data;
      }

      // Map backend LOV format to frontend format
      const mappedValues = lovData.map(lov => ({
        lovId: lov.lovId,
        lovValue: lov.lovValue || lov.value,
        lovDisplayValue: lov.lovDisplayValue || lov.displayValue || lov.lovValue,
        displayOrder: lov.displayOrder,
        isActive: lov.isActive !== false, // Default to true if not specified
        isDefault: lov.isDefault
      }));

      // Sort by display order - backend now returns only active LOVs
      const allLOVs = mappedValues.sort((a, b) => (a.displayOrder || 0) - (b.displayOrder || 0));

      console.log(`✅ Loaded ${allLOVs.length} active LOV values for ${designatorName}`);
      setLovValues(allLOVs);

    } catch (err) {
      console.error(`❌ Error fetching LOV values for ${designatorName}:`, err);
      console.error('Error details:', err.response?.data || err.message);
      setError(err);
      setLovValues([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchLOVValues();
  }, [formId, designatorName]);

  return {
    lovValues,
    loading,
    error,
    refetch: fetchLOVValues
  };
};

/**
 * Hook to fetch LOV values by form name instead of form ID
 * @param {string} formName - The form name (e.g., 'MaterialMaster', 'JobMaster')
 * @param {string} designatorName - The field name (e.g., 'category', 'uom')
 * @returns {Object} { lovValues, loading, error, refetch }
 */
export const useLOVValuesByFormName = (formName, designatorName) => {
  const [lovValues, setLovValues] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchLOVValues = async () => {
    if (!formName || !designatorName) {
      setLovValues([]);
      return;
    }

    try {
      setLoading(true);
      setError(null);

      // Use the simplified frontend API endpoint directly with form name
      const response = await axios.get(`/api/lov/${formName}/${designatorName}`);

      console.log(`✅ LOV values fetched for ${formName}.${designatorName}:`, response.data);

      let lovData = [];

      // Handle different response formats
      if (response.data.status === 'success' && response.data.data) {
        lovData = response.data.data;
      } else if (response.data.responseData) {
        lovData = response.data.responseData;
      } else if (Array.isArray(response.data)) {
        lovData = response.data;
      }

      // Map backend LOV format to frontend format
      const mappedValues = lovData.map(lov => ({
        lovId: lov.lovId,
        lovValue: lov.lovValue || lov.value,
        lovDisplayValue: lov.lovDisplayValue || lov.displayValue || lov.lovValue,
        displayOrder: lov.displayOrder,
        isActive: lov.isActive !== false, // Default to true if not specified
        isDefault: lov.isDefault
      }));

      // Sort by display order - backend now returns only active LOVs
      const allLOVs = mappedValues.sort((a, b) => (a.displayOrder || 0) - (b.displayOrder || 0));

      console.log(`✅ Loaded ${allLOVs.length} active LOV values for ${designatorName}`);
      setLovValues(allLOVs);

    } catch (err) {
      console.error(`❌ Error fetching LOV values for ${designatorName}:`, err);
      console.error('Error details:', err.response?.data || err.message);
      setError(err);
      setLovValues([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchLOVValues();
  }, [formName, designatorName]);

  return {
    lovValues,
    loading,
    error,
    refetch: fetchLOVValues
  };
};

/**
 * Hook to fetch all LOV values for a form at once
 * @param {string} formName - The form name (e.g., 'MaterialMaster')
 * @returns {Object} { dropdowns, loading, error, refetch }
 */
export const useBulkLOVValues = (formName) => {
  const [dropdowns, setDropdowns] = useState({});
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchBulkLOVValues = async () => {
    if (!formName) {
      setDropdowns({});
      return;
    }

    try {
      setLoading(true);
      setError(null);

      // Use bulk fetch endpoint
      const response = await axios.get(`/api/lov/form/${formName}`);

      console.log(`✅ Bulk LOV values fetched for ${formName}:`, response.data);

      let lovData = {};

      if (response.data.status === 'success' && response.data.data) {
        lovData = response.data.data;
      } else if (response.data.responseData) {
        lovData = response.data.responseData;
      } else {
        lovData = response.data;
      }

      setDropdowns(lovData);

    } catch (err) {
      console.error(`❌ Error fetching bulk LOV values for ${formName}:`, err);
      setError(err);
      setDropdowns({});
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchBulkLOVValues();
  }, [formName]);

  return {
    dropdowns,
    loading,
    error,
    refetch: fetchBulkLOVValues
  };
};

export default useLOVValues;
