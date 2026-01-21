import React, { useEffect, useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { getAllUsers, deleteAccountById } from '../services/AccountService';
import { isAdminUser } from '../services/AuthService';
import { ThemeContext } from './ThemeContext';

const ManageAccountsComponent = () => {
  const [accounts, setAccounts] = useState([]);
  const [errors, setErrors] = useState('');
  const navigate = useNavigate();
  const isAdmin = isAdminUser();
  const { currentTheme } = useContext(ThemeContext);

  // --- Fetch accounts on mount ---
  useEffect(() => {
    listAccounts();
  }, []);

  const listAccounts = () => {
    getAllUsers()
      .then((res) => {
        const sortedAccounts = res.data.sort((a, b) => {
          // Role priority: higher number = higher priority
          const rolePriority = (roles) => {
            if (roles.some((r) => r.name === 'ROLE_ADMIN')) return 3;
            if (roles.some((r) => r.name === 'ROLE_STAFF')) return 2;
            return 1; // CUSTOMER
          };

          const priorityDiff = rolePriority(b.roles) - rolePriority(a.roles);
          if (priorityDiff !== 0) return priorityDiff;

          // Alphabetical by name if same role priority
          return a.name.localeCompare(b.name);
        });

        setAccounts(sortedAccounts);
        setErrors('');
      })
      .catch((err) => {
        console.error('[ManageAccounts] Failed to fetch accounts:', err);
        setErrors('Failed to load accounts.');
      });
  };


  // --- Navigation handlers ---
  const handleCreateAccount = () => {
    navigate('/manage-accounts/add-account');
  };

  const handleUpdateAccount = (id) => {
    // FIXED: match backend endpoint for update
    navigate(`/manage-accounts/user/update/${id}`);
  };

  const handleDeleteAccount = (id) => {
    if (!window.confirm('Are you sure you want to delete this account?')) return;

    deleteAccountById(id)
      .then(() => listAccounts())
      .catch((err) => {
        console.error('[ManageAccounts] Failed to delete account:', err);
        setErrors('Failed to delete account.');
      });
  };

  // --- Theme-aware buttons ---
  const getButtonClass = (type = 'primary') => {
    switch (currentTheme) {
      case 'theme-ncstate':
        if (type === 'danger') return 'btn btn-outline-danger';
        if (type === 'info') return 'btn btn-outline-info';
        return 'btn btn-outline-light';
      case 'theme-dark':
        if (type === 'danger') return 'btn btn-danger';
        if (type === 'info') return 'btn btn-info';
        return 'btn btn-light';
      case 'theme-light':
      case 'theme-vaporwave':
        if (type === 'danger') return 'btn btn-danger';
        if (type === 'info') return 'btn btn-info';
        return 'btn btn-primary';
      default:
        return 'btn btn-primary';
    }
  };

  return (
    <div className="container mt-4">
      <h2 className="text-center mb-4">Manage Accounts</h2>

      {errors && <div className="alert alert-danger text-center">{errors}</div>}

      {/* Create Account button for admins */}
      {isAdmin && (
        <div className="text-center mb-3">
          <button className={`${getButtonClass()} btn-lg px-4`} onClick={handleCreateAccount}>
            Create Staff Account
          </button>
        </div>
      )}

      {/* Accounts table */}
      <table className="table table-bordered table-striped">
        <thead>
          <tr>
            <th>Name</th>
            <th>Username</th>
            <th>Email</th>
            <th>Role(s)</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {accounts.map((account) => (
            <tr key={account.id}>
              <td>{account.name}</td>
              <td>{account.username}</td>
              <td>{account.email}</td>
              <td>{account.roles.map((r) => r.name.replace('ROLE_', '')).join(', ')}</td>
              <td>
                {isAdmin && (
                  <>
                    <button
                      className={`${getButtonClass('info')} me-2`}
                      onClick={() => handleUpdateAccount(account.id)}
                    >
                      Update
                    </button>
                    <button
                      className={getButtonClass('danger')}
                      onClick={() => handleDeleteAccount(account.id)}
                    >
                      Delete
                    </button>
                  </>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default ManageAccountsComponent;
