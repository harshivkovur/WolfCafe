// src/components/LoginComponent.jsx
import React, { useState } from 'react';
import { loginAPICall, storeToken, saveLoggedInUser } from '../services/AuthService';
import { useNavigate } from 'react-router-dom';

const LoginComponent = ({ setRole }) => {
  const [usernameOrEmail, setUsernameOrEmail] = useState('');
  const [password, setPassword] = useState('');

  const navigate = useNavigate();

  const handleLoginForm = async (e) => {
    e.preventDefault();

    try {
      console.log('[LoginComponent] Logging in:', usernameOrEmail);

      // Call AuthService login
      const response = await loginAPICall(usernameOrEmail, password);

      // --- DEBUG: Print the full backend response ---
      console.log('[LoginComponent] Backend login response:', response);

      const token = 'Bearer ' + response.data.accessToken;
      const role = response.data.role;
      const userId = response.data.id; // make sure backend sends this
      const username = response.data.username || usernameOrEmail;

      if (!userId) {
        console.error('[LoginComponent] User ID missing in login response!', response.data);
        alert('Login failed: no user ID returned from backend.');
        return;
      }

      // Store token & user info
      storeToken(token);
      saveLoggedInUser(userId, username, role);

      // Update app role state
      if (setRole) setRole(role);

      // Redirect based on role
      if (role === 'ROLE_CUSTOMER' || role === 'ROLE_GUEST') navigate('/orders');
      else navigate('/items');

    } catch (error) {
      console.error('[LoginComponent] Login failed:', error);
      alert('Login failed. Please check your credentials.');
    }
  };

  return (
    <div className='container'>
      <br /><br />
      <div className='row'>
        <div className='col-md-6 offset-md-3'>
          <div className='card'>
            <div className='card-header'>
              <h2 className='text-center'>Login Form</h2>
            </div>
            <div className='card-body'>
              <form onSubmit={handleLoginForm}>
                <div className='row mb-3'>
                  <label className='col-md-3 control-label'>Username</label>
                  <div className='col-md-9'>
                    <input
                      type='text'
                      className='form-control'
                      placeholder='Enter username or email'
                      value={usernameOrEmail}
                      onChange={(e) => setUsernameOrEmail(e.target.value)}
                    />
                  </div>
                </div>

                <div className='row mb-3'>
                  <label className='col-md-3 control-label'>Password</label>
                  <div className='col-md-9'>
                    <input
                      type='password'
                      className='form-control'
                      placeholder='Enter password'
                      value={password}
                      onChange={(e) => setPassword(e.target.value)}
                    />
                  </div>
                </div>

                <div className='form-group mb-3 text-center'>
                  <button type='submit' className='btn btn-primary px-5'>Submit</button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LoginComponent;
