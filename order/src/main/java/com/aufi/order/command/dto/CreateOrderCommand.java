package com.aufi.order.command.dto;

import java.time.LocalDate;

/**
 * Perintah (command) untuk membuat order baru. Command adalah objek niat
 * (intent) — hanya berisi data yang dibutuhkan untuk mengubah write model.
 */
public class CreateOrderCommand {

    private Long produkId;
    private Long pelangganId;
    private LocalDate tglTransaksi;
    private int jumlah;
    private double total;

    public CreateOrderCommand() {
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
}
