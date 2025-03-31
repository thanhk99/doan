package com.example.doan.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.doan.Model.users;

@RequestMapping("admin")
@RestController
public class AdminController {
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/hello")
    public ResponseEntity<?> Home(@RequestBody users body){
        System.out.println(body.getTk());
        return ResponseEntity.ok(body);
    }
}
