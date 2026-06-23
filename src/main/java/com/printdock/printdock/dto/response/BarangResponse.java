package com.printdock.printdock.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BarangResponse {
    private Long id;
    private String kodeBarang;
    private String judulBuku;
    private String penerbit;
    private BigDecimal hargaSatuan;
    private Integer stok;
    private Long kategoriId;
    private String namaKategori;
}
