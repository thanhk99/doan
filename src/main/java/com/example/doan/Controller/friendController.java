package com.example.doan.Controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.doan.Model.friend;
import com.example.doan.Repository.friendRepository;
import org.springframework.beans.factory.annotation.Autowired;
@RequestMapping("friend")
@RestController
public class friendController {
    
    @Autowired
    private friendRepository friendRepository;

    public friendController() {
    }
    
    @PostMapping("/addFriend")
    public ResponseEntity<?> addFriend (@RequestBody friend request) {
        friendRepository.save(request);
        request.setRelative("bạn bè");
        friendRepository.save(request);
        return ResponseEntity.ok("Đã thêm bạn bè và cập nhật trạng thái là 'bạn bè' ");
    }

    @GetMapping("/getListFriend")
    public ResponseEntity<?> getListFriend() {
        return ResponseEntity.ok(friendRepository.findAll());
    }
    
}
