package edu.ncsu.csc326.wolfcafe.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link ItemIngredient} entity.
 * 
 * @author Sreenidhi Kannan
 */
class ItemIngredientTest {

    /** Sample ingredient used in tests. */
    private Ingredient ingredient;

    /** Sample item used in tests. */
    private Item item;

    /**
     * Initializes common test objects before each test case.
     */
    @BeforeEach
    void setup() {
        ingredient = new Ingredient("Coffee Beans", 100);

        item = new Item();
        item.setName("Latte");
        item.setDescription("Coffee drink");
        item.setPrice(500);
    }

    /**
     * Test the constructor without an associated {@link Item}.
     */
    @Test
    void testConstructorWithoutItem() {
        ItemIngredient ii = new ItemIngredient(ingredient, 3, "grams");

        assertEquals(ingredient, ii.getIngredient());
        assertEquals(3, ii.getQuantity());
        assertEquals("grams", ii.getUnit());
        assertNull(ii.getItem());
    }

    /**
     * Test the full constructor with all fields including {@link Item} and id.
     */
    @Test
    void testFullConstructor() {
        ItemIngredient ii = new ItemIngredient(1L, ingredient, 5, "ml", item);

        assertEquals(1L, ii.getId());
        assertEquals(ingredient, ii.getIngredient());
        assertEquals(5, ii.getQuantity());
        assertEquals("ml", ii.getUnit());
        assertEquals(item, ii.getItem());
    }

    /**
     * Test that setters correctly update all fields.
     */
    @Test
    void testSetters() {
        ItemIngredient ii = new ItemIngredient();

        Ingredient sugar = new Ingredient("Sugar", 50);
        Item newItem = new Item();
        newItem.setName("Sweet Latte");
        newItem.setPrice(600);

        ii.setId(10L);
        ii.setIngredient(sugar);
        ii.setQuantity(2);
        ii.setUnit("tsp");
        ii.setItem(newItem);

        assertEquals(10L, ii.getId());
        assertEquals(sugar, ii.getIngredient());
        assertEquals(2, ii.getQuantity());
        assertEquals("tsp", ii.getUnit());
        assertEquals(newItem, ii.getItem());
    }

    /**
     * Test equals and hashCode for reflexivity, consistency, and null safety.
     */
    @Test
    void testEqualsAndHashCode() {
        ItemIngredient ii1 = new ItemIngredient(ingredient, 3, "g");
        ii1.setItem(item);

        ItemIngredient ii2 = new ItemIngredient(ingredient, 3, "g");
        ii2.setItem(item);

        ItemIngredient ii3 = new ItemIngredient(ingredient, 5, "g");
        ii3.setItem(item);

        assertEquals(ii1, ii2);
        assertEquals(ii1.hashCode(), ii2.hashCode());

        assertNotEquals(ii1, ii3);
        assertNotEquals(ii1, null);
        assertNotEquals(ii1, new Object());
    }

    /**
     * Test that toString output contains ingredient name, quantity, unit, and item name.
     */
    @Test
    void testToString() {
        ItemIngredient ii = new ItemIngredient(ingredient, 4, "ml");
        ii.setItem(item);

        String str = ii.toString();

        assertTrue(str.contains("Coffee Beans"));
        assertTrue(str.contains("4"));
        assertTrue(str.contains("ml"));
        assertTrue(str.contains("Latte"));
    }

    /**
     * Test that adding an {@link ItemIngredient} to {@link Item} sets the bidirectional link.
     */
    @Test
    void testItemBidirectionalAdd() {
        ItemIngredient ii = new ItemIngredient(ingredient, 2, "g");

        item.addIngredient(ii);

        assertEquals(item, ii.getItem());
        assertTrue(item.getIngredients().contains(ii));
    }

    /**
     * Test that removing an {@link ItemIngredient} from {@link Item} clears the bidirectional link.
     */
    @Test
    void testItemBidirectionalRemove() {
        ItemIngredient ii = new ItemIngredient(ingredient, 2, "g");

        item.addIngredient(ii);
        item.removeIngredient(ii);

        assertNull(ii.getItem());
        assertFalse(item.getIngredients().contains(ii));
    }
}
