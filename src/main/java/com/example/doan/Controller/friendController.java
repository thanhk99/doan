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

import java.util.List;
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
    public ResponseEntity<?> addFriend(@RequestBody friend request) {
        Integer idMy = request.getIdMy();
        Integer idFriend = request.getIdFriend();
    
        if (idMy == null || idFriend == null) {
            return ResponseEntity.badRequest().body("ID không hợp lệ.");
        }
    
        // Kiểm tra xem hai người đã là bạn bè hay chưa
        friend existingFriend = friendRepository.findByIdMyAndIdFriend(idMy, idFriend);
        if (existingFriend != null && "Bạn bè".equals(existingFriend.getRelative())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Hai bạn đã là bạn bè!");
        }
    
        // Kiểm tra nếu đã gửi lời mời trước đó
        if (existingFriend != null && "Đang chờ".equals(existingFriend.getRelative())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lời mời đã được gửi trước đó!");
        }
    
        // Thêm lời mời kết bạn hai chiều
        friend friendRequest1 = new friend();
        friendRequest1.setIdMy(idMy);
        friendRequest1.setIdFriend(idFriend);
        friendRequest1.setRelative("Đang chờ");
    
        friend friendRequest2 = new friend();
        friendRequest2.setIdMy(idFriend);
        friendRequest2.setIdFriend(idMy);
        friendRequest2.setRelative("Xác nhận");
    
        friendRepository.save(friendRequest1);
        friendRepository.save(friendRequest2);
    
        return ResponseEntity.ok("Đã gửi lời mời kết bạn.");
    }
    
    
    
    @PostMapping("/acceptFriend")
    public ResponseEntity<?> acceptFriend(@RequestBody friend request) {
        Integer idMy = request.getIdMy();
        Integer idFriend = request.getIdFriend();
    
        if (idMy == null || idFriend == null) {
            return ResponseEntity.badRequest().body("ID không hợp lệ.");
        }
    
        // Tìm lời mời kết bạn theo idMy -> idFriend
        friend friendRequest = friendRepository.findByIdMyAndIdFriend(idMy, idFriend);
    
        if (friendRequest == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Lời mời kết bạn không tồn tại hoặc đã bị xóa.");
        }
    
        // Cập nhật trạng thái thành "Bạn bè"
        friendRequest.setRelative("Bạn bè");
        friendRepository.save(friendRequest);
    
        // Kiểm tra quan hệ ngược (idFriend -> idMy)
        friend reverseFriend = friendRepository.findByIdMyAndIdFriend(idFriend, idMy);
        if (reverseFriend == null) {
            // Nếu chưa có, tạo mới
            friend newFriend = new friend();
            newFriend.setIdMy(idFriend);
            newFriend.setIdFriend(idMy);
            newFriend.setRelative("Bạn bè");
            friendRepository.save(newFriend);
        } else {
            // Nếu đã tồn tại nhưng chưa phải "Bạn bè", cập nhật
            if (!"Bạn bè".equals(reverseFriend.getRelative())) {
                reverseFriend.setRelative("Bạn bè");
                friendRepository.save(reverseFriend);
            }
        }
    
        return ResponseEntity.ok("Lời mời kết bạn đã được chấp nhận.");
    }    
    
    

    @PostMapping("/getListFriend")
    public ResponseEntity<?> getListFriend(@RequestBody friend request) {
        Integer idMy = request.getIdMy();
    
        if (idMy == null) {
            return ResponseEntity.badRequest().body("ID người dùng không hợp lệ.");
        }
    
        // Lấy danh sách bạn bè 2 chiều
        List<String> friendNames = friendRepository.findFriendNamesById(idMy);
    
        return ResponseEntity.ok(friendNames);
    }

@PostMapping("/getFriendRequests")
public ResponseEntity<?> getRequests(@RequestBody friend request) {
    Integer idMy = request.getIdMy();

    // Lấy danh sách lời mời có trạng thái "Xác nhận"
    List<String> pendingRequests = friendRepository.findFriendNamesByIdAndRelative(idMy);

    if (pendingRequests.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không có lời mời kết bạn nào.");
    }

    return ResponseEntity.ok(pendingRequests);
}




@DeleteMapping("/deleteFriend")
public ResponseEntity<?> deleteFriend(@RequestBody friend request) {
    Integer idMy = request.getIdMy();
    Integer idFriend = request.getIdFriend();

    if (idMy == null || idFriend == null) {
        return ResponseEntity.badRequest().body("ID không hợp lệ.");
    }

    friend friend1 = friendRepository.findByIdMyAndIdFriend(idMy, idFriend);
    friend friend2 = friendRepository.findByIdMyAndIdFriend(idFriend, idMy);

    if (friend1 == null || friend2 == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Bạn bè không tồn tại.");
    }

    // Chỉ xóa nếu cả hai bản ghi đều có trạng thái "Bạn bè"
    if ("Bạn bè".equals(friend1.getRelative()) && "Bạn bè".equals(friend2.getRelative())) {
        friendRepository.delete(friend1);
        friendRepository.delete(friend2);
        return ResponseEntity.ok("Bạn bè đã bị xóa.");
    }

    return ResponseEntity.badRequest().body("Không thể xóa bạn bè, trạng thái không hợp lệ.");
}

@DeleteMapping("/deleteFriendRequest")
public ResponseEntity<?> deleteFriendRequest(@RequestBody friend request) {
    Integer idMy = request.getIdMy();
    Integer idFriend = request.getIdFriend();

    if (idMy == null || idFriend == null) {
        return ResponseEntity.badRequest().body("ID không hợp lệ.");
    }

    friend friendRequest1 = friendRepository.findByIdMyAndIdFriend(idMy, idFriend);
    friend friendRequest2 = friendRepository.findByIdMyAndIdFriend(idFriend, idMy);

    if (friendRequest1 == null || friendRequest2 == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Lời mời kết bạn không tồn tại.");
    }

    // Chỉ xóa lời mời nếu trạng thái đúng
    if ("Đang chờ".equals(friendRequest1.getRelative()) && "Xác nhận".equals(friendRequest2.getRelative())) {
        friendRepository.delete(friendRequest1);
        friendRepository.delete(friendRequest2);
        return ResponseEntity.ok("Lời mời kết bạn đã bị hủy.");
    }

    return ResponseEntity.badRequest().body("Không thể hủy lời mời, trạng thái không hợp lệ.");
}
  
}
