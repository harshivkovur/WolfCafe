import React, { useContext } from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { isUserLoggedIn, logout } from '../services/AuthService';
import { ThemeContext } from '../components/ThemeContext';

/**
 * GuestHeaderComponent
 * - Header bar for guest users
 * - Theme dropdown appears left of Logout button and is vertically centered
 * - Fully respects theme-specific navbar colors
 */
const GuestHeaderComponent = () => {
  const isAuth = isUserLoggedIn();
  const navigate = useNavigate();

  const { theme, setThemeByName } = useContext(ThemeContext);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <header>
      <nav className="navbar navbar-expand-md">
        {/* Brand link */}
        <NavLink className="navbar-brand" to="/">
          WolfCafe
        </NavLink>

        {/* Left-aligned navigation links */}
        <div className="collapse navbar-collapse">
          <ul className="navbar-nav">
            {(
              <li className="nav-item">
                <NavLink to="/orders" className="nav-link">
                  Orders
                </NavLink>
              </li>
            )}
          </ul>
        </div>

        {/* Right-aligned items: Theme selector and Logout */}
        {isAuth && (
          <ul className="navbar-nav ms-auto d-flex align-items-center">
            {/* Theme selector dropdown */}
            <li className="nav-item me-2">
              <select
                value={theme}
                onChange={(e) => setThemeByName(e.target.value)}
                className="form-select form-select-sm"
                style={{ height: '36px' }} // ensures vertical alignment
              >
                <option value="light">Light</option>
                <option value="dark">Dark</option>
                <option value="ncstate">School Spirit</option>
                <option value="vaporwave">Vaporwave</option>
              </select>
            </li>

            {/* Logout button */}
            <li className="-item">
              <NavLink
                to="/login"
                className="logout-link"
                onClick={handleLogout}
              >
                Logout
              </NavLink>
            </li>

            {/* Show Register/Login for guests */}
        
          </ul>
        )}
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

export default GuestHeaderComponent;
