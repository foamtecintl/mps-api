package com.foamtec.mps.repository;

import com.foamtec.mps.model.Forecast;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class ForecastRepositoryImpl implements ForecastRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Forecast save(Forecast forecast) {
        entityManager.persist(forecast);
        return forecast;
    }

    @Override
    public Forecast update(Forecast forecast) {
        entityManager.merge(forecast);
        entityManager.flush();
        return forecast;
    }

    @Override
    public Forecast findById(Long id) {
        return entityManager.find(Forecast.class, id);
    }

    @Override
    public Forecast findByForecastNo(String forecastNumber) {
        Query query = entityManager.createQuery("SELECT a FROM Forecast a WHERE a.forecastNumber = :forecastNumber");
        query.setParameter("forecastNumber", forecastNumber);
        Forecast forecast = null;
        try {
            forecast = (Forecast) query.getSingleResult();
        } catch (NoResultException nre) {}
        return forecast;
    }

    @Override
    public List<Forecast> searchForecast(String text) {
        String searchText = "%";
        if(text.length() > 0) {
            searchText = "%" + text + "%";
        }
        Query query = entityManager.createQuery("SELECT a FROM Forecast a WHERE a.forecastNumber LIKE :text OR " +
                "a.forecastGroup LIKE :text");
        query.setParameter("text", searchText);
        return query.getResultList();
    }

    @Override
    public List<Forecast> searchForecastLimit(String text, int start, int limit) {
        String searchText = "%";
        if(text.length() > 0) {
            searchText = "%" + text + "%";
        }
        Query query = entityManager.createQuery("SELECT a FROM Forecast a WHERE a.forecastNumber LIKE :text OR " +
                "a.forecastGroup LIKE :text");
        query.setParameter("text", searchText);
        query.setMaxResults(limit);
        query.setFirstResult(start);
        return query.getResultList();
    }
}
