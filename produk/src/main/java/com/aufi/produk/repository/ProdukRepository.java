package com.aufi.produk.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.aufi.produk.entity.Produk;
import java.util.List;

@Repository
public interface ProdukRepository extends JpaRepository<Produk, Long> {
    List<Produk> findByJenis(String jenis);

    @Query("SELECT DISTINCT p.jenis FROM Produk p")
    List<String> findDistinctJenis();
}