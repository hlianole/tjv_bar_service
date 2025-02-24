package cz.cvut.fit.tjv.bar.Controller;

import cz.cvut.fit.tjv.bar.Comparators.ByDateOrderComparator;
import cz.cvut.fit.tjv.bar.Enums.ItemType;
import cz.cvut.fit.tjv.bar.Model.Item;
import cz.cvut.fit.tjv.bar.Model.Order;
import cz.cvut.fit.tjv.bar.Model.User;
import cz.cvut.fit.tjv.bar.Repository.ItemRepository;
import cz.cvut.fit.tjv.bar.Repository.OrderRepository;
import cz.cvut.fit.tjv.bar.Repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;

    @GetMapping("/active-orders")
    public String activeOrders(Principal principal, Model model) {
        String email = principal.getName();
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        List<Order> orders = user.get().getActiveOrders();
        orders.sort(new ByDateOrderComparator());

        model.addAttribute("orders", orders);

        return "active-orders";
    }

    @GetMapping("/create-order")
    public String showCreateOrderForm (Model model) {
        Map<ItemType, List<Item>> itemsByType = Arrays.stream(ItemType.values())
                .collect(Collectors.toMap(
                        itemType -> itemType,
                        itemRepository::findByType,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
       model.addAttribute("itemsByType", itemsByType);
        return "create-order";
    }

    @GetMapping("/manage-order/{id}")
    public String showManageOrderForm (@PathVariable Long id, HttpSession session, Model model) {
        Order temporaryOrder = (Order) session.getAttribute("temporaryOrder");
        if (temporaryOrder == null || !temporaryOrder.getId().equals(id)) {
            temporaryOrder = orderRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Order not found"));
            session.setAttribute("temporaryOrder", temporaryOrder);
        }
        model.addAttribute("order", temporaryOrder);
        return "manage-order";
    }

    @GetMapping("/add-items-to-order/{id}")
    public String showAddItemsToOrderForm (@PathVariable Long id, HttpSession session, Model model) {
        Order order = (Order) session.getAttribute("temporaryOrder");
        if (order == null || !order.getId().equals(id)) {
            order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        }
        Map<ItemType, List<Item>> itemsByType = Arrays.stream(ItemType.values())
                .collect(Collectors.toMap(
                        itemType -> itemType,
                        itemRepository::findByType,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
        model.addAttribute("order", order);
        model.addAttribute("itemsByType", itemsByType);
        model.addAttribute("itemsInOrder", order.getItemsInOrder());

        return "add-items";
    }

}
