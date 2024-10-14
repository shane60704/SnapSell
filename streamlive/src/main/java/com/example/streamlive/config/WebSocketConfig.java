package com.example.streamlive.config;

import com.example.streamlive.websocket.SignalingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private SignalingHandler signalingHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 測試環境
//        registry.addHandler(signalingHandler, "/socket").setAllowedOrigins("*");
        // 正式環境
        registry.addHandler(signalingHandler, "/socket").setAllowedOrigins("https://techwavelab.com");
    }
}

