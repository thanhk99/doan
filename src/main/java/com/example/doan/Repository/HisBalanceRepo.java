package com.example.doan.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.doan.Model.historyBalance;

@Repository
public interface HisBalanceRepo extends JpaRepository<historyBalance,Integer> {
    @Query(value = "SELECT id_player, timechange, content, trans, balance " +
    "FROM historybalance " +
    "WHERE id_player = :idPlayer " +
    "ORDER BY timechange DESC " ,nativeQuery = true)
    List<Object[]> findTop5ByIdPlayer(@Param("idPlayer") int idPlayer);

}
