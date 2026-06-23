package com.printdock.printdock.model.transaksi;

import com.printdock.printdock.model.barang.Barang;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "transaksi")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Transaksi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tanggal", nullable = false)
    private LocalDate tanggal;

    @Column(name = "jumlah", nullable = false)
    private Integer jumlah;

    @Column(name = "keterangan")
    private String keterangan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "barang_id", nullable = false)
    private Barang barang;

    public abstract void proses();
}
