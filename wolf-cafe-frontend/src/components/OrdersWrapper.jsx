import React, { useState, useContext } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import ListOrdersComponent from './ListOrdersComponent';
import ListGuestOrdersComponent from './ListGuestOrdersComponent';
import RoleWrapper from './RoleWrapper';
import { ThemeContext } from '../components/ThemeContext';

const OrdersWrapper = ({ role = null, refreshTrigger }) => {
  const navigate = useNavigate();
  const location = useLocation();

  // --- Theme from context ---
  const { currentTheme } = useContext(ThemeContext); // e.g., "theme-light", "theme-ncstate"

  /** Trigger to refresh guest orders after payment */
  const [localRefresh, setLocalRefresh] = useState(0);

  // Check if we came from payment with a success message
  const paymentMessage = location.state?.message || null;

  // --- Handler to navigate to create order page ---
  const handleCreateOrder = () => {
    console.log('[OrdersWrapper] Create New Order button clicked');
    navigate('/orders/new');
  };

  /** Callback to trigger refresh in ListGuestOrdersComponent */
  const handleRefreshOrders = () => {
    setLocalRefresh(prev => prev + 1);
  };

  /** Helper: Determine theme-specific "Create New Order" button class */
  const getCreateButtonClass = () => {
    if (currentTheme === 'theme-ncstate') return 'btn btn-lg px-5 py-2 btn-ncstate-invert';
    return 'btn btn-primary btn-lg px-5 py-2';
  };

  /** Guest / Customer view */
  const guestComponent = (
    <div className="container mt-5">
      <div className="text-center mb-4">
        <h1 style={{ fontSize: '2.5rem', fontWeight: 'bold' }}>My Orders</h1>
      </div>

      {paymentMessage && (
        <div className="alert alert-success text-center">
          {paymentMessage}
        </div>
      )}

      <div className="text-center mb-4">
        <button
          className={getCreateButtonClass()}
          onClick={handleCreateOrder}
        >
          Create New Order
        </button>
      </div>

      <ListGuestOrdersComponent
        refreshTrigger={refreshTrigger + localRefresh}
        onRefresh={handleRefreshOrders}
        currentTheme={currentTheme} // pass theme down to LGOC
      />
    </div>
  );

  /** Staff / Admin view */
  let staffComponent = null;
  if (role === 'ROLE_ADMIN') {
    staffComponent = <ListOrdersComponent currentTheme={currentTheme} showAdminControls={true} />;
  } else if (role === 'ROLE_STAFF') {
    staffComponent = <ListOrdersComponent currentTheme={currentTheme} showAdminControls={false} />;
  }

  return (
    <RoleWrapper
      role={role}
      guestComponent={guestComponent}
      staffComponent={staffComponent}
    />
  );
};

export default OrdersWrapper;
