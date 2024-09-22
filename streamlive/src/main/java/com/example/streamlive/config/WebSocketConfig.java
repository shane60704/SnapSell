package com.example.streamlive.config;

import com.example.streamlive.websocket.SignalingHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer, WebSocketConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat").setAllowedOriginPatterns("*").withSockJS();
        registry.addEndpoint("/contract").setAllowedOriginPatterns("*").withSockJS();
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 測試環境
        registry.addHandler(new SignalingHandler(), "/socket").setAllowedOrigins("*");
        // 正式環境
        // registry.addHandler(new SignalingHandler(), "/socket").setAllowedOrigins("https://techwavelab.com");
    }
}

