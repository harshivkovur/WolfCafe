import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { getItemById, getAllItems } from "../services/ItemService";

/**
 * ItemComponent
 *
 * Displays either a single menu item (if ID is provided via route params)
 * or all items from the backend. Prices are stored in cents and displayed
 * in $X.XX format.
 *
 * @component
 * @returns {JSX.Element} Menu items or loading/error messages
 */
const ItemComponent = () => {
  const { id } = useParams(); // optional single item ID
  const navigate = useNavigate();

  /** @type {[Array, Function]} items - Array of menu items */
  const [items, setItems] = useState([]);

  /** @type {[boolean, Function]} loading - True while fetching items */
  const [loading, setLoading] = useState(true);

  /** @type {[string|null, Function]} error - Error message if fetch fails */
  const [error, setError] = useState(null);

  /**
   * Fetch items from backend: single item if ID is provided, otherwise all items
   */
  useEffect(() => {
    const fetchItems = async () => {
      try {
        let response;
        if (id) {
          response = await getItemById(id);
          setItems([response.data]);
        } else {
          response = await getAllItems();
          setItems(response.data || []);
        }
      } catch (err) {
        console.error(err);
        setError("Failed to fetch items.");
      } finally {
        setLoading(false);
      }
    };

    fetchItems();
  }, [id]);

  /**
   * Format integer cents as dollar string
   * @param {number} cents - Price in cents
   * @returns {string} Formatted price in dollars
   */
  const formatCents = (cents) => `$${(cents / 100).toFixed(2)}`;

  if (loading) return <p>Loading items...</p>;
  if (error) return <p className="text-danger">{error}</p>;
  if (!items.length) return <p>No items found.</p>;

  return (
    <div className="container mt-4">
      <h2>Menu</h2>
      {items.map((item) => (
        <div key={item.id} className="card mb-3">
          <div className="card-body">
            <h4 className="card-title">
              {item.name} - {formatCents(item.price)}
            </h4>
            <p className="card-text">{item.description}</p>
            {item.ingredients && item.ingredients.length > 0 ? (
              <table className="table table-sm">
                <thead>
                  <tr>
                    <th>Ingredient</th>
                    <th>Quantity</th>
                    <th>Unit</th>
                  </tr>
                </thead>
                <tbody>
                  {item.ingredients.map((ing) => (
                    <tr key={ing.id}>
                      <td>{ing.name}</td>
                      <td>{ing.quantity}</td>
                      <td>{ing.unit}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            ) : (
              <p>No ingredients listed.</p>
            )}
          </div>
        </div>
      ))}
    </div>
  );
};

export default ItemComponent;
