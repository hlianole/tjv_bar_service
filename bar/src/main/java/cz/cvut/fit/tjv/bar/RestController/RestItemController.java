package cz.cvut.fit.tjv.bar.RestController;

import cz.cvut.fit.tjv.bar.DTO.ItemDTO;
import cz.cvut.fit.tjv.bar.Model.Item;
import cz.cvut.fit.tjv.bar.Repository.ItemRepository;
import cz.cvut.fit.tjv.bar.Service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
@Tag(name = "Item controller", description = "CRUD fro menu items")
public class RestItemController {

    private final ItemRepository itemRepository;
    private final ItemService itemService;

    @PostMapping("/create")
    @Operation(summary = "Create method for item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "New menu item created")
    })
    public ResponseEntity<Void> createItem(@RequestBody ItemDTO itemDTO) {
        Item item = new Item();
        item.setName(itemDTO.getName());
        item.setPrice(itemDTO.getPrice());
        item.setType(itemDTO.getType());
        itemRepository.save(item);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/edit/{id}")
    @Operation(summary = "Method for editing single item by it`s id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item updated successfully"),
            @ApiResponse(responseCode = "404", description = "Item not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<Void> editItem(@PathVariable Long id, @RequestBody ItemDTO itemDTO) {
        Optional<Item> item = itemRepository.findById(id);

        if (item.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        item.get().setName(itemDTO.getName());
        item.get().setPrice(itemDTO.getPrice());
        item.get().setType(itemDTO.getType());
        itemRepository.save(item.get());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Method for deleting item by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Item not found"),
            @ApiResponse(responseCode = "500", description = "Iternal server error")
    })
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        System.out.println("Starting to delete item with id: " + id);
        Optional<Item> item = itemRepository.findById(id);
        if (item.isEmpty()) {
            System.out.println("Item not found, Controller response");
            return ResponseEntity.notFound().build();
        }
        try {
            itemService.deleteItem(item.get());
            System.out.println("Item deleted successfully, Controller response");
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            System.out.println("Error while deleting item with id: " + id + ", Controller response");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
//        itemService.deleteItem(item.get());
//        System.out.println("Item deleted successfully, Controller response");
//        return ResponseEntity.ok().build();
    }

}
