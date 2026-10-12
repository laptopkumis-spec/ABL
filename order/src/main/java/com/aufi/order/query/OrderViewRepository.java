package com.aufi.order.query;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository READ MODEL — hanya dipakai sisi Query untuk membaca (dan oleh
 * projector untuk menulis hasil proyeksi event).
 */
@Repository
public interface OrderViewRepository extends JpaRepository<OrderView, Long> {
}
