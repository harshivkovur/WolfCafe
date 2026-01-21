package edu.ncsu.csc326.wolfcafe.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.wolfcafe.dto.ItemDto;
import edu.ncsu.csc326.wolfcafe.entity.Ingredient;
import edu.ncsu.csc326.wolfcafe.entity.Inventory;
import edu.ncsu.csc326.wolfcafe.entity.Item;
import edu.ncsu.csc326.wolfcafe.entity.ItemIngredient;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.mapper.ItemMapper;
import edu.ncsu.csc326.wolfcafe.repository.IngredientRepository;
import edu.ncsu.csc326.wolfcafe.repository.InventoryRepository;
import edu.ncsu.csc326.wolfcafe.repository.ItemRepository;

/**
 * Tests ItemServiceImpl
 */
@SpringBootTest
public class ItemServiceTest {

    /** Reference to ItemService */
    @Autowired
    private ItemService          itemService;

    /** Reference to ItemRepository to clear */
    @Autowired
    private ItemRepository       itemRepository;

    /** Reference to IngredientRepository to clear */
    @Autowired
    private IngredientRepository ingredientRepository;

    /** Reference to InventoryRepository to clear */
    @Autowired
    private InventoryRepository  inventoryRepository;

    /** Item name */
    private static final String  ITEM_NAME        = "Coffee";
    /** Item description */
    private static final String  ITEM_DESCRIPTION = "Coffee is life";
    /** Item price */
    private static final Integer ITEM_PRICE       = 325;

    /**
     * Sets up the test case.
     *
     * @throws java.lang.Exception
     *             if error
     */
    @BeforeEach
    public void setUp () throws Exception {
        itemRepository.deleteAll();
        ingredientRepository.deleteAll();
        inventoryRepository.deleteAll();

        final Inventory inv = new Inventory();
        inventoryRepository.save( inv );

        final Ingredient coffee = new Ingredient( "Coffee", 0, inv );
        ingredientRepository.save( coffee );

        final Ingredient milk = new Ingredient( "Milk", 0, inv );
        ingredientRepository.save( milk );

        final Ingredient sugar = new Ingredient( "Sugar", 0, inv );
        ingredientRepository.save( sugar );
    }

    /**
     * Tests creating an item
     */
    @Test
    @Transactional
    void testCreateItem () {
        // Create ItemDto with all contents but the id

        // Create dto by making item and converting to dto
        final Item item = new Item();
        item.setName( ITEM_NAME );
        item.setDescription( ITEM_DESCRIPTION );
        item.setPrice( ITEM_PRICE );
        final ItemIngredient ii = new ItemIngredient();
        ii.setIngredient( new Ingredient( "Coffee", 0 ) );
        ii.setQuantity( 2 );
        ii.setUnit( "grams" );
        ii.setItem( item );
        item.getIngredients().add( ii );
        final ItemDto itemDto = ItemMapper.mapToDto( item );
        final ItemDto createdItemDto = itemService.addItem( itemDto );
        assertAll( "ItemDto contents", () -> assertEquals( ITEM_NAME, createdItemDto.getName() ),
                () -> assertEquals( ITEM_DESCRIPTION, createdItemDto.getDescription() ),
                () -> assertEquals( ITEM_PRICE, createdItemDto.getPrice() ),
                () -> assertEquals( 2, createdItemDto.getIngredient( "Coffee" ).getQuantity() ),
                () -> assertEquals( "grams", createdItemDto.getIngredient( "Coffee" ).getUnit() ) );
    }

    /**
     * Tests creating an item
     */
    @Test
    @Transactional
    void testGetItem () {
        // Create ItemDto with all contents but the id
        // Create dto by making item and converting to dto
        final Item item = new Item();
        item.setName( ITEM_NAME );
        item.setDescription( ITEM_DESCRIPTION );
        item.setPrice( ITEM_PRICE );
        final ItemIngredient ii = new ItemIngredient();
        ii.setIngredient( new Ingredient( "Coffee", null ) );
        ii.setQuantity( 2 );
        ii.setUnit( "grams" );
        ii.setItem( item );
        item.addIngredient( ii );
        final ItemDto itemDto = ItemMapper.mapToDto( item );
        final ItemDto createdItemDto = itemService.addItem( itemDto );

        final ItemDto retrievedItemDto = itemService.getItem( createdItemDto.getId() );
        assertAll( "ItemDto contents", () -> assertEquals( ITEM_NAME, retrievedItemDto.getName() ),
                () -> assertEquals( ITEM_DESCRIPTION, retrievedItemDto.getDescription() ),
                () -> assertEquals( ITEM_PRICE, retrievedItemDto.getPrice() ) );
    }

    /**
     * Tests creating an item
     */
    @Test
    @Transactional
    void testGetItemException () {
        // Create dto by making item and converting to dto
        final Item item = new Item();
        item.setName( ITEM_NAME );
        item.setDescription( ITEM_DESCRIPTION );
        item.setPrice( ITEM_PRICE );
        final ItemIngredient ii = new ItemIngredient();
        ii.setIngredient( new Ingredient( "Coffee", null ) );
        ii.setQuantity( 2 );
        ii.setUnit( "grams" );
        ii.setItem( item );
        item.addIngredient( ii );
        final ItemDto itemDto = ItemMapper.mapToDto( item );
        final ItemDto createdItemDto = itemService.addItem( itemDto );

        assertThrows( ResourceNotFoundException.class, () -> itemService.getItem( createdItemDto.getId() + 1 ) );
    }

    /**
     * Tests creating an item
     */
    @Test
    @Transactional
    void testUpdateItem () {
        // Create ItemDto with all contents but the id
        // Create dto by making item and converting to dto
        final Item item = new Item();
        item.setName( ITEM_NAME );
        item.setDescription( ITEM_DESCRIPTION );
        item.setPrice( ITEM_PRICE );
        final ItemIngredient ii = new ItemIngredient();
        ii.setIngredient( new Ingredient( "Coffee", null ) );
        ii.setQuantity( 2 );
        ii.setUnit( "grams" );
        ii.setItem( item );
        item.addIngredient( ii );
        final ItemDto itemDto = ItemMapper.mapToDto( item );

        final ItemDto createdItemDto = itemService.addItem( itemDto );

        final Item item2 = new Item();
        item2.setName( "Latte" );
        item2.setDescription( "A yummy beverage" );
        item2.setPrice( 357 );

        final ItemIngredient ii2 = new ItemIngredient();
        ii2.setIngredient( new Ingredient( "Milk", null ) );
        ii2.setQuantity( 3 );
        ii2.setUnit( "cups" );
        ii2.setItem( item );
        item2.addIngredient( ii2 );

        final ItemDto updatedItemDto = itemService.updateItem( createdItemDto.getId(), ItemMapper.mapToDto( item2 ) );
        assertAll( "ItemDto contents", () -> assertEquals( "Latte", updatedItemDto.getName() ),
                () -> assertEquals( "A yummy beverage", updatedItemDto.getDescription() ),
                () -> assertEquals( 357, updatedItemDto.getPrice() ) );
    }

    /**
     * Tests creating an item
     */
    @Test
    @Transactional
    void testUpdateItemException () {
        // Create ItemDto with all contents but the id
        final Item item = new Item();
        item.setName( ITEM_NAME );
        item.setDescription( ITEM_DESCRIPTION );
        item.setPrice( ITEM_PRICE );
        final ItemIngredient ii = new ItemIngredient();
        ii.setIngredient( new Ingredient( "Coffee", null ) );
        ii.setQuantity( 2 );
        ii.setUnit( "grams" );
        ii.setItem( item );
        item.addIngredient( ii );
        final ItemDto itemDto = ItemMapper.mapToDto( item );

        final ItemDto createdItemDto = itemService.addItem( itemDto );

        final Item item2 = new Item();
        item2.setName( "Latte" );
        item2.setDescription( "A yummy beverage" );
        item2.setPrice( 357 );

        final ItemIngredient ii2 = new ItemIngredient();
        ii2.setIngredient( new Ingredient( "Milk", null ) );
        ii2.setQuantity( 3 );
        ii2.setUnit( "cups" );
        ii2.setItem( item );
        item2.addIngredient( ii2 );

        assertThrows( ResourceNotFoundException.class,
                () -> itemService.updateItem( createdItemDto.getId() + 1, ItemMapper.mapToDto( item2 ) ) );
    }

    /**
     * Tests creating an item
     */
    @Test
    @Transactional
    void testDeleteItem () {
        // Create dto by making item and converting to dto
        final Item item = new Item();
        item.setName( ITEM_NAME );
        item.setDescription( ITEM_DESCRIPTION );
        item.setPrice( ITEM_PRICE );
        final ItemIngredient ii = new ItemIngredient();
        ii.setIngredient( new Ingredient( "Coffee", null ) );
        ii.setQuantity( 2 );
        ii.setUnit( "grams" );
        ii.setItem( item );
        item.addIngredient( ii );
        final ItemDto itemDto = ItemMapper.mapToDto( item );

        final ItemDto createdItemDto = itemService.addItem( itemDto );

        itemService.deleteItem( createdItemDto.getId() );
        assertThrows( ResourceNotFoundException.class, () -> itemService.getItem( createdItemDto.getId() ) );
    }

    /**
     * Tests getting all items
     */
    @Test
    @Transactional
    void testGetAllItems () {
        // Create dto by making item and converting to dto
        final Item item = new Item();
        item.setName( ITEM_NAME );
        item.setDescription( ITEM_DESCRIPTION );
        item.setPrice( ITEM_PRICE );
        final ItemIngredient ii = new ItemIngredient();
        ii.setIngredient( new Ingredient( "Coffee", null ) );
        ii.setQuantity( 2 );
        ii.setUnit( "grams" );
        ii.setItem( item );
        item.addIngredient( ii );
        final ItemDto itemDto = ItemMapper.mapToDto( item );
        itemService.addItem( itemDto );

        final Item item2 = new Item();
        item2.setName( "Latte" );
        item2.setDescription( "A yummy beverage" );
        item2.setPrice( 357 );

        final ItemIngredient ii2 = new ItemIngredient();
        ii2.setIngredient( new Ingredient( "Milk", null ) );
        ii2.setQuantity( 3 );
        ii2.setUnit( "cups" );
        ii2.setItem( item );
        item2.addIngredient( ii2 );

        itemService.addItem( ItemMapper.mapToDto( item2 ) );

        final List<ItemDto> retrievedItemDtos = itemService.getAllItems();
        assertAll( "retrieved itemdto 0 contents",
                () -> assertEquals( ITEM_NAME, retrievedItemDtos.get( 0 ).getName() ),
                () -> assertEquals( ITEM_DESCRIPTION, retrievedItemDtos.get( 0 ).getDescription() ),
                () -> assertEquals( ITEM_PRICE, retrievedItemDtos.get( 0 ).getPrice() ) );

        assertAll( "retrievied itemdto 1 contents", () -> assertEquals( "Latte", retrievedItemDtos.get( 1 ).getName() ),
                () -> assertEquals( "A yummy beverage", retrievedItemDtos.get( 1 ).getDescription() ),
                () -> assertEquals( 357, retrievedItemDtos.get( 1 ).getPrice() ) );

    }

}
