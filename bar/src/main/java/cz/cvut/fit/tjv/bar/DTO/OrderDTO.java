package cz.cvut.fit.tjv.bar.DTO;

import lombok.Data;

import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private String orderDate;
    private double price;
    private boolean closed;

    private String customerFirstName;
    private String customerLastName;
}
