package cz.cvut.fit.tjv.bar.Repository;

import cz.cvut.fit.tjv.bar.Model.Customer;
import cz.cvut.fit.tjv.bar.Model.Item;
import cz.cvut.fit.tjv.bar.Model.Order;
import cz.cvut.fit.tjv.bar.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT oi.item FROM OrderItem oi " +
            "JOIN oi.order o " +
            "WHERE o.orderDate BETWEEN :startDate AND :endDate " +
            "GROUP BY oi.item " +
            "ORDER BY SUM(oi.quantity) DESC")
    List<Item> findMostPopularItemsBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT oi.item FROM OrderItem oi WHERE oi.order.user = :user " +
            "AND oi.order.orderDate BETWEEN :startDate AND :endDate " +
            "GROUP BY oi.item ORDER BY SUM(oi.quantity) DESC")
    List<Item> findMostPopularItemByUserBetweenDates(@Param("user") User user,
                                               @Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate);

    int countByCustomer(Customer customer);
}
