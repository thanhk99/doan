package com.example.doan.ws;

import org.springframework.http.HttpHeaders;

import java.util.Map;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
public class CustomHandshake extends HttpSessionHandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,WebSocketHandler wsHandler, Exception exception) {
    }
}
