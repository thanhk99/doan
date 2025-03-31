package com.example.doan.Controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.doan.Model.atm;
import com.example.doan.Model.historyBalance;
import com.example.doan.Repository.HisBalanceRepo;
import com.example.doan.Repository.atmRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/Atm")
public class AtmController {
    @Autowired 
    private atmRepository atmRepository;
    @Autowired 
    private HisBalanceRepo hisBalanceRepo;

    @PostMapping("/updateBalance") // trừ tiền ở số dư của người dùng
    public ResponseEntity<?> upadateBalan(@RequestBody atm entity) {
        try {
            Optional<atm> atmInfo = atmRepository.findByIdPlayer(entity.getIdPlayer());
            if (atmInfo.isPresent()) {
                atm atm = atmInfo.get();
                float balance = atm.getBalance();
                if (balance < 0) {
                    return ResponseEntity.badRequest().body("Dữ liệu không hợp lệ: idPlayer hoặc balance bị thiếu hoặc không hợp lệ.");
                }
    
                if (atm.getBalance() < entity.getBalance()) {
                    return ResponseEntity.badRequest().body("Số dư không đủ để thực hiện giao dịch.");
                }

                atm.setBalance(atm.getBalance() + entity.getBalance());
                atmRepository.save(atm);
                return ResponseEntity.ok(entity);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy người chơi với ID: " + entity.getIdPlayer());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lỗi xử lý yêu cầu: " + e.getMessage());
        }
        
    }
    @PostMapping("/get")    //Lấy ra tất cả thông tin về tiền của người dùng
    public ResponseEntity<?> getAtm (@RequestBody atm request){
        Optional<atm> atmInfo = atmRepository.findByIdPlayer(request.getIdPlayer());
        if (atmInfo.isPresent()) {
            return ResponseEntity.ok(atmInfo.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy thông tin ATM");
        }
    }

    @PostMapping("/saveHis")
    public ResponseEntity<?> saveHis(@RequestBody historyBalance request){
        hisBalanceRepo.save(request);
        return ResponseEntity.ok(request);
    }
    @PostMapping("/search")
    public ResponseEntity<?> searchStk(@RequestBody atm entity) {
        Optional<atm>atm=atmRepository.findByStk(entity.getStk());
        return ResponseEntity.ok(atm);
    }
}
