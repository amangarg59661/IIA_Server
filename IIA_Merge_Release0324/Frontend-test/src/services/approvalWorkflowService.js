/**
 * Approval Workflow Service
 * API service for Dynamic Indent Approval Workflow configuration
 */
import axios from 'axios';

// Base URLs for different API groups
const APPROVAL_LIMITS_URL = '/api/admin/approval-limits';
const DEPARTMENT_APPROVERS_URL = '/api/admin/department-approvers';
const FIELD_STATION_APPROVERS_URL = '/api/admin/field-station-approvers';
const FULL_CONFIG_URL = '/api/admin/approvers';

// ============================================
// APPROVAL LIMITS API
// ============================================

export const ApprovalLimitsService = {
  // Get all approval limits
  getAll: (workflowId) => axios.get(APPROVAL_LIMITS_URL , {
  params: { workflowId }}),

  // Get approval limit by ID
  getById: (limitId) => axios.get(`${APPROVAL_LIMITS_URL}/${limitId}`),

  // Get limits by role name
  getByRole: (roleName) => axios.get(`${APPROVAL_LIMITS_URL}/role/${encodeURIComponent(roleName)}`),

  // Get limits by category
  getByCategory: (category) => axios.get(`${APPROVAL_LIMITS_URL}/category/${category}`),

  // Get applicable limit for specific criteria
  getApplicable: ({ roleName, category, departmentName, location }) => {
    const params = new URLSearchParams();
    if (roleName) params.append('roleName', roleName);
    if (category) params.append('category', category);
    if (departmentName) params.append('departmentName', departmentName);
    if (location) params.append('location', location);
    return axios.get(`${APPROVAL_LIMITS_URL}/applicable?${params.toString()}`);
  },

  // Check if escalation is required
  checkEscalation: ({ roleName, category, amount }) => {
    return axios.get(
      `${APPROVAL_LIMITS_URL}/check-escalation?roleName=${encodeURIComponent(roleName)}&category=${category}&amount=${amount}`
    );
  },

  // Create new approval limit
  create: (data) => axios.post(APPROVAL_LIMITS_URL, data),

  // Update approval limit
  update: (limitId, data) => axios.put(`${APPROVAL_LIMITS_URL}/${limitId}`, data),

  // Delete approval limit
  delete: (limitId) => axios.delete(`${APPROVAL_LIMITS_URL}/${limitId}`),

  // Update status
  updateStatus: (limitId, isActive, updatedBy) => {
    return axios.put(`${APPROVAL_LIMITS_URL}/${limitId}/status?isActive=${isActive}&updatedBy=${encodeURIComponent(updatedBy)}`);
  },

  // Get distinct role names
  getRoles: () => axios.get(`${APPROVAL_LIMITS_URL}/roles`),

  // Get distinct categories
  getCategories: () => axios.get(`${APPROVAL_LIMITS_URL}/categories`)
};

// ============================================
// DEPARTMENT APPROVER MAPPINGS API
// ============================================

export const DepartmentApproversService = {
  // Get all mappings
  getAll: () => axios.get(DEPARTMENT_APPROVERS_URL),

  // Get mapping by ID
  getById: (mappingId) => axios.get(`${DEPARTMENT_APPROVERS_URL}/${mappingId}`),

  // Get mappings by department
  getByDepartment: (departmentName) => axios.get(`${DEPARTMENT_APPROVERS_URL}/department/${encodeURIComponent(departmentName)}`),

  // Get mappings by type (DEAN/HEAD_SEG)
  getByType: (approverType) => axios.get(`${DEPARTMENT_APPROVERS_URL}/type/${approverType}`),

  // Find appropriate approver for department and value
  findApprover: ({ departmentName, indentValue }) => {
    return axios.get(
      `${DEPARTMENT_APPROVERS_URL}/find-approver?departmentName=${encodeURIComponent(departmentName)}&indentValue=${indentValue}`
    );
  },

  // Get Dean for department
  getDean: (departmentName) => axios.get(`${DEPARTMENT_APPROVERS_URL}/department/${encodeURIComponent(departmentName)}/dean`),

  // Get Head SEG for department
  getHeadSEG: (departmentName) => axios.get(`${DEPARTMENT_APPROVERS_URL}/department/${encodeURIComponent(departmentName)}/head-seg`),

  // Create new mapping
  create: (data) => axios.post(DEPARTMENT_APPROVERS_URL, data),

  // Update mapping
  update: (mappingId, data) => axios.put(`${DEPARTMENT_APPROVERS_URL}/${mappingId}`, data),

  // Delete mapping
  delete: (mappingId) => axios.delete(`${DEPARTMENT_APPROVERS_URL}/${mappingId}`),

  // Get distinct departments
  getDepartments: () => axios.get(`${DEPARTMENT_APPROVERS_URL}/departments`),

  // Get all Deans
  getAllDeans: () => axios.get(`${DEPARTMENT_APPROVERS_URL}/deans`),

  // Get all Head SEGs
  getAllHeadSEGs: () => axios.get(`${DEPARTMENT_APPROVERS_URL}/head-segs`)
};

// ============================================
// FIELD STATION APPROVERS API (Engineer/Professor In-Charge)
// ============================================

export const FieldStationApproversService = {
  // Get all field station approvers
  getAll: () => axios.get(FIELD_STATION_APPROVERS_URL),

  // Get approver by ID
  getById: (id) => axios.get(`${FIELD_STATION_APPROVERS_URL}/${id}`),

  // Get approvers by station
  getByStation: (fieldStationName) => axios.get(`${FIELD_STATION_APPROVERS_URL}/station/${encodeURIComponent(fieldStationName)}`),

  // Get by station and type
  getByStationAndType: (stationName, inchargeType) => {
    return axios.get(`${FIELD_STATION_APPROVERS_URL}/station/${encodeURIComponent(stationName)}/type/${inchargeType}`);
  },

  // Get all Engineer In-Charges
  getEngineerIncharges: () => axios.get(`${FIELD_STATION_APPROVERS_URL}/engineer-incharges`),

  // Get all Professor In-Charges
  getProfessorIncharges: () => axios.get(`${FIELD_STATION_APPROVERS_URL}/professor-incharges`),

  // Get by type
  getByType: (inchargeType) => axios.get(`${FIELD_STATION_APPROVERS_URL}/type/${inchargeType}`),

  // Create new approver
  create: (data) => axios.post(FIELD_STATION_APPROVERS_URL, data),

  // Update approver
  update: (id, data) => axios.put(`${FIELD_STATION_APPROVERS_URL}/${id}`, data),

  // Delete approver
  delete: (id) => axios.delete(`${FIELD_STATION_APPROVERS_URL}/${id}`),

  // Get distinct field stations
  getStations: () => axios.get(`${FIELD_STATION_APPROVERS_URL}/stations`),

  // Check if location is a field station
  isFieldStation: (location) => axios.get(`${FIELD_STATION_APPROVERS_URL}/is-field-station?location=${encodeURIComponent(location)}`),

  // Get in-charge for location
  getInchargeForLocation: (location) => axios.get(`${FIELD_STATION_APPROVERS_URL}/location/${encodeURIComponent(location)}/incharge`)
};

// ============================================
// FULL WORKFLOW CONFIGURATION API
// ============================================

export const WorkflowConfigService = {
  // Get complete workflow configuration
  getFullConfig: () => axios.get(`${FULL_CONFIG_URL}/full-config`),

  // Get config for specific workflow
  getConfigForWorkflow: (workflowId) => axios.get(`${FULL_CONFIG_URL}/full-config/workflow/${workflowId}`)
};

// ============================================
// HELPER FUNCTIONS
// ============================================

/**
 * Format currency amount for display
 * @param {number} amount - Amount to format
 * @returns {string} Formatted amount with rupee symbol
 */
export const formatCurrency = (amount) => {
  if (amount === null || amount === undefined) return '-';
  return `₹${Number(amount).toLocaleString('en-IN')}`;
};

/**
 * Category options for dropdown
 */
export const CATEGORY_OPTIONS = [
  { label: 'Computer', value: 'COMPUTER' },
  { label: 'Non-Computer', value: 'NON_COMPUTER' },
  { label: 'Project', value: 'PROJECT' },
  { label: 'All', value: 'ALL' }
];

/**
 * Approver type options for department mapping
 */
export const APPROVER_TYPE_OPTIONS = [
  { label: 'Dean', value: 'DEAN' },
  { label: 'Head SEG', value: 'HEAD_SEG' }
];

/**
 * In-charge type options for field stations
 */
export const INCHARGE_TYPE_OPTIONS = [
  { label: 'Engineer In-Charge', value: 'ENGINEER_INCHARGE' },
  { label: 'Professor In-Charge', value: 'PROFESSOR_INCHARGE' }
];

/**
 * Mode of Procurement options (mandatory field)
 */
export const MODE_OF_PROCUREMENT_OPTIONS = [
  { label: 'GeM (Government e-Marketplace)', value: 'GEM' },
  { label: 'Open Tender', value: 'OPEN_TENDER' },
  { label: 'Limited Tender', value: 'LIMITED_TENDER' },
  { label: 'Single Tender', value: 'SINGLE_TENDER' },
  { label: 'Proprietary Purchase', value: 'PROPRIETARY' },
  { label: 'Rate Contract', value: 'RATE_CONTRACT' },
  { label: 'Direct Purchase', value: 'DIRECT_PURCHASE' },
  { label: 'Quotation Based', value: 'QUOTATION' }
];

/**
 * Default approval limits (for reference)
 */
export const DEFAULT_APPROVAL_LIMITS = {
  PURCHASE_HEAD: 50000,
  HEAD_SEG: 100000,
  DEAN: 150000
};

export default {
  ApprovalLimitsService,
  DepartmentApproversService,
  FieldStationApproversService,
  WorkflowConfigService,
  formatCurrency,
  CATEGORY_OPTIONS,
  APPROVER_TYPE_OPTIONS,
  INCHARGE_TYPE_OPTIONS,
  MODE_OF_PROCUREMENT_OPTIONS,
  DEFAULT_APPROVAL_LIMITS
};
