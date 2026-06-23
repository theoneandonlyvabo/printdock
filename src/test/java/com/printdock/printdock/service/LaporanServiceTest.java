package com.printdock.printdock.service;

import com.printdock.printdock.dto.response.LaporanStokResponse;
import com.printdock.printdock.dto.response.LaporanTransaksiResponse;
import com.printdock.printdock.model.barang.Barang;
import com.printdock.printdock.model.barang.Kategori;
import com.printdock.printdock.model.transaksi.BarangMasuk;
import com.printdock.printdock.repository.BarangRepository;
import com.printdock.printdock.repository.TransaksiRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LaporanServiceTest {

    @Mock BarangRepository barangRepository;
    @Mock TransaksiRepository transaksiRepository;
    @InjectMocks LaporanServiceImpl laporanService;

    @Test
    void getLaporanStok_returnsAllBarangAsStokReport() {
        Kategori k = Kategori.builder().id(1L).namaKategori("Fiksi").build();
        Barang b = Barang.builder().id(1L).kodeBarang("BK001").judulBuku("Laskar Pelangi")
                .penerbit("Bentang").hargaSatuan(BigDecimal.valueOf(75000)).stok(10).kategori(k).build();
        when(barangRepository.findAll()).thenReturn(List.of(b));

        List<LaporanStokResponse> result = laporanService.getLaporanStok();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getKodeBarang()).isEqualTo("BK001");
        assertThat(result.get(0).getStok()).isEqualTo(10);
    }

    @Test
    void getLaporanTransaksi_returnsAllTransaksiAsReport() {
        Kategori k = Kategori.builder().id(1L).namaKategori("Fiksi").build();
        Barang b = Barang.builder().id(1L).kodeBarang("BK001").judulBuku("Laskar Pelangi")
                .stok(10).kategori(k).hargaSatuan(BigDecimal.valueOf(75000)).penerbit("Bentang").build();
        BarangMasuk masuk = new BarangMasuk();
        masuk.setId(1L);
        masuk.setBarang(b);
        masuk.setJumlah(5);
        masuk.setTanggal(LocalDate.now());
        masuk.setNamaPenerbit("Bentang Pustaka");
        when(transaksiRepository.findAll()).thenReturn(List.of(masuk));

        List<LaporanTransaksiResponse> result = laporanService.getLaporanTransaksi();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNamaPenerbit()).isEqualTo("Bentang Pustaka");
    }

    @Test
    void getLaporanStokMenipis_returnsBarangBelowThreshold() {
        Kategori k = Kategori.builder().id(1L).namaKategori("Fiksi").build();
        Barang low = Barang.builder().id(1L).kodeBarang("BK001").judulBuku("Title")
                .penerbit("Pub").hargaSatuan(BigDecimal.TEN).stok(3).kategori(k).build();
        when(barangRepository.findByStokLessThanEqual(10)).thenReturn(List.of(low));

        List<LaporanStokResponse> result = laporanService.getLaporanStokMenipis(10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStok()).isEqualTo(3);
    }
}
