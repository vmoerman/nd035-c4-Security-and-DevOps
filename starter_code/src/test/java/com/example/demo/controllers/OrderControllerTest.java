package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.hibernate.criterion.Order;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;

    private UserRepository userRepository = mock(UserRepository.class);

    private OrderRepository orderRepository = mock(OrderRepository.class);

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
        TestUtils.injectObjects(orderController, "userRepository", userRepository);

        User user = new User();
        Cart cart = new Cart();
        Item item = new Item();
        user.setId(0);
        user.setUsername("test");
        user.setPassword("testPassword");

        item.setId(0L);
        item.setName("pen");
        item.setDescription("Used for writing");
        item.setPrice(BigDecimal.valueOf(5));

        cart.setId(0L);
        cart.setUser(user);
        cart.addItem(item);
        user.setCart(cart);

        UserOrder order = UserOrder.createFromCart(user.getCart());
        List<UserOrder> userOrderList = new ArrayList<>();
        userOrderList.add(order);

        when(userRepository.findByUsername("test")).thenReturn(user);
        when(userRepository.findByUsername("someone")).thenReturn(null);
        when(orderRepository.findByUser(user)).thenReturn(userOrderList);
    }

    @Test
    public void submit_order_happy_flow()
    {
        final ResponseEntity<UserOrder> response = orderController.submit("test");

        assertEquals(200,response.getStatusCodeValue());
        assertEquals("test", response.getBody().getUser().getUsername());
        assertEquals(BigDecimal.valueOf(5), response.getBody().getTotal());
        assertEquals(1, response.getBody().getItems().size());
    }

    @Test
    public void submit_order_user_not_found()
    {
        final ResponseEntity<UserOrder> response = orderController.submit("someone");

        assertEquals(404,response.getStatusCodeValue());
    }

    @Test
    public void get_orders_for_user_happy_flow()
    {
        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("test");
        assertEquals(200,response.getStatusCodeValue());
        assertEquals("test", response.getBody().get(0).getUser().getUsername());
        assertEquals("pen", response.getBody().get(0).getItems().get(0).getName());
    }

    @Test
    public void get_orders_user_not_found()
    {
        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("someone");
        assertEquals(404,response.getStatusCodeValue());
    }
}
