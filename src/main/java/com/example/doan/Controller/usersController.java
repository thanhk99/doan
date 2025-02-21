package com.example.doan.Controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.doan.Model.users;
import com.example.doan.Repository.UsersRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid; 

@RestController
@RequestMapping("/users")
public class usersController {
    public usersController(){}
    @Autowired
    private UsersRepository usersRepository;
    private static String fullname = "";
        @GetMapping()
        public ResponseEntity<users> getUsersById() {
            return usersRepository.findById(1).
            map(users -> ResponseEntity.ok(users)).
            orElse( ResponseEntity.notFound().build());
        }
    
        @PostMapping("/login")
        public ResponseEntity<?> login
            (@Valid @RequestBody users loginRequest,
            HttpServletRequest httpRequest,
            HttpSession session,
            HttpServletResponse response) {
            Optional <users> user = usersRepository.findByTk(loginRequest.getTk());
            if(user.isPresent() && user.get().getMk().equals(loginRequest.getMk())) {
                session.setAttribute("username", user.get().getFullname());
                fullname = user.get().getFullname();
                System.out.println(session.getAttribute("username"));
                return ResponseEntity.ok(user.get().getFullname());
            }
        else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sai tài khoản hoặc mật khẩu");
        }
    }
    public String getFullname() {
        return fullname;
    }
}
