package edu.ncsu.csc326.wolfcafe.service;

import java.time.LocalDateTime;
import java.util.List;

import edu.ncsu.csc326.wolfcafe.dto.OrderDto;

/**
 * Order service
 */
public interface OrderService {
    /**
     * Creates an order with the given information.
     *
     * @param orderDto
     *            order to create
     * @return created order
     */
    public OrderDto createOrder ( OrderDto orderDto );

    /**
     * Gets the orders with the given order id
     *
     * @param id
     *            the id of the order
     * @return the order if it exists, null otherwise
     */
    public OrderDto getOrderbyId ( Long id );

    /**
     * Returns the orders created on the given day
     *
     * @param date
     *            the date to check
     * @return a list of the orders created on that day
     */
    public List<OrderDto> getOrdersByCreatedDate ( LocalDateTime date );

    /**
     * Returns the orders created by the given user
     *
     * @param userId
     *            the id of the user to check
     * @return a list of the orders created by that user
     */
    public List<OrderDto> getOrdersByUser ( Long userId );

    /**
     * Returns all the orders
     *
     * @return a list of all the orders
     */
    public List<OrderDto> getAllOrders ();

    /**
     * Updates the status of the order if valid if the transition makes sense
     *
     * @param id
     *            the id of the order to change status
     * @param status
     *            the status to update to
     * @return the dto of the updated order
     */
    public OrderDto updateStatus ( Long id, String status );

    /**
     * Deletes the order with the given id if it exists
     *
     * @param id
     *            the id of the order to delete
     */
    public void deleteOrder ( Long id );

}
