package com.printdock.printdock.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BarangMasukRequest {
    private Long barangId;
    private LocalDate tanggal;
    private Integer jumlah;
    private String keterangan;
    private String namaPenerbit;
}
