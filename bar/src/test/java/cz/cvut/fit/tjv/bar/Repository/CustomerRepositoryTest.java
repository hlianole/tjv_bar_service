package cz.cvut.fit.tjv.bar.Repository;

import cz.cvut.fit.tjv.bar.Model.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(locations = "classpath:application-test.properties")
public class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void testFindByFirstNameAndLastName() {
        Customer customer = new Customer();
        customer.setFirstName("John");
        customer.setLastName("Doe");

        customerRepository.save(customer);

        assertTrue(customerRepository.findByFirstNameAndLastName("John", "Doe").isPresent());
        assertEquals(customerRepository.findByFirstNameAndLastName("John", "Doe").get().getFirstName(), "John");
        assertEquals(customerRepository.findByFirstNameAndLastName("John", "Doe").get().getLastName(), "Doe");
    }
}
