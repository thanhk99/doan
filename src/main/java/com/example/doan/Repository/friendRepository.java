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
    List<friend> findIdFriendByIdMy(int idMy);
    List<friend> findByIdMyAndRelative(Integer idMy, String relative);
    List<friend> findByIdFriendAndRelative(Integer idFriend, String relative);
    // List<friend> findFriendListByUserId(Integer idMy );
    @Query("SELECT u.fullname FROM friend f " +
       "JOIN users u ON f.idFriend = u.id " +
       "WHERE f.relative = 'Bạn bè' " +
       "AND ((f.idMy = :idMy AND f.idFriend IN " +
       "(SELECT f2.idMy FROM friend f2 WHERE f2.idFriend = :idMy AND f2.relative = 'Bạn bè')))")
    List<String> findFriendNamesById(@Param("idMy") Integer idMy);

    @Query("SELECT u.fullname FROM friend f " +
       "JOIN users u ON f.idFriend = u.id " +
       "WHERE f.relative = 'Xác nhận' " +
       "AND f.idMy = :idMy")
       List<String> findFriendNamesByIdAndRelative(@Param("idMy") Integer idMy);





   
}
