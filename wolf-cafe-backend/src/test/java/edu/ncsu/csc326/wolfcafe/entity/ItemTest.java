package edu.ncsu.csc326.wolfcafe.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link Item} entity.
 * @author Sreenidhi Kannan
 */
class ItemTest {

    /** The Item instance under test. */
    private Item item;

    /** First ItemIngredient used for testing ingredient methods. */
    private ItemIngredient ingredient1;

    /** Second ItemIngredient used for testing ingredient methods. */
    private ItemIngredient ingredient2;

    /**
     * Setup and initializes an Item and two ItemIngredient instances.
     */
    @BeforeEach
    void setup() {
        item = new Item();
        item.setName("Coffee");
        item.setPrice(300);
        item.setDescription("Hot coffee drink");

        ingredient1 = new ItemIngredient();
        ingredient1.setIngredient(new Ingredient("Coffee Beans", 50));
        ingredient1.setQuantity(10);
        ingredient1.setUnit("grams");

        ingredient2 = new ItemIngredient();
        ingredient2.setIngredient(new Ingredient("Milk", 100));
        ingredient2.setQuantity(200);
        ingredient2.setUnit("ml");
    }

    /**
     * Tests setters and getters for Item name, price, description.
     */
    @Test
    void testSettersAndGetters() {
        item.setName("Latte");
        item.setPrice(450);
        item.setDescription("Milk coffee");

        assertEquals("Latte", item.getName());
        assertEquals(450, item.getPrice());
        assertEquals("Milk coffee", item.getDescription());
    }

    /**
     * Tests addIngredient method to ensure ingredients are added
     * and bidirectional link is set.
     */
    @Test
    void testAddIngredient() {
        item.addIngredient(ingredient1);

        assertEquals(1, item.getIngredients().size());
        assertEquals(item, ingredient1.getItem());
        assertTrue(item.getIngredients().contains(ingredient1));
    }

    /**
     * Tests removeIngredient method to ensure ingredients are removed
     * and bidirectional link is cleared.
     */
    @Test
    void testRemoveIngredient() {
        item.addIngredient(ingredient1);
        item.addIngredient(ingredient2);

        item.removeIngredient(ingredient1);

        assertEquals(1, item.getIngredients().size());
        assertNull(ingredient1.getItem());
        assertFalse(item.getIngredients().contains(ingredient1));
    }

    /**
     * Tests setIngredients method to ensure ingredient list
     * is replaced properly, bidirectional links are set, and null handling clears the list.
     */
    @Test
    void testSetIngredients() {
        item.setIngredients(Arrays.asList(ingredient1, ingredient2));

        assertEquals(2, item.getIngredients().size());
        assertEquals(item, ingredient1.getItem());
        assertEquals(item, ingredient2.getItem());

        // Setting null should clear the list
        item.setIngredients(null);
        assertTrue(item.getIngredients().isEmpty());
        
        // Cannot assume the ingredient.item is null because setIngredients doesn't update them
        assertEquals(item, ingredient1.getItem());
        assertEquals(item, ingredient2.getItem());
    }

}
