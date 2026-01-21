package edu.ncsu.csc326.wolfcafe.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.wolfcafe.entity.Ingredient;
import edu.ncsu.csc326.wolfcafe.entity.Inventory;
import edu.ncsu.csc326.wolfcafe.repository.IngredientRepository;
import edu.ncsu.csc326.wolfcafe.repository.InventoryRepository;
import edu.ncsu.csc326.wolfcafe.service.InventoryService;

/**
 * Controller test for Inventory endpoints.
 *
 * REFACTORED WITH GENERATIVE AI
 *
 * - Uses equality JSONPath assertions (instead of regex) so that tests match
 * the actual serialized JSON response - Preserves setup and debug logging to
 * verify JSON responses during test runs
 *
 * @author Michael Lewis
 * @author ChatGPT
 */
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class InventoryControllerTest {

    /** Mock MVC for testing controller endpoints */
    @Autowired
    private MockMvc              mvc;

    /** Ingredient repository */
    @Autowired
    private IngredientRepository ingredientRepository;

    /** Inventory repository */
    @Autowired
    private InventoryRepository  inventoryRepository;

    /** Inventory repository */
    @Autowired
    private InventoryService     inventoryService;

    /**
     * Reset DB state before each test: - Clears out existing ingredients &
     * inventories - Creates one inventory and attaches four base ingredients -
     * Ensures test starts from a known clean state
     */
    @BeforeEach
    void setup () throws Exception {
        ingredientRepository.deleteAll();
        inventoryRepository.deleteAll();

        // Create a single Inventory (persist first so IDs are set)
        Inventory inventory = new Inventory();
        inventory = inventoryRepository.save( inventory );

        // Create and persist ingredients with inventory set
        final Ingredient coffee = new Ingredient( "coffee", 0, inventory );
        final Ingredient milk = new Ingredient( "milk", 0, inventory );
        final Ingredient sugar = new Ingredient( "sugar", 0, inventory );
        final Ingredient chocolate = new Ingredient( "chocolate", 0, inventory );

        ingredientRepository.save( coffee );
        ingredientRepository.save( milk );
        ingredientRepository.save( sugar );
        ingredientRepository.save( chocolate );

        // Associate ingredients with inventory
        inventory.getIngredients().add( coffee );
        inventory.getIngredients().add( milk );
        inventory.getIngredients().add( sugar );
        inventory.getIngredients().add( chocolate );

        inventoryRepository.save( inventory );
    }

    /**
     * Test GET /api/inventory: - Ensures that all four base ingredients are
     * present - Validates that initial quantities are zero - Uses equality
     * JSONPath filters to directly match ingredient names
     *
     * @throws Exception
     *             if MockMvc call fails
     */
    @Test
    @Transactional
    @WithMockUser ( username = "staff", roles = "STAFF" )
    public void testGetInventory () throws Exception {
        final String response = mvc.perform( get( "/api/inventory" ).accept( MediaType.APPLICATION_JSON ) )
                .andDo( print() ) // log request/response for debug
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        // 🔥 Log raw JSON for debugging
        System.out.println( "🔥 testGetInventory Response JSON: " + response );

        // Assert initial quantities are 0 (direct match on name)
        mvc.perform( get( "/api/inventory" ).accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.ingredients[?(@.name == 'coffee')].quantity" ).value( 0 ) )
                .andExpect( jsonPath( "$.ingredients[?(@.name == 'milk')].quantity" ).value( 0 ) )
                .andExpect( jsonPath( "$.ingredients[?(@.name == 'sugar')].quantity" ).value( 0 ) )
                .andExpect( jsonPath( "$.ingredients[?(@.name == 'chocolate')].quantity" ).value( 0 ) );
    }

    /**
     * Test PUT /api/inventory: - Sends an update request with new quantities -
     * Validates that inventory is updated correctly - Uses equality JSONPath
     * filters to directly match ingredient names
     *
     * @throws Exception
     *             if MockMvc call fails
     */
    @Test
    @Transactional
    @WithMockUser ( username = "staff", roles = "STAFF" )
    public void testUpdateInventory () throws Exception {
        final String updateRequest = """
                {
                  "ingredients": [
                    {"name": "coffee", "quantity": 5},
                    {"name": "milk", "quantity": 10},
                    {"name": "sugar", "quantity": 15},
                    {"name": "chocolate", "quantity": 20}
                  ]
                }
                """;

        // Perform the update
        final String updateResponse = mvc
                .perform( put( "/api/inventory" ).contentType( MediaType.APPLICATION_JSON ).content( updateRequest )
                        .accept( MediaType.APPLICATION_JSON ) )
                .andDo( print() ).andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        // 🔥 Log raw JSON for debugging
        System.out.println( "🔥 testUpdateInventory Response JSON: " + updateResponse );

        // Assert updated quantities (direct match on name)
        mvc.perform( get( "/api/inventory" ).accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.ingredients[?(@.name == 'coffee')].quantity" ).value( 5 ) )
                .andExpect( jsonPath( "$.ingredients[?(@.name == 'milk')].quantity" ).value( 10 ) )
                .andExpect( jsonPath( "$.ingredients[?(@.name == 'sugar')].quantity" ).value( 15 ) )
                .andExpect( jsonPath( "$.ingredients[?(@.name == 'chocolate')].quantity" ).value( 20 ) );
    }

    /**
     * Test Post and Get to update tax
     *
     * @throws Exception
     *
     */
    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = "ADMIN" )
    public void testUpdateTax () throws Exception {
        mvc.perform( get( "/api/inventory/tax" ).accept( MediaType.ALL ) ).andExpect( status().isOk() );
        mvc.perform( post( "/api/inventory/tax" ).contentType( MediaType.APPLICATION_JSON ).content( "0.04" )
                .accept( MediaType.ALL ) ).andExpect( status().isOk() );
        assertEquals( 0.04, inventoryService.setTaxRate( null ) );

    }
}
