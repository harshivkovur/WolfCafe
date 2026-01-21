import React, { useEffect, useState } from "react";
import {
  getInventory,
  updateInventory,
  getTaxRate,
  updateTaxRate
} from "../services/InventoryService";

const InventoryComponent = ({ currentTheme }) => {
  const [ingredients, setIngredients] = useState([]);
  const [originalInventory, setOriginalInventory] = useState({});
  const [newName, setNewName] = useState("");
  const [newAmount, setNewAmount] = useState("");
  const [errors, setErrors] = useState({ general: "" });
  const [taxRate, setTaxRate] = useState("");
  const [taxInput, setTaxInput] = useState(""); // controlled input separately
  const [taxLoading, setTaxLoading] = useState(false);
  const [showConfirmModal, setShowConfirmModal] = useState(false);
  const [showSuccessModal, setShowSuccessModal] = useState(false);
  const [pendingTaxRate, setPendingTaxRate] = useState(null);

  // Load inventory and tax on mount
  useEffect(() => {
    loadInventory();
    loadTaxRate();
  }, []);

  const loadInventory = () => {
    getInventory()
      .then((response) => {
        const data = response.data;
        const inventoryArray = Array.isArray(data) ? data : data.ingredients || data.items || null;
        if (!inventoryArray) {
          if (data && data.ingredients && Array.isArray(data.ingredients)) {
            setFromInventoryArray(data.ingredients);
            return;
          }
          setErrors({ general: "Invalid inventory format from backend" });
          return;
        }
        setFromInventoryArray(inventoryArray);
      })
      .catch(() => setErrors({ general: "Failed to load inventory" }));
  };

  const setFromInventoryArray = (inventoryArray) => {
    setIngredients(
      inventoryArray.map((ing) => ({
        name: ing.name,
        addAmount: ""
      }))
    );
    const snapshot = {};
    inventoryArray.forEach((ing) => {
      snapshot[ing.name] = ing.quantity;
    });
    setOriginalInventory(snapshot);
    setErrors({ general: "" });
  };

  const loadTaxRate = () => {
    getTaxRate()
      .then((res) => {
        const val = res.data;
        setTaxRate(val !== null && val !== undefined ? String(val) : "");
        setTaxInput(""); // keep input blank initially
      })
      .catch(() => {});
  };

  const handleAddAmountChange = (index, value) => {
    const copy = [...ingredients];
    copy[index].addAmount = value;
    setIngredients(copy);
  };

  const addIngredient = (e) => {
    e.preventDefault();
    if (!newName.trim()) {
      setErrors({ general: "Ingredient name cannot be empty" });
      return;
    }
    if (newAmount === "" || isNaN(newAmount) || Number(newAmount) < 0) {
      setErrors({ general: "Amount must be a non-negative number" });
      return;
    }
    const exists = ingredients.some(
      (i) => i.name.toLowerCase() === newName.toLowerCase()
    );
    if (exists) {
      setErrors({ general: "Ingredient already exists" });
      return;
    }

    setIngredients([
      ...ingredients,
      { name: newName.trim(), addAmount: String(parseInt(newAmount, 10)) }
    ]);
    setNewName("");
    setNewAmount("");
    setErrors({ general: "" });
  };

  const modifyInventory = (e) => {
    e.preventDefault();
    const updatedIngredients = ingredients
      .map((ing) => {
        const add = parseInt(ing.addAmount || "0", 10);
        if (isNaN(add) || add < 0) {
          setErrors({ general: `Invalid amount to add for ${ing.name}` });
          return null;
        }
        const current = parseInt(originalInventory[ing.name] || 0, 10);
        return { name: ing.name, quantity: current + add };
      })
      .filter(Boolean);

    if (updatedIngredients.length === 0) {
      setErrors({ general: "No valid updates to apply" });
      return;
    }

    const payload = { ingredients: updatedIngredients };

    updateInventory(payload)
      .then(() => {
        const snapshot = {};
        updatedIngredients.forEach((ing) => {
          snapshot[ing.name] = ing.quantity;
        });
        setOriginalInventory(snapshot);

        const resetIngredients = updatedIngredients.map((ing) => ({
          ...ing,
          addAmount: ""
        }));
        setIngredients(resetIngredients);
        setErrors({ general: "" });
      })
      .catch(() => setErrors({ general: "Failed to update inventory" }));
  };

  const isModified = (ing) => {
    const current = originalInventory[ing.name];
    return current === undefined || parseInt(ing.addAmount || "0", 10) > 0;
  };

  const getButtonClass = () => {
    switch (currentTheme) {
      case "theme-dark": return "theme-dark-btn";
      case "theme-light": return "theme-light-btn";
      case "theme-ncstate":
      case "theme-schoolspirit": return "theme-ncstate-btn";
      case "theme-vaporwave": return "theme-vaporwave-btn";
      default: return "btn btn-primary";
    }
  };
  const cardClass = `card ${currentTheme} mb-4`;

  // --- Tax handlers ---
  const handleTaxChange = (val) => setTaxInput(val);

  const submitTaxRate = (e) => {
    e.preventDefault();
    const parsed = parseFloat(taxInput);
    if (isNaN(parsed) || parsed < 0 || parsed > 100) return;
    setPendingTaxRate(parsed);
    setShowConfirmModal(true);
  };

  const confirmTaxChange = () => {
    if (pendingTaxRate === null) return;
    setTaxLoading(true);
    updateTaxRate(pendingTaxRate)
      .then((res) => setTaxRate(String(res.data)))
      .catch(() => {})
      .finally(() => {
        setTaxLoading(false);
        setShowConfirmModal(false);
        setShowSuccessModal(true);
        setPendingTaxRate(null);
        setTaxInput(""); // reset input after success
      });
  };

  const closeSuccessModal = () => setShowSuccessModal(false);

  return (
    <div className={`container ${currentTheme}`}>
      <br /><br />
      <div className="row">
        <div className={`${cardClass} col-md-6 offset-md-3`}>
          <h2 className="text-center">Inventory</h2>
          <div className="card-body">
            {errors.general && <div className="p-3 mb-2 theme-error">{errors.general}</div>}

            <form onSubmit={modifyInventory}>
              {ingredients.map((ing, idx) => (
                <div key={ing.name + "-" + idx} className="form-group mb-3">
                  <label className="form-label">
                    {isModified(ing) ? (
                      <strong>{ing.name} (Current: {originalInventory[ing.name] || 0})</strong>
                    ) : (
                      <>{ing.name} (Current: {originalInventory[ing.name] || 0})</>
                    )}
                  </label>
                  <input
                    type="number"
                    placeholder="Add amount"
                    value={ing.addAmount || ""}
                    onChange={(e) => handleAddAmountChange(idx, e.target.value)}
                    className="form-control"
                  />
                </div>
              ))}
              <div className="d-flex justify-content-center">
                <button type="submit" className={getButtonClass()}>Update Inventory</button>
              </div>
            </form>

            <hr />
            <h4>Add New Ingredient</h4>
            <form onSubmit={addIngredient}>
              <div className="form-group mb-2">
                <input
                  type="text"
                  placeholder="Ingredient name"
                  value={newName}
                  onChange={(e) => setNewName(e.target.value)}
                  className="form-control"
                />
              </div>
              <div className="form-group mb-2">
                <input
                  type="number"
                  placeholder="Amount"
                  value={newAmount}
                  onChange={(e) => setNewAmount(e.target.value)}
                  className="form-control"
                />
              </div>
              <div className="d-flex justify-content-center">
                <button type="submit" className={getButtonClass()}>Add Ingredient</button>
              </div>
            </form>
          </div>
        </div>
      </div>

      {/* Tax rate card */}
      <div className="row">
        <div className={`${cardClass} col-md-6 offset-md-3`}>
          <h2 className="text-center">Global Tax Rate</h2>
          <div className="card-body">
            <div className="mb-2">
              <strong>Current Global Tax Rate:</strong> {taxRate ? `${taxRate}%` : "Not Set"}
            </div>
            <form onSubmit={submitTaxRate}>
              <div className="form-group mb-2">
                <label className="form-label">Set New Tax (%)</label>
                <input
                  type="number"
                  step="0.01"
                  min="0"
                  max="100"
                  placeholder="e.g., 7.5"
                  className="form-control"
                  value={taxInput}
                  onChange={(e) => handleTaxChange(e.target.value)}
                />
              </div>
              <div className="d-flex justify-content-center">
                <button type="submit" disabled={taxLoading} className={getButtonClass()}>
                  {taxLoading ? "Saving..." : "Save Tax Rate"}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>

      {/* Confirm modal */}
      {showConfirmModal && (
        <div className="theme-modal-backdrop">
          <div className="theme-modal-content">
            <h4 className="mb-3">Confirm Tax Change</h4>
            <p>Are you sure you want to set the tax rate to {pendingTaxRate}%?</p>
            <div className="d-flex justify-content-center gap-2">
              <button className="btn btn-secondary" onClick={() => setShowConfirmModal(false)}>Cancel</button>
              <button className="btn btn-primary" onClick={confirmTaxChange}>Confirm</button>
            </div>
          </div>
        </div>
      )}

      {/* Success modal */}
      {showSuccessModal && (
        <div className="theme-modal-backdrop">
          <div className="theme-modal-content">
            <h4 className="mb-3">Tax Updated!</h4>
            <p>Tax rate successfully changed to {taxRate}%.</p>
            <div className="d-flex justify-content-center gap-2">
              <button className="btn btn-primary" onClick={closeSuccessModal}>OK</button>
            </div>
          </div>
        </div>
      )}
      <br />
    </div>
  );
};

export default InventoryComponent;
