package cz.cvut.fit.tjv.bar.Controller;

import cz.cvut.fit.tjv.bar.Enums.ItemType;
import cz.cvut.fit.tjv.bar.Model.Item;
import cz.cvut.fit.tjv.bar.Repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemRepository itemRepository;

    @GetMapping("/create-item")
    public String showItemCreationForm(Model model) {
        model.addAttribute("itemTypes", ItemType.values());
        return "create-item";
    }

    @GetMapping("/manage-items")
    public String showItems(Model model) {
        Map<ItemType, List<Item>> itemsByType = Arrays.stream(ItemType.values())
                .collect(Collectors.toMap(
                        itemType -> itemType,
                        itemRepository::findByType,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
        model.addAttribute("itemsByType", itemsByType);
        return "manage-items";
    }

    @GetMapping("/edit-item/{id}")
    public String showItemEditForm(@PathVariable Long id, Model model) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        model.addAttribute("item", item);
        model.addAttribute("itemTypes", ItemType.values());
        return "edit-item";
    }
}
