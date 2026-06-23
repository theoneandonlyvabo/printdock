package com.printdock.printdock.repository;

import com.printdock.printdock.model.barang.Barang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BarangRepository extends JpaRepository<Barang, Long> {
    Optional<Barang> findByKodeBarang(String kodeBarang);
    boolean existsByKategoriId(Long kategoriId);
    List<Barang> findByStokLessThanEqual(Integer threshold);
}
