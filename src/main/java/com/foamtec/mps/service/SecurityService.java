package com.foamtec.mps.service;

import com.foamtec.mps.model.AppUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class SecurityService {

    final String SECRET_KEY = "Fa,gm8vbog9viNgo=y]go]";

    public String createToken(AppUser appUser) {

        return Jwts.builder().setSubject("token")
                .claim("username", appUser.getUsername())
                .claim("roles", appUser.getRole())
                .setIssuedAt(new Date())
                .setExpiration(DatePlusMinutes(60 * 24))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public Claims checkToken(HttpServletRequest request) throws ServletException {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ServletException("Missing or invalid Authorization header.");
        }

        final String token = authHeader.substring(7); // The part after "Bearer "
        try {
            final Claims claims = Jwts.parser().setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token).getBody();
            return claims;
        } catch (Exception e) {
            throw new ServletException("token fail");
        }
    }

    public Date DatePlusMinutes(int minutes) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, minutes);
        return now.getTime();
    }

    public String hasPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    public boolean checkPassword(String password, AppUser appUser) {
        return BCrypt.checkpw(password, appUser.getPassword());
    }

    public boolean checkRoleAdmin(Claims claims) {
        String role = (String) claims.get("roles");
        if(role.indexOf("Admin") >= 0) {
            return false;
        }
        return true;
    }

    public boolean checkRoleUser(Claims claims) {
        String role = (String) claims.get("roles");
        if(role.indexOf("User") >= 0) {
            return false;
        }
        return true;
    }

    public HttpHeaders getHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8");
        return headers;
    }
}
