package edu.ncsu.csc326.wolfcafe.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for InventoryDto.
 *
 * Covers constructors, add/remove ingredients, retrieval, quantity updates,
 * builder methods, and equality/hashCode behavior.
 *
 * UPDATED WITH ASSISTANCE FROM GENERATIVE AI
 *
 * @author Michael Lewis
 * @author ChatGPT
 */
class InventoryDtoTest {

    private InventoryDto inventory;

    @BeforeEach
    void setUp () {
        inventory = new InventoryDto();
        inventory.addIngredient( new IngredientDto( "coffee", 10 ) );
        inventory.addIngredient( new IngredientDto( "milk", 20 ) );
    }

    /**
     * Test the empty constructor initializes an empty ingredient list.
     */
    @Test
    void testEmptyConstructor () {
        final InventoryDto dto = new InventoryDto();
        assertNotNull( dto.getIngredients(), "Ingredients list should not be null" );
        assertTrue( dto.getIngredients().isEmpty(), "Ingredients list should start empty" );
    }

    /**
     * Test the field constructor initializes id and ingredients.
     */
    @Test
    void testFieldConstructor () {
        final InventoryDto dto = new InventoryDto( 5L,
                Arrays.asList( new IngredientDto( "sugar", 15 ), new IngredientDto( "cocoa", 5 ) ) );

        assertEquals( 5L, dto.getId() );
        assertEquals( 2, dto.getIngredients().size() );
        assertEquals( "sugar", dto.getIngredients().get( 0 ).getName() );
    }

    /**
     * Test addIngredient normalizes names to lowercase and updates quantity if
     * already present.
     */
    @Test
    void testAddIngredient () {
        inventory.addIngredient( new IngredientDto( "Coffee", 50 ) );

        assertEquals( 2, inventory.getIngredients().size(), "Should not add duplicate coffee" );
        assertEquals( 50, inventory.getQuantity( "coffee" ), "Quantity should be updated" );
    }

    /**
     * Test addIngredient with null or missing name is ignored.
     */
    @Test
    void testAddIngredientNullOrInvalid () {
        inventory.addIngredient( null );
        inventory.addIngredient( new IngredientDto( null, 5 ) );

        assertEquals( 2, inventory.getIngredients().size(), "Invalid ingredients should be skipped" );
    }

    /**
     * Test removeIngredient deletes the ingredient from the list.
     */
    @Test
    void testRemoveIngredient () {
        final IngredientDto milk = inventory.getIngredientByName( "milk" );
        inventory.removeIngredient( milk );

        assertEquals( 1, inventory.getIngredients().size() );
        assertThrows( IllegalArgumentException.class, () -> inventory.getIngredientByName( "milk" ) );
    }

    /**
     * Test getIngredientByName returns the correct ingredient or throws if not
     * found.
     */
    @Test
    void testGetIngredientByName () {
        final IngredientDto coffee = inventory.getIngredientByName( "coffee" );
        assertEquals( "coffee", coffee.getName() );
        assertEquals( 10, coffee.getQuantity() );

        assertThrows( IllegalArgumentException.class, () -> inventory.getIngredientByName( "sugar" ) );
    }

    /**
     * Test updateIngredientQuantity modifies stock correctly and prevents
     * negatives.
     */
    @Test
    void testUpdateIngredientQuantity () {
        inventory.updateIngredientQuantity( "coffee", 5 );
        assertEquals( 15, inventory.getQuantity( "coffee" ) );

        inventory.updateIngredientQuantity( "coffee", -10 );
        assertEquals( 5, inventory.getQuantity( "coffee" ) );

        assertThrows( IllegalArgumentException.class, () -> inventory.updateIngredientQuantity( "coffee", -10 ),
                "Should not allow negative quantities" );
    }

    /**
     * Test getQuantity and setQuantity convenience methods.
     */
    @Test
    void testGetAndSetQuantity () {
        assertEquals( 20, inventory.getQuantity( "milk" ) );

        inventory.setQuantity( "milk", 30 );
        assertEquals( 30, inventory.getQuantity( "milk" ) );
    }

    /**
     * Test withQuantity builder updates existing or adds new ingredient.
     */
    @Test
    void testWithQuantity () {
        inventory.withQuantity( "coffee", 40 );
        assertEquals( 40, inventory.getQuantity( "coffee" ) );

        inventory.withQuantity( "sugar", 25 );
        assertEquals( 25, inventory.getQuantity( "sugar" ) );
        assertEquals( 3, inventory.getIngredients().size() );
    }

    /**
     * Test setIngredients replaces existing list with normalized values.
     */
    @Test
    void testSetIngredients () {
        inventory.setIngredients( Collections.singletonList( new IngredientDto( "cocoa", 15 ) ) );

        assertEquals( 1, inventory.getIngredients().size() );
        assertEquals( "cocoa", inventory.getIngredients().get( 0 ).getName() );
    }

    /**
     * Test equals and hashCode compare only ingredients, ignoring id.
     */
    @Test
    void testEqualsAndHashCode () {
        final InventoryDto inv1 = new InventoryDto( 1L, Collections.singletonList( new IngredientDto( "tea", 5 ) ) );
        final InventoryDto inv2 = new InventoryDto( 2L, Collections.singletonList( new IngredientDto( "tea", 5 ) ) );

        assertEquals( inv1, inv2, "Inventories with same ingredients should be equal" );
        assertEquals( inv1.hashCode(), inv2.hashCode(), "HashCodes should match" );

        inv2.addIngredient( new IngredientDto( "sugar", 2 ) );
        assertNotEquals( inv1, inv2, "Different ingredient lists should not be equal" );
    }
}
