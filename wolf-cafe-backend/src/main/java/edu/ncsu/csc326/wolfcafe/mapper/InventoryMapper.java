package edu.ncsu.csc326.wolfcafe.mapper;

import java.util.List;
import java.util.stream.Collectors;

import edu.ncsu.csc326.wolfcafe.dto.IngredientDto;
import edu.ncsu.csc326.wolfcafe.dto.InventoryDto;
import edu.ncsu.csc326.wolfcafe.entity.Ingredient;
import edu.ncsu.csc326.wolfcafe.entity.Inventory;

/**
 * Converts between Inventory and InventoryDto. Ensures consistency of
 * relationships when mapping.
 *
 * GENERATIVE AI WAS USED IN THE CREATION
 *
 * @author Michael Lewis
 * @author ChatGPT
 */
public class InventoryMapper {

    /**
     * Converts an Inventory entity to an InventoryDto.
     *
     * @param entity
     *            Inventory to convert
     * @return InventoryDto object, or null if entity is null
     */
    public static InventoryDto mapToInventoryDto ( final Inventory entity ) {
        if ( entity == null ) {
            return null;
        }

        // Convert each Ingredient entity to IngredientDto
        final List<IngredientDto> ingredients = entity.getIngredients().stream()
                .map( IngredientMapper::mapToIngredientDto ).collect( Collectors.toList() );

        return new InventoryDto( entity.getId(), ingredients );
    }

    /**
     * Updates an existing Inventory entity from an InventoryDto. This ensures
     * we do not replace Ingredient entities but update their quantities in
     * place.
     *
     * Existing ingredient quantities are updated to the DTO values (absolute
     * totals). New ingredients are added if not already present.
     *
     * @param dto
     *            InventoryDto containing updates
     * @param existingInventory
     *            the Inventory entity to update
     * @return updated Inventory entity
     */
    public static Inventory mapToInventory ( final InventoryDto dto, final Inventory existingInventory ) {
        if ( dto == null || existingInventory == null ) {
            return existingInventory;
        }

        // Iterate over DTO ingredients
        for ( final IngredientDto dtoIngredient : dto.getIngredients() ) {
            // Null-safe quantity: default to 0 if null
            final int qty = ( dtoIngredient.getQuantity() != null ) ? dtoIngredient.getQuantity() : 0;

            // Find existing ingredient in inventory by name (case-insensitive)
            final Ingredient ingredient = existingInventory.getIngredients().stream()
                    .filter( i -> i.getName().equalsIgnoreCase( dtoIngredient.getName() ) ).findFirst().orElse( null );

            if ( ingredient != null ) {
                // Update existing ingredient quantity to the absolute total
                // from DTO
                ingredient.setQuantity( qty );
            }
            else {
                // Add new ingredient if it does not exist
                final Ingredient newIngredient = new Ingredient( dtoIngredient.getName().trim(), qty,
                        existingInventory );
                existingInventory.addIngredient( newIngredient );
            }
        }

        return existingInventory;
    }
}
