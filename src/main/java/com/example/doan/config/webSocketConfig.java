package com.example.doan.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.example.doan.ws.ClientInfoHandler;
import com.example.doan.ws.CustomHandshake;

@Configuration
@EnableWebSocket
public class webSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new ClientInfoHandler(), "/socket/send")
                .addInterceptors(new CustomHandshake())
                .setAllowedOrigins("*") ;                
    }
}
