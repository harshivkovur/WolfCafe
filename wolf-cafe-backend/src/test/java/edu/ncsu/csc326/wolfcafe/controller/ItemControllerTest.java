package edu.ncsu.csc326.wolfcafe.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.ncsu.csc326.wolfcafe.WolfCafeApplication;
import edu.ncsu.csc326.wolfcafe.dto.ItemDto;
import edu.ncsu.csc326.wolfcafe.service.ItemService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;


/**
 * Tests the ItemController
 * @author Sreenidhi Kannan (coverage)
 */
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration ( classes = WolfCafeApplication.class )
public class ItemControllerTest {

    /** Mocked MVC */
    @Autowired
    private MockMvc                   mvc;

    /** Item Service */
    @MockitoBean
    private ItemService               itemService;

    /** Object mapper */
    private static final ObjectMapper MAPPER           = new ObjectMapper();

    /** API path */
    private static final String       API_PATH         = "/api/items";
    /** Encoding */
    private static final String       ENCODING         = "utf-8";
    /** Item name */
    private static final String       ITEM_NAME        = "Coffee";
    /** Item description */
    private static final String       ITEM_DESCRIPTION = "Coffee is life";
    /** Item price */
    private static final Integer      ITEM_PRICE       = 325;

    /**
     * Test adding an item
     *
     * @throws Exception
     *             if error
     */
    @Test
    @WithMockUser ( username = "staff", roles = "STAFF" )
    public void testCreateItem () throws Exception {
        // Create ItemDto with all contents but the id
        final ItemDto itemDto = new ItemDto();
        itemDto.setName( ITEM_NAME );
        itemDto.setDescription( ITEM_DESCRIPTION );
        itemDto.setPrice( ITEM_PRICE );

        Mockito.when( itemService.addItem( ArgumentMatchers.any() ) ).thenReturn( itemDto );

        final String json = MAPPER.writeValueAsString( itemDto );

        mvc.perform( post( API_PATH ).contentType( MediaType.APPLICATION_JSON ).characterEncoding( ENCODING )
                .content( json ).accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isCreated() )
                .andExpect( jsonPath( "$.name", Matchers.equalTo( ITEM_NAME ) ) )
                .andExpect( jsonPath( "$.description", Matchers.equalTo( ITEM_DESCRIPTION ) ) )
                .andExpect( jsonPath( "$.price", Matchers.equalTo( ITEM_PRICE ) ) );
    }

    /**
     * Tests trying to create an item if the user role is incorrect
     *
     * @throws Exception
     *             if error
     */
    @Test
    public void testCreateItemNotAdmin () throws Exception {
        // Create ItemDto with all contents but the id
        final ItemDto itemDto = new ItemDto();
        itemDto.setName( ITEM_NAME );
        itemDto.setDescription( ITEM_DESCRIPTION );
        itemDto.setPrice( ITEM_PRICE );

        Mockito.when( itemService.addItem( ArgumentMatchers.any() ) ).thenReturn( itemDto );

        final String json = MAPPER.writeValueAsString( itemDto );

        mvc.perform( post( API_PATH ).contentType( MediaType.APPLICATION_JSON ).characterEncoding( ENCODING )
                .content( json ).accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isUnauthorized() );
    }

    /**
     * Tests getting the item as a staff member
     *
     * @throws Exception
     *             if error
     */
    @Test
    @WithMockUser ( username = "staff", roles = "STAFF" )
    public void testGetItemById () throws Exception {
        final ItemDto itemDto = new ItemDto();
        itemDto.setId( 27L );
        itemDto.setName( ITEM_NAME );
        itemDto.setDescription( ITEM_DESCRIPTION );
        itemDto.setPrice( ITEM_PRICE );

        Mockito.when( itemService.getItem( ArgumentMatchers.any() ) ).thenReturn( itemDto );
        final String json = "";

        mvc.perform( get( API_PATH + "/27" ).contentType( MediaType.APPLICATION_JSON ).characterEncoding( ENCODING )
                .content( json ).accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.id", Matchers.equalTo( 27 ) ) )
                .andExpect( jsonPath( "$.name", Matchers.equalTo( ITEM_NAME ) ) )
                .andExpect( jsonPath( "$.description", Matchers.equalTo( ITEM_DESCRIPTION ) ) )
                .andExpect( jsonPath( "$.price", Matchers.equalTo( ITEM_PRICE ) ) );
    }
    
    /**
     * Tests updating an item successfully with ADMIN role.
     * @throws Exception if error
     */
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testUpdateItem() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Latte");
        itemDto.setDescription("Yummy beverage");
        itemDto.setPrice(360);

        Mockito.when(itemService.updateItem(ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
                .thenReturn(itemDto);

        String json = MAPPER.writeValueAsString(itemDto);

        mvc.perform(put(API_PATH + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", Matchers.equalTo("Latte")))
                .andExpect(jsonPath("$.description", Matchers.equalTo("Yummy beverage")))
                .andExpect(jsonPath("$.price", Matchers.equalTo(360)));
    }

    /**
     * Tests updating an item fails for non-ADMIN user.
     * @throws Exception if error
     */
    @Test
    @WithMockUser(username = "staff", roles = "STAFF")
    public void testUpdateItemUnauthorized() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Latte");
        itemDto.setDescription("Yummy beverage");
        itemDto.setPrice(360);

        String json = MAPPER.writeValueAsString(itemDto);

        mvc.perform(put(API_PATH + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests deleting an item successfully with ADMIN role.
     * @throws Exception if error
     */
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testDeleteItem() throws Exception {
        Mockito.doNothing().when(itemService).deleteItem(ArgumentMatchers.anyLong());

        mvc.perform(delete(API_PATH + "/1"))
                .andExpect(status().isOk());
    }

    /**
     * Tests deleting an item fails for non-ADMIN user.
     * @throws Exception if error
     */
    @Test
    @WithMockUser(username = "staff", roles = "STAFF")
    public void testDeleteItemUnauthorized() throws Exception {
        mvc.perform(delete(API_PATH + "/1"))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests getting all items successfully.
     * @throws Exception if error
     */
    @Test
    @WithMockUser(username = "staff", roles = "STAFF")
    public void testGetAllItems() throws Exception {
        ItemDto item1 = new ItemDto();
        item1.setId(1L);
        item1.setName("Coffee");
        item1.setDescription("Yummy");
        item1.setPrice(325);

        ItemDto item2 = new ItemDto();
        item2.setId(2L);
        item2.setName("Latte");
        item2.setDescription("Yummy beverage");
        item2.setPrice(360);

        List<ItemDto> items = Arrays.asList(item1, item2);
        Mockito.when(itemService.getAllItems()).thenReturn(items);

        mvc.perform(get(API_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", Matchers.equalTo("Coffee")))
                .andExpect(jsonPath("$[1].name", Matchers.equalTo("Latte")));
    }

}
