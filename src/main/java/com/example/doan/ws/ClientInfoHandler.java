package com.example.doan.ws;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.TextMessage;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;

public class ClientInfoHandler extends TextWebSocketHandler {

    private List<WebSocketSession> sessions = Collections.synchronizedList(new ArrayList<>());
    private int countdown = 10;
    private Timer timer;
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
        System.out.println(session.getRemoteAddress()+" send : " + message.getPayload());
        for (WebSocketSession i : sessions) {

        }
    }
    private void startCountdown() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (countdown >= 0) {
                    sendMessageToAll("Countdown: " + countdown);
                    countdown--;
                } else {
                    sendMessageToAll("Kết thúc!"); // In "Kết thúc"
                    timer.cancel();
                    timer.purge();
    
                    Timer delayTimer = new Timer();
                    delayTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            countdown = 10;
                            startCountdown();
                        }
                    }, 3000); // Chờ 3 giây
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
}
