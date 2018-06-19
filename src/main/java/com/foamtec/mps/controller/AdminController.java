package com.foamtec.mps.controller;

import com.foamtec.mps.model.AppUser;
import com.foamtec.mps.service.AppUserService;
import com.foamtec.mps.service.MainService;
import com.foamtec.mps.service.SecurityService;
import io.jsonwebtoken.Claims;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private SecurityService securityService;

    @RequestMapping(value = "/createuser", method = RequestMethod.POST, headers = "Content-Type=Application/json")
    public ResponseEntity<String> createUser(@RequestBody Map<String, String> dataUser, HttpServletRequest request) throws ServletException {
        Claims claims = securityService.checkToken(request);
        if (securityService.checkRoleAdmin(claims)) {
            throw new ServletException("Invalid role");
        }

        String firstName = dataUser.get("firstName");
        String lastName = dataUser.get("lastName");
        String employeeId = dataUser.get("employeeId");
        String email = dataUser.get("email");
        String password = dataUser.get("password");
        String telephone = dataUser.get("telephone");
        String department = dataUser.get("department");
        String role = dataUser.get("role");

        if (appUserService.findByEmployeeId(employeeId) != null) {
            throw new ServletException("duplicate employee ID");
        }

        AppUser appUser = new AppUser();
        appUser.setCreateDate(new Date());
        appUser.setFirstName(firstName);
        appUser.setLastName(lastName);
        appUser.setEmployeeId(employeeId);
        appUser.setUsername(employeeId);
        appUser.setEmail(email);
        appUser.setPassword(securityService.hasPassword(password));
        appUser.setTelephone(telephone);
        appUser.setDepartment(department);
        appUser.setRole(role);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("message", "success");
            jsonObject.put("id", appUserService.save(appUser).getId());
            return new ResponseEntity<>(jsonObject.toString(), securityService.getHeader(), HttpStatus.OK);
        } catch (Exception e) {
            throw new ServletException("save fail");
        }
    }

    @RequestMapping(value = "/updateuser", method = RequestMethod.POST, headers = "Content-Type=Application/json")
    public ResponseEntity<String> updateUser(@RequestBody Map<String, String> dataUser, HttpServletRequest request) throws ServletException {
        Claims claims = securityService.checkToken(request);
        if (securityService.checkRoleAdmin(claims)) {
            throw new ServletException("Invalid role");
        }
        String strId = dataUser.get("id");
        String firstName = dataUser.get("firstName");
        String lastName = dataUser.get("lastName");
        String employeeId = dataUser.get("employeeId");
        String email = dataUser.get("email");
        String password = dataUser.get("password");
        String telephone = dataUser.get("telephone");
        String department = dataUser.get("department");
        String role = dataUser.get("role");

        AppUser appUser = appUserService.findById(Long.parseLong(strId));
        appUser.setCreateDate(new Date());
        appUser.setFirstName(firstName);
        appUser.setLastName(lastName);
        appUser.setEmployeeId(employeeId);
        appUser.setUsername(employeeId);
        appUser.setEmail(email);
        appUser.setPassword(securityService.hasPassword(password));
        appUser.setTelephone(telephone);
        appUser.setDepartment(department);
        appUser.setRole(role);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("message", "success");
            jsonObject.put("id", appUserService.update(appUser).getId());
            return new ResponseEntity<>(jsonObject.toString(), securityService.getHeader(), HttpStatus.OK);
        } catch (Exception e) {
            throw new ServletException("save fail");
        }
    }

    @RequestMapping(value = "/finduserbyid", method = RequestMethod.POST, headers = "Content-Type=Application/json")
    public ResponseEntity<String> findUserById(@RequestBody Map<String, Long> data, HttpServletRequest request) throws ServletException {
        Claims claims = securityService.checkToken(request);
        if (securityService.checkRoleAdmin(claims)) {
            throw new ServletException("Invalid role");
        }
        AppUser appUser = appUserService.findById(data.get("id"));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("firstName", appUser.getFirstName());
            jsonObject.put("lastName", appUser.getLastName());
            jsonObject.put("employeeID", appUser.getEmployeeId());
            jsonObject.put("email", appUser.getEmail());
            jsonObject.put("telephone", appUser.getTelephone());
            jsonObject.put("department", appUser.getDepartment());
            jsonObject.put("role", appUser.getRole());
            return new ResponseEntity<>(jsonObject.toString(), securityService.getHeader(), HttpStatus.OK);
        } catch (Exception e) {
            throw new ServletException("get user fail");
        }
    }

    @RequestMapping(value = "/searchuserslimit", method = RequestMethod.GET, headers = "Content-Type=Application/json")
    public ResponseEntity<String> searchUsersLimit(@RequestParam(value = "start", required = true) Integer start,
                                                 @RequestParam(value = "limit", required = true) Integer limit,
                                                 @RequestParam(value = "searchText", required = true) String searchText,
                                                 HttpServletRequest request) throws ServletException {
        Claims claims = securityService.checkToken(request);
        if (securityService.checkRoleAdmin(claims)) {
            throw new ServletException("Invalid role");
        }
        int totalUser = appUserService.searchUsers(searchText).size();
        List<AppUser> appUsers = appUserService.searchUsersLimit(searchText, start, limit);
        JSONObject jsonObject = new JSONObject();
        try {
            int i = start;
            JSONArray jsonArray = new JSONArray();
            for(AppUser a : appUsers) {
                i++;
                JSONObject jsonObjectAppUser = new JSONObject();
                jsonObjectAppUser.put("id", a.getId());
                jsonObjectAppUser.put("no", i);
                jsonObjectAppUser.put("firstName", a.getFirstName());
                jsonObjectAppUser.put("lastName", a.getLastName());
                jsonObjectAppUser.put("employeeId", a.getEmployeeId());
                jsonObjectAppUser.put("email", a.getEmail());
                jsonObjectAppUser.put("department", a.getDepartment());
                jsonObjectAppUser.put("role", a.getRole());
                jsonArray.put(jsonObjectAppUser);
            }

            jsonObject.put("totalUsers", totalUser);
            jsonObject.put("users", jsonArray);
            return new ResponseEntity<>(jsonObject.toString(), securityService.getHeader(), HttpStatus.OK);
        } catch (Exception e) {
            throw new ServletException("get user fail");
        }
    }
}
