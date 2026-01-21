import axios from "axios";

/** Base URL for the Recipe API - matches backend RecipeController */
const REST_API_BASE_URL = "http://localhost:8080/api/recipes";

/** GET all recipes */
export const listRecipes = () => axios.get(REST_API_BASE_URL);

/** POST new recipe */
export const createRecipe = (recipe) => axios.post(REST_API_BASE_URL, recipe);

/** GET single recipe by ID */
export const getRecipeById = (id) => axios.get(`${REST_API_BASE_URL}/${id}`);

/** PUT update recipe by ID */
export const updateRecipe = (id, recipe) => axios.put(`${REST_API_BASE_URL}/${id}`, recipe);

/** DELETE recipe by ID */
export const deleteRecipe = (id) => axios.delete(`${REST_API_BASE_URL}/${id}`);
