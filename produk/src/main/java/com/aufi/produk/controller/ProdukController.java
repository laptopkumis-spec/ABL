package com.aufi.produk.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.aufi.produk.entity.Jenis;
import com.aufi.produk.entity.Produk;
import com.aufi.produk.service.AppService;

@RestController
@RequestMapping("/api/produk")
public class ProdukController {

    @Autowired
    private AppService appService;

    @GetMapping
    public List<Produk> getAllProduk() {
        return appService.getAllProduk();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produk> getProdukById(@PathVariable("id") Long id) {
        Produk produk = appService.getProdukById(id);
        if (produk == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(produk);
    }

    @PostMapping
    public ResponseEntity<Produk> createProduk(@RequestBody Produk produk) {
        return ResponseEntity.ok(appService.saveProduk(produk));
    }

    @GetMapping("/jenis")
    public List<String> getAllJenisDistinct() {
        return appService.getAllJenisDistinct();
    }

    @GetMapping("/jenis/{jenis}")
    public List<Produk> getProdukByJenis(@PathVariable("jenis") String jenis) {
        return appService.getProdukByJenis(jenis);
    }

    // --- Endpoints for Jenis Entity (Integrated into ProdukController) ---
    @GetMapping("/jenis-entity")
    public List<Jenis> getAllJenisEntity() {
        return appService.getAllJenisEntity();
    }

    @PostMapping("/jenis-entity")
    public ResponseEntity<Jenis> createJenisEntity(@RequestBody Jenis jenis) {
        return ResponseEntity.ok(appService.saveJenis(jenis));
    }

    @DeleteMapping("/jenis-entity/{id}")
    public ResponseEntity<Void> deleteJenisEntity(@PathVariable("id") Long id) {
        Jenis jenis = appService.getJenisEntityById(id);
        if (jenis == null) {
            return ResponseEntity.notFound().build();
        }
        appService.deleteJenis(id);
        return ResponseEntity.ok().build();
    }
}
