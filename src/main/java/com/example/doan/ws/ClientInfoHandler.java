package com.example.doan.ws;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.example.doan.Controller.gameController;

import org.springframework.web.socket.TextMessage;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.io.IOException;
import java.util.ArrayList;

public class ClientInfoHandler extends TextWebSocketHandler {
    private static gameController gameController=new gameController();
    private List<WebSocketSession> sessions = Collections.synchronizedList(new ArrayList<>());
    private int countdown = 10;
    private Timer timer;
    private boolean isRecived = true;
    private HashMap<String ,String > GuessClient= new HashMap<>();
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        String clientInfo = "Client connected: " + session.getRemoteAddress();
        session.sendMessage(new TextMessage(clientInfo));
        System.out.println(clientInfo);
        if (timer == null) {
            startCountdown();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        System.out.println("Client disconnected: " + session.getRemoteAddress());

    }
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        if (isRecived){
            String clientID=session.getId();
            String clientGuess = (String)message.getPayload();
            GuessClient.put(clientID, clientGuess);
            System.out.println(session.getRemoteAddress()+" send : " + message.getPayload());
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
        sendMessageToAll("kết quả là : "  +Integer.toString(result));
        for (WebSocketSession i:sessions){
            String clientId=i.getId();
            if (GuessClient.get(clientId) !=null){
                int tempRs=Integer.parseInt(GuessClient.get(clientId));
                if (tempRs == result){
                    String tempMsg="Chức mừng bạn";
                    i.sendMessage(new TextMessage(tempMsg));
                } 
            }
        }
    }
}
