package com.foamtec.mps.service;

import com.foamtec.mps.model.AppUser;
import com.foamtec.mps.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppUserService {

    @Autowired
    private AppUserRepository appUserRepository;

    public List<AppUser> findAll() {
        return appUserRepository.findAll();
    }

    public AppUser save(AppUser appUser) {
        return appUserRepository.save(appUser);
    }

    public AppUser update(AppUser appUser) {
        return appUserRepository.update(appUser);
    }

    public AppUser findByUsername(String username) {
        return appUserRepository.findByUsername(username);
    }

    public AppUser findById(Long id) {
        return appUserRepository.findById(id);
    }

    public AppUser findByEmployeeId(String employeeId) {
        return appUserRepository.findByEmployeeId(employeeId);
    }

    public List<AppUser> searchUsers(String text) {
        return appUserRepository.searchUsers(text);
    }

    public List<AppUser> searchUsersLimit(String text, int start, int limit) {
        return appUserRepository.searchUsersLimit(text, start, limit);
    }
}
