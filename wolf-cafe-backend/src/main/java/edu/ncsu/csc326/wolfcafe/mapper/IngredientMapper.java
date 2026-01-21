package edu.ncsu.csc326.wolfcafe.mapper;

import edu.ncsu.csc326.wolfcafe.dto.IngredientDto;
import edu.ncsu.csc326.wolfcafe.entity.Ingredient;

/**
 * Converts between Ingredient and IngredientDto. Handles mapping both
 * directions while keeping entities and DTOs decoupled.
 *
 * GENERATIVE AI WAS USED IN THE CREATION
 *
 * @author Michael Lewis
 * @author ChatGPT
 */
public class IngredientMapper {

    /**
     * Converts an Ingredient entity to an IngredientDto.
     *
     * @param entity
     *            Ingredient to convert
     * @return IngredientDto object, or null if entity is null
     */
    public static IngredientDto mapToIngredientDto ( final Ingredient entity ) {
        if ( entity == null ) {
            return null;
        }

        final IngredientDto dto = new IngredientDto( entity.getName(), entity.getQuantity() // int
                                                                                            // →
                                                                                            // Integer
                                                                                            // auto-boxing
        );
        dto.setId( entity.getId() );
        return dto;
    }

    /**
     * Converts an IngredientDto to an Ingredient entity.
     *
     * @param dto
     *            IngredientDto to convert
     * @return Ingredient entity, or null if dto is null
     */
    public static Ingredient mapToIngredient ( final IngredientDto dto ) {
        if ( dto == null ) {
            return null;
        }

        // ✅ Default quantity to 0 if null to prevent NPE
        final int quantity = dto.getQuantity() != null ? dto.getQuantity() : 0;

        final Ingredient entity = new Ingredient( dto.getName(), quantity );
        entity.setId( dto.getId() );
        return entity;
    }
}
