import React, { useState, useEffect } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import axios from 'axios';

// --------------------- Theme ---------------------
import { ThemeProvider } from './components/ThemeContext'; // ThemeProvider for dynamic themes

// --------------------- Components ---------------------
import HeaderComponent from './components/HeaderComponent';          // Staff/Admin header
import GuestHeaderComponent from './components/GuestHeaderComponent'; // Guest/Customer header
import FooterComponent from './components/FooterComponent';
import ManageAccountsComponent from './components/ManageAccountsComponent';
import ManageAccountComponent from './components/ManageAccountComponent';
import ListItemsComponent from './components/ListItemsComponent';
import AddItemComponent from './components/AddItemComponent';
import EditItemComponent from './components/EditItemComponent';
import ItemComponent from './components/ItemComponent';
import RegisterComponent from './components/RegisterComponent';
import LoginComponent from './components/LoginComponent';
import InventoryComponent from './components/InventoryComponent';
import OrdersWrapper from './components/OrdersWrapper';
import CreateOrderComponent from './components/CreateOrderComponent';
import PaymentScreenComponent from './components/PaymentScreenComponent';

import { isUserLoggedIn, isCustomerUser } from './services/AuthService';

// --------------------- Axios Interceptor ---------------------
axios.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) config.headers['Authorization'] = `Bearer ${token}`;
    return config;
  },
  (error) => Promise.reject(error)
);

// --------------------- Authenticated Route ---------------------
function AuthenticatedRoute({ children, allowedRoles = [] }) {
  if (!isUserLoggedIn()) return <Navigate to="/" />;

  const role = localStorage.getItem('role');
  if (allowedRoles.length && !allowedRoles.includes(role)) return <Navigate to="/" />;

  return children;
}

// --------------------- Main App ---------------------
function App() {
  const [role, setRole] = useState(localStorage.getItem('role') || null);

  // ----- Refresh trigger for orders -----
  const [ordersRefreshTrigger, setOrdersRefreshTrigger] = useState(0);
  const handleOrderSubmitted = () => setOrdersRefreshTrigger(prev => prev + 1);

  // Listen to role changes in localStorage (login/logout)
  useEffect(() => {
    const handleStorageChange = () => setRole(localStorage.getItem('role') || null);
    window.addEventListener('storage', handleStorageChange);
    return () => window.removeEventListener('storage', handleStorageChange);
  }, []);

  // --------------------- Header ---------------------
  const renderHeader = () => {
    // Guests (role === null) and customers see the guest header
    if (!role || isCustomerUser()) return <GuestHeaderComponent />;
    // Staff/Admin see the staff/admin header
    return <HeaderComponent />;
  };

  return (
    <ThemeProvider> {/* Wrap everything in ThemeProvider */}
      <BrowserRouter>
        {renderHeader()}

        <Routes>
          {/* Public Routes */}
          <Route path="/" element={<LoginComponent setRole={setRole} />} />
          <Route path="/login" element={<LoginComponent setRole={setRole} />} />
          <Route path="/register" element={<RegisterComponent />} />
          <Route path="/orders/new" element={<CreateOrderComponent />} />
          <Route path="/orders/payment" element={<PaymentScreenComponent onOrderSubmitted={handleOrderSubmitted} />} />

          {/* Orders - Guest or Customer/Staff/Admin */}
          <Route
            path="/orders"
            element={<OrdersWrapper role={role} refreshTrigger={ordersRefreshTrigger} />}
          />

          {/* Items - Staff/Admin Only */}
          <Route
            path="/items"
            element={
              <AuthenticatedRoute allowedRoles={['ROLE_STAFF', 'ROLE_ADMIN']}>
                <ListItemsComponent />
              </AuthenticatedRoute>
            }
          />
          <Route
            path="/add-item"
            element={
              <AuthenticatedRoute allowedRoles={['ROLE_STAFF', 'ROLE_ADMIN']}>
                <AddItemComponent />
              </AuthenticatedRoute>
            }
          />
          <Route
            path="/edit-item/:id"
            element={
              <AuthenticatedRoute allowedRoles={['ROLE_STAFF', 'ROLE_ADMIN']}>
                <EditItemComponent />
              </AuthenticatedRoute>
            }
          />
          <Route
            path="/update-item/:id"
            element={
              <AuthenticatedRoute allowedRoles={['ROLE_STAFF', 'ROLE_ADMIN']}>
                <ItemComponent />
              </AuthenticatedRoute>
            }
          />

          {/* Inventory - Staff/Admin Only */}
          <Route
            path="/inventory"
            element={
              <AuthenticatedRoute allowedRoles={['ROLE_STAFF', 'ROLE_ADMIN']}>
                <InventoryComponent />
              </AuthenticatedRoute>
            }
          />

          {/* Account Management */}
          <Route
            path="/manage-accounts"
            element={
              <AuthenticatedRoute>
                <ManageAccountsComponent />
              </AuthenticatedRoute>
            }
          />
          <Route
            path="/manage-accounts/add-account"
            element={
              <AuthenticatedRoute>
                <ManageAccountComponent />
              </AuthenticatedRoute>
            }
          />
          <Route
            path="/manage-accounts/user/update/:id"
            element={
              <AuthenticatedRoute>
                <ManageAccountComponent />
              </AuthenticatedRoute>
            }
          />
        </Routes>

        <FooterComponent />
      </BrowserRouter>
    </ThemeProvider>
  );
}

export default App;
