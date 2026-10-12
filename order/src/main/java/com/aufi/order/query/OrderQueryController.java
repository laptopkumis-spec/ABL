package com.aufi.order.query;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller sisi QUERY (read). Hanya GET. Membaca read model, bukan write
 * model.
 */
@RestController
@RequestMapping("/api/orders/queries")
public class OrderQueryController {

    private final OrderQueryService orderQueryService;

    public OrderQueryController(OrderQueryService orderQueryService) {
        this.orderQueryService = orderQueryService;
    }

    @GetMapping
    public List<OrderView> getAll() {
        return orderQueryService.getAll();
    }

    @GetMapping("/count")
    public long count() {
        return orderQueryService.count();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderView> getById(@PathVariable("id") Long id) {
        OrderView view = orderQueryService.getById(id);
        if (view == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(view);
    }
}
