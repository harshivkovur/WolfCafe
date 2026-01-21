package edu.ncsu.csc326.wolfcafe.mapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import edu.ncsu.csc326.wolfcafe.dto.ItemDto;
import edu.ncsu.csc326.wolfcafe.dto.ItemIngredientDto;
import edu.ncsu.csc326.wolfcafe.entity.Item;
import edu.ncsu.csc326.wolfcafe.entity.ItemIngredient;

/*
 * Converts between ItemDto and Item entity
 */
public class ItemMapper {

    /**
     * Converts a Item entity to ItemDto
     *
     * @param item
     *            Item to convert
     * @return ItemDto object
     */
    public static ItemDto mapToDto ( final Item item ) {
        if ( item == null ) {
            return null;
        }

        final List<ItemIngredientDto> ingredients = item.getIngredients() != null
                ? item.getIngredients().stream().map( ItemIngredientMapper::mapToDto ).collect( Collectors.toList() )
                : Collections.emptyList();

        return new ItemDto( item.getId(), item.getName(), item.getDescription(), item.getPrice(), ingredients );
    }

    /**
     * Converts a ItemDto object to a Item entity.
     *
     * @param itemDto
     *            ItemDto to convert
     * @return Item entity
     */
    public static Item mapToEntity ( final ItemDto itemDto ) {
        if ( itemDto == null ) {
            return null;
        }

        final List<ItemIngredient> ingredients = itemDto.getIngredients() != null ? itemDto.getIngredients().stream()
                .map( ItemIngredientMapper::mapToEntity ).collect( Collectors.toList() ) : Collections.emptyList();

        return new Item( itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.getPrice(),
                ingredients );
    }
}
