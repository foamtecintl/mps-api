package com.foamtec.mps.repository;

import com.foamtec.mps.model.GroupForecast;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class GroupForecastRepositoryImpl implements GroupForecastRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<GroupForecast> findAll() {
        return entityManager.createQuery("SELECT a from GroupForecast a").getResultList();
    }

    public GroupForecast findById(Long id) {
        return entityManager.find(GroupForecast.class, id);
    }

    public GroupForecast findByGroupName(String groupName) {
        Query query = entityManager.createQuery("SELECT a FROM GroupForecast a WHERE a.groupName = :groupName");
        query.setParameter("groupName", groupName);
        GroupForecast groupForecast = null;
        try {
            groupForecast = (GroupForecast) query.getSingleResult();
        } catch (NoResultException nre) {}
        return groupForecast;
    }

    public GroupForecast save(GroupForecast groupForecast) {
        entityManager.persist(groupForecast);
        return groupForecast;
    }

    @Override
    public void deleteGroup(GroupForecast groupForecast) {
        entityManager.remove(groupForecast);
    }

    public GroupForecast update(GroupForecast groupForecast) {
        entityManager.merge(groupForecast);
        entityManager.flush();
        return groupForecast;
    }

    @Override
    public List<GroupForecast> searchGroupForecast(String text) {
        String searchText = "%";
        if(text.length() > 0) {
            searchText = "%" + text + "%";
        }
        Query query = entityManager.createQuery("SELECT a FROM GroupForecast a WHERE a.groupName LIKE :text OR " +
                "a.groupType LIKE :text");
        query.setParameter("text", searchText);
        return query.getResultList();
    }

    @Override
    public List<GroupForecast> searchGroupForecastLimit(String text, int start, int limit) {
        String searchText = "%";
        if(text.length() > 0) {
            searchText = "%" + text + "%";
        }
        Query query = entityManager.createQuery("SELECT a FROM GroupForecast a WHERE a.groupName LIKE :text OR " +
                "a.groupType LIKE :text");
        query.setParameter("text", searchText);
        query.setMaxResults(limit);
        query.setFirstResult(start);
        return query.getResultList();
    }
}
