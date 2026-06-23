package com.printdock.printdock.model.laporan;

import java.util.List;

public abstract class Laporan<T> {
    public abstract List<T> generateLaporan();
}
