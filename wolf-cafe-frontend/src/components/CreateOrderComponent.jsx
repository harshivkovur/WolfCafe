import React, { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { getAllItems } from '../services/ItemService';
import { getCurrentUser } from '../services/AuthService';
import { getTaxRate } from '../services/InventoryService';

const CreateOrderComponent = () => {
  const navigate = useNavigate();
  const location = useLocation();

  const prefilledOrder = location.state || null;
  const currentUser = getCurrentUser() || { username: null, id: null };

  const [allItems, setAllItems] = useState([]);
  const [orderItems, setOrderItems] = useState([]);
  const [selectedItemId, setSelectedItemId] = useState('');
  const [quantity, setQuantity] = useState('');
  const [tip, setTip] = useState(15);
  const [customTip, setCustomTip] = useState('');
  const [taxRate, setTaxRate] = useState(0); // decimal fraction, e.g., 0.02 for 2%

  // Fetch menu items and restore prefilled order
  useEffect(() => {
    getAllItems()
      .then(res => {
        const formattedItems = res.data.map(item => ({
          ...item,
          price: parseInt(item.price),
          quantity: 0
        }));
        setAllItems(formattedItems);

        if (prefilledOrder?.items) {
          const restoredItems = prefilledOrder.items.map(preItem => {
            const backendItem = formattedItems.find(i => i.name === preItem.name);
            if (!backendItem) {
              return {
                name: preItem.name ?? 'Unknown Item',
                price: preItem.price,
                quantity: preItem.quantity
              };
            }
            return {
              ...backendItem,
              quantity: preItem.quantity,
            };
          });
          setOrderItems(restoredItems);
        }

        console.log('[CreateOrderComponent] Loaded menu items:', formattedItems);
      })
      .catch(err => console.error('[CreateOrderComponent] Error fetching items:', err));
  }, [prefilledOrder]);



  // Fetch current tax rate from backend
  useEffect(() => {
    getTaxRate()
      .then(res => {
        const decimalRate = res.data / 100; // convert backend 2 → 0.02
        setTaxRate(decimalRate);
        console.log('[CreateOrderComponent] Fetched tax rate:', decimalRate);
      })
      .catch(err => console.error('[CreateOrderComponent] Error fetching tax rate:', err));
  }, []);

  const calculateSubtotal = () =>
    orderItems.reduce((sum, item) => sum + item.price * item.quantity, 0);

  const subtotalCents = calculateSubtotal();
  const taxCents = Math.round(subtotalCents * taxRate);

  const calculateTip = () => {
    if (tip === 'Custom') {
      return Math.round(parseFloat(customTip) || 0);
    }
    return Math.round(subtotalCents * (tip / 100));
  };

  const tipCents = calculateTip();
  const totalCents = subtotalCents + taxCents + tipCents;

  const handleAddItem = () => {
    if (!selectedItemId || !quantity) {
      alert('Please select an item and specify a quantity.');
      return;
    }

    const selectedItem = allItems.find(item => item.id === parseInt(selectedItemId, 10));
    if (!selectedItem) return;

    const existing = orderItems.find(i => i.id === selectedItem.id);
    if (existing) {
      alert('This item is already added.');
      return;
    }

    setOrderItems([...orderItems, { ...selectedItem, quantity: parseInt(quantity, 10) }]);
    setSelectedItemId('');
    setQuantity('');
  };

  const handleRemoveItem = (id) => {
    setOrderItems(orderItems.filter(item => item.id !== id));
  };

  const handleSubmit = () => {
    let id = null;
    if (currentUser?.id) id = parseInt(currentUser.id, 10);

    const filteredItems = orderItems.filter(item => item.quantity > 0);
    if (filteredItems.length === 0) {
      alert('Please select at least one item.');
      return;
    }

    if (tipCents < 0) {
      alert('Tip must not be negative.');
      return;
    }

    const orderData = {
      customerId: id,
      items: filteredItems.map(item => ({
        name: item.name,
        quantity: item.quantity,
        price: item.price,
      })),
      subtotal: subtotalCents,
      tax: taxCents,
      tip: tipCents,
      total: totalCents,
      created: new Date().toLocaleString('sv-SE', { timeZone: 'America/New_York' }).replace(' ', 'T'),
    };

    console.log('[CreateOrderComponent] Sending order payload:', orderData);

    navigate('/orders/payment', { state: orderData });
  };

  const formatCents = (cents) => `$${(cents / 100).toFixed(2)}`;

  return (
    <div className="container mt-4">
      <h2 className="text-center mb-4">Create New Order</h2>
      <div className="card p-4 shadow rounded">
        <div className="d-flex align-items-end gap-3 mb-4">
          <div className="flex-grow-1">
            <label className="form-label">Select Item</label>
            <select
              className="form-select"
              value={selectedItemId}
              onChange={(e) => setSelectedItemId(e.target.value)}
            >
              <option value="">Select item</option>
              {allItems.map(item => (
                <option key={item.id} value={item.id}>
                  {item.name} — {formatCents(item.price)}
                </option>
              ))}
            </select>
          </div>

          <div style={{ width: '120px' }}>
            <label className="form-label">Quantity</label>
            <input
              type="number"
              className="form-control"
              min="1"
              placeholder="Qty"
              value={quantity}
              onChange={(e) => setQuantity(e.target.value)}
            />
          </div>

          <button type="button" className="btn btn-outline-primary" onClick={handleAddItem}>
            Add
          </button>
        </div>

        {orderItems.length > 0 && (
          <div className="mt-3">
            <h5>Items Added</h5>
            <ul className="list-group">
              {orderItems.map(item => (
                <li key={item.id} className="list-group-item d-flex justify-content-between align-items-center">
                  <span>
                    {item.name} — {item.quantity} × {formatCents(item.price)} = <strong>{formatCents(item.price * item.quantity)}</strong>
                  </span>
                  <button className="btn btn-sm btn-danger" onClick={() => handleRemoveItem(item.id)}>Remove</button>
                </li>
              ))}
            </ul>
          </div>
        )}

        {orderItems.length > 0 && (
          <div className="mt-4">
            <div className="d-flex justify-content-between mb-2">
              <strong>Subtotal:</strong>
              <span>{formatCents(subtotalCents)}</span>
            </div>
            <div className="d-flex justify-content-between mb-2">
              <strong>Tax:</strong>
              <span>{formatCents(taxCents)}</span>
            </div>
            <div className="d-flex justify-content-between align-items-center mb-2">
              <strong>Tip:</strong>
              <div className="d-flex align-items-center" style={{ gap: '10px' }}>
                <select
                  value={tip}
                  onChange={(e) => setTip(e.target.value)}
                  className="form-select"
                  style={{ width: '120px' }}
                >
                  <option value={15}>15%</option>
                  <option value={18}>18%</option>
                  <option value={20}>20%</option>
                  <option value={22}>22%</option>
                  <option value="Custom">Custom</option>
                </select>

                {tip === 'Custom' ? (
                  <input
                    type="number"
                    min="0"
                    value={customTip}
                    onChange={(e) => setCustomTip(e.target.value)}
                    className="form-control"
                    placeholder="Tip (cents)"
                    style={{ width: '100px' }}
                  />
                ) : (
                  <span>{formatCents(tipCents)}</span>
                )}
              </div>
            </div>

            <div className="d-flex justify-content-between border-top pt-2 mt-3">
              <strong>Total:</strong>
              <span className="fw-bold">{formatCents(totalCents)}</span>
            </div>
          </div>
        )}

        <div className="text-center mt-4">
          <button
            className="btn btn-success btn-lg px-5"
            onClick={handleSubmit}
            disabled={orderItems.length === 0}
          >
            Submit Order
          </button>
        </div>
      </div>
    </div>
  );
};

export default CreateOrderComponent;
