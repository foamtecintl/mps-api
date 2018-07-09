package com.foamtec.mps.repository;

import com.foamtec.mps.model.GroupForecast;
import com.foamtec.mps.model.Product;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class ProductRepositoryImpl implements ProductRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Product findById(Long id) {
        return entityManager.find(Product.class, id);
    }

    @Override
    public Product findByPartNumber(String partNumber) {
        Query query = entityManager.createQuery("SELECT a FROM Product a WHERE a.partNumber = :partNumber");
        query.setParameter("partNumber", partNumber);
        Product product = null;
        try {
            product = (Product) query.getSingleResult();
        } catch (NoResultException nre) {}
        return product;
    }

    @Override
    public void delete(Long id) {
        try {
            String sqlStr = "DELETE FROM GROUP_FORECAST_PRODUCTS WHERE PRODUCTS_ID = :id";
            Query nativeQuery = entityManager.createNativeQuery(sqlStr);
            nativeQuery.setParameter("id", id);
            nativeQuery.executeUpdate();
            sqlStr = "DELETE FROM PRODUCT WHERE ID = :id";
            nativeQuery = entityManager.createNativeQuery(sqlStr);
            nativeQuery.setParameter("id", id);
            nativeQuery.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Product findByCodeSap(String codeSap) {
        Query query = entityManager.createQuery("SELECT a FROM Product a WHERE a.codeSap = :codeSap");
        query.setParameter("codeSap", codeSap);
        Product product = null;
        try {
            product = (Product) query.getSingleResult();
        } catch (NoResultException nre) {}
        return product;
    }

    @Override
    public List<Product> searchProductsByGroup(String text, GroupForecast groupForecast) {
        String searchText = "%";
        if(text.length() > 0) {
            searchText = "%" + text + "%";
        }
        Query query = entityManager.createQuery("SELECT a FROM Product a WHERE (a.groupForecast = :groupForecast) AND " +
                "(a.partNumber LIKE :text OR a.partName LIKE :text OR a.codeSap LIKE :text)");
        query.setParameter("groupForecast", groupForecast);
        query.setParameter("text", searchText);
        return query.getResultList();
    }

    @Override
    public List<Product> searchProductsByGroupLimit(String text, GroupForecast groupForecast, int start, int limit) {
        String searchText = "%";
        if(text.length() > 0) {
            searchText = "%" + text + "%";
        }
        Query query = entityManager.createQuery("SELECT a FROM Product a WHERE (a.groupForecast = :groupForecast) AND " +
                "(a.partNumber LIKE :text OR a.partName LIKE :text OR a.codeSap LIKE :text)");
        query.setParameter("groupForecast", groupForecast);
        query.setParameter("text", searchText);
        query.setMaxResults(limit);
        query.setFirstResult(start);
        return query.getResultList();
    }

    @Override
    public List<Product> searchPart(String text) {
        String searchText = "%";
        if(text.length() > 0) {
            searchText = "%" + text + "%";
        }
        Query query = entityManager.createQuery("SELECT a FROM Product a WHERE a.partNumber LIKE :text");
        query.setParameter("text", searchText);
        return query.getResultList();
    }

    @Override
    public List<Product> searchPartLimit(String text, int start, int limit) {
        String searchText = "%";
        if(text.length() > 0) {
            searchText = "%" + text + "%";
        }
        Query query = entityManager.createQuery("SELECT a FROM Product a WHERE a.partNumber LIKE :text");
        query.setParameter("text", searchText);
        query.setMaxResults(limit);
        query.setFirstResult(start);
        return query.getResultList();
    }
}
