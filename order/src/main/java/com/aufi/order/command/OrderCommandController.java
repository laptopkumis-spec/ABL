package com.aufi.order.command;

import java.util.List;
import com.aufi.order.command.dto.CreateOrderCommand;
import com.aufi.order.command.dto.UpdateOrderCommand;
import com.aufi.order.entity.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller sisi COMMAND (write). Menangani operasi yang mengubah state:
 * POST (create), PUT (update), DELETE. Setelah sukses, perubahan diproyeksikan
 * ke read model lewat event RabbitMQ.
 */
@RestController
@RequestMapping("/api/orders/commands")
public class OrderCommandController {

    private final OrderCommandService orderCommandService;

    public OrderCommandController(OrderCommandService orderCommandService) {
        this.orderCommandService = orderCommandService;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateOrderCommand cmd) {
        try {
            return ResponseEntity.ok(orderCommandService.create(cmd));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @RequestBody UpdateOrderCommand cmd) {
        try {
            return ResponseEntity.ok(orderCommandService.update(id, cmd));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        try {
            orderCommandService.delete(id);
            return ResponseEntity.ok("Order " + id + " dihapus");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /** Cek isi write model (untuk keperluan demo/verifikasi). */
    @GetMapping("/write-model")
    public List<Order> writeModel() {
        return orderCommandService.findAllWriteModel();
    }
}
