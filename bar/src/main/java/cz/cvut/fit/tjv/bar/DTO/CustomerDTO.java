package cz.cvut.fit.tjv.bar.DTO;

import lombok.Data;

@Data
public class CustomerDTO {
    private String firstName;
    private String lastName;
    private int orderCount;
}
