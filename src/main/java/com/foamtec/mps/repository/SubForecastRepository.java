package com.foamtec.mps.repository;

import com.foamtec.mps.model.SubForecast;

import java.util.List;

public interface SubForecastRepository {
    List<SubForecast> findByPartNumber(String partNumber);
}
