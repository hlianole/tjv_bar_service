package cz.cvut.fit.tjv.bar.Service;

import cz.cvut.fit.tjv.bar.Model.Customer;
import cz.cvut.fit.tjv.bar.Model.Item;
import cz.cvut.fit.tjv.bar.Model.Order;
import cz.cvut.fit.tjv.bar.Model.OrderItem;
import cz.cvut.fit.tjv.bar.Repository.CustomerRepository;
import cz.cvut.fit.tjv.bar.Repository.ItemRepository;
import cz.cvut.fit.tjv.bar.Repository.OrderItemRepository;
import cz.cvut.fit.tjv.bar.Repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final OrderItemRepository orderItemRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public void deleteItem(Item item) {
        List<OrderItem> orderItems = orderItemRepository.findAllByItemId(item.getId());
        System.out.println("Order items found in quantity of " + orderItems.size());

        for (OrderItem orderItem : orderItems) {
            Order order = orderItem.getOrder();
            order.removeOrderItem(orderItem);
            orderRepository.save(order);
            System.out.println("Order Item " + orderItem.getId() + " removed from order " + order.getId());

            if (order.getOrderItems().isEmpty()) {
                Optional<Customer> customer = customerRepository.findById(order.getCustomer().getId());
                if (customer.isEmpty()) {
                    System.out.println("Customer not found");
                    throw new RuntimeException("Customer not found");
                }
                customer.get().removeOrder(order);
                customerRepository.save(customer.get());
                System.out.println("Order with id " + order.getId() + " removed from customer " + customer.get().getId());

                if (customer.get().getOrders().isEmpty()) {
                    customerRepository.delete(customer.get());
                    System.out.println("Customer " + customer.get().getFirstName() + " deleted");
                }
                orderRepository.delete(order);
                System.out.println("Order with id " + order.getId() + " deleted");
            }

            orderItemRepository.delete(orderItem);
            System.out.println("OrderItem with id " + orderItem.getId() + " deleted");
        }

        itemRepository.delete(item);
        System.out.println("Item " + item.getId() + " deleted");
    }

}
