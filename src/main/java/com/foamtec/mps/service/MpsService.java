package com.foamtec.mps.service;

import com.foamtec.mps.model.GroupForecast;
import com.foamtec.mps.model.Product;
import com.foamtec.mps.repository.GroupForecastRepository;
import com.foamtec.mps.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MpsService {

    @Autowired
    private GroupForecastRepository groupForecastRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<GroupForecast> findAllGroupForecast() {
        return groupForecastRepository.findAll();
    }

    public GroupForecast save(GroupForecast groupForecast) {
        return groupForecastRepository.save(groupForecast);
    }

    public GroupForecast findById(Long id) {
        return groupForecastRepository.findById(id);
    }

    public GroupForecast update(GroupForecast groupForecast) {
        return groupForecastRepository.save(groupForecast);
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
}
