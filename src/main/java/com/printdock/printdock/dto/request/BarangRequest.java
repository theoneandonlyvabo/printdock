package com.printdock.printdock.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BarangRequest {
    private String kodeBarang;
    private String judulBuku;
    private String penerbit;
    private BigDecimal hargaSatuan;
    private Integer stok;
    private Long kategoriId;
}
