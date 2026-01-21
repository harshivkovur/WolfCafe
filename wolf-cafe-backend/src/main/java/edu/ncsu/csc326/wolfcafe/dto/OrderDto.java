package edu.ncsu.csc326.wolfcafe.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    /** The Unique Id of the Order */
    private Long               id;

    /** The date the order was created */
    private LocalDateTime      created;

    /** Order status, can be pending, picked up, fulfilled, or cancelled */
    private String             status;

    /** Subtotal of the order in cents, sum of prices of items */
    private Integer            subtotal;

    /** Tax of the order in cents */
    private Integer            tax;

    /** Tip of the order in cents */
    private Integer            tip;

    /** The user linked to this order, nullable for anonymous users */
    private Long               customerId;

    /** String representation used for history */
    private String             itemStr;

    /** List of ingredients for this item */
    private List<OrderItemDto> items = new ArrayList<>();

}
