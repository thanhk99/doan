    package com.example.doan.ws;

    import java.io.IOException;
    import java.util.ArrayList;
    import java.util.Collections;
    import java.util.HashMap;
    import java.util.List;
import java.util.PrimitiveIterator;
import java.util.Timer;
    import java.util.TimerTask;

    import org.hibernate.annotations.Comment;
    import org.springframework.stereotype.Component;
    import org.springframework.web.socket.CloseStatus;
    import org.springframework.web.socket.TextMessage;
    import org.springframework.web.socket.WebSocketMessage;
    import org.springframework.web.socket.WebSocketSession;
    import org.springframework.web.socket.handler.TextWebSocketHandler;

    import com.example.doan.Controller.gameController;
    import com.fasterxml.jackson.databind.JsonNode;
    import com.fasterxml.jackson.databind.ObjectMapper;

    import jakarta.annotation.PostConstruct;

    @Component
    public class GameCLHandler extends TextWebSocketHandler {

        private List<WebSocketSession> sessions = Collections.synchronizedList(new ArrayList<>());
        private int countdown = 15;
        private int wait=14;
        private Timer timer;
        private boolean isRecived = true;
        private boolean isStart = false;
        private int totalMoneyC=0;
        private int totalMoneyL=0;
        private HashMap<String ,String > GuessClient= new HashMap<>();
        private HashMap<String ,Integer > MoneyClient= new HashMap<>();
        private static gameController gameController=new gameController();


        @Override 
        public void afterConnectionEstablished( WebSocketSession session) throws Exception {
            sessions.add(session);
            String username = (String) session.getAttributes().get("username");
            session.sendMessage(new TextMessage(Integer.toString(countdown)));
            System.out.println("User Chan le connected: " + username );
            if (!isStart){
                startCountdown();
                isStart = true;
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
            String msg = (String) message.getPayload();
            
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode= objectMapper.readTree(msg);

            String choice =jsonNode.get("choice").asText();
            int money = jsonNode.get("bet").asInt();

            if(choice.equals('l')){
                totalMoneyL+=money;
            }
            else{
                totalMoneyC+=money;
            }
            sendMessageToAll(choice);
            GuessClient.put(username, choice);
            MoneyClient.put(username, money);

            System.out.println(username+" : " + choice +"->" + money);
        }

        private void startCountdown() throws IOException {
            GuessClient.clear();
            timer = new Timer();
            isRecived =true;
            sendMessageToAll("start");
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    
                    if (countdown >= 0) {
                        countdown--;
                    } 
                    else {
                        isRecived=false;
                        sendMessageToAll("end"); 
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
                                countdown = 15;
                                try {
                                    startCountdown();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, wait*1000); 
                    }
                }
            }, 0, 1000);
        }
        public void ShowRs() throws IOException{
            int resultInt=gameController.result();
            String result= "";
            if(resultInt%2==0){
                sendMessageToAll(Integer.toString(resultInt));
                result ="c";
            }
            else{
                sendMessageToAll(Integer.toString(resultInt));
                result="l";
            }
            for (WebSocketSession i:sessions){
                String clientId = (String) i.getAttributes().get("username");
                if (GuessClient.get(clientId) !=null){
                    System.out.println(GuessClient.get(clientId)+MoneyClient.get(clientId));
                    if (GuessClient.get(clientId).equals(result)){
                        String tempMsg="Chức mừng bạn " +clientId +"nhận được : " +MoneyClient.get(clientId)+" điểm";
                        i.sendMessage(new TextMessage(tempMsg));
                    } 
                }
            }
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

