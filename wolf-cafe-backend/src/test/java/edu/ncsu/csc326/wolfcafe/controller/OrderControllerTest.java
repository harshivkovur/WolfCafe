package edu.ncsu.csc326.wolfcafe.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.ncsu.csc326.wolfcafe.TestUtils;
import edu.ncsu.csc326.wolfcafe.WolfCafeApplication;
import edu.ncsu.csc326.wolfcafe.dto.InventoryDto;
import edu.ncsu.csc326.wolfcafe.dto.ItemDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderItemDto;
import edu.ncsu.csc326.wolfcafe.entity.Ingredient;
import edu.ncsu.csc326.wolfcafe.entity.Item;
import edu.ncsu.csc326.wolfcafe.entity.ItemIngredient;
import edu.ncsu.csc326.wolfcafe.entity.Order;
import edu.ncsu.csc326.wolfcafe.entity.Role;
import edu.ncsu.csc326.wolfcafe.entity.User;
import edu.ncsu.csc326.wolfcafe.mapper.ItemMapper;
import edu.ncsu.csc326.wolfcafe.repository.IngredientRepository;
import edu.ncsu.csc326.wolfcafe.repository.InventoryRepository;
import edu.ncsu.csc326.wolfcafe.repository.ItemRepository;
import edu.ncsu.csc326.wolfcafe.repository.OrderRepository;
import edu.ncsu.csc326.wolfcafe.repository.RoleRepository;
import edu.ncsu.csc326.wolfcafe.repository.UserRepository;
import edu.ncsu.csc326.wolfcafe.service.InventoryService;
import edu.ncsu.csc326.wolfcafe.service.ItemService;
import edu.ncsu.csc326.wolfcafe.service.OrderService;
import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration ( classes = WolfCafeApplication.class )
class OrderControllerTest {

    /** Mocked MVC */
    @Autowired
    private MockMvc                   mvc;

    @Autowired
    private ItemService               itemService;

    @Autowired
    private ItemRepository            itemRepository;

    @Autowired
    private OrderRepository           orderRepository;

    @Autowired
    private OrderService              orderService;

    @Autowired
    private IngredientRepository      ingredientRepository;

    @Autowired
    private InventoryRepository       inventoryRepository;

    @Autowired
    private InventoryService          inventoryService;

    /**
     * Used to create users to search by
     */
    @Autowired
    private UserRepository            userRepository;

    /**
     * Repository used to get roles for users
     */
    @Autowired
    private RoleRepository            roleRepository;

    private static final ObjectMapper MAPPER   = new ObjectMapper();
    private static final String       API_PATH = "/api/orders";
    private static final String       ENCODING = "utf-8";

    @BeforeEach
    void setUp () {
        itemRepository.deleteAll();
        ingredientRepository.deleteAll();
        orderRepository.deleteAll();
        inventoryRepository.deleteAll();

        // Reset inventory in DB
        final InventoryDto inventory = inventoryService.getInventory();
        inventory.withQuantity( "coffee", 100 ).withQuantity( "milk", 200 ).withQuantity( "sugar", 100 )
                .withQuantity( "chocolate", 100 );

        inventoryService.updateInventory( inventory );

        final Item item = new Item();
        item.setName( "Coffee" );
        item.setDescription( "Yummy" );
        item.setPrice( 320 );
        final ItemIngredient ii = new ItemIngredient();
        ii.setIngredient( new Ingredient( "Coffee", 0 ) );
        ii.setQuantity( 2 );
        ii.setUnit( "grams" );
        ii.setItem( item );
        item.getIngredients().add( ii );
        final ItemDto itemDto = ItemMapper.mapToDto( item );
        itemService.addItem( itemDto );

        final Item item2 = new Item();
        item2.setName( "Latte" );
        item2.setDescription( "A yummy beverage" );
        item2.setPrice( 360 );

        final ItemIngredient ii2 = new ItemIngredient();
        ii2.setIngredient( new Ingredient( "Milk", null ) );
        ii2.setQuantity( 3 );
        ii2.setUnit( "cups" );
        ii2.setItem( item );
        item2.addIngredient( ii2 );
        final ItemDto itemDto2 = ItemMapper.mapToDto( item2 );
        itemService.addItem( itemDto2 );

    }

    @Test
    @Transactional
    @WithMockUser ( username = "staff", roles = "STAFF" )
    void testGetOrderByUser () throws Exception {

        userRepository.deleteAll();
        // Create some users
        final User user1 = new User();
        user1.setName( "Name1" );
        user1.setUsername( "Username1" );
        user1.setEmail( "email@email1.com" );
        user1.setPassword( "unhashedpw" );
        final Set<Role> roles = new HashSet<>();
        final Role userRole = roleRepository.findByName( "ROLE_CUSTOMER" );
        roles.add( userRole );

        user1.setRoles( roles );

        final User user2 = new User();
        user2.setName( "Name2" );
        user2.setUsername( "Username2" );
        user2.setEmail( "email@email2.com" );
        user2.setPassword( "unhashedpw" );

        user2.setRoles( roles );
        final Long userId1 = userRepository.save( user1 ).getId();
        final Long userId2 = userRepository.save( user2 ).getId();

        // create an order from user 1
        final OrderDto orderDto = new OrderDto();
        orderDto.setSubtotal( 1960 );
        orderDto.setTax( 100 );
        orderDto.setTip( 400 );
        final List<OrderItemDto> items = new ArrayList<>();
        final OrderItemDto item1 = new OrderItemDto();
        item1.setItemName( "Coffee" );
        item1.setQuantity( 5 );
        final OrderItemDto item2 = new OrderItemDto();
        item2.setItemName( "Latte" );
        item2.setQuantity( 1 );
        items.add( item1 );
        items.add( item2 );
        orderDto.setItemStr( "blahblahblah" );
        orderDto.setItems( items );
        orderDto.setCreated( LocalDateTime.now() );
        orderDto.setCustomerId( userId1 );

        // create an order from user 2
        final OrderDto orderDto2 = new OrderDto();
        orderDto2.setSubtotal( 2120 );
        orderDto2.setTax( 200 );
        orderDto2.setTip( 500 );
        final List<OrderItemDto> items2 = new ArrayList<>();
        final OrderItemDto item3 = new OrderItemDto();
        item3.setItemName( "Coffee" );
        item3.setQuantity( 1 );
        final OrderItemDto item4 = new OrderItemDto();
        item4.setItemName( "Latte" );
        item4.setQuantity( 5 );
        items2.add( item3 );
        orderDto2.setItemStr( "blah" );
        orderDto2.setCustomerId( userId2 );

        items2.add( item4 );
        orderDto2.setItems( items2 );
        orderDto2.setCreated( LocalDateTime.of( 1, 1, 1, 0, 0 ) );

        orderService.createOrder( orderDto );
        orderService.createOrder( orderDto2 );

        mvc.perform( post( "/api/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( orderDto ) ) ).andExpect( status().isOk() );
        mvc.perform( post( "/api/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( orderDto2 ) ) ).andExpect( status().isOk() );
        mvc.perform( get( "/api/orders/user/" + userId2 ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() );

    }

    @Test
    @Transactional
    @WithMockUser ( username = "staff", roles = "STAFF" )
    void testFulfillOrder () throws Exception {

        // create an order from user 1
        final OrderDto orderDto = new OrderDto();
        orderDto.setSubtotal( 1960 );
        orderDto.setTax( 100 );
        orderDto.setTip( 400 );
        final List<OrderItemDto> items = new ArrayList<>();
        final OrderItemDto item1 = new OrderItemDto();
        item1.setItemName( "Coffee" );
        item1.setQuantity( 5 );
        final OrderItemDto item2 = new OrderItemDto();
        item2.setItemName( "Latte" );
        item2.setQuantity( 1 );
        items.add( item1 );
        items.add( item2 );
        orderDto.setItemStr( "blahblahblah" );
        orderDto.setItems( items );
        orderDto.setCreated( LocalDateTime.now() );

        final Long id = orderService.createOrder( orderDto ).getId();

        mvc.perform(
                post( "/api/orders/status/" + id ).content( Order.FULFILLED ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() );
        mvc.perform( post( "/api/orders/status/" + id ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isBadRequest() );

    }

    @Test
    @Transactional
    @WithMockUser ( username = "customer", roles = "CUSTOMER" )
    public void testGetAllOrders () throws Exception {
        // create an order from user 1
        final OrderDto orderDto = new OrderDto();
        orderDto.setSubtotal( 1960 );
        orderDto.setTax( 100 );
        orderDto.setTip( 400 );
        final List<OrderItemDto> items = new ArrayList<>();
        final OrderItemDto item1 = new OrderItemDto();
        item1.setItemName( "Coffee" );
        item1.setQuantity( 5 );
        final OrderItemDto item2 = new OrderItemDto();
        item2.setItemName( "Latte" );
        item2.setQuantity( 1 );
        items.add( item1 );
        items.add( item2 );
        orderDto.setItemStr( "blahblahblah" );
        orderDto.setItems( items );
        orderDto.setCreated( LocalDateTime.now() );

        // create an order from user 2
        final OrderDto orderDto2 = new OrderDto();
        orderDto2.setSubtotal( 2120 );
        orderDto2.setTax( 200 );
        orderDto2.setTip( 500 );
        final List<OrderItemDto> items2 = new ArrayList<>();
        final OrderItemDto item3 = new OrderItemDto();
        item3.setItemName( "Coffee" );
        item3.setQuantity( 1 );
        final OrderItemDto item4 = new OrderItemDto();
        item4.setItemName( "Latte" );
        item4.setQuantity( 5 );
        items2.add( item3 );
        orderDto2.setItemStr( "blah" );

        items2.add( item4 );
        orderDto2.setItems( items2 );
        orderDto2.setCreated( LocalDateTime.of( 1, 1, 1, 0, 0 ) );

        final Long id1 = orderService.createOrder( orderDto ).getId();
        final Long id2 = orderService.createOrder( orderDto2 ).getId();

        mvc.perform( get( API_PATH ).contentType( MediaType.APPLICATION_JSON ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$[0].subtotal", Matchers.is( 1960 ) ) )
                .andExpect( jsonPath( "$[1].subtotal", Matchers.is( 2120 ) ) )
                .andExpect( jsonPath( "$[0].id", Matchers.is( id1.intValue() ) ) )
                .andExpect( jsonPath( "$[1].id", Matchers.is( id2.intValue() ) ) );
    }

    @Test

    @Transactional
    @WithMockUser ( username = "staff", roles = "STAFF" )
    public void testGetOrderById () throws Exception {
        // create an order from user 1
        final OrderDto orderDto = new OrderDto();
        orderDto.setSubtotal( 1960 );
        orderDto.setTax( 100 );
        orderDto.setTip( 400 );
        final List<OrderItemDto> items = new ArrayList<>();
        final OrderItemDto item1 = new OrderItemDto();
        item1.setItemName( "Coffee" );
        item1.setQuantity( 5 );
        final OrderItemDto item2 = new OrderItemDto();
        item2.setItemName( "Latte" );
        item2.setQuantity( 1 );
        items.add( item1 );
        items.add( item2 );
        orderDto.setItemStr( "blahblahblah" );
        orderDto.setItems( items );
        orderDto.setCreated( LocalDateTime.now() );

        final OrderDto returned = orderService.createOrder( orderDto );

        mvc.perform( get( API_PATH + "/" + returned.getId() ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() )
                .andExpect( jsonPath( "$.id", Matchers.is( ( returned.getId().intValue() ) ) ) );
    }

    @Test
    @Transactional
    @WithMockUser ( username = "customer", roles = "CUSTOMER" )
    public void testCreateOrder () throws Exception {
        // create an order from user 1
        final OrderDto orderDto = new OrderDto();
        orderDto.setSubtotal( 1960 );
        orderDto.setTax( 100 );
        orderDto.setTip( 400 );
        final List<OrderItemDto> items = new ArrayList<>();
        final OrderItemDto item1 = new OrderItemDto();
        item1.setItemName( "Coffee" );
        item1.setQuantity( 5 );
        final OrderItemDto item2 = new OrderItemDto();
        item2.setItemName( "Latte" );
        item2.setQuantity( 1 );
        items.add( item1 );
        items.add( item2 );
        orderDto.setItemStr( "blahblahblah" );
        orderDto.setItems( items );
        orderDto.setCreated( LocalDateTime.now() );

        // Mockito.when( orderService.createOrder( ArgumentMatchers.any() )
        // ).thenReturn( orderDto );

        final String json = TestUtils.asJsonString( orderDto );

        mvc.perform( post( API_PATH ).contentType( MediaType.APPLICATION_JSON ).characterEncoding( ENCODING )
                .content( json ).accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.subtotal", Matchers.is( 1960 ) ) );
    }

    @Test
    @Transactional

    @WithMockUser ( username = "admin", roles = "ADMIN" )
    public void testDeleteOrder () throws Exception {
        // create an order from user 1
        final OrderDto orderDto = new OrderDto();
        orderDto.setSubtotal( 1960 );
        orderDto.setTax( 100 );
        orderDto.setTip( 400 );
        final List<OrderItemDto> items = new ArrayList<>();
        final OrderItemDto item1 = new OrderItemDto();
        item1.setItemName( "Coffee" );
        item1.setQuantity( 5 );
        final OrderItemDto item2 = new OrderItemDto();
        item2.setItemName( "Latte" );
        item2.setQuantity( 1 );
        items.add( item1 );
        items.add( item2 );
        orderDto.setItemStr( "blahblahblah" );
        orderDto.setItems( items );
        orderDto.setCreated( LocalDateTime.now() );
        final Long id = orderService.createOrder( orderDto ).getId();

        mvc.perform( delete( API_PATH + "/" + id ) ).andExpect( status().isOk() )
                .andExpect( content().string( "Order Successfully deleted" ) );
    }

    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = "ADMIN" )
    public void testDeleteOrderNotFound () throws Exception {

        mvc.perform( delete( API_PATH + "/1000000" ) ).andExpect( status().isNotFound() )
                .andExpect( content().string( "Order not found: 1000000" ) );
    }

}
