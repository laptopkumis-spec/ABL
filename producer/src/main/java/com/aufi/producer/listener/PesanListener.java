package com.aufi.producer.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.aufi.producer.config.RabbitMQConfig;

/**
 * Listener untuk MEMBUKTIKAN pesan benar-benar masuk ke RabbitMQ.
 *
 * ⚠️ PENTING: Listener ini DINONAKTIFKAN secara default.
 * Alasan: ada service terpisah (consumer-service, port 8084) yang bertugas
 * menerima pesan. Jika producer juga mendengarkan queue yang sama, keduanya
 * akan BEREEBUT pesan (competing consumers) sehingga sebagian pesan "hilang"
 * dari consumer.
 *
 * Aktifkan kembali HANYA jika ingin testing tanpa consumer-service:
 *   - buka komentar @Component dan @RabbitListener di bawah
 */
// @Component
public class PesanListener {

    private static final Logger log = LoggerFactory.getLogger(PesanListener.class);

    // @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void terima(String pesan) {
        log.info(">> Pesan diterima dari RabbitMQ [{}]: {}", RabbitMQConfig.QUEUE, pesan);
    }
}
