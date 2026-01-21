package edu.ncsu.csc326.wolfcafe.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

/**
 * Create Ingredient class to track name of ingredient and quantity.
 *
 * Each Ingredient belongs to an Inventory, and tracks how much of that specific
 * ingredient is available.
 *
 * GENERATIVE AI WAS USED IN THE CREATION OF THIS CLASS
 *
 * @author- Michael Lewis
 * @author- ChatGPT
 */
@Entity
public class Ingredient {

    /** id for ingredient entry */
    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY )
    private Long                       id;

    /** Name of the ingredient entry */
    private String                     name;

    /** Quantity of the ingredient entry */
    private Integer                    quantity;                           // switched
                                                                           // to
                                                                           // Integer
                                                                           // for
                                                                           // null-safety

    /** The inventory this ingredient belongs to */
    @ManyToOne ( optional = false )
    @JoinColumn ( name = "inventory_id", nullable = false )
    @JsonBackReference
    private Inventory                  inventory;

    /**
     * List of item-ingredient relationships that use this ingredient.
     */
    @OneToMany ( mappedBy = "ingredient", cascade = CascadeType.ALL, orphanRemoval = true )
    private final List<ItemIngredient> itemIngredients = new ArrayList<>();

    /** Empty constructor required by Hibernate */
    protected Ingredient () {
        // Required by JPA
    }

    /**
     * Ingredient constructor with two fields, name and a quantity.
     *
     * @param name
     *            the name of the ingredient
     * @param quantity
     *            the count of the ingredient
     */
    public Ingredient ( final String name, final Integer quantity ) {
        setName( name );
        setQuantity( quantity );
    }

    /**
     * Ingredient constructor with name, quantity, and inventory.
     */
    public Ingredient ( final String name, final Integer quantity, final Inventory inventory ) {
        setName( name );
        setQuantity( quantity );
        setInventory( inventory );
    }

    @Override
    public int hashCode () {
        if ( id != null ) {
            return Objects.hash( id );
        }
        return Objects.hash( name.toLowerCase(), inventory != null ? inventory.getId() : null );
    }

    @Override
    public boolean equals ( final Object obj ) {
        if ( this == obj ) {
            return true;
        }
        if ( ! ( obj instanceof Ingredient ) ) {
            return false;
        }
        final Ingredient other = (Ingredient) obj;

        if ( id != null && other.id != null ) {
            return Objects.equals( id, other.id );
        }
        return name.equalsIgnoreCase( other.name ) && Objects.equals( inventory != null ? inventory.getId() : null,
                other.inventory != null ? other.inventory.getId() : null );
    }

    public Long getId () {
        return id;
    }

    public void setId ( final Long id ) {
        this.id = id;
    }

    public String getName () {
        return name;
    }

    public void setName ( final String name ) {
        this.name = name;
    }

    public Integer getQuantity () {
        return quantity;
    }

    public void setQuantity ( final Integer quantity ) {
        if ( quantity != null && quantity < 0 ) {
            throw new IllegalArgumentException( "You cannot enter a negative amount." );
        }
        this.quantity = quantity;
    }

    public Integer updateQuantity ( final int count ) {
        if ( quantity == null ) {
            throw new IllegalStateException( "Quantity must be set before updating." );
        }
        if ( count < 0 && -count > this.quantity ) {
            throw new IllegalArgumentException( "You cannot remove this amount" );
        }
        setQuantity( this.quantity + count );
        return getQuantity();
    }

    public Inventory getInventory () {
        return inventory;
    }

    public void setInventory ( final Inventory inventory ) {
        this.inventory = inventory;
    }
}
