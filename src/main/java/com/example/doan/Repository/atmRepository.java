package com.example.doan.Repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.doan.Model.atm;

@Repository
public interface atmRepository extends JpaRepository<atm, Integer> {
    Optional<atm> findByIdPlayer(long idPlayer);
}
