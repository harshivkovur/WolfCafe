package edu.ncsu.csc326.wolfcafe.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * InventoryDto for the coffee maker. Used to transfer inventory data between
 * client and server. This class will serve as the response in the REST API.
 *
 * UPDATED WITH ASSISTANCE FROM GENERATIVE AI
 *
 * @author- Teaching Staff
 * @author- Michael Lewis
 * @author- ChatGPT
 */
public class InventoryDto {

    /** id for inventory entry (may be null for new DTOs) */
    private Long                      id;

    /** List of ingredients in inventory */
    private final List<IngredientDto> ingredients = new ArrayList<>();

    /**
     * Empty constructor for frameworks. Intentionally empty so that data can be
     * deserialized into an InventoryDto object.
     */
    public InventoryDto () {
        // Required for serialization/deserialization
    }

    /**
     * Creates an InventoryDto with all fields.
     *
     * @param id
     *            inventory's id
     * @param newInventory
     *            list of ingredients to initialize the inventory with
     */
    public InventoryDto ( final Long id, final List<IngredientDto> newInventory ) {
        this.id = id;
        setIngredients( newInventory );
    }

    /**
     * Add an ingredient to the stock list. Skips if already exists
     *
     * @param ingredient
     *            the ingredient to add to the stock list
     */
    public void addIngredient ( final IngredientDto ingredient ) {
        if ( ingredient == null || ingredient.getName() == null ) {
            return;
        }

        final String normalizedName = ingredient.getName().toLowerCase();
        ingredient.setName( normalizedName ); // ensure stored in lowercase

        for ( final IngredientDto existing : ingredients ) {
            if ( existing.getName().equalsIgnoreCase( normalizedName ) ) {
                existing.setQuantity( ingredient.getQuantity() );
                return;
            }
        }

        ingredients.add( ingredient );
    }

    /**
     * Returns the ID of the inventory.
     *
     * @return the inventory id
     */
    public Long getId () {
        return id;
    }

    /**
     * Set the ID of the Inventory.
     *
     * @param id
     *            the ID
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Returns the stock of ingredients in the inventory.
     *
     * @return the stock list
     */
    public List<IngredientDto> getIngredients () {
        return ingredients;
    }

    /**
     * Replaces the inventory's stock list with a new list.
     *
     * @param stock
     *            the new list of ingredients
     */
    public void setIngredients ( final List<IngredientDto> stock ) {
        this.ingredients.clear();
        if ( stock != null ) {
            stock.forEach( this::addIngredient ); // normalize on add
        }
    }

    /**
     * Removes an ingredient from the stock list.
     *
     * @param ingredient
     *            the ingredient to remove
     */
    public void removeIngredient ( final IngredientDto ingredient ) {
        ingredients.remove( ingredient );
    }

    /**
     * Retrieves an ingredient from the stock list by name.
     *
     * @param ingredientName
     *            the name of the ingredient to search for
     * @return the matching IngredientDto
     * @throws IllegalArgumentException
     *             if no ingredient with the given name is found
     */
    public IngredientDto getIngredientByName ( final String ingredientName ) {
        return ingredients.stream().filter( i -> i.getName().equalsIgnoreCase( ingredientName ) ).findFirst()
                .orElseThrow( () -> new IllegalArgumentException( "Ingredient not found: " + ingredientName ) );
    }

    /**
     * Updates the quantity of a specific ingredient in the inventory.
     *
     * @param ingredientName
     *            the name of the ingredient to update
     * @param delta
     *            the amount to add (positive) or remove (negative)
     * @throws IllegalArgumentException
     *             if ingredient not found or update would result in negative
     *             quantity
     */
    public void updateIngredientQuantity ( final String ingredientName, final int delta ) {
        final IngredientDto ingredient = getIngredientByName( ingredientName );

        final int newQuantity = ingredient.getQuantity() + delta;
        if ( newQuantity < 0 ) {
            throw new IllegalArgumentException( "Not enough " + ingredientName + " in stock" );
        }
        ingredient.setQuantity( newQuantity );
    }

    /**
     * Convenience method: Get the current quantity of an ingredient.
     *
     * @param ingredientName
     *            the ingredient name
     * @return the current quantity
     */
    public int getQuantity ( final String ingredientName ) {
        return getIngredientByName( ingredientName ).getQuantity();
    }

    /**
     * Convenience method: Set the quantity of an ingredient directly.
     *
     * @param ingredientName
     *            the ingredient name
     * @param quantity
     *            the new quantity
     */
    public void setQuantity ( final String ingredientName, final int quantity ) {
        final IngredientDto ingredient = getIngredientByName( ingredientName );
        ingredient.setQuantity( quantity );
    }

    /**
     * Fluent builder method: Set quantity and return this object.
     *
     * @param ingredientName
     *            the ingredient name
     * @param quantity
     *            the quantity
     * @return this InventoryDto for chaining
     */
    public InventoryDto withQuantity ( final String ingredientName, final int quantity ) {
        try {
            setQuantity( ingredientName, quantity );
        }
        catch ( final IllegalArgumentException e ) {
            // If ingredient does not exist yet, add it
            this.addIngredient( new IngredientDto( ingredientName, quantity ) );
        }
        return this;
    }

    /**
     * Override equals for identity.
     *
     * @param o
     *            object to compare
     * @return true if ingredients are equal (ignores id)
     */
    @Override
    public boolean equals ( final Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( ! ( o instanceof InventoryDto ) ) {
            return false;
        }
        final InventoryDto that = (InventoryDto) o;
        return Objects.equals( ingredients, that.ingredients );
    }

    /**
     * Override hashCode for identity.
     *
     * @return hash based on ingredients only (ignores id)
     */
    @Override
    public int hashCode () {
        return Objects.hash( ingredients );
    }
}
