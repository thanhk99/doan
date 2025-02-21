package com.example.doan.ws;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.example.doan.Controller.gameController;
import com.example.doan.Controller.usersController;
import com.example.doan.Model.users;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.socket.TextMessage;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ClientInfoHandler extends TextWebSocketHandler {
    HttpSession httpSession ;
    private static gameController gameController=new gameController();
    private List<WebSocketSession> sessions = Collections.synchronizedList(new ArrayList<>());
    private int countdown = 10;
    private Timer timer;
    private boolean isRecived = true;
    private HashMap<String ,String > GuessClient= new HashMap<>();
    private static users u = new users();
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        usersController uController = new usersController();
        String username = uController.getFullname();
        if (username != "") {
            sessions.add(session);
            session.getAttributes().put("username", username);
            String clientInfo = String.format(
                "{\"type\":\"CONNECTION_STATUS\",\"message\":\"Client connected: %s\"}", 
                username
            );
            session.sendMessage(new TextMessage(clientInfo));
            System.out.println("Client connected: " + username);
            if (timer == null) {
                startCountdown();
            }
        }
        else{
            String clientInfo = "Vui lòng đăng nhập";
            session.sendMessage(new TextMessage(clientInfo));
            session.close();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        System.out.println("Client disconnected: " + (String)session.getAttributes().get("username"));

    }
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String username = (String) session.getAttributes().get("username");
        if (isRecived){
            String clientID=username;
            String clientGuess = (String)message.getPayload();
            GuessClient.put(clientID, clientGuess);
            System.out.println(username+" send : " + message.getPayload());
        }
        else{
            String TempMsg= "Quá hạn";
            session.sendMessage(new TextMessage(TempMsg));
        }
    }
    private void startCountdown() throws IOException {
        GuessClient.clear();
        timer = new Timer();
        isRecived =true;
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (countdown >= 0) {
                    sendMessageToAll("Countdown: " + countdown);
                    countdown--;
                } else {
                    isRecived=false;
                    sendMessageToAll("Kết thúc!"); 
                    try {
                        ShowRs();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    timer.cancel();
                    timer.purge();
    
                    Timer delayTimer = new Timer();
                    delayTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            countdown = 10;
                            try {
                                startCountdown();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }, 3000); 
                }
            }
        }, 0, 1000);
    }

    private void sendMessageToAll(String message) {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void ShowRs() throws IOException{
        int result=gameController.result();
        sendMessageToAll("kết quả là : "  +result);
        for (WebSocketSession i:sessions){
            String clientId = (String) i.getAttributes().get("username");
            if (GuessClient.get(clientId) !=null){
                int tempRs=Integer.parseInt(GuessClient.get(clientId));
                if (tempRs == result){
                    String tempMsg="Chức mừng bạn" +clientId;
                    i.sendMessage(new TextMessage(tempMsg));
                } 
            }
        }
    }
}
