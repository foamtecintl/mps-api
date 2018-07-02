package com.foamtec.mps.repository;

import com.foamtec.mps.model.Forecast;

public interface ForecastRepository {
    Forecast save(Forecast forecast);
    Forecast update(Forecast forecast);
    Forecast findById(Long id);
    Forecast findByForecastNo(String forecastNumber);
}
