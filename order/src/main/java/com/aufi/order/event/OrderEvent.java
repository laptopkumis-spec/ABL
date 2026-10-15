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

    // --- data terdenormalisasi (diisi command side sebelum publish) ---
    private String namaProduk;
    private double hargaProduk;
    private String namaPelanggan;
    private String emailPelanggan;

    public OrderEvent() {
    }

    // ---- factory ----

    public static OrderEvent created(Order o) {
        return from(EventType.CREATED, o);
    }

    public static OrderEvent updated(Order o) {
        return from(EventType.UPDATED, o);
    }

    public static OrderEvent deleted(Order o) {
        return from(EventType.DELETED, o);
    }

    private static OrderEvent from(EventType type, Order o) {
        OrderEvent e = new OrderEvent();
        e.eventType = type;
        e.orderId = o.getId();
        e.produkId = o.getProdukId();
        e.pelangganId = o.getPelangganId();
        e.tglTransaksi = o.getTglTransaksi();
        e.jumlah = o.getJumlah();
        e.total = o.getTotal();
        return e;
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

    public String getNamaProduk() {
        return namaProduk;
    }

    public void setNamaProduk(String namaProduk) {
        this.namaProduk = namaProduk;
    }

    public double getHargaProduk() {
        return hargaProduk;
    }

    public void setHargaProduk(double hargaProduk) {
        this.hargaProduk = hargaProduk;
    }

    public String getNamaPelanggan() {
        return namaPelanggan;
    }

    public void setNamaPelanggan(String namaPelanggan) {
        this.namaPelanggan = namaPelanggan;
    }

    public String getEmailPelanggan() {
        return emailPelanggan;
    }

    public void setEmailPelanggan(String emailPelanggan) {
        this.emailPelanggan = emailPelanggan;
    }

    @Override
    public String toString() {
        return "OrderEvent{" + eventType + ", orderId=" + orderId + ", total=" + total + "}";
    }
}
