package cz.cvut.fit.tjv.bar.DTO;

import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
public class SetItemsDTO {
    private List<OrderItemDTO> orderItems;

    @Getter
    public static class OrderItemDTO {
        private Long itemId;
        private int quantity;
    }
}
