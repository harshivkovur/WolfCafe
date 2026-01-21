import React, { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { saveOrder } from "../services/OrdersService";

/**
 * PaymentScreenComponent
 */
const PaymentScreenComponent = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const order = location.state;

  const [payment, setPayment] = useState("");
  const [error, setError] = useState("");
  const [paymentSuccess, setPaymentSuccess] = useState(false);
  const [change, setChange] = useState(0);
  const [paymentAmount, setPaymentAmount] = useState(0);

  useEffect(() => {
    if (!order) navigate("/orders");
  }, [order, navigate]);

  if (!order) return null;

  const { items, tip = 0, tax = 0 } = order;

  // Normalize items into a consistent format
  const normalizedItems = items.map((item) => ({
    name: item.name ?? "Unknown Item",
    price: item.price,
    id: item.id,              // must match backend id
    quantity: item.quantity,
  }));

  const formatCents = (cents) => `$${(cents / 100).toFixed(2)}`;

  const subtotal = normalizedItems.reduce(
    (sum, item) => sum + item.price * item.quantity,
    0
  );

  const totalCents = subtotal + tax + tip;

  const itemStr = normalizedItems
    .map((i) => `${i.quantity}x ${i.name} (${formatCents(i.price)})`)
    .join(", ");

  const handleSubmitPayment = async () => {
    const enteredPaymentCents = Math.round(parseFloat(payment) * 100);

    if (isNaN(enteredPaymentCents) || enteredPaymentCents <= 0) {
      setError("Please enter a valid payment amount.");
      return;
    }

    if (enteredPaymentCents < totalCents) {
      setError("Insufficient payment. Please enter the full total.");
      return;
    }

    if (!normalizedItems.length) {
      setError("Cannot submit an empty order.");
      return;
    }

    // THIS is the exact format your backend requires
    const orderToSave = {
      customerId: order.customerId,
      items: normalizedItems.map((item) => ({
        id: item.id,           // backend id
        itemName: item.name,
        price: item.price,
        quantity: item.quantity,
      })),
      subtotal,
      tax,
      tip,
      total: totalCents,
      created: new Date()
        .toLocaleString("sv-SE", { timeZone: "America/New_York" })
        .replace(" ", "T"),
      status: "pending",
      itemStr,
    };

    try {
      await saveOrder(orderToSave);
      setChange(enteredPaymentCents - totalCents);
      setPaymentAmount(enteredPaymentCents);
      setPaymentSuccess(true);
      setPayment("");
    } catch (err) {
      setError("Failed to save order. Please try again.");
    }
  };

  return (
    <div className="container mt-5">
      <h2 className="text-center mb-4">Payment</h2>

      {error && <div className="alert alert-danger text-center">{error}</div>}

      <table className="table table-striped">
        <thead>
          <tr>
            <th>Item</th>
            <th>Price</th>
            <th>Qty</th>
            <th>Total</th>
          </tr>
        </thead>
        <tbody>
          {normalizedItems.map((item, idx) => (
            <tr key={idx}>
              <td>{item.name}</td>
              <td>{formatCents(item.price)}</td>
              <td>{item.quantity}</td>
              <td>{formatCents(item.price * item.quantity)}</td>
            </tr>
          ))}
          <tr>
            <td colSpan="3" className="text-end"><strong>Subtotal:</strong></td>
            <td>{formatCents(subtotal)}</td>
          </tr>
          <tr>
            <td colSpan="3" className="text-end"><strong>Tax:</strong></td>
            <td>{formatCents(tax)}</td>
          </tr>
          <tr>
            <td colSpan="3" className="text-end"><strong>Tip:</strong></td>
            <td>{formatCents(tip)}</td>
          </tr>
          <tr>
            <td colSpan="3" className="text-end"><strong>Total:</strong></td>
            <td>{formatCents(totalCents)}</td>
          </tr>
        </tbody>
      </table>

      {!paymentSuccess && (
        <div className="text-center mt-4">
          <div className="mb-3">
            <label htmlFor="payment" className="form-label">
              Enter Payment Amount
            </label>
            <input
              type="number"
              min="0"
              step="0.01"
              id="payment"
              value={payment}
              onChange={(e) => {
                setPayment(e.target.value);
                setError("");
              }}
              className="form-control"
              style={{ width: "200px", margin: "0 auto" }}
            />
          </div>

          <div className="d-flex justify-content-center gap-2">
            <button
              className="btn btn-secondary"
              onClick={() =>
                navigate("/orders/new", {
                  state: {
                    ...order,
                    items: normalizedItems, // send usable names back
                  },
                })
              }
            >
              Edit Order
            </button>

            <button className="btn btn-success" onClick={handleSubmitPayment}>
              Submit Payment
            </button>
          </div>
        </div>
      )}

      {paymentSuccess && (
        <div className="theme-modal-backdrop">
          <div className="theme-modal-content">
            <h4 className="mb-3">Payment Successful!</h4>

            <p className="mb-1">
              Payment submitted: {formatCents(paymentAmount)}
            </p>
            <p className="mb-1">Total: {formatCents(totalCents)}</p>
            <p className="mb-3">Change due: {formatCents(change)}</p>

            <div className="d-flex justify-content-center gap-2">
              <button
                className="btn btn-primary"
                onClick={() => navigate("/orders")}
              >
                Return to Orders
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default PaymentScreenComponent;
