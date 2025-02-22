package com.example.doan.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.*;
import java.sql.Time;
import java.time.*;
@Entity
public class users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String tk;
    private String mk;
    private String fullname;
    private String email;
    public users() {}
    public users(int id, String tk, String mk, String fullname, String email){
        this.id = id;
        this.tk = tk;
        this.mk = mk;
        this.fullname = fullname;
        this.email = email;
    }
    public long getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTk() {
        return tk;
    }
    public void setTk(String tk) {
        this.tk = tk;
    }
    public String getMk() {
        return mk;
    }
    public void setMk(String mk) {
        this.mk = mk;
    }
    public String getFullname() {
        return fullname;
    }
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public String getEmail() {
        return email;
    }
}
