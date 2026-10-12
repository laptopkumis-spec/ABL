package com.aufi.order.event;

import java.time.LocalDate;
import com.aufi.order.entity.Order;

/**
 * Event yang diterbitkan sisi Command (write side) dan dikonsumsi sisi Query
 * (read side) untuk memperbarui read model. Ini adalah "pesan" yang mengalir
 * lewat RabbitMQ.
 */
public class OrderEvent {

    public enum EventType {
        CREATED, UPDATED, DELETED
    }

    private EventType eventType;
    private Long orderId;
    private Long produkId;
    private Long pelangganId;
    private LocalDate tglTransaksi;
    private int jumlah;
    private double total;

    public OrderEvent() {
    }

    public OrderEvent(EventType eventType, Long orderId, Long produkId, Long pelangganId,
                      LocalDate tglTransaksi, int jumlah, double total) {
        this.eventType = eventType;
        this.orderId = orderId;
        this.produkId = produkId;
        this.pelangganId = pelangganId;
        this.tglTransaksi = tglTransaksi;
        this.jumlah = jumlah;
        this.total = total;
    }

    // ---- factory ----

    public static OrderEvent created(Order o) {
        return from(EventType.CREATED, o);
    }

    public static OrderEvent updated(Order o) {
        return from(EventType.UPDATED, o);
    }

    public static OrderEvent deleted(Long orderId) {
        return new OrderEvent(EventType.DELETED, orderId, null, null, null, 0, 0);
    }

    private static OrderEvent from(EventType type, Order o) {
        return new OrderEvent(type, o.getId(), o.getProdukId(), o.getPelangganId(),
                o.getTglTransaksi(), o.getJumlah(), o.getTotal());
    }

    // ---- getters / setters ----

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getProdukId() {
        return produkId;
    }

    public void setProdukId(Long produkId) {
        this.produkId = produkId;
    }

    public Long getPelangganId() {
        return pelangganId;
    }

    public void setPelangganId(Long pelangganId) {
        this.pelangganId = pelangganId;
    }

    public LocalDate getTglTransaksi() {
        return tglTransaksi;
    }

    public void setTglTransaksi(LocalDate tglTransaksi) {
        this.tglTransaksi = tglTransaksi;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "OrderEvent{" + eventType + ", orderId=" + orderId + ", total=" + total + "}";
    }
}
