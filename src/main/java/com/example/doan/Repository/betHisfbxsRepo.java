package com.example.doan.Repository;

import com.example.doan.Model.betHisfbxs;
<<<<<<< HEAD
import com.example.doan.Model.betHisfbxs.BetType;
=======

import jakarta.transaction.Transactional;
>>>>>>> 1a2e665cee50a075d7d1ee78ea822c7ecf5e5541

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface betHisfbxsRepo extends JpaRepository<betHisfbxs, Integer> {

    @Query(value = "SELECT * FROM betHisfbxs WHERE idplayer = :idPlayer", nativeQuery = true)
    List<betHisfbxs> findByIdPlayer(@Param("idPlayer") int idPlayer);

    List<betHisfbxs> findByBetType(betHisfbxs.BetType betType);

    List<betHisfbxs> findByStatus(Boolean status);

    List<betHisfbxs> findByStatusFalseAndBetType(betHisfbxs.BetType betType);
<<<<<<< HEAD
    List<betHisfbxs> findByIdPlayerAndBetType(int idPlayer, betHisfbxs.BetType betType);
    // List<betHisfbxs> findByBetTypeAndReferenceIdAndStatus(betHisfbxs.BetType betType, String referenceId, Boolean status);
    // List<betHisfbxs> findByReferenceIdAndStatusFalseAndBetType(String referenceId, betHisfbxs.BetType betType);
    List<betHisfbxs> findByReferenceIdAndStatusFalseAndBetTypeAndBetTimeBefore(
        String referenceId, 
        BetType betType, 
        LocalDateTime betTime
    );

    List<betHisfbxs> findByBetTypeAndStatusFalseAndBetTimeBetween(
    BetType betType, LocalDateTime start, LocalDateTime end);
=======

    @Modifying
    @Transactional
    @Query("DELETE FROM betHisfbxs b WHERE b.idPlayer = :id")
    void deleteByBetHisfbxsId(@Param("id") int id);
>>>>>>> 1a2e665cee50a075d7d1ee78ea822c7ecf5e5541
}
