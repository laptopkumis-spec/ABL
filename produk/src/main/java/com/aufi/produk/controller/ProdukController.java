package com.aufi.produk.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.aufi.produk.entity.Produk;
import com.aufi.produk.service.ProdukService;

@RestController
@RequestMapping("/api/produk")
public class ProdukController {

    @Autowired
    ProdukService produkService;

    @GetMapping
    public List<Produk> getAllProduk() {
        return produkService.getAllProduk();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produk> getProdukById(@PathVariable("id") Long id) {
        Produk produk = produkService.getProdukById(id);
        if (produk == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(produk);
    }

    @PostMapping()
    public ResponseEntity<Produk> createProduk(@RequestBody Produk produk) {
        return ResponseEntity.ok(produkService.saveProduk(produk));
    }

    @GetMapping("/jenis")
    public List<String> getAllJenis() {
        return produkService.getAllJenisDistinct();
    }

    @GetMapping("/jenis/{jenis}")
    public List<Produk> getProdukByJenis(@PathVariable("jenis") String jenis) {
        return produkService.getProdukByJenis(jenis);
    } // dari si postman, request ke controller, komunikasi dengan controller,
      // controller komunikasi dengan service
}