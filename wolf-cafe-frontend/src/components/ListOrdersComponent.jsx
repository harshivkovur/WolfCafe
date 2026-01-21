import React, { useEffect, useState, useContext } from 'react'
import { listOrders, updateOrderStatus } from '../services/OrdersService'
import { getUserById } from '../services/AuthService'
import { ThemeContext } from '../components/ThemeContext'

const ListOrdersComponent = () => {
  const { currentTheme } = useContext(ThemeContext)
  const [orders, setOrders] = useState([])
  const [modalVisible, setModalVisible] = useState(false)
  const [selectedOrderId, setSelectedOrderId] = useState(null)
  const [error, setError] = useState('')

  const [dateFilter, setDateFilter] = useState(() => {
    const today = new Date()
    today.setHours(0, 0, 0, 0)
    return today
  })

  useEffect(() => {
    fetchOrders()
  }, [dateFilter])

  const fetchOrders = async () => {
    try {
      const ordersResponse = await listOrders()
      const ordersData = ordersResponse.data

      const enrichedOrders = await Promise.all(
        ordersData.map(async (order) => {
          let customerName = 'Guest'
		  if (order.customerId) {
		    try {
		      const userResponse = await getUserById(order.customerId)
		      if (userResponse?.data?.name) customerName = userResponse.data.name
		    } catch (err) {
		      console.warn(`Failed to fetch customer ${order.customerId}`, err)
		    }

          }
          return { ...order, customerName }
        })
      )

      const getDateString = (isoStr) => isoStr ? isoStr.slice(0, 10) : ''
      const filterDateStr = dateFilter.toISOString().slice(0, 10)

      const filteredOrders = enrichedOrders.filter(order => getDateString(order.created) === filterDateStr)

      filteredOrders.sort((a, b) => new Date(b.created) - new Date(a.created))
      setOrders(filteredOrders)
    } catch (err) {
      console.error('Failed to fetch orders:', err)
      setOrders([])
      setError('Failed to load orders. Please refresh.')
      setTimeout(() => setError(''), 5000)
    }
  }

  const formatCents = (cents) => `$${(cents / 100).toFixed(2)}`
  const calculateOrderTotal = (order) => {
    const subtotal = parseInt(order.subtotal) || 0
    const tax = parseInt(order.tax) || 0
    const tip = parseInt(order.tip) || 0
    return subtotal + tax + tip
  }
  const capitalizeFirst = (str) => str ? str.charAt(0).toUpperCase() + str.slice(1) : ''

  const handleClickComplete = (orderId) => {
    setSelectedOrderId(orderId)
    setModalVisible(true)
    setError('')
  }

  const handleConfirmComplete = async () => {
    try {
      await updateOrderStatus(selectedOrderId, 'fulfilled')
      setOrders((prevOrders) =>
        prevOrders.map((order) =>
          order.id === selectedOrderId ? { ...order, status: 'fulfilled' } : order
        )
      )
    } catch (err) {
      console.error(`[Staff] Failed to update order ${selectedOrderId}:`, err)
      setError(err.response.data || 'Failed to mark order as complete. Please try again.')
      setTimeout(() => setError(''), 5000)
    } finally {
      setModalVisible(false)
      setSelectedOrderId(null)
    }
  }

  const handleCancelModal = () => {
    setModalVisible(false)
    setSelectedOrderId(null)
  }

  const getActionButtonClass = () => {
    switch (currentTheme) {
      case 'theme-ncstate': return 'btn btn-ncstate btn-sm ms-2'
      case 'theme-vaporwave': return 'btn btn-vaporwave btn-sm ms-2'
      case 'theme-light': return 'btn btn-light-theme btn-sm ms-2'
      case 'theme-dark': return 'btn btn-dark-theme btn-sm ms-2'
      default: return 'btn btn-primary btn-sm ms-2'
    }
  }

  const getModalContentClass = () => {
    switch (currentTheme) {
      case 'theme-ncstate': return 'modal-content p-3 theme-ncstate'
      case 'theme-vaporwave': return 'modal-content p-3 theme-vaporwave'
      case 'theme-light': return 'modal-content p-3 theme-light'
      case 'theme-dark': return 'modal-content p-3 theme-dark'
      default: return 'modal-content p-3'
    }
  }

  // ✅ EXCLUDE canceled orders from revenue calculation
  const dailyRevenue = orders
    .filter(order => order.status !== 'canceled')
    .reduce((sum, order) => sum + calculateOrderTotal(order), 0)

  return (
    <div className="container mt-5">
      <div className="text-center mb-4">
        <h1 style={{ fontSize: '2.5rem', fontWeight: 'bold' }}>Orders</h1>
      </div>

      {error && <div className="theme-error text-center p-2 mb-3">{error}</div>}

      <div className="mb-3 text-center">
        <label htmlFor="dateFilter" className="me-2">Select date:</label>
        <input
          id="dateFilter"
          type="date"
          value={dateFilter.toISOString().split('T')[0]}
          onChange={e => setDateFilter(new Date(e.target.value))}
        />
      </div>

      {/* --- Daily Revenue Single-Line Card --- */}
      <div
        className="card inventory-card tax-card mb-4"
        style={{
          maxWidth: '360px',
          marginLeft: 'auto',
          marginRight: 'auto',
          padding: '1rem 1.25rem',
          textAlign: 'center',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          height: '60px'
        }}
      >
        <h5 style={{ margin: 0, fontWeight: '600' }}>
          Daily Revenue: {formatCents(dailyRevenue)}
        </h5>
      </div>

      <table className="table table-striped table-bordered">
        <thead className="table-light">
          <tr>
            <th>Customer Name</th>
            <th>Order Contents</th>
            <th>Order Total</th>
            <th>Order Status</th>
          </tr>
        </thead>
        <tbody>
          {orders.length === 0 ? (
            <tr>
              <td colSpan="4" className="text-center">No orders found.</td>
            </tr>
          ) : (
            orders.map(order => {
              const totalCents = calculateOrderTotal(order)
              return (
                <tr key={order.id}>
                  <td>{order.customerName || 'Guest'}</td>
                  <td>
                    {order.itemStr ? (
                      <ul className="mb-0">
                        {order.itemStr.split(',').map((itemText, idx) => (
                          <li key={idx}>{itemText.trim()}</li>
                        ))}
                      </ul>
                    ) : (
                      <em>No items</em>
                    )}
                  </td>
                  <td>{formatCents(totalCents)}</td>
                  <td>
                    {capitalizeFirst(order.status ?? 'pending')}
                    {order.status === 'pending' && (
                      <button
                        className={getActionButtonClass()}
                        onClick={() => handleClickComplete(order.id)}
                      >
                        Complete
                      </button>
                    )}
                  </td>
                </tr>
              )
            })
          )}
        </tbody>
      </table>

      {modalVisible && (
        <div className="modal d-block" tabIndex="-1" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
          <div className="modal-dialog modal-dialog-centered">
            <div className={getModalContentClass()}>
              <h5 className="text-center mb-3">Confirm completion</h5>
              <p className="text-center mb-3">Are you sure you want to mark this order as complete?</p>
              <div className="d-flex justify-content-center gap-3">
                <button className="btn btn-secondary" onClick={handleCancelModal}>Cancel</button>
                <button className={getActionButtonClass()} onClick={handleConfirmComplete}>Confirm</button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default ListOrdersComponent
