package com.aufi.producer.dto;

public class PesanDto {

    private String pengirim;
    private String isi;

    public PesanDto() {
    }

    public PesanDto(String pengirim, String isi) {
        this.pengirim = pengirim;
        this.isi = isi;
    }

    public String getPengirim() {
        return pengirim;
    }

    public void setPengirim(String pengirim) {
        this.pengirim = pengirim;
    }

    public String getIsi() {
        return isi;
    }

    public void setIsi(String isi) {
        this.isi = isi;
    }

    @Override
    public String toString() {
        return "PesanDto{pengirim='" + pengirim + "', isi='" + isi + "'}";
    }
}
