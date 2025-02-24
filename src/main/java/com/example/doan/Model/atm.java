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
    @Column(name = "id_player")
    private int idplayer;
    private float debt;
    private float deposit;
    private float balance;
    private float borrowed;

    public atm() {}
    public atm(int idPlayer , float debt , float deposit , float balance , float borrowed){
        this.idplayer = idPlayer;
        this.debt = debt;
        this.deposit = deposit;
        this.balance = balance;
        this.borrowed = borrowed;
    }
    public long getId(){
        return idplayer;
    }
    public void setIdPlayer(int idPlayer){
        this.idplayer = idPlayer;
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




    
}
