package com.example.streamlive.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic"); // 設置消息代理前綴
        config.setApplicationDestinationPrefixes("/app"); // 設置應用程序目的地前綴
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/chat").setAllowedOriginPatterns("*").withSockJS();
//        registry.addEndpoint("/contract").setAllowedOriginPatterns("*").withSockJS();
        registry.addEndpoint("/chat").setAllowedOrigins("https://techwavelab.com").withSockJS();
        registry.addEndpoint("/contract").setAllowedOrigins("https://techwavelab.com").withSockJS();

    }
}
