package com.foamtec.mps.repository;

import com.foamtec.mps.model.InformationFileData;

public interface InformationFileDataRepository {
    InformationFileData save(InformationFileData informationFileData);
    InformationFileData update(InformationFileData informationFileData);
    InformationFileData findById(Long id);
}
