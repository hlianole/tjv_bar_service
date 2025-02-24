package cz.cvut.fit.tjv.bar.Repository;

import cz.cvut.fit.tjv.bar.Enums.ItemType;
import cz.cvut.fit.tjv.bar.Model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByType(ItemType itemType);
}
