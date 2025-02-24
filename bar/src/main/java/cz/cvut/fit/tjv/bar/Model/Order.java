package cz.cvut.fit.tjv.bar.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "orders")
@NoArgsConstructor
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime orderDate = LocalDateTime.now();
    private double price;
    private boolean closed = false;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonProperty("customer")
    private Customer customer;

    public String getFormattedOrderDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd , HH:mm");
        return orderDate.format(formatter);
    }

    public Order(double price, User user) {
        this.price = price;
        this.user = user;
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
    }

    public void removeOrderItem(OrderItem orderItem) { orderItems.remove(orderItem); }

    public Map<Long, Integer> getItemsInOrder() {
        Map<Long, Integer> items = new HashMap<>();
        for (OrderItem orderItem : orderItems) {
            items.put(orderItem.getItem().getId(), orderItem.getQuantity());
        }
        return items;
    }

    public List<Item> getItemsInOrderInList() {
        List<Item> items = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            items.add(orderItem.getItem());
        }
        return items;
    }

    public OrderItem getOrderItemByItem(Item item) {
        for (OrderItem orderItem : orderItems) {
            if (orderItem.getItem().getId().equals(item.getId())) {
                return orderItem;
            }
        }
        return null;
    }

    public void setNewOrderItemQuantity(OrderItem orderItem, int quantity) {
        for (OrderItem orderItem1 : orderItems) {
            if (orderItem1.getItem().equals(orderItem.getItem())) {
                this.price -= orderItem.getTotalPrice();
                orderItem1.setQuantity(quantity);
                this.price += orderItem.getTotalPrice();
            }
        }
    }
}
