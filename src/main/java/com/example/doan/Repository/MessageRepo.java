package com.example.doan.Repository;

import java.util.List;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.doan.Model.Message;

@Repository
public interface MessageRepo extends JpaRepository<Message, Integer> {
    List<Message>findByIdMyAndIdFriendOrIdMyAndIdFriend(int idMy,int idFriend,int idFriend2,int idMy2,org.springframework.data.domain.Pageable pageable);
}
