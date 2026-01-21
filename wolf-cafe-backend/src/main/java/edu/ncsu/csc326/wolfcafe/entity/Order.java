package edu.ncsu.csc326.wolfcafe.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents an order of items in the wolf cafe, containing the information
 * needed to display it to staff and customer
 *
 * @author Daniel Yu
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table ( name = "orders" )
public class Order {

    public static String    FULFILLED = "fulfilled";
    public static String    CANCELED  = "canceled";
    public static String    PENDING   = "pending";
    public static String    PICKED_UP = "picked up";

    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY )
    private Long            id;

    /** when the order was created */
    @Column ( nullable = false )
    private LocalDateTime   created;

    /** Order status, can be pending, picked up, fulfilled, or cancelled */
    private String          status;

    /** Subtotal of the order in cents, sum of prices of items */
    @Column ( nullable = false )
    private Integer         subtotal;

    /** Tax of the order in cents */
    @Column ( nullable = false )
    private Integer         tax;

    /** Tip of the order in cents */
    @Column ( nullable = false )
    private Integer         tip;

    /**
     * String representation of item used to display, won't get changed if
     * foreign keys disappear
     */
    @Column
    private String          itemStr;

    /** The user linked to this order, nullable for anonymous users */
    @ManyToOne ( fetch = FetchType.EAGER, optional = true )
    @JoinColumn ( name = "customer_id", nullable = true )
    private User            customer;

    /** List of ingredients for this item */
    @OneToMany ( mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true )
    private List<OrderItem> items     = new ArrayList<>();

    /**
     * Sets the old list of items to the new list of items
     *
     * @param items
     *            the items to set
     */
    public void setItems ( final List<OrderItem> items ) {
        this.items.clear();
        for ( final OrderItem item : items ) {
            this.items.add( item );
            item.setOrder( this );
        }
    }

}
