package com.foamtec.mps.repository;

import com.foamtec.mps.model.SubForecast;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class SubForecastRepositoryImpl implements SubForecastRepository{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<SubForecast> findByPartNumber(String partNumber) {
        Query query = entityManager.createQuery("SELECT a FROM SubForecast a WHERE a.partNumber = :partNumber");
        query.setParameter("partNumber", partNumber);
        return query.getResultList();
    }
}
