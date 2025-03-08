package com.example.doan.ws;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.example.doan.Controller.gameController;
import com.example.doan.Model.users;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.socket.TextMessage;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.io.IOException;
import java.net.InetSocketAddress;
import org.springframework.http.HttpHeaders;
import java.util.ArrayList;

public class ClientInfoHandler extends TextWebSocketHandler {
    HttpSession httpSession ;
    HttpServletRequest request;
    private static gameController gameController=new gameController();
    private List<WebSocketSession> sessions = Collections.synchronizedList(new ArrayList<>());
    private int countdown = 10;
    private Timer timer;
    private boolean isRecived = true;
    private HashMap<String ,String > GuessClient= new HashMap<>();
    private HashMap<String ,String > MoneyClient= new HashMap<>();
    private ObjectMapper ojMapper = new ObjectMapper(); 
    private static users u = new users();
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String username = (String) session.getAttributes().get("username"); // Lấy tên người dùng
        System.out.println("User  connected: " + username);
    }
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        System.out.println("Client disconnected: " + (String)session.getAttributes().get("username"));

    }
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String username = (String) session.getAttributes().get("username");
        String msg=(String)message.getPayload();
        System.out.println(username+":"+msg);
    }
    private void startCountdown() throws IOException {
        GuessClient.clear();
        timer = new Timer();
        isRecived =true;
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (countdown >= 0) {
                    countdown--;
                } 
                else {
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
                    sendMessageToAll("Bắt đầu");
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
        int resultInt=gameController.result();
        String result= "";
        if(resultInt%2==0){
            sendMessageToAll("kết quả là : "  +Integer.toString(resultInt) + "->Chẵn");
            result ="Chẵn";
        }
        else{
            sendMessageToAll("kết quả là : "  +Integer.toString(resultInt) +" -> Lẻ");
            result="Lẻ";
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
}
