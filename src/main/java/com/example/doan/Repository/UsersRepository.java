package com.example.doan.Repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.doan.Model.users;
import java.util.List;


public interface UsersRepository extends JpaRepository<users, Integer> {

    Optional<users> findByTk(String tk);
    Optional<users> findByFullname(String fullname);
    List<users> findByFullnameContaining(String fullname);
}
