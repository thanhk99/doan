package com.example.doan.Repository;
import com.example.doan.Model.betHisfbxs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface betHisfbxsRepo extends JpaRepository<betHisfbxs, Integer> {

    @Query(value = "SELECT * FROM betHisfbxs WHERE id_player = :idPlayer", nativeQuery = true)
    List<betHisfbxs> findByIdPlayer(@Param("idPlayer") int idPlayer);
    
    List<betHisfbxs> findByBetType(betHisfbxs.BetType betType);

}
