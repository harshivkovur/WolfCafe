package edu.ncsu.csc326.wolfcafe.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link ItemIngredientDto}.
 *
 * Covers constructors, getters/setters, equality, convenience methods,
 * and string representation.
 *
 * REFACTORED WITH GENERATIVE AI
 *
 * @author Sreenidhi Kannan
 */
class ItemIngredientDtoTest {

    /**
     * Test the no-args constructor initializes all fields to null.
     */
    @Test
    void testNoArgsConstructor() {
        ItemIngredientDto dto = new ItemIngredientDto();
        assertNull(dto.getId());
        assertNull(dto.getIngredient());
        assertNull(dto.getQuantity());
        assertNull(dto.getUnit());
        assertNull(dto.getItemId());
    }

    /**
     * Test the all-args constructor sets all fields correctly.
     */
    @Test
    void testAllArgsConstructor() {
        IngredientDto ingredient = new IngredientDto("Sugar", 50);
        ItemIngredientDto dto = new ItemIngredientDto(1L, ingredient, 10, "grams", 2L);

        assertEquals(1L, dto.getId());
        assertEquals(ingredient, dto.getIngredient());
        assertEquals(10, dto.getQuantity());
        assertEquals("grams", dto.getUnit());
        assertEquals(2L, dto.getItemId());
    }

    /**
     * Test setters and getters for all fields.
     */
    @Test
    void testSettersAndGetters() {
        ItemIngredientDto dto = new ItemIngredientDto();

        dto.setId(5L);
        dto.setIngredient(new IngredientDto("Milk", 100));
        dto.setQuantity(200);
        dto.setUnit("ml");
        dto.setItemId(10L);

        assertEquals(5L, dto.getId());
        assertEquals("milk", dto.getIngredient().getName());
        assertEquals(200, dto.getQuantity());
        assertEquals("ml", dto.getUnit());
        assertEquals(10L, dto.getItemId());
    }

    /**
     * Test convenience method getName returns the ingredient name safely.
     */
    @Test
    void testGetName() {
        ItemIngredientDto dto = new ItemIngredientDto();
        assertNull(dto.getName(), "Should return null if ingredient is null");

        IngredientDto ingredient = new IngredientDto("Coffee", 100);
        dto.setIngredient(ingredient);
        assertEquals("coffee", dto.getName());
    }

    /**
     * Test convenience method setName creates ingredient if null.
     */
    @Test
    void testSetName() {
        ItemIngredientDto dto = new ItemIngredientDto();
        dto.setName("Sugar");

        assertNotNull(dto.getIngredient());
        assertEquals("sugar", dto.getIngredient().getName());
    }

    /**
     * Test equals and hashCode rely only on the id field.
     */
    @Test
    void testEqualsAndHashCode() {
        ItemIngredientDto dto1 = new ItemIngredientDto(1L, null, 10, "g", 2L);
        ItemIngredientDto dto2 = new ItemIngredientDto(1L, null, 20, "ml", 3L);
        ItemIngredientDto dto3 = new ItemIngredientDto(2L, null, 10, "g", 2L);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1, null);
        assertNotEquals(dto1, new Object());
    }

    /**
     * Test toString contains all relevant field values.
     */
    @Test
    void testToString() {
        IngredientDto ingredient = new IngredientDto("Honey", 25);
        ItemIngredientDto dto = new ItemIngredientDto(42L, ingredient, 5, "grams", 7L);

        String str = dto.toString().toLowerCase();
        assertTrue(str.contains("42"));
        assertTrue(str.contains("honey"));
        assertTrue(str.contains("5"));
        assertTrue(str.contains("grams"));
        assertTrue(str.contains("7"));
    }
}
