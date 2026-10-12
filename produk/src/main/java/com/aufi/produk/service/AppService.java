package com.aufi.produk.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.aufi.produk.entity.Jenis;
import com.aufi.produk.entity.Produk;
import com.aufi.produk.repository.JenisRepository;
import com.aufi.produk.repository.ProdukRepository;
import java.util.List;

@Service
public class AppService {

    @Autowired
    private ProdukRepository produkRepository;

    @Autowired
    private JenisRepository jenisRepository;

    // --- Jenis Methods ---
    public List<Jenis> getAllJenisEntity() {
        return jenisRepository.findAll();
    }

    public Jenis getJenisEntityById(long id) {
        return jenisRepository.findById(id).orElse(null);
    }

    public Jenis saveJenis(Jenis jenis) {
        return jenisRepository.save(jenis);
    }

    public void deleteJenis(long id) {
        jenisRepository.deleteById(id);
    }

    // --- Produk Methods ---
    public List<Produk> getAllProduk() {
        return produkRepository.findAll();
    }

    public List<String> getAllJenisDistinct() {
        return produkRepository.findDistinctJenis();
    }

    public List<Produk> getProdukByJenis(String jenis) {
        return produkRepository.findByJenis(jenis);
    }

    public Produk saveProduk(Produk produk) {
        return produkRepository.save(produk);
    }

    public Produk getProdukById(long id) {
        return produkRepository.findById(id).orElse(null);
    }

    public void deleteProduk(long id) {
        produkRepository.deleteById(id);
    }

    public Produk updateProduk(Long id, Produk produk) {
        Produk existingProduk = produkRepository.findById(id).orElse(null);
        if (existingProduk != null) {
            existingProduk.setNama(produk.getNama());
            existingProduk.setDeskripsi(produk.getDeskripsi());
            existingProduk.setHarga(produk.getHarga());
            existingProduk.setJenis(produk.getJenis());
            return produkRepository.save(existingProduk);
        }
        return null;
    }
}
