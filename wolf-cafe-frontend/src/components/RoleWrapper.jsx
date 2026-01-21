import React, { useEffect } from 'react';

const RoleWrapper = ({ role, guestComponent, staffComponent }) => {
  useEffect(() => {
    console.log('[RoleWrapper] Rendering. Role:', role);
  }, [role]);


  // Treat ROLE_GUEST and ROLE_CUSTOMER as guest
  if (!role || role === 'ROLE_CUSTOMER') {
    return guestComponent;
  }

  return staffComponent;
};

export default RoleWrapper;
