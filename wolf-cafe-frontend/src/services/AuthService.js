// src/services/AuthService.js
import axios from 'axios';

// --------------------- Backend URLs ---------------------
const BASE_AUTH_URL = 'http://localhost:8080/api/auth';

// --------------------- Token & User Utilities ---------------------
export const storeToken = (token) => {
  localStorage.setItem('token', token ? token.trim() : '');
};

export const getToken = () => {
  const token = localStorage.getItem('token');
  return token ? token.trim() : null;
};

// Save user including ID
export const saveLoggedInUser = (id, username, role) => {
  localStorage.setItem('userId', id);
  localStorage.setItem('username', username);
  localStorage.setItem('role', role);
};

// Returns username, role, AND id
export const getCurrentUser = () => {
  const id = localStorage.getItem('userId');
  const username = localStorage.getItem('username');
  const role = localStorage.getItem('role');
  if (!id || !username || !role) return null;
  return { id, username, role };
};

export const isAdminUser = () => localStorage.getItem('role') === 'ROLE_ADMIN';
export const isCustomerUser = () => localStorage.getItem('role') === 'ROLE_CUSTOMER';
export const isStaffUser = () => localStorage.getItem('role') === 'ROLE_STAFF';

// Get ID only (for PaymentScreen)
export const getCurrentUserID = () => localStorage.getItem('userId');

export const isUserLoggedIn = () => !!getToken();

export const logout = () => {
  localStorage.removeItem('token');
  localStorage.removeItem('userId');
  localStorage.removeItem('username');
  localStorage.removeItem('role');
};

// --------------------- Axios Interceptor ---------------------
axios.interceptors.request.use(
  (config) => {
    const token = getToken();
    if (token) {
      config.headers['Authorization'] = token; // Already prefixed with 'Bearer'
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// --------------------- Auth API Calls ---------------------
export const loginAPICall = (usernameOrEmail, password) => {
  return axios.post(`${BASE_AUTH_URL}/login`, { usernameOrEmail, password });
};

export const registerAPICall = (user) => {
  return axios.post(`${BASE_AUTH_URL}/register`, user);
};

export const registerStaffAPICall = (user) => {
  return axios.post(`${BASE_AUTH_URL}/register/staff`, user);
};

// --------------------- New Helper: Fetch User by ID ---------------------
// Needed for staff ListOrdersComponent to show customer names
export const getUserById = (userId) => {
  if (!userId) return Promise.reject('No userId provided');

  return axios.get(`${BASE_AUTH_URL}/${userId}`)
    .then(response => {
      return response;
    });
};

