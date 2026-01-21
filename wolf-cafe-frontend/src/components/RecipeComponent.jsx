import { useEffect, useState } from "react"
import { createRecipe } from "../services/RecipesService"
import { getInventory } from "../services/InventoryService"
import { useNavigate } from "react-router-dom"

/**
 * Component for adding a new recipe to the recipe book.
 * Users can specify ingredient amounts, units, and recipe price.
 * Ingredients are dynamically loaded from backend inventory.
 * Quantity defaults to zero.
 * Unit field is required only for ingredients with quantity > 0.
 *
 * @author Harshini Kovur
 * @author ChatGPT
 */
const RecipeComponent = () => {
  /** Recipe name input state */
  const [name, setName] = useState("")
  /** Recipe price input state */
  const [price, setPrice] = useState("")
  /** Dynamic ingredient list state, loaded from backend */
  const [ingredients, setIngredients] = useState([])
  /** Form validation error messages */
  const [errors, setErrors] = useState({ general: "", name: "", price: "", ingredients: [] })

  /** Navigation hook to redirect after successful save */
  const navigate = useNavigate()

  /**
   * Load inventory from backend on component mount
   * Map inventory into a form-friendly structure with default quantity = 0
   */
  useEffect(() => {
    getInventory()
      .then((response) => {
        const inv = response.data.ingredients.map((item) => ({
          key: item.name.toLowerCase(),
          name: item.name,
          quantity: 0, // default quantity
          unit: ""     // default unit
        }))
        setIngredients(inv)
      })
      .catch((err) => {
        console.error(err)
        setErrors((prev) => ({ ...prev, general: "Failed to load inventory." }))
      })
  }, [])

  /**
   * Handle changes to ingredient quantity or unit fields
   * @param {number} index - index of ingredient in the list
   * @param {string} field - "quantity" or "unit"
   * @param {string} value - new value for the field
   */
  const handleIngredientChange = (index, field, value) => {
    const updated = [...ingredients]
    if (field === "quantity") {
      updated[index].quantity = parseInt(value || "0", 10)
      // If quantity is zero, clear unit field
      if (updated[index].quantity === 0) updated[index].unit = ""
    } else if (field === "unit") {
      updated[index].unit = value
    }
    setIngredients(updated)
  }

  /**
   * Validate the form fields before submission
   * @returns {boolean} true if form is valid
   */
  const validateForm = () => {
    let valid = true
    const newErrors = { general: "", name: "", price: "", ingredients: [] }

    // Recipe name validation
    if (!name.trim()) {
      newErrors.name = "Name is required."
      valid = false
    }

    // Price validation
    if (!price || isNaN(price) || parseInt(price) <= 0) {
      newErrors.price = "Price must be a positive integer."
      valid = false
    }

    // Ingredient quantity/unit validation
    ingredients.forEach((ing, idx) => {
      newErrors.ingredients[idx] = ""
      if (ing.quantity < 0 || isNaN(ing.quantity)) {
        newErrors.ingredients[idx] = "Quantity cannot be negative."
        valid = false
      }
      if (ing.quantity > 0 && !ing.unit.trim()) {
        newErrors.ingredients[idx] = "Unit is required when quantity is greater than zero."
        valid = false
      }
    })

    setErrors(newErrors)
    return valid
  }

  /**
   * Submit recipe to backend
   * Combines ingredient quantities and units into expected DTO
   */
  const saveRecipe = (e) => {
    e.preventDefault()
    if (!validateForm()) return

    const recipeData = {
      name,
      price: parseInt(price, 10),
      ingredients: ingredients.map((ing) => ({
        ingredient: { name: ing.name },
        quantity: ing.quantity,
        ...(ing.quantity > 0 ? { unit: ing.unit } : {})
      }))
    }

    createRecipe(recipeData)
      .then(() => navigate("/recipes"))
      .catch((error) => {
        console.error(error)
        const errorsCopy = { ...errors }
        if (error.response?.status === 507) {
          errorsCopy.general = "Recipe list is at capacity."
        } else if (error.response?.status === 409) {
          errorsCopy.general = "Duplicate recipe name."
        } else {
          errorsCopy.general = "Failed to create recipe. Please try again."
        }
        setErrors(errorsCopy)
      })
  }

  return (
    <div className="container">
      <br /><br />
      <div className="row">
        <div className="card col-md-6 offset-md-3">
          <h2 className="text-center">Add New Recipe</h2>
          <div className="card-body">
            {errors.general && (
              <div className="p-3 mb-2 bg-danger text-white">{errors.general}</div>
            )}
            <form onSubmit={saveRecipe}>
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
                    {/* Quantity input */}
                    <input
                      type="number"
                      min="0"
                      value={ing.quantity}
                      placeholder="Quantity"
                      onChange={(e) => handleIngredientChange(idx, "quantity", e.target.value)}
                      className={`form-control ${errors.ingredients[idx] ? "is-invalid" : ""}`}
                    />
                    {/* Unit input, required only if quantity > 0 */}
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
                Submit
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  )
}

export default RecipeComponent
