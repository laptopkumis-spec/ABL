package com.aufi.order.query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * READ MODEL (sisi Query). Tabel terpisah dari write model `orders`, berisi data
 * yang sudah didenormalisasi/dipersiapkan untuk dibaca cepat. Diperbarui HANYA
 * oleh projector dari event RabbitMQ.
 */
@Entity
@Table(name = "order_view")
public class OrderView {

    @Id
    private Long id;

    private Long produkId;
    private Long pelangganId;
    private LocalDate tglTransaksi;
    private int jumlah;
    private double total;

    /** CREATED / UPDATED sebagai penanda versi terakhir read model. */
    private String status;

    private LocalDateTime lastUpdated;

    public OrderView() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
