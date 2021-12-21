package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;

    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController,"itemRepository", itemRepository);

        Item item = new Item();

        item.setId(0L);
        item.setName("pen");
        item.setDescription("Used for writing");
        item.setPrice(BigDecimal.valueOf(5));
        List<Item> items = new ArrayList<>();
        items.add(item);

        when(itemRepository.findById(0L)).thenReturn(java.util.Optional.of(item));
        when(itemRepository.findAll()).thenReturn(items);
        when(itemRepository.findByName(item.getName())).thenReturn(items);


    }

    @Test
    public void get_all_items()
    {
        final ResponseEntity<List<Item>> response = itemController.getItems();

        assertEquals(200,response.getStatusCodeValue());
        assertEquals(1,response.getBody().size());


    }

    @Test
    public void find_item_by_id()
    {
        Item request = new Item();
        request.setId(0L);
        request.setName("pen");
        request.setDescription("Used for writing");
        request.setPrice(BigDecimal.valueOf(5));
        final ResponseEntity<Item> response = itemController.getItemById(request.getId());

        assertEquals(200,response.getStatusCodeValue());
        assertEquals(request.getId(),response.getBody().getId());

    }

    @Test
    public void get_items_by_name()
    {
        Item request = new Item();
        request.setId(0L);
        request.setName("pen");
        request.setDescription("Used for writing");
        request.setPrice(BigDecimal.valueOf(5));
        final ResponseEntity<List<Item>> response = itemController.getItemsByName(request.getName());

        assertEquals(200,response.getStatusCodeValue());
        assertEquals(1,response.getBody().size());

    }

    @Test
    public void get_items_not_found()
    {
        Item request = new Item();
        request.setId(0L);
        request.setName("pencil");
        request.setDescription("Used for writing");
        request.setPrice(BigDecimal.valueOf(5));
        final ResponseEntity<List<Item>> response = itemController.getItemsByName(request.getName());

        assertEquals(404,response.getStatusCodeValue());
//        assertEquals(null,response.getBody().size());
    }

}
