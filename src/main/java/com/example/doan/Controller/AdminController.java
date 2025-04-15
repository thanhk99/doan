package com.example.doan.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.doan.Model.users;
import com.example.doan.Repository.HisBalanceRepo;
import com.example.doan.Repository.MessageRepo;
import com.example.doan.Repository.UsersRepository;
import com.example.doan.Repository.atmRepository;
import com.example.doan.Repository.betHisfbxsRepo;
import com.example.doan.Repository.friendRepository;
import com.example.doan.Repository.sessionPlayerRepo;

@RequestMapping("admin")
@RestController
public class AdminController {
    @Autowired
    private MessageRepo MessageRepo;
    @Autowired
    private betHisfbxsRepo betHisfbxsRepo;
    @Autowired
    private friendRepository friendRepository;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private HisBalanceRepo hisBalanceRepo;
    @Autowired
    private sessionPlayerRepo sessionPlayerRepo;

    @Autowired
    private atmRepository atmRepository;
    // @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/hello")

    public ResponseEntity<?> Home(@RequestBody users body){
        System.out.println(body.getTk());
        return ResponseEntity.ok(body);
    }

     @PostMapping("/allUsers")
    public ResponseEntity<?> getFullUsers (@RequestBody users request) {
        List<users> users = usersRepository.findAllUsers();
        if (!users.isEmpty()) {
            return ResponseEntity.ok(users);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy");
        }
    }
    @PostMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody users request) {
        Optional<users> user = usersRepository.findById(request.getId());
        if (user.isPresent()) {
            users updatedUser = user.get();
            updatedUser.setFullname(request.getFullname());
            updatedUser.setEmail(request.getEmail());
            usersRepository.save(updatedUser);
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy người dùng với ID: " + request.getId());
        }
    }
    @DeleteMapping("/delete")
public ResponseEntity<Map<String, String>> deleteUser(@RequestBody users request) {
    int userId = request.getId();

    Map<String, String> response = new HashMap<>();

    if (!usersRepository.existsById(userId)) {
        response.put("message", "Không tìm thấy người dùng với ID: " + userId);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    try {
        // Xoá dữ liệu phụ (nếu có)
        betHisfbxsRepo.deleteById(userId);
        MessageRepo.deleteById(userId);
        sessionPlayerRepo.deleteById(userId);
        atmRepository.deleteById(userId);
        hisBalanceRepo.deleteById(userId);
        friendRepository.deleteById(userId);

        // Xoá user
        usersRepository.deleteById(userId);

        response.put("message", "Xóa người dùng và các dữ liệu liên quan thành công");
        return ResponseEntity.ok(response);

    } catch (Exception e) {
        e.printStackTrace();
        response.put("message", "Đã xảy ra lỗi khi xóa người dùng: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
}
