package edu.ncsu.csc326.wolfcafe.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
import jakarta.transaction.Transactional;

/**
 * Tests implementation of orderService
 *
 * @author Sreenidhi Kannan (coverage)
 */
@SpringBootTest
class OrderServiceTest {

    @Autowired
    private ItemService          itemService;

    @Autowired
    private ItemRepository       itemRepository;

    @Autowired
    private OrderRepository      orderRepository;

    @Autowired
    private OrderService         orderService;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private InventoryRepository  inventoryRepository;

    @Autowired
    private InventoryService     inventoryService;

    /**
     * Used to create users to search by
     */
    @Autowired
    private UserRepository       userRepository;

    /**
     * Repository used to get roles for users
     */
    @Autowired
    private RoleRepository       roleRepository;

    @BeforeEach
    void setUp () {
        itemRepository.deleteAll();
        inventoryRepository.deleteAll();
        ingredientRepository.deleteAll();
        orderRepository.deleteAll();

        // Reset inventory in DB
        final InventoryDto inventory = inventoryService.getInventory();
        inventory.withQuantity( "coffee", 100 ).withQuantity( "milk", 200 ).withQuantity( "sugar", 100 )
                .withQuantity( "chocolate", 100 );

        inventoryService.updateInventory( inventory );

        final Item item = new Item();
        item.setName( "Coffee" );
        item.setDescription( "Yummy" );
        item.setPrice( 320 );
        item.addIngredient( new ItemIngredient( null, new Ingredient( "Coffee", null ), 2, "grams", item ) );

        final ItemDto itemDto = ItemMapper.mapToDto( item );
        itemService.addItem( itemDto );

        final Item item2 = new Item();
        item2.setName( "Latte" );
        item2.setDescription( "A yummy beverage" );
        item2.setPrice( 360 );

        item2.addIngredient( new ItemIngredient( null, new Ingredient( "Milk", 0 ), 3, "cups", item2 ) );
        item2.addIngredient( new ItemIngredient( null, new Ingredient( "Coffee", 0 ), 1, "grams", item2 ) );
        item2.addIngredient( new ItemIngredient( null, new Ingredient( "Sugar", 0 ), 5, "grams", item2 ) );

        final ItemDto itemDto2 = ItemMapper.mapToDto( item2 );
        itemService.addItem( itemDto2 );

    }

    @Test
    @Transactional
    void testCreateOrder () {
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
        orderDto.setItems( items );
        orderDto.setCreated( LocalDateTime.now() );

        final OrderDto returnedOrderDto = orderService.createOrder( orderDto );
        assertAll( "returnedOrderDto contents", () -> assertEquals( 1960, returnedOrderDto.getSubtotal() ),
                () -> assertEquals( 100, returnedOrderDto.getTax() ),
                () -> assertEquals( 400, returnedOrderDto.getTip() ),
                () -> assertEquals( "Coffee", returnedOrderDto.getItems().get( 0 ).getItemName() ),
                () -> assertEquals( "Latte", returnedOrderDto.getItems().get( 1 ).getItemName() ) );
    }

    /**
     * Test creating, retrieving, and deleting an order with a proper created
     * date.
     */
    @Test
    @Transactional
    void testCreateRetrieveDeleteOrderWithCreatedDate () {
        // Create order
        final OrderDto orderDto = new OrderDto();
        orderDto.setSubtotal( 500 );
        orderDto.setTax( 50 );
        orderDto.setTip( 25 );
        orderDto.setCreated( LocalDateTime.now() ); // IMPORTANT: avoid null
                                                    // created

        final List<OrderItemDto> items = new ArrayList<>();
        final OrderItemDto item1 = new OrderItemDto();
        item1.setItemName( "Coffee" );
        item1.setQuantity( 2 );
        items.add( item1 );
        orderDto.setItems( items );

        // Create
        final OrderDto savedOrder = orderService.createOrder( orderDto );
        assertNotNull( savedOrder.getId(), "Saved order should have an ID" );

        // Retrieve
        final OrderDto retrievedOrder = orderService.getOrderbyId( savedOrder.getId() );
        assertEquals( savedOrder.getId(), retrievedOrder.getId(), "IDs should match" );

        // Delete
        orderService.deleteOrder( savedOrder.getId() );
        assertTrue( orderRepository.findById( savedOrder.getId() ).isEmpty(), "Order should be deleted" );
    }

    /**
     * Test getOrderbyId throws when order does not exist.
     */
    @Test
    @Transactional
    void testGetOrderByIdNotFound () {
        assertThrows( Exception.class, () -> orderService.getOrderbyId( 9999L ) );
    }

    /**
     * Call getOrdersByCreatedDate and getOrdersByUser even if they are not
     * implemented yet.
     */
    @Test
    @Transactional
    void testUnimplementedGetters () {
        final List<OrderDto> byDate = orderService.getOrdersByCreatedDate( LocalDateTime.now() );
        assertTrue( byDate.isEmpty(), "getOrdersByCreatedDate should currently be empty" );

        final List<OrderDto> byUser = orderService.getOrdersByUser( 1L );
        assertTrue( byUser.isEmpty(), "getOrdersByUser should currently be empty" );
    }

    /**
     * Edge case: create order with empty items list.
     */
    @Test
    @Transactional
    void testCreateOrderWithNoItems () {
        final OrderDto order = new OrderDto();
        order.setSubtotal( 0 );
        order.setTax( 0 );
        order.setTip( 0 );
        order.setCreated( LocalDateTime.now() );
        order.setItems( new ArrayList<>() );

        final OrderDto saved = orderService.createOrder( order );
        assertNotNull( saved.getId(), "Order should be saved even with no items" );
        assertEquals( 0, saved.getItems().size(), "Items list should be empty" );
    }

    /**
     * Edge case: create order with null items list.
     */
    @Test
    @Transactional
    void testCreateOrderWithNullItems () {
        final OrderDto order = new OrderDto();
        order.setSubtotal( 0 );
        order.setTax( 0 );
        order.setTip( 0 );
        order.setCreated( LocalDateTime.now() );
        order.setItems( null );

        final OrderDto saved = orderService.createOrder( order );
        assertNotNull( saved.getId(), "Order should be saved even with null items" );
        assertTrue( saved.getItems().isEmpty(), "Items list should default to empty" );
    }

    @Test
    @Transactional
    void testFulfillOrder () {
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

        final Long orderId = orderService.createOrder( orderDto ).getId();

        final OrderDto updatedOrderDto = orderService.updateStatus( orderId, Order.FULFILLED );
        assertAll( "updatedOrderDto contents", () -> assertEquals( 1960, updatedOrderDto.getSubtotal() ),
                () -> assertEquals( 100, updatedOrderDto.getTax() ),
                () -> assertEquals( 400, updatedOrderDto.getTip() ),
                () -> assertEquals( "Coffee", updatedOrderDto.getItems().get( 0 ).getItemName() ),
                () -> assertEquals( "Latte", updatedOrderDto.getItems().get( 1 ).getItemName() ),
                () -> assertEquals( "blahblahblah", updatedOrderDto.getItemStr() ),
                () -> assertEquals( Order.FULFILLED, updatedOrderDto.getStatus() ) );
        final InventoryDto inv = inventoryService.getInventory();

        // confirm correct amount of ingredients deducted
        assertAll( "inv contents", () -> assertEquals( 89, inv.getIngredientByName( "coffee" ).getQuantity() ),
                () -> assertEquals( 95, inv.getIngredientByName( "sugar" ).getQuantity() ),
                () -> assertEquals( 100, inv.getIngredientByName( "chocolate" ).getQuantity() ),
                () -> assertEquals( 197, inv.getIngredientByName( "milk" ).getQuantity() ) );
        assertThrows( IllegalStateException.class, () -> orderService.updateStatus( orderId, Order.FULFILLED ) );
        assertThrows( IllegalStateException.class, () -> orderService.updateStatus( orderId, Order.CANCELED ) );
        assertDoesNotThrow( () -> orderService.updateStatus( orderId, Order.PICKED_UP ) );
        assertThrows( IllegalStateException.class, () -> orderService.updateStatus( orderId, Order.FULFILLED ) );

    }

    @Test
    @Transactional
    void testCancelOrder () {
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

        final Long orderId = orderService.createOrder( orderDto ).getId();

        final OrderDto updatedOrderDto = orderService.updateStatus( orderId, Order.CANCELED );
        assertAll( "updatedOrderDto contents", () -> assertEquals( 1960, updatedOrderDto.getSubtotal() ),
                () -> assertEquals( 100, updatedOrderDto.getTax() ),
                () -> assertEquals( 400, updatedOrderDto.getTip() ),
                () -> assertEquals( "Coffee", updatedOrderDto.getItems().get( 0 ).getItemName() ),
                () -> assertEquals( "Latte", updatedOrderDto.getItems().get( 1 ).getItemName() ),
                () -> assertEquals( "blahblahblah", updatedOrderDto.getItemStr() ),
                () -> assertEquals( Order.CANCELED, updatedOrderDto.getStatus() ) );
        final InventoryDto inv = inventoryService.getInventory();

        // confirm correct amount of ingredients not deducted
        assertAll( "inv contents", () -> assertEquals( 100, inv.getIngredientByName( "coffee" ).getQuantity() ),
                () -> assertEquals( 100, inv.getIngredientByName( "sugar" ).getQuantity() ),
                () -> assertEquals( 100, inv.getIngredientByName( "chocolate" ).getQuantity() ),
                () -> assertEquals( 200, inv.getIngredientByName( "milk" ).getQuantity() ) );
        assertThrows( IllegalStateException.class, () -> orderService.updateStatus( orderId, Order.FULFILLED ) );
        assertThrows( IllegalStateException.class, () -> orderService.updateStatus( orderId, Order.PICKED_UP ) );

    }

    @Test
    @Transactional
    void testGetOrdersByCreated () {

        // create an order today
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

        // create an order at beginning of time
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
        orderDto2.setItems( items );
        orderDto2.setCreated( LocalDateTime.of( 1, 1, 1, 0, 0 ) );

        orderService.createOrder( orderDto );
        orderService.createOrder( orderDto2 );

        // expect only the one from today
        final List<OrderDto> returnedOrderDtos = orderService.getOrdersByCreatedDate( LocalDateTime.now() );
        assertEquals( 1, returnedOrderDtos.size() );
        final OrderDto returnedOrderDto = returnedOrderDtos.getFirst();
        assertAll( "returnedOrderDto contents", () -> assertEquals( 1960, returnedOrderDto.getSubtotal() ),
                () -> assertEquals( 100, returnedOrderDto.getTax() ),
                () -> assertEquals( 400, returnedOrderDto.getTip() ),
                () -> assertEquals( "Coffee", returnedOrderDto.getItems().get( 0 ).getItemName() ),
                () -> assertEquals( "Latte", returnedOrderDto.getItems().get( 1 ).getItemName() ),
                () -> assertEquals( "blahblahblah", returnedOrderDto.getItemStr() ) );
    }

    @Test
    @Transactional
    void testGetOrdersByCustomer () {
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

        // expect only the one from user 2
        final List<OrderDto> returnedOrderDtos = orderService.getOrdersByUser( userId2 );
        assertEquals( 1, returnedOrderDtos.size() );
        final OrderDto returnedOrderDto = returnedOrderDtos.getFirst();
        assertAll( "returnedOrderDto contents", () -> assertEquals( 2120, returnedOrderDto.getSubtotal() ),
                () -> assertEquals( 200, returnedOrderDto.getTax() ),
                () -> assertEquals( 500, returnedOrderDto.getTip() ),
                () -> assertEquals( "Coffee", returnedOrderDto.getItems().get( 0 ).getItemName() ),
                () -> assertEquals( 1, returnedOrderDto.getItems().get( 0 ).getQuantity() ),
                () -> assertEquals( "Latte", returnedOrderDto.getItems().get( 1 ).getItemName() ),
                () -> assertEquals( 5, returnedOrderDto.getItems().get( 1 ).getQuantity() ),

                () -> assertEquals( "blah", returnedOrderDto.getItemStr() ) );
        // Ensure get empty list on customer with no orders(doesn't exist)
        final List<OrderDto> emptyOrderDtos = orderService.getOrdersByUser( -1L );
        assertEquals( 0, emptyOrderDtos.size() );

    }

}
