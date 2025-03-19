package com.example.doan.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.doan.Model.sessionPlayer;

public interface sessionPlayerRepo extends JpaRepository<sessionPlayer, Integer> {
    
}
