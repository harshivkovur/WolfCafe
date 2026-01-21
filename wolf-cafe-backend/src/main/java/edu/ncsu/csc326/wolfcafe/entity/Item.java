package edu.ncsu.csc326.wolfcafe.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents an item for sale in the WolfCafe.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table ( name = "items" )
public class Item {

    /** Item id */
    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY )
    private Long                 id;

    /** Item name */
    @Column ( nullable = false, unique = true )
    private String               name;

    /** Item description */
    private String               description;

    /** Item price in cents */
    @Column ( nullable = false )
    private Integer              price;

    /** List of ingredients for this item */
    @OneToMany ( mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true )
    private List<ItemIngredient> ingredients = new ArrayList<>();

    /**
     * Replace the ingredient list for this item. Ensures the bidirectional
     * relationship is set properly.
     *
     * @param newIngredients
     *            the new list of item ingredients
     */
    public void setIngredients ( final List<ItemIngredient> newIngredients ) {
        this.ingredients.clear();
        if ( newIngredients != null ) {
            for ( final ItemIngredient ri : newIngredients ) {
                addIngredient( ri );
            }
        }
    }

    /**
     * Helper to add a single ingredient and maintain bidirectional link.
     *
     * @param ii
     *            item ingredient to add
     */
    public void addIngredient ( final ItemIngredient ii ) {
        ii.setItem( this );
        this.ingredients.add( ii );
    }

    /**
     * Helper to remove a single ingredient and maintain bidirectional link.
     *
     * @param ii
     *            item ingredient to remove
     */
    public void removeIngredient ( final ItemIngredient ii ) {
        this.ingredients.remove( ii );
        ii.setItem( null );
    }

}
