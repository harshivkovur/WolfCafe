import axios from 'axios'

// Direct backend URL
const ORDERS_API_BASE_URL = 'http://localhost:8080/api/orders'

/**
 * Fetch all orders from backend
 */
export const listOrders = () => axios.get(ORDERS_API_BASE_URL)

/**
 * Delete an order by ID
 */
export const deleteOrder = (id) => axios.delete(`${ORDERS_API_BASE_URL}/${id}`)

/**
 * Save a new order (POST) with logging
 */
export const saveOrder = (order) => {
  console.log('[OrdersService] Sending order payload:', JSON.stringify(order, null, 2))
  return axios.post(ORDERS_API_BASE_URL, order)
}

/**
 * Update the status of an existing order
 * @param {number|string} orderId - ID of the order to update
 * @param {string} newStatus - new status string (e.g., "cancelled", "fulfilled")
 */
export const updateOrderStatus = (orderId, newStatus) => {
  console.log(`[OrdersService] Updating order ${orderId} status to "${newStatus}"`)
  return axios.post(`${ORDERS_API_BASE_URL}/status/${orderId}`, newStatus, {
    headers: {
      'Content-Type': 'text/plain'
    }
  })
}
