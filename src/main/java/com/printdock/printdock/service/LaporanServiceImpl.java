package com.printdock.printdock.service;

import com.printdock.printdock.dto.response.LaporanStokResponse;
import com.printdock.printdock.dto.response.LaporanTransaksiResponse;
import com.printdock.printdock.model.laporan.LaporanStok;
import com.printdock.printdock.model.laporan.LaporanTransaksi;
import com.printdock.printdock.repository.BarangRepository;
import com.printdock.printdock.repository.TransaksiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LaporanServiceImpl implements LaporanService {

    private final BarangRepository barangRepository;
    private final TransaksiRepository transaksiRepository;

    @Override
    public List<LaporanStokResponse> getLaporanStok() {
        return new LaporanStok(barangRepository.findAll()).generateLaporan();
    }

    @Override
    public List<LaporanTransaksiResponse> getLaporanTransaksi() {
        return new LaporanTransaksi(transaksiRepository.findAll()).generateLaporan();
    }

    @Override
    public List<LaporanStokResponse> getLaporanStokMenipis(Integer threshold) {
        return new LaporanStok(barangRepository.findByStokLessThanEqual(threshold)).generateLaporan();
    }
}
