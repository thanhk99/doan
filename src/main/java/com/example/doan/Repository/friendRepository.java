package com.example.doan.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.doan.Model.friend;

public interface friendRepository extends JpaRepository<friend, Integer> {
} 
