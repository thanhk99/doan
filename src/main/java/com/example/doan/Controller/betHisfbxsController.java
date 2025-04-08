package com.example.doan.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.doan.Model.betHisfbxs;
import com.example.doan.Repository.UsersRepository;
import com.example.doan.Repository.betHisfbxsRepo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.data.domain.Pageable;


@RestController
@RequestMapping("/betHisfbxs")
public class betHisfbxsController {
    
    @Autowired
    private betHisfbxsRepo betHisfbxsRepo;
    @Autowired
    private UsersRepository usersrepository;


    @PersistenceContext
    private EntityManager em;

    @GetMapping("/check-table")
    public ResponseEntity<?> checkRawData() {
        List<Object[]> raw = em.createNativeQuery("SELECT * FROM betHisfbxs").getResultList();
        raw.forEach(row -> System.out.println(Arrays.toString(row)));
        return ResponseEntity.ok(raw);
}   

@GetMapping("/test-all")
public ResponseEntity<?> getAll() {
    List<betHisfbxs> list = betHisfbxsRepo.findAll();
    list.forEach(b -> System.out.println("-> idPlayer: " + b.getIdPlayer()));
    return ResponseEntity.ok(list);
}


    @PostMapping("getbetHisfbxs")
        public ResponseEntity<?> getbetHisfbxs(@RequestBody betHisfbxs request) {
        int idplayer = request.getIdPlayer(); 
        System.out.println("ID PLAYER NHẬN ĐƯỢC: " + idplayer);

        List<betHisfbxs> listBetHisfbxs = betHisfbxsRepo.findByIdPlayer(idplayer); 
        // System.out.println("Object nhận được: " + request);
        // System.out.println("ID PLAYER NHẬN ĐƯỢC: " + request.getIdPlayer());

       
        System.out.println("Tổng số dòng trong bảng: " + listBetHisfbxs.size());  
        listBetHisfbxs.forEach(b -> System.out.println("-> idplayer: " + b.getIdPlayer()));

        return ResponseEntity.ok(listBetHisfbxs);
    }
    
    

}
