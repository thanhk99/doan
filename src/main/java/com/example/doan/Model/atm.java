package com.example.doan.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.util.*;

import org.hibernate.annotations.ManyToAny;

import java.sql.Time;
import java.time.*;
@Entity
public class atm {
    @Id
    @Column(name = "idplayer")
    private Integer idPlayer; 

    @Column(name = "debt")
    private Float debt;

    @Column(name = "deposit")
    private Float deposit;

    @Column(name = "balance")
    private Float balance;

    @Column(name = "borrowed")
    private Float borrowed;

    @ManyToOne
    @JoinColumn(name = "idplayer", insertable = false, updatable = false)
    private users user; 

    public atm() {}
    public atm(int idPlayer , float debt , float deposit , float balance , float borrowed){
        this.idPlayer = idPlayer;
        this.debt = debt;
        this.deposit = deposit;
        this.balance = balance;
        this.borrowed = borrowed;
    }
    public int getIdPlayer(){
        return idPlayer;
    }
    public void setIdPlayer(int idPlayer){
        this.idPlayer = idPlayer;
    }
    public float getDebt(){
        return debt;
    }
    public void setDebt(float debt){
        this.debt = debt;
    }
    public float getDeposit(){
        return deposit;
    }
    public void setDeposit(float deposit){
        this.deposit = deposit;
    }
    public float getBalance(){
        return balance;
    }
    public void setBalance(float balance){
        this.balance = balance;
    }
    public float getBorrowed(){
        return borrowed;
    }
    public void setBorrowed(float borrowed){
        this.borrowed = borrowed;
    }
    public atm get() {
        throw new UnsupportedOperationException("Unimplemented method 'get'");
    }   
}
