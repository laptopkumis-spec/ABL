package com.aufi.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.aufi.order.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
