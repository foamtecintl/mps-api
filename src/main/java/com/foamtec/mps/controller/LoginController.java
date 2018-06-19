package com.foamtec.mps.controller;

import com.foamtec.mps.model.AppUser;
import com.foamtec.mps.service.AppUserService;
import com.foamtec.mps.service.MainService;
import com.foamtec.mps.service.SecurityService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/security")
public class LoginController {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private MainService mainService;

    @RequestMapping(value = "/login", method = RequestMethod.POST, headers = "Content-Type=Application/json")
    public ResponseEntity<String> login(@RequestBody Map<String, String> dataLogin) throws ServletException {
        String username = dataLogin.get("username");
        String password = dataLogin.get("password");
        AppUser appUser = appUserService.findByUsername(username);
        if(appUser == null || !securityService.checkPassword(password, appUser)) {
            throw new ServletException("Invalid username or password.");
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("token", securityService.createToken(appUser));
        jsonObject.put("username", username);
        jsonObject.put("role", appUser.getRole());
        return new ResponseEntity<>(jsonObject.toString(), securityService.getHeader(), HttpStatus.OK);
    }

    @RequestMapping(value = "/createuseradmin/{code}", method = RequestMethod.GET)
    public ResponseEntity<String> createUserAdmin(@PathVariable("code") String code) throws ServletException {
        try {
            if(code.indexOf("pppassword") < 0) {
                throw new ServletException("code invalid");
            }
            AppUser appUser = new AppUser();
            appUser.setCreateDate(new Date());
            appUser.setEmployeeId("00000");
            appUser.setFirstName("Admin");
            appUser.setLastName("Admin");
            appUser.setSex("Male");
            appUser.setDepartment("MIS");
            appUser.setUsername("admin");
            appUser.setTelephone("814");
            appUser.setEmail("apichate@foamtecintl.com");
            appUser.setPassword(securityService.hasPassword("adminpassword"));
            appUser.setRole("Admin");
            appUserService.save(appUser);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", "created");
            return new ResponseEntity<>(jsonObject.toString(), securityService.getHeader(), HttpStatus.OK);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @RequestMapping(value = "/createusertest/{code}", method = RequestMethod.GET)
    public ResponseEntity<String> createUserTest(@PathVariable("code") String code) throws ServletException {
        try {
            if(code.indexOf("pppassword") < 0) {
                throw new ServletException("code invalid");
            }
            Date date = new Date();
            DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            System.out.println(sdf.format(date));
            for(int i = 1; i <= 120000; i++) {
                System.out.println(i);
                AppUser appUser = new AppUser();
                appUser.setCreateDate(new Date());
                appUser.setEmployeeId("00000" + i);
                appUser.setFirstName("Admin" + i);
                appUser.setLastName("Admin" + i);
                appUser.setSex("Male");
                appUser.setDepartment("MIS");
                appUser.setUsername("admin" + i);
                appUser.setTelephone("814");
                appUser.setEmail("apichate@foamtecintl.com");
                appUser.setPassword("adminpassword");
//                appUser.setPassword(securityService.hasPassword("adminpassword"));
                appUser.setRole("Admin");
                appUserService.save(appUser);
            }
            System.out.println("START = " + sdf.format(date));
            System.out.println("END = " + sdf.format(new Date()));
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", "created");
            return new ResponseEntity<>(jsonObject.toString(), securityService.getHeader(), HttpStatus.OK);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @RequestMapping(value = "/checkapionline", method = RequestMethod.GET)
    public ResponseEntity<String> checkApiOnline() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", "online");
        return new ResponseEntity<>(jsonObject.toString(), securityService.getHeader(), HttpStatus.OK);
    }
}
