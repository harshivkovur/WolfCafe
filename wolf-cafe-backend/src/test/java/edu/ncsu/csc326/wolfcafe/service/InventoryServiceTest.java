package edu.ncsu.csc326.wolfcafe.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.wolfcafe.dto.InventoryDto;

/**
 * GENERATED WITH AI Tests InventoryServiceImpl with the new ingredient-based
 * Inventory design.
 *
 * @author- Michael Lewis
 * @author- ChatGPT
 */
@SpringBootTest
@Transactional
public class InventoryServiceTest {

    @Autowired
    private InventoryService inventoryService;

    private InventoryDto     seededInventory;

    @BeforeEach
    public void setUp () {
        // Save the seeded inventory so tests can use the same one
        seededInventory = inventoryService.createInventory( new InventoryDto().withQuantity( "coffee", 5 )
                .withQuantity( "milk", 9 ).withQuantity( "sugar", 14 ).withQuantity( "chocolate", 23 ) );
    }

    @Test
    public void testCreateInventory () {
        final InventoryDto inventoryDto = new InventoryDto().withQuantity( "coffee", 5 ).withQuantity( "milk", 9 )
                .withQuantity( "sugar", 14 ).withQuantity( "chocolate", 23 );

        final InventoryDto createdInventoryDto = inventoryService.createInventory( inventoryDto );

        assertAll( "InventoryDto contents", () -> assertEquals( 5, createdInventoryDto.getQuantity( "coffee" ) ),
                () -> assertEquals( 9, createdInventoryDto.getQuantity( "milk" ) ),
                () -> assertEquals( 14, createdInventoryDto.getQuantity( "sugar" ) ),
                () -> assertEquals( 23, createdInventoryDto.getQuantity( "chocolate" ) ) );
    }

    @Test
    public void testUpdateInventory () {
        // Use the seeded inventory instead of inventoryService.getInventory()
        seededInventory.setQuantity( "coffee", 35 );
        seededInventory.setQuantity( "milk", 17 );
        seededInventory.setQuantity( "sugar", 12 );
        seededInventory.setQuantity( "chocolate", 14 );

        final InventoryDto updatedInventoryDto = inventoryService.updateInventory( seededInventory );

        assertAll( "Updated inventory ingredient quantities",
                () -> assertEquals( 35, updatedInventoryDto.getQuantity( "coffee" ) ),
                () -> assertEquals( 17, updatedInventoryDto.getQuantity( "milk" ) ),
                () -> assertEquals( 12, updatedInventoryDto.getQuantity( "sugar" ) ),
                () -> assertEquals( 14, updatedInventoryDto.getQuantity( "chocolate" ) ) );
    }
}
