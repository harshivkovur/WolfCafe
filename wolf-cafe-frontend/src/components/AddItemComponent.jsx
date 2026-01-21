import React, { useEffect, useState } from 'react';
import { saveItem } from '../services/ItemService';
import { getInventory } from '../services/InventoryService';
import { useNavigate } from 'react-router-dom';

const AddItemComponent = () => {
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [price, setPrice] = useState('');
  const [allIngredients, setAllIngredients] = useState([]);
  const [selectedIngredientId, setSelectedIngredientId] = useState('');
  const [quantity, setQuantity] = useState('');
  const [unit, setUnit] = useState(''); // NEW: unit for ingredient
  const [ingredients, setIngredients] = useState([]);

  const navigate = useNavigate();

  useEffect(() => {
    console.log('[AddItemComponent] Fetching inventory...');
    getInventory()
      .then((res) => {
        console.log('[AddItemComponent] Inventory response:', res.data);
        setAllIngredients(res.data.ingredients || []);
      })
      .catch((err) =>
        console.error('[AddItemComponent] Error fetching ingredients:', err)
      );
  }, []);

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

    console.log('[AddItemComponent] Adding ingredient:', newIngredient);

    setIngredients([...ingredients, newIngredient]);

    // Reset inputs
    setSelectedIngredientId('');
    setQuantity('');
    setUnit('');
  };

  const handleRemoveIngredient = (id) => {
    console.log('[AddItemComponent] Removing ingredient with ID:', id);
    setIngredients(ingredients.filter((i) => i.ingredientId !== id));
  };

  const handleSaveItem = async (e) => {
    e.preventDefault();
    console.log('[AddItemComponent] Save item clicked');
    console.log('[AddItemComponent] Current state before save:', {
      name,
      description,
      price,
      ingredients,
    });

    if (!name || !price || !description || ingredients.length === 0) {
      alert('Please fill all fields and add at least one ingredient.');
      console.log(
        '[AddItemComponent] Validation failed',
        { name, price, description, ingredients }
      );
      return;
    }

    // Map to backend structure using ingredient name
    const itemDto = {
      name,
      description,
      price: parseFloat(price),
      ingredients: ingredients.map((i) => ({
        ingredient: { name: i.ingredientName }, // <-- changed from id to name
        quantity: i.quantity,
        unit: i.unit,
      })),
    };

    console.log('[AddItemComponent] Final Item DTO to send:');
    console.log(JSON.stringify(itemDto, null, 2));
    console.log('[AddItemComponent] Detailed ingredient objects:');
    itemDto.ingredients.forEach((ing, index) => {
      console.log(`Ingredient #${index + 1}:`, ing);
    });

    try {
      const response = await saveItem(itemDto);
      console.log('[AddItemComponent] Save response:', response.data);
      navigate('/items');
    } catch (err) {
      console.error(
        '[AddItemComponent] Error saving item:',
        err.response?.data || err
      );
      alert('Failed to save item. Check console for details.');
    }
  };

  return (
    <div className="container mt-4">
      <h2 className="text-center mb-4">Add New Item</h2>
      <div className="card p-4 shadow rounded">
        <div className="mb-3">
          <label className="form-label">Item Name</label>
          <input
            type="text"
            className="form-control"
            placeholder="Enter item name"
            value={name}
            onChange={(e) => setName(e.target.value)}
          />
        </div>

        <div className="mb-3">
          <label className="form-label">Description</label>
          <textarea
            className="form-control"
            placeholder="Enter item description"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
          />
        </div>

        <div className="mb-3">
          <label className="form-label">Price ($)</label>
          <input
            type="number"
            className="form-control"
            placeholder="Enter item price"
            value={price/100}
            onChange={(e) => setPrice(parseInt(e.target.value * 100))}
          />
        </div>

        {/* Ingredient section */}
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
              placeholder="Qty"
              value={quantity}
              onChange={(e) => setQuantity(e.target.value)}
            />
          </div>

          <div style={{ width: '100px' }}>
            <label className="form-label">Unit</label>
            <input
              type="text"
              className="form-control"
              placeholder="Unit"
              value={unit}
              onChange={(e) => setUnit(e.target.value)}
            />
          </div>

          <button
            type="button"
            className="btn btn-outline-primary"
            onClick={handleAddIngredient}
          >
            Add
          </button>
        </div>

        {/* Ingredient list */}
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
                    className="btn btn-sm btn-danger"
                    onClick={() => handleRemoveIngredient(i.ingredientId)}
                  >
                    Remove
                  </button>
                </li>
              ))}
            </ul>
          </div>
        )}

        <div className="text-center mt-4">
          <button className="btn btn-success" onClick={handleSaveItem}>
            Save Item
          </button>
        </div>
      </div>
    </div>
  );
};

export default AddItemComponent;
