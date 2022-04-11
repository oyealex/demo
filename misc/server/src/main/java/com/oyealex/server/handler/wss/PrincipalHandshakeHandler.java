package com.oyealex.server.handler.wss;

import com.oyealex.server.util.BlankUtil;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

/**
 * @author oye
 * @since 2020-06-17 21:21:43
 */
@Component
public class PrincipalHandshakeHandler extends DefaultHandshakeHandler {
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
        Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            final String userId = servletRequest.getServletRequest().getParameter("userId");
            if (BlankUtil.isBlank(userId)) {
                return null;
            }
            return () -> userId;
        }
        return super.determineUser(request, wsHandler, attributes);
    }
}
