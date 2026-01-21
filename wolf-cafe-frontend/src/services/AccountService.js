import axios from 'axios';
import { getToken } from './AuthService';

const BASE_AUTH_URL = 'http://localhost:8080/api/auth';

// --- Axios interceptor to attach token ---
axios.interceptors.request.use(
  (config) => {
    const token = getToken();
    if (token) config.headers['Authorization'] = token; // do not add 'Bearer'
    return config;
  },
  (error) => Promise.reject(error)
);

// --- Account API calls ---

// Create a staff account (admin only)
export const saveAccount = (account) =>
  axios.post(`${BASE_AUTH_URL}/register/staff`, account);

// Wrapper for frontend to register staff (matches AddAccount component)
export const registerStaffAPICall = (account) => saveAccount(account);

// Get all staff accounts (admin only)
export const getAllAccounts = () =>
  axios.get(`${BASE_AUTH_URL}/staff`);

// Get any user by ID (admin only)
export const getAccountById = (id) =>
  axios.get(`${BASE_AUTH_URL}/${id}`);

// Update a user account (admin only)
export const updateAccount = (id, account) =>
  axios.put(`${BASE_AUTH_URL}/user/update/${id}`, account);

// Delete a user account (admin only)
export const deleteAccountById = (id) =>
  axios.delete(`${BASE_AUTH_URL}/user/delete/${id}`);

// Get all users (admin only)
export const getAllUsers = () =>
  axios.get(`${BASE_AUTH_URL}/all`);
