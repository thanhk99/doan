package com.example.doan.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.doan.Model.Message;

@Repository
public interface MessageRepo extends JpaRepository<Message, Integer> {
    
}
