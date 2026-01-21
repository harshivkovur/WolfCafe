package edu.ncsu.csc326.wolfcafe.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.ncsu.csc326.wolfcafe.dto.IngredientDto;
import edu.ncsu.csc326.wolfcafe.dto.InventoryDto;
import edu.ncsu.csc326.wolfcafe.entity.Ingredient;
import edu.ncsu.csc326.wolfcafe.entity.Inventory;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.mapper.InventoryMapper;
import edu.ncsu.csc326.wolfcafe.repository.InventoryRepository;
import edu.ncsu.csc326.wolfcafe.service.InventoryService;

/**
 * Implementation of the InventoryService interface.
 */
@Service
public class InventoryServiceImpl implements InventoryService {

    /** Connection to the repository to work with the DAO + database */
    @Autowired
    private InventoryRepository inventoryRepository;

    /**
     * Creates the inventory.
     *
     * @param inventoryDto
     *            inventory to create
     * @return updated inventory after creation
     */
    @Override
    public InventoryDto createInventory ( final InventoryDto inventoryDto ) {
        final Inventory inventory = new Inventory();
        // Map ingredients manually for initial creation
        for ( final IngredientDto ingredientDto : inventoryDto.getIngredients() ) {
            inventory
                    .addIngredient( new Ingredient( ingredientDto.getName(), ingredientDto.getQuantity(), inventory ) );
        }
        final Inventory savedInventory = inventoryRepository.save( inventory );
        return InventoryMapper.mapToInventoryDto( savedInventory );
    }

    /**
     * Returns the single inventory.
     *
     * @return the single inventory
     */
    @Override
    public InventoryDto getInventory () {
        final List<Inventory> inventories = inventoryRepository.findAll();

        if ( inventories.isEmpty() ) {
            final InventoryDto newInventoryDto = new InventoryDto();

            return createInventory( newInventoryDto );
        }

        return InventoryMapper.mapToInventoryDto( inventories.get( 0 ) );
    }

    /**
     * Updates the contents of the inventory.
     *
     * @param inventoryDto
     *            values to update
     * @return updated inventory
     */
    @Override
    public InventoryDto updateInventory ( final InventoryDto inventoryDto ) {
        // Fetch the existing inventory (singleton assumption: only one row
        // should exist)
        final Inventory inventory = inventoryRepository.findAll().stream().findFirst()
                .orElseThrow( () -> new ResourceNotFoundException( "No inventory exists" ) );

        // Validate quantities: allow zero, reject negatives, handle null
        for ( final IngredientDto ing : inventoryDto.getIngredients() ) {
            final int qty = ( ing.getQuantity() != null ) ? ing.getQuantity() : 0;
            if ( qty < 0 ) {
                throw new IllegalArgumentException( "Ingredient quantity cannot be negative: " + ing.getName() );
            }
            // Ensure nulls are treated as 0 before mapping
            ing.setQuantity( qty );
        }

        // Update in-place using the mapper
        InventoryMapper.mapToInventory( inventoryDto, inventory );

        // Save updated entity
        final Inventory savedInventory = inventoryRepository.save( inventory );

        return InventoryMapper.mapToInventoryDto( savedInventory );
    }

    @Override
    public Double setTaxRate ( final Double taxRate ) {
        // Fetch the existing inventory (singleton assumption: only one row
        // should exist)
        final Inventory inventory = inventoryRepository.findAll().stream().findFirst()
                .orElseThrow( () -> new ResourceNotFoundException( "No inventory exists" ) );

        inventory.setTaxRate( taxRate );
        final Inventory returnedInv = inventoryRepository.save( inventory );
        return returnedInv.getTaxRate();
    }
}
