package com.aufi.emailservice.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.aufi.emailservice.event.OrderEvent;

/**
 * Layanan pembuat & pengirim email.
 *
 * Menyusun isi email berdasarkan jenis event order, lalu MENGIRIMKANNYA secara
 * sungguhan melalui SMTP (JavaMailSender) ke alamat tujuan yang dikonfigurasi.
 * Hasil setiap pengiriman (berhasil/gagal) juga disimpan di memori agar bisa
 * diperiksa melalui endpoint REST /api/emails.
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final JavaMailSender mailSender;

    /** Alamat tujuan notifikasi (dari app.mail.to). */
    @Value("${app.mail.to:}")
    private String alamatTujuan;

    /** Pengirim (dari spring.mail.username). */
    @Value("${spring.mail.username:}")
    private String alamatPengirim;

    /** Daftar email yang diproses (arsip + status pengiriman). */
    private final List<EmailMessage> outbox = new ArrayList<>();

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public synchronized EmailMessage process(OrderEvent event) {
        EmailMessage email = build(event);

        // Tentukan tujuan: pakai app.mail.to kalau diisi; jika tidak, pakai email pelanggan
        String tujuan = (alamatTujuan != null && !alamatTujuan.isBlank())
                ? alamatTujuan
                : email.getKepadaEmail();
        email.setKepadaEmail(tujuan);

        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            if (alamatPengirim != null && !alamatPengirim.isBlank()) {
                msg.setFrom(alamatPengirim);
            }
            msg.setTo(tujuan);
            msg.setSubject(email.getSubjek());
            msg.setText(email.getIsi());
            mailSender.send(msg);

            email.setStatus("SENT");
            log.info("\n======================================================"
                    + "\n  EMAIL TERKIRIM (SMTP)"
                    + "\n  Kepada : {} <{}>"
                    + "\n  Subjek : {}"
                    + "\n======================================================",
                    email.getKepadaNama(), tujuan, email.getSubjek());
        } catch (Exception e) {
            email.setStatus("FAILED");
            email.setError(e.getMessage());
            log.error("\n======================================================"
                    + "\n  EMAIL GAGAL TERKIRIM"
                    + "\n  Kepada : {}"
                    + "\n  Subjek : {}"
                    + "\n  Error  : {}"
                    + "\n======================================================",
                    tujuan, email.getSubjek(), e.getMessage());
        }

        outbox.add(email);
        return email;
    }

    private EmailMessage build(OrderEvent e) {
        String kepadaNama = e.getNamaPelanggan() != null ? e.getNamaPelanggan() : "Pelanggan";
        String kepadaEmail = e.getEmailPelanggan() != null ? e.getEmailPelanggan() : "";

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
                + "  Tgl Transaksi : " + e.getTglTransaksi() + "\n\n"
                + "---\nEmail otomatis dari Order Service (CQRS + RabbitMQ).";
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

    /** Model pesan email + status pengiriman. */
    public static class EmailMessage {
        private String tipe;
        private String kepadaEmail;
        private String kepadaNama;
        private String subjek;
        private String isi;
        private String waktu;
        private String status;
        private String error;

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

        public void setKepadaEmail(String kepadaEmail) {
            this.kepadaEmail = kepadaEmail;
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

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }
}
