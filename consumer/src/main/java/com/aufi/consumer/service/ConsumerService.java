package com.aufi.consumer.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Menyimpan pesan yang diterima dari RabbitMQ di memori (untuk ditampilkan via REST).
 */
@Service
public class ConsumerService {

    private final List<PesanDiterima> daftarPesan = Collections.synchronizedList(new ArrayList<>());

    public void simpan(String isi) {
        daftarPesan.add(new PesanDiterima(isi, LocalDateTime.now()));
    }

    public List<PesanDiterima> getSemuaPesan() {
        return new ArrayList<>(daftarPesan);
    }

    public int jumlahPesan() {
        return daftarPesan.size();
    }

    public void hapusSemua() {
        daftarPesan.clear();
    }

    /** Model sederhana untuk pesan yang diterima. */
    public static class PesanDiterima {
        private final String isi;
        private final LocalDateTime waktu;

        public PesanDiterima(String isi, LocalDateTime waktu) {
            this.isi = isi;
            this.waktu = waktu;
        }

        public String getIsi() {
            return isi;
        }

        public LocalDateTime getWaktu() {
            return waktu;
        }
    }
}
