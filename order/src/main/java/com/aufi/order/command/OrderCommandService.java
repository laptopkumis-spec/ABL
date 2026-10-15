package com.aufi.order.command;

import java.util.List;
import com.aufi.order.command.dto.CreateOrderCommand;
import com.aufi.order.command.dto.UpdateOrderCommand;
import com.aufi.order.dto.PelangganDto;
import com.aufi.order.dto.ProdukDto;
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
 *
 * Sebelum publish, event DIPERKAYA (Opsi B) dengan data produk & pelanggan yang
 * diambil via Eureka, sehingga konsumen (read model & emailservice) menerima
 * data lengkap.
 */
@Service
public class OrderCommandService {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;
    private final OrderDataEnricher enricher;

    public OrderCommandService(OrderRepository orderRepository, OrderEventPublisher eventPublisher,
                               OrderDataEnricher enricher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
        this.enricher = enricher;
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
        eventPublisher.publish(enrich(OrderEvent.created(saved)));
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
        eventPublisher.publish(enrich(OrderEvent.updated(saved)));
        return saved;
    }

    @Transactional
    public void delete(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order tidak ditemukan: " + id));
        orderRepository.deleteById(id);
        eventPublisher.publish(enrich(OrderEvent.deleted(order)));
    }

    /** Dipakai oleh endpoint lama (backward-compatible) yang menerima entity Order. */
    @Transactional
    public Order save(Order order) {
        boolean isNew = order.getId() == 0 || !orderRepository.existsById(order.getId());
        Order saved = orderRepository.save(order);
        eventPublisher.publish(enrich(isNew ? OrderEvent.created(saved) : OrderEvent.updated(saved)));
        return saved;
    }

    /** Mengisi data terdenormalisasi (produk & pelanggan) ke event via Eureka. */
    private OrderEvent enrich(OrderEvent event) {
        if (event.getProdukId() != null) {
            ProdukDto produk = enricher.fetchProduk(event.getProdukId());
            if (produk != null) {
                event.setNamaProduk(produk.getNama());
                event.setHargaProduk(produk.getHarga());
            }
        }
        if (event.getPelangganId() != null) {
            PelangganDto pelanggan = enricher.fetchPelanggan(event.getPelangganId());
            if (pelanggan != null) {
                event.setNamaPelanggan(pelanggan.getNama());
                event.setEmailPelanggan(pelanggan.getEmail());
            }
        }
        return event;
    }

    public List<Order> findAllWriteModel() {
        return orderRepository.findAll();
    }
}
