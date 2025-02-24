package cz.cvut.fit.tjv.bar.Service;

import cz.cvut.fit.tjv.bar.BarApplication;
import cz.cvut.fit.tjv.bar.Config.SecurityTestConfig;
import cz.cvut.fit.tjv.bar.Model.*;
import cz.cvut.fit.tjv.bar.Repository.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityTestConfig.class)
@ContextConfiguration(classes = {BarApplication.class})
@TestPropertySource(locations = "classpath:application-test.properties")
public class OrderServiceEndToEndTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        customerRepository.deleteAll();
        itemRepository.deleteAll();

        User user = new User();
        user.setEmail("user@example.com");
        user.setName("user");
        user.setPassword("password");
        userRepository.save(user);

        Item item = new Item();
        item.setPrice(10.0);
        item.setName("item");
        itemRepository.save(item);
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    @Transactional
    void testCreateOrder() throws Exception {
        String orderJson = """
                {
                    "customerFirstName": "John",
                    "customerLastName": "Doe",
                    "orderItems": [
                        {
                            "itemId": 1,
                            "quantity": 2
                        }
                    ]
                }""";

        mockMvc.perform(post("/api/v1/orders/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.customerFirstName").value("John"))
                .andExpect(jsonPath("$.customerLastName").value("Doe"))
                .andExpect(jsonPath("$.price").value(20.0));

        Customer customer = customerRepository.findByFirstNameAndLastName("John", "Doe")
                .orElseThrow();

        assertNotNull(customer);
        assertFalse(customer.getOrders().isEmpty());
        assertEquals(1, customer.getOrders().size());

        Order savedOrder = customer.getOrders().iterator().next();
        assertEquals(20.0, savedOrder.getPrice());
        assertEquals(1, savedOrder.getOrderItems().size());
    }
}