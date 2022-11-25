package com.passer.demo.websocket.config;

import com.passer.demo.websocket.handler.TerminalWebsocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.PerConnectionWebSocketHandler;

/**
 * @author passer
 * @time 2022/11/20 16:18
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(getTerminalWebSocketHandler(), "/ws");
    }

    @Bean
    public WebSocketHandler getTerminalWebSocketHandler() {
        return new PerConnectionWebSocketHandler(TerminalWebsocketHandler.class);
    }

//    @Bean
//    public ServerEndpointExporter getServerEndpointExporter() {
//        return new ServerEndpointExporter();
//    }
}
