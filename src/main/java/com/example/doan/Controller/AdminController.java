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

import com.example.doan.Model.atm;
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
    public ResponseEntity<?> Home(@RequestBody users body) {
        System.out.println(body.getTk());
        return ResponseEntity.ok(body);
    }

    // @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/allUsers")
    public ResponseEntity<?> getFullUsers(@RequestBody users request) {
        List<users> users = usersRepository.findAllUsers();
        if (!users.isEmpty()) {
            return ResponseEntity.ok(users);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy");
        }
    }

    @PostMapping("/update") // Chinh sua ten va email
    public ResponseEntity<?> updateUser(@RequestBody users request) {
        Optional<users> userOpt = usersRepository.findById(request.getId());
        if (userOpt.isPresent()) {
            users user = userOpt.get();
            user.setFullname(request.getFullname());
            user.setEmail(request.getEmail());
            users updatedUser = usersRepository.save(user);
            return ResponseEntity.ok(updatedUser); // ✅ Trả về bản ghi vừa lưu
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy user ID: " + request.getId());
        }
    }

    @PostMapping("/updateTkMK")
    public ResponseEntity<?> updateTkMK(@RequestBody users request) {
        Optional<users> userOpt = usersRepository.findById(request.getId());
        if (userOpt.isPresent()) {
            users user = userOpt.get();
            user.setTk(request.getTk());
            user.setMk(request.getMk());
            users updatedUser = usersRepository.save(user);
            return ResponseEntity.ok(updatedUser); // ✅ Trả về bản ghi vừa lưu
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy user ID: " + request.getId());
        }
    }

    @PostMapping("/updateBalan") // sửa số dư của người dùng
    public ResponseEntity<?> updateBalan(@RequestBody atm entity) {
        try {
            Optional<atm> atmInfo = atmRepository.findByIdPlayer(entity.getIdPlayer());
            if (atmInfo.isPresent()) {
                atm atm = atmInfo.get();
                atm.setBalance(entity.getBalance()); // Cập nhật số dư mới
                atmRepository.save(atm);
                return ResponseEntity.ok(entity);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy người chơi với ID: " + entity.getIdPlayer());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lỗi xử lý yêu cầu: " + e.getMessage());
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
            betHisfbxsRepo.deleteByBetHisfbxsId(userId);
            MessageRepo.deleteAllMessagesByUser(userId);
            sessionPlayerRepo.deleteByPlayerId(userId);
            atmRepository.deleteByAtmId(userId);
            hisBalanceRepo.deleteAllByUser(userId);
            friendRepository.deleteAllByUser(userId);
            // Xoá user
            usersRepository.deleteById(userId);
            usersRepository.flush(); // Đảm bảo rằng các thay đổi đã được lưu vào cơ sở dữ liệu

            response.put("message", "Xóa người dùng và các dữ liệu liên quan thành công");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("message", "Đã xảy ra lỗi khi xóa người dùng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
