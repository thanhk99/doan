package com.example.doan.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.doan.Model.historyBalance;

@Repository
public interface HisBalanceRepo extends JpaRepository<historyBalance,Integer> {
    
}
