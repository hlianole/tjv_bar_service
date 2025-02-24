package cz.cvut.fit.tjv.bar.Repository;

import cz.cvut.fit.tjv.bar.Model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("SELECT COALESCE(SUM(oi.quantity), 0) FROM OrderItem oi WHERE oi.item.id = :itemId")
    int getTotalQuantityByItemId(@Param("itemId") Long itemId);

    @Query("SELECT COALESCE(SUM(oi.quantity), 0) FROM OrderItem oi " +
            "WHERE oi.item.id = :itemId AND oi.order.user.id = :userId")
    int getUserIdTotalQuantityByItemId(@Param("itemId") Long itemId, @Param("userId") Long id);

    List<OrderItem> findAllByItemId(Long itemId);

}
