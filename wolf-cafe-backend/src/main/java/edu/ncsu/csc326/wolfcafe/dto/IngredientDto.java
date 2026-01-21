package edu.ncsu.csc326.wolfcafe.dto;

import java.util.Objects;

/**
 * IngredientDto for transferring ingredient data between client and server.
 *
 * Mirrors the Ingredient entity but used for serialization/deserialization.
 *
 * REFACTORED WITH GENERATIVE AI
 *
 * @author Michael Lewis
 * @author ChatGPT
 */
public class IngredientDto {

    /** Unique ID of this Ingredient */
    private Long id;

    /** Name of the ingredient */
    private String name;

    /** Quantity in inventory (not per recipe, that’s in RecipeIngredientDto) */
    private Integer quantity; // use Integer so it can be null when not set

    /**
     * Empty constructor for serialization/deserialization
     */
    public IngredientDto() {
        // Intentionally empty
    }

    /**
     * Constructor with fields
     *
     * @param name
     *            ingredient name
     * @param quantity
     *            quantity in inventory
     */
    public IngredientDto(final String name, final Integer quantity) {
        // normalize name to lowercase
        this.name = (name != null) ? name.toLowerCase() : null;
        this.quantity = quantity;
    }

    // --- Getters/Setters ---

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    /**
     * Sets the name of the ingredient.
     * Always normalizes to lowercase for consistency.
     *
     * @param name name of the ingredient
     */
    public void setName(final String name) {
        this.name = (name != null) ? name.toLowerCase() : null;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(final Integer quantity) {
        this.quantity = quantity;
    }

    // --- Equals/HashCode ---

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IngredientDto)) {
            return false;
        }
        final IngredientDto that = (IngredientDto) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    // --- toString ---

    @Override
    public String toString() {
        return "IngredientDto{" + "id=" + id + ", name='" + name + '\'' + ", quantity=" + quantity + '}';
    }
}
