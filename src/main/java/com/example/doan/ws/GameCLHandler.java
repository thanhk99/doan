    package com.example.doan.ws;

    import java.io.IOException;
    import java.time.LocalDateTime;
    import java.time.format.DateTimeFormatter;
    import java.util.ArrayList;
    import java.util.Collections;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Random;
    import java.util.Timer;
    import java.util.TimerTask;
    import org.springframework.stereotype.Component;
    import org.springframework.web.socket.CloseStatus;
    import org.springframework.web.socket.TextMessage;
    import org.springframework.web.socket.WebSocketMessage;
    import org.springframework.web.socket.WebSocketSession;
    import org.springframework.web.socket.handler.TextWebSocketHandler;

    import com.example.doan.Model.sessionGame;
    import com.example.doan.Repository.sessionGameRepo;
    import com.example.doan.Repository.sessionPlayerRepo;
    import com.fasterxml.jackson.databind.JsonNode;
        import com.fasterxml.jackson.databind.ObjectMapper;
@Component
    public class GameCLHandler extends TextWebSocketHandler {

        private final sessionGameRepo sessionGameRepo;
        private final sessionPlayerRepo sessionplayerRepo;
        public GameCLHandler(sessionGameRepo sessionGameRepo,sessionPlayerRepo sessionPlayerRepo ) {
            this.sessionGameRepo = sessionGameRepo;
            this.sessionplayerRepo = sessionPlayerRepo;
        }

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
            String type= jsonNode.get("type").asText();

            if(type.equals("sumbet")){
                handleBetMsg(jsonNode);
            }
            else if(type.equals("bet")){
                handleChoiceMsg(jsonNode, username);
            }


        }

        private void handleBetMsg(JsonNode jsonNode) throws Exception{

        }
        private void handleChoiceMsg(JsonNode jsonNode,String username) throws Exception{
            String choice =jsonNode.get("choice").asText();
            int money = jsonNode.get("money").asInt();
            if(choice.equals("cuoc_le")){
                totalMoneyL+=money;
            }
            else{
                totalMoneyC+=money;
            }
            System.out.println(MoneyClient.get(username));
            int tempMoney;
            if(MoneyClient.get(username)!=null){
               tempMoney=MoneyClient.get(username)+money;
            }
            else{
                tempMoney=money;
            }
            GuessClient.put(username, choice);
            MoneyClient.put(username, tempMoney);
            System.out.println(username+" : " + choice +"->" + tempMoney);
        }
        private void sendTotalMoney() throws IOException{
            String totalMoney=totalMoneyC+":"+totalMoneyL;
            convertToJson("money", totalMoney);
        }
        private void convertToJson(String type,String message) throws IOException{
            ObjectMapper objectMapper = new ObjectMapper();
            HashMap<String, Object> result = new HashMap<>();
            result.put("type", type);
            result.put("message", message);
            String jsonResult = objectMapper.writeValueAsString(result);
            sendMessageToAll(jsonResult);
        }
        private void startCountdown() throws IOException {
            GuessClient.clear();
            totalMoneyC =0;
            totalMoneyL=0;
            timer = new Timer();
            isRecived =true;
            convertToJson("start","");
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (countdown >= 0) {
                        try {
                            sendTotalMoney();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        countdown--;
                    } 
                    else {
                        isRecived=false;
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
            Random random = new Random();
            int resultInt = random.nextInt(1,6);
            sessionGame sessionGame = new sessionGame();
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = currentDateTime.format(formatter);
            sessionGame.setResult(Integer.toString(resultInt));
            sessionGame.setTimeoccurs(formattedDateTime);
            sessionGame.setNamegame("Chẵn lẻ");

            sessionGameRepo.save(sessionGame);
            String result= "";
            if(resultInt%2==0){
                convertToJson("end", Integer.toString(resultInt));
                result ="cuoc_chan";
            }
            else{
                convertToJson("end", Integer.toString(resultInt));
                result="cuoc_le";
            }
            for (WebSocketSession i:sessions){
                String clientId = (String) i.getAttributes().get("username");
                if (GuessClient.get(clientId) !=null){
                    ObjectMapper objectMapper = new ObjectMapper();
                    HashMap<String, Object> rsMsg = new HashMap<>();
                    rsMsg.put("type", "reward");
                    if (GuessClient.get(clientId).equals(result)){  //Người chơi win
                        rsMsg.put("reward", MoneyClient.get(clientId));
                    } 
                    else{
                        rsMsg.put("reward",0);
                    }
                    rsMsg.put("result", resultInt);
                    rsMsg.put("bet", MoneyClient.get(clientId));
                    rsMsg.put("choice",GuessClient.get(clientId));
                    String jsonResult = objectMapper.writeValueAsString(rsMsg);
                    i.sendMessage(new TextMessage(jsonResult));
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

