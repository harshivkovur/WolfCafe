package edu.ncsu.csc326.wolfcafe.mapper;

import edu.ncsu.csc326.wolfcafe.dto.ItemIngredientDto;
import edu.ncsu.csc326.wolfcafe.entity.ItemIngredient;

/**
 * Converts between ItemIngredient and ItemIngredientDto. Uses IngredientMapper
 * for ingredient conversion.
 *
 * @author Michael Lewis
 * @author ChatGPT
 * @author Daniel Yu
 */
public class ItemIngredientMapper {

    /**
     * Converts a ItemIngredient entity to ItemIngredientDto.
     *
     * @param entity
     *            ItemIngredient to convert
     * @return ItemIngredientDto object, or null if entity is null
     */
    public static ItemIngredientDto mapToDto ( final ItemIngredient entity ) {
        if ( entity == null ) {
            return null;
        }

        final ItemIngredientDto dto = new ItemIngredientDto( entity.getId(),
                IngredientMapper.mapToIngredientDto( entity.getIngredient() ), entity.getQuantity(),
                entity.getUnit() != null ? entity.getUnit() : "",
                entity.getItem() != null ? entity.getItem().getId() : null );
        return dto;
    }

    /**
     * Converts a ItemIngredientDto to a ItemIngredient entity
     *
     * @param dto
     *            ItemIngredientDto to convert
     * @return ItemIngredient entity, or null if dto is null
     */
    public static ItemIngredient mapToEntity ( final ItemIngredientDto dto ) {
        if ( dto == null ) {
            return null;
        }

        final ItemIngredient entity = new ItemIngredient();
        entity.setIngredient( IngredientMapper.mapToIngredient( dto.getIngredient() ) );

        // Default to 0 if quantity is null
        final int quantity = dto.getQuantity() != null ? dto.getQuantity() : 0;
        entity.setQuantity( quantity );

        // Default to empty string if unit is null or blank
        entity.setUnit( dto.getUnit() != null && !dto.getUnit().isBlank() ? dto.getUnit().trim() : "" );

        // Recipe is set later by RecipeMapper
        return entity;
    }
}
