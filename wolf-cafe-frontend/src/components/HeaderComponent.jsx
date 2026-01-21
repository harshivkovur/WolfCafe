import React, { useContext } from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { isUserLoggedIn, logout } from '../services/AuthService';
import { ThemeContext } from '../components/ThemeContext';

/**
 * HeaderComponent
 * - Fully respects theme-specific navbar colors
 * - Light/Dark: dark gray navbar
 * - School Spirit: red navbar
 * - Logout styled like brand (white text, hover highlight), normal weight
 */
const HeaderComponent = () => {
  const isAuth = isUserLoggedIn();
  const navigate = useNavigate();
  const { theme, setThemeByName } = useContext(ThemeContext);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <header>
      {/* Navbar adapts to theme via CSS */}
      <nav className="navbar navbar-expand-md">
        {/* Brand */}
        <NavLink className="navbar-brand" to="/">
          WolfCafe
        </NavLink>

        {/* Main nav links */}
        <div className="collapse navbar-collapse">
          <ul className="navbar-nav">
            {isAuth && (
              <>
                <li className="nav-item">
                  <NavLink to="/orders" className="nav-link">Orders</NavLink>
                </li>
                <li className="nav-item">
                  <NavLink to="/items" className="nav-link">Items</NavLink>
                </li>
                <li className="nav-item">
                  <NavLink to="/inventory" className="nav-link">Inventory</NavLink>
                </li>
                <li className="nav-item">
                  <NavLink to="/manage-accounts" className="nav-link">Manage Accounts</NavLink>
                </li>
              </>
            )}
          </ul>
        </div>

        {/* Right-aligned nav items */}
        {isAuth && (
          <ul className="navbar-nav ms-auto d-flex align-items-center">
            {/* Theme selector dropdown */}
            <li className="nav-item me-2">
              <select
                value={theme}
                onChange={(e) => setThemeByName(e.target.value)}
                className="form-select form-select-sm"
                style={{ height: '36px' }} // vertical alignment
              >
                <option value="light">Light</option>
                <option value="dark">Dark</option>
                <option value="ncstate">School Spirit</option>
                <option value="vaporwave">Vaporwave</option>
              </select>
            </li>

            {/* Logout */}
            <li className="logout-item">
              <NavLink
                to="/login"
                className="nav-link logout-link"
                onClick={handleLogout}
              >
                Logout
              </NavLink>
            </li>
          </ul>
        )}

        {/* Show Register/Login for guests */}
        {!isAuth && (
          <ul className="navbar-nav ms-auto">
            <li className="nav-item">
              <NavLink to="/register" className="nav-link">Register</NavLink>
            </li>
            <li className="nav-item">
              <NavLink to="/login" className="nav-link">Login</NavLink>
            </li>
          </ul>
        )}
      </nav>
    </header>
  );
};

export default HeaderComponent;
