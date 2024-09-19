package com.example.streamlive.config;

import com.example.streamlive.websocket.SignalingHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 測試環境使用
        registry.addHandler(new SignalingHandler(), "/socket").setAllowedOrigins("*");
        // 正式環境使用
//        registry.addHandler(new SignalingHandler(), "/socket").setAllowedOrigins("https://techwavelab.com");
    }
}

