package com.printdock.printdock.model.barang;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "kategori")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Kategori {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nama_kategori", unique = true, nullable = false)
    private String namaKategori;

    @Column(name = "deskripsi")
    private String deskripsi;
}
