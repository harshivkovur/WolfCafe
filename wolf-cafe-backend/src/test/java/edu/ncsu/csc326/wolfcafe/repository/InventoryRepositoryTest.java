package edu.ncsu.csc326.wolfcafe.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
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
 * Tests Inventory repository
 *
 * CODE REFACTORED WITH AI
 *
 * @author- Michael Lewis
 * @author- ChatGPT
 */
@DataJpaTest
@AutoConfigureTestDatabase ( replace = Replace.NONE )
class InventoryRepositoryTest {

    @Autowired
    private InventoryRepository  inventoryRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    private Inventory            inventory;

    private Ingredient           ingredient1;
    private Ingredient           ingredient2;

    @BeforeEach
    public void setUp () throws Exception {
        ingredientRepository.deleteAll();
        inventoryRepository.deleteAll();

        inventory = new Inventory();

        ingredient1 = new Ingredient( "Coffee", 10 );
        ingredient2 = new Ingredient( "Milk", 5 );

        inventory.addIngredient( ingredient1 );
        inventory.addIngredient( ingredient2 );

        inventoryRepository.save( inventory );
    }

    @Test
    public void testSaveAndFindInventory () {
        final Optional<Inventory> found = inventoryRepository.findById( inventory.getId() );
        assertTrue( found.isPresent() );

        final Inventory actual = found.get();
        assertEquals( 2, actual.getIngredients().size() );
    }

    @Test
    public void testAddIngredient () {
        final Ingredient sugar = new Ingredient( "Sugar", 3 );
        inventory.addIngredient( sugar );
        inventoryRepository.save( inventory );

        final Inventory found = inventoryRepository.findById( inventory.getId() ).get();
        assertTrue( found.getIngredients().stream().anyMatch( i -> i.getName().equals( "Sugar" ) ) );
    }

    @Test
    public void testRemoveIngredient () {
        inventory.removeIngredient( ingredient1 );
        inventoryRepository.save( inventory );

        final Inventory found = inventoryRepository.findById( inventory.getId() ).get();
        assertFalse( found.getIngredients().contains( ingredient1 ) );
    }

    @Test
    public void testSetIngredients () {
        final Ingredient a = new Ingredient( "Chocolate", 4 );
        final Ingredient b = new Ingredient( "Tea", 2 );

        inventory.setIngredients( Arrays.asList( a, b ) );
        inventoryRepository.save( inventory );

        final Inventory found = inventoryRepository.findById( inventory.getId() ).get();
        assertEquals( 2, found.getIngredients().size() );
        assertTrue( found.getIngredients().stream().anyMatch( i -> i.getName().equals( "Chocolate" ) ) );
    }

    @Test
    public void testUpdateIngredientQuantity () {
        inventory.updateIngredientQuantity( "Coffee", -3 );
        inventoryRepository.save( inventory );

        final Inventory found = inventoryRepository.findById( inventory.getId() ).get();
        final Ingredient coffee = found.getIngredients().stream()
                .filter( i -> i.getName().equalsIgnoreCase( "Coffee" ) ).findFirst().get();

        assertEquals( 7, coffee.getQuantity() );
    }

    @Test
    public void testUpdateIngredientQuantityInvalid () {
        assertThrows( IllegalArgumentException.class, () -> inventory.updateIngredientQuantity( "Unknown", 1 ) );

        assertThrows( IllegalArgumentException.class, () -> inventory.updateIngredientQuantity( "Milk", -10 ) );
    }

    @Test
    public void testEqualsAndHashCode () {
        final Inventory inv1 = new Inventory();
        inv1.setId( 1L );
        final Inventory inv2 = new Inventory();
        inv2.setId( 1L );

        assertTrue( inv1.equals( inv2 ) );
        assertEquals( inv1.hashCode(), inv2.hashCode() );

        final Inventory inv3 = new Inventory();
        inv3.setId( 2L );
        assertFalse( inv1.equals( inv3 ) );
    }

    @Test
    public void testEqualsBranches () {
        assertTrue( inventory.equals( inventory ) );
        assertFalse( inventory.equals( null ) );
        assertFalse( inventory.equals( new Object() ) );
    }
}
