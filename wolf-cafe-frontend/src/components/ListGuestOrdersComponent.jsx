import React, { useEffect, useState, useContext } from "react";
import { ThemeContext } from "../components/ThemeContext";
import { listOrders, updateOrderStatus } from "../services/OrdersService";
import { isUserLoggedIn, getCurrentUser } from "../services/AuthService";

const ListGuestOrdersComponent = ({ refreshTrigger, onRefresh }) => {
  const { currentTheme } = useContext(ThemeContext); 
  const [orders, setOrders] = useState(null);
  const [actionOrderId, setActionOrderId] = useState(null);
  const [showActionModal, setShowActionModal] = useState(false);
  const [actionType, setActionType] = useState(""); 
  const [error, setError] = useState("");

  useEffect(() => {
    fetchOrders();
  }, [refreshTrigger]);

  const fetchOrders = async () => {
    try {
      const ordersResponse = await listOrders();
      const allOrders = ordersResponse.data;

      const userLoggedIn = isUserLoggedIn();
      const currentUser = getCurrentUser();
      const userId = currentUser?.id ? parseInt(currentUser.id, 10) : null;

      let filteredOrders = allOrders;

      if (userLoggedIn && userId) {
        // Logged-in users see all their orders
        filteredOrders = allOrders.filter(order => order.customerId === userId);
      } else {
        // Guests only see today's orders **that have no customerId**
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        filteredOrders = allOrders.filter(order => {
          const orderDate = new Date(order.created);
          orderDate.setHours(0, 0, 0, 0);
          return orderDate.getTime() === today.getTime() && !order.customerId;
        });
      }

      filteredOrders.sort((a, b) => new Date(b.created) - new Date(a.created));
      setOrders(filteredOrders);
    } catch (err) {
      console.error("Failed to fetch orders:", err);
      setOrders([]);
    }
  };


  const formatCents = (cents) => {
    const value = parseInt(cents, 10) || 0;
    return `$${(value / 100).toFixed(2)}`;
  };

  const getFullTotal = (order) => {
    const subtotal = parseInt(order.subtotal, 10) || 0;
    const tax = parseInt(order.tax, 10) || 0;
    const tip = parseInt(order.tip, 10) || 0;
    return subtotal + tax + tip;
  };

  const capitalizeFirst = (str) => str ? str.charAt(0).toUpperCase() + str.slice(1) : "";

  const confirmAction = (orderId, type) => {
    setActionOrderId(orderId);
    setActionType(type);
    setShowActionModal(true);
  };

  const handleActionConfirmed = async () => {
    if (!actionOrderId || !actionType) return;

    const newStatus = actionType === "cancel" ? "canceled" : "picked up";

    try {
      await updateOrderStatus(actionOrderId, newStatus);

      setOrders(prevOrders =>
        prevOrders.map(order =>
          order.id === actionOrderId ? { ...order, status: newStatus } : order
        )
      );

      setShowActionModal(false);
      setActionOrderId(null);
      setActionType("");
      setError("");
      if (onRefresh) onRefresh();
    } catch (err) {
      console.error(`[LGOC] Failed to update order ${actionOrderId}:`, err);
      setError(`Failed to ${actionType} order. Please try again.`);
      setShowActionModal(false);
      setActionOrderId(null);
      setActionType("");
    }
  };

  if (!orders) return <p className="text-center">Loading orders...</p>;
  if (orders.length === 0) return <p className="text-center">No orders found.</p>;

  const getActionButtonClass = () => {
    switch (currentTheme) {
      case "theme-ncstate": return "btn btn-ncstate btn-sm ms-2";
      case "theme-vaporwave": return "btn btn-vaporwave btn-sm ms-2";
      case "theme-light": return "btn btn-light-theme btn-sm ms-2";
      case "theme-dark": return "btn btn-dark-theme btn-sm ms-2";
      default: return "btn btn-primary btn-sm ms-2";
    }
  };

  return (
    <div>
      {error && <div className={`theme-error text-center p-2 mb-3 ${currentTheme}`}>{error}</div>}

      <table className="table table-striped table-bordered">
        <thead>
          <tr>
            <th>Order Date</th>
            <th>Order Contents</th>
            <th>Order Total</th>
            <th>Order Status</th>
          </tr>
        </thead>
        <tbody>
          {orders.map(order => (
            <tr key={order.created}>
              <td>{new Date(order.created).toLocaleString("en-US", { timeZone: "America/New_York" })}</td>
              <td>
                {order.itemStr && order.itemStr.length > 0 ? (
                  <ul className="mb-0">
                    {order.itemStr.split(",").map((item, idx) => (
                      <li key={idx}>{item.trim()}</li>
                    ))}
                  </ul>
                ) : (
                  <em>No items</em>
                )}
              </td>
              <td>{formatCents(getFullTotal(order))}</td>
              <td>
                {capitalizeFirst(order.status ?? "pending")}
                {order.status === "pending" && (
                  <button
                    className={getActionButtonClass()}
                    onClick={() => confirmAction(order.id, "cancel")}
                  >
                    Cancel
                  </button>
                )}
                {order.status === "fulfilled" && (
                  <button
                    className={getActionButtonClass()}
                    onClick={() => confirmAction(order.id, "pickup")}
                  >
                    Pick Up
                  </button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {showActionModal && (
        <div className="theme-modal-backdrop">
          <div className={`theme-modal-content ${currentTheme}`}>
            <h5 className="mb-2">{actionType === "cancel" ? "Confirm Cancellation" : "Confirm Pickup"}</h5>
            <p className="mb-3">
              {actionType === "cancel"
                ? "Are you sure you want to cancel this order?"
                : "Are you sure you want to mark this order as picked up?"}
            </p>
            <div className="theme-modal-actions">
              <button onClick={() => { setShowActionModal(false); setActionOrderId(null); setActionType(""); }}>
                No
              </button>
              <button onClick={handleActionConfirmed}>
                Yes
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default ListGuestOrdersComponent;
