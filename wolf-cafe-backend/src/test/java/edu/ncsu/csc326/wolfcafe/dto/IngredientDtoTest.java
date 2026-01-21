package edu.ncsu.csc326.wolfcafe.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for IngredientDto.
 *
 * Covers constructors, getters/setters, equality, and string representation.
 *
 * REFACTORED WITH GENERATIVE AI
 *
 * @author Michael Lewis
 * @author ChatGPT
 */
class IngredientDtoTest {

    /**
     * Test the empty constructor initializes all fields to null.
     */
    @Test
    void testEmptyConstructor () {
        final IngredientDto dto = new IngredientDto();
        assertNull( dto.getId(), "ID should be null" );
        assertNull( dto.getName(), "Name should be null" );
        assertNull( dto.getQuantity(), "Quantity should be null" );
    }

    /**
     * Test the constructor with fields sets values correctly.
     */
    @Test
    void testFieldConstructor () {
        final IngredientDto dto = new IngredientDto( "Sugar", 50 );
        dto.setId( 1L );

        assertEquals( 1L, dto.getId() );
        assertEquals( "sugar", dto.getName() );
        assertEquals( 50, dto.getQuantity() );
    }

    /**
     * Test setters and getters for each field.
     */
    @Test
    void testSettersAndGetters () {
        final IngredientDto dto = new IngredientDto();

        dto.setId( 10L );
        dto.setName( "Milk" );
        dto.setQuantity( 100 );

        assertEquals( 10L, dto.getId() );
        assertEquals( "milk", dto.getName() );
        assertEquals( 100, dto.getQuantity() );
    }

    /**
     * Test equals and hashCode rely only on the name field.
     */
    @Test
    void testEqualsAndHashCode () {
        final IngredientDto dto1 = new IngredientDto( "Coffee", 200 );
        dto1.setId( 5L );

        final IngredientDto dto2 = new IngredientDto( "Coffee", 300 );
        dto2.setId( 6L );

        final IngredientDto dto3 = new IngredientDto( "Tea", 200 );

        assertEquals( dto1, dto2, "DTOs with the same name should be equal" );
        assertEquals( dto1.hashCode(), dto2.hashCode(), "Hashcodes must match for same name" );

        assertNotEquals( dto1, dto3, "DTOs with different names should not be equal" );
    }

    /**
     * Test that DTOs with null names are handled safely in equals.
     */
    @Test
    void testEqualsWithNullNames () {
        final IngredientDto dto1 = new IngredientDto( null, 10 );
        final IngredientDto dto2 = new IngredientDto( null, 20 );

        assertEquals( dto1, dto2, "DTOs with both names null should be equal" );
    }

    /**
     * Test toString contains all relevant field values.
     */
    /**
     * Test toString contains all relevant field values (case-insensitive).
     */
    @Test
    void testToString () {
        final IngredientDto dto = new IngredientDto( "Honey", 25 );
        dto.setId( 42L );

        final String str = dto.toString().toLowerCase();

        assertTrue( str.contains( "42" ), "toString should include id" );
        assertTrue( str.contains( "honey" ), "toString should include name" );
        assertTrue( str.contains( "25" ), "toString should include quantity" );
    }
    
}
