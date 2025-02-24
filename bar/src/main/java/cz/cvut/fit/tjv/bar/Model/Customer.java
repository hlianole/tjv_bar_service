package cz.cvut.fit.tjv.bar.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();

    public Customer(String customerFirstName, String customerLastName) {
        this.firstName = customerFirstName;
        this.lastName = customerLastName;
    }

    public void removeOrder(Order order) {
        orders.remove(order);
    }
}
