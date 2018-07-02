package com.foamtec.mps.repository;

import com.foamtec.mps.model.InformationFileData;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Repository
@Transactional
public class InformationFileDataRepositoryImpl implements InformationFileDataRepository{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public InformationFileData save(InformationFileData informationFileData) {
        entityManager.persist(informationFileData);
        return informationFileData;
    }

    @Override
    public InformationFileData update(InformationFileData informationFileData) {
        entityManager.merge(informationFileData);
        entityManager.flush();
        return informationFileData;
    }

    @Override
    public InformationFileData findById(Long id) {
        return entityManager.find(InformationFileData.class, id);
    }
}
