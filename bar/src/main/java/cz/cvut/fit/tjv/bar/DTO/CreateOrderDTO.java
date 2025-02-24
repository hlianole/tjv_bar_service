package cz.cvut.fit.tjv.bar.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class CreateOrderDTO {

    private String customerFirstName;
    private String customerLastName;
    private boolean closed = false;
    private List<OrderItemDTO> orderItems;

    @Getter
    @Setter
    public static class OrderItemDTO {
        private Long itemId;
        private int quantity;
    }

}
