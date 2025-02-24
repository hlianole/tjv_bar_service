package cz.cvut.fit.tjv.bar.Repository;

import cz.cvut.fit.tjv.bar.Model.Item;
import cz.cvut.fit.tjv.bar.Model.Order;
import cz.cvut.fit.tjv.bar.Model.OrderItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(locations = "classpath:application-test.properties")
public class OrderItemRepositoryTest {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void testGetTotalQuantityByItemId() {
        Item item = new Item();
        item.setName("Test Item");
        item.setPrice(10.0);
        itemRepository.save(item);

        Order order = new Order();
        orderRepository.save(order);

        OrderItem orderItem1 = new OrderItem();
        orderItem1.setQuantity(2);
        orderItem1.setItem(item);
        orderItem1.setOrder(order);

        OrderItem orderItem2 = new OrderItem();
        orderItem2.setQuantity(3);
        orderItem2.setItem(item);
        orderItem2.setOrder(order);

        orderItemRepository.save(orderItem1);
        orderItemRepository.save(orderItem2);

        assertEquals(5, orderItemRepository.getTotalQuantityByItemId(1L));
    }
}