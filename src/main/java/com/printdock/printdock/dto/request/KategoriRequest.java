package com.printdock.printdock.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KategoriRequest {
    private String namaKategori;
    private String deskripsi;
}
