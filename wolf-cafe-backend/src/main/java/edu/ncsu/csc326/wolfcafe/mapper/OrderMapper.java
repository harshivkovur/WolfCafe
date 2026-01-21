package edu.ncsu.csc326.wolfcafe.mapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import edu.ncsu.csc326.wolfcafe.dto.OrderDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderItemDto;
import edu.ncsu.csc326.wolfcafe.entity.Order;
import edu.ncsu.csc326.wolfcafe.entity.OrderItem;
import edu.ncsu.csc326.wolfcafe.repository.ItemRepository;
import edu.ncsu.csc326.wolfcafe.repository.UserRepository;

public class OrderMapper {
    /**
     * Converts a Order entity to OrderDto
     *
     * @param item
     *            Item to convert
     * @return ItemDto object
     */
    public static OrderDto mapToDto ( final Order order ) {
        if ( order == null ) {
            return null;
        }

        final List<OrderItemDto> items = order.getItems() != null
                ? order.getItems().stream().map( OrderItemMapper::mapToDto ).collect( Collectors.toList() )
                : Collections.emptyList();

        return new OrderDto( order.getId(), order.getCreated(), order.getStatus(), order.getSubtotal(), order.getTax(),
                order.getTip(), order.getCustomer() != null ? order.getCustomer().getId() : null, order.getItemStr(),
                items );
    }

    /**
     * Converts a OrderDto object to a Order entity.
     *
     * @param orderDto
     *            OrderDto to convert
     * @return Order entity
     */
    public static Order mapToEntity ( final OrderDto orderDto, final ItemRepository itemRepository,
            final UserRepository userRepository ) {
        if ( orderDto == null ) {
            return null;
        }
        // Don't use full constructor as to avoid dependency, could be changed
        // to have the setter within the constructor
        final Order order = new Order();
        order.setId( orderDto.getId() );
        order.setCreated( orderDto.getCreated() );
        order.setStatus( orderDto.getStatus() );
        order.setSubtotal( orderDto.getSubtotal() );
        order.setTax( orderDto.getTax() );
        order.setTip( orderDto.getTip() );
        order.setItemStr( orderDto.getItemStr() );
        order.setCustomer(
                orderDto.getCustomerId() != null ? userRepository.findById( orderDto.getCustomerId() ).get() : null );

        final List<OrderItem> items = orderDto.getItems() != null ? orderDto.getItems().stream()
                .map( dto -> OrderItemMapper.mapToEntity( dto, itemRepository ) ).collect( Collectors.toList() )
                : Collections.emptyList();

        order.setItems( items );
        return order;
        // return new Order( orderDto.getId(), orderDto.getCreated(),
        // orderDto.getFulfilled(), orderDto.getSubtotal(),
        // orderDto.getTax(), orderDto.getTip(), orderDto.getItemStr(), null,
        // items );
    }
}
