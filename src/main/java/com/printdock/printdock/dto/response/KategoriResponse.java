package com.printdock.printdock.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KategoriResponse {
    private Long id;
    private String namaKategori;
    private String deskripsi;
}
