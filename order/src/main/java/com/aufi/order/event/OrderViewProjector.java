package com.aufi.order.event;

import java.time.LocalDateTime;
import com.aufi.order.query.OrderView;
import com.aufi.order.query.OrderViewRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Projector: menerapkan event ke READ MODEL (tabel order_view). Ini satu-satunya
 * tempat yang menulis ke read model.
 */
@Component
public class OrderViewProjector {

    private final OrderViewRepository orderViewRepository;

    public OrderViewProjector(OrderViewRepository orderViewRepository) {
        this.orderViewRepository = orderViewRepository;
    }

    @Transactional
    public void apply(OrderEvent event) {
        switch (event.getEventType()) {
            case CREATED, UPDATED -> orderViewRepository.save(toView(event));
            case DELETED -> orderViewRepository.deleteById(event.getOrderId());
        }
    }

    private OrderView toView(OrderEvent e) {
        OrderView v = new OrderView();
        v.setId(e.getOrderId());
        v.setProdukId(e.getProdukId());
        v.setPelangganId(e.getPelangganId());
        v.setTglTransaksi(e.getTglTransaksi());
        v.setJumlah(e.getJumlah());
        v.setTotal(e.getTotal());
        v.setStatus(e.getEventType().name());
        v.setLastUpdated(LocalDateTime.now());
        return v;
    }
}
