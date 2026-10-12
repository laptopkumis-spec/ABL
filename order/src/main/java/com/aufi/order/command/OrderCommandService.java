package com.aufi.order.command;

import java.util.List;
import com.aufi.order.command.dto.CreateOrderCommand;
import com.aufi.order.command.dto.UpdateOrderCommand;
import com.aufi.order.entity.Order;
import com.aufi.order.event.OrderEvent;
import com.aufi.order.event.OrderEventPublisher;
import com.aufi.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Sisi COMMAND (write). Hanya operasi yang MENGUBAH state: create, update,
 * delete. Setiap perubahan menulis ke WRITE MODEL (tabel `orders`) lalu
 * mem-publish event ke RabbitMQ. Sisi ini tidak melayani query pembacaan.
 */
@Service
public class OrderCommandService {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;

    public OrderCommandService(OrderRepository orderRepository, OrderEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Order create(CreateOrderCommand cmd) {
        Order order = new Order();
        order.setProdukId(cmd.getProdukId());
        order.setPelangganId(cmd.getPelangganId());
        order.setTglTransaksi(cmd.getTglTransaksi());
        order.setJumlah(cmd.getJumlah());
        order.setTotal(cmd.getTotal());
        Order saved = orderRepository.save(order);
        eventPublisher.publish(OrderEvent.created(saved));
        return saved;
    }

    @Transactional
    public Order update(Long id, UpdateOrderCommand cmd) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order tidak ditemukan: " + id));
        order.setProdukId(cmd.getProdukId());
        order.setPelangganId(cmd.getPelangganId());
        order.setTglTransaksi(cmd.getTglTransaksi());
        order.setJumlah(cmd.getJumlah());
        order.setTotal(cmd.getTotal());
        Order saved = orderRepository.save(order);
        eventPublisher.publish(OrderEvent.updated(saved));
        return saved;
    }

    @Transactional
    public void delete(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("Order tidak ditemukan: " + id);
        }
        orderRepository.deleteById(id);
        eventPublisher.publish(OrderEvent.deleted(id));
    }

    /** Dipakai oleh endpoint lama (backward-compatible) yang menerima entity Order. */
    @Transactional
    public Order save(Order order) {
        Order saved = orderRepository.save(order);
        eventPublisher.publish(OrderEvent.created(saved));
        return saved;
    }

    public List<Order> findAllWriteModel() {
        return orderRepository.findAll();
    }
}
