package edu.ncsu.csc326.wolfcafe.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import edu.ncsu.csc326.wolfcafe.entity.Ingredient;
import edu.ncsu.csc326.wolfcafe.entity.Inventory;

/**
 * Tests Ingredient repository
 *
 * CODE REFACTORED WITH AI
 *
 * @author- Michael Lewis
 * @author- ChatGPT
 */
@DataJpaTest
@AutoConfigureTestDatabase ( replace = Replace.NONE )
class IngredientRepositoryTest {

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private InventoryRepository  inventoryRepository;

    private Ingredient           ingredient1;
    private Ingredient           ingredient2;

    @BeforeEach
    public void setUp () throws Exception {
        ingredientRepository.deleteAll();
        inventoryRepository.deleteAll();

        // Create an inventory for ingredients
        final Inventory inventory = new Inventory();

        // Create ingredients and attach them to inventory
        ingredient1 = new Ingredient( "Chocolate", 5 );
        ingredient2 = new Ingredient( "Milk", 3 );

        inventory.addIngredient( ingredient1 );
        inventory.addIngredient( ingredient2 );

        // Save the inventory (ingredients cascade)
        inventoryRepository.save( inventory );
    }

    @Test
    public void testGetIngredient1ByName () {
        final Optional<Ingredient> ingredient = ingredientRepository.findByName( "chocolate" );
        final Ingredient actualIngredient = ingredient.get();

        assertEquals( "Chocolate", actualIngredient.getName() );
        assertEquals( 5, actualIngredient.getQuantity() );
        assertEquals( 2, actualIngredient.updateQuantity( -3 ) );
        assertEquals( 4, actualIngredient.updateQuantity( 2 ) );
    }

    @Test
    public void testGetRecipeByName2 () {
        final Optional<Ingredient> ingredient = ingredientRepository.findByName( "Milk" );
        final Ingredient actualIngredient = ingredient.get();

        assertEquals( "Milk", actualIngredient.getName() );
        assertEquals( 3, actualIngredient.getQuantity() );
    }

    @Test
    public void testGetRecipeByNameInvalid () {
        final Optional<Ingredient> ingredient = ingredientRepository.findByName( "Unknown" );
        assertTrue( ingredient.isEmpty() );
    }

    /**
     * Test that an error is thrown if we try to create an ingredient with a
     * negative quantity. Zero quantity is allowed.
     */
    @Test
    public void testCreateIngredientInvalidQuantity () {
        // Negative quantity should throw
        assertThrows( IllegalArgumentException.class, () -> new Ingredient( "Sugar", -1 ) );

        // Zero quantity is allowed
        final Ingredient zeroIngredient = new Ingredient( "Tea", 0 );
        assertEquals( 0, zeroIngredient.getQuantity() );
    }

    @Test
    public void testUpdateQuantity () {
        final Optional<Ingredient> ingredient = ingredientRepository.findByName( "Chocolate" );
        final Ingredient actualIngredient = ingredient.get();

        assertEquals( "Chocolate", actualIngredient.getName() );
        assertEquals( 5, actualIngredient.getQuantity() );
        assertEquals( 2, actualIngredient.updateQuantity( -3 ) );
        assertEquals( 2, actualIngredient.getQuantity() );
        assertEquals( 4, actualIngredient.updateQuantity( 2 ) );
        assertEquals( 4, actualIngredient.getQuantity() );
    }

    @Test
    public void testUpdateQuantityInvalid () {
        final Optional<Ingredient> ingredient = ingredientRepository.findByName( "Chocolate" );
        final Ingredient actualIngredient = ingredient.get();

        assertThrows( IllegalArgumentException.class, () -> actualIngredient.updateQuantity( -6 ) );
    }

    @Test
    public void testEquals () {
        final Ingredient ingredient3 = new Ingredient( "chocolate", 5 );
        final Ingredient ingredient4 = new Ingredient( "CHOCOLATE", 3 );

        // Persisted ingredient should equal itself
        assertTrue( ingredient1.equals( ingredient1 ) );

        // Transient ingredients with no id should NOT equal persisted
        // ingredient1
        assertFalse( ingredient1.equals( ingredient3 ) );
        assertFalse( ingredient1.equals( ingredient4 ) );

        // ingredient2 (Milk) should not equal chocolate variants
        assertFalse( ingredient2.equals( ingredient1 ) );
        assertFalse( ingredient2.equals( ingredient3 ) );
        assertFalse( ingredient2.equals( ingredient4 ) );
    }

    @Test
    public void testEqualsBranches () {
        assertTrue( ingredient1.equals( ingredient1 ) );
        assertFalse( ingredient1.equals( null ) );
    }
}
