package cz.cvut.fit.tjv.bar.Service;

import cz.cvut.fit.tjv.bar.DTO.CreateOrderDTO;
import cz.cvut.fit.tjv.bar.DTO.SetItemsDTO;
import cz.cvut.fit.tjv.bar.Model.*;
import cz.cvut.fit.tjv.bar.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerRepository customerRepository;

    public Order createOrder(CreateOrderDTO createOrderDTO, Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<Customer> customerFind = customerRepository
                .findByFirstNameAndLastName(createOrderDTO.getCustomerFirstName(),
                        createOrderDTO.getCustomerLastName());

        Customer customer;
        if (customerFind.isEmpty()) {
            customer = new Customer(createOrderDTO.getCustomerFirstName(), createOrderDTO.getCustomerLastName());
            customerRepository.save(customer);
        }
        else {
            customer = customerFind.get();
        }

        Order order = new Order();
        order.setUser(user);
        order.setPrice(0);
        order.setCustomer(customer);

        customer.getOrders().add(order);

        for (CreateOrderDTO.OrderItemDTO orderItemDTO : createOrderDTO.getOrderItems()) {
            Item item = itemRepository.findById(orderItemDTO.getItemId())
                    .orElseThrow(() -> new RuntimeException("Item not found"));

            if (orderItemDTO.getQuantity() > 0) {
                addNewOrderItem(order, item, orderItemDTO.getQuantity());
            }
        }

        System.out.println("Created new order. Customer name: "
                + order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName());

        return orderRepository.save(order);
    }

    public Order setOrderItems (Order order, SetItemsDTO setItemsDTO) {
        for (SetItemsDTO.OrderItemDTO orderItemDTO : setItemsDTO.getOrderItems()) {
            Item item = itemRepository.findById(orderItemDTO.getItemId())
                    .orElseThrow(() -> new RuntimeException("Item not found"));

            OrderItem orderItemInOrder = order.getOrderItemByItem(item);

            if (orderItemDTO.getQuantity() > 0 && orderItemInOrder == null) {
                OrderItem orderItem = new OrderItem();
                orderItem.setItem(item);
                orderItem.setQuantity(orderItemDTO.getQuantity());
                orderItem.setOrder(order);

                order.addOrderItem(orderItem);
                order.setPrice(order.getPrice() + orderItem.getTotalPrice());
            }
            else if (orderItemDTO.getQuantity() > 0) {
                order.setNewOrderItemQuantity(orderItemInOrder, orderItemDTO.getQuantity());
            }
            else if (orderItemDTO.getQuantity() == 0 && orderItemInOrder != null) {
                order.setPrice(order.getPrice() - orderItemInOrder.getTotalPrice());
                order.removeOrderItem(orderItemInOrder);
            }
        }
        return order;
    }

    public void editOrder(Long id, CreateOrderDTO createOrderDTO) {
        Order order = orderRepository.getReferenceById(id);

        if (!Objects.equals(createOrderDTO.getCustomerFirstName(), order.getCustomer().getFirstName())
            || !Objects.equals(createOrderDTO.getCustomerLastName(), order.getCustomer().getLastName())) {

            Customer oldCustomer = order.getCustomer();
            oldCustomer.removeOrder(order);
            order.setCustomer(null);

            Optional<Customer> newCustomer = customerRepository.findByFirstNameAndLastName(createOrderDTO.getCustomerFirstName(),
                    createOrderDTO.getCustomerLastName());

            Customer customer;
            if (newCustomer.isPresent()) {
                customer = newCustomer.get();
            }
            else {
                customer = new Customer(createOrderDTO.getCustomerFirstName(), createOrderDTO.getCustomerLastName());
                customerRepository.save(customer);
            }
            order.setCustomer(customer);
            customer.getOrders().add(order);

            if (oldCustomer.getOrders().isEmpty()) {
                customerRepository.delete(oldCustomer);
            }
        }

        Set<Item> itemsInDto = new HashSet<>();

        for (CreateOrderDTO.OrderItemDTO orderItemDTO : createOrderDTO.getOrderItems()) {
            Item item = itemRepository.findById(orderItemDTO.getItemId())
                    .orElseThrow(() -> new RuntimeException("Item not found"));

            itemsInDto.add(item);

            OrderItem orderItemInOrder = order.getOrderItemByItem(item);

            System.out.println("OrderItem to edit: " + item.getName() + " Q: " + orderItemDTO.getQuantity());
            if (orderItemInOrder != null) {
                System.out.println("Item in order: " + orderItemInOrder.getItem().getName() + " Q: " + orderItemInOrder.getQuantity());
            }

            if (orderItemDTO.getQuantity() > 0 && orderItemInOrder == null) {
                addNewOrderItem(order, item, orderItemDTO.getQuantity());
            }
            else if (orderItemDTO.getQuantity() > 0) {
                order.setPrice(order.getPrice() - orderItemInOrder.getTotalPrice());
                orderItemInOrder.setQuantity(orderItemDTO.getQuantity());
                orderItemRepository.save(orderItemInOrder);
                order.setPrice(order.getPrice() + orderItemInOrder.getTotalPrice());
            }
            else if (orderItemDTO.getQuantity() == 0 && orderItemInOrder != null) {
                order.setPrice(order.getPrice() - orderItemInOrder.getTotalPrice());
                order.removeOrderItem(orderItemInOrder);
                orderItemRepository.delete(orderItemInOrder);
                itemsInDto.remove(orderItemInOrder.getItem());
            }
        }

        if (order.getOrderItems().size() != itemsInDto.size()) {
            for (OrderItem orderItem : order.getOrderItems()) {
                if (!itemsInDto.contains(orderItem.getItem())) {
                    order.setPrice(order.getPrice() - orderItem.getTotalPrice());
                    order.removeOrderItem(orderItem);
                    orderItemRepository.delete(orderItem);
                }
            }
        }

        if (createOrderDTO.isClosed()) {
            order.setClosed(true);
        }

        orderRepository.save(order);
    }

    private void addNewOrderItem(Order order, Item item, int quantity) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setQuantity(quantity);
        orderItem.setOrder(order);

        orderItemRepository.save(orderItem);

        order.addOrderItem(orderItem);
        order.setPrice(order.getPrice() + orderItem.getTotalPrice());
    }

    public Order getMostExpensiveOrderByUser(User user) {
        Order order = null;
        for (Order o : user.getOrders()) {
            if (order == null || o.getPrice() > order.getPrice()) {
                order = o;
            }
        }
        return order;
    }

    public Item getMostPopularItem(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findMostPopularItemsBetweenDates(startDate, endDate)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No popular items found"));
    }

    public Item getMostPopularItemByUser(User user, LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findMostPopularItemByUserBetweenDates(user, startDate, endDate)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No popular items found"));
    }

}
