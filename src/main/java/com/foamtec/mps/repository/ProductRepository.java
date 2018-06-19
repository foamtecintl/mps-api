package com.foamtec.mps.repository;

import com.foamtec.mps.model.GroupForecast;
import com.foamtec.mps.model.Product;

import java.util.List;

public interface ProductRepository {
    Product findById(Long id);
    Product findByPartNumber(String partNumber);
    Product findByCodeSap(String codeSap);
    List<Product> searchProductsByGroup(String text, GroupForecast groupForecast);
    List<Product> searchProductsByGroupLimit(String text, GroupForecast groupForecast, int start, int limit);
}
