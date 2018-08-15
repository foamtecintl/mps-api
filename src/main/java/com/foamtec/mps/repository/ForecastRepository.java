package com.foamtec.mps.repository;

import com.foamtec.mps.model.Forecast;

import java.util.List;

public interface ForecastRepository {
    Forecast save(Forecast forecast);
    Forecast update(Forecast forecast);
    Forecast findById(Long id);
    void delete(Forecast forecast);
    Forecast findByForecastNo(String forecastNumber);
    List<Forecast> searchForecast(String text);
    List<Forecast> searchForecastLimit(String text, int start, int limit);
}
