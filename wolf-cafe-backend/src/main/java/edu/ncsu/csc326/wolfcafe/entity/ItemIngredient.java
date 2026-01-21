package edu.ncsu.csc326.wolfcafe.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Item entity to represent how much of a given ingredient is used in a item.
 *
 *
 * @author Michael Lewis
 * @author Daniel Yu
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemIngredient {

    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY )
    private Long       id;

    /** The ingredient linked to this RecipeIngredient */
    @ManyToOne ( fetch = FetchType.LAZY, optional = false )
    @JoinColumn ( name = "ingredient_id", nullable = false )
    private Ingredient ingredient;

    /**
     * Quantity needed in the recipe (nullable at object creation, validated
     * later)
     */
    @Column ( nullable = false )
    private Integer    quantity = 0;  // default to 0 to avoid null issues

    /** Unit of measurement for the ingredient (e.g., grams, ml, tsp) */
    @Column ( nullable = false )
    private String     unit     = ""; // default empty string to avoid null

    /** The recipe this ItemIngredient belongs to */
    @ManyToOne ( fetch = FetchType.LAZY, optional = false )
    @JoinColumn ( name = "item_id", nullable = false )
    private Item       item;

    /**
     * Constructor without item. Item will be assigned later via
     * Item.addIngredient().
     */
    public ItemIngredient ( final Ingredient ingredient, final Integer quantity, final String unit ) {
        setIngredient( ingredient );
        setQuantity( quantity );
        setUnit( unit );
    }

    // --- Equality and Hashing ---

    @Override
    public boolean equals ( final Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( ! ( o instanceof ItemIngredient ) ) {
            return false;
        }
        final ItemIngredient that = (ItemIngredient) o;
        return Objects.equals( quantity, that.quantity ) && Objects.equals( ingredient, that.ingredient )
                && Objects.equals( unit, that.unit ) && Objects.equals( item, that.item );
    }

    @Override
    public int hashCode () {
        return Objects.hash( ingredient, quantity, unit, item );
    }

    @Override
    public String toString () {
        return "ItemIngredient{" + "id=" + id + ", ingredient=" + ( ingredient != null ? ingredient.getName() : null )
                + ", quantity=" + quantity + ", unit='" + unit + '\'' + ", recipe="
                + ( item != null ? item.getName() : null ) + '}';
    }
}
