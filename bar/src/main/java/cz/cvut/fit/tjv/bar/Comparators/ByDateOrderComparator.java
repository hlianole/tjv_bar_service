package cz.cvut.fit.tjv.bar.Comparators;

import cz.cvut.fit.tjv.bar.Model.Order;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public class ByDateOrderComparator implements Comparator<Order> {

    @Override
    public int compare(@NotNull Order o1, @NotNull Order o2) {
        if (o1.getOrderDate() == null || o2.getOrderDate() == null) {
            return 0;
        }
        return o1.getOrderDate().compareTo(o2.getOrderDate());
    }

}
