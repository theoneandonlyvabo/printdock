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
public class LaporanStokResponse {
    private Long id;
    private String kodeBarang;
    private String judulBuku;
    private String penerbit;
    private Integer stok;
    private BigDecimal hargaSatuan;
    private String namaKategori;
}
