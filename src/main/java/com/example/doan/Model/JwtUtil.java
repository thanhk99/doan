package com.example.doan.Model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.example.doan.Repository.UsersRepository;

@Component
public class JwtUtil extends OncePerRequestFilter{
    private String apiKey="anhthanhdz";
    @Autowired 
    private UsersRepository usersRepository;
    @Autowired 
    private UserDetailsService userDetailsService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;
        // Kiểm tra xem header có chứa token không
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7); // Lấy token
            try {
                username = Jwts.parser().setSigningKey(apiKey).parseClaimsJws(jwt).getBody().getSubject();
            } catch (ExpiredJwtException e) {
                // Xử lý token hết hạn
                System.out.println("Token expired");
            } catch (JwtException e) {
                // Xử lý token không hợp lệ
                System.out.println("Invalid token");
            }
        }
       
        //Nếu token hợp lệ, xác thực người dùng
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Optional<users> u= this.usersRepository.findByTk(username);
            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
            u.get().getTk(), u.get().getMk(), new ArrayList<>());
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }
         chain.doFilter(request, response);
        
    }
    public String generateToken(String tk){
        Map<String , Object> claims = new HashMap<>();
        return createToken(claims,tk);
    }
    public String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)    
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 *10))
        .signWith(SignatureAlgorithm.HS256, apiKey)
        .compact();
    }
}
