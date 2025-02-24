package cz.cvut.fit.tjv.bar.Repository;

import cz.cvut.fit.tjv.bar.Model.Customer;
import cz.cvut.fit.tjv.bar.Model.Order;
import cz.cvut.fit.tjv.bar.Model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(locations = "classpath:application-test.properties")
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void countByCustomerTest() {
        Order order1 = new Order();
        Order order2 = new Order();

        User user = new User();
        user.setEmail("user@example.com");
        user.setName("user");
        user.setPassword("password123");
        userRepository.save(user);

        Customer customer = new Customer();
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customerRepository.save(customer);


        order1.setCustomer(customer);
        order2.setCustomer(customer);

        order1.setUser(user);
        order2.setUser(user);

        orderRepository.save(order1);
        orderRepository.save(order2);

        assertEquals(2, orderRepository.countByCustomer(customer));
    }

}
