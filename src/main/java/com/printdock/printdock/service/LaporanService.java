package com.printdock.printdock.service;

import com.printdock.printdock.dto.response.LaporanStokResponse;
import com.printdock.printdock.dto.response.LaporanTransaksiResponse;
import java.util.List;

public interface LaporanService {
    List<LaporanStokResponse> getLaporanStok();
    List<LaporanTransaksiResponse> getLaporanTransaksi();
    List<LaporanStokResponse> getLaporanStokMenipis(Integer threshold);
}
