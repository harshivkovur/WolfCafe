package edu.ncsu.csc326.wolfcafe.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Item for data transfer.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {

    /** Item id */
    private Long                    id;

    /** Item name */
    private String                  name;

    /** Item description */
    private String                  description;

    /** Item price in cents */
    private Integer                 price;

    /** List of ingredients for this item */
    private List<ItemIngredientDto> ingredients = new ArrayList<>();

    /**
     * Replace the ingredient list for this recipe.
     *
     * @param newIngredients
     *            the new list of recipe ingredients
     */
    public void setIngredients ( final List<ItemIngredientDto> newIngredients ) {
        this.ingredients.clear();
        if ( newIngredients != null ) {
            for ( final ItemIngredientDto ing : newIngredients ) {
                addIngredient( ing );
            }
        }
    }

    /**
     * Add a single ingredient. Normalizes name to lowercase.
     *
     * @param ingredient
     *            the ingredient to add
     */
    public void addIngredient ( final ItemIngredientDto ingredient ) {
        if ( ingredient == null || ingredient.getIngredient() == null
                || ingredient.getIngredient().getName() == null ) {
            return;
        }
        final String normalizedName = ingredient.getIngredient().getName().toLowerCase();
        ingredient.getIngredient().setName( normalizedName );

        // replace if already exists
        for ( final ItemIngredientDto existing : ingredients ) {
            if ( existing.getIngredient() != null
                    && existing.getIngredient().getName().equalsIgnoreCase( normalizedName ) ) {
                existing.setQuantity( ingredient.getQuantity() );
                existing.setUnit( ingredient.getUnit() );
                return;
            }
        }
        ingredients.add( ingredient );
    }

    /**
     * Gets a specific ingredient by name
     *
     * @param name
     *            the name of the ingredient to get
     * @return the ingredient
     */
    public ItemIngredientDto getIngredient ( final String ingredientName ) {
        final ItemIngredientDto ii = ingredients.stream()
                .filter( i -> i.getIngredient() != null
                        && i.getIngredient().getName().equalsIgnoreCase( ingredientName ) )
                .findFirst()
                .orElseThrow( () -> new IllegalArgumentException( "Ingredient not found: " + ingredientName ) );
        return ii;
    }

}
