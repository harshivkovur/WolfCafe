import React, { useState, useEffect, useContext } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { getItemById, updateItem } from '../services/ItemService';
import { getInventory } from '../services/InventoryService';
import { ThemeContext } from './ThemeContext';

const EditItemComponent = () => {
  const { id } = useParams(); // Item ID from URL
  const navigate = useNavigate();
  const { currentTheme } = useContext(ThemeContext); // Current theme

  // --- State for item details ---
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [price, setPrice] = useState(''); // in cents
  const [allIngredients, setAllIngredients] = useState([]);
  const [selectedIngredientId, setSelectedIngredientId] = useState('');
  const [quantity, setQuantity] = useState('');
  const [unit, setUnit] = useState('');
  const [ingredients, setIngredients] = useState([]);

  // --- Fetch item details and inventory on mount ---
  useEffect(() => {
    // Fetch inventory for ingredient selection
    getInventory()
      .then((res) => setAllIngredients(res.data.ingredients || []))
      .catch((err) => console.error('Error fetching inventory:', err));

    // Fetch item to edit
    getItemById(id)
      .then((res) => {
        const item = res.data;
        setName(item.name);
        setDescription(item.description);
        setPrice(item.price); // store in cents
        // Map backend ingredients
        const mappedIngredients = item.ingredients.map((ing) => ({
          ingredientId: ing.ingredient.id, // or name if backend only sends name
          ingredientName: ing.ingredient.name,
          quantity: ing.quantity,
          unit: ing.unit,
        }));
        setIngredients(mappedIngredients);
      })
      .catch((err) => console.error('Error fetching item:', err));
  }, [id]);

  /** Add ingredient to item */
  const handleAddIngredient = () => {
    if (!selectedIngredientId || !quantity || !unit) {
      alert('Please select an ingredient, specify a quantity, and enter a unit.');
      return;
    }

    const ingredient = allIngredients.find(
      (i) => i.id === parseInt(selectedIngredientId)
    );
    if (!ingredient) return;

    const existing = ingredients.find((i) => i.ingredientId === ingredient.id);
    if (existing) {
      alert('This ingredient is already added.');
      return;
    }

    const newIngredient = {
      ingredientId: ingredient.id,
      ingredientName: ingredient.name,
      quantity: parseFloat(quantity),
      unit,
    };

    setIngredients([...ingredients, newIngredient]);
    setSelectedIngredientId('');
    setQuantity('');
    setUnit('');
  };

  /** Remove ingredient */
  const handleRemoveIngredient = (id) => {
    setIngredients(ingredients.filter((i) => i.ingredientId !== id));
  };

  /** Save updated item */
  const handleSaveItem = async (e) => {
    e.preventDefault();
    if (!name || !price || !description || ingredients.length === 0) {
      alert('Please fill all fields and add at least one ingredient.');
      return;
    }

    // Map to backend structure
    const itemDto = {
      name,
      description,
      price: parseFloat(price),
      ingredients: ingredients.map((i) => ({
        ingredient: { name: i.ingredientName },
        quantity: i.quantity,
        unit: i.unit,
      })),
    };

    try {
      await updateItem(id, itemDto);
      navigate('/items'); // Return to item list
    } catch (err) {
      console.error('Error updating item:', err.response?.data || err);
      alert('Failed to update item. Check console for details.');
    }
  };

  /** Determine theme-aware button class */
  const getButtonClass = (type = 'primary') => {
    switch (currentTheme) {
      case 'theme-ncstate':
        if (type === 'danger') return 'btn btn-outline-danger';
        if (type === 'info') return 'btn btn-outline-info';
        return 'btn btn-outline-light';
      case 'theme-dark':
        if (type === 'danger') return 'btn btn-danger';
        if (type === 'info') return 'btn btn-info';
        return 'btn btn-light';
      case 'theme-light':
        if (type === 'danger') return 'btn btn-danger';
        if (type === 'info') return 'btn btn-info';
        return 'btn btn-primary';
      case 'theme-vaporwave':
        if (type === 'danger') return 'btn btn-danger';
        if (type === 'info') return 'btn btn-info';
        return 'btn btn-primary';
      default:
        return 'btn btn-primary';
    }
  };

  return (
    <div className="container mt-4">
      <h2 className="text-center mb-4">Edit Item: "{name}"</h2>
      <div className="card p-4 shadow rounded">
        {/* Item Name */}
        <div className="mb-3">
          <label className="form-label">Item Name</label>
          <input
            type="text"
            className="form-control"
            value={name}
            onChange={(e) => setName(e.target.value)}
          />
        </div>

        {/* Description */}
        <div className="mb-3">
          <label className="form-label">Description</label>
          <textarea
            className="form-control"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
          />
        </div>

        {/* Price */}
        <div className="mb-3">
          <label className="form-label">Price ($)</label>
          <input
            type="number"
            className="form-control"
            value={price / 100}
            onChange={(e) => setPrice(parseInt(e.target.value * 100))}
          />
        </div>

        {/* Ingredients Section */}
        <div className="d-flex align-items-end gap-3 mb-3">
          <div className="flex-grow-1">
            <label className="form-label">Ingredient</label>
            <select
              className="form-select"
              value={selectedIngredientId}
              onChange={(e) => setSelectedIngredientId(e.target.value)}
            >
              <option value="">Select ingredient</option>
              {allIngredients.map((ing) => (
                <option key={ing.id} value={ing.id}>
                  {ing.name}
                </option>
              ))}
            </select>
          </div>

          <div style={{ width: '100px' }}>
            <label className="form-label">Quantity</label>
            <input
              type="number"
              className="form-control"
              value={quantity}
              onChange={(e) => setQuantity(e.target.value)}
            />
          </div>

          <div style={{ width: '100px' }}>
            <label className="form-label">Unit</label>
            <input
              type="text"
              className="form-control"
              value={unit}
              onChange={(e) => setUnit(e.target.value)}
            />
          </div>

          <button
            type="button"
            className={`${getButtonClass()} mt-1`}
            onClick={handleAddIngredient}
          >
            Add
          </button>
        </div>

        {/* List of ingredients */}
        {ingredients.length > 0 && (
          <div className="mt-3">
            <h5>Ingredients Added</h5>
            <ul className="list-group">
              {ingredients.map((i) => (
                <li
                  key={i.ingredientId}
                  className="list-group-item d-flex justify-content-between align-items-center"
                >
                  <span>
                    {i.ingredientName} — {i.quantity} {i.unit}
                  </span>
                  <button
                    className={`${getButtonClass('danger')} btn-sm`}
                    onClick={() => handleRemoveIngredient(i.ingredientId)}
                  >
                    Remove
                  </button>
                </li>
              ))}
            </ul>
          </div>
        )}

        {/* Save button */}
        <div className="text-center mt-4">
          <button className={getButtonClass()} onClick={handleSaveItem}>
            Save Item
          </button>
        </div>
      </div>
    </div>
  );
};

export default EditItemComponent;
