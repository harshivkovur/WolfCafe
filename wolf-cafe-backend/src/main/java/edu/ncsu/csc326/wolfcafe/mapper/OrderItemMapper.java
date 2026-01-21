package edu.ncsu.csc326.wolfcafe.mapper;

import edu.ncsu.csc326.wolfcafe.dto.OrderItemDto;
import edu.ncsu.csc326.wolfcafe.entity.Item;
import edu.ncsu.csc326.wolfcafe.entity.OrderItem;
import edu.ncsu.csc326.wolfcafe.repository.ItemRepository;

public class OrderItemMapper {
    /*
     * Converts a OrderItem entity to OrderItemDto.
     * @param entity OrderItem to convert
     * @return OrderItemDto object, or null if entity is null
     */
    public static OrderItemDto mapToDto ( final OrderItem entity ) {
        if ( entity == null ) {
            return null;
        }

        final OrderItemDto dto = new OrderItemDto( entity.getId(), entity.getOrder().getId(),
                entity.getItem().getName(), entity.getQuantity() );
        return dto;
    }

    /**
     * Converts a OrderItemDto to a OrderItem entity
     *
     * @param dto
     *            OrderItemDto to convert
     * @return OrderItem entity, or null if dto is null
     */
    public static OrderItem mapToEntity ( final OrderItemDto dto, final ItemRepository itemRepository ) {
        if ( dto == null ) {
            return null;
        }
        final OrderItem orderItem = new OrderItem();

        final Item item = itemRepository.findByName( dto.getItemName() ).get();
        orderItem.setId( dto.getId() );
        orderItem.setItem( item );
        orderItem.setQuantity( dto.getQuantity() );
        return orderItem;
    }
}
