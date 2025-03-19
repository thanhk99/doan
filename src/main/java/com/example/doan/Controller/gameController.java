package com.example.doan.Controller;
import java.util.*;
import com.example.doan.Repository.sessionPlayerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.doan.Model.sessionGame;
import com.example.doan.Model.sessionPlayer;
import com.example.doan.Repository.sessionGameRepo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
@RequestMapping("game")
public class gameController {
    @Autowired
    private sessionPlayerRepo sessionPlayerRepo;
    @Autowired
    private sessionGameRepo sessionGameRepo;
    @PostMapping("/getHistoryCl")
    public ResponseEntity<?> saveSession(@RequestBody sessionGame entity) {
        ArrayList<sessionGame> listHis=new ArrayList<>();
        listHis =sessionGameRepo.findTop10ByNamegameOrderByIdDesc(entity.getNamegame());
        return ResponseEntity.ok(listHis);
    }
    @PostMapping("/savePlayerHis")
    public ResponseEntity<?> savePlayerHis(@RequestBody sessionPlayer entity) {
        sessionPlayerRepo.save(entity);
        return ResponseEntity.ok(entity);
    }
    

}
