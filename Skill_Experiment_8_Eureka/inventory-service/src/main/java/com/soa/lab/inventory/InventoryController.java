package com.soa.lab.inventory;

import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    @Value("${server.port}")
    private String port;

    @GetMapping
    public Map<String, Object> getInventory() {
        return Map.of(
            "service", "inventory-service",
            "instance", System.getenv().getOrDefault("INSTANCE_ID", "inventory-" + port),
            "port", port,
            "message", "Inventory service is running",
            "items", new String[]{"Laptop", "Mobile", "Headphones"},
            "timestamp", LocalDateTime.now().toString()
        );
    }

    @GetMapping("/{id}")
    public Map<String, Object> getItem(@PathVariable int id) {
        return Map.of("itemId", id, "itemName", "Sample item " + id,
                      "servedByPort", port, "status", "AVAILABLE");
    }
}
