package edu.ncsu.csc326.wolfcafe.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import edu.ncsu.csc326.wolfcafe.dto.OrderDto;
import edu.ncsu.csc326.wolfcafe.entity.Inventory;
import edu.ncsu.csc326.wolfcafe.entity.ItemIngredient;
import edu.ncsu.csc326.wolfcafe.entity.Order;
import edu.ncsu.csc326.wolfcafe.entity.OrderItem;
import edu.ncsu.csc326.wolfcafe.mapper.OrderMapper;
import edu.ncsu.csc326.wolfcafe.repository.InventoryRepository;
import edu.ncsu.csc326.wolfcafe.repository.ItemRepository;
import edu.ncsu.csc326.wolfcafe.repository.OrderRepository;
import edu.ncsu.csc326.wolfcafe.repository.UserRepository;
import edu.ncsu.csc326.wolfcafe.service.OrderService;
import lombok.AllArgsConstructor;

/**
 * Implemented order service
 */
@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {
    // References to repositories used

    /** Order repository */
    private final OrderRepository     orderRepository;

    /** Item Repository */
    private final ItemRepository      itemRepository;

    /** User repository */
    private final UserRepository      userRepository;

    /** Inventory Repository */
    private final InventoryRepository inventoryRepository;

    @Override
    public OrderDto createOrder ( final OrderDto orderDto ) {
        final Order order = OrderMapper.mapToEntity( orderDto, itemRepository, userRepository );
        order.setStatus( Order.PENDING );

        final Order savedOrder = orderRepository.save( order );
        return OrderMapper.mapToDto( savedOrder );
    }

    @Override
    public OrderDto getOrderbyId ( final Long id ) {
        return OrderMapper.mapToDto( orderRepository.findById( id ).get() );
    }

    @Override
    public List<OrderDto> getOrdersByCreatedDate ( final LocalDateTime date ) {
        final LocalDateTime start = LocalDateTime.of( date.getYear(), date.getMonth(), date.getDayOfMonth(), 0, 0 );
        final LocalDateTime end = LocalDateTime.of( date.getYear(), date.getMonth(), date.getDayOfMonth(), 23, 59 );
        // get all orders during the given date and convert them to Dtos
        return orderRepository.findByCreatedAfterAndCreatedBefore( start, end ).stream().map( OrderMapper::mapToDto )
                .collect( Collectors.toList() );
    }

    @Override
    public List<OrderDto> getOrdersByUser ( final Long userId ) {
        return orderRepository.findByCustomerId( userId ).stream().map( OrderMapper::mapToDto )
                .collect( Collectors.toList() );

    }

    @Override
    public List<OrderDto> getAllOrders () {
        return orderRepository.findAll().stream().map( OrderMapper::mapToDto ).collect( Collectors.toList() );
    }

    @Override
    public void deleteOrder ( final Long id ) {
        orderRepository.findById( id ).get();
        orderRepository.deleteById( id );

    }

    @Override
    public OrderDto updateStatus ( final Long id, final String status ) {
        final Order order = orderRepository.findById( id ).get();

        // If canceling ensure in pending
        if ( status.equals( Order.CANCELED ) ) {
            if ( !order.getStatus().equals( Order.PENDING ) ) {
                throw new IllegalStateException( "Order with " + order.getStatus() + " status cannot be canceled" );
            }
        }
        // if picking up ensure order was fulfilled
        else if ( status.equals( Order.PICKED_UP ) ) {
            if ( !order.getStatus().equals( Order.FULFILLED ) ) {
                throw new IllegalStateException( "Order with " + order.getStatus() + " status cannot be picked up" );
            }
        }
        // If changing to fulfilled run old fulfillOrder code
        else if ( status.equals( Order.FULFILLED ) ) {
            if ( !order.getStatus().equals( Order.PENDING ) ) {
                throw new IllegalStateException( "Order with " + order.getStatus() + " status cannot be fulfilled" );
            }

            // Fetch the existing inventory from DB instead of creating a
            // new
            // one
            final Inventory inventory = inventoryRepository.findAll().stream().findFirst()
                    .orElseThrow( () -> new IllegalStateException( "No inventory exists" ) );

            // Sum up the ingredients in the order
            final HashMap<String, Integer> quantities = new HashMap<String, Integer>();
            for ( final OrderItem oi : order.getItems() ) {
                for ( final ItemIngredient ii : oi.getItem().getIngredients() ) {
                    // increase quantity if it exists, add it to the map
                    // otherwise
                    final String name = ii.getIngredient().getName();
                    if ( quantities.containsKey( name ) ) {
                        quantities.put( name, quantities.get( name ) + ( ( ii.getQuantity() * oi.getQuantity() ) ) );
                    }
                    else {
                        quantities.put( name, ii.getQuantity() * oi.getQuantity() );
                    }

                }
            }

            // Deduct the ingredients if there are enough ingredients
            if ( enoughIngredients( inventory, quantities ) ) {
                for ( final String name : quantities.keySet() ) {
                    inventory.updateIngredientQuantity( name, -quantities.get( name ) );

                }

            }
            else {
                throw new IllegalStateException( "Not enough ingredients" );
            }

        }
        else {
            throw new IllegalStateException( status + " is not a valid status" );
        }
        order.setStatus( status );
        orderRepository.save( order );
        return OrderMapper.mapToDto( order );

    }

    /**
     * Helper method that returns true if there is enough inventory to fulfill
     * the order.
     *
     * @param inventory
     *            the inventory to check
     * @param order
     *            order to check if there are enough ingredients
     * @return true if enough ingredients to fulfill the order
     */
    private boolean enoughIngredients ( final Inventory inventory, final Map<String, Integer> quantities ) {
        // check if we have enough of each ingredient
        for ( final String name : quantities.keySet() ) {
            final int requiredInventory = quantities.get( name );
            final int availableInventory = inventory.getIngredients().stream()
                    .filter( i -> i.getName().equalsIgnoreCase( name ) ).mapToInt( i -> i.getQuantity() ).findFirst()
                    .orElse( 0 );

            if ( availableInventory < requiredInventory ) {
                return false;
            }
        }
        return true;
    }

}
