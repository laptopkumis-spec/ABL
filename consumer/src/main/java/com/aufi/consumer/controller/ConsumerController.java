package com.aufi.consumer.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.aufi.consumer.config.RabbitMQConfig;
import com.aufi.consumer.service.ConsumerService;
import com.aufi.consumer.service.ConsumerService.PesanDiterima;

@RestController
@RequestMapping("/api/consumer")
public class ConsumerController {

    private final ConsumerService consumerService;

    public ConsumerController(ConsumerService consumerService) {
        this.consumerService = consumerService;
    }

    /** Info service & queue. */
    @GetMapping
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("service", "consumer-service");
        resp.put("queue", RabbitMQConfig.QUEUE);
        resp.put("jumlahPesanDiterima", consumerService.jumlahPesan());
        resp.put("caraLihat", "GET /api/consumer/pesan");
        return ResponseEntity.ok(resp);
    }

    /** Lihat semua pesan yang sudah diterima dari RabbitMQ. */
    @GetMapping("/pesan")
    public List<PesanDiterima> getSemuaPesan() {
        return consumerService.getSemuaPesan();
    }

    /** Jumlah pesan yang diterima. */
    @GetMapping("/pesan/count")
    public Map<String, Object> jumlahPesan() {
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("jumlah", consumerService.jumlahPesan());
        return resp;
    }

    /** Bersihkan daftar pesan di memori (tidak menghapus dari RabbitMQ). */
    @DeleteMapping("/pesan")
    public Map<String, Object> hapusSemua() {
        consumerService.hapusSemua();
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("status", "daftar pesan dikosongkan");
        return resp;
    }
}
