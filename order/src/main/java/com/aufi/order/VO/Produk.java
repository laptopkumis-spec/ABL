package com.aufi.order.VO;

public class Produk {
    private long id;
    private String nama;
    private String deskripsi;
    private double harga;
    private String jenis; // Field baru

    public Produk() {
    }

    public Produk(long id, String nama, String deskripsi, double harga, String jenis) {
        this.id = id;
        this.nama = nama;
        this.deskripsi = deskripsi;
        this.harga = harga;
        this.jenis = jenis;
    }

    // ... getters ...
    public String getJenis() {
        return jenis;
    }

    public long getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public double getHarga() {
        return harga;
    }

    // ..setters..
    public void setId(long id) {
        this.id = id;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public void setHarga(double harga) {
        this.harga = harga;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }
}
