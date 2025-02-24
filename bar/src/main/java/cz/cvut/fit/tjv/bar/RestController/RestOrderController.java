package cz.cvut.fit.tjv.bar.RestController;

import cz.cvut.fit.tjv.bar.DTO.CreateOrderDTO;
import cz.cvut.fit.tjv.bar.DTO.OrderDTO;
import cz.cvut.fit.tjv.bar.DTO.SetItemsDTO;
import cz.cvut.fit.tjv.bar.Model.*;
import cz.cvut.fit.tjv.bar.Repository.*;
import cz.cvut.fit.tjv.bar.Service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Order controller", description = "CRUD operations for orders")
public class RestOrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;

    @PostMapping("/create")
    @Operation(summary = "Create method for order, which connects actual user with new order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order created successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<OrderDTO> createOrder(@RequestBody CreateOrderDTO createOrderDTO, Principal principal) {
        try {
            Order order = orderService.createOrder(createOrderDTO, principal);

            OrderDTO orderDTO = new OrderDTO();
            orderDTO.setId(order.getId());
            orderDTO.setOrderDate(order.getFormattedOrderDate());
            orderDTO.setPrice(order.getPrice());
            orderDTO.setClosed(order.isClosed());

            orderDTO.setCustomerFirstName(order.getCustomer().getFirstName());
            orderDTO.setCustomerLastName(order.getCustomer().getLastName());
            return ResponseEntity.ok(orderDTO);
        }
        catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete order method")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order deleted"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<Void> deleteOrder(@PathVariable("id") Long id, HttpSession session) {
        Optional<Order> order = orderRepository.findById(id);

        if (order.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Customer customer = order.get().getCustomer();
        customer.removeOrder(order.get());

        orderRepository.deleteById(id);
        orderRepository.flush();

        if (customer.getOrders().isEmpty()) {
            customerRepository.delete(customer);
        }
        session.removeAttribute("temporaryOrder");
        System.out.println("Order with id " + id + " deleted");
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/manage/{id}")
    @Operation(summary = "Method for editing single order by it`s id. " +
            "Communicates with setTemporaryItems() with session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order edited"),
            @ApiResponse(responseCode = "404", description = "Item in DTO not found")
    })
    public ResponseEntity<Void> editOrder(@PathVariable Long id,
                                           @RequestBody CreateOrderDTO createOrderDTO,
                                           HttpSession session) {
        try {
            orderService.editOrder(id, createOrderDTO);
            session.removeAttribute("temporaryOrder");
            return ResponseEntity.ok().build();
        }
        catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/set-temporary-items/{id}")
    @Transactional
    @Operation(summary = "Method to set new items to order by it`s id. " +
            "New items are temporary at this moment. " +
            "Communicates with editOrder() with session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "New items set to temporary order in session"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<Void> setTemporaryItems (@PathVariable Long id, @RequestBody SetItemsDTO setItemsDTO, HttpSession session) {
        Order order = (Order) session.getAttribute("temporaryOrder");
        if (order == null || !order.getId().equals(id)) {
            Optional<Order> foundOrder = orderRepository.findById(id);
            if (foundOrder.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            order = foundOrder.get();
        }
        try {
            Order changedOrder = orderService.setOrderItems(order, setItemsDTO);
            session.removeAttribute("temporaryOrder");
            session.setAttribute("temporaryOrder", changedOrder);

            return ResponseEntity.ok().build();
        }
        catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
