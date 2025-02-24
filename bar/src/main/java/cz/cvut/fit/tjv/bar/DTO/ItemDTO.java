package cz.cvut.fit.tjv.bar.DTO;

import cz.cvut.fit.tjv.bar.Enums.ItemType;
import lombok.Data;

@Data
public class ItemDTO {

    private String name;
    private Double price;
    private int quantity;
    private ItemType type;

}
