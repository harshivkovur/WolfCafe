package edu.ncsu.csc326.wolfcafe.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

/**
 * Inventory for the coffee maker. Inventory is a Data Access Object (DAO) tied
 * to the database using Hibernate libraries. InventoryRepository provides the
 * methods for database CRUD operations.
 *
 * UPDATED WITH ASSISTANCE FROM GENERATIVE AI
 *
 * @author- Teaching Staff
 * @author- Michael Lewis
 * @author- ChatGPT
 */
@Entity
public class Inventory {

    /** id for inventory entry */
    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY )
    private Long                   id;

    /** List of ingredients in inventory */
    @OneToMany ( mappedBy = "inventory", cascade = CascadeType.ALL, orphanRemoval = true )
    @JsonManagedReference // 🔑 Allows proper JSON serialization of
                          // bidirectional relationship
    private final List<Ingredient> ingredients = new ArrayList<>();

    /** Tax rate of the system, as the percent */
    @Column ( nullable = false )
    private Double                 taxRate     = 2.0;

    /**
     * Empty constructor for Hibernate. Intentionally empty so that Hibernate
     * can instantiate an Inventory object.
     */
    public Inventory () {
        // Required by Hibernate
    }

    /**
     * Creates an Inventory with all fields.
     *
     * @param id
     *            inventory's id
     * @param newInventory
     *            list of ingredients to initialize the inventory with
     */
    public Inventory ( final Long id, final List<Ingredient> newInventory ) {
        this.id = id;
        setIngredients( newInventory );
    }

    /**
     * Add an ingredient to the stock list.
     *
     * @param ingredient
     *            the ingredient to add to the stock list
     * @throws IllegalArgumentException
     *             if ingredient already exists
     */
    public void addIngredient ( final Ingredient ingredient ) {
        if ( ingredients.contains( ingredient ) ) {
            throw new IllegalArgumentException( "Ingredient already exists" );
        }
        ingredient.setInventory( this ); // 🔑 maintain bidirectional link
        ingredients.add( ingredient );
    }

    /**
     * Returns the ID of the entry in the DB.
     *
     * @return the inventory id
     */
    public Long getId () {
        return id;
    }

    /**
     * Set the ID of the Inventory (used by Hibernate).
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
    public List<Ingredient> getIngredients () {
        return ingredients;
    }

    /**
     * Replaces the inventory's stock list with a new list.
     *
     * @param stock
     *            the new list of ingredients
     */
    public void setIngredients ( final List<Ingredient> stock ) {
        this.ingredients.clear();
        if ( stock != null ) {
            stock.forEach( i -> i.setInventory( this ) ); // 🔑 ensure link
            this.ingredients.addAll( stock );
        }
    }

    /**
     * Removes an ingredient from the stock list.
     *
     * @param ingredient
     *            the ingredient to remove
     */
    public void removeIngredient ( final Ingredient ingredient ) {
        ingredients.remove( ingredient );
        ingredient.setInventory( null ); // 🔑 break link
    }

    /**
     * Sets the tax rate of the system
     *
     * @param taxRate
     *            the new tax rate to set
     */
    public void setTaxRate ( final Double taxRate ) {
        if ( taxRate != null ) {
            this.taxRate = taxRate;
        }
    }

    /**
     * Returns the tax rate of the system
     *
     * @return taxRate the tax rate of the system
     */
    public Double getTaxRate () {
        return this.taxRate;
    }

    /**
     * Override equals for entity identity.
     *
     * @param o
     *            object to compare
     * @return true if IDs are equal
     */
    @Override
    public boolean equals ( final Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( ! ( o instanceof Inventory ) ) {
            return false;
        }
        final Inventory that = (Inventory) o;
        return Objects.equals( id, that.id );
    }

    /**
     * Override hashCode for entity identity.
     *
     * @return hash based on id
     */
    @Override
    public int hashCode () {
        return Objects.hash( id );
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
        final Ingredient ingredient = ingredients.stream().filter( i -> i.getName().equalsIgnoreCase( ingredientName ) )
                .findFirst()
                .orElseThrow( () -> new IllegalArgumentException( "Ingredient not found: " + ingredientName ) );

        final int newQuantity = ingredient.getQuantity() + delta;
        if ( newQuantity < 0 ) {
            throw new IllegalArgumentException( "Not enough " + ingredientName + " in stock" );
        }
        ingredient.setQuantity( newQuantity );
    }
}
