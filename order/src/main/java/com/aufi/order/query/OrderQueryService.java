package com.aufi.order.query;

import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Sisi QUERY: HANYA operasi baca. Tidak boleh mengubah write model maupun
 * mem-publish event. Membaca dari read model (tabel order_view).
 */
@Service
public class OrderQueryService {

    private final OrderViewRepository orderViewRepository;

    public OrderQueryService(OrderViewRepository orderViewRepository) {
        this.orderViewRepository = orderViewRepository;
    }

    public List<OrderView> getAll() {
        return orderViewRepository.findAll();
    }

    public OrderView getById(Long id) {
        return orderViewRepository.findById(id).orElse(null);
    }

    public long count() {
        return orderViewRepository.count();
    }
}
