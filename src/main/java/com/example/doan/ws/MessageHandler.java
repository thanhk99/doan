package com.example.doan.ws;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class MessageHandler extends TextWebSocketHandler {
    
    private List<WebSocketSession> sessions= new ArrayList<>();
    private Map <String, WebSocketSession> userSession = new HashMap<>();

    @Override 
    public void afterConnectionEstablished( WebSocketSession session) throws Exception {
        sessions.add(session);
        String username = (String) session.getAttributes().get("username");
        userSession.put(username, session);
    }
    @Override 
    public void afterConnectionClosed(WebSocketSession session,  CloseStatus status) throws Exception {
        sessions.remove(session);
    }

    @Override 
    public void handleMessage(WebSocketSession session,  WebSocketMessage<?> message) throws Exception {
        String username = (String) session.getAttributes().get("username");
    }
}
