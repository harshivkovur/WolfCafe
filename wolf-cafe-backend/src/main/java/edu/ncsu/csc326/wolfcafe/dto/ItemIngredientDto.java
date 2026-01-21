package edu.ncsu.csc326.wolfcafe.dto;

import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ItemIngredientDto class used to transfer json objects into ItemIngredient
 * objects
 *
 * @author Daniel Yu
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ItemIngredientDto {

    /** Unique ID of this ItemIngredient */
    private Long          id;

    /** The ingredient linked to this ItemIngredient */
    private IngredientDto ingredient;

    /** Quantity needed in the item */
    private Integer       quantity;

    /** Unit of measurement for the ingredient (e.g., grams, ml, tsp) */
    private String        unit;

    /** The ID of the recipe this ItemIngredient belongs to */
    private Long          itemId;
    

    // --- Getters and Setters ---

    // --- Convenience methods for Ingredient name ---

    public String getName () {
        return ingredient != null ? ingredient.getName() : null;
    }

    public void setName ( final String name ) {
        if ( this.ingredient == null ) {
            this.ingredient = new IngredientDto();
        }
        this.ingredient.setName( name );
    }

    // --- Equality and Hashing ---

    @Override
    public boolean equals ( final Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( ! ( o instanceof ItemIngredientDto ) ) {
            return false;
        }
        final ItemIngredientDto that = (ItemIngredientDto) o;
        return Objects.equals( id, that.id );
    }

    @Override
    public int hashCode () {
        return Objects.hash( id );
    }

    @Override
    public String toString () {
        return "ItemIngredientDto{" + "id=" + id + ", ingredient="
                + ( ingredient != null ? ingredient.getName() : null ) + ", quantity=" + quantity + ", unit='" + unit
                + '\'' + ", itemId=" + itemId + '}';
    }
}
