package com.foamtec.mps.service;

import com.foamtec.mps.model.Forecast;
import com.foamtec.mps.model.GroupForecast;
import com.foamtec.mps.model.Product;
import com.foamtec.mps.model.SubForecast;
import com.foamtec.mps.repository.ForecastRepository;
import com.foamtec.mps.repository.GroupForecastRepository;
import com.foamtec.mps.repository.ProductRepository;
import com.foamtec.mps.repository.SubForecastRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MpsService {

    @Autowired
    private GroupForecastRepository groupForecastRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ForecastRepository forecastRepository;

    @Autowired
    private SubForecastRepository subForecastRepository;

    public List<GroupForecast> findAllGroupForecast() {
        return groupForecastRepository.findAll();
    }

    public GroupForecast saveGroupForecast(GroupForecast groupForecast) {
        return groupForecastRepository.save(groupForecast);
    }

    public void deleteGroupForecast(GroupForecast groupForecast) {
        groupForecastRepository.deleteGroup(groupForecast);
    }

    public GroupForecast findByIdGroupForecast(Long id) {
        return groupForecastRepository.findById(id);
    }

    public GroupForecast updateGroupForecast(GroupForecast groupForecast) {
        return groupForecastRepository.save(groupForecast);
    }

    public void deleteProduct(Product product) {
        productRepository.delete(product.getId());
    }

    public List<GroupForecast> searchGroupForecast(String text) {
        return groupForecastRepository.searchGroupForecast(text);
    }

    public List<GroupForecast> searchGroupForecastLimit(String text, int start, int limit) {
        return groupForecastRepository.searchGroupForecastLimit(text, start, limit);
    }

    public GroupForecast findGroupById(Long id) {
        return groupForecastRepository.findById(id);
    }

    public Product findProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product findProductByPartNumber(String partNumber) {
        return productRepository.findByPartNumber(partNumber);
    }

    public Product findProductByCodeSap(String codeSap) {
        return productRepository.findByCodeSap(codeSap);
    }

    public List<Product> searchProductsByGroup(String text, GroupForecast groupForecast) {
        return productRepository.searchProductsByGroup(text, groupForecast);
    }

    public List<Product> searchProductsByGroupLimit(String text, GroupForecast groupForecast, int start, int limit) {
        return productRepository.searchProductsByGroupLimit(text, groupForecast, start, limit);
    }

    public Forecast saveForecast(Forecast forecast) {
        return forecastRepository.save(forecast);
    }

    public Forecast findForecastById(Long id) {
        return forecastRepository.findById(id);
    }

    public void deleteForecast(Forecast forecast) {
        forecastRepository.delete(forecast);
    }

    public Forecast updateForecast(Forecast forecast) {
        return forecastRepository.update(forecast);
    }

    public Forecast findForecastByForecastNo(String forecastNo) {
        return forecastRepository.findByForecastNo(forecastNo);
    }

    public List<Forecast> searchForecast(String text) {
        return forecastRepository.searchForecast(text);
    }

    public List<Forecast> searchForecastLimit(String text, int start, int limit) {
        return forecastRepository.searchForecastLimit(text, start, limit);
    }

    public List<Product> searchPart(String text) {
        return productRepository.searchPart(text);
    }

    public List<Product> searchPartLimit(String text, int start, int limit) {
        return productRepository.searchPartLimit(text, start, limit);
    }

    public List<SubForecast> findSubForecastByPartNumber(String partNumber) {
        return subForecastRepository.findByPartNumber(partNumber);
    }
}
