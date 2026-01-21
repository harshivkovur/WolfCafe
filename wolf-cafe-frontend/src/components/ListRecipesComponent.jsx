import React, { useEffect, useState } from 'react'
import { listOrders, deleteOrder } from '../services/OrdersService'
import { useNavigate } from 'react-router-dom'

/**
 * Lists all orders and provides options to create, edit, and delete orders.
 * Each order shows its date, item list (with count), total, and progress status.
 */
const ListOrdersComponent = () => {

  const [orders, setOrders] = useState([])
  const navigate = useNavigate()

  useEffect(() => {
    getAllOrders()
  }, [])

  function getAllOrders() {
    listOrders()
      .then(response => {
        setOrders(response.data)
      })
      .catch(error => {
        console.error('Error fetching orders:', error)
      })
  }

  function addNewOrder() {
    navigate('/orders/new', { state: { order: null } })
  }

  function removeOrder(id) {
    deleteOrder(id)
      .then(() => getAllOrders())
      .catch(error => {
        console.error('Error deleting order:', error)
      })
  }

  function editOrder(order) {
    navigate('/edit-order', { state: { orderData: order } })
  }

  return (
    <div className="container">
      <h2 className="text-center my-4">List of Orders</h2>

      {/* ✅ Centered "Create New Order" button */}
      <div className="d-flex justify-content-center mb-4">
        <button className="btn btn-success btn-lg" onClick={addNewOrder}>
          Create New Order
        </button>
      </div>

      <table className="table table-striped table-bordered">
        <thead>
          <tr>
            <th>Order Date</th>
            <th>Items (Count)</th>
            <th>Order Total</th>
            <th>Progress</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {orders.map(order => (
            <tr key={order.id}>
              {/* Order Date (auto-formatted) */}
              <td>{new Date(order.date).toLocaleDateString()}</td>

              {/* Items list and count */}
              <td>
                {order.items && order.items.length > 0 ? (
                  <>
                    <p><strong>{order.items.length}</strong> item(s)</p>
                    <ul className="mb-0">
                      {order.items.map(item => (
                        <li key={item.name}>{item.name}</li>
                      ))}
                    </ul>
                  </>
                ) : (
                  <p>No items</p>
                )}
              </td>

              {/* Total */}
              <td>${order.total?.toFixed(2) || '0.00'}</td>

              {/* Order progress */}
              <td>
                <span
                  className={
                    order.progress === 'Completed'
                      ? 'text-success fw-bold'
                      : order.progress === 'Processing'
                      ? 'text-warning fw-bold'
                      : 'text-secondary fw-bold'
                  }
                >
                  {order.progress || 'Pending'}
                </span>
              </td>

              {/* Actions */}
              <td>
                <button
                  className="btn btn-primary"
                  onClick={() => editOrder(order)}
                  style={{ marginLeft: '10px' }}
                >
                  Edit
                </button>
                <button
                  className="btn btn-danger"
                  onClick={() => removeOrder(order.id)}
                  style={{ marginLeft: '10px' }}
                >
                  Delete
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

export default ListOrdersComponent
