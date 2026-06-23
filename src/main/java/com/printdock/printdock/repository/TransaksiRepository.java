package com.printdock.printdock.repository;

import com.printdock.printdock.model.transaksi.Transaksi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransaksiRepository extends JpaRepository<Transaksi, Long> {
    List<Transaksi> findByBarangId(Long barangId);
}
