package com.oyealex.server.handler.wss;

import com.oyealex.server.util.BlankUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.security.Principal;

/**
 * @author oye
 * @since 2020-06-17 21:56:54
 */
@Slf4j
@Component
public class EchoWebsocketHandler extends TextWebSocketHandler {
    private static final CloseStatus NOT_AUTH = new CloseStatus(4001, "Not Auth");

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Principal principal = session.getPrincipal();
        String userId;
        if (principal == null || BlankUtil.isBlank((userId = principal.getName()))) {
            log.error("invalid session, no principal provided: {}", session);
            session.close(NOT_AUTH);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
    }
}
