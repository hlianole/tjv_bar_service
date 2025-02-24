package cz.cvut.fit.tjv.bar.RestController;

import cz.cvut.fit.tjv.bar.DTO.CustomerDTO;
import cz.cvut.fit.tjv.bar.DTO.OrderDTO;
import cz.cvut.fit.tjv.bar.DTO.StatisticsItemDTO;
import cz.cvut.fit.tjv.bar.Model.Customer;
import cz.cvut.fit.tjv.bar.Model.Item;
import cz.cvut.fit.tjv.bar.Model.Order;
import cz.cvut.fit.tjv.bar.Model.User;
import cz.cvut.fit.tjv.bar.Repository.CustomerRepository;
import cz.cvut.fit.tjv.bar.Repository.OrderItemRepository;
import cz.cvut.fit.tjv.bar.Repository.OrderRepository;
import cz.cvut.fit.tjv.bar.Repository.UserRepository;
import cz.cvut.fit.tjv.bar.Service.OrderService;
import cz.cvut.fit.tjv.bar.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
@Tag(name = "Statistics controller", description = "CRUD operations to see statistics")
public class RestStatisticsController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerRepository customerRepository;

    @GetMapping("/user-orders-count")
    @Operation(summary = "Get number of user`s orders")
    public int getUserOrdersCount(Principal principal) {
        String email = principal.getName();
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        return userService.getUserOrdersCount(user.get());
    }

    @GetMapping("/user-closed-orders-count")
    @Operation(summary = "Get number of user`s closed orders")
    public int getUserClosedOrdersCount(Principal principal) {
        String email = principal.getName();
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        return userService.getUserClosedOrdersCount(user.get());
    }

    @GetMapping("/most-expensive-order")
    @Operation(summary = "Get user`s most expensive order")
    public OrderDTO getMostExpensiveOrder(Principal principal) {
        String email = principal.getName();
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        Order order = orderService.getMostExpensiveOrderByUser(user.get());

        if (order == null) {
            return null;
        }

        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setPrice(order.getPrice());
        dto.setOrderDate(order.getFormattedOrderDate());
        dto.setClosed(order.isClosed());
        dto.setCustomerFirstName(order.getCustomer().getFirstName());
        dto.setCustomerLastName(order.getCustomer().getLastName());
        return dto;
    }

    @GetMapping("/most-popular-item")
    @Operation(summary = "Get the most popular item ordered between startDate and endDate")
    public StatisticsItemDTO getMostPopularItem(@RequestParam String startDate,
                                                        @RequestParam String endDate) {
        try {
            Item item = orderService
                    .getMostPopularItem(LocalDateTime.parse(startDate), LocalDateTime.parse(endDate));

            StatisticsItemDTO dto = new StatisticsItemDTO();
            dto.setName(item.getName());
            dto.setPrice(item.getPrice());
            dto.setType(item.getType().name());
            dto.setQuantity(orderItemRepository.getTotalQuantityByItemId(item.getId()));

            return dto;
        }
        catch (NoSuchElementException e) {
            return null;
        }
    }

    @GetMapping("/user-most-popular-item")
    @Operation(summary = "Get the most popular item ordered from the actual user between startDate and endDate")
    public StatisticsItemDTO getMostPopularItemByUser(Principal principal,
                                         @RequestParam String startDate,
                                         @RequestParam String endDate) {
        String email = principal.getName();
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        try {
            Item item = orderService
                    .getMostPopularItemByUser(user.get(),
                            LocalDateTime.parse(startDate),
                            LocalDateTime.parse(endDate));

            StatisticsItemDTO dto = new StatisticsItemDTO();
            dto.setName(item.getName());
            dto.setPrice(item.getPrice());
            dto.setType(item.getType().name());
            dto.setQuantity(orderItemRepository.getUserIdTotalQuantityByItemId(item.getId(), user.get().getId()));

            return dto;
        }
        catch (NoSuchElementException e) {
            return null;
        }
    }

    @GetMapping("/best-customer")
    @Operation(summary = "Get the customer who did the most orders")
    public CustomerDTO getBestCustomer() {
        Optional<Customer> customer = customerRepository.findBestCustomerByOrderCount();

        CustomerDTO dto = new CustomerDTO();
        if (customer.isPresent()) {
            dto.setFirstName(customer.get().getFirstName());
            dto.setLastName(customer.get().getLastName());
            dto.setOrderCount(customer.get().getOrders().size());
            return dto;
        }
        return null;
    }

    @GetMapping("/best-user-customer")
    @Operation(summary = "Get the customer who did the most orders from the actual user")
    public CustomerDTO getBestUserCustomer(Principal principal) {
        String email = principal.getName();
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        Optional<Customer> customer = customerRepository.findBestCustomerByUserId(user.get().getId());

        if (customer.isEmpty()) {
            return null;
        }

        CustomerDTO dto = new CustomerDTO();
        dto.setFirstName(customer.get().getFirstName());
        dto.setLastName(customer.get().getLastName());
        dto.setOrderCount(orderRepository.countByCustomer(customer.get()));
        return dto;
    }

}
