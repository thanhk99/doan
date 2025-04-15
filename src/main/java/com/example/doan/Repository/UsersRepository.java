package com.example.doan.Repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.doan.Model.users;
import java.util.List;


public interface UsersRepository extends JpaRepository<users, Integer> {

    Optional<users> findByTk(String tk);
    Optional<users> findByFullname(String fullname);
    List<users> findByFullnameContaining(String fullname);
    Optional<users> findIdAndFullnameById(int id);

    @Query(value = "SELECT * FROM users", nativeQuery = true)
    List<users> findAllUsers(); // Lấy tất cả người dùng từ bảng users
}
