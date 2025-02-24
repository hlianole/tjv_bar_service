package cz.cvut.fit.tjv.bar.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    private String role = "ROLE_USER";

    private boolean active = true;

    @Getter
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Access(AccessType.FIELD)
    private List<Order> orders = new ArrayList<>();

    public void addOrder(Order order) {
        orders.add(order);
    }

    public List<Order> getActiveOrders() {
        List<Order> activeOrders = new ArrayList<>();
        for (Order order : orders) {
            if (!order.isClosed())
                activeOrders.add(order);
        }
        return activeOrders;
    }

    public List<Order> getClosedOrders() {
        List<Order> closedOrders = new ArrayList<>();
        for (Order order : orders) {
            if (order.isClosed())
                closedOrders.add(order);
        }
        return closedOrders;
    }

}
