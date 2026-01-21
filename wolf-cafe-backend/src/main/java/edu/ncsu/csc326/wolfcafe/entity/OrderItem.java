package edu.ncsu.csc326.wolfcafe.entity;

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
 * Class for the many to many connection between orders and items, also
 * containing the quantity
 *
 * @author Daniel Yu
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY )
    private Long    id;

    /** Order of the pairing */
    @ManyToOne ( fetch = FetchType.LAZY, optional = false )
    @JoinColumn ( name = "order_id", nullable = false )
    private Order   order;

    /** Item of the pairing */
    @ManyToOne ( fetch = FetchType.LAZY, optional = false )
    @JoinColumn ( name = "item_id", nullable = false )
    private Item    item;

    /** Quantity of the item in the order */
    @Column ( nullable = false )
    private Integer quantity;

}
