package edu.ncsu.csc326.wolfcafe.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc326.wolfcafe.dto.OrderDto;
import edu.ncsu.csc326.wolfcafe.service.OrderService;
import lombok.AllArgsConstructor;

/**
 * OrderController provides the endpoint for making and managing orders.
 */
@RestController
@RequestMapping ( "api/orders" )
@AllArgsConstructor
@CrossOrigin ( "*" )
public class OrderController {

    /** Connection to service */
    private final OrderService orderService;

    /**
     * Returns all orders. Requires the ADMIN, STAFF or CUSTOMER role.
     *
     * @return a list of all orders
     */
    @GetMapping
    public ResponseEntity<List<OrderDto>> getAllOrders () {
        final List<OrderDto> orders = orderService.getAllOrders();
        return ResponseEntity.ok( orders );
    }

    /**
     * Returns all orders placed today. Requires the ADMIN or STAFF role
     *
     * @return a list of orders from today
     */
    @PreAuthorize ( "hasAnyRole('ADMIN', 'STAFF')" )
    @GetMapping ( "/today" )
    public ResponseEntity<List<OrderDto>> getTodayOrders () {
        return ResponseEntity.ok( orderService.getOrdersByCreatedDate( LocalDateTime.now() ) );
    }

    /**
     * Returns all orders placed by the given customer id. Requires the ADMIN,
     * STAFF or CUSTOMER role
     *
     * @return a list of orders from today
     */
    @PreAuthorize ( "hasAnyRole('ADMIN', 'STAFF', 'CUSTOMER')" )
    @GetMapping ( "/user/{id}" )
    public ResponseEntity<List<OrderDto>> getCustomerOrders ( @PathVariable ( "id" ) final Long id ) {
        return ResponseEntity.ok( orderService.getOrdersByUser( id ) );
    }

    /**
     * Returns order with given id. Requires the ADMIN, STAFF or CUSTOMER role.
     *
     * @return the order with the given id if it exists
     */
    @PreAuthorize ( "hasAnyRole('ADMIN', 'STAFF', 'CUSTOMER')" )
    @GetMapping ( "{id}" )
    public ResponseEntity<OrderDto> getOrder ( @PathVariable ( "id" ) final Long id ) {
        final OrderDto order = orderService.getOrderbyId( id );
        return ResponseEntity.ok( order );
    }

    /**
     * Creates a new order that has been paid for(checked on frontend)
     *
     * @param orderDto
     *            the order to create
     * @return the created order
     */
    @PostMapping
    public ResponseEntity<OrderDto> createOrder ( @RequestBody final OrderDto orderDto ) {

        final OrderDto savedOrderDto = orderService.createOrder( orderDto );
        return ResponseEntity.ok( savedOrderDto );

    }

    /**
     * Updates the status of the order, fulfills if updating from pending to
     * fulfilled
     *
     * @param id
     *            the id of the order
     * @return a success message or the error if it fails
     */
    @PostMapping ( "/status/{id}" )
    public ResponseEntity<String> updateStatus ( @PathVariable ( "id" ) final Long id,
            @RequestBody final String status ) {
        try {
            orderService.updateStatus( id, status );
            return ResponseEntity.ok( "Order Successfully updated to " + status );
        }
        catch ( final Exception e ) {
            return ResponseEntity.status( HttpStatus.BAD_REQUEST )
                    .body( "Error updating order status: " + e.getMessage() );
        }
    }

    /**
     * Deletes the order with the given id
     *
     * @param id
     *            the id of the order to delete
     * @return ok if successful, error message otherwise
     */
    @DeleteMapping ( "{id}" )
    public ResponseEntity<String> deleteOrder ( @PathVariable ( "id" ) final Long id ) {
        try {
            orderService.deleteOrder( id );
            return ResponseEntity.ok( "Order Successfully deleted" );
        }
        catch ( final Exception e ) {
            return ResponseEntity.status( HttpStatus.NOT_FOUND ).body( "Order not found: " + id );
        }
    }

}
