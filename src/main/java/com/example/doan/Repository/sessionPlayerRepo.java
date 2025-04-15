package com.example.doan.Repository;

import com.example.doan.Model.sessionPlayer;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.doan.Model.sessionPlayer;

public interface sessionPlayerRepo extends JpaRepository<sessionPlayer, Integer> {
    @Query(value = "SELECT id, namegame, playerid, timeoccurs, result, bet, reward, choice " +
            "FROM sessionplayer " +
            "WHERE playerid = :idPlayer " +
            "ORDER BY timeoccurs DESC ", nativeQuery = true)
    List<sessionPlayer> findTop5ByIdPlayer(@Param("idPlayer") int idPlayer);

}
