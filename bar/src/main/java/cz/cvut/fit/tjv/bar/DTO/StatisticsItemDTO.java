package cz.cvut.fit.tjv.bar.DTO;

import lombok.Data;

@Data
public class StatisticsItemDTO {

    private String name;
    private Double price;
    private int quantity;
    private String type;

}
