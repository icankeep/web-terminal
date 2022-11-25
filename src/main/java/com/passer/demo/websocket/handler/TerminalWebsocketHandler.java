package com.passer.demo.websocket.handler;

import com.passer.demo.websocket.domain.Message;
import com.passer.demo.websocket.service.TerminalService;
import com.passer.demo.websocket.utils.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * @author passer
 * @time 2022/11/24 22:49
 */
@Slf4j
public class TerminalWebsocketHandler extends TextWebSocketHandler {

    private final TerminalService terminalService;

    @Autowired
    public TerminalWebsocketHandler(TerminalService terminalService) {
        this.terminalService = terminalService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        terminalService.setWebSocketSession(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("receive msg: {}", payload);
        Message info = GsonUtil.fromJson(payload, Message.class);
        terminalService.onCommand(info.getData());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("handleTransportError", exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        terminalService.onClose();
        super.afterConnectionClosed(session, status);
    }

    @Override
    public boolean supportsPartialMessages() {
        return super.supportsPartialMessages();
    }
}
