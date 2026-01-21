package edu.ncsu.csc326.wolfcafe.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import edu.ncsu.csc326.wolfcafe.entity.Item;
import edu.ncsu.csc326.wolfcafe.entity.ItemIngredient;

/**
 * Unit tests for ItemMapper.
 *
 * Covers mapping between Item entity and ItemDto.
 *
 * REFACTORED WITH GENERATIVE AI
 *
 * @author Sreenidhi Kannan
 */
class ItemMapperTest {

    /**
     * Test mapping an Item entity to an ItemDto.
     */
    @Test
    void testMapToDto() {
        ItemIngredient ii = new ItemIngredient();
        Item item = new Item(1L, "Coffee", "Hot coffee", 300, List.of(ii));

        // The bidirectional link should be set by Item constructor
        ii.setItem(item);

        var dto = edu.ncsu.csc326.wolfcafe.mapper.ItemMapper.mapToDto(item);

        assertNotNull(dto, "Mapped ItemDto should not be null");
        assertEquals(item.getId(), dto.getId());
        assertEquals(item.getName(), dto.getName());
        assertEquals(item.getDescription(), dto.getDescription());
        assertEquals(item.getPrice(), dto.getPrice());
        assertEquals(item.getIngredients().size(), dto.getIngredients().size());
    }

    /**
     * Test mapping null Item entity returns null.
     */
    @Test
    void testMapToDtoNull() {
        var dto = edu.ncsu.csc326.wolfcafe.mapper.ItemMapper.mapToDto(null);
        assertNull(dto, "Mapping null Item should return null");
    }

    /**
     * Test mapping an ItemDto to an Item entity.
     */
    @Test
    void testMapToEntity() {
        var ingredientDto = new ItemIngredientDto();
        var dto = new ItemDto(1L, "Latte", "Milk coffee", 400, List.of(ingredientDto));

        var item = edu.ncsu.csc326.wolfcafe.mapper.ItemMapper.mapToEntity(dto);

        assertNotNull(item, "Mapped Item should not be null");
        assertEquals(dto.getId(), item.getId());
        assertEquals(dto.getName(), item.getName());
        assertEquals(dto.getDescription(), item.getDescription());
        assertEquals(dto.getPrice(), item.getPrice());
        assertEquals(dto.getIngredients().size(), item.getIngredients().size());
    }

    /**
     * Test mapping null ItemDto returns null.
     */
    @Test
    void testMapToEntityNull() {
        var item = edu.ncsu.csc326.wolfcafe.mapper.ItemMapper.mapToEntity(null);
        assertNull(item, "Mapping null ItemDto should return null");
    }
}
