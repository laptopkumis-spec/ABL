package com.aufi.pelanggan.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.aufi.pelanggan.entity.Pelanggan;
import com.aufi.pelanggan.service.PelangganService;

@RestController
@RequestMapping("/api/pelanggan")
public class PelangganController {

    @Autowired
    private PelangganService pelangganService;

    @GetMapping
    public List<Pelanggan> getAllPelanggan() {
        return pelangganService.getAllPelanggan();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pelanggan> getPelangganById(@PathVariable("id") Long id) {
        Pelanggan pelanggan = pelangganService.getPelangganById(id);
        if (pelanggan == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pelanggan);
    }

    @PostMapping
    public ResponseEntity<Pelanggan> createPelanggan(@RequestBody Pelanggan pelanggan) {
        return ResponseEntity.ok(pelangganService.savePelanggan(pelanggan));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pelanggan> updatePelanggan(@PathVariable("id") Long id, @RequestBody Pelanggan pelanggan) {
        Pelanggan updated = pelangganService.updatePelanggan(id, pelanggan);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePelanggan(@PathVariable("id") Long id) {
        Pelanggan pelanggan = pelangganService.getPelangganById(id);
        if (pelanggan == null) {
            return ResponseEntity.notFound().build();
        }
        pelangganService.deletePelanggan(id);
        return ResponseEntity.ok().build();
    }
}
