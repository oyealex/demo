package com.oyealex.server.config;

import com.oyealex.server.handler.wss.EchoWebsocketHandler;
import com.oyealex.server.handler.wss.PrincipalHandshakeHandler;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * @author oye
 * @since 2020-06-17 20:54:59
 */
@Configuration
@EnableWebSocket
public class WssConfig implements WebSocketConfigurer {
    @Setter(onMethod_ = {@Autowired})
    private PrincipalHandshakeHandler handshakeHandler;

    @Setter(onMethod_ = {@Autowired})
    private EchoWebsocketHandler echoWebsocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(echoWebsocketHandler, "/echo")
            .setAllowedOrigins("*")
            .setHandshakeHandler(handshakeHandler)
            .withSockJS();
    }
}
