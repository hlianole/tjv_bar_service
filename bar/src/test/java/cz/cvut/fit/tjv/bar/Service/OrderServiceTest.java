package cz.cvut.fit.tjv.bar.Service;

import cz.cvut.fit.tjv.bar.DTO.CreateOrderDTO;
import cz.cvut.fit.tjv.bar.Model.*;
import cz.cvut.fit.tjv.bar.Repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private Principal principal;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Test
    public void testCreateOrder() {
        CreateOrderDTO createOrderDTO = new CreateOrderDTO();
        createOrderDTO.setCustomerFirstName("John");
        createOrderDTO.setCustomerLastName("Doe");

        CreateOrderDTO.OrderItemDTO orderItemDTO = new CreateOrderDTO.OrderItemDTO();
        orderItemDTO.setItemId(1L);
        orderItemDTO.setQuantity(2);
        createOrderDTO.setOrderItems(Collections.singletonList(orderItemDTO));

        User user = new User();
        user.setEmail("user@example.com");

        Item item = new Item();
        item.setId(1L);
        item.setPrice(10.0);

        Customer customer = new Customer("John", "Doe");

        Order order = new Order();
        order.setCustomer(customer);
        order.setUser(user);

        when(principal.getName()).thenReturn("user@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(customerRepository.findByFirstNameAndLastName(anyString(), anyString())).thenReturn(Optional.of(customer));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order createdOrder = orderService.createOrder(createOrderDTO, principal);

        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
        assertNotNull(createdOrder);
        assertEquals(1, createdOrder.getOrderItems().size());
        assertEquals(20.0, createdOrder.getPrice(), 0.01);
    }

}
