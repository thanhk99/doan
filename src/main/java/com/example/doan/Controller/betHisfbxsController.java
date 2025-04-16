package com.example.doan.Controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.doan.Model.atm;
import com.example.doan.Model.betHisfbxs;
import com.example.doan.Model.betHisfbxs.BetType;
import com.example.doan.Repository.UsersRepository;
import com.example.doan.Repository.atmRepository;
import com.example.doan.Repository.betHisfbxsRepo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.data.domain.Pageable;

import java.util.Map;


@RestController
@RequestMapping("/betHisfbxs")
public class betHisfbxsController {
    
    @Autowired
    private betHisfbxsRepo betHisfbxsRepo;
    @Autowired
    private UsersRepository usersrepository;

    @Autowired
    private AtmController atmController;

    @Autowired
    private atmRepository atmRepository;


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
        // System.out.println("ID PLAYER NHẬN ĐƯỢC: " + idplayer);

        List<betHisfbxs> listBetHisfbxs = betHisfbxsRepo.findByIdPlayer(idplayer); 
        // System.out.println("Object nhận được: " + request);
        // System.out.println("ID PLAYER NHẬN ĐƯỢC: " + request.getIdPlayer());

        // System.out.println("Tổng số dòng trong bảng: " + listBetHisfbxs.size());  
        // listBetHisfbxs.forEach(b -> System.out.println("-> idplayer: " + b.getIdPlayer()));

        return ResponseEntity.ok(listBetHisfbxs);
    }
    

    @PostMapping("/placeBet")
    public ResponseEntity<?> placeBet(@RequestBody betHisfbxs betRequest) {
        try {
            betRequest.setBetTime(LocalDateTime.now()); 
            System.out.println(">>> Dữ liệu nhận được: " + betRequest);
            betHisfbxsRepo.save(betRequest); 
            return ResponseEntity.ok(Map.of("message", "Đặt cược thành công!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Đặt cược thất bại!"));
        }
    }
    
    @GetMapping("/settleBets")
    public ResponseEntity<?> settleBets() {
        try {
            List<betHisfbxs> betsToSettle = betHisfbxsRepo.findByStatusFalseAndBetType(BetType.FOOTBALL);
            System.out.println("Số cược chưa xử lý: " + betsToSettle.size());
    
            String apiUrl = "https://api.football-data.org/v4/competitions/PL/matches";  
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Auth-Token", "17ee52ab7c3d494794f524ea8abff2f8");
    
            ResponseEntity<String> response = new RestTemplate().exchange(
                    apiUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class);
    
            ObjectMapper mapper = new ObjectMapper();
            JsonNode matches = mapper.readTree(response.getBody()).get("matches");
            System.out.println("Số trận từ API: " + matches.size());
    
            for (betHisfbxs bet : betsToSettle) {
                System.out.println("Đang xử lý cược ID: " + bet.getId() + " - ReferenceId: " + bet.getReferenceId());
    
                boolean foundMatch = false;
    
                for (JsonNode match : matches) {
                    String matchId = match.get("id").asText();
                    String homeTeam = match.get("homeTeam").get("name").asText();
                    String awayTeam = match.get("awayTeam").get("name").asText();
                    String status = match.get("status").asText();
    
                    if (bet.getReferenceId().equals(matchId)) {
                        foundMatch = true;
    
                        System.out.println("Tìm thấy trận tương ứng: " + homeTeam + " vs " + awayTeam + " - Status: " + status);
    
                        if (!"FINISHED".equals(status)) {
                            System.out.println("Trận chưa kết thúc → bỏ qua");
                            break;
                        }
    
                        JsonNode scoreNode = match.get("score").get("fullTime");
                        int home = scoreNode.get("home").asInt();
                        int away = scoreNode.get("away").asInt();
                        String actualResult = String.format("%d-%d", home, away);
    
                        System.out.println("Kết quả trận đấu: " + actualResult + " | Dự đoán của người chơi: " + bet.getPrediction());
    
                        if (actualResult.equals(bet.getPrediction())) {
                            int reward = bet.getBetAmount() * bet.getMulti();
                            System.out.println(">> Dự đoán đúng! Trả thưởng: " + reward);
    
                            atmRepository.findByIdPlayer(bet.getIdPlayer()).ifPresent(atm -> {
                                atm.setBalance(atm.getBalance() + reward);
                                atmRepository.save(atm);
                            });
                        } else {
                            System.out.println(">> Dự đoán sai, không trả thưởng.");
                        }
    
                        bet.setStatus(true);
                        betHisfbxsRepo.save(bet);
                        System.out.println("Đã cập nhật status = true cho cược ID: " + bet.getId());
                        break;
                    }
                }
    
                if (!foundMatch) {
                    System.out.println("Không tìm thấy trận phù hợp với referenceId: " + bet.getReferenceId());
                }
            }
    
            return ResponseEntity.ok("Hoàn tất xử lý các cược đã kết thúc.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi xử lý cược.");
        }
    }


    

}
