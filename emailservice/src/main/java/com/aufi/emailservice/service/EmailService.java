package com.aufi.emailservice.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.aufi.emailservice.event.OrderEvent;

/**
 * Layanan pembuat email. Menyusun isi email berdasarkan jenis event order, lalu
 * "mengirimkannya" — pada tahap ini pengiriman disimulasikan lewat LOG dan
 * disimpan di memori agar bisa dilihat melalui endpoint REST.
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** Daftar email yang "terkirim" (disimpan di memori untuk keperluan demo). */
    private final List<EmailMessage> outbox = new ArrayList<>();

    public synchronized EmailMessage process(OrderEvent event) {
        EmailMessage email = build(event);
        outbox.add(email);

        // "Pengiriman" email -> log console
        log.info("\n======================================================"
                + "\n  EMAIL TERKIRIM"
                + "\n  Kepada : {} <{}>"
                + "\n  Subjek : {}"
                + "\n------------------------------------------------------"
                + "\n{}"
                + "\n======================================================",
                email.getKepadaNama(), email.getKepadaEmail(), email.getSubjek(), email.getIsi());

        return email;
    }

    private EmailMessage build(OrderEvent e) {
        String kepadaNama = e.getNamaPelanggan() != null ? e.getNamaPelanggan() : "Pelanggan";
        String kepadaEmail = e.getEmailPelanggan() != null ? e.getEmailPelanggan() : "(email tidak diketahui)";

        String subjek;
        String isi;
        switch (e.getEventType()) {
            case CREATED -> {
                subjek = "Pesanan #" + e.getOrderId() + " Berhasil Dibuat";
                isi = "Terima kasih, " + kepadaNama + ".\n"
                        + "Pesanan Anda telah kami terima.\n\n"
                        + detail(e);
            }
            case UPDATED -> {
                subjek = "Pesanan #" + e.getOrderId() + " Telah Diperbarui";
                isi = "Halo " + kepadaNama + ".\n"
                        + "Pesanan Anda telah mengalami perubahan.\n\n"
                        + detail(e);
            }
            case DELETED -> {
                subjek = "Pesanan #" + e.getOrderId() + " Dibatalkan";
                isi = "Halo " + kepadaNama + ".\n"
                        + "Pesanan Anda telah dibatalkan/dihapus.\n\n"
                        + detail(e);
            }
            default -> {
                subjek = "Notifikasi Pesanan #" + e.getOrderId();
                isi = detail(e);
            }
        }

        return new EmailMessage(e.getEventType().name(), kepadaEmail, kepadaNama, subjek, isi,
                LocalDateTime.now().format(FMT));
    }

    private String detail(OrderEvent e) {
        return "Detail Pesanan:\n"
                + "  No. Order     : " + e.getOrderId() + "\n"
                + "  Produk        : " + (e.getNamaProduk() != null ? e.getNamaProduk() : ("#" + e.getProdukId())) + "\n"
                + "  Harga Satuan  : " + e.getHargaProduk() + "\n"
                + "  Jumlah        : " + e.getJumlah() + "\n"
                + "  Total         : " + e.getTotal() + "\n"
                + "  Tgl Transaksi : " + e.getTglTransaksi();
    }

    public synchronized List<EmailMessage> getOutbox() {
        return new ArrayList<>(outbox);
    }

    public synchronized int count() {
        return outbox.size();
    }

    public synchronized void clear() {
        outbox.clear();
    }

    /** Model pesan email. */
    public static class EmailMessage {
        private String tipe;
        private String kepadaEmail;
        private String kepadaNama;
        private String subjek;
        private String isi;
        private String waktu;

        public EmailMessage() {
        }

        public EmailMessage(String tipe, String kepadaEmail, String kepadaNama, String subjek, String isi, String waktu) {
            this.tipe = tipe;
            this.kepadaEmail = kepadaEmail;
            this.kepadaNama = kepadaNama;
            this.subjek = subjek;
            this.isi = isi;
            this.waktu = waktu;
        }

        public String getTipe() {
            return tipe;
        }

        public String getKepadaEmail() {
            return kepadaEmail;
        }

        public String getKepadaNama() {
            return kepadaNama;
        }

        public String getSubjek() {
            return subjek;
        }

        public String getIsi() {
            return isi;
        }

        public String getWaktu() {
            return waktu;
        }
    }
}
