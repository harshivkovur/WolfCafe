// src/components/MakeRecipeComponent.jsx
import React, { useEffect, useState } from 'react'
import { listRecipes } from '../services/RecipesService'
import { makeRecipe } from '../services/MakeRecipeService'

/**
 * Component for making a recipe by paying and crafting from inventory.
 * @author Harshini Kovur
 * @author ChatGPT
 */
const MakeRecipeComponent = () => {
    const [recipes, setRecipes] = useState([])
    const [amtPaid, setAmtPaid] = useState("")
    const [change, setChange] = useState(0)
    const [errors, setErrors] = useState({ general: "" })

    useEffect(() => {
        getAllRecipes()
    }, [])

    function getAllRecipes() {
        listRecipes()
            .then(response => setRecipes(response.data))
            .catch(error => console.error(error))
    }

    function validateForm() {
        const errorsCopy = { ...errors }
        let valid = true

        if (!amtPaid || isNaN(amtPaid) || parseFloat(amtPaid) < 0) {
            errorsCopy.general = "Amount paid must be a positive number."
            valid = false
        } else {
            errorsCopy.general = ""
        }

        setErrors(errorsCopy)
        return valid
    }

    function craftRecipe(name, amtPaid) {
        if (!validateForm()) return

        makeRecipe(name, amtPaid)
            .then(response => {
                setChange(response.data)
                setAmtPaid("")
                getAllRecipes()
            })
            .catch(error => {
                console.error(error)
                const errorsCopy = { ...errors }
                if (error.response?.status === 409) {
                    errorsCopy.general = "Insufficient funds to pay."
                } else if (error.response?.status === 400) {
                    errorsCopy.general = "Insufficient inventory."
                } else {
                    errorsCopy.general = "An error occurred while making the recipe."
                }
                setErrors(errorsCopy)
            })
    }

    return (
        <div className="container">
            <h2 className="text-center">Make a Recipe</h2>

            {errors.general && <div className="p-3 mb-2 bg-danger text-white">{errors.general}</div>}

            <div className="card-body">
                <div className="form-group mb-2">
                    <label className="form-label">Amount Paid</label>
                    <input
                        type="text"
                        placeholder="How much are you paying?"
                        value={amtPaid}
                        onChange={(e) => {
                            setAmtPaid(e.target.value)
                            setChange(0)
                        }}
                        className={`form-control ${errors.general ? "is-invalid" : ""}`}
                    />
                    <label className="form-label mt-2">Change: {change}</label>
                </div>
            </div>

            <table className="table table-striped table-bordered">
                <thead>
                    <tr>
                        <th>Recipe Name</th>
                        <th>Recipe Price</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {recipes.map(recipe => (
                        <tr key={recipe.id}>
                            <td>{recipe.name}</td>
                            <td>{recipe.price}</td>
                            <td>
                                <button
                                    className="btn btn-primary"
                                    onClick={() => craftRecipe(recipe.name, amtPaid)}
                                >
                                    Make Recipe
                                </button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    )
}

export default MakeRecipeComponent
