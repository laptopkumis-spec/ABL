package com.aufi.order.command;

import com.aufi.order.dto.PelangganDto;
import com.aufi.order.dto.ProdukDto;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Pengaya data (enrichment) — memanggil produk-service dan pelanggan-service
 * melalui Eureka (service discovery), BUKAN URL hardcode. Dipakai sisi Command
 * untuk mengisi data terdenormalisasi ke dalam event sebelum dipublikasikan
 * (Opsi B: denormalisasi saat proyeksi).
 */
@Component
public class OrderDataEnricher {

    private final DiscoveryClient discoveryClient;
    private final RestTemplate restTemplate;

    public OrderDataEnricher(DiscoveryClient discoveryClient, RestTemplate restTemplate) {
        this.discoveryClient = discoveryClient;
        this.restTemplate = restTemplate;
    }

    public ProdukDto fetchProduk(Long produkId) {
        try {
            ServiceInstance instance = discoveryClient.getInstances("produk-service").stream()
                    .findFirst().orElse(null);
            if (instance == null) return null;
            return restTemplate.getForObject(
                    instance.getUri() + "/api/produk/" + produkId, ProdukDto.class);
        } catch (Exception e) {
            return null;
        }
    }

    public PelangganDto fetchPelanggan(Long pelangganId) {
        try {
            ServiceInstance instance = discoveryClient.getInstances("pelanggan-service").stream()
                    .findFirst().orElse(null);
            if (instance == null) return null;
            return restTemplate.getForObject(
                    instance.getUri() + "/api/pelanggan/" + pelangganId, PelangganDto.class);
        } catch (Exception e) {
            return null;
        }
    }
}
