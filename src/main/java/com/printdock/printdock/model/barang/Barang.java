package com.printdock.printdock.model.barang;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "barang")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Barang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "kode_barang", unique = true, nullable = false)
    private String kodeBarang;

    @Column(name = "judul_buku", nullable = false)
    private String judulBuku;

    @Column(name = "penerbit")
    private String penerbit;

    @Column(name = "harga_satuan", nullable = false)
    private BigDecimal hargaSatuan;

    @Column(name = "stok", nullable = false)
    private Integer stok;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kategori_id", nullable = false)
    private Kategori kategori;
}
