package com.example.doan.Controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.doan.Model.friend;
import com.example.doan.Model.users;
import com.example.doan.Repository.UsersRepository;
import com.example.doan.Repository.friendRepository;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
@RequestMapping("friend")
@RestController
public class friendController {
    
    @Autowired
    private friendRepository friendRepository;

    @Autowired
    private UsersRepository usersRepository;
    
    @PostMapping("/addFriend")
    public ResponseEntity<?> addFriend (@RequestBody friend request) {
        friendRepository.save(request);
        request.setRelative("Đang chờ");
        friendRepository.save(request);
        return ResponseEntity.ok("Đã gửi lời mời kết bạn. Friend ID: " + request.getId());
}
    

@PostMapping("/acceptFriend")
public ResponseEntity<?> acceptFriend(@RequestBody Map<String, Integer> request) {
    Integer idMy = request.get("idMy");
    Integer idFriend = request.get("idFriend");

    if (idMy == null || idFriend == null) {
        return ResponseEntity.badRequest().body("ID không hợp lệ.");
    }

    // Tìm lời mời kết bạn dựa trên idMy và idFriend
    friend friendRequest = friendRepository.findByIdMyAndIdFriend(idMy, idFriend);

    if (friendRequest == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Lời mời kết bạn không tồn tại hoặc đã bị xóa.");
    }

    // Cập nhật trạng thái thành "Bạn bè"
    friendRequest.setRelative("Bạn bè");
    friendRepository.save(friendRequest);

    return ResponseEntity.ok("Lời mời kết bạn đã được chấp nhận.");
}
    

    @GetMapping("/getListFriend")
    public ResponseEntity<?> getListFriend() {
        return ResponseEntity.ok(friendRepository.findAll());
    }

    @DeleteMapping("/deleteFriend")
public ResponseEntity<?> deleteFriend(@RequestBody Map<String, Integer> request) {
    Integer idMy = request.get("idMy");
    Integer idFriend = request.get("idFriend");

    if (idMy == null || idFriend == null) {
        return ResponseEntity.badRequest().body("ID không hợp lệ.");
    }

    // Tìm bản ghi friend dựa trên idMy và idFriend
    friend friendRequest = friendRepository.findByIdMyAndIdFriend(idMy, idFriend);

    if (friendRequest == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Bạn bè không tồn tại.");
    }

    // Kiểm tra và xóa nếu trạng thái là "Đang chờ"
    if ("Đang chờ".equals(friendRequest.getRelative())) {
        friendRepository.delete(friendRequest);
        return ResponseEntity.ok("Lời mời kết bạn đã được xóa.");
    }

    // Xóa bạn bè
    friendRepository.delete(friendRequest);
    return ResponseEntity.ok("Bạn bè đã được xóa.");
}
    
}
