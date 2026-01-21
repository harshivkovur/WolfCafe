package edu.ncsu.csc326.wolfcafe.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import edu.ncsu.csc326.wolfcafe.dto.ItemDto;
import edu.ncsu.csc326.wolfcafe.dto.ItemIngredientDto;
import edu.ncsu.csc326.wolfcafe.entity.Ingredient;
import edu.ncsu.csc326.wolfcafe.entity.Item;
import edu.ncsu.csc326.wolfcafe.entity.ItemIngredient;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.mapper.ItemIngredientMapper;
import edu.ncsu.csc326.wolfcafe.mapper.ItemMapper;
import edu.ncsu.csc326.wolfcafe.repository.IngredientRepository;
import edu.ncsu.csc326.wolfcafe.repository.ItemRepository;
import edu.ncsu.csc326.wolfcafe.service.ItemService;
import lombok.AllArgsConstructor;

/**
 * Implemented item service
 */
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    /** Item repository */
    private final ItemRepository       itemRepository;

    /** Ingredient Repository */
    private final IngredientRepository ingredientRepository;

    /**
     * Adds given item
     *
     * @param itemDto
     *            item to add
     * @return added item
     */
    @Override
    public ItemDto addItem ( final ItemDto itemDto ) {
        final Item item = ItemMapper.mapToEntity( itemDto );

        validateItemDto( itemDto );

        if ( isDuplicateName( itemDto.getName() ) ) {
            throw new IllegalArgumentException( "Recipe with name '" + itemDto.getName() + "' already exists." );
        }

        // Resolve ingredient references to persistent entities
        item.getIngredients().forEach( ii -> {
            final Ingredient ingredient = ingredientRepository.findByName( ii.getIngredient().getName() )
                    .orElseThrow( () -> new IllegalArgumentException(
                            "Ingredient does not exist: " + ii.getIngredient().getName() ) );
            ii.setIngredient( ingredient );
            ii.setItem( item );
            // Ensure unit is non-null for Hibernate
            if ( ii.getUnit() == null || ii.getUnit().trim().isEmpty() ) {
                ii.setUnit( "unit" );
            }
        } );

        final Item savedItem = itemRepository.save( item );
        return ItemMapper.mapToDto( savedItem );
    }

    /**
     * Gets item by id
     *
     * @param id
     *            id of item to get
     * @return returned item
     */
    @Override
    public ItemDto getItem ( final Long id ) {
        final Item item = itemRepository.findById( id )
                .orElseThrow( () -> new ResourceNotFoundException( "Item not found with id " + id ) );
        return ItemMapper.mapToDto( item );
    }

    /**
     * Returns all items
     *
     * @return all items
     */
    @Override
    public List<ItemDto> getAllItems () {
        final List<Item> items = itemRepository.findAll();
        return items.stream().map( ( item ) -> ItemMapper.mapToDto( item ) ).collect( Collectors.toList() );
    }

    /**
     * Updates the item with the given id
     *
     * @param id
     *            id of item to update
     * @param itemDto
     *            information of item to update
     * @return updated item
     */
    @Override
    public ItemDto updateItem ( final Long id, final ItemDto itemDto ) {
        validateItemDto( itemDto );
        final Item item = itemRepository.findById( id )
                .orElseThrow( () -> new ResourceNotFoundException( "Item not found with id " + id ) );

        // Check for duplicate name if changed
        if ( this.isDuplicateName( itemDto.getName() ) && !item.getName().equals( itemDto.getName() ) ) {
            throw new IllegalArgumentException( "Recipe with name '" + itemDto.getName() + "' already exists." );
        }
        item.setName( itemDto.getName() );
        item.setDescription( itemDto.getDescription() );
        item.setPrice( itemDto.getPrice() );

        // Clear and rebuild ingredients
        item.getIngredients().clear();
        for ( final ItemIngredientDto dto : itemDto.getIngredients() ) {
            final ItemIngredient ii = ItemIngredientMapper.mapToEntity( dto );
            final Ingredient ingredient = ingredientRepository.findByName( dto.getIngredient().getName() )
                    .orElseThrow( () -> new IllegalArgumentException(
                            "Ingredient does not exist: " + dto.getIngredient().getName() ) );
            ii.setIngredient( ingredient );
            ii.setItem( item );
            if ( ii.getUnit() == null || ii.getUnit().trim().isEmpty() ) {
                ii.setUnit( "unit" );
            }
            item.getIngredients().add( ii );
        }
        final Item updatedItem = itemRepository.save( ( item ) );
        return ItemMapper.mapToDto( updatedItem );
    }

    /**
     * Deletes the item with the given id
     *
     * @param id
     *            id of item to delete
     */
    @Override
    public void deleteItem ( final Long id ) {
        itemRepository.findById( id )
                .orElseThrow( () -> new ResourceNotFoundException( "Item not found with id " + id ) );
        itemRepository.deleteById( id );
    }

    @Override
    public ItemDto getItemByName ( final String name ) {
        return itemRepository.findByName( name ).map( ItemMapper::mapToDto ).orElse( null );

    }

    /**
     * Helper method to check item data
     */
    private void validateItemDto ( final ItemDto itemDto ) {
        if ( itemDto.getPrice() <= 0 ) {
            throw new IllegalArgumentException( "Price must be greater than 0." );
        }
        if ( itemDto.getIngredients() == null || itemDto.getIngredients().isEmpty() ) {
            throw new IllegalArgumentException( "Item must contain at least one ingredient." );
        }

        itemDto.getIngredients().forEach( ii -> {
            if ( ii.getQuantity() < 0 ) {
                throw new IllegalArgumentException(
                        "Ingredient quantity cannot be negative for: " + ii.getIngredient().getName() );
            }
            // Ensure unit is non-null for Hibernate
            if ( ii.getUnit() == null || ii.getUnit().trim().isEmpty() ) {
                ii.setUnit( "unit" );
            }
        } );
    }

    @Override
    public boolean isDuplicateName ( final String name ) {
        return itemRepository.findByName( name ).isPresent();

    }
}
