package com.foamtec.mps.repository;

import com.foamtec.mps.model.GroupForecast;

import java.util.List;

public interface GroupForecastRepository {
    List<GroupForecast> findAll();
    GroupForecast findById(Long id);
    GroupForecast findByGroupName(String groupName);
    GroupForecast save(GroupForecast groupForecast);
    void deleteGroup(GroupForecast groupForecast);
    GroupForecast update(GroupForecast groupForecast);
    List<GroupForecast> searchGroupForecast(String text);
    List<GroupForecast> searchGroupForecastLimit(String text, int start, int limit);
}
