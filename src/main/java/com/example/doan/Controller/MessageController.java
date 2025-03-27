package com.example.doan.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.doan.Model.Message;
import com.example.doan.Repository.MessageRepo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/mess")
public class MessageController {
    @Autowired
    private MessageRepo messageRepo;

    @PostMapping("getChatHis")
    public ResponseEntity<?> getChatHis(@RequestBody Message entity) {
        
        return ResponseEntity.ok("ok");
    }
    

}
