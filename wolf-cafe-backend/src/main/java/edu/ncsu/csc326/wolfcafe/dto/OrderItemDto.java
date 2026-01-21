package edu.ncsu.csc326.wolfcafe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
    /** Unique ID of this OrderItem */
    private Long    id;

    /** The Id of the order linked to this OrderItem */
    private Long    orderId;

    /** The name of the item in the order */
    private String  itemName;

    /** Quantity of items in the order */
    private Integer quantity;

}
