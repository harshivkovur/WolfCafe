import axios from "axios"

/** Base URL for the Inventory API - Correspond to methods in Backend's InventoryController. */
const REST_API_BASE_URL = "http://localhost:8080/api/inventory"

/** GET Inventory - returns all inventory */
export const getInventory = () => axios.get(REST_API_BASE_URL)

/** PUT Inventory - updates the inventory */
export const updateInventory = (inventory) => axios.put(REST_API_BASE_URL, inventory)

/** GET Tax Rate - returns the current tax rate */
export const getTaxRate = () => axios.get(`${REST_API_BASE_URL}/tax`)

/** POST Tax Rate - updates the tax rate */
export const updateTaxRate = (taxRate) =>
  axios.post(
    `${REST_API_BASE_URL}/tax`,
    taxRate,
    { headers: { "Content-Type": "application/json" } }
  )