package com.printdock.printdock.service;

import com.printdock.printdock.dto.request.KategoriRequest;
import com.printdock.printdock.dto.response.KategoriResponse;
import java.util.List;

public interface KategoriService {
    List<KategoriResponse> findAll();
    KategoriResponse findById(Long id);
    KategoriResponse create(KategoriRequest request);
    KategoriResponse update(Long id, KategoriRequest request);
    void delete(Long id);
}
