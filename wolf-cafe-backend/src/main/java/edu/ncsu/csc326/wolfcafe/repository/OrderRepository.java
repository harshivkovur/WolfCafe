package edu.ncsu.csc326.wolfcafe.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc326.wolfcafe.entity.Order;

/**
 * Repository interface for Orders.
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Filters orders between the given created date
     *
     * @param start
     *            the beginning of the range
     * @param end
     *            the end of the range
     * @return a list of all orders between the start and end
     */
    List<Order> findByCreatedAfterAndCreatedBefore ( LocalDateTime start, LocalDateTime end );

    /**
     * Filters orders by the given customer's user id
     *
     * @param userId
     *            the id of the customer
     * @return a list of all orders made by the customer with the given id
     */
    List<Order> findByCustomerId ( Long userId );
}
