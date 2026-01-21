import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { getRecipeById, updateRecipe } from "../services/RecipesService";
import { getInventory } from "../services/InventoryService";

/**
 * EditRecipeComponent
 *
 * Page for editing an existing recipe.
 * Auto-populates all recipe data for a given recipe ID and matches RecipeComponent style.
 *
 * Features:
 * - Loads recipe by ID
 * - Loads backend inventory for ingredient dropdowns
 * - Shows recipe ingredients with existing quantities pre-filled
 * - Input validation
 * - Save updates to backend
 *
 * Author: Michael Lewis
 */
const EditRecipeComponent = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const [name, setName] = useState("");
  const [price, setPrice] = useState("");
  const [ingredients, setIngredients] = useState([]);
  const [errors, setErrors] = useState({ general: "", name: "", price: "", ingredients: [] });

  // --- Load inventory ---
  useEffect(() => {
    console.log("Loading inventory...");
    getInventory()
      .then((res) => {
        // Handle case where inventory returns object with .ingredients array
        const inventoryList = Array.isArray(res.data) ? res.data : res.data.ingredients || [];
        console.log("Inventory fetched:", inventoryList);

        // Initialize ingredient form with inventory items
        const initialIngredients = inventoryList.map((ing) => ({
          key: ing.name.toLowerCase(),
          name: ing.name,
          quantity: 0,
          unit: "",
        }));

        setIngredients(initialIngredients);
      })
      .catch((err) => {
        console.error("Error fetching inventory:", err);
      });
  }, []);

  // --- Load recipe by ID ---
  useEffect(() => {
    console.log(`Loading recipe by ID: ${id}`);
    getRecipeById(id)
      .then((res) => {
        console.log("Recipe fetched:", res.data);

        setName(res.data.name || "");
        setPrice(res.data.price?.toString() || "");

        // Merge recipe ingredients with inventory
        setIngredients((prevIngredients) => {
          const mapped = prevIngredients.map((inv) => {
            const match = (res.data.ingredients || []).find(
              (ri) => ri.ingredient.name.toLowerCase() === inv.name.toLowerCase()
            );
            return {
              ...inv,
              quantity: match?.quantity || 0,
              unit: match?.unit || "",
            };
          });
          console.log("Merged ingredients for form:", mapped);
          return mapped;
        });
      })
      .catch((err) => {
        console.error("Error fetching recipe:", err);
        setErrors((prev) => ({ ...prev, general: "Failed to load recipe." }));
      });
  }, [id]);

  // --- Handle ingredient input changes ---
  const handleIngredientChange = (index, field, value) => {
    const updated = [...ingredients];
    if (field === "quantity") {
      updated[index].quantity = parseInt(value || "0", 10);
      if (updated[index].quantity === 0) updated[index].unit = "";
    } else if (field === "unit") {
      updated[index].unit = value;
    }
    setIngredients(updated);
  };

  // --- Validate form ---
  const validateForm = () => {
    let valid = true;
    const newErrors = { general: "", name: "", price: "", ingredients: [] };

    if (!name.trim()) {
      newErrors.name = "Name is required.";
      valid = false;
    }
    if (!price || isNaN(price) || parseInt(price) <= 0) {
      newErrors.price = "Price must be a positive integer.";
      valid = false;
    }

    ingredients.forEach((ing, idx) => {
      newErrors.ingredients[idx] = "";
      if (ing.quantity < 0 || isNaN(ing.quantity)) {
        newErrors.ingredients[idx] = "Quantity cannot be negative.";
        valid = false;
      }
      if (ing.quantity > 0 && !ing.unit.trim()) {
        newErrors.ingredients[idx] = "Unit is required when quantity > 0.";
        valid = false;
      }
    });

    setErrors(newErrors);
    return valid;
  };

  // --- Save updated recipe ---
  const saveRecipeHandler = (e) => {
    e.preventDefault();
    if (!validateForm()) return;

    const recipeData = {
      name,
      price: parseInt(price, 10),
      ingredients: ingredients
        .filter((ing) => ing.quantity > 0)
        .map((ing) => ({
          ingredient: { name: ing.name },
          quantity: ing.quantity,
          unit: ing.unit,
        })),
    };

    console.log("Saving recipe:", recipeData);

    updateRecipe(id, recipeData)
      .then(() => navigate("/recipes"))
      .catch((err) => {
        console.error("Error updating recipe:", err);
        setErrors((prev) => ({ ...prev, general: "Failed to update recipe. Please try again." }));
      });
  };

  return (
    <div className="container">
      <br />
      <br />
      <div className="row">
        <div className="card col-md-6 offset-md-3">
          <h2 className="text-center">Edit Recipe</h2>
          <div className="card-body">
            {errors.general && <div className="p-3 mb-2 bg-danger text-white">{errors.general}</div>}
            <form onSubmit={saveRecipeHandler}>
              {/* Recipe Name */}
              <div className="form-group mb-2">
                <label>Recipe Name</label>
                <input
                  type="text"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  className={`form-control ${errors.name ? "is-invalid" : ""}`}
                />
                {errors.name && <div className="invalid-feedback">{errors.name}</div>}
              </div>

              {/* Price */}
              <div className="form-group mb-2">
                <label>Price</label>
                <input
                  type="text"
                  value={price}
                  onChange={(e) => setPrice(e.target.value)}
                  className={`form-control ${errors.price ? "is-invalid" : ""}`}
                />
                {errors.price && <div className="invalid-feedback">{errors.price}</div>}
              </div>

              {/* Ingredients */}
              <h5 className="mt-3">Ingredients</h5>
              {ingredients.map((ing, idx) => (
                <div className="form-group mb-2" key={ing.key}>
                  <label>{ing.name}</label>
                  <div className="d-flex gap-2">
                    <input
                      type="number"
                      min="0"
                      value={ing.quantity}
                      placeholder="Quantity"
                      onChange={(e) => handleIngredientChange(idx, "quantity", e.target.value)}
                      className={`form-control ${errors.ingredients[idx] ? "is-invalid" : ""}`}
                    />
                    <input
                      type="text"
                      value={ing.unit}
                      placeholder="Unit"
                      onChange={(e) => handleIngredientChange(idx, "unit", e.target.value)}
                      className={`form-control ${errors.ingredients[idx] ? "is-invalid" : ""}`}
                      disabled={ing.quantity === 0}
                    />
                  </div>
                  {errors.ingredients[idx] && (
                    <div className="invalid-feedback">{errors.ingredients[idx]}</div>
                  )}
                </div>
              ))}

              <button className="btn btn-success mt-3" type="submit">
                Save Recipe
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default EditRecipeComponent;
