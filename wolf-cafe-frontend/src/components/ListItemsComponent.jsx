import React, { useEffect, useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { isAdminUser } from '../services/AuthService';
import { getAllItems, deleteItemById } from '../services/ItemService';
import { ThemeContext } from './ThemeContext'; // <-- Theme context

const ListItemsComponent = () => {
  const [items, setItems] = useState([]);
  const [errors, setErrors] = useState('');
  const navigate = useNavigate();
  const isAdmin = isAdminUser(); // Check if current user is admin/staff
  const { currentTheme } = useContext(ThemeContext); // Get current theme

  // Fetch items when component mounts
  useEffect(() => {
    listItems();
  }, []);

  /** Fetch all items from backend */
  const listItems = () => {
    console.log('[ListItemsComponent] Fetching all items...');
    getAllItems()
      .then((response) => {
        if (!response.data || !Array.isArray(response.data)) {
          console.error('[ERROR] Invalid response:', response.data);
          setErrors('Invalid response format from server');
          return;
        }
        setItems(response.data);
        setErrors('');
      })
      .catch((error) => {
        console.error('[ERROR] Failed to fetch items:', error);
        setErrors('Failed to load items');
      });
  };

  /** Navigate to Add Item page */
  const addNewItem = () => {
    navigate('/add-item');
  };

  /** Navigate to Edit Item page */
  const updateItem = (id) => {
    navigate(`/edit-item/${id}`);
  };

  /** Delete an item by ID */
  const deleteItem = (id) => {
    deleteItemById(id)
      .then(() => listItems()) // Refresh list after deletion
      .catch((error) => {
        console.error('[ERROR] Failed to delete item:', error);
        setErrors('Failed to delete item');
      });
  };

  /** Format integer cents as dollar string */
  const formatCents = (cents) => `$${(cents / 100).toFixed(2)}`;

  /** Determine button classes based on theme */
  const getButtonClass = (type = 'primary') => {
    switch (currentTheme) {
      case 'theme-ncstate':
        if (type === 'danger') return 'btn btn-outline-danger';
        if (type === 'info') return 'btn btn-outline-info';
        return 'btn btn-outline-light'; // default primary for NCSU
      case 'theme-dark':
        if (type === 'danger') return 'btn btn-danger';
        if (type === 'info') return 'btn btn-info';
        return 'btn btn-light'; // primary
      case 'theme-light':
        if (type === 'danger') return 'btn btn-danger';
        if (type === 'info') return 'btn btn-info';
        return 'btn btn-primary'; // primary
      case 'theme-vaporwave':
        if (type === 'danger') return 'btn btn-danger';
        if (type === 'info') return 'btn btn-info';
        return 'btn btn-primary'; // primary
      default:
        return 'btn btn-primary';
    }
  };

  return (
    <div className="container">
      <h2 className="text-center my-3">Items</h2>

      {/* Display any errors */}
      {errors && <div className="p-3 mb-2 bg-danger text-white">{errors}</div>}

      {/* Add Item button for admins (centered) */}
      {isAdmin && (
        <div className="text-center mb-3">
          <button className={getButtonClass()} onClick={addNewItem}>
            Add Item
          </button>
        </div>
      )}

      <table className="table table-bordered table-striped">
        <thead>
          <tr>
            <th>Item Name</th>
            <th>Description</th>
            <th>Price</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {items.map((item) => (
            <tr key={item.id}>
              <td>{item.name}</td>
              <td>{item.description}</td>
              <td>{formatCents(item.price)}</td>
              <td>
                {isAdmin && (
                  <>
                    <button
                      className={`${getButtonClass('info')} me-2`}
                      onClick={() => updateItem(item.id)}
                    >
                      Update
                    </button>
                    <button
                      className={getButtonClass('danger')}
                      onClick={() => deleteItem(item.id)}
                    >
                      Delete
                    </button>
                  </>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default ListItemsComponent;
