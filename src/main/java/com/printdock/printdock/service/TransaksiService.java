package com.printdock.printdock.service;

import com.printdock.printdock.dto.request.BarangKeluarRequest;
import com.printdock.printdock.dto.request.BarangMasukRequest;
import com.printdock.printdock.dto.response.TransaksiResponse;
import java.util.List;

public interface TransaksiService {
    TransaksiResponse masuk(BarangMasukRequest request);
    TransaksiResponse keluar(BarangKeluarRequest request);
    List<TransaksiResponse> findAll();
    TransaksiResponse findById(Long id);
}
