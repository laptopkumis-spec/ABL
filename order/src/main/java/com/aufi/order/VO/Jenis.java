package com.aufi.order.VO;

public class Jenis {

    private long id;

    private String nama;

    public Jenis() {
    }

    public Jenis(long id, String nama) {
        this.id = id;
        this.nama = nama;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }
}
