package com.printdock.printdock.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.printdock.printdock.model.transaksi.JenisTransaksi;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LaporanTransaksiResponse {
    private Long id;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggal;
    private Integer jumlah;
    private String keterangan;
    private JenisTransaksi jenisTransaksi;
    private String kodeBarang;
    private String judulBuku;
    private String namaPenerbit;
    private String namaInstitusi;
    private String negaraTujuan;
}
