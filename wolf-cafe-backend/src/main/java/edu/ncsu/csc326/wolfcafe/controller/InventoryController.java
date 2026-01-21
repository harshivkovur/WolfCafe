package edu.ncsu.csc326.wolfcafe.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc326.wolfcafe.dto.InventoryDto;
import edu.ncsu.csc326.wolfcafe.service.InventoryService;

/**
 *
 * CODE REFACTORED WITH AI
 *
 * Controller for CoffeeMaker's inventory. The inventory is a singleton; there's
 * only one row in the database that contains the current inventory for the
 * system.
 *
 * @author- Teaching Staff
 * @author- Michael Lewis
 * @author- ChatGPT
 */
@CrossOrigin ( "*" )
@RestController
@RequestMapping ( "/api/inventory" )
public class InventoryController {

    /**
     * Connection to inventory service for manipulating the Inventory model.
     */
    @Autowired
    private InventoryService inventoryService;

    /**
     * REST API endpoint to provide GET access to the CoffeeMaker's singleton
     * Inventory.
     *
     * @return response to the request
     */
    @PreAuthorize ( "hasAnyRole('STAFF', 'ADMIN')" )
    @GetMapping
    public ResponseEntity<InventoryDto> getInventory () {
        final InventoryDto inventoryDto = inventoryService.getInventory();
        return ResponseEntity.ok( inventoryDto );
    }

    /**
     * REST API endpoint to provide update access to the CoffeeMaker's singleton
     * Inventory.
     *
     * If invalid input is provided (e.g., negative quantities), a 400 Bad
     * Request is returned and the inventory is not updated.
     *
     * @param inventoryDto
     *            amounts to add to inventory
     * @return response to the request
     */
    @PreAuthorize ( "hasAnyRole('STAFF', 'ADMIN')" )
    @PutMapping
    public ResponseEntity< ? > updateInventory ( @RequestBody final InventoryDto inventoryDto ) {
        try {
            final InventoryDto savedInventoryDto = inventoryService.updateInventory( inventoryDto );
            return ResponseEntity.ok( savedInventoryDto );
        }
        catch ( final IllegalArgumentException e ) {
            // Return 400 Bad Request with error message if validation fails
            return ResponseEntity.badRequest().body( e.getMessage() );
        }
    }

    /**
     * REST API endpoint to update the tax rate of the system
     *
     * If invalid input is provided (e.g., negative quantities), a 400 Bad
     * Request is returned and the inventory is not updated.
     *
     * @param taxRate
     *            the tax rate the new tax rate to set
     * @return response to the request
     */
    @PreAuthorize ( "hasRole('ADMIN')" )
    @PostMapping ( "/tax" )
    public ResponseEntity<Double> updateTaxRate ( @RequestBody final Double taxRate ) {
        return ResponseEntity.ok( inventoryService.setTaxRate( taxRate ) );
    }

    /**
     * REST API endpoint to get the tax rate of the system
     *
     *
     * @return response to the request
     */
    @GetMapping ( "/tax" )
    public ResponseEntity<Double> getTaxRate () {
        return ResponseEntity.ok( inventoryService.setTaxRate( null ) );
    }

}
