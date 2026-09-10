package com.aufi.produk.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.aufi.produk.entity.Produk;
import com.aufi.produk.repository.ProdukRepository;
import java.util.List;

@Service
public class ProdukService {
    @Autowired
    private ProdukRepository produkRepository;

    public List<Produk> getAllProduk() {
        return produkRepository.findAll();
    }

    public List<String> getAllJenisDistinct() {
        return produkRepository.findDistinctJenis();
    }

    public List<Produk> getProdukByJenis(String jenis) {
        return produkRepository.findByJenis(jenis); /// dii service ada getByJenis tadi yang berkomunikasi dengan
                                                    /// Repository
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
            existingProduk.setJenis(produk.getJenis()); // Tambahan field jenis
            return produkRepository.save(existingProduk);
        }
        return null;
    }
}