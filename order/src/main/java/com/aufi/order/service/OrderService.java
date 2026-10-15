package com.aufi.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.aufi.order.VO.Produk;
import com.aufi.order.VO.ResponseTemplate;
import com.aufi.order.entity.Order;
import com.aufi.order.event.OrderEvent;
import com.aufi.order.event.OrderEventPublisher;
import com.aufi.order.repository.OrderRepository;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private OrderEventPublisher eventPublisher;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public Order saveOrder(Order order) {
        boolean isNew = order.getId() == 0 || !orderRepository.existsById(order.getId());
        Order saved = orderRepository.save(order);
        eventPublisher.publish(isNew ? OrderEvent.created(saved) : OrderEvent.updated(saved));
        return saved;
    }

    public void deleteOrder(long id) {
        Order order = orderRepository.findById(id).orElse(null);
        orderRepository.deleteById(id);
        if (order != null) {
            eventPublisher.publish(OrderEvent.deleted(order));
        }
    }

    public List<ResponseTemplate> getOrderWithProdukById(Long id) {
        List<ResponseTemplate> responseTemplates = new ArrayList<>();
        Order order = getOrderById(id);
        ServiceInstance serviceInstance = discoveryClient.getInstances("produk-service").get(0);
        Produk produk = restTemplate.getForObject(serviceInstance.getUri()
                + "/api/produk/"
                + order.getProdukId(), Produk.class);
        ResponseTemplate vo = new ResponseTemplate();
        vo.setOrder(order);
        vo.setProduk(produk);
        responseTemplates.add(vo);
        return responseTemplates;
    }

}