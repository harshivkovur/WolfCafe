import axios from 'axios';
import { isUserLoggedIn } from './AuthService';

const BASE_ITEM_URL = 'http://localhost:8080/api/items';
const BASE_INGREDIENT_URL = 'http://localhost:8080/api/ingredients';

// --------------------- Axios Interceptor ---------------------
// Automatically attach Authorization header if user is logged in
axios.interceptors.request.use(
  (config) => {
    if (isUserLoggedIn()) {
      const token = localStorage.getItem('token'); // only read when needed
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// ========== ITEM OPERATIONS ==========
export const saveItem = (item) => axios.post(BASE_ITEM_URL, item);
export const getAllItems = () => axios.get(BASE_ITEM_URL);
export const getItemById = (id) => axios.get(`${BASE_ITEM_URL}/${id}`);
export const updateItem = (id, item) => axios.put(`${BASE_ITEM_URL}/${id}`, item);
export const deleteItemById = (id) => axios.delete(`${BASE_ITEM_URL}/${id}`);
export const getItemByName = (name) =>
  axios.get(`${BASE_ITEM_URL}/name/${encodeURIComponent(name)}`);

// ========== INGREDIENT OPERATIONS ==========
export const getAllIngredients = () => axios.get(BASE_INGREDIENT_URL);
