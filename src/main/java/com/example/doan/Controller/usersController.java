package com.example.doan.Controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.doan.Model.friend;
import com.example.doan.Model.users;
import com.example.doan.Model.atm;
import com.example.doan.Repository.UsersRepository;
import com.example.doan.Repository.friendRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.doan.Repository.atmRepository;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid; 

@RestController
@RequestMapping("user")
public class usersController {
    HttpServletRequest request;
    public usersController(){}
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private atmRepository atmRepository;
    private static String fullname = "";
        @GetMapping()
        public ResponseEntity<users> getUsersById() {
            return usersRepository.findById(1).
            map(users -> ResponseEntity.ok(users)).
            orElse( ResponseEntity.notFound().build());
        }
    
    @PostMapping("/login") // Đăng nhập
    public ResponseEntity<?> login(@Valid @RequestBody users loginRequest,HttpServletResponse response) {
        Optional <users> user = usersRepository.findByTk(loginRequest.getTk());
        System.out.println(loginRequest.getTk());
        if(user.isPresent() && user.get().getMk().equals(loginRequest.getMk())) {
            Cookie cookieId = new Cookie("id", String.valueOf(user.get().getId()));
            cookieId.setMaxAge(60 * 60 * 24 * 7);
            fullname = user.get().getFullname();
            response.addCookie(cookieId);
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("id", user.get().getId());
            responseBody.put("fullname", user.get().getFullname());
            Optional<atm> atmInfo = atmRepository.findByIdPlayer(user.get().getId());
            responseBody.put("balance", atmInfo.get().getBalance());
            return ResponseEntity.ok(responseBody);
        }
        else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sai tài khoản hoặc mật khẩu");
        }
    }
    @PostMapping("/regis") // Đăng ký
    public ResponseEntity<?> regis
        (@Valid @RequestBody users regisRequest,
        HttpServletRequest httpRequest,
        HttpSession session,
        HttpServletResponse response) {
        Optional <users> user = usersRepository.findByTk(regisRequest.getTk());
        if(user.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Tài khoản đã tồn tại");
        }
        else {
            usersRepository.save(regisRequest);
            return ResponseEntity.ok("Đăng ký thành công");
        }
    }
    @GetMapping("/info") 
    public ResponseEntity<?> GetInfo (HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookieId = request.getCookies();
        String idString ="";
        System.out.println(cookieId);
        if (cookieId != null) {
            for (Cookie cookie : cookieId) {
                if (cookie.getName().equals("id")) {
                    idString= cookie.getValue();
                }
            }
        }
        if(idString !="") {
            int IdUser= Integer.parseInt(idString);
            Optional <users> user = usersRepository.findById(IdUser);
            return ResponseEntity.ok(user.get());
        }
        else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Không tìm thấy");
        }
    }
    @PostMapping("/search")
    public ResponseEntity<?> searchUser (@RequestBody users request) {
        List<users> users = usersRepository.findByTkContaining(request.getTk());
        if (!users.isEmpty()) {
            return ResponseEntity.ok(users);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy");
        }
    }

    @PostMapping("/atm")    //Lấy ra tất cả thông tin về tiền của người dùng
    public ResponseEntity<?> getAtm (@RequestBody atm request){
        Optional<atm> atmInfo = atmRepository.findByIdPlayer(request.getIdPlayer());
        if (atmInfo.isPresent()) {
            return ResponseEntity.ok(atmInfo.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy thông tin ATM");
        }
    }
    @PostMapping("/addBalan") // cộng tiền ở số dư của người dùng
    public ResponseEntity<?> addBalance(@RequestBody atm entity) {
        try {
            Optional<atm> atmInfo = atmRepository.findByIdPlayer(entity.getIdPlayer());
            if (atmInfo.isPresent()) {
                atm atm = atmInfo.get();
                float balance = atm.getBalance();
                int idPlayer = atm.getIdPlayer();
    
                if (balance <= 0) {
                    return ResponseEntity.badRequest().body("Dữ liệu không hợp lệ: idPlayer hoặc balance bị thiếu hoặc không hợp lệ.");
                }
    
                atm.setBalance(atm.getBalance() + entity.getBalance());
                atmRepository.save(atm);
                return ResponseEntity.ok("Đã cộng " + entity.getBalance() + " vào tài khoản");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy người chơi với ID: " + entity.getIdPlayer());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lỗi xử lý yêu cầu: " + e.getMessage());
        }
                    
                    
}
               
            
    @PostMapping("/minusBalan") // trừ tiền ở số dư của người dùng
    public ResponseEntity<?> minusBalan(@RequestBody atm entity) {
        try {
            Optional<atm> atmInfo = atmRepository.findByIdPlayer(entity.getIdPlayer());
            if (atmInfo.isPresent()) {
                atm atm = atmInfo.get();
                float balance = atm.getBalance();
                int idPlayer = atm.getIdPlayer();
    
                if (balance < 0) {
                    return ResponseEntity.badRequest().body("Dữ liệu không hợp lệ: idPlayer hoặc balance bị thiếu hoặc không hợp lệ.");
                }
    
                if (atm.getBalance() < entity.getBalance()) {
                    return ResponseEntity.badRequest().body("Số dư không đủ để thực hiện giao dịch.");
                }

                atm.setBalance(atm.getBalance() - entity.getBalance());
                atmRepository.save(atm);
                return ResponseEntity.ok("Đã trừ " + entity.getBalance() + " vào tài khoản");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy người chơi với ID: " + entity.getIdPlayer());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lỗi xử lý yêu cầu: " + e.getMessage());
        }
        
    }

    public String getFullname() {
        return fullname;
    }
}
