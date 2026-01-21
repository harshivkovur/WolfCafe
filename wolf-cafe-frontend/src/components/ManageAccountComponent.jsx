import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { getAccountById, updateAccount } from '../services/AccountService';
import { registerStaffAPICall } from '../services/AuthService';

const AddAccount = () => {
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [passwordConfirm, setPasswordConfirm] = useState('');
  const [errors, setErrors] = useState({});
  const { id } = useParams();
  const navigate = useNavigate();

  // --- Load account for edit ---
  useEffect(() => {
    if (id) {
      getAccountById(id)
        .then((response) => {
          const user = response.data;
          setName(user.name);
          setEmail(user.email);
          setUsername(user.username);
        })
        .catch((error) => console.error('Failed to load account:', error));
    }
  }, [id]);

  // --- Form validation ---
  const validateForm = () => {
    const newErrors = {};

    if (!name) newErrors.name = 'Name Field is Blank';
    if (!username) newErrors.username = 'Username Field is Blank';
    if (!email) newErrors.email = 'Email Field is Blank';
    else {
      const emailRegex = /^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$/;
      if (!emailRegex.test(email)) newErrors.email = 'Email is Invalid';
    }

    if (!id) {
      // Validate passwords only when creating
      if (!password) newErrors.password = 'Password Field is Blank';
      if (password !== passwordConfirm)
        newErrors.passwordConfirm = "Passwords Don't Match";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  // --- Submit handler ---
  const saveOrUpdateAccount = (e) => {
    e.preventDefault();
    if (!validateForm()) return;

    if (id) {
      // Edit existing account (no password)
      const accountData = { name, email, username };
      updateAccount(id, accountData)
        .then(() => navigate('/manage-accounts'))
        .catch((err) => console.error('Update failed:', err));
    } else {
      // Create new staff account (include password)
      registerStaffAPICall({
        name,
        username,
        email,
        password,
        confirmPassword: passwordConfirm,
      })
        .then(() => navigate('/manage-accounts'))
        .catch((err) => console.error('Create failed:', err));
    }
  };

  const pageTitle = id ? 'Edit User' : 'Create Staff Account';
  const pageButton = id ? 'Update Account' : 'Create Account';

  return (
    <div className="container">
      <br />
      <br />
      <div className="row">
        <div className="card col-md-6 offset-md-3">
          <h2 className="text-center">{pageTitle}</h2>
          <div className="card-body">
            <form onSubmit={saveOrUpdateAccount}>
              <div className="form-group mb-2">
                <label className="form-label">Name:</label>
                <input
                  type="text"
                  className={`form-control ${errors.name ? 'is-invalid' : ''}`}
                  placeholder="Enter Name"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                />
                {errors.name && <div className="p-2 mb-2 bg-danger text-white">{errors.name}</div>}
              </div>

              <div className="form-group mb-2">
                <label className="form-label">Email:</label>
                <input
                  type="text"
                  className={`form-control ${errors.email ? 'is-invalid' : ''}`}
                  placeholder="Enter Email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                />
                {errors.email && <div className="p-2 mb-2 bg-danger text-white">{errors.email}</div>}
              </div>

              <div className="form-group mb-2">
                <label className="form-label">Username:</label>
                <input
                  type="text"
                  className={`form-control ${errors.username ? 'is-invalid' : ''}`}
                  placeholder="Enter Username"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                />
                {errors.username && <div className="p-2 mb-2 bg-danger text-white">{errors.username}</div>}
              </div>

              {!id && (
                <>
                  <div className="form-group mb-2">
                    <label className="form-label">Password:</label>
                    <input
                      type="password"
                      className={`form-control ${errors.password ? 'is-invalid' : ''}`}
                      placeholder="Enter Password"
                      value={password}
                      onChange={(e) => setPassword(e.target.value)}
                    />
                    {errors.password && (
                      <div className="p-2 mb-2 bg-danger text-white">{errors.password}</div>
                    )}
                  </div>

                  <div className="form-group mb-2">
                    <label className="form-label">Re-enter Password:</label>
                    <input
                      type="password"
                      className={`form-control ${errors.passwordConfirm ? 'is-invalid' : ''}`}
                      placeholder="Re-Enter Password"
                      value={passwordConfirm}
                      onChange={(e) => setPasswordConfirm(e.target.value)}
                    />
                    {errors.passwordConfirm && (
                      <div className="p-2 mb-2 bg-danger text-white">{errors.passwordConfirm}</div>
                    )}
                  </div>
                </>
              )}

              <button type="submit" className="btn btn-info">
                {pageButton}
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AddAccount;
