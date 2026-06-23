package com.printdock.printdock.service;

import com.printdock.printdock.dto.request.BarangRequest;
import com.printdock.printdock.dto.response.BarangResponse;
import java.util.List;

public interface BarangService {
    List<BarangResponse> findAll();
    BarangResponse findById(Long id);
    BarangResponse create(BarangRequest request);
    BarangResponse update(Long id, BarangRequest request);
    void delete(Long id);
}
