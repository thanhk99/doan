package com.example.doan.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.doan.Model.friend;
import com.example.doan.Model.users;

public interface friendRepository extends JpaRepository<friend, Integer> {
    friend findByIdMyAndIdFriend(Integer idMy, Integer idFriend);
    List<friend> findByIdMyAndRelative(Integer idMy, String relative);
    // List<friend> findFriendListByUserId(Integer idMy );
   
}
