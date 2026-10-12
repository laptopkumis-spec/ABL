package com.aufi.order.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "produk_id", nullable = false)
    private long produkId;

    @Column(name = "pelanggan_id", nullable = false)
    private long pelangganId;

    @Column(name = "tgl_transaksi")
    private LocalDate tglTransaksi;

    private int jumlah;
    private double total;

    public Order() {
    }

    public Order(long id, long produkId, long pelangganId, LocalDate tglTransaksi, int jumlah, double total) {
        this.id = id;
        this.produkId = produkId;
        this.pelangganId = pelangganId;
        this.tglTransaksi = tglTransaksi;
        this.jumlah = jumlah;
        this.total = total;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProdukId() {
        return produkId;
    }

    public void setProdukId(long produkId) {
        this.produkId = produkId;
    }

    public long getPelangganId() {
        return pelangganId;
    }

    public void setPelangganId(long pelangganId) {
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
}
